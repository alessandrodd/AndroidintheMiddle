package it.uniroma2.giadd.aitm.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by Alessandro Di Diego on 17/08/16.
 */

public class PermissionUtils {

    //We are calling this method to check the permission status
    public static boolean isActionAllowed(Context context, String action) {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(context, action);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        //If permission is not granted returning false
        return false;
    }

    public static boolean isReadStorageAllowed(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return isActionAllowed(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        return true;
    }

    public static boolean isWriteStorageAllowed(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return isActionAllowed(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        return true;
    }

}
