package com.fanyafeng.imagepickerdemo.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.fanyafeng.imagepicker.MLImagePicker;
import com.fanyafeng.imagepicker.bean.ImageBean;
import com.fanyafeng.imagepicker.photocompressionutil.MLBatchCompressPictureUtil;
import com.fanyafeng.imagepicker.photocompressionutil.OnBatchCompressListener;
import com.fanyafeng.imagepicker.ui.MLImageListActivity;
import com.fanyafeng.imagepicker.ui.MLTakePhotoActivity;
import com.fanyafeng.imagepickerdemo.ImageLoadFrame.FrescoLoadFrame;
import com.fanyafeng.imagepickerdemo.ImageLoadFrame.GlideLoadFrame;
import com.fanyafeng.imagepickerdemo.ImageLoadFrame.PicassoLoadFrame;
import com.fanyafeng.imagepickerdemo.R;
import com.fanyafeng.imagepickerdemo.BaseActivity;
import com.fanyafeng.imagepickerdemo.adapter.MainAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

//需要搭配baseactivity，这里默认为baseactivity,并且默认Baseactivity为包名的根目录
public class MainActivity extends BaseActivity {
    private RadioButton rbFresco;
    private RadioButton rbGlide;
    private RadioButton rbPicasso;

    private RadioButton rbBlue;
    private RadioButton rbBlack;
    private RadioButton rbRed;

    private RadioButton rbNoSingle;
    private RadioButton rbSingle;

    private EditText etPictureCount;

    private EditText etCroupWidth;
    private EditText etCroupHeight;

    private EditText etSaveWidth;
    private EditText etSaveHeight;

    private RadioButton rbPicture;
    private RadioButton rbTakePhoto;

    private CheckBox cbCompress;

    private CheckBox cbBackIcon;

    private TextView tvImgPath;
    private TextView tvCompressImgPath;

    private RecyclerView rvShowPicture;
    private RecyclerView rvShowCompressPicture;

    private MLImagePicker mlImagePicker;

    private Intent intent;

    private int mainRequestCode;

    private boolean isCompress;

    private MLBatchCompressPictureUtil mlBatchCompressPictureUtil;

    private MainAdapter mainAdapter;
    private List<ImageBean> imageBeanList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//这里默认使用的是toolbar的左上角标题，如果需要使用的标题为中心的采用下方注释的代码，将此注释掉即可
        title = getString(R.string.title_activity_main);
        isSetNavigationIcon = false;

        mlImagePicker = MLImagePicker.getInstance()
                .setImageLoadFrame(new FrescoLoadFrame());
        mainRequestCode = MLImagePicker.CODE_REQUEST_IMG_LIST;

        initView();
        initData();
    }

    //初始化UI空间
    private void initView() {
        rbFresco = findViewById(R.id.rbFresco);
        rbFresco.setChecked(true);
        rbGlide = findViewById(R.id.rbGlide);
        rbPicasso = findViewById(R.id.rbPicasso);

        rbBlue = findViewById(R.id.rbBlue);
        rbBlue.setChecked(true);
        rbBlack = findViewById(R.id.rbBlack);
        rbRed = findViewById(R.id.rbRed);

        rbNoSingle = findViewById(R.id.rbNoSingle);
        rbNoSingle.setChecked(true);
        rbSingle = findViewById(R.id.rbSingle);

        etPictureCount = findViewById(R.id.etPictureCount);

        etCroupWidth = findViewById(R.id.etCroupWidth);
        etCroupHeight = findViewById(R.id.etCroupHeight);

        etSaveWidth = findViewById(R.id.etSaveWidth);
        etSaveHeight = findViewById(R.id.etSaveHeight);

        rbPicture = findViewById(R.id.rbPicture);
        rbPicture.setChecked(true);
        rbTakePhoto = findViewById(R.id.rbTakePhoto);

        cbCompress = findViewById(R.id.cbCompress);
        cbCompress.setChecked(false);

        cbBackIcon = findViewById(R.id.cbBackIcon);
        cbBackIcon.setChecked(false);

        tvImgPath = findViewById(R.id.tvImgPath);
        tvCompressImgPath = findViewById(R.id.tvCompressImgPath);

        rvShowPicture = findViewById(R.id.rvShowPicture);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        gridLayoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
        rvShowPicture.setLayoutManager(gridLayoutManager);

        GridLayoutManager gridLayoutManager1 = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        gridLayoutManager1.setOrientation(GridLayoutManager.HORIZONTAL);
        rvShowCompressPicture = findViewById(R.id.rvShowCompressPicture);
        rvShowCompressPicture.setLayoutManager(gridLayoutManager1);
    }

    //初始化数据
    private void initData() {
        mlBatchCompressPictureUtil = new MLBatchCompressPictureUtil();
        mlBatchCompressPictureUtil.setOnBatchCompressListener(new OnBatchCompressListener() {
            @Override
            public void onAllSuccess(List<String> dealPicturePathList) {
                tvCompressImgPath.append(dealPicturePathList.toString());
                List<ImageBean> imageBeans = new ArrayList<>();
                for (int i = 0; i < dealPicturePathList.size(); i++) {
                    ImageBean imageBean = new ImageBean();
                    imageBean.imgPath = dealPicturePathList.get(i);
                    imageBeans.add(imageBean);
                }
                rvShowCompressPicture.setAdapter(new MainAdapter(MainActivity.this, imageBeans));
            }

            @Override
            public void onAllFailed() {

            }

            @Override
            public void onThreadProgressStart(int position) {

            }

            @Override
            public void onThreadFinish(int position, String dealPicturePath) {

            }

            @Override
            public void onThreadInterrupted(int position) {

            }
        });

        mainAdapter = new MainAdapter(this, imageBeanList);
        rvShowPicture.setAdapter(mainAdapter);

        intent = new Intent(MainActivity.this, MLImageListActivity.class);

        rbFresco.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mlImagePicker.setImageLoadFrame(new FrescoLoadFrame());
                }
            }
        });
        rbGlide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mlImagePicker.setImageLoadFrame(new GlideLoadFrame());
                }
            }
        });
        rbPicasso.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mlImagePicker.setImageLoadFrame(new PicassoLoadFrame());
                }
            }
        });

        rbBlue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mlImagePicker.setStatusBarColor(Color.parseColor("#303F9F"));
                    mlImagePicker.setToolbarColor(Color.parseColor("#3F51B5"));
                    mlImagePicker.setLight(false);
                }
            }
        });
        rbBlack.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mlImagePicker.setStatusBarColor(Color.BLACK);
                    mlImagePicker.setToolbarColor(Color.BLACK);
                    mlImagePicker.setLight(false);
                }
            }
        });
        rbRed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mlImagePicker.setStatusBarColor(Color.RED);
                    mlImagePicker.setToolbarColor(Color.RED);
                    mlImagePicker.setLight(true);
                }
            }
        });

        rbNoSingle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mlImagePicker.setChooseType(MLImagePicker.TYPE_CHOOSE_MULTIPLE);
                    mainRequestCode = MLImagePicker.CODE_REQUEST_IMG_LIST;
                }
            }
        });
        rbSingle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mlImagePicker.setChooseType(MLImagePicker.TYPE_CHOOSE_SINGLE);
                    mainRequestCode = MLImagePicker.CODE_REQUEST_TAKE_PHOTO;
                }
            }
        });

        rbPicture.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    intent = new Intent(MainActivity.this, MLImageListActivity.class);
                }
            }
        });
        rbTakePhoto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    intent = new Intent(MainActivity.this, MLTakePhotoActivity.class);
                    mainRequestCode = MLImagePicker.CODE_REQUEST_TAKE_PHOTO;
                }
            }
        });

        cbBackIcon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mlImagePicker.setNavigationIconRes(android.R.drawable.ic_input_delete);
                } else {
                    mlImagePicker.setNavigationIconRes(0);
                }
            }
        });


        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mlImagePicker.setImageMaxSize(Integer.parseInt(etPictureCount.getText().toString().trim()));
                mlImagePicker.setFocusWeight(Integer.parseInt(etCroupWidth.getText().toString().trim()));
                mlImagePicker.setFocusHeight(Integer.parseInt(etCroupHeight.getText().toString().trim()));
                mlImagePicker.setOutputX(Integer.parseInt(etSaveWidth.getText().toString().trim()));
                mlImagePicker.setOutputY(Integer.parseInt(etSaveHeight.getText().toString().trim()));
                startActivityForResult(intent, mainRequestCode);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageBeanList.clear();
        if (requestCode == MLImagePicker.CODE_REQUEST_IMG_LIST) {
            if (resultCode == MLImagePicker.CODE_RESULT_IMG_LIST) {
                if (data != null) {
                    imageBeanList = data.getParcelableArrayListExtra(MLImagePicker.RESULT_IMG_LIST);
                    mainAdapter.refrescData(imageBeanList);
                    tvImgPath.append(imageBeanList.toString());

                    List<String> stringList = new ArrayList<>();
                    for (int i = 0; i < imageBeanList.size(); i++) {
                        stringList.add(imageBeanList.get(i).imgPath);
                    }
                    if (cbCompress.isChecked()) {
                        mlBatchCompressPictureUtil.submitAll(stringList);
                    }
                }
            }
        } else if (requestCode == MLImagePicker.CODE_REQUEST_TAKE_PHOTO) {
            if (data != null) {
                if (resultCode == MLImagePicker.CODE_RESULT_TAKE_PHOTO) {
                    imageBeanList = data.getParcelableArrayListExtra(MLImagePicker.RESULT_IMG_LIST);
                    mainAdapter.refrescData(imageBeanList);
                    tvImgPath.append(imageBeanList.toString());

                } else if (resultCode == MLImagePicker.CODE_RESULT_LOCAL_PHOTO) {
                    ImageBean imageBean = new ImageBean();
                    imageBean.imgPath = data.getStringExtra(MLImagePicker.LOCATION_DEAL_URL);
                    imageBeanList.add(imageBean);
                    mainAdapter.refrescData(imageBeanList);
                    tvImgPath.append(imageBeanList.toString());
                }
            }
        }
    }

}
