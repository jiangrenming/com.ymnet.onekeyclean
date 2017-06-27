package com.ymnet.onekeyclean.cleanmore.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.ymnet.onekeyclean.cleanmore.utils.NotificationUtil;
import com.example.commonlibrary.utils.PhoneModel;
import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.junk.SilverActivity;
import com.ymnet.onekeyclean.cleanmore.qq.activity.QQActivity;
import com.ymnet.onekeyclean.cleanmore.utils.C;
import com.ymnet.onekeyclean.cleanmore.utils.OnekeyField;
import com.ymnet.onekeyclean.cleanmore.wechat.WeChatActivity;

import java.io.Serializable;


/**
 * Created by MajinBuu on 2017/5/11.
 *
 * @overView 服务启动常驻通知栏.
 */

public class NotifyService extends Service implements Serializable {
    private static final String TAG             = "NotifyService";
    private static final int    REQUEST_CODE1   = 4;
    private static final int    ID              = 102;
    private static final int    REQUEST_CODE01  = 1;
    private static final int    REQUEST_CODE02  = 2;
    private static final int    REQUEST_CODE03  = 3;
    private static final int    REQUEST_CODE04  = 4;
    private static final int    REQUEST_CODE05  = 5;
    private static final int    REQUEST_CODE06  = 6;
    private static final String OPEN_FLASHLIGHT = "openflashlight";
    private static final String MODEL           = "androidmodel";
    private NotificationCompat.Builder mBuilder;
    private NotificationManager        mNotificationManager;
    private String                     mAndroidModel;
    private static boolean status   = false;
    private        Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Intent intent = new Intent(C.get(), FlashlightService.class);
                    String action = intent.getAction();
                    stopService(intent);
                    break;
            }
        }
    };
    private RemoteViews  remoteViews;
    private Notification mNotification;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean status = intent.getBooleanExtra("status", false);
            Log.d(TAG, "onReceive: " + status);

            changeFlashLightColor(status);

            mHandler.removeMessages(0);
            //手电筒服务 在接收到广播的关闭状态5秒后 停止服务
            if (!status) {
                Log.d(TAG, "onReceive: 五秒后停止FlashlightService");
                mHandler.sendEmptyMessageDelayed(0, 5000);
            }
        }
    };
    private boolean mDarkNotificationTheme;

    public NotifyService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Intent accountAuth = new Intent(this, HomeTabActivity.class);
        accountAuth.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(accountAuth);

        registerBroadCastReceiver();
        //启动通知栏
        initNotification();
    }

    private void registerBroadCastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("flashlight_status");
        this.registerReceiver(mReceiver, filter);
    }

    private void initNotification() {
        //初始化服务
        initService();

        //展示常驻通知栏
        showPermanentNotification();
    }

    private void showPermanentNotification() {
        RemoteViews remoteViews = null;
        remoteViews = getRemoteViews();
        //奇酷手机更换图标为白色
        mDarkNotificationTheme = NotificationUtil.isDarkNotificationTheme(C.get());
        Log.d(TAG, "darkNotificationTheme:dark-" + mDarkNotificationTheme);
        if (mDarkNotificationTheme) {
            //白色图标
            remoteViews.setImageViewResource(R.id.iv_head, R.drawable.onekeyclean_white);
            remoteViews.setImageViewResource(R.id.iv_wechat, R.drawable.wechat_white);
            remoteViews.setImageViewResource(R.id.iv_qq, R.drawable.qq_white);
            remoteViews.setImageViewResource(R.id.iv_deep, R.drawable.brush_white);
            //            remoteViews.setImageViewResource(R.id.iv_deep, R.drawable.qq_white);
            remoteViews.setImageViewResource(R.id.iv_flashlight, R.drawable.flashlight_white);
            remoteViews.setImageViewResource(R.id.iv_setting, R.drawable.setting_white);
            /*下面这种或者这种remoteViews.setInt(R.id.tv_head, "setTextColor", Color.parseColor("#ffffff"));*/
            remoteViews.setTextColor(R.id.tv_head, Color.parseColor("#ffffff"));
            remoteViews.setTextColor(R.id.tv_wechat, Color.parseColor("#ffffff"));
            remoteViews.setTextColor(R.id.tv_qq, Color.parseColor("#ffffff"));
            remoteViews.setTextColor(R.id.tv_deep, Color.parseColor("#ffffff"));
            remoteViews.setTextColor(R.id.tv_flashlight, Color.parseColor("#ffffff"));
            remoteViews.setTextColor(R.id.tv_setting, Color.parseColor("#ffffff"));
        }
        /*if (PhoneModel.matchModel("8681", "SM-", "OPPO"*//*, "HUAWEI"*//*,*//*"ONEPLUS",*//*"Le", "M5"*//*,"Coolpad"*//*)) {
            System.out.println("---------------androidModel:奇酷,一加,OPPO,奇酷,华为");
        }*/

        //一键加速
        Intent intent2 = new Intent(C.get(), StatisticReceiver.class);
        intent2.putExtra(OnekeyField.KEY, OnekeyField.KILLBACKGROUND);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(C.get(), REQUEST_CODE01, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.ll_head, pendingIntent);

        //微信清理
                Intent intent3 = new Intent(C.get(), WeChatActivity.class);
//        Intent intent3 = new Intent(C.get(), DeviceManagerReceiver.class);

        intent3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent1 = PendingIntent.getActivity(C.get(), REQUEST_CODE02, intent3, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.ll_wechat, pendingIntent1);

        //QQ清理
        Intent intent4 = new Intent(C.get(), QQActivity.class);
        intent4.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent4 = PendingIntent.getActivity(C.get(), REQUEST_CODE03, intent4, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.ll_qq, pendingIntent4);

        //垃圾清理
        Intent intent5 = new Intent(C.get(), SilverActivity.class);
        intent5.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent5 = PendingIntent.getActivity(C.get(), REQUEST_CODE04, intent5, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.ll_deep, pendingIntent5);

        //手电筒
        Intent intent7 = new Intent(this, FlashlightService.class);
        intent7.putExtra(OPEN_FLASHLIGHT, status);
        if (PhoneModel.matchModel("vivo", "Coolpad")) {//需要收起通知栏的机型
            intent7.putExtra(MODEL, true);
        } else {
            intent7.putExtra(MODEL, false);
        }
        status = !status;

        PendingIntent pendingIntent7 = PendingIntent.getService(C.get(), REQUEST_CODE05, intent7, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.ll_flashlight, pendingIntent7);

        //系统设置
                Intent intent6 = new Intent(this, StatisticReceiver.class);

        intent6.putExtra(OnekeyField.KEY, OnekeyField.SETTINGS);
        PendingIntent pendingIntent6 = PendingIntent.getBroadcast(C.get(), REQUEST_CODE06, intent6, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.ll_setting, pendingIntent6);

        mBuilder = new NotificationCompat.Builder(C.get());

        mNotification = getNotification(mBuilder, remoteViews);
        mNotification.flags = Notification.FLAG_ONGOING_EVENT;
        mNotification.defaults = 8;//没有声音震动三色光
        //        mNotification.when = System.currentTimeMillis();
        //           mNotificationManager.notify(ID, mNotification);
        /**
         *  后台运行的服务被强行kill掉，是系统回收内存的一种机制，
         *  要想避免这种情况可以通过startForeground让服务前台运行，
         *  当stopservice的时候通过stopForeground去掉。
         */
        startForeground(ID, mNotification);
    }


    private void changeFlashLightColor(boolean status) {
        if (status) {
            remoteViews.setImageViewResource(R.id.iv_flashlight, R.drawable.flashlight_open);
            remoteViews.setTextColor(R.id.tv_flashlight, Color.parseColor("#1B98D9"));
            //PhoneModel.matchModel("8681", "SM-", "OPPO"/*, "HUAWEI"*/,/*"ONEPLUS",*/"Le", "M5"/*,"Coolpad"*/)
        } else if (mDarkNotificationTheme) {
            remoteViews.setImageViewResource(R.id.iv_flashlight, R.drawable.flashlight_white);
            remoteViews.setTextColor(R.id.tv_flashlight, Color.WHITE);
        } else {
            remoteViews.setImageViewResource(R.id.iv_flashlight, R.drawable.flashlight);
            remoteViews.setTextColor(R.id.tv_flashlight, Color.parseColor("#545352"));
        }
        mNotificationManager.notify(ID, mNotification);

    }

    public Notification getNotification(NotificationCompat.Builder mBuilder, RemoteViews remoteViews) {

        return mBuilder.setSmallIcon(R.drawable.onekeyclean)
                .setTicker("一键清理为您服务")/*.setContentTitle("常驻测试2").setContentText("常驻通知:去不掉我的3")*/
                .setContent(remoteViews)
                .setContentIntent(getDefalutIntent(0))
                .setPriority(Notification.PRIORITY_MAX)
                .build();
    }

    @NonNull
    public RemoteViews getRemoteViews() {
        //获取手机机型
        mAndroidModel = PhoneModel.getAndroidModel();
        System.out.println("---------------androidModel:" + mAndroidModel);
        if (PhoneModel.matchModel("vivo")) {
            remoteViews = new RemoteViews(C.get().getPackageName(), R.layout.notification_view_white);
        } else {
            remoteViews = new RemoteViews(C.get().getPackageName(), R.layout.notification_view_withoutbg);
        }
        return remoteViews;
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

    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);
        if (mReceiver != null) {
            this.unregisterReceiver(mReceiver);
            Log.d(TAG, "unbindService: 服务解绑了");
        }
    }
}
