package com.ymnet.onekeyclean.cleanmore.uninstall.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

/**
 * Created by MajinBuu on 2017/6/22 0022.
 *
 * @overView 获取系统安装,卸载,替换应用的广播
 */

public class AppInstallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PackageManager manager = context.getPackageManager();
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
//            Toast.makeText(context, "安装成功" + packageName, Toast.LENGTH_LONG).show();
        }
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
//            Toast.makeText(context, "卸载成功" + packageName, Toast.LENGTH_LONG).show();
            Intent intent1 = new Intent("uninstall_app");
            intent1.putExtra("pkg_name", "pkg_name");
            context.sendStickyBroadcast(intent1);
//            context.sendBroadcast();
        }
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
//            Toast.makeText(context, "替换成功" + packageName, Toast.LENGTH_LONG).show();
        }
    }
}
