package it.uniroma2.giadd.aitm.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Alessandro Di Diego on 10/08/16.
 */

public class NetworkHost implements Parcelable {

    private String ip;
    private String hostname;
    private MACAddress macAddress;
    private boolean reachable;
    private boolean gateway;

    public NetworkHost(String ip, String hostname, MACAddress macAddress, boolean reachable) {
        this.ip = ip;
        if (hostname == null) hostname = "";
        this.hostname = hostname;
        this.macAddress = macAddress;
        this.reachable = reachable;
    }

    public NetworkHost(String ip, String hostname, MACAddress macAddress, boolean reachable, boolean gateway) {
        this.ip = ip;
        if (hostname == null) hostname = "";
        this.hostname = hostname;
        this.macAddress = macAddress;
        this.reachable = reachable;
        this.gateway = gateway;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NetworkHost that = (NetworkHost) o;

        return reachable == that.reachable && gateway == that.gateway && (ip != null ? ip.equals(that.ip) : that.ip == null && (hostname != null ? hostname.equals(that.hostname) : that.hostname == null && (macAddress != null ? macAddress.equals(that.macAddress) : that.macAddress == null)));

    }

    @Override
    public int hashCode() {
        int result = ip != null ? ip.hashCode() : 0;
        result = 31 * result + (hostname != null ? hostname.hashCode() : 0);
        result = 31 * result + (macAddress != null ? macAddress.hashCode() : 0);
        result = 31 * result + (reachable ? 1 : 0);
        result = 31 * result + (gateway ? 1 : 0);
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

    public boolean isGateway() {
        return gateway;
    }

    public void setGateway(boolean gateway) {
        this.gateway = gateway;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.ip);
        dest.writeString(this.hostname);
        dest.writeParcelable(this.macAddress, flags);
        dest.writeByte(this.reachable ? (byte) 1 : (byte) 0);
        dest.writeByte(this.gateway ? (byte) 1 : (byte) 0);
    }

    protected NetworkHost(Parcel in) {
        this.ip = in.readString();
        this.hostname = in.readString();
        this.macAddress = in.readParcelable(MACAddress.class.getClassLoader());
        this.reachable = in.readByte() != 0;
        this.gateway = in.readByte() != 0;
    }

    public static final Creator<NetworkHost> CREATOR = new Creator<NetworkHost>() {
        @Override
        public NetworkHost createFromParcel(Parcel source) {
            return new NetworkHost(source);
        }

        @Override
        public NetworkHost[] newArray(int size) {
            return new NetworkHost[size];
        }
    };
}
