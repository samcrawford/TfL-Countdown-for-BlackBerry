package com.acme.countdown;

import net.rim.device.api.system.*;
import net.rim.device.api.servicebook.*;

public class ConnectionUtils {
    public static String getConnectionSuffix()
    {
        String connSuffix;
        if (DeviceInfo.isSimulator()) {
            connSuffix = ";deviceside=true";
        } else
        if ( (WLANInfo.getWLANState() == WLANInfo.WLAN_STATE_CONNECTED) &&
            RadioInfo.areWAFsSupported(RadioInfo.WAF_WLAN)) {
            connSuffix=";interface=wifi";
        } else {
            String uid = null;
            ServiceBook sb = ServiceBook.getSB();
            ServiceRecord[] records = sb.findRecordsByCid("WPTCP");
            for (int i = 0; i < records.length; i++) {
                if (records[i].isValid() && !records[i].isDisabled()) {
                    if (records[i].getUid() != null &&
                        records[i].getUid().length() != 0) {
                        if ((records[i].getCid().toLowerCase().indexOf("wptcp") != -1) &&
                            (records[i].getUid().toLowerCase().indexOf("wifi") == -1) &&
                            (records[i].getUid().toLowerCase().indexOf("mms") == -1)   ) {
                            uid = records[i].getUid();
                            break;
                        }
                    }
                }
            }
            if (uid != null) {
                // WAP2 Connection
                connSuffix = ";ConnectionUID="+uid;
            } else {
                connSuffix = ";deviceside=true";
            }
        }
        return connSuffix;
    }
} 
