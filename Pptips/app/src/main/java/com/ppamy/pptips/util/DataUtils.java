
package com.ppamy.pptips.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 数据备份和用户信息存储工具类
 *
 * @author caizhiping
 */
public class DataUtils {
    static final String TAG = "DataUtils";

    public static String CHARSET = "utf-8";
    // for locale backup file name
    public static final int DEFAULT_BUFFER_SIZE = 8192;

    /**
     * 判断是否为空字符串
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0 || "null".equalsIgnoreCase(str.toString());
    }

    /**
     * 判断List是否为空
     *
     * @param list
     * @return
     */
    public static boolean isEmpty(List<?> list) {
        if (list != null && list.size() > 0) {
            return false;
        }
        return true;
    }

    public static boolean isEmpty(Set<?> set) {
        if (set != null && set.size() > 0) {
            return false;
        }
        return true;
    }

    public static boolean isEmpty(Iterable<?> iterable) {
        if (iterable != null && iterable.iterator().hasNext()) {
            return false;
        }
        return true;
    }

    public static byte[] Zip(byte[] input) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream zipOutputSteam = null;
        InputStream inputStream = null;
        try {
            zipOutputSteam = new GZIPOutputStream(baos);
            inputStream = new BufferedInputStream(new ByteArrayInputStream(input), DEFAULT_BUFFER_SIZE);
            int len;
            byte[] buffer = new byte[4096];
            while ((len = inputStream.read(buffer)) != -1) {
                zipOutputSteam.write(buffer, 0, len);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (zipOutputSteam != null) {
                    zipOutputSteam.close();
                }
                baos.close();
            } catch (Exception ex) {

            }
        }
        return baos.toByteArray();
    }

    public static byte[] UnZip(byte[] input) {
        GZIPInputStream zis = null;
        ByteArrayOutputStream baos = null;
        try {
            zis = new GZIPInputStream(new ByteArrayInputStream(input));
            baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int count;
            while ((count = zis.read(buffer)) != -1) {
                baos.write(buffer, 0, count);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (zis != null) {
                    zis.close();
                }
                if (baos != null) {
                    baos.close();
                }
            } catch (Exception ex) {
            }
        }
        byte[] outData = null;
        if (baos != null) {
            outData = baos.toByteArray();
        }
        return outData;
    }

    /**
     * 对关键字符串进行简单的异或加密，避免被反编译后出现大量明文字符串
     *
     * @param content 待加密的字符串
     * @param key 加密密钥
     * @return 加密后的字符串
     */
    public static String xorDecrypt(String content, String key) {
        if (content == null || key == null) {
            return "";
        }
        char[] contents = content.toCharArray();
        char[] keys = key.toCharArray();
        StringBuilder result = new StringBuilder();
        char xorKey = 0;
        char xorResult = 0;
        for (int i = 0, j = 0; i < contents.length; i++) {
            if (j < keys.length - 1) {
                xorKey = keys[j];
                j++;
            } else {
                xorKey = (char) (keys[0] + (char) i);
            }
            xorResult = (char) (contents[i] ^ xorKey);
            result.append(xorResult);
        }
        return result.toString();
    }

    /**
     * 解密
     *
     * @param content
     * @param key
     * @return
     */
    public static char[] xorEncrypt(String content, String key) {
        if (content == null || key == null) {
            return "".toCharArray();
        }
        char[] contents = content.toCharArray();
        char[] keys = key.toCharArray();
        char[] result = new char[contents.length];
        char xorKey = 0;
        for (int i = 0, j = 0; i < contents.length; i++) {
            if (j < keys.length - 1) {
                xorKey = keys[j];
                j++;
            } else {
                xorKey = (char) (keys[0] + (char) i);
            }
            result[i] = (char) (contents[i] ^ xorKey);
        }
        return result;
    }

}
