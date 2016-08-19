package it.uniroma2.giadd.aitm.models;

/**
 * Created by Alessandro Di Diego on 18/08/16.
 */

public class PcapParser {

    static {
        System.loadLibrary("pcap-parser");
    }

    public native String parsePcapFile(String path);

    public PcapParser() {

    }

    protected void onPacketParsed(String string) {

    }
}
