package com.kdanmobile.pdfviewer.screenui.reader.view.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kdanmobile.pdfviewer.R;
import com.kdanmobile.pdfviewer.base.BaseActivity;
import com.kdanmobile.pdfviewer.screenui.reader.configs.KMReaderConfigs;
import com.kdanmobile.pdfviewer.screenui.reader.utils.BrightnessUtil;
import com.kdanmobile.pdfviewer.utils.eventbus.ConstantBus;

/**
 * @classname：PageTurningFragment
 * @author：liujiyuan
 * @date：2018/8/16 下午4:55
 * @description： 翻页设置界面
 */
public class PageTurningActivity extends BaseActivity implements View.OnClickListener{

    private RelativeLayout idPageTurningVContinue;
    private TextView idPageTurningVContinueText;
    private ImageView idPageTurningVContinueIv;
    private RelativeLayout idPageTurningVSingle;
    private TextView idPageTurningVSingleText;
    private ImageView idPageTurningVSingleIv;
    private RelativeLayout idPageTurningHSingle;
    private TextView idPageTurningHSingleText;
    private ImageView idPageTurningHSingleIv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_turning);

        getSupportActionBar().setTitle(R.string.settings_more_turn);

        idPageTurningVContinue = findViewById(R.id.id_page_turning_v_continue);
        idPageTurningVContinueText = findViewById(R.id.id_page_turning_v_continue_text);
        idPageTurningVContinueIv = findViewById(R.id.id_page_turning_v_continue_iv);
        idPageTurningVSingle = findViewById(R.id.id_page_turning_v_single);
        idPageTurningVSingleText = findViewById(R.id.id_page_turning_v_single_text);
        idPageTurningVSingleIv = findViewById(R.id.id_page_turning_v_single_iv);
        idPageTurningHSingle = findViewById(R.id.id_page_turning_h_single);
        idPageTurningHSingleText = findViewById(R.id.id_page_turning_h_single_text);
        idPageTurningHSingleIv = findViewById(R.id.id_page_turning_h_single_iv);

        idPageTurningVContinue.setOnClickListener(this);
        idPageTurningVSingle.setOnClickListener(this);
        idPageTurningHSingle.setOnClickListener(this);
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
        if(KMReaderConfigs.PAGE_TURN_INDEX != -1){
            setViewChange(KMReaderConfigs.PAGE_TURN_INDEX,false);
        }else{
            setViewChange(ConstantBus.MORE_SETTING_PAGETURN_VC,false);
        }
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_page_turning_v_continue:
                setViewChange(ConstantBus.MORE_SETTING_PAGETURN_VC, true);
                break;
            case R.id.id_page_turning_v_single:
                setViewChange(ConstantBus.MORE_SETTING_PAGETURN_VS, true);
                break;
            case R.id.id_page_turning_h_single:
                setViewChange(ConstantBus.MORE_SETTING_PAGETURN_HS, true);
                break;
            default:
        }
        finish();
    }

    private void setViewChange(int type, boolean sendMessage){
        int themeColor = ContextCompat.getColor(this, R.color.theme_color_cyan);
        int turnTextColor = ContextCompat.getColor(this, R.color.page_turn_text_color);
        switch (type){
            case ConstantBus.MORE_SETTING_PAGETURN_VC:
                idPageTurningVContinueText.setTextColor(themeColor);
                idPageTurningVSingleText.setTextColor(turnTextColor);
                idPageTurningHSingleText.setTextColor(turnTextColor);
                idPageTurningVContinueIv.setVisibility(View.VISIBLE);
                idPageTurningVSingleIv.setVisibility(View.INVISIBLE);
                idPageTurningHSingleIv.setVisibility(View.INVISIBLE);
                break;
            case ConstantBus.MORE_SETTING_PAGETURN_VS:
                idPageTurningVContinueText.setTextColor(turnTextColor);
                idPageTurningVSingleText.setTextColor(themeColor);
                idPageTurningHSingleText.setTextColor(turnTextColor);
                idPageTurningVContinueIv.setVisibility(View.INVISIBLE);
                idPageTurningVSingleIv.setVisibility(View.VISIBLE);
                idPageTurningHSingleIv.setVisibility(View.INVISIBLE);
                break;
            case ConstantBus.MORE_SETTING_PAGETURN_HS:
                idPageTurningVContinueText.setTextColor(turnTextColor);
                idPageTurningVSingleText.setTextColor(turnTextColor);
                idPageTurningHSingleText.setTextColor(themeColor);
                idPageTurningVContinueIv.setVisibility(View.INVISIBLE);
                idPageTurningVSingleIv.setVisibility(View.INVISIBLE);
                idPageTurningHSingleIv.setVisibility(View.VISIBLE);
                break;
            default:
        }
        if (sendMessage) {
            KMReaderConfigs.PAGE_TURN_INDEX = type;
            setResult(RESULT_OK);
        }
    }
}
