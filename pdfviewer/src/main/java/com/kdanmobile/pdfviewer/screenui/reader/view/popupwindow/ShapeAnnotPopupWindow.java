package com.kdanmobile.pdfviewer.screenui.reader.view.popupwindow;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.kdanmobile.pdfviewer.R;
import com.kdanmobile.kmpdfkit.annotation.bean.KMPDFAnnotationBean;
import com.kdanmobile.kmpdfkit.annotation.bean.KMPDFArrowAnnotationBean;
import com.kdanmobile.kmpdfkit.annotation.bean.KMPDFCircleAnnotationBean;
import com.kdanmobile.kmpdfkit.annotation.bean.KMPDFLineAnnotationBean;
import com.kdanmobile.kmpdfkit.annotation.bean.KMPDFSquareAnnotationBean;
import com.kdanmobile.pdfviewer.base.BasePopupWindow;
import com.kdanmobile.pdfviewer.event.MessageEvent;
import com.kdanmobile.pdfviewer.screenui.reader.configs.AnnotDefaultConfig;
import com.kdanmobile.pdfviewer.screenui.reader.view.adapter.ColorSelectAdapter;
import com.kdanmobile.pdfviewer.screenui.reader.view.listener.OnPopupWindowCallback;
import com.kdanmobile.pdfviewer.screenui.reader.view.listener.PopupWindowStruct;
import com.kdanmobile.pdfviewer.screenui.reader.view.listener.SeekBarChangeListenerAbstract;
import com.kdanmobile.pdfviewer.screenui.reader.widget.SelectableImageView;
import com.kdanmobile.pdfviewer.utils.SimpleTabSelectedListener;
import com.kdanmobile.pdfviewer.utils.eventbus.ConstantBus;
import com.kdanmobile.pdfviewer.utils.eventbus.EventBusUtils;

import java.util.ArrayList;

/**
 * @classname：ShapeAnnotPopupWindow
 * @author：liujiyuan
 * @date：2018/8/31 上午9:53
 * @description：形状注释的属性设置界面
 */
public class ShapeAnnotPopupWindow extends BasePopupWindow implements PopupWindowStruct {

    public static final int CREATE_SHAPE= 0X1011;
    public static final int EDIT_SHAPE= 0X1012;

    public static final int STROKE_ATTR= 0;
    public static final int FILL_ATTR= 1;
    private int tab_type;

    private static final int MAXSIZE = 9;

    /****** Popupwindow附着的view ******/
    private View rootview;

    private AnnotDefaultConfig.ShapeAnnotationType shape_type = AnnotDefaultConfig.shapeType;

    private TabLayout idShapeMenuTablayout;
    private RecyclerView idShapeColorChooseLv;
    private RelativeLayout idShapeIncludeStrokeMenuLayout;
    private SeekBar idShapeAlphaBar;
    private TextView idShapeAlphaValue;
    private SeekBar idShapeSizeBar;
    private TextView idShapeSizeValue;
    private LinearLayout idShapeTypeLayout;
    private SelectableImageView idShapeSelectCircle;
    private SelectableImageView idShapeSelectRectangle;
    private SelectableImageView idShapeSelectArrow;
    private SelectableImageView idShapeSelectLine;
    private LinearLayout idShapeIncludeFillMenuLayout;
    private SeekBar idShapeFillAlphaBar;
    private TextView idShapeFillAlphaValue;
    private ColorSelectAdapter colorSelectAdapter;

    private ArrayList<SelectableImageView> shapeTypeArr;

    private int lineAlpha = AnnotDefaultConfig.shapeAnnotLineAlpha;
    private float lineWidth = AnnotDefaultConfig.shapeAnnotLineWidth;
    private int fillAlpha = AnnotDefaultConfig.shapeAnnotFillAlpha;
    private int lineColor = AnnotDefaultConfig.shapeAnnotLineColor;
    private int fillColor = AnnotDefaultConfig.shapeAnnotFillColor;

    public ShapeAnnotPopupWindow(Context context, View rootview) {
        super(context);

        setAnimationStyle(R.style.popwindow_anim_style);
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);

        this.rootview = rootview;
    }

    @Override
    protected View setLayout(LayoutInflater inflater) {
        return inflater.inflate(R.layout.popupwindow_shape_annot,null);
    }

    @Override
    protected void initResource() {
        String[] tablayout_title = new String[2];
        tablayout_title[0] = mContext.getString(R.string.shape_stroke_tab);
        tablayout_title[1] = mContext.getString(R.string.shape_fill_tab);
        for (int i = 0; i < tablayout_title.length; i++) {
            idShapeMenuTablayout.addTab(idShapeMenuTablayout.newTab().setText(tablayout_title[i]));
        }
        /****** 各个seekBar的初始值 ******/
        idShapeAlphaBar.setMax(100);
        idShapeAlphaBar.setProgress((int)(AnnotDefaultConfig.shapeAnnotLineAlpha * (100.0 / 255.0) + 0.5f));
        idShapeAlphaValue.setText((int)(AnnotDefaultConfig.shapeAnnotLineAlpha * (100.0 / 255.0) + 0.5f)+"%");

        idShapeSizeBar.setMax(MAXSIZE);
        idShapeSizeBar.setProgress((int)AnnotDefaultConfig.shapeAnnotLineWidth);
        idShapeSizeValue.setText("" + (int)AnnotDefaultConfig.shapeAnnotLineWidth);

        idShapeFillAlphaBar.setMax(100);
        idShapeFillAlphaBar.setProgress((int)(AnnotDefaultConfig.shapeAnnotFillAlpha * (100.0 / 255.0) + 0.5f));
        idShapeFillAlphaValue.setText((int)(AnnotDefaultConfig.shapeAnnotFillAlpha * (100.0 / 255.0) + 0.5f)+"%");
        setColorSelect();
    }

    /**
     * @methodName：setColorSelect created by liujiyuan on 2018/8/31 下午3:00.
     * @description：给颜色选择器初始值
     */
    private void setColorSelect(){
        if(tab_type == STROKE_ATTR){
            colorSelectAdapter.setSelectIndex(AnnotDefaultConfig.shapeAnnotLineColorId);
        }else{
            colorSelectAdapter.setSelectIndex(AnnotDefaultConfig.shapeAnnotFillColorId);
        }
    }

    @Override
    protected void initListener() {
        idShapeMenuTablayout.setOnClickListener(this);
        idShapeMenuTablayout.addOnTabSelectedListener(new SimpleTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                switch (tab.getPosition()) {
                    case 0:
                        idShapeIncludeStrokeMenuLayout.setVisibility(View.VISIBLE);
                        idShapeIncludeFillMenuLayout.setVisibility(View.GONE);
                        tab_type = STROKE_ATTR;
                        break;
                    case 1:
                        idShapeIncludeStrokeMenuLayout.setVisibility(View.GONE);
                        idShapeIncludeFillMenuLayout.setVisibility(View.VISIBLE);
                        tab_type = FILL_ATTR;
                        break;
                    default:
                }
                setColorSelect();
                colorSelectAdapter.notifyDataSetChanged();
            }
        });
        /****** 添加图片形状点击事件 ******/
        chooseShapeType(shapeTypeArr);

        /****** 颜色适配器的点击事件 ******/
        colorSelectAdapter.setOnItemClickListener(position -> {
            if(tab_type == STROKE_ATTR){
                lineColor = AnnotDefaultConfig.colorArr[position];
                AnnotDefaultConfig.shapeAnnotLineColor = lineColor;
                AnnotDefaultConfig.shapeAnnotLineColorId = position;
            }else{
                fillColor = AnnotDefaultConfig.colorArr[position];
                AnnotDefaultConfig.shapeAnnotFillColor = fillColor;
                AnnotDefaultConfig.shapeAnnotFillColorId = position;
            }
            changeShapeAnnotationAttr();
        });

        /****** 给各个seekBar控件添加监听器 ******/
        idShapeAlphaBar.setOnSeekBarChangeListener(new SeekBarChangeListenerAbstract(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                super.onProgressChanged(seekBar, progress, fromUser);
                idShapeAlphaValue.setText(progress+"%");
                lineAlpha = (int)(progress * (255.0 / 100.0) + 0.5f);
                AnnotDefaultConfig.shapeAnnotLineAlpha = lineAlpha;
                changeShapeAnnotationAttr();
            }
        });
        idShapeSizeBar.setOnSeekBarChangeListener(new SeekBarChangeListenerAbstract(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                super.onProgressChanged(seekBar, progress, fromUser);
                idShapeSizeValue.setText(""+(progress + 1));
                lineWidth = progress + 1;
                AnnotDefaultConfig.shapeAnnotLineWidth = lineWidth;
                changeShapeAnnotationAttr();
            }
        });
        idShapeFillAlphaBar.setOnSeekBarChangeListener(new SeekBarChangeListenerAbstract(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                super.onProgressChanged(seekBar, progress, fromUser);
                idShapeFillAlphaValue.setText(progress+"%");
                fillAlpha = (int)(progress * (255.0 / 100.0) + 0.5f);
                AnnotDefaultConfig.shapeAnnotFillAlpha = fillAlpha;
                changeShapeAnnotationAttr();
            }
        });
    }

    @Override
    protected void initView() {

        idShapeMenuTablayout = mContentView.findViewById(R.id.id_shape_menu_tablayout);
        idShapeColorChooseLv = mContentView.findViewById(R.id.id_shape_color_choose_lv);

        idShapeIncludeStrokeMenuLayout = mContentView.findViewById(R.id.id_shape_include_stroke_menu_layout);
        idShapeAlphaBar = mContentView.findViewById(R.id.id_shape_alpha_bar);
        idShapeAlphaValue = mContentView.findViewById(R.id.id_shape_alpha_value);
        idShapeSizeBar = mContentView.findViewById(R.id.id_shape_size_bar);
        idShapeSizeValue = mContentView.findViewById(R.id.id_shape_size_value);

        idShapeTypeLayout = mContentView.findViewById(R.id.id_shape_type_layout);
        idShapeSelectCircle = mContentView.findViewById(R.id.id_shape_select_circle);
        idShapeSelectRectangle = mContentView.findViewById(R.id.id_shape_select_rectangle);
        idShapeSelectArrow = mContentView.findViewById(R.id.id_shape_select_arrow);
        idShapeSelectLine = mContentView.findViewById(R.id.id_shape_select_line);
        idShapeSelectCircle.setType(AnnotDefaultConfig.ShapeAnnotationType.CIRCLE);
        idShapeSelectRectangle.setType(AnnotDefaultConfig.ShapeAnnotationType.SQUARE);
        idShapeSelectArrow.setType(AnnotDefaultConfig.ShapeAnnotationType.ARROW);
        idShapeSelectLine.setType(AnnotDefaultConfig.ShapeAnnotationType.LINE);

        switch (AnnotDefaultConfig.shapeType){
            case CIRCLE:
                idShapeSelectCircle.setIsDrawRect(true);
                break;
            case LINE:
                idShapeSelectLine.setIsDrawRect(true);
                break;
            case SQUARE:
                idShapeSelectRectangle.setIsDrawRect(true);
                break;
            case ARROW:
                idShapeSelectArrow.setIsDrawRect(true);
                break;
            default:
                break;
        }

        idShapeIncludeFillMenuLayout = mContentView.findViewById(R.id.id_shape_include_fill_menu_layout);
        idShapeFillAlphaBar = mContentView.findViewById(R.id.id_shape_fill_alpha_bar);
        idShapeFillAlphaValue = mContentView.findViewById(R.id.id_shape_fill_alpha_value);

        /****** 适配主题颜色 ******/

        /****** 添加颜色选择器 ******/
        idShapeColorChooseLv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayout.HORIZONTAL, false));
        idShapeColorChooseLv.setItemAnimator(new DefaultItemAnimator());
        colorSelectAdapter = new ColorSelectAdapter(AnnotDefaultConfig.colorArr);
        idShapeColorChooseLv.setAdapter(colorSelectAdapter);
        /****** 添加形状图片的选择器 ******/
        shapeTypeArr = new ArrayList<>();
        shapeTypeArr.add(idShapeSelectCircle);
        shapeTypeArr.add(idShapeSelectRectangle);
        shapeTypeArr.add(idShapeSelectArrow);
        shapeTypeArr.add(idShapeSelectLine);
    }

    @Override
    protected void onClickListener(View view) {
    }

    @Override
    public PopupWindowStruct setCallback(OnPopupWindowCallback callback) {
        return null;
    }

    @Override
    public PopupWindowStruct setObject(Object o) {
        return null;
    }

    @Override
    public void show(int type) {
    }

    public void show(int showType, int tabType){
        if(showType == CREATE_SHAPE){
            idShapeTypeLayout.setVisibility(View.VISIBLE);
        }else{
            idShapeTypeLayout.setVisibility(View.GONE);
        }
        tab_type = tabType;
        idShapeMenuTablayout.getTabAt(tabType).select();
        showAtLocation(rootview, Gravity.BOTTOM, 0, 0);
    }

    /**
     * @methodName：chooseShapeType created by liujiyuan on 2018/8/31 下午2:20.
     * @description：给每一个形状注释添加点击事件
     */
    private void chooseShapeType(final ArrayList<SelectableImageView> arr){
        for(final SelectableImageView m : arr){
            m.setOnClickListener(v -> {
                for(SelectableImageView temp : arr){
                    if(temp == m){
                        temp.setIsDrawRect(true);
                        shape_type = temp.getType();
                        AnnotDefaultConfig.shapeType = shape_type;
                    }else{
                        temp.setIsDrawRect(false);
                    }
                    temp.invalidate();
                }
                changeShapeAnnotationAttr();
            });
        }
    }

    /**
     * @methodName：changeShapeAnnotationAttr created by liujiyuan on 2018/9/14 下午2:13.
     * @description：改变shape属性的方法
     */
    private void changeShapeAnnotationAttr(){
        KMPDFAnnotationBean annotationBean;
        switch (AnnotDefaultConfig.shapeType){
            case LINE:
                annotationBean = new KMPDFLineAnnotationBean("", AnnotDefaultConfig.shapeAnnotLineColor, AnnotDefaultConfig.shapeAnnotLineWidth, AnnotDefaultConfig.shapeAnnotLineAlpha);
                break;
            case ARROW:
                annotationBean = new KMPDFArrowAnnotationBean("", AnnotDefaultConfig.shapeAnnotLineColor, AnnotDefaultConfig.shapeAnnotLineWidth, AnnotDefaultConfig.shapeAnnotLineAlpha);
                break;
            case CIRCLE:
                annotationBean = new KMPDFCircleAnnotationBean("", AnnotDefaultConfig.shapeAnnotLineColor, AnnotDefaultConfig.shapeAnnotLineWidth, AnnotDefaultConfig.shapeAnnotLineAlpha,
                        AnnotDefaultConfig.shapeAnnotFillColor, AnnotDefaultConfig.shapeAnnotFillAlpha);
                break;
            case SQUARE:
                annotationBean = new KMPDFSquareAnnotationBean("", AnnotDefaultConfig.shapeAnnotLineColor, AnnotDefaultConfig.shapeAnnotLineWidth, AnnotDefaultConfig.shapeAnnotLineAlpha,
                        AnnotDefaultConfig.shapeAnnotFillColor, AnnotDefaultConfig.shapeAnnotFillAlpha);
                break;
            default:
                return;
        }
        EventBusUtils.getInstance().post(new MessageEvent<>(ConstantBus.SET_ANNOTATION_ATTR, annotationBean));
    }
}
