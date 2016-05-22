
package com.ppamy.pptips.util;

import android.content.Context;
import android.text.TextUtils;

import java.lang.reflect.Method;

public class PhoneNumberInfoUtils {
    private static String m2;

    /**
     * city code 应该是非空，以0开头，3位或者4位的纯数字哟
     * 
     * @param cityCode
     * @return
     */
    private static boolean validateCityCode(String cityCode) {
        if (cityCode != null && cityCode.startsWith("0") && (cityCode.length() == 3 || cityCode.length() == 4)) {
            for (char c : cityCode.toCharArray()) {
                if (!Character.isDigit(c)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    // 助手使用了两个标识，m和m2：
    // M的计算方式如下：
    // TelephonyManager tm = (TelephonyManager)
    // context.getSystemService(Context.TELEPHONY_SERVICE);
    // m = Md5Util.md5LowerCase(tm.getDeviceId());
    //
    // m2的计算方式如下：
    public static String getMid2(Context context) {
        if (!TextUtils.isEmpty(m2))
            return m2;
        
        String imei = Utils.getIMEI(context);
        String AndroidID = android.provider.Settings.System.getString(context.getContentResolver(), "android_id");
        String serialNo = getDeviceSerialForMid2();
        String _m2 = Utils.getMD5("" + imei + AndroidID + serialNo);
        m2 = _m2;
        return _m2;
    }

    private static String getDeviceSerialForMid2() {
        String serial = "";
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
        } catch (Exception ignored) {
        }
        return serial;
    }

    public static byte[] hexStringToBytes(String s) {
        byte[] ret;

        if (s == null) {
            return null;
        }

        int sz = s.length();
        ret = new byte[sz / 2];
        for (int i = 0; i < sz; i += 2) {
            ret[i / 2] = (byte) ((hexCharToInt(s.charAt(i)) << 4) | hexCharToInt(s.charAt(i + 1)));
        }

        return ret;
    }
    public static int hexCharToInt(char c) {
        if (c >= '0' && c <= '9') {
            return (c - '0');
        }
        if (c >= 'A' && c <= 'F') {
            return (c - 'A' + 10);
        }
        if (c >= 'a' && c <= 'f') {
            return (c - 'a' + 10);
        }

        throw new RuntimeException("invalid hex char '" + c + "'");
    }

    /**
     * 将int转换为byte数组，byte数组的低位是整型的高字节位
     *
     * @param target
     * @return
     */
    public static byte[] int2ByteArray(int target) {
        byte[] array = new byte[4];
        for (int i = 0; i < 4; i++) {
            int offSet = array.length - i - 1;
            array[i] = (byte) (target >> 8 * offSet & 0xFF);
        }
        return array;
    }
}
