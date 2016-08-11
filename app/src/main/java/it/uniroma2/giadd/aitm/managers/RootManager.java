package it.uniroma2.giadd.aitm.managers;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.util.List;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by Alessandro Di Diego on 11/08/16.
 */

public class RootManager {

    private static final String TAG = RootManager.class.getName();

    public static boolean isRooted() {
        return Shell.SU.available();
    }

    public static List<String> execSuCommandSync(String command) {
        if (isRooted())
            return Shell.SU.run(command);
        Log.e(TAG, "Unable to exec command " + command + " device not rooted! ");
        return null;
    }

    public static void execSuCommandAsync(final String command, final OnSuCommandExecuted callback) {
        final Handler mHandler = new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {
                // Perform long-running task here
                final List<String> result = execSuCommandSync(command);

                // update UI
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuCommandExecuted(result);
                    }
                });
            }
        }).start();
    }

    public interface OnSuCommandExecuted {
        void onSuCommandExecuted(List<String> result);
    }
}
