package com.kdanmobile.pdfviewer.screenui.reader.presenter;

import android.annotation.SuppressLint;
import android.content.Context;

import com.kdanmobile.kmpdfkit.pdfcommon.Bookmark;
import com.kdanmobile.pdfviewer.R;
import com.kdanmobile.kmpdfkit.globaldata.Config;
import com.kdanmobile.kmpdfkit.manager.KMPDFFactory;
import com.kdanmobile.kmpdfkit.manager.controller.KMPDFDocumentController;
import com.kdanmobile.kmpdfkit.manager.controller.KMPDFSignatureController;
import com.kdanmobile.pdfviewer.base.mvp.presenter.BaseMvpPresenter;
import com.kdanmobile.pdfviewer.screenui.reader.configs.KMReaderConfigs;
import com.kdanmobile.pdfviewer.screenui.reader.constract.ProReaderSettingMoreConstract;
import com.kdanmobile.pdfviewer.screenui.reader.utils.InitKMPDFControllerUtil;
import com.kdanmobile.pdfviewer.screenui.reader.view.activity.ProReaderSettingMoreActivity;
import com.kdanmobile.pdfviewer.screenui.widget.commondialog.DialogFragmentHelper;
import com.kdanmobile.pdfviewer.screenui.widget.commondialog.IDialogResultListener;
import com.kdanmobile.pdfviewer.utils.CommonUtils;
import com.kdanmobile.pdfviewer.utils.GlobalConfigs;
import com.kdanmobile.pdfviewer.utils.ToastUtil;
import com.kdanmobile.pdfviewer.utils.eventbus.ConstantBus;
import com.kdanmobile.pdfviewer.utils.threadpools.SimpleBackgroundTask;

import java.io.File;

/**
 * @classname：ProReaderSettingMorePresenter
 * @author：liujiyuan
 * @date：2018/8/16 下午4:17
 * @description：settingMore的Presenter
 */
public class ProReaderSettingMorePresenter extends BaseMvpPresenter<ProReaderSettingMoreActivity>
        implements ProReaderSettingMoreConstract.IPresenter, IDialogResultListener {
    private Context context;
    private KMPDFFactory kmpdfFactory;
    private KMPDFDocumentController kmpdfDocumentController;
    private KMPDFSignatureController kmpdfSignatureController;

    private final int REQUEST_BOOKMARK = 0X1000;
    private final int REQUEST_PRINT_CONFIRM = 0X1001;
    private final int REQUEST_CLEAN_SIGN = 0X1002;
    private final int REQUEST_REMOVE_BOOKMARK = 0X1003;
    private int REQUEST_DIALOG = REQUEST_BOOKMARK;

    private int current_page = 0;

    @Override
    public void onInit(final ProReaderSettingMoreActivity mView) {
        this.context = mView.getApplicationContext();
        kmpdfFactory = InitKMPDFControllerUtil.getInstance().getKmpdfFactory();
        kmpdfDocumentController = InitKMPDFControllerUtil.getInstance().getKmpdfDocumentController();
        kmpdfSignatureController = InitKMPDFControllerUtil.getInstance().getKmpdfSignatureController();
        REQUEST_DIALOG = REQUEST_BOOKMARK;
        current_page = kmpdfDocumentController.getCurrentPageNum();
        isAddBookmark();
    }

    /**
     * @methodName：enterBookmarkTitleDialog created by liujiyuan on 2018/9/12 下午5:00.
     * @description：添加书签的方法
     */
    @Override
    public void enterBookmarkTitleDialog() {
        try {
            if (isAttached()) {
                REQUEST_DIALOG = REQUEST_BOOKMARK;
                DialogFragmentHelper.showPasswordInsertDialog(
                        getMvpView().getSupportFragmentManager(),
                        DialogFragmentHelper.TYPE_EDIT_DIALOG_TEXT,
                        context.getString(R.string.dialog_enter_add_bookmark_title),
                        this,
                        true);
            }
        } catch (Exception e) {
            ToastUtil.showToast(context, context.getString(R.string.dialog_error));
        }
    }

    /**
     * @methodName：onDataResult created by liujiyuan on 2018/9/12 下午5:00.
     * @description：打开对话框的回调
     */
    @Override
    public void onDataResult(Object result) {
        switch (REQUEST_DIALOG){
            /****** 添加书签的对话框回调 ******/
            case REQUEST_BOOKMARK:
                int bookmark_pageNum = kmpdfDocumentController.getCurrentPageNum();
                if (result != null) {
                    String feedback = (String) result;
                    if (kmpdfDocumentController.addBookmark(feedback, bookmark_pageNum)) {
                        ToastUtil.showToast(context, context.getString(R.string.add_bookmark_success));
                    } else {
                        ToastUtil.showToast(context, context.getString(R.string.add_bookmark_fail));
                    }
                    getMvpView().setBookMarkState(false);
                }
                break;
            /****** 添加打印确认对话框的回调 ******/
            case REQUEST_PRINT_CONFIRM:
                int confirm = (int) result;
                if(confirm == -1){
                    boolean isEncrypted = kmpdfFactory.needPassWord();
                    int pageCount = kmpdfDocumentController.getDocumentPageCount(false);
                    CommonUtils.printCurrentDocument(getMvpView(), new File(GlobalConfigs.FILE_ABSOLUTE_PATH), pageCount, isEncrypted);
                }
                break;
            /****** 删除签名认对话框的回调 ******/
            case REQUEST_CLEAN_SIGN:
                if(kmpdfSignatureController.deleteAllSign()){
                    ToastUtil.showToast(context, context.getString(R.string.clean_image_sign_suc));
                }else{
                    ToastUtil.showToast(context, context.getString(R.string.clean_image_sign_fail));
                }
                break;
            /****** 删除书签确认对话框的回调 ******/
            case REQUEST_REMOVE_BOOKMARK:
                if(kmpdfDocumentController.deleteBookmarks(current_page)){
                    ToastUtil.showToast(context, context.getString(R.string.settings_more_remove_bookmark_success));
                }else{
                    ToastUtil.showToast(context, context.getString(R.string.settings_more_remove_bookmark_fail));
                }
                getMvpView().setBookMarkState(true);
                break;
            default:
        }

    }

    /**
     * @methodName：refreshPageTurning created by liujiyuan on 2018/9/12 下午5:01.
     * @description：更新页面设置的显示
     */
    @Override
    public void refreshPageTurning(){
        switch (KMReaderConfigs.PAGE_TURN_INDEX) {
            case ConstantBus.MORE_SETTING_PAGETURN_VC:
                kmpdfDocumentController.setPDFViewMode(Config.PDFViewMode.VERTICAL_SINGLE_PAGE_CONTINUES);
                break;
            case ConstantBus.MORE_SETTING_PAGETURN_VS:
                kmpdfDocumentController.setPDFViewMode(Config.PDFViewMode.VERTICAL_SINGLE_PAGE);
                break;
            case ConstantBus.MORE_SETTING_PAGETURN_HS:
                kmpdfDocumentController.setPDFViewMode(Config.PDFViewMode.HORIZONTAL_SINGLE_PAGE);
                break;
            default:
        }
        kmpdfDocumentController.refresh(true);
    }

    /**
     * @methodName：cleanAllSign created by liujiyuan on 2018/9/12 下午5:00.
     * @description：清除签名的功能
     */
    @Override
    public void cleanAllSign(){
        if (!isAttached()) {
            return;
        }
        try {
            REQUEST_DIALOG = REQUEST_CLEAN_SIGN;
            String mes = context.getResources().getString(R.string.clean_signature_warning);
            String title = context.getResources().getString(R.string.sign_annotation_title);
            DialogFragmentHelper.showConfirmDialog(getMvpView().getSupportFragmentManager(), title, mes, this, false, null);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * @methodName：printDocument created by liujiyuan on 2018/9/13 下午6:06.
     * @description：当前文本打印方法
     */
    @Override
    public void printDocument(){
        if (!isAttached()) {
            return;
        }
        try {
            REQUEST_DIALOG = REQUEST_PRINT_CONFIRM;
            String fail_mes = context.getResources().getString(R.string.print_warn_content);
            String title = context.getResources().getString(R.string.print_warn_tip);
            DialogFragmentHelper.showConfirmDialog(getMvpView().getSupportFragmentManager(), title, fail_mes, this, false, null);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @methodName：isAddBookmark created by liujiyuan on 2018/9/29 上午10:57.
     * @description：判断当前页是否为书签页
     */
    @SuppressLint("StaticFieldLeak")
    private void isAddBookmark(){

        new SimpleBackgroundTask<Boolean>(getMvpView()) {
            @Override
            protected Boolean onRun() {
                Bookmark[] bookmarkList = kmpdfDocumentController.getBookmarks();
                if(bookmarkList == null || bookmarkList.length == 0){
                    return true;
                }
                for(Bookmark item:bookmarkList){
                    if(item.pageNum == current_page){
                        return false;
                    }
                }
                return true;
            }

            @Override
            protected void onSuccess(Boolean result) {
                getMvpView().setBookMarkState(result);
            }
        }.execute();
    }

    /**
     * @methodName：removeBookMark created by liujiyuan on 2018/9/29 上午10:57.
     * @description：弹出删除书签的确认对话框
     */
    @Override
    public void removeBookMark(){
        if (!isAttached()) {
            return;
        }
        try {
            REQUEST_DIALOG = REQUEST_REMOVE_BOOKMARK;
            String fail_mes = context.getResources().getString(R.string.settings_more_remove_bookmark_msg);
            String title = context.getResources().getString(R.string.dialog_title_warning);
            DialogFragmentHelper.showConfirmDialog(getMvpView().getSupportFragmentManager(), title, fail_mes, this, false, null);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
