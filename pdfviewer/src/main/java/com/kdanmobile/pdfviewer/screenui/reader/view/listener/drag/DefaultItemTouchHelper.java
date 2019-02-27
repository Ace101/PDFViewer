package com.kdanmobile.pdfviewer.screenui.reader.view.listener.drag;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * @classname：DefaultItemTouchHelper
 * @author：liujiyuan
 * @date：2018/8/30 下午4:20
 * @description：RecyclerView item的拖拽接口
 */
public class DefaultItemTouchHelper extends ItemTouchHelper {

    private ItemTouchHelper.Callback callBack;

    /**
     * Creates an ProItemTouchHelper that will work with the given Callback.
     * <p>
     * You can attach ProItemTouchHelper to a RecyclerView via
     * {@link #attachToRecyclerView(RecyclerView)}. Upon attaching, it will add an item decoration,
     * an onItemTouchListener and a Child attach / detach listener to the RecyclerView.
     * @param callback The Callback which controls the behavior of this touch helper.
     */
    public DefaultItemTouchHelper(Callback callback) {
        super(callback);
        this.callBack = callback;
    }

    public Callback getCallback() {
        return callBack;
    }
}
