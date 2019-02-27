package com.kdanmobile.pdfviewer.screenui.reader.utils;

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

/**
 * @classname：InitKMPDFControllerUtil
 * @author：liujiyuan
 * @date：2018/8/22 上午11:35
 * @description：初始化库中的各个注释控制器
 */
public class InitKMPDFControllerUtil {
    private KMPDFFactory kmpdfFactory;
    private KMPDFMarkupController kmpdfMarkupController;
    private KMPDFInkController kmpdfInkController;
    private KMPDFDocumentController kmpdfDocumentController;
    private KMPDFShapeAnnotController kmpdfShapeAnnotController;
    private KMPDFFreeTextController kmpdfFreeTextController;
    private KMPDFStampController kmpdfStampController;
    private KMPDFSignatureController kmpdfSignatureController;
    private KMPDFLinkController kmpdfLinkController;
    private KMPDFLongPressCreateAnnotController kmpdfLongPressCreateAnnotController;

    private static class SingleTon {
        private static InitKMPDFControllerUtil instance = new InitKMPDFControllerUtil();
    }

    public static InitKMPDFControllerUtil getInstance() {
        return SingleTon.instance;
    }

    public void initControllers(KMPDFFactory kmpdfFactory) {
        this.kmpdfFactory = kmpdfFactory;
        kmpdfInkController = (KMPDFInkController) kmpdfFactory.getController(KMPDFFactory.ControllerType.INK);
        kmpdfMarkupController = (KMPDFMarkupController) kmpdfFactory.getController(KMPDFFactory.ControllerType.MARKER_UP);
        kmpdfDocumentController = (KMPDFDocumentController) kmpdfFactory.getController(KMPDFFactory.ControllerType.DOCUMENT);
        kmpdfShapeAnnotController = (KMPDFShapeAnnotController) kmpdfFactory.getController(KMPDFFactory.ControllerType.SHAPE);
        kmpdfFreeTextController = (KMPDFFreeTextController)kmpdfFactory.getController(KMPDFFactory.ControllerType.FREETEXT);
        kmpdfStampController = (KMPDFStampController)kmpdfFactory.getController(KMPDFFactory.ControllerType.STAMP);
        kmpdfSignatureController = (KMPDFSignatureController)kmpdfFactory.getController(KMPDFFactory.ControllerType.SIGNATURE);
        kmpdfLinkController = (KMPDFLinkController)kmpdfFactory.getController(KMPDFFactory.ControllerType.LINK);
        kmpdfLongPressCreateAnnotController = (KMPDFLongPressCreateAnnotController)kmpdfFactory.getController(KMPDFFactory.ControllerType.LONGCLICK);
    }

    public KMPDFMarkupController getKmpdfMarkupController() {
        return kmpdfMarkupController;
    }

    public KMPDFInkController getKmpdfInkController() {
        return kmpdfInkController;
    }

    public KMPDFDocumentController getKmpdfDocumentController() {
        return kmpdfDocumentController;
    }

    public KMPDFFactory getKmpdfFactory() {
        return kmpdfFactory;
    }

    public KMPDFShapeAnnotController getKmpdfShapeAnnotController() {
        return kmpdfShapeAnnotController;
    }

    public KMPDFFreeTextController getKmpdfFreeTextController() {
        return kmpdfFreeTextController;
    }

    public KMPDFStampController getKmpdfStampController() {
        return kmpdfStampController;
    }

    public KMPDFSignatureController getKmpdfSignatureController() {
        return kmpdfSignatureController;
    }

    public KMPDFLinkController getKmpdfLinkController() {
        return kmpdfLinkController;
    }

    public KMPDFLongPressCreateAnnotController getKmpdfLongPressCreateAnnotController() {
        return kmpdfLongPressCreateAnnotController;
    }

    public static void onRelease() {
        SingleTon.instance.kmpdfFactory = null;
        SingleTon.instance.kmpdfInkController = null;
        SingleTon.instance.kmpdfMarkupController = null;
        SingleTon.instance.kmpdfDocumentController = null;
        SingleTon.instance.kmpdfShapeAnnotController = null;;
        SingleTon.instance.kmpdfFreeTextController = null;;
        SingleTon.instance.kmpdfStampController = null;;
        SingleTon.instance.kmpdfSignatureController = null;;
    }
}
