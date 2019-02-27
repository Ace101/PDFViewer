package com.kdanmobile.pdfviewer.utils.dimens;

/**
 * @classname：DimenGenerator
 * @author：luozhipeng
 * @date：23/7/18 10:10
 * @description：
 */
public class DimenGenerator {

    /**
     * 设计稿尺寸(根据自己设计师的设计稿的宽度填入)
     */
    private static final int DESIGN_WIDTH = 360;

    /**
     * 设计稿高度  没用到
     */
    private static final int DESIGN_HEIGHT = 640;

    public static void main(String[] args) {

        DimenTypes[] values = DimenTypes.values();
        for (DimenTypes value : values) {
            MakeUtils.makeAll(DESIGN_WIDTH, value, "./app/src/main/res");
        }

    }
}
