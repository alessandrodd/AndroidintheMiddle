package it.uniroma2.giadd.aitm.tasks;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.io.IOException;
import java.net.InetAddress;

import it.uniroma2.giadd.aitm.models.NetworkHost;

/**
 * Created by Alessandro Di Diego on 10/08/16.
 */

public class CheckHostTask extends AsyncTaskLoader<Boolean> {

    private static final String TAG = CheckHostTask.class.getName();

    private static final int DEFAULT_TIMEOUT = 3000;

    private NetworkHost networkHost;

    public CheckHostTask(Context context, NetworkHost networkHost) {
        super(context);
        this.networkHost = networkHost;
    }

    @Override
    public Boolean loadInBackground() {
        Boolean reachable = false;
        if (networkHost == null) return false;
        try {
            reachable = InetAddress.getByName(networkHost.getIp()).isReachable(DEFAULT_TIMEOUT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reachable;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }


    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        onStopLoading();
    }

    @Override
    public void onCanceled(Boolean data) {
        // Attempt to cancel the current asynchronous load.
        super.onCanceled(data);
    }


}
