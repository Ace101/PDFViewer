package com.kdanmobile.pdfviewer.screenui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.kdanmobile.pdfviewer.R;
import com.kdanmobile.pdfviewer.utils.ScreenUtil;

/**
 * @classname：RoundCornerImageView
 * @author：liujiyuan
 * @date：2018/8/2 下午4:35
 * @description：
 */
@SuppressLint("AppCompatCustomView")
public class RoundCornerImageView extends android.support.v7.widget.AppCompatImageView {
    private float cornerSize;//圆角大小
    private int color;
    private Context mContext;
    private Paint mPaint;

    public RoundCornerImageView(Context context){
        this(context,null);
        mContext = context;
    }

    public RoundCornerImageView(Context context, AttributeSet attrs){
        this(context,attrs,0);
        mContext = context;
    }

    public RoundCornerImageView(Context context, AttributeSet attrs,int defStyle){
        super(context,attrs,defStyle);
        mContext = context;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundCornerImageView,defStyle,0);
        cornerSize = a.getInt(R.styleable.RoundCornerImageView_corner_size,5);
        color = a.getColor(R.styleable.RoundCornerImageView_corner_color, ContextCompat.getColor(mContext, R.color.white_color));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int w = getWidth();
        int h = getHeight();
        mPaint.setColor(color);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(new RectF(0,0,w,h), ScreenUtil.dip2px(mContext, cornerSize), ScreenUtil.dip2px(mContext, cornerSize), mPaint);
        super.onDraw(canvas);
    }

    /**
     * 设置圆角的大小
     * @param size
     */
    public void setCornerSize(int size){
        cornerSize = size;
    }

    public void setColor(int size){
        color = size;
    }
}