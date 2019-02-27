package com.kdanmobile.pdfviewer.utils;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;


import com.kdanmobile.pdfviewer.base.ProApplication;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @classname：PathManager
 * @author：luozhipeng
 * @date：17/8/18 10:35
 * @description： 项目路径、缓存、文件存放等 统一管理类
 * isSDCardEnableByEnvironment: 根据 Environment 判断 SD 卡是否可用
 * getSDCardPathByEnvironment : 根据 Environment 获取 SD 卡路径
 * isSDCardEnable             : 判断 SD 卡是否可用
 * getSDCardPaths             : 获取 SD 卡路径
 */
public class PathManager {

    private final String APPSTORAGENAME = "PDFReaderPro";
    public static final String SCAN_CACHE = "pdfproscan";
    public static final String SCAN_BACKUP_SUFFIX = ".bak";

    private final static class SingleTon {
        private final static PathManager instance = new PathManager();
    }

    public static PathManager getInstance() {
        return SingleTon.instance;
    }

    /**
     * @methodName：getExternalCacheDir created by luozhipeng on 23/8/18 17:15.
     * @description： 外部缓存
     */
    public File getExternalCacheDir() {
        return ProApplication.getContext().getExternalCacheDir();
    }

    /**
     * @methodName：getInternalCacheDir created by luozhipeng on 23/8/18 17:16.
     * @description： 内部缓存
     */
    public File getInternalCacheDir() {
        return ProApplication.getContext().getCacheDir();
    }

    /**
     * @methodName：isSDCardEnableByEnvironment created by luozhipeng on 17/8/18 10:46.
     * @description： 根据 Environment 判断 SD 卡是否可用
     */
    public boolean isSDCardEnableByEnvironment() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * @methodName：getSDCardPathByEnvironment created by luozhipeng on 17/8/18 10:46.
     * @description： 根据 Environment 获取 SD 卡路径
     */
    public String getSDCardPathByEnvironment() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return "";
    }

    /**
     * @methodName：isSDCardEnable created by luozhipeng on 17/8/18 10:47.
     * @description： 判断 SD 卡是否可用
     */
    public boolean isSDCardEnable() {
        return !getSDCardPaths().isEmpty();
    }

    /**
     * @methodName：getDownloadFolderPath created by liujiyuan on 2018/8/30 下午2:15.
     * @description：获取本机文件下载到文件夹所在路径
     */
    public String getSplitFolderPath() {
        return getSDCardPathByEnvironment() + File.separator + APPSTORAGENAME + File.separator + "splitPdfFile";
    }

    /**
     * @methodName：getPhotoFolderPath created by liujiyuan on 2018/9/11 下午5:13.
     * @description：设置相机拍摄图片的路径
     */
    public String getPhotoFolderPath() {
        return getSDCardPathByEnvironment() + File.separator + APPSTORAGENAME + File.separator + "photo";
    }

    /**
     * @methodName：getSignPicturePath created by liujiyuan on 2018/9/11 下午5:14.
     * @description：设置保存签名图片的路径
     */
    public String getSignPictureFolderPath() {
        return getSDCardPathByEnvironment() + File.separator + APPSTORAGENAME + File.separator + "SignPicture";
    }

    /**
     * @param ：[removable] True to return the paths of removable sdcard, false otherwise.
     * @return : java.util.List<java.lang.String> the paths of sdcard
     * @methodName ：getSDCardPaths created by luozhipeng on 17/8/18 10:47.
     * @description ：获取 SD 卡路径
     */
    public List<String> getSDCardPaths(final boolean removable) {
        List<String> paths = new ArrayList<>();
        StorageManager sm =
                (StorageManager) ProApplication.getContext().getSystemService(Context.STORAGE_SERVICE);
        try {
            Class<?> storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = StorageManager.class.getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(sm);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                boolean res = (Boolean) isRemovable.invoke(storageVolumeElement);
                if (removable == res) {
                    paths.add(path);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return paths;
    }

    /**
     * @methodName：getSDCardPaths created by luozhipeng on 17/8/18 10:48.
     * @description： Return the paths of sdcard.
     */
    public List<String> getSDCardPaths() {
        StorageManager storageManager = (StorageManager) ProApplication.getContext()
                .getSystemService(Context.STORAGE_SERVICE);
        List<String> paths = new ArrayList<>();
        try {
            Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths");
            getVolumePathsMethod.setAccessible(true);
            Object invoke = getVolumePathsMethod.invoke(storageManager);
            paths = Arrays.asList((String[]) invoke);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return paths;
    }

    /**
     * @methodName：getCatchPhotoPath created by wangzhe on 2018/9/11 下午2:29.
     * @description：扫描的缓存图片文件夹
     */
    public File getScanPhotoPath() {
        if (isSDCardEnableByEnvironment()) {
            return new File(ProApplication.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), SCAN_CACHE);
        } else {
            return new File(ProApplication.getContext().getFilesDir(), SCAN_CACHE);
        }
    }

    public File getScanFilePath(){
        if (isSDCardEnableByEnvironment()) {
            return new File(ProApplication.getContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), SCAN_CACHE);
        } else {
            return new File(ProApplication.getContext().getFilesDir(), SCAN_CACHE);
        }
    }
}
