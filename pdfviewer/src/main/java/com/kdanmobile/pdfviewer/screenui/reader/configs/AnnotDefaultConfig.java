package com.kdanmobile.pdfviewer.screenui.reader.configs;

import android.graphics.Color;
import android.graphics.Typeface;

import com.kdanmobile.kmpdfkit.utlis.KMPDFFontUtil;

/**
 * @classname：AnnotDefaultConfig
 * @author：liujiyuan
 * @date：2018/8/20 上午10:49
 * @description：popupWindow 的属性保存类
 */
public class AnnotDefaultConfig {

    public enum ShapeAnnotationType {
        SQUARE, CIRCLE, LINE, ARROW
    }

    public enum MarkerPenType{
        NULL, HIGH_LIGHT, UNDER_LINE, STRIK_EOUT, INK
    }

    public enum DrawType{
        NORMAL_PEN, MARKER_PEN
    }

    public static int[] colorArr = new int[]{
            Color.parseColor("#DD2C00"),
            Color.parseColor("#E140FB"),
            Color.parseColor("#FF80AB"),
            Color.parseColor("#3FC4FF"),
            Color.parseColor("#45CC4D"),
            Color.parseColor("#FFCE01"),
            Color.parseColor("#FFAB00"),
            Color.parseColor("#F25B0A"),
            Color.parseColor("#B300EE"),
            Color.parseColor("#2C8831"),
            Color.parseColor("#005B8D"),
            Color.parseColor("#FFFFFF"),
            Color.parseColor("#333333")
    };

    public static int[] SignColorArr = new int[]{
            Color.parseColor("#333333"),
            Color.parseColor("#005B8D"),
            Color.parseColor("#FFFFFF"),
            Color.parseColor("#DD2C00"),
            Color.parseColor("#E140FB"),
            Color.parseColor("#FF80AB"),
            Color.parseColor("#3FC4FF"),
            Color.parseColor("#45CC4D"),
            Color.parseColor("#FFCE01"),
            Color.parseColor("#FFAB00"),
            Color.parseColor("#F25B0A"),
            Color.parseColor("#B300EE"),
            Color.parseColor("#2C8831")

    };

    /***********************Shape**************************************/
    public static AnnotDefaultConfig.ShapeAnnotationType shapeType = AnnotDefaultConfig.ShapeAnnotationType.SQUARE;

    public static int shapeAnnotLineColor = colorArr[0];

    public static int shapeAnnotLineAlpha = 255;

    public static int shapeAnnotFillColor = colorArr[0];

    public static int shapeAnnotFillColorId = 0;

    public static int shapeAnnotLineColorId = 0;

    public static int shapeAnnotFillAlpha = 0;

    public static float shapeAnnotLineWidth = 4.0f;

    public static String shapeAnnotContent = "";
    /*************************Shape end************************************/

    /***********************FreeTextStruct**************************************/
    public static boolean isBold_freeText = false;

    public static boolean isItalic_freeText = false;

    public static String typeface_freeText = KMPDFFontUtil.Font_Favorite_Type;

    public static Typeface typeface;

    public static int alpha__freeText = 255;

    public static int color__freeText = colorArr[0];

    public static int colorId_freeText = 0;

    public static int textSize__freeText = 12;

    /***********************FreeTextStruct end**************************************/

    /***********************Marker**************************************/
    public static AnnotDefaultConfig.MarkerPenType markerPenType = AnnotDefaultConfig.MarkerPenType.INK;

    public static AnnotDefaultConfig.DrawType drawType = AnnotDefaultConfig.DrawType.NORMAL_PEN;

    public static int markerPenColor_hightlight = colorArr[5];

    public static int markerPenAlpha_hightlight = 100;

    public static int markerPenColor_underline = colorArr[3];

    public static int markerPenAlpha_underline = 255;

    public static int markerPenColor_strikeout = colorArr[0];

    public static int markerPenAlpha_strikeout = 255;

    public static int markerPenColor_ink = colorArr[1];

    public static int markerPenAlpha_ink = 255;

    public static int markerPenSize_ink = 10;

    public static int markerPenColorId_ink = 1;

    public static int markerPenColorId_highlight = 5;

    public static int markerPenColorId_underline = 3;

    public static int markerPenColorId_strikeout = 0;
    /*************************Marker end************************************/

    /*********************** SignEdit **************************************/
    public static int SignEdit_ColorId = 0;

    public static int SignEdit_PenColor = SignColorArr[0];

    public static int SignEdit_PenSize = 10;
    /*********************** SignEdit end **************************************/
}

