package it.uniroma2.giadd.aitm.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import it.uniroma2.giadd.aitm.R;
import it.uniroma2.giadd.aitm.adapters.TcpFlowAdapter;
import it.uniroma2.giadd.aitm.interfaces.OnCommandListener;
import it.uniroma2.giadd.aitm.managers.RootManager;
import it.uniroma2.giadd.aitm.models.TcpFlow;
import it.uniroma2.giadd.aitm.models.TcpickBanner2;

/**
 * Created by Alessandro Di Diego
 */

public class TcpFlowInspectionFragment extends Fragment implements OnCommandListener {
    public static final String TAG = TcpFlowInspectionFragment.class.getName();

    private static final String PCAP_PATH_KEY = "PCAP_PATH_KEY";
    private static final String SOURCE_IP_KEY = "SOURCE_IP_KEY";
    private static final String SOURCE_PORT_KEY = "SOURCE_PORT_KEY";
    private static final String DESTINATION_IP_KEY = "DESTINATION_IP_KEY";
    private static final String DESTINATION_PORT_KEY = "DESTINATION_PORT_KEY";
    private static final String COMMAND_KEY = "COMMAND_KEY";
    private static final int COMMAND_CODE = 7916;

    public static final String COMMAND_TCPICK_FOLLOW_TCP_STREAM = "tcpick -bRB -r <path> \"tcp and ((src host <sourceip> and src port <sourceport> and dst host <destinationip> and dst port <destinationport>) or (dst host <sourceip> and dst port <sourceport> and src host <destinationip> and src port <destinationport>))\" -S -v0";
    public static final String COMMAND_TCPICK_FOLLOW_UDP_STREAM = "tcpick -bRB -r <path> \"udp and ((src host <sourceip> and src port <sourceport> and dst host <destinationip> and dst port <destinationport>) or (dst host <sourceip> and dst port <sourceport> and src host <destinationip> and src port <destinationport>))\" -S -v0";
    public static final String COMMAND_TCPICK_FOLLOW_TCP_TRAFFIC = "tcpick -bRB -r <path> \"tcp and (host <sourceip> and host <destinationip>)\" -S -v0";
    public static final String COMMAND_TCPICK_FOLLOW_UDP_TRAFFIC = "tcpick -bRB -r <path> \"udp and (host <sourceip> and host <destinationip>)\" -S -v0";
    public static final String COMMAND_TCPICK_FOLLOW_TCP_UDP_TRAFFIC = "tcpick -bRB -r <path> \"(tcp or udp) and (host <sourceip> and host <destinationip>)\" -S -v0";

    private ProgressBar loadingCircle;
    private TextView noTcpFlowData;
    private String pcapPath;
    private String sourceIp;
    private int sourcePort;
    private String destinationIp;
    private int destinationPort;
    private String command;
    private ArrayList<TcpFlow> tcpFlows;
    private TcpFlowAdapter tcpFlowAdapter;


    public static TcpFlowInspectionFragment newInstance(String pcapPath, String sourceIp, int sourcePort, String destinationIp, int destinationPort, String command) {
        TcpFlowInspectionFragment myFragment = new TcpFlowInspectionFragment();
        Bundle args = new Bundle();
        args.putString(PCAP_PATH_KEY, pcapPath);
        args.putString(SOURCE_IP_KEY, sourceIp);
        args.putInt(SOURCE_PORT_KEY, sourcePort);
        args.putString(DESTINATION_IP_KEY, destinationIp);
        args.putInt(DESTINATION_PORT_KEY, destinationPort);
        args.putString(COMMAND_KEY, command);
        myFragment.setArguments(args);
        return myFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            pcapPath = bundle.getString(PCAP_PATH_KEY);
            sourceIp = bundle.getString(SOURCE_IP_KEY);
            sourcePort = bundle.getInt(SOURCE_PORT_KEY, 0);
            destinationIp = bundle.getString(DESTINATION_IP_KEY);
            destinationPort = bundle.getInt(DESTINATION_PORT_KEY, 0);
            command = bundle.getString(COMMAND_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parentViewGroup,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tcpflowinspection, parentViewGroup, false);
        loadingCircle = (ProgressBar) rootView.findViewById(R.id.loading_circle);
        tcpFlows = new ArrayList<>();
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        tcpFlowAdapter = new TcpFlowAdapter(getContext(), tcpFlows);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(tcpFlowAdapter);
        noTcpFlowData = (TextView) rootView.findViewById(R.id.no_tcp_flow_data);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void execTcpick(String pcapPath, String sourceIp, String destinationIp) {
        if (pcapPath == null || sourceIp == null || destinationIp == null || command == null) {
            Log.e(TAG, "Error: invalid parameters for execTcpick");
            return;
        }
        RootManager rootManager = new RootManager();
        command = command.replaceAll("<path>", pcapPath).replaceAll("<sourceip>", sourceIp).replaceAll("<destinationip>", destinationIp).replaceAll("<sourceport>", String.valueOf(sourcePort)).replaceAll("<destinationport>", String.valueOf(destinationPort));
        rootManager.execUnprivilegedCommandAsync(command, COMMAND_CODE, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (loadingCircle != null) loadingCircle.setVisibility(View.VISIBLE);
        execTcpick(pcapPath, sourceIp, destinationIp);
    }

    @Override
    public void onPause() {
        super.onPause();
        RootManager.killByName(getContext(), "tcpick", RootManager.SIGINT, new OnCommandListener() {
            @Override
            public void onShellError(int exitCode) {
                Log.d(TAG, "ERROR: EXIT CODE:" + exitCode);
            }

            @Override
            public void onCommandResult(int commandCode, int exitCode) {
                Log.d(TAG, "tcpick terminated; EXIT CODE:" + exitCode);
            }

            @Override
            public void onLine(String line) {
                Log.d(TAG, "LINE: " + line);
            }
        });
    }

    @Override
    public void onShellError(int exitCode) {
        Log.e(TAG, "Shell error, EXIT CODE: " + exitCode);
        if (loadingCircle != null) loadingCircle.setVisibility(View.GONE);
        if (tcpFlows == null || tcpFlows.isEmpty()) noTcpFlowData.setVisibility(View.VISIBLE);
    }


    private TcpFlow parsedTcpFlow;
    private String parsedData = "";

    @Override
    public void onLine(String line) {
        Log.d(TAG, "Tcpick:" + line);
        /**
         *  TODO: BETTER PARSING?!?!
         *  Check byte length and use that as data indicator
         */
        TcpickBanner2 tcpickBanner = TcpickBanner2.tcpickHeaderFromString(line);
        if (tcpickBanner != null) { // banner detected
            Log.d("DBG", tcpickBanner.toString());
            if (parsedTcpFlow != null && parsedData != null && !parsedData.isEmpty()) {
                // ok, new flow detected; if the latest flow isn't null or empty, add it to the list
                parsedTcpFlow.setData(parsedData.getBytes());
                tcpFlows.add(parsedTcpFlow);
                tcpFlowAdapter.notifyDataSetChanged();
            }
            parsedTcpFlow = new TcpFlow();
            parsedData = "";
            parsedTcpFlow.setDestination(tcpickBanner.getDestination());
            parsedTcpFlow.setDestinationIp(tcpickBanner.getDestinationIp());
            parsedTcpFlow.setDestinationPort(String.valueOf(tcpickBanner.getDestinationPort()));
        } else if (parsedTcpFlow == null) {
            Log.e(TAG, "Error: parsedTcpFlow cannot be null here!");
            parsedData = "";
        } else {
            if (!parsedData.isEmpty())
                parsedData += "\n";
            parsedData += line;
        }
    }

    @Override
    public void onCommandResult(int commandCode, int exitCode) {
        Log.d(TAG, "EXIT CODE: " + exitCode);
        if (parsedTcpFlow != null && parsedData != null && !parsedData.isEmpty()) {
            parsedTcpFlow.setData(parsedData.getBytes());
            tcpFlows.add(parsedTcpFlow);
            tcpFlowAdapter.notifyDataSetChanged();
        }
        parsedTcpFlow = new TcpFlow();
        parsedData = "";
        if (loadingCircle != null) loadingCircle.setVisibility(View.GONE);
        if (tcpFlows == null || tcpFlows.isEmpty()) noTcpFlowData.setVisibility(View.VISIBLE);
    }

}
