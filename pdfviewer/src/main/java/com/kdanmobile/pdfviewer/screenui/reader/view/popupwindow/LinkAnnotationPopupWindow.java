package com.kdanmobile.pdfviewer.screenui.reader.view.popupwindow;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kdanmobile.pdfviewer.R;
import com.kdanmobile.kmpdfkit.annotation.link.listener.OnLinkInfoChangeListener;
import com.kdanmobile.pdfviewer.base.BasePopupWindow;
import com.kdanmobile.pdfviewer.screenui.reader.configs.ApplicationConfig;
import com.kdanmobile.pdfviewer.screenui.reader.configs.KMReaderConfigs;
import com.kdanmobile.pdfviewer.screenui.reader.view.listener.OnPopupWindowCallback;
import com.kdanmobile.pdfviewer.screenui.reader.view.listener.PopupWindowStruct;
import com.kdanmobile.pdfviewer.utils.SimpleTextWatcher;

/**
 * @classname：LinkAnnotationPopupWindow
 * @author：liujiyuan
 * @date：2018/9/12 下午5:40
 * @description：
 */
public class LinkAnnotationPopupWindow extends BasePopupWindow implements PopupWindowStruct {

    /****** Popupwindow附着的view ******/
    private View rootview;

    private Drawable webSite_blue;
    private Drawable webSite_gray;
    private Drawable page_blue;
    private Drawable page_gray;
    private Drawable email_blue;
    private Drawable email_gray;
    private int tvColor_gray;
    private int tvColor_blue;

    private LinearLayout idLinkAnnotPageLayout;
    private ImageView idLinkAnnotPageIv;
    private TextView idLinkAnnotPageTv;
    private LinearLayout idLinkAnnotWebsiteLayout;
    private ImageView idLinkAnnotWebsiteIv;
    private TextView idLinkAnnotWebsiteTv;
    private LinearLayout idLinkAnnotEmailLayout;
    private ImageView idLinkAnnotEmailIv;
    private TextView idLinkAnnotEmailTv;
    private EditText idLinkAnnotUrlEtText;
    private EditText idLinkAnnotUrlEtNum;
    private ImageView idLinkAnnotDeleteUrl;
    private TextView idLinkAnnotCancel;
    private TextView idLinkAnnotDone;

    private boolean isDone = false;
    int finishedColor;
    int unFinishedColor;

    private OnLinkInfoChangeListener linkInfoChangeListener;
    private OnLinkInfoChangeListener.KMPDFLinkType kmPDFLinkType = OnLinkInfoChangeListener.KMPDFLinkType.WEBSITE;
    private boolean isEdit = false;

    public LinkAnnotationPopupWindow(Context context, View rootview) {
        super(context);
        this.rootview = rootview;
        setWidth((int)mContext.getResources().getDimension(R.dimen.qb_px_300));
        setAnimationStyle(R.style.popwindow_anim_style);
        setFocusable(true);
    }

    @Override
    protected View setLayout(LayoutInflater inflater) {
        return inflater.inflate(R.layout.popupwindow_link_annotation,null);
    }

    @Override
    protected void initResource() {
        webSite_blue = ContextCompat.getDrawable(mContext, R.drawable.link_ic_url_sel);
        webSite_gray = ContextCompat.getDrawable(mContext, R.drawable.link_ic_url_nor);
        page_blue = ContextCompat.getDrawable(mContext, R.drawable.link_ic_page_sel);
        page_gray = ContextCompat.getDrawable(mContext, R.drawable.link_ic_page_nor);
        email_blue = ContextCompat.getDrawable(mContext, R.drawable.link_ic_email_sel);
        email_gray = ContextCompat.getDrawable(mContext, R.drawable.link_ic_email_nor);

        tvColor_gray = ContextCompat.getColor(mContext, R.color.grayColor_btGone);
        tvColor_blue = ContextCompat.getColor(mContext, R.color.primary_blue);

        finishedColor = ContextCompat.getColor(mContext, R.color.primary_blue);
        unFinishedColor = ContextCompat.getColor(mContext, R.color.menu_border_color);
    }

    @Override
    protected void initListener() {
        idLinkAnnotCancel.setOnClickListener(this);
        idLinkAnnotDone.setOnClickListener(this);
        idLinkAnnotWebsiteLayout.setOnClickListener(this);
        idLinkAnnotPageLayout.setOnClickListener(this);
        idLinkAnnotEmailLayout.setOnClickListener(this);
        idLinkAnnotDeleteUrl.setOnClickListener(this);
        /****** 添加文本编辑监听器，如果文本内容为空，则done为不可点击状态 ******/
        idLinkAnnotUrlEtText.addTextChangedListener(new SimpleTextWatcher(){
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                if(TextUtils.isEmpty(s)){
                    idLinkAnnotDone.setTextColor(unFinishedColor);
                    idLinkAnnotDone.setClickable(false);
                }else{
                    idLinkAnnotDone.setTextColor(finishedColor);
                    idLinkAnnotDone.setClickable(true);
                }
            }
        });
        /****** 添加数字编辑监听器，如果数字内容不符合要求，则done为不可点击状态 ******/
        idLinkAnnotUrlEtNum.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean isPageError = (kmPDFLinkType == OnLinkInfoChangeListener.KMPDFLinkType.PAGE) &&
                        (TextUtils.isEmpty(s)
                                || Integer.parseInt(s.toString()) == 0
                                || Integer.parseInt(s.toString()) > KMReaderConfigs.readerPageCount);
                if(isPageError){
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.overstep_the_boundary) + " 1 - " + KMReaderConfigs.readerPageCount, Toast.LENGTH_SHORT).show();
                    idLinkAnnotDone.setTextColor(unFinishedColor);
                    idLinkAnnotDone.setClickable(false);
                }else{
                    idLinkAnnotDone.setTextColor(finishedColor);
                    idLinkAnnotDone.setClickable(true);
                }
            }
        });
    }

    @Override
    protected void initView() {
        idLinkAnnotPageLayout = mContentView.findViewById(R.id.id_link_annot_page_layout);
        idLinkAnnotPageIv = mContentView.findViewById(R.id.id_link_annot_page_iv);
        idLinkAnnotPageTv = mContentView.findViewById(R.id.id_link_annot_page_tv);

        idLinkAnnotWebsiteLayout = mContentView.findViewById(R.id.id_link_annot_website_layout);
        idLinkAnnotWebsiteIv = mContentView.findViewById(R.id.id_link_annot_website_iv);
        idLinkAnnotWebsiteTv = mContentView.findViewById(R.id.id_link_annot_website_tv);

        idLinkAnnotEmailLayout = mContentView.findViewById(R.id.id_link_annot_email_layout);
        idLinkAnnotEmailIv = mContentView.findViewById(R.id.id_link_annot_email_iv);
        idLinkAnnotEmailTv = mContentView.findViewById(R.id.id_link_annot_email_tv);

        /**
         * 两个EditText，
         * url_et_text：专门为web和email提供文本内容的；
         * url_et_num：专门为page提供文本内容的；
         * 提供两个edittext的原因：由于在某些手机上，使用EditText.setInputType()无法使键盘在文本按键和数字按键中来回切换
         **/
        idLinkAnnotUrlEtText = mContentView.findViewById(R.id.id_link_annot_url_et_text);
        idLinkAnnotUrlEtNum = mContentView.findViewById(R.id.id_link_annot_url_et_num);
        idLinkAnnotDeleteUrl = mContentView.findViewById(R.id.id_link_annot_delete_url);

        idLinkAnnotCancel = mContentView.findViewById(R.id.id_link_annot_cancel);
        idLinkAnnotDone = mContentView.findViewById(R.id.id_link_annot_done);
    }

    @Override
    protected void onClickListener(View view) {
        switch (view.getId()) {
            case R.id.id_link_annot_website_layout:
                selectTypeLayout(OnLinkInfoChangeListener.KMPDFLinkType.WEBSITE);
                break;
            case R.id.id_link_annot_page_layout:
                selectTypeLayout(OnLinkInfoChangeListener.KMPDFLinkType.PAGE);
                break;
            case R.id.id_link_annot_email_layout:
                selectTypeLayout(OnLinkInfoChangeListener.KMPDFLinkType.EMAIL);
                break;
            case R.id.id_link_annot_delete_url:
                changeUrlEt("", "http://www.pdfreaderpro.com", false);
                break;
            case R.id.id_link_annot_done:
                isDone = true;
                dismiss();
                break;
            case R.id.id_link_annot_cancel:
                isDone = false;
                dismiss();
                break;
            default:
        }
    }

    /**
     * @methodName：selectTypeLayout created by liujiyuan on 2018/9/13 上午9:54.
     * @description：三个图标的点击事件
     */
    private void selectTypeLayout(OnLinkInfoChangeListener.KMPDFLinkType type){
        kmPDFLinkType = type;
        switch (type) {
            case WEBSITE:
                if(!isEdit) {
                    changeUrlEt("http://", "", false);
                }
                idLinkAnnotDeleteUrl.setVisibility(View.VISIBLE);
                break;
            case PAGE:
                if(!isEdit) {
                    changeUrlEt("", "1 ～ " + KMReaderConfigs.readerPageCount, true);
                }
                idLinkAnnotDeleteUrl.setVisibility(View.GONE);
                break;
            case EMAIL:
                if(!isEdit) {
                    changeUrlEt("", "support@pdfreaderpro.com", false);
                }
                idLinkAnnotDeleteUrl.setVisibility(View.GONE);
                break;
            default:
        }
        changeImageAndText();
    }
    /**
     * @methodName：changeImageAndText created by liujiyuan on 2018/9/13 上午9:53.
     * @description：设置三个图标的点击动态更新
     */
    private void changeImageAndText(){
        idLinkAnnotWebsiteIv.setImageDrawable(webSite_gray);
        idLinkAnnotPageIv.setImageDrawable(page_gray);
        idLinkAnnotEmailIv.setImageDrawable(email_gray);
        idLinkAnnotWebsiteTv.setTextColor(tvColor_gray);
        idLinkAnnotPageTv.setTextColor(tvColor_gray);
        idLinkAnnotEmailTv.setTextColor(tvColor_gray);
        switch (kmPDFLinkType) {
            case WEBSITE:
                idLinkAnnotWebsiteIv.setImageDrawable(webSite_blue);
                idLinkAnnotWebsiteTv.setTextColor(tvColor_blue);
                break;
            case PAGE:
                idLinkAnnotPageIv.setImageDrawable(page_blue);
                idLinkAnnotPageTv.setTextColor(tvColor_blue);
                break;
            case EMAIL:
                idLinkAnnotEmailIv.setImageDrawable(email_blue);
                idLinkAnnotEmailTv.setTextColor(tvColor_blue);
                break;
            default:
        }
    }

    /**
     * @methodName：changeUrlEt created by liujiyuan on 2018/9/12 下午6:19.
     * @description：切换两中输入框的方法
     */
    private void changeUrlEt(String text, String hintText, boolean isNumber){
        if(isNumber){
            idLinkAnnotUrlEtNum.setVisibility(View.VISIBLE);
            idLinkAnnotUrlEtText.setVisibility(View.GONE);
            idLinkAnnotUrlEtNum.setText(text);
            idLinkAnnotUrlEtNum.setSelection(text.length());
            idLinkAnnotUrlEtNum.setHint(hintText);
        }else{
            idLinkAnnotUrlEtNum.setVisibility(View.GONE);
            idLinkAnnotUrlEtText.setVisibility(View.VISIBLE);
            idLinkAnnotUrlEtText.setText(text);
            idLinkAnnotUrlEtText.setSelection(text.length());
            idLinkAnnotUrlEtText.setHint(hintText);
        }
    }

    /**
     * @methodName：setOnInfoChangeListener created by liujiyuan on 2018/9/13 下午2:06.
     * @description：设置库的回调接口
     */
    public LinkAnnotationPopupWindow setOnInfoChangeListener(OnLinkInfoChangeListener linkInfoChangeListener){
        this.linkInfoChangeListener = linkInfoChangeListener;
        return this;
    }

    @Override
    public PopupWindowStruct setCallback(OnPopupWindowCallback callback) {
        return null;
    }

    @Override
    public PopupWindowStruct setObject(Object o) {
        return null;
    }

    @Override
    public void show(int type) {
    }

    public void show(OnLinkInfoChangeListener.KMPDFLinkType type){
        selectTypeLayout(type);
        if(type == OnLinkInfoChangeListener.KMPDFLinkType.PAGE){
            showOrHintSoftKeyboard(idLinkAnnotUrlEtNum, true);
        }else {
            showOrHintSoftKeyboard(idLinkAnnotUrlEtText, true);
        }
        showAtLocation(rootview, Gravity.CENTER, 0, 0);
        changeWindowAlpha((Activity)mContext, 0.4f);
        isEdit = false;
    }

    /**
     * @methodName：setInitData created by liujiyuan on 2018/9/13 下午4:57.
     * @description：link注释修改的初始值赋值
     */
    public LinkAnnotationPopupWindow setInitData(OnLinkInfoChangeListener.KMPDFLinkType type, String url) {
        isEdit = true;
        String editString = url;
        if(type == OnLinkInfoChangeListener.KMPDFLinkType.PAGE){
            try{
                editString = "" + (Integer.parseInt(url) + 1);
            }catch (NumberFormatException e){
                Toast.makeText(mContext, R.string.number_format_error, Toast.LENGTH_SHORT).show();
            }
            changeUrlEt(editString, "", true);
            return this;
        }else if(type == OnLinkInfoChangeListener.KMPDFLinkType.EMAIL){
            String[] email = url.split(":");
            if(email.length > 0){
                editString= email[email.length - 1];
            }
        }
        changeUrlEt(editString, "", false);
        return this;
    }

    @Override
    public void dismiss() {
        String url;
        if(kmPDFLinkType == OnLinkInfoChangeListener.KMPDFLinkType.PAGE){
            showOrHintSoftKeyboard(idLinkAnnotUrlEtNum, false);
            url = idLinkAnnotUrlEtNum.getText().toString();
        }else {
            showOrHintSoftKeyboard(idLinkAnnotUrlEtText, false);
            url = idLinkAnnotUrlEtText.getText().toString();
        }
        if(linkInfoChangeListener != null){
            if(!isDone){
                linkInfoChangeListener.cancelCreateLink();
            }else{
                switch (kmPDFLinkType){
                    case PAGE:
                        try{
                            linkInfoChangeListener.createPageLink(Integer.parseInt(url));
                        }catch (NumberFormatException e){
                            new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(mContext, mContext.getText(R.string.number_format_error), Toast.LENGTH_SHORT).show());
                            return;
                        }
                        break;
                    case EMAIL:
                        linkInfoChangeListener.createEmailLink(url);
                        break;
                    case WEBSITE:
                        linkInfoChangeListener.createWebsiteLink(url);
                        break;
                }
                isDone = false;
            }
        }
        kmPDFLinkType = OnLinkInfoChangeListener.KMPDFLinkType.WEBSITE;
        idLinkAnnotUrlEtNum.setText("");
        idLinkAnnotUrlEtText.setText("");
        super.dismiss();
    }
}

