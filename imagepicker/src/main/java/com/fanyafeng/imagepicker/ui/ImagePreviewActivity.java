package com.fanyafeng.imagepicker.ui;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.fanyafeng.imagepicker.MLImagePicker;
import com.fanyafeng.imagepicker.R;
import com.fanyafeng.imagepicker.adapter.PreviewListAdapter;
import com.fanyafeng.imagepicker.bean.ImageBean;
import com.fanyafeng.imagepicker.view.photoview.HackyViewPager;
import com.fanyafeng.imagepicker.view.photoview.PhotoView;

import java.io.File;
import java.util.ArrayList;

public class ImagePreviewActivity extends ImageBaseActivity {
    private final static String TAG = ImagePreviewActivity.class.getSimpleName();

    private Toolbar toolbar;
    private TextView toolbarCenterTitle;
    private TextView toolbarRightTitle;
    //
    private HackyViewPager vpPreview;

    private RecyclerView rvPreviewList;
    private PreviewListAdapter previewListAdapter;

    private LinearLayout layoutImagePreview;
    private CheckBox cbPreview;

    private RelativeLayout layoutPreviewBottom;

    private int currPosition = 0;

    private int screenWidth;
    private int screenHeight;

    private ArrayList<ImageBean> imageList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        imageList = getIntent().getParcelableArrayListExtra(MLImagePicker.RESULT_IMG_LIST);
        currPosition = getIntent().getIntExtra("position", 0);

        screenHeight = getScreenHeight(this);
        screenWidth = getScreenWidth(this);

        initView();
        initData();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        if (imageList.size() > 0) {
            toolbar.setTitle((currPosition + 1) + "/" + imageList.size());
        } else {
            toolbar.setTitle(getString(R.string.img_picker_name));
        }
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.go_back_left_arrow);

        layoutPreviewBottom = findViewById(R.id.layoutPreviewBottom);
        if (MLImagePicker.getInstance().getToolbarColor() != Color.parseColor("#3F51B5")) {
            layoutPreviewBottom.setBackgroundColor(MLImagePicker.getInstance().getToolbarColor());
        }
        toolbarCenterTitle = findViewById(R.id.toolbarCenterTitle);
        toolbarRightTitle = findViewById(R.id.toolbarRightTitle);
        toolbarRightTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MLImagePicker.getInstance().getChoosedImageBeanList().size() > 0) {
                    setResult(MLImagePicker.CODE_RESULT_IMG_PREVIEW_LIST);
                    finish();
                } else {
                    Toast.makeText(ImagePreviewActivity.this, getString(R.string.picture_no_choose), Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (MLImagePicker.getInstance().getChoosedImageBeanList().size() > 0) {
            toolbarRightTitle.setText(getString(R.string.send) + "（" + MLImagePicker.getInstance().getChoosedImageBeanList().size() + "/" + MLImagePicker.getInstance().getImageMaxSize() + "）");
        } else {
            toolbarRightTitle.setText(getString(R.string.send));
        }

        vpPreview = findViewById(R.id.vpPreview);
        rvPreviewList = findViewById(R.id.rvPreviewList);

        layoutImagePreview = findViewById(R.id.layoutImagePreview);
        layoutImagePreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MLImagePicker.getInstance().getChoosedImageBeanList().size() >= MLImagePicker.getInstance().getImageMaxSize() && !cbPreview.isChecked()) {
                    Toast.makeText(ImagePreviewActivity.this, getString(R.string.picture_max_limit) + MLImagePicker.getInstance().getImageMaxSize() + getString(R.string.picture_unit), Toast.LENGTH_SHORT).show();
                    cbPreview.setChecked(false);
                    return;
                }
                if (cbPreview.isChecked()) {
                    MLImagePicker.getInstance().getChoosedImageBeanList().remove(imageList.get(currPosition));
                    cbPreview.setChecked(false);
                } else {
                    MLImagePicker.getInstance().getChoosedImageBeanList().add(imageList.get(currPosition));
                    cbPreview.setChecked(true);
                }
                Log.e(TAG, "current imgUrl:" + imageList.get(currPosition).imgPath);

                if (MLImagePicker.getInstance().getChoosedImageBeanList().size() > 0) {
                    toolbarRightTitle.setText(getString(R.string.send) + "（" + MLImagePicker.getInstance().getChoosedImageBeanList().size() + "/" + MLImagePicker.getInstance().getImageMaxSize() + "）");
                    if (!rvPreviewList.isShown()) {
                        rvPreviewList.setVisibility(View.VISIBLE);
                    }
                } else {
                    toolbarRightTitle.setText(getString(R.string.send));
                    rvPreviewList.setVisibility(View.GONE);
                }

                previewListAdapter.notifyDataSetChanged();
            }
        });
        cbPreview = findViewById(R.id.cbPreview);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvPreviewList.setLayoutManager(linearLayoutManager);
    }

    private void initData() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (MLImagePicker.getInstance().getChoosedImageBeanList().contains(imageList.get(currPosition))) {
            cbPreview.setChecked(true);
        } else {
            cbPreview.setChecked(false);
        }

        previewListAdapter = new PreviewListAdapter(this, MLImagePicker.getInstance().getChoosedImageBeanList());
        rvPreviewList.setAdapter(previewListAdapter);
        previewListAdapter.setOnItemClickListener(new PreviewListAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, ImageBean imageBean, int position) {
                if (imageList.contains(imageBean)) {
                    vpPreview.setCurrentItem(imageList.indexOf(imageBean), false);
                    previewListAdapter.notifyDataSetChanged();
                } else {
                    vpPreview.setCurrentItem(0, false);
                    previewListAdapter.setCurrImageBean(null);
                }

            }
        });

        if (MLImagePicker.getInstance().getChoosedImageBeanList().size() > 0) {
            rvPreviewList.setVisibility(View.VISIBLE);
        } else {
            rvPreviewList.setVisibility(View.GONE);
        }


        vpPreview.setAdapter(new PhotoViewAdapter());
        vpPreview.setCurrentItem(currPosition);
        vpPreview.setOffscreenPageLimit(3);
        vpPreview.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                toolbar.setTitle((position + 1) + "/" + imageList.size());

                if (MLImagePicker.getInstance().getChoosedImageBeanList().contains(imageList.get(position))) {
                    cbPreview.setChecked(true);
                } else {
                    cbPreview.setChecked(false);
                }

                currPosition = position;

                previewListAdapter.setCurrImageBean(imageList.get(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });

        previewListAdapter.setCurrImageBean(imageList.get(currPosition));
    }

    class PhotoViewAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imageList.size();
        }


        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            final PhotoView view = new PhotoView(ImagePreviewActivity.this);
            view.setScaleType(ImageView.ScaleType.FIT_CENTER);
            if (MLImagePicker.getInstance().getImageLoadFrame().isFresco()) {
                view.setImageUri(Uri.fromFile(new File(imageList.get(position).imgPath)), screenWidth, screenHeight);
            } else {
                MLImagePicker.getInstance().getImageLoadFrame().displayImage(ImagePreviewActivity.this, imageList.get(position).imgPath, view, screenWidth, screenHeight);
            }

            container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    private int getScreenHeight(Context context) {
        if (context != null) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.heightPixels;
        } else {
            return 1920;
        }
    }

    private int getScreenWidth(Context context) {
        if (context != null) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.widthPixels;
        } else {
            return 1080;
        }
    }
}
