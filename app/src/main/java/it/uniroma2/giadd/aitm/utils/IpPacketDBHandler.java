package it.uniroma2.giadd.aitm.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import it.uniroma2.giadd.aitm.models.MyIpPacket;
import it.uniroma2.giadd.aitm.models.MyTcpPacket;
import it.uniroma2.giadd.aitm.models.MyTransportLayerPacket;
import it.uniroma2.giadd.aitm.models.MyUdpPacket;

import static it.uniroma2.giadd.aitm.models.MyIpPacket.IPPROTO_TCP;
import static it.uniroma2.giadd.aitm.models.MyIpPacket.IPPROTO_UDP;

/**
 * Created by Alessandro Di Diego on 07/11/16.
 */

public class IpPacketDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "IP_PACKETS";
    private static final String TABLE_IP = "TABLE_IP";

    public static final String KEY_ID = "_id";
    public static final String KEY_IP_SOURCE = "KEY_IP_SOURCE";
    public static final String KEY_IP_DESTINATION = "KEY_IP_DESTINATION";
    public static final String KEY_IP_HEADERLENGTH = "KEY_IP_HEADERLENGTH";
    public static final String KEY_IP_LENGTH = "KEY_IP_LENGTH";
    public static final String KEY_IP_ID = "KEY_IP_ID";
    public static final String KEY_IP_OFFSET = "KEY_IP_OFFSET";
    public static final String KEY_IP_PROTOCOL = "KEY_IP_PROTOCOL";
    public static final String KEY_IP_CHECKSUM = "KEY_IP_CHECKSUM";
    public static final String KEY_IP_TYPEOFSERVICE = "KEY_IP_TYPEOFSERVICE";
    public static final String KEY_IP_TTL = "KEY_IP_TTL";
    public static final String KEY_IP_VERSION = "KEY_IP_VERSION";
    public static final String KEY_DATA = "KEY_DATA";
    public static final String KEY_SOURCEPORT = "KEY_SOURCEPORT";
    public static final String KEY_DESTINATIONPORT = "KEY_DESTINATIONPORT";
    public static final String KEY_SEQUENCENUMBER = "KEY_SEQUENCENUMBER";
    public static final String KEY_ACKNUMBER = "KEY_ACKNUMBER";
    public static final String KEY_DATAOFFSET = "KEY_DATAOFFSET";
    public static final String KEY_WINDOW = "KEY_WINDOW";
    public static final String KEY_CHECKSUM = "KEY_CHECKSUM";
    public static final String KEY_URGENTPTR = "KEY_URGENTPTR";
    public static final String KEY_ACKSEQUENCE = "KEY_ACKSEQUENCE";
    public static final String KEY_CWR = "KEY_CWR";
    public static final String KEY_ECE = "KEY_ECE";
    public static final String KEY_FIN = "KEY_FIN";
    public static final String KEY_PSH = "KEY_PSH";
    public static final String KEY_RESERVEDBITS1 = "KEY_RESERVEDBITS1";
    public static final String KEY_RST = "KEY_RST";
    public static final String KEY_SYN = "KEY_SYN";
    public static final String KEY_URG = "KEY_URG";
    public static final String KEY_LENGTH = "KEY_LENGTH";

    public IpPacketDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_IPPACKET_TABLE = "CREATE TABLE " + TABLE_IP + "( "
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_IP_SOURCE + " TEXT, "
                + KEY_IP_DESTINATION + " TEXT, " + KEY_IP_HEADERLENGTH + " INTEGER, " + KEY_IP_LENGTH +
                " INTEGER, " + KEY_IP_ID + " INTEGER, " + KEY_IP_OFFSET + " INTEGER, " +
                KEY_IP_PROTOCOL + " INTEGER, " + KEY_IP_CHECKSUM + " INTEGER, " +
                KEY_IP_TYPEOFSERVICE + " INTEGER, " + KEY_IP_TTL + " INTEGER, " +
                KEY_IP_VERSION + " INTEGER, " + KEY_DATA + " BLOB, " +
                KEY_SOURCEPORT + " INTEGER, " + KEY_DESTINATIONPORT + " INTEGER, " +
                KEY_SEQUENCENUMBER + " INTEGER, " + KEY_ACKNUMBER + " INTEGER, " +
                KEY_DATAOFFSET + " INTEGER, " + KEY_WINDOW + " INTEGER, " +
                KEY_CHECKSUM + " INTEGER, " + KEY_URGENTPTR + " INTEGER, " +
                KEY_ACKSEQUENCE + " INTEGER, " + KEY_CWR + " INTEGER, " +
                KEY_ECE + " INTEGER, " + KEY_FIN + " INTEGER, " +
                KEY_PSH + " INTEGER, " + KEY_RESERVEDBITS1 + " INTEGER, " +
                KEY_RST + " INTEGER, " + KEY_SYN + " INTEGER, " +
                KEY_URG + " INTEGER, " + KEY_LENGTH + " INTEGER" + " )";
        db.execSQL(CREATE_IPPACKET_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop the old table if exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IP);
        // Create tables again
        onCreate(db);
    }

    public void resetDatabase() {
        getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + TABLE_IP);
        onCreate(getWritableDatabase());
    }

    public void addIpPacket(MyIpPacket ipPacket) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_IP_SOURCE, ipPacket.getSourceIp());
        values.put(KEY_IP_DESTINATION, ipPacket.getDestinationIp());
        values.put(KEY_IP_LENGTH, ipPacket.getLength());
        values.put(KEY_IP_PROTOCOL, ipPacket.getProtocol());
        values.put(KEY_DATA, ipPacket.getTransportLayerPacket().getData());
        if (ipPacket.getProtocol() == IPPROTO_UDP) {
            MyUdpPacket udpPacket = (MyUdpPacket) ipPacket.getTransportLayerPacket();
            values.put(KEY_SOURCEPORT, udpPacket.getSourcePort());
            values.put(KEY_DESTINATIONPORT, udpPacket.getDestinationPort());
            values.put(KEY_LENGTH, udpPacket.getLength());
            values.put(KEY_CHECKSUM, udpPacket.getChecksum());
        } else if (ipPacket.getProtocol() == IPPROTO_TCP) {
            MyTcpPacket tcpPacket = (MyTcpPacket) ipPacket.getTransportLayerPacket();
            values.put(KEY_SOURCEPORT, tcpPacket.getSourcePort());
            values.put(KEY_DESTINATIONPORT, tcpPacket.getDestinationPort());
            values.put(KEY_SEQUENCENUMBER, tcpPacket.getSequenceNumber());
            values.put(KEY_ACKNUMBER, tcpPacket.getAcknowledgmentNumber());
            values.put(KEY_DATAOFFSET, tcpPacket.getDataOffset());
            values.put(KEY_WINDOW, tcpPacket.getWindow());
            values.put(KEY_CHECKSUM, tcpPacket.getChecksum());
            values.put(KEY_URGENTPTR, tcpPacket.getUrgentPointer());
            values.put(KEY_ACKSEQUENCE, tcpPacket.getAckSequence());
            values.put(KEY_CWR, tcpPacket.getCwr());
            values.put(KEY_ECE, tcpPacket.getEce());
            values.put(KEY_FIN, tcpPacket.getFin());
            values.put(KEY_PSH, tcpPacket.getPsh());
            values.put(KEY_RESERVEDBITS1, tcpPacket.getReservedBits1());
            values.put(KEY_RST, tcpPacket.getRst());
            values.put(KEY_SYN, tcpPacket.getSyn());
            values.put(KEY_URG, tcpPacket.getUrg());
        }

        db.insert(TABLE_IP, null, values); //Insert query to store the record in the database
        db.close();
    }

    public Cursor getAllIpPacketsCursor() {
        return getReadableDatabase().query(TABLE_IP, null, null, null, null, null, null, null);
    }

    public MyIpPacket getIpPacket(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_IP, null, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor == null) return null;

        MyIpPacket ipPacket = new MyIpPacket();

        cursor.moveToFirst();
        ipPacket.setSourceIp(cursor.getString(cursor.getColumnIndex(KEY_IP_SOURCE)));
        ipPacket.setDestinationIp(cursor.getString(cursor.getColumnIndex(KEY_IP_DESTINATION)));
        ipPacket.setHeaderLength(cursor.getInt(cursor.getColumnIndex(KEY_IP_HEADERLENGTH)));
        ipPacket.setLength(cursor.getInt(cursor.getColumnIndex(KEY_IP_LENGTH)));
        ipPacket.setId(cursor.getInt(cursor.getColumnIndex(KEY_IP_ID)));
        ipPacket.setOffset(cursor.getShort(cursor.getColumnIndex(KEY_IP_OFFSET)));
        ipPacket.setProtocol(cursor.getShort(cursor.getColumnIndex(KEY_IP_PROTOCOL)));
        ipPacket.setChecksum(cursor.getInt(cursor.getColumnIndex(KEY_IP_CHECKSUM)));
        ipPacket.setTypeOfService(cursor.getShort(cursor.getColumnIndex(KEY_IP_TYPEOFSERVICE)));
        ipPacket.setTtl(cursor.getShort(cursor.getColumnIndex(KEY_IP_TTL)));
        ipPacket.setVersion(cursor.getLong(cursor.getColumnIndex(KEY_IP_VERSION)));
        MyTransportLayerPacket transportLayerPacket;
        if (ipPacket.getProtocol() == MyIpPacket.IPPROTO_TCP) {
            MyTcpPacket tcpPacket = new MyTcpPacket();
            tcpPacket.setSourcePort(cursor.getInt(cursor.getColumnIndex(KEY_SOURCEPORT)));
            tcpPacket.setDestinationPort(cursor.getInt(cursor.getColumnIndex(KEY_DESTINATIONPORT)));
            tcpPacket.setSequenceNumber(cursor.getLong(cursor.getColumnIndex(KEY_SEQUENCENUMBER)));
            tcpPacket.setAcknowledgmentNumber(cursor.getLong(cursor.getColumnIndex(KEY_ACKNUMBER)));
            tcpPacket.setDataOffset(cursor.getInt(cursor.getColumnIndex(KEY_DATAOFFSET)));
            tcpPacket.setWindow(cursor.getInt(cursor.getColumnIndex(KEY_WINDOW)));
            tcpPacket.setChecksum(cursor.getInt(cursor.getColumnIndex(KEY_CHECKSUM)));
            tcpPacket.setUrgentPointer(cursor.getInt(cursor.getColumnIndex(KEY_URGENTPTR)));
            tcpPacket.setAckSequence(cursor.getLong(cursor.getColumnIndex(KEY_ACKSEQUENCE)));
            tcpPacket.setCwr(cursor.getInt(cursor.getColumnIndex(KEY_CWR)));
            tcpPacket.setEce(cursor.getInt(cursor.getColumnIndex(KEY_ECE)));
            tcpPacket.setFin(cursor.getInt(cursor.getColumnIndex(KEY_FIN)));
            tcpPacket.setPsh(cursor.getInt(cursor.getColumnIndex(KEY_PSH)));
            tcpPacket.setReservedBits1(cursor.getInt(cursor.getColumnIndex(KEY_RESERVEDBITS1)));
            tcpPacket.setRst(cursor.getInt(cursor.getColumnIndex(KEY_RST)));
            tcpPacket.setSyn(cursor.getInt(cursor.getColumnIndex(KEY_SYN)));
            tcpPacket.setUrg(cursor.getInt(cursor.getColumnIndex(KEY_URG)));
            transportLayerPacket = tcpPacket;
        } else if (ipPacket.getProtocol() == MyIpPacket.IPPROTO_UDP) {
            MyUdpPacket udpPacket = new MyUdpPacket();
            udpPacket.setSourcePort(cursor.getInt(cursor.getColumnIndex(KEY_SOURCEPORT)));
            udpPacket.setDestinationPort(cursor.getInt(cursor.getColumnIndex(KEY_DESTINATIONPORT)));
            udpPacket.setChecksum(cursor.getInt(cursor.getColumnIndex(KEY_CHECKSUM)));
            udpPacket.setLength(cursor.getInt(cursor.getColumnIndex(KEY_LENGTH)));
            transportLayerPacket = udpPacket;
        } else transportLayerPacket = new MyTransportLayerPacket();
        transportLayerPacket.setData(cursor.getBlob(cursor.getColumnIndex(KEY_DATA)));
        cursor.close();
        ipPacket.setTransportLayerPacket(transportLayerPacket);

        return ipPacket;
    }

    /*getUsersCount() will give the total number of records in the table*/
    public int getIpPacketCount() {
        String countQuery = "SELECT  * FROM " + TABLE_IP;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        return cursor.getCount();
    }

}