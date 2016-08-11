package it.uniroma2.giadd.aitm.models;

/**
 * Created by Alessandro Di Diego on 10/08/16.
 */

public class NetworkHost {

    private String ip;
    private String hostname;
    private MACAddress macAddress;
    private boolean reachable;

    public NetworkHost(String ip, String hostname, MACAddress macAddress, boolean reachable) {
        this.ip = ip;
        if (hostname == null) hostname = "";
        this.hostname = hostname;
        this.macAddress = macAddress;
        this.reachable = reachable;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof NetworkHost) {
            NetworkHost networkHost = (NetworkHost) obj;
            if (networkHost.getIp().equals(this.getIp()) && networkHost.getHostname().equals(this.getHostname()) && networkHost.getMacAddress().equals(this.getMacAddress()))
                return true;
        }
        return false;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
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
