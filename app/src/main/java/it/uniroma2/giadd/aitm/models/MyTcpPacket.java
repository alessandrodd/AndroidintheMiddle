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

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public long getAcknowledgmentNumber() {
        return acknowledgmentNumber;
    }

    public void setAcknowledgmentNumber(long acknowledgmentNumber) {
        this.acknowledgmentNumber = acknowledgmentNumber;
    }

    public int getDataOffset() {
        return dataOffset;
    }

    public void setDataOffset(int dataOffset) {
        this.dataOffset = dataOffset;
    }

    public int getWindow() {
        return window;
    }

    public void setWindow(int window) {
        this.window = window;
    }

    public int getChecksum() {
        return checksum;
    }

    public void setChecksum(int checksum) {
        this.checksum = checksum;
    }

    public int getUrgentPointer() {
        return urgentPointer;
    }

    public void setUrgentPointer(int urgentPointer) {
        this.urgentPointer = urgentPointer;
    }

    public long getAckSequence() {
        return ackSequence;
    }

    public void setAckSequence(long ackSequence) {
        this.ackSequence = ackSequence;
    }

    public int getCwr() {
        return cwr;
    }

    public void setCwr(int cwr) {
        this.cwr = cwr;
    }

    public int getEce() {
        return ece;
    }

    public void setEce(int ece) {
        this.ece = ece;
    }

    public int getFin() {
        return fin;
    }

    public void setFin(int fin) {
        this.fin = fin;
    }

    public int getPsh() {
        return psh;
    }

    public void setPsh(int psh) {
        this.psh = psh;
    }

    public int getReservedBits1() {
        return reservedBits1;
    }

    public void setReservedBits1(int reservedBits1) {
        this.reservedBits1 = reservedBits1;
    }

    public int getRst() {
        return rst;
    }

    public void setRst(int rst) {
        this.rst = rst;
    }

    public int getSyn() {
        return syn;
    }

    public void setSyn(int syn) {
        this.syn = syn;
    }

    public int getUrg() {
        return urg;
    }

    public void setUrg(int urg) {
        this.urg = urg;
    }
}
