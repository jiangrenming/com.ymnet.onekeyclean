package com.ymnet.killbackground.view;

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
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.commonlibrary.retrofit2_callback.BaseCallModel;
import com.example.commonlibrary.retrofit2_callback.MyCallBack;
import com.example.commonlibrary.utils.DensityUtil;
import com.example.commonlibrary.utils.ScreenUtil;
import com.example.commonlibrary.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;
import com.ymnet.killbackground.QihooSystemUtil;
import com.ymnet.killbackground.Utilities;
import com.ymnet.killbackground.customlistener.MyViewPropertyAnimatorListener;
import com.ymnet.killbackground.presenter.CleanPresenter;
import com.ymnet.killbackground.presenter.CleanPresenterImpl;
import com.ymnet.killbackground.retrofitservice.RetrofitService;
import com.ymnet.killbackground.retrofitservice.bean.FolderLodingInfo;
import com.ymnet.killbackground.view.customwidget.Wheel;
import com.ymnet.onekeyclean.NotifyService;
import com.ymnet.onekeyclean.R;
import com.ymnet.update.DownLoadFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Response;

import static android.text.format.Formatter.formatFileSize;
import static com.example.commonlibrary.systemmanager.SystemMemory.getAvailMemorySize;
import static com.example.commonlibrary.systemmanager.SystemMemory.getTotalMemorySize;

public class CleanActivity extends Activity implements CleanView {

    private static final String TAG = "CleanActivity";
    private ImageView      mRotateImage;
    private ObjectAnimator mOa1;
    private TextView       mMemoryInfo;
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
    private boolean isFirst = true;

    private boolean updateMemoryInfoFlag = true;
    private boolean        valueChange;
    private ImageView      mDetermine;
    private Button         mMoreFunction;
    private RelativeLayout mRelativeLayout;
    private CleanPresenter mCleanPresenter;
    private int            mCount;
    private int            mUsedMemory;
    private Animation      mAnimation;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //数字动态显示清理量
                case 0:
                    if (mCount >= 0 && valueChange) {
                        mMemoryInfo.setText("" + (mUsedMemory + mCount--) + "%");
                        mHandler.sendEmptyMessageDelayed(0, 200);
                        Log.d(TAG, "handleMessage: " + (mUsedMemory + "  " + mCount));
                    } else {
                        mMemoryInfo.setText("" + mUsedMemory + "%");
                    }
                    break;
                case 1:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mCleanPresenter.killAll(CleanActivity.this, true);
                        }
                    }).start();

                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean);

        //启动常驻通知栏
        startNotification();

        mCleanPresenter = new CleanPresenterImpl(this);
        initView();
        initData();
        QihooSystemUtil.openAllPermission(getApplicationContext(), "com.ymnet.apphelper");
        MobclickAgent.setScenarioType(getApplicationContext(), MobclickAgent.EScenarioType.E_UM_NORMAL);

        //参数的转变
       /* Map<String, String> params = ConvertParamsUtils.getInstatnce().getParamsThree("floderId",String.valueOf(1)
                ,"pageSize",String.valueOf(1),"pageNumber",String.valueOf(32));*/
        //getData(params);
    }

    private void startNotification() {
        Intent service = new Intent(this, NotifyService.class);
        startService(service);
        ToastUtil.showShort(this, "广播:服务已启动");
    }

    private void getData(final Map<String, String> params) {
        RetrofitService.getInstance().githubApi.createFolderMapTwo(params).enqueue(new MyCallBack<BaseCallModel<List<FolderLodingInfo>>>() {

            @Override
            protected void ertryConnection() {
                //递归的回调接口，再次连接接口请求
                getData(params);
            }

            @Override
            public void onSucess(Response<BaseCallModel<List<FolderLodingInfo>>> response) {
                //得到数据的处理
            }

            @Override
            public void onFailure(String message) {
                //处理失败的界面操作
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);

        Log.d(TAG, "onResume: ");
        Log.i("Tagg", "ggg");

        mIncreaseDate = 5;

        if (isFirst) {
            isFirst = false;
            //1.球转动,圆环转动 过程当中清理缓存;
            mAnimation = AnimationUtils.loadAnimation(CleanActivity.this, R.anim.clean_anim);
            mAnimation.setAnimationListener(new Animation.AnimationListener() {

                public long mNowAvailMemorySize;

                @Override
                public void onAnimationStart(Animation animation) {
                    //球转动
                    mOa1 = ObjectAnimator.ofFloat(mRotateImageInside, "rotation", 0, -8000f);
                    mOa1.setInterpolator(new LinearInterpolator());
                    mOa1.setDuration(15000);
                    mOa1.start();

                    mHandler.sendEmptyMessage(1);

                    mMemoryInfo.setVisibility(View.VISIBLE);

                    mBeforeAvailMemorySize = getAvailMemorySize(CleanActivity.this);
                    //吸入软件整体动画结合展示(个个软件图标暂未展示)
                    mWheel = (Wheel) findViewById(R.id.wheel_iv);
                    mWheel.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mWheel.setVisibility(View.VISIBLE);
                        }
                    }, 300);

                }

                @Override
                public void onAnimationEnd(final Animation animation) {

                    //Toast显示清理内存大小
                    mNowAvailMemorySize = getAvailMemorySize(CleanActivity.this);
                    mSize = mBeforeAvailMemorySize - mNowAvailMemorySize;
                    Log.d(TAG, "onAnimationEnd: mSize: " + mSize);

                    startStaticApp(getApplicationContext());
                    DownLoadFactory.getInstance().getInsideInterface().updateApp(CleanActivity.this);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            mRotateImage.setAnimation(mAnimation);
            mAnimation.start();

            mRepeatTime = 1;
            //            mRepeatTotalTime = (int) animation.getDuration() / UPDATE_SNAP_TIME - 3;
            /*mRepeatTotalTime = 3000 / UPDATE_SNAP_TIME - 3;
            mHandler.postDelayed(mUpdateMemoryInfo, UPDATE_SNAP_TIME);*/

        }
    }

    /**
     * 展开动画
     */
    private void openAnimation() {
        //dp转px
        int dp92 = DensityUtil.dp2px(this, 92);

        ViewCompat.animate(mRelativeLayout).translationX(-dp92).setDuration(500).setListener(new MyViewPropertyAnimatorListener() {
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
        mRotateImageInside = (ImageView) findViewById(R.id.imageView_within1);
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

    /*private Runnable mUpdateMemoryInfo = new Runnable() {
        @Override
        public void run() {
            if (mRepeatTime < mRepeatTotalTime) {
                updateMemoryInfo();
                mHandler.postDelayed(mUpdateMemoryInfo, UPDATE_SNAP_TIME);
            }
        }
    };*/

    private void updateMemoryInfo() {

        if (updateMemoryInfoFlag) {
            updateMemoryInfoFlag = false;

            mUsedMemory = getUsedMemoryRate();
            if (mUsedMemory <= mBeforeUsedMemoryRate) {
                mUsedMemory = mBeforeUsedMemoryRate;
            } else {
                mIncreaseDate = mIncreaseDate + mUsedMemory - mBeforeUsedMemoryRate;
                Log.d(TAG, "updateMemoryInfo:mIncreaseDate: " + mIncreaseDate);
                mBeforeUsedMemoryRate = mUsedMemory;
            }

            //动态显示数值
            mCount = mIncreaseDate;
            mHandler.sendEmptyMessageDelayed(0, 300);
        }

    }

    /**
     * 获取可用内存百分比
     */
    private int getUsedMemoryRate() {
        return 100 - (int) (100 * ((float) getAvailMemorySize(CleanActivity.this) / mTotalMemory));
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
            } else if (Utilities.isAppInstalled(context, "com.android.ramcleaner")) {
                Intent intent = new Intent();
                intent.setClassName("com.android.ramcleaner", "com.ymnet.apphelper.AppHelperActivityText");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 吸入应用动画
     *
     * @param v      吸入应用控件
     * @param time   延时展示时间
     * @param isLast 吸入动画的最后一个
     */
    public void playAnimation(final ImageView v, int time, boolean isLast) {

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        final int width = dm.widthPixels;
        final int height = dm.heightPixels;

        //位移量
        final float transX = -v.getX() - v.getMeasuredWidth() / 2 + width / 2;
        float transY;
        //根据设备是否具备permanentMenu键来确定是否有软导航栏-来设值位移量Y
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (ScreenUtil.hasSoftKeys(wm)) {
                transY = -v.getY() - v.getMeasuredHeight() / 2 + (height + ScreenUtil.getNavigationHeight(this)) / 2/* - ScreenUtil.getStatusHeight(this)*/;
            } else {
                transY = -v.getY() - v.getMeasuredHeight() / 2 + height / 2/* - ScreenUtil.getStatusHeight(this)*/;
            }
            showAnimation(v, time, transX, transY, isLast);
        }

    }

    private void showAnimation(final ImageView v, int time, final float transX, final float transY, final boolean isLast) {

        updateMemoryInfo();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                v.setVisibility(View.VISIBLE);

                if (isLast) {
                    ViewCompat.animate(v).scaleX(0.3f).scaleY(0.3f).alpha(0.3f).translationX(transX).translationY(transY).setDuration(450).setListener(new MyViewPropertyAnimatorListener() {

                        @Override
                        public void onAnimationStart(View view) {
                            super.onAnimationStart(view);
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mRotateImage.clearAnimation();
                                    mOa1.cancel();
                                }
                            }, 1000);

                        }

                        @Override
                        public void onAnimationEnd(View view) {
                            super.onAnimationEnd(view);
                            v.setVisibility(View.GONE);

                            //√动画
                            bingoAnimation(false);

                        }

                    }).start();
                } else {
                    ViewCompat.animate(v).scaleX(0.3f).scaleY(0.3f).alpha(0.3f).translationX(transX).translationY(transY).setDuration(450).setListener(new MyViewPropertyAnimatorListener() {
                        @Override
                        public void onAnimationEnd(View view) {
                            super.onAnimationEnd(view);
                            v.setVisibility(View.GONE);
                        }

                    }).start();
                }


            }
        }, time);
    }

    private void bingoAnimation(final Boolean isBest) {
        //对勾动画开启
        //展开动画
        long mNowAvailMemorySize;

        mRotateImage.setVisibility(View.INVISIBLE);
        mMemoryInfo.setVisibility(View.INVISIBLE);

        mDetermine.setScaleX(0);
        mDetermine.setScaleY(0);
        mDetermine.setVisibility(View.VISIBLE);
        int time;
        if (isBest) {
            time = 800;
        } else {
            time = 300;
        }
        //√展示动画
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ViewCompat.animate(mDetermine).scaleX(1).scaleY(1).setDuration(500).setListener(new MyViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationStart(View view) {
                        super.onAnimationStart(view);
                        Log.i(TAG, "onAnimationStart: " + SystemClock.currentThreadTimeMillis());
                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        super.onAnimationEnd(view);
                        //展开动画
                        openAnimation();

                        if (isBest) {
                            //停止旋转动画
                            if (mRotateImage.getAnimation() != null) {
                                mRotateImage.clearAnimation();
                                mOa1.cancel();
                            }
                        }

                    }
                }).start();
            }
        }, time);

        //Toast显示清理内存大小
        mNowAvailMemorySize = getAvailMemorySize(CleanActivity.this);
        mSize = mBeforeAvailMemorySize - mNowAvailMemorySize;
        Log.d(TAG, "onAnimationEnd: mSize: " + mSize);

        startStaticApp(getApplicationContext());
        DownLoadFactory.getInstance().getInsideInterface().updateApp(CleanActivity.this);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void getIconAndShow(final long cleanMem) {

        final Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        final int count = cleanAppLists.size();
        final PackageManager packageManager = this.getPackageManager();
        final List<ResolveInfo> appList = packageManager.queryIntentActivities(intent, 0);

        runOnUiThread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void run() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 1; i < count; i++) {
                            cleanAppLists.get(i).setImageDrawable(appList.get(appList.size() - i - 1).loadIcon(packageManager));
                            if (i == count - 1) {
                                playAnimation(cleanAppLists.get(i), 100 * i, true);
                            } else {
                                playAnimation(cleanAppLists.get(i), 100 * i, false);
                            }
                            //toast展示为用户清理的内存
                            String sAgeFormat = CleanActivity.this.getResources().getString(R.string.toast_clean_result);
                            String content = String.format(sAgeFormat, formatFileSize(CleanActivity.this, cleanMem), mIncreaseDate);
                            showToast(content);
                        }
                    }
                }, 400);
            }
        });

    }

    @Override
    public void bestState(final boolean isBest) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "run: " + SystemClock.currentThreadTimeMillis());
                bingoAnimation(isBest);
            }
        });
    }

    @Override
    public void showToast(String content) {
        showToast = content;
    }

    @Override
    public void isValueChang(boolean valueChange) {
        this.valueChange = valueChange;
    }

}