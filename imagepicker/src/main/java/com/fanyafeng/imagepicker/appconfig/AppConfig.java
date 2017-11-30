package com.fanyafeng.imagepicker.appconfig;

import android.app.Application;
import android.graphics.Bitmap;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.internal.Supplier;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

/**
 * Author： fanyafeng
 * Date： 17/11/16 下午3:18
 * Email: fanyafeng@live.cn
 */
public class AppConfig extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        final MemoryCacheParams bitmapCacheParams = new MemoryCacheParams(
                (int) Runtime.getRuntime().maxMemory() / 4, // Max total size of elements in the cache
                Integer.MAX_VALUE,                     // Max entries in the cache
                (int) Runtime.getRuntime().maxMemory() / 4, // Max total size of elements in eviction queue
                Integer.MAX_VALUE,                     // Max length of eviction queue
                Integer.MAX_VALUE);                    // Max cache entry size

        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(this)
                .setBaseDirectoryPath(getCacheDir())
                .setBaseDirectoryName("images")
                .setMaxCacheSize(40 * 1024 * 1024)//40MB
                .build();
        ImagePipelineConfig imagePipelineConfig = ImagePipelineConfig.newBuilder(this)
                .setMainDiskCacheConfig(diskCacheConfig)
                .setBitmapMemoryCacheParamsSupplier(new Supplier<MemoryCacheParams>() {
                    @Override
                    public MemoryCacheParams get() {
                        return bitmapCacheParams;
                    }
                })
                .setDownsampleEnabled(true)
                .setResizeAndRotateEnabledForNetwork(true)
                .setBitmapsConfig(Bitmap.Config.RGB_565)
                .build();
        Fresco.initialize(this, imagePipelineConfig);
    }
}
