package com.chrisney.enigma.utils;

import com.chrisney.enigma.parser.JavaCode;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import static org.gradle.internal.impldep.com.google.common.io.Resources.getResource;

/**
 * Various helper functions
 */
public class Utils {

    /**
     * Recursive algorithm to list all files in directory
     * @param dir Root directory to scan
     * @param fileType Filter of file type to search (extension)
     * @return All files found
     */
    public static Collection<File> listFileTree(File dir, String fileType) {
        Set<File> fileTree = new HashSet<>();
        if(dir == null || dir.listFiles() == null) {
            return fileTree;
        }
        File[] files = dir.listFiles();
        if (files != null) {
            for (File entry : files) {
                if (entry.isFile() && entry.getName().endsWith(fileType)) {
                    fileTree.add(entry);
                } else {
                    fileTree.addAll(listFileTree(entry, fileType));
                }
            }
        }
        return fileTree;
    }

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
     * Generate a random number between a range (included min and max values)
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
    public static ArrayList<Object> toArrayList(Object[] array) {
        ArrayList<Object> list = new ArrayList<>();
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
        return TextUtils.getRandomString(32, TextUtils.KEY_CHARACTERS);
    }

    /**
     * Check if an item exists in a array
     * @param array Array to analyse
     * @param item Item to search
     * @return True if item exists in the array
     */
    public static boolean arrayContains(Object[] array, Object item) {
        return findIndexInArray(array, item) != -1;
    }

    /**
     * Return the index of the element if it contains by the array, otherwise return -1
     * @param array Array to analyse
     * @param item Item to search
     * @return Index of the element if it contains by the array, otherwise return -1
     */
    public static int findIndexInArray(Object[] array, Object item) {
        for (int i = 0; i < array.length; i++) {
            if (item.equals(array[i])) return i;
        }
        return -1;
    }

    /**
     * Check if an array if not empty (and not null).
     * @param arrayList Array to test
     * @return True if not null and not empty
     */
    public static boolean arrayNotEmpty(ArrayList arrayList) {
        return arrayList != null && arrayList.size() > 0;
    }
}
