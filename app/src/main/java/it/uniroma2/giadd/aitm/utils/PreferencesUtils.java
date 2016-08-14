package it.uniroma2.giadd.aitm.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Alessandro Di Diego on 14/08/16.
 */

public class PreferencesUtils {

    private static final String SHARED_PREFERENCES_KEY = "it.uniroma2.giadd.mitm";
    private static final String INTERFACE_NAME = "INTERFACE_NAME";

    private static SharedPreferences sharedPreferences = null;

    private PreferencesUtils(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
    }

    private static synchronized SharedPreferences getInstance(Context context) {
        if (sharedPreferences == null)
            new PreferencesUtils(context);
        return sharedPreferences;
    }

    public static String getInterfaceName(Context context) {
        return getInstance(context).getString(INTERFACE_NAME, null);
    }

    public static void setInterfaceName(Context context, String interfaceName) {
        getInstance(context).edit().putString(INTERFACE_NAME, interfaceName).apply();
    }

}
