package com.fanyafeng.imagepickerdemo.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.fanyafeng.imagepicker.MLImagePicker;
import com.fanyafeng.imagepicker.bean.ImageBean;
import com.fanyafeng.imagepicker.ui.ImagePreviewActivity;
import com.fanyafeng.imagepickerdemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Author： fanyafeng
 * Date： 17/11/30 上午10:18
 * Email: fanyafeng@live.cn
 */
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {
    private Context context;
    private List<ImageBean> imageBeanList;

    private static int DP_200;


    public void refrescData(List<ImageBean> imageBeanList) {
        if (imageBeanList != null) {
            this.imageBeanList = imageBeanList;
        } else {
            this.imageBeanList = new ArrayList<>();
        }
        notifyDataSetChanged();
    }

    public MainAdapter(Context context, List<ImageBean> imageBeanList) {
        this.context = context;
        this.imageBeanList = imageBeanList;
        DP_200 = (int) dip2px(context, 200);
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_main_layout, parent, false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, final int position) {
        ImageBean imageBean = imageBeanList.get(position);
        if (MLImagePicker.getInstance().getImageLoadFrame().isFresco()) {
            holder.sdvMain.setVisibility(View.VISIBLE);
            MLImagePicker.getInstance().getImageLoadFrame().displayImage(context, imageBean.imgPath, holder.sdvMain, DP_200, DP_200);
        } else {
            holder.ivMain.setVisibility(View.VISIBLE);
            MLImagePicker.getInstance().getImageLoadFrame().displayImage(context, imageBean.imgPath, holder.ivMain, DP_200, DP_200);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ImagePreviewActivity.class);
                intent.putParcelableArrayListExtra(MLImagePicker.RESULT_IMG_LIST, (ArrayList<? extends Parcelable>) imageBeanList);
                intent.putExtra("position", position);
                intent.putExtra(MLImagePicker.PERVIEW_ONLY, true);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageBeanList.size();
    }

    class MainViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView sdvMain;
        ImageView ivMain;

        public MainViewHolder(View itemView) {
            super(itemView);
            sdvMain = itemView.findViewById(R.id.sdvMain);
            ivMain = itemView.findViewById(R.id.ivMain);
        }
    }

    private float dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (float) (dipValue * scale + 0.5f);
    }
}
