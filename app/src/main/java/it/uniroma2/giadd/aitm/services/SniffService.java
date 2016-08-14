package it.uniroma2.giadd.aitm.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.giadd.aitm.R;
import it.uniroma2.giadd.aitm.managers.RootManager;
import it.uniroma2.giadd.aitm.managers.interfaces.OnCommandListener;
import it.uniroma2.giadd.aitm.models.MitmModule;

/**
 * Created by Alessandro Di Diego on 13/08/16.
 */

public class SniffService extends Service implements OnCommandListener {

    private static int ONGOING_NOTIFICATION_ID = 180493; // just a number

    public static final String TAG = SniffService.class.getName();
    public static final String MITM_STOP = "MITM_STOP";
    public static final String MITM_MODULE = "MITM_MODULE";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getBooleanExtra(MITM_STOP, false)) {
            stopSelf();
        }
        if (intent.getParcelableExtra(MITM_MODULE) != null) {
            MitmModule mitmModule = intent.getParcelableExtra(MITM_MODULE);
            if (mitmModule.getCommands() != null && mitmModule.getCommands().size() > 0) {
                showNotification();
                execCommands(mitmModule.getCommands());
            }

        }
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
    }

    private void showNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        Intent stopIntent = new Intent(this, this.getClass());
        stopIntent.putExtra(MITM_STOP, true);
        PendingIntent stopPendingIntent = PendingIntent.getService(this, 1, stopIntent, 0);
        Notification notification = builder.setSmallIcon(R.drawable.ic_action_navigation_refresh).setTicker(getString(R.string.mitm_started))
                .setAutoCancel(false).setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.mitm_started)).addAction(android.R.drawable.ic_media_pause, getString(R.string.stop_mitm), stopPendingIntent).build();
        startForeground(ONGOING_NOTIFICATION_ID, notification);
    }

    private void execCommands(List<String> commands) {
        RootManager rootManager = new RootManager();
        for (String command : commands) {
            rootManager.execSuCommandAsync(command, this);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onShellError(int exitCode) {
        Log.d(TAG, "Shell error, EXIT CODE: " + exitCode);
    }

    @Override
    public void onCommandResult(int commandCode, int exitCode) {
        Log.d(TAG, "EXIT CODE: " + exitCode);
    }

    @Override
    public void onLine(String line) {
        Log.d(TAG, "Line: " + line);

    }
}
