package com.kdanmobile.pdfviewer.screenui.reader.view.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.kdanmobile.pdfviewer.R;
import com.kdanmobile.pdfviewer.base.mvp.factory.CreatePresenter;
import com.kdanmobile.pdfviewer.base.mvp.view.AbstractMvpAppCompatActivity;
import com.kdanmobile.pdfviewer.event.MessageEvent;
import com.kdanmobile.pdfviewer.screenui.reader.configs.KMReaderConfigs;
import com.kdanmobile.pdfviewer.screenui.reader.constract.StampAnnotConstract;
import com.kdanmobile.pdfviewer.screenui.reader.presenter.StampAnnotPresenter;
import com.kdanmobile.pdfviewer.screenui.reader.utils.BrightnessUtil;
import com.kdanmobile.pdfviewer.screenui.reader.utils.PopupWindowUtil;
import com.kdanmobile.pdfviewer.screenui.reader.view.adapter.ImageStampRecycleViewAdapter;
import com.kdanmobile.pdfviewer.screenui.reader.view.adapter.StandardStampRecycleViewAdapter;
import com.kdanmobile.pdfviewer.screenui.reader.view.adapter.TextStampRecycleViewAdapter;
import com.kdanmobile.pdfviewer.utils.AnimationUtil;
import com.kdanmobile.pdfviewer.utils.GlobalConfigs;
import com.kdanmobile.pdfviewer.utils.SimpleTabSelectedListener;
import com.kdanmobile.pdfviewer.utils.eventbus.ConstantBus;
import com.kdanmobile.pdfviewer.utils.eventbus.EventBusUtils;

/**
 * @classname：StampAnnotActivity
 * @author：liujiyuan
 * @date：2018/9/3 下午6:00
 * @description：stamp注释界面
 */
@CreatePresenter(StampAnnotPresenter.class)
public class StampAnnotActivity extends AbstractMvpAppCompatActivity<StampAnnotActivity, StampAnnotPresenter> implements StampAnnotConstract.IView, View.OnClickListener {
    private final static int GRID_LAYOUT_COLUMNS = 2;
    public boolean isDeleteStamp = false;
    private boolean isAddStamp = false;
    private boolean isStandardPage = true;
    private ImageButton idStampMenuBack;
    private TabLayout idStampMenuTabLayout;
    private ImageButton idStampMenuEdit;
    private RecyclerView idStampStandardRecycleView;
    private RelativeLayout idStampDis;
    private RelativeLayout idStampListShow;
    private AppCompatTextView idStampAddText;
    private AppCompatTextView idStampAddImage;
    private RecyclerView idStampAddTextRecycleView;
    private RecyclerView idStampAddImageRecycleView;
    private RelativeLayout idStampFloatingButtonLayout;
    private RelativeLayout idStampFloatingButtonAddMenu;
    private FloatingActionButton idStampFloatingButtonImage;
    private FloatingActionButton idStampFloatingButtonText;
    private FloatingActionButton idStampFloatingButtonAdd;
    private ImageButton idStampMenuDelete;

    private StandardStampRecycleViewAdapter standardStampRecycleViewAdapter;
    private TextStampRecycleViewAdapter textStampRecycleViewAdapter;
    private ImageStampRecycleViewAdapter imageStampRecycleViewAdapter;
    private Drawable menuEditDrawable;
    private Drawable noMenuEditDrawable;
    private boolean isTextEmpty = true;
    private boolean isImageEmpty = true;

    @Override
    public int onLayoutId() {
        return R.layout.activity_stamp_annotation;
    }

    @Override
    public boolean onInitView() {

        idStampMenuBack = findViewById(R.id.id_stamp_menu_back);
        idStampMenuTabLayout = findViewById(R.id.id_stamp_menu_tabLayout);
        idStampMenuEdit = findViewById(R.id.id_stamp_menu_edit);
        idStampMenuDelete = findViewById(R.id.id_stamp_menu_delete);

        idStampStandardRecycleView = findViewById(R.id.id_stamp_standard_recycleView);

        idStampDis = findViewById(R.id.id_stamp_dis);

        idStampListShow = findViewById(R.id.id_stamp_list_show);
        idStampAddTextRecycleView = findViewById(R.id.id_stamp_add_text_recycleView);
        idStampAddImageRecycleView = findViewById(R.id.id_stamp_add_image_recycleView);
        idStampAddText = findViewById(R.id.id_stamp_add_text);
        idStampAddImage = findViewById(R.id.id_stamp_add_image);

        idStampFloatingButtonLayout = findViewById(R.id.id_stamp_floating_button_layout);
        idStampFloatingButtonAddMenu = findViewById(R.id.id_stamp_floating_button_add_menu);
        idStampFloatingButtonImage = findViewById(R.id.id_stamp_floating_button_image);
        idStampFloatingButtonText = findViewById(R.id.id_stamp_floating_button_text);
        idStampFloatingButtonAdd = findViewById(R.id.id_stamp_floating_button_add);
        initResource();
        initListener();
        return true;
    }

    private void initListener() {
        /****** 设置tab点击事件 ******/
        idStampMenuTabLayout.setOnClickListener(this);
        idStampMenuTabLayout.addOnTabSelectedListener(new SimpleTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                isAddStamp = false;
                switch (tab.getPosition()) {
                    case 0:
                        isStandardPage = true;
                        hideCustomScreen();
                        break;
                    case 1:
                        isStandardPage = false;
                        idStampMenuEdit.setVisibility(View.VISIBLE);
                        idStampStandardRecycleView.setVisibility(View.GONE);
                        idStampDis.setVisibility(View.GONE);
                        idStampListShow.setVisibility(View.VISIBLE);
                        idStampFloatingButtonLayout.setVisibility(View.VISIBLE);
                        changeEditImage();
                        changeFloatingAddImage(isAddStamp);
                        setDisStampData();
                        break;
                    default:
                }
            }
        });
        /****** 按钮的点击事件 ******/
        idStampFloatingButtonAdd.setOnClickListener(this);
        idStampFloatingButtonText.setOnClickListener(this);
        idStampFloatingButtonImage.setOnClickListener(this);
        idStampMenuEdit.setOnClickListener(this);
        idStampMenuBack.setOnClickListener(this);
        idStampFloatingButtonLayout.setOnClickListener(this);
        idStampMenuDelete.setOnClickListener(this);
    }

    private void initResource() {
        /****** 给tab设置标题 ******/
        String[] tablayout_title = new String[2];
        tablayout_title[0] = getString(R.string.stamp_tab_standard);
        tablayout_title[1] = getString(R.string.stamp_tab_custom);
        for (int i = 0; i < tablayout_title.length; i++) {
            idStampMenuTabLayout.addTab(idStampMenuTabLayout.newTab().setText(tablayout_title[i]));
        }
        /****** 设置各个view的可见度 ******/
        hideCustomScreen();
        /****** 给悬浮按钮设置主题色 ******/
        idStampFloatingButtonAdd.getBackground().setColorFilter(GlobalConfigs.THEME_COLOR, PorterDuff.Mode.SRC);
        /****** 菜单栏的图标资源初始化 ******/
        menuEditDrawable = ContextCompat.getDrawable(this, R.drawable.ic_edit_white);
        noMenuEditDrawable  = ContextCompat.getDrawable(this, R.drawable.ic_edit_dis);
    }

    @Override
    public void setAdapter() {
        /****** 设置标准stamp的adapter ******/
        this.standardStampRecycleViewAdapter = getMvpPresenter().standardStampRecycleViewAdapter;
        idStampStandardRecycleView.setLayoutManager(new GridLayoutManager(this, GRID_LAYOUT_COLUMNS));
        idStampStandardRecycleView.setItemAnimator(new DefaultItemAnimator());
        idStampStandardRecycleView.setAdapter(standardStampRecycleViewAdapter);
        /****** 设置自定义text stamp的adapter ******/
        this.textStampRecycleViewAdapter = getMvpPresenter().textStampRecycleViewAdapter;
        idStampAddTextRecycleView.setLayoutManager(new GridLayoutManager(this, GRID_LAYOUT_COLUMNS));
        idStampAddTextRecycleView.setItemAnimator(new DefaultItemAnimator());
        idStampAddTextRecycleView.setAdapter(textStampRecycleViewAdapter);
        /****** 解决 ScrollView嵌套RecyclerView 滑动不流畅问题 ******/
        idStampAddTextRecycleView.setNestedScrollingEnabled(false);
        /****** 设置自定义image stamp的adapter ******/
        this.imageStampRecycleViewAdapter = getMvpPresenter().imageStampRecycleViewAdapter;
        idStampAddImageRecycleView.setLayoutManager(new GridLayoutManager(this, GRID_LAYOUT_COLUMNS));
        idStampAddImageRecycleView.setItemAnimator(new DefaultItemAnimator());
        idStampAddImageRecycleView.setAdapter(imageStampRecycleViewAdapter);
        /****** 解决 ScrollView嵌套RecyclerView 滑动不流畅问题 ******/
        idStampAddImageRecycleView.setNestedScrollingEnabled(false);
        /****** 获取两个stamp的空状态 ******/
        isTextEmpty = textStampRecycleViewAdapter.getData() == null || textStampRecycleViewAdapter.getData().isEmpty();
        isImageEmpty = imageStampRecycleViewAdapter.getData() == null || imageStampRecycleViewAdapter.getData().isEmpty();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_stamp_menu_edit:
                isDeleteStamp = true;
                changeEditImage();
                getMvpPresenter().editStampData();
                break;
            case R.id.id_stamp_menu_delete:
                getMvpPresenter().deleteStampData(true);
                break;
            case R.id.id_stamp_floating_button_add:
                isAddStamp = !isAddStamp;
                changeFloatingAddImage(isAddStamp);
                break;
            case R.id.id_stamp_floating_button_layout:
                changeFloatingAddImage(false);
                break;
            case R.id.id_stamp_floating_button_text:
                startActivity(new Intent(StampAnnotActivity.this, TextStampCreateActivity.class));
                break;
            case R.id.id_stamp_floating_button_image:
                /****** 打开选择图片弹框 ******/
                PopupWindowUtil.getInstance().selectPicturePopupWindow.show(0);
                break;
            case R.id.id_stamp_menu_back:
                onBackPressed();
                break;
            default:
        }
    }

    /**
     * @methodName：hideCustomScreen created by liujiyuan on 2018/9/4 上午9:28.
     * @description：切换到STANDARD界面，隐藏CUSTOM界面的所有view
     */
    private void hideCustomScreen() {
        idStampStandardRecycleView.setVisibility(View.VISIBLE);
        idStampDis.setVisibility(View.GONE);
        idStampMenuEdit.setVisibility(View.GONE);
        idStampMenuDelete.setVisibility(View.GONE);
        idStampListShow.setVisibility(View.GONE);
        idStampFloatingButtonLayout.setVisibility(View.GONE);
    }

    /**
     * @methodName：changeEditImage created by liujiyuan on 2018/9/19 上午10:24.
     * @description：改变菜单栏图标
     */
    private void changeEditImage() {
        if(isTextEmpty && isImageEmpty){
            idStampMenuEdit.setImageDrawable(noMenuEditDrawable);
            idStampMenuEdit.setClickable(false);
        }else{
            idStampMenuEdit.setImageDrawable(menuEditDrawable);
            idStampMenuEdit.setClickable(true);
        }
        if(isDeleteStamp){
            idStampFloatingButtonAdd.setVisibility(View.GONE);
            idStampMenuEdit.setVisibility(View.GONE);
            idStampMenuDelete.setVisibility(View.VISIBLE);
            idStampMenuTabLayout.setVisibility(View.GONE);
        }else{
            idStampFloatingButtonAdd.setVisibility(View.VISIBLE);
            idStampMenuEdit.setVisibility(View.VISIBLE);
            idStampMenuDelete.setVisibility(View.GONE);
            idStampMenuTabLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * @methodName：changeFloationgAddImage created by liujiyuan on 2018/9/3 下午9:49.
     * @description：设置悬浮按钮的可见度
     */
    @Override
    public void changeFloatingAddImage(boolean isAddStamp) {
        this.isAddStamp = isAddStamp;
        AnimationUtil.rotateFloatingButton(!isAddStamp, idStampFloatingButtonAdd);
        if (isAddStamp) {
            idStampFloatingButtonAddMenu.setVisibility(View.VISIBLE);
            idStampFloatingButtonLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.floating_button_background));
            idStampFloatingButtonLayout.setClickable(true);
        } else {
            idStampFloatingButtonAddMenu.setVisibility(View.GONE);
            idStampFloatingButtonLayout.setBackgroundColor(Color.TRANSPARENT);
            idStampFloatingButtonLayout.setClickable(false);
        }
    }

    /**
     * @methodName：setDisStamData created by liujiyuan on 2018/9/6 上午10:13.
     * @description：根据stamp的数量来设置显示
     */
    @Override
    public void setDisStampData() {
        if (! isStandardPage) {
            isTextEmpty = textStampRecycleViewAdapter.getData() == null || textStampRecycleViewAdapter.getData().isEmpty();
            isImageEmpty = imageStampRecycleViewAdapter.getData() == null || imageStampRecycleViewAdapter.getData().isEmpty();
            changeEditImage();
            if (isTextEmpty && isImageEmpty) {
                idStampListShow.setVisibility(View.GONE);
                idStampDis.setVisibility(View.VISIBLE);
                backToViewState();
            } else if (isTextEmpty) {
                idStampListShow.setVisibility(View.VISIBLE);
                idStampDis.setVisibility(View.GONE);
                idStampAddText.setVisibility(View.GONE);
                idStampAddImage.setVisibility(View.VISIBLE);
            } else if (isImageEmpty) {
                idStampListShow.setVisibility(View.VISIBLE);
                idStampDis.setVisibility(View.GONE);
                idStampAddText.setVisibility(View.VISIBLE);
                idStampAddImage.setVisibility(View.GONE);
            } else {
                idStampListShow.setVisibility(View.VISIBLE);
                idStampDis.setVisibility(View.GONE);
                idStampAddText.setVisibility(View.VISIBLE);
                idStampAddImage.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (getMvpPresenter() != null) {
            getMvpPresenter().getPictureUri(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
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
        /****** 初始化 PopupWindowUtil 工具类 ******/
        PopupWindowUtil.finishUtil();
        PopupWindowUtil.initUtil(this, idStampListShow);
        /****** 设置 OnTakeOrPickPhotoCallback 接口，并打开popupwindow ******/
        PopupWindowUtil.buildPopupWindow(PopupWindowUtil.PopupWindowType.TAKE_OR_PICK_PHOTO).setCallback(getMvpPresenter());
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        boolean isBackToView = (isDeleteStamp || isAddStamp) && !isStandardPage;
        if (isBackToView) {
            backToViewState();
        } else {
            EventBusUtils.getInstance().post(new MessageEvent<>(ConstantBus.SET_STAMP_ANNOTATION_ATTR, null));
            super.onBackPressed();
        }
    }

    /**
     * @methodName：backToViewState created by liujiyuan on 2018/9/30 下午1:55.
     * @description：返回最初的状态
     */
    private void backToViewState() {
        isDeleteStamp = false;
        isAddStamp = false;
        changeEditImage();
        changeFloatingAddImage(isAddStamp);
        /****** 两个stamp列表变为浏览模式 ******/
        textStampRecycleViewAdapter.setMode(false);
        textStampRecycleViewAdapter.notifyItemRangeChanged(0, textStampRecycleViewAdapter.getItemCount(), "checkbox_hide");
        imageStampRecycleViewAdapter.setMode(false);
        imageStampRecycleViewAdapter.notifyItemRangeChanged(0, imageStampRecycleViewAdapter.getItemCount(), "checkbox_hide");
    }

    @Override
    public void onStop() {
        if (null != getMvpPresenter()) {
            getMvpPresenter().onStop(isFinishing());
        }
        super.onStop();
    }
}
