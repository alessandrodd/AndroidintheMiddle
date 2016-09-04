package it.uniroma2.giadd.aitm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import it.uniroma2.giadd.aitm.adapters.IpPacketAdapter;
import it.uniroma2.giadd.aitm.fragments.ScannerFragment;
import it.uniroma2.giadd.aitm.models.modules.MitmModule;
import it.uniroma2.giadd.aitm.services.ParsePcapService;
import it.uniroma2.giadd.aitm.services.SniffService;
import it.uniroma2.giadd.aitm.utils.PreferencesUtils;
import it.uniroma2.giadd.aitm.utils.RecyclerTouchListener;

public class CaptureActivity extends AppCompatActivity {

    private final static String TAG = CaptureActivity.class.getName();

    private Context context;
    private SwitchCompat saveCaptureSwitch;
    private SwitchCompat parseCaptureSwitch;
    private TextView moduleTitle;
    private TextView moduleMessage;
    private TextView consoleOutputTextView;
    private ProgressBar progressBar;
    private MitmModule mitmModule;
    private IpPacketAdapter ipPacketAdapter = null;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        context = this;

        saveCaptureSwitch = (SwitchCompat) findViewById(R.id.save_capture);
        parseCaptureSwitch = (SwitchCompat) findViewById(R.id.parse_capture);
        moduleTitle = (TextView) findViewById(R.id.module_title);
        moduleMessage = (TextView) findViewById(R.id.module_message);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        consoleOutputTextView = (TextView) findViewById(R.id.console_output);
        consoleOutputTextView.setMovementMethod(new ScrollingMovementMethod());
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        // we can enable optimizations if all item views are of the same height and width for significantly smoother scrolling
        recyclerView.setHasFixedSize(true);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(context, recyclerView, new ScannerFragment.ClickListener() {
            @Override
            public void onClick(View view, int position) {
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        saveCaptureSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (mitmModule != null && b != mitmModule.isDumpToFile()) {
                    mitmModule.setDumpToFile(b);
                    Intent intent = new Intent(getBaseContext(), SniffService.class);
                    if (b) intent.putExtra(SniffService.SAVE_DUMP, true);
                    else intent.putExtra(SniffService.DELETE_DUMP, true);
                    startService(intent);
                }
            }
        });
        parseCaptureSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean b = parseCaptureSwitch.isChecked();
                PreferencesUtils.setParseCapture(context, b);
                if (mitmModule != null) {
                    Intent intent = new Intent(context, ParsePcapService.class);
                    if (b) intent.putExtra(ParsePcapService.PCAP_PATH, mitmModule.getDumpPath());
                    else intent.putExtra(ParsePcapService.READ_PCAP_STOP, true);
                    startService(intent);
                }
            }
        });


        // setting toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setInterface() {
        if (mitmModule != null) {
            moduleTitle.setText(mitmModule.getModuleTitle());
            moduleMessage.setText(mitmModule.getModuleMessage());
            if (mitmModule.getDumpPath() != null) {
                saveCaptureSwitch.setVisibility(View.VISIBLE);
                parseCaptureSwitch.setVisibility(View.VISIBLE);
                if (mitmModule.isDumpToFile()) saveCaptureSwitch.setChecked(true);
                else saveCaptureSwitch.setChecked(false);
                if (parseCaptureSwitch.isChecked()) {
                    Intent intent = new Intent(context, ParsePcapService.class);
                    intent.putExtra(ParsePcapService.PCAP_PATH, mitmModule.getDumpPath());
                    startService(intent);
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
                consoleOutputTextView.append("\n" + intent.getStringExtra(SniffService.CONSOLE_MESSAGE));
            }
            if (intent.getStringExtra(ParsePcapService.ERROR_MESSAGE) != null && consoleOutputTextView != null) {
                Snackbar.make(consoleOutputTextView, intent.getStringExtra(ParsePcapService.ERROR_MESSAGE), Snackbar.LENGTH_LONG).show();
            } else if (intent.getStringExtra(ParsePcapService.MESSAGE) != null && consoleOutputTextView != null) {
                Snackbar.make(consoleOutputTextView, intent.getStringExtra(ParsePcapService.MESSAGE), Snackbar.LENGTH_SHORT).show();
            } else if (intent.getBooleanExtra(ParsePcapService.READ_PCAP_STOP, false)) {
                parseCaptureSwitch.setChecked(false);
            } else if (intent.getBooleanExtra(ParsePcapService.NEW_PACKET, false)) {
                if (ipPacketAdapter != null)
                    ipPacketAdapter.notifyDataSetChanged();
                else {
                    ipPacketAdapter = new IpPacketAdapter(context, ParsePcapService.parsedPackets);
                    recyclerView.setAdapter(ipPacketAdapter);
                }
            }

        }
    };

    @Override
    public void onResume() {
        super.onResume();
        parseCaptureSwitch.setChecked(PreferencesUtils.shouldParseCapture(context));
        try {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(SniffService.TAG);
            intentFilter.addAction(ParsePcapService.TAG);
            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, intentFilter);
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "receiver already registered");
        }
        Intent i = new Intent(this, SniffService.class);
        i.putExtra(SniffService.RETRIEVE_MITM_MODULE, true);
        startService(i);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.main_menu);
        toolbar.setOnMenuItemClickListener(
                new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return onOptionsItemSelected(item);
                    }
                });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_settings was selected
            case R.id.action_settings:
                Intent intent = new Intent(this, OptionsActivity.class);
                startActivity(intent);
            default:
                break;
        }

        return true;
    }

}
