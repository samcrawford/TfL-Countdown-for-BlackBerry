package com.acme.countdown;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.system.*;
import javax.microedition.io.*;
import java.io.*;
import org.json.me.*;
import net.rim.device.api.ui.text.TextFilter;
import java.util.Vector;

/**
 * A simple example using the HTTP connection.
 */
public class Countdown extends UiApplication
{
    //private static final String FETCH_BASE = "http://azof.ph0x.co.uk/stopBoard/";
    private static final String FETCH_BASE = "http://countdown.tfl.gov.uk/stopBoard/";
    private CountdownScreen _mainScreen;
    private EditField _url;
    private RichTextField _statusMessage;
    private RichTextField _tflMessages;
    private ArrivalsListField _arrivalsList;

    StatusThread _statusThread = new StatusThread();
    ConnectionThread _connectionThread = new ConnectionThread();

    private MenuItem _fetchMenuItem = new MenuItem("Fetch" , 100, 10) 
    {
        public void run()
        {
            // Don't execute on a blank url.
            if ( _url.getText().length() > 0 )
            {
                if ( !_connectionThread.isStarted() )
                {
                    fetchPage(_url.getText());
                }
                else
                {
                    Dialog.alert("An outstanding request hasn't yet completed!");
                }
            }
        }
    };
    
    private MenuItem _favouritesMenuItem = new MenuItem("Favourites" , 100, 11) 
    {
        public void run()
        {
            UiApplication.getUiApplication().pushScreen(new FavouriteScreen());
        }
    };

    public static void main(String[] args)
    {
        Countdown theApp = new Countdown();
        theApp.enterEventDispatcher();
    }

    /**
     * The ConnectionThread class manages the HTTP connection. Fetch operations 
     * are not queued, however, if a fetch call is made and, while active, another
     * request made, the 2nd request will stall until the previous request completes.
     */
    private class ConnectionThread extends Thread
    {
        private static final int TIMEOUT = 500; // ms

        private String _theUrl;

        private volatile boolean _start = false;
        private volatile boolean _stop = false;

        // Retrieve the URL.
        public synchronized String getUrl()
        {
            return _theUrl;
        }
        
        public boolean isStarted()
        {
            return _start;
        }            

        // Fetch a page.
        // Synchronized so that I don't miss requests.
        public void fetch(String url)
        {
            synchronized(this)
            {
                _start = true;
                _theUrl = url;
            }
        }

        // Shutdown the thread.
        public void stop()
        {
            _stop = true;
        }

        public void run()
        {
            for(;;)
            {
                // Thread control.
                while( !_start && !_stop)
                {
                    // Sleep for a bit so we don't spin.
                    try 
                    {
                        sleep(TIMEOUT);
                    } 
                    catch (InterruptedException e) 
                    {
                        System.err.println(e.toString());
                    }
                }
                
                // Exit condition.
                if ( _stop )
                {
                    return;
                }
                
                // This entire block is synchronized, this ensures I won't miss fetch requests
                // made while I process a page.
                synchronized(this)
                {
                    // Open the connection and extract the data.
                    HttpConnection httpConn = null;
                    
                    try {
                        String suffix = ConnectionUtils.getConnectionSuffix();
                        httpConn = (HttpConnection)Connector.open(getUrl()+suffix);
                        
                        int status = httpConn.getResponseCode();
                        if (status == HttpConnection.HTTP_OK)
                        {
                            InputStream input = httpConn.openInputStream();

                            byte[] data = new byte[256];
                            int len = 0;
                            int size = 0;
                            StringBuffer raw = new StringBuffer();
                            
                            while ( -1 != (len = input.read(data)) )
                            {
                                raw.append(new String(data, 0, len));
                                size += len;
                            }
                            input.close();
                            
                            String content = raw.toString();
                            StopData stopData = StopData.fromJSON(content);
                            
                            // The long operation is the parsing above, after the parsing is complete, shutdown
                            // the status thread before setting the text (since both threads modify the content
                            // pane, we want to make sure we don't have the status thread overwriting our data).
                            stopStatusThread();
                            updateArrivals(stopData);
                        } 
                        else 
                        {
                            stopStatusThread();
                            updateContent("Error: " + status);
                        }
                        
                        httpConn.close();
                    } 
                    catch (JSONException e) 
                    {
                        stopStatusThread();
                        updateContent("Stop not found");
                    }
                    catch (Exception e) 
                    {
                        stopStatusThread();
                        updateContent(e.toString());
                    }
                    
                    // We're done one connection so reset the start state.
                    _start = false;                    
                }
            }
        }

        private void stopStatusThread()
        {
            _statusThread.pause();
            try 
            {
                synchronized(_statusThread)
                {
                    // Check the paused condition, incase the notify fires prior to our wait, in which 
                    // case we may never see that nofity.
                    while ( !_statusThread.isPaused() );
                    {
                        _statusThread.wait();
                    }
                }
            } 
            catch (InterruptedException e) 
            {
                System.err.println(e.toString());
            }
        }
    }

    /**
     * The StatusThread class manages display of the status message while lengthy 
     * HTTP/HTML operations are taking place.
     */
    private class StatusThread extends Thread
    {
        private static final int INTERVAL = 6;
        private static final int TIMEOUT = 500; // ms
        private static final int THREAD_TIMEOUT = 500;

        private volatile boolean _stop = false;
        private volatile boolean _running = false;
        private volatile boolean _isPaused = false;
        
        private final String[] workingMessages = new String[] {
            "Working","Computing","Thinking","Considering","Pondering","Plotting","Debating" };
        private volatile StringBuffer workingMessage = new StringBuffer(32);

        // Resume the thread.
        public void go()
        {
            workingMessage.setLength(0);
            workingMessage.append("[X] ").append(workingMessages[(int) System.currentTimeMillis() % workingMessages.length]);
            _running = true;
            _isPaused = false;
        }

        // Pause the thread.
        public void pause()
        {
            _running = false;
        }

        // Query the paused status.
        public boolean isPaused()
        {
            return _isPaused;
        }

        // Shutdown the thread.
        public void stop()
        {
            _stop = true;
        }

        public void run()
        {
            int i = 0;      
            char[] statusMsg = new char[]{ '-', '\\', '/' };

            for (;;)
            {
                while (!_stop && !_running)
                {
                    // Sleep a bit so we don't spin.
                    try 
                    {
                        sleep(THREAD_TIMEOUT);
                    } 
                    catch ( InterruptedException e) 
                    {
                        System.err.println(e.toString());
                    }

                }
                
                if ( _stop )
                {
                    return;
                }
                
                i = 0;
                
                // Clear the status buffer.
                //status.delete(0, status.length()); 
                
                for ( ;; )
                {
                    // We're not synchronizing on the boolean flag! value is declared volatile therefore.
                    if ( _stop )
                    {
                        return;
                    }
                    
                    if ( !_running )
                    {
                        _isPaused = true;                        
                        synchronized(this)
                        {
                            this.notify();
                        }
                        
                        break;
                    }

                    workingMessage.setCharAt(1,statusMsg[++i % statusMsg.length]);
                    updateContent(workingMessage.toString());

                    try 
                    {
                        this.sleep(TIMEOUT); // Wait for a bit.
                    } 
                    catch (InterruptedException e) 
                    {
                        System.err.println(e.toString());
                    }
                }
            }
        }
    }
    
    private class CountdownScreen extends MainScreen
    {        
        protected void makeMenu(Menu menu, int instance)
        {
            menu.add(_fetchMenuItem);
            menu.add(_favouritesMenuItem);
            menu.add(MenuItem.separator(100));
            
            Vector favourites = FavouriteStore.getFavourites();
            if (favourites != null) {
                for(int i=0, c=favourites.size(); i<c; i++) {
                    final Favourite fav = (Favourite) favourites.elementAt(i);
                    MenuItem menuitem = new MenuItem(fav.toString() , 100, 20)
                    {
                        public void run()
                        {
                            _url.setText(fav.stopCode);                            
                            if (!_connectionThread.isStarted()) {
                                fetchPage(fav.stopCode);
                            } else {
                                Dialog.alert("An outstanding request hasn't yet completed!");
                            }
                        }
                    };
                    menu.add(menuitem);
                }
                menu.add(MenuItem.separator(100));
            }
            
            super.makeMenu(menu, instance);
        }
        
        public boolean onSavePrompt()
        {
            return true;
        }
        
        public void close()
        {
            _statusThread.stop();
            _connectionThread.stop();
            super.close();
        } 

        protected boolean keyChar(char key, int status, int time)
        {
            // UiApplication.getUiApplication().getActiveScreen().
            if ( getLeafFieldWithFocus() == _url && key == Characters.ENTER )
            {
                _fetchMenuItem.run();
                return true; // I've absorbed this event, so return true.
            }
            return super.keyChar(key, status, time);
        }
    }

    public Countdown()
    {
        _mainScreen = new CountdownScreen();
        _mainScreen.setTitle( new LabelField("Countdown" , LabelField.ELLIPSIS | LabelField.USE_ALL_WIDTH));

        _url = new EditField("Code: ", "", 8, EditField.NON_SPELLCHECKABLE | Field.FIELD_LEFT | EditField.NO_NEWLINE | EditField.FILTER_INTEGER)
        {
             protected void layout(int maxWidth, int maxHeight) {
                int myWidth = getMaxSize() * getFont().getAdvance("W");
                myWidth = Math.min(maxWidth, myWidth);
                super.layout(myWidth, maxHeight);
            }
        };
        _url.setFilter(TextFilter.get(TextFilter.NUMERIC));
        _statusMessage = new RichTextField("",RichTextField.TEXT_ALIGN_RIGHT | EditField.NON_FOCUSABLE | EditField.NO_NEWLINE);
        
        HorizontalFieldManager hf = new HorizontalFieldManager() {
            public int getPreferredHeight() { return 50; }
        };
        hf.add(_url);
        hf.add(_statusMessage);
        _mainScreen.add(hf);
        
        _mainScreen.add(new SeparatorField());
        _arrivalsList = new ArrivalsListField();
        _mainScreen.add(_arrivalsList);

        _mainScreen.add(new SeparatorField());
        _tflMessages = new RichTextField(RichTextField.NON_FOCUSABLE | RichTextField.NON_SPELLCHECKABLE);
        _mainScreen.add(_tflMessages);
        
        // Start the helper threads.
        _statusThread.start();
        _connectionThread.start();

        pushScreen(_mainScreen); // Push the main screen - a method on the UiApplication class.
    }

    private void fetchPage(String code)
    {
        // First, normalize the url.
        String url = FETCH_BASE + code.toLowerCase();
        
        /*
         * It is illegal to open a connection on the event thread, due to the 
         * system architecture, therefore, spawn a new thread for connection 
         * operations.
         */
        _connectionThread.fetch(url);

        // Create a thread for showing status of the operation.
        _statusThread.go();
    }

    private void updateContent(final String text)
    {
        // This will create significant garbage, but avoids threading issues
        // (compared with creating a static Runnable and setting the text).
        UiApplication.getUiApplication().invokeLater(new Runnable() 
        {
            public void run()
            {
                _statusMessage.setText(text);
            }
        });
    }
    
    private void updateArrivals(final StopData stopData)
    {
        UiApplication.getUiApplication().invokeLater(new Runnable() 
        {
            public void run()
            {
                _arrivalsList.setData(stopData);
                _statusMessage.setText("Updated "+stopData.lastUpdated);
                
                StringBuffer sb = new StringBuffer(256);
                for(int i=0; i<stopData.criticalMessages.length; i++)
                   sb.append(stopData.criticalMessages[i]).append('\n');
                for(int i=0; i<stopData.importantMessages.length; i++)
                   sb.append(stopData.importantMessages[i]).append('\n');
                for(int i=0; i<stopData.infoMessages.length; i++)
                   sb.append(stopData.infoMessages[i]).append('\n');
                if (sb.length() > 0)
                   sb.deleteCharAt(sb.length() - 1);
                _tflMessages.setText(sb.toString());
            }
        });
    }
}
