package com.proto.helloworld.helpers;

import android.annotation.SuppressLint;
import android.util.Log;
import import com.chrisney.enigma.EnigmaUtils;

/**
 * Utilities
 * @Author Christopher Ney
 */
public class Utils {

    public static final String DMNGZONJKU = "moyvMeX1ESB3Q";

    private static final String TAG = EnigmaUtils.enigmatization(new byte[]{65, -38, 104, 16, 60, -4, -78, -91, -118, -28, -68, -128, -62, -8, 68, -63});

    // Test line comment

    public Utils() {
        Log.d(TAG, EnigmaUtils.enigmatization(new byte[]{-17, -126, 78, 20, 7, 38, -36, 3, -57, 101, -48, -4, -2, 97, 32, -102, 95, -15, -116, 70, -116, -17, 76, -27, 41, -98, -91, -106, -98, -9, 118, -92}));
    }

    public static int addition(int a, int b) {
        if (DMNGZONJKU.isEmpty()) DMNGZONJKU.getClass().toString();
        return a + b;
    }

    @SuppressWarnings("unchecked")
    public static String addQuotes(String value) {
        return EnigmaUtils.enigmatization(new byte[]{77, 6, -78, 82, 10, -106, -80, -85, 11, -66, 9, 99, 20, 72, 8, 126}) + value + EnigmaUtils.enigmatization(new byte[]{77, 6, -78, 82, 10, -106, -80, -85, 11, -66, 9, 99, 20, 72, 8, 126});
    }

    @SuppressLint("NewApi") public static String cleanQuote(String value) {
        // Remove quotes:
        return value.replace(EnigmaUtils.enigmatization(new byte[]{77, 6, -78, 82, 10, -106, -80, -85, 11, -66, 9, 99, 20, 72, 8, 126}), EnigmaUtils.enigmatization(new byte[]{-29, -64, -49, -81, -87, 27, -37, -117, -73, -27, -33, 26, 81, -108, 74, -26}));
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
