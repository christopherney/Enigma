package com.proto.helloworld.helpers;

import android.annotation.SuppressLint;
import android.util.Log;

/**
 * Utilities
 * @Author Christopher Ney
 */
public class Utils {

    private static final String TAG = "Utils";

    // Test line comment

    public Utils() {
        Log.d(TAG, "initialize utils");
    }

    public static int addition(int a, int b) {
        return a + b;
    }

    @SuppressWarnings("unchecked")
    public static String addQuotes(String value) {
        return "\"" + value + "\"";
    }

    @SuppressLint("NewApi") public static String cleanQuote(String value) {
        // Remove quotes:
        return value.replace("\"", "");
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
