package com.kdanmobile.pdfviewer.screenui.reader.configs;


import com.kdanmobile.pdfviewer.screenui.reader.utils.KMReaderSpUtils;

/**
 * @classname：ReaderConfigs
 * @author：liujiyuan
 * @date：2018/9/6 下午8:16
 * @description：
 */
public class KMReaderConfigs {
    public final static int LANDSCAPE = 0X2022;
    public final static int PORTRAIT = 0X2024;

    public static int PAGE_TURN_INDEX = -1;
    public static String PAGE_TURN;
    public static float READER_BRIGHTNESS = 0.6f;
    public static boolean ISLOCKED = false;
    public static int ORIENTATION = LANDSCAPE;
    public static boolean IS_PORTRAIT = true;
    public static int readerPageCount = 0;

    public static void initReaderConfigs(){
        PAGE_TURN_INDEX = KMReaderSpUtils.getInstance().getPageTurnIndex();
        PAGE_TURN = KMReaderSpUtils.getInstance().getPageTurn();
        READER_BRIGHTNESS = KMReaderSpUtils.getInstance().getBrightness();
        ISLOCKED = KMReaderSpUtils.getInstance().getScreenLock();
        ORIENTATION = KMReaderSpUtils.getInstance().getReaderOrientation();
    }

    public static void saveReaderConfigs(){
        KMReaderSpUtils.getInstance().savePageTurnIndex(PAGE_TURN_INDEX);
        KMReaderSpUtils.getInstance().savePageTurn(PAGE_TURN);
        KMReaderSpUtils.getInstance().saveBrightness(READER_BRIGHTNESS);
        KMReaderSpUtils.getInstance().saveScreenLock(ISLOCKED);
        KMReaderSpUtils.getInstance().saveReaderOrientation(ORIENTATION);
    }
}
