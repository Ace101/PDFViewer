package com.kdanmobile.pdfviewer.screenui.reader.utils;


import com.google.gson.reflect.TypeToken;
import com.kdanmobile.kmpdfkit.annotation.stamp.TextStampConfig;
import com.kdanmobile.pdfviewer.screenui.reader.configs.KMReaderConfigs;
import com.kdanmobile.pdfviewer.screenui.reader.view.been.ImageItem;
import com.kdanmobile.pdfviewer.utils.GsonUtil;
import com.kdanmobile.pdfviewer.utils.sputils.SharedPreferencesSava;
import com.kdanmobile.pdfviewer.utils.sputils.SpUtils;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @classname：KMReaderSpUtils
 * @author：liujiyuan
 * @date：2018/9/5 上午10:29
 * @description：reader sharePreference 本地保存统一管理
 */
public class KMReaderSpUtils {
    private final static class SingleTon {
        private final static KMReaderSpUtils instance = new KMReaderSpUtils();
    }

    public static KMReaderSpUtils getInstance() {
        return KMReaderSpUtils.SingleTon.instance;
    }

    /**
     * @methodName：saveTextStampMessage created by liujiyuan on 2018/9/5 上午10:41.
     * @description：保存text stamp的数据到本地
     */
    public synchronized void saveTextStampMessage(List<TextStampConfig> stampConfigList) {
        try {
            Type type = new TypeToken<List<TextStampConfig>>() {
            }.getType();
            String json_str = GsonUtil.objectToJson(stampConfigList, type);
            String key = String.format("%s_textStamp", SpUtils.getInstance().onGetUserUUID());
            SharedPreferencesSava.getInstance().savaStringValue(SharedPreferencesSava.DEFAULT_SPNAME, key, json_str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @methodName：getTextStampMessage created by liujiyuan on 2018/9/5 下午3:57.
     * @description：得到text stamp的本地数据
     */
    public List<TextStampConfig> getTextStampMessage() {

        try {
            String key = String.format("%s_textStamp", SpUtils.getInstance().onGetUserUUID());
            String json_str = SharedPreferencesSava.getInstance().getStringValue(SharedPreferencesSava.DEFAULT_SPNAME, key, "");
            Type type = new TypeToken<List<TextStampConfig>>() {
            }.getType();
            List<TextStampConfig> stampConfigList = (List<TextStampConfig>) GsonUtil.jsonToList(json_str, type);
            return stampConfigList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * @methodName：saveImageStampMessage created by liujiyuan on 2018/9/6 上午9:28.
     * @description：保存image stamp的数据到本地
     */
    public synchronized void saveImageStampMessage(List<ImageItem> stampConfigList) {
        try {
            Type type = new TypeToken<List<ImageItem>>() {
            }.getType();
            String json_str = GsonUtil.objectToJson(stampConfigList, type);
            String key = String.format("%s_imageStamp", SpUtils.getInstance().onGetUserUUID());
            SharedPreferencesSava.getInstance().savaStringValue(SharedPreferencesSava.DEFAULT_SPNAME, key, json_str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @methodName：getImageStampMessage created by liujiyuan on 2018/9/6 上午9:29.
     * @description：得到image stamp的本地数据
     */
    public List<ImageItem> getImageStampMessage() {
        try {
            String key = String.format("%s_imageStamp", SpUtils.getInstance().onGetUserUUID());
            String json_str = SharedPreferencesSava.getInstance().getStringValue(SharedPreferencesSava.DEFAULT_SPNAME, key, "");
            Type type = new TypeToken<List<ImageItem>>() {
            }.getType();
            List<ImageItem> stampConfigList = (List<ImageItem>) GsonUtil.jsonToList(json_str, type);
            checkImageExist(stampConfigList);
            return stampConfigList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * @methodName：saveImageSignMessage created by liujiyuan on 2018/9/26 下午2:54.
     * @description：保存image sign的数据到本地
     */
    public synchronized void saveImageSignMessage(List<ImageItem> signImageList) {
        try {
            Type type = new TypeToken<List<ImageItem>>() {
            }.getType();
            String json_str = GsonUtil.objectToJson(signImageList, type);
            String key = String.format("%s_imageSign", SpUtils.getInstance().onGetUserUUID());
            SharedPreferencesSava.getInstance().savaStringValue(SharedPreferencesSava.DEFAULT_SPNAME, key, json_str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @methodName：getImageSignMessage created by liujiyuan on 2018/9/26 下午2:59.
     * @description：得到image sign的本地数据
     */
    public List<ImageItem> getImageSignMessage() {
        try {
            String key = String.format("%s_imageSign", SpUtils.getInstance().onGetUserUUID());
            String json_str = SharedPreferencesSava.getInstance().getStringValue(SharedPreferencesSava.DEFAULT_SPNAME, key, "");
            Type type = new TypeToken<List<ImageItem>>() {
            }.getType();
            List<ImageItem> signImageList = (List<ImageItem>) GsonUtil.jsonToList(json_str, type);
            checkImageExist(signImageList);
            return signImageList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * @methodName：checkImageExist created by liujiyuan on 2018/9/26 下午3:26.
     * @description：检查列表中的文件是否存在，不存在则移除
     */
    private void checkImageExist(List<ImageItem> imageList){
        for(ImageItem item:imageList){
            File file = new File(item.filePath);
            if(!file.exists()){
                imageList.remove(item);
            }
        }
    }

    /**
     * @methodName：getPageTurn created by liujiyuan on 2018/9/6 下午8:26.
     * @description：得到翻页设置的值
     */
    public String getPageTurn(){
        String key = String.format("%s_page_turning", SpUtils.getInstance().onGetUserUUID());
        return SharedPreferencesSava.getInstance().getStringValue(SharedPreferencesSava.DEFAULT_SPNAME, key, "");
    }

    /**
     * @methodName：savePageTurn created by liujiyuan on 2018/9/6 下午8:26.
     * @description：存储翻页设置的值
     */
    public void savePageTurn(String pageTurn){
        String key = String.format("%s_page_turning", SpUtils.getInstance().onGetUserUUID());
        SharedPreferencesSava.getInstance().savaStringValue(SharedPreferencesSava.DEFAULT_SPNAME, key, pageTurn);
    }

    /**
     * @methodName：getPageTurnIndex created by liujiyuan on 2018/9/6 下午8:27.
     * @description：得到翻页设置的值索引
     */
    public int getPageTurnIndex(){
        String key = String.format("%s_page_turning_index", SpUtils.getInstance().onGetUserUUID());
        return SharedPreferencesSava.getInstance().getIntValue(SharedPreferencesSava.DEFAULT_SPNAME, key, -1);
    }

    /**
     * @methodName：savePageTurnIndex created by liujiyuan on 2018/9/6 下午8:27.
     * @description：存储翻页设置的值索引
     */
    public void savePageTurnIndex(int index){
        String key = String.format("%s_page_turning_index", SpUtils.getInstance().onGetUserUUID());
        SharedPreferencesSava.getInstance().savaIntValue(SharedPreferencesSava.DEFAULT_SPNAME, key, index);
    }

    /**
     * @methodName：getBrightness created by liujiyuan on 2018/9/11 下午4:42.
     * @description：得到设置的应用屏幕亮度
     */
    public float getBrightness(){
        String key = String.format("%s_reader_brightness", SpUtils.getInstance().onGetUserUUID());
        return SharedPreferencesSava.getInstance().getFloatValue(SharedPreferencesSava.DEFAULT_SPNAME, key, 0.6f);
    }

    /**
     * @methodName：saveBrightness created by liujiyuan on 2018/9/11 下午4:42.
     * @description：保存设置的应用屏幕亮度
     */
    public void saveBrightness(float brightness){
        String key = String.format("%s_reader_brightness", SpUtils.getInstance().onGetUserUUID());
        SharedPreferencesSava.getInstance().savaFloatValue(SharedPreferencesSava.DEFAULT_SPNAME, key, brightness);
    }

    /**
     * @methodName：getScreenLock created by liujiyuan on 2018/9/11 下午4:43.
     * @description：得到是否设置了屏幕水平锁定
     */
    public boolean getScreenLock(){
        String key = String.format("%s_reader_screenLock", SpUtils.getInstance().onGetUserUUID());
        return  SharedPreferencesSava.getInstance().getBooleanValue(SharedPreferencesSava.DEFAULT_SPNAME, key, false);
    }

    /**
     * @methodName：saveScreenLock created by liujiyuan on 2018/9/11 下午4:43.
     * @description：保存是否设置了屏幕水平锁定
     */
    public void saveScreenLock(boolean isLocked){
        String key = String.format("%s_reader_screenLock", SpUtils.getInstance().onGetUserUUID());
        SharedPreferencesSava.getInstance().savaBooleanValue(SharedPreferencesSava.DEFAULT_SPNAME, key, isLocked);
    }

    /**
     * @methodName：getReaderOrientation created by liujiyuan on 2018/9/11 下午4:43.
     * @description：得到屏幕锁定的值：横屏或竖屏
     */
    public int getReaderOrientation(){
        String key = String.format("%s_reader_orientation", SpUtils.getInstance().onGetUserUUID());
        return SharedPreferencesSava.getInstance().getIntValue(SharedPreferencesSava.DEFAULT_SPNAME, key, KMReaderConfigs.PORTRAIT);
    }

    /**
     * @methodName：saveReaderOrientation created by liujiyuan on 2018/9/11 下午4:44.
     * @description：保存屏幕锁定的值：横屏或竖屏
     */
    public void saveReaderOrientation(int orientation){
        String key = String.format("%s_reader_orientation", SpUtils.getInstance().onGetUserUUID());
        SharedPreferencesSava.getInstance().savaIntValue(SharedPreferencesSava.DEFAULT_SPNAME, key, orientation);
    }

    /**
     * @methodName：getFirstHighLightUsed created by liujiyuan on 2018/9/27 下午1:54.
     * @description：获取是否是第一次打开reader界面的HighLight工具栏
     */
    public boolean getFirstHighLightUsed(){
        String key = String.format("%s_reader_firstHighLightUsed", SpUtils.getInstance().onGetUserUUID());
        return  SharedPreferencesSava.getInstance().getBooleanValue(SharedPreferencesSava.DEFAULT_SPNAME, key, true);
    }

    /**
     * @methodName：setFirstHighLightUsed created by liujiyuan on 2018/9/27 下午1:55.
     * @description：保存是第一次打开reader界面的HighLight工具栏
     */
    public void setNoFirstHighLightUsed(){
        String key = String.format("%s_reader_firstHighLightUsed", SpUtils.getInstance().onGetUserUUID());
        SharedPreferencesSava.getInstance().savaBooleanValue(SharedPreferencesSava.DEFAULT_SPNAME, key, false);
    }

    /**
     * @methodName：getFirstUnderLineUsed created by liujiyuan on 2018/9/27 下午1:54.
     * @description：获取是否是第一次打开reader界面的UnderLine工具栏
     */
    public boolean getFirstUnderLineUsed(){
        String key = String.format("%s_reader_firstUnderLineUsed", SpUtils.getInstance().onGetUserUUID());
        return  SharedPreferencesSava.getInstance().getBooleanValue(SharedPreferencesSava.DEFAULT_SPNAME, key, true);
    }

    /**
     * @methodName：setFirstUnderLineUsed created by liujiyuan on 2018/9/27 下午1:55.
     * @description：保存是第一次打开reader界面的UnderLine工具栏
     */
    public void setNoFirstUnderLineUsed(){
        String key = String.format("%s_reader_firstUnderLineUsed", SpUtils.getInstance().onGetUserUUID());
        SharedPreferencesSava.getInstance().savaBooleanValue(SharedPreferencesSava.DEFAULT_SPNAME, key, false);
    }

    /**
     * @methodName：getFirstStrickUsed created by liujiyuan on 2018/9/27 下午1:54.
     * @description：获取是否是第一次打开reader界面的Strick工具栏
     */
    public boolean getFirstStrickUsed(){
        String key = String.format("%s_reader_firstStrickUsed", SpUtils.getInstance().onGetUserUUID());
        return  SharedPreferencesSava.getInstance().getBooleanValue(SharedPreferencesSava.DEFAULT_SPNAME, key, true);
    }

    /**
     * @methodName：setFirstStrickUsed created by liujiyuan on 2018/9/27 下午1:55.
     * @description：保存是第一次打开reader界面的Strick工具栏
     */
    public void setNoFirstStrickUsed(){
        String key = String.format("%s_reader_firstStrickUsed", SpUtils.getInstance().onGetUserUUID());
        SharedPreferencesSava.getInstance().savaBooleanValue(SharedPreferencesSava.DEFAULT_SPNAME, key, false);
    }

    /**
     * @methodName：getFirstShapeUsed created by liujiyuan on 2018/9/27 下午1:54.
     * @description：获取是否是第一次打开reader界面的Shape工具栏
     */
    public boolean getFirstShapeUsed(){
        String key = String.format("%s_reader_firstShapeUsed", SpUtils.getInstance().onGetUserUUID());
        return  SharedPreferencesSava.getInstance().getBooleanValue(SharedPreferencesSava.DEFAULT_SPNAME, key, true);
    }

    /**
     * @methodName：setFirstShapeUsed created by liujiyuan on 2018/9/27 下午1:55.
     * @description：保存是第一次打开reader界面的Shape工具栏
     */
    public void setNoFirstShapetUsed(){
        String key = String.format("%s_reader_firstShapeUsed", SpUtils.getInstance().onGetUserUUID());
        SharedPreferencesSava.getInstance().savaBooleanValue(SharedPreferencesSava.DEFAULT_SPNAME, key, false);
    }

    /**
     * @methodName：getFirstPageEditUsed created by liujiyuan on 2018/10/12 下午1:38.
     * @description：获取是否是第一次打开PageEdit界面
     */
    public boolean getFirstPageEditUsed(){
        String key = String.format("%s_reader_firstPageEditUsed", SpUtils.getInstance().onGetUserUUID());
        return  SharedPreferencesSava.getInstance().getBooleanValue(SharedPreferencesSava.DEFAULT_SPNAME, key, true);
    }

    /**
     * @methodName：setNoFirstPageEditUsed created by liujiyuan on 2018/10/12 下午1:38.
     * @description：保存第一次打开PageEdit界面的记录
     */
    public void setNoFirstPageEditUsed(){
        String key = String.format("%s_reader_firstPageEditUsed", SpUtils.getInstance().onGetUserUUID());
        SharedPreferencesSava.getInstance().savaBooleanValue(SharedPreferencesSava.DEFAULT_SPNAME, key, false);
    }

    /**
     * @methodName：saveDocumentPage created by liujiyuan on 2018/9/29 上午9:56.
     * @description：保存当前文档的阅读页码
     */
    public void saveDocumentPage(String path, int page){
        String keyWord = "%s_"+path;
        String key = String.format(keyWord, SpUtils.getInstance().onGetUserUUID());
        SharedPreferencesSava.getInstance().savaIntValue(SharedPreferencesSava.DEFAULT_SPNAME, key, page);
    }

    /**
     * @methodName：getDocumentPage created by liujiyuan on 2018/9/29 上午9:56.
     * @description：得到该文档上一次阅读的页码
     */
    public int getDocumentPage(String path){
        String keyWord = "%s_"+path;
        String key = String.format(keyWord, SpUtils.getInstance().onGetUserUUID());
        return SharedPreferencesSava.getInstance().getIntValue(SharedPreferencesSava.DEFAULT_SPNAME, key, 0);
    }

}