package com.ymnet.onekeyclean.cleanmore.download;/*
package com.example.baidumapsevice.download;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.market2345.MarketApplication;
import com.market2345.app.MTask;
import com.market2345.applist.special.BaseAppListFragmentSpecial;
import com.market2345.base.C;
import com.market2345.base.log.L;
import com.market2345.common.util.ApplicationUtils;
import com.market2345.common.util.DownloadReportProvider;
import com.market2345.common.util.SwapUtil;
import com.market2345.common.util.Utils;
import com.market2345.download.DownloadInfo;
import com.market2345.download.DownloadManager;
import com.market2345.download.DownloadProvider;
import com.market2345.download.DownloadThread;
import com.market2345.download.Downloads;
import com.market2345.download.StorageManager;
import com.market2345.framework.http.Call;
import com.market2345.framework.http.Callback;
import com.market2345.framework.http.MHttp;
import com.market2345.framework.http.bean.Response;
import com.market2345.navigation.model.ifly.MaterialObject;
import com.market2345.navigation.util.IFLYHttpUtil;
import com.market2345.temp.TApier;
import com.market2345.util.SPUtil;
import com.statistic2345.util.TJDeviceInfoUtil;

import java.util.HashMap;
import java.util.Map;

*/
/**
 * @author zhangcm
 * @version 2014-10-24 上午10:02:35
 * @类说明 上报下载信息
 *//*

public class DownloadLog {

    private static final String TAG = DownloadLog.class.getSimpleName();

    */
/**
     * 下载状态：下载
     *//*

    public static final int START_DOWNLOAD = 1;
    */
/**
     * 下载状态：下载成功
     *//*

    public static final int DOWNLOAD_SUCCESS = 2;
    */
/**
     * 下载状态：安装
     *//*

    public static final int START_INSTALL = 3;
    */
/**
     * 下载状态：安装成功
     *//*

    public static final int INSTALL_SUCCESS = 4;

    */
/**
     * 历史包 统计
     *//*

    private static final int IS_UPDATE = 2;


    */
/**
     * 发送下载相关的统计
     * <p/>
     * 1下载 2下载成功 3安装 4安装成功 5卸载
     *//*

    public static void downloadEvent(final int type, final long downloadId) {

        MTask.BACKGROUND_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = null;
                try {
                    if (C.get().getContentResolver() != null) {
                        cursor = C.get().getContentResolver().query(DownloadManager.getInstance(C.get()).getDownloadUriWithID(downloadId), null, null, null, null);

                        if (cursor != null && cursor.getCount() > 0) {
                            DownloadInfo.Reader reader = new DownloadInfo.Reader(C.get().getContentResolver(), cursor, DownloadManager.getInstance(C.get()));
                            while (cursor.moveToNext()) {
                                downloadEvent(type, reader.newDownloadInfo(C.get(), new StorageManager()));
                            }
                        }
                    }
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        });
    }


    public static void downloadEvent(final int type, final String filePath) {


        MTask.BACKGROUND_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {

                Context context = MarketApplication.getInstance();

                Cursor cursor = context.getContentResolver().query(DownloadManager.getInstance(context).getDownloadUri(), null, Downloads.Impl.COLUMN_URI + " = ? ", new String[]{filePath}, null);
                try {
                    if (cursor != null && cursor.getCount() > 0) {
                        DownloadInfo.Reader reader = new DownloadInfo.Reader(context.getContentResolver(), cursor, DownloadManager.getInstance(context));
                        while (cursor.moveToNext()) {
                            downloadEvent(type, reader.newDownloadInfo(context, new StorageManager()));
                        }
                    }
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        });


    }


    private static void downloadEvent(final int type, final DownloadInfo downloadInfo) {

        L.d(TAG, "downloadEvent--" + type + "|" + downloadInfo.mPackageName);
        if (downloadInfo == null) {
            return;
        }

        if (downloadInfo.mSourceFrom == DownloadReportProvider.SOURCEFROM_XFEI_SPLASH) {
            Gson gson = new Gson();

            try {
                MaterialObject object = gson.fromJson(downloadInfo.mPatchUrl, MaterialObject.class);
                if (object.package_name.equals(downloadInfo.mPackageName)) {
                    L.d(TAG, "downloadEvent--" + type + "|" + "xunfei");
                    switch (type) {
                        case START_DOWNLOAD:
                            IFLYHttpUtil.reportOperationThread(C.get(), object.inst_downstart_url);
                            break;
                        case DOWNLOAD_SUCCESS:
                            IFLYHttpUtil.reportOperationThread(C.get(), object.inst_downsucc_url);
                            break;
                        case START_INSTALL:
                            IFLYHttpUtil.reportOperationThread(C.get(), object.inst_installstart_url);
                            break;
                        case INSTALL_SUCCESS:
                            IFLYHttpUtil.reportOperationThread(C.get(), object.inst_installsucc_url);
                            break;
                    }
                }

            } catch (JsonSyntaxException e) {
                // ignore
            }
        }

        try {
            if (TextUtils.isEmpty(downloadInfo.mSid) || Integer.parseInt(downloadInfo.mSid) <= 0) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }


        ContentValues values = new ContentValues();
        String mac = TJDeviceInfoUtil.getMac(MarketApplication.getInstance());
        String imei = TJDeviceInfoUtil.getIMEI(MarketApplication.getInstance());
        String imsi = TJDeviceInfoUtil.getImsi(MarketApplication.getInstance());
        String androidId = TJDeviceInfoUtil.getAndroidID(MarketApplication.getInstance());
        String channel = Utils.getChannel();

        values.put(DownloadReportProvider.COLUMN_SOFT_ID, downloadInfo.mSid);
        values.put(DownloadReportProvider.COLUMN_MAC, mac);
        values.put(DownloadReportProvider.COLUMN_IMEI, imei);
        values.put(DownloadReportProvider.COLUMN_IMSI, imsi);
        values.put(DownloadReportProvider.COLUMN_ANDROID_ID, androidId);
        values.put(DownloadReportProvider.COLUMN_DOWNLOAD_TYPE, type);
        if (!TextUtils.isEmpty(downloadInfo.mUrl) && downloadInfo.mUrl.endsWith(DownloadThread.HISTORY_SUFFIX)) {
            values.put(DownloadReportProvider.COLUMN_IS_UPDATE, IS_UPDATE);
        } else {
            values.put(DownloadReportProvider.COLUMN_IS_UPDATE, downloadInfo.mIsUpdate);
        }
        values.put(DownloadReportProvider.COLUMN_PLATFORM, downloadInfo.mPlatform);
        values.put(DownloadReportProvider.COLUMN_VERSION, MarketApplication.versionname);
        values.put(DownloadReportProvider.COLUMN_VERCODE, MarketApplication.versioncode);
        values.put(DownloadReportProvider.COLUMN_CHANNEL, channel);
        values.put(DownloadReportProvider.COLUMN_SOFTNAME, downloadInfo.mTitle);
        byte[] src = SwapUtil.intToBytes(downloadInfo.mSourceFrom);
        if (BaseAppListFragmentSpecial.SOURCE_RECOMMEND == src[2] && 1 == src[3]) {
            values.put(DownloadReportProvider.COLUMN_SOURCEFROM, (int) src[0]);
        } else if (BaseAppListFragmentSpecial.SOURCE_RECOMMEND == src[2] && 0 == src[3]) {
            values.put(DownloadReportProvider.COLUMN_SOURCEFROM, (int) src[0]);
        } else {
            values.put(DownloadReportProvider.COLUMN_SOURCEFROM, downloadInfo.mSourceFrom);
        }
        values.put(DownloadReportProvider.COLUMN_DOWNLOAD_ID, "i" + downloadInfo.mId + "v" + DownloadProvider.DB_VERSION);
        values.put(DownloadReportProvider.COLUMN_REPORTED, DownloadReportProvider.UNREPORTED);

        Uri uri = MarketApplication.getInstance().getContentResolver().insert(DownloadReportProvider.DOWNLOAD_REPORT_URL, values);
        final long id = (uri != null ? ContentUris.parseId(uri) : -1);
        if (id > 0) {
            Map<String, String> paraMap = new HashMap<String, String>();
            paraMap.put("softId", downloadInfo.mSid);
            paraMap.put("mac", mac);
            paraMap.put("imei", imei);
            paraMap.put("deviceId", androidId);
            paraMap.put("imsi", imsi);
            paraMap.put("type", type + "");
            paraMap.put("platform", downloadInfo.mPlatform);
            paraMap.put("softName", downloadInfo.mTitle);
            if (BaseAppListFragmentSpecial.SOURCE_RECOMMEND == src[2] && 1 == src[3]) {
                paraMap.put("position", BaseAppListFragmentSpecial.SOURCE_RECOMMEND + "_detail");
                paraMap.put("sourceFrom", src[0] + "");
            } else if (BaseAppListFragmentSpecial.SOURCE_RECOMMEND == src[2] && 0 == src[3]) {
                paraMap.put("position", BaseAppListFragmentSpecial.SOURCE_RECOMMEND + "");
                paraMap.put("sourceFrom", src[0] + "");
            } else {
                paraMap.put("position", "");
                paraMap.put("sourceFrom", downloadInfo.mSourceFrom + "");
            }
            paraMap.put("version", MarketApplication.versionname);
            paraMap.put("vercode", MarketApplication.versioncode + "");
            paraMap.put("android_vercode", Build.VERSION.SDK_INT + "");
            paraMap.put("channel", channel);
            if (!TextUtils.isEmpty(downloadInfo.mUrl) && downloadInfo.mUrl.endsWith(DownloadThread.HISTORY_SUFFIX)) {
                paraMap.put("is_update", IS_UPDATE + "");
            } else {
                paraMap.put("is_update", downloadInfo.mIsUpdate + "");
            }

            paraMap.put("bdi_loc", SPUtil.getLocationInfo(C.get()));
            paraMap.put("bdi_bear", ApplicationUtils.getNetworkType(C.get()));
            paraMap.put("phone_resolution", TJDeviceInfoUtil.getDeviceResolution(C.get()));
            paraMap.put("phone_brand", Build.BRAND);
            paraMap.put("phone_model", Build.MODEL);
            paraMap.put("os_version", Build.VERSION.RELEASE);
            paraMap.put("os_apilevel", Build.VERSION.SDK_INT + "");

            StringBuilder builder = new StringBuilder();
            for (String value : paraMap.values()) {
                builder.append(value);
            }
            String sign = builder.toString();
            String code = Utils.strCode(Utils.getMD5(sign));
            code = code.substring(0, code.length() - 1);
            paraMap.put("sign", code);
            TApier.get().downloadEvent(paraMap).enqueue(new Callback<Response>() {
                @Override
                public void onResponse(Call<Response> call, Response response) {
                    if (response != null && MHttp.responseOK(response.getCode())) {
                        ContentValues updateValues = new ContentValues();
                        updateValues.put(DownloadReportProvider.COLUMN_REPORTED, DownloadReportProvider.REPORTED);
                        String where = DownloadReportProvider.COLUMN_ID + " = ?";
                        String[] selectionArgs = {id + ""};
                        int rows = MarketApplication.getInstance().getContentResolver().update(DownloadReportProvider.DOWNLOAD_REPORT_URL, updateValues, where, selectionArgs);
                        L.i(TAG, "DownloadLog downloadEvent:" + response.getCode() + "*" + type + "*" + downloadInfo.mId + "*" + rows + "platform:" + downloadInfo.mPlatform);
                    }
                }

                @Override
                public void onFailure(Call<Response> call, Throwable t) {
                }
            });
        }

    }


    */
/**
     * 上报未成功上报的信息
     *
     * @param report
     *//*

    public static void downloadEvent(final DownloadReport report) {

        try {
            if (TextUtils.isEmpty(report.softId) || Integer.parseInt(report.softId) <= 0) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Map<String, String> paraMap = new HashMap<String, String>();

        paraMap.put("softId", report.softId);
        paraMap.put("mac", report.mac);
        paraMap.put("imei", report.imei);
        paraMap.put("deviceId", report.androidId);
        paraMap.put("imsi", report.imsi);
        paraMap.put("type", report.downloadType + "");
        paraMap.put("platform", report.platform);
        paraMap.put("softName", report.softName);
        paraMap.put("sourceFrom", report.sourceFrom + "");
        paraMap.put("version", report.version);
        paraMap.put("vercode", report.vercode + "");
        paraMap.put("android_vercode", Build.VERSION.SDK_INT + "");
        paraMap.put("channel", report.channel);
        paraMap.put("is_update", report.isUpdate + "");

        paraMap.put("bdi_loc", SPUtil.getLocationInfo(C.get()));
        paraMap.put("bdi_bear", ApplicationUtils.getNetworkType(C.get()));
        paraMap.put("phone_resolution", TJDeviceInfoUtil.getDeviceResolution(C.get()));
        paraMap.put("phone_brand", Build.BRAND);
        paraMap.put("phone_model", Build.MODEL);
        paraMap.put("os_version", Build.VERSION.RELEASE);
        paraMap.put("os_apilevel", Build.VERSION.SDK_INT + "");

        StringBuilder builder = new StringBuilder();
        for (String value : paraMap.values()) {
            builder.append(value);
        }
        String sign = builder.toString();
        String code = Utils.strCode(Utils.getMD5(sign));
        code = code.substring(0, code.length() - 1);
        paraMap.put("sign", code);

        TApier.get().downloadEvent(paraMap).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, Response response) {
                if (response != null && MHttp.responseOK(response.getCode())) {
                    ContentValues updateValues = new ContentValues();
                    updateValues.put(DownloadReportProvider.COLUMN_REPORTED, DownloadReportProvider.REPORTED);
                    String where = DownloadReportProvider.COLUMN_ID + " = ?";
                    String[] selectionArgs = {report.id + ""};
                    MarketApplication.getInstance().getContentResolver().update(DownloadReportProvider.DOWNLOAD_REPORT_URL, updateValues, where, selectionArgs);
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {

            }
        });
    }


}
*/
