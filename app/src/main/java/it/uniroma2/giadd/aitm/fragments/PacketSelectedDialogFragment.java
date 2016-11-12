package it.uniroma2.giadd.aitm.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.uniroma2.giadd.aitm.PacketDetailActivity;
import it.uniroma2.giadd.aitm.R;
import it.uniroma2.giadd.aitm.models.MyIpPacket;
import it.uniroma2.giadd.aitm.models.MyTcpPacket;
import it.uniroma2.giadd.aitm.models.MyUdpPacket;

/**
 * Created by Alessandro Di Diego on 09/09/16.
 */

public class PacketSelectedDialogFragment extends DialogFragment {

    public static final String TAG = PacketSelectedDialogFragment.class.getName();
    public static final String CURRENTDUMPPATH_KEY = "CURRENTDUMPPATH_KEY";
    public static final String MYPACKET_KEY = "MYPACKET_KEY";
    public static final String CURSOR_POSITION = "CURSOR_POSITION";


    private MyIpPacket packet;
    private int cursorPosition;
    private String currentDumpPath;

    private View.OnClickListener buttonManager = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (packet == null || currentDumpPath == null) return;
            String command = getContext().getFilesDir() + "/";
            switch (view.getId()) {
                case R.id.inspect_packet:
                    Intent intent = new Intent(getContext(), PacketDetailActivity.class);
                    intent.putExtra(PacketDetailActivity.SELECTED_PACKET_POSITION, cursorPosition);
                    startActivity(intent);
                    return;
                case R.id.follow_tcp_stream:
                    command += TcpFlowInspectionFragment.COMMAND_TCPICK_FOLLOW_TCP_STREAM;
                    break;
                case R.id.follow_tcp_traffic_between_hosts:
                    command += TcpFlowInspectionFragment.COMMAND_TCPICK_FOLLOW_TCP_TRAFFIC;
                    break;
                case R.id.follow_tcp_and_udp_traffic_between_hosts:
                    command += TcpFlowInspectionFragment.COMMAND_TCPICK_FOLLOW_TCP_UDP_TRAFFIC;
                    break;
            }
            TcpFlowInspectionFragment fragment;
            if (packet.getProtocol() == MyIpPacket.IPPROTO_TCP) {
                MyTcpPacket tcpPacket = (MyTcpPacket) packet.getTransportLayerPacket();
                fragment = TcpFlowInspectionFragment.newInstance(currentDumpPath, packet.getSourceIp(), tcpPacket.getSourcePort(), packet.getDestinationIp(), tcpPacket.getDestinationPort(), command);
            } else if (packet.getProtocol() == MyIpPacket.IPPROTO_UDP) {
                MyUdpPacket udpPacket = (MyUdpPacket) packet.getTransportLayerPacket();
                fragment = TcpFlowInspectionFragment.newInstance(currentDumpPath, packet.getSourceIp(), udpPacket.getSourcePort(), packet.getDestinationIp(), udpPacket.getDestinationPort(), command);
            } else {
                Log.e(TAG, "ERROR!! Trying to follow flow of a not tcp/udp packet!");
                return;
            }
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    };

    public static PacketSelectedDialogFragment newInstance(MyIpPacket ipPacket, int cursorPosition, String currentDumpPath) {
        PacketSelectedDialogFragment myFragment = new PacketSelectedDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(MYPACKET_KEY, ipPacket);
        args.putInt(CURSOR_POSITION, cursorPosition);
        args.putString(CURRENTDUMPPATH_KEY, currentDumpPath);
        myFragment.setArguments(args);
        return myFragment;
    }

    // Empty constructor required for DialogFragment
    public PacketSelectedDialogFragment() {

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            packet = bundle.getParcelable(MYPACKET_KEY);
            cursorPosition = bundle.getInt(CURSOR_POSITION);
            currentDumpPath = bundle.getString(CURRENTDUMPPATH_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_packet_selected, container);
        AppCompatButton inspectPacketButton = (AppCompatButton) view.findViewById(R.id.inspect_packet);
        AppCompatButton followTcpStreamButton = (AppCompatButton) view.findViewById(R.id.follow_tcp_stream);
        AppCompatButton followTcpTrafficBetweenHostsButton = (AppCompatButton) view.findViewById(R.id.follow_tcp_traffic_between_hosts);
        AppCompatButton followTcpUdpTrafficButton = (AppCompatButton) view.findViewById(R.id.follow_tcp_and_udp_traffic_between_hosts);

        inspectPacketButton.setOnClickListener(buttonManager);
        followTcpStreamButton.setOnClickListener(buttonManager);
        followTcpTrafficBetweenHostsButton.setOnClickListener(buttonManager);
        followTcpUdpTrafficButton.setOnClickListener(buttonManager);

        if (packet != null && packet.getProtocol() == MyIpPacket.IPPROTO_TCP) {
            followTcpStreamButton.setVisibility(View.VISIBLE);
            followTcpTrafficBetweenHostsButton.setVisibility(View.VISIBLE);
            followTcpUdpTrafficButton.setVisibility(View.VISIBLE);
        } else if (packet != null && packet.getProtocol() == MyIpPacket.IPPROTO_UDP) {
            followTcpUdpTrafficButton.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            currentDumpPath = savedInstanceState.getString(CURRENTDUMPPATH_KEY);
            packet = savedInstanceState.getParcelable(MYPACKET_KEY);
            cursorPosition = savedInstanceState.getInt(CURSOR_POSITION, 0);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (currentDumpPath != null)
            outState.putString(CURRENTDUMPPATH_KEY, currentDumpPath);
        if (packet != null)
            outState.putParcelable(MYPACKET_KEY, packet);
        outState.putInt(CURSOR_POSITION, cursorPosition);
    }


}