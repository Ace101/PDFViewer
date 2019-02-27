package com.kdanmobile.pdfviewer.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.kdanmobile.pdfviewer.BuildConfig;
import com.kdanmobile.pdfviewer.base.ProApplication;

/**
 * @类名:ToastUtil
 * @类描述:吐司管理工具类
 * @作者:luozhipeng
 * @创建时间:2015年7月14日-下午2:23:53
 * @修改人:
 * @修改时间:
 * @修改备注: 1.通过工具类的静态方法就可以直接显示Toast。
 * 2.如果当前有Toast显示，则让当前toast消失，显示正在调用的toast
 * 3.通过建立一个UI线程的Looper绑定的Handler，讲Toast的显示通过该handler来post运行,解决不能在非UI线程中调用的问题
 * @版本:
 * @Copyright:(c)-2015kdanmobile
 */
public class ToastUtil {
    private static Toast mToast;
    private static Handler mHandler;

    private ToastUtil() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static Handler getMainThreadHandler() {
        if (mHandler == null) {
            synchronized (ToastUtil.class) {
                if (mHandler == null) {
                    mHandler = new Handler(Looper.getMainLooper());
                }
            }
        }
        return mHandler;
    }

    public static void showToast(final Context mContext, final String text, final int duration) {
        if (mToast != null) {
            mToast.cancel();
        }

        final Context context = ProApplication.getContext();
        getMainThreadHandler().post(new Runnable() {
            @Override
            public void run() {
                mToast = Toast.makeText(context, text, duration);
                mToast.show();
            }
        });
    }

    public static void showToast(Context mContext, String text) {
        if (mContext != null) {
            showToast(mContext, text, Toast.LENGTH_SHORT);
        }
    }

    public static void showToast(Context mContext, int resourceId) {
        if (mContext != null) {
            showToast(mContext, mContext.getResources().getString(resourceId), Toast.LENGTH_SHORT);
        }
    }

    public static void showLongToast(Context mContext, int resourceId) {
        if (mContext != null) {
            showToast(mContext, mContext.getResources().getString(resourceId), Toast.LENGTH_LONG);
        }
    }

    public static void showToast(Context mContext, int resourceId, int duration) {
        if (mContext != null) {
            showToast(mContext, mContext.getResources().getString(resourceId), duration);
        }
    }

    public static void showDebugToast(Context mContext, String text) {
        showDebugToast(mContext, text, false);
    }

    public static void showDebugToast(Context mContext, String text, boolean isLong) {
        if ((mContext != null) && BuildConfig.LOG_DEBUG) {
            showToast(mContext, text, isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        }
    }
}
