package it.uniroma2.giadd.aitm.models.modules;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import it.uniroma2.giadd.aitm.R;

/**
 * Created by Alessandro Di Diego on 13/08/16.
 */

public class KillConnectionModule extends MitmModule implements Parcelable {

    private static final String TAG = KillConnectionModule.class.getName();

    public KillConnectionModule() {
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

    protected KillConnectionModule(Parcel in) {
        super(in);
    }

    public static final Creator<KillConnectionModule> CREATOR = new Creator<KillConnectionModule>() {
        @Override
        public KillConnectionModule createFromParcel(Parcel source) {
            return new KillConnectionModule(source);
        }

        @Override
        public KillConnectionModule[] newArray(int size) {
            return new KillConnectionModule[size];
        }
    };
}
