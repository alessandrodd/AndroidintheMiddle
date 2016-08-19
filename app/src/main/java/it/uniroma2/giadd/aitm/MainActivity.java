package it.uniroma2.giadd.aitm;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;

import it.uniroma2.giadd.aitm.fragments.ScannerFragment;
import it.uniroma2.giadd.aitm.models.PcapParser;
import it.uniroma2.giadd.aitm.tasks.InitializeBinariesTask;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    private static final int WRITE_PERMISSION_CODE = 1;
    private static final int READ_PERMISSION_CODE = 2;

    private ProgressDialog progress;
    private AsyncTask asyncTask;
    private Context context;
    private Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        activity = this;

        // setting toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // initializing Activity with the first fragment
        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
            // Create a new Fragment to be placed in the activity layout
            ScannerFragment scannerFragment = new ScannerFragment();
            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).add(R.id.fragment_container, scannerFragment).commit();
        }

        asyncTask = new InitializeBinariesTask(this) {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progress = ProgressDialog.show(context, getString(R.string.please_wait),
                        getString(R.string.message_copying_binaries), true);
            }

            @Override
            protected void onPostExecute(Boolean error) {
                super.onPostExecute(error);
                progress.dismiss();
                if (error) {
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.activity_main), R.string.error_copy_binaries, Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION_CODE);
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION_CODE);
                }

                PcapParser pcapParser = new PcapParser(){
                    @Override
                    protected void onPacketParsed(String string) {
                        super.onPacketParsed(string);
                        Log.d("DBG", "IP:"+string);
                    }
                };
                //pcapParser.parsePcapFile(Environment.getExternalStorageDirectory()+"/pcaps/prova2.pcap");

                /*
                Debug.setDebug(true);
                RootManager rootManager = new RootManager();
                //rootManager.execSuCommandAsync("/data/user/0/it.uniroma2.giadd.aitm/files/arpspoof -i wlan0 -t 192.168.1.102 192.168.1.254", 0, new OnCommandListener() {
                rootManager.execSuCommandAsync("/data/user/0/it.uniroma2.giadd.aitm/files/tcpdump -qns 0 -A -r /data/user/0/it.uniroma2.giadd.aitm/files/prova.cap", 0, new OnCommandListener() {
                    //rootManager.execSuCommandAsync("ping www.google.it", 0, new OnCommandListener() {
                    @Override
                    public void onShellError(int exitCode) {
                        Log.d("DBG", "ERROR" + exitCode);
                    }

                    @Override
                    public void onCommandResult(int commandCode, int exitCode) {
                        Log.d("DBG", "EXITCODE" + exitCode);
                    }

                    @Override
                    public void onLine(String line) {
                        Log.d("DBG", "LINE" + line);
                    }
                });*/


            }
        }.execute();
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Checking the request code of our request
        if (requestCode == WRITE_PERMISSION_CODE || requestCode == READ_PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //permission granted
                File folder = new File(Environment.getExternalStorageDirectory() + "/pcaps");
                if (!folder.exists() && !folder.mkdir()) {
                    Snackbar.make(activity.findViewById(R.id.activity_main), getString(R.string.error_unable_create_folder), Snackbar.LENGTH_LONG).show();
                }
            } else {
                Snackbar.make(activity.findViewById(R.id.activity_main), R.string.error_permission_denied, Snackbar.LENGTH_LONG).show();
            }
        }
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

    @Override
    protected void onPause() {
        super.onPause();
        if (asyncTask != null) asyncTask.cancel(true);
    }
}
