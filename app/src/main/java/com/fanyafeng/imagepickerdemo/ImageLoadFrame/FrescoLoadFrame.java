package com.fanyafeng.imagepickerdemo.ImageLoadFrame;

import android.content.Context;
import android.net.Uri;
import android.view.View;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.fanyafeng.imagepicker.imageloadframe.ImageLoadFrame;

import java.io.File;

/**
 * Author： fanyafeng
 * Date： 17/11/29 下午7:13
 * Email: fanyafeng@live.cn
 */
public class FrescoLoadFrame implements ImageLoadFrame {
    @Override
    public boolean isFresco() {
        return true;
    }

    @Override
    public void displayImage(Context context, String imagePath, View view, int width, int height) {
        ImageRequest request = ImageRequestBuilder
                .newBuilderWithSource(Uri.fromFile(new File(imagePath)))
                .setResizeOptions(new ResizeOptions(width, width))
                .build();
        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder().setOldController(((SimpleDraweeView) view).getController()).setImageRequest(request)
                .setAutoPlayAnimations(true)
                .build();
        ((SimpleDraweeView) view).setController(controller);
    }

    @Override
    public void clearMemoryCache(Context context) {
        Fresco.getImagePipeline().clearMemoryCaches();
    }
}
