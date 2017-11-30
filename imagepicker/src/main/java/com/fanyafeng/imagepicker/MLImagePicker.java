package com.fanyafeng.imagepicker;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v4.content.FileProvider;

import com.fanyafeng.imagepicker.bean.ImageBean;
import com.fanyafeng.imagepicker.imageloadframe.ImageLoadFrame;
import com.fanyafeng.imagepicker.view.CropImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Author： fanyafeng
 * Date： 17/11/15 下午5:48
 * Email: fanyafeng@live.cn
 * <p>
 * 暂时这样写，但是感觉不好，后期会把一些中期会变化的属性抽象为option
 */
public class MLImagePicker {

    private final static String TAG = MLImagePicker.class.getSimpleName();

    //选择类型
    public final static String SELECT_TYPE = "select_type";
    //单选
    public final static int TYPE_CHOOSE_SINGLE = 1;
    //多选
    public final static int TYPE_CHOOSE_MULTIPLE = 0;
    //多选图片列表返回
    public final static String RESULT_IMG_LIST = "result_img_list";
    //单张本地图片处理返回
    public final static String LOCATION_DEAL_URL = "location_deal_url";

    public final static String LOCATION_URL = "location_url";

    //图片列表请求
    public final static int CODE_REQUEST_IMG_LIST = 2000;
    //图片列表返回结果
    public final static int CODE_RESULT_IMG_LIST = 2001;
    //预览图片页面请求
    public final static int CODE_REQUEST_IMG_PREVIEW_LIST = 3000;
    //预览图片页面返回结果
    public final static int CODE_RESULT_IMG_PREVIEW_LIST = 3001;
    //拍照页面请求
    public final static int CODE_REQUEST_TAKE_PHOTO = 5000;
    //拍照页面返回结果
    public final static int CODE_RESULT_TAKE_PHOTO = 5001;
    //处理本地图片请求
    public final static int CODE_REQUEST_LOCAL_PHOTO = 6000;
    //处理本地图片结果
    public final static int CODE_RESULT_LOCAL_PHOTO = 6001;


    //加载框架注入
    private ImageLoadFrame imageLoadFrame;
    //选中图片列表
    private List<ImageBean> choosedImageBeanList = new ArrayList<>();
    //单张拍照图片处理路径
    private List<ImageBean> dealImageBeanList = new ArrayList<>();
    //最大选择图片张数
    private int imageMaxSize = 9;
    //裁剪图片文件夹
    private File cropImgFolder;
    //压缩图片后的文件夹
    private File compressImgFolder;

    private File takeImageFile;
    //处理后输出图片的宽度
    private int outputX = 1000;
    //处理后输出图片的高度
    private int outputY = 1000;
    //裁剪框宽度
    private int focusWeight = 600;
    //裁剪框高度
    private int focusHeight = 600;
    //处理图片框形状
    private boolean isSaveRectangle = true;

    private CropImageView.Style focusStyle = CropImageView.Style.RECTANGLE;
    /**
     * 单选 or 多选
     * <p>
     * 默认多选，单选的情况下不支持最大图片张数
     */
    private int chooseType = TYPE_CHOOSE_MULTIPLE;

    private boolean isLight;

    @ColorInt
    private int toolbarColor = Color.parseColor("#3F51B5");

    @ColorInt
    private int statusBarColor = Color.parseColor("#303F9F");

    @DrawableRes
    private int navigationIconRes = 0;

    //私有化构造方法，防止使用者去new
    private MLImagePicker() {
    }

    private static MLImagePicker instance;

    public static MLImagePicker getInstance() {
        if (instance == null) {
            synchronized (MLImagePicker.class) {
                if (instance == null) {
                    instance = new MLImagePicker();
                }
            }
        }
        return instance;
    }

    public File getCropCacheFolder(Context context) {
        if (cropImgFolder == null) {
            cropImgFolder = new File(context.getCacheDir() + "/MLImagePicker/cropImg/");
        }
        return cropImgFolder;
    }

    public ImageLoadFrame getImageLoadFrame() {
        return imageLoadFrame;
    }

    public List<ImageBean> getChoosedImageBeanList() {
        return choosedImageBeanList;
    }

    public List<ImageBean> getDealImageBeanList() {
        return dealImageBeanList;
    }

    public int getImageMaxSize() {
        return imageMaxSize;
    }

    public File getTakeImageFile() {
        return takeImageFile;
    }

    public MLImagePicker setImageMaxSize(int imageMaxSize) {
        this.imageMaxSize = imageMaxSize;
        return this;
    }

    public MLImagePicker setImageLoadFrame(ImageLoadFrame imageLoadFrame) {
        this.imageLoadFrame = imageLoadFrame;
        return this;
    }

    public MLImagePicker setChoosedImageBeanList(List<ImageBean> choosedImageBeanList) {
        this.choosedImageBeanList = choosedImageBeanList;
        return this;
    }

    public MLImagePicker setDealImageBeanList(List<ImageBean> dealImageBeanList) {
        this.dealImageBeanList = dealImageBeanList;
        return this;
    }

    public int getOutputX() {
        return outputX;
    }

    public MLImagePicker setOutputX(int outputX) {
        this.outputX = outputX;
        return this;
    }

    public int getOutputY() {
        return outputY;
    }

    public MLImagePicker setOutputY(int outputY) {
        this.outputY = outputY;
        return this;
    }

    public int getFocusWeight() {
        return focusWeight;
    }

    public MLImagePicker setFocusWeight(int focusWeight) {
        this.focusWeight = focusWeight;
        return this;
    }

    public int getFocusHeight() {
        return focusHeight;
    }

    public MLImagePicker setFocusHeight(int focusHeight) {
        this.focusHeight = focusHeight;
        return this;
    }

    public boolean isSaveRectangle() {
        return isSaveRectangle;
    }

    public MLImagePicker setSaveRectangle(boolean saveRectangle) {
        isSaveRectangle = saveRectangle;
        return this;
    }

    public int getChooseType() {
        return chooseType;
    }

    public MLImagePicker setChooseType(int chooseType) {
        this.chooseType = chooseType;
        return this;
    }

    public int getToolbarColor() {
        return toolbarColor;
    }

    public MLImagePicker setToolbarColor(int toolbarColor) {
        this.toolbarColor = toolbarColor;
        return this;
    }

    public int getStatusBarColor() {
        return statusBarColor;
    }

    public MLImagePicker setStatusBarColor(int statusBarColor) {
        this.statusBarColor = statusBarColor;
        return this;
    }

    public CropImageView.Style getFocusStyle() {
        return focusStyle;
    }

    public MLImagePicker setFocusStyle(CropImageView.Style focusStyle) {
        this.focusStyle = focusStyle;
        return this;
    }

    public boolean isLight() {
        return isLight;
    }

    public MLImagePicker setLight(boolean light) {
        isLight = light;
        return this;
    }

    public int getNavigationIconRes() {
        return navigationIconRes;
    }

    public MLImagePicker setNavigationIconRes(int navigationIconRes) {
        this.navigationIconRes = navigationIconRes;
        return this;
    }


    public File getCompressImgFolder() {
        if (compressImgFolder == null) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                compressImgFolder = new File(Environment.getExternalStorageDirectory(), "/DCIM/camera/");
            } else {
                compressImgFolder = Environment.getDataDirectory();
            }
        }
        return compressImgFolder;
    }

    public void takePicture(Context context, int requestCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                takeImageFile = new File(Environment.getExternalStorageDirectory(), "/DCIM/camera/");
            } else {
                takeImageFile = Environment.getDataDirectory();
            }
            takeImageFile = createFile(takeImageFile, "IMG_", ".jpg");
            if (takeImageFile != null) {
                Uri uri;
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                    uri = Uri.fromFile(takeImageFile);
                } else {
                    uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", takeImageFile);
                    List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                }

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            }
        }
        ((Activity) context).startActivityForResult(takePictureIntent, requestCode);
    }

    private File createFile(File folder, String prefix, String suffix) {
        if (!folder.exists() || !folder.isDirectory()) folder.mkdirs();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
        String filename = prefix + dateFormat.format(new Date(System.currentTimeMillis())) + suffix;
        return new File(folder, filename);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        imageLoadFrame = (ImageLoadFrame) savedInstanceState.getSerializable("imageLoadFrame");
        imageMaxSize = savedInstanceState.getInt("imageMaxSize");
        choosedImageBeanList = savedInstanceState.getParcelableArrayList("choosedImageBeanList");
        cropImgFolder = (File) savedInstanceState.getSerializable("cropImgFolder");
        takeImageFile = (File) savedInstanceState.getSerializable("takeImageFile");
        outputX = savedInstanceState.getInt("outputX");
        outputY = savedInstanceState.getInt("outputY");
        isSaveRectangle = savedInstanceState.getBoolean("isSaveRectangle");
        focusWeight = savedInstanceState.getInt("focusWeight");
        focusHeight = savedInstanceState.getInt("focusHeight");
        chooseType = savedInstanceState.getInt("chooseType");
        focusStyle = (CropImageView.Style) savedInstanceState.getSerializable("focusStyle");
        toolbarColor = savedInstanceState.getInt("toolbarColor");
        statusBarColor = savedInstanceState.getInt("statusBarColor");
        navigationIconRes = savedInstanceState.getInt("navigationIconRes");
        compressImgFolder = (File) savedInstanceState.getSerializable("compressImgFolder");
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("imageLoadFrame", imageLoadFrame);
        outState.putInt("imageMaxSize", imageMaxSize);
        outState.putParcelableArrayList("choosedImageBeanList", (ArrayList<? extends Parcelable>) choosedImageBeanList);
        outState.putSerializable("cropImgFolder", cropImgFolder);
        outState.putSerializable("takeImageFile", takeImageFile);
        outState.putInt("outputX", outputX);
        outState.putInt("outputY", outputY);
        outState.putBoolean("isSaveRectangle", isSaveRectangle);
        outState.putInt("focusWeight", focusWeight);
        outState.putInt("focusHeight", focusHeight);
        outState.putInt("chooseType", chooseType);
        outState.putSerializable("focusStyle", focusStyle);
        outState.putInt("toolbarColor", toolbarColor);
        outState.putInt("statusBarColor", statusBarColor);
        outState.putInt("navigationIconRes", navigationIconRes);
        outState.putSerializable("compressImgFolder", compressImgFolder);
    }


}
