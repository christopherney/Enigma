package com.chrisney.enigma.utils;

import java.util.Random;

public class TextUtils {

    public static boolean endsWith(String value, char character) {
        if (value == null) return false;
        String tempValue = value.trim();
        return tempValue.charAt(tempValue.length() - 1) == character;
    }

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

    public static boolean isSpace(String s) {
        return s != null && s.trim().length() == 0;
    }

    public static boolean isReturnChar(Character c) {
        return c.equals('\r') || c.equals('\n') || c.equals(System.lineSeparator().charAt(0));
    }

    public static boolean isEqualsToChar(String s, char c) {
        return s != null && s.charAt(0) == c;
    }
}
