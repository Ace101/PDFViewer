package com.kdanmobile.pdfviewer.screenui.reader.view.adapter;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kdanmobile.pdfviewer.R;
import com.kdanmobile.pdfviewer.screenui.reader.view.been.ProOutLineItem;
import com.kdanmobile.pdfviewer.utils.GlobalConfigs;
import com.kdanmobile.pdfviewer.utils.threadpools.ThreadPoolUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @classname：OutLineListAdapter
 * @author：liujiyuan
 * @date：2018/8/22 下午2:15
 * @description：多层树型目录适配器
 */
public class OutLineListAdapter extends BaseAdapter {

    private final int TITLE_TEXTSIZE = R.dimen.qb_px_14;

    private Activity activity;
    private final List<ProOutLineItem> alldatas;
    private List<ProOutLineItem> currentShowdatas;

    public OutLineListAdapter(Activity activity, final List<ProOutLineItem> alldatas) {
        this.activity = activity;
        this.alldatas = alldatas;
        this.currentShowdatas = new ArrayList<>();
        ThreadPoolUtils.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                for (ProOutLineItem item : alldatas) {
                    if (0 == item.level) {
                        currentShowdatas.add(item);
                    }

                    List<ProOutLineItem> items = getCurrentItemChilds(item);
                    item.isHasChildItems = !items.isEmpty();
                }
            }
        });
    }

    @Override
    public int getCount() {
        return currentShowdatas.size();
    }

    @Override
    public ProOutLineItem getItem(int position) {
        return currentShowdatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        HolderView holderView;
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_outline_list, null);
            holderView = new HolderView(view);
            view.setTag(holderView);
        } else {
            holderView = (HolderView) view.getTag();
        }
        ProOutLineItem item = getItem(position);
        /****** 目录名称的层次缩进 ******/
        String space = "";
        String null_str = parent.getContext().getString(R.string.null_str);
        for (int i = 1; i <= item.level; i++) {
            space += (null_str + null_str);
        }
        holderView.tv_padding.setText(space);

        /****** 下划线的层次缩进 ******/
        ViewGroup.LayoutParams params = holderView.tv_underLine_padding.getLayoutParams();
        params.width = (int)activity.getResources().getDimension(TITLE_TEXTSIZE) * item.level;
        holderView.tv_underLine_padding.setLayoutParams(params);

        if(item.isExpose){
            holderView.iv_irrow.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.view_ic_zhankai_select));
        }else{
            holderView.iv_irrow.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.view_ic_zhankai));
        }
        holderView.iv_irrow.setVisibility(item.isHasChildItems ? View.VISIBLE : View.INVISIBLE);
        holderView.tv_name.setText(item.title);
        holderView.tv_page.setText(String.valueOf(item.page + 1));
        holderView.tv_page.setTextColor(GlobalConfigs.THEME_COLOR);

        return view;
    }

    /**
     * @param ：[currentItem]
     * @return : java.util.List<com.kdanmobile.kmpdfkit.pdfcommon.OutlineItem>
     * @methodName ：getCurrentItemChilds created by liujiyuan on 2018/8/22 下午2:20.
     * @description ：如果该目录的 title 跟其他 item 的 parent_title，则证明其有子目录
     */
    private List<ProOutLineItem> getCurrentItemChilds(ProOutLineItem currentItem) {
        List<ProOutLineItem> childItems = new ArrayList<>();
        for (ProOutLineItem item : alldatas) {
            if (currentItem.title.equals(item.parent_title)) {
                childItems.add(item);
            }
        }
        return childItems;
    }

    /**
     * @param item
     * @return boolean
     * @methodName ：showExpose created by liujiyuan on 2018/8/22 下午2:45.
     * @description ：点击的目录如果有子目录，则展开，并添加子目录到当前的list中
     */
    public synchronized boolean showExpose(ProOutLineItem item) {
        List<ProOutLineItem> currentItemChildren = getCurrentItemChilds(item);
        if (currentItemChildren.isEmpty()) {
            return false;
        } else {
            int currentIndex = currentShowdatas.indexOf(item);
            item.isExpose = true;
            currentShowdatas.addAll(currentIndex + 1, currentItemChildren);
            return true;
        }
    }

    /**
     * @param ：[item]
     * @return : boolean
     * @methodName ：hideExpose created by liujiyuan on 2018/8/22 下午3:21.
     * @description ：点击的目录如果有子目录，则收起
     */
    public synchronized boolean hideExpose(ProOutLineItem item) {
        List<ProOutLineItem> currentItemChildren = getCurrentItemChilds(item);
        if (currentItemChildren.isEmpty()) {
            return false;
        } else {
            item.isExpose = false;
            hideLoopExpose(item);
            return true;
        }
    }

    /**
     * @param ：[proOutLineItem]
     * @return : void
     * @methodName ：hideLoopExpose created by liujiyuan on 2018/8/22 下午2:53.
     * @description ：在当前的list中删除子目录
     */
    private synchronized void hideLoopExpose(ProOutLineItem proOutLineItem) {
        try {
            List<ProOutLineItem> currentItemChildren = getCurrentItemChilds(proOutLineItem);
            for (ProOutLineItem item : currentItemChildren) {
                if (item.isExpose) {
                    item.isExpose = false;
                    hideLoopExpose(item);
                }
                currentShowdatas.remove(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class HolderView {
        public TextView tv_name;
        public TextView tv_page;
        public ImageView iv_irrow;
        public TextView tv_padding;
        public TextView tv_underLine_padding;

        public HolderView(View view) {
            tv_name = view.findViewById(R.id.id_outline_item_title);
            tv_page = view.findViewById(R.id.id_outline_item_page);
            iv_irrow = view.findViewById(R.id.id_outline_item_arrow);
            tv_padding = view.findViewById(R.id.id_outline_item_padding);
            tv_underLine_padding = view.findViewById(R.id.id_outline_item_underline_padding);
        }
    }
}
