package com.kdanmobile.pdfviewer.screenui.reader.view.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;

import com.kdanmobile.pdfviewer.R;
import com.kdanmobile.pdfviewer.base.mvp.factory.CreatePresenter;
import com.kdanmobile.pdfviewer.base.mvp.view.AbstractMvpAppCompatActivity;
import com.kdanmobile.pdfviewer.event.MessageEvent;
import com.kdanmobile.pdfviewer.screenui.reader.configs.KMReaderConfigs;
import com.kdanmobile.pdfviewer.screenui.reader.constract.ProReaderSettingMoreConstract;
import com.kdanmobile.pdfviewer.screenui.reader.presenter.ProReaderSettingMorePresenter;
import com.kdanmobile.pdfviewer.screenui.reader.utils.BrightnessUtil;
import com.kdanmobile.pdfviewer.screenui.reader.utils.PopupWindowUtil;
import com.kdanmobile.pdfviewer.screenui.reader.view.listener.SeekBarChangeListenerAbstract;
import com.kdanmobile.pdfviewer.utils.CommonUtils;
import com.kdanmobile.pdfviewer.utils.eventbus.ConstantBus;
import com.kdanmobile.pdfviewer.utils.eventbus.EventBusUtils;

/**
 * @classname：ProReaderSettingMoreActivity
 * @author：liujiyuan
 * @date：2018/8/16 下午3:44
 * @description： PDF阅读界面设置
 */
@CreatePresenter(ProReaderSettingMorePresenter.class)
public class ProReaderSettingMoreActivity extends AbstractMvpAppCompatActivity<ProReaderSettingMoreActivity, ProReaderSettingMorePresenter>
        implements ProReaderSettingMoreConstract.IView, View.OnClickListener {

    public static final int PAGE_TURNING_ITEM_INDEX = 0x1009;

    private ConstraintLayout idMoreSettingShare;
    private AppCompatTextView idMoreSettingBookmarkTitle;
    private ConstraintLayout idMoreSettingBookmark;
    private ConstraintLayout idMoreSettingPrint;
    private ConstraintLayout idMoreSettingReflow;
    private ConstraintLayout idMoreSettingInfo;
    private Switch idMoreSettingLockSwitch;
    private ConstraintLayout idMoreSettingTurning;
    private AppCompatTextView idMoreSettingTurningSubTitle;
    private SeekBar idMoreSettingBrightnessSeekbar;
    private ConstraintLayout idMoreSettingCleanSign;
    private boolean isAddBookMark = true;
    private Drawable bookmarkAddImage;
    private Drawable bookmarkRemoveImage;

    @Override
    public int onLayoutId() {
        return R.layout.activity_setting_more;
    }

    @Override
    public boolean onInitView() {
        getSupportActionBar().setTitle(R.string.settings_more_theme_choose);

        idMoreSettingShare = findViewById(R.id.id_more_setting_share);
        idMoreSettingBookmark = findViewById(R.id.id_more_setting_bookmark);
        idMoreSettingBookmarkTitle = findViewById(R.id.id_more_setting_bookmark_title);

        idMoreSettingPrint = findViewById(R.id.id_more_setting_print);
        idMoreSettingCleanSign = findViewById(R.id.id_more_setting_cleanSign);

        idMoreSettingReflow = findViewById(R.id.id_more_setting_reflow);
        idMoreSettingInfo = findViewById(R.id.id_more_setting_info);

        idMoreSettingLockSwitch = findViewById(R.id.id_more_setting_lock_switch);
        idMoreSettingTurning = findViewById(R.id.id_more_setting_turning);
        idMoreSettingTurningSubTitle = findViewById(R.id.id_more_setting_turning_sub_title);
        idMoreSettingBrightnessSeekbar = findViewById(R.id.id_more_setting_brightness_seekbar);

        idMoreSettingShare.setOnClickListener(this);
        idMoreSettingBookmark.setOnClickListener(this);
        idMoreSettingPrint.setOnClickListener(this);
        idMoreSettingCleanSign.setOnClickListener(this);
        idMoreSettingReflow.setOnClickListener(this);
        idMoreSettingInfo.setOnClickListener(this);
        idMoreSettingTurning.setOnClickListener(this);

        bookmarkAddImage = ContextCompat.getDrawable(this, R.drawable.view_ic_bookmark);
        bookmarkAddImage.setBounds(0 , 0, bookmarkAddImage.getMinimumWidth(), bookmarkAddImage.getMinimumHeight());
        bookmarkRemoveImage = ContextCompat.getDrawable(this, R.drawable.view_ic_bookmark_in);
        bookmarkRemoveImage.setBounds(0 , 0, bookmarkRemoveImage.getMinimumWidth(), bookmarkRemoveImage.getMinimumHeight());

        if (!TextUtils.isEmpty(KMReaderConfigs.PAGE_TURN)) {
            idMoreSettingTurningSubTitle.setText(KMReaderConfigs.PAGE_TURN);
        } else {
            idMoreSettingTurningSubTitle.setText(getResources().getString(R.string.page_turn_v_continue));
        }

        idMoreSettingLockSwitch.setChecked(KMReaderConfigs.ISLOCKED);
        idMoreSettingLockSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            KMReaderConfigs.ISLOCKED = isChecked;
            setScreenOrientation(isChecked);
        });

        idMoreSettingBrightnessSeekbar.setProgress((int)(KMReaderConfigs.READER_BRIGHTNESS*100));
        idMoreSettingBrightnessSeekbar.setOnSeekBarChangeListener(new SeekBarChangeListenerAbstract(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                super.onProgressChanged(seekBar, progress, fromUser);
                float alpha = (float) (progress / 100.0);
                KMReaderConfigs.READER_BRIGHTNESS = alpha;
                BrightnessUtil.setActivityBrightness(alpha, ProReaderSettingMoreActivity.this);
            }
        });
        return true;
    }

    /**
     * @methodName：setScreenOrientation created by liujiyuan on 2018/9/6 下午9:44.
     * @description：根据当前屏幕的横纵向，设置应用的屏幕横纵向
     */
    private void setScreenOrientation(boolean isChecked) {
        if(isChecked){
            if(!KMReaderConfigs.IS_PORTRAIT){
                KMReaderConfigs.ORIENTATION = KMReaderConfigs.LANDSCAPE;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }else{
                KMReaderConfigs.ORIENTATION = KMReaderConfigs.PORTRAIT;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAGE_TURNING_ITEM_INDEX && resultCode == RESULT_OK) {
            switch (KMReaderConfigs.PAGE_TURN_INDEX) {
                case ConstantBus.MORE_SETTING_PAGETURN_VC:
                    KMReaderConfigs.PAGE_TURN = getResources().getString(R.string.page_turn_v_continue);
                    break;
                case ConstantBus.MORE_SETTING_PAGETURN_VS:
                    KMReaderConfigs.PAGE_TURN  = getResources().getString(R.string.page_turn_v_single);
                    break;
                case ConstantBus.MORE_SETTING_PAGETURN_HS:
                    KMReaderConfigs.PAGE_TURN  = getResources().getString(R.string.page_turn_h_single);
                    break;
                default:
            }
            idMoreSettingTurningSubTitle.setText(KMReaderConfigs.PAGE_TURN);
            getMvpPresenter().refreshPageTurning();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_more_setting_share:
                CommonUtils.shareCurrentDocument(this);
                break;
            case R.id.id_more_setting_bookmark:
                if(isAddBookMark) {
                    getMvpPresenter().enterBookmarkTitleDialog();
                }else{
                    getMvpPresenter().removeBookMark();
                }
                break;
            case R.id.id_more_setting_print:
                getMvpPresenter().printDocument();
                break;
            case R.id.id_more_setting_cleanSign:
                getMvpPresenter().cleanAllSign();
                break;
            case R.id.id_more_setting_reflow:
                break;
            case R.id.id_more_setting_info:
                PopupWindowUtil.getInstance().pdfInfoPopupWindow.show(0);
                break;
            case R.id.id_more_setting_turning:
                Intent intent = new Intent();
                intent.setClass(this, PageTurningActivity.class);
                startActivityForResult(intent, PAGE_TURNING_ITEM_INDEX);
                break;
            default:
        }
    }

    /**
     * @methodName：setBookMarkState created by liujiyuan on 2018/9/29 上午10:31.
     * @description：设置显示书签的状态：添加书签 或者 删除书签
     */
    @Override
    public void setBookMarkState(boolean isAddBookMark){
        this.isAddBookMark = isAddBookMark;
        String result;
        if(isAddBookMark){
            result = getResources().getString(R.string.settings_more_add_bookmark);
            idMoreSettingBookmarkTitle.setCompoundDrawables(bookmarkAddImage, null ,null ,null);
        }else{
            result = getResources().getString(R.string.settings_more_remove_bookmark);
            idMoreSettingBookmarkTitle.setCompoundDrawables(bookmarkRemoveImage, null ,null ,null);
        }
        idMoreSettingBookmarkTitle.setText(result);
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
        PopupWindowUtil.initUtil(this, idMoreSettingInfo);
        /****** 设置 OnTakeOrPickPhotoCallback 接口，并打开popupwindow ******/
        PopupWindowUtil.buildPopupWindow(PopupWindowUtil.PopupWindowType.PDF_INFO);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        EventBusUtils.getInstance().post(new MessageEvent<>(ConstantBus.REFRESH_BRIGHTNESS, 0));
        super.onDestroy();
    }
}