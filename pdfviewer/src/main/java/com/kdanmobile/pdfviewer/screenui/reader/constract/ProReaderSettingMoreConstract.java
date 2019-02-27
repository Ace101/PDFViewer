package com.kdanmobile.pdfviewer.screenui.reader.constract;

import com.kdanmobile.pdfviewer.base.mvp.presenter.IBasePresenter;
import com.kdanmobile.pdfviewer.base.mvp.view.IBaseView;

/**
 * @classname：ProReaderSettingMoreConstract
 * @author：liujiyuan
 * @date：2018/8/16 下午4:17
 * @description：
 */
public class ProReaderSettingMoreConstract {
    public interface IView extends IBaseView {
        void setBookMarkState(boolean isAddBookMark);
    }

    public interface IPresenter extends IBasePresenter {
        void enterBookmarkTitleDialog();
        void refreshPageTurning();
        void cleanAllSign();
        void printDocument();
        void removeBookMark();
    }

}
