package com.ymnet.onekeyclean.cleanmore.utils;/*
package com.example.baidumapsevice.utils;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.example.baidumapsevice.junk.mode.InstallTipsViewHelper;
import com.example.baidumapsevice.junk.mode.SilenceInstaller;
import com.example.baidumapsevice.wechat.MarketApplication;
import com.example.baidumapsevice.wechat.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

*/
/**
 * Created by huyang on 2016/2/25.
 *//*

public class AccessibilityUtil {

    //关闭点击安装时的引导
    public static final String CLOSE_ACCESSIBILITY_GUIDE = "close_accessibility_guide";
    //关闭更新列表页面的提示
    public static final String CLOSE_ACCESSIBILITY_TIP = "close_accessibility_tip";
    //第一次关闭升级页面引导的时间
    public static final String FIRST_CLOSE_UPDATEPAGE_ACCESSIBILITY_TIP = "first_close_updatepage_accessibility_tip";
    //升级页面引导是否手动关闭过两次
    public static final String SECOND_CLOSE_UPDATEPAGE_ACCESSIBILITY_TIP = "second_close_updatepage_accessibility_tip";
    //第一次关闭下载页面引导的时间
    public static final String FIRST_CLOSE_DOWNLOADPAGE_ACCESSIBILITY_TIP = "first_close_downloadpage_accessibility_tip";
    //下载页面引导是否手动关闭过两次
    public static final String SECOND_CLOSE_DOWNLOADPAGE_ACCESSIBILITY_TIP = "second_close_downloadpage_accessibility_tip";
    public static final String DOWNLOADPAGE_KEY = "downloadpage_key";
    public static final String UPDATEPAGE_KEY = "updatepage_key";
    //是否本次启动第一次点击安装
    public static final String FIRST_SHOW_ACCESSIBILITY_GUIDE = "first_show_accessibility_guide";
    //之前是否开启过辅助安装，展示的引导不一样
    public static final String HAS_OPENED_ACCESSIBILITY = "has_opened_accessibility";
    public static final String EXTRA_FILE_PATH = "filepath";
    public static final String ACCESSIBILITY_SERVICE_NAME = MarketApplication.packegename + "/com.market2345.accessibility.AccessibilityDistributor";
    public static final String ACTION_ACCESSIBILITY_SETTINGS = "android.settings.ACCESSIBILITY_SETTINGS";
    public static final String ACTION_SETTING = "android.settings.SETTINGS";
    public static final String ZH_CN = "zh-CN";
    public static final String TAG = "Accessibility";

    private static final String PREFERENCE_NAME = "accessibility_service";
    private static final String KEY_NEW_SCHEME = "new_scheme";
    private static final String KEY_PHONE_MANAGER_VERSION = "version_code";
    private static final String KEY_SAVE_CLICK = "save_click";
    private static final String KEY_INSTALL_STATUS = "install_status";
    private static final int THRESHOLD_VALUE = 1000;
    private static final HashMap<String, Boolean> mAppNameMap = new HashMap<>();
    private static Handler mHandler = new Handler(Looper.getMainLooper());
    private static final int TIME_CLEAR_NAME = 60000;
    private static boolean isRequestSuccess = false;
    private static List<String> mInstallerNames = Arrays.asList("com.android.packageinstaller",
            "com.google.android.packageinstaller", "com.lenovo.safecenter", "com.lenovo.security");

    // Install or Uninstall class name
    private static final String UNINSTALL_ACTIVITY = "com.android.packageinstaller.UninstallerActivity";
    private static final String UNINSTALL_PROGRESS = "com.android.packageinstaller.UninstallAppProgress";
    private static final String INSTALL_ACTIVITY = "com.android.packageinstaller.PackageInstallerActivity";
    private static final String INSTALL_PROGRESS = "com.android.packageinstaller.InstallAppProgress";
    private static final String LENOVO_INSTALL_ACTIVITY = "com.lenovo.safecenter.install.InstallerActivity";
    private static final String LENOVO_INTERCEPT_ACTIVITY_1 = "com.lenovo.safecenter.defense.install.fragment.InstallInterceptActivity";
    private static final String LENOVO_INTERCEPT_ACTIVITY_2 = "com.lenovo.safecenter.defense.fragment.install.InstallInterceptActivity";
    private static final String LENOVO_INSTALL_PROGRESS_1 = "com.lenovo.safecenter.install.InstallProgress";
    private static final String LENOVO_INSTALL_PROGRESS_2 = "com.lenovo.safecenter.install.InstallAppProgress";

    // Lenovo installer package name
    private static final String LENOVO_INSTALLer_1 = "com.lenovo.safecenter";
    private static final String LENOVO_INSTALLer_2 = "com.lenovo.security";

    private static final long SEVEN_DAY_TIMES = 7 * 24 * 60 * 60 * 1000;

    private static HashSet<Integer> mWindowIdSet = new HashSet<>();

    */
/**
     * Get the latest auto installation scheme in JSON format.
     *
     * @return null or the updated scheme
     *//*

    public static String getLatestInstallScheme() {
        SharedPreferences preferences = C.get().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return preferences.getString(KEY_NEW_SCHEME, null);
    }

    */
/**
     * Save the latest auto installation scheme
     *
     * @param newScheme the latest scheme
     * @return true if save successfully; false otherwise.
     *//*

    public static boolean setLatestInstallScheme(String newScheme) {
        SharedPreferences preferences = C.get().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return preferences.edit().putString(KEY_NEW_SCHEME, newScheme).commit();
    }

    public static boolean savePhoneManagerVersion(int code) {
        SharedPreferences preferences = C.get().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return preferences.edit().putInt(KEY_PHONE_MANAGER_VERSION, code).commit();
    }

    public static int getPhoneManagerVersion() {
        SharedPreferences preferences = C.get().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return preferences.getInt(KEY_PHONE_MANAGER_VERSION, MarketApplication.versioncode);
    }

    public static boolean saveClickNumber() {
        int num = getClickNumber();
        SharedPreferences preferences = C.get().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        if (num == THRESHOLD_VALUE) {
            num = 0;
        } else {
            ++num;
        }

        return preferences.edit().putInt(KEY_SAVE_CLICK, num).commit();
    }

    public static int getClickNumber() {
        SharedPreferences preferences = C.get().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return preferences.getInt(KEY_SAVE_CLICK, 0);
    }

    public static boolean saveInstallStatus(boolean status) {
        SharedPreferences preferences = C.get().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return preferences.edit().putBoolean(KEY_INSTALL_STATUS, status).commit();
    }

    public static boolean getInstallStatus() {
        SharedPreferences preferences = C.get().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(KEY_INSTALL_STATUS, true);
    }

    */
/**
     * Request for the latest installation scheme
     * when local scheme is not suitable for this phone.
     *//*

    */
/*public static void requestLatestScheme() {
        if (!isRequestSuccess && NetworkUtils.isNetworkAvailable(C.get()) && !AccessibilityUtil.getInstallStatus()) {
            final String osVersion = Utils.getOSVersion();
            TApier.get().getAutoInstallSolution(Utils.getPhoneModel(), osVersion).enqueue(new AbsCallback<Response<AutoInstallSolutionEntity>>() {
                @Override
                public void onResponse(Call<Response<AutoInstallSolutionEntity>> call, Response<AutoInstallSolutionEntity> response) {
                    if (MHttp.responseOK(response)) {
                        InstallSchemeResponseData responseData = new InstallSchemeResponseDataMapper().transform(response.getData());

                        if (responseData.list != null && osVersion.equals(responseData.list.osVersion)) {
                            setLatestInstallScheme(responseData.data);
                            isRequestSuccess = true;
                            saveInstallStatus(true);
                            return;
                        }
                    }
                    isRequestSuccess = false;
                }
            });

        }

    }

    public static InstallSchemeData getLatestSchemeData() {
        String newScheme = getLatestInstallScheme();
        if (newScheme == null) {
            return null;
        }

        Gson gson = new Gson();
        InstallSchemeResponseData responseData = gson.fromJson(newScheme, InstallSchemeResponseData.class);
        return responseData.list;
    }*//*


    public static void reportSuccessEvent(String appName) {
        synchronized (mAppNameMap) {
            boolean isPhoneManagerApp = mAppNameMap.containsKey(appName);
            if (appName == null || (isPhoneManagerApp && mAppNameMap.get(appName))) {
                return;
            }

            if (AccessibilityUtil.getInstallStatus()) {
                if (isPhoneManagerApp) {
                    mAppNameMap.put(appName, true);
                }

                // Send statistics
//                Statistics.onEvent(C.get(), StatisticEventContants.AutoInstall_Finish);
            }
        }
    }

    public static void reportSuccessEvent() {
        if (AccessibilityUtil.getInstallStatus()) {
            // Send statistics
//            Statistics.onEvent(C.get(), StatisticEventContants.AutoInstall_Finish);
        }
    }

    */
/**
     * Get a set of all last possible installation steps.
     *
     * @param resId Must be a string array resource id.
     * @return A set of all possible last installation steps.
     *//*

   */
/* public static HashSet<String> getLastStepSet(int resId) {
        HashSet<String> lastStepSet = getLocalInstallStepSet(resId);
        InstallSchemeData latestSchemeData = AccessibilityUtil.getLatestSchemeData();

        if (latestSchemeData != null && latestSchemeData.solution != null
                && latestSchemeData.solution.zh_last != null && !latestSchemeData.solution.zh_last.isEmpty()) {
            lastStepSet.addAll(latestSchemeData.solution.zh_last);
            lastStepSet.remove(null);
        }

        return lastStepSet;
    }
*//*

    */
/**
     * Get a set of all possible installation steps.
     *
     * @param resId Must be a string array resource id.
     * @return A set of all possible installation steps.
   */
/*  *//*
*/
/*
    public static HashSet<String> getInstallStepSet(int resId) {
        HashSet<String> installStepSet = getLocalInstallStepSet(resId);
        InstallSchemeData latestSchemeData = AccessibilityUtil.getLatestSchemeData();

        if (latestSchemeData != null && latestSchemeData.solution != null
                && latestSchemeData.solution.zh_all != null && !latestSchemeData.solution.zh_all.isEmpty()) {
            installStepSet.addAll(latestSchemeData.solution.zh_all);
            installStepSet.remove(null);
        }

        return installStepSet;
    }*//*


    */
/**
     * Get local default installation scheme.
     *
     * @param resId Must be a string array resource id.
     * @return A set of default installation steps.
     *//*

    public static HashSet<String> getLocalInstallStepSet(int resId) {
        String[] localInstallSteps = C.get().getResources().getStringArray(resId);
        return new HashSet<>(Arrays.asList(localInstallSteps));
    }


    */
/**
     * Try to find a clickable parent {@link AccessibilityNodeInfo}
     * when <code>child</code> is not clickable.
     *
     * @param child which is not clickable
     * @return <code>child</code> or the first parent which is clickable.
     *//*

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static AccessibilityNodeInfo findClickableNode(AccessibilityNodeInfo child) {
        if (child == null || child.isClickable()) {
            return child;
        }
        AccessibilityNodeInfo exChild;
        AccessibilityNodeInfo currentChild = child.getParent();

        while (currentChild != null) {
            if (currentChild.isClickable()) {
                return currentChild;
            }
            exChild = currentChild;
            currentChild = currentChild.getParent();
            exChild.recycle();
        }

        return child;
    }

    */
/**
     * To judge if there are any clickable views in this window.
     *
     * @param root {@link AccessibilityService#getRootInActiveWindow()}
     * @return true if we find a clickable view; false otherwise.
     *//*

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean containsClickableView(AccessibilityNodeInfo root) {
        if (root == null) {
            return false;
        }

        boolean containsClickableView = false;
        for (int i = 0; i < root.getChildCount(); i++) {
            AccessibilityNodeInfo node = root.getChild(i);
            if (node == null) {
                continue;
            }

            if (node.isClickable()) {
                containsClickableView = true;
            } else if (node.getChildCount() > 0) {
                containsClickableView = containsClickableView(node);
            }
            node.recycle();

            if (containsClickableView) {
                break;
            }
        }

        return containsClickableView;
    }

    */
/**
     * To judge if there are any clickable views that is enabled in this window.
     *
     * @param root {@link AccessibilityService#getRootInActiveWindow()}
     * @return true if we find a clickable & enabled view; false otherwise.
     *//*

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean containsEnableClickableView(AccessibilityNodeInfo root) {
        if (root == null) {
            return false;
        }

        boolean contains = false;
        for (int i = 0; i < root.getChildCount(); i++) {
            AccessibilityNodeInfo node = root.getChild(i);
            if (node == null) {
                continue;
            }

            if (node.isClickable() && node.isEnabled()) {
                contains = true;
            } else if (node.getChildCount() > 0) {
                contains = containsEnableClickableView(node);
            }
            node.recycle();

            if (contains) {
                break;
            }
        }

        return contains;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean isCurrentStepSuccess(AccessibilityNodeInfo root, HashSet<String> steps) {
        if (root == null || steps == null) {
            return false;
        }

        // To judge whether local scheme is suitable
        // Situation 1:We can't find any text contained in steps in this window.
        boolean isNotMatching = true;
        List<AccessibilityNodeInfo> matchedInfos = new ArrayList<>();
        for (String text : steps) {
            List<AccessibilityNodeInfo> nodeInfos = findAccessibilityNodeInfosByTextPlus(root, text);
            if (!nodeInfos.isEmpty()) {
                isNotMatching = false;
                matchedInfos.addAll(nodeInfos);
            }
        }

        // Situation 2:We find all of these matched AccessibilityNodeInfos are unclickable.
        if (!isNotMatching) {
            boolean isNotClickable = true;
            for (Iterator<AccessibilityNodeInfo> iterator = matchedInfos.iterator(); iterator.hasNext(); ) {
                AccessibilityNodeInfo info = iterator.next();
                if (info != null) {
                    isNotClickable = isNotClickable && !findClickableNode(info).isClickable();
                    info.recycle();
                }
            }

            if (isNotClickable) {
                isNotMatching = true;
            }
        }

        // If local scheme does not match and there are enabled & clickable
        // views in this window, then current step fails.
        return !(isNotMatching && containsEnableClickableView(root));
    }

    public static boolean isNotBelowApi(int version) {
        return Build.VERSION.SDK_INT >= version;
    }

    */
/**
     * Try to find a view whose text requested by {@link AccessibilityNodeInfo#getText()}
     * equals to <code>text</code> exactly.
     *
     * @param root {@link AccessibilityService#getRootInActiveWindow()}
     * @param text The special text that we want to check in current window.
     * @return A list composed of satisfied {@link AccessibilityNodeInfo} or empty list.
     *//*

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static List<AccessibilityNodeInfo> findAccessibilityNodeInfosByTextPlus(AccessibilityNodeInfo root, String text) {
        if (root == null || text == null) {
            return Collections.emptyList();
        }

        List<AccessibilityNodeInfo> infos = root.findAccessibilityNodeInfosByText(text);
        List<AccessibilityNodeInfo> targetInfos = new ArrayList<>();

        for (Iterator<AccessibilityNodeInfo> iterator = infos.iterator(); iterator.hasNext(); ) {
            AccessibilityNodeInfo info = iterator.next();
            CharSequence realText = info.getText();
            if (!TextUtils.isEmpty(realText) && text.equals(realText.toString())) {
                targetInfos.add(info);
            } else {
                info.recycle();
            }
        }

        return targetInfos;
    }


    public static void addView(Context context, View view, int gravity, int width, int height) {
        if (null == context || null == view) {
            return;
        }

        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        params.format = PixelFormat.TRANSLUCENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.gravity = gravity;
        params.width = width;
        params.height = height;
        manager.addView(view, params);
    }

    public static void removeView(Context context, View view) {
        if (null == context || null == view) {
            return;
        }

        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        manager.removeView(view);
    }

    */
/**
     * If this is the last step of application installation,
     * but we can't find a {@link AccessibilityNodeInfo}
     * whose text received by {@link AccessibilityNodeInfo#getText()} is "Done",
     * then we perform {@link AccessibilityService#GLOBAL_ACTION_BACK} action.
     *
     * @param service {@link AccessibilityService}
     * @param root    {@link AccessibilityService#getRootInActiveWindow()}
     *//*

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean performBackAction(AccessibilityService service, AccessibilityNodeInfo root) {
        if (root == null || service == null) {
            return false;
        }
        Resources resources = C.get().getResources();
        String done = resources.getString(R.string.install_step_done);
        String open = resources.getString(R.string.install_step_start);
        if (containsSpecialNodeInfo(root, open) && !containsSpecialNodeInfo(root, done)) {
            InstallTipsViewHelper.updateClickTime();
            InstallTipsViewHelper.removeTipsView();
            return service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
        }

        return false;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean containsSpecialNodeInfo(AccessibilityNodeInfo root, String text) {
        List<AccessibilityNodeInfo> infos = AccessibilityUtil.findAccessibilityNodeInfosByTextPlus(root, text);
        boolean contains = false;

        for (Iterator<AccessibilityNodeInfo> iterator = infos.iterator(); iterator.hasNext(); ) {
            AccessibilityNodeInfo info = iterator.next();
            AccessibilityNodeInfo tempInfo = AccessibilityUtil.findClickableNode(info);
            if (tempInfo.isClickable() && tempInfo.isEnabled()) {
                contains = true;
                if (!tempInfo.equals(info)) {
                    tempInfo.recycle();
                }
                break;
            }
        }
        recycleAccessibilityNodeInfoList(infos);
        return contains;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void recycleAccessibilityNodeInfoList(List<AccessibilityNodeInfo> infos) {
        if (infos == null || infos.isEmpty()) {
            return;
        }

        for (Iterator<AccessibilityNodeInfo> iterator = infos.iterator(); iterator.hasNext(); ) {
            AccessibilityNodeInfo info = iterator.next();
            if (info != null) {
                info.recycle();
            }
        }
    }

    public static boolean isRootingOrRooted() {
        return RootManager.getInstance().isRooting() || SettingUtils.checkLastSetValue(C.get(), SettingUtils.SETTING.AUTO_INSTALL, false);
    }

    public static void addAppName(String appName) {
        if (appName == null) {
            return;
        }
        synchronized (mAppNameMap) {
            mAppNameMap.put(appName, false);
        }
    }

    public static void removeAppName(final String appName) {
        synchronized (mAppNameMap) {
            if (appName == null || !mAppNameMap.containsKey(appName)) {
                return;
            }
            mHandler.removeCallbacksAndMessages(null);
            boolean isReported = mAppNameMap.get(appName);
            if (!isReported) {
                reportSuccessEvent(appName);
            }
            mAppNameMap.remove(appName);
            if (mAppNameMap.isEmpty()) {
                InstallTipsViewHelper.removeTipsView();
            }
        }
    }

    public static void clearAccessibilityCollection() {
        mAppNameMap.clear();
        mWindowIdSet.clear();
    }


    */
/**
     * To judge if we are installing an application comes from 2345PhoneManager.
     *
     * @param root {@link AccessibilityService#getRootInActiveWindow()}
     * @return true if this installation behaviour is triggered by 2345PhoneManager; false otherwise.
     *//*

    public static boolean isPhoneManagerApp(AccessibilityNodeInfo root) {
        if (root == null) {
            return false;
        }

        synchronized (mAppNameMap) {
            Set<String> appNameSet = mAppNameMap.keySet();
            for (String text : appNameSet) {
                List<AccessibilityNodeInfo> target = findAccessibilityNodeInfosByTextPlus(root, text);
                if (!target.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String findAppNameInWindow(AccessibilityNodeInfo root) {
        if (root == null) {
            return null;
        }

        synchronized (mAppNameMap) {
            Set<String> appNameSet = mAppNameMap.keySet();
            for (String text : appNameSet) {
                List<AccessibilityNodeInfo> target = findAccessibilityNodeInfosByTextPlus(root, text);
                if (!target.isEmpty()) {
                    return text;
                }
            }
        }
        return null;
    }

    public static void removeAppNameDelayed(final String name) {
        if (name == null) {
            return;
        }

        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                removeAppName(name);
            }
        }, TIME_CLEAR_NAME);
    }

    */
/**
     * To judge if current window belongs to any installer in {@link #mInstallerNames}.
     *
     * @param root {@link AccessibilityService#getRootInActiveWindow()}.
     * @return true if it is a installer window; false otherwise.
     *//*

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean isInstallerComponent(AccessibilityNodeInfo root) {
        if (null == root || !mInstallerNames.contains(root.getPackageName().toString())) {
            InstallTipsViewHelper.removeTipsView();
            return false;
        }

        return true;
    }

    */
/**
     * 是否正在获取或已经获取系统静默安装权限
     *
     * @return
     *//*

    private static boolean isPermissionOrHasSystemPermission() {
        return SilenceInstaller.getInstance().hasSystemPermission();
    }

    */
/**
     * 是否需要展示辅助安装引导弹窗，用于安装应用时使用
     *
     * @return
     *//*

    public static boolean needShowAccessibilityGuide() {
        return canUseAccessibility() && !isAutoInstallEnabled() && isCNLanguageEnv() &&
                !isRootingOrRooted() &&
                !isPermissionOrHasSystemPermission() &&
                !SharedPreferencesUtil.getBooleanFromDefaultSharedPreferences(CLOSE_ACCESSIBILITY_GUIDE, false) &&
                SharedPreferencesUtil.getBooleanFromDefaultSharedPreferences(FIRST_SHOW_ACCESSIBILITY_GUIDE, true);
    }

    */
/**
     * 是否需要展示辅助安装的提示
     *
     * @param pagekey 区分下载管理页面和应用升级页面
     * @return
     *//*

    public static boolean needShowAccessibilityTip(String pagekey) {
        if (TextUtils.isEmpty(pagekey)) {
            return false;
        }
        if (canUseAccessibility() && !isAutoInstallEnabled() && isCNLanguageEnv() &&
                !isRootingOrRooted() &&
                !isPermissionOrHasSystemPermission()) {
            if (pagekey.equals(DOWNLOADPAGE_KEY)) {
                return needShowDPAccessibilityTip();
            } else if (pagekey.equals(UPDATEPAGE_KEY)) {
                return needShowUPAccessibilityTip();
            }
        }
        return false;
    }

    */
/**
     * 是否需要展示升级页面的辅助安装提示
     *
     * @return
     *//*

    public static boolean needShowUPAccessibilityTip() {
        if (SharedPreferencesUtil.getLongFromDefaultSharedPreferences(FIRST_CLOSE_UPDATEPAGE_ACCESSIBILITY_TIP, -1) == -1) {
            return true;
        }
        if (isOver7Days(SharedPreferencesUtil.getLongFromDefaultSharedPreferences(FIRST_CLOSE_UPDATEPAGE_ACCESSIBILITY_TIP, -1))
                && !SharedPreferencesUtil.getBooleanFromDefaultSharedPreferences(SECOND_CLOSE_UPDATEPAGE_ACCESSIBILITY_TIP, false)) {
            return true;
        }
        return false;
    }

    */
/**
     * 是否需要展示下载页面的辅助安装提示
     *
     * @return
     *//*

    public static boolean needShowDPAccessibilityTip() {
        if (SharedPreferencesUtil.getLongFromDefaultSharedPreferences(FIRST_CLOSE_DOWNLOADPAGE_ACCESSIBILITY_TIP, -1) == -1) {
            return true;
        }
        if (isOver7Days(SharedPreferencesUtil.getLongFromDefaultSharedPreferences(FIRST_CLOSE_DOWNLOADPAGE_ACCESSIBILITY_TIP, -1))
                && !SharedPreferencesUtil.getBooleanFromDefaultSharedPreferences(SECOND_CLOSE_DOWNLOADPAGE_ACCESSIBILITY_TIP, false)) {
            return true;
        }
        return false;
    }

    */
/**
     * 当前时间比指定时间是否超过七天
     *
     * @param times
     * @return
     *//*

    private static boolean isOver7Days(long times) {
        if (new Date().getTime() - times > SEVEN_DAY_TIMES) {
            return true;
        }
        return false;
    }

    */
/**
     * 关闭下载页面引导
     *//*

    public static void closeDownloadPageTip() {
        if (SharedPreferencesUtil.getLongFromDefaultSharedPreferences(FIRST_CLOSE_DOWNLOADPAGE_ACCESSIBILITY_TIP, -1) == -1) {
            SharedPreferencesUtil.putLongToDefaultSharedPreferences(FIRST_CLOSE_DOWNLOADPAGE_ACCESSIBILITY_TIP, new Date().getTime());
        } else {
            SharedPreferencesUtil.putBooleanToDefaultSharedPreferences(SECOND_CLOSE_DOWNLOADPAGE_ACCESSIBILITY_TIP, true);
        }
    }

    public static void closeUpdatePageTip() {
        if (SharedPreferencesUtil.getLongFromDefaultSharedPreferences(FIRST_CLOSE_UPDATEPAGE_ACCESSIBILITY_TIP, -1) == -1) {
            SharedPreferencesUtil.putLongToDefaultSharedPreferences(FIRST_CLOSE_UPDATEPAGE_ACCESSIBILITY_TIP, new Date().getTime());
        } else {
            SharedPreferencesUtil.putBooleanToDefaultSharedPreferences(SECOND_CLOSE_UPDATEPAGE_ACCESSIBILITY_TIP, true);
        }
    }

    */
/**
     * 设置界面中是否需要展示辅助安装的开关
     *
     * @return
     *//*

    public static boolean canShowAccessibilityEnter() {
        return canUseAccessibility() && isCNLanguageEnv();
    }

    */
/**
     * 当前版本是否能够使用辅助安装
     *
     * @return
     *//*

    public static boolean canUseAccessibility() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    */
/**
     * 辅助安装是否打开
     *
     * @return
     *//*

    public static boolean isAutoInstallEnabled() {
        boolean isEnabled = false;
        TextUtils.SimpleStringSplitter splitter = new TextUtils.SimpleStringSplitter(':');

        if (isAccessibilityEnable()) {
            String enabledAccessibilityServices = Settings.Secure.getString(C.get().getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (enabledAccessibilityServices != null) {
                splitter.setString(enabledAccessibilityServices);
                while (splitter.hasNext()) {
                    if (ACCESSIBILITY_SERVICE_NAME.equalsIgnoreCase(splitter.next())) {
                        isEnabled = true;
                        break;
                    }
                }
            }
        }
        if (isEnabled) {
            SharedPreferencesUtil.putBooleanToDefaultSharedPreferences(HAS_OPENED_ACCESSIBILITY, isEnabled);
        }
        return isEnabled;
    }

    private static boolean isAccessibilityEnable() {
        boolean enable = false;

        int var1;
        try {
            var1 = Settings.Secure.getInt(C.get().getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException var3) {
            var1 = 0;
        }

        if (var1 != 0) {
            enable = true;
        }

        return enable;
    }

    */
/**
     * 前往辅助安装设置界面，并弹出引导框
     *
     * @param context
     *//*

   */
/* public static void gotoAccessibilitySettings(Context context) {
        if (openAutoInstallSettings(context)) {
            if (!isAutoInstallEnabled()) {
                context.startActivity(new Intent(context, OpenAccessibilityTipActivity.class));
            }
        }
    }*//*


    */
/**
     * 打开辅助安装设置界面
     *
     * @param context
     * @return
     *//*

    private static boolean openAutoInstallSettings(Context context) {
        if (null == context) {
            return false;
        }
        Intent intent = new Intent(ACTION_ACCESSIBILITY_SETTINGS);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // FLAG_ACTIVITY_NEW_TASK
        }

        if (canHandleIntent(context, intent)) {
            context.startActivity(intent);
            return true;
        } else {
            intent.setAction(ACTION_SETTING);
            if (canHandleIntent(context, intent)) {
                context.startActivity(intent);
                return true;
            } else {
                return false;
            }
        }
    }

    public static boolean canHandleIntent(Context paramContext, Intent paramIntent) {
        List<ResolveInfo> infoList = queryIntentActivities(paramContext, paramIntent);
        return infoList != null && !infoList.isEmpty();
    }

    public static List<ResolveInfo> queryIntentActivities(Context paramContext, Intent paramIntent) {
        return paramContext.getPackageManager().queryIntentActivities(paramIntent, PackageManager.MATCH_DEFAULT_ONLY);
    }

    */
/**
     * 是否是简体中文
     *
     * @return
     *//*

    public static boolean isCNLanguageEnv() {
        return ZH_CN.equals(getLanguageEnv());
    }

    private static String getLanguageEnv() {
        Locale l = Locale.getDefault();
        String language = l.getLanguage();
        String country = l.getCountry().toLowerCase();
        if ("zh".equals(language)) {
            if ("cn".equals(country)) {
                language = ZH_CN;
            }
        }
        return language;
    }

    */
/**
     * 安装应用时弹出引导打开辅助安装的提示弹窗
     *
     * @param filepath
     *//*

  */
/*  public static void showAccessibilityDialog(String filepath) {
        Intent intent = new Intent(C.get(), AccessibilityGuideDialogActivity.class);
        intent.putExtra(EXTRA_FILE_PATH, filepath);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        C.get().startActivity(intent);
    }*//*


    */
/**
     * Just for get concrete info if current window.
     *
     * @param root {@link AccessibilityService#getRootInActiveWindow()}
     **//*

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static void printWindowInfo(AccessibilityNodeInfo root) {
        if (root == null) {
            return;
        }

        for (int i = 0; i < root.getChildCount(); i++) {
            AccessibilityNodeInfo info = root.getChild(i);
            if (info != null) {
                Log.i(TAG, "Text:" + info.getText());
                Log.i(TAG, "Content description:" + info.getContentDescription());
                printWindowInfo(info);
                info.recycle();
            }
        }
    }

    */
/**
     * To judge if we are in the installation procedure of Installer.
     *
     * @param event {@link AccessibilityEvent} that is received by {@link com.market2345.accessibility.AccessibilityDistributor}
     * @return true if this event came from Installer; false otherwise.
     **//*

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean isInstallerProgram(AccessibilityEvent event) {
        AccessibilityNodeInfo sourceInfo;
        if (event == null || (sourceInfo = event.getSource()) == null) {
            return false;
        }

        String packageName = String.valueOf(sourceInfo.getPackageName());
        String className = String.valueOf(event.getClassName());
        int id = event.getWindowId();

        if (Utils.isMiUi() || Utils.isFlymeOS()) {
            if (UNINSTALL_ACTIVITY.equals(className) || UNINSTALL_PROGRESS.equals(className)) {
                mWindowIdSet.add(id);
            } else if (!mWindowIdSet.contains(id)) {
                return true;
            }

            return false;
        } else if (LENOVO_INSTALLer_1.equals(packageName) || LENOVO_INSTALLer_2.equals(packageName)) {
            if (INSTALL_ACTIVITY.equals(className) || LENOVO_INSTALL_ACTIVITY.equals(className)
                    || LENOVO_INTERCEPT_ACTIVITY_1.equals(className) || LENOVO_INTERCEPT_ACTIVITY_2.equals(className)
                    || LENOVO_INSTALL_PROGRESS_1.equals(className) || LENOVO_INSTALL_PROGRESS_2.equals(className)) {
                mWindowIdSet.add(id);
            } else if (!mWindowIdSet.contains(id)) {
                return false;
            }
            return true;
        } else {
            if (INSTALL_ACTIVITY.equals(className) || INSTALL_PROGRESS.equals(className)) {
                mWindowIdSet.add(id);
            } else if (!mWindowIdSet.contains(id)) {
                return false;
            }
            return true;
        }
    }
}
*/
