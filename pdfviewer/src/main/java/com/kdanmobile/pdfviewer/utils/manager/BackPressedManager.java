package com.kdanmobile.pdfviewer.utils.manager;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

/**
 * @classname：BackPressedManager
 * @description：
 */
public class BackPressedManager {
    /****** 采用十六进制的整形常量 ******/
    private static final int Exit = 0x10011111;

    /****** 点击返回按钮，确定是否退出 ******/
    private boolean isExit = false;

    private static BackPressedManager instance = null;

    private BackListener listener;

    private BackExitListener backListener;

    private static String flag = "";

    public enum Mode {
        EXIT, BACK
    }

    private static Mode mode = Mode.BACK;

    private BackPressedManager(String flag, Mode mode, BackListener listener) {
        BackPressedManager.flag = flag;
        this.listener = listener;
        BackPressedManager.mode = mode;
        this.isExit = false;
    }

    private BackPressedManager(String flag, Mode mode, BackExitListener listener) {
        BackPressedManager.flag = flag;
        this.backListener = listener;
        BackPressedManager.mode = mode;
        this.isExit = false;
    }

    public static BackPressedManager getInstance(Class cls, Mode mode_, BackListener listener) {
        if ((instance == null) || (!((mode == mode_) && cls.getSimpleName().equals(flag)))) {
            synchronized (BackPressedManager.class) {
                if ((instance == null) || ((instance != null) && !((mode == mode_) && cls.getSimpleName().equals(flag)))) {
                    instance = new BackPressedManager(cls.getSimpleName(), mode_, listener);
                }
            }
        }
        return instance;
    }

    public static BackPressedManager getInstance(Class cls, Mode mode_, BackExitListener listener) {
        if ((instance == null) || (!((mode == mode_) && cls.getSimpleName().equals(flag)))) {
            synchronized (BackPressedManager.class) {
                if ((instance == null) || ((instance != null) && !((mode == mode_) && cls.getSimpleName().equals(flag)))) {
                    instance = new BackPressedManager(cls.getSimpleName(), mode_, listener);
                }
            }
        }
        return instance;
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Exit:
                    /****** 复位 ******/
                    isExit = false;
                    break;
                default:
            }
        }
    };

    public void onBackPressed() {
        if (mode == Mode.BACK) {
            if (isExit) {
                listener.Confirm_Do();
            } else {
                isExit = true;
                handler.sendEmptyMessageDelayed(Exit, 2500);
                listener.Prompt_Do();
            }
        } else {
            backListener.Exit();
        }
    }

    public void secondBack(BackListener listener) {
        if (isExit) {
            listener.Confirm_Do();
        } else {
            isExit = true;
            handler.sendEmptyMessageDelayed(Exit, 2500);
            listener.Prompt_Do();
        }
    }

    public interface BackListener {
        void Prompt_Do();

        void Confirm_Do();
    }

    public interface BackExitListener {
        void Exit();
    }
}
