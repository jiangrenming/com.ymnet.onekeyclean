package com.ymnet.onekeyclean.cleanmore.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.RemoteException;
import android.os.StatFs;
import android.text.TextUtils;

import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.datacenter.DataCenterObserver;
import com.ymnet.onekeyclean.cleanmore.junk.SilverActivity;
import com.ymnet.onekeyclean.cleanmore.junk.mode.InstalledAppAndRAM;
import com.ymnet.onekeyclean.cleanmore.junk.mode.JunkChild;
import com.ymnet.onekeyclean.cleanmore.junk.mode.JunkChildApk;
import com.ymnet.onekeyclean.cleanmore.junk.mode.JunkChildCache;
import com.ymnet.onekeyclean.cleanmore.junk.mode.JunkChildCacheOfChild;
import com.ymnet.onekeyclean.cleanmore.junk.mode.JunkChildResidual;
import com.ymnet.onekeyclean.cleanmore.junk.mode.JunkGroup;
import com.ymnet.onekeyclean.cleanmore.shortcut.DeleteHelp;
import com.ymnet.onekeyclean.cleanmore.utils.C;
import com.ymnet.onekeyclean.cleanmore.utils.CleanSetSharedPreferences;
import com.ymnet.onekeyclean.cleanmore.utils.FileTreeUtils;
import com.ymnet.onekeyclean.cleanmore.utils.Util;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class BackgroundDoSomethingService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.market2345.dumpclean.action.FOO";
    private static final String ACTION_BAZ = "com.market2345.dumpclean.action.BAZ";
    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.market2345.dumpclean.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.market2345.dumpclean.extra.PARAM2";

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, BackgroundDoSomethingService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, BackgroundDoSomethingService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    public BackgroundDoSomethingService() {
        super("DackgroundDoSomethingService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SilverActivity.CleanDataModeEvent cleanData = DataCenterObserver.get(this).getCleanData();

        if (cleanData != null) {
            this.data = cleanData.datas;
            this.selectSize = cleanData.selectSize;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (data != null) {
            data.clear();
            this.data = null;
        }
        DataCenterObserver.get(this).setCleanData(null);

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                handleActionFoo();
            } else if (ACTION_BAZ.equals(action)) {
                handleActionBaz();
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo() {
        // TODO: Handle action Foo
//        throw new UnsupportedOperationException("Not yet implemented");
        if (data != null) {
            if (checkDefaultSelect(data)) {
                CleanSetSharedPreferences.setLastSet(this, CleanSetSharedPreferences.CLEAN_RESULT_CACHE, false);
            } else {
                CleanSetSharedPreferences.setLastSet(this, CleanSetSharedPreferences.CLEAN_RESULT_CACHE, true);
            }

            CleanSetSharedPreferences.setCleanLastTimeSize(this, selectSize);
            com.ymnet.onekeyclean.cleanmore.cacheclean.Util.setClearDate(this);
            for (JunkGroup group : data) {
                if (getString(R.string.header_ram).equals(group.getName()) && group.getSelect() == 1) {
                    InstalledAppAndRAM.lastCleanTime = System.currentTimeMillis();
                }
                List<JunkChild> childs = group.getChildrenItems();
                for (JunkChild child : childs) {
                    if (child instanceof JunkChildCache) {
                        //删除应用缓存
                        JunkChildCache cache = (JunkChildCache) child;

                        deleteJunkChildCache(cache);
                    } else if (child instanceof JunkChildResidual && child.getSelect() == 1) {
                        //删除app残留
                        JunkChildResidual residual = (JunkChildResidual) child;
                        deleteJunkChildResidual(residual);

                    } else if (child instanceof JunkChildApk && child.getSelect() == 1) {
                        //删除apk文件
                        JunkChildApk apk = (JunkChildApk) child;
                        deleteJunkChildApk(apk);
                    } else if (child instanceof InstalledAppAndRAM) {
                        //删除应用内存
//                        killBackgroundProcess(childs);
                        DeleteHelp.killBackgroundProcess(this, childs);
                        break;
                    }
                }
                CleanSetSharedPreferences.setPreviousScanSize(C.get(), 0L);//后台扫描大小归零
            }
        }
    }

    List<JunkGroup> data;
    long selectSize;

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz() {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * 判断是否保存清理结果
     * true 默认值发生改变
     * false 默认值没有发生改变
     */
    private boolean checkDefaultSelect(List<JunkGroup> dataList) {
        for (JunkGroup group : dataList) {
            if (group != null && group.getChildrenItems() != null) {
                List<JunkChild> childs = group.getChildrenItems();
                for (JunkChild child : childs) {
                    if (child != null) {
                        if (!(child instanceof JunkChildCache)) {
                            if (!child.getDefaultSelect()) {
                                return true;//发生改变
                            }
                        } else {
                            List<JunkChildCacheOfChild> ccs = ((JunkChildCache) child).childCacheOfChild;
                            for (JunkChildCacheOfChild cc : ccs) {
                                if (!cc.getDefaultSelect()) {
                                    return true;
                                }
                            }
                        }
                    }

                }
            }

        }
//        Log.i("wdh","检查完所有项耗时："+(System.currentTimeMillis()-start));
        return false;
    }

    private void deleteJunkChildCache(JunkChildCache cache) {
        if (cache == null || cache.childCacheOfChild == null || cache.childCacheOfChild.size() == 0) {
            return;
        }
        if (JunkChildCache.systemCachePackName.equals(cache.packageName)) {
            if (cache.getSelect() != 0) {
                //删除系统缓存
                cleanSystemCache();
            }
        } else {
            List<JunkChildCacheOfChild> childOfChilds = cache.childCacheOfChild;
            for (JunkChildCacheOfChild childOfChild : childOfChilds) {
                String path = childOfChild.path;
                if (childOfChild.getSelect() != 0 && !TextUtils.isEmpty(path)) {
//                    Util.deleteNotContainFolder(new File(path));
                    FileTreeUtils.deleteContents(new File(path));
                }
            }
        }
    }

    private void cleanSystemCache() {
//        if(true)return;
        final PackageManager pm = getPackageManager();
        File directory = Environment.getDataDirectory();
        if(directory==null||!directory.exists()){
            return;
        }
        StatFs statFs = new StatFs(directory.getAbsolutePath());
        long size=Long.MAX_VALUE;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR2){
             size = statFs.getBlockCountLong() * statFs.getBlockSizeLong();
        }
        try {
            // TODO: 2017/4/28 0028 暂时注释so文件
            Method method = pm.getClass().getMethod("freeStorageAndNotify", long.class, IPackageDataObserver.class);
            method.invoke(pm, size, new IPackageDataObserver.Stub() {
                @Override
                public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
                }
            });
        } catch (Exception e) {

        }
    }

    private void deleteJunkChildResidual(JunkChildResidual residual) {
        if (residual == null || residual.paths == null || residual.paths.size() == 0) {
            return;
        }
        if (residual.getSelect() == 0) {
            return;
        }
        for (String path : residual.paths) {
//            Util.delete(new File(path));
            FileTreeUtils.deleteAll(new File(path));
        }
    }

    private void deleteJunkChildApk(JunkChildApk apk) {
        if (apk == null || apk.path == null) {
            return;
        }
        if (apk.getSelect() == 0) {
            return;
        }
        Util.delete(new File(apk.path));
    }


}
