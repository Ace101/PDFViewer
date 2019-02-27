package com.kdanmobile.pdfviewer.screenui.reader.view.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kdanmobile.pdfviewer.R;
import com.kdanmobile.pdfviewer.event.MessageEvent;
import com.kdanmobile.pdfviewer.screenui.reader.utils.InitKMPDFControllerUtil;
import com.kdanmobile.pdfviewer.screenui.reader.view.been.SearchTextInfo;
import com.kdanmobile.pdfviewer.utils.eventbus.ConstantBus;
import com.kdanmobile.pdfviewer.utils.eventbus.EventBusUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @classname：SearchTextRecyclerviewAdapter
 * @author：liujiyuan
 * @date：2018/8/23 下午3:34
 * @description：文本搜索的Recyclerview适配器
 */
public class SearchTextRecyclerviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    /****** 头部 ******/
    private final static int ITEM_TYPE_HEAD = 0x100;
    /****** 搜索的内容 ******/
    private final static int ITEM_TYPE_CONTENT = 0x200;
    private final List<SearchTextInfo> searchTextInfoList;
    private RecyclerView recyclerView;

    public SearchTextRecyclerviewAdapter(RecyclerView recyclerView) {
        this.searchTextInfoList = new ArrayList<>();
        this.recyclerView = recyclerView;
    }

    public void addItem(SearchTextInfo item) {
        searchTextInfoList.add(item);
    }

    public void addList(List<SearchTextInfo> list) {
        searchTextInfoList.addAll(list);
    }

    public void clearList() {
        searchTextInfoList.clear();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (ITEM_TYPE_HEAD == viewType) {
            return new SearchTextHeadViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_text_header, parent, false));
        }
        return new SearchTextContentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_text_content, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        if (searchTextInfoList != null && searchTextInfoList.size() > 0) {
            if (searchTextInfoList.get(position).isHeader) {
                return ITEM_TYPE_HEAD;
            }
        }
        return ITEM_TYPE_CONTENT;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final int adapterPosition = holder.getAdapterPosition();
        if (searchTextInfoList != null && searchTextInfoList.size() > 0) {
            if (holder instanceof SearchTextHeadViewHolder) {
                ((SearchTextHeadViewHolder) holder).idItemSearchHeadPage.setText(String.valueOf(searchTextInfoList.get(adapterPosition).page+1));
            } else {
                ((SearchTextContentViewHolder) holder).idItemSearchContentText.setText(searchTextInfoList.get(adapterPosition).stringBuilder);
                ((SearchTextContentViewHolder) holder).idItemSearchContentText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SearchTextInfo clickItem = searchTextInfoList.get(adapterPosition);
                        InitKMPDFControllerUtil.getInstance().getKmpdfDocumentController().setSearchResult(clickItem.search, clickItem.page, clickItem.rf);
                        EventBusUtils.getInstance().post(new MessageEvent<>(ConstantBus.READER_SEARCH_TEXT, clickItem.page));
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return searchTextInfoList.size();
    }

    public static class SearchTextHeadViewHolder extends RecyclerView.ViewHolder {
        private TextView idItemSearchHeadPage;

        public SearchTextHeadViewHolder(View itemView) {
            super(itemView);
            idItemSearchHeadPage = itemView.findViewById(R.id.id_item_search_head_page);
        }
    }

    public static class SearchTextContentViewHolder extends RecyclerView.ViewHolder {
        private TextView idItemSearchContentText;

        public SearchTextContentViewHolder(View itemView) {
            super(itemView);
            idItemSearchContentText = itemView.findViewById(R.id.id_item_search_content_text);
        }
    }
}
