package com.kdanmobile.pdfviewer.screenui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * @author Aidan Follestad (afollestad)
 */
public class AccentHeaderView extends RelativeLayout {

    public AccentHeaderView(Context context) {
        super(context);
        init(context);
    }

    public AccentHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AccentHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AccentHeaderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        setTag("bg_accent_color");
        getBackground().setAlpha(76);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setAlpha(0.3f);
    }
}
