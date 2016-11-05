package it.uniroma2.giadd.aitm.models.modules;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import it.uniroma2.giadd.aitm.R;

/**
 * Created by Alessandro Di Diego on 13/08/16.
 */

public class ModuleKillConnection extends ModuleMitm implements Parcelable {

    private static final String TAG = ModuleKillConnection.class.getName();

    public ModuleKillConnection() {
        super();
        setForwardConnections(false);
    }

    @Override
    public void initialize(Context context) {
        super.initialize(context);
        setModuleTitle(context.getString(R.string.module_killconnection_title));
        setModuleMessage(context.getString(R.string.module_killconnection_message));
    }

    @Override
    public void onModuleTermination(Context context) {
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

    protected ModuleKillConnection(Parcel in) {
        super(in);
    }

    public static final Creator<ModuleKillConnection> CREATOR = new Creator<ModuleKillConnection>() {
        @Override
        public ModuleKillConnection createFromParcel(Parcel source) {
            return new ModuleKillConnection(source);
        }

        @Override
        public ModuleKillConnection[] newArray(int size) {
            return new ModuleKillConnection[size];
        }
    };
}
