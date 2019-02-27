package com.kdanmobile.pdfviewer.screenui.reader.view.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kdanmobile.pdfviewer.R;
import com.kdanmobile.pdfviewer.base.ProActivityManager;
import com.kdanmobile.pdfviewer.base.ProApplication;
import com.kdanmobile.pdfviewer.screenui.reader.configs.ApplicationConfig;
import com.kdanmobile.pdfviewer.utils.ToastUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {

    public static String[] ActivityList = {"com.kdanmobile.pdfviewer.screenui.reader.view.activity.PDFSimpleReaderActivity", "com.kdanmobile.pdfviewer.screenui.reader.view.activity.ProReaderActivity"};
    private static String[] ActivityName = {"Simple PDF Viewer", "Annotation Sample"};
    private final String PDF_Name = "Quick Start Guide.pdf";
    private ListView listView;
    private RelativeLayout iv_more_layout;
    private TextView sdkName;
    private TextView sdk_version;
    private final static String[] PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    private final static int RC_PERMISSION_PERM = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_list_layout);
        initView();
        onDoNext();
    }

    private void initView() {
        listView = findViewById(R.id.activity_list);
        iv_more_layout = findViewById(R.id.iv_more_layout);
        ActivityAdapter activityAdapter = new ActivityAdapter(this);
        listView.setAdapter(activityAdapter);
        sdkName = findViewById(R.id.sdk_name);
        sdk_version = findViewById(R.id.sdk_version);
        sdkName.setText(ApplicationConfig.KIT_NAME + " ");
        sdk_version.setText(ApplicationConfig.VERSION);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            String path = "";
            try {
                path = getTestPDF();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Intent intent = null;
            try {
                intent = new Intent(MainActivity.this, Class.forName(ActivityList[i]));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            intent.putExtra("file_absolutepath", path);
            startActivity(intent);
        });

        iv_more_layout.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, MoreMessageActivity.class);
            startActivity(intent);
        });
    }

    private String getTestPDF() throws IOException {
        // 获取SD卡根路径
        String sdPath = Environment.getExternalStorageDirectory().getPath();
        String filePath = sdPath + "/" + PDF_Name;
        File file = new File(filePath);
        if (!file.exists()) {
            AssetManager am = this.getAssets();
            InputStream is = am.open(PDF_Name);

            FileOutputStream fos = new FileOutputStream(filePath);
            // 写入SD卡
            byte[] buff = new byte[512];
            int count = is.read(buff);
            while (count != -1) {
                fos.write(buff);
                count = is.read(buff);
            }
            is.close();
            fos.close();
        }
        return filePath;
    }

    private boolean hasPermissions() {
        return EasyPermissions.hasPermissions(this, PERMISSIONS);
    }

    @AfterPermissionGranted(RC_PERMISSION_PERM)
    private void onDoNext() {
        if (!hasPermissions()) {
            EasyPermissions.requestPermissions(this, getString(R.string.app_permission_storage_and_camera), RC_PERMISSION_PERM, PERMISSIONS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            // 监听从设置页面返回，然后进行下一步处理，如弹出一个toast
            if (!hasPermissions()) {
                ToastUtil.showDebugToast(this, getString(R.string.app_exist_warn));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ProActivityManager.getInstance().destoryAllActivity();
                    }
                }, 2000);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        ProApplication.getRefWatcher(this).watch(this);
    }

    static class ActivityAdapter extends BaseAdapter {

        public Activity activity;

        public ActivityAdapter(Activity activity) {
            this.activity = activity;
        }

        @Override
        public int getCount() {
            return ActivityList.length;
        }

        @Override
        public Object getItem(int i) {
            return ActivityList[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null) {
                view = LayoutInflater.from(activity).inflate(R.layout.main_activity_list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.activity_name = view.findViewById(R.id.activity_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.activity_name.setText(ActivityName[i]);
            return view;
        }
    }

    static class ViewHolder {
        public TextView activity_name;
    }
}
