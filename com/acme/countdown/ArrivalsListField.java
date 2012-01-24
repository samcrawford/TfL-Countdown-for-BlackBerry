package com.acme.countdown;

import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.system.Display;

public class ArrivalsListField extends ListField implements ListFieldCallback
{
    StopData data;
    int destXOffset;
    int waitXOffset;
    int spaceWidth;
    
    ArrivalsListField() {
        super(0, net.rim.device.api.ui.component.ListField.NON_FOCUSABLE);
        this.setEmptyString("",0);
        this.spaceWidth = this.getFont().getAdvance(" ");
        this.destXOffset = this.getFont().getAdvance("N888  ");
        this.waitXOffset = Display.getWidth() - this.getFont().getAdvance("  99 min");
    }
    
    public void setData(StopData data) {
        this.data = data;
        this.setSize(data.arrivals.length);
        this.setSearchable(false);
        this.invalidate();
    }
    
    public void drawListRow(ListField listField, Graphics graphics, int index, int y, int width)
    {
        if (data == null || index >= data.arrivals.length)
            return;

        graphics.drawText(data.arrivals[index].routeName, spaceWidth, y, 0, width);
        graphics.drawText(data.arrivals[index].destination, destXOffset, y, 0, width);
        graphics.drawText(data.arrivals[index].estimatedWait, waitXOffset, y, Graphics.RIGHT, width - waitXOffset - spaceWidth);
    }
    
    public Object get(ListField listField, int index) {
        return data.arrivals[index];
    }
    
    public int getPreferredWidth(ListField list) {
        return Display.getWidth();
    }
    
    public int indexOfList(ListField listField, String prefix, int start) {
        return start;
    }
} 
