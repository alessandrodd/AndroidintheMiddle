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

// TODO: filter by *.fbcdn.net *.facebook.com

public class SniffInstagramModule extends MitmModule implements Parcelable {

    private static final String TAG = SniffInstagramModule.class.getName();
    public static final String PREFIX = "instagram_";

    private static final String TCPDUMP_COMMAND = "tcpdump -i <interface> -XSs 0 -U -w <path> host <target> and \"not arp and not rarp and (port 443)\"";

    public SniffInstagramModule() {
        super();
        setForwardConnections(true);
    }

    @Override
    public void initialize(Context context) {
        super.initialize(context);
        setModuleTitle(context.getString(R.string.module_sniffinstagram_title));
        setModuleMessage(context.getString(R.string.module_sniffinstagram_message));

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

    protected SniffInstagramModule(Parcel in) {
        super(in);
    }

    public static final Creator<SniffInstagramModule> CREATOR = new Creator<SniffInstagramModule>() {
        @Override
        public SniffInstagramModule createFromParcel(Parcel source) {
            return new SniffInstagramModule(source);
        }

        @Override
        public SniffInstagramModule[] newArray(int size) {
            return new SniffInstagramModule[size];
        }
    };
}
