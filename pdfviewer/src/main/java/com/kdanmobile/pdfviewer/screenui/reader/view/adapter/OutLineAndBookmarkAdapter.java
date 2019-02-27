package com.kdanmobile.pdfviewer.screenui.reader.view.adapter;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.ViewGroup;

import com.kdanmobile.pdfviewer.R;
import com.kdanmobile.pdfviewer.base.ProApplication;
import com.kdanmobile.pdfviewer.screenui.reader.view.fragment.BookmarkFragment;
import com.kdanmobile.pdfviewer.screenui.reader.view.fragment.OutLineFragment;

/**
 * @classname：OutLineAndBookmarkAdapter
 * @author：liujiyuan
 * @date：2018/8/15 下午2:08
 * @description：
 */
public class OutLineAndBookmarkAdapter extends FragmentStatePagerAdapter {

    /**
     * 数据源
     */
    private String[] DATA = {
            ProApplication.getContext().getString(R.string.OUTLINE_PAGE),
            ProApplication.getContext().getString(R.string.BOOKMARK_PAGE)
    };
    private OutLineFragment outLineFragment;
    private BookmarkFragment bookmarkFragment;

    public OutLineAndBookmarkAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (outLineFragment == null) {
                    outLineFragment = OutLineFragment.newInstance();
                }
                return outLineFragment;
            case 1:
                if (bookmarkFragment == null) {
                    bookmarkFragment = BookmarkFragment.newInstance();
                }
                return bookmarkFragment;
        }
        return null;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public int getCount() {
        return DATA.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return DATA[position];
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        try {
            super.restoreState(state, loader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        if (position == 0 && outLineFragment != null) {
            outLineFragment.onDestroy();
        }

        if (position == 1 && bookmarkFragment != null) {
            bookmarkFragment.onDestroy();
        }
    }
}
