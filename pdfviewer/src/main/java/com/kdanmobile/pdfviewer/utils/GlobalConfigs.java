package com.kdanmobile.pdfviewer.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;

import com.kdanmobile.pdfviewer.R;
import com.kdanmobile.pdfviewer.screenui.reader.configs.KMReaderConfigs;

/**
 * @classname：GlobalConfigs
 * @author：luozhipeng
 * @date：23/7/18 10:08
 * @description： 全局参数配置
 */
public class GlobalConfigs {

    private final static String GLOBAL_CONFIG_PREFS_KEY = "[[pdf_reader_pro_global]]";

    private final int KEY_THEME_COLOR_INDEX_DEFAULT = 5;

    private final static String KEY_THEME_COLOR_INDEX = "theme_color_index";
    private final static String KEY_THEME_COLOR = "theme_color";

    private Context mContext;
    private static SharedPreferences.Editor mEditor;
    public static int THEME_COLOR = -1;
    public static int THEME_COLOR_INDEX = -1;

    public static String FILE_ABSOLUTE_PATH;
    public static String CURRENT_DOCUMENT_NAME;

    @NonNull
    public static SharedPreferences prefs(@NonNull Context context) {
        return context.getSharedPreferences(GLOBAL_CONFIG_PREFS_KEY, Context.MODE_PRIVATE);
    }

    @SuppressLint("CommitPrefEdits")
    public GlobalConfigs(@NonNull Context context) {
        mContext = context;
        mEditor = prefs(context).edit();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void initGlobalConfigs(){
        THEME_COLOR = getThemeColor(mContext);
        THEME_COLOR_INDEX = getThemeColorIndex(mContext);

        /****** 设置reader界面的全局变量 ******/
        KMReaderConfigs.initReaderConfigs();
    }

    public static void commit() {
        mEditor.commit();
    }

    /**
     * @param ：[colorIdex]
     * @return : null
     * @methodName ：setThemeColorIndex created by liujiyuan on 2018/8/9 上午9:07.
     * @description ：设置定义的主题色序号
     */
    public static void setThemeColorIndex(int colorIndex) {
        mEditor.putInt(KEY_THEME_COLOR_INDEX, colorIndex);
        commit();
    }


    /**
     * @param ：[color]
     * @return : void
     * @methodName ：setThemeColor created by liujiyuan on 2018/8/9 下午3:19.
     * @description ：设置定义的主题色
     */
    public static void setThemeColor(int color) {
        mEditor.putInt(KEY_THEME_COLOR, color);
        commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private int getThemeColorIndex(@NonNull Context context){
        return prefs(context).getInt(KEY_THEME_COLOR_INDEX, KEY_THEME_COLOR_INDEX_DEFAULT);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private int getThemeColor(@NonNull Context context){
        return prefs(context).getInt(KEY_THEME_COLOR, ContextCompat.getColor(context, R.color.theme_color_blue));
    }

}
