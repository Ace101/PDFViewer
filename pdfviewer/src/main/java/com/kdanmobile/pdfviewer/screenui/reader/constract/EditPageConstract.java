package com.kdanmobile.pdfviewer.screenui.reader.constract;

import com.kdanmobile.pdfviewer.base.mvp.presenter.IBasePresenter;
import com.kdanmobile.pdfviewer.base.mvp.view.IBaseView;
import com.kdanmobile.pdfviewer.screenui.reader.view.adapter.EditPageRecyclerViewAdapter;

/**
 * @classname：EditPageConstract
 * @author：liujiyuan
 * @date：2018/8/29 上午11:05
 * @description：
 */
public class EditPageConstract {
    public interface IView extends IBaseView {
        void setAdapter(final EditPageRecyclerViewAdapter editPageAdapter);
        void setActivityTitle();
    }

    public interface IPresenter extends IBasePresenter {
        EditPageRecyclerViewAdapter getAdapter();
        void setViewSize();
        void pageDelete();
        void pageRotate();
        void onNewPdfExport();
    }
}
