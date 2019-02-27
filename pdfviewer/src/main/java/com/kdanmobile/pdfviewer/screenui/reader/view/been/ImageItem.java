package com.kdanmobile.pdfviewer.screenui.reader.view.been;

/**
 * @classname：ImageStampItem
 * @author：liujiyuan
 * @date：2018/9/5 下午5:42
 * @description：
 */
public class ImageItem {
    public String filePath;
    public boolean isChecked;
    public int width,height;

    public ImageItem(String filePath, boolean isChecked, int width, int height) {
        this.filePath = filePath;
        this.isChecked = isChecked;
        this.width = width;
        this.height = height;
    }
}
