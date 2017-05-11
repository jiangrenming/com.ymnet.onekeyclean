package com.ymnet.onekeyclean.cleanmore.junk.mode;

import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2015/1/15.
 */
public class JunkChildApk extends JunkChild {
    public static final int INSTALLED = 0; // 表示已安装，且跟现在这个apk文件是一个版本
    public static final  int UNINSTALLED = 1; // 表示未安装
    public static final  int INSTALLED_UPDATE = 2; // 表示新版本
    public static final  int INSTALLED_OLD = 3; // 表示老版本
    public static  final int BREAK_APK = 4; // 表示破损


    public String   packageName;
    public String   path;
    public String   versionName;
    public int      versionCode;
    public int      installedType;
    public long     fileTime;
    public Drawable icon;
}
