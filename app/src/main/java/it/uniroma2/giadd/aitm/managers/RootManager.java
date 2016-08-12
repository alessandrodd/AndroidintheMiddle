package it.uniroma2.giadd.aitm.managers;

import android.os.Handler;
import android.util.Log;

import java.util.List;

import eu.chainfire.libsuperuser.Shell;
import it.uniroma2.giadd.aitm.managers.interfaces.OnCommandListener;
import it.uniroma2.giadd.aitm.managers.interfaces.OnSuCommandExecuted;

/**
 * Created by Alessandro Di Diego on 11/08/16.
 */

public class RootManager {

    private static final String TAG = RootManager.class.getName();

    private Shell.Interactive rootSession;

    public RootManager() {
    }

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

    private void execCommand(final String command, final OnCommandListener callback) {
        rootSession.addCommand(new String[]{command},
                1, // a command id
                new Shell.OnCommandLineListener() {
                    @Override
                    public void onCommandResult(int commandCode, int exitCode) {
                        callback.onCommandResult(commandCode, exitCode);
                    }

                    @Override
                    public void onLine(String line) {
                        callback.onLine(line);
                    }
                });
    }

    public void execSuCommandAsync(final String command, final OnCommandListener callback) {
        // start the shell in the background and keep it alive as long as the app is running
        if (rootSession != null) {
            execCommand(command, callback);
            return;
        }
        rootSession = new Shell.Builder().
                useSU().
                setWantSTDERR(true).
                setWatchdogTimeout(5).
                setMinimalLogging(true).
                open(new Shell.OnCommandResultListener() {
                    @Override
                    public void onCommandResult(int commandCode, int exitCode, List<String> output) {
                        if (exitCode != Shell.OnCommandResultListener.SHELL_RUNNING) {
                            callback.onShellError(exitCode);
                        } else execCommand(command, callback);

                    }
                });
    }


}
