package com.ymnet.onekeyclean.cleanmore.wechat;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.umeng.analytics.MobclickAgent;
import com.ymnet.onekeyclean.cleanmore.utils.C;

/**
 * Created with IntelliJ IDEA.
 * Date: 4/7/16
 * Author: zhangcm
 */
public class BaseApplication extends Application {

    public static String versionname;
    public static String packegename;
    public static int    versioncode;

    public static BaseApplication sApplication;

    private static String sChannel = "";


    public static BaseApplication getInstance() {
        if (sApplication == null) {
            sApplication = new BaseApplication();
        }
        return sApplication;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        sApplication = this;
    }

    public static String getChannel() {
        if (TextUtils.isEmpty(sChannel)) {
            if (sApplication != null) {
                PackageManager manager = sApplication.getPackageManager();
                if (manager != null) {
                    try {
                        ApplicationInfo appInfo = manager.getApplicationInfo(packegename,
                                PackageManager.GET_META_DATA);
                        sChannel = appInfo.metaData.getString("UMENG_CHANNEL");
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                        MobclickAgent.reportError(C.get(), e.fillInStackTrace());
                    }
                }
            }
        }
        return sChannel;
    }

    public static void setChannel(String channel) {
        sChannel = channel;
    }
}
