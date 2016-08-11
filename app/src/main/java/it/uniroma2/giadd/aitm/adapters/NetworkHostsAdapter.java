package it.uniroma2.giadd.aitm.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import it.uniroma2.giadd.aitm.R;
import it.uniroma2.giadd.aitm.models.NetworkHost;

/**
 * Created by Alessandro Di Diego on 10/08/16.
 */

public class NetworkHostsAdapter extends RecyclerView.Adapter<NetworkHostsAdapter.MyViewHolder> {

    private List<NetworkHost> networkHostsList;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView ip, hostname, mac, vendor;

        MyViewHolder(View view) {
            super(view);
            ip = (TextView) view.findViewById(R.id.ip);
            hostname = (TextView) view.findViewById(R.id.hostname);
            mac = (TextView) view.findViewById(R.id.mac);
            vendor = (TextView) view.findViewById(R.id.vendor);
        }
    }


    public NetworkHostsAdapter(List<NetworkHost> networkHostsList) {
        this.networkHostsList = networkHostsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_network_host, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        NetworkHost networkHost = networkHostsList.get(position);
        holder.ip.setText((networkHost.getIp()));
        holder.hostname.setText((networkHost.getHostname()));
        holder.mac.setText((networkHost.getMacAddress().getAddress()));
        holder.vendor.setText((networkHost.getMacAddress().getVendor()));
        if (networkHost.isReachable())
            holder.itemView.setBackgroundResource(R.color.transparent);
        else holder.itemView.setBackgroundResource(R.color.transparent_red);
    }

    @Override
    public int getItemCount() {
        return networkHostsList.size();
    }
}
