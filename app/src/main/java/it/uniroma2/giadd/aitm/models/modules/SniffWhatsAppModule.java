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

public class SniffWhatsAppModule extends MitmModule implements Parcelable {

    private static final String TAG = SniffWhatsAppModule.class.getName();
    public static final String PREFIX = "whatsapp_";

    private static final String TCPDUMP_COMMAND = "tcpdump -i <interface> -XSs 0 -U -w <path> host <target> and \"not arp and not rarp and (<whatsappfilter>) and (port 80 or port 443 or port 4244 or port 5222 or port 5223 or port 5228 or port 5242)\"";


    public SniffWhatsAppModule() {
        super();
        setForwardConnections(true);
    }

    @Override
    public void initialize(Context context) {
        super.initialize(context);
        setModuleTitle(context.getString(R.string.module_sniffwhatsapp_title));
        setModuleMessage(context.getString(R.string.module_sniffwhatsapp_message));

        String whatsappFilter = "";
        int i;
        for (i = 0; i < getNets().size(); i++) {
            whatsappFilter += "net " + getNets().get(i);
            if (i < (getNets().size() - 1)) whatsappFilter += " or ";
        }
        //dump to file
        setDumpToFile(true);
        String command = context.getFilesDir() + "/" + TCPDUMP_COMMAND;
        command = command.replaceAll("<path>", getDumpPath());
        command = command.replaceAll("<interface>", getInterfaceName());
        command = command.replaceAll("<target>", getTarget());
        command = command.replace("<whatsappfilter>", whatsappFilter);
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

    protected SniffWhatsAppModule(Parcel in) {
        super(in);
    }

    public static final Creator<SniffWhatsAppModule> CREATOR = new Creator<SniffWhatsAppModule>() {
        @Override
        public SniffWhatsAppModule createFromParcel(Parcel source) {
            return new SniffWhatsAppModule(source);
        }

        @Override
        public SniffWhatsAppModule[] newArray(int size) {
            return new SniffWhatsAppModule[size];
        }
    };
}
