package it.uniroma2.giadd.aitm.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;

import it.uniroma2.giadd.aitm.CaptureActivity;
import it.uniroma2.giadd.aitm.R;
import it.uniroma2.giadd.aitm.models.MyIpPacket;
import it.uniroma2.giadd.aitm.tasks.ParsePcapTask;
import it.uniroma2.giadd.aitm.utils.IpPacketDBHandler;

/**
 * Created by Alessandro Di Diego on 13/08/16.
 */

public class ParsePcapService extends Service {

    @SuppressWarnings("FieldCanBeLocal")
    private static int ONGOING_NOTIFICATION_ID = 30201; // just a number

    public static final String TAG = ParsePcapService.class.getName();
    public static final String PCAP_PATH = "PCAP_PATH ";
    public static final String READ_PCAP_STOP = "READ_PCAP_STOP";
    public static final String MESSAGE = "MESSAGE";
    public static final String ERROR_MESSAGE = "ERROR_MESSAGE";
    public static final String NEW_PACKET = "NEW_PACKET";
    public static final String RETRIEVE_DUMP_PATH = "RETRIEVE_DUMP_PATH";

    private IpPacketDBHandler dbHandler = new IpPacketDBHandler(this);
    private AsyncTask parsePcapTask = null;
    private String currentPcapPath;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getBooleanExtra(READ_PCAP_STOP, false)) {
            stopSelf();
        } else if (intent.getStringExtra(PCAP_PATH) != null && !intent.getStringExtra(PCAP_PATH).equals(currentPcapPath)) {
            // A new pcap path was provided

            // stop old parsing task
            if (parsePcapTask != null) {
                parsePcapTask.cancel(true);
                parsePcapTask = null;
            }
            dbHandler.resetDatabase();
            currentPcapPath = intent.getStringExtra(PCAP_PATH);

            // start parsing the new pcap file
            parsePcapTask = new ParsePcapTask(currentPcapPath) {

                @Override
                protected void onProgressUpdate(MyIpPacket... values) {
                    super.onProgressUpdate(values);
                    dbHandler.addIpPacket(values[0]);
                    notifyNewPacket();
                }

                @Override
                protected void onPostExecute(Long offset) {
                    super.onPostExecute(offset);
                    if (offset < 0)
                        sendErrorMessage(getString(R.string.error_parsing_pcap) + currentPcapPath);
                    else
                        sendMessage(String.format(getString(R.string.message_parsed_pcap), offset));
                    currentPcapPath = null;
                }
            }.execute();
            showNotification();
        } else if (intent.getBooleanExtra(RETRIEVE_DUMP_PATH, false)) {
            sendDumpPath();
        }
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        stopParsing();
        //dbHandler.resetDatabase();
        super.onDestroy();
    }

    private void showNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        Intent stopIntent = new Intent(this, this.getClass());
        stopIntent.putExtra(READ_PCAP_STOP, true);
        PendingIntent stopPendingIntent = PendingIntent.getService(this, 1, stopIntent, 0);
        Intent notificationIntent = new Intent(this, CaptureActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification = builder.setSmallIcon(R.drawable.ic_antenna).setTicker(getString(R.string.parsing_pcap_message))
                .setAutoCancel(false).setContentTitle(getString(R.string.app_name))
                .setContentText(String.format(getString(R.string.monitoring_pcap_file), currentPcapPath)).addAction(R.drawable.ic_action_stop, getString(R.string.stop_parsing_pcap), stopPendingIntent).setContentIntent(pendingIntent).build();
        startForeground(ONGOING_NOTIFICATION_ID, notification);
    }

    void stopParsing() {
        if (parsePcapTask != null) {
            parsePcapTask.cancel(true);
            parsePcapTask = null;
        }
        currentPcapPath = null;
        sendStopMessage();
    }

    private void sendStopMessage() {
        Intent intent = new Intent(TAG);
        intent.putExtra(READ_PCAP_STOP, true);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendDumpPath() {
        Intent intent = new Intent(TAG);
        intent.putExtra(RETRIEVE_DUMP_PATH, currentPcapPath);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendMessage(String message) {
        Intent intent = new Intent(TAG);
        intent.putExtra(MESSAGE, message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void notifyNewPacket() {
        Intent intent = new Intent(TAG);
        intent.putExtra(NEW_PACKET, true);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendErrorMessage(String message) {
        Intent intent = new Intent(TAG);
        intent.putExtra(ERROR_MESSAGE, message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
