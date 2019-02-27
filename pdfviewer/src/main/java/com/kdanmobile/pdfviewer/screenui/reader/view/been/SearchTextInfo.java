package com.kdanmobile.pdfviewer.screenui.reader.view.been;

import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;

import com.kdanmobile.pdfviewer.R;
import com.kdanmobile.kmpdfkit.pdfcommon.TextWord;
import com.kdanmobile.pdfviewer.base.ProApplication;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @classname：SearchTextInfo
 * @author：liujiyuan
 * @date：2018/8/23 下午1:46
 * @description：
 */
public class SearchTextInfo {
    public int page;
    public RectF[] rf;
    public String search;
    public TextWord[] line;
    public SpannableStringBuilder stringBuilder;
    public boolean isHeader;

    public SearchTextInfo(int page, String search, RectF[] rf, TextWord[] line, boolean isHeader) {
        this.page = page;
        this.search = search;
        this.rf = rf;
        this.line = line;
        this.isHeader = isHeader;

        if(line == null){
            return;
        }

        String result = "";
        for (TextWord aLine : line) {
            result = result + aLine.w + " ";
        }
        this.stringBuilder = highlight(result.toLowerCase(), search.toLowerCase());
    }

    /**
     * 关键字高亮显示
     * @param target 需要高亮的关键字
     * @param text 需要显示的文字
     * @return spannable 处理完后的结果，记得不要toString()，否则没有效果
     */
    public SpannableStringBuilder highlight(String text, String target) {
        SpannableStringBuilder spannable = new SpannableStringBuilder(text);
        CharacterStyle span;
        Pattern p = Pattern.compile(target);
        Matcher m = p.matcher(text);
        int backGroundColor = ContextCompat.getColor(ProApplication.getContext(), R.color.search_result_text_highlight);
        while (m.find()) {
            /****** 需要重复 ******/
            span = new BackgroundColorSpan(backGroundColor);
            spannable.setSpan(span, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }
}
