/*
 * Copyright (C) 2010 mAPPn.Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ymnet.onekeyclean.cleanmore.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.ymnet.onekeyclean.MarketApplication;
import com.ymnet.onekeyclean.R;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static android.text.format.DateUtils.SECOND_IN_MILLIS;

/**
 * Common Utils for the application
 */
public class Utils {

    public static final String TAG = Utils.class.getSimpleName();

    public static final int DEFAULT_TIMEOUT = (int) (20 * SECOND_IN_MILLIS);

    public static final String ACTION_INSTALL_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
    public static final String MD5_SP = "md5_sp";
    final static String AUTHORITY = "com.android.launcher2.settings";
    final static String TABLE_FAVORITES = "favorites";
    final static Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_FAVORITES);
    // UTF-8 encoding
    private static final String ENCODING_UTF8 = "UTF-8";
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";
    private static final String KEY_PRODUCT_VERSION = "ro.product.version";
    private static final String KEY_VIVO_PRODUCT_VERSION = "ro.vivo.product.version";
    private static final String KEY_SMARTISAN_VERSION = "ro.smartisan.version";
    private static final String KEY_BUILD_VERSION = "ro.build.version.incremental";
    private static final String KEY_BUILD_DISPLAY = "ro.build.display.id";
    private static final String KEY_YUNOS_VERSION = "ro.yunos.version";
    private static final String KEY_YUNOS_BUILD_VERSION = "ro.yunos.build.version";
    private static final String KEY_PRODUCT_MODEL = "ro.product.model";

    private static final String SCHEME = "package";
    /**
     * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.1及之前版本)
     */
    private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
    /**
     * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.2)
     */
    private static final String APP_PKG_NAME_22 = "pkg";
    /**
     * InstalledAppDetails所在包名
     */
    private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
    /**
     * InstalledAppDetails类名
     */
    private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";
    private static final String[] PROJECTION = {"_id", "title",};
    public static boolean sDebug;
    public static String sLogTag;
    private static WeakReference<Calendar> calendar;
    private static HashMap<String, String> authies = new HashMap<String, String>();
    final String PARAMETER_NOTIFY = "notify";

    /**
     * <p>
     * Parse int value from string
     * </p>
     *
     * @param value string
     * @return int value
     */
    public static int getInt(String value) {
        if (TextUtils.isEmpty(value)) {
            return 0;
        }

        try {
            return Integer.parseInt(value.trim(), 10);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * <p>
     * Parse long value from string
     * </p>
     *
     * @param value string
     * @return long value
     */
    public static long getLong(String value) {
        if (value == null)
            return 0L;

        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    public static void V(String msg) {
        if (sDebug) {
            Log.v(sLogTag, msg);
        }
    }

    public static void V(String msg, Throwable e) {
        if (sDebug) {
            Log.v(sLogTag, msg, e);
        }
    }

    public static void D(String msg) {
        if (sDebug) {
            Log.d(sLogTag, msg);
        }
    }

    public static void D(String msg, Throwable e) {
        if (sDebug) {
            Log.d(sLogTag, msg, e);
        }
    }

    public static void I(String msg) {
        if (sDebug) {
            Log.i(sLogTag, msg);
        }
    }

    public static void I(String msg, Throwable e) {
        if (sDebug) {
            Log.i(sLogTag, msg, e);
        }
    }

    public static void W(String msg) {
        if (sDebug) {
            Log.w(sLogTag, msg);
        }
    }

    public static void W(String msg, Throwable e) {
        if (sDebug) {
            Log.w(sLogTag, msg, e);
        }
    }

    public static void E(String msg) {
        if (sDebug) {
            Log.e(sLogTag, msg);
        }
    }

    public static void E(String msg, Throwable e) {
        if (sDebug) {
            Log.e(sLogTag, msg, e);
        }
    }

    /**
     * Returns whether the network is available
     */
    public static boolean isNetworkAvailable(Context context) {
        if (context == null) {
            return false;
        }
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        }
        NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();

        return !(networkInfo == null || !networkInfo.isConnected());
    }

    /**
     * 网络是否ok
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context, boolean alert) {

        boolean active = isNetworkAvailable(context);
        if (active) {
            return true;
        }
        if (alert) {
            Toast.makeText(context, R.string.no_network, Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    /**
     * Get MD5 Code
     */
    public static String getMD5(String text) {
        try {
            byte[] byteArray = text.getBytes("utf8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(byteArray, 0, byteArray.length);
            return convertToHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Convert byte array to Hex string
     */
    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    /**
     * Android 安装应用
     *
     * @param context Application Context
     */
  /*  public static void installApk(final Context context, File file, String sid) {
        if (file.exists()) {
            try {
                String[] args1 = {"chmod", "771", file.getPath().substring(0, file.getPath().lastIndexOf("/"))};
                Process p1 = Runtime.getRuntime().exec(args1);
                p1.waitFor();
                p1.destroy();
                String[] args2 = {"chmod", "777", file.getPath()};
                Process p2 = Runtime.getRuntime().exec(args2);
                p2.waitFor();
                p2.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }

            PackageUtils.startInstall(context, file.getAbsolutePath());
        }
    }*/

    /**
     * 获取网卡地址
     *
     * @return 获取不到 ，返回 ""
     */
    public static String getMac(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String mac = sharedPreferences.getString("MAC", "");
        try {
            if (TextUtils.isEmpty(mac)) {
                WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo info = wifi.getConnectionInfo();
                if (info != null) {
                    mac = info.getMacAddress();
                }
                if (mac != null) {
                    mac = mac.replaceAll(":", "");
                    sharedPreferences.edit().putString("MAC", mac).commit();
                } else {
                    mac = "";
                }
            }
        } catch (Exception e) {
            mac = "";
        }
        return mac;
    }

   /* public static AppInfo getApkInfo(Context context, String filepath) {
        AppInfo appInfo = null;
        PackageManager pm = context.getPackageManager();
        if (pm == null) {
            return null;
        }
        PackageInfo pkgInfo = pm.getPackageArchiveInfo(filepath, PackageManager.GET_ACTIVITIES);
        if (pkgInfo != null) {
            ApplicationInfo info = pkgInfo.applicationInfo;
            // Must include this two lines
            info.sourceDir = filepath;
            info.publicSourceDir = filepath;

            appInfo = new AppInfo();
            appInfo.setAppName(info.loadLabel(pm).toString());
            // 包名
            appInfo.setPackageName(pkgInfo.packageName);
            // 版本号
            appInfo.setVersionName(pkgInfo.versionName);
            // 版本码
            appInfo.setCurVersionCode(pkgInfo.versionCode);
            // apk本地路径
            appInfo.setApkLocUrl(filepath);
        }
        return appInfo;
    }*/

    /**
     * 通过包名启动程序
     *
     * @param context
     * @param packageName
     */
    public static void startAppByPackage(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = null;
        try {
            intent = packageManager.getLaunchIntentForPackage(packageName);
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addCategory(Intent.CATEGORY_HOME);
                context.startActivity(intent);
            } else {
                RunApp(context, packageName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void RunApp(Context context, String packageName) {
        PackageInfo pi;
        try {
            pi = context.getPackageManager().getPackageInfo(packageName, 0);
            Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
            resolveIntent.setPackage(pi.packageName);
            PackageManager pManager = context.getPackageManager();
            List<ResolveInfo> apps = pManager.queryIntentActivities(resolveIntent, 0);

            ResolveInfo ri = apps.iterator().next();
            if (ri != null) {
                packageName = ri.activityInfo.packageName;
                String className = ri.activityInfo.name;
                Intent intent = new Intent();
                ComponentName cn = new ComponentName(packageName, className);
                intent.setComponent(cn);
                intent.setAction("android.intent.action.VIEW");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加密
     *
     * @param str
     */
    public static String strCode(String str) {

        String hash = "N#gK3OgTw#eRUI8+8bZsti78P==4s.5";
        String key = GetMD5.Md5(hash);

        byte[] bstr = str.getBytes();

        int keylen = key.length();
        int strlen = bstr.length;

        byte resultByte[] = new byte[bstr.length];
        for (int i = 0; i < strlen; i++) {
            int k = i % keylen;
            int x = bstr[i];
            int y = key.charAt(k);
            byte z = (byte) (x ^ y);
            resultByte[i] = z;
        }

        try {
            String r = new String(Base64.encode(resultByte, Base64.DEFAULT), "utf-8");
            return r;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 解密
     *
     * @param encryptedVal
     * @param hash
     */
    public static String decryptCode(String encryptedVal, String hash) {
        String key = GetMD5.Md5(hash);
        int keylen = key.length();
        byte[] decode = Base64.decode(encryptedVal, Base64.DEFAULT);
        byte resultByte[] = new byte[decode.length];
        for (int i = 0; i < decode.length; i++) {
            int i1 = i % keylen;
            int b = key.getBytes()[i1];
            int b1 = decode[i];
            byte i2 = (byte) (b ^ b1);
            resultByte[i] = i2;
        }
        return new String(resultByte);
    }

    /**
     * 查询是否已经创建快捷方式
     *
     * @param context
     */
   /* public static void checkInstallShorCut(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean b = preferences.getBoolean(Constants.CHECK_SEND, false);
        if (!b && !isMiUi()) {
            createShortCut(context);
            preferences.edit().putBoolean(Constants.CHECK_SEND, true).commit();
        }
    }*/

    private static String checkLastForAll(Context context) {
        String auth = "";
        List<PackageInfo> infos = context.getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
        if (null != infos) {
            for (PackageInfo info : infos) {
                ProviderInfo[] pinfos = info.providers;
                if (null != pinfos) {
                    for (ProviderInfo pinfo : pinfos) {
                        if (!TextUtils.isEmpty(pinfo.readPermission)) {
                            if (pinfo.packageName.contains("launcher")) {
                                auth = pinfo.authority;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return auth;
    }

    /**
     * 创建快捷方式
     *
     * @param context
     */
   /* private static void createShortCut(Context context) {
        // 名称
        String title = context.getString(R.string.app_name_new_logo);

        // 桌面已经存在快捷方式
        if (hasShortcut(context, title, context.getPackageName())) {
            return;
        }

        // 点击快捷方式后操作Intent,快捷方式建立后，再次启动该程序
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClass(context, NavigationActivity.class);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        Intent addShortCut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        // 设置快捷方式的标题
        addShortCut.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
        addShortCut.putExtra("duplicate", false);
        // 设置快捷方式的图标
        addShortCut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(context, R.drawable.ic_launcher));
        // 设置快捷方式对应的Intent
        addShortCut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        // 发送广播添加快捷方式
        context.sendBroadcast(addShortCut);
    }*/

    /**
     * create the ball desk shortcut
     *
     * @param context
     */
    /*public static void createCleanShortCut(Context context) {
        // 名称
        String title = context.getString(R.string.short_cuts_name_old);
        // 桌面已经存在快捷方式
        if (LauncherUtil.isShortCutExist(context, title, context.getPackageName())) {
            return;
        }
        Intent addShortCut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        // 设置快捷方式的标题
        addShortCut.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
        addShortCut.putExtra("duplicate", false);
        // 设置快捷方式的图标
        addShortCut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(context, R.drawable.desk_clear_launcher));
        // 设置快捷方式对应的Intent
        addShortCut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent("com.market2345.clean")*//* intent *//*);
        // 发送广播添加快捷方式
        context.sendBroadcast(addShortCut);

        if (isMiUi()) {
            Toast.makeText(C.get(), context.getString(R.string.ball_miui_add), Toast.LENGTH_LONG).show();
        }
    }*/

    /*public static void checkInstallCleanShorCut(Context context) {
        SharedPreferences sharedPreferences = SPUtil.getSharedPreferences(context);
        boolean b = sharedPreferences.getBoolean("check_clean_shortcut", false);
        if (!b) {
            createCleanShortCut(context);
            sharedPreferences.edit().putBoolean("check_clean_shortcut", true).commit();
        }
    }

    public static void createMyGameShortCut(Context context, List<InstalledApp> apps) {
        String title = context.getString(R.string.short_cut_mygame);
        if (LauncherUtil.isShortCutExist(context, title, context.getPackageName())) {
            return;
        }
        MyGameManage.setIsCreatedMyGame(context, true);
        Intent addMygameShortCut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        addMygameShortCut.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
        // 设置快捷方式的图标
        if (apps == null || apps.isEmpty()) {
            return;
        }
        Bitmap[] bitmaps = new Bitmap[apps.size() > 4 ? 4 : apps.size()];
        for (int i = 0; i < apps.size(); i++) {
            if (i < 4) {
                bitmaps[i] = AppsUtils.getIcon(context, apps.get(i).packageName);
            }
        }

        // 设置快捷方式对应的Intent
        addMygameShortCut.putExtra(
                Intent.EXTRA_SHORTCUT_INTENT, new Intent("com.market2345.mygame")); /*//* intent *//*
        addMygameShortCut.putExtra("duplicate", false);
        addMygameShortCut.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapUtil.combineBitmap(context, bitmaps));
        // 发送广播添加快捷方式
        context.sendBroadcast(addMygameShortCut);
    }*/

    private static String getAuthorityFromPermission(Context context, String permission) {
        if (context == null || TextUtils.isEmpty(permission)) {
            return null;
        }

        try {
            List<PackageInfo> list = context.getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
            if (list != null && list.size() > 0) {
                for (PackageInfo info : list) {
                    ProviderInfo[] pis = info.providers;
                    if (pis != null && pis.length > 0) {
                        for (ProviderInfo pi : pis) {
                            if (permission.equals(pi.readPermission)) {
                                return pi.authority;
                            }

                            if (permission.equals(pi.writePermission)) {
                                return pi.authority;
                            }
                        }
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 根据 title 判断快捷方式是否存在；尚未考虑非原生桌面
     *
     * @return
     */
    public static boolean hasShortcut(Context context, String title, String iconPkg) {
        boolean result = false;

        String uriString = null;
        String authorty = getAuthorityFromPermission(context, "com.android.launcher.permission.READ_SETTINGS");

        if (TextUtils.isEmpty(authorty)) {
            if (Build.VERSION.SDK_INT < 8) {
                uriString = "content://com.android.launcher.settings/favorites?notify=true";
            } else {
                uriString = "content://com.android.launcher2.settings/favorites?notify=true";
            }
        } else {
            uriString = "content://" + authorty + "/favorites?notify=true";
        }

        Cursor c = null;
        try {
            final Uri CONTENT_URI = Uri.parse(uriString);
            c = context.getContentResolver().query(CONTENT_URI, new String[]{"iconPackage"}, "title=?", new String[]{title}, null);
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();

                String pkg = null;
                try {
                    pkg = c.getString(0);
                } catch (Throwable e1) {
                    e1.printStackTrace();
                }

                if (iconPkg.equals(pkg)) {
                    result = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return result;
    }

    public static String strCode(String str, String dict) {
        String key = GetMD5.Md5(dict);

        byte[] bstr = str.getBytes();

        int keylen = key.length();
        int strlen = bstr.length;

        byte resultByte[] = new byte[bstr.length];
        for (int i = 0; i < strlen; i++) {
            int k = i % keylen;
            int x = bstr[i];
            int y = key.charAt(k);
            byte z = (byte) (x ^ y);
            resultByte[i] = z;
        }

        try {
            String r = new String(Base64.encode(resultByte, Base64.DEFAULT), "utf-8");
            return r;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getTwelveLengthString(String value) {

        if (value == null)
            return "";

        if (value.length() > 12) {
            return value.substring(0, 12);
        }
        return value;
    }

    public static String getSubLengthString(String value) {

        if (value == null) {
            return "";
        }
        if (value.length() > 5) {
            value = "(" + value.substring(0, 2) + "..." + value.substring(value.length() - 2) + ")";
            return value;
        }
        return "(" + value + ")";
    }

    public static String getChannel() {
        return MarketApplication.getChannel();
    }

    public static String getLocalFileMd5(String mFileName) {
        if (TextUtils.isEmpty(mFileName)) {
            return "";
        }
        File file = new File(mFileName);
        if (!file.isFile()) {
            return "";
        }
        return getLocalFileMd5(file);
    }

    public static String getLocalFileMd5(File file) {
        if (file != null && file.exists()) {
            MessageDigest digest = null;
            FileInputStream in = null;
            byte buffer[] = new byte[1024];
            int len;
            try {
                digest = MessageDigest.getInstance("MD5");
                in = new FileInputStream(file);
                while ((len = in.read(buffer, 0, 1024)) != -1) {
                    digest.update(buffer, 0, len);
                }
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
            return convertToHex(digest.digest());
        }
        return "";
    }

   /* public static String formatSizeToMB(long fileSize) {
        if (fileSize == 0) {
            return "0KB";
        } else {
            return ApplicationUtils.formatFileSizeToString(fileSize);
        }
    }*/

    public static boolean isMiUi() {
        try {
            Properties properties = new Properties();
            File file = new File(Environment.getRootDirectory(), "build.prop");
            FileInputStream fis = new FileInputStream(file);
            properties.load(fis);
            fis.close();
            return properties.getProperty(KEY_MIUI_VERSION_CODE, null) != null
                    || properties.getProperty(KEY_MIUI_VERSION_NAME, null) != null
                    || properties.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getMiUiName() {
        try {
            Properties properties = new Properties();
            FileInputStream fileInputStream = new FileInputStream(new File(Environment.getRootDirectory(), "build.prop"));
            properties.load(fileInputStream);
            fileInputStream.close();

            return properties.getProperty(KEY_MIUI_VERSION_NAME, null);
        } catch (Throwable e) {
            return null;
        }
    }

    public static void showInstalledAppDetails(Context context, String packageName) {
        Intent intent = new Intent();
        final int apiLevel = Build.VERSION.SDK_INT;
        if (apiLevel >= 9) { // 2.3（ApiLevel 9）以上，使用SDK提供的接口
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts(SCHEME, packageName, null);
            intent.setData(uri);
        } else {
            // 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）
            // 2.2和2.1中，InstalledAppDetails使用的APP_PKG_NAME不同。
            final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22
                    : APP_PKG_NAME_21);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName(APP_DETAILS_PACKAGE_NAME,
                    APP_DETAILS_CLASS_NAME);
            intent.putExtra(appPkgName, packageName);
        }

        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (resolveInfo.size() > 0) {
            context.startActivity(intent);
        }

    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 保存原图片的width, height
        final int width = options.outWidth;
        final int height = options.outHeight;
        int inSampleSize = 1;

        if (width > reqWidth && height > reqHeight) {
            int w = (int) Math.ceil(width / (float) reqWidth);
            int h = (int) Math.ceil(height / (float) reqHeight);
            inSampleSize = w > h ? w : h;
        }

        return inSampleSize;
    }

    /**
     * 检测是否是积分渠道的APK
     *
     * @param info 应用的PackageInfo
     * @return 如果是积分渠道软件则返回true, 否则false
     */
    public static boolean isLmApk(PackageInfo info) {
        Enumeration<?> entries = null;
        ZipFile zipfile = null;
        try {
            String sourceDir = info.applicationInfo.sourceDir;
            zipfile = new ZipFile(sourceDir);
            entries = zipfile.entries();
            ZipEntry entry;
            while (entries.hasMoreElements()) {
                entry = ((ZipEntry) entries.nextElement());
                if (entry.getName().startsWith("META-INF/appid_")) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (zipfile != null) {
                try {
                    zipfile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "com.UCMobile".equals(info.applicationInfo.packageName);
    }

    public static String getAppID(PackageInfo info) {

        String sourceDir = info.applicationInfo.sourceDir;
        //以zip文件打开
        Enumeration<?> entries = null;
        ZipFile zipfile = null;
        try {
            zipfile = new ZipFile(sourceDir);
            entries = zipfile.entries();
            ZipEntry entry;
            while (entries.hasMoreElements()) {
                entry = ((ZipEntry) entries.nextElement());
                if (entry.getName().startsWith("META-INF/appid_")) {
                    String[] suffixs = entry.getName().split("_");
                    if (suffixs.length == 2) {
                        return suffixs[1];
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zipfile != null) {
                try {
                    zipfile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            zipfile = new ZipFile(sourceDir);
            entries = zipfile.entries();
            ZipEntry entry;
            while (entries.hasMoreElements()) {
                entry = ((ZipEntry) entries.nextElement());
                if (entry.getName().startsWith("META-INF/channel_")) {
                    String[] suffixs = entry.getName().split("_");
                    if (suffixs.length == 3) {
                        return suffixs[1];
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zipfile != null) {
                try {
                    zipfile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    public static Set<String> getSignaturesFromApk(File file) throws IOException {
        Set<String> signatures = new HashSet<String>();
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(file);
            JarEntry je = jarFile.getJarEntry("AndroidManifest.xml");
            byte[] readBuffer = new byte[8192];
            Certificate[] certs = loadCertificates(jarFile, je, readBuffer);
            if (certs != null) {
                for (Certificate c : certs) {
                    Signature s = new Signature(c.getEncoded());
                    signatures.add(parseSignature(s.toByteArray()));
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
        }
        return null;
    }

    private static String parseSignature(byte[] signature) {
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

    public static final boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    /*public static void validateDownPartial(Context context, Collection<App> apps) {
        SharedPreferences sp = context.getSharedPreferences(MD5_SP, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        for (App app : apps) {
            if (validateFormerMD5(context, app, sp, gson)) {
                app.mDownPartial = Downloads.Impl.DOWN_PARTIAL;
            }
        }
    }

    public static boolean validateDownPartial(Context context, App app) {
        SharedPreferences sp = context.getSharedPreferences(MD5_SP, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        if (validateFormerMD5(context, app, sp, gson)) {
            app.mDownPartial = Downloads.Impl.DOWN_PARTIAL;
            return true;
        }
        return false;
    }

    public static boolean validateDownPartial(Context context, DownloadInfo downloadInfo) {
        SharedPreferences sp = context.getSharedPreferences(MD5_SP, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        return validateFormerMD5(context, downloadInfo, sp, gson);
    }


    private static boolean validateFormerMD5(Context context, App app, SharedPreferences sp, Gson gson) {
        if (!TextUtils.isEmpty(app.low_md5)) {
            InstalledApp installedApp = DataCenterObserver.get(context).getInstalledApp(app.packageName);
            if (installedApp != null && !TextUtils.isEmpty(installedApp.storeLocation)) {
                return app.low_md5.equals(getLocalFileMd5(installedApp, sp, gson));
            }
        }
        return false;
    }


    private static boolean validateFormerMD5(Context context, DownloadInfo downloadInfo, SharedPreferences sp, Gson gson) {
        if (!TextUtils.isEmpty(downloadInfo.mLowMD5)) {
            InstalledApp installedApp = DataCenterObserver.get(context).getInstalledApp(downloadInfo.mPackageName);
            if (installedApp != null && !TextUtils.isEmpty(installedApp.storeLocation)) {
                return downloadInfo.mLowMD5.equals(getLocalFileMd5(installedApp, sp, gson));
            }
        }
        return false;
    }


    public static String getLocalFileMd5(InstalledApp installedApp, SharedPreferences sp, Gson gson) {
        String JsonMD5 = sp.getString(installedApp.packageName, "");
        if (!TextUtils.isEmpty(JsonMD5)) {
            AppMD5 appMD5 = gson.fromJson(JsonMD5, AppMD5.class);
            if (appMD5.lastModify == installedApp.lastUpdateTime) {
                return appMD5.MD5;
            }
        }


        String MD5 = Utils.getLocalFileMd5(installedApp.storeLocation);
        AppMD5 appMD5 = new AppMD5();
        appMD5.lastModify = installedApp.lastUpdateTime;
        appMD5.MD5 = MD5;

        sp.edit().putString(installedApp.packageName, gson.toJson(appMD5)).commit();

        return MD5;
    }*/

    /**
     * To judge whether INSTALL_NON_MARKET_APPS settings is enabled.
     *
     * @return true if the third-party applications are allowed to install, otherwise false.
     */
    public static boolean isNonMarketAppsAllowed(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(),
                Settings.Secure.INSTALL_NON_MARKET_APPS, 0) > 0;
    }


    /**
     * 将半角字符转化为全角字符
     *
     * @return 全角编码的String
     */
    public static String toFullWidth(String input) {
        if (null == input) {
            return null;
        }

        char[] arr = input.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == 12288) {
                arr[i] = (char) 32;
                continue;
            }
            if (arr[i] > 65280 && arr[i] < 65375)
                arr[i] = (char) (arr[i] - 65248);
        }
        return new String(arr);
    }

    public static void getSuperVariable(Object obj, String variableName, Boolean flag) {
        try {
            Field f = obj.getClass().getSuperclass().getSuperclass().getDeclaredField(variableName);
            f.setAccessible(true);
            f.set(obj, flag);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static boolean isHTC() {
        return containsBrand("htc");
    }

    public static boolean isVivo() {
        return containsBrand("vivo");
    }

    public static boolean isSmartisan() {
        return containsBrand("smartisan");
    }

    public static boolean isLenovo() {
        return containsBrand("lenovo");
    }

    private static boolean containsBrand(String brand) {
        if (TextUtils.isEmpty(brand)) {
            return false;
        }

        boolean flag;

        try {
            brand = brand.toLowerCase();
            flag = Build.BRAND.toLowerCase().contains(brand) || Build.FINGERPRINT.toLowerCase().contains(brand);
        } catch (Exception exception) {
            return false;
        }

        return flag;
    }


    public static boolean isYunOS() {
        FileInputStream fis = null;
        try {
            Properties properties = new Properties();
            File file = new File(Environment.getRootDirectory(), "build.prop");
            fis = new FileInputStream(file);
            properties.load(fis);
            return properties.getProperty(KEY_YUNOS_VERSION, null) != null
                    || properties.getProperty(KEY_YUNOS_BUILD_VERSION, null) != null;
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }

    public static boolean isFlymeOS() {
        return Build.USER.toLowerCase().contains("flyme");
    }

    public static String getProductVersion() {
        return getProperty(KEY_PRODUCT_VERSION);
    }

    public static String getVersionIncremental() {
        return getProperty(KEY_BUILD_VERSION);
    }

    public static String getDisplayId() {
        return getProperty(KEY_BUILD_DISPLAY);
    }

    /**
     * Get OS version label of this phone.
     *
     * @return null or specific OS version
     */
    public static String getOSVersion() {
        if (isMiUi()) {
            return "miui-" + getProperty(KEY_MIUI_VERSION_NAME) + "-" + getProperty(KEY_BUILD_VERSION);
        } else if (isVivo()) {
            String vivoVersion = getProperty(KEY_VIVO_PRODUCT_VERSION);
            if (!TextUtils.isEmpty(vivoVersion)) {
                return vivoVersion;
            }
        } else if (isSmartisan()) {
            String smVersion = getProperty(KEY_SMARTISAN_VERSION);
            if (!TextUtils.isEmpty(smVersion)) {
                return smVersion;
            }
        } else if (isYunOS()) {
            return "YunOS " + getProperty(KEY_YUNOS_BUILD_VERSION);
        }

        return getDefaultOSVersion();
    }

    private static String getDefaultOSVersion() {
        String displayId = getDisplayId();
        String proVersion = getProductVersion();
        String incremental = getVersionIncremental();

        String osVersion = "";
        if (!TextUtils.isEmpty(displayId)) {
            osVersion += "DisplayId:" + displayId;
        }

        if (!TextUtils.isEmpty(proVersion)) {
            osVersion += "; ProductVersion:" + proVersion;
        }

        if (!TextUtils.isEmpty(incremental)) {
            osVersion += "; VersionIncremental:" + incremental;
        }

        return osVersion;
    }

    /**
     * Get value that combined with key in build.prop file.
     *
     * @param key One of keys which are contained in various build.prop
     * @return The value in name/value mappings
     */
    private static String getProperty(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }

        FileInputStream fis = null;
        try {
            Properties properties = new Properties();
            File file = new File(Environment.getRootDirectory(), "build.prop");
            fis = new FileInputStream(file);
            properties.load(fis);
            return properties.getProperty(key, null);
        } catch (Exception e) {
            return null;
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }

    /**
     * Fetch model of this phone.
     *
     * @return null or specific model
     */
    public static String getPhoneModel() {
        return getProperty(KEY_PRODUCT_MODEL);
    }

    public static String getVersonName(Context context) {
        String versonName = "";
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versonName = pInfo.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return versonName;
    }

    public static int getVersionCode(Context context) {
        int versionCode = -1;
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionCode = pInfo.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionCode;
    }

    /**
     * Returns the consumer friendly device name
     */
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;
        String phrase = "";
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase += Character.toUpperCase(c);
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase += c;
        }
        return phrase;
    }

    /*public static App retrieveApp(String softId) {
        App res = null;
        int sid = -1;
        try {
            sid = Integer.valueOf(softId);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (sid > 0) {
            try {
                SingleAppResp c;
                Response<DetailAppEntity> response = TApier.get().getAppByID(sid).execute();
                c = new SingleAppRespMapper().transform(response);
                if (c != null) {
                    if (c.response != null && c.response.code == 200 && c.list != null) {
                        res = c.list;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ConvertException e) {
                e.printStackTrace();
            }
        }

        return res;
    }*/


    /**
     * To judge whether the framework SDK version is above this versionCode.
     *
     * @param versionCode Reference to {@link Build.VERSION_CODES}
     * @return true if the user-visible SDK version of the framework
     * is above versionCode, false otherwise.
     */
    public static boolean isAboveAPI(int versionCode) {
        return versionCode > 0 && Build.VERSION.SDK_INT > versionCode;
    }


    public static boolean isConnectionFast(int type, int subType) {
        if (type == ConnectivityManager.TYPE_WIFI) {
            System.out.println("CONNECTED VIA WIFI");
            return true;
        } else if (type == ConnectivityManager.TYPE_MOBILE) {
            switch (subType) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return false; // ~ 14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return true; // ~ 400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return true; // ~ 600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return false; // ~ 100 kbps
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return true; // ~ 2-14 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return true; // ~ 700-1700 kbps
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return true; // ~ 1-23 Mbps
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return true; // ~ 400-7000 kbps


                case 14:
                    return true; // ~ 1-2 Mbps
                case 12:
                    return true; // ~ 5 Mbps
                case 15:
                    return true; // ~ 10-20 Mbps
                case 11:
                    return false; // ~25 kbps
                case 13:
                    return true; // ~ 10+ Mbps
                // Unknown
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                    return false;
                default:
                    return false;
            }
        } else {
            return false;
        }
    }
}