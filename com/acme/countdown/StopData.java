package com.acme.countdown;

import org.json.me.*;

public class StopData {
    
    StopDataItem[] arrivals;
    String lastUpdated;
    String[] criticalMessages;
    String[] importantMessages;
    String[] infoMessages;
    
    private StopData() { }
    
    public static StopData fromJSON(String jsonString) throws JSONException
    {
        StopData sd = new StopData();
        
        JSONObject outer = new JSONObject(jsonString);
        if (outer != null) {
            JSONArray arrivalsArray = outer.getJSONArray("arrivals");
            sd.arrivals = new StopDataItem[arrivalsArray.length()];
            for (int i=0; i<sd.arrivals.length; i++) {
                JSONObject a = arrivalsArray.getJSONObject(i);
                String destination = a.getString("destination");
                String estimatedWait = a.getString("estimatedWait");
                boolean isCancelled = a.getBoolean("isCancelled");
                boolean isRealTime = a.getBoolean("isRealTime");
                String routeId = a.getString("routeId");
                String routeName = a.getString("routeName");
                String scheduledTime = a.getString("scheduledTime");
                sd.arrivals[i] = new StopDataItem(destination, estimatedWait, isCancelled, isRealTime,
                                                  routeId, routeName, scheduledTime);
            }
            
            JSONObject serviceDisruptions = outer.getJSONObject("serviceDisruptions");
            if (serviceDisruptions != null) {
                JSONArray messagesArray = serviceDisruptions.getJSONArray("infoMessages");
                sd.infoMessages = new String[messagesArray.length()];
                for (int i=0; i<sd.infoMessages.length; i++) {
                    String msg = (String) messagesArray.getString(i);
                    sd.infoMessages[i] = msg;
                }
                
                messagesArray = serviceDisruptions.getJSONArray("importantMessages");
                sd.importantMessages = new String[messagesArray.length()];
                for (int i=0; i<sd.importantMessages.length; i++) {
                    String msg = (String) messagesArray.getString(i);
                    sd.importantMessages[i] = msg;
                }
                
                messagesArray = serviceDisruptions.getJSONArray("criticalMessages");
                sd.criticalMessages = new String[messagesArray.length()];
                for (int i=0; i<sd.criticalMessages.length; i++) {
                    String msg = (String) messagesArray.getString(i);
                    sd.criticalMessages[i] = msg;
                }
            }
            
            sd.lastUpdated = outer.getString("lastUpdated");
        }
        
        return sd;
    }
    
    /*
        "destination" : "Waterloo",
        "estimatedWait" : "28 min",
        "isCancelled" : false,
        "isRealTime" : true,
        "routeId" : "381",
        "routeName" : "381",
        "scheduledTime" : "11:07"
    */
    public static class StopDataItem {
        String destination;
        String estimatedWait;
        boolean isCancelled;
        boolean isRealTime;
        String routeId;
        String routeName;
        String scheduledTime;
        
        public StopDataItem(String destination, String estimatedWait, boolean isCancelled,
                            boolean isRealTime, String routeId, String routeName,
                            String scheduledTime) {
            this.destination = destination;
            this.estimatedWait = estimatedWait;
            this.isCancelled = isCancelled;
            this.isRealTime = isRealTime;
            this.routeId = routeId;
            this.routeName = routeName;
            this.scheduledTime = scheduledTime;
        }
    }
} 
