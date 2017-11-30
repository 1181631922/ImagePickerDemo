package com.fanyafeng.imagepicker;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;


import com.fanyafeng.imagepicker.bean.ImageBean;
import com.fanyafeng.imagepicker.bean.ImageFolderBean;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Author： fanyafeng
 * Date： 17/11/16 下午4:38
 * Email: fanyafeng@live.cn
 */
public class ScanImageSource implements LoaderManager.LoaderCallbacks<Cursor> {

    private final int SCAN_ALL_IMAGES = 100;
    private final int SCAN_FOLDER = 101;

    //    图片属性
    private final String[] IMAGE_ATTRIBUTE = {
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.DATE_ADDED
    };

    private AppCompatActivity appCompatActivity;
    private List<ImageFolderBean> imageFolderBeanList = new ArrayList<>();
    private OnImageLoadedListener onImageLoadedListener;

    public ScanImageSource(AppCompatActivity appCompatActivity, String path, OnImageLoadedListener onImageLoadedListener) {
        this.appCompatActivity = appCompatActivity;
        this.onImageLoadedListener = onImageLoadedListener;

        LoaderManager loaderManager = appCompatActivity.getSupportLoaderManager();
        if (path == null) {
            loaderManager.initLoader(SCAN_ALL_IMAGES, null, this);
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("path", path);
            loaderManager.initLoader(SCAN_FOLDER, bundle, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = null;
        if (id == SCAN_ALL_IMAGES) {
            cursorLoader = new CursorLoader(appCompatActivity, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_ATTRIBUTE, null, null, IMAGE_ATTRIBUTE[6]);
        } else if (id == SCAN_FOLDER) {
            cursorLoader = new CursorLoader(appCompatActivity, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_ATTRIBUTE, IMAGE_ATTRIBUTE[1] + " like %" + args.getString("path") + "%", null, IMAGE_ATTRIBUTE[6]);
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        imageFolderBeanList.clear();
        if (data != null) {
            List<ImageBean> imageBeanList = new ArrayList<>();
            while (data.moveToNext()) {
                String imageName = data.getString(data.getColumnIndexOrThrow(IMAGE_ATTRIBUTE[0]));
                String path = data.getString(data.getColumnIndexOrThrow(IMAGE_ATTRIBUTE[1]));

                File file = new File(path);
                if (!file.exists() || file.length() <= 0) {
                    continue;
                }

                long imageSize = data.getLong(data.getColumnIndexOrThrow(IMAGE_ATTRIBUTE[2]));
                int imageWidth = data.getInt(data.getColumnIndexOrThrow(IMAGE_ATTRIBUTE[3]));
                int imageHeight = data.getInt(data.getColumnIndexOrThrow(IMAGE_ATTRIBUTE[4]));
                String imageType = data.getString(data.getColumnIndexOrThrow(IMAGE_ATTRIBUTE[5]));
                long imageCreateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_ATTRIBUTE[6]));

                ImageBean imageBean = new ImageBean(imageName, path, imageSize, imageWidth, imageHeight, imageType, imageCreateTime);
                imageBeanList.add(imageBean);

                File imageFile = new File(path);
                File imageParentFile = imageFile.getParentFile();
                ImageFolderBean imageFolderBean = new ImageFolderBean();
                imageFolderBean.folderName = imageParentFile.getName();
                imageFolderBean.folderPath = imageParentFile.getAbsolutePath();

                if (!imageFolderBeanList.contains(imageFolderBean)) {
                    List<ImageBean> imageBeans = new ArrayList<>();
                    imageBeans.add(imageBean);
                    imageFolderBean.imageBean = imageBean;
                    imageFolderBean.imageBeanList = imageBeans;
                    imageFolderBeanList.add(imageFolderBean);
                } else {
                    imageFolderBeanList.get(imageFolderBeanList.indexOf(imageFolderBean)).imageBeanList.add(imageBean);
                }
            }

            //按照时间排序
            Collections.sort(imageBeanList);

            if (data.getCount() > 0 && imageBeanList.size() > 0) {
                ImageFolderBean allImageFolderBean = new ImageFolderBean();
                allImageFolderBean.folderName = "所有图片";
                allImageFolderBean.folderPath = "/";
                allImageFolderBean.imageBean = imageBeanList.get(0);
                allImageFolderBean.imageBeanList = imageBeanList;
                imageFolderBeanList.add(0, allImageFolderBean);
            }
        }

        onImageLoadedListener.onImageLoaded(imageFolderBeanList);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public interface OnImageLoadedListener {
        void onImageLoaded(List<ImageFolderBean> imageFolderBeanList);
    }
}
