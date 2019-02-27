package com.kdanmobile.pdfviewer.screenui.reader.view.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kdanmobile.kmpdfkit.annotation.link.listener.OnLinkInfoChangeListener;
import com.kdanmobile.kmpdfkit.manager.KMPDFFactory;
import com.kdanmobile.kmpdfkit.manager.controller.KMPDFDocumentController;
import com.kdanmobile.kmpdfkit.manager.controller.KMPDFLinkController;
import com.kdanmobile.kmpdfkit.manager.controller.KMPDFStampController;
import com.kdanmobile.kmpdfkit.manager.listener.KMPDFAnnotCallBack;
import com.kdanmobile.kmpdfkit.manager.listener.KMPDFDocumentMessageCallback;
import com.kdanmobile.kmpdfkit.pdfcommon.FilePicker;
import com.kdanmobile.kmpdfkit.pdfcommon.KMPDFReaderView;
import com.kdanmobile.pdfviewer.R;
import com.kdanmobile.pdfviewer.screenui.reader.configs.ApplicationConfig;
import com.kdanmobile.pdfviewer.utils.ToastUtil;

public class PDFSimpleReaderActivity extends Activity implements KMPDFDocumentMessageCallback, FilePicker.FilePickerSupport, KMPDFAnnotCallBack {

    private final static String SHARE = "PDF_SHARE";
    private RelativeLayout readerLayout;
    private AlertDialog.Builder mAlertBuilder;
    private KMPDFFactory kmpdfFactory;
    private KMPDFReaderView mDocView;
    private KMPDFStampController pdfStampController;
    private KMPDFLinkController pdfLinkController;
    protected KMPDFDocumentController pdfDocumentController;
    private EditText mPasswordView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kmpdf);

        mAlertBuilder = new AlertDialog.Builder(this);

        Intent intent = getIntent();
        String filePath = intent.getStringExtra("file_absolutepath");
        Uri uri = Uri.parse(filePath);
        int errorId;
        if((errorId = KMPDFFactory.verifyLicense(this, ApplicationConfig.READER_LICENSE, ApplicationConfig.RSA_MESSAGE)) != 0){
            switch (errorId){
                case 1001:
                    ToastUtil.showToast(this, "License key can not be empty.", Toast.LENGTH_SHORT);
                    break;
                case 1002:
                    ToastUtil.showToast(this, "The license you have entered is not correct. Please enter valid license.", Toast.LENGTH_SHORT);
                    break;
                case 1003:
                    ToastUtil.showToast(this, "The license you have entered is not correct. Please enter valid license.", Toast.LENGTH_SHORT);
                    break;
                case 1004:
                    ToastUtil.showToast(this, "The license you have entered is not correct. Please enter valid license.", Toast.LENGTH_SHORT);
                    break;
                case 1005:
                    ToastUtil.showToast(this, "The license you have entered is not correct. \\n Your license key has expired.", Toast.LENGTH_SHORT);
                    break;
                case 1006:
                    ToastUtil.showToast(this, "The license you have entered is not correct. \\n Each KMPDFKit license is bound to a specific app bundle id.", Toast.LENGTH_SHORT);
                    break;
                case 1007:
                    ToastUtil.showToast(this, "The license you have entered is not correct. \\n Please enter valid KMPDFKitFeatureModuleType.", Toast.LENGTH_SHORT);
                    break;
            }
            finish();
        }
        kmpdfFactory = KMPDFFactory.open(this, uri, intent.getType());
        if(kmpdfFactory == null){
            finish();
        }else if(kmpdfFactory.needPassWord()){
            //这里去做密码认证操作
            onDocumentRequestInputPassword();
        }else if(kmpdfFactory.init()){
            //初始化成功
            initUI();
        }else{
            //初始化失败
            finish();
        }
    }

    private void initUI(){
        createReaderView();
        initPDFManager();

        // Stick the document view and the buttons overlay into a parent view
        readerLayout = (RelativeLayout) findViewById(R.id.reader_layout);
        readerLayout.addView(mDocView);

        //保存当前页码，下次进来直接跳到该页面
        SharedPreferences prefs = getSharedPreferences(SHARE, Context.MODE_PRIVATE);
        pdfDocumentController.gotoPage(prefs.getInt("page"+ kmpdfFactory.getFileName(), 0));
    }

    /****** 打开pdf文档时需要输入密码 ******/
    public void onDocumentRequestInputPassword() {
        mPasswordView = new EditText(this);
        mPasswordView.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
        mPasswordView.setTransformationMethod(new PasswordTransformationMethod());

        AlertDialog alert = mAlertBuilder.create();
        alert.setTitle(R.string.enter_password);
        alert.setView(mPasswordView);
        alert.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.simple_confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (kmpdfFactory.authenticatePassword(mPasswordView.getText().toString())) {
                            kmpdfFactory.init();
                            initUI();
                        } else {
                            onDocumentRequestInputPassword();
                        }
                    }
                });
        alert.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.simple_cancel),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        alert.show();
    }

    private void initPDFManager() {
        kmpdfFactory.setReaderView(mDocView);
        pdfStampController = (KMPDFStampController) kmpdfFactory.getController(KMPDFFactory.ControllerType.STAMP);
        pdfLinkController = (KMPDFLinkController) kmpdfFactory.getController(KMPDFFactory.ControllerType.LINK);
        pdfDocumentController = (KMPDFDocumentController) kmpdfFactory.getController(KMPDFFactory.ControllerType.DOCUMENT);
        pdfLinkController.setLinkCreateCallback(this);
        pdfStampController.setCreateStampSuccCallback(this);
    }

    private void createReaderView() {
        // Now create the UI.
        // First create the document view
        mDocView = new KMPDFReaderView(this);
    }

    @Override
    public void onBackPressed() {
        if (pdfDocumentController.hasChanges()) {
            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == AlertDialog.BUTTON_POSITIVE) {
                        try {
                            pdfDocumentController.save();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    finish();
                }
            };
            AlertDialog alert = mAlertBuilder.create();
            alert.setTitle(getString(R.string.save_changes_dialog_title));
            alert.setMessage(getString(R.string.document_has_changes_save_them_));
            alert.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.simple_yes), listener);
            alert.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.simple_no), listener);
            alert.show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        if(kmpdfFactory != null) {
            kmpdfFactory.onStart();
        }
        super.onStart();
    }

    @Override
    protected void onResume() {
        if(kmpdfFactory != null) {
            kmpdfFactory.onResume();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if(kmpdfFactory != null) {
            kmpdfFactory.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        if(kmpdfFactory != null) {
            kmpdfFactory.onStop();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if(kmpdfFactory != null) {
            kmpdfFactory.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void onStampCreateFinish(boolean isSuccess) {

    }

    @Override
    public void onDocumentLoadError(ERROR_CODE errorId, String message) {
        switch (errorId){
            case OPEN_FILE_ERROR:
                Toast.makeText(PDFSimpleReaderActivity.this, R.string.open_file_error, Toast.LENGTH_LONG).show();
                break;
            case GET_CORE_ERROR:
                Toast.makeText(PDFSimpleReaderActivity.this, R.string.get_core_error, Toast.LENGTH_LONG).show();
                break;
            case VERIFY_LICENSE_ERROR:
                Toast.makeText(PDFSimpleReaderActivity.this, R.string.verify_license_failed, Toast.LENGTH_LONG).show();
                break;
            case OPEN_BUFFER_ERROR:
                Toast.makeText(PDFSimpleReaderActivity.this, R.string.get_stream_of_pdf_failed, Toast.LENGTH_LONG).show();
                break;
            case CANNOT_OPEN_FILE:
                Toast.makeText(PDFSimpleReaderActivity.this, R.string.open_file_failed, Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onDocumentCanProof(boolean canProof) {

    }

    @Override
    public void onDocumentIsProofing(boolean isProofing) {

    }

    @Override
    public void onLinkInitAttr(OnLinkInfoChangeListener onLinkInfoChangeListener) {

    }

    @Override
    public void onLinkEditAttr(OnLinkInfoChangeListener onLinkInfoChangeListener, OnLinkInfoChangeListener.KMPDFLinkType i, String s) {

    }

    @Override
    public void performPickFor(FilePicker picker) {

    }

    @Override
    public void onError(ErrorId errorId) {
        switch (errorId){
            case NOT_A_NUMBER:
                Toast.makeText(PDFSimpleReaderActivity.this, R.string.please_input_a_number, Toast.LENGTH_LONG).show();
                break;
            case NO_EMAIL_APP:
                Toast.makeText(PDFSimpleReaderActivity.this, R.string.no_browser, Toast.LENGTH_LONG).show();
                break;
            case NO_BROWSE_APP:
                Toast.makeText(PDFSimpleReaderActivity.this, R.string.no_email_app, Toast.LENGTH_LONG).show();
                break;
            case NO_CONTENT_TO_PASTE:
                Toast.makeText(PDFSimpleReaderActivity.this, R.string.no_content_to_paste, Toast.LENGTH_LONG).show();
                break;
        }
    }
}
