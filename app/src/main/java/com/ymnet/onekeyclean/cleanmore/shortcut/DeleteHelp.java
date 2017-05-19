package com.ymnet.onekeyclean.cleanmore.shortcut;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.junk.mode.InstalledAppAndRAM;
import com.ymnet.onekeyclean.cleanmore.junk.mode.JunkChild;
import com.ymnet.onekeyclean.cleanmore.junk.root.RootStatus;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2015/1/20.
 */
public class DeleteHelp {


    private static String[] notCleanPackages = {"com.android.lockscreen2345", "com.service.usbhelper.service.HelperService"};

    public static void killBackgroundProcess(Context context, List<JunkChild> childs) {
        if (context == null || childs == null || childs.size() == 0) {
            return;
        }
        boolean bySDK = RootStatus.getInstance().isRootedBySDK();
        notCleanPackages = context.getResources().getStringArray(R.array.filter_packnames);
        PackageManager pm = context.getPackageManager();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        // 获得正在运行的所有进程
        List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();
        String currentName = context.getPackageName();
        String inputMethod = getCurrentInputMethod(context);
        //2345锁屏app包名
        if (processes != null) {
            for (ActivityManager.RunningAppProcessInfo info : processes) {
                if (info != null && info.processName != null && info.processName.length() > 0) {
                    try {
                        String processName = info.processName;
                        String[] pkgList = info.pkgList;
                        int pid = info.pid;
                        ApplicationInfo appinfo = pm.getPackageInfo(processName, 0).applicationInfo;
                        if (checkSelect(childs, appinfo.uid)) {
                            if (appinfo != null && filterApp(appinfo) && !processName.equals(currentName) && !isNotClean(processName)) {
                                if (bySDK) {
                                    for (String packageName : pkgList) {
                                        if(!packageName.equals(inputMethod)){
                                            // TODO: 2017/4/28 0028 root相关的代码
//                                        SuUtil.kill(packageName);
                                        }
                                    }
                                } else {
                                    am.killBackgroundProcesses(processName);// 杀进程
                                }
                            }
                        }

                    } catch (Exception e) {

                    }
                }

            }
        }

    }

    public static void killBackgroundProcess(Context context, InstalledAppAndRAM ram) {
        List<JunkChild> item = new ArrayList<JunkChild>();
        item.add(ram);
        killBackgroundProcess(context, item);
    }

    private static boolean checkSelect(List<JunkChild> childs, int uid) {
        for (JunkChild child : childs) {
            if (child instanceof InstalledAppAndRAM) {
                InstalledAppAndRAM ram = (InstalledAppAndRAM) child;
                // TODO: 2017/5/19 0019 修改 去除选中标准
                if (ram.getSelect() == 1/* && uid == ram.app.uid*/) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean filterApp(ApplicationInfo info) {
        if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
            return true;
        } else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
            return true;
        }

        return false;
    }


    private static boolean isNotClean(String packageName) {
        if (TextUtils.isEmpty(packageName))
            return false;
        for (int i = 0; i < notCleanPackages.length; i++) {
            if (notCleanPackages[i].equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    private static String getCurrentInputMethod(Context context) {
        String result = "";
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        List<InputMethodInfo> mInputMethodProperties = imm.getEnabledInputMethodList();
        String mLastInputMethodId = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.DEFAULT_INPUT_METHOD);
        int N = (mInputMethodProperties == null ? 0 : mInputMethodProperties
                .size());
        for (int i = 0; i < N; ++i) {
            InputMethodInfo property = mInputMethodProperties.get(i);
            String prefKey = property.getId();
            if (prefKey.equals(mLastInputMethodId)) {
                result=property.getPackageName();
                break;
            }
        }
        return result;
    }


}
