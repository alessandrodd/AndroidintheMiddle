package it.uniroma2.giadd.aitm.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import it.uniroma2.giadd.aitm.R;
import it.uniroma2.giadd.aitm.adapters.IpPacketAdapter;
import it.uniroma2.giadd.aitm.models.MyIpPacket;
import it.uniroma2.giadd.aitm.models.modules.ModuleMitm;
import it.uniroma2.giadd.aitm.services.ParsePcapService;
import it.uniroma2.giadd.aitm.services.SniffService;
import it.uniroma2.giadd.aitm.utils.IpPacketDBHandler;
import it.uniroma2.giadd.aitm.utils.PreferencesUtils;

/**
 * Created by Alessandro Di Diego
 */

public class CaptureFragment extends Fragment {

    private final static String TAG = CaptureFragment.class.getName();

    private RelativeLayout moduleLayout;
    private SwitchCompat saveCaptureSwitch;
    private SwitchCompat parseCaptureSwitch;
    private TextView moduleTitle;
    private TextView moduleMessage;
    private CardView consoleOutputCardview;
    private TextView consoleOutputTextView;
    private ProgressBar progressBar;
    private ModuleMitm mitmModule;
    private String currentDumpPath;
    private IpPacketAdapter ipPacketAdapter = null;
    private RecyclerView recyclerView;
    private boolean switchActivated = true;

    private IpPacketDBHandler dbHandler;

    private IpPacketAdapter.OnItemClickListener onItemClickListener = new IpPacketAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(MyIpPacket item, int position) {
            showChooseDialog(item, position);
        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parentViewGroup,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_capture, parentViewGroup, false);

        moduleLayout = (RelativeLayout) rootView.findViewById(R.id.module_layout);
        moduleLayout.setVisibility(View.GONE);
        saveCaptureSwitch = (SwitchCompat) rootView.findViewById(R.id.save_capture);
        parseCaptureSwitch = (SwitchCompat) rootView.findViewById(R.id.parse_capture);
        saveCaptureSwitch.setFocusableInTouchMode(false);
        saveCaptureSwitch.setFocusable(false);
        parseCaptureSwitch.setFocusableInTouchMode(false);
        parseCaptureSwitch.setFocusable(false);
        moduleTitle = (TextView) rootView.findViewById(R.id.module_title);
        moduleMessage = (TextView) rootView.findViewById(R.id.module_message);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        consoleOutputCardview = (CardView) rootView.findViewById(R.id.cardview_console_output);
        consoleOutputCardview.setVisibility(View.GONE);
        consoleOutputTextView = (TextView) rootView.findViewById(R.id.console_output);
        consoleOutputTextView.setMovementMethod(new ScrollingMovementMethod());
        consoleOutputTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(consoleOutputTextView.getText());
                builder.setCancelable(true);
                AlertDialog dialog = builder.create();
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.show();
                TextView textView = (TextView) dialog.findViewById(android.R.id.message);
                if (textView != null) {
                    textView.setMaxLines(9999);
                    textView.setScroller(new Scroller(getContext()));
                    textView.setVerticalScrollBarEnabled(true);
                    textView.setMovementMethod(new ScrollingMovementMethod());
                }
            }
        });
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        // we can enable optimizations if all item views are of the same height and width for significantly smoother scrolling
        recyclerView.setHasFixedSize(true);

        dbHandler = new IpPacketDBHandler(getContext());
        ipPacketAdapter = new IpPacketAdapter(getContext(), dbHandler.getAllIpPacketsCursor(), onItemClickListener);
        recyclerView.setAdapter(ipPacketAdapter);

        saveCaptureSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (mitmModule != null && b != mitmModule.isDumpToFile()) {
                    mitmModule.setDumpToFile(b);
                    Intent intent = new Intent(getContext(), SniffService.class);
                    if (b) intent.putExtra(SniffService.SAVE_DUMP, true);
                    else intent.putExtra(SniffService.DELETE_DUMP, true);
                    getContext().startService(intent);
                }
            }
        });
        parseCaptureSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (switchActivated) {
                    PreferencesUtils.setParseCapture(getContext(), b);
                    if (mitmModule != null) {
                        Intent intent = new Intent(getContext(), ParsePcapService.class);
                        if (b) {
                            intent.putExtra(ParsePcapService.PCAP_PATH, mitmModule.getDumpPath());
                            currentDumpPath = mitmModule.getDumpPath();
                        } else intent.putExtra(ParsePcapService.READ_PCAP_STOP, true);
                        getContext().startService(intent);
                    }
                }
            }
        });
        return rootView;
    }

    private void closeFragmentDialogs() {
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag(PacketSelectedDialogFragment.TAG);
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }
    }

    private void showChooseDialog(MyIpPacket ipPacket, int position) {
        closeFragmentDialogs();
        FragmentManager manager = getFragmentManager();
        PacketSelectedDialogFragment packetSelectedDialogFragment = PacketSelectedDialogFragment.newInstance(ipPacket, position, currentDumpPath);
        packetSelectedDialogFragment.show(manager, PacketSelectedDialogFragment.TAG);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void setInterface() {
        if (mitmModule != null) {
            moduleLayout.setVisibility(View.VISIBLE);
            moduleTitle.setText(mitmModule.getModuleTitle());
            moduleMessage.setText(mitmModule.getModuleMessage());
            if (mitmModule.getDumpPath() != null) {
                saveCaptureSwitch.setVisibility(View.VISIBLE);
                parseCaptureSwitch.setVisibility(View.VISIBLE);
                if (mitmModule.isDumpToFile()) saveCaptureSwitch.setChecked(true);
                else saveCaptureSwitch.setChecked(false);
                if (parseCaptureSwitch.isChecked()) {
                    Intent intent = new Intent(getContext(), ParsePcapService.class);
                    intent.putExtra(ParsePcapService.PCAP_PATH, mitmModule.getDumpPath());
                    currentDumpPath = mitmModule.getDumpPath();
                    getContext().startService(intent);
                }
            } else {
                saveCaptureSwitch.setVisibility(View.GONE);
                parseCaptureSwitch.setVisibility(View.GONE);
            }
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getParcelableExtra(SniffService.MITM_MODULE) != null && !intent.getParcelableExtra(SniffService.MITM_MODULE).equals(mitmModule)) {
                mitmModule = intent.getParcelableExtra(SniffService.MITM_MODULE);
                setInterface();
            } else if (intent.getBooleanExtra(SniffService.MITM_STOP, false)) {
                progressBar.setVisibility(View.GONE);
            } else if (intent.getStringExtra(SniffService.CONSOLE_MESSAGE) != null) {
                consoleOutputCardview.setVisibility(View.VISIBLE);
                consoleOutputTextView.append("\n" + intent.getStringExtra(SniffService.CONSOLE_MESSAGE));
            } else if (intent.getStringExtra(ParsePcapService.RETRIEVE_DUMP_PATH) != null) {
                currentDumpPath = intent.getStringExtra(ParsePcapService.RETRIEVE_DUMP_PATH);
            } else if (intent.getStringExtra(ParsePcapService.ERROR_MESSAGE) != null && consoleOutputTextView != null) {
                Snackbar.make(recyclerView, intent.getStringExtra(ParsePcapService.ERROR_MESSAGE), Snackbar.LENGTH_LONG).show();
            } else if (intent.getStringExtra(ParsePcapService.MESSAGE) != null && consoleOutputTextView != null) {
                Snackbar.make(recyclerView, intent.getStringExtra(ParsePcapService.MESSAGE), Snackbar.LENGTH_SHORT).show();
            } else if (intent.getBooleanExtra(ParsePcapService.READ_PCAP_STOP, false)) {
                switchActivated = false;
                parseCaptureSwitch.setChecked(false);
                switchActivated = true;
            } else if (intent.getBooleanExtra(ParsePcapService.NEW_PACKET, false)) {
                if (ipPacketAdapter != null)
                    ipPacketAdapter.changeCursor(dbHandler.getAllIpPacketsCursor());
                else {
                    ipPacketAdapter = new IpPacketAdapter(getContext(), dbHandler.getAllIpPacketsCursor(), onItemClickListener);
                    recyclerView.setAdapter(ipPacketAdapter);
                }
            }

        }
    };

    @Override
    public void onResume() {
        super.onResume();
        parseCaptureSwitch.setChecked(PreferencesUtils.shouldParseCapture(getContext()));
        try {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(SniffService.TAG);
            intentFilter.addAction(ParsePcapService.TAG);
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver, intentFilter);
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "receiver already registered");
        }
        Intent i = new Intent(getContext(), SniffService.class);
        i.putExtra(SniffService.RETRIEVE_MITM_MODULE, true);
        getContext().startService(i);
        i = new Intent(getContext(), ParsePcapService.class);
        i.putExtra(ParsePcapService.RETRIEVE_DUMP_PATH, true);
        getContext().startService(i);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mMessageReceiver);
        closeFragmentDialogs();
    }

    @Override
    public void onDestroy() {
        Intent intent = new Intent(getContext(), ParsePcapService.class);
        intent.putExtra(ParsePcapService.READ_PCAP_STOP, true);
        getContext().startService(intent);
        super.onDestroy();
    }
}
