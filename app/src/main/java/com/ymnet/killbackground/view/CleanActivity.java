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
import android.support.v4.view.ViewPropertyAnimatorListener;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.commonlibrary.utils.ConvertParamsUtils;
import com.example.commonlibrary.utils.DensityUtil;
import com.example.commonlibrary.utils.ScreenUtil;
import com.example.commonlibrary.utils.ToastUtil;
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
import com.ymnet.onekeyclean.cleanmore.HomeActivity;
import com.ymnet.onekeyclean.cleanmore.notification.NotifyService;
import com.ymnet.onekeyclean.cleanmore.utils.C;
import com.ymnet.onekeyclean.cleanmore.utils.OnekeyField;
import com.ymnet.onekeyclean.cleanmore.utils.SharedPreferencesUtil;
import com.ymnet.onekeyclean.cleanmore.utils.StatisticMob;
import com.ymnet.onekeyclean.cleanmore.web.WebHtmlActivity;
import com.ymnet.retrofit2service.RetrofitService;
import com.ymnet.update.DownLoadFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.R.attr.order;
import static android.text.format.Formatter.formatFileSize;
import static com.example.commonlibrary.utils.SystemMemory.getAvailMemorySize;
import static com.example.commonlibrary.utils.SystemMemory.getTotalMemorySize;

public class CleanActivity extends Activity implements CleanView, View.OnClickListener {

    private static final String TAG                             = "CleanActivity";
    private static final String CLEANACTIVITY_NET_PREFERENCES   = "cleanactivity_net_preferences";
    private static final String CLEANACTIVITY_ORDER_PREFERENCES = "cleanactivity_order_preferences";
    private static final String NET_DATA                        = "net_data";
    private static final String NET_ORDER                       = "net_order";
    private ImageView            mRotateImage;
    private ObjectAnimator       mOa1;
    private TextView             mMemoryInfo;
    private ImageView            mRotateImageInside;
    private ArrayList<ImageView> cleanAppLists;
    private Wheel                mWheel;
    private String               showToast;
    private boolean              valueChange;
    private ImageView            mDetermine;
    private Button               mMoreFunction;
    private RelativeLayout       mRelativeLayout;
    private CleanPresenter       mCleanPresenter;
    private int                  mCount;
    private int                  mUsedMemory;
    private Animation            mAnimation;
    private boolean isFirst      = true;
    private long    mTotalMemory = 0;
    private int temp;
    private Random mR = new Random();
    private CustomDialog   mCustomDialog;
    private RelativeLayout mRl_clean_result;
    private RelativeLayout mRl_leaninto_home;
    private int            mLayoutType;
    private static final int     LAYOUT_DEFAULT  = 0;
    private static final int     LAYOUT_NEWS     = 1;
    private static final int     LAYOUT_DOWNLOAD = 2;
    private              Handler mHandler        = new Handler(C.get().getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //数字动态显示清理量
                case 0:
                    if (mCount >= 0 && valueChange) {
                        if (temp < mCount) {
                            mMemoryInfo.setText("" + (mUsedMemory - ++temp) + "%");
                            mHandler.sendEmptyMessageDelayed(0, 200);
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
    private TextView mTv_clean_result;
    private int      showPosition;
    private boolean mBingoVisible;
    private Spanned mContent;
    private boolean mIsBest;
    private View mRl_morefunction;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean);
        mCleanPresenter = new CleanPresenterImpl(this);

        String onekeyclean = getIntent().getStringExtra(OnekeyField.ONEKEYCLEAN);
        String statistic_home_id = getIntent().getStringExtra(StatisticMob.STATISTIC_HOME_ID);
        String statistic_id = getIntent().getStringExtra(StatisticMob.STATISTIC_ID);
        Map<String, String> m = new HashMap<>();
        m.put(OnekeyField.ONEKEYCLEAN, "手机加速");
        if (onekeyclean != null) {
            if (onekeyclean.equals("home")) {
                MobclickAgent.onEvent(this, statistic_home_id, m);
            } else if (onekeyclean.equals("notifymanager")){
                MobclickAgent.onEvent(this, statistic_id, m);
            }
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
                    DownLoadFactory.getInstance().getInsideInterface().updateApp();
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
        DownLoadFactory.getInstance().init(this, null, PushManager.getInstance());
        PushManager.getInstance().init(getApplicationContext());//dont't remove
        initNetData();
    }

    private List<CleanEntrance.DataBean> mNetList = new ArrayList<>();// TODO: 2017/6/26 0026 添加数据

    private void initNetData() {
        /**
         * 1.加速球点击时,网络请求数据
         * 2.网络数据为空:展示界面时展示默认布局
         *   网络数据有 : 判断数据是否和之前的一致 :1.一致-展示的数据为sharedperference存储顺序位置position
         *                                       2.不一致,删除旧数据,存入新数据,展示的数据为position=0开始执行
         */
        //http://zm.youmeng.com/Api/App/getClearRecommend
        Map<String, String> infosPamarms = ConvertParamsUtils.getInstatnce().getParamsOne("", "");
        RetrofitService.getInstance().githubApi.createClearRecommend(infosPamarms).enqueue(new Callback<CleanEntrance>() {
            @Override
            public void onResponse(Call<CleanEntrance> call, Response<CleanEntrance> response) {
                int type = response.body().getData().get(0).getType();
                if (type == 1) {
                    mLayoutType = LAYOUT_NEWS;
                } else if (type == 2) {
                    mLayoutType = LAYOUT_DOWNLOAD;
                }
                mNetList = response.body().getData();
                matchSP(true);
            }

            @Override
            public void onFailure(Call<CleanEntrance> call, Throwable t) {
                //网络获取数据空/失败
                mLayoutType = LAYOUT_DEFAULT;
                matchSP(false);
                Log.d(TAG, "网络获取失败");
            }
        });

    }

    private void matchSP(boolean hasNet) {
        //获取sp文件,如果无-建 , 有-一致 ...
        List<CleanEntrance.DataBean> dataList = getDataList(NET_DATA);
        if (dataList == null) {//无缓存
            if (hasNet) {
                setDataList(NET_DATA, mNetList);
                showPosition = 0;
                SharedPreferencesUtil.putIntToSharePreferences(CLEANACTIVITY_ORDER_PREFERENCES, NET_ORDER, showPosition);
            } else {
                //默认布局
            }
        } else {//有缓存
            if (hasNet) {//有缓存,有网络
                Log.d(TAG, "a-----"+dataList.toString());
                Log.d(TAG, "b-----"+mNetList.toString());
                if (dataList.toString().equals(mNetList.toString())) {//有缓存,有网络,数据类型一致
                    int order = SharedPreferencesUtil.getIntFromSharePreferences(CLEANACTIVITY_ORDER_PREFERENCES, NET_ORDER, 0);
                    //展示order+1条数据,并存入sp
                    if (dataList.size() > order + 1) {
                        showPosition = order + 1;
                    } else {
                        showPosition = 0;
                    }
                } else {//有缓存,有网络,数据类型不一致
                    //接着展示第order+1条数据
                    if (mNetList.size() > order + 1) {
                        showPosition = order + 1;
                    } else {
                        showPosition = 0;
                    }
                    setDataList(NET_DATA, mNetList);
                }

            } else {//有缓存,无网络

            }

            SharedPreferencesUtil.putIntToSharePreferences(CLEANACTIVITY_ORDER_PREFERENCES, NET_ORDER, showPosition);

        }
    }

    public List<CleanEntrance.DataBean> getDataList(String tag) {

        List<CleanEntrance.DataBean> list = new ArrayList<>();
        String jsonString = SharedPreferencesUtil.getStringFromSharePreferences(CLEANACTIVITY_NET_PREFERENCES, tag, null);
        if (null == jsonString) {
            return null;
        }
        try {
            Gson gson = new Gson();
            JsonArray arry = new JsonParser().parse(jsonString).getAsJsonArray();
            for (JsonElement jsonElement : arry) {
                list.add(gson.fromJson(jsonElement, CleanEntrance.DataBean.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private void setDataList(String tag, List list) {
        if (null == list || list.size() <= 0)
            return;

        Gson gson = new Gson();
        //转换成json数据，再保存
        String strJson = gson.toJson(list);
        SharedPreferencesUtil.putStringToSharePreferences(CLEANACTIVITY_NET_PREFERENCES, tag, strJson);
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
                       /* //展开动画
                        openAnimation();*/
                        //清理球消失
                        ballGone();
                        //弹出清理结束界面
                        cleanResult();

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
        DownLoadFactory.getInstance().getInsideInterface().updateApp();
    }

    /**
     * 加速球消失动画
     */
    private void ballGone() {
        ViewCompat.animate(mRelativeLayout).scaleX(0).scaleY(0).setDuration(500).setListener(new MyViewPropertyAnimatorListener() {
            @Override
            public void onAnimationEnd(View view) {
                super.onAnimationEnd(view);
                mRelativeLayout.setVisibility(View.GONE);
            }
        }).start();
    }

    private void cleanResult() {
        //创建dialog
        initDialog();
    }

    private void initDialog() {

        if (mCustomDialog != null) {
            mCustomDialog.dismiss();
            mCustomDialog = null;
        }

        mCustomDialog = new CustomDialog(this);
        mCustomDialog.show();

        //根据请求的数据类型展示界面类型
        /**
         * 0.无网络数据:默认布局
         * 1.type==web:
         * 2.type==download:
         */
        if (this.mLayoutType == LAYOUT_DEFAULT) {
            //默认布局,不翻转
            mCustomDialog.getWindow().setContentView(R.layout.view_dialog_defalut);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finishDialogAndMyself();
                }
            }, 3000);
        } else {
            //有网络数据类型,翻转
            if (this.mLayoutType == LAYOUT_NEWS) {
                mCustomDialog.getWindow().setContentView(R.layout.view_dialog_news);
                TextView tv_foot = (TextView) mCustomDialog.findViewById(R.id.tv_foot);
                ImageView iv_foot = (ImageView) mCustomDialog.findViewById(R.id.iv_foot);
                tv_foot.setText(mNetList.get(showPosition).getTitle());
                Glide.with(C.get()).load(mNetList.get(showPosition).getIcon()).placeholder(R.drawable.pic_holder).error(R.drawable.pic_error).into(iv_foot);
            }
            if (this.mLayoutType == LAYOUT_DOWNLOAD) {
                //                mCustomDialog.getWindow().setContentView(R.layout.xxx);
                TextView tv_foot = (TextView) mCustomDialog.findViewById(R.id.tv_foot);
                //                tv_foot.setText(R.string.download_foot);
            }
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ViewCompat.animate(mRl_clean_result).rotationY(180).alpha(0).setDuration(500).setListener(new ViewPropertyAnimatorListener() {
                        @Override
                        public void onAnimationStart(View view) {
                            mRl_leaninto_home.setRotationY(-180);
                            mRl_leaninto_home.setVisibility(View.VISIBLE);
                            ViewCompat.animate(mRl_leaninto_home).rotationY(0).setDuration(500).start();
                        }

                        @Override
                        public void onAnimationEnd(View view) {
                            mRl_clean_result.setVisibility(View.GONE);
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finishDialogAndMyself();
                                }
                            }, 3000);
                        }

                        @Override
                        public void onAnimationCancel(View view) {

                        }
                    }).start();
                }
            }, 3000);
            ImageView arrow_foot = (ImageView) mCustomDialog.findViewById(R.id.iv_arrow_foot);
            gifAnim(arrow_foot);
        }

        mTv_clean_result = (TextView) mCustomDialog.findViewById(R.id.tv_clean_result);
        View iv_bingo = mCustomDialog.findViewById(R.id.iv_bingo);
        mRl_clean_result = (RelativeLayout) mCustomDialog.findViewById(R.id.rl_clean_result);
        mRl_leaninto_home = (RelativeLayout) mCustomDialog.findViewById(R.id.rl_leaninto_home);
        ImageView arrow_result = (ImageView) mCustomDialog.findViewById(R.id.iv_arrow_result);
        ImageView arrow_head2 = (ImageView) mCustomDialog.findViewById(R.id.iv_arrow_head2);
        gifAnim(arrow_result);
        gifAnim(arrow_head2);
        mRl_clean_result.setOnClickListener(this);
        mRl_leaninto_home.setOnClickListener(this);
        if (mLayoutType != LAYOUT_DEFAULT) {
            mRl_morefunction = mCustomDialog.findViewById(R.id.rl_morefunction);
            mRl_morefunction.setOnClickListener(this);
        }

        if (mIsBest) {
            mTv_clean_result.setText(showToast);
        } else {
            mTv_clean_result.setText(mContent);
        }

        if (mBingoVisible) {
            iv_bingo.setVisibility(View.VISIBLE);
        } else {
            iv_bingo.setVisibility(View.GONE);
        }
    }

    private void gifAnim(ImageView imageView) {
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
        if (animationDrawable.isRunning()) {
            animationDrawable.stop();
        }
        animationDrawable.start();
    }

    private void finishDialogAndMyself() {
        if (mCustomDialog != null) {
            mCustomDialog.dismiss();
            mCustomDialog = null;
        }
        finish();
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
                            //展示为用户清理的内存
                            Spanned spanned = Html.fromHtml(CleanActivity.this.getResources().getString(R.string.toast_clean_result, formatFileSize(CleanActivity.this, cleanMem), mCount));
                            mContent = spanned;
                        }

                        mHandler.sendEmptyMessageDelayed(0, 300);
                    }
                }, 400);
            }
        });

    }

    @Override
    public void bestState(final boolean isBest) {
        mIsBest = isBest;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "run: " + SystemClock.currentThreadTimeMillis());
                bingoAnimation(isBest);
                mBingoVisible = isBest;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_clean_result:
            case R.id.rl_leaninto_home:
                //结束本界面(动画),跳转
                finishDialogAndMyself();
                startActivity(new Intent(CleanActivity.this, HomeActivity.class));
                break;
            case R.id.rl_morefunction:
                //点击事件
                /**
                 * 1.type==web:跳转用浏览器打开web_url
                 * 2.type==download:跳转用浏览器打开news_url
                 */
                finishDialogAndMyself();
                Log.d(TAG, "mLayoutType:" + mLayoutType);
                if (mLayoutType == LAYOUT_DEFAULT) {
                    finishDialogAndMyself();
                    startActivity(new Intent(CleanActivity.this, HomeActivity.class));
                } else if (mLayoutType == LAYOUT_NEWS) {
                    Intent intent = new Intent(CleanActivity.this, WebHtmlActivity.class);
                    intent.putExtra("html", mNetList.get(showPosition).getUrl());
                    intent.putExtra("flag", 10);
                    intent.putExtra(OnekeyField.CLEAN_NEWS, "引导新闻");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                break;
        }
    }
}