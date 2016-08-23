package it.uniroma2.giadd.aitm.models;

/**
 * Created by Alessandro Di Diego on 22/08/16.
 */

import it.uniroma2.giadd.aitm.models.MyTransportLayerPacket;

/**
 * 0                   1                   2                   3
 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |          Source Port          |       Destination Port        |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                        Sequence Number                        |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                    Acknowledgment Number                      |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |  Data |           |U|A|P|R|S|F|                               |
 * | Offset| Reserved  |R|C|S|S|Y|I|            Window             |
 * |       |           |G|K|H|T|N|N|                               |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |           Checksum            |         Urgent Pointer        |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                    Options                    |    Padding    |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                             data                              |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * TCP Header Format
 */

public class MyTcpPacket extends MyTransportLayerPacket {
    private int sourcePort;
    private int destinationPort;
    private long sequenceNumber;
    private long acknowledgmentNumber;
    private int dataOffset;
    private int window;
    private int checksum;
    private int urgentPointer;
    private long ackSequence;
    private int cwr;
    private int ece;
    private int fin;
    private int psh;
    private int reservedBits1;
    private int rst;
    private int syn;
    private int urg;

    public MyTcpPacket(byte[] data, int sourcePort, int destinationPort, long sequenceNumber, long acknowledgmentNumber, int dataOffset, int window, int checksum, int urgentPointer, long ackSequence, int cwr, int ece, int fin, int psh, int reservedBits1, int rst, int syn, int urg) {
        super(data);
        this.sourcePort = sourcePort;
        this.destinationPort = destinationPort;
        this.sequenceNumber = sequenceNumber;
        this.acknowledgmentNumber = acknowledgmentNumber;
        this.dataOffset = dataOffset;
        this.window = window;
        this.checksum = checksum;
        this.urgentPointer = urgentPointer;
        this.ackSequence = ackSequence;
        this.cwr = cwr;
        this.ece = ece;
        this.fin = fin;
        this.psh = psh;
        this.reservedBits1 = reservedBits1;
        this.rst = rst;
        this.syn = syn;
        this.urg = urg;
    }

    @Override
    public String toString() {
        return "MyTcpPacket{" +
                "sourcePort=" + sourcePort +
                ", destinationPort=" + destinationPort +
                ", sequenceNumber=" + sequenceNumber +
                ", acknowledgmentNumber=" + acknowledgmentNumber +
                ", dataOffset=" + dataOffset +
                ", window=" + window +
                ", checksum=" + checksum +
                ", urgentPointer=" + urgentPointer +
                ", ackSequence=" + ackSequence +
                ", cwr=" + cwr +
                ", ece=" + ece +
                ", fin=" + fin +
                ", psh=" + psh +
                ", reservedBits1=" + reservedBits1 +
                ", rst=" + rst +
                ", syn=" + syn +
                ", urg=" + urg +
                '}';
    }
}
