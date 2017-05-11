package com.ymnet.onekeyclean;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.os.StrictMode;

import com.ymnet.onekeyclean.cleanmore.constants.Constants;
import com.ymnet.onekeyclean.cleanmore.datacenter.WifiConnectionStatus;
import com.ymnet.onekeyclean.cleanmore.utils.C;
import com.ymnet.onekeyclean.cleanmore.wechat.BaseApplication;
import com.ymnet.onekeyclean.cleanmore.wechat.component.ApplicationComponent;
import com.ymnet.onekeyclean.cleanmore.wechat.component.DaggerApplicationComponent;
import com.ymnet.onekeyclean.cleanmore.wechat.modules.ApplicationModule;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Maj1nBuu
 * @data 2017/4/23 19:02.
 * @overView ${TODO}
 */

public class MarketApplication extends BaseApplication {
    private static final String TAG = MarketApplication.class.getSimpleName();

    private ApplicationComponent applicationComponent;

    //    public static final int versioncode = BuildConfig.VERSION_CODE;
    //    public static final String versionname = BuildConfig.VERSION_NAME;
    //    public static final String packegename = BuildConfig.APPLICATION_ID;

    private static final String BIDDINGOS_CLIENT_ID = "90001";
    private static final String BIDDINGOS_SECRET    = "0aa498b313271973f3a310b47b555d22";

    public static int sHotfixVersion = -1;
    private static MarketApplication application;
    //    private static String channel;

    private Map<String, String> mUnionAppMaps;

    private ConcurrentHashMap<Integer, Activity> mRunningActivities;
    private WifiConnectionStatus                 mWifiConnectionStatus;

  /*  private WifiConnectionStatus mWifiConnectionStatus;

    private DaemonClient mDaemonClient;*/

    public static MarketApplication getInstance() {
        return application;
    }

  /*  public static RefWatcher getRefWatcher(Context context) {
        MarketApplication application = (MarketApplication) context.getApplicationContext();
        return application.refWatcher;
    }

    private RefWatcher refWatcher;*/

    private boolean mMainProcess;

    private void initializeInjector() {
        this.applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public ApplicationComponent getApplicationComponent() {
        return this.applicationComponent;
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        versionname = BuildConfig.VERSION_NAME;
        packegename = BuildConfig.APPLICATION_ID;
        versioncode = BuildConfig.VERSION_CODE;

//        MultiDex.install(this);

        C.setContext(base);
    }

    @Override
    @SuppressLint("NewApi")
    public void onCreate() {
        C.setContext(getApplicationContext());

        //        if (BuildConfig.DEV_DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
        }

        super.onCreate();

        // ClipboardUIManager is a static singleton that leaks an activity context.
        // Fix: trigger a call to ClipboardUIManager.getInstance() in Application.onCreate(), so
        // that the ClipboardUIManager instance gets cached with a reference to the
        // application context. Example: https://gist.github.com/pepyakin/8d2221501fd572d4a61c
        try {
            Class<?> cls = Class.forName("android.sec.clipboard.ClipboardUIManager");
            Method m = cls.getDeclaredMethod("getInstance", Context.class);
            Object o = m.invoke(null, this);
        } catch (Exception ignored) {
        }

        initializeInjector();

        registerActivityLifecycleCallbacks();

        application = this;


        // 不要删，这个是给设备设置别名，推送前测试会用到
        //        JPushInterface.setAlias(this, "zt", new TagAliasCallback() {
        //
        //			@Override
        //			public void gotResult(int arg0, String arg1, Set<String> arg2) {
        //				Log.i(TAG, "flag = " + arg0);
        //				Toast.makeText(getApplicationContext(), "flag = " + arg0, Toast.LENGTH_SHORT).show();
        //			}
        //		});

        /*if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }*/
        try {
            startService(new Intent(this, ProcessService.class).putExtra(ProcessService.KEY_PID, Process.myPid()));
        } catch (SecurityException ignored) {
            if (BuildConfig.DEBUG) {
                throw ignored;
            }
        }
//        Pipeline.init(C.get());//bitmap load libs to usge Pipeline
        //daemon
//        initDaemonProcess();
       /* if ("com.market2345".equals(ApplicationUtils.getProcessNameByPID(this, Process.myPid()))) {
            StatisticSpec.sendEvent(StatisticEventContants.mainservice);
        } else if ("com.market2345:PushService".equals(ApplicationUtils.getProcessNameByPID(this, Process.myPid()))) {
            StatisticSpec.sendEvent(StatisticEventContants.pushservice);
        }*/

    }

   /* private void initDaemonProcess() {
        Observable.just("")
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String str) {
                        boolean totalSwitch = C.getPreference(DaemonConstant.DAEMON_IS_OPEN, true);
                        boolean daemonSwitch = SPUtil.getDaemonStatus(C.get());
                        if (!totalSwitch || !daemonSwitch) {
                            return;
                        }

                        String processName = DaemonUtil.getCurProcessName(C.get());
                        DaemonUtil.preventRepeat(processName);

                        boolean flag = C.getPreference(DaemonConstant.DAEMON_IS_PROCESS_CRASH, false);
                        if (!flag) {
                            doDaemon(processName);
                        } else {
                            long historyTime = C.getPreference(DaemonConstant.DAEMON_FIRST_CRASH_TIME, 0L);
                            long intervalTime = System.currentTimeMillis() - historyTime;
                            if (intervalTime > DaemonConstant.DAEMON_THREE_DAYS_TIME) {
                                C.setPreference(DaemonConstant.DAEMON_REPEAT_OPEN, true);
                                DaemonUtil.preventRepeat(processName);
                                if (intervalTime > DaemonConstant.DAEMON_FOUR_DAYS_TIME) {
                                    DaemonUtil.daemonReset();
                                }
                                doDaemon(processName);
                            }
                        }
                    }
                });
    }*/


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void registerActivityLifecycleCallbacks() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mRunningActivities = new ConcurrentHashMap<>();
            registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

                }

                @Override
                public void onActivityStarted(Activity activity) {
                    mRunningActivities.put(activity.hashCode(), activity);
                }

                @Override
                public void onActivityResumed(Activity activity) {

                }

                @Override
                public void onActivityPaused(Activity activity) {

                }

                @Override
                public void onActivityStopped(Activity activity) {
                    mRunningActivities.remove(activity.hashCode());
                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

                }

                @Override
                public void onActivityDestroyed(Activity activity) {

                }
            });
        }
    }

    public boolean isFrontTask() {
        return mRunningActivities != null && mRunningActivities.size() > 0;
    }

    public WifiConnectionStatus getWifiConnectionStatus() {
        return mWifiConnectionStatus;
    }

    public boolean isMainProcess() {
        return mMainProcess;
    }

    public void setMainProcess(boolean mainProcess) {
        mMainProcess = mainProcess;
    }


    public static class ProcessService extends IntentService {

        private static final String TAG = ProcessService.class.getSimpleName();

        private static final String KEY_PID = "key_pid";

        /**
         * Creates an IntentService.  Invoked by your subclass's constructor.
         */
        public ProcessService() {
            super(TAG);
        }


        @Override
        protected void onHandleIntent(Intent intent) {
            if (intent != null) {
                int pid = intent.getIntExtra(KEY_PID, -1);
                if (Process.myPid() == pid) {//只在主进程初始化

                    MarketApplication.getInstance().setMainProcess(true);

                    //tip:方法调用顺序不要随便改变

                    //                initImageLoader();
                  /*  if ("jf1".equals(Utils.getChannel())) {
                        UsbhelperSdk.setDebug(BuildConfig.DEV_DEBUG);
                        UsbhelperSdk.init(MarketApplication.getInstance());
                    }*/


                  /*  DataCenterObserver session = DataCenterObserver.get(getApplicationContext());
                    session.getInstalledApps();

                    WifiConnectionStatus wifiStatus = new WifiConnectionStatus();
                    wifiStatus.init(session.getMarketHandler(), getApplicationContext());

                    MarketApplication.getInstance().setWifiConnectionStatus(wifiStatus);

                    DownloadManager.getInstance(getApplicationContext());//don't delete this code,for init

                    Intent initIntent = new Intent(MarketApplication.getInstance(), CoreService.class);
                    startService(initIntent);
                    if (getSharedPreferences(SPUtils.LM_INSTALL_LIST, Context.MODE_PRIVATE).getInt(SPUtils.LM_STATISTICS_RELEASED_KEY, 0) == SPUtils.LM_STATISTICS_RELEASING) {
                        getSharedPreferences(SPUtils.LM_INSTALL_LIST, Context.MODE_PRIVATE).edit().remove(SPUtils.LM_STATISTICS_RELEASED_KEY).commit();
                    }
*/

                    File dir = new File(android.os.Environment.getExternalStorageDirectory(), getString(R.string.app_name));
                    if (!dir.exists()) {
                        dir.mkdir();
                    }

                    File dir1 = new File(Constants.ImageCacheDir);
                    if (!dir1.exists()) {
                        dir1.mkdirs();
                    }
                }
            }
        }


    }


   /* public void startMemoryMonitor() {
        Pipeline.scheduleNextStatsClockTick();
    }

    public void endMemoryMonitor() {
        Pipeline.cancelNextStatsClockTick();
    }
*/
    //    public static String getChannel() {
    //        return channel;
    //    }
    //
    //    public static void setChannel(String channel) {
    //        MarketApplication.channel = channel;
    //    }

    public void setmUnionAppMaps(Map<String, String> unionAppMaps) {
        this.mUnionAppMaps = unionAppMaps;
    }

    public Map<String, String> getmUnionAppMaps() {
        return mUnionAppMaps;
    }

}
