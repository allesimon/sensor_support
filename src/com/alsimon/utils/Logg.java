package com.alsimon.utils;

import android.util.Log;

/**
 * Created by a on 07/07/14.
 */
public class Logg {
    private static long timestamp;

    public static void e(Object o, Object o2) {
        Log.e(o.toString(), o2.toString());
    }

    public static void e(Object o) {
        Log.e(getLineCalled(), o.toString());
    }

    public static void e() {
        Log.e(getLineCalled(), "");
    }

    public static void ping() {
        timestamp = System.currentTimeMillis();
    }

    public static void pong() {
        Log.e(getLineCalled(), "Time since last ping " + (System.currentTimeMillis() - timestamp));
    }


    private static String getLineCalled() {
        String fullClassName = Thread.currentThread().getStackTrace()[4].getClassName();
        String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
        String methodName = Thread.currentThread().getStackTrace()[4].getMethodName();
        int lineNumber = Thread.currentThread().getStackTrace()[4].getLineNumber();
        return className + "." + methodName + "():" + lineNumber;
    }

    public static void d(Object o, Object o2) {
        Log.d(o.toString(), o2.toString());
    }

    public static void d(Object o) {
        Log.d(o.toString(), o.toString());
    }
}
