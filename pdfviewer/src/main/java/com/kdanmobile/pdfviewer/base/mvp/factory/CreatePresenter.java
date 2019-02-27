package com.kdanmobile.pdfviewer.base.mvp.factory;


import com.kdanmobile.pdfviewer.base.mvp.presenter.BaseMvpPresenter;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @classname：CreatePresenter
 * @author：luozhipeng
 * @date：19/12/17 13:21
 * @description：标注创建Presenter的注解
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface CreatePresenter {
    Class<? extends BaseMvpPresenter> value();
}
