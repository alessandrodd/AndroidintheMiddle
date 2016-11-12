package it.uniroma2.giadd.aitm.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

/**
 * Created by Alessandro Di Diego on 22/08/16.
 */

public class MyTransportLayerPacket implements Parcelable {
    private byte[] data;

    public MyTransportLayerPacket() {
    }

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByteArray(this.data);
    }

    protected MyTransportLayerPacket(Parcel in) {
        this.data = in.createByteArray();
    }

    public static final Creator<MyTransportLayerPacket> CREATOR = new Creator<MyTransportLayerPacket>() {
        @Override
        public MyTransportLayerPacket createFromParcel(Parcel source) {
            return new MyTransportLayerPacket(source);
        }

        @Override
        public MyTransportLayerPacket[] newArray(int size) {
            return new MyTransportLayerPacket[size];
        }
    };
}
