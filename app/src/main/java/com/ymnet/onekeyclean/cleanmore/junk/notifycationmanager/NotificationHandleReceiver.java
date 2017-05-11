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

    public static final String ACTION_STARTMARKET = "action.2345.startmarket.fromnotification";

    public static final String ACTION_UPDATESELF = "action.2345.updateself.fromnotification";

    public static final String ACTION_UPDATEANAPP = "action.2345.updateanapp.fromnotification";

    public static final String ACTION_UPDATEAPPS = "action.2345.updateapps.fromnotification";

    public static final String ACTION_UPDATEAPPSALL = "action.2345.updateallapps.fromnotification";

    public static final String ACTION_UPDATE_2345_DIALOG = "action.2345.update2345.dialog.fromnotification";

    public static final String ACTION_UPDATE_2345_DOWNLOAD = "action.2345.update2345.download.fromnotification";

    public static final String ACTION_INSTALL_2345_DIALOG = "action.2345.install2345.dialog.fromnotification";

    public static final String ACTION_INSTALL_2345_NOW = "action.2345.install2345.now.fromnotification";

    public static final String ACTION_CLEAN_2345 = "action.2345.clean.fromnotifition";

    public static final String ACTION_JPUSH_TEMPLATE_TITLE = "action.2345.jpush.template.title.fromnotifition";

    public static final String ACTION_JPUSH_TEMPLATE_BANNER = "action.2345.jpush.template.banner.fromnotifition";

    public static final String ACTION_JPUSH_CANCELED = "action.2345.jpush.canceled.fromnotification";

    public static final String ACTION_CLEAN_CANCELED = "action.2345.clean.canceled";

    public static final String ACTION_ACCELERATE_2345 = "action.2345.accelerate.fromnotifition";

    public static final String ACTION_SEARCH_2345 = "action.2345.search.fromnotifition";

    public static final String ACTION_MANAGE_2345 = "action.2345.manage.fromnotifition";

    public static final String ACTION_NOTIFICATION_SETTINGS_2345 = "action.2345.notification.settings.fromnotifition";

    public static final String ACTION_DOWNLOAD_2345 = "action.2345.download.fromnotifition";

    public static final String ACTION_OPEN_INSTALLED_APP = "action.2345.open.app";

    public static final String EXTRA_FROM_NOTIFICATION = "from_notification";

    public static final String EXTRA_PACKAGE_NAME = "package_name";

    public static final String KEY_UPDATE_SINGLE = "key_update_single";


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String notification_bar = intent.getStringExtra("notification_bar");
//        StatisticSpec.sendEvent(intent.getStringExtra("pushevent"));
       /* if (ACTION_STARTMARKET.equals(action)) {

            HomeUtils.startHomeClearTop(context, new Intent());
        } else if (ACTION_UPDATEAPPS.equals(action)) {
            collapseStatusBar(context);
            if ("notification_bar_statistic".equals(notification_bar)) {

                Intent it = new Intent(C.get(), CoreService.class);
                it.setAction(CoreService.ACTION_RESIDENT_NOTIFICATION);
                it.putExtra(CoreService.INTENT_ACTION_TYPE, CoreService.FLAG_REMOVE_RED_POINT);
                context.startService(it);
                if (UpdateAppActivity.class.getName().equals(Util.getCurrentTopActivity(context))) {
                    return;
                }
            }

            HomeUtils.startHomeClearTop(context, new Intent().putExtra(
                    HomeUtils.NOTIFY,
                    HomeUtils.NOTIFY_VALUE_OPENHOME_TO_APPMANAGMENT_ACTIVITY));
        } else if (ACTION_UPDATEAPPSALL.equals(action)) {
            Intent pendingIntent = new Intent();
            pendingIntent.putExtra(HomeUtils.NOTIFY, HomeUtils.NOTIFY_VALUE_OPENHOME_TO_APPMANAGMENT_ACTIVITY_AND_DOWNLOADALL);
            pendingIntent.putExtra(KEY_UPDATE_SINGLE, intent.getSerializableExtra(KEY_UPDATE_SINGLE));
            HomeUtils.startHomeClearTop(context, pendingIntent);
            collapseStatusBar(context);
            NotificationManager2345.getInstance(context).cancelNotification(
                    NotificationManager2345.APPS_UPDATED);
        } else if (ACTION_UPDATE_2345_DIALOG.equals(action)) {
//            UpdateUtils.getInstance().showUpdateDialog(null);
        } else if (ACTION_UPDATE_2345_DOWNLOAD.equals(action)) {
            UpdateUtils.getInstance().downloadNow(null);
            collapseStatusBar(context);
            NotificationManager2345.getInstance(context).cancelNotification(
                    NotificationManager2345.NOTIFI_UPDATE_2345);
//            Statistics.onEvent(context, StatisticEventContants.Notification_Updatezhushou);
        } else if (ACTION_INSTALL_2345_DIALOG.equals(action)) {
//            UpdateUtils.getInstance().showInstallDialog(null);
        } else if (ACTION_INSTALL_2345_NOW.equals(action)) {
            UpdateUtils.getInstance().install(null);
            collapseStatusBar(context);
            NotificationManager2345.getInstance(context).cancelNotification(
                    NotificationManager2345.NOTIFI_INSTALL_2345);
//            Statistics.onEvent(context, StatisticEventContants.Notification_Updatezhushou);
        } else if (ACTION_CLEAN_2345.equals(action)) {
            collapseStatusBar(context);
            if ("notification_bar_statistic".equals(notification_bar)) {
                if (SilverActivity.class.getName().equals(Util.getCurrentTopActivity(context))) {
                    return;
                }
            } else {
//                StatisticSpec.sendEvent(StatisticEventContants.notification_clean_interval_click);
            }
            Intent it = new Intent();
            it.putExtra(HomeUtils.NOTIFY, HomeUtils.NOTIFY_VALUE_OPNEHONE_TO_CLEANACTIVITY);
            HomeUtils.startHomeClearTop(context, it);
            NotificationManager2345.getInstance(context).cancelNotification(NotificationManager2345.NOTIFI_CLAEAN_2345);
        } else if (ACTION_JPUSH_TEMPLATE_TITLE.equals(action)) {
            PushInfo info = PushContants.getPushInfo(context);

            if (info != null) {
                Intent i = new Intent();
                i.putExtra(HomeUtils.NOTIFY, HomeUtils.NOTIFY_VALUE_OPNEHONE_TO_JPUSH);
                i.putExtra("contentevent",intent.getStringExtra("contentevent"));
                HomeUtils.startHomeClearTop(context, i);
            }

            collapseStatusBar(context);
            NotificationManager2345.getInstance(context).cancelNotification(NotificationManager2345.NOTIFI_JPUSH_TEMPLATE_TITLE);

        } else if (ACTION_JPUSH_TEMPLATE_BANNER.equals(action)) {
            PushInfo info = PushContants.getPushInfo(context);
            String contentclickevent = intent.getStringExtra("contentclickevent");
            if (info != null) {
                Intent i = new Intent();
                i.putExtra(HomeUtils.NOTIFY, HomeUtils.NOTIFY_VALUE_OPNEHONE_TO_JPUSH);

                i.putExtra("contentclickevent",contentclickevent);
                HomeUtils.startHomeClearTop(context, i);
            }

            collapseStatusBar(context);
            NotificationManager2345.getInstance(context).cancelNotification(NotificationManager2345.NOTIFI_JPUSH_TEMPLATE_BANNER);
        } else if (ACTION_JPUSH_CANCELED.equals(action)) {
            int topicId = intent.getIntExtra("topicId", 0);
//            StatisticSpec.sendEvent(StatisticEventContants.push_delete_id + topicId);
        } else if (ACTION_ACCELERATE_2345.equals(action)) {
            collapseStatusBar(context);

            Intent it = new Intent(context, AccelerateActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(it);

        } else if (ACTION_SEARCH_2345.equals(action)) {
            collapseStatusBar(context);
            if (CleanSearchActivity.class.getName().equals(Util.getCurrentTopActivity(context))) {
                return;
            }

            Intent it = new Intent();
            it.putExtra(HomeUtils.NOTIFY, HomeUtils.NOTIFY_VALUE_OPENHOME_TO_SEARCH);
            HomeUtils.startHomeClearTop(context, it);
        } else if (ACTION_MANAGE_2345.equals(action)) {
            collapseStatusBar(context);
            Intent showIntent = new Intent();
            showIntent.putExtra(HomeUtils.NOTIFY, HomeUtils.NOTIFY_VALUE_OPENHOME_TO_OPEN_MANAGEMENT);
            showIntent.putExtra(EXTRA_FROM_NOTIFICATION, true);
            HomeUtils.startHomeClearTop(context, showIntent);
        } else if (ACTION_NOTIFICATION_SETTINGS_2345.equals(action)) {
            collapseStatusBar(context);
            if (!NotificationSettingsActivity.class.getName().equals(Util.getCurrentTopActivity(context))) {
                Intent showIntent = new Intent();
                showIntent.putExtra(HomeUtils.NOTIFY, HomeUtils.NOTIFY_OPEN_NOTIFICATION_SETTING);
                HomeUtils.startHomeClearTop(context, showIntent);
            }
//            Statistics.onEvent(C.get(), StatisticEventContants.notification_set);
        } else if (ACTION_DOWNLOAD_2345.equals(action)) {
            collapseStatusBar(context);
            Intent showIntent = new Intent();
            showIntent.putExtra(HomeTabActivity.NOTIFI, HomeUtils.NOTIFY_VALUE_OPENHOME_TO_DOWNLOAD_TAB);
            showIntent.putExtra(KEY_SILENCE_INSTALLED_NOTIFY, intent.getBooleanExtra(KEY_SILENCE_INSTALLED_NOTIFY, false));
            showIntent.putExtra(KEY_AUTO_INSTALL_NOTIFY, intent.getBooleanExtra(KEY_AUTO_INSTALL_NOTIFY, false));
            HomeUtils.startHomeClearTop(context, showIntent);
        } else if (ACTION_OPEN_INSTALLED_APP.equals(action)) {
            String packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME);
            Utils.startAppByPackage(context, packageName);
            collapseStatusBar(context);
            NotificationManager2345.getInstance(context).cancelNotification(NotificationManager2345.NOTIFY_APP_INSTALL_SUCCESS);
        } else if (ACTION_CLEAN_CANCELED.equals(action)) {
//            StatisticSpec.sendEvent(StatisticEventContants.notification_clean_interval_cancel);
        }*/

    }

    @SuppressLint("WrongConstant")
    //关闭statusbar，自定义notification按钮时，很多机器在点击按钮时statusbar不会关闭，需强制关闭
    private void collapseStatusBar(Context context) {
       /* Object sbservice = context.getSystemService("statusbar");
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        try {

            Class<?> statusbarManager = Class
                    .forName("android.app.StatusBarManager");
            Method collapse = null;
            if (currentApiVersion <= 16) {
                collapse = statusbarManager.getMethod("collapse");

            } else {
                collapse = statusbarManager.getMethod("collapsePanels");

            }
            collapse.setAccessible(true);
            collapse.invoke(sbservice);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

}
