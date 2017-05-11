package com.ymnet.onekeyclean.cleanmore.datacenter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Pair;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ymnet.onekeyclean.MarketApplication;
import com.ymnet.onekeyclean.cleanmore.SessionManager;
import com.ymnet.onekeyclean.cleanmore.junk.SilverActivity;
import com.ymnet.onekeyclean.cleanmore.junk.mode.InstalledApp;
import com.ymnet.onekeyclean.cleanmore.junk.mode.JunkGroup;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.ymnet.onekeyclean.cleanmore.SessionManager.P_INSTALL_APP;
import static com.ymnet.onekeyclean.cleanmore.SessionManager.P_REMOVE_APP;
import static com.ymnet.onekeyclean.cleanmore.SessionManager.P_UPGRADE_NUM;


/**
 * data transfer center,not change data here,just for notify data change and
 * offer snapshot of data
 */
public class DataCenterObserver extends MarketObservable {
    private static final String TAG = DataCenterObserver.class.getSimpleName();

    public static final int UPDATE_STATUS_SUCCESS = 1;
    public static final int UPDATE_STATUS_FAILE = -1;
    public static final int UPDATE_STATUS_PROCESS = 0;
    public static final int UPDATE_STATUS_UNKOWN = 2;

    private int updateStatus = UPDATE_STATUS_UNKOWN;

 /*   public int getUpdateStatus() {
        return updateStatus;
    }

    public void setUpdateStatus(int updateStatus) {
        this.updateStatus = updateStatus;
        mHandler.sendEmptyMessage(UPDATE_STATUS_CHANGE);
    }*/
/*
    *//**jf渠道需要屏蔽的应用数量*//*
    public static int jfIgnoreNum = 0;*/

    /**
     * 创建下载数据结果集
     */
    private static final int CURSOR_CREATED = 0;

    /**
     * 更新下载数据结果集
     */
    private static final int CURSOR_CHANGED = 1;

    /**
     * 产品更新
     */
    private static final int CURSOR_UPDATE = 2;

    /**
     * 下载列表更新
     */
    static final int UPDATE_COUNT = 4;

    /**
     * 忽略列表更新
     */
    static final int UPDATE_IGNORE = 5;

    /**
     * wifi pc link status
     */
    public static final int WIFI_PC_LINK_STATUS = 6;

    public static final int RECEVIRE_FILE_FROM_PC = 7;

    public static final int DOWNLOADING_COUNT = 8;

    public static final int INSTALL_APP = 9;

    public static final int REMOVE_APP = 10;

    public static final int ERROR_DOWNLOAD = 13;

    public static final int DOWNLOADURL_ERROR = 14;

    public static final int UPDATE_STATUS_CHANGE = 22;
    public static final String STR_STATUS_CHANGE = "update_status_change";

    /**
     * 新增下载
     */
    public static final int ADD_OR_REMOVE_DOWNLOAD = 15;

    /**
     * 下载发生变化
     */
    public static final int DOWNLOAD_STATUS_CHANGE = 16;

    /**
     * 初始化下载信息完毕
     */
    public static final int DOWNLOAD_LOAD_COMPLETED = 17;


    /**
     * 安装完成删除文件
     */
    public static final int INSTALLED_REMOVE_FILE = 18;

    /**
     * 下载过程中的URI
     */
    public static final int DOWNLOAD_NOTIFY_URI = 19;

    /**
     * DownloadFragment删除Dialog
     */
    public static final int MANAGE_DEL_DIALOG = 20;


    /**
     * 检测完毕升级MD5
     */
    public static final int VARIFY_MD5_END = 21;

    /**
     * 文件下载状态变化
     */
    public static final int DOWNLOAD_FILE_STATUS_CHANGE = 23;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case INSTALL_APP:

                    notifyObservers(new Pair<String, Object>(P_INSTALL_APP, (String) msg.obj));
                    break;
                case CURSOR_CREATED:
                    break;

                case CURSOR_CHANGED:
                    break;

                case CURSOR_UPDATE:

                    break;
                case REMOVE_APP:

                    notifyObservers(new Pair<String, Object>(P_REMOVE_APP, (String) msg.obj));
                    break;
                case UPDATE_COUNT:

                    notifyObservers(new Pair<String, Object>(P_UPGRADE_NUM, msg.arg1));
                    break;
                case UPDATE_IGNORE:

                    notifyObservers(new Pair<String, Object>(SessionManager.P_IGNORE_NUM, msg.arg1));
                    break;
                case WIFI_PC_LINK_STATUS:
                    notifyObservers(new Pair<String, Object>(SessionManager.WIFI_PC_LINK, getConnStatus()));
                    break;
                /*case RECEVIRE_FILE_FROM_PC:

                    notifyObservers(new Pair<String, Object>(SessionManager.PC_TRANSFER_FILE, pcFromFile.getFileCount()));
                    break;*/
                case DOWNLOADING_COUNT:
                    notifyObservers(new Pair<String, Object>(SessionManager.DOWNLOADING_COUNT, msg.arg1));
                    break;
                case ERROR_DOWNLOAD:

                    break;
                case DOWNLOADURL_ERROR:
                    Toast.makeText(mContext, "安装包下载地址有误，请稍后再试", Toast.LENGTH_SHORT).show();
                    break;

                case ADD_OR_REMOVE_DOWNLOAD:
                    notifyObservers(SessionManager.ADD_OR_REMOVE_DOWNLOAD);
                    break;

                case DOWNLOAD_STATUS_CHANGE:
                    notifyObservers(new Pair<String, Object>(SessionManager.DOWNLOAD_STATUS_CHANGE, msg));
                    break;

                case DOWNLOAD_LOAD_COMPLETED:
                    notifyObservers(SessionManager.DOWNLOAD_LOAD_COMPLETED);
                    break;
                case INSTALLED_REMOVE_FILE:
                    notifyObservers(SessionManager.INSTALLED_REMOVE_FILE);
                    break;
                case DOWNLOAD_NOTIFY_URI:
                    notifyObservers(new Pair<String, Object>(SessionManager.DOWNLOAD_NOTIFY_URI, msg.obj));
                    break;
                case MANAGE_DEL_DIALOG:
                    notifyObservers(new Pair<String, Object>(SessionManager.MANAGE_DEL_DIALOG, msg.obj));
                    break;
                case VARIFY_MD5_END:
                    notifyObservers(SessionManager.VARIFY_MD5_END);
                    break;
                case UPDATE_STATUS_CHANGE:
                    notifyObservers(STR_STATUS_CHANGE);
                case DOWNLOAD_FILE_STATUS_CHANGE:
                    notifyObservers(new Pair<>(SessionManager.DOWNLOAD_FILE_STATUS_CHANGE, msg.obj));
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * Application Context
     */
    private Context mContext;

    /**
     * The singleton instance
     */
    private static volatile DataCenterObserver sInstance;

    /**
     * 搜索热词
     */
    private String hot_words;

    /**
     * 推荐应用或热词列表
     */
/*    private List<RecommendEntity> recommendationList;


    private UserInfo userInfo;*/

//    private FileRecieverFromPC pcFromFile;

    private SilverActivity.CleanDataModeEvent cleanData;
    private AppsInfoHandler                   appsInfo;

    private DataCenterObserver(Context context) {
        mContext = context.getApplicationContext();
        /*userInfo = new UserInfo();
        userInfo.init(mContext);*/
        /*pcFromFile = new FileRecieverFromPC();
        pcFromFile.init(mHandler, mContext);*/
        appsInfo = new AppsInfoHandler(mContext, mHandler);
    }

    public static DataCenterObserver get(Context context) {
        if (sInstance == null) {
            synchronized (DataCenterObserver.class) {
                if (sInstance == null) {
                    sInstance = new DataCenterObserver(context);
                }
            }
        }
        return sInstance;
    }


   /* public Handler getMarketHandler() {
        return mHandler;
    }

    public AppsInfoHandler getAppsInfoHandler() {
        return appsInfo;
    }

    public void setmLianMengApk(ArrayList<String> mLianMengApk) {
        appsInfo.setmLianMengApk(mLianMengApk);
    }

    public boolean isLianMengApk(String packageName) {
        return appsInfo.isLianMengApk(packageName);
    }

    public int lianMengApkSize() {
        return appsInfo.lianMengApkSize();
    }

    public int getUpgradeNumber() {

        return appsInfo.getUpgradeNumber();
    }

    public int getIgnoreNumber() {
        return appsInfo.getIgnoreNumber();
    }


    *//**
     * 获取所有已安装应用列表
     *
     * @return
     */
    public ConcurrentHashMap<String, InstalledApp> getInstalledApps() {
        return appsInfo.getInstalledApps();
    }

    /**
     * 获取系统预装应用列表
     *
     * @return
     *//*
    public ConcurrentHashMap<String, InstalledApp> getSysInstalledApps() {
        return appsInfo.getSysInstalledApps();
    }

    *//**
     * 获取用户安装的应用列表
     *
     * @return
     */
    public ConcurrentHashMap<String, InstalledApp> getUserInstalledApps() {
        return appsInfo.getUserInstalledApps();
    }


    /*public String getInstalledAppsJson() {

        ConcurrentHashMap<String, InstalledApp> syss = getSysInstalledApps();
        JsonArray sysArray = new JsonArray();
        for (InstalledApp app : syss.values()) {
            JsonObject object = new JsonObject();
            String packageName = app.packageName;
            object.addProperty("pn", packageName);
            int versionCode = app.versionCode;
            object.addProperty("vc", versionCode);

            sysArray.add(object);
        }

        ConcurrentHashMap<String, InstalledApp> users = getUserInstalledApps();
        JsonArray userArray = new JsonArray();
        for (InstalledApp app : users.values()) {
            JsonObject object = new JsonObject();
            String packageName = app.packageName;
            object.addProperty("pn", packageName);
            int versionCode = app.versionCode;
            object.addProperty("vc", versionCode);

            userArray.add(object);
        }

        JsonObject obj = new JsonObject();
        obj.add("sys", sysArray);
        obj.add("user", userArray);

        return obj.toString();
    }*/

    /**
     * 获取用户安装应用列表
     *
     * @return
     */
    public String getUserAppListJsonStr() {
        ConcurrentHashMap<String, InstalledApp> users = getUserInstalledApps();
        JsonArray userArray = new JsonArray();
        for (InstalledApp app : users.values()) {
            JsonObject object = new JsonObject();
            object.addProperty("package", app.packageName);
            object.addProperty("appv", app.versionName);
            object.addProperty("name", app.appName);
            object.addProperty("size", app.totalFileSize);
            userArray.add(object);
        }
        return userArray.toString();
    }

    /**
     * 是否安装过
     *
     * @param packageName
     * @return
     *//*
    public boolean isInstalled(String packageName) {
        return getInstalledApp(packageName) != null;
    }

    *//**
     * 获取单个应用信息
     *
     * @param packageName
     * @return
     *//*
    public InstalledApp getInstalledApp(String packageName) {
        return appsInfo.getInstalledApp(packageName);
    }


    *//**
     * 获取已安装应用的md5值
     *
     * @param packageName
     * @return
     *//*
    public String getInstalledAppMd5(String packageName) {
        String md5 = null;
        InstalledApp app = getInstalledApp(packageName);
        if (app != null) {
            md5 = Utils.getLocalFileMd5(app.storeLocation);
        }

        return md5;
    }

    *//**
     * 获取应用的version code
     *
     * @param packageName
     * @return
     *//*
    public int getInstalledAppVersionCode(String packageName) {
        InstalledApp app = appsInfo.getInstalledApp(packageName);
        return app != null ? app.versionCode : 0;
    }

    *//**
     * 获取应用的version name
     *//*
    public String getInstalledAppVersionName(String packageName) {
        InstalledApp app = appsInfo.getInstalledApp(packageName);
        return app != null ? app.versionName : "";
    }


    public void addInstalledApp(String packageName) {
        appsInfo.addInstalledApp(packageName, appsInfo.getInstalledApp(packageName));
    }


    public void removeInstalledApp(String packageName) {
        appsInfo.removeInstalledApp(packageName);
    }*/

/*
    public HashMap<String, App> getUpdateList() {
        return appsInfo.getUpdateList();
    }

    public HashMap<String, App> getDeprecatedList() {
        return appsInfo.getDeprecatedList();
    }

    public HashMap<String, App> getIgnoreUpdateList() {
        return appsInfo.getIgnoreUpdateList();
    }

    public void removeUpdateApp(App app) {
        appsInfo.removeUpdateApp(app);
    }

    public boolean ignoreApp(App app) {
        return appsInfo.ignoreApp(app);
    }

    public boolean unIgnoreApp(App app) {
        return appsInfo.unIgnoreApp(app);
    }

    public UpdateAppsListController getUpdateAppsListController() {
        return appsInfo.getUpdateAppsListController();
    }

    //除去ApiResponseFactory 其余处不调此方法
    public void setUpdateList(HashMap<String, App> list) {

        appsInfo.setUpdateList(list);
    }

    //除去ApiResponseFactory 其余处不调此方法
    public void removeUpdatedApp(String app) {
        appsInfo.removeUpdatedApp(app);
    }

    // 来自pc端的电脑
    public int getFileFromPCCount() {
        return pcFromFile.getFileCount();
    }

    public ArrayList<FileFromPC> getFilesFromPC() {
        return pcFromFile.getFiles();
    }

    public void deleteFilesFromPC(int[] ids, String[] files, boolean deleFile) {
        pcFromFile.deleteFileById(ids, files, deleFile);
    }

    public String getHot_words() {
        return hot_words;
    }

    public void setHot_words(String hot_words) {
        this.hot_words = hot_words;
    }

    public List<RecommendEntity> getRecommendationList() {
        return recommendationList;
    }

    public void setRecommendationList(List<RecommendEntity> recommendationList) {
        this.recommendationList = recommendationList;
    }*/


    public WifiConnectionStatus getWifiStatus() {
        return MarketApplication.getInstance().getWifiConnectionStatus();
    }

    public int getConnStatus() {
        WifiConnectionStatus wifiConnectionStatus = getWifiStatus();
        return wifiConnectionStatus != null ? wifiConnectionStatus.getConnStatus() : 0;
    }

   /* public App checkInUpdateListForInstalled(String packageName) {

        return appsInfo.checkInupdatelist(packageName, true);
    }

    private List<App> mustAppList;

    public List<App> getMustAppList() {
        if (mustAppList == null)
            mustAppList = new ArrayList<App>();
        return mustAppList;
    }*/

    public void setCleanData(SilverActivity.CleanDataModeEvent cleanData) {
        this.cleanData = cleanData;
    }

    public SilverActivity.CleanDataModeEvent getCleanData() {
        return cleanData;
    }

   /* public UserInfo getUserInfo() {
        if (userInfo == null) {
            userInfo = new UserInfo();
            userInfo.init(mContext);
        }
        return userInfo;
    }
*/

    private boolean refreshCleanActivity = false;

    public boolean isRefreshCleanActivity() {
        return refreshCleanActivity;
    }

    public void setRefreshCleanActivity(boolean refreshCleanActivity) {
        this.refreshCleanActivity = refreshCleanActivity;
    }


    public long lastScanTime;

    public long getLastScanTime() {
        return lastScanTime;
    }

    public void setLastScanTime(long lastScanTime) {
        this.lastScanTime = lastScanTime;
    }


    private List<JunkGroup> junkDataList;

    public List<JunkGroup> getJunkDataList() {
        return junkDataList;
    }


    public void setJunkDataList(List<JunkGroup> junkDataList) {
        this.junkDataList = junkDataList;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                cleanData();
            }
        }, 6000);

    }

    private void cleanData() {
        this.junkDataList = null;
    }

    private int lastCleanRAMData;//加速球用到

    public int getLastCleanRAMData() {
        return lastCleanRAMData;
    }

    public void setLastCleanRAMData(int lastCleanRAMData) {
        this.lastCleanRAMData = lastCleanRAMData;
    }
}