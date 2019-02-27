package com.kdanmobile.pdfviewer.utils.glide;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

/**
 * @classname：MyAppGlideModule
 * @author：luozhipeng
 * @date：11/9/18 09:57
 * @description：
 */
@GlideModule
public class MyAppGlideModule extends AppGlideModule {

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        /*内存缓存池*/
        MemorySizeCalculator cache_calculator = new MemorySizeCalculator.Builder(context)
                .setMemoryCacheScreens(2)
                .build();
        builder.setMemoryCache(new LruResourceCache(cache_calculator.getMemoryCacheSize()));

        /*磁盘缓存*/
        int diskCacheSizeBytes = 1024 * 1024 * 100;//100 MB
        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, diskCacheSizeBytes));

        /*Bitmap 池*/
        MemorySizeCalculator bitmap_calculator = new MemorySizeCalculator.Builder(context)
                .setBitmapPoolScreens(3)
                .build();
        builder.setBitmapPool(new LruBitmapPool(bitmap_calculator.getBitmapPoolSize()));

        builder.setDefaultRequestOptions(
                new RequestOptions()
                        .format(DecodeFormat.PREFER_RGB_565)
                        .disallowHardwareConfig());
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
