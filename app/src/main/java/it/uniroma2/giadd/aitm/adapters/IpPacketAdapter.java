package it.uniroma2.giadd.aitm.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import it.uniroma2.giadd.aitm.R;
import it.uniroma2.giadd.aitm.models.MyIpPacket;
import it.uniroma2.giadd.aitm.models.MyTcpPacket;
import it.uniroma2.giadd.aitm.models.MyUdpPacket;

import static it.uniroma2.giadd.aitm.models.MyIpPacket.IPPROTO_TCP;
import static it.uniroma2.giadd.aitm.models.MyIpPacket.IPPROTO_UDP;

/**
 * Created by Alessandro Di Diego on 10/08/16.
 */

public class IpPacketAdapter extends RecyclerView.Adapter<IpPacketAdapter.MyViewHolder> {

    private String[] protocols;
    private Context context;
    private List<MyIpPacket> myIpPacketList;

    class MyViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView ipSource, ipDestination, portSource, portDestination, protocol, length;

        MyViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.cardview);
            ipSource = (TextView) view.findViewById(R.id.ip_source);
            ipDestination = (TextView) view.findViewById(R.id.ip_destination);
            portSource = (TextView) view.findViewById(R.id.port_source);
            portDestination = (TextView) view.findViewById(R.id.port_destination);
            protocol = (TextView) view.findViewById(R.id.protocol);
            length = (TextView) view.findViewById(R.id.length);
        }
    }


    public IpPacketAdapter(Context context, List<MyIpPacket> myIpPacketList) {
        this.context = context;
        this.myIpPacketList = myIpPacketList;
        protocols = context.getResources().getStringArray(R.array.protocols);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_ip_packet, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MyIpPacket ipPacket = myIpPacketList.get(position);
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

    @Override
    public int getItemCount() {
        return myIpPacketList.size();
    }
}
