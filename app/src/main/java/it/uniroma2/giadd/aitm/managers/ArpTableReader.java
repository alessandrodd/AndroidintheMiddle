package it.uniroma2.giadd.aitm.managers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import it.uniroma2.giadd.aitm.models.MACAddress;
import it.uniroma2.giadd.aitm.models.NetworkHost;
import it.uniroma2.giadd.aitm.utils.MACAddressVendorLookup;

/**
 * Created by Alessandro Di Diego on 10/08/16.
 */

public class ArpTableReader {

    private MACAddressVendorLookup macLut;
    private String gateway;

    public ArpTableReader(MACAddressVendorLookup initializedMACAddressVendorLookup) {
        this.macLut = initializedMACAddressVendorLookup;
    }

    public ArpTableReader(MACAddressVendorLookup initializedMACAddressVendorLookup, String gateway) {
        this.macLut = initializedMACAddressVendorLookup;
        this.gateway = gateway;
    }

    public ArrayList<NetworkHost> readAddresses() {
        ArrayList<NetworkHost> networkHosts = new ArrayList<>();
        BufferedReader bufferedReader = null;
        try {
            // Open the ARP table located at /proc/net/arp
            bufferedReader = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted.length >= 4) {
                    String ip = splitted[0];
                    String mac = splitted[3];
                    if (mac.matches("..:..:..:..:..:..") && !mac.equals("00:00:00:00:00:00")) {
                        String vendor = macLut.getVendor(mac);
                        MACAddress macAddress = new MACAddress(mac, vendor);
                        NetworkHost networkHost = new NetworkHost(ip, "", macAddress, true);
                        if (gateway != null && gateway.equals(ip)) networkHost.setGateway(true);
                        networkHosts.add(networkHost);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return networkHosts;
    }
}
