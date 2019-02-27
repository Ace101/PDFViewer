package com.kdanmobile.pdfviewer.utils;

/**
 * @classname：Constants
 * @author：luozhipeng
 * @date：23/7/18 11:11
 * @description：
 */
public class Constants {
    /**
     * 主界面中Documents中recent与Documents两个选项，采用十六进制常量
     */
    public static int DOCUMENT_INDEX = 0x10000021;//默认是RECENT
    public static final int DOCUMENT_RECENT = 0x10000021;//RECENT
    public static final int DOCUMENT_LOCAL = 0x10000022;//LOCAL
    public static final int DOCUMENT_COLLECTION = 0x10000023;//COLLECTION

    /**
     * fileObserver是否可用，true是不可用，false是可用
     */
    public static final String SP_VALUE_BROKEN_OBSERVER = "file_observer_is_broken";

    public static final String SP_VALUE_LOCAL_FILE_LIST_IS_LIST_MODE = "file_list_is_list_mode";

    /****** 记录扫描预览的状态 ******/
    public static final String SP_VALUE_SCAN_REVIEW_PROMPTED = "scan_review_prompted";

    public static final String DOCUMENT_CHOOSE_FOLDER_TYPE = "folder_choose_type";
    public static final String DOCUMENT_CHOOSE_FOLDER_TYPE_MOVE = "folder_choose_type_move";
    public static final String DOCUMENT_CHOOSE_FOLDER_TYPE_COPY = "folder_choose_type_copy";

    public static final String SCAN_ITEM_DATA_LIST = "scan_selected_images";
    public static final String SCAN_CROP_EDIT = "scan_crop_edit";
    public static final String SCAN_CROP_PROJECT_ID = "scan_crop_project_id";
    public static final String SCAN_CROP_EDIT_POSITION = "scan_crop_edit_position";
}
