package com.fanyafeng.imagepicker.photocompressionutil;

/**
 * Author： fanyafeng
 * Date： 17/11/29 上午9:52
 * Email: fanyafeng@live.cn
 */
public interface OnThreadResultListener {

    void onStart();

    /**
     * 线程完成
     */
    void onFinish(String dealPicturePath);

    /**
     * 线程被中断
     */
    void onInterrupted();
}
