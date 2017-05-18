package com.example.commonlibrary.utils;

import android.os.Build;

/**
 * Created by MajinBuu on 2017/5/12 0012.
 *
 * @overView 手机机型工具类
 */

public class PhoneModel {

    /**
     * 获取手机版本号
     */
    public static String getAndroidDisplayVersion() {
        String androidDisplay = null;
        androidDisplay = android.os.Build.DISPLAY;
        return androidDisplay;
    }
    /**
     * 获取手机型号
     */
    public static String getAndroidModel() {
        return Build.MODEL;
    }
}
