package com.kdanmobile.pdfviewer.screenui.reader.utils;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;


/**
 * @classname：JsonUtil
 * @author：liujiyuan
 * @date：2018/8/28 上午9:42
 * @description：json数据解析类
 */
public class JsonUtil {
    private static final int ADD_ZERO = 10;

    /**
     * @param ：[json]
     * @return : java.lang.String
     * @methodName ：BookmarkTimeToString created by liujiyuan on 2018/8/28 上午9:43.
     * @description ：传回来的json格式的时间转化为想要的日期数据例如：2018/08/28 09：43
     */
    public static String BookmarkTimeToString(String json){
        if(json == null){
            return "";
        }
        /****** 创建json解析器 ******/
        JsonParser parse =new JsonParser();
        try {
            StringBuffer result;
            /****** 创建jsonObject对象 ******/
            JsonObject jsonObj = (JsonObject) parse.parse(json);

            String  year, month, date, hour, minute;
            year = jsonObj.get("year").getAsString();
            month = addZero(jsonObj.get("month").getAsString());
            date = addZero(jsonObj.get("date").getAsString());
            hour = addZero(jsonObj.get("hour").getAsString());
            minute = addZero(jsonObj.get("minute").getAsString());

            result = new StringBuffer(year);
            result.append("/").append(month).append("/").append(date).append(" ").append(hour).append(":").append(minute);
            return result.toString();
        } catch (JsonIOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (ClassCastException e){
            e.printStackTrace();
        }
        return null;
    }

    private static String addZero(String str){
        int value = Integer.valueOf(str);
        if(value < ADD_ZERO){
            return new StringBuffer("0").append(str).toString();
        }else{
            return str;
        }
    }
}
