package com.ppamy.pptips.util;


import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * Created by zhuangqianliu on 2015/11/23.
 */
public class Logger {

    static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    static public final int LEVEL_ERROR = 5;
    static public final int LEVEL_WARN = 4;
    static public final int LEVEL_INFO = 3;
    static public final int LEVEL_DEBUG = 2;
    static private int level = LEVEL_DEBUG;
    static private String logFilesFolder = "logs/";
    static public boolean enableWriteToFile=false;
    public static final boolean DEBUG = false;

    public static String getLogFilesFolder() {
        return logFilesFolder;
    }

    private static void setLogFilesFolder(String logFilesFolder) {
        File file = new File(logFilesFolder);
        if (!file.exists()) {
            file.mkdirs();
        }
        Logger.logFilesFolder = logFilesFolder;
    }

    static void out(String tag, Object[] args) {
        String s = "";
        for (Object o : args) {
            if (o == null) {
                s += "null ";
            } else {
                s += o.toString() + " ";
            }
        }
        String str = "[" + format.format(new Date()) + "] " + tag + ":" + s;
        System.out.println(str);
        String fpath = Utils.getSDPathBySDKApi()+"/"+logFilesFolder + format.format(new Date()).substring(0, 10) + ".log";
        File temp = new File(fpath);
        if (temp.exists() && temp.length() > 900000000) {
            fpath = logFilesFolder + str + ".log";
        }
        writeToFile(fpath, str + "\n");


    }
    private static String getString(Object[] args){
        String s = "";
        for (Object o : args) {
            if (o == null) {
                s += "null ";
            } else {
                s += o.toString() + " ";
            }
        }
        return s;
    }


    static public void d(String tag, Object... args) {
        if (DEBUG && level > LEVEL_DEBUG) {
            return;
        }
        Log.d(tag, getString(args));
    }
    static public void i(String tag, Object... args) {
        if (DEBUG && level > LEVEL_INFO) {
            return;
        }
        Log.i(tag,getString(args));
    }

    static public void e(String tag, Object... args) {
        if (DEBUG && level > LEVEL_ERROR) {
            return;
        }
        Log.e(tag, getString(args));
    }

    static public void w(String tag, Object... args) {
        if (level > LEVEL_WARN) {
            return;
        }
        out("WARN/" + tag, args);
    }

    static public void config(int logLevel, String logFileDir) {
        level = logLevel;
        if (logFileDir != null) {
            setLogFilesFolder(logFileDir);
        }
    }

    public static void writeToFile(String filePath, String content) {
        if(!enableWriteToFile){
            return;
        }
        try {
            FileWriter writer = new FileWriter(filePath, true);
            writer.write(content);
            writer.close();

        } catch (Exception e) {
            System.out.println("error:" + e);
        }
    }

}