package it.uniroma2.giadd.aitm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import it.uniroma2.giadd.aitm.models.modules.MitmModule;
import it.uniroma2.giadd.aitm.services.SniffService;

public class CaptureActivity extends AppCompatActivity {

    private final static String TAG = CaptureActivity.class.getName();

    private Context context;
    private SwitchCompat saveCaptureSwitch;
    private TextView moduleTitle;
    private TextView moduleMessage;
    private TextView consoleOutputTextView;
    private MitmModule mitmModule;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        context = this;

        saveCaptureSwitch = (SwitchCompat) findViewById(R.id.save_capture);
        moduleTitle = (TextView) findViewById(R.id.module_title);
        moduleMessage = (TextView) findViewById(R.id.module_message);
        consoleOutputTextView = (TextView) findViewById(R.id.console_output);
        consoleOutputTextView.setMovementMethod(new ScrollingMovementMethod());

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
                if (mitmModule.isDumpToFile()) saveCaptureSwitch.setChecked(true);
                else saveCaptureSwitch.setChecked(false);
            } else saveCaptureSwitch.setVisibility(View.GONE);
            consoleOutputTextView.setText("");
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getParcelableExtra(SniffService.MITM_MODULE) != null && !intent.getParcelableExtra(SniffService.MITM_MODULE).equals(mitmModule)) {
                mitmModule = intent.getParcelableExtra(SniffService.MITM_MODULE);
                setInterface();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        try {
            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(SniffService.TAG));
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
