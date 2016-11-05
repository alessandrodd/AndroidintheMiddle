package it.uniroma2.giadd.aitm.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.uniroma2.giadd.aitm.R;
import it.uniroma2.giadd.aitm.models.MyIpPacket;
import it.uniroma2.giadd.aitm.models.MyTcpPacket;
import it.uniroma2.giadd.aitm.models.MyUdpPacket;
import it.uniroma2.giadd.aitm.utils.TcpickParseUtils;

/**
 * Created by Alessandro Di Diego on 09/09/16.
 */

public class PacketDetailFragment extends Fragment {

    public static final String TAG = PacketDetailFragment.class.getName();
    public static final String SELECTED_PACKET_KEY = "SELECTED_PACKET_KEY";

    private TextView ipSource;
    private TextView destinationSource;
    private TextView protocol;
    private TextView headerLength;
    private TextView length;
    private TextView id;
    private TextView offset;
    private TextView checksum;
    private TextView tos;
    private TextView ttl;
    private TextView version;
    private TextView tcpDetails;
    private CardView tcpCardView;
    private TextView tcpSourcePort;
    private TextView tcpDestinationPort;
    private TextView sequenceNumber;
    private TextView ackNumber;
    private TextView dataOffset;
    private TextView window;
    private TextView tcpChecksum;
    private TextView urgentPtr;
    private TextView ackSequence;
    private TextView cwr;
    private TextView ece;
    private TextView fin;
    private TextView psh;
    private TextView reservedBits1;
    private TextView rst;
    private TextView syn;
    private TextView urg;
    private TextView udpDetails;
    private CardView udpCardView;
    private TextView udpSourcePort;
    private TextView udpDestinationPort;
    private TextView udpLength;
    private TextView udpChecksum;
    private TextView payload;

    private MyIpPacket selectedPacket;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(
                R.layout.fragment_packet_detail, container, false);

        ipSource = (TextView) rootView.findViewById(R.id.ip_source);
        destinationSource = (TextView) rootView.findViewById(R.id.ip_destination);
        protocol = (TextView) rootView.findViewById(R.id.protocol);
        headerLength = (TextView) rootView.findViewById(R.id.header_length);
        length = (TextView) rootView.findViewById(R.id.length);
        id = (TextView) rootView.findViewById(R.id.id_value);
        offset = (TextView) rootView.findViewById(R.id.offset);
        checksum = (TextView) rootView.findViewById(R.id.checksum);
        tos = (TextView) rootView.findViewById(R.id.type_of_service);
        ttl = (TextView) rootView.findViewById(R.id.ttl);
        version = (TextView) rootView.findViewById(R.id.version);
        tcpDetails = (TextView) rootView.findViewById(R.id.tcp_details);
        tcpCardView = (CardView) rootView.findViewById(R.id.tcp_cardview);
        tcpSourcePort = (TextView) rootView.findViewById(R.id.tcp_port_source);
        tcpDestinationPort = (TextView) rootView.findViewById(R.id.tcp_port_destination);
        sequenceNumber = (TextView) rootView.findViewById(R.id.sequence_number);
        ackNumber = (TextView) rootView.findViewById(R.id.ack_number);
        dataOffset = (TextView) rootView.findViewById(R.id.data_offset);
        window = (TextView) rootView.findViewById(R.id.window);
        tcpChecksum = (TextView) rootView.findViewById(R.id.checksum_tcp);
        urgentPtr = (TextView) rootView.findViewById(R.id.urgent_ptr);
        ackSequence = (TextView) rootView.findViewById(R.id.ack_sequence);
        cwr = (TextView) rootView.findViewById(R.id.cwr);
        ece = (TextView) rootView.findViewById(R.id.ece);
        fin = (TextView) rootView.findViewById(R.id.fin);
        psh = (TextView) rootView.findViewById(R.id.psh);
        reservedBits1 = (TextView) rootView.findViewById(R.id.reserved_bits_1);
        rst = (TextView) rootView.findViewById(R.id.rst);
        syn = (TextView) rootView.findViewById(R.id.syn);
        urg = (TextView) rootView.findViewById(R.id.urg);
        udpDetails = (TextView) rootView.findViewById(R.id.udp_details);
        udpCardView = (CardView) rootView.findViewById(R.id.udp_cardview);
        udpSourcePort = (TextView) rootView.findViewById(R.id.udp_port_source);
        udpDestinationPort = (TextView) rootView.findViewById(R.id.udp_port_destination);
        udpLength = (TextView) rootView.findViewById(R.id.length_udp);
        udpChecksum = (TextView) rootView.findViewById(R.id.checksum_udp);
        payload = (TextView) rootView.findViewById(R.id.data);

        Bundle args = getArguments();
        if (args != null) {
            selectedPacket = args.getParcelable(SELECTED_PACKET_KEY);
        }
        if (selectedPacket != null) {
            ipSource.setText(String.valueOf(selectedPacket.getSourceIp()));
            destinationSource.setText(String.valueOf(selectedPacket.getDestinationIp()));
            String[] protocols = getResources().getStringArray(R.array.protocols);
            if (selectedPacket.getProtocol() > 0 && selectedPacket.getProtocol() < protocols.length)
                protocol.setText(protocols[selectedPacket.getProtocol()]);
            else
                protocol.setText(String.valueOf(selectedPacket.getProtocol()));
            headerLength.setText(String.valueOf(selectedPacket.getHeaderLength()));
            length.setText(String.valueOf(selectedPacket.getLength()));
            id.setText(String.valueOf(selectedPacket.getId()));
            offset.setText(String.valueOf(selectedPacket.getOffset()));
            checksum.setText(String.valueOf(selectedPacket.getChecksum()));
            tos.setText(String.valueOf(selectedPacket.getTypeOfService()));
            ttl.setText(String.valueOf(selectedPacket.getTtl()));
            version.setText(String.valueOf(selectedPacket.getVersion()));
            payload.setText(TcpickParseUtils.byteArrayToReadableString(selectedPacket.getTransportLayerPacket().getData()));


            if (selectedPacket.getProtocol() == MyIpPacket.IPPROTO_TCP) {
                protocol.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                MyTcpPacket tcpPacket = (MyTcpPacket) selectedPacket.getTransportLayerPacket();
                tcpDetails.setVisibility(View.VISIBLE);
                tcpCardView.setVisibility(View.VISIBLE);
                tcpSourcePort.setText(String.valueOf(tcpPacket.getSourcePort()));
                tcpDestinationPort.setText(String.valueOf(tcpPacket.getDestinationPort()));
                sequenceNumber.setText(String.valueOf(tcpPacket.getSequenceNumber()));
                ackNumber.setText(String.valueOf(tcpPacket.getAcknowledgmentNumber()));
                dataOffset.setText(String.valueOf(tcpPacket.getDataOffset()));
                window.setText(String.valueOf(tcpPacket.getWindow()));
                tcpChecksum.setText(String.valueOf(tcpPacket.getChecksum()));
                urgentPtr.setText(String.valueOf(tcpPacket.getUrgentPointer()));
                ackSequence.setText(String.valueOf(tcpPacket.getAckSequence()));
                cwr.setText(String.valueOf(tcpPacket.getCwr()));
                ece.setText(String.valueOf(tcpPacket.getEce()));
                fin.setText(String.valueOf(tcpPacket.getFin()));
                psh.setText(String.valueOf(tcpPacket.getPsh()));
                reservedBits1.setText(String.valueOf(tcpPacket.getReservedBits1()));
                rst.setText(String.valueOf(tcpPacket.getRst()));
                syn.setText(String.valueOf(tcpPacket.getSyn()));
                urg.setText(String.valueOf(tcpPacket.getUrg()));
            } else if (selectedPacket.getProtocol() == MyIpPacket.IPPROTO_UDP) {
                protocol.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                MyUdpPacket udpPacket = (MyUdpPacket) selectedPacket.getTransportLayerPacket();
                udpDetails.setVisibility(View.VISIBLE);
                udpCardView.setVisibility(View.VISIBLE);
                udpSourcePort.setText(String.valueOf(udpPacket.getSourcePort()));
                udpDestinationPort.setText(String.valueOf(udpPacket.getDestinationPort()));
                udpLength.setText(String.valueOf(udpPacket.getLength()));
                udpChecksum.setText(String.valueOf(udpPacket.getChecksum()));
            }

        }

        return rootView;
    }
}