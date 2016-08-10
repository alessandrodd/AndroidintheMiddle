package it.uniroma2.giadd.aitm.utils;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Alessandro Di Diego on 10/08/16.
 */

public class ListIPs {
    private static final String TAG = ListIPs.class.getName();

    private static int ipNum(int a, int b, int c, int d) {
        int ip = ((a * 256 | b) * 256 | c) * 256 | d;
        return ip;
    }

    private static List<String> listIPs(int ip, int mask) {
        List<String> ipList = new LinkedList<>();
        int index;
        int count = 1;
        int temp = mask;
        long a, b, c, d;

        while ((temp & 1) == 0) {
            count *= 2;
            temp >>= 1;
        }

        for (index = 1; index < count - 1; index++) {
            long newIP = ((ip & mask) | index) & 0xFFFFFFFFL;


            d = newIP & 0xFF;
            c = (newIP / 256) & 0xFF;
            b = (newIP / 65536) & 0xFF;
            a = (newIP / 16777216) & 0xFF;

            ipList.add("" + a + "." + b + "." + c + "." + d);
        }
        return ipList;
    }

    public static List<String> listIPs(String ip, String mask) {
        String[] ipComponents = ip.split(".");
        String[] maskComponents = mask.split(".");
        if (ipComponents.length < 4 || maskComponents.length < 4) {
            Log.e(TAG, "invalid ip or mask!");
            return null;
        }
        return listIPs(ipNum(Integer.parseInt(ipComponents[0]), Integer.parseInt(ipComponents[1]), Integer.parseInt(ipComponents[2]), Integer.parseInt(ipComponents[3])), ipNum(Integer.parseInt(maskComponents[0]), Integer.parseInt(maskComponents[1]), Integer.parseInt(maskComponents[2]), Integer.parseInt(maskComponents[3])));
    }

}