package com.kdanmobile.pdfviewer.screenui.reader.view.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.kdanmobile.pdfviewer.R;
import com.kdanmobile.kmpdfkit.manager.controller.KMPDFDocumentController;
import com.kdanmobile.kmpdfkit.pdfcommon.OutlineItem;
import com.kdanmobile.pdfviewer.base.BaseFragment;
import com.kdanmobile.pdfviewer.screenui.reader.utils.InitKMPDFControllerUtil;
import com.kdanmobile.pdfviewer.screenui.reader.view.adapter.OutLineListAdapter;
import com.kdanmobile.pdfviewer.screenui.reader.view.been.ProOutLineItem;
import com.kdanmobile.pdfviewer.utils.threadpools.SimpleBackgroundTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @classname：OutLineFragment
 * @author：liujiyuan
 * @date：2018/8/15 下午2:18
 * @description：目录大纲界面
 */
public class OutLineFragment extends BaseFragment {

    private KMPDFDocumentController kmpdfDocumentController;
    OutlineItem[] outlineItems;
    List<ProOutLineItem> copyList = new ArrayList<>();

    private OutLineListAdapter adapter;

    private ListView idOutlineList;
    private RelativeLayout outlineMiiss;

    public static OutLineFragment newInstance() {
        OutLineFragment fragment = new OutLineFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_outline, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        idOutlineList = view.findViewById(R.id.id_outline_list);
        outlineMiiss = view.findViewById(R.id.id_outline_miss);

        kmpdfDocumentController = InitKMPDFControllerUtil.getInstance().getKmpdfDocumentController();

        if(!kmpdfDocumentController.hasOutLine()){
            showOutLine(false);
            return;
        }else{
            showOutLine(true);
        }

        /****** 结构体的转换，并设置adapter ******/
        new SimpleBackgroundTask<Boolean>(mActivity) {
            @Override
            protected Boolean onRun() {
                outlineItems = kmpdfDocumentController.getOutline();
                if(outlineItems != null) {
                    return convertToProOutLineItem(Arrays.asList(outlineItems), copyList);
                }else{
                    return false;
                }
            }

            @Override
            protected void onSuccess(Boolean result) {
                if(result){
                    showOutLine(true);
                    adapter = new OutLineListAdapter(mActivity, copyList);
                    idOutlineList.setAdapter(adapter);
                }else{
                    showOutLine(false);
                }
            }
        }.execute();

        /****** 设置listView item的点击事件 ******/
        idOutlineList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final ProOutLineItem item = adapter.getItem(position);
                new SimpleBackgroundTask<Boolean>(mActivity) {

                    @Override
                    protected Boolean onRun() {
                        if(item.isExpose){
                            return adapter.hideExpose(item);
                        }else{
                            return adapter.showExpose(item);
                        }
                    }

                    @Override
                    protected void onSuccess(Boolean result) {
                        /****** 如果为 false 则说明点击目录没有子目录，跳转到相应页面；如果true,则展开子目录 ******/
                        if(result){
                            if(adapter != null){
                                adapter.notifyDataSetChanged();
                            }
                        }else{
                            kmpdfDocumentController.gotoPage(item.page);
                            mActivity.onBackPressed();
                        }
                    }
                }.execute();
            }
        });

    }

    /**
     * @param ：[list, copyList]
     * @return : boolean
     * @methodName ：convertToProOutLineItem created by liujiyuan on 2018/8/22 下午4:55.
     * @description ：把pso库中的结构体 OutlineItem 转换成自定义的 ProOutLineItem，保存序列
     */
    private boolean convertToProOutLineItem(List<OutlineItem> list, List<ProOutLineItem> copyList){
        for(OutlineItem item: list){
            ProOutLineItem new_one = new ProOutLineItem(item.level, item.title,
                    item.page,item.parent_pos, item.parent_title);
            copyList.add(new_one);
        }
        if(copyList != null && copyList.size() != 0){
            return true;
        }else{
            return false;
        }
    }

    private void showOutLine(boolean isShow){
        if(isShow){
            outlineMiiss.setVisibility(View.GONE);
            idOutlineList.setVisibility(View.VISIBLE);
        }else{
            outlineMiiss.setVisibility(View.VISIBLE);
            idOutlineList.setVisibility(View.GONE);
        }
    }
}
