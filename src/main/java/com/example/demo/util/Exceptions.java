package com.example.demo.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by WCL on 2018/7/31.
 */
public class Exceptions {
    public Exceptions() {
    }

    public static RuntimeException unchecked(Exception e) {
        return e instanceof RuntimeException?(RuntimeException)e:new RuntimeException(e);
    }

    public static String getStackTraceAsString(Throwable e) {
        if(e == null) {
            return "";
        } else {
            StringWriter stringWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(stringWriter));
            return stringWriter.toString();
        }
    }

    public static boolean isCausedBy(Exception ex, Class... causeExceptionClasses) {
        for(Throwable cause = ex.getCause(); cause != null; cause = cause.getCause()) {
            Class[] var3 = causeExceptionClasses;
            int var4 = causeExceptionClasses.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                Class causeClass = var3[var5];
                if(causeClass.isInstance(cause)) {
                    return true;
                }
            }
        }

        return false;
    }
}
