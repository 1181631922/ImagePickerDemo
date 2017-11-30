package com.fanyafeng.imagepicker.ui;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.fanyafeng.imagepicker.MLImagePicker;
import com.fanyafeng.imagepicker.R;


public class ImageBaseActivity extends AppCompatActivity implements View.OnClickListener {

    protected Toolbar toolbar;

    /**
     * android md风格
     * <p>
     * 标题栏颜色为toolbar色值+16（印象中是）
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            if (MLImagePicker.getInstance().getStatusBarColor() == Color.parseColor("#303F9F") &&
                    MLImagePicker.getInstance().getToolbarColor() == Color.parseColor("#3F51B5")) {
                return;
            } else {
                window.setStatusBarColor(MLImagePicker.getInstance().getStatusBarColor());
                if (MLImagePicker.getInstance().isLight()) {
                    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                } else {
                    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);

            if (MLImagePicker.getInstance().getNavigationIconRes() != 0) {
                toolbar.setNavigationIcon(MLImagePicker.getInstance().getNavigationIconRes());
            }

            if (MLImagePicker.getInstance().getToolbarColor() != Color.parseColor("#3F51B5")) {
                toolbar.setBackgroundColor(MLImagePicker.getInstance().getToolbarColor());
            }

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        MLImagePicker.getInstance().onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        MLImagePicker.getInstance().onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {

    }
}
