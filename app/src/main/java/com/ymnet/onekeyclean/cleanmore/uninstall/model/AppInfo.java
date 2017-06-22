package com.ymnet.onekeyclean.cleanmore.uninstall.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MajinBuu on 2017/6/21 0021.
 *
 * @overView 卸载界面数据
 */

public class AppInfo implements Parcelable {


    public String versionName;
    public String appName;
    public String pkgName;
    public Bitmap appIcon;
    public long   size;

    public AppInfo() {
    }

    protected AppInfo(Parcel in) {
        versionName = in.readString();
        appName = in.readString();
        pkgName = in.readString();
        size = in.readLong();

    }

    public static final Creator<AppInfo> CREATOR = new Creator<AppInfo>() {
        @Override
        public AppInfo createFromParcel(Parcel in) {
            return new AppInfo(in);
        }

        @Override
        public AppInfo[] newArray(int size) {
            return new AppInfo[size];
        }
    };

    @Override
    public String toString() {
        return "AppInfo{" +
                "versionName='" + versionName + '\'' +
                ", appName='" + appName + '\'' +
                ", pkgName='" + pkgName + '\'' +
                ", appIcon=" + appIcon +
                ", size=" + size +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(appIcon, flags);
    }

}
