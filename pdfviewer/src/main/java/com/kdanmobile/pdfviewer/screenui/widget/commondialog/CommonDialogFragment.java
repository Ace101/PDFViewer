package com.kdanmobile.pdfviewer.screenui.widget.commondialog;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;
import android.view.WindowManager;

import com.kdanmobile.pdfviewer.utils.ReflectionUtils;
import com.orhanobut.logger.Logger;

/**
 * @classname：CommonDialogFragment
 * @author：luozhipeng
 * @date：15/12/17 21:00
 * @description：完美实现PDF阅读底部分享全屏弹出框
 */
public class CommonDialogFragment extends DialogFragment {
    private Activity activity;
    /****** 监听弹出窗是否被取消 ******/
    private OnDialogCancelListener mCancelListener;
    /****** 回调获得需要显示的 dialog ******/
    private OnCallDialog mOnCallDialog;

    public interface OnDialogCancelListener {
        void onCancel();
    }

    public interface OnCallDialog {
        Dialog getDialog(Context context);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    public static CommonDialogFragment newInstance(OnCallDialog callDialog, boolean cancelable) {
        return newInstance(callDialog, cancelable, null);
    }

    public static CommonDialogFragment newInstance(OnCallDialog callDialog, boolean cancelable, OnDialogCancelListener cancelListener) {
        CommonDialogFragment instance = new CommonDialogFragment();
        instance.setCancelable(cancelable);
        instance.mCancelListener = cancelListener;
        instance.mOnCallDialog = callDialog;
        return instance;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (null != mOnCallDialog) {
            return mOnCallDialog.getDialog(activity);
        }
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (null != dialog) {

            /****** 在 5.0 以下的版本会出现白色背景边框，若在 5.0 以上设置则会造成文字部分的背景也变成透明 ******/
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                /****** 目前只有这两个 dialog 会出现边框 ******/
                if (dialog instanceof ProgressDialog || dialog instanceof DatePickerDialog) {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }
            }

            Window window = dialog.getWindow();
            WindowManager.LayoutParams windowParams = window.getAttributes();
            windowParams.dimAmount = 0.4f;
            window.setAttributes(windowParams);
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (mCancelListener != null) {
            mCancelListener.onCancel();
        }
    }

    /**
     * @param ：[manager, tag]
     * @return : void
     * @methodName ：onShow created by luozhipeng on 12/12/17 11:07.
     * @description ：自定义重写了父类的方法show
     */
    public void onShow(FragmentManager manager, String tag) throws Exception {
        try {
            /****** 通过反射修改父类的属性值：mDismissed = false ******/
            Boolean mDismissed = (Boolean) ReflectionUtils.getFieldValue(this, "mDismissed");
            Logger.t(getClass().getSimpleName()).d("mDismissed修改前: " + mDismissed);
            ReflectionUtils.setFieldValue(this, "mDismissed", false);
            mDismissed = (Boolean) ReflectionUtils.getFieldValue(this, "mDismissed");
            Logger.t(getClass().getSimpleName()).d("mDismissed修改后: " + mDismissed);

            /****** 通过反射修改父类的属性值：mShownByMe = true ******/
            Boolean mShownByMe = (Boolean) ReflectionUtils.getFieldValue(this, "mShownByMe");
            Logger.t(getClass().getSimpleName()).d("mShownByMe修改前: " + mShownByMe);
            ReflectionUtils.setFieldValue(this, "mShownByMe", true);
            mShownByMe = (Boolean) ReflectionUtils.getFieldValue(this, "mShownByMe");
            Logger.t(getClass().getSimpleName()).d("mShownByMe修改后: " + mShownByMe);
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commitNowAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setmOnCallDialog(OnCallDialog mOnCallDialog) {
        this.mOnCallDialog = mOnCallDialog;
    }

    public void setmCancelListener(OnDialogCancelListener mCancelListener) {
        this.mCancelListener = mCancelListener;
    }
}
