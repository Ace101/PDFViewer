package com.kdanmobile.pdfviewer.utils.threadpools;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

/**
 * @classname：MainRunnable
 * @author：luozhipeng
 * @date：27/7/18 10:55
 * @description：
 */
public class MainPoolUtils {

    private MainPoolUtils() {

    }

    private final static class SingleTon {
        private final static MainPoolUtils instance = new MainPoolUtils();
    }

    public static MainPoolUtils getInstance() {
        return SingleTon.instance;
    }

    public boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    public void onMainRuning(final View view, final Runnable runnable) {
        if (null != view) {
            view.post(runnable);
        }
    }

    public void onMainRuning(final Activity activity, final Runnable runnable) {
        if ((null != activity) && !activity.isFinishing()) {
            activity.runOnUiThread(runnable);
        }
    }

    public void onMainRuning(final Handler handler, final Runnable runnable) {
        if (null != handler) {
            handler.post(runnable);
        }
    }

    public void onMainRuningDelay(final View view, final Runnable runnable, final long delay) {
        if (null != view) {
            view.postDelayed(runnable, delay);
        }
    }

    public void onMainRuningDelay(final Handler handler, final Runnable runnable, final long delay) {
        if (null != handler) {
            handler.postDelayed(runnable, delay);
        }
    }
}
