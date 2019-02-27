package com.kdanmobile.pdfviewer.screenui.reader.constract;

import android.graphics.drawable.Drawable;

import com.kdanmobile.pdfviewer.base.mvp.presenter.IBasePresenter;
import com.kdanmobile.pdfviewer.base.mvp.view.IBaseView;
import com.kdanmobile.pdfviewer.screenui.reader.configs.AnnotMenuStatus;

/**
 * @classname：ProReaderConstract
 * @author：liujiyuan
 * @date：2018/8/14 下午3:25
 * @description：
 */
public class ProReaderConstract {
    public interface IView extends IBaseView {
        void changeButtonBackgroundColor(AnnotMenuStatus.STATUS status);
        void openActivity(int type);
    }

    public interface IPresenter extends IBasePresenter {
        void highLightSingleTap();
        void highLightLongPress();
        void underLineSingleTap();
        void underLineLongPress();
        void strikeOutSingleTap();
        void strikeOutLongPress();
        void inkSingleTap();
        void inkLongPress();
        void shapeAnnotLongPress();
        void freeTextAnnotLongPress();
        Drawable returnInkButtonPicture();
        void shapeAnnotSingleTap();
        void stampAnnotSingleTap();
        void freeTextAnnotSingleTap();
        void signAnnotSingleTap();
        void linkAnnotSingleTap();
        void resetAnnotationState();

    }
}
