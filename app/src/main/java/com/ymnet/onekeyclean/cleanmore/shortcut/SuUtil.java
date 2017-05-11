package com.ymnet.onekeyclean.cleanmore.shortcut;/*
package com.example.baidumapsevice.shortcut;

import android.text.TextUtils;

import com.example.baidumapsevice.root.RootSupervise;
import com.example.baidumapsevice.root.exception.NoAuthorizationException;

import java.util.List;

public class SuUtil {
    */
/**
     * 结束进程,执行操作调用即可
     *//*

    public static void kill(List<String> packageNames) {
        if (packageNames == null || packageNames.size() == 0) {
            return;
        }
        killProcess(packageNames);
    }

    public static void kill(String packageName){
        if (TextUtils.isEmpty(packageName)) return;
        killProcess(packageName);
    }

    private static void killProcess(String packageName) {
        String cmd = "am force-stop " + packageName + " \n";

        try {
            RootSupervise.requireRoot().execute(cmd);
        } catch (NoAuthorizationException e) {
            e.printStackTrace();
        }
    }

    private static void killProcess(List<String> packageNames) {
        for (String packageName : packageNames) {
            kill(packageName);
        }
    }
}
*/
