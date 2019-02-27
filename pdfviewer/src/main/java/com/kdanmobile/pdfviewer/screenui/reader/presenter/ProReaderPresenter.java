package com.kdanmobile.pdfviewer.screenui.reader.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.kdanmobile.kmpdfkit.annotation.bean.KMPDFAnnotationBean;
import com.kdanmobile.kmpdfkit.annotation.bean.KMPDFArrowAnnotationBean;
import com.kdanmobile.kmpdfkit.annotation.bean.KMPDFCircleAnnotationBean;
import com.kdanmobile.kmpdfkit.annotation.bean.KMPDFFreetextAnnotationBean;
import com.kdanmobile.kmpdfkit.annotation.bean.KMPDFHighlightAnnotationBean;
import com.kdanmobile.kmpdfkit.annotation.bean.KMPDFInkAnnotationBean;
import com.kdanmobile.kmpdfkit.annotation.bean.KMPDFLineAnnotationBean;
import com.kdanmobile.kmpdfkit.annotation.bean.KMPDFSignatureAnnotationBean;
import com.kdanmobile.kmpdfkit.annotation.bean.KMPDFSquareAnnotationBean;
import com.kdanmobile.kmpdfkit.annotation.bean.KMPDFStampAnnotationBean;
import com.kdanmobile.kmpdfkit.annotation.bean.KMPDFStrikeoutAnnotationBean;
import com.kdanmobile.kmpdfkit.annotation.bean.KMPDFUnderlineAnnotationBean;
import com.kdanmobile.kmpdfkit.annotation.link.listener.OnLinkInfoChangeListener;
import com.kdanmobile.kmpdfkit.contextmenu.KMPDFMenuItem;
import com.kdanmobile.kmpdfkit.manager.KMPDFFactory;
import com.kdanmobile.kmpdfkit.manager.controller.KMPDFDocumentController;
import com.kdanmobile.kmpdfkit.manager.controller.KMPDFFreeTextController;
import com.kdanmobile.kmpdfkit.manager.controller.KMPDFInkController;
import com.kdanmobile.kmpdfkit.manager.controller.KMPDFLinkController;
import com.kdanmobile.kmpdfkit.manager.controller.KMPDFLongPressCreateAnnotController;
import com.kdanmobile.kmpdfkit.manager.controller.KMPDFMarkupController;
import com.kdanmobile.kmpdfkit.manager.controller.KMPDFShapeAnnotController;
import com.kdanmobile.kmpdfkit.manager.controller.KMPDFSignatureController;
import com.kdanmobile.kmpdfkit.manager.controller.KMPDFStampController;
import com.kdanmobile.kmpdfkit.manager.listener.KMPDFLinkCreateCallback;
import com.kdanmobile.kmpdfkit.utlis.KMFileUtil;
import com.kdanmobile.pdfviewer.R;
import com.kdanmobile.pdfviewer.base.ProApplication;
import com.kdanmobile.pdfviewer.base.mvp.presenter.BaseMvpPresenter;
import com.kdanmobile.pdfviewer.event.MessageEvent;
import com.kdanmobile.pdfviewer.screenui.reader.configs.AnnotDefaultConfig;
import com.kdanmobile.pdfviewer.screenui.reader.configs.AnnotMenuStatus;
import com.kdanmobile.pdfviewer.screenui.reader.constract.ProReaderConstract;
import com.kdanmobile.pdfviewer.screenui.reader.utils.InitKMPDFControllerUtil;
import com.kdanmobile.pdfviewer.screenui.reader.utils.PopupWindowUtil;
import com.kdanmobile.pdfviewer.screenui.reader.view.activity.ProReaderActivity;
import com.kdanmobile.pdfviewer.screenui.reader.view.listener.OnTakeOrPickPhotoCallback;
import com.kdanmobile.pdfviewer.screenui.reader.view.popupwindow.ShapeAnnotPopupWindow;
import com.kdanmobile.pdfviewer.utils.CommonUtils;
import com.kdanmobile.pdfviewer.utils.PathManager;
import com.kdanmobile.pdfviewer.utils.UriToPathUtil;
import com.kdanmobile.pdfviewer.utils.eventbus.ConstantBus;
import com.kdanmobile.pdfviewer.utils.eventbus.EventBusUtils;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @classname：ProReaderPresenter
 * @author：liujiyuan
 * @date：2NO_PARAM_OPEN_POPUPWINDOW18/8/14 下午3:25
 * @description：ReaderActivity的功能实现部分
 */
public class ProReaderPresenter extends BaseMvpPresenter<ProReaderActivity> implements ProReaderConstract.IPresenter, KMPDFLinkCreateCallback, OnTakeOrPickPhotoCallback {

    private final int NO_PARAM_OPEN_POPUPWINDOW = 0;
    private KMPDFMarkupController kmpdfMarkupController;
    private KMPDFDocumentController kmpdfDocumentController;
    private KMPDFShapeAnnotController kmpdfShapeAnnotController;
    private KMPDFFreeTextController kmpdfFreeTextController;
    private KMPDFStampController kmpdfStampController;
    private KMPDFSignatureController kmpdfSignatureController;
    private KMPDFLinkController kmpdfLinkController;
    private KMPDFInkController kmpdfInkController;
    private KMPDFFactory kmpdfFactory;
    private KMPDFLongPressCreateAnnotController kmpdfLongPressCreateAnnotController;

    /****** markup注释属性对象 ******/
    KMPDFHighlightAnnotationBean kmpdfHighlightAnnotationBean;
    KMPDFUnderlineAnnotationBean kmpdfUnderlineAnnotationBean;
    KMPDFStrikeoutAnnotationBean kmpdfStrikeoutAnnotationBean;
    KMPDFInkAnnotationBean kmpdfInkAnnotationBean;
    /****** 形状注释属性对象 ******/
    KMPDFCircleAnnotationBean kmpdfCircleAnnotationBean;
    KMPDFArrowAnnotationBean kmpdfArrowAnnotationBean;
    KMPDFSquareAnnotationBean kmpdfSquareAnnotationBean;
    KMPDFLineAnnotationBean kmpdfLineAnnotationBean;
    /****** freetext注释属性对象 ******/
    KMPDFFreetextAnnotationBean kmpdfFreetextAnnotationBean;

    Drawable[] inkPictures;
    private Context context;
    public String takePhoto_tempPicPath;

    @Override
    public void onInit(ProReaderActivity mView) {
        EventBusUtils.getInstance().register(this);
        if (isAttached()) {
            context = mView.getApplicationContext();
            /****** 设置各个注释共能的Controller ******/
            kmpdfFactory = InitKMPDFControllerUtil.getInstance().getKmpdfFactory();
            kmpdfMarkupController = InitKMPDFControllerUtil.getInstance().getKmpdfMarkupController();
            kmpdfDocumentController = InitKMPDFControllerUtil.getInstance().getKmpdfDocumentController();
            kmpdfShapeAnnotController = InitKMPDFControllerUtil.getInstance().getKmpdfShapeAnnotController();
            kmpdfFreeTextController = InitKMPDFControllerUtil.getInstance().getKmpdfFreeTextController();
            kmpdfStampController = InitKMPDFControllerUtil.getInstance().getKmpdfStampController();
            kmpdfSignatureController = InitKMPDFControllerUtil.getInstance().getKmpdfSignatureController();
            kmpdfLinkController = InitKMPDFControllerUtil.getInstance().getKmpdfLinkController();
            kmpdfLongPressCreateAnnotController = InitKMPDFControllerUtil.getInstance().getKmpdfLongPressCreateAnnotController();
            kmpdfInkController = InitKMPDFControllerUtil.getInstance().getKmpdfInkController();

            /****** 初始化各个注释的属性bean ******/
            kmpdfHighlightAnnotationBean = new KMPDFHighlightAnnotationBean("", AnnotDefaultConfig.markerPenColor_hightlight, AnnotDefaultConfig.markerPenAlpha_hightlight);
            kmpdfUnderlineAnnotationBean = new KMPDFUnderlineAnnotationBean("", AnnotDefaultConfig.markerPenColor_underline, AnnotDefaultConfig.markerPenAlpha_underline);
            kmpdfStrikeoutAnnotationBean = new KMPDFStrikeoutAnnotationBean("", AnnotDefaultConfig.markerPenColor_strikeout, AnnotDefaultConfig.markerPenAlpha_strikeout);
            kmpdfInkAnnotationBean = new KMPDFInkAnnotationBean("", AnnotDefaultConfig.markerPenColor_ink, AnnotDefaultConfig.markerPenSize_ink, AnnotDefaultConfig.markerPenAlpha_ink);
            kmpdfLineAnnotationBean = new KMPDFLineAnnotationBean("", AnnotDefaultConfig.shapeAnnotLineColor, AnnotDefaultConfig.shapeAnnotLineWidth, AnnotDefaultConfig.shapeAnnotLineAlpha);
            kmpdfArrowAnnotationBean = new KMPDFArrowAnnotationBean("", AnnotDefaultConfig.shapeAnnotLineColor, AnnotDefaultConfig.shapeAnnotLineWidth, AnnotDefaultConfig.shapeAnnotLineAlpha);
            kmpdfCircleAnnotationBean = new KMPDFCircleAnnotationBean("", AnnotDefaultConfig.shapeAnnotLineColor, AnnotDefaultConfig.shapeAnnotLineWidth, AnnotDefaultConfig.shapeAnnotLineAlpha,
                    AnnotDefaultConfig.shapeAnnotFillColor, AnnotDefaultConfig.shapeAnnotFillAlpha);
            kmpdfSquareAnnotationBean = new KMPDFSquareAnnotationBean("", AnnotDefaultConfig.shapeAnnotLineColor, AnnotDefaultConfig.shapeAnnotLineWidth, AnnotDefaultConfig.shapeAnnotLineAlpha,
                    AnnotDefaultConfig.shapeAnnotFillColor, AnnotDefaultConfig.shapeAnnotFillAlpha);
            kmpdfFreetextAnnotationBean = new KMPDFFreetextAnnotationBean("", AnnotDefaultConfig.color__freeText, AnnotDefaultConfig.textSize__freeText, AnnotDefaultConfig.alpha__freeText,
                    AnnotDefaultConfig.typeface_freeText, AnnotDefaultConfig.isBold_freeText, AnnotDefaultConfig.isItalic_freeText);

            kmpdfFactory.setAnnotationAttribute(kmpdfHighlightAnnotationBean);
            kmpdfFactory.setAnnotationAttribute(kmpdfUnderlineAnnotationBean);
            kmpdfFactory.setAnnotationAttribute(kmpdfStrikeoutAnnotationBean);
            kmpdfFactory.setAnnotationAttribute(kmpdfInkAnnotationBean);
            kmpdfFactory.setAnnotationAttribute(kmpdfLineAnnotationBean);
            kmpdfFactory.setAnnotationAttribute(kmpdfArrowAnnotationBean);
            kmpdfFactory.setAnnotationAttribute(kmpdfCircleAnnotationBean);
            kmpdfFactory.setAnnotationAttribute(kmpdfSquareAnnotationBean);
            kmpdfFactory.setAnnotationAttribute(kmpdfFreetextAnnotationBean);

            /****** 设置 手指在屏幕上滑动后抬起时的回调，表示手指已经在屏幕绘制出Link，需要用户使用UI操作，对link的属性进行设置 ******/
            kmpdfLinkController.setLinkCreateCallback(this);

            initKMPDFMenuItem();
            getInkButtonPictures();
        }
    }

    /**
     * @methodName：initKMPDFMenuItem created by liujiyuan on 2018/9/6 下午4:57.
     * @description：初始化各个注释弹出菜单的样式，回调方法
     */
    private void initKMPDFMenuItem() {
        /**
         * 初始化HIGH_LIGHT、UNDER_LINE、STRIKE_OUT的弹出菜单
         **/
        KMPDFMenuItem deleteMarkupKMPDFMenuItem = new KMPDFMenuItem();
        deleteMarkupKMPDFMenuItem.annotType = KMPDFMenuItem.AnnotType.HIGH_LIGHT;
        deleteMarkupKMPDFMenuItem.menu_resId = R.menu.annotation_menu_edit;
        deleteMarkupKMPDFMenuItem.menuCallbacks.add(0, (view, annotType) -> {
            openMarkupMenuPopupWindow(annotType);
            return false;
        });
        deleteMarkupKMPDFMenuItem.menuCallbacks.add(1, (view, annotType) -> {
            openMarkupMenuPopupWindow(annotType);
            return false;
        });
        deleteMarkupKMPDFMenuItem.menuCallbacks.add(2, (view, annotType) -> {
            kmpdfMarkupController.deleteMarkupAnnotView();
            return false;
        });
        kmpdfMarkupController.setAnnotMenuItem(deleteMarkupKMPDFMenuItem);

        /**
         * INK的弹出菜单
         **/
        KMPDFMenuItem inkKMPDFMenuItem = new KMPDFMenuItem();
        inkKMPDFMenuItem.annotType = KMPDFMenuItem.AnnotType.INK;
        inkKMPDFMenuItem.menu_resId = R.menu.ink_annot_menu;
        inkKMPDFMenuItem.menuCallbacks.add(0, (view, annotType) -> {
            openMarkupMenuPopupWindow(annotType);
            return false;
        });
        inkKMPDFMenuItem.menuCallbacks.add(1, (view, annotType) -> {
            openMarkupMenuPopupWindow(annotType);
            return false;
        });
        inkKMPDFMenuItem.menuCallbacks.add(2, (view, annotType) -> {
            openMarkupMenuPopupWindow(annotType);
            return false;
        });
        inkKMPDFMenuItem.menuCallbacks.add(3, (view, annotType) -> {
            kmpdfInkController.deleteInkAnnotView();
            return false;
        });

        kmpdfInkController.setAnnotMenuItem(inkKMPDFMenuItem);

        /**
         * 初始化shape 注释的Line,ARROW,SQUARE,CIRCLE的弹出菜单
         **/
        KMPDFMenuItem editShapeKMPDFMenuItem = new KMPDFMenuItem();
        editShapeKMPDFMenuItem.annotType = KMPDFMenuItem.AnnotType.LINE;
        editShapeKMPDFMenuItem.menu_resId = R.menu.shape_annot_menu;
        editShapeKMPDFMenuItem.menuCallbacks.add(0, (view, annotType) -> {
            /****** 打开 SHAPE_ANNOT_CONFIG 的popupWindow ******/
            PopupWindowUtil.getInstance().shapeAnnotPopupWindow.show(ShapeAnnotPopupWindow.EDIT_SHAPE, ShapeAnnotPopupWindow.STROKE_ATTR);
            return false;
        });
        editShapeKMPDFMenuItem.menuCallbacks.add(1, (view, annotType) -> {
            /****** 打开 SHAPE_ANNOT_CONFIG 的popupWindow ******/
            PopupWindowUtil.getInstance().shapeAnnotPopupWindow.show(ShapeAnnotPopupWindow.EDIT_SHAPE, ShapeAnnotPopupWindow.STROKE_ATTR);
            return false;
        });
        editShapeKMPDFMenuItem.menuCallbacks.add(2, (view, annotType) -> {
            /****** 打开 SHAPE_ANNOT_CONFIG 的popupWindow ******/
            PopupWindowUtil.getInstance().shapeAnnotPopupWindow.show(ShapeAnnotPopupWindow.EDIT_SHAPE, ShapeAnnotPopupWindow.FILL_ATTR);
            return false;
        });
        editShapeKMPDFMenuItem.menuCallbacks.add(3, (view, annotType) -> {
            kmpdfShapeAnnotController.deleteShapeAnnotView();
            return false;
        });
        kmpdfShapeAnnotController.setAnnotMenuItem(editShapeKMPDFMenuItem);

        /**
         * 初始化 freeText注释 的弹出菜单
         **/
        KMPDFMenuItem editFreeTextKMPDFMenuItem = new KMPDFMenuItem();
        editFreeTextKMPDFMenuItem.annotType = KMPDFMenuItem.AnnotType.FREETEXT;
        editFreeTextKMPDFMenuItem.menu_resId = R.menu.freetext_annot_menu;
        editFreeTextKMPDFMenuItem.menuCallbacks.add(0, (view, annotType) -> {
            kmpdfFreeTextController.copyFreeTextContent();
            return true;
        });
        editFreeTextKMPDFMenuItem.menuCallbacks.add(1, (view, annotType) -> {
            kmpdfFreeTextController.setCurrentFreeTextViewEditMode(1);
            return false;
        });
        editFreeTextKMPDFMenuItem.menuCallbacks.add(2, (view, annotType) -> {
            kmpdfFreeTextController.setCurrentFreeTextViewEditMode(0);
            /****** 打开 freeText 的popupWindow ******/
            PopupWindowUtil.getInstance().freeTextPopupWindow.show(0);
            return false;
        });
        editFreeTextKMPDFMenuItem.menuCallbacks.add(3, (view, annotType) -> {
            kmpdfFreeTextController.setCurrentFreeTextViewEditMode(0);
            /****** 打开 freeText 的popupWindow ******/
            PopupWindowUtil.getInstance().freeTextPopupWindow.show(1);
            return false;
        });
        editFreeTextKMPDFMenuItem.menuCallbacks.add(4, (view, annotType) -> {
            kmpdfFreeTextController.deleteFreeTextAnnotView();
            return false;
        });
        kmpdfFreeTextController.setAnnotMenuItem(editFreeTextKMPDFMenuItem);

        /**
         * 初始化 stamp注释 的弹出菜单
         **/
        KMPDFMenuItem deleteStampKMPDFMenuItem = new KMPDFMenuItem();
        deleteStampKMPDFMenuItem.annotType = KMPDFMenuItem.AnnotType.STAMP;
        deleteStampKMPDFMenuItem.menu_resId = R.menu.annotation_common_delete;
        deleteStampKMPDFMenuItem.menuCallbacks.add(0, (view, annotType) -> {
            kmpdfStampController.deleteStampAnnotView();
            return false;
        });
        kmpdfStampController.setAnnotMenuItem(deleteStampKMPDFMenuItem);

        /**
         * 初始化 sign注释 的弹出菜单
         **/
        KMPDFMenuItem signKMPDFMenuItem = new KMPDFMenuItem();
        signKMPDFMenuItem.annotType = KMPDFMenuItem.AnnotType.SIGNATURE;
        signKMPDFMenuItem.menu_resId = R.menu.sign_annot_menu;
        signKMPDFMenuItem.menuCallbacks.add(0, (view, annotType) -> {
            kmpdfSignatureController.deleteSignatureAnnotView();
            return false;
        });
        signKMPDFMenuItem.menuCallbacks.add(1, (view, annotType) -> {
            kmpdfSignatureController.saveSignatureAnnot();
            return false;
        });
        kmpdfSignatureController.setAnnotMenuItem(signKMPDFMenuItem);

        /**
         * 初始化 link注释 的弹出菜单
         **/
        KMPDFMenuItem linkKMPDFMenuItem = new KMPDFMenuItem();
        linkKMPDFMenuItem.annotType = KMPDFMenuItem.AnnotType.LINK;
        linkKMPDFMenuItem.menu_resId = R.menu.link_annot_menu;
        linkKMPDFMenuItem.menuCallbacks.add(0, (view, annotType) -> {
            kmpdfLinkController.editLinkAttr();
            return false;
        });
        linkKMPDFMenuItem.menuCallbacks.add(1, (view, annotType) -> {
            kmpdfLinkController.deleteLinkAnnotView();
            return false;
        });
        kmpdfLinkController.setAnnotMenuItem(linkKMPDFMenuItem);

        /**
         * 初始化长按菜单
         **/
        KMPDFMenuItem longClickKMPDFMenuItem = new KMPDFMenuItem();
        longClickKMPDFMenuItem.annotType = KMPDFMenuItem.AnnotType.LONG_PRESS;
        longClickKMPDFMenuItem.menu_resId = R.menu.long_press_menu;
        longClickKMPDFMenuItem.menuCallbacks.add(0, (view, annotType) -> {
            KMPDFFreetextAnnotationBean kmpdfFreetextAnnotation = new KMPDFFreetextAnnotationBean(
                    "",
                    AnnotDefaultConfig.color__freeText,
                    AnnotDefaultConfig.textSize__freeText,
                    AnnotDefaultConfig.alpha__freeText,
                    AnnotDefaultConfig.typeface_freeText,
                    AnnotDefaultConfig.isBold_freeText,
                    AnnotDefaultConfig.isItalic_freeText
            );
            kmpdfFactory.setAnnotationAttribute(kmpdfFreetextAnnotation);
            kmpdfFreeTextController.createFreetext(view.getLeft(), view.getTop());
            return true;
        });
        longClickKMPDFMenuItem.menuCallbacks.add(1, (view, annotType) -> {
            kmpdfLongPressCreateAnnotController.longPress_Paste();
            return true;
        });
        longClickKMPDFMenuItem.menuCallbacks.add(2, (view, annotType) -> {
            getMvpView().openActivity(getMvpView().OPEN_SIGN_ANNOT);
            return true;
        });
        longClickKMPDFMenuItem.menuCallbacks.add(3, (view, annotType) -> {
            getMvpView().openActivity(getMvpView().OPEN_STAMP_ANNOT);
            return true;
        });
        longClickKMPDFMenuItem.menuCallbacks.add(4, (view, annotType) -> {
            /****** 打开选择图片弹框 ******/
            PopupWindowUtil.getInstance().selectPicturePopupWindow.show(0);
            return true;
        });
        longClickKMPDFMenuItem.menuCallbacks.add(5, (view, annotType) -> {
            kmpdfLinkController.createSingleUseLink();
            return true;
        });
        kmpdfLongPressCreateAnnotController.setAnnotMenuItem(longClickKMPDFMenuItem);
    }

    /**
     * @methodName：openMarkupMenuPopupWindow created by liujiyuan on 2018/9/26 下午4:46.
     * @description：在菜单中打开MarkupMenuPopupWindow的方法
     */
    private void openMarkupMenuPopupWindow(KMPDFMenuItem.AnnotType annotType) {
        switch (annotType) {
            case INK:
                AnnotDefaultConfig.markerPenType = AnnotDefaultConfig.MarkerPenType.INK;
                break;
            case HIGH_LIGHT:
                AnnotDefaultConfig.markerPenType = AnnotDefaultConfig.MarkerPenType.HIGH_LIGHT;
                break;
            case UNDER_LINE:
                AnnotDefaultConfig.markerPenType = AnnotDefaultConfig.MarkerPenType.UNDER_LINE;
                break;
            case STRIKE_OUT:
                AnnotDefaultConfig.markerPenType = AnnotDefaultConfig.MarkerPenType.STRIK_EOUT;
                break;
            default:
                return;
        }
        PopupWindowUtil.getInstance().markerPenAttrPopupWindow.show(NO_PARAM_OPEN_POPUPWINDOW);
    }


    /**
     * @methodName：highLightSingleTap created by liujiyuan on 2NO_PARAM_OPEN_POPUPWINDOW18/8/31 下午4:33.
     * @description：高亮注释-单击操作
     */
    @Override
    public void highLightSingleTap() {
        if (isAttached()) {
            if (AnnotMenuStatus.status != AnnotMenuStatus.STATUS.HIGHLIGHT) {
                AnnotMenuStatus.status = AnnotMenuStatus.STATUS.HIGHLIGHT;
                getMvpView().changeButtonBackgroundColor(AnnotMenuStatus.STATUS.HIGHLIGHT);
                kmpdfFactory.setAnnotationEditMode(kmpdfHighlightAnnotationBean.type);
            } else {
                resetAnnotationState();
            }
        }
    }

    /**
     * @methodName：highLightLongPress created by liujiyuan on 2NO_PARAM_OPEN_POPUPWINDOW18/8/31 下午4:32.
     * @description：高亮注释-长按操作
     */
    @Override
    public void highLightLongPress() {
        if (isAttached()) {
            AnnotMenuStatus.status = AnnotMenuStatus.STATUS.HIGHLIGHT;
            AnnotDefaultConfig.markerPenType = AnnotDefaultConfig.MarkerPenType.HIGH_LIGHT;
            kmpdfFactory.setAnnotationEditMode(kmpdfHighlightAnnotationBean.type);

            PopupWindowUtil.getInstance().markerPenAttrPopupWindow.show(NO_PARAM_OPEN_POPUPWINDOW);
            getMvpView().changeButtonBackgroundColor(AnnotMenuStatus.STATUS.HIGHLIGHT);
        }
    }

    /**
     * @methodName：underLineSingleTap created by liujiyuan on 2NO_PARAM_OPEN_POPUPWINDOW18/8/31 下午4:32.
     * @description：下划线注释-单击操作
     */
    @Override
    public void underLineSingleTap() {
        if (isAttached()) {
            if (AnnotMenuStatus.status != AnnotMenuStatus.STATUS.UNDERLINE) {
                AnnotMenuStatus.status = AnnotMenuStatus.STATUS.UNDERLINE;
                kmpdfFactory.setAnnotationEditMode(kmpdfUnderlineAnnotationBean.type);
                getMvpView().changeButtonBackgroundColor(AnnotMenuStatus.STATUS.UNDERLINE);
            } else {
                resetAnnotationState();
            }
        }
    }

    /**
     * @methodName：underLineLongPress created by liujiyuan on 2NO_PARAM_OPEN_POPUPWINDOW18/8/31 下午4:32.
     * @description：下划线注释-长按操作
     */
    @Override
    public void underLineLongPress() {
        if (isAttached()) {
            AnnotDefaultConfig.markerPenType = AnnotDefaultConfig.MarkerPenType.UNDER_LINE;
            AnnotMenuStatus.status = AnnotMenuStatus.STATUS.UNDERLINE;
            kmpdfFactory.setAnnotationEditMode(kmpdfUnderlineAnnotationBean.type);
            PopupWindowUtil.getInstance().markerPenAttrPopupWindow.show(NO_PARAM_OPEN_POPUPWINDOW);
            getMvpView().changeButtonBackgroundColor(AnnotMenuStatus.STATUS.UNDERLINE);
        }
    }

    /**
     * @methodName：strikeOutSingleTap created by liujiyuan on 2NO_PARAM_OPEN_POPUPWINDOW18/8/31 下午4:32.
     * @description：删除线注释-单击操作
     */
    @Override
    public void strikeOutSingleTap() {
        if (isAttached()) {
            if (AnnotMenuStatus.status != AnnotMenuStatus.STATUS.STRIKEOUT) {
                AnnotMenuStatus.status = AnnotMenuStatus.STATUS.STRIKEOUT;
                kmpdfFactory.setAnnotationEditMode(kmpdfStrikeoutAnnotationBean.type);
                getMvpView().changeButtonBackgroundColor(AnnotMenuStatus.STATUS.STRIKEOUT);
            } else {
                resetAnnotationState();
            }
        }
    }

    /**
     * @methodName：strikeOutLongPress created by liujiyuan on 2NO_PARAM_OPEN_POPUPWINDOW18/8/31 下午4:32.
     * @description：删除线注释-长按操作
     */
    @Override
    public void strikeOutLongPress() {
        if (isAttached()) {
            AnnotDefaultConfig.markerPenType = AnnotDefaultConfig.MarkerPenType.STRIK_EOUT;
            AnnotMenuStatus.status = AnnotMenuStatus.STATUS.STRIKEOUT;
            kmpdfFactory.setAnnotationEditMode(kmpdfStrikeoutAnnotationBean.type);
            PopupWindowUtil.getInstance().markerPenAttrPopupWindow.show(NO_PARAM_OPEN_POPUPWINDOW);
            getMvpView().changeButtonBackgroundColor(AnnotMenuStatus.STATUS.STRIKEOUT);
        }
    }

    /**
     * @methodName：inkSingleTap created by liujiyuan on 2NO_PARAM_OPEN_POPUPWINDOW18/8/31 下午4:31.
     * @description：涂鸦注释-单击操作
     */
    @Override
    public void inkSingleTap() {
        if (isAttached()) {
            if (AnnotMenuStatus.status == AnnotMenuStatus.STATUS.INK) {
                resetAnnotationState();
            } else {
                AnnotMenuStatus.status = AnnotMenuStatus.STATUS.INK;
                kmpdfFactory.setAnnotationEditMode(kmpdfInkAnnotationBean.type);
                getMvpView().changeButtonBackgroundColor(AnnotMenuStatus.STATUS.INK);
            }
        }
    }

    /**
     * @methodName：inkLongPress created by liujiyuan on 2NO_PARAM_OPEN_POPUPWINDOW18/8/31 下午4:31.
     * @description：涂鸦注释-长按操作
     */
    @Override
    public void inkLongPress() {
        if (isAttached()) {
            AnnotDefaultConfig.markerPenType = AnnotDefaultConfig.MarkerPenType.INK;
            AnnotMenuStatus.status = AnnotMenuStatus.STATUS.INK;
            kmpdfFactory.setAnnotationEditMode(kmpdfInkAnnotationBean.type);
            PopupWindowUtil.getInstance().markerPenAttrPopupWindow.show(NO_PARAM_OPEN_POPUPWINDOW);
            getMvpView().changeButtonBackgroundColor(AnnotMenuStatus.STATUS.INK);
        }
    }

    /**
     * @methodName：shapeAnnotSingleTap created by liujiyuan on 2018/9/10 下午3:56.
     * @description：形状注释 — 单击操作
     */
    @Override
    public void shapeAnnotSingleTap() {
        if (isAttached()) {
            if (AnnotMenuStatus.status == AnnotMenuStatus.STATUS.SHAPE) {
                resetAnnotationState();
            } else {
                AnnotMenuStatus.status = AnnotMenuStatus.STATUS.SHAPE;
                changeShapeEditMode();
                getMvpView().changeButtonBackgroundColor(AnnotMenuStatus.STATUS.SHAPE);
            }
        }
    }

    /**
     * @methodName：changeShapeEditMode created by liujiyuan on 2018/9/10 下午3:57.
     * @description：设置每个shape注释类型的操作
     */
    private void changeShapeEditMode() {
        switch (AnnotDefaultConfig.shapeType) {
            case LINE:
                kmpdfFactory.setAnnotationEditMode(kmpdfLineAnnotationBean.type);
                break;
            case ARROW:
                kmpdfFactory.setAnnotationEditMode(kmpdfArrowAnnotationBean.type);
                break;
            case CIRCLE:
                kmpdfFactory.setAnnotationEditMode(kmpdfCircleAnnotationBean.type);
                break;
            case SQUARE:
                kmpdfFactory.setAnnotationEditMode(kmpdfSquareAnnotationBean.type);
                break;
            default:
        }
    }

    /**
     * @methodName：shapeAnnotLongPress created by liujiyuan on 2NO_PARAM_OPEN_POPUPWINDOW18/8/31 下午4:33.
     * @description：形状注释—长按操作
     */
    @Override
    public void shapeAnnotLongPress() {
        if (isAttached()) {
            /****** 打开 SHAPE_ANNOT_CONFIG 的popupWindow ******/
            PopupWindowUtil.getInstance().shapeAnnotPopupWindow.show(ShapeAnnotPopupWindow.CREATE_SHAPE, ShapeAnnotPopupWindow.STROKE_ATTR);

            AnnotMenuStatus.status = AnnotMenuStatus.STATUS.SHAPE;
            getMvpView().changeButtonBackgroundColor(AnnotMenuStatus.STATUS.SHAPE);

            changeShapeEditMode();
        }
    }

    /**
     * @methodName：freeTextAnnotLongPress created by liujiyuan on 2NO_PARAM_OPEN_POPUPWINDOW18/8/31 下午4:34.
     * @description：freeText注释—长按操作
     */
    @Override
    public void freeTextAnnotLongPress() {
        if (isAttached()) {
            /****** 打开 freeText 的popupWindow ******/
            PopupWindowUtil.getInstance().freeTextPopupWindow.show(NO_PARAM_OPEN_POPUPWINDOW);

            AnnotMenuStatus.status = AnnotMenuStatus.STATUS.FREETEXT;
            getMvpView().changeButtonBackgroundColor(AnnotMenuStatus.STATUS.FREETEXT);

            kmpdfFactory.setAnnotationEditMode(kmpdfFreetextAnnotationBean.type);
        }
    }

    /**
     * @methodName：freeTextAnnotSingleTap created by liujiyuan on 2018/9/10 下午5:48.
     * @description：freeText注释—单击操作
     */
    @Override
    public void freeTextAnnotSingleTap() {
        if (isAttached()) {
            if (AnnotMenuStatus.status == AnnotMenuStatus.STATUS.FREETEXT) {
                resetAnnotationState();
            } else {
                AnnotMenuStatus.status = AnnotMenuStatus.STATUS.FREETEXT;
                getMvpView().changeButtonBackgroundColor(AnnotMenuStatus.STATUS.FREETEXT);
                kmpdfFactory.setAnnotationEditMode(kmpdfFreetextAnnotationBean.type);
            }
        }
    }

    /**
     * @methodName：stampAnnotSingleTap created by liujiyuan on 2018/9/10 下午4:32.
     * @description：stamp注释 — 单击操作
     */
    @Override
    public void stampAnnotSingleTap() {
        if (isAttached()) {
            if (AnnotMenuStatus.status == AnnotMenuStatus.STATUS.STAMP) {
                resetAnnotationState();
            } else {
                AnnotMenuStatus.status = AnnotMenuStatus.STATUS.STAMP;
                getMvpView().openActivity(getMvpView().OPEN_STAMP_ANNOT);
                getMvpView().changeButtonBackgroundColor(AnnotMenuStatus.STATUS.STAMP);

            }
        }
    }

    /**
     * @methodName：signAnnotSingleTap created by liujiyuan on 2018/9/13 下午1:30.
     * @description：sign注释 — 单击操作
     */
    @Override
    public void signAnnotSingleTap() {
        if (isAttached()) {
            if (AnnotMenuStatus.status == AnnotMenuStatus.STATUS.SIGN) {
                resetAnnotationState();
            } else {
                AnnotMenuStatus.status = AnnotMenuStatus.STATUS.SIGN;
                getMvpView().openActivity(getMvpView().OPEN_SIGN_ANNOT);
                getMvpView().changeButtonBackgroundColor(AnnotMenuStatus.STATUS.SIGN);

            }
        }
    }

    /**
     * @methodName：linkAnnotSingleTap created by liujiyuan on 2018/9/13 下午1:33.
     * @description：link注释 — 单击操作
     */
    @Override
    public void linkAnnotSingleTap() {
        if (isAttached()) {
            if (AnnotMenuStatus.status == AnnotMenuStatus.STATUS.LINK) {
                resetAnnotationState();
            } else {
                AnnotMenuStatus.status = AnnotMenuStatus.STATUS.LINK;
                getMvpView().changeButtonBackgroundColor(AnnotMenuStatus.STATUS.LINK);
                kmpdfFactory.setAnnotationEditMode(KMPDFAnnotationBean.AnnotationType.LINK);
            }
        }
    }

    /**
     * @methodName：resetAnnotationState created by liujiyuan on 2018/9/27 下午5:55.
     * @description：重置页面的状态，由编辑模式的转换浏览模式
     */
    @Override
    public void resetAnnotationState() {
        if (!isAttached()) {
            return;
        }
        AnnotMenuStatus.status = AnnotMenuStatus.STATUS.NULL;
        getMvpView().changeButtonBackgroundColor(AnnotMenuStatus.STATUS.NULL);
        kmpdfFactory.setAnnotationEditMode(KMPDFAnnotationBean.AnnotationType.NULL);
    }

    /**
     * @methodName：onLinkInitAttr created by liujiyuan on 2018/9/13 下午2:10.
     * @description：绘制了link区域后的
     */
    @Override
    public void onLinkInitAttr(OnLinkInfoChangeListener onLinkInfoChangeListener) {
        /****** 打开 编辑link注释 的popupWindow ******/
        PopupWindowUtil.getInstance().linkAnnotationPopupWindow.setOnInfoChangeListener(onLinkInfoChangeListener).show(OnLinkInfoChangeListener.KMPDFLinkType.WEBSITE);
    }

    @Override
    public void onLinkEditAttr(OnLinkInfoChangeListener onLinkInfoChangeListener, OnLinkInfoChangeListener.KMPDFLinkType kmpdfLinkType, String s) {
        PopupWindowUtil.getInstance().linkAnnotationPopupWindow.setOnInfoChangeListener(onLinkInfoChangeListener).setInitData(kmpdfLinkType, s).show(kmpdfLinkType);
    }

    /**
     * @methodName：getInkButtonPictures created by liujiyuan on 2NO_PARAM_OPEN_POPUPWINDOW18/8/31 下午4:31.
     * @description：得到ink图标的所有图片
     */
    private void getInkButtonPictures() {
        TypedArray ar = ProApplication.getContext().getResources().obtainTypedArray(R.array.annotation_ink_images);
        int len = ar.length();
        inkPictures = new Drawable[len];
        for (int i = NO_PARAM_OPEN_POPUPWINDOW; i < len; i++) {
            inkPictures[i] = ar.getDrawable(i);
        }
        ar.recycle();
    }

    @Override
    public Drawable returnInkButtonPicture() {
        if (inkPictures != null && inkPictures.length != NO_PARAM_OPEN_POPUPWINDOW) {
            return inkPictures[AnnotDefaultConfig.markerPenColorId_ink];
        } else {
            return null;
        }
    }

    @Subscribe
    public void onEventMainThread(MessageEvent<Object> event) {
        String tag = event.getTag();
        switch (tag) {
            case ConstantBus.SET_ANNOTATION_ATTR:
                kmpdfFactory.setAnnotationAttribute((KMPDFAnnotationBean) event.getEvent());
                break;
            case ConstantBus.SET_MARKUP_ANNOTATION_ATTR:
                setMarkupAnnotationAttribute(event);
                break;
            case ConstantBus.SET_STAMP_ANNOTATION_ATTR:
                if (event.getEvent() != null) {
                    kmpdfFactory.setAnnotationAttribute((KMPDFStampAnnotationBean) event.getEvent());
                    kmpdfFactory.setAnnotationEditMode(KMPDFAnnotationBean.AnnotationType.STAMP);
                }
                AnnotMenuStatus.status = AnnotMenuStatus.STATUS.NULL;
                getMvpView().changeButtonBackgroundColor(AnnotMenuStatus.STATUS.NULL);
                break;
            case ConstantBus.SET_SIGN_ANNOTATION_ATTR:
                if (event.getEvent() != null) {
                    kmpdfFactory.setAnnotationAttribute((KMPDFSignatureAnnotationBean) event.getEvent());
                    kmpdfFactory.setAnnotationEditMode(KMPDFAnnotationBean.AnnotationType.SIGNATURE);
                }
                AnnotMenuStatus.status = AnnotMenuStatus.STATUS.NULL;
                getMvpView().changeButtonBackgroundColor(AnnotMenuStatus.STATUS.NULL);
                break;
            default:
        }
    }

    /**
     * @methodName：setMarkupAnnotationAttribute created by liujiyuan on 2018/9/17 下午5:29.
     * @description：修改markup属性值的回调方法
     */
    private void setMarkupAnnotationAttribute(MessageEvent<Object> event) {
        switch (AnnotDefaultConfig.markerPenType) {
            case HIGH_LIGHT:
                KMPDFHighlightAnnotationBean kmpdfHighlightAnnotationBean = (KMPDFHighlightAnnotationBean) event.getEvent();
                kmpdfFactory.setAnnotationAttribute(kmpdfHighlightAnnotationBean);
                break;
            case UNDER_LINE:
                KMPDFUnderlineAnnotationBean kmpdfUnderlineAnnotationBean = (KMPDFUnderlineAnnotationBean) event.getEvent();
                kmpdfFactory.setAnnotationAttribute(kmpdfUnderlineAnnotationBean);
                break;
            case STRIK_EOUT:
                KMPDFStrikeoutAnnotationBean kmpdfStrikeoutAnnotationBean = (KMPDFStrikeoutAnnotationBean) event.getEvent();
                kmpdfFactory.setAnnotationAttribute(kmpdfStrikeoutAnnotationBean);
            case INK:
                KMPDFInkAnnotationBean kmpdfInkAnnotationBean = (KMPDFInkAnnotationBean) event.getEvent();
                kmpdfFactory.setAnnotationAttribute(kmpdfInkAnnotationBean);
                break;
            default:
        }
    }

    /**
     * @methodName：onTakeOrPickPhoto created by liujiyuan on 2018/9/25 下午5:16.
     * @description：获取打开图片窗口的回调
     */
    @Override
    public void onTakeOrPickPhoto(int requestCode) {
        if (!isAttached()) {
            return;
        }
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                /****** 指定调用相机拍照后的照片存储的路径 ******/
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                Date date = new Date(System.currentTimeMillis());
                takePhoto_tempPicPath = PathManager.getInstance().getPhotoFolderPath() + File.separator + simpleDateFormat.format(date) + ".jpeg";
                KMFileUtil.createFile(takePhoto_tempPicPath, true);
                CommonUtils.takePicture(getMvpView(), CommonUtils.onGetUriBySystem(context, new File(takePhoto_tempPicPath)), requestCode);
                break;
            case GALLERY_REQUEST_CODE:
                CommonUtils.openPic(getMvpView(), requestCode);
                break;
            default:
        }
    }

    /**
     * @methodName：getPictureUri created by liujiyuan on 2018/9/25 下午5:16.
     * @description：选取图片之后的回调,加入图片注释
     */
    public void getPictureUri(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        String file_absolutepath = "";
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                file_absolutepath = takePhoto_tempPicPath;
                break;
            case GALLERY_REQUEST_CODE:
                if (null != data) {
                    file_absolutepath = UriToPathUtil.getInstance().getPath(context, data.getData());
                }
                break;
            default:
        }
        if (!TextUtils.isEmpty(file_absolutepath)) {
            getMvpView().showProgressDialog(context.getResources().getString(R.string.loading_image), true, false);
            KMPDFStampAnnotationBean.ImageStamp imageStamp = new KMPDFStampAnnotationBean.ImageStamp(file_absolutepath, b -> {
                getMvpView().stopProgressDialog();
            });
            KMPDFAnnotationBean kmpdfStampAnnotationBean = new KMPDFStampAnnotationBean("", KMPDFStampAnnotationBean.StampType.IMAGE, imageStamp);
            kmpdfFactory.setAnnotationAttribute(kmpdfStampAnnotationBean);
            kmpdfFactory.setAnnotationEditMode(KMPDFAnnotationBean.AnnotationType.STAMP);
        }
    }

    @Override
    public void onDestroyPresenter() {
        EventBusUtils.getInstance().unRegister(this);
        super.onDestroyPresenter();
    }

}