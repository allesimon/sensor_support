package com.alsimon.utils;

import android.util.Log;

/**
 * Created by a on 07/07/14.
 */
public class Logg {
    public static void e(Object o, Object o2) {
        Log.e(o.toString(), o2.toString());
    }

    public static void e(Object o) {

        String fullClassName = Thread.currentThread().getStackTrace()[3].getClassName();
        String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
        String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();
        int lineNumber = Thread.currentThread().getStackTrace()[3].getLineNumber();

        Log.e(className + "." + methodName + "():" + lineNumber, o.toString());
    }

    public static void d(Object o, Object o2) {
        Log.d(o.toString(), o2.toString());
    }

    public static void d(Object o) {
        Log.d(o.toString(), o.toString());
    }
}
