package com.ymnet.onekeyclean.cleanmore.datacenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.Formatter;

import com.ymnet.onekeyclean.MarketApplication;
import com.ymnet.onekeyclean.cleanmore.constants.LMConstant;
import com.ymnet.onekeyclean.cleanmore.junk.mode.InstalledApp;
import com.ymnet.onekeyclean.cleanmore.utils.C;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class AppsInfoHandler {
    private static final String TAG = AppsInfoHandler.class.getSimpleName();


    private final static HashSet<String> filterSet = new HashSet<String>();

    static {
        filterSet.add(MarketApplication.packegename);
        filterSet.add(LMConstant.STATISTICS_PACKAGENAME);
        filterSet.add("com.lenovo.gps");
    }


    /**
     * 已安装应用
     */
    private ConcurrentHashMap<String, InstalledApp> mInstalledApps;
    /**
     * 系统已安装应用
     */
    private ConcurrentHashMap<String, InstalledApp> mSysInstalledApps;
    /**
     * 用户已安装应用
     */
    private ConcurrentHashMap<String, InstalledApp> mUserInstalledApps;


    private ArrayList<String> mLianMengApk;

//    private HashMap<String, App> mUpdateApps = new LinkedHashMap<String, App>();

//    private HashMap<String, App> mDeprecatedApps = new HashMap<String, App>();

//    private HashMap<String, App> mIgnoreUpdateApps = new HashMap<String, App>();

//    private UpdateAppsListController listController;

    /**
     * FIXME The apps upgrade number
     */
    private int upgradeNumber = 0;

    private int ignoreNumber;

    private Context mContext;

    private Handler mHandler;

    private Object updateAppLock = new Object();


    AppsInfoHandler(Context context, Handler handler) {

        mContext = context;
        mHandler = handler;


//        createUpdateAppsList();

    }


    //lianmeng
    public void setmLianMengApk(ArrayList<String> mLianMengApk) {
        AppsInfoHandler.this.mLianMengApk = (ArrayList<String>) mLianMengApk.clone();
    }

    public boolean isLianMengApk(String packageName) {
        if (null == mLianMengApk) {
            return false;
        }

        if (mLianMengApk.contains(packageName) || LMConstant.STATISTICS_PACKAGENAME.equals(packageName)) {
            return true;
        }
        return false;
    }

    public int lianMengApkSize() {
        if (null == mLianMengApk) {
            return 0;
        }

        return mLianMengApk.size();
    }

    /**
     * 获取所有已安装应用列表
     *
     * @return
     */
    public ConcurrentHashMap<String, InstalledApp> getInstalledApps() {
        if (mInstalledApps == null) {
            synchronized (AppsInfoHandler.class) {
                if (mInstalledApps == null) {
                    loadInstalledApps();
                }
            }
        }
        return mInstalledApps;
    }

    /**
     * 获取系统预装应用列表
     *
     * @return
     */
    public ConcurrentHashMap<String, InstalledApp> getSysInstalledApps() {
        if (mSysInstalledApps == null) {
            loadInstalledApps();
        }
        return mSysInstalledApps;
    }

    /**
     * 获取用户安装的应用列表
     *
     * @return
     */
    public ConcurrentHashMap<String, InstalledApp> getUserInstalledApps() {
        if (mUserInstalledApps == null) {
            loadInstalledApps();
        }
        return mUserInstalledApps;
    }

    private boolean isLoaded() {
        return mInstalledApps != null && mUserInstalledApps != null && mSysInstalledApps != null;
    }

    private void setAllInstalledApps(ConcurrentHashMap<String, InstalledApp> installedApps) {
        this.mInstalledApps = installedApps;
    }

    private void setSysInstalledApps(ConcurrentHashMap<String, InstalledApp> sysInstalledApps) {
        this.mSysInstalledApps = sysInstalledApps;
    }

    private void setUserInstalledApps(ConcurrentHashMap<String, InstalledApp> userInstalledApps) {
        this.mUserInstalledApps = userInstalledApps;
    }


    @SuppressLint("NewApi")
    private synchronized void loadInstalledApps() {

        if (isLoaded()) {
            return;
        }

        ConcurrentHashMap<String, InstalledApp> installed = new ConcurrentHashMap<String, InstalledApp>();
        ConcurrentHashMap<String, InstalledApp> sysInstalled = new ConcurrentHashMap<String, InstalledApp>();
        ConcurrentHashMap<String, InstalledApp> userInstalled = new ConcurrentHashMap<String, InstalledApp>();

        PackageManager packageManager = MarketApplication.getInstance().getPackageManager();
        if (packageManager == null) {
            return;
        }
        List<PackageInfo> packages = packageManager.getInstalledPackages(PackageManager.GET_SIGNATURES);
        for (PackageInfo packageInfo : packages) {
            if (packageInfo != null && packageInfo.applicationInfo != null && unNeedFilter(packageInfo.applicationInfo.packageName)) {
                InstalledApp appInfo = new InstalledApp();

                ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                appInfo.uid = applicationInfo.uid;
                appInfo.appName = applicationInfo.loadLabel(packageManager).toString();

                appInfo.versionName = packageInfo.versionName != null ? packageInfo.versionName : "";

                appInfo.versionCode = packageInfo.versionCode;

                appInfo.packageName = packageInfo.packageName;

                appInfo.flags = applicationInfo.flags;

                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.FROYO) {
                    appInfo.lastUpdateTime = new File(applicationInfo.sourceDir).lastModified();
                } else {
                    appInfo.lastUpdateTime = packageInfo.lastUpdateTime;
                }

//
                appInfo.signatures = new HashSet<String>();

                if (packageInfo.signatures != null) {
                    for (int i = 0; i < packageInfo.signatures.length; i++) {
                        String str = parseSignature(packageInfo.signatures[i].toByteArray());
                        appInfo.signatures.add(str);
                    }
                }

                String dir = applicationInfo.sourceDir;
                if (dir != null) {
                    File sourceFile = new File(dir);
                    if (sourceFile.exists()) {
                        appInfo.totalFileSize = sourceFile.length();

                        appInfo.size = Formatter.formatFileSize(C.get(), appInfo.totalFileSize);
                    }
                    appInfo.storeLocation = dir;
                    appInfo.store = (dir.contains("/system/app") || dir.contains("/data/app")) ? 0 : 1;
                }

                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    appInfo.flag = InstalledApp.APK_USER;
                    userInstalled.put(appInfo.packageName, appInfo);
                } else {
                    appInfo.flag = InstalledApp.APK_SYS;
                    sysInstalled.put(appInfo.packageName, appInfo);
                }
                installed.put(appInfo.packageName, appInfo);
            }
        }

        setSysInstalledApps(sysInstalled);
        setUserInstalledApps(userInstalled);
        setAllInstalledApps(installed);
    }


    private boolean unNeedFilter(String packageName) {
        return !filterSet.contains(packageName);
    }

    private String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }


    private String parseSignature(byte[] signature) {
        try {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(signature));
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(cert.getEncoded());
            String str = bytesToHexString(md5.digest());
            return str;
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;

    }


    /**
     * 获取单个应用信息
     *
     * @param packageName 包名
     * @return
     */
    @SuppressLint("NewApi")
    InstalledApp getInstalledApp(String packageName) {

        if (TextUtils.isEmpty(packageName)) {
            return null;
        }

        InstalledApp installedApp = getInstalledApps().get(packageName);

        if (installedApp != null) {
            return installedApp;
        }

        try {

            PackageManager packageManager = MarketApplication.getInstance().getPackageManager();

            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);

            if (packageInfo != null && packageInfo.applicationInfo != null) {
                installedApp = new InstalledApp();

                ApplicationInfo applicationInfo = packageInfo.applicationInfo;

                installedApp.uid = applicationInfo.uid;

                installedApp.appName = applicationInfo.loadLabel(packageManager).toString();

                installedApp.versionName = packageInfo.versionName != null ? packageInfo.versionName : "";

                installedApp.versionCode = packageInfo.versionCode;

                installedApp.packageName = packageInfo.packageName;

                installedApp.flags = applicationInfo.flags;

                installedApp.signatures = new HashSet<String>();

                if (packageInfo.signatures != null) {
                    for (int i = 0; i < packageInfo.signatures.length; i++) {
                        String str = parseSignature(packageInfo.signatures[i].toByteArray());
                        installedApp.signatures.add(str);
                    }
                }


                int canMove = 0;
                try {
                    Field field = packageInfo.getClass().getField("installLocation");
                    field.setAccessible(true);
                    canMove = field.getInt(packageInfo);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                installedApp.canMove = canMove;
                installedApp.isCanMoveGeted = true;

                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.FROYO) {
                    installedApp.lastUpdateTime = new File(applicationInfo.sourceDir).lastModified();
                } else {
                    installedApp.lastUpdateTime = packageInfo.lastUpdateTime;
                }

                String dir = packageInfo.applicationInfo.sourceDir;
                if (dir != null) {
                    File sourceFile = new File(dir);
                    if (sourceFile.exists()) {
                        installedApp.totalFileSize = sourceFile.length();
                        installedApp.size = Formatter.formatFileSize(MarketApplication.getInstance(), installedApp.totalFileSize);
                    }
                    installedApp.storeLocation = dir;
                    installedApp.store = (dir.contains("/system/app") || dir.contains("/data/app")) ? 0 : 1;
                }
                return installedApp;
            }

        } catch (NameNotFoundException e) {
//            e.printStackTrace();
        }
        return null;
    }


    void addInstalledApp(String packageName, InstalledApp installedApp) {
        if (installedApp != null) {
            if ((installedApp.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                installedApp.flag = 1;
                getUserInstalledApps().put(packageName, installedApp);
            } else {
                installedApp.flag = 0;
                getSysInstalledApps().put(packageName, installedApp);
            }
            getInstalledApps().put(packageName, installedApp);

            Message msg = mHandler.obtainMessage(DataCenterObserver.INSTALL_APP);
            msg.obj = packageName;
            msg.sendToTarget();
        }
    }

    void removeInstalledApp(String packageName) {
        getInstalledApps().remove(packageName);
        getSysInstalledApps().remove(packageName);
        getUserInstalledApps().remove(packageName);
        Message msg = mHandler.obtainMessage(DataCenterObserver.REMOVE_APP);
        msg.obj = packageName;
        msg.sendToTarget();
    }

    public boolean checkIsHasInatall(String packageName) {
        return !TextUtils.isEmpty(packageName) && getInstalledApps().containsKey(packageName);
    }

    //update app
    /*UpdateAppsListController getUpdateAppsListController() {
        createUpdateAppsList();
        return listController;
    }

    private void createUpdateAppsList() {
        if (listController == null) {
            listController = (UpdateAppsListController) ListControllerFactory.getListController(ListControllerFactory.UPDATEAPPSLIST, mContext);
        }
    }*/

    int getUpgradeNumber() {
        return upgradeNumber;
    }

    int getIgnoreNumber() {
        return ignoreNumber;
    }


    //除去ApiResponseFactory 其余处不调此方法
    /*void removeUpdatedApp(String app) {
        synchronized (updateAppLock) {
            if (null != mUpdateApps) {
                mUpdateApps.remove(app);
                mDeprecatedApps.remove(app);
                upgradeNumber = mUpdateApps.size();
            }
            Message msg = new Message();
            msg.what = DataCenterObserver.UPDATE_COUNT;
            msg.arg1 = upgradeNumber;
            mHandler.sendMessage(msg);
        }
    }


    *//**
     * 当返回的app在系统中找不到的时候调用该方法
     *
     * @param app
     * @return
     *//*
    void removeUpdateApp(App app) {
        synchronized (updateAppLock) {
            mUpdateApps.remove(app.packageName);
            mDeprecatedApps.remove(app.packageName);
            upgradeNumber = mUpdateApps.size();
            Message msg = new Message();
            msg.what = DataCenterObserver.UPDATE_COUNT;
            msg.arg1 = upgradeNumber;
            mHandler.sendMessage(msg);
        }
    }


    boolean ignoreApp(App app) {
        synchronized (updateAppLock) {
            boolean done = listController.addToBlackList(AppBAWLocal.createFromApp(app));
            if (done) {
                mUpdateApps.remove(app.packageName);
                mIgnoreUpdateApps.put(app.packageName, app);
                upgradeNumber = mUpdateApps.size();
                ignoreNumber = mIgnoreUpdateApps.size();
                Message msg = new Message();
                msg.what = DataCenterObserver.UPDATE_COUNT;
                msg.arg1 = upgradeNumber;
                mHandler.sendMessage(msg);
            }

            return done;
        }
    }

    boolean unIgnoreApp(App app) {
        synchronized (updateAppLock) {
            boolean done = listController.removeFromBlackList(AppBAWLocal.createFromApp(app));
            if (done) {
                mIgnoreUpdateApps.remove(app.packageName);
                //mUpdateApps.put(app.packageName, app);
                LinkedHashMap<String, App> tempMap = new LinkedHashMap<>();
                tempMap.put(app.packageName, app);// Put this unignored app in the first place of mUpdateApps
                tempMap.putAll(mUpdateApps);
                mUpdateApps.clear();
                mUpdateApps.putAll(tempMap);

                verifyVersion(app);
                ignoreNumber = mIgnoreUpdateApps.size();
                upgradeNumber = mUpdateApps.size();
                Message msg = mHandler.obtainMessage(DataCenterObserver.UPDATE_IGNORE);
                msg.arg1 = ignoreNumber;
                mHandler.sendMessage(msg);
                Message msg1 = mHandler.obtainMessage(DataCenterObserver.UPDATE_COUNT);
                msg1.arg1 = upgradeNumber;
                mHandler.sendMessageDelayed(msg1, 300);

            }
            return done;
        }
    }*/

    /**
     * 屏蔽最低版本比当前系统版本高的应用
     *
     * @param app
     */
   /* private void verifyVersion(App app) {
        if (app.minSDK > Build.VERSION.SDK_INT) {
            mDeprecatedApps.put(app.packageName, app);
            mUpdateApps.remove(app.packageName);
        }
    }

    HashMap<String, App> getUpdateList() {
        synchronized (updateAppLock) {
            return (HashMap<String, App>) mUpdateApps.clone();
        }
    }

    HashMap<String, App> getDeprecatedList() {
        synchronized (updateAppLock) {
            return (HashMap<String, App>) mDeprecatedApps.clone();
        }
    }


    HashMap<String, App> getIgnoreUpdateList() {
        synchronized (updateAppLock) {
            return (HashMap<String, App>) mIgnoreUpdateApps.clone();
        }
    }

    //除去ApiResponseFactory 其余处不调此方法
    void setUpdateList(HashMap<String, App> list) {

        final HashMap<String, App> partialApps = new HashMap<>();

        synchronized (updateAppLock) {
            mUpdateApps.clear();
            if (list != null) {
                for (App app : list.values()) {
                    mUpdateApps.put(app.packageName, app);
                    verifyVersion(app);
                    if (downPartial(app)) {
                        partialApps.put(app.packageName, app);
                    }
                }
            }
        }
        MTask.BACKGROUND_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                Utils.validateDownPartial(mContext, partialApps.values());
                mHandler.sendEmptyMessage(DataCenterObserver.VARIFY_MD5_END);
            }
        });
        changeUpdateApps();

    }*/


    /**
     * 是否可以通过增量升级
     *
     * @return
     */
   /* private boolean downPartial(App app) {
        return !TextUtils.isEmpty(app.patch_url) && !TextUtils.isEmpty(app.low_md5);
    }


    boolean checkInupdatelistForInstall(String packageName) {
        synchronized (updateAppLock) {
            if (null == mUpdateApps) {
                return false;
            }
            if (!mUpdateApps.containsKey(packageName)) {
                return true;
            }

            return false;
        }
    }


    public App getUpdateApp(String packageName) {
        synchronized (updateAppLock) {
            if (mUpdateApps != null) {
                return mUpdateApps.get(packageName);
            }
        }
        return null;
    }


    public boolean checkInupdatelist(String packageName) {
        synchronized (updateAppLock) {
            if (null == mUpdateApps) {
                return false;
            }
            if (mUpdateApps.containsKey(packageName)) {
                return true;
            }

            return false;
        }
    }

    public App checkInupdatelist(String packageName, boolean second) {
        synchronized (updateAppLock) {
            if (null == mUpdateApps) {
                return null;
            }
            if (mUpdateApps.containsKey(packageName)) {
                return mUpdateApps.get(packageName);
            }

            return null;
        }
    }

    private void changeUpdateApps() {
        synchronized (updateAppLock) {
            Collection<App> coll = ((HashMap<String, App>) mUpdateApps.clone()).values();
//            ArrayList<App> updateList = new ArrayList<App>();
            for (App app : coll) {
                if (listController.isInBlackList(AppBAWLocal.createFromApp(app))) {
                    mUpdateApps.remove(app.packageName);
                    mDeprecatedApps.remove(app.packageName);
                    mIgnoreUpdateApps.put(app.packageName, app);
                }
//                else {
//                    updateList.add(app);
//                }
            }
            upgradeNumber = mUpdateApps.size();
            ignoreNumber = mIgnoreUpdateApps.size();
            DataCenterObserver.get(C.get()).setUpdateStatus(DataCenterObserver.UPDATE_STATUS_SUCCESS);
            Message msg1 = new Message();
            msg1.what = DataCenterObserver.UPDATE_COUNT;
            msg1.arg1 = upgradeNumber;
            mHandler.sendMessage(msg1);
            int networkType = NetworkUtils.getNetworkType(mContext, true);
            if (networkType == ConnectivityManager.TYPE_WIFI) {
                if (SettingUtils.checkLastSetValue(mContext, SETTING.FREE_UPDATE, false) && CoreService.level > 30) {

                    Collection<App> list = mUpdateApps.values();
                    ArrayList<App> downList = new ArrayList<App>();
                    for (App app : list) {
                        if (app.minSDK <= Build.VERSION.SDK_INT) {
                            downList.add(app);
                        }
                    }

                    ArrayList<App> down = new ArrayList<>();
                    for (App app : downList) {
                        InstalledApp installedApp = DataCenterObserver.get(mContext).getInstalledApp(app.packageName);
                        if (installedApp != null) {
                            if (TextUtils.isEmpty(app.certMd5) || installedApp.signatures.contains(app.certMd5)) {
                                down.add(app);
                            }
                        }
                    }

                    if (down.size() > 0) {
                        DownloadManager.getInstance(mContext).startOrResumeDownload(false, false, down.toArray(new App[down.size()]));
                    }
                }
            }

//            updateList.clear();
        }
    }*/


}
