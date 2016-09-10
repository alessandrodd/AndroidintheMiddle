package it.uniroma2.giadd.aitm.models.modules;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.SocketException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;

import it.uniroma2.giadd.aitm.R;
import it.uniroma2.giadd.aitm.interfaces.OnCommandListener;
import it.uniroma2.giadd.aitm.managers.RootManager;
import it.uniroma2.giadd.aitm.models.exceptions.ParsingWhatsappCidrException;

/**
 * Created by Alessandro Di Diego on 13/08/16.
 */

public class SniffWhatsAppModule extends MitmModule implements Parcelable {

    private static final String TAG = SniffWhatsAppModule.class.getName();

    private static final String TCPDUMP_COMMAND = "tcpdump host <target> -i <interface> -XSs 0 -U -w <path> and not arp and not rarp and \\(<whatsappfilter>\\)";

    public SniffWhatsAppModule(Context context, String target, @NonNull String path, String cidrTxtPath, List<String> additionalCommands) throws SocketException, ParsingWhatsappCidrException {
        super(context, true, target, path, additionalCommands);

        setModuleTitle(context.getString(R.string.module_sniffwhatsapp_title));
        setModuleMessage(context.getString(R.string.module_sniffwhatsapp_message));

        if (cidrTxtPath == null || cidrTxtPath.isEmpty())
            throw new ParsingWhatsappCidrException("cidrTextPath cannot be null or empty");

        ArrayList<String> whatsappNets = parseCidr(cidrTxtPath);
        String whatsappFilter = "";
        int i;
        for (i = 0; i < whatsappNets.size(); i++) {
            whatsappFilter += "net " + whatsappNets.get(i);
            if (i < (whatsappNets.size() - 1)) whatsappFilter += " or ";
        }
        //dump to file
        setDumpToFile(true);
        String command = context.getFilesDir() + "/" + TCPDUMP_COMMAND;
        command = command.replaceAll("<path>", path);
        command = command.replaceAll("<interface>", getInterfaceName());
        command = command.replaceAll("<target>", target);
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

    private ArrayList<String> parseCidr(String cidrTxtPath) throws ParsingWhatsappCidrException {
        ArrayList<String> whatsappNets = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(cidrTxtPath));
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) whatsappNets.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new ParsingWhatsappCidrException("Unable to parse cidr: " + e.getMessage());
        }
        return whatsappNets;
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
