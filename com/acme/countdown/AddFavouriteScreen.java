package com.acme.countdown;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.system.*;
import java.util.Vector;

public class AddFavouriteScreen extends MainScreen
{
    EditField _editStopCode;
    EditField _editDescription;
    
    AddFavouriteScreen()
    {
        this.setTitle("Add new favourite");
        
        _editStopCode = new EditField("Stop code: ", "", 8, EditField.NON_SPELLCHECKABLE | Field.FIELD_LEFT | EditField.NO_NEWLINE | EditField.FILTER_INTEGER);
        this.add(_editStopCode);
        
        _editDescription = new EditField("Description: ", "", 100, Field.FIELD_LEFT | EditField.NO_NEWLINE);
        this.add(_editDescription);
    }
    
    public boolean isDirty() {
        return _editStopCode.getText().length() > 0;
    }
    
    public void save() {
        Vector vec = FavouriteStore.getFavourites();
        vec.addElement(new Favourite(_editStopCode.getText(), _editDescription.getText()));
        FavouriteStore.saveFavourites(vec);
    }
} 
