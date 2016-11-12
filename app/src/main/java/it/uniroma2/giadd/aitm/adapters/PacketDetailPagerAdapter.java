package it.uniroma2.giadd.aitm.adapters;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import it.uniroma2.giadd.aitm.R;
import it.uniroma2.giadd.aitm.fragments.PacketDetailFragment;
import it.uniroma2.giadd.aitm.models.MyIpPacket;
import it.uniroma2.giadd.aitm.models.MyTcpPacket;
import it.uniroma2.giadd.aitm.models.MyTransportLayerPacket;
import it.uniroma2.giadd.aitm.models.MyUdpPacket;

import static it.uniroma2.giadd.aitm.utils.IpPacketDBHandler.KEY_ACKNUMBER;
import static it.uniroma2.giadd.aitm.utils.IpPacketDBHandler.KEY_ACKSEQUENCE;
import static it.uniroma2.giadd.aitm.utils.IpPacketDBHandler.KEY_CHECKSUM;
import static it.uniroma2.giadd.aitm.utils.IpPacketDBHandler.KEY_CWR;
import static it.uniroma2.giadd.aitm.utils.IpPacketDBHandler.KEY_DATA;
import static it.uniroma2.giadd.aitm.utils.IpPacketDBHandler.KEY_DATAOFFSET;
import static it.uniroma2.giadd.aitm.utils.IpPacketDBHandler.KEY_DESTINATIONPORT;
import static it.uniroma2.giadd.aitm.utils.IpPacketDBHandler.KEY_ECE;
import static it.uniroma2.giadd.aitm.utils.IpPacketDBHandler.KEY_FIN;
import static it.uniroma2.giadd.aitm.utils.IpPacketDBHandler.KEY_IP_CHECKSUM;
import static it.uniroma2.giadd.aitm.utils.IpPacketDBHandler.KEY_IP_DESTINATION;
import static it.uniroma2.giadd.aitm.utils.IpPacketDBHandler.KEY_IP_HEADERLENGTH;
import static it.uniroma2.giadd.aitm.utils.IpPacketDBHandler.KEY_IP_ID;
import static it.uniroma2.giadd.aitm.utils.IpPacketDBHandler.KEY_IP_LENGTH;
import static it.uniroma2.giadd.aitm.utils.IpPacketDBHandler.KEY_IP_OFFSET;
import static it.uniroma2.giadd.aitm.utils.IpPacketDBHandler.KEY_IP_PROTOCOL;
import static it.uniroma2.giadd.aitm.utils.IpPacketDBHandler.KEY_IP_SOURCE;
import static it.uniroma2.giadd.aitm.utils.IpPacketDBHandler.KEY_IP_TTL;
import static it.uniroma2.giadd.aitm.utils.IpPacketDBHandler.KEY_IP_TYPEOFSERVICE;
import static it.uniroma2.giadd.aitm.utils.IpPacketDBHandler.KEY_IP_VERSION;
import static it.uniroma2.giadd.aitm.utils.IpPacketDBHandler.KEY_LENGTH;
import static it.uniroma2.giadd.aitm.utils.IpPacketDBHandler.KEY_PSH;
import static it.uniroma2.giadd.aitm.utils.IpPacketDBHandler.KEY_RESERVEDBITS1;
import static it.uniroma2.giadd.aitm.utils.IpPacketDBHandler.KEY_RST;
import static it.uniroma2.giadd.aitm.utils.IpPacketDBHandler.KEY_SEQUENCENUMBER;
import static it.uniroma2.giadd.aitm.utils.IpPacketDBHandler.KEY_SOURCEPORT;
import static it.uniroma2.giadd.aitm.utils.IpPacketDBHandler.KEY_SYN;
import static it.uniroma2.giadd.aitm.utils.IpPacketDBHandler.KEY_URG;
import static it.uniroma2.giadd.aitm.utils.IpPacketDBHandler.KEY_URGENTPTR;
import static it.uniroma2.giadd.aitm.utils.IpPacketDBHandler.KEY_WINDOW;

/**
 * Created by Alessandro Di Diego on 09/09/16.
 */

public class PacketDetailPagerAdapter extends CursorFragmentStatePagerAdapter {

    public PacketDetailPagerAdapter(Context context, FragmentManager fm, Cursor cursor) {
        super(context, fm, cursor);
    }

    @Override
    public Fragment getItem(Context context, Cursor cursor) {
        final MyIpPacket ipPacket = new MyIpPacket();
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
        ipPacket.setTransportLayerPacket(transportLayerPacket);

        Fragment fragment = new PacketDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(PacketDetailFragment.SELECTED_PACKET_KEY, ipPacket);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return String.format(mContext.getString(R.string.ip_packet_title), position + 1, getCount());
    }
}
