package com.example.commonlibrary.utils;

import android.content.Context;

/**
 * Created by MajinBuu on 2017/5/12 0012.
 *
 * @overView 手机机型工具类
 */

public class PhoneModel {

    /**
     * 获取手机型号
     */
    public static String getAndroidDisplayVersion(Context context) {
        String androidDisplay = null;
        androidDisplay = android.os.Build.DISPLAY;
        return androidDisplay;
    }
}
