package com.kdanmobile.pdfviewer.screenui.reader.view.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.kdanmobile.kmpdfkit.manager.controller.KMPDFDocumentController;
import com.kdanmobile.kmpdfkit.pdfcommon.Bookmark;
import com.kdanmobile.pdfviewer.R;
import com.kdanmobile.pdfviewer.base.BaseFragment;
import com.kdanmobile.pdfviewer.event.MessageEvent;
import com.kdanmobile.pdfviewer.screenui.common.decoration.ProItemDecoration;
import com.kdanmobile.pdfviewer.screenui.reader.utils.InitKMPDFControllerUtil;
import com.kdanmobile.pdfviewer.screenui.reader.view.adapter.BookmarkListAdapter;
import com.kdanmobile.pdfviewer.screenui.widget.commondialog.DialogFragmentHelper;
import com.kdanmobile.pdfviewer.screenui.widget.commondialog.IDialogResultListener;
import com.kdanmobile.pdfviewer.utils.ToastUtil;
import com.kdanmobile.pdfviewer.utils.eventbus.ConstantBus;
import com.kdanmobile.pdfviewer.utils.eventbus.EventBusUtils;
import com.kdanmobile.pdfviewer.utils.threadpools.SimpleBackgroundTask;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.greenrobot.eventbus.Subscribe;

import java.util.Arrays;
import java.util.List;

/**
 * @classname：BookmarkFragment
 * @author：liujiyuan
 * @date：2018/8/15 下午2:32
 * @description：文本阅读书签界面
 */
public class BookmarkFragment extends BaseFragment implements IDialogResultListener {

    private SmartRefreshLayout idBookmarkListRefresh;
    private RecyclerView idBookmarkList;
    private RelativeLayout idBookmarkListDis;
    private BookmarkListAdapter bookmarkListAdapter;

    private KMPDFDocumentController kmpdfDocumentController;
    public SimpleBackgroundTask setDateTask;

    int return_pageNum = -1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBusUtils.getInstance().register(this);
    }

    public static BookmarkFragment newInstance() {
        BookmarkFragment fragment = new BookmarkFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bookmark, container, false);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        kmpdfDocumentController = InitKMPDFControllerUtil.getInstance().getKmpdfDocumentController();

        idBookmarkListRefresh = view.findViewById(R.id.id_bookmark_list_refresh);
        idBookmarkList = view.findViewById(R.id.id_bookmark_list);
        idBookmarkListDis = view.findViewById(R.id.id_bookmark_list_dis);

        idBookmarkList.setLayoutManager(new LinearLayoutManager(mActivity));
        idBookmarkList.addItemDecoration(new ProItemDecoration());
        bookmarkListAdapter = new BookmarkListAdapter();
        idBookmarkList.setAdapter(bookmarkListAdapter);

        setDateTask = new SimpleBackgroundTask<List<Bookmark>>(mActivity) {
            @Override
            protected List<Bookmark> onRun() {
                return getBookMarkDataList();
            }

            @Override
            protected void onSuccess(List<Bookmark> list) {
                if(list == null || list.size() == 0){
                    showBookMarkList(false);
                }else{
                    showBookMarkList(true);
                    bookmarkListAdapter.setData(list);
                }
            }
        };

        setDateTask.execute();

        idBookmarkListRefresh.setOnRefreshListener(refreshLayout -> {
            try {
                if (null != bookmarkListAdapter) {
                    setDateTask.execute();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                idBookmarkListRefresh.finishRefresh(200);
            }
        });
    }

    /**
     * @param ：[isShow]
     * @return : void
     * @methodName ：showBookMarkList created by liujiyuan on 2018/8/28 上午10:57.
     * @description ：控制是否显示list
     */
    private void showBookMarkList(boolean isShow){
        if(isShow){
            idBookmarkListRefresh.setVisibility(View.VISIBLE);
            idBookmarkListDis.setVisibility(View.GONE);
        }else{
            idBookmarkListDis.setVisibility(View.VISIBLE);
            idBookmarkListRefresh.setVisibility(View.GONE);
        }
    }

    /**
     * @param ：[]
     * @return : java.util.List<com.kdanmobile.kmpdfkit.pdfcommon.Bookmark>
     * @methodName ：getBookMarkDataList created by liujiyuan on 2018/8/28 上午10:46.
     * @description ：得到bookmark的数据
     */
    private List<Bookmark> getBookMarkDataList(){
        Bookmark[] bookmarkList = kmpdfDocumentController.getBookmarks();
        if(bookmarkList == null || bookmarkList.length == 0){
            return null;
        }
        return Arrays.asList(bookmarkList);
    }

    @Override
    public void onDestroy() {
        EventBusUtils.getInstance().unRegister(this);
        super.onDestroy();
    }

    @SuppressLint("StaticFieldLeak")
    @Subscribe
    public void onEventMainThread(MessageEvent<Integer> event) {
        String tag = event.getTag();
        return_pageNum = event.getEvent();
        switch (tag) {
            case ConstantBus.BOOKMARK_RENAME:
                try {
                    DialogFragmentHelper.showPasswordInsertDialog(
                            getFragmentManager(),
                            DialogFragmentHelper.TYPE_EDIT_DIALOG_TEXT,
                            mContext.getString(R.string.dialog_enter_rename_bookmark_title),
                            this, true);
                }catch (Exception e){
                    ToastUtil.showToast(mContext, mContext.getString(R.string.dialog_error));
                }
                break;
            case ConstantBus.BOOKMARK_GOTOPAGE:
                if(return_pageNum != -1){
                    kmpdfDocumentController.gotoPage(return_pageNum);
                    mActivity.onBackPressed();
                }
                break;
            case ConstantBus.BOOKMARK_DELETE:
                if (return_pageNum != -1 && kmpdfDocumentController.deleteBookmarks(return_pageNum) && null != bookmarkListAdapter) {
                    setDateTask = new SimpleBackgroundTask<List<Bookmark>>(mActivity) {
                        @Override
                        protected List<Bookmark> onRun() {
                            return getBookMarkDataList();
                        }

                        @Override
                        protected void onSuccess(List<Bookmark> list) {
                            if(list == null || list.size() == 0){
                                showBookMarkList(false);
                            }else{
                                showBookMarkList(true);
                                bookmarkListAdapter.setData(list);
                            }
                        }
                    };
                    setDateTask.execute();
                }
                break;
            default:
        }
    }

    @Override
    public void onDataResult(Object result) {
        if (result != null && return_pageNum != -1) {
            String feedback = (String) result;
            if(kmpdfDocumentController.modifyBookmark(feedback, return_pageNum) && null != bookmarkListAdapter){
                bookmarkListAdapter.setData(getBookMarkDataList());
                ToastUtil.showToast(mContext, mContext.getString(R.string.rename_bookmark_success));
            }else{
                ToastUtil.showToast(mContext, mContext.getString(R.string.rename_bookmark_fail));
            }
        }
    }
}

