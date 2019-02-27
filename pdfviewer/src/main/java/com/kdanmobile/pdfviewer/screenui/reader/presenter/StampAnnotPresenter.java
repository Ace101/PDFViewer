package com.kdanmobile.pdfviewer.screenui.reader.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.text.TextUtils;

import com.kdanmobile.pdfviewer.R;
import com.kdanmobile.kmpdfkit.annotation.bean.KMPDFAnnotationBean;
import com.kdanmobile.kmpdfkit.annotation.bean.KMPDFStampAnnotationBean;
import com.kdanmobile.kmpdfkit.annotation.stamp.StampConfig;
import com.kdanmobile.kmpdfkit.annotation.stamp.TextStampConfig;
import com.kdanmobile.kmpdfkit.utlis.KMFileUtil;
import com.kdanmobile.pdfviewer.base.mvp.presenter.BaseMvpPresenter;
import com.kdanmobile.pdfviewer.event.MessageEvent;
import com.kdanmobile.pdfviewer.screenui.reader.constract.StampAnnotConstract;
import com.kdanmobile.pdfviewer.screenui.reader.utils.KMReaderSpUtils;
import com.kdanmobile.pdfviewer.screenui.reader.view.activity.StampAnnotActivity;
import com.kdanmobile.pdfviewer.screenui.reader.view.adapter.ImageStampRecycleViewAdapter;
import com.kdanmobile.pdfviewer.screenui.reader.view.adapter.StandardStampRecycleViewAdapter;
import com.kdanmobile.pdfviewer.screenui.reader.view.adapter.TextStampRecycleViewAdapter;
import com.kdanmobile.pdfviewer.screenui.reader.view.been.ImageItem;
import com.kdanmobile.pdfviewer.screenui.reader.view.listener.OnTakeOrPickPhotoCallback;
import com.kdanmobile.pdfviewer.utils.CommonUtils;
import com.kdanmobile.pdfviewer.utils.PathManager;
import com.kdanmobile.pdfviewer.utils.ScreenUtil;
import com.kdanmobile.pdfviewer.utils.ToastUtil;
import com.kdanmobile.pdfviewer.utils.UriToPathUtil;
import com.kdanmobile.pdfviewer.utils.eventbus.ConstantBus;
import com.kdanmobile.pdfviewer.utils.eventbus.EventBusUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @classname：StampAnnotPresenter
 * @author：liujiyuan
 * @date：2018/9/3 下午6:01
 * @description：页面注释功能实现
 */
public class StampAnnotPresenter extends BaseMvpPresenter<StampAnnotActivity> implements StampAnnotConstract.IPresenter, OnTakeOrPickPhotoCallback {
    public StandardStampRecycleViewAdapter standardStampRecycleViewAdapter;
    public TextStampRecycleViewAdapter textStampRecycleViewAdapter;
    public ImageStampRecycleViewAdapter imageStampRecycleViewAdapter;
    public String takePhoto_tempPicPath;
    private Context context;

    @Override
    public void onInit(StampAnnotActivity mView) {
        EventBusUtils.getInstance().register(this);

        context = mView.getApplicationContext();
        /****** 本地存储中恢复数据集合 ******/
        final List<TextStampConfig> stampConfigList = new ArrayList<>();
        final List<ImageItem> imageStampList = new ArrayList<>();
        try {
            stampConfigList.addAll(KMReaderSpUtils.getInstance().getTextStampMessage());
            imageStampList.addAll(KMReaderSpUtils.getInstance().getImageStampMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            /****** 标准stamp内容 ******/
            standardStampRecycleViewAdapter = new StandardStampRecycleViewAdapter(StampConfig.data);
            /****** 自定义text stamp的内容 ******/
            textStampRecycleViewAdapter = new TextStampRecycleViewAdapter(stampConfigList);
            /****** 自定义image stamp的内容 ******/
            imageStampRecycleViewAdapter = new ImageStampRecycleViewAdapter(context, imageStampList);
            getMvpView().setAdapter();
            getMvpView().setDisStampData();
            setOnItemClickListener();
        }
    }

    /**
     * @methodName：setOnItemClickListener created by liujiyuan on 2018/9/11 上午10:26.
     * @description：设置适配器的item点击事件
     */
    private void setOnItemClickListener() {
        /****** 设置 standard stamp 的item点击事件 ******/
        standardStampRecycleViewAdapter.setOnStandardStampItemClickListener((view, position, resId) -> {
            KMPDFStampAnnotationBean.StandardStamp standardStamp = new KMPDFStampAnnotationBean.StandardStamp(resId);
            KMPDFAnnotationBean kmpdfStampAnnotationBean = new KMPDFStampAnnotationBean("", KMPDFStampAnnotationBean.StampType.STANDARD, standardStamp);
            EventBusUtils.getInstance().post(new MessageEvent<>(ConstantBus.SET_STAMP_ANNOTATION_ATTR, kmpdfStampAnnotationBean));
            getMvpView().finish();
        });
        /****** 设置 text stamp 的item点击事件 ******/
        textStampRecycleViewAdapter.setTextStampItemClickListener((view, position, textStampConfig, stampTextView) -> {
            KMPDFStampAnnotationBean.TextStamp textStamp = new KMPDFStampAnnotationBean.TextStamp(new Rect(0, 0, stampTextView.getWidth(), stampTextView.getHeight()), textStampConfig);
            KMPDFAnnotationBean kmpdfStampAnnotationBean = new KMPDFStampAnnotationBean("", KMPDFStampAnnotationBean.StampType.TEXT, textStamp);
            EventBusUtils.getInstance().post(new MessageEvent<>(ConstantBus.SET_STAMP_ANNOTATION_ATTR, kmpdfStampAnnotationBean));
            getMvpView().finish();
        });
        /****** 设置 image stamp 的item点击事件 ******/
        imageStampRecycleViewAdapter.setOnImageStampItemClickListener((view, position, path, imageView) -> {
            getMvpView().showProgressDialog(context.getResources().getString(R.string.loading_image_stamp), true, false);
            KMPDFStampAnnotationBean.ImageStamp imageStamp = new KMPDFStampAnnotationBean.ImageStamp(path, b -> {
                getMvpView().stopProgressDialog();
                getMvpView().finish();
            });
            KMPDFAnnotationBean kmpdfStampAnnotationBean = new KMPDFStampAnnotationBean("", KMPDFStampAnnotationBean.StampType.IMAGE, imageStamp);
            EventBusUtils.getInstance().post(new MessageEvent<>(ConstantBus.SET_STAMP_ANNOTATION_ATTR, kmpdfStampAnnotationBean));

        });
    }

    /**
     * @methodName：editTextStampData created by liujiyuan on 2018/9/4 下午4:27.
     * @description：textStampRecycleViewAdapter进入编辑模式
     */
    @Override
    public void editStampData() {
        if (null != textStampRecycleViewAdapter) {
            textStampRecycleViewAdapter.setMode(true);
            textStampRecycleViewAdapter.notifyItemRangeChanged(0, textStampRecycleViewAdapter.getItemCount(), "checkbox_show");
        }
        if (null != imageStampRecycleViewAdapter) {
            imageStampRecycleViewAdapter.setMode(true);
            imageStampRecycleViewAdapter.notifyItemRangeChanged(0, imageStampRecycleViewAdapter.getItemCount(), "checkbox_show");
        }
    }

    /**
     * @methodName：deleteTextStampData created by liujiyuan on 2018/9/4 下午4:11.
     * @description：删除text stamp
     */
    @Override
    public void deleteStampData(boolean isDelete) {
        if (null == textStampRecycleViewAdapter || null == imageStampRecycleViewAdapter) {
            return;
        }

        if (isDelete) {
            final int size_textStampList = textStampRecycleViewAdapter.getItemCount();
            for (int i = size_textStampList - 1; i >= 0; i--) {
                TextStampConfig textStampConfig = textStampRecycleViewAdapter.getItemData(i);
                if (textStampConfig != null && textStampConfig.isChecked){
                    textStampRecycleViewAdapter.onDeleteData(i);
                }
            }

            final int size_imageStampList = imageStampRecycleViewAdapter.getItemCount();
            for (int j = size_imageStampList - 1; j >= 0; j--) {
                ImageItem item = imageStampRecycleViewAdapter.getItemData(j);
                if (item != null && item.isChecked) {
                    imageStampRecycleViewAdapter.onDeleteData(j);
                }
            }
        }
        if (isAttached()) {
            getMvpView().setDisStampData();
        }
    }

    @Override
    public void onStop(boolean isFinishing) {
        if (isFinishing) {
            /****** 保存数据到本地 ******/
            try {
                if (null != textStampRecycleViewAdapter) {
                    List<TextStampConfig> stampConfigList = textStampRecycleViewAdapter.getData();
                    KMReaderSpUtils.getInstance().saveTextStampMessage(stampConfigList);

                }
                if (null != imageStampRecycleViewAdapter) {
                    List<ImageItem> imageStampList = imageStampRecycleViewAdapter.getData();
                    KMReaderSpUtils.getInstance().saveImageStampMessage(imageStampList);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroyPresenter() {
        EventBusUtils.getInstance().unRegister(this);
        super.onDestroyPresenter();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent<Object> event) {
        if (!isAttached()) {
            return;
        }

        String tag = event.getTag();
        switch (tag) {
            case ConstantBus.CREATE_TEXT_STAMP:
                TextStampConfig textStampConfig = (TextStampConfig) event.getEvent();
                textStampRecycleViewAdapter.onAddData(textStampConfig);
                getMvpView().changeFloatingAddImage(false);
                getMvpView().setDisStampData();
                break;
            case ConstantBus.BACK_STAMP_ACTIVITY:
                getMvpView().changeFloatingAddImage(false);
                break;
            default:
        }
    }

    /**
     * @methodName：onTakeOrPickPhoto created by liujiyuan on 2018/9/5 下午4:45.
     * @description：SelectPicturePopupWindow的接口回调方法
     */
    @Override
    public void onTakeOrPickPhoto(int requestCode) {
        if (!isAttached()) {
            return;
        }
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                /****** 指定调用相机拍照后的照片存储的路径 ******/
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                Date date = new Date(System.currentTimeMillis());
                takePhoto_tempPicPath = PathManager.getInstance().getPhotoFolderPath() + File.separator + simpleDateFormat.format(date) + ".jpeg";
                KMFileUtil.createFile(takePhoto_tempPicPath, true);
                CommonUtils.takePicture(getMvpView(), CommonUtils.onGetUriBySystem(context, new File(takePhoto_tempPicPath)), requestCode);
                break;
            case GALLERY_REQUEST_CODE:
                CommonUtils.openPic(getMvpView(), requestCode);
                break;
            default:
        }
        getMvpView().changeFloatingAddImage(false);
    }

    @Override
    public void getPictureUri(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        String file_absolutepath = "";
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                file_absolutepath = takePhoto_tempPicPath;
                break;
            case GALLERY_REQUEST_CODE:
                if (null != data) {
                    file_absolutepath = UriToPathUtil.getInstance().getPath(context, data.getData());
                }
                break;
            default:
        }
        if (!TextUtils.isEmpty(file_absolutepath)) {
            imageStampRecycleViewAdapter.onAddData(new ImageItem(file_absolutepath, false, ScreenUtil.getScreenWidth(context),ScreenUtil.getScreenHeight(context)));
            getMvpView().setDisStampData();
        }
    }
}