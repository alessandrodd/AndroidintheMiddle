package it.uniroma2.giadd.aitm.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.List;

import it.uniroma2.giadd.aitm.CaptureActivity;
import it.uniroma2.giadd.aitm.R;
import it.uniroma2.giadd.aitm.managers.RootManager;
import it.uniroma2.giadd.aitm.interfaces.OnCommandListener;
import it.uniroma2.giadd.aitm.models.modules.ModuleMitm;

/**
 * Created by Alessandro Di Diego on 13/08/16.
 */

public class SniffService extends Service implements OnCommandListener {

    private static int ONGOING_NOTIFICATION_ID = 180493; // just a number

    public static final String TAG = SniffService.class.getName();
    public static final String MITM_STOP = "MITM_STOP";
    public static final String MITM_MODULE = "MITM_MODULE";
    public static final String RETRIEVE_MITM_MODULE = "RETRIEVE_MITM_MODULE";
    public static final String DELETE_DUMP = "DELETE_DUMP";
    public static final String SAVE_DUMP = "SAVE_DUMP";
    public static final String CONSOLE_MESSAGE = "CONSOLE_MESSAGE";

    private PowerManager.WakeLock wakeLock;
    private WifiManager.WifiLock wifiLock;
    private ModuleMitm mitmModule;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getBooleanExtra(MITM_STOP, false)) {
            stopSelf();
        } else if (intent.getParcelableExtra(MITM_MODULE) != null) {
            // New MITM module received; let's execute the contained commands
            mitmModule = intent.getParcelableExtra(MITM_MODULE);
            if (mitmModule.getCommands() != null && mitmModule.getCommands().size() > 0) {
                acquireLock();
                showNotification();
                execCommands(mitmModule.getCommands());
            }
        } else if (intent.getBooleanExtra(RETRIEVE_MITM_MODULE, false)) {
            sendModule();
        } else if (intent.getBooleanExtra(DELETE_DUMP, false)) {
            if (mitmModule != null) mitmModule.setDumpToFile(false);
        } else if (intent.getBooleanExtra(SAVE_DUMP, false)) {
            if (mitmModule != null) mitmModule.setDumpToFile(true);
        }

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        if (mitmModule != null) mitmModule.onModuleTermination(this);
        sendStopMessage();
        mitmModule = null;
        releaseLock();
        super.onDestroy();
    }

    private void acquireLock() {
        if (wakeLock == null) {
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            if (powerManager != null) {
                wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
                wakeLock.acquire();
            }
        } else if (!wakeLock.isHeld()) {
            wakeLock.acquire();
        }
        if (wifiLock == null) {
            WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
            if (wifiManager != null) {
                wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, TAG);
                wifiLock.acquire();
            }
        } else if (!wifiLock.isHeld()) {
            wifiLock.acquire();
        }
    }

    private void releaseLock() {
        if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }
        if (wifiLock != null) {
            wifiLock.release();
            wifiLock = null;
        }
    }

    private void showNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        Intent stopIntent = new Intent(this, this.getClass());
        stopIntent.putExtra(MITM_STOP, true);
        PendingIntent stopPendingIntent = PendingIntent.getService(this, 1, stopIntent, 0);
        Intent notificationIntent = new Intent(this, CaptureActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification = builder.setSmallIcon(R.drawable.ic_eavesdropping).setTicker(getString(R.string.mitm_started))
                .setAutoCancel(false).setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.mitm_started)).addAction(R.drawable.ic_action_stop, getString(R.string.stop_mitm), stopPendingIntent).setContentIntent(pendingIntent).build();


        startForeground(ONGOING_NOTIFICATION_ID, notification);
    }

    private void execCommands(List<String> commands) {
        for (int i = 0; i < commands.size(); i++) {
            RootManager rootManager = new RootManager();
            rootManager.execSuCommandAsync(commands.get(i), i, this);
        }
    }

    private void sendModule() {
        Intent intent = new Intent(TAG);
        if (mitmModule != null) {
            intent.putExtra(MITM_MODULE, mitmModule);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        } else sendStopMessage();
    }

    private void sendConsoleMessage(String message) {
        Intent intent = new Intent(TAG);
        intent.putExtra(CONSOLE_MESSAGE, message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendStopMessage() {
        Intent intent = new Intent(TAG);
        intent.putExtra(MITM_STOP, true);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
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
        sendConsoleMessage(line);
    }
}
