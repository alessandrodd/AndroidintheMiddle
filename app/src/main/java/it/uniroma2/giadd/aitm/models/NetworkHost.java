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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NetworkHost that = (NetworkHost) o;

        return reachable == that.reachable && (ip != null ? ip.equals(that.ip) : that.ip == null && (hostname != null ? hostname.equals(that.hostname) : that.hostname == null && (macAddress != null ? macAddress.equals(that.macAddress) : that.macAddress == null)));

    }

    @Override
    public int hashCode() {
        int result = ip != null ? ip.hashCode() : 0;
        result = 31 * result + (hostname != null ? hostname.hashCode() : 0);
        result = 31 * result + (macAddress != null ? macAddress.hashCode() : 0);
        result = 31 * result + (reachable ? 1 : 0);
        return result;
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
