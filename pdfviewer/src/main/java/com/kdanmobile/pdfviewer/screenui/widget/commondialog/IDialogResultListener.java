package com.kdanmobile.pdfviewer.screenui.widget.commondialog;

/**
 * @classname：IDialogResultListener
 * @author：luozhipeng
 * @date：15/8/18 17:20
 * @description： 对话框返回结果回调
 */
public interface IDialogResultListener<T> {
    void onDataResult(T result);
}
