package com.kdanmobile.pdfviewer.utils.sputils;

/**
 * @classname：SpUtils
 * @author：luozhipeng
 * @date：23/7/18 15:03
 * @description： 本地SP存储
 */
public class SpUtils {
    private final static class SingleTon {
        private final static SpUtils instance = new SpUtils();
    }

    public static SpUtils getInstance() {
        return SingleTon.instance;
    }

    public synchronized void saveUserUUID(String userUUID) {
        String key = String.format("koso_useruuid");
        SharedPreferencesSava.getInstance().savaStringValue(SharedPreferencesSava.DEFAULT_SPNAME, key, userUUID);
    }

    public String onGetUserUUID() {
        String key = String.format("koso_useruuid");
        return SharedPreferencesSava.getInstance().getStringValue(SharedPreferencesSava.DEFAULT_SPNAME, key, "pdf_reader_pro");
    }

    public String onGetUserImgStr() {
        String key = String.format("%s_userImgStr", onGetUserUUID());
        return SharedPreferencesSava.getInstance().getStringValue(SharedPreferencesSava.DEFAULT_SPNAME, key);
    }
}
