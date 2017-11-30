package com.fanyafeng.imagepicker.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.fanyafeng.imagepicker.MLImagePicker;
import com.fanyafeng.imagepicker.R;
import com.fanyafeng.imagepicker.bean.ImageBean;
import com.fanyafeng.imagepicker.util.BitmapUtil;
import com.fanyafeng.imagepicker.view.CropImageView;

import java.io.File;
import java.util.ArrayList;


public class MLTakePhotoActivity extends ImageBaseActivity implements CropImageView.OnBitmapSaveCompleteListener {
    private final static String TAG = MLTakePhotoActivity.class.getSimpleName();

    private final static int REQUEST_CODE_CAMERA_AND_STORAGE = 1000;

    private final static int REQUEST_CODE_CAMERA = 1001;

    private final static int REQUEST_CODE_STORAGE = 1002;

    private final static int PICTURE_DEAL_TAKE_PHOTO = 500;

    //    private Toolbar toolbar;
    private TextView toolbarRightTitle;
    private CropImageView civPhotoView;

    private String locationUrl;
    private Bitmap currBitmap;

    private boolean isTakePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);

        if (getIntent().getStringExtra(MLImagePicker.LOCATION_URL) != null) {
            locationUrl = getIntent().getStringExtra(MLImagePicker.LOCATION_URL);
        } else {
            requestMediaPermission();
        }

        initView();
        initData();
    }

    private void requestMediaPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA_AND_STORAGE);
            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE);
            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
            } else {
                operateCamera();
            }
        } else {
            return;
        }
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.deal_picture));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.go_back_left_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        toolbarRightTitle = findViewById(R.id.toolbarRightTitle);
        toolbarRightTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// TODO: 17/11/24 图片裁剪完成
                civPhotoView.saveBitmapToFile(MLImagePicker.getInstance().getCropCacheFolder(MLTakePhotoActivity.this),
                        MLImagePicker.getInstance().getOutputX(), MLImagePicker.getInstance().getOutputY(), MLImagePicker.getInstance().isSaveRectangle());
            }
        });

    }

    private void initData() {
        civPhotoView = findViewById(R.id.civPhotoView);
        civPhotoView.setFocusHeight(MLImagePicker.getInstance().getFocusHeight());
        civPhotoView.setFocusWidth(MLImagePicker.getInstance().getFocusWeight());
        civPhotoView.setOnBitmapSaveCompleteListener(this);
        civPhotoView.setFocusStyle(MLImagePicker.getInstance().getFocusStyle());

        if (locationUrl != null && !locationUrl.equals("")) {
            loadPicture(locationUrl);
        }
    }

    private void loadPicture(String picUrl) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picUrl, options);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        options.inSampleSize = calculateInSampleSize(options, displayMetrics.widthPixels, displayMetrics.heightPixels);
        options.inJustDecodeBounds = false;
        currBitmap = BitmapFactory.decodeFile(picUrl, options);
        civPhotoView.setImageBitmap(civPhotoView.rotate(currBitmap, BitmapUtil.getBitmapDegree(picUrl)));
    }

    private void operateCamera() {
        MLImagePicker.getInstance().takePicture(this, PICTURE_DEAL_TAKE_PHOTO);
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = width / reqWidth;
            } else {
                inSampleSize = height / reqHeight;
            }
        }
        return inSampleSize;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICTURE_DEAL_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                loadPicture(MLImagePicker.getInstance().getTakeImageFile().getAbsolutePath());
                isTakePhoto = true;
            } else {
                Toast.makeText(this, "拍照取消", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBitmapSaveSuccess(File file) {
        ImageBean imageBean = new ImageBean();
        imageBean.imgPath = file.getAbsolutePath();
        Intent intent = new Intent();
        if (isTakePhoto) {
            MLImagePicker.getInstance().getDealImageBeanList().add(imageBean);
            intent.putParcelableArrayListExtra(MLImagePicker.RESULT_IMG_LIST, (ArrayList<? extends Parcelable>) MLImagePicker.getInstance().getDealImageBeanList());
            setResult(MLImagePicker.CODE_RESULT_TAKE_PHOTO, intent);
        } else {
            intent.putExtra(MLImagePicker.LOCATION_DEAL_URL, file.getAbsolutePath());
            setResult(MLImagePicker.CODE_RESULT_LOCAL_PHOTO, intent);
        }

        finish();

    }

    @Override
    public void onBitmapSaveError(File file) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_CAMERA_AND_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    operateCamera();
                } else {
                    Toast.makeText(MLTakePhotoActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case REQUEST_CODE_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    operateCamera();
                } else {
                    Toast.makeText(MLTakePhotoActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case REQUEST_CODE_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    operateCamera();
                } else {
                    Toast.makeText(MLTakePhotoActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != civPhotoView) {
            civPhotoView.setOnBitmapSaveCompleteListener(null);
        }
        if (null != currBitmap && !currBitmap.isRecycled()) {
            currBitmap.recycle();
            currBitmap = null;
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //android可以保存view，但是view会进行重新绘制，重绘需要重新设置属性
        civPhotoView.setFocusHeight(MLImagePicker.getInstance().getFocusHeight());
        civPhotoView.setFocusWidth(MLImagePicker.getInstance().getFocusWeight());
        civPhotoView.setOnBitmapSaveCompleteListener(this);
        civPhotoView.setFocusStyle(MLImagePicker.getInstance().getFocusStyle());

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }
}
