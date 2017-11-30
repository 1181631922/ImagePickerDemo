package com.fanyafeng.imagepicker.photocompressionutil;

import java.util.concurrent.CountDownLatch;

/**
 * Author： fanyafeng
 * Date： 17/11/29 上午10:31
 * Email: fanyafeng@live.cn
 */
public class CompressListener implements Runnable {
    private CountDownLatch countDownLatch;
    private OnAllThreadResultListener onAllThreadResultListener;

    private CompressListener() {
    }

    public CompressListener(CountDownLatch countDownLatch, OnAllThreadResultListener onAllThreadResultListener) {
        this.countDownLatch = countDownLatch;
        this.onAllThreadResultListener = onAllThreadResultListener;
    }

    @Override
    public void run() {
        try {
            countDownLatch.await();
            onAllThreadResultListener.onSuccess();
        } catch (InterruptedException e) {
            onAllThreadResultListener.onFailed();
        }
    }
}
