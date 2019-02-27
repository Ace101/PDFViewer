package com.kdanmobile.pdfviewer.screenui.reader.view.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kdanmobile.kmpdfkit.pdfcommon.Bookmark;
import com.kdanmobile.pdfviewer.R;
import com.kdanmobile.pdfviewer.event.MessageEvent;
import com.kdanmobile.pdfviewer.screenui.common.viewholder.BaseViewHolder;
import com.kdanmobile.pdfviewer.screenui.reader.utils.JsonUtil;
import com.kdanmobile.pdfviewer.utils.GlobalConfigs;
import com.kdanmobile.pdfviewer.utils.eventbus.ConstantBus;
import com.kdanmobile.pdfviewer.utils.eventbus.EventBusUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @classname：BookmarkListAdapter
 * @author：liujiyuan
 * @date：2018/8/28 上午9:15
 * @description：书签界面适配器
 */
public class BookmarkListAdapter extends RecyclerView.Adapter<BookmarkListAdapter.BookMarkViewHolder>{
    private List<Bookmark> bookMarkDataList;
    private Bookmark bookMarkItem;

    public BookmarkListAdapter() {
        this.bookMarkDataList = new ArrayList<>();
    }

    public void setData(List<Bookmark> list){
        bookMarkDataList.clear();

        if(list != null) {
            bookMarkDataList.addAll(list);
        }
        notifyDataSetChanged();
    }

    public Bookmark getItemBean(int position) {
        if (bookMarkDataList.size() > position) {
            return bookMarkDataList.get(position);
        }
        return null;
    }

    @NonNull
    @Override
    public BookMarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BookMarkViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bookmark_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BookmarkListAdapter.BookMarkViewHolder holder, int position) {
        final int adapterPosition = holder.getAdapterPosition();
        bookMarkItem = getItemBean(adapterPosition);
        holder.idBookmarkSwipeItemTitle.setText(bookMarkItem.title);
        holder.idBookmarkSwipeItemPage.setText(String.valueOf(bookMarkItem.pageNum +1));
        holder.idBookmarkSwipeItemPage.setTextColor(GlobalConfigs.THEME_COLOR);
        holder.idBookmarkSwipeItemTime.setText(JsonUtil.BookmarkTimeToString(bookMarkItem.createTime));
        holder.idItemBookmarkSwipe.setOnClickListener(v -> EventBusUtils.getInstance().post(new MessageEvent<>(ConstantBus.BOOKMARK_GOTOPAGE, bookMarkDataList.get(adapterPosition).pageNum)));
        holder.idBookmarkSwipeMenuRename.setOnClickListener(v -> EventBusUtils.getInstance().post(new MessageEvent<>(ConstantBus.BOOKMARK_RENAME, bookMarkDataList.get(adapterPosition).pageNum)));
        holder.idBookmarkSwipeMenuDelete.setOnClickListener(v -> EventBusUtils.getInstance().post(new MessageEvent<>(ConstantBus.BOOKMARK_DELETE, bookMarkDataList.get(adapterPosition).pageNum)));
    }

    @Override
    public int getItemCount() {
        return bookMarkDataList.size();
    }

    static class BookMarkViewHolder extends BaseViewHolder{

        private TextView idBookmarkSwipeItemPage;
        private TextView idBookmarkSwipeItemTitle;
        private TextView idBookmarkSwipeItemTime;
        private ImageButton idBookmarkSwipeMenuRename;
        private ImageButton idBookmarkSwipeMenuDelete;
        private RelativeLayout idItemBookmarkSwipe;

        public BookMarkViewHolder(View itemView) {
            super(itemView);

            idBookmarkSwipeItemPage = itemView.findViewById(R.id.id_bookmark_swipe_item_page);
            idBookmarkSwipeItemTitle = itemView.findViewById(R.id.id_bookmark_swipe_item_title);
            idBookmarkSwipeItemTime = itemView.findViewById(R.id.id_bookmark_swipe_item_time);
            idBookmarkSwipeMenuRename = itemView.findViewById(R.id.id_bookmark_swipe_menu_rename);
            idBookmarkSwipeMenuDelete = itemView.findViewById(R.id.id_bookmark_swipe_menu_delete);
            idItemBookmarkSwipe = itemView.findViewById(R.id.id_item_bookmark_swipe);

        }
    }
}
