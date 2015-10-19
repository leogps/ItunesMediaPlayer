package com.gps.imp.utils;

/**
 * Created by leogps on 9/15/15.
 */
public class JavaVersionUtils {

    public static final String VERSION_KEY = "java.version";

    public static boolean isGreaterThan6() {
        String javaVersion = System.getProperty(VERSION_KEY);
        return javaVersion != null &&
                !javaVersion.startsWith("1.6")
                &&
                !javaVersion.startsWith("1.5")
                &&
                !javaVersion.startsWith("1.4")
                &&
                !javaVersion.startsWith("1.3")
                &&
                !javaVersion.startsWith("1.2")
                &&
                !javaVersion.startsWith("1.1")
                &&
                !javaVersion.startsWith("1.0");
    }

    public static boolean isFXAvailable() {
        return isGreaterThan6();
    }

}
