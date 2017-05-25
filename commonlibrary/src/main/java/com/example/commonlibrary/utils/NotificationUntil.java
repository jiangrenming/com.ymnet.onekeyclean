package com.example.commonlibrary.utils;

import android.content.Context;
import android.os.Build;

import com.umeng.analytics.MobclickAgent;

import java.lang.reflect.Method;

/**
 * Created by MajinBuu on 2017/5/25 0025.
 *
 * @overView 通知栏收放相关工具类
 */

public class NotificationUntil {
    /**
     * 收起通知栏
     */
    public static void collapseStatusBar(Context context) {

        try {//"statusbar"
            Object statusBarManager = context.getSystemService("statusbar");
            Method collapse;

            if (Build.VERSION.SDK_INT <= 16) {
                collapse = statusBarManager.getClass().getMethod("collapse");
            } else {
                collapse = statusBarManager.getClass().getMethod("collapsePanels");
            }
            collapse.invoke(statusBarManager);
        } catch (Exception localException) {
            localException.printStackTrace();
            MobclickAgent.reportError(context, localException.toString());
        }
    }
}
