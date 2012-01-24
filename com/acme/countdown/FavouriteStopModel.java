package com.acme.countdown;

import java.util.Vector;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.Persistable;

final class FavouriteStopModel implements Persistable
{
    private Vector _fields;

    public static final int STOP_CODE = 0;
    public static final int STOP_NAME = 1;
    public static final int NUM_FIELDS = 2;

    public FavouriteStopModel()
    {
        _fields = new Vector( NUM_FIELDS );
        for ( int i = 0; i < NUM_FIELDS; ++i )
        {
            _fields.addElement("");
        }
    }
    
    public String getField(int id)
    {
        Object encoding = _fields.elementAt( id );
        return PersistentContent.decodeString( encoding );
    }

    public void setField(int id, String value)
    {
        Object encoding = PersistentContent.encode( value );
        _fields.setElementAt( encoding, id );
    }
    
    public void reEncode() 
    {
        for ( int i = 0; i < NUM_FIELDS; ++i ) 
        {
            Object encoding = _fields.elementAt( i );
            
            if ( ! PersistentContent.checkEncoding( encoding ) ) 
            {
                encoding = PersistentContent.reEncode( encoding );
                _fields.setElementAt( encoding, i );
            }
        }
    }
}

