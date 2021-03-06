package com.ymnet.killbackground.view;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewCompat;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.commonlibrary.utils.ConvertParamsUtils;
import com.example.commonlibrary.utils.DensityUtil;
import com.example.commonlibrary.utils.ScreenUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.umeng.analytics.MobclickAgent;
import com.ymnet.killbackground.QihooSystemUtil;
import com.ymnet.killbackground.Utilities;
import com.ymnet.killbackground.customlistener.MyViewPropertyAnimatorListener;
import com.ymnet.killbackground.download.PushManager;
import com.ymnet.killbackground.model.bean.CleanEntrance;
import com.ymnet.killbackground.presenter.CleanPresenter;
import com.ymnet.killbackground.presenter.CleanPresenterImpl;
import com.ymnet.killbackground.utils.Run;
import com.ymnet.killbackground.view.customwidget.CustomDialog;
import com.ymnet.killbackground.view.customwidget.Wheel;
import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.home.HomeActivity;
import com.ymnet.onekeyclean.cleanmore.notification.NotifyService;
import com.ymnet.onekeyclean.cleanmore.utils.C;
import com.ymnet.onekeyclean.cleanmore.utils.OnekeyField;
import com.ymnet.onekeyclean.cleanmore.utils.SharedPreferencesUtil;
import com.ymnet.onekeyclean.cleanmore.web.WebHtmlActivity;
import com.ymnet.retrofit2service.RetrofitService;
import com.ymnet.update.DownLoadFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Response;

import static android.R.attr.order;
import static android.text.format.Formatter.formatFileSize;
import static com.example.commonlibrary.utils.SystemMemory.getAvailMemorySize;
import static com.example.commonlibrary.utils.SystemMemory.getTotalMemorySize;


import static com.example.commonlibrary.utils.SystemMemory.getAvailMemorySize;
import static com.example.commonlibrary.utils.SystemMemory.getTotalMemorySize;

public class CleanActivity extends Activity implements CleanView {
    private static final String TAG = "CleanActivity";

    private ImageView            mRotateImage;
    private ObjectAnimator       mOa1;
    private TextView             mMemoryInfo;
    private ImageView            mRotateImageInside;
    private ArrayList<ImageView> cleanAppLists;
    private Wheel                mWheel;
    private String               showToast;
    private boolean              valueChange;
    private ImageView            mDetermine;
    private TextView             mMoreFunction;
    private RelativeLayout       mRelativeLayout;
    private CleanPresenter       mCleanPresenter;
    private int                  mCount;
    private int                  mUsedMemory;
    private Animation            mAnimation;
    private boolean isFirst      = true;
    private long    mTotalMemory = 0;
    private int temp;
    private Random mR = new Random();

    private Handler mHandler = new Handler(C.get().getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //数字动态显示清理量
                case 0:
                    if (mCount >= 0 && valueChange) {
                        if (temp < mCount) {
                            mMemoryInfo.setText("" + (mUsedMemory - ++temp) + "%");
                            mHandler.sendEmptyMessageDelayed(0, 800 / mCount);
                        }
                        Log.d(TAG, "handleMessage: " + (mUsedMemory + "  " + mCount));
                    } else {
                        mMemoryInfo.setText("" + mUsedMemory + "%");
                    }
                    break;
                case 1:
                    Run.onSub(new Runnable() {
                        @Override
                        public void run() {
                            mCleanPresenter.killAll(CleanActivity.this, true);
                        }
                    });
                    break;
                case 2://清理前显示的数值
                    mMemoryInfo.setText("" + mUsedMemory + "%");
                    break;
                case 3://数值跳动
                    mMemoryInfo.setText("" + mR.nextInt(100) + "%");
                    mHandler.sendEmptyMessageDelayed(3, 80);
                    break;
            }
        }
    };

    private View              mRl_more_function;
    private ImageView         mArrow;
    private AnimationDrawable mAnimationDrawable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean);
        mCleanPresenter = new CleanPresenterImpl(this);
        String onekeyclean = getIntent().getStringExtra(OnekeyField.ONEKEYCLEAN);
        String statistic_id = getIntent().getStringExtra(OnekeyField.STATISTICS_KEY);
        if (onekeyclean != null) {
            Log.d(TAG, statistic_id);
            Map<String, String> m = new HashMap<>();
            m.put(OnekeyField.ONEKEYCLEAN, "手机加速");
            MobclickAgent.onEvent(this, statistic_id, m);
        }
        initView();
        initData();
        QihooSystemUtil.openAllPermission(getApplicationContext(), "com.ymnet.apphelper");
        MobclickAgent.setScenarioType(getApplicationContext(), MobclickAgent.EScenarioType.E_UM_NORMAL);

    }

    @Override
    protected void onStart() {
        super.onStart();
        //启动常驻通知栏
        startNotification();
    }

    private void startNotification() {
        Intent service = new Intent(this, NotifyService.class);
        startService(service);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);

        if (isFirst) {
            //1.球转动,圆环转动 过程当中清理缓存;
            mAnimation = AnimationUtils.loadAnimation(CleanActivity.this, R.anim.clean_anim);
            mAnimation.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                    //球转动
                    mOa1 = ObjectAnimator.ofFloat(mRotateImageInside, "rotation", 0, -8000f);
                    mOa1.setInterpolator(new LinearInterpolator());
                    mOa1.setDuration(15000);
                    mOa1.start();
                    if (isFirst) {
                        mHandler.sendEmptyMessage(1);
                        isFirst = false;
                    }
                    mMemoryInfo.setVisibility(View.VISIBLE);
                    mUsedMemory = getUsedMemoryRate();
                    //                    mMemoryInfo.setText("" + mUsedMemory + "%");
                    //                    mHandler.sendEmptyMessageDelayed(2, 100);
                    //开始数字跳动
                    mHandler.sendEmptyMessage(3);
                    //                    mHandler.sendEmptyMessage(2);

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

                    startStaticApp(getApplicationContext());
                    DownLoadFactory.getInstance().getInsideInterface().umUpdateApp(C.get());
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            mRotateImage.setAnimation(mAnimation);
            mAnimation.start();
        }
    }

    /**
     * 展开动画
     */
    private void openAnimation() {
        //dp转px
        int dp92 = DensityUtil.dp2px(this, 112);

        ViewCompat.animate(mRelativeLayout).translationX(-dp92).setDuration(500).setListener(new MyViewPropertyAnimatorListener() {
            @Override
            public void onAnimationStart(View view) {
                super.onAnimationStart(view);
                //按钮展示动画
                mRl_more_function.setScaleX(0);
                mRl_more_function.setVisibility(View.VISIBLE);
                if (HomeActivity.getInstance().isVisible) {
                    mArrow.setVisibility(View.INVISIBLE);
                } else {
                    mArrow.setVisibility(View.VISIBLE);
                }
//                gifAnim(mArrow);
                ViewCompat.animate(mRl_more_function).scaleX(1).setDuration(500).start();

                mMoreFunction.setText(showToast);
               /*
                mDataList.add(showToast);
                mDataList.add("要想手机更快，立刻深度清理");
                mSuv_more_function.setData(mDataList);
                mSuv_more_function.setTimer(2000);
                mSuv_more_function.start();*/
            }

            @Override
            public void onAnimationEnd(View view) {
                super.onAnimationEnd(view);
                //关闭动画
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //                        mSuv_more_function.stop();
                        finish();
                    }
                }, 4000);
            }
        }).start();
    }

    private void gifAnim(ImageView imageView) {
        mAnimationDrawable = (AnimationDrawable) imageView.getDrawable();
        if (mAnimationDrawable.isRunning()) {
            mAnimationDrawable.stop();
        }
        mAnimationDrawable.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        /*if (mAnimationDrawable != null) {
            mAnimationDrawable.stop();
        }*/
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

        mRl_more_function = findViewById(R.id.rl_more_function);
        mMoreFunction = (TextView) findViewById(R.id.btn_more_function);
        mArrow = (ImageView) findViewById(R.id.iv_arrow);
        mRl_more_function.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CleanActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                //ToastUtil.showShort(CleanActivity.this, "更多功能开发中...");
            }
        });
        /*mSuv_more_function = (MainScrollUpAdvertisementView) findViewById(R.id.suv_more_function);
        initSUV();*/
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

    private void initSUV() {
        /*mSuv_more_function.setTextSize(15);
        mSuv_more_function
                .setOnItemClickListener(new BaseAutoScrollUpTextView.OnItemClickListener() {

                    @Override
                    public void onItemClick(int position) {
                        Toast.makeText(C.get(),
                                "点击了第" + position + "个广告条", Toast.LENGTH_SHORT)
                                .show();
                    }
                });*/

    }

    private void initData() {
        mTotalMemory = getTotalMemorySize(CleanActivity.this);
        DownLoadFactory.getInstance().init(C.get(), null, PushManager.getInstance());
        PushManager.getInstance().init(getApplicationContext());//dont't remove


        /*AdvertisementObject advertisementObject = new AdvertisementObject();
        advertisementObject.info = "踏青零食上京东，百万零食1元秒";
        mDataList.add(advertisementObject);
        advertisementObject = new AdvertisementObject();
        advertisementObject.info = "看老刘中国行，满129减50！";
        mDataList.add(advertisementObject);
        advertisementObject = new AdvertisementObject();
        advertisementObject.info = "高姿CC霜全渠道新品首发，领券199减50，点击查看";
        mDataList.add(advertisementObject);*/

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

        mRotateImage.setVisibility(View.INVISIBLE);
        mDetermine.setScaleX(0);
        mDetermine.setScaleY(0);
        mDetermine.setVisibility(View.VISIBLE);
        int time;
        if (isBest) {
            //显示当前值
            mHandler.removeMessages(3);
            mHandler.sendEmptyMessage(2);
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
                        mMemoryInfo.setVisibility(View.INVISIBLE);
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

        startStaticApp(getApplicationContext());
        DownLoadFactory.getInstance().getInsideInterface().umUpdateApp(C.get());
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void getIconAndShow(final long cleanMem) {

        runOnUiThread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void run() {

                final Intent intent = new Intent(Intent.ACTION_MAIN, null);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                final int count = cleanAppLists.size();
                final PackageManager packageManager = CleanActivity.this.getPackageManager();
                final List<ResolveInfo> appList = packageManager.queryIntentActivities(intent, 0);

                //当前手机剩余内存百分比
                //                mUsedMemory = getUsedMemoryRate();

                mCount = (int) (Math.abs(cleanMem * 100) / mTotalMemory + 0.5f);
                Log.d(TAG, "onAnimationEnd: mSize: " + "/内存总量:" + mTotalMemory + "/清理量:" + cleanMem + "/清理百分比:" + mCount);
                if (mCount == 0) {
                    mCount = 3;
                }
                Log.d(TAG, "run: 清理百分比 " + mCount);
                //停止数字跳动,显示正确值
                mHandler.removeMessages(3);
                mHandler.sendEmptyMessage(2);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 1; i < count; i++) {
                            cleanAppLists.get(i).setImageDrawable(appList.get(appList.size() - i - 1).loadIcon(packageManager));
                            if (i == count - 1) {
                                playAnimation(cleanAppLists.get(i), 120 * i, true);
                            } else {
                                playAnimation(cleanAppLists.get(i), 120 * i, false);
                            }
                            //toast展示为用户清理的内存

                            String sAgeFormat = CleanActivity.this.getResources().getString(R.string.toast_clean_result);
                            //                            String content = String.format(sAgeFormat, formatFileSize(CleanActivity.this, cleanMem), mCount);
                            String content = String.format(sAgeFormat, mCount);
                            showToast(content);

                        }

                        mHandler.sendEmptyMessageDelayed(0, 300);
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