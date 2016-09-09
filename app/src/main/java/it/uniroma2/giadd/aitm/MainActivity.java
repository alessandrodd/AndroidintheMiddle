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
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;

import it.uniroma2.giadd.aitm.fragments.OpenPcapFragment;
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

    private static final int WRITE_PERMISSION_CODE = 1;
    private static final int READ_PERMISSION_CODE = 2;
    private static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 3;
    private static final String FRAGMENT_SAVE_KEY = "FRAGMENT_SAVE_KEY";

    private ProgressDialog progress;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private AsyncTask asyncTask;
    private Fragment fragment;
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
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_navigation_menu);
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setVisibility(View.GONE);
        // Find our drawer view
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        // Setup drawer view
        setupDrawerContent(navigationView);

        if (savedInstanceState != null && getSupportFragmentManager().getFragment(savedInstanceState, FRAGMENT_SAVE_KEY) != null) {
            fragment = getSupportFragmentManager().getFragment(savedInstanceState, FRAGMENT_SAVE_KEY);
        } else {
            fragment = new ScannerFragment();
            setTitle(R.string.scanner);
        }
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commitAllowingStateLoss();
        // Highlight the selected item has been done by NavigationView

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
        } else {
            initializeBinaries();
            drawerLayout.setVisibility(View.VISIBLE);
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Class fragmentClass;
        switch (menuItem.getItemId()) {
            case R.id.nav_first_fragment:
                fragmentClass = ScannerFragment.class;
                break;
            case R.id.nav_second_fragment:
                fragmentClass = OpenPcapFragment.class;
                break;
            case R.id.action_settings:
                Intent intent = new Intent(this, OptionsActivity.class);
                startActivity(intent);
                return;
            default:
                fragmentClass = ScannerFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commitAllowingStateLoss();
        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        drawerLayout.closeDrawers();
    }


    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Checking the request code of our request
        if (requestCode == WRITE_PERMISSION_CODE || requestCode == READ_PERMISSION_CODE || requestCode == ASK_MULTIPLE_PERMISSION_REQUEST_CODE) {
            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //permission granted
                File folder = new File(Environment.getExternalStorageDirectory() + "/pcaps");
                if (!folder.exists() && !folder.mkdir()) {
                    Snackbar.make(activity.findViewById(R.id.activity_main), getString(R.string.error_unable_create_folder), Snackbar.LENGTH_LONG).show();
                } else {
                    // We got permissions; let's start the real behavior
                    initializeBinaries();
                    if (drawerLayout != null) {
                        drawerLayout.setVisibility(View.VISIBLE);
                        drawerLayout.openDrawer(GravityCompat.START);
                    }
                }
            } else {
                Snackbar.make(activity.findViewById(R.id.activity_main), R.string.error_permission_denied, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private void initializeBinaries() {
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
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //Save the fragment's instance
        if (fragment != null)
            getSupportFragmentManager().putFragment(outState, FRAGMENT_SAVE_KEY, fragment);
        super.onSaveInstanceState(outState);

    }

    // `onPostCreate` called when activity start-up is complete after `onStart()`
    // NOTE! Make sure to override the method with only a single `Bundle` argument
    // needed to show hamburger button
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

}
