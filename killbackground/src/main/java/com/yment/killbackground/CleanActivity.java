package com.yment.killbackground;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.commonlibrary.systemmanager.SystemMemory;
import com.example.commonlibrary.utils.ShareDataUtils;
import com.example.commonlibrary.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;
import com.wenming.library.processutil.AndroidProcess;
import com.wenming.library.processutil.ProcessManager;
import com.yment.killbackground.anim.MyViewPropertyAnimatorListener;
import com.yment.killbackground.view.Wheel;
import com.ymnet.update.DownLoadFactory;

import java.util.ArrayList;
import java.util.List;

import static android.text.format.Formatter.formatFileSize;
import static com.example.commonlibrary.systemmanager.SystemMemory.getAvailMemorySize;
import static com.example.commonlibrary.systemmanager.SystemMemory.getTotalMemorySize;
import static com.yment.killbackground.R.id.imageView_within1;

public class CleanActivity extends Activity {
    private static final String TAG = "CleanActivity";
    private ImageView mRotateImage;
    private Animation animation;
    private TextView  mMemoryInfo;
    private static final int  UPDATE_SNAP_TIME      = 100;
    private              int  mRepeatTime           = 0;
    private              int  mRepeatTotaleTime     = 0;
    private              long mTotaleMemory         = 0;
    private              int  mBeforeUsedMemoryRate = 0;
    private static       int  mIncreaseDate         = 1;
    private ImageView            mRotateImageInside;
    private ImageView            mClean1;
    private ImageView            mClean2;
    private ImageView            mClean3;
    private ImageView            mClean4;
    private ImageView            mClean5;
    private ImageView            mClean6;
    private ImageView            mClean7;
    private ImageView            mClean8;
    private ImageView            mClean9;
    private ImageView            mClean10;
    private ArrayList<ImageView> cleanAppLists;
    private long                 mBeforeAvailMemorySize;
    private long                 mSize;
    private Wheel                mWheel;
    private String               showToast;
    private boolean isFirst = true;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            finish();
        }
    };
    private ImageView     mDetermine;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean);
        initView();
        initData();
        QihooSystemUtil.openAllPermission(getApplicationContext(), "com.ymnet.apphelper");
        MobclickAgent.setScenarioType(getApplicationContext(), MobclickAgent.EScenarioType.E_UM_NORMAL);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);

        Log.d(TAG, "onResume: ");

        mIncreaseDate = 5;

        if (isFirst) {
            isFirst = false;
            //旋转动画
            animation = AnimationUtils.loadAnimation(CleanActivity.this, R.anim.clean_anim);//加载动画
            animation.setRepeatCount(0);
            animation.setAnimationListener(new Animation.AnimationListener() {

                private long mNowAvailMemorySize;

                @Override
                public void onAnimationStart(Animation animation) {
                    mHandler.postDelayed(mShowMemoInfo, 1500);
                    mBeforeAvailMemorySize = getAvailMemorySize(CleanActivity.this);
                    //吸入软件动画
                    mWheel = (Wheel) findViewById(R.id.wheel_iv);
                    mWheel.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mWheel.setVisibility(View.VISIBLE);
                        }
                    }, 400);

                    //清理所有进程
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            killAll(CleanActivity.this, true);
                        }
                    }).start();
                }

                @Override
                public void onAnimationEnd(final Animation animation) {
                    mRotateImage.setVisibility(View.INVISIBLE);
                    mMemoryInfo.setVisibility(View.INVISIBLE);

                    mDetermine.setScaleX(0);
                    mDetermine.setScaleY(0);
                    mDetermine.setVisibility(View.VISIBLE);
                    //√展示动画
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ViewCompat.animate(mDetermine).scaleX(1).scaleY(1).setDuration(500).setListener(new MyViewPropertyAnimatorListener() {
                                @Override
                                public void onAnimationEnd(View view) {
                                    super.onAnimationEnd(view);
                                }
                            }).start();
                        }
                    }, 300);

                    mHandler.sendEmptyMessageDelayed(0, 900);

                    /*//显示框动画
                    mBox.setScaleY(0);
                    mBox.setVisibility(View.VISIBLE);
                    ViewCompat.animate(mBox).scaleY(1).setDuration(500).start();*/

                    //Toast显示清理内存大小
                    mNowAvailMemorySize = SystemMemory.getAvailMemorySize(CleanActivity.this);
                    mSize = mBeforeAvailMemorySize - mNowAvailMemorySize;
                    Log.d(TAG, "onAnimationEnd: mSize: " + mSize);

                    ToastUtil.showToast(getApplicationContext(), showToast);

                    startStaticApp(getApplicationContext());
                    DownLoadFactory.getInstance().getInsideInterface().updateApp(CleanActivity.this);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    updateMomoryInfo();
                }
            });
            mRotateImage.setAnimation(animation);

            ObjectAnimator oa1 = ObjectAnimator.ofFloat(mRotateImageInside, "rotation", 0, -1800f);
            oa1.setDuration(3000);
            oa1.start();

            mRepeatTime = 1;
            mRepeatTotaleTime = (int) this.animation.getDuration() / UPDATE_SNAP_TIME - 3;
            mHandler.postDelayed(mUpdateMemoryInfo, UPDATE_SNAP_TIME);

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);

    }

    private void initView() {
        mRotateImage = (ImageView) findViewById(R.id.imageView_cylindrical);
        mRotateImageInside = (ImageView) findViewById(imageView_within1);
        mWheel = (Wheel) findViewById(R.id.wheel_iv);
        //        mBox = (GridView) findViewById(R.id.iv_box);
        mDetermine = (ImageView) findViewById(R.id.imageView_determine);

        mMemoryInfo = (TextView) findViewById(R.id.memory_info);
        mMemoryInfo.setVisibility(View.INVISIBLE);

        cleanAppLists = new ArrayList<>();
        mClean1 = (ImageView) findViewById(R.id.iv_clean1);
        mClean2 = (ImageView) findViewById(R.id.iv_clean2);
        mClean3 = (ImageView) findViewById(R.id.iv_clean3);
        mClean4 = (ImageView) findViewById(R.id.iv_clean4);
        mClean5 = (ImageView) findViewById(R.id.iv_clean5);
        mClean6 = (ImageView) findViewById(R.id.iv_clean6);
        mClean7 = (ImageView) findViewById(R.id.iv_clean7);
        mClean8 = (ImageView) findViewById(R.id.iv_clean8);
        mClean9 = (ImageView) findViewById(R.id.iv_clean9);
        mClean10 = (ImageView) findViewById(R.id.iv_clean10);
        cleanAppLists.add(mClean1);
        cleanAppLists.add(mClean2);
        cleanAppLists.add(mClean3);
        cleanAppLists.add(mClean4);
        cleanAppLists.add(mClean5);
        cleanAppLists.add(mClean6);
        cleanAppLists.add(mClean7);
        cleanAppLists.add(mClean8);
        cleanAppLists.add(mClean9);
        cleanAppLists.add(mClean10);


    }

    private void initData() {
        mTotaleMemory = getTotalMemorySize(CleanActivity.this);
        mBeforeUsedMemoryRate = getUsedMemoryRate();
        DownLoadFactory.getInstance().init(this);
        //        PushManager.getInstance().init(getApplicationContext());
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
            if (mRepeatTime < mRepeatTotaleTime) {
                updateMomoryInfo();
                mHandler.postDelayed(mUpdateMemoryInfo, UPDATE_SNAP_TIME);
            }
        }
    };

    private void updateMomoryInfo() {
        int usedMomory = getUsedMemoryRate();
        if (usedMomory <= mBeforeUsedMemoryRate) {
            usedMomory = mBeforeUsedMemoryRate;
        } else {
            mIncreaseDate = mIncreaseDate + usedMomory - mBeforeUsedMemoryRate;
            mBeforeUsedMemoryRate = usedMomory;
        }
        mMemoryInfo.setText("" + usedMomory + "%");

    }

    /**
     * 获取可用内存百分比
     */
    private int getUsedMemoryRate() {
        return 100 - (int) (100 * ((float) SystemMemory.getAvailMemorySize(CleanActivity.this) / mTotaleMemory));
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

    public static void startStaticApp(Context context) {
        try {
            if (Utilities.isAppInstalled(context, "com.ymnet.apphelper")) {
                Intent intent = new Intent();
                intent.setClassName("com.ymnet.apphelper", "com.ymnet.apphelper.AppHelperActivityText");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        } catch (Exception ex) {
            // TODO: 2017/4/11 0011 统一异常处理
        }
    }

    /**
     * 杀死后台运行程序
     * @param context
     * @param visiable
     * @return
     */
    public int killAll(Context context, boolean visiable) {
        List<Drawable> loadIcon = new ArrayList<>();

        int count = 0;//被杀进程计数
        String nameList = "";//记录被杀死进程的包名
        long beforeMem = SystemMemory.getAvailMemorySize(context);//清理前的可用内存

        //获取一个ActivityManager 对象
        ActivityManager activityManager = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
        final PackageManager packageManager = context.getPackageManager();

        //获取系统中所有正在运行的进程
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            List<ActivityManager.RunningAppProcessInfo> appProcessInfos = activityManager
                    .getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessInfos) {
                nameList = "";
                if (appProcessInfo.processName.contains("com.android.system")
                        || appProcessInfo.pid == android.os.Process.myPid() || appProcessInfo.processName.contains("com.ymnet.apphelper"))//跳过系统 及当前进程
                    continue;
                String[] pkNameList = appProcessInfo.pkgList;//进程下的所有包名
                for (int i = 0; i < pkNameList.length; i++) {
                    String pkName = pkNameList[i];
                    activityManager.killBackgroundProcesses(pkName);//杀死该进程
                    count++;//杀死进程的计数+1
                    nameList += "  " + pkName;
                }
                Log.i(TAG, nameList + "---------------------");
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


                Log.i(TAG, nameList + "---------------------");
            }
        }

        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        Log.d(TAG, "killAll: loadIcon:" + loadIcon.size());

        long lastCleanTime = ShareDataUtils.getSharePrefLongData(context, "clean_data", "last_clean_time");
        ShareDataUtils.setSharePrefData(context, "clean_data", "last_clean_time", System.currentTimeMillis());
        boolean canClean = System.currentTimeMillis() - lastCleanTime > 1000 * 30;

        long afterMem = SystemMemory.getAvailMemorySize(context);//清理后的内存占用
        if (visiable) {

            if ((count < 2 && afterMem - beforeMem < 5) || !canClean) {
                //toast展示内存已达最佳
                showToast = context.getResources().getString(R.string.toast_bean_best);

            } else {
                //获取最近十个应用图标,展示吸入动画
                final List<ResolveInfo> appList = packageManager.queryIntentActivities(intent, 0);
                for (int i = 0; i < appList.size(); i++) {
                    loadIcon.add(appList.get(i).loadIcon(packageManager));
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int count = cleanAppLists.size();
                        for (int i = 0; i < count; i++) {
                            cleanAppLists.get(i).setImageDrawable(appList.get(appList.size() - i - 1).loadIcon(packageManager));
                            playAnimation(cleanAppLists.get(i), 500 + 100 * i);
                        }
                    }
                });
                //toast展示为用户清理的内存
                String sAgeFormat = context.getResources().getString(R.string.toast_clean_result);
                showToast = String.format(sAgeFormat, formatFileSize(context, Math.abs(afterMem - beforeMem)), mIncreaseDate);
            }

        }
        Log.d(TAG, "killAll: count:" + count);
        return count;
    }

  /*  private int[] icon = {R.mipmap.robot,R.mipmap.robot,R.mipmap.robot,R.mipmap.robot};
    private String[] iconName = {"微信清理", "QQ清理", "深度清理", "更多功能"};
    public List<Map<String, Object>> getData() {

        for (int i = 0; i < icon.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", icon[i]);
            map.put("text", iconName[i]);
            data_list.add(map);
        }

        return data_list;
    }*/

    /**
     * 吸入应用动画
     * @param v 吸入应用控件
     * @param time 延时展示时间
     */
    public void playAnimation(final ImageView v, int time) {

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        final int width = dm.widthPixels;
        final int height = dm.heightPixels;

        //获取actionbar的高度
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        final int statusBarHeight  = getResources().getDimensionPixelSize(resourceId);

        //位移量
        final float transX = -v.getX() - v.getMeasuredWidth() / 2 + width / 2;
        final float transY = -v.getY() + v.getMeasuredHeight()/2 + height / 2 - statusBarHeight;

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                v.setVisibility(View.VISIBLE);
                Log.d(TAG, "run: mWheel.getX():" + mWheel.getX() + " mWheel.getY():" + mWheel.getY());
                ViewCompat.animate(v).scaleX(0.3f).scaleY(0.3f).alpha(0.3f).translationX(transX).translationY(transY).setDuration(450).setListener(new MyViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationEnd(View view) {
                        super.onAnimationEnd(view);
                        v.setVisibility(View.GONE);
                    }

                }).start();

            }
        }, time);

    }

}