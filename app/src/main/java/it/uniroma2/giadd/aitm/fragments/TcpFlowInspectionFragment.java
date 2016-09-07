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

import java.util.ArrayList;

import it.uniroma2.giadd.aitm.R;
import it.uniroma2.giadd.aitm.adapters.TcpFlowAdapter;
import it.uniroma2.giadd.aitm.interfaces.OnCommandListener;
import it.uniroma2.giadd.aitm.managers.RootManager;
import it.uniroma2.giadd.aitm.models.TcpFlow;
import it.uniroma2.giadd.aitm.models.TcpickHeader;

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
    private static final int COMMAND_CODE = 7916;

    private static final String COMMAND_TCPICK_EXTRACT_TCP_FLOW = "tcpick -bP -r <path> \"host <sourceip> and host <destinationip>\" -bP -t -S -v0 -h";

    private ProgressBar loadingCircle;
    private String pcapPath;
    private String sourceIp;
    private int sourcePort;
    private String destinationIp;
    private int destinationPort;
    private ArrayList<TcpFlow> tcpFlows;
    private TcpFlowAdapter tcpFlowAdapter;


    public static TcpFlowInspectionFragment newInstance(String pcapPath, String sourceIp, int sourcePort, String destinationIp, int destinationPort) {
        TcpFlowInspectionFragment myFragment = new TcpFlowInspectionFragment();
        Bundle args = new Bundle();
        args.putString(PCAP_PATH_KEY, pcapPath);
        args.putString(SOURCE_IP_KEY, sourceIp);
        args.putInt(SOURCE_PORT_KEY, sourcePort);
        args.putString(DESTINATION_IP_KEY, destinationIp);
        args.putInt(DESTINATION_PORT_KEY, destinationPort);
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parentViewGroup,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tcpflowinspection, parentViewGroup, false);
        loadingCircle = (ProgressBar) rootView.findViewById(R.id.loading_circle);
        tcpFlows = new ArrayList<>();
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        tcpFlowAdapter = new TcpFlowAdapter(getContext(), tcpFlows, sourceIp, destinationIp);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(tcpFlowAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void execTcpick(String pcapPath, String sourceIp, String destinationIp) {
        if (pcapPath == null || sourceIp == null || destinationIp == null) {
            Log.e(TAG, "Error: invalid parameters for execTcpick");
            return;
        }
        RootManager rootManager = new RootManager();
        String command = getContext().getFilesDir() + "/" + COMMAND_TCPICK_EXTRACT_TCP_FLOW.replace("<path>", pcapPath).replace("<sourceip>", sourceIp).replace("<destinationip>", destinationIp);
        rootManager.execSuCommandAsync(command, COMMAND_CODE, this);
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
    }

    @Override
    public void onCommandResult(int commandCode, int exitCode) {
        Log.d(TAG, "EXIT CODE: " + exitCode);
        if (loadingCircle != null) loadingCircle.setVisibility(View.GONE);
    }

    private TcpFlow parsedTcpFlow;
    private String parsedData = "";

    @Override
    public void onLine(String line) {
        Log.d(TAG, "Tcpick:" + line);
        /**
         *  TODO: BETTER PARSING!!! THIS IS JUST A QUICK WORKAROUND!!!
         *  Check byte length and use that as data indicator
         */
        TcpickHeader tcpickHeader = TcpickHeader.tcpickHeaderFromString(line);
        if (tcpickHeader != null && (tcpFlows.isEmpty() || !tcpFlows.get(tcpFlows.size() - 1).getSourceIp().equals(tcpickHeader.getSourceIp()) || !tcpFlows.get(tcpFlows.size() - 1).getDestinationIp().equals(tcpickHeader.getDestinationIp()))) {
            if (parsedTcpFlow != null && parsedData != null && !parsedData.isEmpty()) {
                parsedTcpFlow.setData(parsedData.getBytes());
                tcpFlows.add(parsedTcpFlow);
            }
            parsedTcpFlow = new TcpFlow();
            parsedData = "";
            parsedTcpFlow.setSourceIp(tcpickHeader.getSourceIp());
            parsedTcpFlow.setSourcePort(String.valueOf(tcpickHeader.getSourcePort()));
            parsedTcpFlow.setDestinationIp(tcpickHeader.getDestinationIp());
            parsedTcpFlow.setDestinationPort(String.valueOf(tcpickHeader.getDestinationPort()));
            tcpFlowAdapter.notifyDataSetChanged();
        } else if (parsedTcpFlow == null) {
            Log.e(TAG, "Error: parsedTcpFlow cannot be null here!");
            parsedData = "";
        } else if (tcpickHeader != null) {
            parsedData += line;
        }
    }

}
