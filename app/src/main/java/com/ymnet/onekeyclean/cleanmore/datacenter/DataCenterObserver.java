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

    private SilverActivity.CleanDataModeEvent cleanData;
    private AppsInfoHandler                   appsInfo;

    private DataCenterObserver(Context context) {
        mContext = context.getApplicationContext();
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

    /**
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

    public WifiConnectionStatus getWifiStatus() {
        return MarketApplication.getInstance().getWifiConnectionStatus();
    }

    public int getConnStatus() {
        WifiConnectionStatus wifiConnectionStatus = getWifiStatus();
        return wifiConnectionStatus != null ? wifiConnectionStatus.getConnStatus() : 0;
    }

    public void setCleanData(SilverActivity.CleanDataModeEvent cleanData) {
        this.cleanData = cleanData;
    }

    public SilverActivity.CleanDataModeEvent getCleanData() {
        return cleanData;
    }


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