package it.uniroma2.giadd.aitm.models.modules;

import android.app.ProgressDialog;
import android.content.Context;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import it.uniroma2.giadd.aitm.R;
import it.uniroma2.giadd.aitm.interfaces.AutonomousSystemGrabber;
import it.uniroma2.giadd.aitm.interfaces.OnAutonomousSystemGrabbedListener;
import it.uniroma2.giadd.aitm.models.exceptions.ParsingWhatsappCidrException;
import it.uniroma2.giadd.aitm.utils.PermissionUtils;

/**
 * Created by Alessandro Di Diego on 01/10/16.
 */

public class ASGrabberWhatsapp implements AutonomousSystemGrabber {

    private static final String WHATSAPP_CIDR_URI = "https://www.whatsapp.com/cidr.txt";

    @Override
    public void getRoutingPrefixes(final Context context, final OnAutonomousSystemGrabbedListener listener) {
        if (!PermissionUtils.isWriteStorageAllowed(context)) {
            listener.onError(context.getString(R.string.error_write_permissions));
            return;
        }
        final String whatsappCidrPath = context.getFilesDir() + "/cidr.txt";
        final ProgressDialog whatsappUpdateProgressDialog = new ProgressDialog(context);
        whatsappUpdateProgressDialog.setMessage(context.getString(R.string.whatsapp_cidr_update_message));
        whatsappUpdateProgressDialog.setCancelable(false);
        whatsappUpdateProgressDialog.show();
        Ion.with(context)
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
                                listener.onError(context.getString(R.string.error_cidr_retrieve));
                                return;
                            }
                        } else cidrPath = file.getPath();
                        try {
                            ArrayList<String> whatsappNets = parseCidr(cidrPath);
                            listener.onSuccess(whatsappNets);
                        } catch (ParsingWhatsappCidrException e1) {
                            listener.onError(e1.getMessage());
                        }
                    }
                });
    }

    private ArrayList<String> parseCidr(String cidrTxtPath) throws ParsingWhatsappCidrException {
        ArrayList<String> whatsappNets = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(cidrTxtPath));
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) whatsappNets.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new ParsingWhatsappCidrException("Unable to parse cidr: " + e.getMessage());
        }
        return whatsappNets;
    }
}
