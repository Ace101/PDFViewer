package com.kdanmobile.pdfviewer.screenui.reader.view.activity;

import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.kdanmobile.pdfviewer.R;
import com.kdanmobile.pdfviewer.base.mvp.factory.CreatePresenter;
import com.kdanmobile.pdfviewer.base.mvp.view.AbstractMvpAppCompatActivity;
import com.kdanmobile.pdfviewer.event.MessageEvent;
import com.kdanmobile.pdfviewer.screenui.reader.configs.KMReaderConfigs;
import com.kdanmobile.pdfviewer.screenui.reader.constract.EditPageConstract;
import com.kdanmobile.pdfviewer.screenui.reader.presenter.EditPagePresenter;
import com.kdanmobile.pdfviewer.screenui.reader.utils.BrightnessUtil;
import com.kdanmobile.pdfviewer.screenui.reader.utils.KMReaderSpUtils;
import com.kdanmobile.pdfviewer.screenui.reader.view.adapter.EditPageRecyclerViewAdapter;
import com.kdanmobile.pdfviewer.screenui.reader.view.listener.OnRecyclerItemClickListener;
import com.kdanmobile.pdfviewer.screenui.reader.view.listener.drag.ProItemTouchHelper;
import com.kdanmobile.pdfviewer.utils.Constants;
import com.kdanmobile.pdfviewer.utils.eventbus.ConstantBus;
import com.kdanmobile.pdfviewer.utils.eventbus.EventBusUtils;
import com.kdanmobile.pdfviewer.utils.glide.GlideLoadUtil;
import com.kdanmobile.pdfviewer.utils.sputils.SharedPreferencesSava;

import java.util.Map;

/**
 * @classname：EditPageActivity
 * @author：liujiyuan
 * @date：2018/8/29 上午11:05
 * @description：页面编辑界面
 */
@CreatePresenter(EditPagePresenter.class)
public class EditPageActivity extends AbstractMvpAppCompatActivity<EditPageActivity, EditPagePresenter> implements EditPageConstract.IView, View.OnClickListener {

    public final static int VIEW_TYPE = 0X1010;
    public final static int EDIT_TYPE = 0X1020;
    private final int EDIT_TYPE_UNALLSELECT = 0X1030;
    private final int EDIT_TYPE_ALLSELECT = 0X1040;
    private final int SUCCESS_EDIT = 0X1001;

    private int menu_type = VIEW_TYPE;
    private int edit_type = EDIT_TYPE_UNALLSELECT;

    private EditPageRecyclerViewAdapter editPageAdapter;
    private int lastClickPosition;

    private RecyclerView idEditPageRecyclerView;
    private LinearLayout idEditPageFunction;
    private Button idEditPageFunctionRotate;
    private Button idEditPageFunctionSplit;
    private Button idEditPageFunctionDelete;
    private View idEditPageBlank;

    @Override
    public int onLayoutId() {
        return R.layout.activity_edit_page;
    }

    @Override
    public boolean onInitView() {
        setActivityTitle();
        idEditPageRecyclerView = findViewById(R.id.id_edit_page_recyclerView);
        idEditPageFunction = findViewById(R.id.id_edit_page_function);

        idEditPageFunctionRotate = findViewById(R.id.id_edit_page_function_rotate);
        idEditPageFunctionSplit = findViewById(R.id.id_edit_page_function_split);
        idEditPageFunctionDelete = findViewById(R.id.id_edit_page_function_delete);
        idEditPageBlank = findViewById(R.id.id_edit_page_blank);
        idEditPageBlank.setVisibility(View.GONE);

        idEditPageFunctionRotate.setOnClickListener(this);
        idEditPageFunctionSplit.setOnClickListener(this);
        idEditPageFunctionDelete.setOnClickListener(this);
        setFunctionButton(false);

        showHint();
        return true;

    }

    @Override
    public void setAdapter(final EditPageRecyclerViewAdapter editPageAdapter) {
        this.editPageAdapter = editPageAdapter;
        idEditPageRecyclerView.setLayoutManager(new GridLayoutManager(idEditPageRecyclerView.getContext(), getMvpPresenter().recyclerView_columns));
        idEditPageRecyclerView.setAdapter(editPageAdapter);
        idEditPageRecyclerView.scrollToPosition(getMvpPresenter().currentPage);

        /****** 滑动拖拽效果 ******/
        ProItemTouchHelper itemTouchHelper = new ProItemTouchHelper(editPageAdapter);
        itemTouchHelper.attachToRecyclerView(idEditPageRecyclerView);
        itemTouchHelper.setDragEnable(true);
        itemTouchHelper.setSwapEnable(false);
        itemTouchHelper.setSwipeEnable(false);

        /****** 设置RecyclerView的item点击监听器 ******/
        idEditPageRecyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(idEditPageRecyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                if (null == editPageAdapter) {
                    return;
                }
                editPageAdapter.setItemClick(vh.getAdapterPosition());
                editPageAdapter.notifyItemChanged(lastClickPosition);
                editPageAdapter.notifyItemChanged(vh.getAdapterPosition());
                lastClickPosition = vh.getAdapterPosition();
                if (menu_type == VIEW_TYPE) {
                    EventBusUtils.getInstance().post(new MessageEvent<>(ConstantBus.PAGEEDIT_GOTOPAGE, vh.getAdapterPosition()));
                    finish();
                }
                setActivityTitle();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (menu_type == VIEW_TYPE) {
            menu.findItem(R.id.edit_menu_edit).setVisible(true);
            menu.findItem(R.id.edit_menu_unSelect).setVisible(false);
        } else {
            menu.findItem(R.id.edit_menu_edit).setVisible(false);
            menu.findItem(R.id.edit_menu_unSelect).setVisible(true);
        }
        menu.findItem(R.id.edit_menu_no_edit).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_menu_edit:
                if (menu_type == VIEW_TYPE) {
                    menu_type = EDIT_TYPE;
                    editPageAdapter.setType(EDIT_TYPE);
                    invalidateOptionsMenu();
                    idEditPageFunction.setVisibility(View.VISIBLE);
                    idEditPageBlank.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.edit_menu_unSelect:
                if (menu_type == EDIT_TYPE) {
                    if (edit_type == EDIT_TYPE_UNALLSELECT) {
                        edit_type = EDIT_TYPE_ALLSELECT;
                        editPageAdapter.setAllClick();
                        item.setIcon(R.drawable.ic_quanxuan_in_white);
                    } else {
                        edit_type = EDIT_TYPE_UNALLSELECT;
                        editPageAdapter.setAllUnClick();
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
     * @methodName：setActivityTitle created by liujiyuan on 2018/9/12 上午9:49.
     * @description：根据选择的item数量，设置activity的标题
     */
    @Override
    public void setActivityTitle(){
        if (menu_type == VIEW_TYPE) {
            getSupportActionBar().setTitle(R.string.edit_page_title);
        }else{
            int selectItemCount = 0;
            Map<String, Boolean> selectedList = editPageAdapter.mapSelect;
            if(selectedList != null) {
                selectItemCount = selectedList.size();
            }
            String title = selectItemCount + " " + getResources().getString(R.string.pdf_common_selected);
            getSupportActionBar().setTitle(title);
            if(selectItemCount == 0){
                setFunctionButton(false);
            }else{
                setFunctionButton(true);
            }
        }
    }

    /**
     * @methodName：setFunctionButton created by liujiyuan on 2018/10/17 下午3:05.
     * @description：在未选择任何页面的情况下，设置底下三个功能键不可点击
     */
    private void setFunctionButton(boolean isClickable){
        if(isClickable){
            idEditPageFunctionRotate.setClickable(true);
            idEditPageFunctionSplit.setClickable(true);
            idEditPageFunctionDelete.setClickable(true);
        }else{
            idEditPageFunctionRotate.setClickable(false);
            idEditPageFunctionSplit.setClickable(false);
            idEditPageFunctionDelete.setClickable(false);
        }
    }

    @Override
    public void onBackPressed() {
        if (menu_type == VIEW_TYPE) {
            if (EditPagePresenter.isChangeSuccess) {
                EventBusUtils.getInstance().post(new MessageEvent<>(ConstantBus.REFRESH_DOCUMENT, SUCCESS_EDIT));
            }
            super.onBackPressed();
        } else {
            menu_type = VIEW_TYPE;
            editPageAdapter.setType(VIEW_TYPE);
            invalidateOptionsMenu();
            idEditPageFunction.setVisibility(View.GONE);
            idEditPageBlank.setVisibility(View.GONE);
            setActivityTitle();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getMvpPresenter().setViewSize();
        editPageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_edit_page_function_rotate:
                getMvpPresenter().pageRotate();
                break;
            case R.id.id_edit_page_function_split:
                getMvpPresenter().onNewPdfExport();
                break;
            case R.id.id_edit_page_function_delete:
                getMvpPresenter().pageDelete();
                break;
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

    /**
     * @methodName：showHint created by liujiyuan on 2018/10/12 下午1:34.
     * @description：展示拖拽排序的动画提示框
     */
    private void showHint() {
        boolean isShowHint = KMReaderSpUtils.getInstance().getFirstPageEditUsed();
        if (!isShowHint) {
            return;
        }
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_scan_project_review_hint, null);
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();
        alertDialog.show();

        AppCompatImageView idScanProjectReviewHintIcon = view.findViewById(R.id.id_scan_project_review_hint_icon);
        AppCompatTextView idScanProjectReviewHintConfirm = view.findViewById(R.id.id_scan_project_review_hint_confirm);
        AppCompatImageView idScanProjectReviewHintClose = view.findViewById(R.id.id_scan_project_review_hint_close);

        GlideLoadUtil.LoadGiftAsGistKeepFidelity(this, R.drawable.gif_past_due, idScanProjectReviewHintIcon, R.drawable.gif_past_due);
        idScanProjectReviewHintConfirm.setOnClickListener(v -> {
            alertDialog.dismiss();
            SharedPreferencesSava.getInstance().savaBooleanValue(SharedPreferencesSava.DEFAULT_SPNAME, Constants.SP_VALUE_SCAN_REVIEW_PROMPTED, true);
        });
        idScanProjectReviewHintClose.setOnClickListener(v -> {
            alertDialog.dismiss();
            SharedPreferencesSava.getInstance().savaBooleanValue(SharedPreferencesSava.DEFAULT_SPNAME, Constants.SP_VALUE_SCAN_REVIEW_PROMPTED, true);
        });
        KMReaderSpUtils.getInstance().setNoFirstPageEditUsed();
    }
}