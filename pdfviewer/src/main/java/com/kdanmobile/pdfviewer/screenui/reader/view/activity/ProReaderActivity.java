package com.kdanmobile.pdfviewer.screenui.reader.view.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kdanmobile.kmpdfkit.manager.KMPDFFactory;
import com.kdanmobile.kmpdfkit.manager.controller.KMPDFDocumentController;
import com.kdanmobile.kmpdfkit.manager.controller.KMPDFInkController;
import com.kdanmobile.kmpdfkit.manager.listener.KMPDFDocumentMessageCallback;
import com.kdanmobile.kmpdfkit.manager.listener.KMPDFErrorMessageCallback;
import com.kdanmobile.kmpdfkit.pdfcommon.FilePicker;
import com.kdanmobile.kmpdfkit.pdfcommon.KMPDFReaderView;
import com.kdanmobile.pdfviewer.R;
import com.kdanmobile.pdfviewer.base.ProApplication;
import com.kdanmobile.pdfviewer.base.mvp.factory.CreatePresenter;
import com.kdanmobile.pdfviewer.base.mvp.view.AbstractMvpAppCompatActivity;
import com.kdanmobile.pdfviewer.event.MessageEvent;
import com.kdanmobile.pdfviewer.screenui.reader.configs.AnnotDefaultConfig;
import com.kdanmobile.pdfviewer.screenui.reader.configs.AnnotMenuStatus;
import com.kdanmobile.pdfviewer.screenui.reader.configs.ApplicationConfig;
import com.kdanmobile.pdfviewer.screenui.reader.configs.KMReaderConfigs;
import com.kdanmobile.pdfviewer.screenui.reader.constract.ProReaderConstract;
import com.kdanmobile.pdfviewer.screenui.reader.presenter.ProReaderPresenter;
import com.kdanmobile.pdfviewer.screenui.reader.utils.BrightnessUtil;
import com.kdanmobile.pdfviewer.screenui.reader.utils.InitKMPDFControllerUtil;
import com.kdanmobile.pdfviewer.screenui.reader.utils.KMReaderSpUtils;
import com.kdanmobile.pdfviewer.screenui.reader.utils.PopupWindowUtil;
import com.kdanmobile.pdfviewer.screenui.reader.view.listener.OnMarkerPenAttrChangeListener;
import com.kdanmobile.pdfviewer.screenui.widget.SuperButton;
import com.kdanmobile.pdfviewer.screenui.widget.commondialog.DialogFragmentHelper;
import com.kdanmobile.pdfviewer.screenui.widget.commondialog.IDialogResultListener;
import com.kdanmobile.pdfviewer.utils.AnimationUtil;
import com.kdanmobile.pdfviewer.utils.GlobalConfigs;
import com.kdanmobile.pdfviewer.utils.ToastUtil;
import com.kdanmobile.pdfviewer.utils.UriToPathUtil;
import com.kdanmobile.pdfviewer.utils.eventbus.ConstantBus;
import com.kdanmobile.pdfviewer.utils.eventbus.EventBusUtils;
import com.kdanmobile.pdfviewer.utils.threadpools.SimpleBackgroundTask;

import org.greenrobot.eventbus.Subscribe;

/**
 * @classname：ProReaderActivity
 * @author：liujiyuan
 * @date：2018/8/14 下午3:24
 * @description：文本阅读界面
 */
@CreatePresenter(ProReaderPresenter.class)
public class ProReaderActivity extends AbstractMvpAppCompatActivity<ProReaderActivity, ProReaderPresenter>
        implements ProReaderConstract.IView, KMPDFDocumentMessageCallback, KMPDFErrorMessageCallback,
        View.OnClickListener, View.OnLongClickListener, FilePicker.FilePickerSupport, OnMarkerPenAttrChangeListener, IDialogResultListener {

    private final int OPEN_SEARCH_DOC = 0X1101;
    private final int OPEN_BO_DOC = 0X1102;
    private final int OPEN_MORE_DOC = 0X1103;
    private final int OPEN_PAGE_EDIT = 0X1104;
    public final int OPEN_STAMP_ANNOT = 0X1105;
    public final int OPEN_SIGN_ANNOT = 0X1106;

    /**
     * 验证license的错误码
     * 错误码  1001：签名为空  1002：签名信息错误  1003：权限信息错误  1004：权限信息格式错误  1005：签名过期  1006：签名与包名不匹配  1007：权限等级不匹配
     **/
    private static final int EMPTY_LICENSE = 1001;
    private static final int ERROR_LICENSE = 1002;
    private static final int ERROR_RSAMESSAGE = 1003;
    private static final int RSAMESSAGE_ERROR_TYPE = 1004;
    private static final int RSAMESSAGE_EXPIRED = 1005;
    private static final int RSAMESSAGE_ERROR_APPLICATIONID = 1006;
    private static final int RSAMESSAGE_ERROR_MODULE = 1007;
    private static final int VERIFY_SUCCESS = 0;

    public KMPDFFactory kmpdfFactory;
    private KMPDFInkController kmpdfInkController;
    private KMPDFDocumentController kmpdfDocumentController;

    private FrameLayout idReaderDocument;
    private KMPDFReaderView mDocView;

    private RelativeLayout idReaderToolPopBg;
    private TextView idReaderToolHighlightPop;
    private TextView idReaderToolUnderlinePop;
    private TextView idReaderToolStrickPop;
    private TextView idReaderToolShapePop;
    private AppBarLayout idAppbarLayout;
    private HorizontalScrollView idReaderToolers;
    private RelativeLayout idReaderToolHighlight;
    private View idReaderToolHighlightBg;
    private RelativeLayout idReaderToolUnderline;
    private View idReaderToolUnderlineBg;
    private RelativeLayout idReaderToolStrick;
    private View idReaderToolStrickBg;
    private RelativeLayout idReaderToolPageEdit;
    private RelativeLayout idReaderToolInk;
    private ImageView idReaderToolInkIv;
    private RelativeLayout idReaderToolShape;
    private RelativeLayout idReaderToolFreeText;
    private RelativeLayout idReaderToolSign;
    private RelativeLayout idReaderToolStamp;
    private RelativeLayout idReaderToolLink;

    private LinearLayout idInkOperate;
    private ImageButton idInkOperateCancel;
    private ImageButton idInkOperateRecover;
    private ImageButton idInkOperateClean;
    private ImageButton idInkOperateComplete;
    private TextView idReaderPageIndicator;
    private ImageView idReaderFloatingBackPage;

    private int toolsButtonNormalColor;
    private int toolsButtonClickedColor;

    private String file_absolutepath;
    private boolean isEnterFullScreen = false;
    private int lastPageIndex = -1;
    private boolean isClickedBackPage = false;
    private boolean isFirstHighLightOpen = true;
    private boolean isFirstUnderLineOpen = true;
    private boolean isFirstStrickOpen = true;
    private boolean isFirstShapeOpen = true;

    public static void onOpenPDFReader(Context context, String absolutePath) {
        try {
            Intent intent = new Intent(context, ProReaderActivity.class);
            intent.putExtra("file_absolutepath", absolutePath);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onLayoutId() {
        return R.layout.activity_reader;
    }

    @Override
    public boolean onInitView() {
        EventBusUtils.getInstance().register(this);
        Intent intent = getIntent();
        if (null == intent) {
            finish();
            return true;
        }

        /****** 根据调用方式得到文件路径和uri ******/
        Uri uri;
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            uri = intent.getData();
            file_absolutepath = UriToPathUtil.getInstance().getPath(getApplicationContext(), uri);
        } else {
            file_absolutepath = intent.getStringExtra("file_absolutepath");
            uri = Uri.parse(file_absolutepath);
        }
        GlobalConfigs.FILE_ABSOLUTE_PATH = file_absolutepath;

        /****** 验证签名 ******/
        int errorId = KMPDFFactory.verifyLicense(this, ApplicationConfig.READER_LICENSE, ApplicationConfig.RSA_MESSAGE);
        if (errorId != VERIFY_SUCCESS) {
            showVerifyLicenseError(errorId);
            finish();
        }
        /****** 创建文本管理器 KMPDFManager******/
        kmpdfFactory = KMPDFFactory.open(this, uri, intent.getType());
        if (kmpdfFactory != null) {
            kmpdfFactory.onCreated(savedInstanceState);
        }
        /****** 查询是否需要密码 ******/
        if (kmpdfFactory == null) {
            finish();
        } else if (kmpdfFactory.needPassWord()) {
            confirmPasswordDialog();
            return false;
        } else {
            if (kmpdfFactory.init()) {
                doAfterConfirm();
            } else {
                finish();
            }
        }
        return true;
    }

    /**
     * @param ：[]
     * @return : void
     * @methodName ：doAfterConfirmPass created by liujiyuan on 2018/8/27 下午4:33.
     * @description ：确认密码之后的调用
     */
    private void doAfterConfirm() {
        idReaderDocument = findViewById(R.id.id_reader_document);
        /****** 创建文本阅读view ******/
        mDocView = new KMPDFReaderView(this) {
            @Override
            protected void onTapMainDocArea() {
                super.onTapMainDocArea();
                isEnterFullScreen = !isEnterFullScreen;
                enterFullScreenMode();
                /****** 隐藏提示语 ******/
                idReaderToolPopBg.setVisibility(View.GONE);
            }

            @Override
            public void endScroll() {
                handler.postDelayed(() -> idReaderPageIndicator.setVisibility(View.GONE), 1000);
                super.endScroll();
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                updateReaderPageIndicator();
                return super.onScroll(motionEvent, motionEvent1, v, v1);
            }

            @Override
            public void recordLastJumpPageNum(int i) {
                super.recordLastJumpPageNum(i);
                updateReaderPageIndicator();
                lastPageIndex = i;
                if (!isClickedBackPage) {
                    idReaderFloatingBackPage.setVisibility(View.VISIBLE);
                }
                isClickedBackPage = false;
                if (getMvpPresenter() != null) {
                    getMvpPresenter().resetAnnotationState();
                }
            }
        };
        idReaderDocument.addView(mDocView);
        /****** 给文本管理器设置 ReaderView******/
        kmpdfFactory.setReaderView(mDocView);
        /****** 初始化全局的KMPDF services ******/
        InitKMPDFControllerUtil.getInstance().initControllers(kmpdfFactory);
        kmpdfInkController = InitKMPDFControllerUtil.getInstance().getKmpdfInkController();
        kmpdfDocumentController = InitKMPDFControllerUtil.getInstance().getKmpdfDocumentController();

        findByID();
        setListener();
        if (!kmpdfFactory.isReadByStream()) {
            onUpdateRecent();
        }
        /****** 初始化 PopupWindow ******/
        initPopupWindow();
        /****** 设置标题栏名称 ******/
        GlobalConfigs.CURRENT_DOCUMENT_NAME = kmpdfFactory.getFileName();
        getSupportActionBar().setTitle(GlobalConfigs.CURRENT_DOCUMENT_NAME);
        enterFullScreenMode();
        /****** 工具栏移动到指定位置 ******/
        int scrollX = (int) (getResources().getDimension(R.dimen.qb_px_60) / 2);
        handler.postDelayed(() -> idReaderToolers.scrollTo(scrollX, 0), 300);
        /****** 判断是否是第一次打开工具栏 ******/
        onSetFirstOpenToolBar();
        /****** 跳转到上一次浏览页 ******/
        kmpdfDocumentController.gotoPage(KMReaderSpUtils.getInstance().getDocumentPage(GlobalConfigs.FILE_ABSOLUTE_PATH));
        initUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (kmpdfFactory != null) {
            kmpdfFactory.onStart();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (kmpdfFactory != null) {
            kmpdfFactory.onSaveInstanceState(outState);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (kmpdfFactory != null) {
            kmpdfFactory.onPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (kmpdfFactory != null) {
            kmpdfFactory.onStop();
        }
    }

    /**
     * @methodName：onSetFirstOpenToolBar created by liujiyuan on 2018/9/27 下午2:02.
     * @description：判断是否是第一次打开 HighLight, UnderLine, Strick, Shape 工具栏
     */
    private void onSetFirstOpenToolBar() {
        isFirstHighLightOpen = KMReaderSpUtils.getInstance().getFirstHighLightUsed();
        isFirstUnderLineOpen = KMReaderSpUtils.getInstance().getFirstUnderLineUsed();
        isFirstStrickOpen = KMReaderSpUtils.getInstance().getFirstStrickUsed();
        isFirstShapeOpen = KMReaderSpUtils.getInstance().getFirstShapeUsed();
    }

    /**
     * @methodName：onUpdateRecent created by liujiyuan on 2018/8/24 下午2:48.
     * @description： 更新数据库表记录，最近打开文档
     */
    private void onUpdateRecent() {
        EventBusUtils.getInstance().post(new MessageEvent<>(ConstantBus.MEDIA_STORE_UPDATE_RECENT_FILE, file_absolutepath));
    }

    /**
     * @param ：[]
     * @return : void
     * @methodName ：findByID created by liujiyuan on 2018/8/21 下午6:16.
     * @description ：初始化各个控件
     */
    private void findByID() {
        idReaderToolPopBg = findViewById(R.id.id_reader_tool_pop_bg);
        idReaderToolHighlightPop = findViewById(R.id.id_reader_tool_highlight_pop);
        idReaderToolUnderlinePop = findViewById(R.id.id_reader_tool_underline_pop);
        idReaderToolStrickPop = findViewById(R.id.id_reader_tool_strick_pop);
        idReaderToolShapePop = findViewById(R.id.id_reader_tool_shape_pop);


        idAppbarLayout = findViewById(R.id.id_reader_about_us_toolbar);
        idReaderToolers = findViewById(R.id.id_reader_toolers);
        idReaderToolHighlight = findViewById(R.id.id_reader_tool_highlight);
        idReaderToolHighlightBg = findViewById(R.id.id_reader_tool_highlight_bg);
        idReaderToolUnderline = findViewById(R.id.id_reader_tool_underline);
        idReaderToolUnderlineBg = findViewById(R.id.id_reader_tool_underline_bg);
        idReaderToolStrick = findViewById(R.id.id_reader_tool_strick);
        idReaderToolStrickBg = findViewById(R.id.id_reader_tool_strick_bg);

        idReaderToolPageEdit = findViewById(R.id.id_reader_tool_pageEdit);
        idReaderToolInk = findViewById(R.id.id_reader_tool_ink);
        idReaderToolInkIv = findViewById(R.id.id_reader_tool_ink_iv);
        idReaderToolShape = findViewById(R.id.id_reader_tool_shape);
        idReaderToolFreeText = findViewById(R.id.id_reader_tool_freeText);
        idReaderToolSign = findViewById(R.id.id_reader_tool_sign);
        idReaderToolStamp = findViewById(R.id.id_reader_tool_stamp);
        idReaderToolLink = findViewById(R.id.id_reader_tool_link);

        idInkOperate = findViewById(R.id.id_reader_ink_operate);
        idInkOperateCancel = findViewById(R.id.id_ink_operate_cancel);
        idInkOperateRecover = findViewById(R.id.id_ink_operate_recover);
        idInkOperateClean = findViewById(R.id.id_ink_operate_clean);
        idInkOperateComplete = findViewById(R.id.id_ink_operate_complete);

        idReaderPageIndicator = findViewById(R.id.id_reader_page_indicator);
        idReaderFloatingBackPage = findViewById(R.id.id_reader_floating_backPage);

    }


    /**
     * @param ：[]
     * @return : void
     * @methodName ：setListener created by liujiyuan on 2018/8/21 下午6:15.
     * @description ：设置各个控件的监听事件
     */
    private void setListener() {
        idReaderToolHighlight.setOnClickListener(this);
        idReaderToolHighlight.setOnLongClickListener(this);
        idReaderToolUnderline.setOnClickListener(this);
        idReaderToolUnderline.setOnLongClickListener(this);
        idReaderToolStrick.setOnClickListener(this);
        idReaderToolStrick.setOnLongClickListener(this);
        idReaderToolPageEdit.setOnClickListener(this);
        idReaderToolInk.setOnClickListener(this);
        idReaderToolInk.setOnLongClickListener(this);
        idReaderToolShape.setOnClickListener(this);
        idReaderToolShape.setOnLongClickListener(this);
        idReaderToolFreeText.setOnClickListener(this);
        idReaderToolFreeText.setOnLongClickListener(this);
        idReaderToolSign.setOnClickListener(this);
        idReaderToolStamp.setOnClickListener(this);
        idReaderToolLink.setOnClickListener(this);

        idInkOperateCancel.setOnClickListener(this);
        idInkOperateRecover.setOnClickListener(this);
        idInkOperateClean.setOnClickListener(this);
        idInkOperateComplete.setOnClickListener(this);
        idReaderFloatingBackPage.setOnClickListener(this);

    }

    /**
     * @param ：[]
     * @return : void
     * @methodName ：initPopupWindow created by liujiyuan on 2018/8/21 下午6:15.
     * @description ：初始化各个popupWindow
     */
    private void initPopupWindow() {
        /****** 初始化 PopupWindowUtil 工具类 ******/
        PopupWindowUtil.finishUtil();
        PopupWindowUtil.initUtil(this, idReaderDocument);
        /****** 创建 MARKER_PEN_ATTR 的popupWindow ******/
        PopupWindowUtil.buildPopupWindow(PopupWindowUtil.PopupWindowType.MARKER_PEN_ATTR).setCallback(this);
        /****** 创建 形状注释 属性的弹出******/
        PopupWindowUtil.buildPopupWindow(PopupWindowUtil.PopupWindowType.SHAPE_ANNOT_CONFIG);
        /****** 创建 FreeText注释 属性的弹框 ******/
        PopupWindowUtil.buildPopupWindow(PopupWindowUtil.PopupWindowType.FREETEXT_ATTR);
        /****** 创建 link注释 属性的弹框 ******/
        PopupWindowUtil.buildPopupWindow(PopupWindowUtil.PopupWindowType.CREATE_LINK_ANNOT);
        /****** 创建 打开相册的 属性的弹框 ******/
        PopupWindowUtil.buildPopupWindow(PopupWindowUtil.PopupWindowType.TAKE_OR_PICK_PHOTO).setCallback(getMvpPresenter());
    }

    /**
     * @param ：[]
     * @return : void
     * @methodName ：initUI created by liujiyuan on 2018/8/21 下午6:14.
     * @description ：初始化各个控件的状态
     */
    private void initUI() {
        /****** 设置高亮、下划线、横线按钮的初始颜色 ******/
        idReaderToolHighlightBg.setBackgroundColor(AnnotDefaultConfig.markerPenColor_hightlight);
        idReaderToolUnderlineBg.setBackgroundColor(AnnotDefaultConfig.markerPenColor_underline);
        idReaderToolStrickBg.setBackgroundColor(AnnotDefaultConfig.markerPenColor_strikeout);

        /****** 点击注释工具栏按钮颜色值 ******/
        toolsButtonNormalColor = ContextCompat.getColor(this, R.color.reader_tools_button_normal);
        toolsButtonClickedColor = ContextCompat.getColor(this, R.color.reader_tools_button_clicked);
        /****** 隐藏页面跳转按钮 ******/
        idReaderFloatingBackPage.setVisibility(View.GONE);
        lastPageIndex = -1;
        /****** 隐藏提示语 ******/
        idReaderToolPopBg.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        setAllButtonToNormalColor();
        /****** 隐藏提示语 ******/
        idReaderToolPopBg.setVisibility(View.GONE);
        if (annotToolsOnClick(v)) {
            kmpdfDocumentController.saveModifingAnnot();
            kmpdfInkController.saveDrawInk();
        }
        /****** ink编辑工具类按钮点击事件 ******/
        inkMenuOnClick(v);
        /****** 界面其他按钮的点击事件 ******/
        switch (v.getId()) {
            case R.id.id_reader_floating_backPage:
                isClickedBackPage = true;
                idReaderFloatingBackPage.setVisibility(View.GONE);
                if (getMvpPresenter() != null) {
                    getMvpPresenter().resetAnnotationState();
                }
                kmpdfDocumentController.gotoPage(lastPageIndex);
                updateReaderPageIndicator();
                lastPageIndex = -1;
                break;
            default:
        }
    }

    /**
     * @param ：[v]
     * @return : boolean
     * @methodName ：annotToolsOnClick created by liujiyuan on 2018/8/23 下午6:19.
     * @description ：注释工具条的按钮点击事件
     */
    private boolean annotToolsOnClick(View v) {
        switch (v.getId()) {
            case R.id.id_reader_tool_pageEdit:
                openActivity(OPEN_PAGE_EDIT);
                break;
            case R.id.id_reader_tool_highlight:
                getMvpPresenter().highLightSingleTap();
                break;
            case R.id.id_reader_tool_underline:
                getMvpPresenter().underLineSingleTap();
                break;
            case R.id.id_reader_tool_strick:
                getMvpPresenter().strikeOutSingleTap();
                break;
            case R.id.id_reader_tool_ink:
                idInkOperate.setVisibility(View.VISIBLE);
                getMvpPresenter().inkSingleTap();
                isEnterFullScreen = true;
                enterFullScreenMode();
                break;
            case R.id.id_reader_tool_shape:
                getMvpPresenter().shapeAnnotSingleTap();
                break;
            case R.id.id_reader_tool_freeText:
                getMvpPresenter().freeTextAnnotSingleTap();
                break;
            case R.id.id_reader_tool_sign:
                getMvpPresenter().signAnnotSingleTap();
                break;
            case R.id.id_reader_tool_stamp:
                getMvpPresenter().stampAnnotSingleTap();
                break;
            case R.id.id_reader_tool_link:
                getMvpPresenter().linkAnnotSingleTap();
                break;
            default:
                return false;
        }
        return true;
    }

    /**
     * @param ：[v]
     * @return : boolean
     * @methodName ：inkMenuOnClick created by liujiyuan on 2018/8/23 下午6:20.
     * @description ：涂鸦工具栏的按钮点击事件
     */
    private boolean inkMenuOnClick(View v) {
        switch (v.getId()) {
            case R.id.id_ink_operate_cancel:
                idReaderToolInk.setBackgroundColor(toolsButtonClickedColor);
                idInkOperate.setVisibility(View.VISIBLE);
                kmpdfInkController.cancelDraw();
                break;
            case R.id.id_ink_operate_recover:
                idReaderToolInk.setBackgroundColor(toolsButtonClickedColor);
                idInkOperate.setVisibility(View.VISIBLE);
                kmpdfInkController.recoverDraw();
                break;
            case R.id.id_ink_operate_clean:
                idReaderToolInk.setBackgroundColor(toolsButtonClickedColor);
                idInkOperate.setVisibility(View.VISIBLE);
                kmpdfInkController.cleanDraw();
                break;
            case R.id.id_ink_operate_complete:
                kmpdfInkController.stopDrawInk();
                AnnotMenuStatus.status = AnnotMenuStatus.STATUS.NULL;
                idReaderToolInk.setBackgroundColor(toolsButtonNormalColor);
                isEnterFullScreen = false;
                enterFullScreenMode();
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public boolean onLongClick(View v) {
        setAllButtonToNormalColor();
        switch (v.getId()) {
            case R.id.id_reader_tool_highlight:
                getMvpPresenter().highLightLongPress();
                break;
            case R.id.id_reader_tool_underline:
                getMvpPresenter().underLineLongPress();
                break;
            case R.id.id_reader_tool_strick:
                getMvpPresenter().strikeOutLongPress();
                break;
            case R.id.id_reader_tool_ink:
                idInkOperate.setVisibility(View.VISIBLE);
                getMvpPresenter().inkLongPress();
                isEnterFullScreen = true;
                enterFullScreenMode();
                break;
            case R.id.id_reader_tool_shape:
                getMvpPresenter().shapeAnnotLongPress();
                break;
            case R.id.id_reader_tool_freeText:
                getMvpPresenter().freeTextAnnotLongPress();
                break;
            default:
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reader_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reader_doc_search:
                openActivity(OPEN_SEARCH_DOC);
                break;
            case R.id.reader_doc_book_line:
                openActivity(OPEN_BO_DOC);
                break;
            case R.id.reader_doc_more:
                openActivity(OPEN_MORE_DOC);
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void openActivity(int type) {
        Intent intent = new Intent();
        switch (type) {
            case OPEN_SEARCH_DOC:
                intent.setClass(this, ProReaderSearchActivity.class);
                break;
            case OPEN_BO_DOC:
                intent.setClass(this, OutLineAndBookmarkActivity.class);
                break;
            case OPEN_PAGE_EDIT:
                intent.setClass(this, EditPageActivity.class);
                break;
            case OPEN_STAMP_ANNOT:
                intent.setClass(this, StampAnnotActivity.class);
                break;
            case OPEN_SIGN_ANNOT:
                intent.setClass(this, SignatureAnnotActivity.class);
                break;
            default:
                intent.setClass(this, ProReaderSettingMoreActivity.class);
                break;
        }
        startActivity(intent);
    }

    @Override
    public void onDocumentLoadError(ERROR_CODE error_code, String s) {
        switch (error_code) {
            case OPEN_FILE_ERROR:
                ToastUtil.showToast(this, getResources().getString(R.string.open_file_error));
                break;
            case GET_CORE_ERROR:
                ToastUtil.showToast(this, getResources().getString(R.string.get_code_error));
                break;
            case VERIFY_LICENSE_ERROR:
                ToastUtil.showToast(this, getResources().getString(R.string.verify_license_error));
                break;
            case OPEN_BUFFER_ERROR:
                ToastUtil.showToast(this, getResources().getString(R.string.open_buffer_error));
                break;
            case CANNOT_OPEN_FILE:
                ToastUtil.showToast(this, getResources().getString(R.string.cannot_open_file));
                break;
            default:
        }
    }

    @Override
    public void onDocumentCanProof(boolean b) {
    }

    @Override
    public void onDocumentIsProofing(boolean b) {
    }

    @Override
    protected void onDestroy() {
        /****** 释放 PopupWindowUtil 工具类的资源 ******/
        if (kmpdfFactory != null && kmpdfFactory.annotConfig != null) {
            PopupWindowUtil.finishUtil();
        }
        /****** 存储全局变量 ******/
        KMReaderConfigs.saveReaderConfigs();
        /****** 释放注释控制管理器 ******/
        InitKMPDFControllerUtil.onRelease();
        /****** 保存当前浏览界面页码 ******/
        if (kmpdfDocumentController != null) {
            KMReaderSpUtils.getInstance().saveDocumentPage(GlobalConfigs.FILE_ABSOLUTE_PATH, kmpdfDocumentController.getCurrentPageNum());
        }
        /****** 底层库清除factroy，防止产生OOM问题 ******/
        if (kmpdfFactory != null) {
            kmpdfFactory.onDestroy();
        }

        EventBusUtils.getInstance().unRegister(this);
        super.onDestroy();
    }

    /**
     * @param ：[status]
     * @return : void
     * @methodName ：changeButtonBackgroundColor created by liujiyuan on 2018/8/21 下午3:10.
     * @description ：点击tools事件的回调方法
     */
    @Override
    public void changeButtonBackgroundColor(AnnotMenuStatus.STATUS status) {
        switch (status) {
            case HIGHLIGHT:
                changeLongPressPopVisible(status);
                idReaderToolHighlight.setBackgroundColor(toolsButtonClickedColor);
                break;
            case UNDERLINE:
                changeLongPressPopVisible(status);
                idReaderToolUnderline.setBackgroundColor(toolsButtonClickedColor);
                break;
            case STRIKEOUT:
                changeLongPressPopVisible(status);
                idReaderToolStrick.setBackgroundColor(toolsButtonClickedColor);
                break;
            case INK:
                idReaderToolInk.setBackgroundColor(toolsButtonClickedColor);
                break;
            case SHAPE:
                changeLongPressPopVisible(status);
                idReaderToolShape.setBackgroundColor(toolsButtonClickedColor);
                break;
            case FREETEXT:
                idReaderToolFreeText.setBackgroundColor(toolsButtonClickedColor);
                break;
            case STAMP:
                idReaderToolStamp.setBackgroundColor(toolsButtonClickedColor);
                break;
            case SIGN:
                idReaderToolSign.setBackgroundColor(toolsButtonClickedColor);
                break;
            case LINK:
                idReaderToolLink.setBackgroundColor(toolsButtonClickedColor);
                break;
            case NULL:
                setAllButtonToNormalColor();
                break;
            default:
        }
    }

    /**
     * @methodName：changeLongPressPopVisible created by liujiyuan on 2018/9/27 下午2:15.
     * @description：显示长按提示语的方法
     */
    private void changeLongPressPopVisible(AnnotMenuStatus.STATUS status) {
        idReaderToolPopBg.setVisibility(View.VISIBLE);
        idReaderToolHighlightPop.setVisibility(View.GONE);
        idReaderToolUnderlinePop.setVisibility(View.GONE);
        idReaderToolStrickPop.setVisibility(View.GONE);
        idReaderToolShapePop.setVisibility(View.GONE);
        switch (status) {
            case HIGHLIGHT:
                if (isFirstHighLightOpen) {
                    AnimationUtil.showViewFromBottomToTop(idReaderToolHighlightPop);
                    KMReaderSpUtils.getInstance().setNoFirstHighLightUsed();
                    isFirstHighLightOpen = false;
                }
                break;
            case UNDERLINE:
                if (isFirstUnderLineOpen) {
                    AnimationUtil.showViewFromBottomToTop(idReaderToolUnderlinePop);
                    KMReaderSpUtils.getInstance().setNoFirstUnderLineUsed();
                    isFirstUnderLineOpen = false;
                }
                break;
            case STRIKEOUT:
                if (isFirstStrickOpen) {
                    AnimationUtil.showViewFromBottomToTop(idReaderToolStrickPop);
                    KMReaderSpUtils.getInstance().setNoFirstStrickUsed();
                    isFirstStrickOpen = false;
                }
                break;
            case SHAPE:
                if (isFirstShapeOpen) {
                    AnimationUtil.showViewFromBottomToTop(idReaderToolShapePop);
                    KMReaderSpUtils.getInstance().setNoFirstShapetUsed();
                    isFirstShapeOpen = false;
                }
                break;
            default:
        }
    }


    /**
     * @param ：[]
     * @return : void
     * @methodName ：setAllButtonToNormalColor created by liujiyuan on 2018/8/21 下午4:20.
     * @description ：设置工具栏按钮颜色为非点击颜色
     */
    private void setAllButtonToNormalColor() {
        idReaderToolHighlight.setBackgroundColor(toolsButtonNormalColor);
        idReaderToolUnderline.setBackgroundColor(toolsButtonNormalColor);
        idReaderToolStrick.setBackgroundColor(toolsButtonNormalColor);
        idReaderToolPageEdit.setBackgroundColor(toolsButtonNormalColor);
        idReaderToolInk.setBackgroundColor(toolsButtonNormalColor);
        idReaderToolShape.setBackgroundColor(toolsButtonNormalColor);
        idReaderToolFreeText.setBackgroundColor(toolsButtonNormalColor);
        idReaderToolSign.setBackgroundColor(toolsButtonNormalColor);
        idReaderToolStamp.setBackgroundColor(toolsButtonNormalColor);
        idReaderToolLink.setBackgroundColor(toolsButtonNormalColor);
        idInkOperate.setVisibility(View.GONE);
    }

    /**
     * @param ：[]
     * @return : void
     * @methodName ：attrChanged created by liujiyuan on 2018/8/21 下午3:09.
     * @description ：下划线、高亮、横线、ink的属性值修改回调方法
     */
    @Override
    public void markerPenAttrChanged() {
        switch (AnnotDefaultConfig.markerPenType) {
            case HIGH_LIGHT:
                idReaderToolHighlightBg.setBackgroundColor(AnnotDefaultConfig.markerPenColor_hightlight);
                break;
            case UNDER_LINE:
                idReaderToolUnderlineBg.setBackgroundColor(AnnotDefaultConfig.markerPenColor_underline);
                break;
            case STRIK_EOUT:
                idReaderToolStrickBg.setBackgroundColor(AnnotDefaultConfig.markerPenColor_strikeout);
                break;
            case INK:
                idReaderToolInkIv.setImageDrawable(getMvpPresenter().returnInkButtonPicture());
                break;
            default:
        }
    }

    @Subscribe
    public void onEventMainThread(MessageEvent<Integer> event) {
        String tag = event.getTag();
        switch (tag) {
            case ConstantBus.READER_SEARCH_TEXT:
                /****** 跳转到相关页面 ******/
                int feedBack = event.getEvent();
                kmpdfDocumentController.gotoPage(feedBack);
                break;
            case ConstantBus.REFRESH_DOCUMENT:
                /****** 刷新页面 ******/
                kmpdfDocumentController.refresh(false);
                break;
            default:
        }
    }

    @Override
    public void onDataResult(Object result) {
        if (result == null) {
            finish();
        } else {
            String feedback = (String) result;
            boolean result_confirm = kmpdfFactory.authenticatePassword(feedback);
            if (!result_confirm) {
                confirmPasswordDialog();
            } else {
                if (kmpdfFactory.init()) {
                    doAfterConfirm();
                    initPresenter();
                } else {
                    finish();
                }
            }
        }
    }

    /**
     * @param ：[]
     * @return : void
     * @methodName ：confirmPasswordDialog created by liujiyuan on 2018/8/28 下午4:09.
     * @description ：文本密码的输入框
     */
    private void confirmPasswordDialog() {
        try {
            DialogFragmentHelper.showPasswordInsertDialog(getSupportFragmentManager(), DialogFragmentHelper.TYPE_EDIT_DIALOG_PASSWORD, getString(R.string.dialog_enter_password_title), this, true);
        } catch (Exception e) {
            ToastUtil.showToast(this, getString(R.string.dialog_error));
        }
    }

    @Override
    public void onBackPressed() {
        if ((null != kmpdfDocumentController) && kmpdfDocumentController.hasChanges()) {
            @SuppressLint("StaticFieldLeak") SimpleBackgroundTask simpleBackgroundTask = new SimpleBackgroundTask<Boolean>(this) {
                @Override
                protected Boolean onRun() {
                    return kmpdfDocumentController.save();
                }

                @Override
                protected void onSuccess(Boolean result) {
                    if (result) {
                        ToastUtil.showToast(ProApplication.getContext(), R.string.save_annot_success);
                    } else {
                        ToastUtil.showToast(ProApplication.getContext(), R.string.save_annot_fail);
                    }
                    stopProgressDialog();
                    finish();
                }
            };
            showProgressDialog(getString(R.string.saving_annotation), true, false);
            simpleBackgroundTask.execute();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        /****** 阅读界面隐藏状态栏 ******/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        /****** 设置activity的亮度 ******/
        BrightnessUtil.setActivityBrightness(KMReaderConfigs.READER_BRIGHTNESS, this);
        /****** 设置activity的横竖位置 ******/
        if (KMReaderConfigs.ISLOCKED) {
            if (KMReaderConfigs.ORIENTATION == KMReaderConfigs.PORTRAIT) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
        /****** 初始化 PopupWindow ******/
        initPopupWindow();
        /****** 获取页面数 ******/
        if (kmpdfDocumentController != null) {
            KMReaderConfigs.readerPageCount = kmpdfDocumentController.getDocumentPageCount(false);
        }
        /****** 设置页码指示器 ******/
        updateReaderPageIndicator();
        if (idReaderPageIndicator != null) {
            /****** 页码指示器停留1s消失 ******/
            handler.postDelayed(() -> idReaderPageIndicator.setVisibility(View.GONE), 1000);
        }
        super.onResume();
    }

    /**
     * @methodName：updateReaderPageIndicator created by liujiyuan on 2018/9/18 上午11:57.
     * @description：设置页码指示器
     */
    private void updateReaderPageIndicator() {
        if (idReaderPageIndicator == null) {
            return;
        }
        idReaderPageIndicator.setVisibility(View.VISIBLE);
        int currentPage = 0;
        if (kmpdfDocumentController != null) {
            currentPage = kmpdfDocumentController.getCurrentPageNum();
        }
        String page_indicator = String.valueOf(currentPage + 1) + " / " + String.valueOf(KMReaderConfigs.readerPageCount);
        idReaderPageIndicator.setText(page_indicator);
    }

    /**
     * @methodName：enterFullScreenMode created by liujiyuan on 2018/9/20 下午5:18.
     * @description：切换全屏模式的方法
     */
    private void enterFullScreenMode() {
        if (isEnterFullScreen) {
            AnimationUtil.hideViewFromBottomToTop(idAppbarLayout);
            AnimationUtil.hideViewFromTopToBottom(idReaderToolers);
            idReaderPageIndicator.setVisibility(View.GONE);
            idReaderFloatingBackPage.setVisibility(View.GONE);
        } else {
            AnimationUtil.showViewFromBottomToTop(idReaderToolers);
            AnimationUtil.showViewFromTopToBottom(idAppbarLayout);
            idReaderPageIndicator.setVisibility(View.VISIBLE);
            if (lastPageIndex != -1) {
                idReaderFloatingBackPage.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (getMvpPresenter() != null) {
            getMvpPresenter().getPictureUri(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showVerifyLicenseError(int error) {
        switch (error) {
            case EMPTY_LICENSE:
                ToastUtil.showToast(this, getResources().getString(R.string.verify_key_empty_license));
                break;
            case ERROR_LICENSE:
                ToastUtil.showToast(this, getResources().getString(R.string.verify_key_error_license));
                break;
            case ERROR_RSAMESSAGE:
                ToastUtil.showToast(this, getResources().getString(R.string.verify_key_error_rsamessage));
                break;
            case RSAMESSAGE_ERROR_TYPE:
                ToastUtil.showToast(this, getResources().getString(R.string.verify_key_error_rsamessage_type));
                break;
            case RSAMESSAGE_EXPIRED:
                ToastUtil.showToast(this, getResources().getString(R.string.verify_key_error_rsamessage_expire));
                break;
            case RSAMESSAGE_ERROR_APPLICATIONID:
                ToastUtil.showToast(this, getResources().getString(R.string.verify_key_error_rsamessage_applicationid));
                break;
            case RSAMESSAGE_ERROR_MODULE:
                ToastUtil.showToast(this, getResources().getString(R.string.verify_key_error_rsamessage_module));
                break;
            default:
        }
    }

    @Override
    public void performPickFor(FilePicker filePicker) {
    }

    @Override
    public void onError(ErrorId errorId) {

    }

    //监听音量键，上下键用于翻页设置
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                kmpdfDocumentController.gotoPage(kmpdfDocumentController.getCurrentPageNum() - 1);
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                kmpdfDocumentController.gotoPage(kmpdfDocumentController.getCurrentPageNum() + 1);
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }
}
