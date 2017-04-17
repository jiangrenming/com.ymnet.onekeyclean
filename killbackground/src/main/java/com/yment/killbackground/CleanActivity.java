package com.yment.killbackground;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.commonlibrary.systemmanager.SystemMemory;
import com.example.commonlibrary.utils.DensityUtil;
import com.example.commonlibrary.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;
import com.yment.killbackground.customlistener.MyViewPropertyAnimatorListener;
import com.yment.killbackground.presenter.CleanPresenter;
import com.yment.killbackground.presenter.CleanPresenterImpl;
import com.yment.killbackground.view.CleanView;
import com.yment.killbackground.view.customwidget.Wheel;
import com.ymnet.update.DownLoadFactory;

import java.util.ArrayList;
import java.util.List;

import static android.text.format.Formatter.formatFileSize;
import static com.example.commonlibrary.systemmanager.SystemMemory.getAvailMemorySize;
import static com.example.commonlibrary.systemmanager.SystemMemory.getTotalMemorySize;
import static com.yment.killbackground.R.id.imageView_within1;

public class CleanActivity extends Activity implements CleanView {
    private static final String TAG = "CleanActivity";
    private ImageView mRotateImage;
    private TextView  mMemoryInfo;
    private static final int  UPDATE_SNAP_TIME      = 100;
    private              int  mRepeatTime           = 0;
    private              int  mRepeatTotalTime      = 0;
    private              long mTotalMemory          = 0;
    private              int  mBeforeUsedMemoryRate = 0;
    private static       int  mIncreaseDate         = 1;
    private ImageView            mRotateImageInside;
    private ArrayList<ImageView> cleanAppLists;
    private long                 mBeforeAvailMemorySize;
    private long                 mSize;
    private Wheel                mWheel;
    private String               showToast;
    private boolean        isFirst  = true;
    private ImageView      mDetermine;
    private Button         mMoreFunction;
    private RelativeLayout mRelativeLayout;
    private CleanPresenter mCleanPresenter;
    private Handler mHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean);

        mCleanPresenter = new CleanPresenterImpl(this);
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
            Animation animation = AnimationUtils.loadAnimation(CleanActivity.this, R.anim.clean_anim);//加载动画
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
                            mCleanPresenter.killAll(CleanActivity.this, true);
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
                                    //展开动画
                                    openAnimation();

                                }
                            }).start();
                        }
                    }, 300);

                    //Toast显示清理内存大小
                    mNowAvailMemorySize = SystemMemory.getAvailMemorySize(CleanActivity.this);
                    mSize = mBeforeAvailMemorySize - mNowAvailMemorySize;
                    Log.d(TAG, "onAnimationEnd: mSize: " + mSize);

//                    ToastUtil.showLong(getApplicationContext(), showToast);

                    startStaticApp(getApplicationContext());
                    DownLoadFactory.getInstance().getInsideInterface().updateApp(CleanActivity.this);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    updateMemoryInfo();
                }
            });
            mRotateImage.setAnimation(animation);

            ObjectAnimator oa1 = ObjectAnimator.ofFloat(mRotateImageInside, "rotation", 0, -1800f);
            oa1.setDuration(3000);
            oa1.start();

            mRepeatTime = 1;
            mRepeatTotalTime = (int) animation.getDuration() / UPDATE_SNAP_TIME - 3;
            mHandler.postDelayed(mUpdateMemoryInfo, UPDATE_SNAP_TIME);

        }
    }

    /**
     * 展开动画
     */
    private void openAnimation() {
        //dp转px
        int dp80 = DensityUtil.dp2px(this, 100);

        ViewCompat.animate(mRelativeLayout).translationX(-dp80).setDuration(500).setListener(new MyViewPropertyAnimatorListener() {
            @Override
            public void onAnimationStart(View view) {
                super.onAnimationStart(view);
                //按钮展示动画
                mMoreFunction.setScaleX(0);
                mMoreFunction.setVisibility(View.VISIBLE);
                ViewCompat.animate(mMoreFunction).scaleX(1).setDuration(500).start();
                mMoreFunction.setText(showToast);
            }

            @Override
            public void onAnimationEnd(View view) {
                super.onAnimationEnd(view);
                //关闭动画
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 2500);
            }
        }).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);

    }

    private void initView() {

        mRelativeLayout = (RelativeLayout) findViewById(R.id.rl_anim);
        mRotateImage = (ImageView) findViewById(R.id.imageView_cylindrical);
        mRotateImageInside = (ImageView) findViewById(imageView_within1);
        mWheel = (Wheel) findViewById(R.id.wheel_iv);
        //        mBox = (GridView) findViewById(R.id.iv_box);
        mDetermine = (ImageView) findViewById(R.id.imageView_determine);

        mMemoryInfo = (TextView) findViewById(R.id.memory_info);
        mMemoryInfo.setVisibility(View.INVISIBLE);

        mMoreFunction = (Button) findViewById(R.id.btn_more_function);
        mMoreFunction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showShort(CleanActivity.this, "更多功能开发中...");
            }
        });

        cleanAppLists = new ArrayList<>();
        ImageView mClean1 = (ImageView) findViewById(R.id.iv_clean1);
        ImageView mClean2 = (ImageView) findViewById(R.id.iv_clean2);
        ImageView mClean3 = (ImageView) findViewById(R.id.iv_clean3);
        ImageView mClean4 = (ImageView) findViewById(R.id.iv_clean4);
        ImageView mClean5 = (ImageView) findViewById(R.id.iv_clean5);
        ImageView mClean6 = (ImageView) findViewById(R.id.iv_clean6);
        ImageView mClean7 = (ImageView) findViewById(R.id.iv_clean7);
        ImageView mClean8 = (ImageView) findViewById(R.id.iv_clean8);
        ImageView mClean9 = (ImageView) findViewById(R.id.iv_clean9);
        ImageView mClean10 = (ImageView) findViewById(R.id.iv_clean10);
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
        mTotalMemory = getTotalMemorySize(CleanActivity.this);
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
            if (mRepeatTime < mRepeatTotalTime) {
                updateMemoryInfo();
                mHandler.postDelayed(mUpdateMemoryInfo, UPDATE_SNAP_TIME);
            }
        }
    };

    private void updateMemoryInfo() {
        int usedMemory = getUsedMemoryRate();
        if (usedMemory <= mBeforeUsedMemoryRate) {
            usedMemory = mBeforeUsedMemoryRate;
        } else {
            mIncreaseDate = mIncreaseDate + usedMemory - mBeforeUsedMemoryRate;
            mBeforeUsedMemoryRate = usedMemory;
        }
        mMemoryInfo.setText("" + usedMemory + "%");

    }

    /**
     * 获取可用内存百分比
     */
    private int getUsedMemoryRate() {
        return 100 - (int) (100 * ((float) SystemMemory.getAvailMemorySize(CleanActivity.this) / mTotalMemory));
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
     * 吸入应用动画
     *
     * @param v    吸入应用控件
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
        final int statusBarHeight = getResources().getDimensionPixelSize(resourceId);

        //位移量
        final float transX = -v.getX() - v.getMeasuredWidth() / 2 + width / 2;
        final float transY = -v.getY() + v.getMeasuredHeight() / 2 + height / 2 - statusBarHeight;

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

    @Override
    public void getIconAndShow(long cleanMem) {

        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        final PackageManager packageManager = this.getPackageManager();

        final List<ResolveInfo> appList = packageManager.queryIntentActivities(intent, 0);

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
        String sAgeFormat = this.getResources().getString(R.string.toast_clean_result);
        String content = String.format(sAgeFormat, formatFileSize(this, cleanMem), mIncreaseDate);
        showToast(content);
    }

    @Override
    public void showToast(String content) {
        showToast = content;
    }
}