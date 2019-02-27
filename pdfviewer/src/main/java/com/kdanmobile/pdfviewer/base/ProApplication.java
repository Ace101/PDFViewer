package com.kdanmobile.pdfviewer.base;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.ndk.CrashlyticsNdk;
import com.kdanmobile.pdfviewer.BuildConfig;
import com.kdanmobile.pdfviewer.R;
import com.kdanmobile.pdfviewer.utils.GlobalConfigs;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import io.fabric.sdk.android.Fabric;


/**
 * @classname：PDFReaderProApplication
 * @author：luozhipeng
 * @date：23/7/18 11:10
 * @description：
 */
public class ProApplication extends Application {
    private static Context context;
    private volatile boolean isBackgroundRunning = false;

    public static Context getContext() {
        return context;
    }

    public boolean isBackgroundRunning() {
        return isBackgroundRunning;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics(), new CrashlyticsNdk());
        context = getApplicationContext();

        /****** 初始化全局变量 ******/
        initConfigs();
        /****** activity的生命周期监听接口 ******/
        onMonitorSwitchState();

        /****** 第三方初始化处理 ******/
        onInitLogger();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initConfigs() {
        int themeColor = ContextCompat.getColor(this, R.color.theme_color_cyan);

        GlobalConfigs globalConfigs = new GlobalConfigs(this);
        globalConfigs.initGlobalConfigs();
    }

    private void onMonitorSwitchState() {
        ActivityLifecycleManager.init(this).setOnTaskSwitchListener(new ActivityLifecycleManager.OnTaskSwitchListener() {
            @Override
            public void onTaskSwitchToForeground() {
                isBackgroundRunning = false;
            }

            @Override
            public void onTaskSwitchToBackground() {
                isBackgroundRunning = true;
            }
        });
    }

    private void onInitLogger() {
        /******
         * 初始化日志 Use LogLevel.NONE for the release versions.
         * 第一行 default PRETTYLOGGER or use just init()
         * 第三行 default shown
         * 第四行 default LogLevel.FULL
         * 第五行 default 0
         * 第六行 default AndroidLogAdapter
         * ******/
        Logger.init()
                .methodCount(0)
                .hideThreadInfo()
                .logLevel(BuildConfig.LOG_DEBUG ? LogLevel.FULL : LogLevel.NONE)
                .methodOffset(0)
                .logAdapter(null);
    }

    @Override
    public void onTerminate() {
        ActivityLifecycleManager.unregister(this);
        super.onTerminate();
    }
}
