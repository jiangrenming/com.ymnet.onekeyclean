package com.ymnet.onekeyclean.cleanmore.junk.autocheck;/*
package com.example.baidumapsevice.junk.autocheck;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;

import com.example.baidumapsevice.junk.ScanHelp;
import com.example.baidumapsevice.junk.alert.CleanAlert;
import com.example.baidumapsevice.junk.clearstrategy.ClearManager;
import com.example.baidumapsevice.junk.mode.InstalledApp;
import com.example.baidumapsevice.junk.notifycationmanager.NotificationManager2345;
import com.example.baidumapsevice.junk.notifycationmanager.medium.ResidentViewData;
import com.example.baidumapsevice.junk.notifycationmanager.presenter.ResidentPresenter;
import com.example.baidumapsevice.junk.notifycationmanager.view.ResidentView;
import com.example.baidumapsevice.junk.root.RootStatus;
import com.example.baidumapsevice.utils.C;
import com.example.baidumapsevice.utils.Utils;
import com.example.baidumapsevice.wechat.MTask;
import com.example.baidumapsevice.wechat.MarketApplication;

import java.util.List;



*/
/**
 * @author janan
 * @date 2014-3-27
 * @类说明 常驻内存的service，用来统计日活用户，监听usb状态
 *//*

public class CoreService extends Service implements ResidentView {

    public static final String TAG = "CoreService";

    public static final String ACTION_SCAN_DUMP = "action_scan_dump";

    public static final String ACTION_WAKE_USBHELPER = "action.wake.usbhelper";

    public static final String ACTION_ROOT = "action.root";

    public static final String ACTION_RESIDENT_NOTIFICATION = "resident_view";

    public static final String INTENT_ACTION_TYPE = "action_type";

    public static final String RED_POINT_STATUS = "red_point";

    public static final String MEMORY_PERCENT = "memory_percent";

    public static final int FLAG_UPDATE_RESIDENT = 0;

    public static final int FLAG_UPDATE_SKIN_STYLE = 1;

    public static final int FLAG_SHOW_RESIDENT = 2;

    public static final int FLAG_UPDATE_RED_POINT = 3;

    public static final int FLAG_UPDATE_MEM_PERCENT = 4;

    public static final int FLAG_REMOVE_RED_POINT = 5;

    public static final boolean HIDE_RED_POINT = false;

    public static final int RESIDENT_NOTIFICATION_ID = 666;

    private AutoResidentReceiver autoReceiver;

    private BatteryReceiver br = new BatteryReceiver();

    private LocationManager mLocationManager;

    private LocationListener mLocationListener;

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }


    private ResidentPresenter mPresenter;

    @Override
    public void onCreate() {

        super.onCreate();

        EventBus.getDefault().register(this);

        SharedPreferences sp = getSharedPreferences(SPUtils.LM_INSTALL_LIST, Context.MODE_PRIVATE);

        if (!TextUtils.isEmpty(Utils.getChannel())) {
            if (Utils.getChannel().startsWith("jf")) {
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(this, CoreService.class);
                intent.setAction(ACTION_WAKE_USBHELPER);
                PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 60 * 60 * 1000L, pendingIntent);

                if (Build.VERSION.SDK_INT > 19) {
                    StartUpHelper.init(getApplicationContext());
                }

                DataCenterObserver session = DataCenterObserver.get(this);
                InstalledApp app = session.getInstalledApp(MarketApplication.packegename);


                if (app.lastUpdateTime > sp.getLong(SPUtils.LM_CHANNEL_SAVED_TIME, 0)) {
                    Editor editor = sp.edit();
                    editor.putLong(SPUtils.LM_CHANNEL_SAVED_TIME, System.currentTimeMillis());
                    if (TextUtils.isEmpty(GlobalParams.UID)) {
                        ChannelUtil.checkLM(this);
                    }
                    editor.putString(SPUtils.LM_CID_KEY, GlobalParams.UID);
                    editor.commit();
                }
            }
        }

        checkUpdate();
        registReceiver();
        upProcess();
        a();

        startDownloadWaiter();

        performRoot();

        //统计热修复版本
        StringBuilder sb = new StringBuilder()
                .append(StatisticEventContants.HOT_FIX_VERSION_ID)
                .append(MarketApplication.versioncode);
        if (MarketApplication.sHotfixVersion > 0) {
            sb.append('.').append(MarketApplication.sHotfixVersion);
        }

        Statistics.onEvent(getApplicationContext(), sb.toString());

        if (Utils.isAboveAPI(Build.VERSION_CODES.GINGERBREAD_MR1)) {
            mPresenter = ResidentFactory.getPresenter(this);
            mPresenter.updateResidentView();
        }

        startLocationListener();
    }

    private void startLocationListener() {
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);//获取手机位置信息
        if (mLocationManager == null) {
            return;
        }

        mLocationListener = new UserLocationListener();

        try {
            List<String> providers = mLocationManager.getProviders(true);
            if (providers == null) {
                return;
            }

            Location location = null;
            for (String provider : providers) {
                mLocationManager.requestLocationUpdates(provider, 0, 0, mLocationListener);
                location = mLocationManager.getLastKnownLocation(provider);
                if (location != null) {
                    break;
                }
            }

            if (location != null) {
                String locationStr = location.getLongitude() + "," + location.getLatitude();
                SPUtil.putLocationInfo(this, locationStr);
                mLocationManager.removeUpdates(mLocationListener);
            }

        } catch (Exception e) {
            e.printStackTrace();
            try {
                mLocationManager.removeUpdates(mLocationListener);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }


    private void a() {
        Intent intent = new Intent(this, ActionService.class);
        intent.setAction(ActionService.ACTION_A);
        startService(intent);
    }


    public void onEventMainThread(UpdateRedPointEvent event) {
        if (mPresenter != null) {
            mPresenter.updateRedPoint(event.getStatus());
        }
    }


    private void startDownloadWaiter() {
        Intent intent = new Intent(this, DownloadWaiter.class);
        startService(intent);
    }


    private void performRoot() {
        if (!BuildConfig.ROOT_TOGGLE) return;

        final RootManager rootManager = RootManager.getInstance();
        if (!rootManager.isRooting() && !RootStatus.getInstance().isRootedBySDK()) {

            MTask.BACKGROUND_EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    boolean result = rootManager.getRoot(getApplicationContext(), false);
                    RootStatus.getInstance().setRootedBySDK(result);
                    L.i(TAG, "result:" + result);
                    EventBus.getDefault().post(new RootResultEvent(result));
                    if (result && !SettingUtils.checkLastSetValue(getApplicationContext(), SETTING.AUTO_INSTALL, false)) {
                        if (!SettingUtils.checkUserChange(getApplicationContext())) {
                            SettingUtils.changeDefaultValue(C.get(), SETTING.AUTO_INSTALL, true);
                        }
                    }
                }
            });
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();

            if (ACTION_SCAN_DUMP.equals(action)) {
                MTask.BACKGROUND_EXECUTOR.execute(new Runnable() {
                    @Override
                    public void run() {
                        startScan();
                    }
                });

                CleanAlert.initAlert(this);
            } else if (ACTION_WAKE_USBHELPER.equals(action)) {
                L.i("usbhelper", "action wake usbhelper");
                L.i("usbhelper", getApplicationContext() == null);
                StartUpHelper.init(getApplicationContext());
            } else if (ACTION_ROOT.equals(action)) {
                performRoot();
            } else if (ACTION_RESIDENT_NOTIFICATION.equals(action)) {
                handleResidentNotification(intent);
            }
        }
        return START_NOT_STICKY;
    }

    private void handleResidentNotification(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null && Utils.isAboveAPI(Build.VERSION_CODES.GINGERBREAD_MR1)) {
            int type = bundle.getInt(INTENT_ACTION_TYPE, FLAG_UPDATE_RESIDENT);
            switch (type) {
                case FLAG_UPDATE_RESIDENT:
                    mPresenter.updateResidentView();
                    break;
                case FLAG_UPDATE_SKIN_STYLE:
                    mPresenter.updateSkinStyle();
                    break;
                case FLAG_SHOW_RESIDENT:
                    mPresenter.showResidentView();
                    break;
                case FLAG_UPDATE_RED_POINT:
                    boolean show = bundle.getBoolean(RED_POINT_STATUS, HIDE_RED_POINT);
                    mPresenter.updateRedPoint(show);
                    break;
                case FLAG_UPDATE_MEM_PERCENT:
                    int percent = bundle.getInt(MEMORY_PERCENT, new TaskInfoProvider(C.get()).getUsedPercent());
                    mPresenter.updateMemoryPercent(percent);
                    break;
                case FLAG_REMOVE_RED_POINT:
                    mPresenter.removeRedPoint();
                    break;
            }
        }

    }

    public static Intent getMemUpdateIntent(int percent) {
        Intent intent = new Intent(C.get(), CoreService.class);
        intent.setAction(CoreService.ACTION_RESIDENT_NOTIFICATION);
        intent.putExtra(CoreService.INTENT_ACTION_TYPE, CoreService.FLAG_UPDATE_MEM_PERCENT);
        intent.putExtra(CoreService.MEMORY_PERCENT, percent);
        return intent;
    }

    public void startScan() {
        SQLiteDatabase db = new ClearManager(this).openClearDatabase();
        ScanHelp mScan = ScanHelp.getInstance(this);
        mScan.setDb(db);
        mScan.setRun(true);
        mScan.startScan(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);

        unregisterReceiver(autoReceiver);
        unregisterReceiver(br);
        level = -1;
        try {
            if (mLocationManager != null) {
                mLocationManager.removeUpdates(mLocationListener);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private void checkUpdate() {
        Intent intent = new Intent(C.get(), ActionService.class);
        intent.setAction(ActionService.ACTION_UPDATE);
        startService(intent);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent actionIntent = new Intent(C.get(), ActionService.class);
        actionIntent.setAction(ActionService.ACTION_UPDATE);
        actionIntent.putExtra("auto", true);
        PendingIntent pendingIntent = PendingIntent.getService(C.get(), 0, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long intervalTime = 6 * 60 * 60 * 1000L;

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + intervalTime, intervalTime, pendingIntent);
    }

    private void upProcess() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent actionIntent = new Intent(C.get(), ActionService.class);
        actionIntent.setAction(ActionService.ACTION_PROCESS);
        actionIntent.putExtra("auto", true);
        PendingIntent pendingIntent = PendingIntent.getService(C.get(), 0, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long intervalTime = 24 * 60 * 60 * 1000L;

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + intervalTime, intervalTime, pendingIntent);
    }


    private void registReceiver() {
        autoReceiver = new AutoResidentReceiver();
        IntentFilter filter = new IntentFilter();
        for (int i = 0; i < autoReceiver.actions.length; i++) {
            filter.addAction(autoReceiver.actions[i]);
        }
        registerReceiver(autoReceiver, filter);

        IntentFilter bFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(br, bFilter);
        level = -1;


        AppIOUReceiver packageReceiver = new AppIOUReceiver();
        IntentFilter packageFilter = new IntentFilter();


        packageFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        packageFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        packageFilter.addDataScheme("package");

        registerReceiver(packageReceiver, packageFilter);

    }


    @Override
    public void showResidentView(ResidentViewData data) {
        Notification notification = ResidentNotificationHelper.getNotification(data);
        notifyResident(RESIDENT_NOTIFICATION_ID, notification);
    }

    @Override
    public void hideResidentView() {
        NotificationManager2345.getInstance(getApplicationContext()).cancelNotification(RESIDENT_NOTIFICATION_ID);
        stopForeground(true);
    }

    @Override
    public void updateMemoryPercent(ResidentViewData data) {
        Notification notification = ResidentNotificationHelper.updateMemPercent(data);
        notifyResident(RESIDENT_NOTIFICATION_ID, notification);
    }

    @Override
    public void updateRedPoint(ResidentViewData data) {
        Notification notification = ResidentNotificationHelper.updateRedPoint(data);
        notifyResident(RESIDENT_NOTIFICATION_ID, notification);
    }

    private void notifyResident(int id, Notification notification) {
        if (notification == null) {
            return;
        }

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(id, notification);
        }
        startForeground(id, notification);
    }


    public class AutoResidentReceiver extends BroadcastReceiver {

        public static final String ACTION_APPNEEDUPDATE = "com.2345.2345marketneedupdate.fromapplication";

        public static final String ACTION_NEWAPPADDED = "com.2345.newappadded";

        public final String[] actions = {Intent.ACTION_UMS_CONNECTED, Intent.ACTION_UMS_DISCONNECTED, ACTION_NEWAPPADDED,
                ACTION_APPNEEDUPDATE};

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (Intent.ACTION_UMS_CONNECTED.equals(action)) {
                NotificationManager2345.getInstance(getApplicationContext()).notifyUSBConnection();
            } else if (Intent.ACTION_UMS_DISCONNECTED.equals(action)) {
                NotificationManager2345.getInstance(getApplicationContext()).cancelUSBConnectionNotification();
            } else if (ACTION_NEWAPPADDED.equals(action)) {
                final String packageName = intent.getStringExtra(Constants.NEWAPPPACKGE);


                TApier.getUpdateList(new TApier.AbsUpdateCallback() {
                    @Override
                    public void onComplete() {
                        DataCenterObserver session = DataCenterObserver.get(CoreService.this);
                        if (session.getUpdateList().containsKey(packageName)) {
                            try {
                                PackageManager pManager = CoreService.this.getPackageManager();
                                PackageInfo info = pManager.getPackageInfo(packageName, PackageManager.GET_RECEIVERS);
                                NotificationManager2345.getInstance(getApplicationContext()).notifyNewAppNeededUpdate(info);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            } */
/*else if ("android.net.conn.CONNECTIVITY_CHANGE".equals(action)) {
                String net = ApplicationUtils.getNetworkType(getApplicationContext());
                if (net != null) {
                    mHandler.sendEmptyMessage(1);
                }

            }*//*

        }

    }

    public static Integer level = -1;
    public static int BatteryV = -1;
    public static double BatteryT = -1;

    public class BatteryReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
                //获取当前电量
                level = intent.getIntExtra("level", 0);
                BatteryV = intent.getIntExtra("voltage", 0);
                BatteryT = ((double) intent.getIntExtra("temperature", 0)) * 0.1d;
            }
        }

    }

    private class UserLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                String locationStr = location.getLongitude() + "," + location.getLatitude();
                SPUtil.putLocationInfo(CoreService.this, locationStr);
                mLocationManager.removeUpdates(mLocationListener);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    }
}
*/
