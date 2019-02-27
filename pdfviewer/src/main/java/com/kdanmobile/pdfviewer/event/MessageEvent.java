package com.kdanmobile.pdfviewer.event;

/**
 * @classname：BaseMessageEvent
 * @author：luozhipeng
 * @date：31/7/18 14:27
 * @description：
 */
public class MessageEvent<T> {
    private final String tag;
    private final T event;

    public MessageEvent(String tag, T event) {
        this.tag = tag;
        this.event = event;
    }

    public String getTag() {
        return tag;
    }

    public T getEvent() {
        return event;
    }
}
