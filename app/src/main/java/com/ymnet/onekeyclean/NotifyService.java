package com.ymnet.onekeyclean;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.example.commonlibrary.utils.PhoneModel;
import com.ymnet.killbackground.view.CleanActivity;
import com.ymnet.onekeyclean.cleanmore.junk.SilverActivity;
import com.ymnet.onekeyclean.cleanmore.qq.activity.QQActivity;
import com.ymnet.onekeyclean.cleanmore.utils.C;
import com.ymnet.onekeyclean.cleanmore.wechat.WeChatActivity;

import static android.R.attr.id;

/**
 * Created by MajinBuu on 2017/5/11.
 *
 * @overView 服务启动常驻通知栏.
 */

public class NotifyService extends Service {

    private static final int REQUEST_CODE1  = 4;
    private static final int ID             = 102;
    private static final int REQUEST_CODE01 = 1;
    private static final int REQUEST_CODE02 = 2;
    private static final int REQUEST_CODE03 = 3;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager        mNotificationManager;
    private String mAndroidModel;

    public NotifyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("");
        //启动通知栏
        initNotification();
    }

    private void initNotification() {
        //初始化服务
        initService();

        //获取手机机型
        /**
         * ONEPLUS A3010
         * SM-C5000    三星SM-C5000
         * 8681-A01   奇酷 8681-A10
         */
        //        String version = PhoneModel.getAndroidDisplayVersion();
        mAndroidModel = PhoneModel.getAndroidModel();

        System.out.println("---------------androidModel:" + mAndroidModel);

        //展示常驻通知栏
        showPermanentNotification(mAndroidModel);
        //常驻通知栏被删时再次开启
//        deleteNotification();
    }
    //勿删!预留常驻通知栏唤醒功能
    private void deleteNotification() {
       /* AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent11 = new Intent(this, CoreService.class);
        intent.setAction(ACTION_WAKE_USBHELPER);
        PendingIntent pendingIntent11 = PendingIntent.getService(this, 0, intent11, PendingIntent.FLAG_UPDATE_CURRENT);
        // TODO: 2017/5/12 0012 常驻通知栏唤醒
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 60 * 60 * 1000L, pendingIntent11);*/
    }

    private void showPermanentNotification(String androidModel) {
        RemoteViews remoteViews = null;
        if (androidModel.contains("Redmi") || androidModel.contains("MI")) {
            remoteViews = new RemoteViews(C.get().getPackageName(), R.layout.notification_view);
            System.out.println("---------------androidModel:我是小米系列");
        } else {
            remoteViews = new RemoteViews(C.get().getPackageName(), R.layout.notification_view_withoutbg);
        }
        //奇酷手机更换图标为白色
        if (androidModel.contains("8681")) {
            System.out.println("---------------androidModel:奇酷手机");
            remoteViews.setImageViewResource(R.id.iv_head, R.mipmap.onekeyclean_white);
            remoteViews.setImageViewResource(R.id.iv_wechat, R.mipmap.wechat_white);
            remoteViews.setImageViewResource(R.id.iv_qq, R.mipmap.qq_white);
            remoteViews.setImageViewResource(R.id.iv_deep, R.mipmap.brush_white);
            remoteViews.setImageViewResource(R.id.iv_setting, R.mipmap.setting_white);
        }

        //一键加速
        Intent intent2 = new Intent(C.get(), CleanActivity.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(C.get(), REQUEST_CODE1, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.ll_head, pendingIntent);

        //微信清理
        Intent intent3 = new Intent(C.get(), WeChatActivity.class);
        intent3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent1 = PendingIntent.getActivity(C.get(), REQUEST_CODE01, intent3, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.ll_wechat, pendingIntent1);

        //QQ清理
        Intent intent4 = new Intent(C.get(), QQActivity.class);
        intent4.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent4 = PendingIntent.getActivity(C.get(), REQUEST_CODE02, intent4, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.ll_qq, pendingIntent4);

        //深度清理
        Intent intent5 = new Intent(C.get(), SilverActivity.class);
        intent5.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent5 = PendingIntent.getActivity(C.get(), REQUEST_CODE03, intent5, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.ll_deep, pendingIntent5);

        //系统设置
        Intent intent6 = new Intent(Settings.ACTION_SETTINGS);
        //                intent6.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent6 = PendingIntent.getActivity(C.get(), REQUEST_CODE03, intent6, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.ll_setting, pendingIntent6);

        mBuilder = new NotificationCompat.Builder(C.get());

        Intent intent7 = new Intent(this, NotifyService.class);
        PendingIntent intent8 = PendingIntent.getService(this, 2, intent7, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification mNotification = mBuilder.setSmallIcon(R.mipmap.brush)
                .setTicker("一键清理为您服务")/*.setContentTitle("常驻测试2").setContentText("常驻通知:去不掉我的3")*/
                .setContent(remoteViews)
                .setContentIntent(getDefalutIntent(0))
                .setPriority(Notification.PRIORITY_MAX)
                .setDeleteIntent(intent8)
                //                .setContentIntent(pendingIntent5)
                .build();
        mNotification.flags = Notification.FLAG_ONGOING_EVENT;
        mNotification.defaults = 8;//没有声音震动三色光
        //        mNotification.when = System.currentTimeMillis();
//           mNotificationManager.notify(ID, mNotification);
        /**
         *  后台运行的服务被强行kill掉，是系统回收内存的一种机制，
         *  要想避免这种情况可以通过startForeground让服务前台运行，
         *  当stopservice的时候通过stopForeground去掉。
         */
        startForeground(id, mNotification);
    }

    private void initService() {
        mNotificationManager = (NotificationManager) C.get().getSystemService(NOTIFICATION_SERVICE);
    }

    /**
     * 获取默认的pendingIntent,为了防止2.3及以下版本报错
     * flags属性: 在顶部常驻:Notification.FLAG_ONGOING_EVENT
     * 点击去除： Notification.FLAG_AUTO_CANCEL
     */
    public PendingIntent getDefalutIntent(int flags) {
        PendingIntent pendingIntent = PendingIntent.getActivity(C.get(), 1, new Intent(), flags);
        return pendingIntent;
    }

}
