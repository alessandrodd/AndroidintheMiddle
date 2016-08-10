package it.uniroma2.giadd.aitm.models;

/**
 * Created by Alessandro Di Diego on 10/08/16.
 */

public class NetworkHost {

    private String ip;
    private MACAddress macAddress;
    private boolean reachable;

    public NetworkHost() {
    }

    public NetworkHost(String ip, MACAddress macAddress, boolean reachable) {
        this.ip = ip;
        this.macAddress = macAddress;
        this.reachable = reachable;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public MACAddress getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(MACAddress macAddress) {
        this.macAddress = macAddress;
    }

    public boolean isReachable() {
        return reachable;
    }

    public void setReachable(boolean reachable) {
        this.reachable = reachable;
    }
}
