package com.yment.killbackground.download;

import android.app.ActivityManager;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.yment.killbackground.utils.FileUtilsSdk;
import com.ymnet.update.DownLoadFactory;
import com.ymnet.update.DownLoadUnit;
import com.ymnet.update.ExternalInterface;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by Administrator on 2016/9/19.
 */
@Deprecated
public class PushManager implements ExternalInterface {

    public static final String TAG = "PushManager";
    private Context mContext;
    public static final int INVALID_APP_VERSION_CODE = 0;
    public static final int MAX_PUSH_APP_NUM = 3;

    public static final int APP_STATUS_DOWNLOADING = 1;
    public static final int APP_STATUS_DOWNLOAD_SUCCESS = 2;
    public static final int APP_STATUS_INSTALL_START_USER = 3;
    public static final int APP_STATUS_INSTALL_SUCCESS = 4;
    public static final int APP_STATUS_INSTALL_FAIL = 5;
    public static final int APP_STATUS_INSTALL_START_ROOT = 6;
    public static final String PREFIX = "_";
    public static final String VIRTUAL_APP_PREFIX = "11f" + PREFIX;
    public static final int OTHER_TYPE = 99;
    public static final int NONE_TYPE = -1;
    public static final String STATISTICS_PUSH_APP_USER_INSTALL_KEY = "push_app_user_install";

    private static final PushManager INSTANCE = new PushManager();
    DownloadChangeObserver mDownloadChangeObserver;
    private DownloadManager mDownloadManager = null;
    public static final float INVALID_PROGRESS_VALUE = -1f;
    public static final float PROGRESS_SUCCESS = 1f;
    public static final Uri CONTENT_URI = Uri.parse("content://downloads/my_downloads");
    public static final long DOWNLOADS_ENQUEUE_EXCEPTION = -1L;

    public static final long DAY_TIME = 24 * 60 * 60 * 1000;

    Handler mHandler = new Handler();

    private PushManager() {
    }

    public static PushManager getInstance() {
        return INSTANCE;
    }

    public void init(Context context) {
        try {
            mContext = context;
            mDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            mDownloadChangeObserver = new DownloadChangeObserver(null);
            context.getContentResolver().registerContentObserver(CONTENT_URI, true, mDownloadChangeObserver);
            registerReceiver();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static ConcurrentHashMap<Long, String> sDownloadRef = new ConcurrentHashMap<Long, String>();
    public static ArrayList<String> sInstallApps = new ArrayList<String>();
    public static ArrayList<String> sInstallApping = new ArrayList<String>();
    BroadcastReceiver mNaviReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                final long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (isDownloadRefHasKey(reference)) {
                    final String pkgName = getPkgFromDownloadRef(reference);
                    final boolean virtualFolderApp = isVirtualFolderApp(reference);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            installApk(mContext, reference, pkgName, virtualFolderApp);
                        }
                    });
                    downloadRefRemove(reference);
                }
            } else if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
                try {
                    String pkgName = intent.getData().getSchemeSpecificPart();
                    boolean installAppsHasPkg = isInstallAppsHasPkg(pkgName);
                    if (installAppsHasPkg) {
                        installAppsRemove(pkgName);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {

            }
        }
    };

    private void registerReceiver() {
        if (mContext != null) {
            IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            mContext.registerReceiver(mNaviReceiver, filter);

            filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
            filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
            filter.addDataScheme("package");
            mContext.registerReceiver(mNaviReceiver, filter);
        }
    }

    private void unRegisterReceiver() {
        try {
            if (mContext != null) {
                mContext.unregisterReceiver(mNaviReceiver);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public void onDestroy() {
        try {
            unRegisterReceiver();
            mContext.getContentResolver().unregisterContentObserver(mDownloadChangeObserver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Context getContext() {
        return mContext;
    }

    // protected boolean installApk(Context context, String packageName, Uri packageFileUri, String sig, int version) {
    // return installApk(context, packageName, packageFileUri, sig, version, false);
    // }

    protected boolean installApk(final Context context, final String packageName, final Uri packageFileUri, final String sig, final int version, final boolean isFromVirtualFolder) {
        if (context == null) {
            return false;
        }

        DownLoadUnit downLoadUnit = DownLoadFactory.getInstance().getInsideInterface().getDownLoadUnit();
        if (downLoadUnit != null && downLoadUnit.is_tip()) {
            DownLoadFactory.getInstance().getInsideInterface().startToast(packageName);
            return false;
        }

//		String key = PushJsonDataParse.getInstallNotifyKey(packageName, sig, version + "");
//		if (!isFromVirtualFolder && from == NONE_TYPE && !PushJsonDataParse.increaseInstallTimes(key)) {
//			Logger.e(TAG, "PushManager.installApk2 intercept: the pushed app(" + key + ") is installed num more than " + MAX_PUSH_APP_NUM
//					+ ",or toady is already notified.");
//			return false;
//		}

        if (!isInstallAppsHasPkg(packageName)) {
            installAppsAdd(packageName, isFromVirtualFolder);
        }

        if (sInstallApping.contains(packageName)) {
            return false;
        }

        try {
            String path = packageFileUri.getPath();
            ApplicationManager am = new ApplicationManager(mContext);
            am.installPackage(path);
            sInstallApping.add(packageName);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    sInstallApping.remove(packageName);
                }
            }, 10000);
        } catch (Exception e) {
            e.printStackTrace();

//            String key = PushJsonDataParse.getInstallNotifyKey(packageName, sig, version + "");
//            //Bug 1.2.3
//            if (!PushJsonDataParse.increaseInstallTimes(context,key)) {
//                Logger.e(TAG, "PushManager.installApk2 intercept: the pushed app(" + key + ") is installed num more than " + MAX_PUSH_APP_NUM
//                        + ",or toady is already notified.");
//                return false;
//            }


            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                intent.setDataAndType(packageFileUri, "application/vnd.android.package-archive");
                context.startActivity(intent);
            } catch (Exception e1) {
                try {
                    e1.printStackTrace();
                    intent.setDataAndType(Uri.parse("file://" + getRealPathFromURI(context, packageFileUri)), "application/vnd.android.package-archive");
                    context.startActivity(intent);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }

        return true;
    }

    protected boolean installApk(Context context, Long reference, String pkgName, boolean isFromVirtualFolder) {
        if (context == null) {
            return false;
        }
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri fileUri = downloadManager.getUriForDownloadedFile(reference);
        File file = null;
        String downloadingApkFlagPath = null;
        try {
            try {
                file = new File(new URI(fileUri.toString()));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                file = new File(getRealPathFromURI(context, fileUri));
                //修改bug ：在HUAWEI EVA-AL10中下载成功通过id得到的fileUri不准确导致安装界面直接自动关闭
                fileUri = Uri.fromFile(file);
            }
            downloadingApkFlagPath = file.getAbsolutePath() + FileUtilsSdk.COMMONSDK_TEMP_FILE_SUFFIX;
        } catch (Exception e) {
            e.printStackTrace();
            downloadingApkFlagPath = PushManager.getInstance().getDownloadedAppPath(context, pkgName) + FileUtilsSdk.COMMONSDK_TEMP_FILE_SUFFIX;
            return false;
        } finally {
            if (!TextUtils.isEmpty(downloadingApkFlagPath)) {
                File downloadedAppTempFile = new File(downloadingApkFlagPath);
                boolean flagExists = downloadedAppTempFile.exists();
                if (flagExists) {
                    FileUtilsSdk.deleteFile(downloadedAppTempFile);
                }
            }
            if (!(file != null && file.exists())) {
                return false;
            }
        }
        //vivo 5.1.1 无法弹出安装框bug
        try {
            downloadManager.openDownloadedFile(reference);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            if (!(file != null && file.exists())) {
                return false;
            }
        } catch (NullPointerException e2) {
            e2.printStackTrace();
            return false;
        }

        String packageName = file.getName();
        packageName = packageName.substring(0, packageName.lastIndexOf("."));

        String installedAppSignatures = getInstalledAppSignatures(context, packageName).toString();
        int installedAppVerison = getInstalledAppVerison(context, packageName);
        return installApk(context, packageName, fileUri, installedAppSignatures, installedAppVerison, isFromVirtualFolder);
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj,
                null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        return path;
    }

    public void downloadApkFile(Context context, DownLoadUnit downLoadUnit) {
        downloadApkFile(context, downLoadUnit.getPackageName(), "", downLoadUnit.getVersion(), true, Uri.parse(downLoadUnit.getUrl()), true);
    }

    public void downloadApkFile(final Context context, final String packageName, String downloadSignatures, int downloadVersionCode, boolean forceInstall, final Uri uri,
                                final boolean hideNotification) {
        if (context == null || TextUtils.isEmpty(packageName) || (uri != null && TextUtils.isEmpty(uri.toString()))) {//|| TextUtils.isEmpty(downloadSignatures)
            return;
        }
        DownloadInstallRecord.setDownloadRecodr(context, packageName, uri.toString());
        final File downloadedAppFile = new File(PushManager.getInstance().getDownloadedAppPath(context, packageName));

        final File downloadedAppTempFile = new File(PushManager.getInstance().getDownloadedAppPath(context, packageName)
                + FileUtilsSdk.COMMONSDK_TEMP_FILE_SUFFIX);
        if (downloadedAppTempFile.exists()) {
            long tempFileLastModified = downloadedAppTempFile.lastModified();
            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis - tempFileLastModified > DAY_TIME || !isAppDownloading(packageName, false)) {
                long id = downloadRefKey(packageName);
                downloadRefRemove(id);
                FileUtilsSdk.deleteFile(downloadedAppTempFile);
            } else {
                return;
            }
        }

        if (downloadedAppFile.exists()) {
            boolean isServerStatistic = false;
            installApk(context, packageName, Uri.fromFile(downloadedAppFile), downloadSignatures, downloadVersionCode, isServerStatistic);
            return;

        }


        boolean fileIsDownloadingBySDK = isDownloadRefHasValue(packageName);

        String installedAppSignatures = getInstalledAppSignatures(context, packageName).toString();
        int installedAppVerison = getInstalledAppVerison(context, packageName);
        boolean stopInstallApk = isStopInstallApk(context, packageName, installedAppSignatures, downloadSignatures, installedAppVerison, downloadVersionCode,
                forceInstall);
        if (stopInstallApk) {
            return;
        }

        String serviceString = Context.DOWNLOAD_SERVICE;
        DownloadManager downloadManager;
        downloadManager = (DownloadManager) context.getSystemService(serviceString);
        DownloadManager.Request request = null;
        try {
            request = new DownloadManager.Request(uri);
            //Bug 1.2.3
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);

            if (hideNotification) {
                // 需要权限：android.permission.DOWNLOAD_WITHOUT_NOTIFICATION
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
            } else {
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
            }
            // request.setNotificationVisibility(request.VISIBILITY_VISIBLE);
            request.setDestinationUri(Uri.fromFile(downloadedAppFile));

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        long downloadReference = addDownloadsEnqueueSafely(downloadManager, context, request);
        if (downloadReference == DOWNLOADS_ENQUEUE_EXCEPTION) {
            return;
        }

        if (!downloadedAppTempFile.exists()) {
            try {
                downloadedAppTempFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        downloadRefPut(downloadReference, packageName);

        return;
    }


    public boolean isAppDownloading(String pkg, boolean defaultValue) {
        try {
            DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterByStatus(DownloadManager.STATUS_RUNNING | DownloadManager.STATUS_PAUSED | DownloadManager.STATUS_PENDING
            /*| DownloadManager.STATUS_SUCCESSFUL*/);
            // ArrayList<Long> donwnlonging = new ArrayList<Long>();
            Cursor c = downloadManager.query(query);
            if (c != null) {
                while (c.moveToNext()) {
                    Long id = c.getLong(c.getColumnIndex(DownloadManager.COLUMN_ID));
                    String url = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    if (!TextUtils.isEmpty(url) && url.contains(pkg)) {
                        c.close();
                        return true;
                    } else {
                        String p = getPkgFromDownloadRef(id);
                        if (!TextUtils.isEmpty(p) && p.equals(pkg)) {
                            c.close();
                            return true;
                        }
                    }
                    // donwnlonging.add(id);
                }
            }
            c.close();

            DownloadManager.Query deleteQuery = new DownloadManager.Query();
            deleteQuery.setFilterByStatus(DownloadManager.STATUS_FAILED);
            Cursor deleteCursor = downloadManager.query(deleteQuery);
            boolean isFailed = false;
            if (deleteCursor != null) {
                while (deleteCursor.moveToNext()) {
                    Long id = deleteCursor.getLong(deleteCursor.getColumnIndex(DownloadManager.COLUMN_ID));
                    String url = deleteCursor.getString(deleteCursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    if (!TextUtils.isEmpty(url) && url.contains(pkg)) {
                        downloadManager.remove(id);
                        isFailed = true;
                    }
                }
            }
            deleteCursor.close();
            if (isFailed) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    public boolean isStopInstallApk(Context context, String packageName, String installedAppSignatures, String downloadSignatures, int installedAppVerison,
                                    int downloadVersionCode, boolean forceInstall) {
        boolean isInstall = checkApkExist(context, packageName);
        if (isInstall) {
            if (installedAppSignatures != null && downloadSignatures != null) {
                if (installedAppSignatures.toString().equals(downloadSignatures)) {
                    if (downloadVersionCode <= installedAppVerison) {
                        return true;
                    }
                } else {
                    if (!forceInstall) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean checkApkExist(Context context, String packageName) {
        if (packageName == null || "".equals(packageName)) {
            return false;
        }
        ApplicationInfo info;
        try {
            info = context.getPackageManager().getApplicationInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }

        return info != null;
    }

    public String getDownloadedAppPath(Context context, String pkg) {
        return FileUtilsSdk.getSDcardDownloadPath(context) + pkg + FileUtilsSdk.APK_FILE_SUFFIX;
    }

    public StringBuffer getInstalledAppSignatures(Context context, String pkgName) {
        StringBuffer installedAppSignatures = new StringBuffer();
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(pkgName, PackageManager.GET_SIGNATURES);
            if (packageInfo.signatures != null) {
                for (int i = 0; i < packageInfo.signatures.length; i++) {
                    String signatureStr = parseSignature(packageInfo.signatures[i].toByteArray());
                    installedAppSignatures.append(signatureStr);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return installedAppSignatures;
    }

    public static String parseSignature(byte[] signature) {
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String bytesToHexString(byte[] src) {
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

    public static StringBuffer getSignaturesFromApk(File file) throws IOException {
        StringBuffer signatures = new StringBuffer();
        JarFile jarFile = null;
        jarFile = new JarFile(file);
        try {
            JarEntry je = jarFile.getJarEntry("AndroidManifest.xml");
            byte[] readBuffer = new byte[8192];
            Certificate[] certs = loadCertificates(jarFile, je, readBuffer);
            if (certs != null) {
                for (Certificate c : certs) {
                    Signature s = new Signature(c.getEncoded());
                    String signatureStr = parseSignature(s.toByteArray());
                    signatures.append(signatureStr);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (jarFile != null) {
                jarFile.close();
            }
        }
        return signatures;
    }

    private static Certificate[] loadCertificates(JarFile jarFile, JarEntry je, byte[] readBuffer) {
        try {
            InputStream is = jarFile.getInputStream(je);
            while (is.read(readBuffer, 0, readBuffer.length) != -1) {
            }
            is.close();
            return je != null ? je.getCertificates() : null;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private int getInstalledAppVerison(Context context, String pkgName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(pkgName, PackageManager.GET_ACTIVITIES);
            if (packageInfo != null) {
                return packageInfo.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return INVALID_APP_VERSION_CODE;
    }

    class DownloadChangeObserver extends ContentObserver {

        public DownloadChangeObserver(Handler handler) {

            super(handler);

            // TODO Auto-generated constructor stub

        }

        @Override
        public void onChange(boolean selfChange) {
            // queryDownloadStatus();
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
//            queryDownloadStatus(uri);
        }

    }

    public void queryDownloadStatus(Uri uri) {
        try {
            String path = uri.getLastPathSegment();
            if (!isNum(path)) {
                return;
            }

            long downloadId = Long.parseLong(path);

            String packageName = getPkgFromDownloadRef(downloadId);

            if (mDownloadManager == null) {
                mDownloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
            }
            DownloadManager.Query query = new DownloadManager.Query();

            query.setFilterById(downloadId);
            Cursor c = mDownloadManager.query(query);
            if (c != null && c.moveToFirst()) {
                int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                int reasonIdx = c.getColumnIndex(DownloadManager.COLUMN_REASON);
                int titleIdx = c.getColumnIndex(DownloadManager.COLUMN_TITLE);
                int fileSizeIdx = c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
                int bytesDLIdx = c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
                String title = c.getString(titleIdx);
                int fileSize = c.getInt(fileSizeIdx);
                int bytesDL = c.getInt(bytesDLIdx);
                int reason = c.getInt(reasonIdx);

                float progress = INVALID_PROGRESS_VALUE;
                try {
                    progress = bytesDL * 1f / fileSize * 1f;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                switch (status) {
                    case DownloadManager.STATUS_PAUSED:
                        break;
                    case DownloadManager.STATUS_PENDING:
                        break;
                    case DownloadManager.STATUS_RUNNING:
                        break;
                    case DownloadManager.STATUS_SUCCESSFUL:
                        break;
                    case DownloadManager.STATUS_FAILED:
                        break;
                    default:
                        break;
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

    }

    public static boolean isNum(String str) {
        return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
    }


    private void downloadRefPut(long key, String pkg) {
        try {
            sDownloadRef.put(key, pkg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void downloadRefRemove(long key) {
        try {
            sDownloadRef.remove(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private long downloadRefKey(String pkg) {
        Iterator<Long> keys = sDownloadRef.keySet().iterator();
        while (keys.hasNext()) {
            Long key = keys.next();
            if (sDownloadRef.get(key).equals(pkg)) {
                return key;
            }
        }
        return -1;
    }


    public static String getPkgFromDownloadRef(long key) {
        String pkg = sDownloadRef.get(key);
        if (TextUtils.isEmpty(pkg)) {
            return "";
        }
        return pkg;
    }

    private boolean isVirtualFolderApp(long key) {
        String pkg = sDownloadRef.get(key);
        return isVirtualFolderApp(pkg);
    }


    private boolean isVirtualFolderApp(String pkg) {
        if (TextUtils.isEmpty(pkg)) {
            return false;
        }
        return pkg.startsWith(VIRTUAL_APP_PREFIX);
    }

    private boolean isDownloadRefHasValue(String pkg) {
        return sDownloadRef.containsValue(pkg);
    }

    private boolean isDownloadRefHasKey(long key) {
        return sDownloadRef.containsKey(key);
    }

    private int downloadRefSize() {
        return sDownloadRef.size();
    }

    private boolean isInstallAppsHasPkg(String pkg) {
        return sInstallApps.contains(pkg);
    }

    private void installAppsAdd(String pkg, boolean isVirtualFolder) {
        sInstallApps.add(pkg);
    }

    private void installAppsRemove(String pkg) {
        sInstallApps.remove(pkg);
    }

    public static long addDownloadsEnqueueSafely(DownloadManager downloadManager, final Context context, DownloadManager.Request request) {
        long downloadReference = 0;
        try {
            downloadReference = downloadManager.enqueue(request);
            return downloadReference;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            String sysDownloadsPkg = "com.android.providers.downloads";
            final Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("package:" + sysDownloadsPkg));
            List<ResolveInfo> apps = context.getPackageManager().queryIntentActivities(intent, 0);
            ResolveInfo info = apps.get(0);
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> tasksInfo = am.getRunningTasks(1);
            ActivityManager.RunningTaskInfo runningTaskInfo = tasksInfo.get(0);
            if (runningTaskInfo != null && runningTaskInfo.topActivity != null) {
                String taskPkgName = runningTaskInfo.topActivity.getPackageName();
                if (info != null && info.activityInfo != null && info.activityInfo.applicationInfo.packageName.equals(taskPkgName)) {
                    return DOWNLOADS_ENQUEUE_EXCEPTION;
                }
            }
            return DOWNLOADS_ENQUEUE_EXCEPTION;
        }
    }

    @Override
    public void startDownload() {
        DownLoadUnit downLoadUnit = DownLoadFactory.getInstance().getInsideInterface().getDownLoadUnit();
        if (downLoadUnit != null) {
            downloadApkFile(mContext, downLoadUnit);
        }
    }

    @Override
    public void startInstall() {
        DownLoadUnit downLoadUnit = DownLoadFactory.getInstance().getInsideInterface().getDownLoadUnit();
        downLoadUnit.setIs_tip(false);
        File file = new File(PushManager.getInstance().getDownloadedAppPath(mContext, downLoadUnit.getPackageName()));
        if (file.exists())
            installApk(mContext, downLoadUnit.getPackageName(), Uri.fromFile(file), "", downLoadUnit.getVersion(), false);
    }
}
