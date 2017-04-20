package com.example.commonlibrary.systemmanager;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.os.Build;
import android.text.format.Formatter;

import com.umeng.analytics.MobclickAgent;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by MajinBuu on 2017/4/10 0010.
 *
 * @overView 系统内存工具类,获取系统总内存大小,获取系统可用内存大小
 */

public class SystemMemory {

    /**
     * 获取当前设备总共的内存大小
     */
    public static long getTotalMemorySize(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo outInfo = new MemoryInfo();
        activityManager.getMemoryInfo(outInfo);

        long totalMem = 0;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN){
            totalMem = outInfo.totalMem;//API16才开始有的,so有版本问题
        }else{
            //MemTotal:         513000 kB
            //存放位置proc/meminfo
            try {
                BufferedReader br = new BufferedReader(new FileReader("proc/meminfo"));
                String readLine = br.readLine();
                readLine = readLine.replace("MemToal", "").trim();
                readLine = readLine.replace("kB", "").trim();
                totalMem = Integer.valueOf(readLine)*1024;
            } catch (FileNotFoundException e) {
                MobclickAgent.reportError(context,e.fillInStackTrace());
            } catch (IOException e) {
                MobclickAgent.reportError(context,e.fillInStackTrace());
            }
            totalMem = 0;
        }

        return totalMem;
    }

    /**
     * 获取当前设备可用的内存大小
     */
    public static long getAvailMemorySize(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo outinfo = new MemoryInfo();
        activityManager.getMemoryInfo(outinfo);
        long availMem = outinfo.availMem;

        return availMem;
    }

    /**
     * 字符串转换 long-string KB/MB
     */
    private static String formatFileSize(Context context ,long number){
        return Formatter.formatFileSize(context, number);
    }
}
