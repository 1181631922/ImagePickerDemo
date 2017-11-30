package com.fanyafeng.imagepicker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.fanyafeng.imagepicker.MLImagePicker;
import com.fanyafeng.imagepicker.R;
import com.fanyafeng.imagepicker.bean.ImageBean;

import java.util.List;

/**
 * Author： fanyafeng
 * Date： 17/11/20 下午4:28
 * Email: fanyafeng@live.cn
 */
public class PreviewListAdapter extends RecyclerView.Adapter<PreviewListAdapter.PreviewListViewHolder> {
    private final static String TAG = PreviewListAdapter.class.getSimpleName();

    private static int DP_60;

    private ImageBean currImageBean = null;

    public void setCurrImageBean(ImageBean currImageBean) {
        this.currImageBean = currImageBean;
        notifyDataSetChanged();
    }

    private Context context;
    private List<ImageBean> imageBeanList;

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClickListener(View view, ImageBean imageBean, int position);
    }

    public PreviewListAdapter(Context context, List<ImageBean> imageBeanList) {
        this.context = context;
        this.imageBeanList = imageBeanList;
        DP_60 = (int) dip2px(context, 60);
    }


    @Override
    public PreviewListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_preview_list_layout, parent, false);
        return new PreviewListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PreviewListViewHolder holder, final int position) {
        final ImageBean imageBean = imageBeanList.get(position);

        if (MLImagePicker.getInstance().getImageLoadFrame().isFresco()) {
            holder.sdvPreviewItem.setVisibility(View.VISIBLE);
            MLImagePicker.getInstance().getImageLoadFrame().displayImage(context, imageBean.imgPath, holder.sdvPreviewItem, DP_60, DP_60);
        } else {
            holder.ivPreviewItem.setVisibility(View.VISIBLE);
            MLImagePicker.getInstance().getImageLoadFrame().displayImage(context, imageBean.imgPath, holder.ivPreviewItem, DP_60, DP_60);
        }

        if (imageBean.equals(currImageBean)) {
            holder.layoutPreviewItem.setBackgroundResource(R.drawable.preview_item_shape);
        } else {
            holder.layoutPreviewItem.setBackgroundResource(R.drawable.preview_item_shape_no_select);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClickListener(v, imageBean, position);
                    Log.e(TAG, imageBean.toString());
                    Log.e(TAG, MLImagePicker.getInstance().getChoosedImageBeanList().toString());
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return imageBeanList.size();
    }

    class PreviewListViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout layoutPreviewItem;
        SimpleDraweeView sdvPreviewItem;
        ImageView ivPreviewItem;

        public PreviewListViewHolder(View itemView) {
            super(itemView);
            layoutPreviewItem = itemView.findViewById(R.id.layoutPreviewItem);
            sdvPreviewItem = itemView.findViewById(R.id.sdvPreviewItem);
            ivPreviewItem = itemView.findViewById(R.id.ivPreviewItem);
        }
    }

    private float dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (float) (dipValue * scale + 0.5f);
    }
}
