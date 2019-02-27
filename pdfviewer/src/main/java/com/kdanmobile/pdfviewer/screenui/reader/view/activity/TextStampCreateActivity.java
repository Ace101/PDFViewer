package com.kdanmobile.pdfviewer.screenui.reader.view.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Switch;

import com.kdanmobile.pdfviewer.R;
import com.kdanmobile.kmpdfkit.annotation.stamp.TextStampConfig;
import com.kdanmobile.kmpdfkit.annotation.stamp.view.KMPDFStampTextView;
import com.kdanmobile.pdfviewer.base.BaseActivity;
import com.kdanmobile.pdfviewer.event.MessageEvent;
import com.kdanmobile.pdfviewer.screenui.reader.configs.KMReaderConfigs;
import com.kdanmobile.pdfviewer.screenui.reader.utils.BrightnessUtil;
import com.kdanmobile.pdfviewer.screenui.reader.widget.SelectableImageView;
import com.kdanmobile.pdfviewer.utils.KeyboardUtils;
import com.kdanmobile.pdfviewer.utils.SimpleTextWatcher;
import com.kdanmobile.pdfviewer.utils.eventbus.ConstantBus;
import com.kdanmobile.pdfviewer.utils.eventbus.EventBusUtils;

import java.util.HashMap;
import java.util.Map;

import static com.kdanmobile.kmpdfkit.annotation.stamp.StampConfig.StandardColor.bg_blue;
import static com.kdanmobile.kmpdfkit.annotation.stamp.StampConfig.StandardColor.bg_green;
import static com.kdanmobile.kmpdfkit.annotation.stamp.StampConfig.StandardColor.bg_red;
import static com.kdanmobile.kmpdfkit.annotation.stamp.StampConfig.StandardColor.bg_white;
import static com.kdanmobile.kmpdfkit.annotation.stamp.StampConfig.StandardColor.line_blue;
import static com.kdanmobile.kmpdfkit.annotation.stamp.StampConfig.StandardColor.line_green;
import static com.kdanmobile.kmpdfkit.annotation.stamp.StampConfig.StandardColor.line_red;
import static com.kdanmobile.kmpdfkit.annotation.stamp.StampConfig.StandardColor.line_white;
import static com.kdanmobile.kmpdfkit.annotation.stamp.StampConfig.StandardColor.text_black;
import static com.kdanmobile.kmpdfkit.annotation.stamp.StampConfig.StandardColor.text_blue;
import static com.kdanmobile.kmpdfkit.annotation.stamp.StampConfig.StandardColor.text_green;
import static com.kdanmobile.kmpdfkit.annotation.stamp.StampConfig.StandardColor.text_red;

/**
 * @classname：TextStampCreateActivity
 * @author：liujiyuan
 * @date：2018/9/4 下午1:54
 * @description：自定义text stamp功能界面
 */
public class TextStampCreateActivity extends BaseActivity {

    private HashMap<String, SelectableImageView> arr;

    private KMPDFStampTextView idStampTextStampTv;
    private EditText idStampTextStampEt;
    private SelectableImageView a4;
    private SelectableImageView a3;
    private SelectableImageView a2;
    private SelectableImageView a1;
    private SelectableImageView a4a;
    private SelectableImageView a3a;
    private SelectableImageView a2a;
    private SelectableImageView a4b;
    private SelectableImageView a3b;
    private SelectableImageView a2b;
    private Switch idStampTextDateSwitch;
    private Switch idStampTextTimeSwitch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_text_stamp);

        getSupportActionBar().setTitle(R.string.stamp_create_text);

        idStampTextStampTv = findViewById(R.id.id_stamp_text_stamp_tv);

        idStampTextStampEt = findViewById(R.id.id_stamp_text_stamp_et);

        a4 = findViewById(R.id.a_4);
        a3 = findViewById(R.id.a_3);
        a2 = findViewById(R.id.a_2);
        a2.setIsDrawRect(true);
        a1 = findViewById(R.id.a_1);
        a4a = findViewById(R.id.a_4a);
        a3a = findViewById(R.id.a_3a);
        a2a = findViewById(R.id.a_2a);
        a4b = findViewById(R.id.a_4b);
        a3b = findViewById(R.id.a_3b);
        a2b = findViewById(R.id.a_2b);

        idStampTextDateSwitch = findViewById(R.id.id_stamp_text_date_switch);
        idStampTextTimeSwitch = findViewById(R.id.id_stamp_text_time_switch);
        initListener();
    }

    private void initListener() {
        /****** 给每个color选项设置监听 ******/
        arr = new HashMap<>();
        arr.put("a_1", a1);
        arr.put("a_2", a2);
        arr.put("a_3", a3);
        arr.put("a_4", a4);
        arr.put("a_2a", a2a);
        arr.put("a_3a", a3a);
        arr.put("a_4a", a4a);
        arr.put("a_2b", a2b);
        arr.put("a_3b", a3b);
        arr.put("a_4b", a4b);
        for(Map.Entry<String, SelectableImageView> entry : arr.entrySet()){
            entry.getValue().setOnClickListener(v -> {

                for(Map.Entry<String, SelectableImageView> t : arr.entrySet()){
                    if(t.getValue() != v){
                        t.getValue().setIsDrawRect(false);
                    }else{
                        t.getValue().setIsDrawRect(true);
                        setBgAndLineColor(idStampTextStampTv, t.getKey());
                    }
                    t.getValue().invalidate();
                }
                idStampTextStampTv.requestLayout();
            });
        }
        /****** 编辑框的监听器 ******/
        idStampTextStampEt.addTextChangedListener(new SimpleTextWatcher(){
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                idStampTextStampTv.setContent(idStampTextStampEt.getText().toString());
                idStampTextStampTv.requestLayout();
            }
        });

        /****** 日期的switch监听 ******/
        idStampTextDateSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                idStampTextStampTv.setDateSwitch(true);
            }else{
                idStampTextStampTv.setDateSwitch(false);
            }
            idStampTextStampTv.requestLayout();
        });

        /****** 时间的switch监听 ******/
        idStampTextTimeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                idStampTextStampTv.setTimeSwitch(true);
            }else{
                idStampTextStampTv.setTimeSwitch(false);
            }
            idStampTextStampTv.requestLayout();

        });
    }

    /**
     * @param ：[stampTextView, resId]
     * @return : void
     * @methodName ：setBgAndLineColor created by liujiyuan on 2018/9/4 下午2:13.
     * @description ：根据每个color选项，设置预览stampTextView的形状，边框色，背景色和字体颜色
     */
    private void setBgAndLineColor(KMPDFStampTextView stampTextView, String resId){
        switch (resId){
            case "a_1":
                stampTextView.setShape(KMPDFStampTextView.Shape.RECT);
                stampTextView.setBgColor(bg_white);
                stampTextView.setTextColor(text_black);
                stampTextView.setLineColor(line_white);
                break;
            case "a_2":
                stampTextView.setShape(KMPDFStampTextView.Shape.RECT);
                stampTextView.setBgColor(bg_green);
                stampTextView.setTextColor(text_green);
                stampTextView.setLineColor(line_green);
                break;
            case "a_3":
                stampTextView.setShape(KMPDFStampTextView.Shape.RECT);
                stampTextView.setBgColor(bg_red);
                stampTextView.setTextColor(text_red);
                stampTextView.setLineColor(line_red);
                break;
            case "a_4":
                stampTextView.setShape(KMPDFStampTextView.Shape.RECT);
                stampTextView.setBgColor(bg_blue);
                stampTextView.setTextColor(text_blue);
                stampTextView.setLineColor(line_blue);
                break;
            case "a_2a":
                stampTextView.setShape(KMPDFStampTextView.Shape.LEFT_RECT);
                stampTextView.setBgColor(bg_green);
                stampTextView.setTextColor(text_green);
                stampTextView.setLineColor(line_green);
                break;
            case "a_3a":
                stampTextView.setShape(KMPDFStampTextView.Shape.LEFT_RECT);
                stampTextView.setBgColor(bg_red);
                stampTextView.setTextColor(text_red);
                stampTextView.setLineColor(line_red);
                break;
            case "a_4a":
                stampTextView.setShape(KMPDFStampTextView.Shape.LEFT_RECT);
                stampTextView.setBgColor(bg_blue);
                stampTextView.setTextColor(text_blue);
                stampTextView.setLineColor(line_blue);
                break;
            case "a_2b":
                stampTextView.setShape(KMPDFStampTextView.Shape.RIGHT_RECT);
                stampTextView.setBgColor(bg_green);
                stampTextView.setTextColor(text_green);
                stampTextView.setLineColor(line_green);
                break;
            case "a_3b":
                stampTextView.setShape(KMPDFStampTextView.Shape.RIGHT_RECT);
                stampTextView.setBgColor(bg_red);
                stampTextView.setTextColor(text_red);
                stampTextView.setLineColor(line_red);
                break;
            case "a_4b":
                stampTextView.setShape(KMPDFStampTextView.Shape.RIGHT_RECT);
                stampTextView.setBgColor(bg_blue);
                stampTextView.setTextColor(text_blue);
                stampTextView.setLineColor(line_blue);
                break;
            default:
                break;
        }
        stampTextView.requestLayout();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_text_stamp_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.create_text_stamp_done:
                if(idStampTextStampTv != null){
                    TextStampConfig textStampConfig = new TextStampConfig(idStampTextStampTv.getLineColor(),
                            idStampTextStampTv.getBgColor(),
                            idStampTextStampTv.getTextColor(),
                            idStampTextStampTv.getContent(),
                            idStampTextStampTv.getDateStr(),
                            idStampTextStampTv.getShape(),
                            idStampTextStampTv.getTimeType());
                    EventBusUtils.getInstance().post(new MessageEvent<>(ConstantBus.CREATE_TEXT_STAMP, textStampConfig));
                }
                KeyboardUtils.hideKeyboard(this);
                onBackPressed();
                break;
            default:

        }
        return super.onOptionsItemSelected(item);
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

    @Override
    public void onBackPressed() {
        EventBusUtils.getInstance().post(new MessageEvent<>(ConstantBus.BACK_STAMP_ACTIVITY, ""));
        KeyboardUtils.hideKeyboard(this);
        super.onBackPressed();
    }
}
