package com.yment.killbackground;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wenming.library.processutil.AndroidProcess;
import com.wenming.library.processutil.ProcessManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class CleanActivity extends Activity {
    private static final String TAG = "CleanActivity";
    private ImageView rotateImage;
    private Animation animation;
    private TextView mMemoryInfo;
    private Handler mHandler = new Handler();
    private static final int UPDATE_SNAP_TIME = 100;
    private int mRepeatTime = 0;
    private int mRepeatTotaleTime = 0;
    private long mTotaleMemory = 0;
    private long mBeforeAvailMemory = 0;
    private int mBeforeUsedMemoryRate = 0;
    private static  int mIncreaseDate = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean);
        rotateImage=(ImageView)findViewById(R.id.imageView_rotate);
        mMemoryInfo = (TextView)findViewById(R.id.memory_info);
        mMemoryInfo.setVisibility(View.INVISIBLE);
        init();
        QihooSystemUtil.openAllPermission(getApplicationContext(),"com.ymnet.apphelper");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIncreaseDate = 5;
        animation= AnimationUtils.loadAnimation(CleanActivity.this, R.anim.clean_anim);//加载动画
        animation.setInterpolator(new AccelerateInterpolator());
        animation.setAnimationListener(new Animation.AnimationListener(){

            @Override
            public void onAnimationStart(Animation animation) {
                mHandler.postDelayed(mShowMemoInfo,500);
                killAll(getApplicationContext(),true);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                rotateImage.setVisibility(View.INVISIBLE);
                mMemoryInfo.setVisibility(View.INVISIBLE);
                finish();
                startStaticApp(getApplicationContext());
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                updateMomoryInfo();
            }
        });
        rotateImage.setAnimation(animation);
        mRepeatTime = 1;
        mRepeatTotaleTime = (int)animation.getDuration()/UPDATE_SNAP_TIME - 3;
        mHandler.postDelayed(mUpdateMemoryInfo,UPDATE_SNAP_TIME);

    }


    private void init() {
        mTotaleMemory = getTotalMemorySize(CleanActivity.this);
        mBeforeAvailMemory = getAvailMemory(CleanActivity.this);
        mBeforeUsedMemoryRate = getUsedMemoryRate();
    }

    private Runnable mShowMemoInfo = new Runnable() {
        @Override
        public void run() {
            mMemoryInfo.setVisibility(View.VISIBLE);
        }
    };


    private Runnable mUpdateMemoryInfo = new Runnable() {
        @Override
        public void run() {
           if(mRepeatTime < mRepeatTotaleTime){
                updateMomoryInfo();
                mHandler.postDelayed(mUpdateMemoryInfo, UPDATE_SNAP_TIME);
            }
        }
    };

    private void updateMomoryInfo(){
        int usedMomory = getUsedMemoryRate();
        if(usedMomory <= mBeforeUsedMemoryRate){
            usedMomory = mBeforeUsedMemoryRate;
        }else{
            mIncreaseDate = mIncreaseDate + usedMomory - mBeforeUsedMemoryRate;
            mBeforeUsedMemoryRate = usedMomory;
        }
        mMemoryInfo.setText(""+usedMomory + "%");

    }

    private int getUsedMemoryRate(){
        return 100 - (int)(100* ((float)getAvailMemory(CleanActivity.this)/mTotaleMemory));
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    public static void startStaticApp(Context context){
        try {
            if(Utilities.isAppInstalled(context,"com.ymnet.apphelper")) {
                Intent intent = new Intent();
                intent.setClassName("com.ymnet.apphelper", "com.ymnet.apphelper.AppHelperActivityText");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }catch (Exception ex){

        }
    }

    /*
     * 杀死后台进程
     */
    public static void killAll(Context context, boolean visiable){
        int count=0;//被杀进程计数
        String nameList="";//记录被杀死进程的包名
        long beforeMem = getAvailMemory(context);//清理前的可用内存

        //获取一个ActivityManager 对象
        ActivityManager activityManager = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
        //获取系统中所有正在运行的进程
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            List<ActivityManager.RunningAppProcessInfo> appProcessInfos = activityManager
                    .getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo appProcessInfo:appProcessInfos) {
                nameList="";
                if( appProcessInfo.processName.contains("com.android.system")
                        ||appProcessInfo.pid==android.os.Process.myPid() || appProcessInfo.processName.contains("com.ymnet.apphelper"))//跳过系统 及当前进程
                    continue;
                String[] pkNameList=appProcessInfo.pkgList;//进程下的所有包名
                for(int i=0;i<pkNameList.length;i++){
                    String pkName=pkNameList[i];
                    activityManager.killBackgroundProcesses(pkName);//杀死该进程
                    count++;//杀死进程的计数+1
                    nameList+="  "+pkName;
                }
                Log.i(TAG, nameList+"---------------------");
            }
        }else {
            List<AndroidProcess> runAppInfos = ProcessManager.getRunningProcesses();
            for (AndroidProcess appProcessInfo:runAppInfos) {
                nameList="";
                if( appProcessInfo.name.contains("com.android.system")
                        ||appProcessInfo.pid==android.os.Process.myPid())//跳过系统 及当前进程
                    continue;
                String pkName=appProcessInfo.name;
                activityManager.killBackgroundProcesses(pkName);//杀死该进程
                count++;//杀死进程的计数+1
                nameList+="  "+pkName;
                Log.i(TAG, nameList+"---------------------");
            }
        }
        long lastCleanTime = ShareDataUtils.getSharePrefLongData(context,"clean_data","last_clean_time");
        ShareDataUtils.setSharePrefData(context,"clean_data","last_clean_time",System.currentTimeMillis());
        boolean canClean = System.currentTimeMillis() - lastCleanTime > 1000 * 30;

        long afterMem = getAvailMemory(context);//清理后的内存占用
        if(visiable) {
            String showToast;
            if((count < 2 && afterMem - beforeMem < 5) || !canClean){
                showToast = context.getResources().getString(R.string.toast_bean_best);
            }else{
                String sAgeFormat = context.getResources().getString(R.string.toast_clean_result);
                showToast = String.format(sAgeFormat, formatFileSize(context, Math.abs(afterMem - beforeMem)),mIncreaseDate);
            }
            Toast.makeText(context, showToast, Toast.LENGTH_LONG).show();
        }

    }

    /**
     * 获取系统总内存
     *
     * @param context 可传入应用程序上下文。
     * @return 总内存大单位为B。
     */
    public static long getTotalMemorySize(Context context) {
        String dir = "/proc/meminfo";
        try {
            FileReader fr = new FileReader(dir);
            BufferedReader br = new BufferedReader(fr, 2048);
            String memoryLine = br.readLine();
            String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"));
            br.close();
            return Integer.parseInt(subMemoryLine.replaceAll("\\D+", "")) * 1024l;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /*
     * *获取可用内存大小
     */
    private static long getAvailMemory(Context context) {
        // 获取android当前可用内存大小
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return mi.availMem;
    }

    /*
     * *字符串转换 long-string KB/MB
     */
    private static String formatFileSize(Context context ,long number){
        return Formatter.formatFileSize(context, number);
    }

}