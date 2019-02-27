package com.kdanmobile.pdfviewer.screenui.widget.multiaction;

import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.kdanmobile.pdfviewer.R;
import com.kdanmobile.pdfviewer.event.MessageEvent;
import com.kdanmobile.pdfviewer.utils.ScreenUtil;
import com.kdanmobile.pdfviewer.utils.eventbus.ConstantBus;
import com.kdanmobile.pdfviewer.utils.eventbus.EventBusUtils;

/**
 * @classname：DocumentMultiActionToolbar
 * @author：gongzuo
 * @date：2018/9/5 上午10:54
 * @description：
 */
public class DocumentMultiActionToolbar extends LinearLayout implements View.OnClickListener {
    private String TAG = "DocumentMultiActionToolbar";

    boolean isInflate = false;

    private int[][] menu = new int[][]{
            {R.string.local_doc_more_menu_move, R.drawable.ic_move},
            {R.string.local_doc_more_menu_rename, R.drawable.ic_rename},
            {R.string.local_doc_more_menu_collection, R.drawable.ic_collect_in},
            {R.string.local_doc_more_menu_share, R.drawable.ic_share},
            {R.string.local_doc_more_menu_copy, R.drawable.ic_copy},
            {R.string.local_doc_more_menu_info, R.drawable.ic_info},
            {R.string.local_doc_more_menu_delete, R.drawable.ic_delete}
    };

    private final float alphaDisabled = 0.5f;
    private final float alphaNormal = 1.0f;

    public DocumentMultiActionToolbar(Context context) {
        this(context, null);
    }

    public DocumentMultiActionToolbar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DocumentMultiActionToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        /****** 显示的时候如果没有初始化，先初始化 ******/
        if (View.VISIBLE == visibility && !isInflate) {
            initMenuItemView();
        }
        /****** 初始化之后，每次显示的时候先将所有菜单置为不可用 ******/
        if (View.VISIBLE == visibility && isInflate) {
            disableAllAction();
            fixToolbarWidth();
        }
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "orientation = " + newConfig.orientation);
        if (((HorizontalScrollView) this.getParent()).getVisibility() == View.VISIBLE) {
            fixToolbarWidth();
        }
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag != null && tag instanceof Integer) {
            switch ((int) tag) {
                case R.string.local_doc_more_menu_move:
                    sendActionMessage(ConstantBus.DOC_FILE_ACTION_MOVE);
                    break;
                case R.string.local_doc_more_menu_rename:
                    sendActionMessage(ConstantBus.DOC_FILE_ACTION_RENAME);
                    break;
                case R.string.local_doc_more_menu_collection:
                    sendActionMessage(ConstantBus.DOC_FILE_ACTION_COLLECTION);
                    break;
                case R.string.local_doc_more_menu_share:
                    sendActionMessage(ConstantBus.DOC_FILE_ACTION_SHARE);
                    break;
                case R.string.local_doc_more_menu_copy:
                    sendActionMessage(ConstantBus.DOC_FILE_ACTION_COPY);
                    break;
                case R.string.local_doc_more_menu_info:
                    sendActionMessage(ConstantBus.DOC_FILE_ACTION_INFO);
                    break;
                case R.string.local_doc_more_menu_delete:
                    sendActionMessage(ConstantBus.DOC_FILE_ACTION_DELETE);
                    break;
                default:
            }
        }
    }

    /**
     * @methodName：disableActionWithCount created by wangzhe on 2018/9/5 下午4:39.
     * @description：根据选择的文件数量修改菜单状态
     */
    public void disableActionWithCount(int count) {
        if (count <= 0) {
            disableAllAction();
        } else if (count == 1) {
            enableAllAction();
        } else {
            disableSomeAction();
        }
    }

    /**
     * @methodName：disableAllAction created by wangzhe on 2018/9/5 下午4:35.
     * @description：让所有的菜单都点击不了
     */
    private void disableAllAction() {
        for (int i = 0, size = this.getChildCount(); i < size; i++) {
            this.getChildAt(i).setAlpha(alphaDisabled);
            this.getChildAt(i).setEnabled(false);
        }
    }

    /**
     * @methodName：enableAllAction created by wangzhe on 2018/9/5 下午4:36.
     * @description：让所有的菜单都可以点击
     */
    private void enableAllAction() {
        for (int i = 0, size = this.getChildCount(); i < size; i++) {
            this.getChildAt(i).setAlpha(alphaNormal);
            this.getChildAt(i).setEnabled(true);
        }
    }

    /**
     * @methodName：disableSomeAction created by wangzhe on 2018/9/5 下午4:37.
     * @description：让某些菜单无法点击
     */
    private void disableSomeAction() {
        for (int i = 0, size = this.getChildCount(); i < size; i++) {
            Object tag = this.getChildAt(i).getTag();
            // 选中了几个的时候，屏蔽掉重命名和查看文件详情
            if (tag != null && tag instanceof Integer) {
                if (R.string.local_doc_more_menu_rename == (int) tag || R.string.local_doc_more_menu_info == (int) tag) {
                    this.getChildAt(i).setAlpha(alphaDisabled);
                    this.getChildAt(i).setEnabled(false);
                } else {
                    this.getChildAt(i).setAlpha(alphaNormal);
                    this.getChildAt(i).setEnabled(true);
                }
            } else {
                this.getChildAt(i).setAlpha(alphaDisabled);
                this.getChildAt(i).setEnabled(false);
            }
        }
    }

    /**
     * @methodName：initMenuItemView created by wangzhe on 2018/9/5 下午4:42.
     * @description：生成菜单
     */
    private void initMenuItemView() {
        isInflate = true;
        for (int i = 0, size = menu.length; i < size; i++) {
            View itemDocumentAction = LayoutInflater.from(getContext()).inflate(R.layout.item_document_action, this, false);
            int[] menuItem = menu[i];
            AppCompatTextView idDocumentActionName = itemDocumentAction.findViewById(R.id.id_document_action_name);
            AppCompatImageView idDocumentActionIcon = itemDocumentAction.findViewById(R.id.id_document_action_icon);
            idDocumentActionName.setText(menuItem[0]);
            idDocumentActionIcon.setImageResource(menuItem[1]);
            this.addView(itemDocumentAction);
            itemDocumentAction.setTag(menuItem[0]);
            itemDocumentAction.setOnClickListener(this);
        }
    }

    private void fixToolbarWidth() {
        this.post(() -> {
            int size = DocumentMultiActionToolbar.this.getChildCount();
            if (DocumentMultiActionToolbar.this.getWidth() < ScreenUtil.getScreenWidth(getContext())) {
                int itemWidth = ((HorizontalScrollView) DocumentMultiActionToolbar.this.getParent()).getWidth() / size;
                for (int i = 0; i < size; i++) {
                    DocumentMultiActionToolbar.this.getChildAt(i).setLayoutParams(new LayoutParams(itemWidth, LayoutParams.MATCH_PARENT));
                }
            } else {
                for (int i = 0; i < size; i++) {
                    DocumentMultiActionToolbar.this.getChildAt(i).setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
                }
            }
        });
    }

    /**
     * @methodName：sendActionMessage created by wangzhe on 2018/9/5 下午4:40.
     * @description：用eventbus发送菜单的点击事件
     */
    private void sendActionMessage(String docFileActionMove) {
        EventBusUtils.getInstance().post(new MessageEvent<>(ConstantBus.DOC_FILE_ACTION, docFileActionMove));
    }
}
