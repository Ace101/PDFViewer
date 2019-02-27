package com.kdanmobile.pdfviewer.screenui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.kdanmobile.pdfviewer.R;
import com.kdanmobile.pdfviewer.utils.ScreenUtil;

/**
 * @classname：EmptyLayout
 * @author：wangzhe
 * @date：2018/9/6 下午3:05
 * @description：
 */
public class EmptyLayout extends ConstraintLayout {
    private int textColor;
    private String text;
    private float textSize;
    private int icon;

    private AppCompatImageView idEmptyLayoutIcon;
    private AppCompatTextView idEmptyLayoutTittle;

    public EmptyLayout(@NonNull Context context) {
        this(context, null);
    }

    public EmptyLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmptyLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_empty_layout, this, true);

        getAttribute(context, attrs, defStyleAttr);

        initView();
    }

    public void setIcon(@DrawableRes int icon){
        idEmptyLayoutIcon.setImageResource(icon);
    }

    private void initView() {
        idEmptyLayoutIcon = findViewById(R.id.id_empty_layout_icon);
        idEmptyLayoutTittle = findViewById(R.id.id_empty_layout_tittle);

        idEmptyLayoutIcon.setImageResource(icon);
        idEmptyLayoutTittle.setText(text);
        idEmptyLayoutTittle.setTextColor(textColor);
        idEmptyLayoutTittle.setTextSize(textSize);
    }

    private void getAttribute(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.EmptyLayout);
        textColor = typedArray.getColor(R.styleable.EmptyLayout_empty_text_color, ContextCompat.getColor(context, R.color.second_title_text_color));
        text = typedArray.getString(R.styleable.EmptyLayout_empty_text);
        textSize = typedArray.getDimension(R.styleable.EmptyLayout_empty_text_size, 0);
        if (textSize != 0) {
            textSize = ScreenUtil.px2dip(context, textSize);
        } else {
            textSize = 18f;
        }
        icon = typedArray.getResourceId(R.styleable.EmptyLayout_empty_icon, R.drawable.ic_collection_dis);
        typedArray.recycle();
    }
}
