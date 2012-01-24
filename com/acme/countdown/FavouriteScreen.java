package com.acme.countdown;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.system.*;
import java.util.Vector;

public class FavouriteScreen extends MainScreen implements ListFieldCallback
{
    ListField _list;
    Vector _vecFavourites;
    
    FavouriteScreen() {
        _vecFavourites = FavouriteStore.getFavourites();
        
        this.setTitle("Manage favourite stops");
        
        _list = new ListField(_vecFavourites.size());
        _list.setCallback(this);
        this.add(_list);
    }
    
    public void save() {
        FavouriteStore.saveFavourites(_vecFavourites);
    }
    
    public void onExposed() {
        _vecFavourites = FavouriteStore.getFavourites();
        _list.setSize(_vecFavourites.size());
        _list.invalidate();
    }
    
    public void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
        if (_vecFavourites != null && index < _vecFavourites.size()) {
            graphics.drawText(_vecFavourites.elementAt(index).toString(), 0, y, 0, width);
        }
    }
    
    public Object get(ListField listField, int index) {
        return _vecFavourites.elementAt(index);
    }
    
    public int getPreferredWidth(ListField list) {
        return Display.getWidth();
    }
    
    public int indexOfList(ListField listField, String prefix, int start) {
        return start;
    }

    private void deleteSelectedItem() {
        int i = _list.getSelectedIndex();
        _vecFavourites.removeElementAt(i);
        _list.delete(i);
        _list.setSize(_vecFavourites.size());
        _list.setDirty(true);
    }

    protected boolean keyChar(char key, int status, int time) {
        if (key == Characters.DELETE || key == Characters.BACKSPACE) {
            deleteSelectedItem();
            return true;
        }
        return super.keyChar(key, status, time);
    }

    protected void makeMenu(Menu menu, int instance)
    {
        MenuItem addnew = new MenuItem("Add new", 100, 10)
        {
            public void run()
            {
                UiApplication.getUiApplication().pushScreen(new AddFavouriteScreen());
            }
        };
        menu.add(addnew);
        
        MenuItem delete = new MenuItem("Delete", 100, 10)
        {
            public void run()
            {
                deleteSelectedItem();
            }
        };
        menu.add(delete);
        
        menu.add(MenuItem.separator(100));
        
        super.makeMenu(menu, instance);
    }
}
