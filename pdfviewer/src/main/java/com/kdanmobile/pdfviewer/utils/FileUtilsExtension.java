package com.kdanmobile.pdfviewer.utils;

import android.graphics.Bitmap;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Stack;

/**
 * @classname：FileUtilsExtension
 * @author：luozhipeng
 * @date：23/8/18 16:37
 * @description： 在Apache的FileUtils基础上扩展的类
 */
public class FileUtilsExtension{
    private static long dir_size = 0L;

    /**
     * Return the name of file.
     * @param file The file.
     * @return the name of file; 根据全路径获取文件名
     */
    public static String getFileName(final File file) {
        if (file == null) {
            return null;
        }
        return getFileName(file.getAbsolutePath());
    }

    /**
     * Return the name of file.
     * @param filePath The path of file.
     * @return the name of file; 根据全路径获取文件名
     */
    public static String getFileName(final String filePath) {
        if (isSpace(filePath)) {
            return filePath;
        }
        int lastSep = filePath.lastIndexOf(File.separator);
        return lastSep == -1 ? filePath : filePath.substring(lastSep + 1);
    }

    /**
     * Return the name of file without extension.
     * @param file The file.
     * @return the name of file without extension; 根据全路径获取文件名不带拓展名
     */
    public static String getFileNameNoExtension(final File file) {
        if (file == null) {
            return null;
        }
        return getFileNameNoExtension(file.getPath());
    }

    /**
     * Return the name of file without extension.
     * @param filePath The path of file.
     * @return the name of file without extension; 根据全路径获取文件名不带拓展名
     */
    public static String getFileNameNoExtension(final String filePath) {
        if (isSpace(filePath)) {
            return filePath;
        }
        int lastPoi = filePath.lastIndexOf('.');
        int lastSep = filePath.lastIndexOf(File.separator);
        if (lastSep == -1) {
            return (lastPoi == -1 ? filePath : filePath.substring(0, lastPoi));
        }
        if (lastPoi == -1 || lastSep > lastPoi) {
            return filePath.substring(lastSep + 1);
        }
        return filePath.substring(lastSep + 1, lastPoi);
    }

    /**
     * Return the extension of file.
     * @param file The file.
     * @return the extension of file; 根据全路径获取文件拓展名
     */
    public static String getFileExtension(final File file) {
        if (file == null) {
            return null;
        }
        return getFileExtension(file.getPath());
    }

    /**
     * Return the extension of file.
     * @param filePath The path of file.
     * @return the extension of file ; 根据全路径获取文件拓展名
     */
    public static String getFileExtension(final String filePath) {
        if (isSpace(filePath)) {
            return filePath;
        }
        int lastPoi = filePath.lastIndexOf('.');
        int lastSep = filePath.lastIndexOf(File.separator);
        if (lastPoi == -1 || lastSep >= lastPoi) {
            return "";
        }
        return filePath.substring(lastPoi + 1);
    }

    private static boolean isSpace(final String s) {
        if (s == null) {
            return true;
        }
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }


    /**
     * @methodName：getFileSize created by liujiyuan on 2018/9/7 下午2:37.
     * @description：根据文件路径得到文件大小
     */
    public static Long getFileSize(final String filePath) {
        if (isSpace(filePath)) {
            return 0L;
        }
        Long fileSize = new File(filePath).length();
        return fileSize;
    }

    /**
     * @methodName：saveBitmap created by liujiyuan on 2018/9/11 下午5:46.
     * @description：保存bitmap到预制路径上
     */
    public static String saveSignBitmap(Bitmap mBitmap) {
        String savePath = PathManager.getInstance().getSignPictureFolderPath();
        File filePic;
        try {
            filePic = new File(savePath + File.separator + generateFileName() + ".png");
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

        return filePic.getAbsolutePath();
    }

    /**
     * @methodName：generateFileName created by liujiyuan on 2018/9/11 下午5:46.
     * @description：设置文件名称
     */
    private static String generateFileName() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

    /**
     * @methodName：deleteFile created by liujiyuan on 2018/9/18 下午2:29.
     * @description：根据文件路径删除文件
     */
    public static boolean deleteFile(String filePath) {
        File filePic;
        try{
            filePic = new File(filePath);
            if(filePic.exists() && filePic.isFile()){
                return  filePic.delete();
            }
        }catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
}
