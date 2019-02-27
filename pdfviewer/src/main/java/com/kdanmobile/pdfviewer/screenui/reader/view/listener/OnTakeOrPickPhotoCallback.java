package com.kdanmobile.pdfviewer.screenui.reader.view.listener;

/**
 * @classname：OnTakeOrPickPhotoCallback
 * @author：liujiyuan
 * @date：2018/9/5 上午9:29
 * @description：
 */
public interface OnTakeOrPickPhotoCallback extends OnPopupWindowCallback{
    /****** 相册选图标记 ******/
    int GALLERY_REQUEST_CODE = 0X1000;
    /****** 相机拍照标记 ******/
    int CAMERA_REQUEST_CODE = 0X1010;
    void onTakeOrPickPhoto(int requestCode);
}
