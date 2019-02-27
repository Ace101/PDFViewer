package com.kdanmobile.pdfviewer.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupWindow;

public abstract class BasePopupWindow extends PopupWindow implements View.OnClickListener{
    /**
     * @classname：BasePopupWindow
     * @author：liujiyuan
     * @date：2018/8/20 上午10:25
     * @description：所有 PopupWindow 的基类
     */

    private final static String TAG = "BasePopupWindow";
    private InputMethodManager imm;
    protected View mContentView;
    private LayoutInflater mInflater;
    protected Context mContext;

    @SuppressLint("WrongConstant")
    public BasePopupWindow(Context context) {
        super(context);

        this.mContext=context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContentView = setLayout(mInflater);
        if(mContentView == null){
            return;
        }
        /****** 设置View ******/
        setContentView(mContentView);
        /****** 设置宽与高 ******/
        setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        /**
         * 设置进出动画
         */
//        setAnimationStyle(R.style.popwindow_anim_style);
        /**
         * 设置背景只有设置了这个才可以点击外边和BACK消失
         */
        //setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ddffffff")));
//        setBackgroundDrawable(null);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        /**
         * 解决popupwindow被软键盘遮挡的问题
         */
        setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        /**
         * 设置可以获取集点
         */
        setFocusable(false);
        /**
         * 设置点击外边可以消失
         */
        setOutsideTouchable(true);
        /**
         *设置可以触摸
         */
//        setTouchable(true);
        /**
         * 设置点击外部可以消失
         */
        setTouchInterceptor((v, event) -> {

            /****** 判断是不是点击了外部 ******/
            if(event.getAction()== MotionEvent.ACTION_OUTSIDE){
                dismiss();
                return true;
            }
            /****** 不是点击外部 ******/
            return false;
        });

        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);

        /**
         * 初始化View与监听器
         */

        initView();

        initResource();

        initListener();
    }

    protected abstract View setLayout(LayoutInflater inflater);

    protected abstract void initResource();

    protected abstract void initListener();

    protected abstract void initView();

    protected abstract void onClickListener(View view);

    @Override
    public void onClick(View view) {
        onClickListener(view);
    }

    @Override
    public void dismiss() {
        changeWindowAlpha((Activity)mContext, 1.0f);
        super.dismiss();
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
    }

    public void changeWindowAlpha(Activity activity, float alpha){
        /****** 产生背景变暗效果 ******/
        WindowManager.LayoutParams lp = activity.getWindow()
                .getAttributes();
        lp.alpha = alpha;
        if (lp.alpha == 1) {
            /****** 不移除该Flag的话,可能出现黑屏的bug ******/
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        } else {
            /****** 此行代码主要是解决在华为手机上半透明效果无效的bug ******/
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        activity.getWindow().setAttributes(lp);
    }

    protected void showOrHintSoftKeyboard(View view, boolean show) {

        if (show) {
            view.requestFocus();
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        } else {
            view.clearFocus();
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
