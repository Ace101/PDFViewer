package com.kdanmobile.pdfviewer.screenui.reader.view.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.kdanmobile.kmpdfkit.annotation.stamp.TextStampConfig;
import com.kdanmobile.kmpdfkit.annotation.stamp.view.KMPDFStampTextView;
import com.kdanmobile.pdfviewer.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @classname：TextStampRecycleViewAdapter
 * @author：liujiyuan
 * @date：2018/9/4 下午3:21
 * @description：自定义text stamp的适配器
 */
public class TextStampRecycleViewAdapter extends RecyclerView.Adapter<TextStampRecycleViewAdapter.ViewHolder> {
    private TextStampRecycleViewAdapter.OnTextStampItemClickListener onTextStampItemClickListener;
    private boolean isEdit = false;
    private final ArrayList<TextStampConfig> data;

    public TextStampRecycleViewAdapter(List<TextStampConfig> datas) {
        this.data = new ArrayList<>();
        this.data.addAll(datas);
    }

    public void setMode(boolean isEdit) {
        this.isEdit = isEdit;
    }

    public void onAddData(TextStampConfig textStampConfig) {
        this.data.add(textStampConfig);
        notifyItemInserted(this.data.size() - 1);
    }

    public void onDeleteData(final int position) {
        this.data.remove(position);
        notifyItemRemoved(position);
    }

    public TextStampConfig getItemData(final int position) {
        if (position < data.size()) {
            return data.get(position);
        }
        return null;
    }

    public ArrayList<TextStampConfig> getData() {
        return data;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @NonNull
    @Override
    public TextStampRecycleViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text_stamp_list, parent, false);
        return new TextStampRecycleViewAdapter.ViewHolder(this, view);
    }

    @Override
    public void onBindViewHolder(@NonNull TextStampRecycleViewAdapter.ViewHolder holder, int position) {
        final TextStampConfig textStampConfig = getItemData(holder.getAdapterPosition());
        if (null != textStampConfig) {
            holder.stampTextView.setTextColor(textStampConfig.textColor);
            holder.stampTextView.setBgColor(textStampConfig.bgColor);
            holder.stampTextView.setLineColor(textStampConfig.lineColor);
            holder.stampTextView.setContent(textStampConfig.content);
            holder.stampTextView.setTimeType(textStampConfig.timeType);
            holder.stampTextView.setShape(textStampConfig.shape);
            holder.get.setChecked(textStampConfig.isChecked);
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

    public void setTextStampItemClickListener(OnTextStampItemClickListener onTextStampItemClickListener){
        this.onTextStampItemClickListener = onTextStampItemClickListener;
    }

    public interface OnTextStampItemClickListener {
        void onItemClick(View view, int position, TextStampConfig textStampConfig, KMPDFStampTextView stampTextView);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        public KMPDFStampTextView stampTextView;
        public CheckBox get;

        public ViewHolder(final TextStampRecycleViewAdapter adapter, final View itemView) {
            super(itemView);
            stampTextView = itemView.findViewById(R.id.id_item_textStamp_stampTextView);
            get = itemView.findViewById(R.id.d_item_textStamp_select);

            itemView.setOnClickListener(new TextStampRecycleViewAdapter.MyOnClickListener(adapter, this, stampTextView, get));
            get.setOnCheckedChangeListener(new TextStampRecycleViewAdapter.MyOnClickListener(adapter, this, stampTextView, get));
        }
    }

    private static class MyOnClickListener implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
        private final KMPDFStampTextView stampTextView;
        private final CheckBox get;
        private final TextStampRecycleViewAdapter adapter;
        private final ViewHolder viewHolder;

        public MyOnClickListener(TextStampRecycleViewAdapter adapter, ViewHolder viewHolder, KMPDFStampTextView stampTextView, CheckBox get) {
            this.adapter = adapter;
            this.viewHolder = viewHolder;
            this.stampTextView = stampTextView;
            this.get = get;
        }

        @Override
        public void onClick(View v) {
            final int position = viewHolder.getAdapterPosition();
            TextStampConfig textStampConfig = adapter.getItemData(position);
            if (adapter.isEdit) {
                textStampConfig.isChecked = !textStampConfig.isChecked;
                get.setChecked(textStampConfig.isChecked);
                get.setVisibility(View.VISIBLE);
            } else {
                get.setVisibility(View.INVISIBLE);
                if (adapter.onTextStampItemClickListener != null) {
                    adapter.onTextStampItemClickListener.onItemClick(v, position, textStampConfig, stampTextView);
                }
            }
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            final int position = viewHolder.getAdapterPosition();
            TextStampConfig textStampConfig = adapter.getItemData(position);
            textStampConfig.isChecked = isChecked;
        }
    }
}
