package com.kdanmobile.pdfviewer.utils.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.kdanmobile.pdfviewer.utils.threadpools.ThreadPoolUtils;

/**
 * @classname：GlideLoadUtil
 * @author：luozhipeng
 * @date：11/9/18 10:38
 * @description： Glide图片加载工具类；有其他需求后期添加
 */
public class GlideLoadUtil {

    /**
     * @param ：[c, url, target]
     * @return : void
     * @methodName ：loadSourseImgWithNoCache created by luozhipeng on 11/9/18 10:47.
     * @description ：加载图片不需要缓存的
     */
    public static void loadSourseImgWithNoCache(Context c, String url, ImageView target) {
        GlideApp.with(c)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .centerCrop()
                .transition(new DrawableTransitionOptions().crossFade(200))
                .into(target);
    }

    public static void LoadGiftAsGistKeepFidelity(Context context, int url, ImageView imageView, int erroId) {
        GlideApp.with(context)
                .asGif()
                .load(url)
                .format(DecodeFormat.PREFER_ARGB_8888)
                .error(erroId)
                .into(imageView);
    }

    /**
     * @param ：[c, resourceId, target, defaultId]
     * @return : void
     * @methodName ：loadResourseImg created by luozhipeng on 11/9/18 10:47.
     * @description ：根据资源ID加载图片
     */
    public static void loadResourseImg(Context c, int resourceId, ImageView target, int defaultId) {
        GlideApp.with(c)
                .load(resourceId)
                .placeholder(defaultId)
                .transition(new DrawableTransitionOptions().crossFade(200))
                .centerCrop()
                .into(target);
    }

    /**
     * @param ：[c, imgFile, target, defaultId]
     * @return : void
     * @methodName ：loadFileImg created by luozhipeng on 11/9/18 10:47.
     * @description ：根据图片路径加载图片
     */
    public static void loadStampImg(Context c, String imgFilePath, ImageView target, int width, int height) {
        GlideApp.with(c)
                .load(imgFilePath)
                .transition(new DrawableTransitionOptions().crossFade(200))
                .centerCrop()
                .skipMemoryCache(true)
                .override(width, height)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(target);
    }

    /**
     * @param ：[context, url, imageView]
     * @return : void
     * @methodName ：LoadGiftAsBitmap created by luozhipeng on 11/9/18 10:47.
     * @description ：加载Gif为一张静态图片
     */
    public static void LoadGiftAsBitmap(Context context, String url, ImageView imageView) {
        GlideApp.with(context).asBitmap().load(url).into(imageView);
    }

    /**
     * @param ：[context, url, imageView, erroId]
     * @return : void
     * @methodName ：LoadGiftAsGist created by luozhipeng on 11/9/18 10:48.
     * @description ：你想只有加载对象是Gif时才能加载成功
     */
    public static void LoadGiftAsGist(Context context, String url, ImageView imageView, int erroId) {
        GlideApp.with(context).asGif().load(url).error(erroId).into(imageView);
    }

    /**
     * @param ：[fragment, url, imageView]
     * @return : void
     * @methodName ：LoadThumbNail created by luozhipeng on 11/9/18 10:48.
     * @description ：加载缩略图,会自动与传入的fragment绑定生命周期,加载请求现在会自动在onStop中暂停在，onStart中重新开始。
     * 需要保证 ScaleType 的设置是正确的。
     */
    public static void LoadThumbNail(Fragment fragment, String url, ImageView imageView) {
        GlideApp.with(fragment).load(url).thumbnail(0.1f).into(imageView);
    }

    /**
     * @param ：[context, url, target, placeholder_resId, error_resId]
     * @return : void
     * @methodName ：loadRoundImg created by luozhipeng on 11/9/18 10:48.
     * @description ：加载圆形头像;如果是activity glide会与其生命周期关联,在onStop()中取消加载图片,如果
     * 想要始终加载图片则需要传入Application实例
     */
    public static void loadRoundImg(Context context, String url, ImageView target, int placeholder_resId, int error_resId) {
        //https://github.com/wasabeef/glide-transformations--glide转换库
        GlideApp.with(context)
                .load(url)
                .placeholder(placeholder_resId)
                .error(error_resId)
                .circleCrop()//直接在链式中调用就行哦
                .transition(new DrawableTransitionOptions().crossFade(1000))//渐显效果
                .into(target);
    }

    /**
     * @param ：[context, url]
     * @return : android.graphics.Bitmap
     * @methodName ：downLoadImage created by luozhipeng on 11/9/18 10:49.
     * @description ：下载图片,耗时操作不能放在主线程中进行
     */
    public static Bitmap downLoadImage(Context context, String url) {
        try {
            return GlideApp.with(context).asBitmap().load(url).centerCrop().listener(new RequestListener<Bitmap>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                    return false;
                }
            }).submit().get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param ：[context]
     * @return : void
     * @methodName ：clearCache created by luozhipeng on 11/9/18 10:49.
     * @description ：清除缓存
     */
    public static void clearCache(final Context context) {
        clearMemoryCache(context);
        ThreadPoolUtils.getInstance().execute(() -> clearDiskCache(context));
    }

    /**
     * @param ：[context]
     * @return : void
     * @methodName ：clearMemoryCache created by luozhipeng on 11/9/18 10:49.
     * @description ：清除内存缓存
     */
    public static void clearMemoryCache(Context context) {
        GlideApp.get(context).clearMemory();
    }

    /**
     * @param ：[context]
     * @return : void
     * @methodName ：clearDiskCache created by luozhipeng on 11/9/18 10:50.
     * @description ：清除磁盘缓存
     */
    public static void clearDiskCache(Context context) {
        GlideApp.get(context).clearDiskCache();
    }
}
