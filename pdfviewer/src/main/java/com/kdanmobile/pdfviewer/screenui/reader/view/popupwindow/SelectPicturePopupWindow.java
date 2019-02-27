package com.kdanmobile.pdfviewer.screenui.reader.view.popupwindow;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.kdanmobile.pdfviewer.R;
import com.kdanmobile.pdfviewer.base.BasePopupWindow;
import com.kdanmobile.pdfviewer.screenui.reader.view.listener.OnPopupWindowCallback;
import com.kdanmobile.pdfviewer.screenui.reader.view.listener.OnTakeOrPickPhotoCallback;
import com.kdanmobile.pdfviewer.screenui.reader.view.listener.PopupWindowStruct;

/**
 * @classname：SelectPicturePopupWindow
 * @author：liujiyuan
 * @date：2018/9/5 上午9:23
 * @description：选择图片的弹出框
 */
public class SelectPicturePopupWindow extends BasePopupWindow implements PopupWindowStruct, View.OnClickListener {

    /****** Popupwindow附着的view ******/
    private View rootview;
    private OnTakeOrPickPhotoCallback onTakeOrPickPhotoCallback;

    private Button idPictureSelectorCancel;
    private Button idPictureSelectorPickPicture;
    private Button idPictureSelectorTakePhoto;

    public SelectPicturePopupWindow(Context context, View rootview) {
        super(context);
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        this.rootview = rootview;
    }


    @Override
    protected View setLayout(LayoutInflater inflater) {
        return inflater.inflate(R.layout.popupwindow_picture_selector, null);
    }

    @Override
    protected void initResource() {

    }

    @Override
    protected void initListener() {
        idPictureSelectorCancel.setOnClickListener(this);
        idPictureSelectorPickPicture.setOnClickListener(this);
        idPictureSelectorTakePhoto.setOnClickListener(this);
    }

    @Override
    protected void initView() {
        idPictureSelectorCancel = mContentView.findViewById(R.id.id_picture_selector_cancel);
        idPictureSelectorPickPicture = mContentView.findViewById(R.id.id_picture_selector_pick_picture);
        idPictureSelectorTakePhoto = mContentView.findViewById(R.id.id_picture_selector_take_photo);

    }

    @Override
    protected void onClickListener(View view) {
        switch (view.getId()) {
            case R.id.id_picture_selector_take_photo:
                onTakeOrPickPhotoCallback.onTakeOrPickPhoto(OnTakeOrPickPhotoCallback.CAMERA_REQUEST_CODE);
                dismiss();
                break;
            case R.id.id_picture_selector_pick_picture:
                onTakeOrPickPhotoCallback.onTakeOrPickPhoto(OnTakeOrPickPhotoCallback.GALLERY_REQUEST_CODE);
                dismiss();
                break;
            case R.id.id_picture_selector_cancel:
                onTakeOrPickPhotoCallback.onTakeOrPickPhoto(-1);
                dismiss();
                break;
            default:
        }
    }

    @Override
    public PopupWindowStruct setCallback(OnPopupWindowCallback callback) {
        this.onTakeOrPickPhotoCallback = (OnTakeOrPickPhotoCallback) callback;
        return null;
    }

    @Override
    public PopupWindowStruct setObject(Object o) {
        return null;
    }

    @Override
    public void show(int type) {
        changeWindowAlpha((Activity) mContext, 0.4f);
        showAtLocation(rootview, Gravity.CENTER | Gravity.BOTTOM, 0, 0);
    }
}
