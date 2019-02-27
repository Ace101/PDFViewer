package com.kdanmobile.pdfviewer.utils.eventbus;

import org.greenrobot.eventbus.EventBus;

/**
 * @classname：EventBusUtils
 * @author：luozhipeng
 * @date：14/8/18 15:01
 * @description： eventbus的管理类，register 与 unRegister 需要在生命周期中成对出现；如：
 * oncreate、onDestory
 * onstart、onStop
 * 按需使用。与@Subscribe 成对出现
 */
public class EventBusUtils {

    private final static class SingleTon {
        private final static EventBusUtils instance = new EventBusUtils();
    }

    public static EventBusUtils getInstance() {
        return SingleTon.instance;
    }

    public void register(Object object) {
        if (!EventBus.getDefault().isRegistered(object)) {
            EventBus.getDefault().register(object);
        }
    }

    public void unRegister(Object object) {
        if (EventBus.getDefault().isRegistered(object)) {
            EventBus.getDefault().unregister(object);
        }
    }

    public void post(Object Publisher) {
        EventBus.getDefault().post(Publisher);
    }

    public void postSticky(Object Publisher) {
        EventBus.getDefault().postSticky(Publisher);
    }

    /**
     * @methodName：cancelEventDelivery created by luozhipeng on 14/8/18 15:06.
     * @description： 取消事件传送 事件取消仅限于ThreadMode.PostThread下才可以使用,不取消事件就会一直存在
     */
    public static void cancelEventDelivery(Object event) {
        EventBus.getDefault().cancelEventDelivery(event);
    }

    /**
     * @param ：[eventType]
     * @return : void
     * @methodName ：removeStickyEvent created by luozhipeng on 14/8/18 15:08.
     * @description ：移除指定的粘性订阅事件
     */
    public static <T> void removeStickyEvent(Class<T> eventType) {
        T stickyEvent = EventBus.getDefault().getStickyEvent(eventType);
        if (stickyEvent != null) {
            EventBus.getDefault().removeStickyEvent(stickyEvent);
        }
    }

    /**
     * @methodName：removeAllStickyEvents created by luozhipeng on 14/8/18 15:06.
     * @description： 移除所有的粘性订阅事件
     */
    public static void removeAllStickyEvents() {
        EventBus.getDefault().removeAllStickyEvents();
    }
}
