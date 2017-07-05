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
import android.util.Log;

import com.ymnet.onekeyclean.cleanmore.constants.Constants;
import com.ymnet.onekeyclean.cleanmore.datacenter.WifiConnectionStatus;
import com.ymnet.onekeyclean.cleanmore.notification.HomeTabActivity;
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

    private static MarketApplication application;

    private Map<String, String> mUnionAppMaps;

    private ConcurrentHashMap<Integer, Activity> mRunningActivities;
    private WifiConnectionStatus                 mWifiConnectionStatus;
//    private List<Activity> mList = new LinkedList<>();

    public static MarketApplication getInstance() {
        return application;
    }

    /*public void addActivity(Activity activity) {
        mList.add(activity);
    }*/


    @Override
    @SuppressLint("NewApi")
    public void onCreate() {
        C.setContext(getApplicationContext());

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

        try {
            startService(new Intent(this, ProcessService.class).putExtra(ProcessService.KEY_PID, Process.myPid()));
        } catch (SecurityException ignored) {
            if (BuildConfig.DEBUG) {
                throw ignored;
            }
        }

        Log.d(TAG, "onCreate: MarketApplication");
        //账户同步
        Intent intent = new Intent(this, HomeTabActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

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

        C.setContext(base);
    }

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

    public void setmUnionAppMaps(Map<String, String> unionAppMaps) {
        this.mUnionAppMaps = unionAppMaps;
    }

    public Map<String, String> getmUnionAppMaps() {
        return mUnionAppMaps;
    }

}
