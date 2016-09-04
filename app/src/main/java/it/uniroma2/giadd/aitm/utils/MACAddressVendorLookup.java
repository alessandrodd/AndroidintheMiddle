package it.uniroma2.giadd.aitm.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import it.uniroma2.giadd.aitm.R;

/**
 * Created by Alessandro Di Diego
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public class MACAddressVendorLookup {

    private static final String TAG = MACAddressVendorLookup.class.getName();

    private static final String COL_MACPREFIX = "MACPREFIX";
    private static final String COL_VENDOR = "VENDOR";

    private static final String DATABASE_NAME = "MAC_VENDOR_DB";
    private static final String FTS_VIRTUAL_TABLE = "FTS";
    private static final int DATABASE_VERSION = 1;

    private final DatabaseOpenHelper mDatabaseOpenHelper;
    private int macPrefixesResource;

    @SuppressWarnings("unused")
    public MACAddressVendorLookup(Context context) {
        // default MAC prefixes file used
        this(context, R.raw.nmap_mac_prefixes);
    }

    @SuppressWarnings("WeakerAccess")
    public MACAddressVendorLookup(Context context, int macPrefixesResource) {
        mDatabaseOpenHelper = new DatabaseOpenHelper(context);
        this.macPrefixesResource = macPrefixesResource;
    }

    @SuppressWarnings("WeakerAccess")
    public void initializeSync(Context context, boolean reinitialize) {
        if (reinitialize) context.deleteDatabase(DATABASE_NAME);
        if (isTablePopulated() && !reinitialize) return;
        try {
            loadEntries(context);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unused")
    public void initializeAsync(final Context context, final boolean reinitialize, final Runnable callback) {
        final Handler mHandler = new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {
                // Perform long-running task here
                initializeSync(context, reinitialize);

                // update UI
                mHandler.post(callback);
            }
        }).start();
    }


    public boolean isTablePopulated() {
        String count = "count(*)";
        Cursor cursor = query(null, null, new String[]{count});
        int icount = 0;
        if (cursor != null) {
            icount = cursor.getInt(0);
        }
        return icount > 0;
    }

    @SuppressWarnings("unused")
    public String getVendor(String macAddress) {
        // aa:bb:cc:dd:ee:ff to AABBCCDDEEFF
        String formattedMAC = macAddress.replaceAll(":", "").trim().toUpperCase(Locale.ENGLISH);
        if (formattedMAC.length() < 6) {
            Log.e(TAG, "BAD MAC address, too short: " + macAddress);
            return null;
        }
        // AABBCCDDEEFF to AABBCC
        formattedMAC = formattedMAC.substring(0, 6);
        String selection = COL_MACPREFIX + " MATCH ?";
        String[] selectionArgs = new String[]{formattedMAC};

        Cursor cursor = query(selection, selectionArgs, new String[]{COL_VENDOR});
        if (cursor == null) return null;
        String vendor = cursor.getString(cursor.getColumnIndex(COL_VENDOR));
        cursor.close();
        return vendor;
    }

    private Cursor query(String selection, String[] selectionArgs, String[] columns) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(FTS_VIRTUAL_TABLE);

        Cursor cursor = builder.query(mDatabaseOpenHelper.getReadableDatabase(),
                columns, selection, selectionArgs, null, null, null);

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }


    private void loadEntries(Context context) throws IOException {
        final Resources resources = context.getResources();
        InputStream inputStream = resources.openRawResource(macPrefixesResource);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        Map<String, String> entryList = new HashMap<>();

        //noinspection TryFinallyCanBeTryWithResources
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                // if empty lines or comment skip this iteration
                if (line.isEmpty() || line.trim().charAt(0) == '#') continue;
                String[] strings = TextUtils.split(line, " ");
                // if less than two pieces skip this iteration
                if (strings.length < 2) continue;
                String vendor = line.replaceAll(strings[0], "").trim();
                entryList.put(strings[0].trim(), vendor);
            }
        } finally {
            //noinspection ThrowFromFinallyBlock
            reader.close();
            mDatabaseOpenHelper.addEntryList(entryList);
        }
    }

    public void setMacPrefixesResource(int macPrefixesResource) {
        this.macPrefixesResource = macPrefixesResource;
    }

    private static class DatabaseOpenHelper extends SQLiteOpenHelper {

        private SQLiteDatabase mDatabase;

        private static final String FTS_TABLE_CREATE =
                "CREATE VIRTUAL TABLE " + FTS_VIRTUAL_TABLE +
                        " USING fts3 (" +
                        COL_MACPREFIX + ", " +
                        COL_VENDOR + ")";

        DatabaseOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            mDatabase = db;
            mDatabase.execSQL(FTS_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + FTS_VIRTUAL_TABLE);
            onCreate(db);
        }

        @SuppressWarnings("WeakerAccess")
        public long addEntry(String prefix, String vendor) {
            ContentValues initialValues = new ContentValues();
            initialValues.put(MACAddressVendorLookup.COL_MACPREFIX, prefix);
            initialValues.put(MACAddressVendorLookup.COL_VENDOR, vendor);
            return this.getWritableDatabase().insert(MACAddressVendorLookup.FTS_VIRTUAL_TABLE, null, initialValues);
        }

        @SuppressWarnings("WeakerAccess")
        public void addEntryList(Map<String, String> entryList) {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            for (Map.Entry<String, String> pair : entryList.entrySet()) {
                ContentValues initialValues = new ContentValues();
                initialValues.put(MACAddressVendorLookup.COL_MACPREFIX, pair.getKey());
                initialValues.put(MACAddressVendorLookup.COL_VENDOR, pair.getValue());
                long id = db.insert(MACAddressVendorLookup.FTS_VIRTUAL_TABLE, null, initialValues);
                if (id < 0) {
                    Log.e(TAG, "unable to parse vendor with prefix: " + pair.getKey() + " and value: " + pair.getValue());
                }
            }
            db.setTransactionSuccessful();
            db.endTransaction();
        }
    }
}
