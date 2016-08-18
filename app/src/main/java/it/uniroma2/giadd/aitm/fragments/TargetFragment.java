package it.uniroma2.giadd.aitm.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import it.uniroma2.giadd.aitm.R;
import it.uniroma2.giadd.aitm.models.NetworkHost;
import it.uniroma2.giadd.aitm.models.modules.SniffAllModule;
import it.uniroma2.giadd.aitm.services.SniffService;
import it.uniroma2.giadd.aitm.tasks.CheckHostTask;
import it.uniroma2.giadd.aitm.utils.PermissionUtils;

/**
 * Created by Alessandro Di Diego
 */

public class TargetFragment extends Fragment implements LoaderManager.LoaderCallbacks<Boolean> {

    private static final String TAG = TargetFragment.class.getName();
    private static final String HOST_KEY = "host";
    private static final char[] ILLEGAL_CHARACTERS = {'/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':'};


    private TargetFragment thisFragment;
    private NetworkHost host;
    private Snackbar snackbar;

    public static TargetFragment newInstance(NetworkHost networkHost) {
        TargetFragment myFragment = new TargetFragment();
        Bundle args = new Bundle();
        args.putParcelable(HOST_KEY, networkHost);
        myFragment.setArguments(args);
        return myFragment;
    }

    View.OnClickListener buttonManager = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.button_check_host_status:
                    getLoaderManager().restartLoader(1, null, thisFragment);
                    break;
                case R.id.button_kill_connection:
                    break;
                case R.id.button_mitm_all:
                    if (!PermissionUtils.isWriteStorageAllowed(getContext())) {
                        if (getView() != null)
                            Snackbar.make(getView(), R.string.error_write_permissions, Snackbar.LENGTH_LONG).show();
                        break;
                    }

                    final AlertDialog.Builder popDialog = new AlertDialog.Builder(getContext());
                    final EditText fileNameEditText = new EditText(getContext());
                    Date now = new Date(); // java.util.Date, NOT java.sql.Date or java.sql.Timestamp!
                    String dateString = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(now);
                    fileNameEditText.setText(dateString);
                    popDialog.setTitle(R.string.title_set_capture_filename);
                    popDialog.setView(fileNameEditText);
                    popDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String insertedString = fileNameEditText.getText().toString();
                            for (Character character : ILLEGAL_CHARACTERS) {
                                if (insertedString.indexOf(character) != -1 && getView() != null) {
                                    Snackbar.make(getView(), getString(R.string.error_illegal_character) + " " + character, Snackbar.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                            Intent i = new Intent(getContext(), SniffService.class);
                            SniffAllModule module = null;
                            try {
                                module = new SniffAllModule(getContext(), host.getIp(), null, Environment.getExternalStorageDirectory() + "/pcaps" + "/" + insertedString + ".pcap");
                            } catch (SocketException e) {
                                e.printStackTrace();
                                if (getView() != null)
                                    Snackbar.make(getView(), getString(R.string.error_sniff_all_module) + e.getMessage(), Snackbar.LENGTH_LONG).show();
                            }
                            i.putExtra(SniffService.MITM_MODULE, module);
                            getContext().startService(i);
                        }
                    });

                    popDialog.create();
                    popDialog.show();


                    break;
                case R.id.button_mitm_messages:
                    break;

            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            host = bundle.getParcelable(HOST_KEY);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parentViewGroup,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_target, parentViewGroup, false);
        thisFragment = this;
        if (host != null) {
            TextView ipTextView = (TextView) rootView.findViewById(R.id.ip);
            ipTextView.setText(host.getIp());
            TextView hostnameTextView = (TextView) rootView.findViewById(R.id.hostname);
            hostnameTextView.setText(host.getHostname());
            if (host.getMacAddress() != null) {
                TextView macTextView = (TextView) rootView.findViewById(R.id.mac);
                macTextView.setText(host.getMacAddress().getAddress());
                TextView vendorTextView = (TextView) rootView.findViewById(R.id.vendor);
                vendorTextView.setText(host.getMacAddress().getVendor());
            }
        }
        CardView checkHostStatusButton = (CardView) rootView.findViewById(R.id.button_check_host_status);
        CardView killConnectionButton = (CardView) rootView.findViewById(R.id.button_kill_connection);
        CardView mitmAllButton = (CardView) rootView.findViewById(R.id.button_mitm_all);
        CardView mitmMessagesButton = (CardView) rootView.findViewById(R.id.button_mitm_messages);
        checkHostStatusButton.setOnClickListener(buttonManager);
        killConnectionButton.setOnClickListener(buttonManager);
        mitmAllButton.setOnClickListener(buttonManager);
        mitmMessagesButton.setOnClickListener(buttonManager);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null)
            host = savedInstanceState.getParcelable(HOST_KEY);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(HOST_KEY, host);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Boolean> onCreateLoader(int id, Bundle args) {
        if (snackbar != null) snackbar.dismiss();
        if (getView() != null && getView().findViewById(R.id.fragment_target) != null) {
            snackbar = Snackbar.make(getView().findViewById(R.id.fragment_target), R.string.host_reachable_check, Snackbar.LENGTH_INDEFINITE);
            snackbar.show();
        }
        return new CheckHostTask(getContext(), host);
    }

    @Override
    public void onLoadFinished(Loader<Boolean> loader, Boolean reachable) {
        Log.d("DBG", "FINISHED2");
        if (snackbar != null) snackbar.dismiss();
        if (getView() != null && getView().findViewById(R.id.fragment_target) != null) {
            CardView deviceInfo = (CardView) getView().findViewById(R.id.cardview_device_info);
            if (reachable) {
                snackbar = Snackbar.make(getView().findViewById(R.id.fragment_target), R.string.host_reachable_message, Snackbar.LENGTH_LONG);
                host.setReachable(true);
                deviceInfo.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.cardview_dark_background));
            } else {
                snackbar = Snackbar.make(getView().findViewById(R.id.fragment_target), R.string.host_unreachable_message, Snackbar.LENGTH_LONG);
                host.setReachable(false);
                deviceInfo.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.transparent_red));
            }
            snackbar.show();
        }
    }

    @Override
    public void onLoaderReset(Loader<Boolean> loader) {
        if (snackbar != null) snackbar.dismiss();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (snackbar != null) snackbar.dismiss();
    }

}
