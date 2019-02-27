package com.kdanmobile.pdfviewer.screenui.reader.constract;

import android.content.Intent;

import com.kdanmobile.pdfviewer.base.mvp.presenter.IBasePresenter;
import com.kdanmobile.pdfviewer.base.mvp.view.IBaseView;

/**
 * @classname：StampAnnotConstract
 * @author：liujiyuan
 * @date：2018/9/3 下午6:00
 * @description：
 */
public class StampAnnotConstract {
    public interface IView extends IBaseView {
        void setAdapter();

        void changeFloatingAddImage(boolean isAddStamp);

        void setDisStampData();
    }

    public interface IPresenter extends IBasePresenter {
        void editStampData();

        void deleteStampData(boolean isDelete);

        void onStop(boolean isFinishing);

        void getPictureUri(int requestCode, int resultCode, Intent data);
    }
}

