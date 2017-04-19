package com.example.commonlibrary.utils;

import android.content.Context;

/**
 * Created by MajinBuu on 2017/4/17 0017.
 *
 * @overView 常用单位转换的辅助类
 */

public class DensityUtil {

    private DensityUtil() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static int dp2px(Context context, float dp) {
        float density = context.getResources().getDisplayMetrics().density;//表示1dp有几个像素
        return (int)(dp * density + 0.5F);
    }

    public static int px2dp(Context context, float px) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int)(px / density + 0.5F);
    }

    public static int sp2px(Context context, float sp) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return (int)(sp * scaledDensity + 0.5F);
    }

    public static int px2sp(Context context, float px) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return (int)(px / scaledDensity + 0.5F);
    }
}
