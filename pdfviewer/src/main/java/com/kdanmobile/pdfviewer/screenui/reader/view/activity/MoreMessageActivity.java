package com.kdanmobile.pdfviewer.screenui.reader.view.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import com.kdanmobile.pdfviewer.R;
import com.kdanmobile.pdfviewer.screenui.common.decoration.ProItemDecoration;
import com.kdanmobile.pdfviewer.screenui.reader.configs.ApplicationConfig;
import com.kdanmobile.pdfviewer.screenui.reader.view.adapter.MessageListAdapter;

import java.util.ArrayList;

public class MoreMessageActivity extends AppCompatActivity {

    private RecyclerView messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_message);

        initView();
    }

    private void initView() {
        messageList = findViewById(R.id.message_list);
        ArrayList<MessageListAdapter.MessageListBean> data = new ArrayList<>();
        data.add(new MessageListAdapter.SimpleDataBean(getResources().getString(R.string.sdk_version), ApplicationConfig.VERSION));
        data.add(new MessageListAdapter.SimpleDataBean(getResources().getString(R.string.sdk_phone), ApplicationConfig.PHONE));
        data.add(new MessageListAdapter.SimpleDataBean(getResources().getString(R.string.sdk_email), ApplicationConfig.EMAIL));
        data.add(new MessageListAdapter.SimpleDataBean(getResources().getString(R.string.sdk_official_website), ApplicationConfig.OFFICIAL_WEBSITE));
        data.add(new MessageListAdapter.MoreDataBean(getResources().getString(R.string.sdk_copy_right), ApplicationConfig.COPYRIGHT_CLASSNAME));
        MessageListAdapter messageListAdapter = new MessageListAdapter(this, data);
        messageList.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        messageList.setItemAnimator(new DefaultItemAnimator());
        messageList.addItemDecoration(new ProItemDecoration());
        messageList.setAdapter(messageListAdapter);
    }
}
