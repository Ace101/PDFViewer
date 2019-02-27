package com.kdanmobile.pdfviewer.screenui.reader.view.popupwindow;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kdanmobile.kmpdfkit.manager.KMPDFFactory;
import com.kdanmobile.kmpdfkit.manager.controller.KMPDFDocumentController;
import com.kdanmobile.kmpdfkit.pdfcommon.PDFInfo;
import com.kdanmobile.pdfviewer.R;
import com.kdanmobile.pdfviewer.base.BasePopupWindow;
import com.kdanmobile.pdfviewer.screenui.reader.utils.InitKMPDFControllerUtil;
import com.kdanmobile.pdfviewer.screenui.reader.view.listener.OnPopupWindowCallback;
import com.kdanmobile.pdfviewer.screenui.reader.view.listener.PopupWindowStruct;
import com.kdanmobile.pdfviewer.utils.FileUtilsExtension;
import com.kdanmobile.pdfviewer.utils.GlobalConfigs;

/**
 * @classname：PdfInfoPopupWindow
 * @author：liujiyuan
 * @date：2018/9/7 下午2:39
 * @description：pdf info的界面展示
 */
public class PdfInfoPopupWindow extends BasePopupWindow implements PopupWindowStruct {

    private View rootview;
    private PDFInfo pdfInfo;
    private int backgroundColor = Color.WHITE;
    private final long MB = 1024*1024;
    private final int KB = 1024;

    private KMPDFFactory kmpdfFactory;
    private KMPDFDocumentController kmpdfDocumentController;

    private RelativeLayout idPdfInfoToolbar;
    private ImageButton idPdfInfoBack;
    private TextView idPdfInfoFileName;
    private TextView idPdfInfoSize;
    private TextView idPdfInfoTitle;
    private TextView idPdfInfoAuthor;
    private TextView idPdfInfoVersion;
    private TextView idPdfInfoPages;
    private TextView idPdfInfoCreator;
    private TextView idPdfInfoProducer;
    private TextView idPdfInfoCreationDate;
    private TextView idPdfInfoModification;
    private TextView idPdfInfoEncrypted;
    private TextView idPdfInfoUnlocked;
    private TextView idPdfInfoAllowCopy;
    private TextView idPdfInfoAllowPrint;

    public PdfInfoPopupWindow(Context context, View rootview) {
        super(context);
        setBackgroundDrawable(new ColorDrawable(backgroundColor));
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        kmpdfFactory = InitKMPDFControllerUtil.getInstance().getKmpdfFactory();
        kmpdfDocumentController = InitKMPDFControllerUtil.getInstance().getKmpdfDocumentController();
        this.pdfInfo = kmpdfDocumentController.getPDFInfo();
        this.rootview = rootview;
    }

    @Override
    protected View setLayout(LayoutInflater inflater) {
        return inflater.inflate(R.layout.popupwindow_pdfinfo, null);
    }

    @Override
    protected void initResource() {
    }

    private void initData(){
        Long fileSize = FileUtilsExtension.getFileSize(GlobalConfigs.FILE_ABSOLUTE_PATH);
        float size;
        String yes = mContext.getString(R.string.context_common_yes);
        String no = mContext.getString(R.string.context_common_no);

        idPdfInfoFileName.setText(GlobalConfigs.CURRENT_DOCUMENT_NAME);

        String unit = " M";
        if(fileSize > MB){
            size = ((float)fileSize/(1024*1024));
        }else if(fileSize > KB){
            size = ((float)fileSize/1024);
            unit = " KB";
        }else{
            size = fileSize;
            unit = " B";
        }
        idPdfInfoSize.setText(String.format("%.2f", size)+unit);

        if(pdfInfo != null) {
            idPdfInfoTitle.setText(pdfInfo.title == null? "": pdfInfo.title);
            idPdfInfoAuthor.setText(pdfInfo.author == null? "": pdfInfo.author);

            idPdfInfoVersion.setText(pdfInfo.version == null? "": pdfInfo.version);
            idPdfInfoPages.setText(String.valueOf(pdfInfo.count));
            idPdfInfoCreator.setText(pdfInfo.creator == null? "": pdfInfo.creator);
            idPdfInfoProducer.setText(pdfInfo.producer == null? "":pdfInfo.producer);
            idPdfInfoCreationDate.setText(pdfInfo.creationDate == null? "": translateDateInfo(pdfInfo.creationDate));
            idPdfInfoModification.setText(pdfInfo.modification == null? "": translateDateInfo(pdfInfo.modification));

            idPdfInfoEncrypted.setText(kmpdfFactory.needPassWord()?yes:no);
            idPdfInfoUnlocked.setText(pdfInfo.unlocked == null? yes: pdfInfo.unlocked);
            idPdfInfoAllowCopy.setText(pdfInfo.allowCopy == null? yes: pdfInfo.allowCopy);
            idPdfInfoAllowPrint.setText(pdfInfo.allowPrint == null? yes: pdfInfo.allowPrint);
        }
    }

    /**
     * @methodName：translateDateInfo created by liujiyuan on 2018/9/17 上午11:28.
     * @description：把字符串转换为 yyyy-MM-dd HH:mm:ss 的日期格式字符串
     */
    private String translateDateInfo(String date){
        if(TextUtils.isEmpty(date)){
            return "";
        }
        return date.substring(2, 6)+"-"+date.substring(6, 8)+"-"+date.substring(8, 10)+" "
                +date.substring(10, 12)+":"+date.substring(12, 14)+":"+date.substring(14, 16);
    }

    @Override
    protected void initListener() {
    }

    @Override
    protected void initView() {

        idPdfInfoToolbar = mContentView.findViewById(R.id.id_pdf_info_toolbar);
        idPdfInfoBack = mContentView.findViewById(R.id.id_pdf_info_back);

        idPdfInfoFileName = mContentView.findViewById(R.id.id_pdf_info_fileName);
        idPdfInfoSize = mContentView.findViewById(R.id.id_pdf_info_size);
        idPdfInfoTitle = mContentView.findViewById(R.id.id_pdf_info_title);
        idPdfInfoAuthor = mContentView.findViewById(R.id.id_pdf_info_author);

        idPdfInfoVersion = mContentView.findViewById(R.id.id_pdf_info_version);
        idPdfInfoPages = mContentView.findViewById(R.id.id_pdf_info_pages);
        idPdfInfoCreator = mContentView.findViewById(R.id.id_pdf_info_creator);
        idPdfInfoProducer = mContentView.findViewById(R.id.id_pdf_info_producer);
        idPdfInfoCreationDate = mContentView.findViewById(R.id.id_pdf_info_creationDate);
        idPdfInfoModification = mContentView.findViewById(R.id.id_pdf_info_modification);

        idPdfInfoEncrypted = mContentView.findViewById(R.id.id_pdf_info_encrypted);
        idPdfInfoUnlocked = mContentView.findViewById(R.id.id_pdf_info_unlocked);
        idPdfInfoAllowCopy = mContentView.findViewById(R.id.id_pdf_info_allowCopy);
        idPdfInfoAllowPrint = mContentView.findViewById(R.id.id_pdf_info_allowPrint);
        idPdfInfoBack.setOnClickListener(this);
    }

    @Override
    protected void onClickListener(View view) {
        if(view.getId() == R.id.id_pdf_info_back){
            dismiss();
        }
    }

    @Override
    public PopupWindowStruct setCallback(OnPopupWindowCallback callback) {
        return null;
    }

    @Override
    public PopupWindowStruct setObject(Object object) {
        return null;
    }

    @Override
    public void show(int type) {
        showAtLocation(rootview, Gravity.BOTTOM, 0, 0);
        initData();
    }
}
