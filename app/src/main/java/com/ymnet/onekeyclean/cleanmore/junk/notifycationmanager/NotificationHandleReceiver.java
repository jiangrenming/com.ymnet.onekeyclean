package com.ymnet.onekeyclean.cleanmore.junk.notifycationmanager;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * 通知（不包括下载相关）注册的receiver，用以接收通知的操作
 *
 * @author janan
 * @date 2014-3-19
 * @since Version 1.5.0
 */
public class NotificationHandleReceiver extends BroadcastReceiver {

    public static final String ACTION_STARTMARKET = "action.ymnet.startmarket.fromnotification";

    public static final String ACTION_UPDATESELF = "action.ymnet.updateself.fromnotification";

    public static final String ACTION_UPDATEANAPP = "action.ymnet.updateanapp.fromnotification";

    public static final String ACTION_UPDATEAPPS = "action.ymnet.updateapps.fromnotification";

    public static final String ACTION_UPDATEAPPSALL = "action.ymnet.updateallapps.fromnotification";

    public static final String ACTION_UPDATE_ymnet_DIALOG = "action.ymnet.updateymnet.dialog.fromnotification";

    public static final String ACTION_UPDATE_ymnet_DOWNLOAD = "action.ymnet.updateymnet.download.fromnotification";

    public static final String ACTION_INSTALL_ymnet_DIALOG = "action.ymnet.installymnet.dialog.fromnotification";

    public static final String ACTION_INSTALL_ymnet_NOW = "action.ymnet.installymnet.now.fromnotification";

    public static final String ACTION_CLEAN_ymnet = "action.ymnet.clean.fromnotifition";

    public static final String ACTION_JPUSH_TEMPLATE_TITLE = "action.ymnet.jpush.template.title.fromnotifition";

    public static final String ACTION_JPUSH_TEMPLATE_BANNER = "action.ymnet.jpush.template.banner.fromnotifition";

    public static final String ACTION_JPUSH_CANCELED = "action.ymnet.jpush.canceled.fromnotification";

    public static final String ACTION_CLEAN_CANCELED = "action.ymnet.clean.canceled";

    public static final String ACTION_ACCELERATE_ymnet = "action.ymnet.accelerate.fromnotifition";

    public static final String ACTION_SEARCH_ymnet = "action.ymnet.search.fromnotifition";

    public static final String ACTION_MANAGE_ymnet = "action.ymnet.manage.fromnotifition";

    public static final String ACTION_NOTIFICATION_SETTINGS_ymnet = "action.ymnet.notification.settings.fromnotifition";

    public static final String ACTION_DOWNLOAD_ymnet = "action.ymnet.download.fromnotifition";

    public static final String ACTION_OPEN_INSTALLED_APP = "action.ymnet.open.app";

    public static final String EXTRA_FROM_NOTIFICATION = "from_notification";

    public static final String EXTRA_PACKAGE_NAME = "package_name";

    public static final String KEY_UPDATE_SINGLE = "key_update_single";


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String notification_bar = intent.getStringExtra("notification_bar");

    }

    @SuppressLint("WrongConstant")
    //关闭statusbar，自定义notification按钮时，很多机器在点击按钮时statusbar不会关闭，需强制关闭
    private void collapseStatusBar(Context context) {
    }

}
