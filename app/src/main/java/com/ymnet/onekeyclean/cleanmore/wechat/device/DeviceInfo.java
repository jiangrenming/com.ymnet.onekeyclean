package com.ymnet.onekeyclean.cleanmore.wechat.device;

import android.app.Activity;
import android.util.DisplayMetrics;


/**
 * Created by wangduheng26 on 4/20/16.
 */
public class DeviceInfo {
    private static int screenW;
    private static int screenH;

    public static int getScreenWidth(Activity a) {
        if (a == null) return screenW;
        if (screenW == 0) {
            DisplayMetrics dm = new DisplayMetrics();
            a.getWindowManager().getDefaultDisplay().getMetrics(dm);
            screenW = dm.widthPixels;
            screenH = dm.heightPixels;
        }
        return screenW;
    }

    public static int getScreenHeight(Activity a) {
        if (a == null) return screenH;
        if (screenH == 0) {
            DisplayMetrics dm = new DisplayMetrics();
            a.getWindowManager().getDefaultDisplay().getMetrics(dm);
            screenW = dm.widthPixels;
            screenH = dm.heightPixels;
        }
        return screenH;
    }
}
