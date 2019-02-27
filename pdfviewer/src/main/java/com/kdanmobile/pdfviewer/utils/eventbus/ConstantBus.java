package com.kdanmobile.pdfviewer.utils.eventbus;

/**
 * @classname：ConstantBus
 * @author：luozhipeng
 * @date：14/8/18 15:18
 * @description： EventBus 消息发送常量tag
 */
public class ConstantBus {

    /****** main 侧边栏 Navigation drawer ******/
    public final static String NAVIGATION_DRAWER_DOC = "navigation_drawer_doc";
    public final static String NAVIGATION_DRAWER_SCAN = "navigation_drawer_scan";
    public final static String NAVIGATION_DRAWER_SETTING = "navigation_drawer_setting";

    /****** more setting的通信消息 ******/
    public final static String MORE_SETTING_BRIGHTNESS = "more_setting_brightness";
    public final static int MORE_SETTING_PAGETURN_VC = 0X1001;
    public final static int MORE_SETTING_PAGETURN_VS = 0X1002;
    public final static int MORE_SETTING_PAGETURN_HS = 0X1003;

    /****** 本地文档的操作 ******/
    public final static String MEDIA_STORE_REFRESH = "MediaStore_Refresh";
    public final static String MEDIA_STORE_REFRESH_LOCAL_DATA = "local_data";
    public final static String MEDIA_STORE_REFRESH_MEDIA_DATA = "media_data";

    /****** 已读和收藏文档的操作 ******/
    public final static String MEDIA_STORE_UPDATE_RECENT_FILE = "MediaStore_Update_recent_data";
    public final static String MEDIA_STORE_UPDATE_COLLECTION_FILE = "MediaStore_Update_collection_data";

    public final static String DOC_FILE_ACTION = "DocFileAction";
    public final static String DOC_FILE_ACTION_MOVE = "DocFileAction_Move";
    public final static String DOC_FILE_ACTION_RENAME = "DocFileAction_Rename";
    public final static String DOC_FILE_ACTION_COLLECTION = "DocFileAction_Collection";
    public final static String DOC_FILE_ACTION_SHARE = "DocFileAction_Share";
    public final static String DOC_FILE_ACTION_COPY = "DocFileAction_Copy";
    public final static String DOC_FILE_ACTION_INFO = "DocFileAction_Info";
    public final static String DOC_FILE_ACTION_DELETE = "DocFileAction_Delete";

    public final static String DOC_FILE_ACTION_SHOW = "DocFileActionShow";
    public final static String DOC_FILE_ACTION_DISABLE = "DocFileActionDisable";

    /****** 阅读文本的的操作 ******/
    public final static String READER_SEARCH_TEXT = "Reader_SearchText";
    public final static String REFRESH_DOCUMENT = "Refresh_Document";
    public final static String REFRESH_BRIGHTNESS = "Refresh_Brightness";
    public final static String SET_ANNOTATION_ATTR = "Set_Annotation_Attr";
    public final static String SET_MARKUP_ANNOTATION_ATTR = "Set_Markup_Annotation_Attr";
    public final static String SET_STAMP_ANNOTATION_ATTR = "Set_Stamp_Annotation_Attr";
    public final static String SET_SIGN_ANNOTATION_ATTR = "Set_Sign_Annotation_Attr";

    /****** bookmark的操作 ******/
    public final static String BOOKMARK_RENAME = "BookMark_rename";
    public final static String BOOKMARK_GOTOPAGE = "BookMark_goto_page";
    public final static String BOOKMARK_DELETE = "BookMark_delete";

    /****** pageEdit的操作 ******/
    public final static String PAGEEDIT_GOTOPAGE = "PageEdit_goto_page";

    /****** stamp的操作 ******/
    public final static String CREATE_TEXT_STAMP = "Create_text_stamp";
    public final static String BACK_STAMP_ACTIVITY = "Back_stamp_activity";

    public final static String DOCUMENT_OPEN_SEARCH = "document_open_search";
    public final static String DOCUMENT_OPEN_SEARCH_DOCUMENT = "document_open_search_document";
    public final static String DOCUMENT_OPEN_SEARCH_SCAN = "document_open_search_scan";

    /****** 选择目录的操作 ******/
    public final static String CHOOSE_FOLDER_COPY = "choose_folder_copy";
    public final static String CHOOSE_FOLDER_MOVE = "choose_folder_move";
    public final static String CHOOSE_FOLDER_CANCEL = "choose_folder_cancel";
}
