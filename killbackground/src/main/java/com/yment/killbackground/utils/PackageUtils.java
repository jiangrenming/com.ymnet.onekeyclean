package com.yment.killbackground.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/9.
 */
public class PackageUtils {

    //是否安装
    public static boolean isAppInstalled(Context context , String pkgName) {
        boolean installed = false;
        try {
            PackageManager pm = context.getPackageManager();
            try {
                pm.getPackageInfo(pkgName, PackageManager.GET_ACTIVITIES);
                installed = true;
            } catch (PackageManager.NameNotFoundException e) {
                installed = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return installed;
    }

    /**
     * Query the package manager for MAIN/LAUNCHER activities in the supplied package.
     */
    public static List<ResolveInfo> findActivitiesForPackage(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();

        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        List<ResolveInfo> apps = null;

            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            mainIntent.setPackage(packageName);
            apps = packageManager.queryIntentActivities(mainIntent, 0);

        return apps != null ? apps : new ArrayList<ResolveInfo>();
    }

    public static boolean isPackageInsatlled(Context context, ComponentName cmpName) {
        try {
            return findActivity(findActivitiesForPackage(context, cmpName.getPackageName()), cmpName);
        } catch (Exception e) {
            e.printStackTrace();
            return false;

        }
    }

    /**
     * Returns whether <em>apps</em> contains <em>component</em>.
     */
    public static boolean findActivity(List<ResolveInfo> apps, ComponentName component) {
        final String className = component.getClassName();
        for (ResolveInfo info : apps) {
            final ActivityInfo activityInfo = info.activityInfo;
            if (activityInfo.name.equals(className)) {
                return true;
            }
        }
        return false;
    }

    public static void startService(Context context, ResolveInfo info) {
        Intent serviceIntent = new Intent();
        serviceIntent.setComponent(new ComponentName(info.serviceInfo.packageName,info.serviceInfo.name));
        context.startService(serviceIntent);
    }

}
