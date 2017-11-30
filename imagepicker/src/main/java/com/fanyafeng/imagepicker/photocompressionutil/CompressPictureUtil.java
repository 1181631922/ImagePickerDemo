package com.fanyafeng.imagepicker.photocompressionutil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;


import com.fanyafeng.imagepicker.MLImagePicker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Author： fanyafeng
 * Date： 17/11/28 上午11:06
 * Email: fanyafeng@live.cn
 */
public class CompressPictureUtil {
    private final static String TAG = CompressPictureUtil.class.getSimpleName();

    private CompressPictureUtil() {
    }

    private static CompressPictureUtil instance;

    public static CompressPictureUtil getInstance() {
        if (instance == null) {
            synchronized (CompressPictureUtil.class) {
                if (instance == null) {
                    instance = new CompressPictureUtil();
                }
            }
        }
        return instance;
    }


    private OnCompressListener onCompressListener;

    /**
     * 旋转bitmap
     *
     * @param angle
     * @param bitmap
     * @return
     */
    private static Bitmap rotateImage(float angle, Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public File thirdCompress(File file, OnCompressListener onCompressListener) {
        this.onCompressListener = onCompressListener;
        this.onCompressListener.onStart();
//        String thumbPath = Environment.getExternalStorageDirectory().getAbsoluteFile() + File.separator + "Adhoc" + File.separator
//                + System.currentTimeMillis() + ".jpg";
        String thumbPath = MLImagePicker.getInstance().getCompressImgFolder().getAbsolutePath() + File.separator + "IMG_"+System.currentTimeMillis() + ".jpg";
        long size;
        String filePath = file.getAbsolutePath();

        int angle = getImageSpinAngle(filePath);
        int width = getImageSize(filePath)[0];
        int height = getImageSize(filePath)[1];

        int thumbW = width;
        int thumbH = height;

        if (width > 4000) {
            thumbW = (int) (width * 0.4);
            thumbH = (int) (height * 0.4);
        } else if (width > 3000) {
            thumbW = width >> 1;
            thumbH = height >> 1;
        } else if (width > 2048) {
            thumbW = (int) (width * 0.6);
            thumbH = (int) (height * 0.6);
        }
        size = 800;

        return compress(filePath, thumbPath, thumbW, thumbH, angle, size);
    }

    private File compress(String largeImagePath, String thumbFilePath, int width, int height, int angle, long size) {
        Bitmap bitmap = compress(largeImagePath, width, height);
        bitmap = rotateImage(angle, bitmap);
        return saveImage(thumbFilePath, bitmap, size);
    }

    private File saveImage(String filePath, Bitmap bitmap, long size) {
        if (bitmap != null) {
            File result = new File(filePath.substring(0, filePath.lastIndexOf("/")));
            if (!result.exists() && !result.mkdirs()) {
                return null;
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int quality = 80;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);

            try {
                FileOutputStream fileOutputStream = new FileOutputStream(filePath);
                fileOutputStream.write(byteArrayOutputStream.toByteArray());
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                onCompressListener.onError(e);
            }
            onCompressListener.onSuccess(filePath);
            Log.e(TAG, "处理后图片的路径：" + filePath);
            return new File(filePath);
        } else {
            onCompressListener.onError(new Throwable("bitmap is null"));
            return null;
        }
    }

    private Bitmap compress(String imagePath, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        int outH = options.outHeight;
        int outW = options.outWidth;
        int inSampleSize = 1;

        if (outH > height || outW > width) {
            int halfH = outH >> 1;
            int halfW = outW >> 1;

            while ((halfH / inSampleSize) > height && (halfW / inSampleSize) > width) {
                inSampleSize *= 2;
            }
        }

        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;

        int heightRatio = (int) Math.ceil(options.outHeight / (float) height);
        int widthRatio = (int) Math.ceil(options.outWidth / (float) width);

        if (heightRatio > 1 || widthRatio > 1) {
            if (heightRatio > widthRatio) {
                options.inSampleSize = heightRatio;
            } else {
                options.inSampleSize = widthRatio;
            }
        }

        if (options.inSampleSize > 2) {
            options.inSampleSize = 2;
        }
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(imagePath, options);
    }

    private int[] getImageSize(String imagePath) {
        int[] res = new int[2];

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 1;
        BitmapFactory.decodeFile(imagePath, options);

        res[0] = options.outWidth;
        res[1] = options.outHeight;
        return res;
    }

    /**
     * 获取图片旋转角度
     *
     * @param path
     * @return
     */
    private int getImageSpinAngle(String path) {
        int degree = 0;
        if (!isJPG(path)) {
            return degree;
        }
        try {
            ExifInterface exifInterface = new ExifInterface(path);//仅支持jpg
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    private boolean isJPG(String path) {
        if (path != null && path.equals("")) {
            String suffix = path.substring(path.lastIndexOf("."), path.length()).toLowerCase();
            if (suffix.contains("jpg") || suffix.contains("jpeg")) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


}
