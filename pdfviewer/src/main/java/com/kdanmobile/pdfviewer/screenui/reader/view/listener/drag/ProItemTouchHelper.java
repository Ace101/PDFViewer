package com.kdanmobile.pdfviewer.screenui.reader.view.listener.drag;

/**
 * @classname：ItemTouchHelper
 * @author：liujiyuan
 * @date：2018/8/30 下午4:19
 * @description：自定义的 RecyclerView item的拖拽接口
 */
public class ProItemTouchHelper extends DefaultItemTouchHelper {
    private DefaultItemTouchHelpCallback itemTouchHelpCallback;

    /**
     * Creates an ProItemTouchHelper that will work with the given Callback.
     * <p>
     * You can attach ProItemTouchHelper to a RecyclerView via
     * . Upon attaching, it will add an item decoration,
     * an onItemTouchListener and a Child attach / detach listener to the RecyclerView.
     */
    public ProItemTouchHelper(DefaultItemTouchHelpCallback.OnItemTouchCallbackListener onItemTouchCallbackListener) {
        super(new DefaultItemTouchHelpCallback(onItemTouchCallbackListener));
        itemTouchHelpCallback = (DefaultItemTouchHelpCallback) getCallback();
    }

    /**
     * @param ：[canDrag]
     * @return : void
     * @methodName ：setDragEnable created by luozhipeng on 1/11/17 17:46.
     * @description ：设置是否可以被拖拽
     */
    public void setDragEnable(boolean canDrag) {
        itemTouchHelpCallback.setDragEnable(canDrag);
    }

    /**
     * @param ：[canSwap]
     * @return : void
     * @methodName ：setSwapEnable created by luozhipeng on 2/11/17 10:57.
     * @description ：设置是否可以被位置交换
     */
    public void setSwapEnable(boolean canSwap){
        itemTouchHelpCallback.setSwapEnable(canSwap);
    }

    /**
     * @param ：[canSwipe]
     * @return : void
     * @methodName ：setSwipeEnable created by luozhipeng on 1/11/17 17:46.
     * @description ：设置是否可以被滑动
     */
    public void setSwipeEnable(boolean canSwipe) {
        itemTouchHelpCallback.setSwipeEnable(canSwipe);
    }
}
