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

public class SniffImoModule extends MitmModule implements Parcelable {

    private static final String TAG = SniffImoModule.class.getName();
    public static final String PREFIX = "imo_";

    private static final String TCPDUMP_COMMAND = "tcpdump -i <interface> -XSs 0 -U -w <path> host <target> and \"not arp and not rarp and (<netfilter>) and (port 443 or port 5222 or port 5223 or port 5228)\"";

    public SniffImoModule() {
        super();
        setForwardConnections(true);
    }

    @Override
    public void initialize(Context context) {
        super.initialize(context);
        setModuleTitle(context.getString(R.string.module_sniffimo_title));
        setModuleMessage(context.getString(R.string.module_sniffimo_message));

        String netfilter = "";
        int i;
        for (i = 0; i < getNets().size(); i++) {
            netfilter += "net " + getNets().get(i);
            if (i < (getNets().size() - 1)) netfilter += " or ";
        }
        //dump to file
        setDumpToFile(true);
        String command = context.getFilesDir() + "/" + TCPDUMP_COMMAND;
        command = command.replaceAll("<path>", getDumpPath());
        command = command.replaceAll("<interface>", getInterfaceName());
        command = command.replaceAll("<target>", getTarget());
        command = command.replace("<netfilter>", netfilter);
        commands.add(command);
        largeLog(TAG, command);
    }

    private static void largeLog(String tag, String content) {
        if (content.length() > 4000) {
            Log.d(tag, content.substring(0, 4000));
            largeLog(tag, content.substring(4000));
        } else {
            Log.d(tag, content);
        }
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

    protected SniffImoModule(Parcel in) {
        super(in);
    }

    public static final Creator<SniffImoModule> CREATOR = new Creator<SniffImoModule>() {
        @Override
        public SniffImoModule createFromParcel(Parcel source) {
            return new SniffImoModule(source);
        }

        @Override
        public SniffImoModule[] newArray(int size) {
            return new SniffImoModule[size];
        }
    };
}
