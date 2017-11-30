package com.fanyafeng.imagepicker.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author： fanyafeng
 * Date： 17/11/17 下午2:18
 * Email: fanyafeng@live.cn
 */
public class FolderListBean extends BaseBean implements Parcelable {
    private String imgUrl;
    private String title;
    private boolean isChecked;

    public FolderListBean() {
    }

    protected FolderListBean(Parcel in) {
        imgUrl = in.readString();
        title = in.readString();
        isChecked = in.readByte() != 0;
    }

    public static final Creator<FolderListBean> CREATOR = new Creator<FolderListBean>() {
        @Override
        public FolderListBean createFromParcel(Parcel in) {
            return new FolderListBean(in);
        }

        @Override
        public FolderListBean[] newArray(int size) {
            return new FolderListBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imgUrl);
        dest.writeString(title);
        dest.writeByte((byte) (isChecked ? 1 : 0));
    }

    @Override
    public String toString() {
        return "FolderListBean{" +
                "imgUrl='" + imgUrl + '\'' +
                ", title='" + title + '\'' +
                ", isChecked=" + isChecked +
                '}';
    }
}
