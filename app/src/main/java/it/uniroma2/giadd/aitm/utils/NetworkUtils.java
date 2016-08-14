package it.uniroma2.giadd.aitm.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import it.uniroma2.giadd.aitm.models.NetworkHost;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by Alessandro Di Diego on 11/08/16.
 */

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getName();

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

    public static String getDeviceMacAddr(Context context) {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                String interfaceName;
                interfaceName = getActiveInterface(context);
                Log.d(TAG, "Using interface: " + interfaceName);
                if (interfaceName == null) {
                    return "02:00:00:00:00:00";
                }
                if (!nif.getName().equalsIgnoreCase(interfaceName)) continue;

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

    public static String getWifiGateway(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        InetAddress gatewayInet = IPUtils.intToInetAddress(dhcpInfo.gateway);
        String gateway = null;
        // mark gateway host
        if (gatewayInet != null) {
            gateway = gatewayInet.getHostAddress();
        }
        return gateway;
    }

    public static List<NetworkInterface> getNetworkInterfacesList() {
        List<NetworkInterface> interfaces = new LinkedList<>();
        try {
            interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return interfaces;
    }

    public static String getActiveInterface(Context context) throws SocketException {
        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        @SuppressLint("HardwareIds")
        String wifiMacString = wifiInfo.getMacAddress();
        // on Android 6 mac address is hidden for security reasons and cannot be retrieved by WifiInfo
        // the dummy mac address returned is 02:00:00:00:00:00
        if (PreferencesUtils.getInterfaceName(context) != null) {
            return PreferencesUtils.getInterfaceName(context);
        }
        if (wifiMacString.equals("02:00:00:00:00:00")) {
            for (NetworkInterface networkInterface : getNetworkInterfacesList()) {
                if (networkInterface.getName().contains("wlan") || networkInterface.getName().contains("wls")) {
                    PreferencesUtils.setInterfaceName(context, networkInterface.getName());
                    return networkInterface.getName();
                }

            }
            for (NetworkInterface networkInterface : getNetworkInterfacesList()) {
                if (networkInterface.getName().contains("eth")) {
                    PreferencesUtils.setInterfaceName(context, networkInterface.getName());
                    return networkInterface.getName();
                }
            }
            PreferencesUtils.setInterfaceName(context, getNetworkInterfacesList().get(0).getName());
            return getNetworkInterfacesList().get(0).getName();
        }
        for (NetworkInterface currentInterface : getNetworkInterfacesList()) {
            byte[] hardwareAddress = currentInterface.getHardwareAddress();
            if (hardwareAddress != null) {
                String currentMac = byteArrayToMacAddress(hardwareAddress);
                Log.d("DBG", "current interface: " + currentInterface.getName() + " mac: " + currentMac + " wifimac: " + wifiMacString);
                if (currentMac.toLowerCase(Locale.ENGLISH).replaceAll("\\-", "\\:").equals(wifiMacString.toLowerCase(Locale.ENGLISH).replaceAll("\\-", "\\:"))) {
                    return currentInterface.getName();
                }
            }
        }
        // ERROR
        Log.e(TAG, "Error: no interface found!");
        return "wlan0";
    }


    private static String byteArrayToMacAddress(byte[] mac) {
        if (mac == null)
            return null;

        StringBuilder sb = new StringBuilder(18);
        for (byte b : mac) {
            if (sb.length() > 0)
                sb.append(':');
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static byte[] macAddressToByteArray(String macString) {
        String[] mac = macString.split("[:\\s-]");
        byte[] macAddress = new byte[6];
        for (int i = 0; i < mac.length; i++) {
            macAddress[i] = Integer.decode("0x" + mac[i]).byteValue();
        }

        return macAddress;
    }

}
