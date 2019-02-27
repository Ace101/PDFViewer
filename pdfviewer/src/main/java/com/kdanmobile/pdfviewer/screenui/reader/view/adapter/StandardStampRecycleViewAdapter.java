package com.kdanmobile.pdfviewer.screenui.reader.view.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.kdanmobile.pdfviewer.R;
import com.kdanmobile.kmpdfkit.annotation.stamp.StampConfig;

/**
 * @classname：StandardStampRecycleViewAdapter
 * @author：liujiyuan
 * @date：2018/9/4 上午10:43
 * @description：标准stamp list的适配器
 */
public class StandardStampRecycleViewAdapter extends RecyclerView.Adapter<StandardStampRecycleViewAdapter.ViewHolder>{

    private StampConfig.STANDARD_STAMP_RES[] data;

    private OnStandardStampItemClickListener onStandardStampItemClickListener;

    public StandardStampRecycleViewAdapter(StampConfig.STANDARD_STAMP_RES[] data){
        this.data = data;
    }
    @NonNull
    @Override
    public StandardStampRecycleViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_standard_stamp_list, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StandardStampRecycleViewAdapter.ViewHolder holder, int position) {
        holder.iv.setImageResource(data[position].getResId());
        holder.itemView.setOnClickListener(new MyOnClickListener(position, data[position]));
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    private class MyOnClickListener implements View.OnClickListener {
        private int position;
        private StampConfig.STANDARD_STAMP_RES resId;

        public MyOnClickListener(int position, StampConfig.STANDARD_STAMP_RES resId) {
            this.position = position;
            this.resId = resId;
        }

        @Override
        public void onClick(View v) {
            if(onStandardStampItemClickListener != null){
                onStandardStampItemClickListener.onItemClick(v, position, resId);
            }
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView iv;

        public ViewHolder(View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.id_item_standard_stamp_list_iv);
        }
    }

    public void setOnStandardStampItemClickListener(OnStandardStampItemClickListener onStandardStampItemClickListener){
        this.onStandardStampItemClickListener = onStandardStampItemClickListener;
    }

    public interface OnStandardStampItemClickListener{
        void onItemClick(View view, int position, StampConfig.STANDARD_STAMP_RES resId);
    }
}
