package com.ppamy.pptips.util;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Display;
import android.view.WindowManager;

import com.ppamy.pptips.MainApplication;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class Utils {
    private static int screenWidth = 0;
    private static int screenHeight = 0;
    
    
    public static boolean isEmpty(CharSequence str) {
        if (str == null || str.length() == 0) {
            return true;
        } else {
            return false;
        }
    }
    
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * 取得第一sd卡路径
     **/
    public static String getSDPathBySDKApi() {
        File sdDir = null;
        String state = Environment.getExternalStorageState();
        if (android.os.Environment.MEDIA_MOUNTED.equals(state)) {// 判断sd卡是否存在
            sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
        }
        String path = "";
        if (sdDir != null) {
            path = sdDir.getAbsolutePath();
        }
        return path;
    }

    public static int getScreenHeight(Context c) {
        if (screenHeight == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenHeight = size.y;
        }

        return screenHeight;
    }

    public static int getScreenWidth(Context c) {
        if (screenWidth == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenWidth = size.x;
        }

        return screenWidth;
    }
    /**
     * 传入一个时间获取今天的起始时间s
     * */
    public static long getDateStartTime(long time){
        Calendar currentDate = new GregorianCalendar();
        currentDate.setTimeInMillis(time);
        currentDate.set(Calendar.HOUR_OF_DAY, 0);
        currentDate.set(Calendar.MINUTE, 0);
        currentDate.set(Calendar.SECOND, 0);
        return currentDate.getTimeInMillis();
    }
    /**
     * 获取n天前的起始时间
     * */
    public static long getDateStartTimeBeforeNday(long time,int n){
        Calendar currentDate = new GregorianCalendar();
        currentDate.setTimeInMillis(time);
        currentDate.set(Calendar.HOUR_OF_DAY, 0);
        currentDate.set(Calendar.MINUTE, 0);
        currentDate.set(Calendar.SECOND, 0);
        long todayTime = currentDate.getTimeInMillis();
        long timeBeforeNday = todayTime - (n*86400000);
        return timeBeforeNday;
    }

    public static String getFormatedDate(Context c, long time) {
        final Date date = new Date(time);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
        StringBuffer buffer = new StringBuffer();
        buffer.append(sdf.format(date));
        return buffer.toString();
    }

    public static String getFormatedTime(Context c, long time) {
        final Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
        StringBuffer buffer = new StringBuffer();
        buffer.append(sdf.format(date));
        return buffer.toString();
    }
    
    /** 封装这个方法是因为保证使用Application context来调用getSystemService，否则会内存泄漏 */
    public static Object getSystemService(String name) {
        return MainApplication.getApp().getSystemService(name);
    }

    public static boolean isAndroid5() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }
    /**
     * 拷贝文件
     *
     * @param src
     * @param des
     * @return true if success
     */
    public static boolean copyFile(String src, String dst) {
        boolean ret = false;
        try {
            File srcfile = new File(src);
            File dstfile = new File(dst);
            if ((srcfile.exists()) && srcfile.isFile()) {
                if ((dstfile.exists() && dstfile.isFile())) {
                    dstfile.delete();
                }

                FileInputStream fis = null;
                FileOutputStream fos = null;
                try {
                    fis = new FileInputStream(srcfile);
                    fos = new FileOutputStream(dstfile);
                    byte[] buffer = new byte[4096];
                    int count = 0;

                    while ((count = fis.read(buffer)) != -1) {
                        fos.write(buffer, 0, count);
                    }
                    ret = true;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    secureClose(fis);
                    secureClose(fos);
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return ret;
    }
    private static void secureClose(Closeable s) {
        try {
            if (null != s) {
                s.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }    
    
    private static ActivityManager mActivityManager;

    public static ActivityManager getActivityManager(Context c) {

        if (mActivityManager == null) {
            Object object = c.getSystemService(Context.ACTIVITY_SERVICE);
            if (object != null && object instanceof ActivityManager) {
                mActivityManager = (ActivityManager) object;
            }
        }

        return mActivityManager;
    }
    
    /**
     * 获取top的activity
     * */
    public static ComponentName getTopComponentName(Context c) {
        ActivityManager am = getActivityManager(c);
        if (am != null) {
            List<RunningTaskInfo> runningTasks;
            try {
                runningTasks = am.getRunningTasks(1);
            } catch (Exception e) {
                runningTasks = null;
            }
            if (runningTasks != null && runningTasks.size() > 0) {
                RunningTaskInfo rti = runningTasks.get(0);
                return rti.topActivity;
            }
        }
        return null;
    }

    /**
     * 判断跳转的itent是否可用
     *
     * @param context
     * @param intent
     * @return
     */
    public static boolean isIntentAvailable(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.GET_ACTIVITIES);
        return list.size() > 0;
    }

    /**
     * 获取颜色值
     * */

    public static int getColor(Context c,int rid){
        if(Build.VERSION.SDK_INT >= 23){
            return getColor23(c,rid);
        }else{
            return c.getResources().getColor(rid);
        }
    }

    @TargetApi(23)
    private static int getColor23(Context c,int rid){
        return c.getColor(rid);
    }

    private static final String DEFAULT_IMEI = "360_DEFAULT_IMEI";
    public static String getIMEI(Context c) {
        String imei = null;
        if (TextUtils.isEmpty(imei)) {
            TelephonyManager telephonyManager = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
            imei = telephonyManager.getDeviceId();
            if (!TextUtils.isEmpty(imei)) {
//                SharedPrefFreeCall.setPhoneImei(imei);
            }
        }
        if (TextUtils.isEmpty(imei)) {
            imei = DEFAULT_IMEI;
        }
        return imei;
    }

    public static String getMD5(byte[] input) {
        return bytesToHexString(MD5(input));
    }

    public static String getMD5(String input) {
        return getMD5(input.getBytes());
    }
    public static byte[] MD5(byte[] input) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (md != null) {
            md.update(input);
            return md.digest();
        } else {
            return null;
        }
    }
    public static String bytesToHexString(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        String table = "0123456789abcdef";
        int len = 2 * bytes.length;
        char[] cchars = new char[len];// 不再使用StringBuilder,使用char数组优化速度，dmtrace
        // 发现string.append最耗时
        for (int i = 0, k = 0; i < bytes.length; i++, k++) {
            int b;
            b = 0x0f & (bytes[i] >> 4);
            cchars[k] = table.charAt(b);
            b = 0x0f & bytes[i];
            k++;
            cchars[k] = table.charAt(b);
        }
        String sret = String.valueOf(cchars);
        cchars = null;
        return sret;
    }
}
