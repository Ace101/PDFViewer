package com.kdanmobile.pdfviewer.screenui.widget.commondialog;

import android.app.ProgressDialog;
import android.os.Build;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import com.kdanmobile.pdfviewer.R;
import com.kdanmobile.pdfviewer.base.ProApplication;
import com.kdanmobile.pdfviewer.utils.KeyboardUtils;

/**
 * @classname：DialogFragmentHelper
 * @author：xiaowumac
 * @date：2017/11/8 下午4:50
 * @description：https://github.com/developerHaoz/DialogFragmentDemos DialogFragment的帮助类
 */
public class DialogFragmentHelper {
    public static final int TYPE_EDIT_DIALOG_PASSWORD = 0X1001;
    public static final int TYPE_EDIT_DIALOG_TEXT = 0X1002;

    private static final String TAG_HEAD = DialogFragmentHelper.class.getSimpleName();
    private static final String DIALOG_POSITIVE = ProApplication.getContext().getString(R.string.ok);
    private static final String DIALOG_NEGATIVE = ProApplication.getContext().getString(R.string.cancel);

    /****** 加载中的弹出窗 ******/
    private static final int PROGRESS_THEME = R.style.progress_dialog_helper_style;
    private static final String PROGRESS_TAG = TAG_HEAD + ":progress";

    public static CommonDialogFragment showProgress(FragmentManager fragmentManager, String message) throws Exception {
        return showProgress(fragmentManager, message, true, null);
    }

    public static CommonDialogFragment showProgress(FragmentManager fragmentManager, String message, boolean cancelable) throws Exception {
        return showProgress(fragmentManager, message, cancelable, null);
    }

    public static CommonDialogFragment showProgress(FragmentManager fragmentManager, final String message, boolean cancelable, CommonDialogFragment.OnDialogCancelListener cancelListener) throws Exception {
        CommonDialogFragment dialogFragment = CommonDialogFragment.newInstance(context -> {
            ProgressDialog progressDialog = new ProgressDialog(context, PROGRESS_THEME);
            progressDialog.setMessage(message);
            return progressDialog;
        }, cancelable, cancelListener);
        dialogFragment.onShow(fragmentManager, PROGRESS_TAG);
        return dialogFragment;
    }


    /****** 简单提示弹出窗 ******/
    private static final int TIPS_THEME = R.style.common_dialog_helper_style;
    private static final String TIPS_TAG = TAG_HEAD + ":tips";

    public static void showTips(FragmentManager fragmentManager, String message) throws Exception {
        showTips(fragmentManager, message, true, null);
    }

    public static void showTips(FragmentManager fragmentManager, String message, boolean cancelable) throws Exception {
        showTips(fragmentManager, message, cancelable, null);
    }

    public static void showTips(FragmentManager fragmentManager, final String message, boolean cancelable, CommonDialogFragment.OnDialogCancelListener cancelListener) throws Exception {
        CommonDialogFragment dialogFragment = CommonDialogFragment.newInstance(context -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, TIPS_THEME);
            builder.setMessage(message);
            return builder.create();
        }, cancelable, cancelListener);
        dialogFragment.onShow(fragmentManager, TIPS_TAG);
    }


    /****** 确定取消框 ******/
    private static final int CONFIRM_THEME = R.style.common_dialog_helper_style;
    private static final String CONfIRM_TAG = TAG_HEAD + ":confirm";

    public static void showConfirmDialog(FragmentManager fragmentManager, final String message, final IDialogResultListener<Integer> listener, boolean cancelable, CommonDialogFragment.OnDialogCancelListener cancelListener) throws Exception {
        CommonDialogFragment dialogFragment = CommonDialogFragment.newInstance(context -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, CONFIRM_THEME);
            builder.setMessage(message);
            builder.setPositiveButton(DIALOG_POSITIVE, (dialog, which) -> {
                if (listener != null) {
                    listener.onDataResult(which);
                }
            });
            builder.setNegativeButton(DIALOG_NEGATIVE, (dialog, which) -> {
                if (cancelListener != null) {
                    cancelListener.onCancel();
                }
            });
            return builder.create();
        }, cancelable, cancelListener);
        dialogFragment.onShow(fragmentManager, CONfIRM_TAG);
    }

    public static void showConfirmDialog(FragmentManager fragmentManager, final String title, final String message, final IDialogResultListener<Integer> listener, boolean cancelable, CommonDialogFragment.OnDialogCancelListener cancelListener) throws Exception {
        CommonDialogFragment dialogFragment = CommonDialogFragment.newInstance(context -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, CONFIRM_THEME);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setPositiveButton(DIALOG_POSITIVE, (dialog, which) -> {
                if (listener != null) {
                    listener.onDataResult(which);
                }
            });
            builder.setNegativeButton(DIALOG_NEGATIVE, (dialog, which) -> {
                if (cancelListener != null) {
                    cancelListener.onCancel();
                }
            });
            return builder.create();
        }, cancelable, cancelListener);
        dialogFragment.onShow(fragmentManager, CONfIRM_TAG);
    }

    /****** 带列表的弹出窗 ******/
    private static final int LIST_THEME = R.style.common_dialog_helper_style;
    private static final String LIST_TAG = TAG_HEAD + ":list";

    public static DialogFragment showListDialog(FragmentManager fragmentManager, final String title, final String[] items, final IDialogResultListener<Integer> resultListener, boolean cancelable) throws Exception {
        CommonDialogFragment dialogFragment = CommonDialogFragment.newInstance(context -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, LIST_THEME);
            builder.setTitle(title);
            builder.setItems(items, (dialog, which) -> {
                if (resultListener != null) {
                    resultListener.onDataResult(which);
                }
            });
            return builder.create();
        }, cancelable, null);
        dialogFragment.onShow(fragmentManager, LIST_TAG);
        return null;
    }

    /****** 带输入框的弹出窗 ******/
    private static final int INSERT_THEME = R.style.common_dialog_helper_style;
    private static final String INSERT_TAG = TAG_HEAD + ":insert";

    public static void showInsertDialog(FragmentManager manager, final String title, final IDialogResultListener<String> resultListener, final boolean cancelable) throws Exception {
        CommonDialogFragment dialogFragment = CommonDialogFragment.newInstance(context -> {
            final AppCompatEditText editText = new AppCompatEditText(context);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                editText.setBackground(null);
            } else {
                editText.setBackgroundDrawable(null);
            }
            editText.setPadding(60, 40, 0, 0);
            AlertDialog.Builder builder = new AlertDialog.Builder(context, INSERT_THEME);
            builder.setTitle(title);
            builder.setView(editText);
            builder.setPositiveButton(DIALOG_POSITIVE, (dialog, which) -> {
                if (resultListener != null) {
                    resultListener.onDataResult(editText.getText().toString());
                }
            });
            builder.setNegativeButton(DIALOG_NEGATIVE, null);
            return builder.create();
        }, cancelable, null);
        dialogFragment.onShow(manager, INSERT_TAG);
    }

    /****** 带输入密码框的弹出窗 ******/
    private static final int PASSWORD_INSER_THEME = R.style.common_dialog_helper_style;
    private static final String PASSWORD_INSERT_TAG = TAG_HEAD + ":insert";

    public static void showPasswordInsertDialog(FragmentManager manager, final String title, final IDialogResultListener<String> resultListener, final boolean cancelable) throws Exception {
        CommonDialogFragment dialogFragment = CommonDialogFragment.newInstance(context -> {
            final EditText editText = new EditText(context);
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            editText.setEnabled(true);
            AlertDialog.Builder builder = new AlertDialog.Builder(context, PASSWORD_INSER_THEME);
            builder.setTitle(title);
            builder.setView(editText);
            builder.setPositiveButton(DIALOG_POSITIVE, (dialog, which) -> {
                if (resultListener != null) {
                    resultListener.onDataResult(editText.getText().toString());
                }
            });
            builder.setNegativeButton(DIALOG_NEGATIVE, null);
            return builder.create();
        }, cancelable, null);
        dialogFragment.onShow(manager, INSERT_TAG);
    }

    /**
     * @param ：[manager, title, resultListener, cancelable]
     * @return : void
     * @methodName ：showPasswordInsertDialog created by liujiyuan on 2018/8/28 上午11:03.
     * @description ：在activity中弹出输入文本对话框
     */
    private static final int DIALOG_INSERT_THEME = R.style.common_dialog_helper_style;
    private static final String DIALOG_INSERT_TAG = TAG_HEAD + ":insert";

    public static CommonDialogFragment showPasswordInsertDialog(FragmentManager manager, final int type, final String title, final IDialogResultListener<String> resultListener, final boolean cancelable) throws Exception {
        CommonDialogFragment dialogFragment = CommonDialogFragment.newInstance(context -> {
            View view = View.inflate(context, R.layout.layout_dialog_edittext, null);
            final EditText editText = view.findViewById(R.id.id_dialog_input_edt);
            if (type == TYPE_EDIT_DIALOG_PASSWORD) {
                editText.setHint(context.getString(R.string.dialog_input_password));
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            } else if (type == TYPE_EDIT_DIALOG_TEXT) {
                editText.setHint(context.getString(R.string.dialog_input_content));
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
            }
            editText.setEnabled(true);

            //弹出键盘
            KeyboardUtils.showKeyboard(editText);

            AlertDialog.Builder builder = new AlertDialog.Builder(context, DIALOG_INSERT_THEME);
            builder.setTitle(title);
            builder.setView(view);
            builder.setPositiveButton(DIALOG_POSITIVE, (dialog, which) -> {
                try {
                    if (resultListener != null) {
                        resultListener.onDataResult(editText.getText().toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    //隐藏键盘
                    KeyboardUtils.hideKeyboard(editText);
                }
            });
            builder.setNegativeButton(DIALOG_NEGATIVE, (dialog, which) -> {
                try {
                    if (resultListener != null) {
                        resultListener.onDataResult(null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    //隐藏键盘
                    KeyboardUtils.hideKeyboard(editText);
                }
            });
            return builder.create();
        }, cancelable, null);
        dialogFragment.onShow(manager, DIALOG_INSERT_TAG);
        return dialogFragment;
    }
}
