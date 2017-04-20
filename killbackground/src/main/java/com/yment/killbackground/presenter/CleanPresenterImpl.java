package com.yment.killbackground.presenter;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.example.commonlibrary.systemmanager.SystemMemory;
import com.example.commonlibrary.utils.ShareDataUtils;
import com.wenming.library.processutil.AndroidProcess;
import com.wenming.library.processutil.ProcessManager;
import com.yment.killbackground.R;
import com.yment.killbackground.view.CleanView;

import java.util.List;

import static com.ymnet.update.utils.NetworkUtils.TAG;

/**
 * Created by MajinBuu on 2017/4/17 0017.
 *
 * @overView CleanActivity的逻辑实现
 */

public class CleanPresenterImpl implements CleanPresenter {


    private CleanView cleanView;
    public String content;

    public CleanPresenterImpl(CleanView cleanView) {
        this.cleanView = cleanView;
    }

//    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public int killAll(Context context, boolean visible) {

        int count = 0;//被杀进程计数
        String nameList = "";//记录被杀死进程的包名
        long beforeMem = SystemMemory.getAvailMemorySize(context);//清理前的可用内存

        //获取一个ActivityManager 对象
        ActivityManager activityManager = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);

        //获取系统中所有正在运行的进程
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            List<ActivityManager.RunningAppProcessInfo> appProcessInfos = activityManager
                    .getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessInfos) {
                nameList = "";
                if (appProcessInfo.processName.contains("com.android.system")
                        || appProcessInfo.pid == android.os.Process.myPid() || appProcessInfo.processName.contains("com.ymnet.apphelper") || appProcessInfo.processName.contains("com.qjkj.nnb"))//跳过系统 及当前进程
                    continue;
                String[] pkNameList = appProcessInfo.pkgList;//进程下的所有包名
                for (int i = 0; i < pkNameList.length; i++) {
                    String pkName = pkNameList[i];
                    activityManager.killBackgroundProcesses(pkName);//杀死该进程
                    count++;//杀死进程的计数+1
                    nameList += "  " + pkName;
                }
            }
        } else {
            List<AndroidProcess> runAppInfos = ProcessManager.getRunningProcesses();
            for (AndroidProcess appProcessInfo : runAppInfos) {
                nameList = "";
                if (appProcessInfo.name.contains("com.android.system")
                        || appProcessInfo.pid == android.os.Process.myPid())//跳过系统 及当前进程
                    continue;
                String pkName = appProcessInfo.name;

                activityManager.killBackgroundProcesses(pkName);//杀死该进程
                count++;//杀死进程的计数+1
                nameList += "  " + pkName;
            }
        }

        long lastCleanTime = ShareDataUtils.getSharePrefLongData(context, "clean_data", "last_clean_time");
        ShareDataUtils.setSharePrefData(context, "clean_data", "last_clean_time", System.currentTimeMillis());
        boolean canClean = System.currentTimeMillis() - lastCleanTime > 1000 * 30;

        long afterMem = SystemMemory.getAvailMemorySize(context);//清理后的内存占用

        long cleanMem = Math.abs(afterMem - beforeMem);
        boolean valueChange = true;
        if (visible) {

//            if ((count < 2 && afterMem - beforeMem < 5) || !canClean) {
            if (cleanMem < 5 || !canClean) {
                valueChange = false;
                //toast展示内存已达最佳
                content = context.getResources().getString(R.string.toast_bean_best);
                cleanView.showToast(content);
            } else {
                valueChange = true;
                //获取最近十个应用图标,展示吸入动画
                cleanView.getIconAndShow(cleanMem);
            }

            cleanView.isValueChang(valueChange);

        }
        Log.d(TAG, "killAll: count:" + count);
        return count;
    }

}
