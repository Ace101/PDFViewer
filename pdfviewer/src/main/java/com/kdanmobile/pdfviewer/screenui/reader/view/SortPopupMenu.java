package com.kdanmobile.pdfviewer.screenui.reader.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import com.kdanmobile.pdfviewer.R;

import java.lang.reflect.Field;

/**
 * @classname：SortPopupMenu
 * @author：wangzhe
 * @date：2018/9/7 下午5:09
 * @description：
 */
public class SortPopupMenu extends PopupMenu implements PopupMenu.OnMenuItemClickListener {

    public SortPopupMenu(@NonNull Context context, @NonNull View anchor) {
        super(context, anchor);
        init();
    }


    public SortPopupMenu(@NonNull Context context, @NonNull View anchor, int gravity) {
        super(context, anchor, gravity);
        init();
    }

    public SortPopupMenu(@NonNull Context context, @NonNull View anchor, int gravity, int popupStyleAttr, int popupStyleRes) {
        super(context, anchor, gravity, popupStyleAttr, popupStyleRes);
        init();
    }

    private void init() {
        inflate(R.menu.local_file_sort_menu);
        setGravity(Gravity.TOP | Gravity.END);
        beforeShow();
    }

    @SuppressLint("RestrictedApi")
    private void beforeShow() {
        try {
            Field field = this.getClass().getDeclaredField("mPopup");
            field.setAccessible(true);
            MenuPopupHelper mHelper = (MenuPopupHelper) field.get(this);
            mHelper.setForceShowIcon(true);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }
}
