package com.jqorz.picbox.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;


public class FileUtil {

    public static final String ROOT_PATH = Environment.getExternalStorageDirectory().toString();
    private static final char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static final String LOG_TAG = "FileUtil";
    private static final String FILENAME_REGIX = "^[^\\/?\"*:<>\\]{1,255}$";

    public static String getFileMD5(String path) {
        File file = new File(path);
        if (!file.isFile()) {
            return "unknow";
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return toHexString(digest.digest());
    }

    public static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[b[i] & 0x0f]);
        }
        return sb.toString();
    }

    /**
     * 转换显示的文件大小到合适大小
     *
     * @param size 字节数
     * @return 文件大小
     */
    public static String getPrintSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;

        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else
            return String.format("%d B", size);
    }


    /**
     * 当SD卡存在或者SD卡不可被移除的时候，就调用getExternalCacheDir()方法来获取缓存路径，
     * 获取到的就是 /sdcard/Android/data/<application package>/cache 这个路径;
     * 否则就调用getCacheDir()方法来获取缓存路径,
     * 获取到的是 /data/data/<application package>/cache 这个路径;
     */
    public static String getDiskCacheDir(Context context) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }

    /**
     * 判断SD卡是否准备完毕
     */
    public static boolean isSDCardReady() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 判断path1是否包含path2
     * if path1 contains path2
     */
    public static boolean containsPath(String path1, String path2) {
        String path = path2;
        while (path != null) {
            if (path.equalsIgnoreCase(path1))
                return true;

            if (path.equals(ROOT_PATH))
                break;
            path = new File(path).getParent();
        }

        return false;
    }

    public static String getSdDirectory() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    /**
     * 通过原路径和文件名构建新路径
     *
     * @param path1    原路径
     * @param fileName 文件名
     * @return 新路径
     */
    public static String makePath(String path1, String fileName) {
        if (path1.endsWith(File.separator))
            return path1 + fileName;

        return path1 + File.separator + fileName;
    }


    /**
     * 从路径得到父路径
     *
     * @param filepath 文件路径
     * @return 文件的父路径
     */
    public static String getParentPathFromFilePath(String filepath) {
        int pos = filepath.lastIndexOf('/');
        if (pos != -1) {
            return filepath.substring(0, pos);
        }
        return "";
    }


    /**
     * 从文件的全名得到文件的名字
     *
     * @param filename 文件的全名
     * @return 文件的名字
     */
    public static String getNameFromFileName(String filename) {
        int dotPosition = filename.lastIndexOf('.');
        if (dotPosition != -1) {
            return filename.substring(0, dotPosition);
        }
        return "";
    }

    /**
     * 创建新文件/文件夹
     *
     * @param path 所在父路径
     * @param name 文件/文件夹名
     * @return 是否成功
     */
    public static boolean createFileOrFolder(String path, String name, boolean isFile) {
        File file = new File(FileUtil.makePath(path, name));
        if (isFile && !file.exists()) {
            try {
                return file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return !isFile && !file.exists() && !file.isDirectory() && file.mkdir();
    }

    /**
     * 重命名文件/文件夹 不修改后缀
     *
     * @param file        原文件
     * @param newFileName 新文件名
     * @return 是否成功
     */
    public static boolean renameFileWithExt(File file, String newFileName) {
        if (newFileName.matches(FILENAME_REGIX)) {
            File newFile;
            if (file.isDirectory()) {
                newFile = new File(file.getParentFile(), newFileName);
            } else {
                String temp = newFileName
                        + file.getName().substring(
                        file.getName().lastIndexOf('.'));
                newFile = new File(file.getParentFile(), temp);
            }
            if (newFile.exists()) {
                newFile.delete();
            }
            if (file.renameTo(newFile)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 重命名文件/文件夹 修改后缀
     *
     * @param file        原文件
     * @param newFileName 新文件名
     * @return 是否成功
     */
    public static boolean renameFile(File file, String newFileName) {
        if (newFileName.matches(FILENAME_REGIX)) {
            File newFile = new File(file.getParentFile(), newFileName);
            if (newFile.exists()) {
                newFile.delete();
            }
            if (file.renameTo(newFile)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 修改文件修改时间
     */
    public static boolean setFileModifiedTime(String filePath, long modifiedTime) {
        return new File(filePath).setLastModified(modifiedTime);
    }

    /**
     * 删除文件/文件夹
     *
     * @param file 路径
     * @return 是否成功
     */
    public static boolean deleteFileOrFolder(File file) {
        if (file.isFile()) {
            return file.delete();
        }

        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                return file.delete();
            }

            for (File childFile : childFiles) {
                deleteFileOrFolder(childFile);
            }
            return file.delete();
        }
        return false;
    }

    /**
     * 向文件写入内容
     *
     * @return 是否成功
     */
    public static boolean writeFile(File file, String result) {
        OutputStreamWriter otw = null;
        try {
            otw = new OutputStreamWriter(new FileOutputStream(file));
            BufferedWriter br = new BufferedWriter(otw);
            br.write(result);
            br.close();
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            if (otw != null)
                try {
                    otw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    /**
     * 复制文件
     *
     * @param src  文件的原路径
     * @param dest 文件新路径
     * @return 新文件所在路径 失败返回null
     */
    public static String copyFile(String src, String dest) {
        File file = new File(src);
        if (!file.exists() || file.isDirectory()) {
            Log.v(LOG_TAG, "copyFile: file not exist or is directory, " + src);
            return null;
        }
        FileInputStream fi = null;
        FileOutputStream fo = null;
        try {
            fi = new FileInputStream(file);
            File destPlace = new File(dest);
            if (!destPlace.exists()) {
                if (!destPlace.mkdirs())
                    return null;
            }

            String destPath = makePath(dest, file.getName());
            File destFile = new File(destPath);
            int i = 1;
            while (destFile.exists()) {
                String destName = getNameFromFileName(file.getName()) + " " + i++ + "."
                        + getExtFromFileName(file.getName());
                destPath = makePath(dest, destName);
                destFile = new File(destPath);
            }

            if (!destFile.createNewFile())
                return null;

            fo = new FileOutputStream(destFile);
            int count = 102400;
            byte[] buffer = new byte[count];
            int read;
            while ((read = fi.read(buffer, 0, count)) != -1) {
                fo.write(buffer, 0, read);
            }


            return destPath;
        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, "copyFile: file not found, " + src);
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(LOG_TAG, "copyFile: " + e.toString());
        } finally {
            try {
                if (fi != null)
                    fi.close();
                if (fo != null)
                    fo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }


    /**
     * 获取文件名字
     *
     * @param filename
     */
    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    /**
     * 从文件路径得到文件名字
     *
     * @param filepath 文件路径
     * @return 文件名字
     */
    public static String getNameFromFilePath(String filepath) {
        int pos = filepath.lastIndexOf('/');
        if (pos != -1) {
            return filepath.substring(pos + 1);
        }
        return "";
    }

    /**
     * 从文件得到文件名字
     *
     * @param file 文件
     * @return 文件名字
     */
    public static String getNameFromFile(File file) {
        String filepath = file.getPath();
        int pos = filepath.lastIndexOf('/');
        if (pos != -1) {
            return filepath.substring(pos + 1);
        }
        return "";
    }

    /**
     * 从文件的全名得到文件的扩展名
     *
     * @param filename 文件的全名
     * @return 文件的扩展名"txt"
     */
    public static String getExtFromFileName(String filename) {
        int dotPosition = filename.lastIndexOf('.');
        if (dotPosition != -1 && filename.length() > dotPosition + 1) {
            return filename.substring(dotPosition + 1, filename.length()).toLowerCase();
        }
        return "";
    }

    /**
     * 以 <b> UTF-8 </b>格式从文件开始处写入字符串,如果文件存在，则会被重写
     *
     * @param path    文件路径
     * @param content 待写入的字符串
     * @return 成功时返回true，失败返回false
     */
    public static boolean writeString(String path, String content) {
        String encoding = "UTF-8";
        File file = new File(path);
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        return writeString(path, content, encoding);
    }

    /**
     * 从文件开始处写入字符串,如果文件存在，则会被重写
     *
     * @param path     文件路径
     * @param content  待写入的字符串
     * @param encoding String转换为byte[]编码
     * @return 成功时返回true，失败返回false
     */
    public static boolean writeString(String path, String content,
                                      String encoding) {
        FileOutputStream fos = null;
        boolean result = false;
        try {
            fos = new FileOutputStream(path);
            byte[] cover = content.getBytes(encoding);
            fos.write(cover, 0, cover.length);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }


    public static boolean isExistPath(String recordH5) {
        File folder = new File(recordH5);
        return folder.exists();
    }


    public static StringBuffer readText(String filePath, String decoder) {
        try {
            File file = new File(filePath);
            if (!file.exists() || !file.canRead())
                return null;

            return readText(filePath, decoder, 0, (int) file.length());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static StringBuffer readText(String filePath, String decoder,
                                        int offset, int length) {
        FileInputStream fileInputStream = null;
        BufferedInputStream buffReader = null;

        try {
            fileInputStream = new FileInputStream(filePath);
            buffReader = new BufferedInputStream(fileInputStream);

            StringBuffer buffer = new StringBuffer();

            byte[] bytesBuf = new byte[length];
            buffReader.skip(offset);
            buffReader.read(bytesBuf, 0, length);

            return buffer.append(new String(bytesBuf, decoder));
        } catch (Exception e) {


        } finally {
            closeCloseable(fileInputStream);
            closeCloseable(buffReader);
        }

        return null;
    }


    private static void closeCloseable(Closeable closeObj) {
        try {
            if (null != closeObj)
                closeObj.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
