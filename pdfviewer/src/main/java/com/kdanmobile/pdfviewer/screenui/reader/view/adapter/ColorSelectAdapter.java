package com.kdanmobile.pdfviewer.screenui.reader.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kdanmobile.pdfviewer.R;
import com.kdanmobile.pdfviewer.screenui.common.listener.OnItemClickListener;
import com.kdanmobile.pdfviewer.screenui.reader.widget.SelectableCircleView;

/**
 * @classname：ColorSelectAdapter
 * @author：liujiyuan
 * @date：2018/8/17 下午6:37
 * @description：颜色选择的适配器
 */
public class ColorSelectAdapter extends RecyclerView.Adapter<ColorSelectAdapter.ViewHolder>{

    private static int selected = 0;

    private int[] colorArr;


    private OnItemClickListener onItemClickListener;

    private int layoutId = -1;

    public ColorSelectAdapter(int[] colorArr){
        this.colorArr = colorArr;
    }

    public ColorSelectAdapter(int[] colorArr, int layoutId){
        this.colorArr = colorArr;
        this.layoutId = layoutId;
    }

    public void setSelectIndex(int selectIndex){
        selected = selectIndex;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(layoutId != -1){
            view = LayoutInflater.from(parent.getContext()).inflate(layoutId, null);
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_color_list, null);
        }

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.selectableCircleView.setColor(colorArr[position]);
        if(position == selected){
            holder.selectableCircleView.setIsDrawRecr(true);
        }else{
            holder.selectableCircleView.setIsDrawRecr(false);
        }
        holder.selectableCircleView.setOnClickListener(new OnColorClickListener(this, onItemClickListener, position));
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return colorArr.length;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        SelectableCircleView selectableCircleView;

        public ViewHolder(View itemView) {
            super(itemView);
            selectableCircleView = itemView.findViewById(R.id.id_select_color_item_color);
        }
    }

    static class OnColorClickListener implements View.OnClickListener {
        private int position;
        private OnItemClickListener onItemClickListener;
        private ColorSelectAdapter colorSelectAdapter;

        public OnColorClickListener( ColorSelectAdapter colorSelectAdapter, OnItemClickListener onItemClickListener, int position) {
            this.colorSelectAdapter = colorSelectAdapter;
            this.position = position;
            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public void onClick(View v) {
            selected = position;
            colorSelectAdapter.notifyDataSetChanged();
            if(onItemClickListener != null){
                onItemClickListener.onItemClick(position);
            }
        }
    }
}
