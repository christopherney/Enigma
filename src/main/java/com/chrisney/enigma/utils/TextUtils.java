package com.chrisney.enigma.utils;

import java.util.Random;

public class TextUtils {

    public static boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }

    public static String getRandomString(int sizeOfRandomString, String chars) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(sizeOfRandomString);
        for (int i = 0; i < sizeOfRandomString; ++i) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public static boolean isEmptyChar(Character c) {
        return  c.equals(' ') || c.equals('\r') || c.equals('\n') || c.equals('\t');
    }

    public static boolean inCharactersList(char[] list, char character) {
        for (char c : list) {
            if ( c == character) return true;
        }
        return false;
    }

    public static boolean isReturnChar(Character c) {
        return c.equals('\r') || c.equals('\n') || c.equals(System.lineSeparator().charAt(0));
    }
}
