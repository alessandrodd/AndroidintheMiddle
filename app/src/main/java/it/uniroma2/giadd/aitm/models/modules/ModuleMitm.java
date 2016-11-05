package it.uniroma2.giadd.aitm.models.modules;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import it.uniroma2.giadd.aitm.R;
import it.uniroma2.giadd.aitm.interfaces.OnCommandListener;
import it.uniroma2.giadd.aitm.managers.RootManager;
import it.uniroma2.giadd.aitm.utils.NetworkUtils;

/**
 * Created by Alessandro Di Diego on 13/08/16.
 */

public class ModuleMitm implements Parcelable {

    private static final String TAG = ModuleMitm.class.getName();
    public static final String PREFIX = "";

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
    // Arp spoofing
    private static final String ARP_SPOOF_COMMAND = "arpspoof -i <interface> -t <target> <default gateway>";

    protected List<String> commands = new LinkedList<>();
    protected boolean dumpToFile = false;
    protected boolean forwardConnections = false;
    protected String target;
    protected List<String> nets;
    protected String binaryFolder;
    protected String dumpPath = null;
    protected String moduleTitle;
    protected String moduleMessage;
    protected String interfaceName;

    protected ModuleMitm() {
    }

    public void initialize(Context context) {
        commands.clear();
        moduleTitle = context.getString(R.string.module_mitm_title);
        moduleMessage = context.getString(R.string.module_mitm_message);
        binaryFolder = context.getFilesDir() + "/";
        addKernelRoutingCommand(this.forwardConnections);
        // spoof client connection to gateway
        String command = context.getFilesDir() + "/" + ARP_SPOOF_COMMAND;
        interfaceName = NetworkUtils.getActiveInterface(context);
        command = command.replaceAll("<interface>", this.interfaceName);
        if (target != null)
            command = command.replaceAll("<target>", this.target);
        String gateway = NetworkUtils.getWifiGateway(context);
        if (gateway == null) {
            Log.e(TAG, "Unable to get wifi gateway!");
            gateway = "";
        }
        command = command.replaceAll("<default gateway>", gateway);
        commands.add(command);
    }

    protected void setCommands(List<String> commands) {
        this.commands = commands;
    }

    public boolean isForwardConnections() {
        return forwardConnections;
    }

    protected void setForwardConnections(boolean forwardConnections) {
        this.forwardConnections = forwardConnections;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
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

    protected void setModuleMessage(String moduleMessage) {
        this.moduleMessage = moduleMessage;
    }

    protected void setModuleTitle(String moduleTitle) {
        this.moduleTitle = moduleTitle;
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

    public String getDumpPath() {
        return dumpPath;
    }

    public void setDumpPath(String dumpPath) {
        this.dumpPath = dumpPath;
    }

    public void onModuleTermination(Context context) {
        if (!isDumpToFile() && dumpPath != null) {
            File file = new File(dumpPath);
            boolean delete = file.delete();
        }
        RootManager.killByName(context, "arpspoof", RootManager.SIGINT, new OnCommandListener() {
            @Override
            public void onShellError(int exitCode) {
                Log.d(TAG, "ERROR: EXIT CODE:" + exitCode);
            }

            @Override
            public void onCommandResult(int commandCode, int exitCode) {
                Log.d(TAG, "Arpsoof terminated; EXIT CODE:" + exitCode);
                if (forwardConnections) {
                    RootManager rootManager = new RootManager();
                    rootManager.execSuCommandsAsync(getKernelRoutingCommands(false), 1, null);
                }
            }

            @Override
            public void onLine(String line) {
                Log.d(TAG, "LINE: " + line);
            }
        });
    }

    public String getModuleTitle() {
        return moduleTitle;
    }

    public String getModuleMessage() {
        return moduleMessage;
    }


    public String getInterfaceName() {
        return interfaceName;
    }

    public List<String> getNets() {
        return nets;
    }

    public void setNets(List<String> nets) {
        this.nets = nets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ModuleMitm that = (ModuleMitm) o;

        if (dumpToFile != that.dumpToFile) return false;
        if (forwardConnections != that.forwardConnections) return false;
        if (commands != null ? !commands.equals(that.commands) : that.commands != null)
            return false;
        if (target != null ? !target.equals(that.target) : that.target != null) return false;
        if (nets != null ? !nets.equals(that.nets) : that.nets != null) return false;
        if (binaryFolder != null ? !binaryFolder.equals(that.binaryFolder) : that.binaryFolder != null)
            return false;
        if (dumpPath != null ? !dumpPath.equals(that.dumpPath) : that.dumpPath != null)
            return false;
        if (moduleTitle != null ? !moduleTitle.equals(that.moduleTitle) : that.moduleTitle != null)
            return false;
        if (moduleMessage != null ? !moduleMessage.equals(that.moduleMessage) : that.moduleMessage != null)
            return false;
        return interfaceName != null ? interfaceName.equals(that.interfaceName) : that.interfaceName == null;

    }

    @Override
    public int hashCode() {
        int result = commands != null ? commands.hashCode() : 0;
        result = 31 * result + (dumpToFile ? 1 : 0);
        result = 31 * result + (forwardConnections ? 1 : 0);
        result = 31 * result + (target != null ? target.hashCode() : 0);
        result = 31 * result + (nets != null ? nets.hashCode() : 0);
        result = 31 * result + (binaryFolder != null ? binaryFolder.hashCode() : 0);
        result = 31 * result + (dumpPath != null ? dumpPath.hashCode() : 0);
        result = 31 * result + (moduleTitle != null ? moduleTitle.hashCode() : 0);
        result = 31 * result + (moduleMessage != null ? moduleMessage.hashCode() : 0);
        result = 31 * result + (interfaceName != null ? interfaceName.hashCode() : 0);
        return result;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(this.commands);
        dest.writeByte(this.dumpToFile ? (byte) 1 : (byte) 0);
        dest.writeByte(this.forwardConnections ? (byte) 1 : (byte) 0);
        dest.writeString(this.target);
        dest.writeStringList(this.nets);
        dest.writeString(this.binaryFolder);
        dest.writeString(this.dumpPath);
        dest.writeString(this.moduleTitle);
        dest.writeString(this.moduleMessage);
        dest.writeString(this.interfaceName);
    }

    protected ModuleMitm(Parcel in) {
        this.commands = in.createStringArrayList();
        this.dumpToFile = in.readByte() != 0;
        this.forwardConnections = in.readByte() != 0;
        this.target = in.readString();
        this.nets = in.createStringArrayList();
        this.binaryFolder = in.readString();
        this.dumpPath = in.readString();
        this.moduleTitle = in.readString();
        this.moduleMessage = in.readString();
        this.interfaceName = in.readString();
    }

    public static final Creator<ModuleMitm> CREATOR = new Creator<ModuleMitm>() {
        @Override
        public ModuleMitm createFromParcel(Parcel source) {
            return new ModuleMitm(source);
        }

        @Override
        public ModuleMitm[] newArray(int size) {
            return new ModuleMitm[size];
        }
    };
}
