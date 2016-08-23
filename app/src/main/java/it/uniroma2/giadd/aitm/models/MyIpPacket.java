package it.uniroma2.giadd.aitm.models;

/**
 * Created by Alessandro Di Diego on 22/08/16.
 */

/**
 * 0                   1                   2                   3
 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |Version|  IHL  |Type of Service|          Total Length         |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |         Identification        |Flags|      Fragment Offset    |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |  Time to Live |    Protocol   |         Header Checksum       |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                       Source Address                          |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                    Destination Address                        |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                    Options                    |    Padding    |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 */

public class MyIpPacket {
    private String sourceIp;
    private String destinationIp;
    private int headerLength;
    private int length;
    private int id;
    private short offset;
    private short protocol;
    private int checksum;
    private short typeOfService;
    private short ttl;
    private long version;
    private MyTransportLayerPacket transportLayerPacket;

    public MyIpPacket(String sourceIp, String destinationIp, int headerLength, int length, int id, short offset, short protocol, int checksum, short typeOfService, short ttl, long version, MyTransportLayerPacket transportLayerPacket) {
        this.sourceIp = sourceIp;
        this.destinationIp = destinationIp;
        this.headerLength = headerLength;
        this.length = length;
        this.id = id;
        this.offset = offset;
        this.protocol = protocol;
        this.checksum = checksum;
        this.typeOfService = typeOfService;
        this.ttl = ttl;
        this.version = version;
        this.transportLayerPacket = transportLayerPacket;
    }

    @Override
    public String toString() {
        return "MyIpPacket{" +
                "sourceIp='" + sourceIp + '\'' +
                ", destinationIp='" + destinationIp + '\'' +
                ", headerLength=" + headerLength +
                ", length=" + length +
                ", id=" + id +
                ", offset=" + offset +
                ", protocol=" + protocol +
                ", checksum=" + checksum +
                ", typeOfService=" + typeOfService +
                ", ttl=" + ttl +
                ", version=" + version +
                ", transportLayerPacket=" + transportLayerPacket +
                '}';
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    public String getDestinationIp() {
        return destinationIp;
    }

    public void setDestinationIp(String destinationIp) {
        this.destinationIp = destinationIp;
    }

    public int getHeaderLength() {
        return headerLength;
    }

    public void setHeaderLength(int headerLength) {
        this.headerLength = headerLength;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public short getOffset() {
        return offset;
    }

    public void setOffset(short offset) {
        this.offset = offset;
    }

    public short getProtocol() {
        return protocol;
    }

    public void setProtocol(short protocol) {
        this.protocol = protocol;
    }

    public int getChecksum() {
        return checksum;
    }

    public void setChecksum(int checksum) {
        this.checksum = checksum;
    }

    public short getTypeOfService() {
        return typeOfService;
    }

    public void setTypeOfService(short typeOfService) {
        this.typeOfService = typeOfService;
    }

    public short getTtl() {
        return ttl;
    }

    public void setTtl(short ttl) {
        this.ttl = ttl;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public MyTransportLayerPacket getTransportLayerPacket() {
        return transportLayerPacket;
    }

    public void setTransportLayerPacket(MyTransportLayerPacket transportLayerPacket) {
        this.transportLayerPacket = transportLayerPacket;
    }
}
