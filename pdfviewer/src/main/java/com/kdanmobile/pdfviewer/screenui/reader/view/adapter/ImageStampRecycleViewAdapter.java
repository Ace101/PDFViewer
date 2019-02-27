package com.kdanmobile.pdfviewer.screenui.reader.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.kdanmobile.pdfviewer.R;
import com.kdanmobile.pdfviewer.screenui.reader.view.been.ImageItem;
import com.kdanmobile.pdfviewer.utils.ToastUtil;
import com.kdanmobile.pdfviewer.utils.glide.GlideLoadUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @classname：ImageStampRecycleViewAdapter
 * @author：liujiyuan
 * @date：2018/9/5 下午5:45
 * @description：自定义image stamp的适配器
 */
public class ImageStampRecycleViewAdapter extends RecyclerView.Adapter<ImageStampRecycleViewAdapter.ViewHolder> {

    private OnImageStampItemClickListener onImageStampItemClickListener;
    private boolean isEdit = false;
    private final ArrayList<ImageItem> data;
    private Context context;

    public ImageStampRecycleViewAdapter(Context context, List<ImageItem> datas) {
        this.context = context;
        this.data = new ArrayList<>();
        this.data.addAll(datas);
    }

    public void setMode(boolean isEdit) {
        this.isEdit = isEdit;
    }

    public void onAddData(ImageItem textStampConfig) {
        this.data.add(textStampConfig);
        notifyItemInserted(this.data.size() - 1);
    }

    public void onDeleteData(final int position) {
        this.data.remove(position);
        notifyItemRemoved(position);
    }

    public ArrayList<ImageItem> getData() {
        return data;
    }

    public ImageItem getItemData(final int position) {
        if (position < data.size()) {
            return data.get(position);
        }
        return null;
    }

    @NonNull
    @Override
    public ImageStampRecycleViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_stamp_list, parent, false);
        return new ImageStampRecycleViewAdapter.ViewHolder(this, view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageStampRecycleViewAdapter.ViewHolder holder, int position) {
        final ImageItem imageItem = getItemData(holder.getAdapterPosition());
        if (null != imageItem) {
            /****** 当Glide正在加载图片的时候，activity被销毁了，就会报错：You cannot load start a load for a destory activity ******/
            try {
                GlideLoadUtil.loadStampImg(holder.iv.getContext(), imageItem.filePath, holder.iv, imageItem.width/3, imageItem.height/3);
            } catch (Exception e) {
                ToastUtil.showToast(context, context.getText(R.string.load_image_stamp_fail).toString());
            }
            holder.get.setChecked(imageItem.isChecked);
            holder.get.setVisibility(isEdit ? View.VISIBLE : View.INVISIBLE);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (null == payloads || payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            for (int i = 0; i < payloads.size(); i++) {
                Object object = payloads.get(i);
                if (object instanceof String) {
                    String msg = (String) object;
                    holder.get.setChecked(false);
                    if ("checkbox_hide".equals(msg)) {
                        holder.get.setVisibility(View.INVISIBLE);
                    }

                    if ("checkbox_show".equals(msg)) {
                        holder.get.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        GlideLoadUtil.clearCache(context);
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView iv;
        public CheckBox get;

        public ViewHolder(final ImageStampRecycleViewAdapter adapter, View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.id_item_imageStamp_iv);
            get = itemView.findViewById(R.id.d_item_imageStamp_select);
            itemView.setOnClickListener(new MyOnClickListener(this, adapter));
            get.setOnCheckedChangeListener(new MyOnClickListener(this, adapter));
        }
    }

    private static class MyOnClickListener implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
        private final ViewHolder viewHolder;
        private final ImageStampRecycleViewAdapter adapter;

        public MyOnClickListener(ViewHolder viewHolder, ImageStampRecycleViewAdapter adapter) {
            this.viewHolder = viewHolder;
            this.adapter = adapter;
        }

        @Override
        public void onClick(View v) {
            ImageItem imageItem = adapter.getItemData(viewHolder.getAdapterPosition());
            if (adapter.isEdit) {
                if (imageItem.isChecked) {
                    imageItem.isChecked = false;
                    viewHolder.get.setChecked(false);
                } else {
                    imageItem.isChecked = true;
                    viewHolder.get.setChecked(true);
                }
                viewHolder.get.setVisibility(View.VISIBLE);
            } else {
                viewHolder.get.setVisibility(View.INVISIBLE);
                if (null != adapter.onImageStampItemClickListener) {
                    adapter.onImageStampItemClickListener.onItemClick(v, viewHolder.getAdapterPosition(), imageItem.filePath, viewHolder.iv);
                }
            }
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            ImageItem imageItem = adapter.getItemData(viewHolder.getAdapterPosition());
            if (isChecked) {
                imageItem.isChecked = true;
            } else {
                imageItem.isChecked = false;
            }
        }
    }

    public void setOnImageStampItemClickListener(OnImageStampItemClickListener onImageStampItemClickListener){
        this.onImageStampItemClickListener = onImageStampItemClickListener;
    }

    public interface OnImageStampItemClickListener {
        void onItemClick(View view, int position, String path, ImageView imageView);
    }
}

