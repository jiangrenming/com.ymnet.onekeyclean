package com.ymnet.onekeyclean.cleanmore.utils;

import android.app.Notification;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import java.lang.reflect.Method;
import java.util.LinkedList;

/**
 * Created by MajinBuu on 2017/5/25 0025.
 *
 * @overView 通知栏收放相关工具类
 */

public class NotificationUtil {
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
            MobclickAgent.reportError(context, localException.fillInStackTrace());
        }
    }

    public static boolean isDarkNotificationTheme(Context context) {
        return !isSimilarColor(Color.BLACK, getNotificationColor(context));
    }

    /**
     * 获取通知栏颜色
     * @param context
     * @return
     */
    public static int getNotificationColor(Context context) {
        NotificationCompat.Builder builder=new NotificationCompat.Builder(context);
        Notification notification=builder.build();
        int layoutId=notification.contentView.getLayoutId();
        ViewGroup viewGroup= (ViewGroup) LayoutInflater.from(context).inflate(layoutId, null, false);
        if (viewGroup.findViewById(android.R.id.title)!=null) {
            return ((TextView) viewGroup.findViewById(android.R.id.title)).getCurrentTextColor();
        }
        return findColor(viewGroup);
    }

    private static boolean isSimilarColor(int baseColor, int color) {
        Log.d("NotificationUtil", "color:" + color);
        int simpleBaseColor=baseColor|0xff000000;
        int simpleColor=color|0xff000000;
        int baseRed=Color.red(simpleBaseColor)-Color.red(simpleColor);
        int baseGreen=Color.green(simpleBaseColor)-Color.green(simpleColor);
        int baseBlue=Color.blue(simpleBaseColor)-Color.blue(simpleColor);
        double value=Math.sqrt(baseRed*baseRed+baseGreen*baseGreen+baseBlue*baseBlue);
        Log.d("NotificationUtil", "value:" + value);

        int red = (0xFF0000 & color) >>> 16;
        int green = (0xFF00 & color) >>> 8;
        int blue = color & 0xFF;
        double colorValue = (int) (0.3D * red + 0.59D * green + 0.11D * blue);
        Log.d("NotificationUtil", "colorValue:" + colorValue);
        if (value<180.0) {
            return true;
        }
        return false;
    }

    private static int findColor(ViewGroup viewGroupSource) {
        int color=Color.TRANSPARENT;
        LinkedList<ViewGroup> viewGroups=new LinkedList<>();
        viewGroups.add(viewGroupSource);
        while (viewGroups.size()>0) {
            ViewGroup viewGroup1=viewGroups.getFirst();
            for (int i = 0; i < viewGroup1.getChildCount(); i++) {
                if (viewGroup1.getChildAt(i) instanceof ViewGroup) {
                    viewGroups.add((ViewGroup) viewGroup1.getChildAt(i));
                }
                else if (viewGroup1.getChildAt(i) instanceof TextView) {
                    if (((TextView) viewGroup1.getChildAt(i)).getCurrentTextColor()!=-1) {
                        color=((TextView) viewGroup1.getChildAt(i)).getCurrentTextColor();
                    }
                }
            }
            viewGroups.remove(viewGroup1);
        }
        return color;
    }
}
