package com.ymnet.onekeyclean.cleanmore.junk.notifycationmanager;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.utils.C;

import java.util.ArrayList;

/**
 * 通知管理，包括但不限于连usb通知，应用更新提醒通知，2345助手版本更新通知
 *
 * @author janan
 * @date 2014-3-20
 * @since Version 1.5.0
 */
public class NotificationManager2345 {

    private Context mContext;

    /**
     * usb连接的时候notification id
     */
    public static final int USB_CONNETION = 1 << 2;

    /**
     * 有应用可以更新的时候的notification id
     */
    public static final int APPS_UPDATED = 1 << 3;

    /**
     * 2345macket版本有更新的时候的 notification id
     */
    public static final int PHONEMAKET_UPDATED = 1 << 4;

    /**
     * 本应用有更新时的notification id
     */
    public static final int NOTIFI_UPDATE_2345 = 1 << 5;

    /**
     * 本应用新版本下载好了，等待安装的notification id
     */
    public static final int NOTIFI_INSTALL_2345 = 1 << 6;

    /**
     * 本应用正在下载新版本的notification id
     */
    public static final int NOTIFI_DOWNLOADING_2345 = 1 << 7;

    /**
     * 垃圾清理 id
     */
    public static final int NOTIFI_CLAEAN_2345 = 1 << 8;

    /**
     * 推送消息模板1的notification id
     */
    public static final int NOTIFI_JPUSH_TEMPLATE_TITLE = 1 << 9;

    /**
     * 推送消息模板2的notification id
     */
    public static final int NOTIFI_JPUSH_TEMPLATE_BANNER = 1 << 10;

    /**
     * 静默安装成功
     */
    public static final int NOTIFI_SILENCE_INSTALL_SUCCEED = 1 << 11;

    /**
     * 自动下载完成
     */
    public static final int NOTIFI_AUTO_DOWNLOAD_COMPLETED = 1 << 12;

    /**
     * 自动升级完成
     */
    public static final int NOTIFI_AUTO_INSTALL_COMPLETED = 1 << 13;

    /**
     * 应用下载中
     */
    public static final int NOTIFY_APP_DOWNLOADING = 1 << 14;

    /**
     * 应用下载暂停
     */
    public static final int NOTIFY_APP_DOWNLOAD_PAUSED = NOTIFY_APP_DOWNLOADING + 1;

    /**
     * 应用下载成功
     */
    public static final int NOTIFY_APP_DOWNLOAD_COMPLETED = NOTIFY_APP_DOWNLOAD_PAUSED + 1;

    /**
     * 应用安装成功
     */
    public static final int NOTIFY_APP_INSTALL_SUCCESS = NOTIFY_APP_DOWNLOAD_COMPLETED + 1;

    /**
     * 系统的notificationmanager
     */
    private NotificationManager sysNotificationManager;

    private NotificationManager2345(Context context) {
        this.mContext = context;
        checkSystemNotificationManager();
    }

    private static NotificationManager2345 managerInstance;

    public static NotificationManager2345 getInstance(Context context) {
        if (managerInstance == null) {
            managerInstance = new NotificationManager2345(context);
        }
        return managerInstance;
    }

    /**
     * 发送usb连接通知
     */
    /*public void notifyUSBConnection() {
        Intent intent = new Intent(
                NotificationHandleReceiver.ACTION_STARTMARKET);
        intent.setClass(mContext, NotificationHandleReceiver.class);
        MyNotification usbNotification = MyNotification
                .makeData(USB_CONNETION, "已通过USB连接电脑", "点击启动2345手机助手，免费获取海量资源",
                        R.mipmap.ic_launcher, mContext)
                .setNotificationFlag(
                        Notification.FLAG_NO_CLEAR
                                | Notification.FLAG_ONGOING_EVENT)
                .setIsAutoCancel(false)
                .setTicker("已通过USB连接电脑")
                .setLargeIcon(null)
                .setTouchIntent(intent);
        nofify(usbNotification, null);
    }*/

    /**
     * Show app update {@link Notification} initialized with {@code data}.
     *
     * @param data used to initialize update notification.
     */
   /* public void notifyAppUpdate(AppUpdateData data) {
        Pair<String, Integer> params;

        if (data == null || (params = data.getViewParams()) == null || params.first == null || data.getContentIntent() == null) {
            return;
        }

        RemoteViews remoteViews = new RemoteViews(params.first, params.second);
        remoteViews.setOnClickPendingIntent(R.id.update_button, data.getClickableIntent());
        for (int i = 0; i < data.getViewBitmap().size(); i++) {
            remoteViews.setImageViewBitmap(data.getViewBitmap().keyAt(i), data.getViewBitmap().valueAt(i));
        }

        for (int i = 0; i < data.getViewText().size(); i++) {
            remoteViews.setTextViewText(data.getViewText().keyAt(i), data.getViewText().valueAt(i));
        }

        for (int i = 0; i < data.getViewVisibility().size(); i++) {
            remoteViews.setViewVisibility(data.getViewVisibility().keyAt(i), data.getViewVisibility().valueAt(i));
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(C.get());
        builder.setContent(remoteViews)
                .setAutoCancel(true)
                .setTicker(data.getTicker())
                .setContentIntent(data.getContentIntent())
                .setSmallIcon(R.drawable.small_notificaiton_icon)
        ;

        Notification notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        if (Build.VERSION.SDK_INT < 11) {
            notification.contentView = remoteViews;
        }
        notify(data.getNotificationId(), notification);
    }*/

    /**
     * Show JPush {@link Notification} initialized with {@code data}.
     *
     * @param data used to initialize JPush notification.
     */
   /* public void showJPushNotification(JPushData data) {
        Pair<String, Integer> params;
        if (data == null || data.getContentIntent() == null || (params = data.getViewParams()) == null || params.first == null) {
            return;
        }

        RemoteViews remoteViews = new RemoteViews(params.first, params.second);
        Pair<Integer, Bitmap> viewBitmap = data.getViewBitmap();
        if (viewBitmap != null && viewBitmap.second != null) {
            remoteViews.setImageViewBitmap(viewBitmap.first, viewBitmap.second);
        }

        if (data.getStyleId() == PushContants.TEMPLATE_STYLE_ICON_TITLE_CONTENT) {
            initJPushTitleStyle(remoteViews, data);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(C.get());
        builder.setContent(remoteViews)
                .setAutoCancel(true)
                .setTicker(data.getTicker())
                .setContentIntent(data.getContentIntent())
                .setDeleteIntent(data.getDeleteIntent())
                .setSmallIcon(R.drawable.small_notificaiton_icon);

        Notification notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        if (Build.VERSION.SDK_INT < 11) {
            notification.contentView = remoteViews;
        }
        notify(data.getNotificationId(), notification);
    }*/

    /*private void initJPushTitleStyle(RemoteViews remoteViews, JPushData data) {
        if (remoteViews == null) {
            return;
        }

        Pair<Integer, Integer> params = data.getViewBackground();
        if (params != null) {
            remoteViews.setInt(params.first, "setBackgroundColor", params.second);
        }

        if (data.getClickableIntent() != null) {
            remoteViews.setOnClickPendingIntent(R.id.check_out, data.getClickableIntent());
        }

        for (int i = 0; i < data.getViewText().size(); i++) {
            remoteViews.setTextViewText(data.getViewText().keyAt(i), data.getViewText().valueAt(i));
        }

        for (int i = 0; i < data.getTextColor().size(); i++) {
            remoteViews.setTextColor(data.getTextColor().keyAt(i), data.getTextColor().valueAt(i));
        }

        for (int i = 0; i < data.getViewVisibility().size(); i++) {
            remoteViews.setViewVisibility(data.getViewVisibility().keyAt(i), data.getViewVisibility().valueAt(i));
        }

    }*/

    private void notify(int id, Notification notification) {
        if (notification == null) {
            return;
        }

        NotificationManager manager = (NotificationManager) C.get().getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(id, notification);
        }
    }

    /**
     * Call this method when you want a downloading display notification.
     *
     * @param data used to initialize {@link Notification}.
     * @return a new {@link Notification} used to notify user the latest download task status.
     */
    /*public Notification getDownloadingNotification(DownloadNotifyData data) {
        Pair<String, Integer> params;
        if (data == null || data.getContentIntent() == null || (params = data.getViewParams()) == null) {
            return null;
        }

        RemoteViews remoteViews = new RemoteViews(params.first, params.second);

        for (int i = 0; i < data.getViewText().size(); i++) {
            remoteViews.setTextViewText(data.getViewText().keyAt(i), data.getViewText().valueAt(i));
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(C.get());
        builder.setContent(remoteViews)
                .setAutoCancel(false) // 设置自动清除
                .setOngoing(true)
                .setTicker(data.getTicker())
                .setContentIntent(data.getContentIntent())
                .setSmallIcon(R.drawable.small_notificaiton_icon);

        Notification notification = builder.build();
        if (Build.VERSION.SDK_INT < 11) {
            notification.contentView = remoteViews;
        }
        return notification;
    }*/

    /**
     * Call this method when download was paused by some reasons
     * include network status change and human factor.
     *
     * @param size    the size of applications to be paused.
     * @param content the content of custom {@link Notification}.
     */
 /*   @Deprecated
    public void notifyDownloadPaused(int size, CharSequence content) {
        CharSequence[] contents = new CharSequence[]{
                Html.fromHtml(mContext.getString(R.string.download_paused_title, size)),
                content
        };
        showDownloadNotification(contents, NOTIFY_APP_DOWNLOAD_PAUSED, null);

    }*/

    /**
     * Call this method when download was paused by some reasons
     * include network status change and human factor.
     *
     * @param size            the size of applications to be paused.
     * @param contentStringId the content of custom {@link Notification}.
     */
    /*public void notifyDownloadPaused(int size, int contentStringId) {
        CharSequence[] contents = new CharSequence[]{
                Html.fromHtml(mContext.getString(R.string.download_paused_title, size)),
                C.get().getString(contentStringId)
        };

        if (R.string.item_net_error == contentStringId) {
            StatisticSpec.sendEvent(StatisticEventContants.notifcation_downloadpause0_show);
            showDownloadNotification(contents, NOTIFY_APP_DOWNLOAD_PAUSED, StatisticEventContants.notifcation_downloadpause0_click);
        } else if (R.string.download_paused_content == contentStringId) {
            StatisticSpec.sendEvent(StatisticEventContants.notifcation_downloadpause1_show);
            showDownloadNotification(contents, NOTIFY_APP_DOWNLOAD_PAUSED, StatisticEventContants.notifcation_downloadpause1_click);
        }
    }

    private boolean isSystemAppOrRooted() {
        boolean isSystemApp = SilenceInstaller.getInstance().hasSystemPermission();
        boolean isRootedByMe = SettingUtils.checkLastSetValue(mContext, SettingUtils.SETTING.AUTO_INSTALL, false)
                && RootStatus.getInstance().isRooted();
        return isSystemApp || isRootedByMe;
    }*/

    /**
     * Call this method when you need to notify user a new app was download successfully.
     */
   /* public void notifyDownloadCompleted() {
        if (isSystemAppOrRooted()) {
            return;
        }

        LinkedHashMap<String, DownloadInfo> desiredMap = getCurrentCompletedMap();
        int size = desiredMap.size();
        if (size < 1) {
            return;
        }

        CharSequence title = "";
        CharSequence content = "";
        String appName = getDesiredAppName(desiredMap);
        if (size == 1) {
            title = appName + "下载成功";
            content = "点击查看";
        } else {
            title = Html.fromHtml(mContext.getString(R.string.download_completed_title, size));
            content = appName;

        }
        CharSequence[] contents = new CharSequence[]{title, content};
        showDownloadNotification(contents, NOTIFY_APP_DOWNLOAD_COMPLETED, StatisticEventContants.notifcation_downloadfinish_click);
        StatisticSpec.sendEvent(StatisticEventContants.notifcation_downloadfinish_show);
    }*/
/*
    public String getDesiredAppName(Map<String, DownloadInfo> map) {
        String appName = "";
        if (map == null || map.size() < 1) {
            return appName;
        }

        if (map.size() == 1) {
            for (String key : map.keySet()) {
                DownloadInfo info = map.get(key);
                appName = getAppName(info);
            }
        } else {
            List<Map.Entry<String, DownloadInfo>> list = new ArrayList<>(map.entrySet());
            ListIterator<Map.Entry<String, DownloadInfo>> iterator = list.listIterator(list.size());
            while (iterator.hasPrevious()) {
                Map.Entry<String, DownloadInfo> entry = iterator.previous();
                DownloadInfo info = entry.getValue();
                if ("".equals(appName)) {
                    appName = getAppName(info);
                } else {
                    appName += "，" + getAppName(info);
                }
            }
        }
        return appName;
    }*/

   /* private LinkedHashMap<String, DownloadInfo> getCurrentCompletedMap() {
        DownloadManager manager = DownloadManager.getInstance(mContext);
        Set<String> keySet = manager.getCompletedDownloads().keySet();
        String uninstalledPkg = SPUtil.getUninstalledPkgName(mContext);
        LinkedHashMap<String, DownloadInfo> desiredMap = new LinkedHashMap<>();

        for (String key : keySet) {
            DownloadInfo info = manager.getCompletedDownloads().get(key);
            desiredMap.put(key, info);
            String pkgName = info.mPackageName;
            if (uninstalledPkg.contains(pkgName)) {
                if (!TextUtils.isEmpty(info.mFileName) && new File(info.mFileName).exists()) {
                    SPUtil.deleteUninstalledPkgName(mContext, pkgName);
                } else {
                    desiredMap.remove(key);
                }
            }
        }

        return desiredMap;
    }*/

    /**
     * Call this method when you need to notify user a new app was installed successfully.
     *
     * @param info used to initialize {@link Notification}.
     */
   /* public void notifyInstallSuccess(DownloadInfo info) {
        if (info == null || SPUtil.checkSystemPermission(mContext)) {
            return;
        }

        Intent intent = new Intent(NotificationHandleReceiver.ACTION_OPEN_INSTALLED_APP);
        intent.setClass(mContext, NotificationHandleReceiver.class);
        intent.putExtra(NotificationHandleReceiver.EXTRA_PACKAGE_NAME, info.mPackageName);
        intent.putExtra("pushevent", StatisticEventContants.notifcation_install_click);
        PendingIntent contentIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent clickIntent = new Intent(intent);
        clickIntent.putExtra("pushevent", StatisticEventContants.notifcation_install_open);
        PendingIntent clickPendingIntent = PendingIntent.getBroadcast(mContext, 1, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.install_success_notification);
        remoteViews.setTextViewText(R.id.notification_title, info.mTitle);
        remoteViews.setTextViewText(R.id.notification_content, mContext.getString(R.string.install_success_content));
        remoteViews.setOnClickPendingIntent(R.id.open_now, clickPendingIntent);
        Bitmap bitmap = AppsUtils.getIcon(mContext, info.mPackageName);
        if (bitmap != null) {
            remoteViews.setImageViewBitmap(R.id.app_icon, bitmap);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(C.get());
        builder.setContent(remoteViews)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.small_notificaiton_icon)
                .setContentIntent(contentIntent);
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        if (Build.VERSION.SDK_INT < 11) {
            notification.contentView = remoteViews;
        }

        notify(NOTIFY_APP_INSTALL_SUCCESS, notification);

        StatisticSpec.sendEvent(StatisticEventContants.notifcation_install_show);
    }*/

   /* public String getAppName(DownloadInfo info) {
        if (info == null) {
            return "";
        }

        String appName = info.mPackageName;
        if (info.mPackageName != null && (info.mPackageName.startsWith("http") || info.mPackageName.startsWith("https"))) {
            if (info.mPackageName.contains(".apk")) {
                String[] parts = info.mPackageName.split("/");
                for (String part : parts) {
                    if (part.contains(".apk")) {
                        appName = part;
                        break;
                    }
                }
            }
        } else {
            appName = info.mTitle;
        }

        return appName;
    }*/

    /*private void showDownloadNotification(CharSequence[] contents, int id, String pushEvent) {
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.download_notification_template);

        remoteViews.setTextViewText(R.id.notification_title, contents[0]);
        remoteViews.setTextViewText(R.id.notification_content, contents[1]);
        remoteViews.setTextViewText(R.id.notification_time, DateFormatUtils.format(System.currentTimeMillis(), "HH:mm"));

        NotificationCompat.Builder builder = new NotificationCompat.Builder(C.get());
        builder.setContent(remoteViews)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.small_notificaiton_icon);
//        if(NOTIFY_APP_DOWNLOADING == id){
        builder.setContentIntent(getJumpDownloadIntent(pushEvent));
//        }else if(NOTIFY_APP_DOWNLOAD_PAUSED == id){
//            builder.setContentIntent(getPausedIntent())
//        }

        Notification notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        if (Build.VERSION.SDK_INT < 11) {
            notification.contentView = remoteViews;
        }

        notify(id, notification);
    }*/

    /**
     * @return a new {@link PendingIntent} that used to jump to {@link com.ymnet.download.activity.DownloadActivity}.
     */
    public PendingIntent getJumpDownloadIntent(String pushevent) {
        Intent touchIntent = new Intent(NotificationHandleReceiver.ACTION_DOWNLOAD_ymnet);
        touchIntent.setClass(C.get(), NotificationHandleReceiver.class);
        if (!TextUtils.isEmpty(pushevent)) {
            touchIntent.putExtra("pushevent", pushevent);
        }

        return PendingIntent.getBroadcast(C.get(), 0, touchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent getPausedIntent(String clickPushEvent) {
        Intent touchIntent = new Intent(NotificationHandleReceiver.ACTION_DOWNLOAD_ymnet);
        touchIntent.setClass(C.get(), NotificationHandleReceiver.class);
        touchIntent.putExtra("pushevent", clickPushEvent);
        return PendingIntent.getBroadcast(C.get(), 0, touchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * 发送垃圾清理通知
     */
    public void notifyClean2345(String strSize) {
        // 自定义view与点击事件
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.notifition_clean);

        remoteViews.setTextViewText(R.id.tv_clean_size, Html.fromHtml(mContext.getString(R.string.clean_find_size, strSize)));
        // 整体notification的点击事件
        Intent touchIntent = new Intent(NotificationHandleReceiver.ACTION_CLEAN_ymnet);
        touchIntent.setClass(mContext, NotificationHandleReceiver.class);

        Intent buttonIntent = new Intent(NotificationHandleReceiver.ACTION_CLEAN_ymnet);
        buttonIntent.setClass(mContext, NotificationHandleReceiver.class);
        ClickButton button = new ClickButton(R.id.iv_clean, touchIntent);
        ArrayList<ClickButton> buttons = new ArrayList<ClickButton>();
        buttons.add(button);

        Intent deleteIntent = new Intent(NotificationHandleReceiver.ACTION_CLEAN_CANCELED);
        deleteIntent.setClass(mContext, NotificationHandleReceiver.class);
        PendingIntent deletePendingIntent
                = PendingIntent.getBroadcast(mContext, 0, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // 生成本地notification
        MyNotification notifition = MyNotification.
                makeDataWithoutLargeIcon(NOTIFI_CLAEAN_2345, null, "清理可加速运行", mContext)
                .setNotificationFlag(Notification.FLAG_AUTO_CANCEL)
                .setIsAutoCancel(true)
                .setTouchIntent(touchIntent).setRemoteViews(remoteViews)
                .setButtons(buttons)
                .setDeleteIntent(deletePendingIntent);


        nofify(notifition, null);
//        StatisticSpec.sendEvent(StatisticEventContants.notification_clean_interval);
    }

    /**
     * 发送新安装应用更新通知
     */
    public void notifyNewAppNeededUpdate(PackageInfo info) {

    }

    /**
     * 关闭usb连接通知
     */
    public void cancelUSBConnectionNotification() {
        cancelNotification(USB_CONNETION);
    }

    public void cancelNotification(int notificationId) {
        cancelNotification(notificationId, null);
    }

    /**
     * 发出通知
     */
    public void notify(MyNotification mNotification) {
        nofify(mNotification, null);
    }

    private void nofify(MyNotification mNotification, String tag) {
        //In case of NullPointerException
        if (null == sysNotificationManager || null == mNotification) {
            return;
        }

        sysNotificationManager.notify(mNotification.notifiId,
                mNotification.changeToSystemNotification(mContext));
    }

    private void cancelNotification(int notificationId, String tag) {
        if (null == sysNotificationManager) {
            return;
        }
        sysNotificationManager.cancel(notificationId);
    }

    private void checkSystemNotificationManager() {
        if (sysNotificationManager == null) {
            sysNotificationManager = (NotificationManager) mContext
                    .getSystemService(Context.NOTIFICATION_SERVICE);
        }
    }

    /**
     * 要提示的notification的信息 the information of notification-to-be（上帝能看懂不？）
     */
    public static class MyNotification {
        private Integer notifiId;

        private String title;

        private String content;

        private String ticker;

        private String contentInfo;

        private Bitmap largeIcon;

        private Integer smallIcon;

        private Uri sound;

        private Intent touchIntent;

        private ArrayList<ClickButton> buttons;

        private ArrayList<ClickButton> bottomButtons;

        private Integer notificationFlag;

        private Boolean isAutoCancel;

        private Context context;

        private RemoteViews romoteViews;

        private PendingIntent deleteIntent;

        private NotificationCompat.Style style;

        private MyNotification() {

        }

        /**
         * 生成信息对象
         *
         * @param notifyId      notification的id
         * @param notifyTitle   notification的标题信息
         * @param notifyContent notification的信息栏
         * @param smallIconId   notification小图标的id(必选项，否则将发不出通知)
         *//*

        public static MyNotification makeData(int notifyId, String notifyTitle,
                                              String notifyContent, Integer smallIconId, Context context) {
            return makeData(notifyId, notifyTitle, notifyContent, context);
        }

        *//**
         * 生成信息对象
         *
         * @param notifyId      notification的id
         * @param notifyTitle   notification的标题信息
         * @param notifyContent notification的信息栏
         *//*
        private static MyNotification makeData(int notifyId,
                                               String notifyTitle, String notifyContent, Context context) {
            MyNotification notification = new MyNotification();
            notification.notifiId = notifyId;
            notification.title = notifyTitle;
            notification.content = notifyContent;
            notification.smallIcon = R.drawable.small_notificaiton_icon;
            notification.context = context;
            notification.largeIcon = BitmapFactory.decodeResource(
                    context.getResources(), R.drawable.ic_launcher);
            return notification;
        }

        *//**
         * 生成不带大图标的信息对象
         *
         * @param notifyId      notification的id
         * @param notifyTitle   notification的标题信息
         * @param notifyContent notification的信息栏
         * @param context
         * @return
         */
        public static MyNotification makeDataWithoutLargeIcon(int notifyId,
                                                              String notifyTitle, String notifyContent, Context context) {
            MyNotification notification = new MyNotification();
            notification.notifiId = notifyId;
            notification.title = notifyTitle;
            notification.content = notifyContent;
            notification.smallIcon = R.drawable.small_notificaiton_icon;
            notification.context = context;
            return notification;
        }

        /**
         * 添加ticker（状态栏的通知提示语）
         *//*
        public MyNotification setTicker(String ticker) {
            this.ticker = ticker;
            return this;
        }

        *//**
         * 添加contentinfo（android版本3.0以上使用）
         *//*
        public MyNotification setContentInfo(String contentInfo) {
            this.contentInfo = contentInfo;
            return this;
        }

        *//**
         * 设置自定义view
         */
        public MyNotification setRemoteViews(RemoteViews views) {
            this.romoteViews = views;
            return this;
        }

        /**
         * 添加remoteview中的按钮
         */
        public MyNotification setButtons(ArrayList<ClickButton> buttons) {
            this.buttons = buttons;
            return this;
        }

        /**
         * 添加底部按钮
         *//*
        public MyNotification setBottomButtons(ArrayList<ClickButton> buttons) {
            this.bottomButtons = buttons;
            return this;
        }

        *//**
         * 设置大图标(android版本3.0以上使用)
         *//*
        public MyNotification setLargeIcon(Bitmap largeIcon) {
            this.largeIcon = largeIcon;
            return this;
        }

        *//**
         * 添加通知提示音
         *//*
        public MyNotification setSound(Uri sound) {
            this.sound = sound;
            return this;
        }

        *//**
         * 添加通知执行intent
         */
        public MyNotification setTouchIntent(Intent intent) {
            this.touchIntent = intent;
            return this;
        }

        /**
         * 添加通知的flag
         */
        public MyNotification setNotificationFlag(int flag) {
            this.notificationFlag = flag;
            return this;
        }

        /**
         * 设置点击后是否自动关闭
         */
        public MyNotification setIsAutoCancel(boolean autoCancel) {
            this.isAutoCancel = autoCancel;
            return this;
        }

        /**
         * 设置notification销毁监听
         *
         * @param deleteIntelt
         * @return
         */
        public MyNotification setDeleteIntent(PendingIntent deleteIntelt) {
            this.deleteIntent = deleteIntelt;
            return this;
        }

        /**
         * 设置notification style  4.1以上可用
         *
         * @param style
         * @return
         *//*
        public MyNotification setStyle(NotificationCompat.Style style) {
            this.style = style;
            return this;
        }


        *//**
         * 将MyNotification转换成系统的notification
         */
        @SuppressLint("NewApi")
        private Notification changeToSystemNotification(Context context) {
            Notification notification = null;
            NotificationCompat.Builder builder = new NotificationCompat.Builder(
                    context);
            if (isNotNull(title)) {
                builder.setContentTitle(title);

            }
            if (isNotNull(content)) {
                builder.setContentText(content);
            }
            if (isNotNull(smallIcon)) {
                builder.setSmallIcon(smallIcon);
            }
            if (isNotNull(ticker)) {
                builder.setTicker(ticker);// 通知出现时的提示语
            }
            if (isNotNull(contentInfo)) {
                builder.setContentInfo(contentInfo);// 3.1以上，会放在右下角
            }
            if (isNotNull(isAutoCancel)) {
                builder.setAutoCancel(isAutoCancel);
            }
            if (isNotNull(touchIntent)) {

                PendingIntent touchPendingIntent = PendingIntent.getBroadcast(
                        context, 0, touchIntent, 0);

                builder.setContentIntent(touchPendingIntent);
            }
            if (isNotNull(largeIcon)) {
                builder.setLargeIcon(largeIcon);
            }
            if (isNotNull(sound)) {
                builder.setSound(sound);
            }
            if (isNotNull(buttons) && buttons.size() > 0) {
                int size = buttons.size();
                if (romoteViews != null) {
                    for (int i = 0; i < size; i++) {
                        ClickButton button = buttons.get(i);
                        PendingIntent buttonIntent = PendingIntent
                                .getBroadcast(context, 0, button.actionIntent,
                                        0);
                        romoteViews.setOnClickPendingIntent(button.viewId,
                                buttonIntent);
                    }
                }
            }

            if (isNotNull(romoteViews)) {
                builder.setContent(romoteViews);
            }

            if (isNotNull(deleteIntent)) {
                builder.setDeleteIntent(deleteIntent);
            }

            //4.1以上才可用
            if (isNotNull(style))
                builder.setStyle(style);


            notification = builder.build();
            if (isNotNull(notificationFlag)) {
                notification.flags = notificationFlag.intValue();
            }

            if (Build.VERSION.SDK_INT < 14 && isNotNull(romoteViews)) {
                notification.contentView = romoteViews;
            }

            if (Build.VERSION.SDK_INT >= 16 && isNotNull(romoteViews)) {
                notification.bigContentView = romoteViews;
            }

            return notification;
        }

        private boolean isNotNull(Object obj) {
            return obj != null;
        }
    }

}
