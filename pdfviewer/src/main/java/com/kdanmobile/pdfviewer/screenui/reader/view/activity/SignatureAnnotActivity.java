package com.kdanmobile.pdfviewer.screenui.reader.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.kdanmobile.kmpdfkit.annotation.bean.KMPDFSignatureAnnotationBean;
import com.kdanmobile.pdfviewer.R;
import com.kdanmobile.pdfviewer.base.BaseActivity;
import com.kdanmobile.pdfviewer.event.MessageEvent;
import com.kdanmobile.pdfviewer.screenui.reader.configs.KMReaderConfigs;
import com.kdanmobile.pdfviewer.screenui.reader.utils.BrightnessUtil;
import com.kdanmobile.pdfviewer.screenui.reader.utils.KMReaderSpUtils;
import com.kdanmobile.pdfviewer.screenui.reader.view.adapter.SignImageRecycleViewAdapter;
import com.kdanmobile.pdfviewer.screenui.reader.view.been.ImageItem;
import com.kdanmobile.pdfviewer.utils.FileUtilsExtension;
import com.kdanmobile.pdfviewer.utils.GlobalConfigs;
import com.kdanmobile.pdfviewer.utils.eventbus.ConstantBus;
import com.kdanmobile.pdfviewer.utils.eventbus.EventBusUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @classname：SignatureAnnotActivity
 * @author：liujiyuan
 * @date：2018/9/11 下午4:22
 * @description：签名列表选择界面
 */
public class SignatureAnnotActivity extends BaseActivity implements View.OnClickListener{
    private final static int REQUESTCODE = 1;
    private final static int VIEW_TYPE = 0X1010;
    private final static int EDIT_TYPE = 0X1020;
    private final int EDIT_TYPE_UNALLSELECT = 0X1030;
    private final int EDIT_TYPE_ALLSELECT = 0X1040;

    private int menu_type = VIEW_TYPE;
    private int edit_type = EDIT_TYPE_UNALLSELECT;

    private RecyclerView idSignAnnotRecyclerView;
    private Button idSignAnnotDelete;
    private FloatingActionButton idSignAnnotFloatingButtonAdd;
    private RelativeLayout idSignAnnotListDis;

    private SignImageRecycleViewAdapter signImageRecycleViewAdapter;
    private List<ImageItem> deleteImageSignList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature_annotation);
        setActivityTitle();

        idSignAnnotRecyclerView = findViewById(R.id.id_sign_annot_recyclerView);
        idSignAnnotDelete = findViewById(R.id.id_sign_annot_delete);
        idSignAnnotFloatingButtonAdd = findViewById(R.id.id_sign_annot_floating_button_add);
        idSignAnnotListDis = findViewById(R.id.id_sign_annot_list_dis);

        /****** 给悬浮按钮设置主题色 ******/
        idSignAnnotFloatingButtonAdd.getBackground().setColorFilter(GlobalConfigs.THEME_COLOR, PorterDuff.Mode.SRC);

        setFunctionButton(false);
        /****** 本地存储中恢复数据集合 ******/
        final List<ImageItem> imageSignList = new ArrayList<>();
        try {
            imageSignList.addAll(KMReaderSpUtils.getInstance().getImageSignMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            signImageRecycleViewAdapter = new SignImageRecycleViewAdapter(this, imageSignList);
            updateDisLayout();
        }
        /****** 设置 RecyclerView 的适配器******/
        idSignAnnotRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        idSignAnnotRecyclerView.setItemAnimator(new DefaultItemAnimator());
        idSignAnnotRecyclerView.setAdapter(signImageRecycleViewAdapter);

        /****** 给 adapter 设置item的点击监听 ******/
        signImageRecycleViewAdapter.setOnImageSignItemClickListener(new SignImageRecycleViewAdapter.OnImageSignItemClickListener(){
            @Override
            public void onItemClick(View view, int position, String path, ImageView imageView) {
                showProgressDialog(getString(R.string.loading_image_sign), true, false);
                KMPDFSignatureAnnotationBean kmpdfSignatureAnnotationBean = new KMPDFSignatureAnnotationBean("", path, () -> stopProgressDialog());
                EventBusUtils.getInstance().post(new MessageEvent<>(ConstantBus.SET_SIGN_ANNOTATION_ATTR, kmpdfSignatureAnnotationBean));
                finish();
            }

            @Override
            public void onItemSelect(ImageItem imageItem) {
                setActivityTitle();
            }
        });
        /****** 给 button 设置监听 ******/
        idSignAnnotDelete.setOnClickListener(this);
        idSignAnnotFloatingButtonAdd.setOnClickListener(this);
    }

    /**
     * @methodName：setFucntionButton created by liujiyuan on 2018/9/12 上午9:50.
     * @description：设置功能键的显示
     */
    private void setFunctionButton(boolean isEdit){
        if(isEdit){
            idSignAnnotDelete.setVisibility(View.VISIBLE);
            idSignAnnotFloatingButtonAdd.setVisibility(View.GONE);
        }else{
            idSignAnnotDelete.setVisibility(View.GONE);
            idSignAnnotFloatingButtonAdd.setVisibility(View.VISIBLE);
        }
    }

    /**
     * @methodName：updateDisLayout created by liujiyuan on 2018/9/25 下午1:38.
     * @description：更新缺省文档
     */
    private void updateDisLayout(){
        List<ImageItem> imageSignList = signImageRecycleViewAdapter.getData();
        if(imageSignList == null ||imageSignList.size() == 0){
            idSignAnnotListDis.setVisibility(View.VISIBLE);
            idSignAnnotRecyclerView.setVisibility(View.GONE);
        }else{
            idSignAnnotListDis.setVisibility(View.GONE);
            idSignAnnotRecyclerView.setVisibility(View.VISIBLE);
        }
        invalidateOptionsMenu();
    }

    /**
     * @methodName：setActivityTitle created by liujiyuan on 2018/9/12 上午9:49.
     * @description：根据选择的item数量，设置activity的标题
     */
    private void setActivityTitle(){
        if (menu_type == VIEW_TYPE) {
            getSupportActionBar().setTitle(R.string.sign_annotation_title);
        }else{
            int selectItemCount = 0;
            List<ImageItem> imageSignList = signImageRecycleViewAdapter.getData();
            if(imageSignList != null && imageSignList.size() >0) {
                for (ImageItem item : imageSignList) {
                    if (item.isChecked) {
                        selectItemCount++;
                    }
                }
            }
            String title = selectItemCount + " " + getResources().getString(R.string.pdf_common_selected);
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_sign_annot_delete:
                int size_imageStampList = signImageRecycleViewAdapter.getItemCount();
                for (int j = size_imageStampList; j >= 0; j--) {
                    ImageItem item = signImageRecycleViewAdapter.getItemData(j);
                    if (item != null && item.isChecked) {
                        deleteImageSignList.add(item);
                        signImageRecycleViewAdapter.onDeleteData(j);
                    }
                }
                if(signImageRecycleViewAdapter.getItemCount() == 0){
                    reBackToViewType();
                    updateDisLayout();
                }else{
                    setActivityTitle();
                }
                break;
            case R.id.id_sign_annot_floating_button_add:
                startActivityForResult(new Intent(this, SignatureEditActivity.class), REQUESTCODE);
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
            String saveSignPicturePath = data.getStringExtra("saveSignPicturePath");
            int width = data.getIntExtra("width", 0);
            int height = data.getIntExtra("height", 0);
            if(!TextUtils.isEmpty(saveSignPicturePath)){
                signImageRecycleViewAdapter.onAddData(new ImageItem(saveSignPicturePath, false, width, height));
            }
            updateDisLayout();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (menu_type == VIEW_TYPE) {
            List<ImageItem> imageSignList = signImageRecycleViewAdapter.getData();
            if(imageSignList == null ||imageSignList.size() == 0){
                menu.findItem(R.id.edit_menu_edit).setVisible(false);
                menu.findItem(R.id.edit_menu_no_edit).setVisible(true);
            }else{
                menu.findItem(R.id.edit_menu_edit).setVisible(true);
                menu.findItem(R.id.edit_menu_no_edit).setVisible(false);
            }
            menu.findItem(R.id.edit_menu_unSelect).setVisible(false);
        } else {
            menu.findItem(R.id.edit_menu_edit).setVisible(false);
            menu.findItem(R.id.edit_menu_no_edit).setVisible(false);
            menu.findItem(R.id.edit_menu_unSelect).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /****** 编辑菜单图标点击事件 ******/
            case R.id.edit_menu_edit:
                if (menu_type == VIEW_TYPE) {
                    menu_type = EDIT_TYPE;
                    setAdapterMode(true);
                    invalidateOptionsMenu();
                    setFunctionButton(true);
                }else{
                    menu_type = VIEW_TYPE;
                    setAdapterMode(false);
                    invalidateOptionsMenu();
                    setFunctionButton(false);
                }
                break;
            /****** 全选菜单图标点击事件 ******/
            case R.id.edit_menu_unSelect:
                if (menu_type == EDIT_TYPE) {
                    if (edit_type == EDIT_TYPE_UNALLSELECT) {
                        edit_type = EDIT_TYPE_ALLSELECT;
                        signImageRecycleViewAdapter.selectAllItems(true);
                        item.setIcon(R.drawable.ic_quanxuan_in_white);
                    } else {
                        edit_type = EDIT_TYPE_UNALLSELECT;
                        signImageRecycleViewAdapter.selectAllItems(false);
                        item.setIcon(R.drawable.ic_quanxuan_no_white);
                    }
                }
                break;
            default:
        }
        setActivityTitle();
        return super.onOptionsItemSelected(item);
    }

    /**
     * @methodName：setAdapterMode created by liujiyuan on 2018/9/12 上午11:33.
     * @description：设置adapter的状态，控制显示或隐藏checkBox 控件
     */
    private void setAdapterMode(boolean isEdit){
        if(signImageRecycleViewAdapter != null) {
            signImageRecycleViewAdapter.setMode(isEdit);
            if (isEdit) {
                signImageRecycleViewAdapter.notifyItemRangeChanged(0, signImageRecycleViewAdapter.getItemCount(), "checkbox_show");
            }else{
                signImageRecycleViewAdapter.notifyItemRangeChanged(0, signImageRecycleViewAdapter.getItemCount(), "checkbox_hide");
            }
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
        super.onResume();
    }

    @Override
    public void onStop() {
        /****** 保存数据到本地 ******/
        try {
            if (null != signImageRecycleViewAdapter) {
                List<ImageItem> imageSignList = signImageRecycleViewAdapter.getData();
                KMReaderSpUtils.getInstance().saveImageSignMessage(imageSignList);
                deleteLocalSignPicture();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStop();
    }

    /**
     * @methodName：deleteLoacalSignPicture created by liujiyuan on 2018/9/18 下午2:24.
     * @description：删除本地保存的签名图片
     */
    private void deleteLocalSignPicture(){
        try {
            if (deleteImageSignList != null && deleteImageSignList.size() > 0) {
                for (ImageItem item : deleteImageSignList) {
                    FileUtilsExtension.deleteFile(item.filePath);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        deleteImageSignList.clear();
    }

    @Override
    public void onBackPressed() {
        if (menu_type == EDIT_TYPE) {
            reBackToViewType();
            updateDisLayout();
        }else {
            EventBusUtils.getInstance().post(new MessageEvent<>(ConstantBus.SET_SIGN_ANNOTATION_ATTR, null));
            super.onBackPressed();
        }
    }

    /**
     * @methodName：reBackToViewType created by liujiyuan on 2018/9/25 下午1:39.
     * @description：设置回到浏览签名状态
     */
    private void reBackToViewType() {
        menu_type = VIEW_TYPE;
        setAdapterMode(false);
        invalidateOptionsMenu();
        setFunctionButton(false);
        setActivityTitle();
    }
}
