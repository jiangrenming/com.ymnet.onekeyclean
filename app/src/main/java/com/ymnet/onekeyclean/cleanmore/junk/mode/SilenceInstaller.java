package com.ymnet.onekeyclean.cleanmore.junk.mode;/*
package com.example.baidumapsevice.junk.mode;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.example.baidumapsevice.root.exception.NoAuthorizationException;
import com.example.baidumapsevice.root.shell.Shell;
import com.example.baidumapsevice.utils.C;
import com.example.baidumapsevice.utils.PackageUtils;
import com.example.baidumapsevice.wechat.MTask;
import com.example.baidumapsevice.wechat.MarketApplication;
import com.example.baidumapsevice.wechat.R;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


*/
/**
 * Created with IntelliJ IDEA.
 * Date: 15/7/29
 *//*

public class SilenceInstaller {

    private static final String TAG = "SilenceInstaller";

    private static volatile SilenceInstaller sSilenceInstaller;


    */
/**
     * 是否拥有系统级别静默安装权限
     *//*

    private volatile boolean mHasSysPermission;


    private volatile boolean mInitialized;

    private BlockingQueue<DownloadInfo> mInstallQueue;

    private Shell mShell;

    private SilenceInstaller() {
        boolean hasSysPermission = false;
        try {
            PackageInfo packageInfo = C.get().getPackageManager().getPackageInfo(MarketApplication.packegename, PackageManager.GET_GIDS);
            ApplicationInfo info = packageInfo.applicationInfo;
            if ((info.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                mHasSysPermission = true;
                mShell = InstallShell.getInstance();
                hasSysPermission = true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            hasSysPermission = false;
        }
        SPUtil.setSystemPermission(C.get(), hasSysPermission);
    }

    */
/**
     * 开启消息处理
     *//*

    private void init() {

        mInstallQueue = new LinkedBlockingQueue<>();

        MTask.BACKGROUND_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                boolean loop = true;
                Context context = C.get();
                while (loop) {
                    DownloadInfo downloadInfo = null;
                    try {
                        downloadInfo = mInstallQueue.take();
                        String path = DownloadInfo.queryDownloadPath(context, downloadInfo.mId);
                        if (PMUtils.installPackage(path, getShell()) == PackageUtils.INSTALL_SUCCEEDED) {
                            SPUtil.setSystemPermission(context, true);
                            if (!hasSystemPermission()) {
                                notifyPushMessage(downloadInfo);
                            }
                        } else {
                            SPUtil.setSystemPermission(context, false);
                            backToNormal(context, downloadInfo);
                        }
                    } catch (InterruptedException e) {
                        loop = false;
                        sSilenceInstaller = null;
                    } catch (NoAuthorizationException e) {
                        loop = false;
                        sSilenceInstaller = null;
                        backToNormal(context, downloadInfo);
                        SettingUtils.setUserChange(context, false);
                        SettingUtils.changeDefaultValue(context, SettingUtils.SETTING.AUTO_INSTALL, false);
                    }
                }
            }
        });
    }


    */
/**
     * 显示下拉悬浮窗
     *
     * @param downloadInfo
     *//*

    private void notifyPushMessage(final DownloadInfo downloadInfo) {
        final Context context = C.get();
        if (downloadInfo.mStartInstall) {
            if (!isKeyguardLocked()) {
                if (!isFrontTask()) {
                    Intent intent = new Intent(context, PushNotificationMessageActivity.class);
                    intent.putExtra(KEY_PACKAGENAME, downloadInfo.mPackageName);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (downloadInfo.mTitle != null) {
                                Toast.makeText(context, context.getString(R.string.silent_install_success, downloadInfo.mTitle), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
            NotifyManager.notifyInstallSuccess(downloadInfo, context);
        } else {//免流量升级
            NotifyManager.notifyAutoInstall(downloadInfo, context);
        }

    }

    private boolean isKeyguardLocked() {
        KeyguardManager mKeyguardManager = (KeyguardManager) C.get().getSystemService(Context.KEYGUARD_SERVICE);
        return mKeyguardManager.inKeyguardRestrictedInputMode();
    }


    private boolean isFrontTask() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return MarketApplication.getInstance().isFrontTask();
        } else {
            ActivityManager manager = (ActivityManager) C.get().getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> runningTasks = manager.getRunningTasks(1);
            return MarketApplication.packegename.equals(runningTasks.get(0).topActivity.getPackageName());
        }
    }

    */
/**
     * 静默安装不成功转系统安装
     *
     * @param context
     * @param downloadInfo
     *//*

    private void backToNormal(Context context, DownloadInfo downloadInfo) {

        if (!hasSystemPermission()) {
            synchronized (NotifyManager.class) {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                int installing = sp.getInt(KEY_INSTALLING, 0);
                if (installing > 0) {
                    sp.edit().putInt(KEY_INSTALLING, --installing).commit();
                }
            }
        }

        ContentValues values = new ContentValues();
        values.put(Downloads.Impl.COLUMN_STATUS, Downloads.Impl.STATUS_DOWNLOAD_SUCCESS);
        context.getContentResolver().update(downloadInfo.getDownloadUri(), values, null, null);
        if (SettingUtils.checkLastSetValue(context, SettingUtils.SETTING.INSTALL_TIP, true)) {
            PackageUtils.installNormal(context, downloadInfo);
        }
    }

    public static SilenceInstaller getInstance() {
        if (sSilenceInstaller == null) {
            synchronized (SilenceInstaller.class) {
                if (sSilenceInstaller == null) {
                    sSilenceInstaller = new SilenceInstaller();
                }
            }
        }
        return sSilenceInstaller;
    }

    public void addInstall(DownloadInfo info) {

        if (!mInitialized) {
            init();
            mInitialized = true;
        }

        if (info != null) {
            try {
                Context context = C.get();
                DownloadLog.downloadEvent(DownloadLog.START_INSTALL, info.mId);

                mInstallQueue.put(info);
                if (!hasSystemPermission()) {
                    synchronized (NotifyManager.class) {
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                        int installing = sp.getInt(KEY_INSTALLING, 0);
                        sp.edit().putInt(KEY_INSTALLING, ++installing).commit();
                    }
                }
                ContentValues values = new ContentValues();
                values.put(Downloads.Impl.COLUMN_DESCRIPTION, "");
                values.put(Downloads.Impl.COLUMN_STATUS, Downloads.Impl.STATUS_INSTALLING);
                context.getContentResolver().update(info.getDownloadUri(), values, null, null);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    */
/**
     * 是否具有静默安装权限
     *
     * @return
     *//*

    public boolean hasSystemPermission() {
        return mHasSysPermission;
    }


    private Shell getShell() {
        return mShell;
    }
}
*/
