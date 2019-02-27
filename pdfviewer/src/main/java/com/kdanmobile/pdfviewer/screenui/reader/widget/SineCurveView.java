package com.kdanmobile.pdfviewer.screenui.reader.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

/**
 * @classname：SineCurveView
 * @author：liujiyuan
 * @date：2018/8/17 下午5:40
 * @description：正玄曲线view
 */
public class SineCurveView extends View {

    private int lineColor = Color.RED;

    private int lineAlpha = 255;

    private float lineWidth = 20f;

    private Paint paint;

    private float sineCurveHeight = 70f;
    public SineCurveView(Context context) {
        super(context);
        initPaint();
    }

    public SineCurveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public SineCurveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SineCurveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initPaint();
    }
    private void initPaint() {
        paint = new Paint();
        paint.setColor(lineColor);
        paint.setAlpha(lineAlpha);
        paint.setStrokeWidth(lineWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
    }

    /**
     * @param ：[lineColor]
     * @return : void
     * @methodName ：setLineColor created by liujiyuan on 2018/8/17 下午5:44.
     * @description ：设置画笔的颜色
     */
    public void setLineColor(int lineColor){
        this.lineColor = lineColor;
        paint.setColor(lineColor);
    }

    /**
     * @param ：[lineWidth]
     * @return : void
     * @methodName ：setLineWidth created by liujiyuan on 2018/8/17 下午5:44.
     * @description ：设置画笔的大小
     */
    public void setLineWidth(float lineWidth){
        this.lineWidth = lineWidth;
        paint.setStrokeWidth(lineWidth);
    }

    /**
     * @param ：[lineAlpha]
     * @return : void
     * @methodName ：setLineAlpha created by liujiyuan on 2018/8/17 下午5:44.
     * @description ：设置画笔的透明度
     */
    public void setLineAlpha(int lineAlpha){
        this.lineAlpha = lineAlpha;
        paint.setAlpha(lineAlpha);
    }

    /**
     * @param ：[]
     * @return : int
     * @methodName ：getLineColor created by liujiyuan on 2018/8/17 下午5:43.
     * @description ：获取画笔的颜色
     */
    public int getLineColor() {
        return lineColor;
    }

    /**
     * @param ：[]
     * @return : int
     * @methodName ：getLineAlpha created by liujiyuan on 2018/8/17 下午5:43.
     * @description ：获取画笔的透明度
     */
    public int getLineAlpha() {
        return lineAlpha;
    }

    /**
     * @param ：[]
     * @return : float
     * @methodName ：getLineWidth created by liujiyuan on 2018/8/17 下午5:43.
     * @description ：获取画笔的大小
     */
    public float getLineWidth() {
        return lineWidth;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        Path path = new Path();
        /****** 起始位置 ******/
        path.moveTo(0, getHeight()/2);
        /****** rQuardto的位置是相对的 ******/
        path.rQuadTo(getWidth()/4, -sineCurveHeight, getWidth()/2, 0);
        path.rQuadTo(getWidth()/4, sineCurveHeight, getWidth()/2, 0);
        canvas.drawPath(path, paint);
    }
}
