package com.ppamy.pptips.util;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileUtils {
    /**
     * 读取源文件字符数组
     * 
     * @param File
     *            file 获取字符数组的文件
     * @return 字符数组
     */
    public static byte[] readFileByte(File file) {
        FileInputStream fis = null;
        FileChannel fc = null;
        byte[] data = null;
        try {
            fis = new FileInputStream(file);
            fc = fis.getChannel();
            data = new byte[(int) (fc.size())];
            fc.read(ByteBuffer.wrap(data));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fc != null) {
                try {
                    fc.close();
                } catch (IOException e) {
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                }
            }
        }
        return data;
    }
    
    public static void deleteFile(File file) {
        if (file != null && file.exists()) {
            if (file.isDirectory()) {
                File[] delFiles = file.listFiles();
                if (delFiles != null) {
                    for (File delFile : delFiles) {
                        deleteFile(delFile);
                    }
                }
            }
            file.delete();
        }
    }

    /**
     * 字符数组写入文件
     * 
     * @param byte[] bytes 被写入的字符数组
     * @param File file 被写入的文件
     * @return 字符数组
     */
    public static boolean writeByteFile(byte[] bytes, File file) {
        if (bytes == null) {
            return false;
        }

        boolean flag = false;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(bytes);
            flag = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                } catch (Exception e) {
                }
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
        return flag;
    }

    // copy a file from srcFile to destFile, return true if succeed, return
    // false if fail
    public static boolean copyFile(File srcFile, File destFile) {
        boolean result = false;
        if (srcFile != null && srcFile.exists()) {
            InputStream in = null;
            try {
                in = new FileInputStream(srcFile);
                result = copyToFile(in, destFile);
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (Exception ex) {
                    }
                }
            }
        }
        return result;
    }

    /**
     * Copy data from a source stream to destFile. Return true if succeed,
     * Return true if succeed, return false if failed.
     */
    public static boolean copyToFile(InputStream inputStream, File destFile) {
        boolean result = false;
        OutputStream out = null;
        try {
            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdirs();
            } else if (destFile.exists()) {
                destFile.delete();
            }
            out = new FileOutputStream(destFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) >= 0) {
                out.write(buffer, 0, bytesRead);
            }

            result = true;
        } catch (Exception e) {
            result = false;
        } finally {
            if (out != null) {
                try {
                    out.flush();
                } catch (Exception e) {
                    result = false;
                }
                try {
                    out.close();
                } catch (Exception e) {
                    result = false;
                }
            }
        }
        if (!result) {
            destFile.delete();
        }
        return result;
    }

    public static void deleteFile(String filepath) {
        File file = new File(filepath);
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] delFiles = file.listFiles();
                if (delFiles != null) {
                    for (File delFile : delFiles) {
                        if (delFile != null) {
                            deleteFile(delFile.getAbsolutePath());
                        }
                    }
                }
            }
            file.delete();
        }
    }

    /**
     * 删除目录以及子目录
     * 
     * @param filepath 要删除的目录地址
     * @throws IOException
     */
    public static void deleteDir(String filepath) {
        File f = new File(filepath);
        if (f.exists() && f.isDirectory()) {
            File[] delFiles = f.listFiles();
            if (delFiles != null) {
                if (delFiles.length == 0) {
                    f.delete();
                } else {
                    for (File delFile : delFiles) {
                        if (delFile != null) {
                            if (delFile.isDirectory()) {
                                deleteDir(delFile.getAbsolutePath());
                            }
                            delFile.delete();
                        }
                    }
                    f.delete();
                }
            }
        }
    }

    public static void closeCloseable(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (Exception e) {
            }
        }
    }

}
