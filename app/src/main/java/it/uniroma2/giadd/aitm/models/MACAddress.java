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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MACAddress that = (MACAddress) o;

        return address != null ? address.equals(that.address) : that.address == null && (vendor != null ? vendor.equals(that.vendor) : that.vendor == null);

    }

    @Override
    public int hashCode() {
        int result = address != null ? address.hashCode() : 0;
        result = 31 * result + (vendor != null ? vendor.hashCode() : 0);
        return result;
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
