package it.uniroma2.giadd.aitm.managers;

import android.content.Context;
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

    public static final int SIGINT = 2;
    public static final int SIGKILL = 9;
    private static final String COMMAND_KILL_BY_NAME = "mykill <search_string> <signal>";


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

    @Deprecated
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

    private void execCommands(List<String> commands, int id, final OnCommandListener callback) {
        for (String command : commands)
            Log.d("DBG", "Executing this command: " + command);
        rootSession.addCommand(commands,
                id, // a command id
                new Shell.OnCommandLineListener() {
                    @Override
                    public void onCommandResult(int commandCode, int exitCode) {
                        if (callback != null)
                            callback.onCommandResult(commandCode, exitCode);
                    }

                    @Override
                    public void onLine(String line) {
                        if (callback != null)
                            callback.onLine(line);
                    }
                });
    }

    private void execCommand(String command, int id, final OnCommandListener callback) {
        Log.d("DBG", "Executing this command: " + command);
        rootSession.addCommand(command,
                id, // a command id
                new Shell.OnCommandLineListener() {
                    @Override
                    public void onCommandResult(int commandCode, int exitCode) {
                        if (callback != null)
                            callback.onCommandResult(commandCode, exitCode);
                    }

                    @Override
                    public void onLine(String line) {
                        if (callback != null)
                            callback.onLine(line);
                    }
                });
    }

    public void execSuCommandsAsync(final List<String> commands, final int id, final OnCommandListener callback) {
        // start the shell in the background and keep it alive as long as the app is running
        if (rootSession != null) {
            execCommands(commands, id, callback);
            return;
        }
        rootSession = new Shell.Builder().
                useSU().
                setWantSTDERR(true).setOnSTDERRLineListener(callback).
                setMinimalLogging(false).setWatchdogTimeout(0).
                open(new Shell.OnCommandResultListener() {
                    @Override
                    public void onCommandResult(int commandCode, int exitCode, List<String> output) {
                        if (exitCode != Shell.OnCommandResultListener.SHELL_RUNNING) {
                            if (callback != null)
                                callback.onShellError(exitCode);
                        } else execCommands(commands, id, callback);

                    }
                });
    }

    public void execSuCommandAsync(final String command, final int id, final OnCommandListener callback) {
        // start the shell in the background and keep it alive as long as the app is running
        if (rootSession != null) {
            execCommand(command, id, callback);
            return;
        }
        rootSession = new Shell.Builder().
                useSU().
                setWantSTDERR(true).setOnSTDERRLineListener(callback).
                setMinimalLogging(false).setWatchdogTimeout(0).
                open(new Shell.OnCommandResultListener() {
                    @Override
                    public void onCommandResult(int commandCode, int exitCode, List<String> output) {
                        if (exitCode != Shell.OnCommandResultListener.SHELL_RUNNING) {
                            if (callback != null)
                                callback.onShellError(exitCode);
                        } else execCommand(command, id, callback);

                    }
                });
    }

    public void closeShellSync() {
        if (rootSession != null) rootSession.kill();
    }

    public void closeShellAsync() {
        final Handler mHandler = new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {
                closeShellSync();

                // update UI
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Shell closed");
                    }
                });
            }
        }).start();
    }

    public static void killByName(Context context, String processName, int signal, OnCommandListener callback) {
        String folder = context.getFilesDir() + "/";
        String command = folder + COMMAND_KILL_BY_NAME.replaceAll("<search_string>", processName).replaceAll("<signal>", "" + signal);
        RootManager rootManager = new RootManager();
        rootManager.execSuCommandAsync(command, 666, callback);
    }
}



