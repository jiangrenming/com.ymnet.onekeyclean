package com.ymnet.onekeyclean.cleanmore.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.umeng.analytics.MobclickAgent;
import com.ymnet.onekeyclean.MarketApplication;
import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.constants.Constants;
import com.ymnet.onekeyclean.cleanmore.utils.C;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Application utilities.
 */
public class ApplicationUtils {

    private static String TAG = "ApplicationUtils";

    private static int mFaviconSize = -1;

    private static int mImageButtonSize = -1;

    private static int mFaviconSizeForBookmarks = -1;

    private final static int MyDENSITY_XHIGH = 320;

    public static int getFaviconSizeForBookmarks(Activity activity) {
        if (mFaviconSizeForBookmarks == -1) {
            DisplayMetrics metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

            switch (metrics.densityDpi) {
                case DisplayMetrics.DENSITY_LOW:
                    mFaviconSizeForBookmarks = 12;
                    break;
                case DisplayMetrics.DENSITY_MEDIUM:
                    mFaviconSizeForBookmarks = 16;
                    break;
                case DisplayMetrics.DENSITY_HIGH:
                    mFaviconSizeForBookmarks = 24;
                    break;
                case MyDENSITY_XHIGH:
                    mFaviconSizeForBookmarks = 36;
                    break;
                default:
                    mFaviconSizeForBookmarks = 16;
            }
        }

        return mFaviconSizeForBookmarks;
    }

    /**
     * Check if the SD card is available. Display an alert if not.
     *
     * @param context     The current context.
     * @param showMessage If true, will display a message for the user.
     * @return True if the SD card is available, false otherwise.
     */
    public static boolean checkCardState(Context context, boolean showMessage) {
        // Check to see if we have an SDCard
        String status = Environment.getExternalStorageState();
        if (!status.equals(Environment.MEDIA_MOUNTED)) {

            String messageId;

            // Check to see if the SDCard is busy, same as the music app
            if (status.equals(Environment.MEDIA_SHARED)) {
                messageId = "SD不可用";
            } else {
                messageId = "SD错误";
            }

            if (showMessage) {
                ApplicationUtils.showToastShort(context, "SD错误");
            }

            return false;
        }

        return true;
    }

    /**
     * String 转化为URLEncoder
     *
     * @param str
     * @return
     */
    public static String changeToUrlencode(String str) {
        if (str == null) {
            return "";
        }
        try {

            String changBefore = URLEncoder.encode(str, "gbk");
            return changBefore;
        } catch (Exception ex) {
            MobclickAgent.reportError(C.get(), "com.ymnet.onekeyclean.cleanmore.common.ApplicationUtils:" + ex.toString());
            return str;
        }
    }

    /**
     * String 转化为URLEncoder
     *
     * @param str
     * @return
     */
    public static String UrlencodeToString(String str) {
        if (str == null) {
            return "";
        }
        try {

            String stringinfo = URLDecoder.decode(str, "utf-8");

            return stringinfo;
        } catch (Exception ex) {
            MobclickAgent.reportError(C.get(), "com.ymnet.onekeyclean.cleanmore.common.ApplicationUtils:" + ex.toString());
            return str;
        }
    }

    /**
     * Load the start page html.
     *
     * @param context The current context.
     * @return The start page html.
     */
    public static String getStartPage(Context context) {
        String result = null;

        return result;
    }

    /**
     * Load a raw string resource.
     *
     * @param context    The current context.
     * @param resourceId The resource id.
     * @return The loaded string.
     */
    private static String getStringFromRawResource(Context context, int resourceId) {
        String result = null;

        InputStream is = context.getResources().openRawResource(resourceId);
        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } catch (IOException e) {
                MobclickAgent.reportError(C.get(), "com.ymnet.onekeyclean.cleanmore.common.ApplicationUtils:" + e.toString());
                Log.w("ApplicationUtils", String.format("Unable to load resource %s: %s", resourceId, e.getMessage()));
            } finally {
                StreamUtil.closeInputStream(is);
                StreamUtil.closeReader(reader);
            }
            result = sb.toString();
        } else {
            result = "";
        }

        return result;
    }

    // 获取到文件的名称
    public static String getFileName(String urlString) {
        try {
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();
            String fileHeader = conn.getHeaderField("Content-Disposition");

            if (fileHeader != null) {
                fileHeader = fileHeader.toLowerCase(Locale.getDefault());
                int index = fileHeader.indexOf("filename");
                if (index != -1) {
                    String name = fileHeader.substring(index + "filename".length() + 1, fileHeader.length());

                    name = name.replace("'", "").replace("\"", "").replace("%20", " ");

                    name = ApplicationUtils.UrlencodeToString(name);
                    return name;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String fileName = urlString.substring(urlString.lastIndexOf("/") + 1);
        int queryParamStart = fileName.indexOf("?");
        if (queryParamStart > 0) {
            fileName = fileName.substring(0, queryParamStart);
        }

        fileName = fileName.replace("%20", " ").replace("'", "").replace("\"", "");
        fileName = ApplicationUtils.UrlencodeToString(fileName);
        return fileName;
    }

    public static String getFilesize(String urlString) {
        int nFileLength = -1;
        try {
            URL url = new URL(urlString);
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestProperty("Content-type", "application/x-java-serialized-object");
            int responseCode = httpConnection.getResponseCode();
            if (responseCode >= 400) {

                return "0";
            }

            nFileLength = httpConnection.getContentLength();

        } catch (Exception e) {
            e.printStackTrace();
            MobclickAgent.reportError(C.get(), "com.ymnet.onekeyclean.cleanmore.common.ApplicationUtils:" + e.toString());
            return "0";
        }
        return nFileLength + "";

    }

    public static String getUrlContentType(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestProperty("Content-type", "application/x-java-serialized-object");
            int responseCode = httpConnection.getResponseCode();
            if (responseCode >= 400) {
                return "";
            }
            String MIME = httpConnection.getContentType();

            return MIME;
        } catch (Exception e) {
            e.printStackTrace();
            MobclickAgent.reportError(C.get(), "com.ymnet.onekeyclean.cleanmore.common.ApplicationUtils:" + e.toString());
            return "";
        }
    }

    public static String getFilesize(String urlString, Context mContext) {
        int nFileLength = -1;

        try {

            URL url = new URL(urlString);
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestProperty("Content-type", "application/x-java-serialized-object");
            int responseCode = httpConnection.getResponseCode();
            if (responseCode >= 400) {

                return "未知";
            }

            nFileLength = httpConnection.getContentLength();
        } catch (Exception e) {
            e.printStackTrace();
            MobclickAgent.reportError(C.get(), "com.ymnet.onekeyclean.cleanmore.common.ApplicationUtils:" + e.toString());
            return "未知";
        }
        return Formatter.formatFileSize(mContext, nFileLength);

    }

    public static void showToastShort(Context context, String infoString) {
        Toast.makeText(context, infoString, Toast.LENGTH_SHORT).show();
    }

    public static void showToastLong(Context context, String infoString) {
        Toast.makeText(context, infoString, Toast.LENGTH_LONG).show();
    }

    /**
     * 测试专用Toast
     *
     * @param context
     * @param infoString
     */
    public static void showToastShort_test(Context context, String infoString) {
        Toast.makeText(context, infoString, Toast.LENGTH_SHORT).show();
    }

    public static void showToastLong_test(Context context, String infoString) {
        Toast.makeText(context, infoString, Toast.LENGTH_LONG).show();
    }

    public static int ACTIVITY_HIGHT = 0;

    public static int ACTIVITY_WIDTH = 0;

    public static int ACTIVITY_TOP_HIGHT = 0;

    /**
     * 获取手机屏幕的像素高
     *
     * @param context
     * @return
     */
    public static int getScreenHight(Activity context) {
        if (ACTIVITY_HIGHT == 0 && ACTIVITY_HIGHT != -1) {
            DisplayMetrics dm = new DisplayMetrics();
            context.getWindowManager().getDefaultDisplay().getMetrics(dm);
            ACTIVITY_HIGHT = dm.heightPixels;
            return ACTIVITY_HIGHT;
        } else {
            return ACTIVITY_HIGHT;
        }
    }

    /**
     * 获取手机状态栏的高度
     *
     * @param mContext
     * @return
     */
    public static int getStatusBarHeight(Activity mContext) {
        Rect frame = new Rect();
        mContext.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        return statusBarHeight;
    }

    /**
     * 获取手机屏幕的像素宽
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Activity context) {
        if (ACTIVITY_WIDTH == 0 && ACTIVITY_WIDTH != -1) {
            DisplayMetrics dm = new DisplayMetrics();
            context.getWindowManager().getDefaultDisplay().getMetrics(dm);
            ACTIVITY_WIDTH = dm.widthPixels;
            return ACTIVITY_WIDTH;
        } else {
            return ACTIVITY_WIDTH;
        }
    }

    public static int getScreenWidth() {
        return ACTIVITY_WIDTH;
    }

    /**
     * 获取应用程序的像素高度
     *
     * @param context
     * @return
     */
    public static int getTopBarHight(Activity context) {
        if (ACTIVITY_TOP_HIGHT == 0 && ACTIVITY_TOP_HIGHT != -1) {
            Rect frame = new Rect();
            context.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            ACTIVITY_TOP_HIGHT = frame.top;
            return ACTIVITY_TOP_HIGHT;
        } else {
            return ACTIVITY_TOP_HIGHT;
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static void shareImage(Activity activity, String imagepathString) {

        File imageFile = new File(imagepathString);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(imageFile)); // 传输图片或者文件
        // 采用流的方式
        shareIntent.putExtra(Intent.EXTRA_TEXT, "分享"); // 说明信息
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        shareIntent.setType("image/*"); // 分享图片

        try {
            activity.startActivity(Intent.createChooser(shareIntent, "分享"));
        } catch (android.content.ActivityNotFoundException ex) {
            // if no app handles it, do nothing
        }
    }

    /**
     * 获取应用程序版本
     *
     * @param context
     * @return
     */
    public static int getVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            // 当前版本的版本号
            return info.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return 1;
        }
    }

    /**
     * 获取应用程序外部版本
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {

        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            // 当前版本的版本号
            return info.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取sdk版本号
     *
     * @return sdk版本号
     */
    public static int getSdkVersion() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * 网络是否ok
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context, boolean alert) {

        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        if (alert) {
            Toast.makeText(context, R.string.no_network, Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    /**
     * 网络是否ok
     *
     * @param context
     * @return
     */
    public static boolean isWifiNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isConnected() && ni.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    public static boolean isMobileNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isConnected() && ni.getType() == ConnectivityManager.TYPE_MOBILE) {
            return true;
        }
        return false;
    }

    /**
     * 获取网络类型：WiFi、2G、3G、4G
     *
     * @param context
     * @return
     */
    public static String getNetworkType(Context context) {
        String result = null;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isConnected()) {
            int type = ni.getType();
            switch (type) {
                case ConnectivityManager.TYPE_MOBILE:
                    int subtype = ni.getSubtype();
                    switch (subtype) {
                        case TelephonyManager.NETWORK_TYPE_GPRS:
                        case TelephonyManager.NETWORK_TYPE_EDGE:
                        case TelephonyManager.NETWORK_TYPE_CDMA:
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            return "2G";
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            return "3G";
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            return "4G";
                        default:
                            return "UNKNOWN";
                    }
                case ConnectivityManager.TYPE_WIFI:
                    result = "WiFi";
                    break;

                default:
                    break;
            }
        }

        return result;
    }

    /**
     * 简单判断是否合法url
     *
     * @param text
     * @return
     */
    public static boolean isUrlSimple(String text) {
        if (text == null || text.trim().equals("")) {
            return false;
        }
        String[] a = text.split("\\.");
        if (a != null && a.length >= 2) {
        } else {
            return false;
        }
        return true;
    }

    public static boolean isUrl(String pInput) {
        if (pInput == null) {
            return false;
        }
        if (pInput.endsWith("/")) {
            pInput = pInput.substring(0, pInput.lastIndexOf("/"));
        }

        String regEx = "^(http|https|ftp)\\://([a-zA-Z0-9\\.\\-]+(\\:[a-zA-" + "Z0-9\\.&%\\$\\-]+)*@)?((25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{"
                + "2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}" + "[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|"
                + "[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-" + "4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])|([a-zA-Z0"
                + "-9\\-]+\\.)*[a-zA-Z0-9\\-]+\\.[a-zA-Z]{2,4})(\\:[0-9]+)?(/" + "[^/][a-zA-Z0-9\\.\\,\\?\\'\\\\/\\+&%\\$\\=~_\\-@]*)*$";
        Pattern p = Pattern.compile(regEx);
        Matcher matcher = p.matcher(pInput);
        return matcher.matches();
    }

    protected static final String DATE_TIME_LASTMODIFIED = "yyyy-MM-dd HH:mm";// 文件属性,最后修改

    protected static final String DATE_TIME_TODAY = "今天 HH:mm";// 今天

    protected static final String DATE_TIME_YESTERDAY = "昨天 HH:mm";// 昨天

    protected static final String DATE_TIME_NOT_TODAY_FORMAT = "MM-dd";// 非今天

    /**
     * nixtime转化成yyyy-MM-dd HH:mm
     *
     * @param unixtime
     * @return
     */
    public static String getFormatDateTimeFromUnixTime(String unixtime, String formatString) {
        if (formatString == null) {
            formatString = DATE_TIME_NOT_TODAY_FORMAT;
        }
        String tmpdatestr = "";
        try {
            int unixtimeInt = Integer.parseInt(unixtime);
            if (unixtimeInt != 0) {
                Timestamp tmpdate = new Timestamp(unixtimeInt * 1000L);
                // 判断年月日
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR) - 1900;
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DATE);

                if (year == tmpdate.getYear() && month == tmpdate.getMonth() && day == tmpdate.getDate()) {
                    formatString = DATE_TIME_TODAY;
                } else if (year == tmpdate.getYear() && month == tmpdate.getMonth() && day == (tmpdate.getDate() + 1)) {
                    formatString = DATE_TIME_YESTERDAY;
                }
                SimpleDateFormat tmpfmt = new SimpleDateFormat(formatString);
                tmpdatestr = tmpfmt.format(tmpdate);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MobclickAgent.reportError(C.get(), "com.ymnet.onekeyclean.cleanmore.common.ApplicationUtils:" + ex.toString());
        }
        return tmpdatestr;
    }

    /**
     * nixtime转化成yyyy-MM-dd HH:mm
     *
     * @param unixtime
     * @return
     */
    public static String getLastModifiedFomat(String unixtime) {
        String tmpdatestr = "";
        try {
            long unixtimeInt = Long.parseLong(unixtime);
            if (unixtimeInt != 0) {
                Timestamp tmpdate = new Timestamp(unixtimeInt);
                SimpleDateFormat tmpfmt = new SimpleDateFormat(DATE_TIME_LASTMODIFIED);
                tmpdatestr = tmpfmt.format(tmpdate);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MobclickAgent.reportError(C.get(), "com.ymnet.onekeyclean.cleanmore.common.ApplicationUtils:" + ex.toString());
        }
        return tmpdatestr;
    }

    public static void hideIme(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
    }

    /**
     * 扩大点击范围
     *
     * @param view
     */
    private static final int ENLARGE = 200;

    public static void enlargeClickArea(final View view) {
        ViewParent parent = view.getParent();
        if (!View.class.isInstance(parent)) {
            return;
        }
        final View parentView = (View) parent;
        parentView.post(new Runnable() {
            @Override
            public void run() {
                Rect bounds = new Rect();

                view.getHitRect(bounds);
                bounds.left -= ENLARGE;
                // bounds.right += ENLARGE;
                bounds.bottom += ENLARGE;
                bounds.top -= ENLARGE;

                TouchDelegate touchDelegate = new TouchDelegate(bounds, view);

                parentView.setTouchDelegate(touchDelegate);
            }
        });
    }

    /**
     * 将网络图片临时保存到SD卡
     *
     * @param url
     * @return
     */
    public static String savePic2SD(String url) {
        URL string2url;
        try {
            string2url = new URL(url);
            URLConnection pichttpURLConnection = string2url.openConnection();
            pichttpURLConnection.setConnectTimeout(3000);
            InputStream ff = pichttpURLConnection.getInputStream();
            return saveFile(ff, getFileName(url));

        } catch (MalformedURLException e) {
            e.printStackTrace();
            MobclickAgent.reportError(C.get(), "com.ymnet.onekeyclean.cleanmore.common.ApplicationUtils:" + e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            MobclickAgent.reportError(C.get(), "com.ymnet.onekeyclean.cleanmore.common.ApplicationUtils:" + e.toString());
        } catch (Exception e) {
            e.printStackTrace();
            MobclickAgent.reportError(C.get(), "com.ymnet.onekeyclean.cleanmore.common.ApplicationUtils:" + e.toString());
        }
        return null;
    }

    /**
     * 输入流保存文件
     *
     * @param ff
     * @param fileName
     * @return
     * @throws IOException
     */
    private static String saveFile(InputStream ff, String fileName) throws IOException {
        File dirFile = new File(Constants.ImageCacheDir);
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        File saveimageFile = new File(Constants.ImageCacheDir + fileName);
        // 1K的数据缓冲
        byte[] bs = new byte[1024];
        // 读取到的数据长度
        int len;
        // 输出的文件流
        OutputStream os = new FileOutputStream(saveimageFile);
        // 开始读取
        while ((len = ff.read(bs)) != -1) {
            os.write(bs, 0, len);
        }
        os.close();
        ff.close();
        return saveimageFile.getPath();
    }


    /**
     * 全局单位统一
     *
     * @param fileSize
     * @return
     */
    public static String formatFileSizeToString(long fileSize) {// 转换文件大小
        String fileSizeString = "";

        if (fileSize <= 1 * 1024) {
            fileSizeString = "1" + "KB";
        } else if (fileSize <= (800 * 1024)) {
            fileSizeString = fileSize / 1024 + "KB";
        } else if (fileSize <= (800 * 1024 * 1024)) {
            fileSizeString = String.format("%.2f", ((float) fileSize / (1 * 1024 * 1024))) + "MB";
        } else {
            // 直接除以 800 * 1024 * 1024 * 1024 会出错，乘积太大
            double size = (float) fileSize / (1 * 1024 * 1024 * 1024);
            if (size <= 800) {
                fileSizeString = String.format("%.2f", size) + "GB";
            } else {
                fileSizeString = String.format("%.2f", size / 1024) + "TB";
            }
        }

        return fileSizeString;
    }

    public static String[] formatFileSizeToArr(long fileSize) {// 转换文件大小
        String srt[] = new String[2];
        String fileSizeString = "";
        String unit = "";

        if (fileSize <= 1 * 1024) {
            fileSizeString = "1";
            unit = "KB";
        } else if (fileSize <= (800 * 1024)) {
            fileSizeString = fileSize / 1024 + "";
            unit = "KB";
        } else if (fileSize <= (800 * 1024 * 1024)) {
            fileSizeString = String.format("%.2f", ((float) fileSize / (1 * 1024 * 1024))) + "";
            unit = "MB";
        } else {
            // 直接除以 800 * 1024 * 1024 * 1024 会出错，乘积太大
            double size = (float) fileSize / (1 * 1024 * 1024 * 1024);
            if (size <= 800) {
                fileSizeString = String.format("%.2f", size) + "";
                unit = "GB";
            } else {
                fileSizeString = String.format("%.2f", size / 1024) + "";
                unit = "TB";
            }
        }

        srt[0] = fileSizeString;
        srt[1] = unit;
        return srt;
    }


    public static CharSequence getFormatDownloads(String totalDowns) {
        if (TextUtils.isEmpty(totalDowns)) {
            return "0下载";
        } else {
            int length = totalDowns.length();
            if (length == 5) {
                float times = Float.parseFloat(totalDowns) / 10000F;
                return String.format("%.1f", times) + "万下载";
            } else if (4 < length && length < 9) {
                int times = Integer.parseInt(totalDowns) / 10000;
                return times + "万下载";
            } else if (length == 9) {
                return totalDowns.charAt(0) + "." + totalDowns.charAt(1) + "亿下载";
            } else if (length >= 10) {
                return totalDowns.substring(0, length - 8) + "亿下载";
            } else {
                return totalDowns + "下载";
            }
        }
    }


    public static String getProviderName() {

        TelephonyManager telephonyManager = (TelephonyManager) MarketApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
        String IMSI = telephonyManager.getSubscriberId();
        String providersName = "";
        if (!TextUtils.isEmpty(IMSI)) {
            if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
                providersName = "中国移动";
            } else if (IMSI.startsWith("46001")) {
                providersName = "中国联通";
            } else if (IMSI.startsWith("46003")) {
                providersName = "中国电信";
            }
        }
        return providersName;

    }

    /**
     * 评论及留言板上报数据
     *
     * @param context
     * @return
     */
    public static String getFeedBackDeviceInfo(Context context) {
        JsonObject feedback = new JsonObject();

        //运营商
        feedback.addProperty("providersName", getProviderName());
        //网络类型
        feedback.addProperty("networkType", ApplicationUtils.getNetworkType(context));
        //手机型号
        feedback.addProperty("model", Build.MODEL);
        //手机品牌
        feedback.addProperty("brand", Build.BRAND);
        //系统版本字符串
        feedback.addProperty("release", Build.VERSION.RELEASE);
        //手机助手版本
        feedback.addProperty("appVersionCode", MarketApplication.versionname);
        return feedback.toString();
    }

    /**
     * 通过进程ID获取ProcessName
     *
     * @param context
     * @param pid
     * @return
     */
    public static String getProcessNameByPID(Context context, int pid) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        for (RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == pid) {
                return processInfo.processName;
            }
        }
        return "";
    }


    /**
     * 拷贝到剪切板
     *
     * @param content
     */
    @SuppressLint("NewApi")
    public static void copy(CharSequence content) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // 得到剪贴板管理器
            ClipboardManager cmb = (ClipboardManager) C.get().getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setPrimaryClip(ClipData.newPlainText(null, content));
        } else {
            android.text.ClipboardManager cmb = (android.text.ClipboardManager) C.get().getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setText(content);
        }

    }

}
