package com.fanyafeng.imagepicker.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Author： fanyafeng
 * Date： 17/11/16 上午10:53
 * Email: fanyafeng@live.cn
 */
public class ImageListBean extends BaseBean implements Parcelable, Comparable<ImageListBean> {
    public String imgName;
    public String imgPath;
    public long imgSize;
    public int imgWidth;
    public int imgHeight;
    public String imgType;
    public long imgCreateTime;

    public ImageListBean() {
    }

    protected ImageListBean(Parcel in) {
        imgName = in.readString();
        imgPath = in.readString();
        imgSize = in.readLong();
        imgWidth = in.readInt();
        imgHeight = in.readInt();
        imgType = in.readString();
        imgCreateTime = in.readLong();
    }

    public static final Creator<ImageListBean> CREATOR = new Creator<ImageListBean>() {
        @Override
        public ImageListBean createFromParcel(Parcel in) {
            return new ImageListBean(in);
        }

        @Override
        public ImageListBean[] newArray(int size) {
            return new ImageListBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imgName);
        dest.writeString(imgPath);
        dest.writeLong(imgSize);
        dest.writeInt(imgWidth);
        dest.writeInt(imgHeight);
        dest.writeString(imgType);
        dest.writeLong(imgCreateTime);
    }

    @Override
    public int compareTo(@NonNull ImageListBean o) {
        if (this.imgCreateTime > o.imgCreateTime) {
            return 1;
        } else if (this.imgCreateTime < o.imgCreateTime) {
            return -1;
        } else {
            return 0;
        }
    }
}
