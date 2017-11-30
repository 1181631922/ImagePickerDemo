package com.fanyafeng.imagepicker.imageloadframe;

import android.content.Context;
import android.view.View;

import java.io.Serializable;

/**
 * Author： fanyafeng
 * Date： 17/11/16 下午3:17
 * Email: fanyafeng@live.cn
 */
public interface ImageLoadFrame extends Serializable {

    //小米手机会报错
    boolean isFresco();

    void displayImage(Context context, String imagePath, View view, int width, int height);

    void clearMemoryCache(Context context);
}
