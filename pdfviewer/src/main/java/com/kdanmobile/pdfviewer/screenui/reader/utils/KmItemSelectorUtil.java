package com.kdanmobile.pdfviewer.screenui.reader.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @classname：KmItemSelectorUtil
 * @author：liujiyuan
 * @date：2018/9/5 下午2:41
 * @description： 适配器选择操作处理
 */
public class KmItemSelectorUtil<T> {
    /***** 复选框已选项缓存集合、页码 —— isSelected *****/
    private final Map<String, T> mapSelect = new HashMap<>();

    public KmItemSelectorUtil() {
    }

    public void setAllUnClick() {
        mapSelect.clear();
    }

    public boolean isContain(final String key) {
        return mapSelect.containsKey(key);
    }

    public Map<String, T> getMapSelect() {
        return mapSelect;
    }
}
