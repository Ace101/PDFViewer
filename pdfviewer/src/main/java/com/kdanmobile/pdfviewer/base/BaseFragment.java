package com.kdanmobile.pdfviewer.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.View;
import com.kdanmobile.pdfviewer.Inter.IHandlerCallBack;
import com.kdanmobile.pdfviewer.screenui.widget.commondialog.DialogFragmentHelper;
import com.kdanmobile.pdfviewer.utils.backpressedhandler.BackHandlerHelper;
import com.kdanmobile.pdfviewer.utils.backpressedhandler.FragmentBackHandler;
import com.orhanobut.logger.Logger;

import java.lang.ref.SoftReference;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * @classname：BaseFragment
 * @author：luozhipeng
 * @date：23/7/18 12:02
 * @description：
 */
public class BaseFragment extends Fragment implements EasyPermissions.PermissionCallbacks, FragmentBackHandler {
    protected BaseFragmentHandler handler;
    private DialogFragment mDialogFragment = null;
    public Context mContext;
    public Activity mActivity;
    /****** 是否与View建立起映射关系 ******/
    private boolean isInitView = false;
    /****** 是否是第一次加载数据 ******/
    private boolean isFirstLoad = true;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
        this.mActivity = (Activity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.handler = new BaseFragmentHandler(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isInitView = true;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        lazyLoadData();
    }

    /**
     * @param ：[]
     * @return : void
     * @methodName ：lazyLoadData created by wangzhe on 2018/8/23 下午1:48.
     * @description ：create a lazyLoad Method for viewpager with fragment
     */
    private void lazyLoadData() {
        if (getUserVisibleHint() && isInitView && isFirstLoad) {
            Logger.t(getClass().getSimpleName()).d("------加载数据------");
            onLazyInitData();
            isFirstLoad = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.handler.removeCallbacksAndMessages(null);
        this.handler = null;
        this.mContext = null;
        this.mActivity = null;
        isInitView = false;
        isFirstLoad = true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        // (Optional) 检查用户拒绝权限的时候是否选择了“不再提醒”的情况
        // 这里将弹出对话框引导用户去系统设置
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        } else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if ((null != getActivity())) {
                        getActivity().finish();
                    }
                }
            }, 2000);
        }
    }

    public void showProgressDialog(String msg, boolean isCancelable, boolean isdismiss) {
        try {
            if (isAdded()) {
                mDialogFragment = DialogFragmentHelper.showProgress(getChildFragmentManager(), msg, isCancelable);
                if (mDialogFragment.getDialog() != null) {
                    mDialogFragment.getDialog().setCanceledOnTouchOutside(isdismiss);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopProgressDialog() {
        try {
            if (mDialogFragment != null) {
                mDialogFragment.dismissAllowingStateLoss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mDialogFragment = null;
        }
    }

    @Override
    public boolean onBackPressed() {
        return interceptBackPressed()
                || BackHandlerHelper.handleBackPress(this);
    }

    public boolean interceptBackPressed() {
        return false;
    }

    /**
     * @classname：BaseActivity
     * @author：luozhipeng
     * @date：22/11/17 17:23
     * @description：初始化Handler对象,采用静态内部类的实现方式
     */
    public final static class BaseFragmentHandler extends Handler {
        private final SoftReference<BaseFragment> mBaseActivityWeakReference;
        private IHandlerCallBack mHandlerCallBack;

        public BaseFragmentHandler(BaseFragment baseFragment) {
            mBaseActivityWeakReference = new SoftReference<>(baseFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BaseFragment baseFragment = mBaseActivityWeakReference.get();
            if (baseFragment != null && mHandlerCallBack != null) {
                mHandlerCallBack.handleMsg(msg);
            }
        }

        public void setHandlerCallBack(IHandlerCallBack handlerCallBack) {
            mHandlerCallBack = handlerCallBack;
        }
    }

    protected void onLazyInitData() {

    }
}
