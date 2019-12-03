package com.chrisney.utils;

import com.chrisney.parser.JavaCode;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import static org.gradle.internal.impldep.com.google.common.io.Resources.getResource;

public class Utils {

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

    public static int getRandomNumberInRange(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

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
