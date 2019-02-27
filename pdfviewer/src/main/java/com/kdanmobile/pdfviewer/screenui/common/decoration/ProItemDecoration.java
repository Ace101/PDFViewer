package com.kdanmobile.pdfviewer.screenui.common.decoration;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.kdanmobile.pdfviewer.R;
import com.kdanmobile.pdfviewer.base.ProApplication;
import com.kdanmobile.pdfviewer.utils.ScreenUtil;

/**
 * @classname：ProItemDecoration
 * @author：gongzuo
 * @date：2018/8/16 下午3:53
 * @description：
 */
public class ProItemDecoration extends RecyclerView.ItemDecoration {
    private final float lineHeight;
    private boolean showLastOne = true;

    public ProItemDecoration() {
        this.lineHeight = ScreenUtil.dip2px(ProApplication.getContext(), 1);
    }

    public ProItemDecoration(boolean showLastLine) {
        this.showLastOne = showLastLine;
        this.lineHeight = ScreenUtil.dip2px(ProApplication.getContext(), 1);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(ProApplication.getContext(), R.color.color_setting_item_line));
        float leftMargin = ProApplication.getContext().getResources().getDimension(R.dimen.qb_px_16);
        int left;
        int right;
        int top;
        int bottom;
        left = (int) (parent.getPaddingLeft() + leftMargin);
        right = parent.getWidth() - parent.getPaddingRight();
        final int lineCount = showLastOne ? parent.getChildCount() : parent.getChildCount() - 1;
        for (int i = 0; i < lineCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params =
                    (RecyclerView.LayoutParams) child.getLayoutParams();
            top = child.getBottom() + params.bottomMargin;
            bottom = (int) (top + lineHeight);
            colorDrawable.setBounds(left, top, right, bottom);
            colorDrawable.draw(c);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(0, 0, 0, (int) lineHeight);
    }
}
