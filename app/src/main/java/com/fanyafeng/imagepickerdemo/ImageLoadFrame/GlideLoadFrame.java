package com.fanyafeng.imagepickerdemo.ImageLoadFrame;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.fanyafeng.imagepicker.imageloadframe.ImageLoadFrame;

import java.io.File;

/**
 * Author： fanyafeng
 * Date： 17/11/29 下午7:13
 * Email: fanyafeng@live.cn
 */
public class GlideLoadFrame implements ImageLoadFrame{
    @Override
    public boolean isFresco() {
        return false;
    }

    @Override
    public void displayImage(Context context, String imagePath, View view, int width, int height) {
        Glide.with(context)
                .load(Uri.fromFile(new File(imagePath)))
                .into((ImageView) view);
    }

    @Override
    public void clearMemoryCache(Context context) {
        Glide.get(context).clearMemory();
    }
}
