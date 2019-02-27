package com.kdanmobile.pdfviewer.screenui.reader.view.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kdanmobile.kmpdfkit.manager.controller.KMPDFDocumentController;
import com.kdanmobile.kmpdfkit.pdfcommon.Bookmark;
import com.kdanmobile.pdfviewer.R;
import com.kdanmobile.pdfviewer.screenui.reader.presenter.EditPagePresenter;
import com.kdanmobile.pdfviewer.screenui.reader.utils.InitKMPDFControllerUtil;
import com.kdanmobile.pdfviewer.screenui.reader.view.listener.drag.DefaultItemTouchHelpCallback;
import com.kdanmobile.pdfviewer.utils.GlobalConfigs;
import com.kdanmobile.pdfviewer.utils.ToastUtil;
import com.kdanmobile.pdfviewer.utils.threadpools.SimpleBackgroundTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kdanmobile.pdfviewer.screenui.reader.view.activity.EditPageActivity.EDIT_TYPE;
import static com.kdanmobile.pdfviewer.screenui.reader.view.activity.EditPageActivity.VIEW_TYPE;


/**
 * @classname：EditPageRecyclerViewAdapter
 * @author：liujiyuan
 * @date：2018/8/29 下午2:01
 * @description：页面编辑适配器
 */
public class EditPageRecyclerViewAdapter extends RecyclerView.Adapter<EditPageRecyclerViewAdapter.EditPageHolderView> implements DefaultItemTouchHelpCallback.OnItemTouchCallbackListener{

    private final static int MODE_DAY = 0;

    /****** 通过 LruCache<String,Bitmap> least recentlly use 最少最近使用算法会将内存控制在一定的大小内, 超出最大值时会自动回收 ******/
    public static LruCache<String,Bitmap> mMemoryCache;
    /****** 得到手机最大允许内存的1/8,即超过指定内存,则开始回收 ******/
    long maxMemory = Runtime.getRuntime().maxMemory()/8;

    /****** 复选框已选项缓存集合、页码 —— isSelected ******/
    public Map<String, Boolean> mapSelect;
    private Handler handler;
    private int currentPage;
    private int totalPages;
    private String fileName;
    private int width, padding;
    private int menu_type = VIEW_TYPE;

    private  Activity mActivity;

    /****** 书签 ******/
    private Bookmark[] mBookmarks;

    private KMPDFDocumentController kmpdfDocumentController;

    public EditPageRecyclerViewAdapter(Activity activity, Handler handler) {
        this.mActivity = activity;
        kmpdfDocumentController = InitKMPDFControllerUtil.getInstance().getKmpdfDocumentController();
        this.mBookmarks = kmpdfDocumentController.getBookmarks();
        this.fileName = GlobalConfigs.FILE_ABSOLUTE_PATH.replaceAll("/", "_");
        this.handler = handler;
        this.totalPages = kmpdfDocumentController.getDocumentPageCount(false);
        currentPage = kmpdfDocumentController.getCurrentPageNum();
        mMemoryCache=new LruCache<String,Bitmap>((int) maxMemory){
            /****** 用于计算每个条目的大小 ******/
            @Override
            protected int sizeOf(String key, Bitmap value) {
                int byteCount = value.getByteCount();
                return byteCount;
            }
        };
    }

    @NonNull
    @Override
    public EditPageRecyclerViewAdapter.EditPageHolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.item_edit_page_list, null);
        EditPageHolderView holder = new EditPageHolderView(view, mActivity, kmpdfDocumentController);
        holder.setSize(width, padding);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull EditPageHolderView holder, int position) {
        /****** 书签 ******/
        if (isHasBookmarkInfo(holder.getAdapterPosition())) {
            holder.idItemEditPageBookmark.setVisibility(View.VISIBLE);
        } else {
            holder.idItemEditPageBookmark.setVisibility(View.GONE);
        }

        /****** 页码显示，当前页边框 ******/
        holder.idItemEditPagePageNum.setText("" + (holder.getAdapterPosition() + 1));
        if (currentPage == holder.getAdapterPosition()) {
            holder.idItemEditPageRel.setBackgroundColor(GlobalConfigs.THEME_COLOR);
            holder.idItemEditPagePageNum.setTextColor(GlobalConfigs.THEME_COLOR);
        } else {

            holder.idItemEditPageRel.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.color_page_edit_frame));
            holder.idItemEditPagePageNum.setTextColor(ContextCompat.getColor(mActivity, R.color.theme_color_black));
        }

        /****** 复选框 ******/
        holder.idItemEditPageSelect.setChecked(false);
        if (menu_type == VIEW_TYPE) {
            holder.idItemEditPageSelect.setVisibility(View.GONE);
        } else {
            holder.idItemEditPageSelect.setVisibility(View.VISIBLE);
            if ((mapSelect != null) && mapSelect.containsKey(String.valueOf(holder.getAdapterPosition()))) {
                holder.idItemEditPageSelect.setChecked(true);
            }
        }

        /****** 缩略图 ******/
        holder.loadImage(fileName);
    }

    @Override
    public void onBindViewHolder(@NonNull EditPageRecyclerViewAdapter.EditPageHolderView holder, int position, @NonNull List<Object> payloads) {
        if (null == payloads || payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            for (int i = 0; i < payloads.size(); i++) {
                Object object = payloads.get(i);
                if (object instanceof String) {
                    String msg = (String) object;
                    holder.idItemEditPageSelect.setChecked(false);
                    if ("checkbox_hide".equals(msg)) {
                        holder.idItemEditPageSelect.setVisibility(View.INVISIBLE);
                    }

                    if ("checkbox_show".equals(msg)) {
                        holder.idItemEditPageSelect.setVisibility(View.VISIBLE);
                    }

                    if("setChecked".equals(msg)){
                        holder.idItemEditPageSelect.setChecked(true);
                    }

                    if("setUnChecked".equals(msg)){
                        holder.idItemEditPageSelect.setChecked(false);
                    }
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return totalPages;
    }

    public void setBookmarks(Bookmark[] bookmarks) {
        mBookmarks = bookmarks;
    }

    public void setPageCount(final int totalPages){
        this.totalPages = totalPages;
    }

    /**
     * @methodName：setSize created by liujiyuan on 2018/8/29 下午3:32.
     * @description：设置每个item的长宽间距
     */
    public void setSize(int width, int padding) {
        this.width = width;
        this.padding = padding;
    }

    /**
     * @methodName：isHasBookmarkInfo created by liujiyuan on 2018/8/30 上午11:12.
     * @description：判断该页是否用户书签
     */
    private boolean isHasBookmarkInfo(int page) {
        if (mBookmarks != null) {
            for (Bookmark bookmark : mBookmarks) {
                if (page == bookmark.pageNum) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @methodName：setTpe created by liujiyuan on 2018/8/29 下午3:41.
     * @description：根据菜单按钮设置显示的类型
     */
    public void setType(int type){
        if(mapSelect != null) {
            mapSelect.clear();
        }
        menu_type = type;
        notifyItemChanged(currentPage);
        switch (type){
            case VIEW_TYPE:
                notifyItemRangeChanged(0, getItemCount(), "checkbox_hide");
                break;
            case EDIT_TYPE:
                notifyItemRangeChanged(0, getItemCount(), "checkbox_show");
                break;
            default:
        }
    }

    /**
     * @methodName：setItemClick created by liujiyuan on 2018/8/30 上午11:13.
     * @description：单击选择
     */
    public void setItemClick(int position) {
        if (menu_type != VIEW_TYPE) {
            if (mapSelect == null) {
                mapSelect = new HashMap<>();
            }
            if (mapSelect.containsKey(String.valueOf(position))) {
                mapSelect.remove(String.valueOf(position));
            } else {
                mapSelect.put(String.valueOf(position), true);
            }
        }
    }

    /**
     * @methodName：setAllClick created by liujiyuan on 2018/8/30 上午11:13.
     * @description：复选框全选操作
     */
    public void setAllClick() {
        if (menu_type != VIEW_TYPE) {
            if (mapSelect == null) {
                mapSelect = new HashMap<>();
            }
            for (int i = 0; i < totalPages; i++) {
                mapSelect.put("" + i, true);
            }
            notifyItemRangeChanged(0, getItemCount(), "setChecked");
        }
    }

    /**
     * @methodName：setAllUnClick created by liujiyuan on 2018/8/30 上午11:12.
     * @description：复选框全反选操作
     */
    public void setAllUnClick() {
        if (menu_type != VIEW_TYPE) {
            if (mapSelect == null) {
                mapSelect = new HashMap<>();
            }
            mapSelect.clear();
            notifyItemRangeChanged(0, getItemCount(), "setUnChecked");
        }
    }

    /**
     * @methodName：onDeleteNotification created by liujiyuan on 2018/8/30 上午11:14.
     * @description：页面删除刷新通知
     */
    public void onDeleteNotification(final int totalPages) {
        handler.post(() -> {
            setPageCount(totalPages);
            /****** 删除页码，需要清理掉已缓存的缩列图，根据最新索引重新加载缩列图 ******/
            mMemoryCache.evictAll();
            notifyDataSetChanged();
        });
    }

    /**
     * @methodName：onRorateNotification created by liujiyuan on 2018/8/30 上午11:59.
     * @description：指定缩列图页面刷新
     */
    public void onRotateNotification(final int[] pages) {
        handler.post(() -> {
            for (int page : pages) {
                notifyItemChanged(page);
            }
        });
    }

    @Override
    public void onSwiped(int adapterPosition) {
        /****** do nothing ******/
    }

    /**
     * @param ：[sourceViewHolder, targetViewHolder]
     * @return : boolean
     * @methodName ：onDragging created by liujiyuan on 2018/8/30 下午3:59.
     * @description ：实现当前拖拽的item排序功能
     */
    @Override
    public boolean onDragging(RecyclerView.ViewHolder sourceViewHolder, RecyclerView.ViewHolder targetViewHolder) {
        int sourcePosition = sourceViewHolder.getAdapterPosition();
        int targetPosition = targetViewHolder.getAdapterPosition();
        notifyItemMoved(sourcePosition, targetPosition);
        return true;
    }

    /**
     * @param ：[sourceViewHolder, sourcePosition, targetPosition]
     * @return : void
     * @methodName ：onMoved created by liujiyuan on 2018/8/30 下午4:06.
     * @description ：拖拽当前item到指定位置的具体实现
     */
    @Override
    public void onMoved(RecyclerView.ViewHolder sourceViewHolder, int sourcePosition, int targetPosition) {
        try {
            boolean isSuccess = kmpdfDocumentController.reorderPages(sourcePosition, targetPosition);

            if (isSuccess) {
                EditPagePresenter.isChangeSuccess = true;
                /****** 获取最新的书签数据 ******/
                this.mBookmarks = kmpdfDocumentController.getBookmarks();

                /****** 往前移动 ******/
                if (sourcePosition > targetPosition) {
                    Bitmap sourceBitmap = mMemoryCache.get(fileName + sourcePosition);
                    for (int i = sourcePosition; i > targetPosition; i--) {
                        mMemoryCache.put(fileName + i, mMemoryCache.get(fileName + (i - 1)));
                    }
                    mMemoryCache.put(fileName + targetPosition, sourceBitmap);
                    notifyItemRangeChanged(targetPosition, sourcePosition - targetPosition + 1);
                }

                /****** 往后移动 ******/
                if (sourcePosition < targetPosition) {
                    Bitmap sourceBitmap = mMemoryCache.get(fileName + sourcePosition);
                    for (int i = sourcePosition; i < targetPosition; i++) {
                        mMemoryCache.put(fileName + i, mMemoryCache.get(fileName + (i + 1)));
                    }
                    mMemoryCache.put(fileName + targetPosition, sourceBitmap);
                    notifyItemRangeChanged(sourcePosition, targetPosition - sourcePosition + 1);
                }

            } else {
                ToastUtil.showToast(sourceViewHolder.itemView.getContext(), R.string.pdf_drag_failed);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param ：[sourceViewHolder, targetViewHolder]
     * @return : void
     * @methodName ：onSwaped created by liujiyuan on 2018/8/30 下午4:05.
     * @description ：实现两个item的位置交换
     */
    @Override
    public void onSwaped(RecyclerView.ViewHolder sourceViewHolder, RecyclerView.ViewHolder targetViewHolder) {
        try {
            int sourcePosition = sourceViewHolder.getAdapterPosition();
            int targetPosition = targetViewHolder.getAdapterPosition();
            boolean isSuccess = kmpdfDocumentController.exchangePages(sourcePosition, targetPosition);
            if (isSuccess) {
                EditPagePresenter.isChangeSuccess = true;
                String sourceKey = fileName + sourcePosition;
                String targetKey = fileName + targetPosition;

                Bitmap sourceBitmap = mMemoryCache.get(sourceKey);
                Bitmap targetBitmap = mMemoryCache.get(targetKey);
                mMemoryCache.put(sourceKey, targetBitmap);
                mMemoryCache.put(targetKey, sourceBitmap);

                ((EditPageHolderView) sourceViewHolder).idItemEditPageImage.setImageBitmap(targetBitmap);
                ((EditPageHolderView) targetViewHolder).idItemEditPageImage.setImageBitmap(sourceBitmap);

            } else {
                ToastUtil.showToast(sourceViewHolder.itemView.getContext(), R.string.button_clickable_text);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class EditPageHolderView extends RecyclerView.ViewHolder {
        private RelativeLayout idItemEditPageRel;
        private ImageView idItemEditPageImage;
        private ImageView idItemEditPageBookmark;
        private CheckBox idItemEditPageSelect;
        private TextView idItemEditPagePageNum;

        private Context context;
        private SimpleBackgroundTask editPage_async;
        private Activity activity;
        private KMPDFDocumentController kmpdfDocumentController;
        private int pic_width;

        public EditPageHolderView(View itemView, Activity activity, KMPDFDocumentController kmpdfDocumentController) {
            super(itemView);
            context = itemView.getContext();
            this.activity = activity;
            this.kmpdfDocumentController = kmpdfDocumentController;

            idItemEditPageRel = itemView.findViewById(R.id.id_item_edit_page_rel);
            idItemEditPageImage = itemView.findViewById(R.id.id_item_edit_page_image);
            idItemEditPageBookmark = itemView.findViewById(R.id.id_item_edit_page_bookmark);
            idItemEditPageSelect = itemView.findViewById(R.id.id_item_edit_page_select);
            idItemEditPagePageNum = itemView.findViewById(R.id.id_item_edit_page_pageNum);

        }

        /**
         * @param ：[width, padding]
         * @return : void
         * @methodName ：setSize created by liujiyuan on 2018/8/30 上午11:12.
         * @description ：设定每个Item的大小
         */
        public void setSize(int width, int padding) {
            pic_width = width;
            int height = (int) context.getResources().getDimension(R.dimen.qb_px_134);
            if (width != idItemEditPageRel.getWidth()) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
                params.leftMargin = padding;
                params.rightMargin = padding;
                idItemEditPageRel.setLayoutParams(params);
                int padding_marg = (int)context.getResources().getDimension(R.dimen.qb_px_1);
                idItemEditPageImage.setPadding(padding_marg, padding_marg, padding_marg, padding_marg);
            }
        }


        /**
         * @param ：[fileName]
         * @return : void
         * @methodName ：loadImage created by liujiyuan on 2018/8/29 下午3:55.
         * @description ：加载当前页面的缩略图
         */
        @SuppressLint("StaticFieldLeak")
        private void loadImage(final String fileName) {
            stopLoadEditPageSync();

            editPage_async = new SimpleBackgroundTask<Bitmap>(activity) {

                @Override
                protected Bitmap onRun() {
                    String pageNameTag = fileName + getAdapterPosition();
                    if (null != mMemoryCache.get(pageNameTag)) {
                        return mMemoryCache.get(pageNameTag);
                    } else {
                        Bitmap bitmap = kmpdfDocumentController.covertPDFToBitmap(getAdapterPosition(), pic_width, MODE_DAY, true);
                        if(bitmap != null && pageNameTag != null) {
                            mMemoryCache.put(pageNameTag, bitmap);
                        }
                        return bitmap;
                    }
                }

                @Override
                protected void onSuccess(Bitmap result) {
                    idItemEditPageImage.setImageBitmap(result);
                    idItemEditPageImage.invalidate();
                }
            };

            editPage_async.execute();
        }

        private void stopLoadEditPageSync() {
            if (null != editPage_async && !editPage_async.isCancelled()) {
                editPage_async.cancel(true);
                idItemEditPageImage.setImageBitmap(null);
                idItemEditPageImage.invalidate();
            }
        }
    }
}
