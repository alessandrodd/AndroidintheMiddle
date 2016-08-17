package it.uniroma2.giadd.aitm.models.modules;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Alessandro Di Diego on 13/08/16.
 */

public abstract class MitmModule {

    // enable/disable kernel routing
    private static final String COMMAND_KERNEL_ROUTING = "echo <0or1> > /proc/sys/net/ipv4/ip_forward";
    // enable iptables packet forwarding (needed for Android 4.0)
    private static final String COMMAND_IPTABLES_FORWARD_ACCEPT = "iptables -P FORWARD ACCEPT";
    // enable iptables on Android 5.0+ Tethering
    private static final String COMMAND_IPTABLES_TETHERING_ENABLE = "iptables -I natctrl_FORWARD -j RETURN";
    private static final String COMMAND_IPTABLES_TETHERING_DISABLE = "iptables -D natctrl_FORWARD -j RETURN";
    // This makes masquerading the default policy for any outgoing packet, including any forwarded packet.
    // All forwarded packets will appear to come from the masquerading host.
    private static final String COMMAND_IPTABLES_MASQUERADING_ENABLE = "iptables -t nat -A POSTROUTING -j MASQUERADE";
    private static final String COMMAND_IPTABLES_MASQUERADING_DISABLE = "iptables -t nat -D POSTROUTING -j MASQUERADE";

    protected List<String> commands = new LinkedList<>();
    protected boolean dumpToFile = false;
    protected String binaryFolder;


    protected MitmModule(Context context, List<String> additionalCommands) {
        binaryFolder = context.getFilesDir() + "/";
        if (additionalCommands != null)
            commands.addAll(additionalCommands);
    }

    protected MitmModule(Context context) {
        binaryFolder = context.getFilesDir() + "/";
    }

    protected MitmModule() {
    }

    protected List<String> getKernelRoutingCommands(boolean enabled) {
        List<String> commandList = new LinkedList<>();
        if (enabled) {
            commandList.add(COMMAND_KERNEL_ROUTING.replaceAll("<0or1>", "1"));
            commandList.add(binaryFolder + COMMAND_IPTABLES_FORWARD_ACCEPT);
            commandList.add(binaryFolder + COMMAND_IPTABLES_TETHERING_ENABLE);
            commandList.add(binaryFolder + COMMAND_IPTABLES_MASQUERADING_ENABLE);
        } else {
            commandList.add(COMMAND_KERNEL_ROUTING.replaceAll("<0or1>", "0"));
            commandList.add(binaryFolder + COMMAND_IPTABLES_TETHERING_DISABLE);
            commandList.add(binaryFolder + COMMAND_IPTABLES_MASQUERADING_DISABLE);
        }
        return commandList;
    }

    protected void addKernelRoutingCommand(boolean enabled) {
        commands.addAll(getKernelRoutingCommands(enabled));
    }

    public List<String> getCommands() {
        return commands;
    }

    public boolean isDumpToFile() {
        return dumpToFile;
    }

    public void setDumpToFile(boolean dumpToFile) {
        this.dumpToFile = dumpToFile;
    }

    public abstract void onModuleTermination(Context context);

}
