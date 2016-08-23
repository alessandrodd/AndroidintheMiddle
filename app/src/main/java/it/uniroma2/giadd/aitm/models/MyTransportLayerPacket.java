package it.uniroma2.giadd.aitm.models;

import java.util.Arrays;

/**
 * Created by Alessandro Di Diego on 22/08/16.
 */

public class MyTransportLayerPacket {
    private byte[] data;

    public MyTransportLayerPacket(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "MyTransportLayerPacket{" +
                "data=" + Arrays.toString(data) +
                '}';
    }
}
