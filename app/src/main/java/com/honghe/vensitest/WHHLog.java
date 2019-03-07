package com.honghe.vensitest;

import android.util.Log;

/**
 * 可以记录行号,类名,方法名的Log工具
 *
 * @author wanghh
 */
public class WHHLog {
    private static final boolean DEBUG = true;
    private static final String TAG = WHHLog.class.getSimpleName();

    public static void e(String message) {
        if (DEBUG) {
            Log.e(getTag(Thread.currentThread().getStackTrace()), message);
        }
    }

    public static void d(String message) {
        if (DEBUG) {
            Log.d(getTag(Thread.currentThread().getStackTrace()), message);
        }
    }

    public static void i(String message) {
        if (DEBUG) {
            Log.i(getTag(Thread.currentThread().getStackTrace()), message);
        }
    }

    public static void w(String message) {
        if (DEBUG) {
            Log.w(getTag(Thread.currentThread().getStackTrace()), message);
        }
    }

    public static void v(String message) {
        if (DEBUG) {
            Log.v(getTag(Thread.currentThread().getStackTrace()), message);
        }
    }

    private static String getTag(StackTraceElement[] elements) {
        StringBuffer tag = new StringBuffer("wanghh");
        if (elements.length < 4) {
            Log.e(TAG, "Stack to shallow");
        } else {
            String fullClassName = elements[3].getClassName();
            tag.append(elements[3].getLineNumber() +
                    fullClassName.substring(fullClassName.lastIndexOf(".") + 1) + "." +
                    elements[3].getMethodName());
        }
        return tag.toString();
    }
}
