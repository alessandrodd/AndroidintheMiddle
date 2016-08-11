package it.uniroma2.giadd.aitm.models;

/**
 * Created by Alessandro Di Diego on 10/08/16.
 */

public class MACAddress {
    private String address;
    private String vendor;

    public MACAddress() {
    }

    public MACAddress(String address) {
        this.address = address;
    }

    public MACAddress(String address, String vendor) {
        this.address = address;
        this.vendor = vendor;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof MACAddress) {
            MACAddress macAddress = (MACAddress) obj;
            if (macAddress.getAddress().equals(this.address) && macAddress.getVendor().equals(this.vendor))
                return true;
        }
        return false;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }
}
