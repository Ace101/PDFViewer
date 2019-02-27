package com.kdanmobile.pdfviewer.screenui.common.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @classname：BaseViewHolder
 * @author：gongzuo
 * @date：2018/8/17 上午9:17
 * @description：
 */
public class BaseViewHolder extends RecyclerView.ViewHolder {

    private static volatile int existing = 0;
    private static int createdTimes = 0;

    public BaseViewHolder(View itemView) {
        super(itemView);
        createdTimes++;
        existing++;
    }

    @Override
    protected void finalize() throws Throwable {
        existing--;
        super.finalize();
    }
}