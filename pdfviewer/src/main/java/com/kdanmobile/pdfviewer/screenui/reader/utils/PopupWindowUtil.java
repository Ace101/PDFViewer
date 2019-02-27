package com.kdanmobile.pdfviewer.screenui.reader.utils;

import android.content.Context;
import android.view.View;
import com.kdanmobile.pdfviewer.screenui.reader.view.listener.PopupWindowStruct;
import com.kdanmobile.pdfviewer.screenui.reader.view.popupwindow.FreeTextPopupWindow;
import com.kdanmobile.pdfviewer.screenui.reader.view.popupwindow.LinkAnnotationPopupWindow;
import com.kdanmobile.pdfviewer.screenui.reader.view.popupwindow.MarkerPenAttrPopupWindow;
import com.kdanmobile.pdfviewer.screenui.reader.view.popupwindow.PdfInfoPopupWindow;
import com.kdanmobile.pdfviewer.screenui.reader.view.popupwindow.SelectPicturePopupWindow;
import com.kdanmobile.pdfviewer.screenui.reader.view.popupwindow.ShapeAnnotPopupWindow;

/**
 * @classname：PopupWindowUtil
 * @author：liujiyuan
 * @date：2018/8/17 下午4:50
 * @description：PopupWindow管理工具类
 */
public class PopupWindowUtil {
    private static PopupWindowUtil instance;

    private View rootview;

    public static PopupWindowUtil getInstance(){
        if(instance == null){
            instance = new PopupWindowUtil();
        }
        return instance;
    }

    public enum PopupWindowType{
        /****** 形状注释的弹窗 ******/
        SHAPE_ANNOT_CONFIG,

        /****** link注释的弹窗 ******/
        CREATE_LINK_ANNOT,

        /****** 便签的弹窗 ******/
        NOTE_PAD,

        /****** 标记笔的属性弹窗 ******/
        MARKER_PEN_ATTR,

        /****** FreeText的属性弹窗 ******/
        FREETEXT_ATTR,

        /****** 拍照或选择照片的弹窗 ******/
        TAKE_OR_PICK_PHOTO,

        /****** pdf info ******/
        PDF_INFO

    }

    private Context mContext;

    /****** 用于修改标记笔的属性的弹窗 ******/
    public MarkerPenAttrPopupWindow markerPenAttrPopupWindow;

    /****** 用于修改形状注释的属性的弹窗 ******/
    public ShapeAnnotPopupWindow shapeAnnotPopupWindow;

    /****** 用于修改freeText注释的属性的弹窗 ******/
    public FreeTextPopupWindow freeTextPopupWindow;

    /****** 用于选择图片的弹窗 ******/
    public SelectPicturePopupWindow selectPicturePopupWindow;

    /****** 用于Pdf Info的弹窗 ******/
    public PdfInfoPopupWindow pdfInfoPopupWindow;

    /****** 用于Link annotation的弹窗 ******/
    public LinkAnnotationPopupWindow linkAnnotationPopupWindow;

    /**
     * @param ：[mContext]
     * @return : void
     * @methodName ：initUtil created by liujiyuan on 2018/8/17 下午4:53.
     * @description ：初始化
     */
    public static void initUtil(Context mContext,View rootview){
        if(instance == null){
            instance = new PopupWindowUtil();
        }
        instance.mContext = mContext;
        instance.rootview = rootview;
    }

    /**
     * @param ：[]
     * @return : void
     * @methodName ：finishUtil created by liujiyuan on 2018/8/17 下午4:52.
     * @description ：不使用Util时，对资源的释放
     */
    public static void finishUtil(){
        if(instance == null){
            return;
        }
        dismissAllPopupwindow();
        instance.mContext = null;
        instance.rootview = null;

        instance.markerPenAttrPopupWindow = null;
        instance.shapeAnnotPopupWindow = null;
        instance.freeTextPopupWindow = null;
        instance.selectPicturePopupWindow = null;
        instance.pdfInfoPopupWindow = null;
        instance.linkAnnotationPopupWindow = null;

        instance = null;
    }

    /**
     * @param ：[popupWindowType]
     * @return : android.widget.PopupWindow
     * @methodName ：showPopupWindow created by liujiyuan on 2018/8/17 下午4:56.
     * @description ：根据类型展示不同PopupWindow
     */
    public static PopupWindowStruct buildPopupWindow(PopupWindowType popupWindowType){
        switch (popupWindowType){
            case SHAPE_ANNOT_CONFIG:
                return createShapeAnnotPopupWindow();
            case CREATE_LINK_ANNOT:
                return createLinkAnnotationPopupWindow();
            case NOTE_PAD:
            case MARKER_PEN_ATTR:
                return createMarkerPenAttrPopupWindow();
            case FREETEXT_ATTR:
                return createFreeTextPopupWindow();
            case TAKE_OR_PICK_PHOTO:
                return createSelePicturePopupWindow();
            case PDF_INFO:
                return createPdfInfoPopupWindow();
            default:
                return null;
        }
    }

    /**
     * @param ：[popupWindowType]
     * @return : void
     * @methodName ：dismissPopupWindow created by liujiyuan on 2018/8/17 下午4:57.
     * @description ：根据类型隐藏不同PopupWindow
     */
    public static void dismissPopupWindow(PopupWindowType popupWindowType){
        switch (popupWindowType){
            case SHAPE_ANNOT_CONFIG:
                dismissShapeAnnotPopupWindow();
                break;
            case CREATE_LINK_ANNOT:
                dismissLinkAnnotationPopupWindow();
                break;
            case NOTE_PAD:
                break;
            case MARKER_PEN_ATTR:
                dismissMarkerPenAttrPopupWindow();
                break;
            case FREETEXT_ATTR:
                dismissFreeTextPopupWindow();
                break;
            case TAKE_OR_PICK_PHOTO:
                dismissSelePicturePopupWindow();
                break;
            case PDF_INFO:
                dismissPdfInfoPopupWindow();
                break;
            default:
                break;
        }
    }

    /**
     * @param ：[]
     * @return : void
     * @methodName ：dismissllPopupwindow created by liujiyuan on 2018/8/17 下午4:58.
     * @description ：隐藏所有PopupWindow
     */
    public static void dismissAllPopupwindow(){
        dismissMarkerPenAttrPopupWindow();
        dismissShapeAnnotPopupWindow();
        dismissFreeTextPopupWindow();
        dismissSelePicturePopupWindow();
        dismissPdfInfoPopupWindow();
        dismissLinkAnnotationPopupWindow();
    }

    /***********************用于创建MarkerPenAttr的弹窗***相关方法*******************************/
    private static PopupWindowStruct createMarkerPenAttrPopupWindow(){
        if(instance.markerPenAttrPopupWindow == null){
            instance.markerPenAttrPopupWindow = new MarkerPenAttrPopupWindow(instance.mContext, instance.rootview);
        }
        return instance.markerPenAttrPopupWindow;
    }

    private static void dismissMarkerPenAttrPopupWindow() {
        if(instance.markerPenAttrPopupWindow != null){
            instance.markerPenAttrPopupWindow.dismiss();
        }
    }
    /**********************************END*******************************************/

    /***********************用于创建ShapeAnnotAttr的弹窗***相关方法*******************************/
    private static PopupWindowStruct createShapeAnnotPopupWindow(){
        if(instance.shapeAnnotPopupWindow == null){
            instance.shapeAnnotPopupWindow = new ShapeAnnotPopupWindow(instance.mContext, instance.rootview);
        }
        return instance.shapeAnnotPopupWindow;
    }

    private static void dismissShapeAnnotPopupWindow() {
        if(instance.shapeAnnotPopupWindow != null){
            instance.shapeAnnotPopupWindow.dismiss();
        }
    }
    /**********************************END*******************************************/

    /***********************用于创建FreeTextAnnotAttr的弹窗***相关方法*******************************/
    private static PopupWindowStruct createFreeTextPopupWindow(){
        if(instance.freeTextPopupWindow == null){
            instance.freeTextPopupWindow = new FreeTextPopupWindow(instance.mContext, instance.rootview);
        }
        return instance.freeTextPopupWindow;
    }

    private static void dismissFreeTextPopupWindow() {
        if(instance.freeTextPopupWindow != null){
            instance.freeTextPopupWindow.dismiss();
        }
    }
    /**********************************END*******************************************/

    /***********************用于创建SelePicture的弹窗***相关方法*******************************/
    private static PopupWindowStruct createSelePicturePopupWindow(){
        if(instance.selectPicturePopupWindow == null){
            instance.selectPicturePopupWindow = new SelectPicturePopupWindow(instance.mContext, instance.rootview);
        }
        return instance.selectPicturePopupWindow;
    }

    private static void dismissSelePicturePopupWindow() {
        if(instance.selectPicturePopupWindow != null){
            instance.selectPicturePopupWindow.dismiss();
        }
    }
    /**********************************END*******************************************/

    /***********************用于创建PDF Info的弹窗***相关方法*******************************/
    private static PopupWindowStruct createPdfInfoPopupWindow(){
        if(instance.pdfInfoPopupWindow == null){
            instance.pdfInfoPopupWindow = new PdfInfoPopupWindow(instance.mContext, instance.rootview);
        }
        return instance.pdfInfoPopupWindow;
    }

    private static void dismissPdfInfoPopupWindow() {
        if(instance.pdfInfoPopupWindow != null){
            instance.pdfInfoPopupWindow.dismiss();
        }
    }
    /**********************************END*******************************************/

    /***********************用于创建Link annotation的弹窗***相关方法*******************************/
    private static PopupWindowStruct createLinkAnnotationPopupWindow(){
        if(instance.linkAnnotationPopupWindow == null){
            instance.linkAnnotationPopupWindow = new LinkAnnotationPopupWindow(instance.mContext, instance.rootview);
        }
        return instance.linkAnnotationPopupWindow;
    }

    private static void dismissLinkAnnotationPopupWindow(){
        if(instance.linkAnnotationPopupWindow != null){
            instance.linkAnnotationPopupWindow.dismiss();
        }
    }
    /**********************************END*******************************************/

}
