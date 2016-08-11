package it.uniroma2.giadd.aitm.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import it.uniroma2.giadd.aitm.models.NetworkHost;

/**
 * Created by Alessandro Di Diego on 11/08/16.
 */

public class NetworkUtils {

    public static final int TYPE_WIFI = ConnectivityManager.TYPE_WIFI;
    public static final int TYPE_MOBILE = ConnectivityManager.TYPE_MOBILE;
    public static final int NO_CONNECTION = -1;


    public static int checkActiveConnectionType(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                return TYPE_WIFI;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                return TYPE_MOBILE;
            }
        }
        return NO_CONNECTION;
    }

    public static String getDeviceMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString().toLowerCase(Locale.ENGLISH);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }

}
