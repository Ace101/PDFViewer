package com.kdanmobile.pdfviewer.base.mvp.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.kdanmobile.pdfviewer.base.BaseActivity;
import com.kdanmobile.pdfviewer.base.mvp.factory.PresenterMvpFactory;
import com.kdanmobile.pdfviewer.base.mvp.factory.PresenterMvpFactoryImpl;
import com.kdanmobile.pdfviewer.base.mvp.presenter.BaseMvpPresenter;
import com.kdanmobile.pdfviewer.base.mvp.proxy.BaseMvpProxy;
import com.kdanmobile.pdfviewer.base.mvp.proxy.PresenterProxyInterface;

/**
 * @classname：AbstractMvpAppCompatActivity
 * @author：luozhipeng
 * @date：19/12/17 13:38
 * @description：
 */
public abstract class AbstractMvpAppCompatActivity<V extends IBaseView, P extends BaseMvpPresenter<V>> extends BaseActivity implements PresenterProxyInterface<V, P> {
    private static final String PRESENTER_SAVE_KEY = "presenter_save_key";
    /****** 创建被代理对象,传入默认Presenter的工厂 ******/
    private BaseMvpProxy<V, P> mProxy = new BaseMvpProxy<>(PresenterMvpFactoryImpl.<V, P>createFactory(getClass()));
    protected Bundle savedInstanceState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        Log.e("perfect-mvp", "V onCreate");
        Log.e("perfect-mvp", "V onCreate mProxy = " + mProxy);
        Log.e("perfect-mvp", "V onCreate this = " + this.hashCode());

        if (onLayoutId() > 0) {
            setContentView(onLayoutId());
        }

        if(onInitView()){
            initPresenter();
        }

    }

    public void initPresenter(){
        mProxy.onAttachMvpView((V) this);
        if (savedInstanceState != null) {
            mProxy.onRestoreInstanceState(savedInstanceState.getBundle(PRESENTER_SAVE_KEY));
        }

        getMvpPresenter().onInit((V) this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("perfect-mvp", "V onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("perfect-mvp", "V onDestroy = " + isChangingConfigurations());
        mProxy.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e("perfect-mvp", "V onSaveInstanceState");
        outState.putBundle(PRESENTER_SAVE_KEY, mProxy.onSaveInstanceState());
    }

    @Override
    public void setPresenterFactory(PresenterMvpFactory<V, P> presenterFactory) {
        Log.e("perfect-mvp", "V setPresenterFactory");
        mProxy.setPresenterFactory(presenterFactory);
    }

    @Override
    public PresenterMvpFactory<V, P> getPresenterFactory() {
        Log.e("perfect-mvp", "V getPresenterFactory");
        return mProxy.getPresenterFactory();
    }

    @Override
    public P getMvpPresenter() {
        Log.e("perfect-mvp", "V getMvpPresenter");
        return mProxy.getMvpPresenter();
    }

    public abstract int onLayoutId();

    public abstract boolean onInitView();
}