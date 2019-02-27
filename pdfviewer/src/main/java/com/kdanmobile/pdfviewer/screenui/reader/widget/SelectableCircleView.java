package com.kdanmobile.pdfviewer.screenui.reader.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.kdanmobile.kmpdfkit.utlis.KMBitmapUtil;
import com.kdanmobile.pdfviewer.R;

/**
 * @classname：SelectableCircleView
 * @author：liujiyuan
 * @date：2018/8/17 下午6:20
 * @description：选择圆的自定义view
 */
public class SelectableCircleView extends View {

    Paint paint;

    Paint linePaint;

    public int color = 0xcccccc;

    /****** 边框的绘制颜色 ******/
    private int lineColor = Color.parseColor("#FF4444FF");

    private float lineWidht = 2.0f;

    private boolean mIsdrawRecr = false;

    private Context mContext;

    /****** 圆的半径 ******/
    private int radio;

    private Bitmap arrow;

    private int arrowWidth;

    private Paint bitmapPaint;

    public SelectableCircleView(Context context) {
        this(context, null);
    }

    public SelectableCircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectableCircleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        initView();
    }

    public void setIsDrawRecr(boolean mIsdrawRecr){
        this.mIsdrawRecr = mIsdrawRecr;
    }

    private void initView() {
        bitmapPaint = new Paint();
        bitmapPaint.setAntiAlias(true);
        paint = new Paint();

        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(lineWidht);
        linePaint.setColor(lineColor);
        linePaint.setStyle(Paint.Style.STROKE);

        arrowWidth = mContext.getResources().getDimensionPixelSize(R.dimen.qb_px_10);
        arrow = KMBitmapUtil.createBitmapFitRect(mContext, R.drawable.btn_bota_more, arrowWidth, arrowWidth);
        arrowWidth = arrow.getWidth();
    }

    public void setColor(int color){
        this.color = color;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);

        if(mIsdrawRecr){
            drawRect(canvas);
        }
    }

    private void drawBackground(Canvas canvas){
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        radio = width < height ? width/2 : height/2;
        radio -= arrowWidth * 0.75;

        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        canvas.save();
        canvas.drawCircle(width/2, (int)(height/2 + arrowWidth * 0.75), radio, paint);
        canvas.restore();
    }

    private void drawRect(Canvas canvas){
        canvas.save();
        canvas.drawBitmap(arrow, getWidth()/2 - arrowWidth/2, 0, bitmapPaint);
        canvas.restore();
    }

}
