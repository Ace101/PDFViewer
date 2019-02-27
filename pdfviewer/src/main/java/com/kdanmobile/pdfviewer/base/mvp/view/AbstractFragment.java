package com.kdanmobile.pdfviewer.base.mvp.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kdanmobile.pdfviewer.base.BaseFragment;
import com.kdanmobile.pdfviewer.base.mvp.factory.PresenterMvpFactory;
import com.kdanmobile.pdfviewer.base.mvp.factory.PresenterMvpFactoryImpl;
import com.kdanmobile.pdfviewer.base.mvp.presenter.BaseMvpPresenter;
import com.kdanmobile.pdfviewer.base.mvp.proxy.BaseMvpProxy;
import com.kdanmobile.pdfviewer.base.mvp.proxy.PresenterProxyInterface;


/**
 * @classname：AbstractFragment
 * @author：luozhipeng
 * @date：19/12/17 13:44
 * @description：继承Fragment的MvpFragment基类
 */
public abstract class AbstractFragment<V extends IBaseView, P extends BaseMvpPresenter<V>> extends BaseFragment implements PresenterProxyInterface<V, P> {
    /****** 调用onSaveInstanceState时存入Bundle的key ******/
    private static final String PRESENTER_SAVE_KEY = "presenter_save_key";
    /****** 创建被代理对象,传入默认Presenter的工厂 ******/
    private BaseMvpProxy<V, P> mProxy = new BaseMvpProxy<>(PresenterMvpFactoryImpl.<V, P>createFactory(getClass()));

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProxy.onAttachMvpView((V) this);
        if (savedInstanceState != null) {
            mProxy.onRestoreInstanceState(savedInstanceState);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (onLayoutId() > 0) {
            return inflater.inflate(onLayoutId(), container, false);
        } else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onInitView(view);
        if (null != getMvpPresenter()) {
            getMvpPresenter().onInit((V) this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mProxy.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(PRESENTER_SAVE_KEY, mProxy.onSaveInstanceState());
    }

    /**
     * @param ：[presenterFactory]
     * @return : void
     * @methodName ：setPresenterFactory created by luozhipeng on 19/12/17 13:47.
     * @description ：可以实现自己PresenterMvpFactory工厂
     */
    @Override
    public void setPresenterFactory(PresenterMvpFactory<V, P> presenterFactory) {
        mProxy.setPresenterFactory(presenterFactory);
    }

    /**
     * @param ：[]
     * @return : com.lzp.commondemo.BaseMvp.factory.PresenterMvpFactory<V,P> PresenterMvpFactory类型
     * @methodName ：getPresenterFactory created by luozhipeng on 19/12/17 13:46.
     * @description ：获取创建Presenter的工厂
     */
    @Override
    public PresenterMvpFactory<V, P> getPresenterFactory() {
        return mProxy.getPresenterFactory();
    }

    /**
     * @param ：[]
     * @return : P
     * @methodName ：getMvpPresenter created by luozhipeng on 19/12/17 13:46.
     * @description ：获取Presenter
     */
    @Override
    public P getMvpPresenter() {
        return mProxy.getMvpPresenter();
    }

    public abstract int onLayoutId();

    public abstract void onInitView(View view);
}

