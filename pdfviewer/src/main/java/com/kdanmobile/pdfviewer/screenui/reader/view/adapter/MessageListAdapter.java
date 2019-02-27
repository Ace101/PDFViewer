package com.kdanmobile.pdfviewer.screenui.reader.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kdanmobile.pdfviewer.R;
import java.util.ArrayList;

/**
 * @author zhaoyu
 * @classname MessageListAdapter
 * @date 2018/9/26 下午3:22
 * @description
 */
public class MessageListAdapter extends RecyclerView.Adapter{

    private ArrayList<MessageListBean> data;
    private Context context;
    private LayoutInflater inflater;

    public MessageListAdapter(Context context, ArrayList<MessageListBean> data){
        this.context = context;
        this.data = data;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View view;
        switch (MessageType.valueOf(viewType)){
            case SIMPLE:
                view = inflater.inflate(R.layout.more_message_list_item_simple, null);
                viewHolder = new SimpleViewHolder(view);
                break;
            case MORE:
                view = inflater.inflate(R.layout.more_message_list_item_more, null);
                viewHolder = new MoreViewHolder(view);
                break;
            default:
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (data.get(position).messageType){
            case SIMPLE:
                ((SimpleViewHolder) holder).key.setText(((SimpleDataBean)data.get(position)).key);
                ((SimpleViewHolder) holder).value.setText(((SimpleDataBean)data.get(position)).value);
                break;
            case MORE:
                ((MoreViewHolder) holder).key.setText(((MoreDataBean)data.get(position)).key);
                break;
            default:
                break;
        }

        if(holder != null){
            holder.itemView.setOnClickListener(view1 -> {
                switch (data.get(position).messageType){
                    case SIMPLE:
                        break;
                    case MORE:
                        try {
                            Class clazz = Class.forName(((MoreDataBean)data.get(position)).className);
                            Intent intent = new Intent(context, clazz);
                            context.startActivity(intent);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(data.get(position) instanceof SimpleDataBean){
            return MessageType.SIMPLE.ordinal();
        }else if(data.get(position) instanceof MoreDataBean){
            return MessageType.MORE.ordinal();
        }
//        MessageType messageType = data.get(position).messageType;
//        int type = data.get(position).messageType.ordinal();
        return MessageType.NULL.ordinal();
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder{
        public TextView key;
        public TextView value;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            key = itemView.findViewById(R.id.key);
            value = itemView.findViewById(R.id.value);
        }
    }

    public static class MoreViewHolder extends RecyclerView.ViewHolder{
        public TextView key;

        public MoreViewHolder(View itemView) {
            super(itemView);
            key = itemView.findViewById(R.id.key);
        }
    }

    public static class MessageListBean{
        public MessageType messageType = MessageType.NULL;
    }

    public static class SimpleDataBean extends MessageListBean{
        public String key;
        public String value;

        public SimpleDataBean(String key, String value){
            messageType = MessageType.SIMPLE;
            this.key = key;
            this.value = value;
        }
    }

    public static class MoreDataBean extends MessageListBean{
        public String key;
        public String className;

        public MoreDataBean(String key, String className){
            messageType = MessageType.MORE;
            this.key = key;
            this.className = className;
        }
    }

    public enum MessageType{
        NULL(0), SIMPLE(1), MORE(2);

        MessageType(int value){
        }
        public static MessageType valueOf(int index){
            if (index < 0 || index >= values().length) {
                index = 0;
            }
            return values()[index];
        }
    }
}
