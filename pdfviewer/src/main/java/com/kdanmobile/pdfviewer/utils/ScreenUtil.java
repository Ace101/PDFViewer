package com.kdanmobile.pdfviewer.utils;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.orhanobut.logger.Logger;

/**
 * @classname：ScreenUtil
 * @author：luozhipeng
 * @date：5/12/17 13:46
 * @description：屏幕相关工具类
 */
public class ScreenUtil {

    private ScreenUtil() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * @param ：[context]
     * @return : android.util.DisplayMetrics
     * @methodName ：getDisPlayMetrics created by luozhipeng on 5/12/17 13:47.
     * @description ：获取DisplayMetrics
     */
    public static DisplayMetrics getDisPlayMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }

    /**
     * @param ：[context]
     * @return : int
     * @methodName ：getScreenWidth created by luozhipeng on 5/12/17 13:49.
     * @description ：获取屏幕的宽度（像素）
     */
    public static int getScreenWidth(Context context) {
        return getDisPlayMetrics(context).widthPixels;
    }

    /**
     * @param ：[context]
     * @return : int
     * @methodName ：getScreenHeight created by luozhipeng on 5/12/17 13:51.
     * @description ：获取屏幕的高度
     */
    public static int getScreenHeight(Context context) {
        return getDisPlayMetrics(context).heightPixels;
    }

    /**
     * @param ：[context]
     * @return : float
     * @methodName ：getDensity created by luozhipeng on 5/12/17 13:55.
     * @description ：屏幕密度(0.75 / 1.0 / 1.5)
     */
    public static float getDensity(Context context) {
        return getDisPlayMetrics(context).density;
    }

    /**
     * @param ：[context]
     * @return : int
     * @methodName ：getDensityDpi created by luozhipeng on 5/12/17 13:55.
     * @description ：屏幕密度DPI(120 / 160 / 240)
     */
    public static int getDensityDpi(Context context) {
        return getDisPlayMetrics(context).densityDpi;
    }

    /**
     * @param ：[context]
     * @return : int
     * @methodName ：getNavigationHeight created by luozhipeng on 5/12/17 13:56.
     * @description ：导航栏高度
     */
    public static int getNavigationHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            int rid = context.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
            boolean isTrue = context.getResources().getBoolean(rid);
            if (isTrue) {
                return context.getResources().getDimensionPixelSize(resourceId);
            }
        }
        return 0;
    }


    /**
     * @param ：[context]
     * @return : int
     * @methodName ：getStatusBarHeight created by luozhipeng on 5/12/17 14:02.
     * @description ：获取状态栏的高度
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * @param ：[context]
     * @return : int
     * @methodName ：getStatusHeight created by luozhipeng on 5/12/17 14:02.
     * @description ：获取状态栏的高度（反射）
     */
    public static int getStatusHeight(Context context) {
        int statusHeight = -1;
        try {
            Class clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    /**
     * @methodName：getXDp created by luozhipeng on 5/12/17 13:56.
     * @description： 屏幕X轴方向上的长度（dp）
     */
    public static double getXDp(Context context) {
        return getScreenWidth(context) / getDensity(context);
    }

    /**
     * @methodName：getYDp created by luozhipeng on 5/12/17 13:57.
     * @description：屏幕Y轴方向上长度（sp）
     */
    public static double getYDp(Context context) {
        return (getScreenHeight(context) + getNavigationHeight(context)) / getDensity(context);
    }

    /**
     * @param ：[context, dpValue]
     * @return : int
     * @methodName ：dip2px created by luozhipeng on 5/12/17 13:57.
     * @description ：根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    @SuppressWarnings("UnnecessaryFinalOnLocalVariableOrParameter")
    public static int dip2px(Context context, float dpValue) {
        float scale = getDisPlayMetrics(context).density;
        return (int) ((dpValue * scale) + 0.5f);
    }

    /**
     * @param ：[context, pxValue]
     * @return : int
     * @methodName ：px2dip created by luozhipeng on 5/12/17 14:00.
     * @description ：根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    @SuppressWarnings("UnnecessaryFinalOnLocalVariableOrParameter")
    public static int px2dip(Context context, float pxValue) {
        float scale = getDisPlayMetrics(context).density;
        return (int) ((pxValue / scale) + 0.5f);
    }

    /**
     * @methodName：getMinWidthOrHeight created by luozhipeng on 5/12/17 14:04.
     * @description：屏幕最小的值
     */
    public static int getMinWidthOrHeight(Context context) {
        /****** 屏幕的高度 ******/
        int height = getScreenHeight(context);
        /****** 屏幕的宽度 ******/
        int width = getScreenWidth(context);
        return Math.min(width, height);
    }

    /**
     * @methodName：getMaxWidthOrHeight created by luozhipeng on 5/12/17 14:04.
     * @description：屏幕最大的值
     */
    public static int getMaxWidthOrHeight(Context context) {
        /****** 屏幕的高度 ******/
        int height = getScreenHeight(context);
        /****** 屏幕的宽度 ******/
        int width = getScreenWidth(context);
        return Math.max(width, height);
    }

    /**
     * @param ：[context]
     * @return : boolean true:平板,false:手机
     * @methodName ：isPadDevices created by luozhipeng on 5/12/17 14:05.
     * @description ：判断是否平板设备(官方给出的)
     */
    public static boolean isPadDevices(Context context) {
        boolean isPad = (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
        Logger.t(ScreenUtil.class.getSimpleName()).i("尺寸: " + getScreenDimension(context) + "英寸，属于" + (isPad ? "平板设备" : "手机设备"));
        return isPad;
    }

    /**
     * @param ：[context]
     * @return : double
     * @methodName ：getScreenDimension created by luozhipeng on 5/12/17 14:05.
     * @description ：获取屏幕的尺寸（英寸）
     */
    public static double getScreenDimension(Context context) {
        // 得到屏幕的宽(像素)
        int screenX = getScreenWidth(context);
        // 得到屏幕的高(像素)
        int screenY = getScreenHeight(context);
        // 每英寸的像素点
        int dpi = getDensityDpi(context);
        // 得到屏幕的宽(英寸)
        float a = screenX / dpi;
        // 得到屏幕的高(英寸)
        float b = screenY / dpi;
        // 勾股定理
        double screenIn = Math.sqrt((a * a) + (b * b));
        Logger.t(ScreenUtil.class.getSimpleName()).i("设备尺寸大小: " + screenIn + "英寸");
        return screenIn;
    }

    /**
     * @param ：[activity]
     * @return : void
     * @methodName ：setFullScreen created by luozhipeng on 15/12/17 21:40.
     * @description ：设置屏幕为全屏
     */
    public static void setFullScreen(@NonNull final Activity activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * @param ：[activity, isLock]
     * @return : void
     * @methodName ：lockScreenOrientation created by luozhipeng on 14/11/17 16:25.
     * @description ：屏幕锁定
     */
    public static void lockScreenOrientation(Activity activity, boolean isLock) {
        if (isLock) {
            /****** 锁定 ******/
            Display display = activity.getWindowManager().getDefaultDisplay();
            int rotation = display.getRotation();
            switch (rotation) {
                case Surface.ROTATION_0:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    break;
                case Surface.ROTATION_90:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    break;
                case Surface.ROTATION_180:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                    break;
                case Surface.ROTATION_270:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                    break;
                default:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        } else {
            /****** 根据手机陀螺仪来设定方向 ******/
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            /****** 跟随系统的方向设定 ******/
            //activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    /**
     * @param ：[activity]
     * @return : android.graphics.Bitmap
     * @methodName ：screenShot created by luozhipeng on 15/12/17 21:44.
     * @description ：截屏，保留状态栏
     */
    public static Bitmap screenShot(@NonNull final Activity activity) {
        return screenShot(activity, false);
    }

    /**
     * @param ：[activity, isDeleteStatusBar]
     * @return : android.graphics.Bitmap
     * @methodName ：screenShot created by luozhipeng on 15/12/17 21:44.
     * @description ：截屏
     */
    public static Bitmap screenShot(@NonNull final Activity activity, boolean isDeleteStatusBar) {
        View decorView = activity.getWindow().getDecorView();
        decorView.setDrawingCacheEnabled(true);
        decorView.buildDrawingCache();
        Bitmap bmp = decorView.getDrawingCache();
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        Bitmap ret;
        if (isDeleteStatusBar) {
            Resources resources = activity.getResources();
            int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
            int statusBarHeight = resources.getDimensionPixelSize(resourceId);
            ret = Bitmap.createBitmap(bmp, 0, statusBarHeight, dm.widthPixels, dm.heightPixels - statusBarHeight);
        } else {
            ret = Bitmap.createBitmap(bmp, 0, 0, dm.widthPixels, dm.heightPixels);
        }
        decorView.destroyDrawingCache();
        return ret;
    }

    /**
     * @param ：[context]
     * @return : boolean
     * @methodName ：isScreenLock created by luozhipeng on 15/12/17 21:46.
     * @description ：判断是否锁屏
     */
    public static boolean isScreenLock(Context context) {
        KeyguardManager km = (KeyguardManager) context.getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
        return km.inKeyguardRestrictedInputMode();
    }

    /**
     * @param ：[context, duration]
     * @return : void
     * @methodName ：setSleepDuration created by luozhipeng on 15/12/17 21:46.
     * @description ：设置进入休眠时长;需添加权限 {@code <uses-permission android:name="android.permission.WRITE_SETTINGS"
     */
    public static void setSleepDuration(Context context, final int duration) {
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, duration);
    }

    public static void measureWidthAndHeight(View view) {
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthMeasureSpec, heightMeasureSpec);
    }
}
