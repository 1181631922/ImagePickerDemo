package com.fanyafeng.imagepicker.photocompressionutil;

import java.util.List;

/**
 * Author： fanyafeng
 * Date： 17/11/29 上午9:47
 * Email: fanyafeng@live.cn
 */
public interface OnBatchCompressListener {
    void onAllSuccess(List<String> dealPicturePathList);

    void onAllFailed();

    void onThreadProgressStart(int position);

    void onThreadFinish(int position, String dealPicturePath);

    void onThreadInterrupted(int position);
}
