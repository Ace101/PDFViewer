package com.kdanmobile.pdfviewer.utils.dimens;

/**
 * @classname：DimenTypes
 * @author：luozhipeng
 * @date：23/7/18 10:08
 * @description：
 *
 * <dimen name="qb_px_1">2.00dp</dimen>
 * 表示 1px 对应 2dp
 */
public enum DimenTypes {

    //适配Android 4.4以上   大部分手机的sw值集中在  300-720之间 dp_px
    DP_sw__213(213),  // values-sw213
    DP_sw__320(320),  // values-sw320
    DP_sw__360(360),  // values-sw360
    DP_sw__384(384),  // values-sw384
    DP_sw__400(400),  // values-sw400
    DP_sw__420(420),  // values-sw420
    DP_sw__432(432),  // values-sw432
    DP_sw__480(480),  // values-sw480
    DP_sw__533(533),  // values-sw533
    DP_sw__560(560),  // values-sw560
    DP_sw__600(600),  // values-sw600
    DP_sw__720(720);  // values-sw720

    /**
     * 屏幕最小宽度
     */
    private int swWidthDp;


    DimenTypes(int swWidthDp) {

        this.swWidthDp = swWidthDp;
    }

    public int getSwWidthDp() {
        return swWidthDp;
    }

    public void setSwWidthDp(int swWidthDp) {
        this.swWidthDp = swWidthDp;
    }

}
