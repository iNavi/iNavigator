package com.wustrans.bukaiei.inavigator.util;

import android.util.Log;

public class LogUtil {

    public final static boolean DEBUG = true;

    private final static String TAG = "HARDemo";

    public static void info(String str) {
        if(DEBUG) {
            Log.i(TAG, str);
        }
    }

    public static void err(String str) {
        if(DEBUG) {
            Log.e(TAG, str);
        }
    }
}
