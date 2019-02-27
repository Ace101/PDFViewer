package com.kdanmobile.pdfviewer.screenui.reader.configs;


/**
 * @classname：AnnotMenuStatus
 * @author：liujiyuan
 * @date：2018/8/20 下午5:16
 * @description：readerActivity界面菜单工具栏的状态
 */
public class AnnotMenuStatus {

    public enum STATUS{
        NULL, FREETEXT, SHAPE, LINK, INK, STAMP, HIGHLIGHT, UNDERLINE, STRIKEOUT, SIGN
    }

    public static STATUS status = STATUS.NULL;

}
