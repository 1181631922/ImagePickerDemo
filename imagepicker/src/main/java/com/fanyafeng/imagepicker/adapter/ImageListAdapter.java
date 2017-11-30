package com.fanyafeng.imagepicker.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.fanyafeng.imagepicker.MLImagePicker;
import com.fanyafeng.imagepicker.R;
import com.fanyafeng.imagepicker.bean.ImageBean;
import com.fanyafeng.imagepicker.ui.ImagePreviewActivity;
import com.fanyafeng.imagepicker.ui.MLTakePhotoActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Author： fanyafeng
 * Date： 17/11/16 上午10:53
 * Email: fanyafeng@live.cn
 */
public class ImageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static String TAG = ImageListAdapter.class.getSimpleName();

    private static int SCREEN_WIDTH;
    private static int ONE_DP;
    private static RelativeLayout.LayoutParams LAYOUTPARAMS;

    private Context context;
    private List<ImageBean> imageBeanList;


    private OnChooseImgChangeListener onChooseImgChangeListener;

    public void setOnChooseImgChangeListener(OnChooseImgChangeListener onChooseImgChangeListener) {
        this.onChooseImgChangeListener = onChooseImgChangeListener;
    }

    public interface OnChooseImgChangeListener {
        void onChooseImgChangeListener();
    }

    public void refreshData(List<ImageBean> imageBeanList) {
        if (imageBeanList == null || imageBeanList.size() == 0) {
            this.imageBeanList = new ArrayList<>();
        } else {
            this.imageBeanList = imageBeanList;
        }
        notifyDataSetChanged();
    }

    public ImageListAdapter(Context context, List<ImageBean> imageBeanList) {
        this.context = context;
        this.imageBeanList = imageBeanList;
        SCREEN_WIDTH = getScreenWidth(context);
        ONE_DP = (int) dip2px(context, 1);
        LAYOUTPARAMS = new RelativeLayout.LayoutParams(SCREEN_WIDTH / 3, SCREEN_WIDTH / 3);
        LAYOUTPARAMS.setMargins(0, (int) (1.5 * ONE_DP), 0, 0);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image_list_layout, parent, false);
        return new ImageListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ImageBean imageBean = imageBeanList.get(position);
        ImageListViewHolder imageListViewHolder = null;
        if (holder instanceof ImageListViewHolder) {
            imageListViewHolder = (ImageListViewHolder) holder;
        }

        switch (position % 3) {
            case 0:
                imageListViewHolder.layoutItemImageList.setPadding(0, 0, 1 * ONE_DP, 0);
                imageListViewHolder.layoutItemImageList.setLayoutParams(LAYOUTPARAMS);
                break;
            case 1:
                imageListViewHolder.layoutItemImageList.setPadding((int) (0.5 * ONE_DP), 0, (int) (0.5 * ONE_DP), 0);
                imageListViewHolder.layoutItemImageList.setLayoutParams(LAYOUTPARAMS);
                break;
            case 2:
                imageListViewHolder.layoutItemImageList.setPadding(1 * ONE_DP, 0, 0, 0);
                imageListViewHolder.layoutItemImageList.setLayoutParams(LAYOUTPARAMS);
                break;
        }


        int width = SCREEN_WIDTH / 3 - ONE_DP;
        if (MLImagePicker.getInstance().getImageLoadFrame().isFresco()) {
            imageListViewHolder.sdvItemImageList.setVisibility(View.VISIBLE);
            MLImagePicker.getInstance().getImageLoadFrame().displayImage(context, imageBean.imgPath, imageListViewHolder.sdvItemImageList, width, width);
        } else {
            imageListViewHolder.ivItemImageList.setVisibility(View.VISIBLE);
            MLImagePicker.getInstance().getImageLoadFrame().displayImage(context, imageBean.imgPath, imageListViewHolder.ivItemImageList, width, width);
        }

        if (MLImagePicker.getInstance().getChooseType() == MLImagePicker.TYPE_CHOOSE_SINGLE) {
            imageListViewHolder.cbItemImageList.setVisibility(View.GONE);
        } else {
            imageListViewHolder.cbItemImageList.setVisibility(View.VISIBLE);
        }

        boolean isChecked = MLImagePicker.getInstance().getChoosedImageBeanList().contains(imageBean);
        imageListViewHolder.cbItemImageList.setChecked(isChecked);
        imageListViewHolder.tvItemImageListShadow.setVisibility(isChecked ? View.VISIBLE : View.GONE);

        imageListViewHolder.cbItemImageList.setOnClickListener(new EditImageCheckListener(imageListViewHolder.cbItemImageList, imageListViewHolder.tvItemImageListShadow, position));


        imageListViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (MLImagePicker.getInstance().getChooseType() == MLImagePicker.TYPE_CHOOSE_SINGLE) {
                    Intent intent = new Intent(context, MLTakePhotoActivity.class);
                    intent.putExtra(MLImagePicker.LOCATION_URL, imageBeanList.get(position).imgPath);
                    ((Activity) context).startActivityForResult(intent, MLImagePicker.CODE_REQUEST_LOCAL_PHOTO);
                } else {
                    Intent intent = new Intent(context, ImagePreviewActivity.class);
                    intent.putParcelableArrayListExtra(MLImagePicker.RESULT_IMG_LIST, (ArrayList<? extends Parcelable>) imageBeanList);
                    intent.putExtra("position", position);
                    ((Activity) context).startActivityForResult(intent, MLImagePicker.CODE_REQUEST_IMG_PREVIEW_LIST);
                }
            }
        });


    }


    @Override
    public int getItemCount() {
        return imageBeanList.size();
    }

    class ImageListViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout layoutItemImageList;
        SimpleDraweeView sdvItemImageList;
        ImageView ivItemImageList;
        CheckBox cbItemImageList;
        TextView tvItemImageListShadow;

        public ImageListViewHolder(View itemView) {
            super(itemView);
            layoutItemImageList = itemView.findViewById(R.id.layoutItemImageList);
            sdvItemImageList = itemView.findViewById(R.id.sdvItemImageList);
            ivItemImageList = itemView.findViewById(R.id.ivItemImageList);
            cbItemImageList = itemView.findViewById(R.id.cbItemImageList);
            tvItemImageListShadow = itemView.findViewById(R.id.tvItemImageListShadow);
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

    private float dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (float) (dipValue * scale + 0.5f);
    }

    class EditImageCheckListener implements View.OnClickListener {

        private CheckBox checkBox;
        private TextView tvItemImageListShadow;
        private int position;

        public EditImageCheckListener(CheckBox checkBox, TextView tvItemImageListShadow, int position) {
            this.checkBox = checkBox;
            this.tvItemImageListShadow = tvItemImageListShadow;
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            if (!checkBox.isChecked()) {
                MLImagePicker.getInstance().getChoosedImageBeanList().remove(imageBeanList.get(position));
            } else {
                if (MLImagePicker.getInstance().getChoosedImageBeanList().size() >= MLImagePicker.getInstance().getImageMaxSize()) {
                    Toast.makeText(context, context.getString(R.string.picture_max_limit) + MLImagePicker.getInstance().getImageMaxSize() + context.getString(R.string.picture_unit), Toast.LENGTH_SHORT).show();
                    checkBox.setChecked(false);
                    return;
                } else {
                    MLImagePicker.getInstance().getChoosedImageBeanList().add(imageBeanList.get(position));
                }
            }
            tvItemImageListShadow.setVisibility(checkBox.isChecked() ? View.VISIBLE : View.GONE);
            if (onChooseImgChangeListener != null) {
                onChooseImgChangeListener.onChooseImgChangeListener();
            }

            Log.e(TAG, MLImagePicker.getInstance().getChoosedImageBeanList().toString());
        }
    }

}
