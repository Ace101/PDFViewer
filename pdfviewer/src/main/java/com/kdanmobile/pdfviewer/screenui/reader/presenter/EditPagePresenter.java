package com.kdanmobile.pdfviewer.screenui.reader.presenter;


import android.annotation.SuppressLint;
import android.content.Context;

import com.kdanmobile.kmpdfkit.manager.KMPDFFactory;
import com.kdanmobile.kmpdfkit.manager.controller.KMPDFDocumentController;
import com.kdanmobile.kmpdfkit.pdfcommon.Bookmark;
import com.kdanmobile.pdfviewer.R;
import com.kdanmobile.pdfviewer.base.mvp.presenter.BaseMvpPresenter;
import com.kdanmobile.pdfviewer.event.MessageEvent;
import com.kdanmobile.pdfviewer.screenui.reader.constract.EditPageConstract;
import com.kdanmobile.pdfviewer.screenui.reader.utils.InitKMPDFControllerUtil;
import com.kdanmobile.pdfviewer.screenui.reader.view.activity.EditPageActivity;
import com.kdanmobile.pdfviewer.screenui.reader.view.adapter.EditPageRecyclerViewAdapter;
import com.kdanmobile.pdfviewer.screenui.widget.commondialog.DialogFragmentHelper;
import com.kdanmobile.pdfviewer.screenui.widget.commondialog.IDialogResultListener;
import com.kdanmobile.pdfviewer.utils.FileUtilsExtension;
import com.kdanmobile.pdfviewer.utils.GlobalConfigs;
import com.kdanmobile.pdfviewer.utils.PathManager;
import com.kdanmobile.pdfviewer.utils.ScreenUtil;
import com.kdanmobile.pdfviewer.utils.ToastUtil;
import com.kdanmobile.pdfviewer.utils.eventbus.ConstantBus;
import com.kdanmobile.pdfviewer.utils.eventbus.EventBusUtils;
import com.kdanmobile.pdfviewer.utils.threadpools.SimpleBackgroundTask;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.Set;


/**
 * @classname：EditPagePresenter
 * @author：liujiyuan
 * @date：2018/8/29 上午11:05
 * @description：页面编辑功能实现
 */
public class EditPagePresenter extends BaseMvpPresenter<EditPageActivity> implements EditPageConstract.IPresenter, IDialogResultListener {

    private final int MAX_SPLIT_FILE_NAME = 3;
    private KMPDFDocumentController kmpdfDocumentController;
    public int totoalPages = 0;
    public int currentPage = 0;
    public int recyclerView_columns;
    /****** 页面是否修改成功，成功，则刷新页面 ******/
    public static boolean isChangeSuccess = false;

    private Context context;
    private EditPageRecyclerViewAdapter editPageAdapter;
    private SimpleBackgroundTask exportPageTask;
    private SimpleBackgroundTask rotatePageTask;
    private SimpleBackgroundTask deletePageTask;

    @Override
    public void onInit(EditPageActivity mView) {
        EventBusUtils.getInstance().register(this);

        isChangeSuccess = false;

        context = mView.getApplicationContext();
        kmpdfDocumentController = InitKMPDFControllerUtil.getInstance().getKmpdfDocumentController();

        if(kmpdfDocumentController != null) {
            totoalPages = kmpdfDocumentController.getDocumentPageCount(false);
            currentPage = kmpdfDocumentController.getCurrentPageNum();
        }

        editPageAdapter = new EditPageRecyclerViewAdapter(mView, mView.handler);
        editPageAdapter.setType(EditPageActivity.VIEW_TYPE);
        setViewSize();
        getMvpView().setAdapter(editPageAdapter);
    }

    @Override
    public EditPageRecyclerViewAdapter getAdapter(){
        return editPageAdapter;
    }

    /**
     * @methodName：pageDelete created by liujiyuan on 2018/8/30 上午11:51.
     * @description：页面删除方法
     */
    @Override
    public void pageDelete() {
        try {
            String mes = context.getResources().getString(R.string.delete_page_warning);
            String title = context.getResources().getString(R.string.dialog_title_warning);
            DialogFragmentHelper.showConfirmDialog(getMvpView().getSupportFragmentManager(), title, mes, this, false, null);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * @methodName：confirmPageDelete created by liujiyuan on 2018/10/12 上午10:17.
     * @description：确认删除页面的回调方法
     */
    @SuppressLint("StaticFieldLeak")
    private void confirmPageDelete(){
        try {
            if ((editPageAdapter.mapSelect == null) || (editPageAdapter.mapSelect.size() == 0)) {
                ToastUtil.showToast(context, R.string.choose_page);
                return;
            }

            if (editPageAdapter.mapSelect.size() == editPageAdapter.getItemCount()) {
                ToastUtil.showToast(context, R.string.not_delete_all);
                return;
            }
            getMvpView().showProgressDialog(context.getResources().getString(R.string.page_edit_delete), false, false);
            deletePageTask = new SimpleBackgroundTask<Boolean>(getMvpView()) {
                @Override
                protected Boolean onRun() {
                    /****** 删除页面 ******/
                    Set<String> set = editPageAdapter.mapSelect.keySet();
                    int delete_count = editPageAdapter.mapSelect.size();
                    int[] delete_pages = new int[delete_count];
                    int i = 0;
                    for (String key : set) {
                        /****** 真实页码，从0开始 ******/
                        int position = Integer.parseInt(key);
                        delete_pages[i] = position;
                        i++;
                    }
                    return kmpdfDocumentController.deletePages(delete_pages);
                }

                @Override
                protected void onSuccess(Boolean result) {
                    getMvpView().stopProgressDialog();
                    if (result) {
                        isChangeSuccess = true;
                        if(editPageAdapter != null) {
                            Bookmark[] outlineItems = kmpdfDocumentController.getBookmarks();
                            editPageAdapter.setBookmarks(outlineItems);

                            int count = kmpdfDocumentController.getDocumentPageCount(true);

                            editPageAdapter.setAllUnClick();
                            editPageAdapter.onDeleteNotification(count);
                        }
                        getMvpView().setActivityTitle();
                    }
                }
            };
            deletePageTask.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @methodName：pageRotate created by liujiyuan on 2018/8/30 下午1:34.
     * @description：旋转缩略图
     */
    @SuppressLint("StaticFieldLeak")
    @Override
    public void pageRotate() {
        try {
            if ((editPageAdapter.mapSelect == null) || (editPageAdapter.mapSelect.size() == 0)) {
                ToastUtil.showToast(context, R.string.choose_page);
                return;
            }

            getMvpView().showProgressDialog(context.getResources().getString(R.string.page_edit_rotating), false, false);
            Set<String> set = editPageAdapter.mapSelect.keySet();
            int rotate_count = editPageAdapter.mapSelect.size();
            int[] rotate_pages = new int[rotate_count];
            int i = 0;
            for (String key : set) {
                /****** 真实页码，从0开始 ******/
                int position = Integer.parseInt(key);
                rotate_pages[i] = position;
                i++;

                /****** 去掉缩列图缓存MAP集合的元素 ******/
                String fileName = GlobalConfigs.FILE_ABSOLUTE_PATH.replaceAll("/", "_");
                String thumbName = fileName + position;
                EditPageRecyclerViewAdapter.mMemoryCache.remove(thumbName);
            }

            rotatePageTask = new SimpleBackgroundTask<Boolean>(getMvpView()) {
                @Override
                protected Boolean onRun() {
                    return kmpdfDocumentController.rotatePages(rotate_pages, KMPDFDocumentController.RotationalDirection.CLOCK_WISE);
                }

                @Override
                protected void onSuccess(Boolean result) {
                    getMvpView().stopProgressDialog();
                    if (result) {
                        editPageAdapter.onRotateNotification(rotate_pages);
                        isChangeSuccess = true;
                    }
                }
            };
            rotatePageTask.execute();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @methodName：onNewPdfExport created by liujiyuan on 2018/8/30 下午3:22.
     * @description：页面拆分
     */
    @SuppressLint("StaticFieldLeak")
    @Override
    public void onNewPdfExport(){
        getMvpView().showProgressDialog(context.getResources().getString(R.string.page_edit_extracting), false, false);
        Set<String> set = editPageAdapter.mapSelect.keySet();
        int export_count = editPageAdapter.mapSelect.size();
        int[] export_page = new int[export_count];
        /****** 实际显示的名称页面，从1开始 ******/
        int[] display_page = new int[export_count];
        int i = 0;
        for (String key : set) {
            /****** 真实页码，从0开始 ******/
            int position = Integer.parseInt(key);
            export_page[i] = position;
            display_page[i] = position+1;
            i++;
        }
        String filePath = getNewFilePath(getNewFileName(display_page));
        exportPageTask = new SimpleBackgroundTask<Boolean>(getMvpView()) {

            @Override
            protected Boolean onRun() {
                return kmpdfDocumentController.splitPDFWithPages(filePath, export_page);
            }

            @Override
            protected void onSuccess(Boolean result) {
                getMvpView().stopProgressDialog();
                try {
                    if(result){
                        isChangeSuccess = true;
                        String title = context.getResources().getString(R.string.pdf_export_success);
                        String success_mes = context.getResources().getString(R.string.pdf_export_success_mes) + ": '" + filePath + "'";
                        DialogFragmentHelper.showConfirmDialog(getMvpView().getSupportFragmentManager(), title, success_mes, null, false, null);
                    }else{
                        String fail_mes = context.getResources().getString(R.string.pdf_export_fail_mes);
                        DialogFragmentHelper.showConfirmDialog(getMvpView().getSupportFragmentManager(), fail_mes, null, false, null);
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        exportPageTask.execute();
    }

    /**
     * @methodName：getNewFilePath created by liujiyuan on 2018/8/30 下午3:09.
     * @description：设置拆分文件的新路径
     */
    private String getNewFilePath(String fileName){
        return PathManager.getInstance().getSplitFolderPath() + File.separator + fileName;
    }

    /**
     * @methodName：getNewFileName created by liujiyuan on 2018/8/30 下午3:09.
     * @description：定义到拆分文件的新名称
     */
    private String getNewFileName(int[] export_page){
        String fileName = InitKMPDFControllerUtil.getInstance().getKmpdfFactory().getFileName();
        String newName = fileName.substring(0, fileName.indexOf(".pdf"));
        if(export_page.length <= MAX_SPLIT_FILE_NAME){
            newName = newName + "_Page";
            for(int i=0; i < export_page.length; i++){
                if(i != 0){
                    newName = newName + "," + export_page[i];
                }else {
                    newName = newName + export_page[i];
                }
            }
            newName = newName + ".pdf";
        }else{
            newName = newName + "_Page" + export_page[0] + "," + export_page[1]+ "," + export_page[2] + ",etc" + ".pdf";
        }
        return newName;
    }

    /**
     * @methodName：setViewSize created by liujiyuan on 2018/8/29 下午4:27.
     * @description：动态设置RecyclerView布局
     */
    @Override
    public void setViewSize() {
        int width, padding;
        width = (int) context.getResources().getDimension(R.dimen.qb_px_104);
        recyclerView_columns =  (ScreenUtil.getScreenWidth(context) / width);
        padding = (ScreenUtil.getScreenWidth(context) - width * recyclerView_columns) / 2 * (recyclerView_columns + 1);
        editPageAdapter.setSize(width, padding);
    }

    @Override
    public void onDestroyPresenter() {
        EventBusUtils.getInstance().unRegister(this);
        super.onDestroyPresenter();
    }

    @Subscribe
    public void onEventMainThread(MessageEvent<Integer> event) {
        String tag = event.getTag();
        int page = event.getEvent();
        switch (tag) {
            case ConstantBus.PAGEEDIT_GOTOPAGE:
                kmpdfDocumentController.gotoPage(page);
                break;
            default:
        }
    }

    @Override
    public void onDataResult(Object result) {
        confirmPageDelete();
    }
}