package it.uniroma2.giadd.aitm.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.File;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import it.uniroma2.giadd.aitm.CaptureActivity;
import it.uniroma2.giadd.aitm.R;
import it.uniroma2.giadd.aitm.models.NetworkHost;
import it.uniroma2.giadd.aitm.models.exceptions.ParsingWhatsappCidrException;
import it.uniroma2.giadd.aitm.models.modules.SniffWhatsAppModule;
import it.uniroma2.giadd.aitm.services.SniffService;
import it.uniroma2.giadd.aitm.utils.FileUtilities;
import it.uniroma2.giadd.aitm.utils.PermissionUtils;

/**
 * Created by Alessandro Di Diego
 */

public class MessagingAppsFragment extends Fragment {

    private static final String TAG = MessagingAppsFragment.class.getName();
    private static final String HOST_KEY = "HOST_KEY";
    private static final String WHATSAPP_CIDR_URI = "https://www.whatsapp.com/cidr.txt";

    private NetworkHost host;

    public static MessagingAppsFragment newInstance(NetworkHost networkHost) {
        MessagingAppsFragment myFragment = new MessagingAppsFragment();
        Bundle args = new Bundle();
        args.putParcelable(HOST_KEY, networkHost);
        myFragment.setArguments(args);
        return myFragment;
    }

    View.OnClickListener buttonManager = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final AlertDialog.Builder popDialog = new AlertDialog.Builder(getContext());
            final EditText fileNameEditText = new EditText(getContext());
            Date now = new Date(); // java.util.Date, NOT java.sql.Date or java.sql.Timestamp!
            String filenameString = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(now);
            popDialog.setTitle(R.string.title_set_capture_filename);
            popDialog.setView(fileNameEditText);
            switch (view.getId()) {
                case R.id.button_whatsapp:
                    if (!PermissionUtils.isWriteStorageAllowed(getContext())) {
                        if (getView() != null)
                            Snackbar.make(getView(), R.string.error_write_permissions, Snackbar.LENGTH_LONG).show();
                        break;
                    }
                    filenameString = "whatsapp_" + filenameString;
                    fileNameEditText.setText(filenameString);
                    popDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String insertedString = fileNameEditText.getText().toString();
                            if (FileUtilities.getInvalidCharacter(insertedString) != null && getView() != null) {
                                Snackbar.make(getView(), getString(R.string.error_illegal_character) + " " + FileUtilities.getInvalidCharacter(insertedString), Snackbar.LENGTH_SHORT).show();
                                return;
                            }
                            updateCidrAndStartWhatsappSniffing(Environment.getExternalStorageDirectory() + "/pcaps" + "/" + insertedString + ".pcap");
                        }
                    });
                    popDialog.create();
                    popDialog.show();
                    break;
                case R.id.button_telegram:
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
        View rootView = inflater.inflate(R.layout.fragment_messaging_app, parentViewGroup, false);
        CardView whatsappButton = (CardView) rootView.findViewById(R.id.button_whatsapp);
        CardView telegramButton = (CardView) rootView.findViewById(R.id.button_telegram);
        if (host != null) {
            whatsappButton.setOnClickListener(buttonManager);
            telegramButton.setOnClickListener(buttonManager);
        } else Log.e(TAG, "Error: host cannot be null!");
        return rootView;
    }

    private void updateCidrAndStartWhatsappSniffing(final String desiredPcapPath) {
        final String whatsappCidrPath = getContext().getFilesDir() + "/cidr.txt";
        final ProgressDialog whatsappUpdateProgressDialog = new ProgressDialog(getContext());
        whatsappUpdateProgressDialog.setMessage(getString(R.string.whatsapp_cidr_update_message));
        whatsappUpdateProgressDialog.setCancelable(false);
        whatsappUpdateProgressDialog.show();
        Ion.with(getContext())
                .load(WHATSAPP_CIDR_URI)
                .progressDialog(whatsappUpdateProgressDialog)
                .write(new File(whatsappCidrPath))
                .setCallback(new FutureCallback<File>() {
                    @Override
                    public void onCompleted(Exception e, File file) {
                        whatsappUpdateProgressDialog.dismiss();
                        String cidrPath;
                        if (e != null) {
                            File cidr = new File(whatsappCidrPath);
                            if (cidr.exists() && cidr.length() > 0) {
                                cidrPath = cidr.getPath();
                            } else {
                                if (getView() != null)
                                    Snackbar.make(getView(), R.string.error_cidr_retrieve, Snackbar.LENGTH_LONG).show();
                                return;
                            }
                        } else cidrPath = file.getPath();
                        Intent i = new Intent(getContext(), SniffService.class);
                        SniffWhatsAppModule module;
                        try {
                            module = new SniffWhatsAppModule(getContext(), host.getIp(), desiredPcapPath, cidrPath, null);
                            i.putExtra(SniffService.MITM_MODULE, module);
                            getContext().startService(i);
                            i = new Intent(getContext(), CaptureActivity.class);
                            startActivity(i);
                        } catch (SocketException e1) {
                            e1.printStackTrace();
                            if (getView() != null)
                                Snackbar.make(getView(), getString(R.string.error_sniff_all_module) + e1.getMessage(), Snackbar.LENGTH_LONG).show();
                        } catch (ParsingWhatsappCidrException e1) {
                            e1.printStackTrace();
                            if (getView() != null)
                                Snackbar.make(getView(), getString(R.string.error_cidr_parse) + e1.getMessage(), Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            host = savedInstanceState.getParcelable(HOST_KEY);
        }
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
    public void onPause() {
        super.onPause();
    }

}
