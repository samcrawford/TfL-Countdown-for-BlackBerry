/*
 * FavouriteStopPersistentListener.java
 *
 * © <your company here>, 2003-2008
 * Confidential and proprietary.
 */

package com.acme.countdown;


import java.util.Vector;
import net.rim.device.api.system.*;


/**
 * Persistent content listener for the Contacts app.  Listens for changes to the 
 * device's Content Protection/Compression security settings and re-encodes the 
 * database accordingly.  Changes to the device's state are ignored.
 */
/*package*/ final class FavouriteStopPersistentListener implements PersistentContentListener
{
    /**
     * Called when the state of the device changes (unlocked/locking/locked insecure/
     * locked secure). This app doesn't care about these state changes because data 
     * is always encoded inside the contact models; thus, there is no need to encode
     * or decode them during locking and unlocking.
     * 
     * @param state The device's new state.
     * 
     * @see net.rim.device.api.system.PersistentContentListener#persistentContentStateChanged(int)
     */
    public void persistentContentStateChanged( int state ) 
    {
        // Ignored
    }
    
    
    /**
     * Called when the device's Content Protection/Compression security settings are 
     * changed. Re-encodes the Contacts app's database accordingly (if one exists).
     * 
     * @param generation Used to determine if the user has changed the content protection 
     * settings since the listener was notified.
     * 
     * @see net.rim.device.api.system.PersistentContentListener#persistentContentModeChanged(int)
     */
    public void persistentContentModeChanged( int generation ) 
    {
        PersistentObject persist = PersistentStore.getPersistentObject( 0x8c746bdb8fb74513L );
        
        // No persistent object found; nothing to re-encode.
        if ( persist == null ) 
        {  
            return;
        }
        
        synchronized( persist ) 
        {
            Vector models = (Vector) persist.getContents();
            
            // Database is empty; nothing to re-encode.
            if ( models == null ) 
            {  
                return;
            }
            
            int numModels = models.size();
            
            for ( int i = 0; i < numModels; ++i ) 
            {
                FavouriteStopModel model = (FavouriteStopModel) models.elementAt( i );
                model.reEncode();
                if ( generation != PersistentContent.getModeGeneration() ) 
                {
                    // Device's Content Protection/Compression security settings have changed again
                    // since the listener was last notified.  Abort this re-encoding because it will
                    // have to be done again anyway according to the new Content Protection/Compression
                    // security settings.
                    break;
                }
            }
            
            persist.commit();  // Commit the updated database to the persistent store.
        }
    }
}

