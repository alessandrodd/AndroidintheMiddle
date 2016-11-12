package it.uniroma2.giadd.aitm.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.uniroma2.giadd.aitm.R;
import it.uniroma2.giadd.aitm.models.MyIpPacket;
import it.uniroma2.giadd.aitm.models.MyTcpPacket;
import it.uniroma2.giadd.aitm.models.MyTransportLayerPacket;
import it.uniroma2.giadd.aitm.models.MyUdpPacket;

import static it.uniroma2.giadd.aitm.models.MyIpPacket.IPPROTO_TCP;
import static it.uniroma2.giadd.aitm.models.MyIpPacket.IPPROTO_UDP;
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
 * Created by Alessandro Di Diego on 10/08/16.
 */

public class IpPacketAdapter extends CursorRecyclerAdapter<IpPacketAdapter.MyViewHolder> {

    public static final String TAG = IpPacketAdapter.class.getSimpleName();

    private String[] protocols;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(MyIpPacket item, int position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView ipSource, ipDestination, portSource, portDestination, protocol, length;
        View view;

        MyViewHolder(View view) {
            super(view);
            this.view = view;
            cardView = (CardView) view.findViewById(R.id.cardview);
            ipSource = (TextView) view.findViewById(R.id.ip_source);
            ipDestination = (TextView) view.findViewById(R.id.ip_destination);
            portSource = (TextView) view.findViewById(R.id.port_source);
            portDestination = (TextView) view.findViewById(R.id.port_destination);
            protocol = (TextView) view.findViewById(R.id.protocol);
            length = (TextView) view.findViewById(R.id.length);
        }
    }

    public IpPacketAdapter(Context context, Cursor c, OnItemClickListener listener) {
        super(c);
        this.context = context;
        this.listener = listener;
        protocols = context.getResources().getStringArray(R.array.protocols);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_ip_packet, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final Cursor cursor) {
        if (cursor == null) {
            Log.e(IpPacketAdapter.TAG, "ERROR IN ADAPTER: cursor is null");
            return;
        }

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

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(ipPacket, holder.getAdapterPosition());
            }
        });

        holder.ipSource.setText(ipPacket.getSourceIp());
        holder.ipDestination.setText(ipPacket.getDestinationIp());
        holder.length.setText(String.valueOf(ipPacket.getLength()));
        if (ipPacket.getProtocol() > 0 && ipPacket.getProtocol() < 256) {
            holder.protocol.setText(protocols[ipPacket.getProtocol()]);
        } else holder.protocol.setText("");
        if (ipPacket.getProtocol() == IPPROTO_UDP) {
            holder.protocol.setTextColor(ContextCompat.getColor(context, R.color.red));
            MyUdpPacket udpPacket = (MyUdpPacket) ipPacket.getTransportLayerPacket();
            holder.portSource.setText(String.valueOf(udpPacket.getSourcePort()));
            holder.portDestination.setText(String.valueOf(udpPacket.getDestinationPort()));
        } else if (ipPacket.getProtocol() == IPPROTO_TCP) {
            holder.protocol.setTextColor(ContextCompat.getColor(context, R.color.blue));
            MyTcpPacket tcpPacket = (MyTcpPacket) ipPacket.getTransportLayerPacket();
            holder.portSource.setText(String.valueOf(tcpPacket.getSourcePort()));
            holder.portDestination.setText(String.valueOf(tcpPacket.getDestinationPort()));
        } else {
            holder.protocol.setTextColor(ContextCompat.getColor(context, android.R.color.primary_text_dark));
            holder.portSource.setText("");
            holder.portDestination.setText("");
        }
    }

}
