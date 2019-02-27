package com.kdanmobile.pdfviewer.screenui.reader.view.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;

import com.kdanmobile.pdfviewer.R;
import com.kdanmobile.pdfviewer.base.BaseActivity;
import com.kdanmobile.pdfviewer.screenui.reader.configs.KMReaderConfigs;
import com.kdanmobile.pdfviewer.screenui.reader.utils.BrightnessUtil;
import com.kdanmobile.pdfviewer.screenui.reader.view.adapter.OutLineAndBookmarkAdapter;
import com.kdanmobile.pdfviewer.screenui.widget.NoScrollViewPager;

/**
 * @classname：OutLineAndBookmark
 * @author：liujiyuan
 * @date：2018/8/15 下午1:49
 * @description：
 */
public class OutLineAndBookmarkActivity extends BaseActivity {

    private TabLayout idOandBTabLayout;
    private NoScrollViewPager idOandBViewPager;
    private OutLineAndBookmarkAdapter pagerAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outline_bookmark);

        getSupportActionBar().setTitle(R.string.OUTLINE_AND_BOOKMARK_THEME);
        idOandBTabLayout = findViewById(R.id.id_OandB_tab_layout);
        idOandBViewPager = findViewById(R.id.id_OandB_view_pager);
        initViewPager();
    }

    private void initViewPager() {
        /****** 设置Tab的选择监听 ******/
        idOandBTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (idOandBViewPager != null) {
                    /****** 每当我们选择了一个Tab就将ViewPager滚动至对应的Page ******/
                    idOandBViewPager.setCurrentItem(tab.getPosition());
                    switch (tab.getPosition()) {
                        /****** OUTLINE ******/
                        case 0:
                            break;
                        /****** BOOKMARKS ******/
                        case 1:
                            break;
                        default:
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        pagerAdapter = new OutLineAndBookmarkAdapter(getSupportFragmentManager());
        idOandBViewPager.setOffscreenPageLimit(1);
        idOandBViewPager.setAdapter(pagerAdapter);
        idOandBViewPager.setNoScroll(true);
        /****** 设置从PagerAdapter中获取Tab ******/
        //tabLayout.setTabsFromPagerAdapter(pagerAdapter);
        idOandBTabLayout.setupWithViewPager(idOandBViewPager);
        /****** 构造一个TabLayoutOnPageChangeListener对象,设置ViewPager页面改变后的监听 ******/
        /****** 防止切换的时候tab中选项出现闪的情况 ******/
        idOandBViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(idOandBTabLayout));
    }

    @Override
    protected void onResume() {
        /****** 设置activity的亮度 ******/
        BrightnessUtil.setActivityBrightness(KMReaderConfigs.READER_BRIGHTNESS, this);
        /****** 设置activity的横竖位置 ******/
        if(KMReaderConfigs.ISLOCKED){
            if(KMReaderConfigs.ORIENTATION == KMReaderConfigs.PORTRAIT){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }else{
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
        super.onResume();
    }
}
