package it.uniroma2.giadd.aitm.models;

/**
 * Created by Alessandro Di Diego on 22/08/16.
 */

import android.os.Parcel;

/**
 * 0                   1                   2                   3
 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |          Source Port          |       Destination Port        |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |            Length             |           Checksum            |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |   .... data ....                                              |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * UDP Header Format
 */

public class MyUdpPacket extends MyTransportLayerPacket {
    private int sourcePort;
    private int destinationPort;
    private int length;
    private int checksum;

    public MyUdpPacket(byte[] data, int sourcePort, int destinationPort, int length, int checksum) {
        super(data);
        this.sourcePort = sourcePort;
        this.destinationPort = destinationPort;
        this.length = length;
        this.checksum = checksum;
    }

    @Override
    public String toString() {
        return "MyUdpPacket{" +
                "sourcePort=" + sourcePort +
                ", destinationPort=" + destinationPort +
                ", length=" + length +
                ", checksum=" + checksum +
                '}';
    }

    public int getSourcePort() {
        return sourcePort;
    }

    public void setSourcePort(int sourcePort) {
        this.sourcePort = sourcePort;
    }

    public int getDestinationPort() {
        return destinationPort;
    }

    public void setDestinationPort(int destinationPort) {
        this.destinationPort = destinationPort;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getChecksum() {
        return checksum;
    }

    public void setChecksum(int checksum) {
        this.checksum = checksum;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.sourcePort);
        dest.writeInt(this.destinationPort);
        dest.writeInt(this.length);
        dest.writeInt(this.checksum);
    }

    protected MyUdpPacket(Parcel in) {
        super(in);
        this.sourcePort = in.readInt();
        this.destinationPort = in.readInt();
        this.length = in.readInt();
        this.checksum = in.readInt();
    }

    public static final Creator<MyUdpPacket> CREATOR = new Creator<MyUdpPacket>() {
        @Override
        public MyUdpPacket createFromParcel(Parcel source) {
            return new MyUdpPacket(source);
        }

        @Override
        public MyUdpPacket[] newArray(int size) {
            return new MyUdpPacket[size];
        }
    };
}
