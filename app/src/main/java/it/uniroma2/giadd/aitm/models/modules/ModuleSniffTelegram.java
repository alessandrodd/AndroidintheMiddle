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

public class ModuleSniffTelegram extends ModuleMitm implements Parcelable {

    private static final String TAG = ModuleSniffTelegram.class.getName();
    public static final String PREFIX = "telegram_";

    private static final String TCPDUMP_COMMAND = "tcpdump -i <interface> -XSs 0 -U -w <path> host <target> and \"not arp and not rarp and (<netfilter>)\"";

    public ModuleSniffTelegram() {
        super();
        setForwardConnections(true);
    }

    @Override
    public void initialize(Context context) {
        super.initialize(context);
        setModuleTitle(context.getString(R.string.module_snifftelegram_title));
        setModuleMessage(context.getString(R.string.module_snifftelegram_message));

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

    protected ModuleSniffTelegram(Parcel in) {
        super(in);
    }

    public static final Creator<ModuleSniffTelegram> CREATOR = new Creator<ModuleSniffTelegram>() {
        @Override
        public ModuleSniffTelegram createFromParcel(Parcel source) {
            return new ModuleSniffTelegram(source);
        }

        @Override
        public ModuleSniffTelegram[] newArray(int size) {
            return new ModuleSniffTelegram[size];
        }
    };
}
