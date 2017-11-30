package com.fanyafeng.imagepicker.photocompressionutil;

/**
 * Author： fanyafeng
 * Date： 17/11/29 下午2:15
 * Email: fanyafeng@live.cn
 */
public interface OnCompressListener {
    void onStart();

    void onSuccess(String dealPicturePath);

    void onError(Throwable e);
}
