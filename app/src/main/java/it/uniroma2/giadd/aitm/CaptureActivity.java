package it.uniroma2.giadd.aitm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import it.uniroma2.giadd.aitm.fragments.CaptureFragment;

public class CaptureActivity extends AppCompatActivity {

    private static final String FRAGMENT_SAVE_KEY = "FRAGMENT_SAVE_KEY";

    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        // setting toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState != null && getSupportFragmentManager().getFragment(savedInstanceState, FRAGMENT_SAVE_KEY) != null) {
            fragment = getSupportFragmentManager().getFragment(savedInstanceState, FRAGMENT_SAVE_KEY);
        }
        // initializing Activity with the first fragment
        else if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
            // Create a new Fragment to be placed in the activity layout
            fragment = new CaptureFragment();
            // Add the fragment to the 'fragment_container' FrameLayout
        }
        getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).add(R.id.fragment_container, fragment).commitAllowingStateLoss();

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
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //Save the fragment's instance
        if (fragment != null)
            getSupportFragmentManager().putFragment(outState, FRAGMENT_SAVE_KEY, fragment);
        super.onSaveInstanceState(outState);

    }

}
