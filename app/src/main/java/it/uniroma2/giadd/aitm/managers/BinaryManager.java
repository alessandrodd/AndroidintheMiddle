package it.uniroma2.giadd.aitm.managers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import it.uniroma2.giadd.aitm.BuildConfig;

import static android.content.Context.MODE_PRIVATE;
import static java.lang.System.in;

/**
 * Created by Alessandro Di Diego on 12/08/16.
 */

public class BinaryManager {

    private static final String TAG = BinaryManager.class.getName();

    private static final String[] binaries = new String[]{"native-binary"};
    private static final int BUFFER_SIZE = 4096;
    public static final String VERSION_CODE_KEY = "VERSION_CODE";


    private Context context;
    private SharedPreferences sharedPreferences;

    public BinaryManager(Context context) {
        sharedPreferences = context.getSharedPreferences(TAG, MODE_PRIVATE);
        this.context = context;
    }

    private String getBinariesAssetPath() {
        String abi;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            abi = Build.SUPPORTED_ABIS[0];
        } else {
            //noinspection deprecation
            abi = Build.CPU_ABI;
        }
        String folder;
        if (abi.contains("armeabi-v7a")) {
            folder = "armeabi-v7a";
        } else if (abi.contains("x86_64")) {
            folder = "x86_64";
        } else if (abi.contains("x86")) {
            folder = "x86";
        } else if (abi.contains("armeabi")) {
            folder = "armeabi";
        } else {
            Log.e(TAG, "Warning - unknown architecture, defaulting on armeabi");
            folder = "armeabi";
        }
        return folder;

    }

    @SuppressLint("CommitPrefEdits")
    public void initializeBinaries() throws IOException {
        for (String binary : binaries) {
            deleteModifedBinary(binary);
            if (!isBinaryPresent(binary)) {
                String binaryNewPath = copyAssetsToAppFilesDir(getBinariesAssetPath(), binary);
                if (binaryNewPath != null) {
                    setExecutable(binaryNewPath);
                }
            }
        }
        sharedPreferences.edit().putInt(VERSION_CODE_KEY, BuildConfig.VERSION_CODE).commit();
    }

    private boolean deleteModifedBinary(String binary) throws IOException {
        String filePath = context.getFilesDir() + "/" + binary;
        File binaryFile = new File(filePath);
        // check if exists
        if (!binaryFile.exists()) return false;
        // check if there is an older version
        int versionCode = BuildConfig.VERSION_CODE;
        if (versionCode != sharedPreferences.getInt(VERSION_CODE_KEY, 0)) {
            Log.d(TAG, "Deleting " + binary);
            boolean delete = binaryFile.delete();
            if (!delete) Log.e(TAG, "Unable to delete binary " + binary);
            return delete;
        }
        // check if corrupted
        AssetManager assetManager = context.getAssets();
        try {
            InputStream binaryInputStream = new FileInputStream(binaryFile);
            long binarySize = 0;
            long size = 0;
            long nRead;
            byte[] data = new byte[BUFFER_SIZE];
            InputStream inputStream = assetManager.open(getBinariesAssetPath() + "/" + binary);
            while ((nRead = inputStream.read(data, 0, BUFFER_SIZE)) != -1) {
                size += nRead;
            }
            while ((nRead = binaryInputStream.read(data, 0, BUFFER_SIZE)) != -1) {
                binarySize += nRead;
            }
            if (size != binaryFile.length()) {
                Log.d(TAG, "Deleting " + binary + "; asset size: " + size + " binary size: " + binarySize);
                boolean delete = binaryFile.delete();
                if (!delete) Log.e(TAG, "Unable to delete binary " + binary);
                return delete;
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Unable to determine binary size in asset!");
            throw e;
        }
        return false;
    }

    private boolean isBinaryPresent(String binary) {
        String filePath = context.getFilesDir() + "/" + binary;
        File binaryFile = new File(filePath);
        return binaryFile.exists();
    }

    private String copyAssetsToAppFilesDir(String assetFolder, String filename) throws IOException {
        AssetManager assetManager = context.getAssets();
        String filePath = null;

        InputStream in = null;
        OutputStream out = null;
        Log.d(TAG, "Attempting to copy this file: " + filename);

        final byte[] buff = new byte[BUFFER_SIZE];

        try {
            in = assetManager.open(assetFolder + "/" + filename);
            out = context.openFileOutput(filename, MODE_PRIVATE);
            Log.d(TAG, "outDir: " + context.getFilesDir());
            long size = 0;
            int nRead;
            while ((nRead = in.read(buff)) != -1) {
                out.write(buff, 0, nRead);
                size += nRead;
            }
            out.flush();
            Log.d(TAG, "Copy success: " + filename + " " + size + " bytes");
            filePath = context.getFilesDir() + "/" + filename;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to copy asset file: " + filename, e);
            throw e;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return filePath;
    }

    private boolean setExecutable(String executableFilePath) {
        File execFile = new File(executableFilePath);
        return execFile.setExecutable(true);
    }

}
