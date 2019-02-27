package com.kdanmobile.pdfviewer.screenui.reader.view.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.kdanmobile.pdfviewer.R;
import com.kdanmobile.pdfviewer.screenui.reader.configs.ApplicationConfig;

public class CopyrightActivity extends AppCompatActivity {

    private TextView copyright;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copyright);
        initView();
    }

    private void initView() {
        copyright = findViewById(R.id.copyright);
        copyright.setText(ApplicationConfig.COPYRIGHT);
    }
}
