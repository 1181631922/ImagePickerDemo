package com.fanyafeng.imagepickerdemo.ImageLoadFrame;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.fanyafeng.imagepicker.imageloadframe.ImageLoadFrame;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Author： fanyafeng
 * Date： 17/11/29 下午7:13
 * Email: fanyafeng@live.cn
 */
public class PicassoLoadFrame implements ImageLoadFrame{
    @Override
    public boolean isFresco() {
        return false;
    }

    @Override
    public void displayImage(Context context, String imagePath, View view, int width, int height) {
        Picasso.with(context)//
                .load(Uri.fromFile(new File(imagePath)))//
                .resize(width, height)//
                .centerInside()//
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)//
                .into((ImageView) view);
    }

    @Override
    public void clearMemoryCache(Context context) {

    }
}
