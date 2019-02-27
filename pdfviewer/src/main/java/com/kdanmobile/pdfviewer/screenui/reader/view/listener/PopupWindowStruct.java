package com.kdanmobile.pdfviewer.screenui.reader.view.listener;

/**
 * @classname：PopupWindowStruct
 * @author：liujiyuan
 * @date：2018/8/21 下午5:00
 * @description：所有popwinow的实现接口
 */
public interface PopupWindowStruct {

    PopupWindowStruct setCallback(OnPopupWindowCallback callback);

    PopupWindowStruct setObject(Object o);

    void show(int type);
}
