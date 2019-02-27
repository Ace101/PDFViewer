package com.kdanmobile.pdfviewer.screenui.reader.view.popupwindow;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.kdanmobile.kmpdfkit.annotation.bean.KMPDFAnnotationBean;
import com.kdanmobile.kmpdfkit.annotation.bean.KMPDFFreetextAnnotationBean;
import com.kdanmobile.kmpdfkit.utlis.KMPDFFontUtil;
import com.kdanmobile.pdfviewer.R;
import com.kdanmobile.pdfviewer.base.BasePopupWindow;
import com.kdanmobile.pdfviewer.event.MessageEvent;
import com.kdanmobile.pdfviewer.screenui.reader.configs.AnnotDefaultConfig;
import com.kdanmobile.pdfviewer.screenui.reader.view.adapter.ColorSelectAdapter;
import com.kdanmobile.pdfviewer.screenui.reader.view.listener.OnPopupWindowCallback;
import com.kdanmobile.pdfviewer.screenui.reader.view.listener.PopupWindowStruct;
import com.kdanmobile.pdfviewer.screenui.reader.view.listener.SeekBarChangeListenerAbstract;
import com.kdanmobile.pdfviewer.utils.SimpleTabSelectedListener;
import com.kdanmobile.pdfviewer.utils.eventbus.ConstantBus;
import com.kdanmobile.pdfviewer.utils.eventbus.EventBusUtils;

/**
 * @classname：FreeTextPopupWindow
 * @author：liujiyuan
 * @date：2018/8/31 下午4:08
 * @description：freeText注释属性设置界面
 */
public class FreeTextPopupWindow extends BasePopupWindow implements PopupWindowStruct {

    private View rootview;

    private RelativeLayout idFreeTextPopupWindow;
    private TabLayout idFreeTextMenuTabLayout;

    private LinearLayout idFreeTextTypefaceLayout;
    private ImageButton idFreeTextTypefaceBlod;
    private ImageButton idFreeTextTypefaceItalic;
    private ListView idFreeTextTypefaceList;

    private RelativeLayout idFreeTextFontLayout;
    private RecyclerView idFreeTextFontColorChooseLv;
    private SeekBar idFreeTextFontAlphaBar;
    private TextView idFreeTextFontAlphaValue;
    private SeekBar idFreeTextFontSizeBar;
    private TextView idFreeTextFontSizeValue;
    private ColorSelectAdapter colorSelectAdapter;

    private boolean isBlod;
    private boolean isItalic;

    private static int typefaceSelectedIndex = 0;
    /****** 三种字体资源在pso 库中，只支持这三种字体 "Courier", "Helvetica", "Times-Roman"******/
    private static String[] typefaceArr = new String[3];
    private TypefaceAdapter typefaceAdapter;


    public FreeTextPopupWindow(Context context, View rootview) {
        super(context);
        setAnimationStyle(R.style.popwindow_anim_style);
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        this.rootview = rootview;
    }

    @Override
    protected View setLayout(LayoutInflater inflater) {
        return inflater.inflate(R.layout.popupwindow_freetext,null);
    }

    @Override
    protected void initResource() {
        /****** 设置TAB的比奥体 ******/
        String[] tablayout_title = new String[2];
        tablayout_title[0] = mContext.getString(R.string.freetext_typeface_tab);
        tablayout_title[1] = mContext.getString(R.string.freetext_font_tab);
        for (int i = 0; i < tablayout_title.length; i++) {
            idFreeTextMenuTabLayout.addTab(idFreeTextMenuTabLayout.newTab().setText(tablayout_title[i]));
        }
        /****** 设置三种字体名称的字串 ******/
        typefaceArr[0] = mContext.getString(R.string.freetext_typeface_name_one);
        typefaceArr[1] = mContext.getString(R.string.freetext_typeface_name_two);
        typefaceArr[2] = mContext.getString(R.string.freetext_typeface_name_three);
    }

    @Override
    protected void initListener() {
        /****** tab的监听 ******/
        idFreeTextMenuTabLayout.setOnClickListener(this);
        idFreeTextMenuTabLayout.addOnTabSelectedListener(new SimpleTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                setTabLayout(tab.getPosition());
            }
        });
        /****** 颜色选择适配器的监听事件 ******/
        colorSelectAdapter.setOnItemClickListener(position -> {
            AnnotDefaultConfig.color__freeText = AnnotDefaultConfig.colorArr[position];
            AnnotDefaultConfig.colorId_freeText = position;
            setFreeTextAttributes();
        });
        /****** 给各个seekBar控件添加监听器 ******/
        idFreeTextFontAlphaBar.setOnSeekBarChangeListener(new SeekBarChangeListenerAbstract(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                super.onProgressChanged(seekBar, progress, fromUser);
                idFreeTextFontAlphaValue.setText(progress+"%");
                AnnotDefaultConfig.alpha__freeText = (int)(progress * 255.0f / 100.0f + 0.5f);
                setFreeTextAttributes();
            }
        });
        idFreeTextFontSizeBar.setOnSeekBarChangeListener(new SeekBarChangeListenerAbstract(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                super.onProgressChanged(seekBar, progress, fromUser);
                idFreeTextFontSizeValue.setText(""+(progress + 1));
                AnnotDefaultConfig.textSize__freeText = progress + 1;
                setFreeTextAttributes();
            }
        });
        /****** 各个按钮的监听 ******/
        idFreeTextTypefaceBlod.setOnClickListener(this);
        idFreeTextTypefaceItalic.setOnClickListener(this);
        /****** 字体列表适配器的监听事件 ******/
        idFreeTextTypefaceList.setOnItemClickListener((adapterView, view, i, l) -> {
            typefaceSelectedIndex = i;
            AnnotDefaultConfig.typeface_freeText = getTypeface();
            AnnotDefaultConfig.typeface = KMPDFFontUtil.getInnerTypeface(mContext, AnnotDefaultConfig.typeface_freeText);
            typefaceAdapter.notifyDataSetChanged();
            setFreeTextAttributes();
        });
    }

    @Override
    protected void initView() {

        idFreeTextPopupWindow = mContentView.findViewById(R.id.id_freeText_popupWindow);
        idFreeTextMenuTabLayout = mContentView.findViewById(R.id.id_freeText_menu_tabLayout);

        idFreeTextTypefaceLayout = mContentView.findViewById(R.id.id_freeText_typeface_layout);
        idFreeTextTypefaceBlod = mContentView.findViewById(R.id.id_freeText_typeface_blod);
        idFreeTextTypefaceItalic = mContentView.findViewById(R.id.id_freeText_typeface_italic);
        idFreeTextTypefaceList = mContentView.findViewById(R.id.id_freeText_typeface_list);

        idFreeTextFontLayout = mContentView.findViewById(R.id.id_freeText_font_layout);
        idFreeTextFontColorChooseLv = mContentView.findViewById(R.id.id_freeText_font_color_choose_lv);
        idFreeTextFontAlphaBar = mContentView.findViewById(R.id.id_freeText_font_alpha_bar);
        idFreeTextFontAlphaValue = mContentView.findViewById(R.id.id_freeText_font_alpha_value);
        idFreeTextFontSizeBar = mContentView.findViewById(R.id.id_freeText_font_size_bar);
        idFreeTextFontSizeValue = mContentView.findViewById(R.id.id_freeText_font_size_value);

        /****** 初始化seekBar的值 ******/
        idFreeTextFontAlphaBar.setProgress((int)(AnnotDefaultConfig.alpha__freeText * (100.0 / 255.0) + 0.5f));
        idFreeTextFontAlphaValue.setText(String.valueOf((int)(AnnotDefaultConfig.alpha__freeText * (100.0 / 255.0) + 0.5f) + "%"));
        idFreeTextFontSizeBar.setProgress(AnnotDefaultConfig.textSize__freeText - 1);
        idFreeTextFontSizeValue.setText(String.valueOf(AnnotDefaultConfig.textSize__freeText));

        /****** 颜色选择适配器 ******/
        idFreeTextFontColorChooseLv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayout.HORIZONTAL, false));
        idFreeTextFontColorChooseLv.setItemAnimator(new DefaultItemAnimator());
        colorSelectAdapter = new ColorSelectAdapter(AnnotDefaultConfig.colorArr);
        colorSelectAdapter.setSelectIndex(AnnotDefaultConfig.colorId_freeText);
        idFreeTextFontColorChooseLv.setAdapter(colorSelectAdapter);

        /****** 字体列表适配器 ******/
        typefaceAdapter = new TypefaceAdapter(mContext, typefaceArr);
        idFreeTextTypefaceList.setAdapter(typefaceAdapter);

    }

    /**
     * @methodName：setTabLayout created by liujiyuan on 2018/9/18 下午2:09.
     * @description：设置tab的布局
     */
    private void setTabLayout(int position){
        switch (position) {
            case 0:
                idFreeTextTypefaceLayout.setVisibility(View.VISIBLE);
                idFreeTextFontLayout.setVisibility(View.GONE);
                break;
            case 1:
                idFreeTextTypefaceLayout.setVisibility(View.GONE);
                idFreeTextFontLayout.setVisibility(View.VISIBLE);
                break;
            default:
        }
    }

    @Override
    protected void onClickListener(View view) {
        switch (view.getId()){
            case R.id.id_freeText_typeface_blod:
                isBlod = !isBlod;
                AnnotDefaultConfig.isBold_freeText = !AnnotDefaultConfig.isBold_freeText;
                AnnotDefaultConfig.typeface_freeText = getTypeface();
                AnnotDefaultConfig.typeface = KMPDFFontUtil.getInnerTypeface(mContext, AnnotDefaultConfig.typeface_freeText);
                break;
            case R.id.id_freeText_typeface_italic:
                isItalic = !isItalic;
                AnnotDefaultConfig.isItalic_freeText = !AnnotDefaultConfig.isItalic_freeText;
                AnnotDefaultConfig.typeface_freeText = getTypeface();
                AnnotDefaultConfig.typeface = KMPDFFontUtil.getInnerTypeface(mContext, AnnotDefaultConfig.typeface_freeText);
                break;
            default:
        }
        changePictureOfTypefaceButton();
        setFreeTextAttributes();
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
        isBlod = AnnotDefaultConfig.isBold_freeText;
        isItalic = AnnotDefaultConfig.isItalic_freeText;
        changePictureOfTypefaceButton();
        idFreeTextMenuTabLayout.getTabAt(type).select();
        setTabLayout(type);
        showAtLocation(rootview, Gravity.BOTTOM, 0, 0);
    }

    /**
     * @methodName：setFreeTextAttributes created by liujiyuan on 2018/9/14 下午3:03.
     * @description：修改freeText的属性值
     */
    private void setFreeTextAttributes(){
        KMPDFAnnotationBean annotationBean = new KMPDFFreetextAnnotationBean("", AnnotDefaultConfig.color__freeText, AnnotDefaultConfig.textSize__freeText, AnnotDefaultConfig.alpha__freeText,
                AnnotDefaultConfig.typeface_freeText, AnnotDefaultConfig.isBold_freeText, AnnotDefaultConfig.isItalic_freeText);
        EventBusUtils.getInstance().post(new MessageEvent<>(ConstantBus.SET_ANNOTATION_ATTR, annotationBean));
    }


    /**
     * @param ：[isBlod, isItalic]
     * @return : void
     * @methodName ：changePictureOfTypefaceButton created by liujiyuan on 2018/9/3 上午11:39.
     * @description ：设置字体的衍生字体选择键的标示
     */
    private void changePictureOfTypefaceButton(){
        if(isBlod) {
            idFreeTextTypefaceBlod.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.font_ic_bold_sel));
        }else{
            idFreeTextTypefaceBlod.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.font_ic_bold_nor));
        }
        if(isItalic) {
            idFreeTextTypefaceItalic.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.font_ic_italic_sel));
        }else{
            idFreeTextTypefaceItalic.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.font_ic_italic_nor));
        }
    }

    /**
     * @methodName：getTypeface created by liujiyuan on 2018/9/3 上午11:26.
     * @description：得到最终衍生字体的类型
     */
    private String getTypeface(){
        switch (typefaceSelectedIndex){
            case 0:
                return "Courier";
            case 1:
                return "Helvetica";
            case 2:
                return "Times-Roman";
            default:
                return "Helvetica";
        }
    }

    static class TypefaceAdapter extends BaseAdapter {

        private String[] typeArr;

        private Context context;

        private LayoutInflater inflater;

        public TypefaceAdapter(Context context, String[] typeArr){
            this.context = context;
            this.typeArr = typeArr;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return typeArr.length;
        }

        @Override
        public Object getItem(int position) {
            return typeArr[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView == null){
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_typeface_list, null);
                viewHolder.typefaceTv = convertView.findViewById(R.id.id_typeface_list_name);
                viewHolder.selectedIv = convertView.findViewById(R.id.id_typeface_list_selected);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.typefaceTv.setText(typeArr[position]);
            if(typefaceSelectedIndex == position){
                viewHolder.selectedIv.setVisibility(View.VISIBLE);
            }else{
                viewHolder.selectedIv.setVisibility(View.GONE);
            }

            return convertView;
        }

        class ViewHolder{
            TextView typefaceTv;
            ImageView selectedIv;
        }
    }
}
