package com.fanyafeng.imagepicker.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.fanyafeng.imagepicker.R;


/**
 * Author： fanyafeng
 * Date： 17/11/17 上午10:16
 * Email: fanyafeng@live.cn
 */
public class FolderListDialog extends Dialog {

    private Context context;
    private int layoutRes;

    private LinearLayout layoutFolderList;
    private RecyclerView rvFolderList;

    private FolderListDialog(@NonNull Context context) {
        super(context);
    }

    public FolderListDialog(@NonNull Context context, int layoutRes) {
        super(context, R.style.dialogStyle);
        this.context = context;
        this.layoutRes = layoutRes;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_folder_list_layout);

        layoutFolderList = findViewById(R.id.layoutFolderList);
        rvFolderList = findViewById(R.id.rvFolderList);

        Window window = this.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.x = 0;
        layoutParams.y = (int) dip2px(context, 45);
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = getScreenHeight(context) * 2 / 3;
        window.setWindowAnimations(R.style.dialogStyle);
        this.onWindowAttributesChanged(layoutParams);

        FolderListDialog.this.setCanceledOnTouchOutside(false);
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

    private float dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (float) (dipValue * scale + 0.5f);
    }


}
