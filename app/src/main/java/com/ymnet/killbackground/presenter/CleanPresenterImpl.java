package com.ymnet.killbackground.presenter;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.commonlibrary.utils.PhoneModel;
import com.example.commonlibrary.utils.ShareDataUtils;
import com.example.commonlibrary.utils.SystemMemory;
import com.wenming.library.processutil.AndroidProcess;
import com.wenming.library.processutil.ProcessManager;
import com.ymnet.killbackground.utils.Run;
import com.ymnet.killbackground.view.CleanView;
import com.ymnet.onekeyclean.R;

import java.util.List;


/**
 * Created by MajinBuu on 2017/4/17 0017.
 *
 * @overView CleanActivity的逻辑实现
 */

public class CleanPresenterImpl implements CleanPresenter {

    private CleanView cleanView;
    public  String    content;

    public CleanPresenterImpl(CleanView cleanView) {
        this.cleanView = cleanView;
    }

    @Override
    public void killAll(final Context context, final boolean visible) {

        long beforeMem;
        int count = 0;//被杀进程计数

        beforeMem = SystemMemory.getAvailMemorySize(context);//清理前的可用内存
        //        long beforeMem = getAvailMemorySize(context);

        String nameList = "";//记录被杀死进程的包名
        int temp = 0;//被杀进程计数

        Run.onMain(new Runnable() {
            @Override
            public void run() {
                //调用系统清理
                callSystem(context);
            }
        });

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
                    temp++;//杀死进程的计数+1
                    nameList += "  " + pkName;
                }
                count = temp;
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
                temp++;//杀死进程的计数+1
                nameList += "  " + pkName;
            }
            count = temp;
        }


        long lastCleanTime = ShareDataUtils.getSharePrefLongData(context, "clean_data", "last_clean_time");
        ShareDataUtils.setSharePrefData(context, "clean_data", "last_clean_time", System.currentTimeMillis());
        boolean canClean = System.currentTimeMillis() - lastCleanTime > 1000 * 30;

        long afterMem = SystemMemory.getAvailMemorySize(context);//清理后的内存占用
        //        long afterMem = getAvailMemorySize(context);//清理后的内存占用
        long cleanMem = Math.abs(afterMem - beforeMem);

        boolean valueChange = true;
        if (visible) {
            //2.清理缓存扫描结果:
            //1>不需要清理:
            if (cleanMem < 5 || !canClean) {
                valueChange = false;
                //toast展示内存已达最佳
                content = context.getResources().getString(R.string.toast_bean_best);
                cleanView.showToast(content);
                cleanView.bestState(true);

            } else {
                //2>需要清理:
                valueChange = true;
                //获取最近十个应用图标,展示吸入动画
                cleanView.getIconAndShow(cleanMem);
            }
            cleanView.isValueChang(valueChange);
        }


    }

    private void callSystem(Context context) {
        if (PhoneModel.matchModel("mi")) {

            Intent localIntent = new Intent("com.android.systemui.taskmanager.Clear");
            context.sendBroadcast(localIntent);
        }
    }

}
