package com.chrisney.utils;

import java.util.ArrayList;
import java.util.Random;

public class Utils {

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
}
