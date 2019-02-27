package com.kdanmobile.pdfviewer.base.mvp.factory;

import com.kdanmobile.pdfviewer.base.mvp.presenter.BaseMvpPresenter;
import com.kdanmobile.pdfviewer.base.mvp.view.IBaseView;

/**
 * @classname：PresenterMvpFactory
 * @author：luozhipeng
 * @date：19/12/17 13:23
 * @description：Presenter工厂接口
 */
public interface PresenterMvpFactory<V extends IBaseView, P extends BaseMvpPresenter<V>> {
    /**
     * 创建Presenter的接口方法
     *
     * @return 需要创建的Presenter
     */
    P createMvpPresenter();
}
