package it.uniroma2.giadd.aitm.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Alessandro Di Diego on 10/08/16.
 */

public class IPUtils {
    private static final String TAG = IPUtils.class.getName();

    public static InetAddress intToInetAddress(int hostAddress) {
        byte[] addressBytes = {(byte) (0xff & hostAddress),
                (byte) (0xff & (hostAddress >> 8)),
                (byte) (0xff & (hostAddress >> 16)),
                (byte) (0xff & (hostAddress >> 24))};

        try {
            return InetAddress.getByAddress(addressBytes);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

}