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

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof NetworkHost) {
            NetworkHost networkHost = (NetworkHost) obj;
            if (networkHost.getIp().equals(this.getIp()) && networkHost.getMacAddress().equals(this.getMacAddress()))
                return true;
        }
        return false;
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
