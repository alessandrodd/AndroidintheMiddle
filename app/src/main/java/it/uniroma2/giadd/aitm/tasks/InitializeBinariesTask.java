package it.uniroma2.giadd.aitm.tasks;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.annotation.BoolRes;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import it.uniroma2.giadd.aitm.managers.ArpTableReader;
import it.uniroma2.giadd.aitm.managers.BinaryManager;
import it.uniroma2.giadd.aitm.managers.PacketSender;
import it.uniroma2.giadd.aitm.models.MACAddress;
import it.uniroma2.giadd.aitm.models.NetworkHost;
import it.uniroma2.giadd.aitm.utils.IPUtils;
import it.uniroma2.giadd.aitm.utils.MACAddressVendorLookup;
import it.uniroma2.giadd.aitm.utils.NetworkUtils;
import it.uniroma2.giadd.aitm.utils.SubnetUtils;

/**
 * Created by Alessandro Di Diego on 10/08/16.
 */

public class InitializeBinariesTask extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = InitializeBinariesTask.class.getName();

    private BinaryManager binaryManager;

    public InitializeBinariesTask(Context context) {
        binaryManager = new BinaryManager(context);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            binaryManager.initializeBinaries();
            return false;
        } catch (IOException e) {
            return true;
        }
    }

    @Override
    protected void onPostExecute(Boolean error) {
        super.onPostExecute(error);
    }
}
