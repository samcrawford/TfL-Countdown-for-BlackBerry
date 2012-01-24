package com.acme.countdown;

import net.rim.device.api.util.Persistable;

public class Favourite implements Persistable
{
    public String stopCode;
    public String name;
    
    public Favourite() {}
    public Favourite(String stopCode, String name) {
        this.stopCode = stopCode;
        this.name = name;
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer(name.length()+10);
        sb.append(stopCode);
        sb.append(" - ");
        sb.append(name);
        return sb.toString();
    }
} 
