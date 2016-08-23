package it.uniroma2.giadd.aitm.models;

/**
 * Created by Alessandro Di Diego on 22/08/16.
 */

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
}
