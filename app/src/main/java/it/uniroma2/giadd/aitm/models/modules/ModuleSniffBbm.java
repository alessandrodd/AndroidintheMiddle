package it.uniroma2.giadd.aitm.models.modules;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import it.uniroma2.giadd.aitm.R;
import it.uniroma2.giadd.aitm.interfaces.OnCommandListener;
import it.uniroma2.giadd.aitm.managers.RootManager;

/**
 * Created by Alessandro Di Diego on 13/08/16.
 */

// TODO: filter by *.blackberry.net

public class ModuleSniffBbm extends ModuleMitm implements Parcelable {

    private static final String TAG = ModuleSniffBbm.class.getName();
    public static final String PREFIX = "bbm_";

    private static final String TCPDUMP_COMMAND = "tcpdump -i <interface> -XSs 0 -U -w <path> host <target> and \"not arp and not rarp and (port 443)\"";

    public ModuleSniffBbm() {
        super();
        setForwardConnections(true);
    }

    @Override
    public void initialize(Context context) {
        super.initialize(context);
        setModuleTitle(context.getString(R.string.module_sniffbbm_title));
        setModuleMessage(context.getString(R.string.module_sniffbbm_message));

        //dump to file
        setDumpToFile(true);
        String command = context.getFilesDir() + "/" + TCPDUMP_COMMAND;
        command = command.replaceAll("<path>", getDumpPath());
        command = command.replaceAll("<interface>", getInterfaceName());
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
        super.onModuleTermination(context);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    protected ModuleSniffBbm(Parcel in) {
        super(in);
    }

    public static final Creator<ModuleSniffBbm> CREATOR = new Creator<ModuleSniffBbm>() {
        @Override
        public ModuleSniffBbm createFromParcel(Parcel source) {
            return new ModuleSniffBbm(source);
        }

        @Override
        public ModuleSniffBbm[] newArray(int size) {
            return new ModuleSniffBbm[size];
        }
    };
}
