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

    public ArpTableReader(MACAddressVendorLookup initializedMACAddressVendorLookup) {
        this.macLut = initializedMACAddressVendorLookup;
    }

    public ArrayList<NetworkHost> readAddresses() {
        ArrayList<NetworkHost> networkHosts = new ArrayList<>();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted.length >= 4) {
                    String ip = splitted[0];
                    String mac = splitted[3];
                    if (mac.matches("..:..:..:..:..:..")) {
                        String vendor = macLut.getVendor(mac);
                        MACAddress macAddress = new MACAddress(mac, vendor);
                        NetworkHost networkHost = new NetworkHost(ip, macAddress, true);
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
