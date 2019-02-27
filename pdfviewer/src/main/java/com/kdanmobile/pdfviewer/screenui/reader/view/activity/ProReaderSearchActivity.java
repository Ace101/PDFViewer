package com.kdanmobile.pdfviewer.screenui.reader.view.activity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kdanmobile.pdfviewer.R;
import com.kdanmobile.kmpdfkit.manager.controller.KMPDFDocumentController;
import com.kdanmobile.kmpdfkit.pdfcommon.TextWord;
import com.kdanmobile.pdfviewer.base.BaseActivity;
import com.kdanmobile.pdfviewer.base.ProApplication;
import com.kdanmobile.pdfviewer.event.MessageEvent;
import com.kdanmobile.pdfviewer.screenui.reader.configs.KMReaderConfigs;
import com.kdanmobile.pdfviewer.screenui.reader.utils.BrightnessUtil;
import com.kdanmobile.pdfviewer.screenui.reader.utils.InitKMPDFControllerUtil;
import com.kdanmobile.pdfviewer.screenui.reader.view.adapter.SearchTextRecyclerviewAdapter;
import com.kdanmobile.pdfviewer.screenui.reader.view.been.SearchTextInfo;
import com.kdanmobile.pdfviewer.utils.KeyboardUtils;
import com.kdanmobile.pdfviewer.utils.SimpleTabSelectedListener;
import com.kdanmobile.pdfviewer.utils.SimpleTextWatcher;
import com.kdanmobile.pdfviewer.utils.eventbus.ConstantBus;
import com.kdanmobile.pdfviewer.utils.eventbus.EventBusUtils;
import com.kdanmobile.pdfviewer.utils.threadpools.MainPoolUtils;
import com.kdanmobile.pdfviewer.utils.threadpools.SimpleBackgroundTask;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * @classname：ProReaderSearchActivity
 * @author：liujiyuan
 * @date：2018/8/15 下午3:58
 * @description：
 */
public class ProReaderSearchActivity extends BaseActivity implements View.OnClickListener {

    private final String[] tablayout_title = {
            ProApplication.getContext().getString(R.string.search_text_tab),
            ProApplication.getContext().getString(R.string.search_page_tab)
    };
    private final int SEARCH_TEXT = 1001;
    private final int SEARCH_PAGE = 1002;
    private final int MAX_SEARCH_NUM = 10000;
    private int searchType = SEARCH_TEXT;
    private int ALL_PAGE_COUNT;

    private final List<SearchTextInfo> searchTextInfoList = new ArrayList<>();
    private KMPDFDocumentController kmpdfDocumentController;
    private SearchTextRecyclerviewAdapter searchTextAdapter;
    private boolean isFinishedSearching = false;
    private boolean isClearSearching = false;
    SimpleBackgroundTask recyclerviewAdapterTask;

    private ImageButton idReaderSearchBack;
    private EditText idReaderSearchContent;
    private ImageButton readerSearchClean;
    private TabLayout idReaderSearchTabLayout;
    private RecyclerView idReaderSearchRecyclerView;
    private RelativeLayout idReaderSearchDis;
    private TextView idReaderSearchDisText;

    private String lastSeatchText = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBusUtils.getInstance().register(this);
        kmpdfDocumentController = InitKMPDFControllerUtil.getInstance().getKmpdfDocumentController();
        setContentView(R.layout.activity_reader_search);

        ALL_PAGE_COUNT = kmpdfDocumentController.getDocumentPageCount(false);
        idReaderSearchBack = findViewById(R.id.id_reader_search_back);
        idReaderSearchContent = findViewById(R.id.id_reader_search_content);
        readerSearchClean = findViewById(R.id.reader_search_clean);
        idReaderSearchTabLayout = findViewById(R.id.id_reader_search_tab_layout);
        idReaderSearchDis = findViewById(R.id.id_reader_search_dis);
        idReaderSearchDisText = findViewById(R.id.id_reader_search_dis_text);

        idReaderSearchRecyclerView = findViewById(R.id.id_reader_search_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(idReaderSearchRecyclerView.getContext());
        idReaderSearchRecyclerView.setLayoutManager(layoutManager);

        idReaderSearchContent.setHint(R.string.search_text_hint);
        for (int i = 0; i < tablayout_title.length; i++) {
            idReaderSearchTabLayout.addTab(idReaderSearchTabLayout.newTab().setText(tablayout_title[i]));
        }
        /****** 设置各类监听的方法 ******/
        setListener();

        searchTextAdapter = new SearchTextRecyclerviewAdapter(idReaderSearchRecyclerView);
        idReaderSearchRecyclerView.setAdapter(searchTextAdapter);

        showKeyBoard(true);
    }

    private void setListener() {
        idReaderSearchBack.setOnClickListener(this);
        readerSearchClean.setOnClickListener(this);
        idReaderSearchContent.setOnClickListener(this);
        /****** 设置Tab的选择监听 ******/
        idReaderSearchTabLayout.addOnTabSelectedListener(new SimpleTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                idReaderSearchContent.setText("");
                lastSeatchText = "";
                showKeyBoard(true);
                cleanSearchTextRecyclerView();
                switch (tab.getPosition()) {
                    /****** TEXT ******/
                    case 0:
                        searchType = SEARCH_TEXT;
                        idReaderSearchRecyclerView.setVisibility(View.VISIBLE);
                        idReaderSearchDis.setVisibility(View.GONE);
                        idReaderSearchContent.setInputType(InputType.TYPE_CLASS_TEXT);
                        idReaderSearchContent.setHint(R.string.search_text_hint);
                        idReaderSearchDisText.setText(R.string.search_text_dis);
                        break;
                    /****** PAGE NUM ******/
                    case 1:
                        searchType = SEARCH_PAGE;
                        idReaderSearchRecyclerView.setVisibility(View.GONE);
                        idReaderSearchDis.setVisibility(View.GONE);
                        idReaderSearchContent.setInputType(InputType.TYPE_CLASS_NUMBER);
                        if(ALL_PAGE_COUNT == 1) {
                            idReaderSearchContent.setHint("1");
                        }else{
                            idReaderSearchContent.setHint("1 ~ " + String.valueOf(ALL_PAGE_COUNT));
                        }
                        idReaderSearchDisText.setText(R.string.search_page_dis);
                        break;
                    default:
                }
            }
        });

        /****** 设置软键盘的搜索按键监听 ******/
        idReaderSearchContent.setOnEditorActionListener((v, actionId, event) -> {
            boolean isKey = (actionId == 0 || actionId == 3) && event != null;
            if (isKey) {
                isClearSearching = false;
                isShowRecyclerView(true);
                startSearch();
                lastSeatchText = idReaderSearchContent.getText().toString();
            }
            return false;
        });

        idReaderSearchContent.addTextChangedListener(new SimpleTextWatcher(){
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                cleanSearchTextRecyclerView();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_reader_search_content:
                showKeyBoard(true);
                break;
            case R.id.id_reader_search_back:
                this.onBackPressed();
                showKeyBoard(false);
                break;
            case R.id.reader_search_clean:
                idReaderSearchContent.setText("");
                showKeyBoard(false);
                cleanSearchTextRecyclerView();
                lastSeatchText = "";
                break;
            default:
        }
    }

    /**
     * @param ：[]
     * @return : void
     * @methodName ：startSearch created by liujiyuan on 2018/8/27 上午10:39.
     * @description ：点击键盘的搜索按钮事件
     */
    @SuppressLint("StaticFieldLeak")
    private void startSearch() {
        final String search = idReaderSearchContent.getText().toString();
        if(search.equals(lastSeatchText)){
            return;
        }
        if (search.length() > 0) {
            if (searchType == SEARCH_PAGE) {
                int page = Integer.parseInt(search) - 1;
                if (page >= 0 && page < ALL_PAGE_COUNT) {
                    kmpdfDocumentController.gotoPage(page);
                    showKeyBoard(false);
                    finish();
                } else {
                    isShowRecyclerView(false);
                }
            } else {
                showProgressDialog(getString(R.string.edit_page_search_word), true, false);
                cancelTask();
                recyclerviewAdapterTask = new SimpleBackgroundTask<Object>(this) {
                    @Override
                    protected Object onRun() {
                        for (int i = 0; i < ALL_PAGE_COUNT; i++) {
                            if (!isFinishedSearching) {
                                RectF[] results = kmpdfDocumentController.searchPage(i, search);
                                if ((results != null) && (results.length > 0)) {
                                    TextWord[][] texts = kmpdfDocumentController.textLines(i);
                                    final List<SearchTextInfo> searchPageContent = findSearchText(texts, results, search, i);
                                    if (searchPageContent.size() > 0) {
                                        final int searchPageNum = i;
                                        if(!isClearSearching) {
                                            MainPoolUtils.getInstance().onMainRuning(ProReaderSearchActivity.this, () -> {
                                                final SearchTextInfo pageTextInfo = new SearchTextInfo(searchPageNum, search, null, null, true);
                                                searchTextInfoList.add(pageTextInfo);
                                                searchTextInfoList.addAll(searchPageContent);
                                                searchTextAdapter.addItem(pageTextInfo);
                                                searchTextAdapter.addList(searchPageContent);
                                                final int positionStart = searchTextInfoList.size() - searchPageContent.size() - 1;
                                                searchTextAdapter.notifyItemRangeChanged(positionStart, searchPageContent.size() + 1);
                                            });
                                        }else{
                                            break;
                                        }
                                        stopProgressDialog();
                                    }
                                    if ((searchTextInfoList.size() + searchPageContent.size()) >= MAX_SEARCH_NUM) {
                                        isFinishedSearching = true;
                                    }
                                }
                            } else {
                                break;
                            }
                        }
                        return null;
                    }

                    @Override
                    protected void onSuccess(Object result) {
                        if (searchTextInfoList.size() < 1) {
                            isShowRecyclerView(false);
                            stopProgressDialog();
                        }
                        isFinishedSearching = false;
                        KeyboardUtils.hideKeyboard(idReaderSearchContent);
                    }
                };
                recyclerviewAdapterTask.execute();
            }
        }
    }

    /**
     * @methodName：cleanSearchTextRecyclerView created by liujiyuan on 2018/9/26 下午3:49.
     * @description：停止搜索关键字，并清除搜索界面内容
     */
    private void cleanSearchTextRecyclerView(){
        isClearSearching = true;
        cancelTask();
    }

    /**
     * @param ：[]
     * @return : void
     * @methodName ：cancleTask created by liujiyuan on 2018/8/27 上午10:38.
     * @description ：撤销task的运行
     */
    private void cancelTask() {
        if(recyclerviewAdapterTask!=null && !recyclerviewAdapterTask.isCancelled()){
            recyclerviewAdapterTask.cancel(true);
        }
        recyclerviewAdapterTask = null;
        if(searchTextInfoList != null){
            searchTextInfoList.clear();
        }
        if(searchTextAdapter != null){
            searchTextAdapter.clearList();
            searchTextAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        cancelTask();
        EventBusUtils.getInstance().unRegister(this);
        super.onDestroy();
    }

    /**
     * @param ：[texts, results, search, page]
     * @return : java.util.List<com.pdftechnologies.pdfreaderpro.screenui.reader.view.been.SearchTextInfo>
     * @methodName ：findSearchText created by liujiyuan on 2018/8/23 下午4:01.
     * @description ：把每一页检索到内容都加入到List中
     */
    private List<SearchTextInfo> findSearchText(TextWord[][] texts, RectF[] results, String search, int page) {
        final List<SearchTextInfo> list = new ArrayList<>();
        if ((texts == null) || (results == null)) {
            return list;
        }
        for (int i = 0; i < texts.length; i++) {
            TextWord[] tw = texts[i];
            for (int j = 0; j < tw.length; j++) {
                for (int k = 0; k < results.length; k++) {
                    if (tw[j].contains(results[k])) {
                        list.add(new SearchTextInfo(page, search, results, tw, false));
                        if ((this.searchTextInfoList.size() + list.size()) >= MAX_SEARCH_NUM) {
                            isFinishedSearching = true;
                            return list;
                        }
                    }
                }
            }
        }
        return list;
    }

    /**
     * @param ：[isShow]
     * @return : void
     * @methodName ：isShowRecyclerView created by liujiyuan on 2018/8/24 下午1:46.
     * @description ：是否显示搜索内容
     */
    private void isShowRecyclerView(boolean isShow){
        if(isShow){
            idReaderSearchRecyclerView.setVisibility(View.VISIBLE);
            idReaderSearchDis.setVisibility(View.GONE);
        }else{
            idReaderSearchRecyclerView.setVisibility(View.GONE);
            idReaderSearchDis.setVisibility(View.VISIBLE);
        }
    }

    /**
     * @param ：[isShow]
     * @return : void
     * @methodName ：showKeyBoard created by liujiyuan on 2018/8/24 下午1:46.
     * @description ：是否显示软键盘
     */
    public void showKeyBoard(boolean isShow) {
        if(isShow){
            readerSearchClean.setVisibility(View.VISIBLE);
            KeyboardUtils.showKeyboard(idReaderSearchContent);
        }else{
            readerSearchClean.setVisibility(View.INVISIBLE);
            KeyboardUtils.hideKeyboard(idReaderSearchContent);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            /****** 得到当前焦点的view，即输入框的View ******/
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                KeyboardUtils.hideKeyboard(idReaderSearchContent);
            }
            return super.dispatchTouchEvent(ev);
        }
        /****** 必不可少，否则所有的组件都不会有TouchEvent了 ******/
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    /**
     * @param ：[v, event]
     * @return : boolean
     * @methodName ：isShouldHideInput created by liujiyuan on 2018/8/27 上午11:14.
     * @description ：判断界面点击位置是否为输入框，不是则隐藏软键盘
     */
    public  boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            /****** 获取输入框当前的location位置 ******/
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                /****** 点击的是输入框区域，保留点击EditText的事件 ******/
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    @Subscribe
    public void onEventMainThread(MessageEvent<String> event) {
        String tag = event.getTag();
        switch (tag) {
            case ConstantBus.READER_SEARCH_TEXT:
                finish();
                break;
            default:
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

}
