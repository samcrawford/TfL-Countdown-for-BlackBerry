package com.acme.countdown;

import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import java.util.Vector;

public class FavouriteStore {
    
    private static PersistentObject store = PersistentStore.getPersistentObject( 0x1dfc10ec9447eb14L );
    
    public static void saveFavourites(Vector vec){
        synchronized(store) {
            store.setContents(vec); 
            store.commit();
        }
    }
    
    public static Vector getFavourites() {
        Vector vec = null;
        synchronized(store) {
            vec = (Vector) store.getContents();
        }
        if(vec == null) {
            vec = new Vector();
        }
        return vec;
    }
} 
