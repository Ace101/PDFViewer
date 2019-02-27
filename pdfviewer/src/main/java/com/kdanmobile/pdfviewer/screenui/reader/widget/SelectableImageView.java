package com.kdanmobile.pdfviewer.screenui.reader.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.kdanmobile.kmpdfkit.utlis.KMBitmapUtil;
import com.kdanmobile.pdfviewer.R;
import com.kdanmobile.pdfviewer.screenui.reader.configs.AnnotDefaultConfig;

/**
 * @classname：SelectableImageView
 * @author：liujiyuan
 * @date：2018/8/30 下午6:29
 * @description：形状注释的选择自定义View
 */
public class SelectableImageView extends View {


    private boolean isDrawRect = false;

    private AnnotDefaultConfig.ShapeAnnotationType type;

    private Context mContext;

    private Bitmap arrow;

    private Bitmap src;

    private int arrowWidth;

    private int srcId;

    private int currentSrcHeight;

    private int currentSrcWidth;

    private int srcHeight;

    private int srcWidth;

    private Paint bitmapPaint;

    public SelectableImageView(Context context) {
        this(context, null);
    }

    public SelectableImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectableImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        getAttrs(context, attrs);
        this.mContext = context;
        initView();
    }

    public AnnotDefaultConfig.ShapeAnnotationType getType() {
        return type;
    }

    public void setType(AnnotDefaultConfig.ShapeAnnotationType type) {
        this.type = type;
    }

    private void initView(){
        bitmapPaint = new Paint();

        arrowWidth = mContext.getResources().getDimensionPixelSize(R.dimen.qb_px_10);
        arrow = KMBitmapUtil.createBitmapFitRect(mContext, R.drawable.btn_bota_more, arrowWidth, arrowWidth);
        arrowWidth = arrow.getWidth();

        src = KMBitmapUtil.createBitmapFitRect(mContext, srcId, srcWidth, Math.abs(srcHeight - arrowWidth * 1.5f));
        currentSrcWidth = src.getWidth();
        currentSrcHeight = src.getHeight();
    }

    /**
     * 得到属性值
     * @param context
     * @param attrs
     */
    private void getAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SelectableImageView);
        srcId = ta.getResourceId(R.styleable.SelectableImageView_android_src, R.drawable.rectangle_pic_rectangle);
        srcWidth = ta.getLayoutDimension(R.styleable.SelectableImageView_android_layout_width, 100);
        srcHeight = ta.getLayoutDimension(R.styleable.SelectableImageView_android_layout_height, 100);
        ta.recycle();
    }

    public void setIsDrawRect(boolean isDrawRect){
        this.isDrawRect = isDrawRect;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(src, (srcWidth - currentSrcWidth)/2, (int)(arrowWidth * 1.5f), bitmapPaint);
        if(isDrawRect){
            canvas.save();
            canvas.drawBitmap(arrow, srcWidth/2 - arrowWidth/2, 0, bitmapPaint);
            canvas.restore();
        }
    }
}
