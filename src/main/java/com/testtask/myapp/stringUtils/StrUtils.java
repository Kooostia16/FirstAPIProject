package com.testtask.myapp.stringUtils;

public class StrUtils {
    public static String fillMaxStringLengthWith(String s, String c, int maxL) {
        return s.concat(c.repeat(maxL - s.length()));
    }
}
