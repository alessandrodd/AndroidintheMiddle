package it.uniroma2.giadd.aitm.models.modules;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import it.uniroma2.giadd.aitm.R;

/**
 * Created by Alessandro Di Diego on 13/08/16.
 */

public class MitmModule implements Parcelable {

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
    protected String dumpPath = null;
    protected String moduleTitle;
    protected String moduleMessage;


    protected MitmModule(Context context, String dumpPath, List<String> additionalCommands) {
        this(context, dumpPath);
        if (additionalCommands != null)
            commands.addAll(additionalCommands);
    }

    protected MitmModule(Context context, String dumpPath) {
        moduleTitle = context.getString(R.string.module_mitm_title);
        moduleMessage = context.getString(R.string.module_mitm_message);
        this.dumpPath = dumpPath;
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

    public String getDumpPath() {
        return dumpPath;
    }

    public void setDumpPath(String dumpPath) {
        this.dumpPath = dumpPath;
    }

    public void onModuleTermination(Context context) {
        if (!isDumpToFile() && dumpPath != null) {
            File file = new File(dumpPath);
            file.delete();
        }
    }

    public String getModuleTitle() {
        return moduleTitle;
    }

    public String getModuleMessage() {
        return moduleMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MitmModule that = (MitmModule) o;

        if (dumpToFile != that.dumpToFile) return false;
        if (commands != null ? !commands.equals(that.commands) : that.commands != null)
            return false;
        if (binaryFolder != null ? !binaryFolder.equals(that.binaryFolder) : that.binaryFolder != null)
            return false;
        if (dumpPath != null ? !dumpPath.equals(that.dumpPath) : that.dumpPath != null)
            return false;
        if (moduleTitle != null ? !moduleTitle.equals(that.moduleTitle) : that.moduleTitle != null)
            return false;
        return moduleMessage != null ? moduleMessage.equals(that.moduleMessage) : that.moduleMessage == null;

    }

    @Override
    public int hashCode() {
        int result = commands != null ? commands.hashCode() : 0;
        result = 31 * result + (dumpToFile ? 1 : 0);
        result = 31 * result + (binaryFolder != null ? binaryFolder.hashCode() : 0);
        result = 31 * result + (dumpPath != null ? dumpPath.hashCode() : 0);
        result = 31 * result + (moduleTitle != null ? moduleTitle.hashCode() : 0);
        result = 31 * result + (moduleMessage != null ? moduleMessage.hashCode() : 0);
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
        dest.writeString(this.binaryFolder);
        dest.writeString(this.dumpPath);
        dest.writeString(this.moduleTitle);
        dest.writeString(this.moduleMessage);
    }

    protected MitmModule(Parcel in) {
        this.commands = in.createStringArrayList();
        this.dumpToFile = in.readByte() != 0;
        this.binaryFolder = in.readString();
        this.dumpPath = in.readString();
        this.moduleTitle = in.readString();
        this.moduleMessage = in.readString();
    }

    public static final Creator<MitmModule> CREATOR = new Creator<MitmModule>() {
        @Override
        public MitmModule createFromParcel(Parcel source) {
            return new MitmModule(source);
        }

        @Override
        public MitmModule[] newArray(int size) {
            return new MitmModule[size];
        }
    };
}
