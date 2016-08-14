package it.uniroma2.giadd.aitm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import it.uniroma2.giadd.aitm.fragments.ScannerFragment;
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

    private ProgressDialog progress;
    private AsyncTask asyncTask;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

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
            }
        }.execute();

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
