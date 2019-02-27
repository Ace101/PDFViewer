package com.kdanmobile.pdfviewer.screenui.reader.view.been;

/**
 * @classname：ProOutLineItem
 * @author：liujiyuan
 * @date：2018/8/22 下午2:26
 * @description：
 */
public class ProOutLineItem {
    public final int level;
    public final String title;
    public final int page;
    public final int parent_pos;
    public final String parent_title;
    public boolean isExpose = false;
    public boolean isHasChildItems = false;

    public ProOutLineItem(int _level, String _title, int _page, int _parent_pos, String _parent_title) {
        level = _level;
        title = _title;
        page = _page;
        parent_pos = _parent_pos;
        parent_title = _parent_title;
    }
}
