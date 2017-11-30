package com.fanyafeng.imagepicker.photocompressionutil;

import android.util.Log;

import java.io.File;
import java.util.concurrent.CountDownLatch;

/**
 * Author： fanyafeng
 * Date： 17/11/29 上午9:58
 * Email: fanyafeng@live.cn
 */
public class CompressPicture implements Runnable {
    private final static String TAG = CompressPicture.class.getSimpleName();

    /**
     * 计时器，辅助类
     * <p>
     * 在完成一组正在其他线程中执行的操作之前
     * 允许一个或多个线程一直等待
     */
    private CountDownLatch countDownLatch;

    //文件名称
    private String fileName;

    private OnThreadResultListener onThreadResultListener;

    private CompressPicture() {
    }

    public CompressPicture(CountDownLatch countDownLatch, String fileName, OnThreadResultListener onThreadResultListener) {
        this.countDownLatch = countDownLatch;
        this.fileName = fileName;
        this.onThreadResultListener = onThreadResultListener;
    }

    @Override
    public void run() {

        try {
            if (fileName != null)
                Log.e(TAG, fileName);
            CompressPictureUtil.getInstance().thirdCompress(new File(fileName), new OnCompressListener() {
                @Override
                public void onStart() {
                    onThreadResultListener.onStart();
                }

                @Override
                public void onSuccess(String dealPicturePath) {
                    countDownLatch.countDown();
                    onThreadResultListener.onFinish(dealPicturePath);
                }

                @Override
                public void onError(Throwable e) {
                    onThreadResultListener.onInterrupted();
                }
            });

        } catch (Exception e) {
            onThreadResultListener.onInterrupted();
        }
    }
}
