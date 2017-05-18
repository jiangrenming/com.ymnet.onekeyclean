package com.ymnet.onekeyclean.cleanmore.utils;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

/**
 * Created with IntelliJ IDEA.
 * User: yangning.roy
 * Date: 1/4/15
 * To change this template use File | Settings | File Templates.
 */
public class SecurityAppInfo {

    static {

        try {
//            System.loadLibrary("gnustl_shared");
            System.loadLibrary("appinfo");
        } catch (Exception e) {
            e.printStackTrace();
            MobclickAgent.reportError(C.get(),"com.ymnet.onekeyclean.cleanmore.utils.SecurityAppInfo:"+e.toString());
        }
    }

    private static final String ARM_V7 = "_arm_v7";
    private static final String ARM = "_arm";
    private static final String MIP = "_mip";
    private static final String X86 = "_x86";


    //solid key
    public static native byte[] getSolidKey(Context context);

    public static native boolean checkDexMd5(Context context);

    public static native boolean checkApkMd5(Context context);

    public static native boolean isEmulator(Context context);

    public static native boolean checkSignAndPkgName(Context context);
}
