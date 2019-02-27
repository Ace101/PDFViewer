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
import android.widget.LinearLayout;

import com.kdanmobile.pdfviewer.R;
import com.kdanmobile.pdfviewer.screenui.reader.view.been.ImageItem;
import com.kdanmobile.pdfviewer.utils.ToastUtil;
import com.kdanmobile.pdfviewer.utils.glide.GlideLoadUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @classname：SignImageRecycleViewAdapter
 * @author：liujiyuan
 * @date：2018/9/11 下午6:04
 * @description：适配签名图片列表的适配器
 */
public class SignImageRecycleViewAdapter extends RecyclerView.Adapter<SignImageRecycleViewAdapter.ViewHolder>{
    private OnImageSignItemClickListener onImageSignItemClickListener;
    private boolean isEdit = false;
    private final ArrayList<ImageItem> data;
    private Context context;

    public SignImageRecycleViewAdapter(Context context, List<ImageItem> datas) {
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

    /**
     * @methodName：selectAllItems created by liujiyuan on 2018/9/12 上午9:09.
     * @description：全选和非全选操作
     */
    public void selectAllItems(boolean isSeleted){
        for(ImageItem item:data){
            item.isChecked = isSeleted;
        }
        if(isSeleted){
            notifyItemRangeChanged(0, getItemCount(), "setChecked");
        }else{
            notifyItemRangeChanged(0, getItemCount(), "setUnChecked");
        }

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
    public SignImageRecycleViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_signature_list, parent, false);
        return new SignImageRecycleViewAdapter.ViewHolder(this, view);
    }

    @Override
    public void onBindViewHolder(@NonNull SignImageRecycleViewAdapter.ViewHolder holder, int position) {
        final ImageItem imageItem = getItemData(holder.getAdapterPosition());
        if (null != imageItem) {
            /****** 当Glide正在加载图片的时候，activity被销毁了，就会报错：You cannot load start a load for a destory activity ******/
            try {
                GlideLoadUtil.loadStampImg(holder.iv.getContext(), imageItem.filePath, holder.iv, imageItem.width/2, imageItem.height/2);
            } catch (Exception e) {
                ToastUtil.showToast(context, context.getText(R.string.load_image_sign_fail).toString());
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

                    if("setChecked".equals(msg)){
                        holder.get.setChecked(true);
                    }

                    if("setUnChecked".equals(msg)){
                        holder.get.setChecked(false);
                    }
                }
            }
        }
    }

    @Override
    public void onViewRecycled(@NonNull SignImageRecycleViewAdapter.ViewHolder holder) {
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

        private LinearLayout idItemSignListImageLayout;

        public ViewHolder(final SignImageRecycleViewAdapter adapter, View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.id_item_signList_iv);
            get = itemView.findViewById(R.id.id_item_signList_select);
            idItemSignListImageLayout = itemView.findViewById(R.id.id_item_signList_imageLayout);
            idItemSignListImageLayout.setOnClickListener(new SignImageRecycleViewAdapter.MyOnClickListener(this, adapter));
            get.setOnCheckedChangeListener(new SignImageRecycleViewAdapter.MyOnClickListener(this, adapter));
        }
    }

    private static class MyOnClickListener implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
        private final SignImageRecycleViewAdapter.ViewHolder viewHolder;
        private final SignImageRecycleViewAdapter adapter;

        public MyOnClickListener(SignImageRecycleViewAdapter.ViewHolder viewHolder, SignImageRecycleViewAdapter adapter) {
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
                if (null != adapter.onImageSignItemClickListener) {
                    adapter.onImageSignItemClickListener.onItemSelect(imageItem);
                }
            } else {
                viewHolder.get.setVisibility(View.INVISIBLE);
                if (null != adapter.onImageSignItemClickListener) {
                    adapter.onImageSignItemClickListener.onItemClick(v, viewHolder.getAdapterPosition(), imageItem.filePath, viewHolder.iv);
                }
            }
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            ImageItem imageItem = adapter.getItemData(viewHolder.getAdapterPosition());
            imageItem.isChecked = isChecked;
            if (null != adapter.onImageSignItemClickListener) {
                adapter.onImageSignItemClickListener.onItemSelect(imageItem);
            }
        }
    }

    public void setOnImageSignItemClickListener(OnImageSignItemClickListener onImageSignItemClickListener){
        this.onImageSignItemClickListener = onImageSignItemClickListener;
    }

    public interface OnImageSignItemClickListener {
        void onItemClick(View view, int position, String path, ImageView imageView);
        void onItemSelect(ImageItem imageItem);
    }
}
