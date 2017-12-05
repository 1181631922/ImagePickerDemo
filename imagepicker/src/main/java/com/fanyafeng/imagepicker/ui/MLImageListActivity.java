package com.fanyafeng.imagepicker.ui;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.fanyafeng.imagepicker.MLImagePicker;
import com.fanyafeng.imagepicker.R;
import com.fanyafeng.imagepicker.ScanImageSource;
import com.fanyafeng.imagepicker.adapter.FolderListAdapter;
import com.fanyafeng.imagepicker.adapter.ImageListAdapter;
import com.fanyafeng.imagepicker.bean.ImageBean;
import com.fanyafeng.imagepicker.bean.ImageFolderBean;

import java.util.ArrayList;
import java.util.List;

public class MLImageListActivity extends ImageBaseActivity implements ScanImageSource.OnImageLoadedListener {
    private final static String TAG = MLImageListActivity.class.getSimpleName();

    private final static int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 101;

    private TextView toolbarCenterTitle;
    private TextView toolbarRightTitle;
    private RecyclerView rvImageList;

    private List<ImageBean> imageBeanList = new ArrayList<>();
    private ImageListAdapter imageListAdapter;

    private LinearLayout layoutImageFileList;
    private TextView tvImageFileList;
    private LinearLayout layoutImagePreview;
    private TextView tvImagePreview;

    private RelativeLayout layoutFolderList;
    private View tvFolderList;
    private RelativeLayout layoutRvFolderList;
    private RecyclerView rvFolderList;
    private List<ImageFolderBean> imageFolderBeanList = new ArrayList<>();
    private FolderListAdapter folderListAdapter;

    private RelativeLayout layoutImageListBottom;

    private int rvFolderHeight = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);
        requestMediaPermission();
        initView();
        initData();
    }

    private void requestMediaPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
            } else {
                operateData();
            }
        } else {
            return;
        }
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.img_picker_name));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.go_back_left_arrow);

        layoutImageListBottom = findViewById(R.id.layoutImageListBottom);
        if (MLImagePicker.getInstance().getToolbarColor() != Color.parseColor("#3F51B5")) {
            layoutImageListBottom.setBackgroundColor(MLImagePicker.getInstance().getToolbarColor());
        }

        toolbarCenterTitle = findViewById(R.id.toolbarCenterTitle);
        toolbarRightTitle = findViewById(R.id.toolbarRightTitle);

        toolbarRightTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MLImagePicker.getInstance().getChoosedImageBeanList().size() > 0) {
//                    Toast.makeText(MLImageListActivity.this, MLImagePicker.getInstance().getChoosedImageBeanList().toString(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putParcelableArrayListExtra(MLImagePicker.RESULT_IMG_LIST, (ArrayList<? extends Parcelable>) MLImagePicker.getInstance().getChoosedImageBeanList());
                    setResult(MLImagePicker.CODE_RESULT_IMG_LIST, intent);
                    finish();
                } else {
                    Toast.makeText(MLImageListActivity.this, getString(R.string.picture_no_choose), Toast.LENGTH_SHORT).show();
                }
            }
        });

        layoutImageFileList = findViewById(R.id.layoutImageFileList);
        tvImageFileList = findViewById(R.id.tvImageFileList);
        layoutImagePreview = findViewById(R.id.layoutImagePreview);
        tvImagePreview = findViewById(R.id.tvImagePreview);

        layoutFolderList = findViewById(R.id.layoutFolderList);
        layoutFolderList.setVisibility(View.INVISIBLE);

        layoutFolderList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFolderList();
            }
        });

        tvFolderList = findViewById(R.id.tvFolderList);
        rvFolderList = findViewById(R.id.rvFolderList);
        layoutRvFolderList = findViewById(R.id.layoutRvFolderList);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(0, (int) dip2px(this, 120), 0, 0);
        layoutRvFolderList.setLayoutParams(layoutParams);
        layoutFolderList.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rvFolderHeight = layoutFolderList.getHeight();
                if (rvFolderHeight > 0) {
                    layoutFolderList.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });

        rvFolderList.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));
        rvFolderList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        rvImageList = findViewById(R.id.rvImageList);
        rvImageList.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false));

        if (MLImagePicker.getInstance().getChooseType() == MLImagePicker.TYPE_CHOOSE_SINGLE) {
            toolbarRightTitle.setVisibility(View.GONE);
            layoutImagePreview.setVisibility(View.GONE);
        } else {
            toolbarRightTitle.setVisibility(View.VISIBLE);
            layoutImagePreview.setVisibility(View.VISIBLE);
        }
    }

    private void initData() {

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        layoutImageFileList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layoutFolderList.isShown()) {
                    closeFolderList();
                } else {
                    openFolderList();
                }
            }
        });

        layoutImagePreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 17/11/16 打开图片预览列表
                if (MLImagePicker.getInstance().getChoosedImageBeanList().size() > 0) {
                    Intent intent = new Intent(MLImageListActivity.this, ImagePreviewActivity.class);
                    intent.putParcelableArrayListExtra(MLImagePicker.RESULT_IMG_LIST, (ArrayList<? extends Parcelable>) MLImagePicker.getInstance().getChoosedImageBeanList());
                    intent.putExtra("position", 0);
                    startActivityForResult(intent, MLImagePicker.CODE_REQUEST_IMG_PREVIEW_LIST);
                } else {
                    Toast.makeText(MLImageListActivity.this, getString(R.string.picture_no_choose), Toast.LENGTH_SHORT).show();
                }
            }
        });

        imageListAdapter = new ImageListAdapter(this, imageBeanList);
        rvImageList.setAdapter(imageListAdapter);
        imageListAdapter.setOnChooseImgChangeListener(new ImageListAdapter.OnChooseImgChangeListener() {
            @Override
            public void onChooseImgChangeListener() {
                int imageSize = MLImagePicker.getInstance().getChoosedImageBeanList().size();
                if (imageSize > 0) {
                    toolbarRightTitle.setText(getString(R.string.send) + "（" + MLImagePicker.getInstance().getChoosedImageBeanList().size() + "/" + MLImagePicker.getInstance().getImageMaxSize() + "）");
                    tvImagePreview.setText("（" + imageSize + "）" + getString(R.string.preview));
                } else {
                    toolbarRightTitle.setText(getString(R.string.send));
                    tvImagePreview.setText(getString(R.string.preview));
                }
            }
        });

        folderListAdapter = new FolderListAdapter(this, imageFolderBeanList);
        rvFolderList.setAdapter(folderListAdapter);
        folderListAdapter.setOnItemClickListener(new FolderListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, ImageFolderBean imageFolderBean, int position) {
                if (imageFolderBean != null) {
                    tvImageFileList.setText(imageFolderBean.folderName);


                    for (int i = 0; i < imageFolderBeanList.size(); i++) {
                        if (i == position) {
                            imageFolderBeanList.get(i).isChecked = true;
                        } else {
                            imageFolderBeanList.get(i).isChecked = false;
                        }
                    }
                    folderListAdapter.notifyDataSetChanged();


                    imageListAdapter.refreshData(imageFolderBean.imageBeanList);
                    closeFolderList();
                }
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_WRITE_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    operateData();
                } else {
                    Toast.makeText(MLImageListActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private float dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (float) (dipValue * scale + 0.5f);
    }

    private void openFolderList() {
        layoutFolderList.setVisibility(View.VISIBLE);

        ObjectAnimator moveTopAnimator = ObjectAnimator.ofFloat(rvFolderList, "translationY", dip2px(MLImageListActivity.this, rvFolderHeight + 165), 0);
        moveTopAnimator.setDuration(400);
        moveTopAnimator.start();

        tvFolderList.setBackgroundColor(Color.parseColor("#000000"));
        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 0.8f);
        tvFolderList.startAnimation(alphaAnimation);
        alphaAnimation.setFillAfter(true);
        alphaAnimation.setDuration(400);
    }

    private void closeFolderList() {
        ObjectAnimator moveTopAnimator = ObjectAnimator.ofFloat(rvFolderList, "translationY", 0, dip2px(MLImageListActivity.this, rvFolderHeight + 165));
        moveTopAnimator.setDuration(400);
        moveTopAnimator.start();

        tvFolderList.setBackgroundColor(Color.parseColor("#000000"));
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.8f, 0f);
        tvFolderList.startAnimation(alphaAnimation);
        alphaAnimation.setFillAfter(true);
        alphaAnimation.setDuration(400);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                layoutFolderList.setVisibility(View.GONE);
            }
        }, 400);
    }

    private void operateData() {
        new ScanImageSource(this, null, this);
    }

    // TODO: 17/11/17 初始加载数据只进入一次
    @Override
    public void onImageLoaded(List<ImageFolderBean> imageFolderBeanList) {
        this.imageFolderBeanList = imageFolderBeanList;
        rvFolderList.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));
        rvFolderList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        imageFolderBeanList.get(0).isChecked = true;
        folderListAdapter.refreshData(imageFolderBeanList);

        if (imageFolderBeanList.size() == 0) {
            // TODO: 17/11/16 文件夹为0
        } else {
            Log.d(TAG, "imageFolderBeanListSize:" + imageFolderBeanList.size());
            Log.d(TAG, "imageFolderBeanList:" + imageFolderBeanList.get(0).toString());
            rvImageList.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false));
            imageListAdapter.refreshData(imageFolderBeanList.get(0).imageBeanList);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        imageListAdapter.notifyDataSetChanged();
        int imageSize = MLImagePicker.getInstance().getChoosedImageBeanList().size();
        if (imageSize > 0) {
            toolbarRightTitle.setText(getString(R.string.send) + "（" + MLImagePicker.getInstance().getChoosedImageBeanList().size() + "/" + MLImagePicker.getInstance().getImageMaxSize() + "）");
            tvImagePreview.setText("（" + imageSize + "）" + getString(R.string.preview));
        } else {
            toolbarRightTitle.setText(getString(R.string.send));
            tvImagePreview.setText(getString(R.string.preview));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MLImagePicker.CODE_REQUEST_IMG_PREVIEW_LIST) {
            if (resultCode == MLImagePicker.CODE_RESULT_IMG_PREVIEW_LIST) {
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra(MLImagePicker.RESULT_IMG_LIST, (ArrayList<? extends Parcelable>) MLImagePicker.getInstance().getChoosedImageBeanList());
                setResult(MLImagePicker.CODE_RESULT_IMG_LIST, intent);
                finish();
            }
        } else if (requestCode == MLImagePicker.CODE_REQUEST_LOCAL_PHOTO) {
            if (resultCode == MLImagePicker.CODE_RESULT_LOCAL_PHOTO) {
                Intent intent = new Intent();
                intent.putExtra(MLImagePicker.LOCATION_DEAL_URL, data.getStringExtra(MLImagePicker.LOCATION_DEAL_URL));
                setResult(MLImagePicker.CODE_RESULT_LOCAL_PHOTO, intent);
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MLImagePicker.getInstance().getChoosedImageBeanList().clear();
        // FIXME: 17/11/20 退出时清空内存
        MLImagePicker.getInstance().getImageLoadFrame().clearMemoryCache(this);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
