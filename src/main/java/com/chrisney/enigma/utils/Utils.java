package com.chrisney.enigma.utils;

import com.chrisney.enigma.parser.JavaCode;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import static org.gradle.internal.impldep.com.google.common.io.Resources.getResource;

/**
 * Various helper functions
 */
public class Utils {

    /**
     * Return the file path of a resource
     * @param fileName Name of the resource
     * @return File path of the resource if exists, otherwise return null
     */
    public static File getFileResource(String fileName) {
        File file = null;
        try {
            URL src = getResource(fileName);
            file = new File(src.toURI());
            if (!file.exists()) return null;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * Generate a random number between a range
     * @param min Min value
     * @param max Max value
     * @return Random value
     */
    public static int getRandomNumberInRange(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    /**
     * Insert an Object into an ArrayList at an specific index (without erase the existing one)
     * @param arrayList ArrayList to edit
     * @param index Index where insert the new object
     * @param value Object to insert
     */
    public static void insertInArray(ArrayList arrayList, int index, Object value) {
        for (int i = arrayList.size() - 1; i >= 0; i--) {
            Object v =  arrayList.get(i);
            if (i < arrayList.size() - 1) {
                arrayList.set(i + 1, v);
            } else {
                arrayList.add(v);
            }
            if (i == index) break;
        }
        arrayList.set(index, value);
    }

    /**
     * Convert a simple array to an ArrayList
     * @param array Simple Array
     * @return ArrayList
     */
    public static ArrayList toArrayList(Object[] array) {
        ArrayList list = new ArrayList();
        for (Object o : array) {
            list.add(o);
        }
        return list;
    }

    /**
     * Generate random hash key (32 characters)
     * @return Hash key
     */
    public static String randomHashKey() {
        return TextUtils.getRandomString(32, JavaCode.KEY_CHARACTERS);
    }
}