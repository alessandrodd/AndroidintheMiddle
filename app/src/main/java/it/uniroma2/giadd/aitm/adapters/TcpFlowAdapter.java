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
import it.uniroma2.giadd.aitm.models.TcpFlow;

import static it.uniroma2.giadd.aitm.models.MyIpPacket.IPPROTO_TCP;
import static it.uniroma2.giadd.aitm.models.MyIpPacket.IPPROTO_UDP;

/**
 * Created by Alessandro Di Diego on 10/08/16.
 */

public class TcpFlowAdapter extends RecyclerView.Adapter<TcpFlowAdapter.MyViewHolder> {

    private Context context;
    private List<TcpFlow> tcpFlows;
    private String sourceIp;
    private String destinationIp;

    class MyViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView ipSource, ipDestination, portSource, portDestination, data;

        MyViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.cardview);
            ipSource = (TextView) view.findViewById(R.id.ip_source);
            ipDestination = (TextView) view.findViewById(R.id.ip_destination);
            portSource = (TextView) view.findViewById(R.id.port_source);
            portDestination = (TextView) view.findViewById(R.id.port_destination);
            data = (TextView) view.findViewById(R.id.data);
        }
    }


    public TcpFlowAdapter(Context context, List<TcpFlow> tcpFlows) {
        this.context = context;
        this.tcpFlows = tcpFlows;
    }

    public TcpFlowAdapter(Context context, List<TcpFlow> tcpFlows, String sourceIp, String destinationIp) {
        this.context = context;
        this.tcpFlows = tcpFlows;
        this.sourceIp = sourceIp;
        this.destinationIp = destinationIp;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_tcp_flow, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        TcpFlow tcpFlow = tcpFlows.get(position);
        holder.ipSource.setText(tcpFlow.getSourceIp());
        holder.ipDestination.setText(tcpFlow.getDestinationIp());
        holder.portSource.setText(tcpFlow.getSourcePort());
        holder.portDestination.setText(tcpFlow.getDestinationPort());
        if (tcpFlow.getData() != null)
            holder.data.setText(new String(tcpFlow.getData()));
        if (sourceIp != null && sourceIp.equals(tcpFlow.getSourceIp())) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.blue));
        } else if (destinationIp != null && destinationIp.equals(tcpFlow.getSourceIp())) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.red));
        } else
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, android.R.color.background_dark));

    }

    @Override
    public int getItemCount() {
        return tcpFlows.size();
    }
}
