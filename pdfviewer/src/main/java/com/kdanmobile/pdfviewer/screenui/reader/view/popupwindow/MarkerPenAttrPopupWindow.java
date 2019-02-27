package com.kdanmobile.pdfviewer.screenui.reader.view.popupwindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.kdanmobile.kmpdfkit.annotation.bean.KMPDFAnnotationBean;
import com.kdanmobile.kmpdfkit.annotation.bean.KMPDFHighlightAnnotationBean;
import com.kdanmobile.kmpdfkit.annotation.bean.KMPDFInkAnnotationBean;
import com.kdanmobile.kmpdfkit.annotation.bean.KMPDFStrikeoutAnnotationBean;
import com.kdanmobile.kmpdfkit.annotation.bean.KMPDFUnderlineAnnotationBean;
import com.kdanmobile.pdfviewer.R;
import com.kdanmobile.pdfviewer.base.BasePopupWindow;
import com.kdanmobile.pdfviewer.base.ProApplication;
import com.kdanmobile.pdfviewer.event.MessageEvent;
import com.kdanmobile.pdfviewer.screenui.reader.configs.AnnotDefaultConfig;
import com.kdanmobile.pdfviewer.screenui.reader.view.adapter.ColorSelectAdapter;
import com.kdanmobile.pdfviewer.screenui.reader.view.listener.OnMarkerPenAttrChangeListener;
import com.kdanmobile.pdfviewer.screenui.reader.view.listener.OnPopupWindowCallback;
import com.kdanmobile.pdfviewer.screenui.reader.view.listener.PopupWindowStruct;
import com.kdanmobile.pdfviewer.screenui.reader.view.listener.SeekBarChangeListenerAbstract;
import com.kdanmobile.pdfviewer.screenui.reader.widget.SineCurveView;
import com.kdanmobile.pdfviewer.utils.eventbus.ConstantBus;
import com.kdanmobile.pdfviewer.utils.eventbus.EventBusUtils;

/**
 * @classname：MarkerPenAttrPopupWindow
 * @author：liujiyuan
 * @date：2018/8/20 上午10:47
 * @description：高亮、下划线、横线、ink和签名编辑的弹出属性界面
 */
public class MarkerPenAttrPopupWindow extends BasePopupWindow implements PopupWindowStruct {

	private int backgroundColor = ContextCompat.getColor(ProApplication.getContext(), R.color.makeup_bg);
	private static final int MAXSIZE = 9;

	private OnMarkerPenAttrChangeListener onMarkerPenAttrChangeListener;

	/****** Popupwindow附着的view ******/
	private View rootview;
	
	private SineCurveView idMarkerpenPopSineCurve;
	private RecyclerView idMarkerpenPopColorLv;
	private SeekBar idMarkerpenPopAlphaBar;
	private TextView idMarkerpenPopAlphaValue;
	private LinearLayout idMarkerpenPopSizeLayout;
	private SeekBar idMarkerpenPopSizeBar;
	private TextView idMarkerpenPopSizeValue;
	private LinearLayout idMarkerpenPopStyleArrowLayout;
	private ImageView idMarkerpenPopNormalPenArrow;
	private ImageView idMarkerpenPopMarkerPenArrow;
	private LinearLayout idMarkerpenPopStyleIvLayout;
	private ImageView idMarkerpenPopNormalPen;
	private ImageView idMarkerpenPopMarkerPen;
	
	private ColorSelectAdapter colorSelectAdapter;

	public MarkerPenAttrPopupWindow(Context context, View rootview) {
		super(context);
		/****** 设置宽与高 ******/
		setWidth(WindowManager.LayoutParams.MATCH_PARENT);
		setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

		setBackgroundDrawable(new ColorDrawable(backgroundColor));
		setAnimationStyle(R.style.popwindow_anim_style);

		this.rootview = rootview;
	}

	@Override
	protected View setLayout(LayoutInflater inflater) {
		return inflater.inflate(R.layout.popupwindow_markerpen, null);
	}

	@Override
	protected void initResource() {

	}

	@Override
	protected void initListener() {
		/****** 颜色选择适配器的监听事件 ******/
		colorSelectAdapter.setOnItemClickListener(position -> {
			idMarkerpenPopSineCurve.setLineColor(AnnotDefaultConfig.colorArr[position]);
			idMarkerpenPopSineCurve.invalidate();
			switch (AnnotDefaultConfig.markerPenType){
				case HIGH_LIGHT:
					AnnotDefaultConfig.markerPenColor_hightlight = AnnotDefaultConfig.colorArr[position];
					AnnotDefaultConfig.markerPenColorId_highlight = position;
					idMarkerpenPopSineCurve.setLineAlpha(AnnotDefaultConfig.markerPenAlpha_hightlight);
					break;
				case UNDER_LINE:
					AnnotDefaultConfig.markerPenColor_underline = AnnotDefaultConfig.colorArr[position];
					AnnotDefaultConfig.markerPenColorId_underline = position;
					idMarkerpenPopSineCurve.setLineAlpha(AnnotDefaultConfig.markerPenAlpha_underline);
					break;
				case STRIK_EOUT:
					AnnotDefaultConfig.markerPenColor_strikeout = AnnotDefaultConfig.colorArr[position];
					AnnotDefaultConfig.markerPenColorId_strikeout = position;
					idMarkerpenPopSineCurve.setLineAlpha(AnnotDefaultConfig.markerPenAlpha_strikeout);
					break;
				case INK:
					AnnotDefaultConfig.markerPenColor_ink = AnnotDefaultConfig.colorArr[position];
					AnnotDefaultConfig.markerPenColorId_ink = position;
					idMarkerpenPopSineCurve.setLineAlpha(AnnotDefaultConfig.markerPenAlpha_ink);
					break;
				default:
			}
			if(onMarkerPenAttrChangeListener != null){
				onMarkerPenAttrChangeListener.markerPenAttrChanged();
			}
			setMarkupAnnotationAttribution();
		});
		/****** 给各个seekBar控件添加监听器 ******/
		idMarkerpenPopAlphaBar.setOnSeekBarChangeListener(new SeekBarChangeListenerAbstract(){
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				super.onProgressChanged(seekBar, progress, fromUser);
				idMarkerpenPopAlphaValue.setText(progress+"%");
				int alpha = (int) (progress * 255.0 / 100.0 + 0.5f);
				idMarkerpenPopSineCurve.setLineAlpha(alpha);
				idMarkerpenPopSineCurve.invalidate();
				switch (AnnotDefaultConfig.markerPenType){
					case HIGH_LIGHT:
						AnnotDefaultConfig.markerPenAlpha_hightlight = alpha;
						break;
					case UNDER_LINE:
						AnnotDefaultConfig.markerPenAlpha_underline = alpha;
						break;
					case STRIK_EOUT:
						AnnotDefaultConfig.markerPenAlpha_strikeout = alpha;
						break;
					case INK:
						AnnotDefaultConfig.markerPenAlpha_ink = alpha;
						break;
					default:
				}

				if(onMarkerPenAttrChangeListener != null){
					onMarkerPenAttrChangeListener.markerPenAttrChanged();
				}
				setMarkupAnnotationAttribution();
			}
		});
		idMarkerpenPopSizeBar.setOnSeekBarChangeListener(new SeekBarChangeListenerAbstract(){
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				super.onProgressChanged(seekBar, progress, fromUser);
				idMarkerpenPopSizeValue.setText(""+(progress + 1));
				float lineWidth = 1;
				switch (AnnotDefaultConfig.drawType){
					case NORMAL_PEN:
						lineWidth = (idMarkerpenPopSizeBar.getProgress() + 1) * 2;
						break;
					case MARKER_PEN:
						lineWidth = (progress + 1) * 5 + 15;
						break;
					default:
				}
				idMarkerpenPopSineCurve.setLineWidth(lineWidth);
				idMarkerpenPopSineCurve.invalidate();
				idMarkerpenPopSizeValue.setText(AnnotDefaultConfig.markerPenSize_ink + "");
				switch (AnnotDefaultConfig.markerPenType){
					case INK:
						AnnotDefaultConfig.markerPenSize_ink = (int)lineWidth;
						break;
					default:
				}
				if(onMarkerPenAttrChangeListener != null){
					onMarkerPenAttrChangeListener.markerPenAttrChanged();
				}
				setMarkupAnnotationAttribution();
			}
		});

		/****** 各个按钮的监听 ******/
		idMarkerpenPopNormalPen.setOnClickListener(this);
		idMarkerpenPopMarkerPen.setOnClickListener(this);
	}

	@Override
	protected void initView() {
		idMarkerpenPopSineCurve = mContentView.findViewById(R.id.id_markerpen_pop_sineCurve);

		idMarkerpenPopColorLv = mContentView.findViewById(R.id.id_markerpen_pop_color_lv);

		idMarkerpenPopColorLv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayout.HORIZONTAL, false));
		idMarkerpenPopColorLv.setItemAnimator(new DefaultItemAnimator());
		colorSelectAdapter = new ColorSelectAdapter(AnnotDefaultConfig.colorArr);
		setColorAdapterSelect();
		idMarkerpenPopColorLv.setAdapter(colorSelectAdapter);

		idMarkerpenPopAlphaValue = mContentView.findViewById(R.id.id_markerpen_pop_alpha_value);
		idMarkerpenPopAlphaBar = mContentView.findViewById(R.id.id_markerpen_pop_alpha_bar);
		idMarkerpenPopAlphaBar.setMax(100);

		idMarkerpenPopSizeValue = mContentView.findViewById(R.id.id_markerpen_pop_size_value);
		idMarkerpenPopSizeBar = mContentView.findViewById(R.id.id_markerpen_pop_size_bar);
		idMarkerpenPopSizeBar.setMax(MAXSIZE);

		idMarkerpenPopNormalPen = mContentView.findViewById(R.id.id_markerpen_pop_normal_pen);
		idMarkerpenPopMarkerPen = mContentView.findViewById(R.id.id_markerpen_pop_marker_pen);

		idMarkerpenPopSizeLayout = mContentView.findViewById(R.id.id_markerpen_pop_size_layout);
		idMarkerpenPopNormalPenArrow = mContentView.findViewById(R.id.id_markerpen_pop_normal_pen_arrow);
		idMarkerpenPopMarkerPenArrow = mContentView.findViewById(R.id.id_markerpen_pop_marker_pen_arrow);
		idMarkerpenPopStyleArrowLayout = mContentView.findViewById(R.id.id_markerpen_pop_style_arrow_layout);
		idMarkerpenPopStyleIvLayout = mContentView.findViewById(R.id.id_markerpen_pop_style_iv_layout);

		changeDrawType();
		changeVisibility();
		setConfigValue();
	}


	/**
	 * @param ：[]
	 * @return : void
	 * @methodName ：setColorAdapterSelect created by liujiyuan on 2018/8/20 上午11:54.
	 * @description ：设置选择的颜色值
	 */
	private void setColorAdapterSelect(){
		switch (AnnotDefaultConfig.markerPenType){
			case HIGH_LIGHT:
				colorSelectAdapter.setSelectIndex(AnnotDefaultConfig.markerPenColorId_highlight);
				break;
			case UNDER_LINE:
				colorSelectAdapter.setSelectIndex(AnnotDefaultConfig.markerPenColorId_underline);
				break;
			case STRIK_EOUT:
				colorSelectAdapter.setSelectIndex(AnnotDefaultConfig.markerPenColorId_strikeout);
				break;
			case INK:
				colorSelectAdapter.setSelectIndex(AnnotDefaultConfig.markerPenColorId_ink);
				idMarkerpenPopSineCurve.setLineColor(AnnotDefaultConfig.markerPenColor_ink);
				idMarkerpenPopSineCurve.setLineAlpha(AnnotDefaultConfig.markerPenAlpha_ink);
				break;
			default:
		}
	}

    /**
     * @param ：[]
     * @return : void
     * @methodName ：setConfigValue created by liujiyuan on 2018/8/20 上午11:58.
     * @description ：设置透明度，makerPen类型、粗细
     */
	private void setConfigValue(){
		int alpha = 0;
		switch (AnnotDefaultConfig.markerPenType){
			case HIGH_LIGHT:
				alpha = (int)(AnnotDefaultConfig.markerPenAlpha_hightlight * (100.0 / 255.0) + 0.5f);
				break;
			case UNDER_LINE:
				alpha = (int)(AnnotDefaultConfig.markerPenAlpha_underline * (100.0 / 255.0) + 0.5f);
				break;
			case STRIK_EOUT:
				alpha = (int)(AnnotDefaultConfig.markerPenAlpha_strikeout * (100.0 / 255.0) + 0.5f);
				break;
			case INK:
				alpha = (int)(AnnotDefaultConfig.markerPenAlpha_ink * (100.0 / 255.0) + 0.5f);
				if(AnnotDefaultConfig.drawType == AnnotDefaultConfig.DrawType.NORMAL_PEN){
					idMarkerpenPopSizeBar.setProgress(AnnotDefaultConfig.markerPenSize_ink /2 - 1);
				}else{
					idMarkerpenPopSizeBar.setProgress((AnnotDefaultConfig.markerPenSize_ink - 15)/5 - 1);
				}
				idMarkerpenPopSizeValue.setText(AnnotDefaultConfig.markerPenSize_ink + "");
				break;
			default:
		}

		idMarkerpenPopAlphaBar.setProgress(alpha);
		idMarkerpenPopAlphaValue.setText(alpha + "%");
	}

    /**
     * @param ：[]
     * @return : void
     * @methodName ：changeVisibility created by liujiyuan on 2018/8/21 上午11:19.
     * @description ：根据类型控制popupWindow的控件
     */
	private void changeVisibility() {
		switch (AnnotDefaultConfig.markerPenType){
			case HIGH_LIGHT:
			case UNDER_LINE:
			case STRIK_EOUT:
				idMarkerpenPopSineCurve.setVisibility(View.GONE);
				idMarkerpenPopSizeLayout.setVisibility(View.GONE);
				idMarkerpenPopStyleIvLayout.setVisibility(View.GONE);
				idMarkerpenPopStyleArrowLayout.setVisibility(View.GONE);
				break;
			case INK:
				idMarkerpenPopSineCurve.setVisibility(View.VISIBLE);
				idMarkerpenPopSizeLayout.setVisibility(View.VISIBLE);
				idMarkerpenPopStyleIvLayout.setVisibility(View.VISIBLE);
				idMarkerpenPopStyleArrowLayout.setVisibility(View.VISIBLE);
				break;
			default:
		}
	}


    /**
     * @param ：[]
     * @return : void
     * @methodName ：changeDrawType created by liujiyuan on 2018/8/21 上午11:19.
     * @description ：转换画笔类型：normal pen or marker pen
     */
	private void changeDrawType(){
		if(AnnotDefaultConfig.markerPenType == AnnotDefaultConfig.MarkerPenType.INK){
			if(AnnotDefaultConfig.drawType == AnnotDefaultConfig.DrawType.NORMAL_PEN){
				idMarkerpenPopNormalPenArrow.setVisibility(View.VISIBLE);
				idMarkerpenPopMarkerPenArrow.setVisibility(View.GONE);
			}else{
				idMarkerpenPopNormalPenArrow.setVisibility(View.GONE);
				idMarkerpenPopMarkerPenArrow.setVisibility(View.VISIBLE);
			}
		}else{
			idMarkerpenPopNormalPenArrow.setVisibility(View.GONE);
			idMarkerpenPopMarkerPenArrow.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onClickListener(View view) {
		int lineWidth = 1;
		switch (view.getId()){
			case R.id.id_markerpen_pop_normal_pen:
				lineWidth = (idMarkerpenPopSizeBar.getProgress() + 1) * 2;
				AnnotDefaultConfig.drawType = AnnotDefaultConfig.DrawType.NORMAL_PEN;
				break;
			case R.id.id_markerpen_pop_marker_pen:
				lineWidth = (idMarkerpenPopSizeBar.getProgress() + 1) * 5 + 15;
				AnnotDefaultConfig.drawType = AnnotDefaultConfig.DrawType.MARKER_PEN;
				break;
			default:
				return;
		}
		idMarkerpenPopSineCurve.setLineWidth(lineWidth);
		idMarkerpenPopSineCurve.invalidate();
		changeDrawType();
		idMarkerpenPopSizeValue.setText(AnnotDefaultConfig.markerPenSize_ink + "");
		switch (AnnotDefaultConfig.markerPenType){
			case INK:
				AnnotDefaultConfig.markerPenSize_ink = lineWidth;
				break;
			default:
		}

		if(onMarkerPenAttrChangeListener != null){
			onMarkerPenAttrChangeListener.markerPenAttrChanged();
		}
		setMarkupAnnotationAttribution();
	}

	/**
	 * @methodName：setMarkupAnnotationAttribution created by liujiyuan on 2018/9/17 下午5:22.
	 * @description：通过eventBus修改markup的属性值到文档
	 */
	private void setMarkupAnnotationAttribution(){
		KMPDFAnnotationBean annotationBean;
		switch (AnnotDefaultConfig.markerPenType){
			case HIGH_LIGHT:
				annotationBean = new KMPDFHighlightAnnotationBean("", AnnotDefaultConfig.markerPenColor_hightlight, AnnotDefaultConfig.markerPenAlpha_hightlight);
				break;
			case STRIK_EOUT:
				annotationBean = new KMPDFStrikeoutAnnotationBean("", AnnotDefaultConfig.markerPenColor_strikeout, AnnotDefaultConfig.markerPenAlpha_strikeout);
				break;
			case UNDER_LINE:
				annotationBean = new KMPDFUnderlineAnnotationBean("", AnnotDefaultConfig.markerPenColor_underline, AnnotDefaultConfig.markerPenAlpha_underline);
				break;
			case INK:
				annotationBean = new KMPDFInkAnnotationBean("",AnnotDefaultConfig.markerPenColor_ink, AnnotDefaultConfig.markerPenSize_ink, AnnotDefaultConfig.markerPenAlpha_ink);
				break;
			default:
				return;
		}
		EventBusUtils.getInstance().post(new MessageEvent<>(ConstantBus.SET_MARKUP_ANNOTATION_ATTR, annotationBean));
	}

	@Override
	public void showAtLocation(View parent, int gravity, int x, int y) {
		changeVisibility();
		setConfigValue();
		changeDrawType();
		setColorAdapterSelect();
		colorSelectAdapter.notifyDataSetChanged();
		super.showAtLocation(parent, gravity, x, y);
	}

	@Override
	public PopupWindowStruct setCallback(OnPopupWindowCallback callback) {
		this.onMarkerPenAttrChangeListener = (OnMarkerPenAttrChangeListener)callback;
		return null;
	}

	@Override
	public PopupWindowStruct setObject(Object object) {
		return null;
	}

	@Override
	public void show(int type){
		showAtLocation(rootview, Gravity.BOTTOM, 0, 0);
	}
}
