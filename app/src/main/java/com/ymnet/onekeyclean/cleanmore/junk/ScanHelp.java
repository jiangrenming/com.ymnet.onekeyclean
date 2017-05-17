package com.ymnet.onekeyclean.cleanmore.junk;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Debug;
import android.os.Environment;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.umeng.analytics.MobclickAgent;
import com.ymnet.onekeyclean.cleanmore.constants.ByteConstants;
import com.ymnet.onekeyclean.cleanmore.constants.ScanState;
import com.ymnet.onekeyclean.cleanmore.datacenter.DataCenterObserver;
import com.ymnet.onekeyclean.cleanmore.db.CleanTrustDBManager;
import com.ymnet.onekeyclean.cleanmore.junk.alert.CleanAlert;
import com.ymnet.onekeyclean.cleanmore.junk.clearstrategy.ClearManager;
import com.ymnet.onekeyclean.cleanmore.junk.mode.InstalledApp;
import com.ymnet.onekeyclean.cleanmore.junk.mode.InstalledAppAndRAM;
import com.ymnet.onekeyclean.cleanmore.junk.mode.JunkChild;
import com.ymnet.onekeyclean.cleanmore.junk.mode.JunkChildApk;
import com.ymnet.onekeyclean.cleanmore.junk.mode.JunkChildCache;
import com.ymnet.onekeyclean.cleanmore.junk.mode.JunkChildCacheOfChild;
import com.ymnet.onekeyclean.cleanmore.junk.mode.JunkChildResidual;
import com.ymnet.onekeyclean.cleanmore.junk.mode.JunkGroup;
import com.ymnet.onekeyclean.cleanmore.junk.notifycationmanager.NotificationManager2345;
import com.ymnet.onekeyclean.cleanmore.utils.C;
import com.ymnet.onekeyclean.cleanmore.utils.CleanSetSharedPreferences;
import com.ymnet.onekeyclean.cleanmore.utils.FileTreeUtils;
import com.ymnet.onekeyclean.cleanmore.utils.FormatUtils;
import com.ymnet.onekeyclean.cleanmore.utils.SecurityAppInfo;
import com.ymnet.onekeyclean.cleanmore.utils.Util;
import com.ymnet.onekeyclean.cleanmore.utils.Utils;
import com.ymnet.onekeyclean.cleanmore.utils.WeChatUtil;
import com.ymnet.onekeyclean.cleanmore.wechat.MTask;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Created by Administrator on 2015/1/15.
 */
public class ScanHelp {
    private static final String TAG = "ScanHelp";

    public static final long ONE_DAY_LONG = 24 * 60 * 60 * 1000;

    private static final int SCAN_FILE_OVER = 6;//扫描文件的深度

    public static final int STATE_UNALL_SELECT = 0;//全不选中状态
    public static final int STATE_ALL_SELECT   = 1;//全选中状态
    public static final int STATE_HALF_SELECT  = 2;//半选中状态

    public static final String RAM_QUICKEN      = "内存加速";
    public static final String CACHE_JUNK       = "缓存垃圾";
    public static final String SYATEM_CACHE     = "系统缓存";
    public static final String UNINSTALL_REMAIN = "卸载残留";
    public static final String APK_FIAL         = "无用安装包";

    private static final String filterDir1 = "/一键清理/";
    private static final String filterDir2 = "/onekeyclean/";

    private static final String selectSql          = "select a.encrypted_file_path,a.encrypted_file_desp,a.encrypted_file_tip,a.proposal," +
            "b.encrypted_pck_name,b.encrypted_app_name,b.encrypted_tip " +
            "from file_table as a join package_table as b " +
            "on a.pck_name_id = b._id " +
            "where b.encrypted_pck_name = ?";
    private static final String WeiXin_PackageName = "com.tencent.mm";
    private static final String WeiXin_Match       = "********************************";//必须是32个×

    private static ScanHelp mInstance;

    private Context         context;
    private SQLiteDatabase  db;
    private boolean         isRun;
    private ActivityManager am;

    private List<JunkGroup>             datas;
    private List<JunkChild>             cacheDatas;
    private List<JunkChild>             residualDatas;
    private List<JunkChild>             apkFileDatas;
    private List<JunkChild>             ramDatas;
    private List<JunkChildCacheOfChild> cacheSystemDatas;
    private IScanResult                 iScanResult;//扫描回调

    private Collection<InstalledApp> collection;

    private long cacheSystemSize;
    private long cacheSize;
    private long residualSize;
    private long apkSize;
    private long ramSize = 0;

    private PackageManager  pm;
    private List<TrustMode> queryAll;

    private String       sdPath;
    private String       outSdPath;
    private ClearManager clearManager;


    private long lastTime = 0;

    private ScanHelp(Context context) {
        this.context = context.getApplicationContext();
    }

    public static ScanHelp getInstance(Context context) {
        if (mInstance != null) {
            mInstance = null;
        }
        mInstance = new ScanHelp(context);
        return mInstance;
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    public void setDb(SQLiteDatabase db) {
        this.db = db;
    }

    public boolean isRun() {
        return isRun;
    }

    public void setRun(boolean isRun) {
        this.isRun = isRun;
    }

    public List<JunkGroup> getDatas() {
        return datas;
    }

    public void setDatas(List<JunkGroup> datas) {
        this.datas = datas;
    }

    public void setiScanResult(IScanResult iScanResult) {
        this.iScanResult = iScanResult;
    }

    /**
     * 开始扫描
     *
     * @param flag 扫描的结果只需要大小 不需要具体的数据
     */
    // TODO: 2017/4/20 0020 微信扫描:开始扫描
    public synchronized void startScan(final boolean flag) {
        NotificationManager2345 manager = NotificationManager2345.getInstance(context);
        manager.cancelNotification(NotificationManager2345.NOTIFI_CLAEAN_2345);
        MTask.BACKGROUND_EXECUTOR.execute(new Runnable() {

            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                try {
                    if (flag) {
                        if (!SilverActivity.class.getName().equals(Util.getCurrentTopActivity(context))) {
                            scanSize();
                        }
                    } else {
                        scanAll();
                    }
                } catch (Exception e) {
                    Log.i(TAG, e.toString());
                }

            }
        });
    }


    private void scanSize() {
        initData();
        try {
            getSystemCache();
        } catch (Exception e) {
            e.printStackTrace();
        }
        getAppCache(true);
        getAppResidual(true);
        getRAMCache(true);
        getApkFile(true);
        long size = getTotalSize();
        long setSize = 100 * ByteConstants.MB;
        int setSizeIndex = CleanSetSharedPreferences.getLastSet(context,
                CleanSetSharedPreferences.CLEAN_SIZE_SET, 1);
        if (setSizeIndex == 0)
            setSize = 50 * ByteConstants.MB;
        else if (setSizeIndex == 1)
            setSize = 100 * ByteConstants.MB;
        else if (setSizeIndex == 2)
            setSize = 300 * ByteConstants.MB;
        else if (setSizeIndex == 3)
            setSize = 500 * ByteConstants.MB;

        if (size > setSize) {
            CleanAlert.notify(context, FormatUtils.formatFileSize(size));
        }
        CleanSetSharedPreferences.setPreviousScanSize(C.get(), size);
    }

    public long getTotalSize() {
        return cacheSystemSize + cacheSize + residualSize + apkSize + ramSize;
    }

    /**
     * 判断是否在六秒内
     *
     * @return
     */
    private boolean checkLastScanTime() {
        return System.currentTimeMillis() - DataCenterObserver.get(C.get()).getLastScanTime() <= 6000;
    }


    /**
     * 扫描数据
     */
    private void initDataFromScan() {
        long start = System.currentTimeMillis();
        try {
            getSystemCache();
        } catch (Exception e) {
            e.printStackTrace();
        }
        getRAMCache(false);
        if (checkSDState()) {
            getAppCache(false);
            getAppResidual(false);
        }
        getApkFile(false);

        if (residualDatas.size() > 0) {
            JunkGroup group2 = new JunkGroup();
            group2.setName(UNINSTALL_REMAIN);
            group2.setSize(residualSize);
            group2.setChildrenItems(residualDatas);
            datas.add(group2);
            System.out.println("值------1:" + group2.toString());
        }
        if (apkFileDatas.size() > 0) {
            JunkGroup group3 = new JunkGroup();
            group3.setName(APK_FIAL);
            group3.setSize(apkSize);
            group3.setChildrenItems(apkFileDatas);
            datas.add(group3);
            System.out.println("值------2:" + group3.toString());
        }
        if (cacheSystemDatas.size() > 0) {
            JunkChildCache cache = new JunkChildCache();
            cache.select = 1;
            cache.size = cacheSystemSize;
            cache.tip = "建议清理";
            cache.name = SYATEM_CACHE;
            cache.packageName = JunkChildCache.systemCachePackName;
            Collections.sort(cacheSystemDatas, new Comparator<JunkChildCacheOfChild>() {

                @Override
                public int compare(JunkChildCacheOfChild lhs, JunkChildCacheOfChild rhs) {
                    if (lhs.size > rhs.size) {
                        return -1;
                    } else if (lhs.size == rhs.size) {
                        return 0;

                    } else {
                        return 1;
                    }
                }
            });
            cache.childCacheOfChild = cacheSystemDatas;
            cacheDatas.add(0, cache);
            System.out.println("值------3:" + cache.toString());
        }
        if (cacheDatas.size() > 0) {
            JunkGroup group1 = new JunkGroup();
            group1.setName(CACHE_JUNK);
            group1.setSize(cacheSize + cacheSystemSize);
            initCacheDatas();
            group1.setChildrenItems(cacheDatas);
            datas.add(0, group1);
            System.out.println("值------4:" + group1.toString());
        }

        if (ramDatas.size() > 0) {
            JunkGroup group4 = new JunkGroup();
            group4.setName(RAM_QUICKEN);
            group4.setSize(ramSize);
            group4.setChildrenItems(ramDatas);
            group4.setSelect(1);
            Collections.sort(ramDatas, new Comparator<JunkChild>() {
                @Override
                public int compare(JunkChild lhs, JunkChild rhs) {
                    if (lhs instanceof InstalledAppAndRAM && rhs instanceof InstalledAppAndRAM) {
                        InstalledAppAndRAM ram1 = (InstalledAppAndRAM) lhs;
                        InstalledAppAndRAM ram2 = (InstalledAppAndRAM) rhs;
                        if (ram1.getApp().flag > ram2.getApp().flag) {
                            return -1;
                        } else if (ram1.getApp().flag == ram2.getApp().flag) {
                            return 0;
                        } else {
                            return 1;
                        }
                    }
                    return 0;
                }

            });
            datas.add(0, group4);
            System.out.println("值------5:" + group4.toString());
        }
        //扫描时间不足6秒的话 等待满足6秒再结束
        if (System.currentTimeMillis() - start <= 6000 && isRun) {
            try {
                Thread.sleep(6000 - (System.currentTimeMillis() - start));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.setRun(false);
        callCleanActivity(ScanState.SCAN_ALL_END);
    }

    /**
     * 初始化 cache的选中状态
     */
    private void initCacheDatas() {
        if (cacheDatas != null && cacheDatas.size() > 0) {
            for (JunkChild child : cacheDatas) {
                if (child instanceof JunkChildCache) {
                    initCacheDataList((JunkChildCache) child);
                }
            }
        }
    }

    /**
     * 初始化数据的选中状态
     *
     * @param child
     */
    private void initCacheDataList(JunkChildCache child) {
        List<JunkChildCacheOfChild> list = child.childCacheOfChild;
        if (list != null && list.size() > 0) {
            JunkChildCacheOfChild childOfChild = list.get(0);
            if (STATE_ALL_SELECT == childOfChild.getSelect()) {
                // index 0 处于选中状态
                for (JunkChildCacheOfChild cc : list) {
                    if (0 == cc.getSelect()) {
                        child.setSelect(ScanHelp.STATE_HALF_SELECT);//半选中状态
                        return;
                    }
                }
                child.setSelect(ScanHelp.STATE_ALL_SELECT);//选中状态
            } else {
                // index 0 处于非选中状态
                for (JunkChildCacheOfChild cc : list) {
                    if (1 == cc.getSelect()) {
                        child.setSelect(ScanHelp.STATE_HALF_SELECT);//半选中状态
                        return;
                    }
                }
                child.setSelect(ScanHelp.STATE_UNALL_SELECT);//选中状态
            }
        }
    }


    private void scanAll() {
        initData();
        if (checkLastScanTime()) {
            initDataFromStatic();
        } else {
            initDataFromScan();
        }

    }


    private void initData() {
        sdPath = Util.getSDPath();
        outSdPath = Util.getOutSDPath();
        queryAll = new CleanTrustDBManager(context).queryAll();
        pm = context.getPackageManager();
        cacheSystemSize = 0;
        cacheSize = 0;
        residualSize = 0;
        apkSize = 0;
        ramSize = 0;
        collection = DataCenterObserver.get(context).getInstalledApps().values();
        datas = new ArrayList<JunkGroup>();
        datas.clear();
        cacheDatas = new ArrayList<JunkChild>();
        cacheDatas.clear();
        cacheSystemDatas = new ArrayList<JunkChildCacheOfChild>();
        cacheSystemDatas.clear();
        residualDatas = new ArrayList<JunkChild>();
        residualDatas.clear();
        apkFileDatas = new ArrayList<JunkChild>();
        apkFileDatas.clear();
        ramDatas = new ArrayList<JunkChild>();
        ramDatas.clear();
        clearManager = new ClearManager(context);

    }

    /**
     * 创建数据从MarkApplication中
     */
    private void initDataFromStatic() {
        datas = DataCenterObserver.get(C.get()).getJunkDataList();
        if (datas == null || datas.size() == 0) {
            datas = new ArrayList<JunkGroup>();
            initDataFromScan();
        } else {
            for (JunkGroup group : datas) {
                if (RAM_QUICKEN.equals(group.getName())) {
                    ramDatas = group.getChildrenItems();
                    for (JunkChild child : ramDatas) {
                        ramSize += child.size;
                    }
                } else if (APK_FIAL.equals(group.getName())) {
                    apkFileDatas = group.getChildrenItems();
                    for (JunkChild child : apkFileDatas) {
                        apkSize += child.size;
                    }
                } else if (UNINSTALL_REMAIN.equals(group.getName())) {
                    residualDatas = group.getChildrenItems();
                    for (JunkChild child : residualDatas) {
                        residualSize += child.size;
                    }
                } else if (CACHE_JUNK.equals(group.getName())) {
                    cacheDatas = group.getChildrenItems();

                    for (JunkChild child : cacheDatas) {
                        if (child instanceof JunkChildCache) {
                            JunkChildCache cache = (JunkChildCache) child;
                            if (JunkChildCache.systemCachePackName.equals(cache.packageName)) {
                                cacheSystemDatas = cache.childCacheOfChild;
                                for (JunkChildCacheOfChild cc : cacheSystemDatas) {
                                    cacheSystemSize += cc.size;
                                }

                            } else {
                                cacheSize += cache.size;
                            }
                        }
                    }
                }
            }
            callCleanActivity(ScanState.SCAN_RAM_END);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            callCleanActivity(ScanState.SCAN_ALL_END);
        }

    }

    @SuppressLint("NewApi")
    private void getSystemCache() throws Exception {
        for (final InstalledApp app : collection) {
            callCleanActivity(app.appName);
            if (isRun == false)
                return;
            Method method = pm.getClass().getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
            method.invoke(pm, app.packageName, new IPackageStatsObserver.Stub() {

                @Override
                public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
                    if (succeeded) {
                        long cacheSize1 = pStats.cacheSize;
                        long cacheSize2 = 0;
                        if (Build.VERSION.SDK_INT >= 11) {
                            cacheSize2 = pStats.externalCacheSize;
                        }
                        long cacheSize = cacheSize1 + cacheSize2;
                        if (cacheSize > 24 * 1024) {
                            try {
                                pm.getApplicationInfo(app.packageName, 0);
                                JunkChildCacheOfChild childOfChild = new JunkChildCacheOfChild();
                                childOfChild.size = cacheSize;
                                childOfChild.type = JunkChildCacheOfChild.SYSTEM_CACHE;
                                childOfChild.name = app.appName;
                                childOfChild.packageName = app.packageName;
                                childOfChild.select = 1;
                                Drawable icon = pm.getApplicationIcon(pm.getApplicationInfo(app.packageName, 0));
                                childOfChild.icon = icon;
                                //                                System.out.println("值------:"+icon);
                                cacheSystemDatas.add(childOfChild);
                                //                                System.out.println("值------:" + childOfChild.toString());
                                cacheSystemSize += cacheSize;
                            } catch (Exception e) {
                                System.out.println("值------:崩溃了");
                                e.printStackTrace();
                            }

                        }
                    }
                }
            });
        }
        callCleanActivity(ScanState.SCANING_SYSTEM_CACHE_END);
    }


    /**
     * 扫描app缓存
     */
    private void getAppCache(boolean onlySize) {
        List<String> mm_path_matchs = WeChatUtil.MMPaths;
        if (db != null && db.isOpen()) {
            //得到手机中用户app
            Collection<InstalledApp> userApps = DataCenterObserver.get(context).getUserInstalledApps().values();
            for (InstalledApp app : userApps) {
                if (isRun == false)
                    return;

                if (checkTrust(app.packageName))  /*检查白名单的包名*/
                    continue;

                List<JunkChildCacheOfChild> result = new ArrayList<JunkChildCacheOfChild>();
                callCleanActivity(app.packageName);
                Log.d(TAG, "ScanHelp#getAppCache");
                byte[] solidKey = SecurityAppInfo.getSolidKey(context);
                if (solidKey == null) {
                    break;
                }
                String encryptedName = Utils.strCode(app.packageName, new String(solidKey)).trim();
                Log.d(TAG, "getAppCache: ");
                Cursor filePathCursor = db.rawQuery(selectSql, new String[]{encryptedName});
                long size = 0;
                String tip = "";
                int count = 0;
                while (filePathCursor.moveToNext()) {
                    Log.d(TAG, "getAppCache: " + count++);
                    if (isRun == false) {
                        filePathCursor.close();
                        return;
                    }
                    /*String filePath = ClearManager.decrptString(filePathCursor.getString(filePathCursor.getColumnIndex("encrypted_file_path")));
                    if (checkTrust(Util.getRootPath() + filePath)) {
                        continue;
                    }*/
                    String filename = ClearManager.decrptString(filePathCursor.getString(filePathCursor.getColumnIndex("encrypted_file_desp")));
                    String fileTip = ClearManager.decrptString(filePathCursor.getString(filePathCursor.getColumnIndex("encrypted_file_tip")));
                    int proposal = filePathCursor.getInt(filePathCursor.getColumnIndex("proposal"));
                    tip = ClearManager.decrptString(filePathCursor.getString(filePathCursor.getColumnIndex("encrypted_tip")));
                   /* if (WeiXin_PackageName.equals(app.packageName)) {
                        if (filePath.contains(WeiXin_Match) && mm_path_matchs != null && mm_path_matchs.size() > 0) {
                            for (int i = 0; i < mm_path_matchs.size(); i++) {
                                String path = mm_path_matchs.get(i);
                                String newFilePath = filePath.replace(WeiXin_Match, path);
                                long fileSize = getPathFileSize(sdPath + File.separator + newFilePath);
                                size += fileSize;
                                if (onlySize == false && fileSize > 0) {
                                    JunkChildCacheOfChild cacheChild = new JunkChildCacheOfChild();
                                    cacheChild.type = JunkChildCacheOfChild.APP_CACHE;
                                    cacheChild.fileTip = fileTip;
                                    cacheChild.select = proposal;
                                    cacheChild.size = fileSize;
                                    if (mm_path_matchs.size() == 1) {
                                        cacheChild.name = filename;
                                    } else {
                                        cacheChild.name = filename + "(" + (i + 1) + ")";
                                    }
                                    Drawable icon = pm.getApplicationIcon(pm.getPackageArchiveInfo(filePath, 0).applicationInfo);
                                    cacheChild.icon = icon;
                                    System.out.println("值------6:" + icon);
                                    cacheChild.packageName = app.packageName;
                                    cacheChild.path = sdPath + File.separator + newFilePath;
                                    result.add(cacheChild);
                                }
                            }
                            continue;
                        }

                    }*/
                    /*if (checkPathExistNotContainFolder(sdPath, filePath)) {
                        filePath = sdPath + filePath;
                    } else if (!TextUtils.isEmpty(outSdPath) && checkPathExistNotContainFolder(outSdPath, filePath)) {
                        filePath = outSdPath + filePath;
                    } else {
                        continue;
                    }

                    long fileSize = getPathFileSize(filePath);
                    if (fileSize == 0) {
                        continue;
                    }
                    size += fileSize;
                    if (onlySize == false) {
                        JunkChildCacheOfChild cacheChild = new JunkChildCacheOfChild();
                        cacheChild.type = JunkChildCacheOfChild.APP_CACHE;
                        cacheChild.fileTip = fileTip;
                        cacheChild.select = proposal;
                        cacheChild.size = fileSize;
                        cacheChild.name = filename;
                        cacheChild.packageName = app.packageName;
                        //不能加
                        *//*Drawable icon = pm.getApplicationIcon(pm.getPackageArchiveInfo(filePath, 0).applicationInfo);
                        cacheChild.icon = icon;
                        System.out.println("值------7:" + icon);
                        System.out.println("值------8:" + cacheChild.toString());*//*
                        cacheChild.path = filePath;
                        result.add(cacheChild);
                    }*/


                }

                if (filePathCursor != null) {
                    filePathCursor.close();
                }
                if (size > 0) {
                    cacheSize += size;
                    if (onlySize == false) {
                        JunkChildCache cache = new JunkChildCache();
                        cache.childCacheOfChild = result;
                        cache.select = 1;
                        for (JunkChildCacheOfChild childOfChild : result) {
                            if (childOfChild.getSelect() == 0) {
                                cache.select = 0;
                                break;
                            }
                        }

                        cache.size = size;
                        cache.tip = tip;
                        cache.packageName = app.packageName;
                        cache.name = app.appName;
                        Drawable icon;
                        try {
                            icon = pm.getApplicationIcon(pm.getApplicationInfo(app.packageName, 0));
                            cache.icon = icon;
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                            MobclickAgent.reportError(C.get(), e.toString());
                        }
                        cacheDatas.add(cache);
                    }
                }

            }
            //扫描广告小分类
            scanADSDK(onlySize);
            callCleanActivity(ScanState.SCANING_APP_CACHE_END);
        }

    }

    /**
     * 扫描广告和小分类
     *
     * @param flag true 只要大小 不要数据
     */
    private void scanADSDK(boolean flag) {
        if (db != null) {
            String sql = "select * from package_table where type=1";
            String sql2 = "select * from file_table where pck_name_id= ?";
            if (!db.isOpen()) {
                db = null;
                db = clearManager.openClearDatabase();
            }
            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                if (isRun == false) {
                    cursor.close();
                    return;
                }
                String appName = ClearManager.decrptString(cursor.getString(cursor.getColumnIndex("encrypted_app_name")));
                if (TextUtils.isEmpty(appName)) {
                    break;
                }
                String packageName = ClearManager.decrptString(cursor.getString(cursor.getColumnIndex("encrypted_pck_name")));
                String tip = ClearManager.decrptString(cursor.getString(cursor.getColumnIndex("encrypted_tip")));
                if (checkTrust(packageName)) {
                    continue;
                }
                callCleanActivity(appName);
                int _id = cursor.getInt(cursor.getColumnIndex("_id"));
                if (!db.isOpen()) {
                    db = null;
                    db = clearManager.openClearDatabase();
                }

                List<JunkChildCacheOfChild> childCacheOfChilds = new ArrayList<JunkChildCacheOfChild>();
                Cursor cursor2 = db.rawQuery(sql2, new String[]{_id + ""});
                long size = 0;
                while (cursor2.moveToNext()) {
                    if (isRun == false) {
                        cursor2.close();
                        return;
                    }
                    String filePath = ClearManager.decrptString(cursor2.getString(cursor2.getColumnIndex("encrypted_file_path")));
                    if (checkPathExistNotContainFolder(sdPath, filePath)) {
                        filePath = sdPath + filePath;
                    } else if (checkPathExistNotContainFolder(outSdPath, filePath)) {
                        filePath = outSdPath + filePath;
                    } else {
                        continue;
                    }
                    String filename = ClearManager.decrptString(cursor2.getString(cursor2.getColumnIndex("encrypted_file_desp")));
                    String fileTip = ClearManager.decrptString(cursor2.getString(cursor2.getColumnIndex("encrypted_file_tip")));
                    int proposal = cursor2.getInt(cursor2.getColumnIndex("proposal"));
                    long fileSize = getPathFileSize(filePath);
                    JunkChildCacheOfChild childOfChild = new JunkChildCacheOfChild();
                    childOfChild.type = JunkChildCacheOfChild.APP_CACHE;
                    childOfChild.fileTip = fileTip;
                    childOfChild.select = proposal;
                    childOfChild.size = fileSize;
                    childOfChild.name = filename;
                    childOfChild.packageName = packageName;
                    childOfChild.path = filePath;
                    childCacheOfChilds.add(childOfChild);
                    size += fileSize;

                }
                if (childCacheOfChilds.size() > 0) {
                    cacheSize += size;
                    if (flag == false) {
                        JunkChildCache cache = new JunkChildCache();
                        cache.name = appName;
                        cache.size = size;
                        cache.select = 1;
                        cache.packageName = packageName;
                        cache.tip = tip;
                        cache.childCacheOfChild = childCacheOfChilds;
                        cacheDatas.add(cache);
                    }
                }
                if (cursor2 != null)
                    cursor2.close();
            }
            if (cursor != null)
                cursor.close();
        }
    }


    /**
     * 扫描 app残留
     */
    private void getAppResidual(boolean onlySize) {
        if (db != null) {
            String sql = "select * from package_table where type=0";
            String sql2 = "select * from file_table where pck_name_id= ?";
            if (!db.isOpen()) {
                db = null;
                db = clearManager.openClearDatabase();
            }
            Cursor cursor = db.rawQuery(sql, null);
            while (cursor != null && cursor.moveToNext()) {
                if (!isRun) {
                    cursor.close();
                    return;
                }
                String appName = ClearManager.decrptString(cursor.getString(cursor.getColumnIndex("encrypted_app_name")));
                String packageName = ClearManager.decrptString(cursor.getString(cursor.getColumnIndex("encrypted_pck_name")));

                if (checkPackageNameExists(packageName)) {
                    //手机上存在该包名 则是 不是残留
                    continue;
                }
                if (checkTrust(packageName)) {
                    continue;
                }
                callCleanActivity(packageName);
                int _id = cursor.getInt(cursor.getColumnIndex("_id"));
                if (!db.isOpen()) {
                    db = null;
                    db = clearManager.openClearDatabase();
                }
                Cursor cursor2 = db.rawQuery(sql2, new String[]{_id + ""});
                long size = 0;
                List<String> paths = new ArrayList<String>();
                while (cursor2.moveToNext()) {
                    if (isRun == false) {
                        cursor2.close();
                        return;
                    }
                    String filePath = ClearManager.decrptString(cursor2.getString(cursor2.getColumnIndex("encrypted_file_path")));
                    if (checkPathExist(sdPath, filePath)) {
                        filePath = sdPath + filePath;
                    } else if (checkPathExist(outSdPath, filePath)) {
                        filePath = outSdPath + filePath;
                    } else {
                        continue;
                    }
                    paths.add(filePath);
                    long fileSize = getPathFileSize(filePath);
                    size += fileSize;
                }
                if (paths.size() > 0) {
                    residualSize += size;
                    if (onlySize == false) {
                        JunkChildResidual residual = new JunkChildResidual();
                        residual.name = appName;
                        residual.paths = paths;
                        residual.size = size;
                        residual.select = 1;
                        residual.packageName = packageName;
                        /*try {
                            Drawable icon = pm.getApplicationIcon(pm.getApplicationInfo(packageName, 0));
                            residual.icon = icon;
                            System.out.println("值------:"+icon);
                        } catch (PackageManager.NameNotFoundException e) {
                            System.out.println("值------:崩溃了");
                            e.printStackTrace();
                        }*/
                        residualDatas.add(residual);
                    }
                }
                if (cursor2 != null)
                    cursor2.close();
            }
            if (cursor != null)
                cursor.close();
            clearManager.closeClearDatabase(db);
            callCleanActivity(ScanState.SCANING_RESIDUAL_END);
        }
    }

    /**
     * 扫描内存占用
     */

    private void getRAMCache(boolean onlySize) {
        if (System.currentTimeMillis() - InstalledAppAndRAM.lastCleanTime < 3 * 60 * 1000) {
            try {
                Thread.sleep(2000);
                callCleanActivity(ScanState.SCAN_RAM_END);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return;
        }
        am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> RAPIS = getRAPI();
        for (ActivityManager.RunningAppProcessInfo rapi : RAPIS) {
            callCleanActivity(rapi.processName);
            if (isRun == false)
                return;
            int uid = rapi.uid;
            int[] myMempid = new int[]{rapi.pid};
            Debug.MemoryInfo[] memoryInfo = am.getProcessMemoryInfo(myMempid);
            int memSize = memoryInfo[0].getTotalPrivateDirty() * 1024;
            if (onlySize == false) {
                InstalledAppAndRAM ram = getContainFromUid(uid);
                if (ram == null) {
                    InstalledAppAndRAM app = getAppFromUid(uid);
                    if (app != null && memSize > 0) {
                        app.size = memSize;
                        ramSize += memSize;

                        try {
                            app.icon = pm.getApplicationIcon(pm.getApplicationInfo(rapi.processName, 0));
                            System.out.println("值------0:" + app.toString());
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }

                        ramDatas.add(app);

                    }
                } else {
                    ramSize += memSize;
                    ram.size = ram.size + memSize;
                }
            } else {
                ramSize += memSize;
            }

        }
        callCleanActivity(ScanState.SCAN_RAM_END);
    }

    /**
     * 扫描全盘apk文件
     */
    private void getApkFile(boolean onlySize) {
        //扫描内部sd卡
        if (!TextUtils.isEmpty(sdPath)) {
            File file = Environment.getExternalStorageDirectory();
            findApkFile(file, onlySize);
        }
        //扫描外部sd卡
        if (!TextUtils.isEmpty(outSdPath)) {
            File file = new File(outSdPath);
            if (file.exists()) {
                findApkFile(file, onlySize);
            }
        }
        callCleanActivity(ScanState.SCANING_APK_FILE_END);
    }


    /**
     * 检查对应路径的文件是否存在，path和根路径 如果目录是空文件夹则不存在
     *
     * @param path
     * @return
     */
    private boolean checkPathExistNotContainFolder(String dir, String path) {
        if (TextUtils.isEmpty(path) || TextUtils.isEmpty(dir)) {
            return false;
        }
        File file = new File(dir, path);
        if (file.exists()) {
            if (file.isFile())
                return true;
            else {
                File[] files = file.listFiles();
                if (files != null && files.length > 0) {
                    return true;
                }
                return false;
            }

        }
        return false;
    }

    /**
     * 检查对应路径的文件是否存在，path和根路径  不论是否空文件夹
     *
     * @param path
     * @return
     */
    private boolean checkPathExist(String dir, String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        File file = new File(dir, path);
        if (file.exists()) {
            if (file.isFile())
                return true;
            else {
                File[] files = file.listFiles();
                if (files != null) {
                    return true;
                }
            }

        }
        return false;
    }

    /**
     * 获取全部的运行进程信息
     *
     * @return
     */
    private List<ActivityManager.RunningAppProcessInfo> getRAPI() {
        List<ActivityManager.RunningAppProcessInfo> mRunningPros = am.getRunningAppProcesses();
        return mRunningPros;
    }

    /**
     * 查询内存垃圾中是否已经包含uid的项
     * 包含的话则返回 不包含返回null;
     *
     * @param uid
     * @return
     */
    private InstalledAppAndRAM getContainFromUid(int uid) {
        for (JunkChild item : ramDatas) {
            if (item instanceof InstalledAppAndRAM) {
                InstalledAppAndRAM ram = (InstalledAppAndRAM) item;
                if (ram.getApp().uid == uid) {
                    return ram;
                }
            }
        }
        return null;
    }

    /**
     * 从手机的全部应用集合中找到对应uid的app
     *
     * @param uid
     * @return
     */
    private InstalledAppAndRAM getAppFromUid(int uid) {
        for (InstalledApp item : collection) {
            if (item.uid == uid) {
                InstalledAppAndRAM iaar = new InstalledAppAndRAM(item);

                iaar.select = 1;// item.flag;//flag 代替选择
                iaar.name = item.appName;
                return iaar;
            }
        }

        return null;
    }

    /**
     * 判断该应用在手机中的安装情况
     *
     * @param pm          PackageManager
     * @param packageName 要判断应用的包名
     * @param versionCode 要判断应用的版本号
     */
    private int doType(PackageManager pm, String packageName, int versionCode) {
        List<PackageInfo> pakageinfos = pm
                .getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (PackageInfo pi : pakageinfos) {
            String pi_packageName = pi.packageName;
            int pi_versionCode = pi.versionCode;
            // 如果这个包名在系统已经安装过的应用中存在
            if (packageName.equals(pi_packageName)) {
                // Log.i("test","此应用安装过了");
                if (versionCode == pi_versionCode) {
                    return JunkChildApk.INSTALLED;
                } else if (versionCode > pi_versionCode) {
                    return JunkChildApk.INSTALLED_UPDATE;
                } else if (versionCode < pi_versionCode) {
                    return JunkChildApk.INSTALLED_OLD;
                }
            }
        }
        return JunkChildApk.UNINSTALLED;
    }


    private void findApkFile(File file, boolean onlySize) {
        if (isRun == false)
            return;

        if (file.exists() && file.canWrite()) {
            callCleanActivity(file.getAbsolutePath());
            if (checkTrust(file.getAbsolutePath())) {
                return;
            }
            if (file.isFile()) {
                String name_s = file.getName();
                String path = file.getAbsolutePath();
                System.out.println("path:"+path);
                try{
                    if (name_s.toLowerCase(Locale.getDefault()).endsWith(".apk") && checkOneKeyCleanFiler(path)) {
                        long size = file.length();
                        apkSize = apkSize + size;
                        if (onlySize == false) {
                            callCleanActivity(path);
                            JunkChildApk apk = getApkInfoFromPackageInfo(pm, path);
                            apk.fileTime = file.lastModified();
                            apk.path = file.getAbsolutePath();
                            apk.size = size;
                            apk.icon = pm.getApplicationIcon(pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES).applicationInfo);
                            if (JunkChildApk.INSTALLED == apk.installedType) {
                                apk.select = 1;
                            } else {
                                long currentTime = System.currentTimeMillis();
                                if (currentTime - apk.fileTime < ONE_DAY_LONG) {
                                    apk.select = 0;
                                } else {
                                    apk.select = 1;
                                }
                            }
                            apkFileDatas.add(apk);
                        }
                    }

                }catch (Exception e){
                    Log.i("Tagv",e.toString());

                }

            } else {
                if (isRun == false) {
                    return;
                }
                if (FileTreeUtils.isFileDirOver(file, SCAN_FILE_OVER)) {
                    //深度判断
                    return;
                }
                File[] files = file.listFiles();
                if (files != null && files.length > 0) {
                    for (File file_str : files) {
                        if (isRun == false)
                            return;
                        findApkFile(file_str, onlySize);
                    }
                }

            }
        }
    }

    /**
     * 检查自己的目录下apk TRUE 为正常显示 FALSE 过滤掉 不显示
     */
    private boolean checkOneKeyCleanFiler(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        if (!(path.contains(filterDir1) || path.contains(filterDir2))) {
            return true;
        }
        JunkChildApk apk = getApkInfoFromPackageInfo(pm, path);
        if (apk.installedType == JunkChildApk.INSTALLED || apk.installedType == JunkChildApk.INSTALLED_OLD) {
            //已安装 旧版本
            apk.select = 1;
            return true;
        } else {
            //未安装 新版本 破损
            long currentTime = System.currentTimeMillis();
            if (currentTime - apk.fileTime < ONE_DAY_LONG) {
                return false;
            } else {
                apk.select = 1;
                return true;
            }
        }
    }


    private JunkChildApk getApkInfoFromPackageInfo(PackageManager pm, String str) {
        JunkChildApk apk = new JunkChildApk();
        PackageInfo pi = null;
        if (pm != null) {
            pi = pm.getPackageArchiveInfo(str, PackageManager.GET_ACTIVITIES);
        }
        if (pi != null) {
            ApplicationInfo appInfo = pi.applicationInfo;
            /** 获取apk的图标 */
            Drawable drawable = appInfo.loadIcon(pm);
            apk.icon = drawable;
            /**
             * google 必须加这两行代码才能得到名字和icon
             */
            appInfo.sourceDir = str;
            appInfo.publicSourceDir = str;
            apk.name = (String) appInfo.loadLabel(pm);
            appInfo.loadIcon(pm);
            apk.packageName = pi.packageName;
            apk.versionName = pi.versionName;
            apk.versionCode = pi.versionCode;

            apk.installedType = doType(pm, pi.packageName, pi.versionCode);
        } else {
            apk.name = "未知";
            apk.packageName = "未知";
            apk.versionName = "未知";
            apk.versionCode = 0;
            apk.installedType = JunkChildApk.BREAK_APK;
        }
        if (!TextUtils.isEmpty(str)) {
            apk.fileTime = new File(str).lastModified();
        }
        return apk;

    }


    /**
     * 检查白名单过滤
     *
     * @param path
     * @return
     */
    private boolean checkTrust(String path) {
        if (queryAll == null || queryAll.size() == 0 || TextUtils.isEmpty(path)) {
            return false;
        }
        for (TrustMode trustMode : queryAll) {
            if (path.equals(trustMode.path)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 检查手机上是否有此app
     *
     * @param packageName app包名
     * @return
     */
    private boolean checkPackageNameExists(String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }

        for (InstalledApp app : collection) {
            if (packageName.equals(app.packageName)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 得到应用缓存选中大小
     */
    private long getCacheSelectSize() {
        long size = 0;
        if (cacheDatas != null && cacheDatas.size() > 0) {
            for (JunkChild child : cacheDatas) {
                if (child instanceof JunkChildCache) {
                    JunkChildCache cache = (JunkChildCache) child;
                    if (JunkChildCache.systemCachePackName.equals(cache.packageName)) {
                        if (cache.getSelect() == 1)
                            size += cache.size;
                    } else {
                        List<JunkChildCacheOfChild> childOfChilds = cache.childCacheOfChild;
                        for (JunkChildCacheOfChild childOfChild : childOfChilds) {
                            if (childOfChild.getSelect() == 1)
                                size += childOfChild.size;
                        }
                    }

                }
            }
        }

        return size;
    }

    /**
     * 得到卸载残留的选中大小
     */
    private long getResidualSelectSize() {
        long size = 0;
        if (residualDatas != null && residualDatas.size() > 0) {
            for (JunkChild child : residualDatas) {
                if (child instanceof JunkChildResidual) {
                    JunkChildResidual residual = (JunkChildResidual) child;
                    if (residual.getSelect() == 1) {
                        size += residual.size;
                    }
                }
            }
        }
        return size;
    }

    /**
     * 得到apk文件的选中大小
     */
    private long getApkFileSelectSize() {
        long size = 0;
        if (apkFileDatas != null && apkFileDatas.size() > 0) {
            for (JunkChild child : apkFileDatas) {
                JunkChildApk apk = (JunkChildApk) child;
                if (apk.getSelect() == 1) {
                    size += apk.size;
                }
            }
        }
        return size;
    }

    /**
     * 得到内存选中的大小
     */
    public long getRAMSelectSize() {
        long size = 0;
        if (ramDatas != null && ramDatas.size() > 0) {
            for (JunkChild child : ramDatas) {
                InstalledAppAndRAM ram = (InstalledAppAndRAM) child;
                if (ram.getSelect() == 1) {
                    size += ram.size;
                }
            }
        }
        return size;
    }

    public long getTotalSelectSize() {
        return getCacheSelectSize() + getResidualSelectSize() + getApkFileSelectSize() + getRAMSelectSize();
    }

    public void close() {
        if (db.isOpen()) {
            clearManager.closeClearDatabase(db);
            //            mInstance=null;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            if (db != null && db.isOpen()) {
                db.close();
            }
        } finally {
            super.finalize();
        }

    }

    private boolean checkSDState() {
        if (TextUtils.isEmpty(sdPath) && TextUtils.isEmpty(outSdPath)) {
            return false;
        }
        return true;
    }

    /**
     * 因为需要回掉 所以方法不进行公开
     *
     * @param path
     * @return
     */
    private long getPathFileSize(String path) {
        long fileSizes = 0;
        File file = new File(path.trim());
        try {
            if (null != file && file.exists()) {
                if (file.isDirectory()) { // 如果路径是文件夹的时候
                    fileSizes = getFileFolderTotalSize(file);
                } else if (file.isFile()) {
                    fileSizes = file.length();
                }
            }
            return fileSizes;
        } catch (Throwable th) {
            return fileSizes;
        }
    }

    //    private Drawable getFileIcon(File fileDir) {
    //        if (fileDir == null || !fileDir.exists()) {
    //            return null;
    //        } else if (fileDir.isDirectory()) {
    //            File[] fileList = fileDir.listFiles();
    //            if (fileList != null) {
    //                PackageManager pm = context.getPackageManager();
    //                for (File file : fileList) {
    //                    PackageInfo pkgInfo = pm.getPackageArchiveInfo(file.getAbsolutePath(), PackageManager.GET_ACTIVITIES);
    //                    if (pkgInfo != null) {
    //                        ApplicationInfo appInfo = pkgInfo.applicationInfo;
    //                         /* 必须加这两句，不然下面icon获取是default icon而不是应用包的icon */
    //                        appInfo.sourceDir = file.getAbsolutePath();
    //                        appInfo.publicSourceDir = file.getAbsolutePath();
    //                        String appName = pm.getApplicationLabel(appInfo).toString();// 得到应用名
    //                        String packageName = appInfo.packageName; // 得到包名
    //                        //                        String version = pkgInfo.versionName; // 得到版本信息
    //                         /* icon1和icon2其实是一样的 */
    //                        Drawable icon1 = pm.getApplicationIcon(appInfo);// 得到图标信息
    //                        Drawable icon2 = appInfo.loadIcon(pm);
    //                        String pkgInfoStr = String.format("PackageName:%s, Vesion: %s, AppName: %s", packageName, version, appName);
    //                        Log.i(TAG, String.format("PkgInfo: %s", pkgInfoStr));
    //                        return icon1;
    //                    }
    //                }
    //            }
    //        }
    //
    //        return null;
    //    }

    private long getFileFolderTotalSize(File fileDir) {
        long totalSize = 0;
        if (fileDir == null || !fileDir.exists()) {
            return 0;
        } else {
            callCleanActivity(fileDir.getAbsolutePath());
            if (fileDir.isFile()) {
                return fileDir.length();
            } else if (fileDir.isDirectory()) {
                File[] fileList = fileDir.listFiles();
                if (fileList != null) {
                    for (File file : fileList) {
                        if (isRun == false) {
                            return totalSize;
                        }
                        if (FileTreeUtils.isFileDirOver10(file)) {
                            //深度判断
                            return totalSize;
                        }
                        totalSize += getFileFolderTotalSize(file);
                    }
                }
            }
        }
        return totalSize;
    }

    private void callCleanActivity(Object obj) {
        if (obj == null || iScanResult == null) {
            return;
        } else {
            if (obj instanceof String) {
                if (System.currentTimeMillis() - lastTime > 200) {
                    lastTime = System.currentTimeMillis();
                    iScanResult.scanning((String) obj);
                }
            } else if (obj instanceof Integer) {
                iScanResult.scanState((Integer) obj);
                if (((Integer) obj) == ScanState.SCAN_ALL_END) {
                    iScanResult = null;
                }
            }
        }
    }

    /**
     * 扫描回调接口
     */
    public interface IScanResult {
        public void scanning(String path);

        public void scanState(int state);
    }
}
