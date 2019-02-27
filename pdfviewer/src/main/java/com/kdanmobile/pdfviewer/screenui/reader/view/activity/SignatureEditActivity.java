package com.kdanmobile.pdfviewer.screenui.reader.view.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.kdanmobile.kmpdfkit.annotation.signature.KMPDFSignatureEditView;
import com.kdanmobile.kmpdfkit.globaldata.Config;
import com.kdanmobile.kmpdfkit.utlis.KMBitmapUtil;
import com.kdanmobile.kmpdfkit.utlis.KMFileUtil;
import com.kdanmobile.pdfviewer.R;
import com.kdanmobile.pdfviewer.base.BaseActivity;
import com.kdanmobile.pdfviewer.screenui.reader.configs.AnnotDefaultConfig;
import com.kdanmobile.pdfviewer.screenui.reader.configs.KMReaderConfigs;
import com.kdanmobile.pdfviewer.screenui.reader.utils.BrightnessUtil;
import com.kdanmobile.pdfviewer.screenui.reader.utils.PopupWindowUtil;
import com.kdanmobile.pdfviewer.screenui.reader.view.adapter.ColorSelectAdapter;
import com.kdanmobile.pdfviewer.screenui.reader.view.listener.OnTakeOrPickPhotoCallback;
import com.kdanmobile.pdfviewer.screenui.reader.view.listener.SeekBarChangeListenerAbstract;
import com.kdanmobile.pdfviewer.screenui.widget.SuperButton;
import com.kdanmobile.pdfviewer.utils.CommonUtils;
import com.kdanmobile.pdfviewer.utils.FileUtilsExtension;
import com.kdanmobile.pdfviewer.utils.PathManager;
import com.kdanmobile.pdfviewer.utils.ScreenUtil;
import com.kdanmobile.pdfviewer.utils.UriToPathUtil;
import com.kdanmobile.pdfviewer.utils.threadpools.SimpleBackgroundTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @classname：SignatureEditActivity
 * @author：liujiyuan
 * @date：2018/9/11 下午2:15
 * @description：编辑 sign 注释界面
 */
public class SignatureEditActivity extends BaseActivity implements View.OnClickListener, OnTakeOrPickPhotoCallback {

    public String takePhoto_tempPicPath;
    private Bitmap imageSignatureBitmap;
    private SimpleBackgroundTask createSignatureEditImageAsyncTask;
    private String file_absolutePath = "";
    private String saveSignPicturePath = "";
    private int savePicWidth = 0;
    private int savePicHeight = 0;

    private RelativeLayout idEditSignMenuRel;
    private ImageButton idEditSignBack;
    private ImageButton idEditSignChoosePhoto;
    private SuperButton idEditSignDone;
    private RecyclerView idEditSignColorLv;
    private SeekBar idEditSignSizeBar;
    private TextView idEditSignSizeValue;
    private KMPDFSignatureEditView idEditSignEditView;
    private TextView idEditSignAddText;
    private ImageButton idEditSignDeleteSign;

    private ColorSelectAdapter colorSelectAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_signature);

        idEditSignMenuRel = findViewById(R.id.id_edit_sign_menu_rel);
        idEditSignBack = findViewById(R.id.id_edit_sign_back);
        idEditSignChoosePhoto = findViewById(R.id.id_edit_sign_choosePhoto);
        idEditSignDone = findViewById(R.id.id_edit_sign_done);

        idEditSignColorLv = findViewById(R.id.id_edit_sign_color_lv);
        idEditSignSizeBar = findViewById(R.id.id_edit_sign_size_bar);
        idEditSignSizeValue = findViewById(R.id.id_edit_sign_size_value);

        idEditSignEditView = findViewById(R.id.id_edit_sign_edit_view);
        idEditSignAddText = findViewById(R.id.id_edit_sign_addText);
        idEditSignDeleteSign = findViewById(R.id.id_edit_sign_deleteSign);

        /****** 适配主题颜色 ******/
        idEditSignDone.setBackgroundColor(Color.TRANSPARENT);
        idEditSignDone.setShapeStrokeColor(ContextCompat.getColor(this, R.color.white_color));
        idEditSignDone.setUseShape();

        /****** 初始化seekBar的值 ******/
        idEditSignSizeBar.setProgress(AnnotDefaultConfig.SignEdit_PenSize - 1);
        idEditSignSizeValue.setText(String.valueOf(AnnotDefaultConfig.SignEdit_PenSize));

        /****** 颜色选择适配器 ******/
        idEditSignColorLv.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false));
        idEditSignColorLv.setItemAnimator(new DefaultItemAnimator());
        colorSelectAdapter = new ColorSelectAdapter(AnnotDefaultConfig.SignColorArr);
        colorSelectAdapter.setSelectIndex(AnnotDefaultConfig.SignEdit_ColorId);
        idEditSignColorLv.setAdapter(colorSelectAdapter);

        /****** 初始化  EditSignEditView 的属性值******/
        idEditSignEditView.setAttribute(AnnotDefaultConfig.SignEdit_PenSize, AnnotDefaultConfig.SignEdit_PenColor, 255);
        idEditSignAddText.setVisibility(View.VISIBLE);

        initListener();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initListener(){
        /****** 给seekBar控件添加监听器 ******/
        idEditSignSizeBar.setOnSeekBarChangeListener(new SeekBarChangeListenerAbstract(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                super.onProgressChanged(seekBar, progress, fromUser);
                idEditSignSizeValue.setText(""+(progress + 1));
                AnnotDefaultConfig.SignEdit_PenSize = progress + 1;
                idEditSignEditView.setAttribute(AnnotDefaultConfig.SignEdit_PenSize, AnnotDefaultConfig.SignEdit_PenColor, 255);
            }
        });
        /****** 颜色选择适配器的监听事件 ******/
        colorSelectAdapter.setOnItemClickListener(position -> {
            AnnotDefaultConfig.SignEdit_PenColor = AnnotDefaultConfig.SignColorArr[position];
            AnnotDefaultConfig.SignEdit_ColorId = position;
            idEditSignEditView.setAttribute(AnnotDefaultConfig.SignEdit_PenSize, AnnotDefaultConfig.SignEdit_PenColor, 255);
        });

        /****** 各个按钮的监听 ******/
        idEditSignBack.setOnClickListener(this);
        idEditSignChoosePhoto.setOnClickListener(this);
        idEditSignDone.setOnClickListener(this);
        idEditSignDeleteSign.setOnClickListener(this);

        /****** sign编辑界面的touch监听 ******/
        idEditSignEditView.setOnTouchListener((v, event) -> {
            idEditSignAddText.setVisibility(View.GONE);
            return false;
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_edit_sign_back:
                finishActivty();
                break;
            case R.id.id_edit_sign_choosePhoto:
                /****** 打开选择图片弹框 ******/
                PopupWindowUtil.getInstance().selectPicturePopupWindow.show(0);
                break;
            case R.id.id_edit_sign_done:
                saveSignaturePicture();
                finishActivty();
                break;
            case R.id.id_edit_sign_deleteSign:
                cancelDrawSignPicture();
                idEditSignAddText.setVisibility(View.VISIBLE);
                break;
            default:
        }
    }

    @Override
    public void onTakeOrPickPhoto(int requestCode) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                /****** 指定调用相机拍照后的照片存储的路径 ******/
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                Date date = new Date(System.currentTimeMillis());
                takePhoto_tempPicPath = PathManager.getInstance().getPhotoFolderPath() + File.separator + simpleDateFormat.format(date) + ".jpeg";
                KMFileUtil.createFile(takePhoto_tempPicPath, true);
                CommonUtils.takePicture(this, CommonUtils.onGetUriBySystem(this, new File(takePhoto_tempPicPath)), requestCode);
                break;
            case GALLERY_REQUEST_CODE:
                CommonUtils.openPic(this, requestCode);
                break;
            default:
        }
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        showProgressDialog(getString(R.string.loading_image_sign), true, false);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                file_absolutePath = takePhoto_tempPicPath;
                break;
            case GALLERY_REQUEST_CODE:
                if (null != data) {
                    file_absolutePath = UriToPathUtil.getInstance().getPath(this, data.getData());
                }
                break;
            default:
        }
        stopLoadSignatureEditImageSync();
        /****** 开启图片加载任务 ******/
        createSignatureEditImageAsyncTask = new SimpleBackgroundTask<Bitmap>(this) {
            @Override
            protected Bitmap onRun() {
                imageSignatureBitmap = KMBitmapUtil.createBitmapFitRect(
                        SignatureEditActivity.this,
                        file_absolutePath,
                        ScreenUtil.getScreenHeight(SignatureEditActivity.this)/2,
                        ScreenUtil.getScreenWidth(SignatureEditActivity.this)/2);
                if(imageSignatureBitmap == null){
                    return null;
                }

                imageSignatureBitmap = KMBitmapUtil.createTransparentBitmapFromBitmap(imageSignatureBitmap, Color.WHITE);
                imageSignatureBitmap = KMBitmapUtil.compressImageForSize(imageSignatureBitmap, 100);
                return imageSignatureBitmap;
            }

            @Override
            protected void onSuccess(Bitmap result) {
                stopProgressDialog();
                if(imageSignatureBitmap == null || idEditSignEditView == null){
                    idEditSignAddText.setVisibility(View.VISIBLE);
                    return;
                }
                idEditSignAddText.setVisibility(View.GONE);
                idEditSignEditView.setPictureBitmap(imageSignatureBitmap);
                idEditSignEditView.invalidate();
            }
        };
        createSignatureEditImageAsyncTask.execute();
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * @methodName：stopLoadSignatureEditImageSync created by liujiyuan on 2018/9/11 下午3:16.
     * @description：取消图片加载任务
     */
    private void stopLoadSignatureEditImageSync() {
        if (null != createSignatureEditImageAsyncTask && !createSignatureEditImageAsyncTask.isCancelled()) {
            createSignatureEditImageAsyncTask.cancel(true);
            cancelDrawSignPicture();
        }
    }

    /**
     * @methodName：saveSignaturePicture created by liujiyuan on 2018/9/11 下午5:49.
     * @description：保存编辑的签名图片
     */
    private void saveSignaturePicture(){
        if(idEditSignEditView.getDrawPathPoints()==null && imageSignatureBitmap == null){
            return;
        }
        Bitmap signEditPic;
        if(idEditSignEditView.getDrawPathPoints()==null) {
            signEditPic = imageSignatureBitmap;
        }else{
            idEditSignEditView.setBackgroundColor(Color.TRANSPARENT);
            signEditPic = KMBitmapUtil.loadBitmapFromView(idEditSignEditView);
            signEditPic = KMBitmapUtil.cropBitmap(signEditPic, idEditSignEditView.getSignViewRect());
            savePicWidth = idEditSignEditView.getSignViewRect().width();
            savePicHeight = idEditSignEditView.getSignViewRect().height();

        }
        if(signEditPic != null){
            saveSignPicturePath = FileUtilsExtension.saveSignBitmap(signEditPic);
        }
        if(signEditPic != imageSignatureBitmap){
            signEditPic.recycle();
        }
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
        PopupWindowUtil.initUtil(this, idEditSignMenuRel);
        /****** 设置 OnTakeOrPickPhotoCallback 接口，并打开popupwindow ******/
        PopupWindowUtil.buildPopupWindow(PopupWindowUtil.PopupWindowType.TAKE_OR_PICK_PHOTO).setCallback(this);
        super.onResume();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    /**
     * @methodName：finishActivty created by liujiyuan on 2018/9/12 上午10:29.
     * @description：关闭app之前的处理
     */
    private void finishActivty() {
        Intent intent = new Intent();
        intent.putExtra("saveSignPicturePath", saveSignPicturePath);
        intent.putExtra("width", savePicWidth);
        intent.putExtra("height", savePicHeight);
        setResult(Activity.RESULT_OK, intent);
        cancelDrawSignPicture();
        finish();
    }

    /**
     * @methodName：cancleDrawSignPicture created by liujiyuan on 2018/9/11 下午3:15.
     * @description：取消 EditSignEditView 的图片绘制
     */
    private void cancelDrawSignPicture(){
        if(imageSignatureBitmap != null){
            imageSignatureBitmap.recycle();
            imageSignatureBitmap = null;
        }
        if(idEditSignEditView != null) {
            idEditSignEditView.cancelDraw();
            idEditSignEditView.invalidate();
        }
    }

    @Override
    public void onBackPressed() {
        finishActivty();
        super.onBackPressed();
    }
}
