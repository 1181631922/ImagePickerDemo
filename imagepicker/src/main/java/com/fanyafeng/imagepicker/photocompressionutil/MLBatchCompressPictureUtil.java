package com.fanyafeng.imagepicker.photocompressionutil;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author： fanyafeng
 * Date： 17/11/29 上午10:35
 * Email: fanyafeng@live.cn
 */
public class MLBatchCompressPictureUtil {
    private final static String TAG = MLBatchCompressPictureUtil.class.getSimpleName();

    /**
     * 线程开始
     */
    private final static int THREAD_START_CODE = 100;

    /**
     * 线程完成
     */
    private final static int THREAD_FINISH_CODE = 101;

    /**
     * 线程被中断
     */
    private final static int THREAD_INTERRUPT_CODE = 102;

    /**
     * 所有线程完成
     */
    private final static int THREAD_ALL_SUCCESS_CODE = 103;

    /**
     * 所有线程执行失败
     */
    private final static int THREAD_ALL_FAILED_CODE = 104;

    private final static String THREAD_POSITION = "THREAD_POSITION";

    private final static String THREAD_DEAL_PICTURE_PATH = "THREAD_DEAL_PICTURE_PATH";
    /**
     * 任务数量
     */
    private int threadCount = 0;
    /**
     * 线程池核心数
     * <p>
     * 单线程处理
     */
    private int threadCore = 1;
    /**
     * 线程池
     */
    private ExecutorService executorService;
    /**
     * 计数器
     */
    private CountDownLatch countDownLatch;

    private OnBatchCompressListener onBatchCompressListener;

    private CompressHandler compressHandler;

    private List<String> dealPicturePathList = new ArrayList<>();

    public List<String> getDealPicturePathList() {
        return dealPicturePathList;
    }

    public MLBatchCompressPictureUtil() {
        init();
    }

    public void setOnBatchCompressListener(OnBatchCompressListener onBatchCompressListener) {
        this.onBatchCompressListener = onBatchCompressListener;
        if (dealPicturePathList != null) {
            dealPicturePathList.clear();
        } else {
            dealPicturePathList = new ArrayList<>();
        }
    }

    public void init() {
        compressHandler = new CompressHandler(this);
    }

    public void shutDownNow() {
        executorService.shutdownNow();
    }

    public void submitAll(List<String> fileNameList) {
        threadCount = fileNameList.size();
        countDownLatch = new CountDownLatch(threadCount);
        executorService = Executors.newFixedThreadPool(threadCore + 1);

        executorService.submit(new CompressListener(countDownLatch, new OnAllThreadResultListener() {
            @Override
            public void onSuccess() {
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("dealPicturePathList", (ArrayList<String>) dealPicturePathList);
//                compressHandler.sendEmptyMessage(THREAD_ALL_SUCCESS_CODE);
                Message.obtain(compressHandler, THREAD_ALL_SUCCESS_CODE, bundle).sendToTarget();
            }

            @Override
            public void onFailed() {
                compressHandler.sendEmptyMessage(THREAD_ALL_FAILED_CODE);
            }
        }));

        for (int i = 0; i < threadCount; i++) {
            final Bundle bundle = new Bundle();
            bundle.putInt(THREAD_POSITION, i);
            Log.e(TAG, "处理图片position：" + i);//图片数量正确
            executorService.submit(new CompressPicture(countDownLatch, fileNameList.get(i), new OnThreadResultListener() {
                @Override
                public void onStart() {
                    Message.obtain(compressHandler, THREAD_START_CODE, bundle).sendToTarget();
                }

                @Override
                public void onFinish(String dealPicturePath) {
                    bundle.putString("THREAD_DEAL_PICTURE_PATH", dealPicturePath);
                    dealPicturePathList.add(dealPicturePath);
                    Message.obtain(compressHandler, THREAD_FINISH_CODE, bundle).sendToTarget();
                }

                @Override
                public void onInterrupted() {
                    Message.obtain(compressHandler, THREAD_INTERRUPT_CODE, bundle).sendToTarget();
                }
            }));
        }
        executorService.shutdown();
    }

    private static class CompressHandler extends Handler {
        private WeakReference<MLBatchCompressPictureUtil> compressPictureUtilWeakReference;

        public CompressHandler(MLBatchCompressPictureUtil MLBatchCompressPictureUtil) {
            super(Looper.getMainLooper());
            compressPictureUtilWeakReference = new WeakReference<MLBatchCompressPictureUtil>(MLBatchCompressPictureUtil);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MLBatchCompressPictureUtil MLBatchCompressPictureUtil = compressPictureUtilWeakReference.get();
            if (MLBatchCompressPictureUtil != null) {
                Bundle bundle = (Bundle) msg.obj;
                int position;
                switch (msg.what) {
                    case THREAD_START_CODE:
                        position = bundle.getInt(THREAD_POSITION);
                        MLBatchCompressPictureUtil.onBatchCompressListener.onThreadProgressStart(position);
                        break;
                    case THREAD_FINISH_CODE:
                        position = bundle.getInt(THREAD_POSITION);
                        MLBatchCompressPictureUtil.onBatchCompressListener.onThreadFinish(position, bundle.getString(THREAD_DEAL_PICTURE_PATH));
                        break;
                    case THREAD_INTERRUPT_CODE:
                        position = bundle.getInt(THREAD_POSITION);
                        MLBatchCompressPictureUtil.onBatchCompressListener.onThreadInterrupted(position);
                        break;
                    case THREAD_ALL_SUCCESS_CODE:
                        Log.e(TAG, "传过去的长度size：" + bundle.getStringArrayList("dealPicturePathList").size());
                        MLBatchCompressPictureUtil.onBatchCompressListener.onAllSuccess(bundle.getStringArrayList("dealPicturePathList"));
                        MLBatchCompressPictureUtil.getDealPicturePathList().clear();
                        break;
                    case THREAD_ALL_FAILED_CODE:
                        MLBatchCompressPictureUtil.onBatchCompressListener.onAllFailed();
                        break;
                }
            }
        }
    }

}
