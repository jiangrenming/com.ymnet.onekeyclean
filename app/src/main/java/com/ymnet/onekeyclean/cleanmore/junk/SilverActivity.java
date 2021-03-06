package com.ymnet.onekeyclean.cleanmore.junk;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.umeng.analytics.MobclickAgent;
import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.ImmersiveActivity;
import com.ymnet.onekeyclean.cleanmore.animation.TweenAnimationUtils;
import com.ymnet.onekeyclean.cleanmore.utils.CacheCleanUtil;
import com.ymnet.onekeyclean.cleanmore.constants.ByteConstants;
import com.ymnet.onekeyclean.cleanmore.constants.ScanState;
import com.ymnet.onekeyclean.cleanmore.constants.TimeConstants;
import com.ymnet.onekeyclean.cleanmore.datacenter.DataCenterObserver;
import com.ymnet.onekeyclean.cleanmore.fragment.CleanOverFragment;
import com.ymnet.onekeyclean.cleanmore.fragment.ScanningFragment;
import com.ymnet.onekeyclean.cleanmore.fragment.fragmentcontroller.SingleDisplayFragmentController;
import com.ymnet.onekeyclean.cleanmore.fragment.mainfragment.CleanFragmentInfo;
import com.ymnet.onekeyclean.cleanmore.home.HomeToolBarAD;
import com.ymnet.onekeyclean.cleanmore.junk.clearstrategy.ClearManager;
import com.ymnet.onekeyclean.cleanmore.junk.mode.CleaningFragment;
import com.ymnet.onekeyclean.cleanmore.junk.mode.InstalledAppAndRAM;
import com.ymnet.onekeyclean.cleanmore.junk.mode.JunkChild;
import com.ymnet.onekeyclean.cleanmore.junk.mode.JunkChildCache;
import com.ymnet.onekeyclean.cleanmore.junk.mode.JunkChildCacheOfChild;
import com.ymnet.onekeyclean.cleanmore.junk.mode.JunkGroup;
import com.ymnet.onekeyclean.cleanmore.service.BackgroundDoSomethingService;
import com.ymnet.onekeyclean.cleanmore.utils.C;
import com.ymnet.onekeyclean.cleanmore.utils.CleanSetSharedPreferences;
import com.ymnet.onekeyclean.cleanmore.utils.FormatUtils;
import com.ymnet.onekeyclean.cleanmore.utils.OnekeyField;
import com.ymnet.onekeyclean.cleanmore.utils.StatisticMob;
import com.ymnet.onekeyclean.cleanmore.utils.Util;
import com.ymnet.onekeyclean.cleanmore.web.JumpUtil;
import com.ymnet.retrofit2service.RetrofitService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.droidsonroids.gif.GifDrawable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SilverActivity extends ImmersiveActivity implements View.OnClickListener, ScanHelp.IScanResult, ScanFinishFragment.OnScanFinishFragmentInteractionListener, CleaningFragment.OnCleanFragmentEndListener {
    private String TAG = "SilverActivity";
    private RelativeLayout rl_page_title;
    private Button         btn_stop;
    private View           ani_view;
    private static final String scanningFragmentTag = "scanning";
    private ScanHelp  mScan;
    private Resources resources;
    private boolean needSave = false;

    private long               selectSize;
    private List<JunkGroup>    datas;
    /**
     * 必须用两个相同的动画，一个的话两个view颜色更新不同步
     */
    private TransitionDrawable blue2Green1;
    private TransitionDrawable green2Orange1;
    private TransitionDrawable orange2Red1;
    private TransitionDrawable blue2Green2;
    private TransitionDrawable green2Orange2;
    private TransitionDrawable orange2Red2;
    private TransitionDrawable currentDrawable;
    private int transitionAnimationDuration = 1000;
    private int from                        = -1;//1为来自管理,2为微信
    private boolean state;
    private  boolean saveScrollState =false;
    private SilverActivity newInstance;
    private ImageView mAdvertisement;

    public SilverActivity newInstance() {
        if (newInstance == null) {
            newInstance = new SilverActivity();
        }
        return newInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_silver);

        addActivity(this);

        String stringExtra = getIntent().getStringExtra(OnekeyField.ONEKEYCLEAN);
        String statistics_key = getIntent().getStringExtra(OnekeyField.STATISTICS_KEY);
        if (stringExtra != null) {
            Map<String, String> m = new HashMap<>();
            m.put(OnekeyField.ONEKEYCLEAN, stringExtra);
            MobclickAgent.onEvent(this, statistics_key, m);
        }
        //startactivityresult
        setResult(CleanFragmentInfo.ACTIVITY_RESULT_NO_CLEAN);
        resources = getResources();
        initDrawable();
        initView();
        initScanningFragment();

        initScan();
        from = getIntent().getIntExtra("from", -1);

    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        state = true;
    }

    public boolean getState() {
        return state;
    }
    public void setState(boolean b) {
        state = b;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (DataCenterObserver.get(C.get()).isRefreshCleanActivity()) {
            DataCenterObserver.get(C.get()).setRefreshCleanActivity(false);
            currentDrawable = null;
            rl_page_title.setBackgroundColor(resources.getColor(R.color.main_blue_new1));
            initScanningFragment();
            revertBtn();
            initScan();
        }
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onDestroy() {
        blue2Green1 = null;
        green2Orange1 = null;
        orange2Red1 = null;
        blue2Green2 = null;
        green2Orange2 = null;
        orange2Red2 = null;
        currentDrawable = null;
        try {
            if (mScan != null) {
                mScan.setRun(false);
                mScan.close();
                if (needSave) {
                    DataCenterObserver.get(C.get()).setJunkDataList(datas);
                    DataCenterObserver.get(C.get()).setLastScanTime(System.currentTimeMillis());
                }
            }
        } catch (Exception e) {
        }
        super.onDestroy();
    }

    /**
     * 初始化头部渐变动画
     */
    private void initDrawable() {
        blue2Green1 = (TransitionDrawable) resources.getDrawable(R.drawable.drawable_blue2green);
        blue2Green2 = (TransitionDrawable) resources.getDrawable(R.drawable.drawable_blue2green);

        green2Orange1 = (TransitionDrawable) resources.getDrawable(R.drawable.drawable_green2oragle);
        green2Orange2 = (TransitionDrawable) resources.getDrawable(R.drawable.drawable_green2oragle);

        orange2Red1 = (TransitionDrawable) resources.getDrawable(R.drawable.drawable_oragle2red);
        orange2Red2 = (TransitionDrawable) resources.getDrawable(R.drawable.drawable_oragle2red);
    }

    private void initScan() {

        if (checkHasCleanCache()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    selectSize = CleanOverFragment.HAS_CLEAN_CACHE;
                    startCleanOverActivity();
                }
            }, 1000);
        } else {
            mScan = ScanHelp.getInstance(this);
            Log.d("SilverActivity", "mScan.hashCode():" + mScan.hashCode());
            if (!mScan.isScanned()) {
                SQLiteDatabase db = new ClearManager(this).openClearDatabase();
                mScan.setDb(db);
                mScan.setiScanResult(this);
                mScan.setRun(true);
                mScan.startScan(false);
            } else {
                // TODO: 2017/6/15 0015  显示清理结果界面
                scanState(ScanState.SCAN_ALL_END);
            }

        }
    }

    private void initScanningFragment() {
        ScanningFragment scanningF = ScanningFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_content, scanningF, scanningFragmentTag).commit();
        //扫描动画
        // TODO: 2017/5/24 0024 删
        //        ani_view.setVisibility(View.VISIBLE);
        ani_view.setVisibility(View.GONE);
        TweenAnimationUtils.startScanTranslateAnimation(this, ani_view);
    }

    private ScanningFragment getScanningFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(scanningFragmentTag);
        if (fragment != null) {
            return (ScanningFragment) fragment;
        }
        return null;
    }

    private void initView() {

        rl_page_title = (RelativeLayout) findViewById(R.id.rl_page_title);
        btn_stop = (Button) findViewById(R.id.btn_stop);
        ani_view = findViewById(R.id.ani_view);
        TextView junk_title_txt = (TextView) findViewById(R.id.junk_title_txt);
        junk_title_txt.setOnClickListener(this);
        btn_stop.setOnClickListener(this);
        btn_stop.setTag("stop");

        initAdvertisement();
    }
    private void initAdvertisement() {
        mAdvertisement = (ImageView) findViewById(R.id.iv_clean_advertisement);
        requestData();
    }

    private void requestData() {
        Map<String, String> infosPamarms = com.example.commonlibrary.utils.ConvertParamsUtils.getInstatnce().getParamsOne("", "");

        RetrofitService.getInstance().githubApi.createHomeAD(infosPamarms).enqueue(new Callback<HomeToolBarAD>() {
            @Override
            public void onResponse(Call<HomeToolBarAD> call, Response<HomeToolBarAD> response) {
                if (response.raw().body() != null) {
                    HomeToolBarAD body = response.body();
                    String open_ad = null;
                    final String url;
                    String icon = null;
                    try {
                        open_ad = body.getData().getOpen_ad();
                        url = body.getData().getUrl();
                        icon = body.getData().getIcon();
                        String key = body.getData().getKey();
                        //如果获取的数据需要展示
                        if (open_ad.equals("on")) {
                            if (key.equals("bianxianmao")) {
                                mAdvertisement.setImageResource(R.drawable.bianxianmao);
                            } else {
                                String str = icon.toLowerCase();
                                showPic(str,icon);
                            }
                            mAdvertisement.setVisibility(View.VISIBLE);
                        } else {
                            mAdvertisement.setVisibility(View.GONE);
                        }
                        mAdvertisement.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Map<String, String> m = new HashMap<>();
                                m.put(OnekeyField.ONEKEYCLEAN, "垃圾清理ToolBar");
                                MobclickAgent.onEvent(C.get(), StatisticMob.STATISTIC_FLOATAD_ID, m);

                                JumpUtil.getInstance().unJumpAddress(C.get(), url, 10);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        MobclickAgent.reportError(C.get(), e.fillInStackTrace());
                        mAdvertisement.setVisibility(View.GONE);
                    }

                }
            }

            @Override
            public void onFailure(Call<HomeToolBarAD> call, Throwable t) {
                mAdvertisement.setVisibility(View.GONE);
            }
        });
    }
    public void showPic(String str, final String icon) {
        if ((str.endsWith("png") || str.endsWith("jpeg") || str.endsWith("bmp"))){
            Glide.with(C.get()).load(str).into(mAdvertisement);
        }else if(str.endsWith("gif")){
            new AsyncTask<Void, Void, GifDrawable>() {
                @Override
                protected GifDrawable doInBackground(Void... params) {
                    ByteBuffer buffer = null;
                    try {
                        URLConnection urlConnection = new URL(icon).openConnection();
                        urlConnection.connect();
                        final int contentLength = urlConnection.getContentLength();
                        if (contentLength < 0) {
                            throw new IOException("Content-Length not present");
                        }
                        buffer = ByteBuffer.allocateDirect(contentLength);
                        InputStream inputStream = urlConnection.getInputStream();
                        ReadableByteChannel channel = Channels.newChannel(inputStream);
                        while (buffer.remaining() > 0)
                            channel.read(buffer);
                        channel.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    GifDrawable gifDrawable = null;
                    try {
                        gifDrawable = new GifDrawable(buffer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return gifDrawable;
                }
                protected void onPostExecute(GifDrawable gifDrawable) {
                    super.onPostExecute(gifDrawable);
                    mAdvertisement.setImageDrawable(gifDrawable);
                }
            }.execute();
        }
    }
    private void revertBtn() {
        if (btn_stop != null) {
            btn_stop.setText(getString(R.string.bt_stop));
            btn_stop.setTextColor(resources.getColor(R.color.qr_scan_menu_back));
            btn_stop.setBackgroundResource(R.drawable.junk_bottom_btn_selector);
            btn_stop.setTag("stop");
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.junk_title_txt) {
//            this.finish();
            openHome(true);
        } else if (i == R.id.btn_stop) {
            if (v.getTag() == null) {
                return;
            }
            if ("stop".equals(v.getTag().toString())) {
                if (mScan != null && mScan.isRun()) {
                    mScan.setRun(false);
                }
                needSave = false;
            } else if ("scanFinish".equals(v.getTag().toString())) {
                ArrayList<JunkChild> cleanFragmentDatas = createCleanFragmentDatas();
                if (cleanFragmentDatas == null || cleanFragmentDatas.size() == 0) {
                    return;
                }
                selectSize = mScan.getTotalSelectSize();
                Log.d("SilverActivity", "selectSize:" + selectSize);
                CleaningFragment cleaningF = CleaningFragment.newInstance();
                cleaningF.setCleanFragmentData(cleanFragmentDatas, selectSize);
                getSupportFragmentManager().beginTransaction().replace(R.id.fl_content, cleaningF).commitAllowingStateLoss();
                needSave = false;
                btn_stop.setTag("cleaning");
            }

        }
    }

    @Override
    public void scanning(final String path) {
        if (isFinishing())
            return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ScanningFragment fragment = getScanningFragment();
                if (fragment != null) {
                    fragment.scanning(path);
                    setScanSize();
                }
            }
        });
    }

    @Override
    public void scanState(final int state) {

        if (isFinishing() || getSupportFragmentManager().isDestroyed()) {
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (state) {
                    case ScanState.SCAN_ALL_END:
                        ani_view.clearAnimation();
                        ani_view.setVisibility(View.GONE);
                        datas = mScan.getDatas();
                        if (datas == null || datas.size() == 0) {
                            startCleanOverActivity();
                        } else {
                            ScanFinishFragment scanFinishF = ScanFinishFragment.newInstance();
                            long dataSize = mScan.getTotalSize();
                            Log.d("SilverActivity", "dataSize:" + dataSize);
                            scanFinishF.setDatas(datas);
                            scanFinishF.setDataSize(dataSize);
                            getSupportFragmentManager().beginTransaction().replace(R.id.fl_content, scanFinishF).commitAllowingStateLoss();
                            updateBottomBtn();
                            needSave = true;
                            btn_stop.setTag("scanFinish");
                        }
                        break;
                    default:
                        ScanningFragment fragment = getScanningFragment();
                        if (fragment != null) {
                            getScanningFragment().scanState(state);
                        }
                        break;
                }
                setScanSize();
            }
        });

    }

    private void setScanSize() {
        if (mScan != null && !isFinishing()) {
            long size = mScan.getTotalSize();
            String[] sizeAndUnit = FormatUtils.getFileSizeAndUnit(size);
            ScanningFragment fragment = getScanningFragment();
            if (fragment != null) {
                fragment.setScanSize(sizeAndUnit);
                updateHeadColor(size);
            }
        }
    }

    @SuppressLint("NewApi")
    private void updateHeadColor(long size) {

        ScanningFragment fragment = getScanningFragment();
        if (fragment == null)
            return;
        if (android.os.Build.VERSION.SDK_INT >= 16) {
            if (size <= 10 * ByteConstants.MB) {
                // 绿色
                if (currentDrawable == null) {
                    currentDrawable = blue2Green1;
                    fragment.scanColor(blue2Green2, transitionAnimationDuration);
                    blue2Green1.startTransition(transitionAnimationDuration);
                }

            } else if (size <= 75 * ByteConstants.MB) {
                // 橙色
                if (currentDrawable == null || currentDrawable == blue2Green1) {
                    currentDrawable = green2Orange1;
                    fragment.scanColor(green2Orange1, transitionAnimationDuration);
                    //                    rl_page_title.setBackground(green2Orange2);
                    green2Orange2.startTransition(transitionAnimationDuration);
                }
            } else {
                // 红色
                if (currentDrawable == null || currentDrawable == green2Orange1 || currentDrawable == blue2Green1) {
                    currentDrawable = orange2Red1;
                    fragment.scanColor(orange2Red1, transitionAnimationDuration);
                    orange2Red2.startTransition(transitionAnimationDuration);
                }
            }
        } else {
            if (size <= 10 * ByteConstants.MB) {
                fragment.scanColor(resources.getColor(R.color.clean_bg_green));
            } else if (size <= 75 * ByteConstants.MB) {
                fragment.scanColor(resources.getColor(R.color.clean_bg_orange));
            } else {
                fragment.scanColor(resources.getColor(R.color.clean_bg_red));
            }
        }
    }

    /**
     * 更新清理按钮显示的大小
     */
    private void updateBottomBtn() {
        long size = mScan.getTotalSelectSize();
        String mString = resources.getString(R.string.btn_junk_clean) + Util.formatFileSizeToPic(size);
        btn_stop.setText(mString);
        btn_stop.setTextColor(resources.getColor(R.color.white));
        btn_stop.setBackgroundResource(R.drawable.junk_std_bottom_btn_selector);
    }

    /**
     * ScanFinish界面数据选中大小发生改变后的 回掉
     * 主要用来更新activity界面下面的清理按钮的显示
     */
    @Override
    public void callback() {
        updateBottomBtn();
    }

    /**
     * ScanFinish界面数据选中大小发生改变后的 回掉
     * 主要用来更新activity界面title的颜色
     */
    @Override
    public void updateTitleColor(int color) {
        if (rl_page_title != null && color != 0) {
            rl_page_title.clearAnimation();
            if (currentDrawable != null) {
                currentDrawable.mutate().clearColorFilter();
            }
            rl_page_title.setBackgroundColor(color);
        }
    }

    /**
     * 从Scan的结果数据源中生成适合CleanFragmeng 种listview的数据源
     */
    private ArrayList<JunkChild> createCleanFragmentDatas() {
        //内存加速
        List<JunkGroup> dataList = mScan.getDatas();
        ArrayList<JunkChild> data = new ArrayList<>();
        String ramClean = getString(R.string.header_ram);
        for (JunkGroup group : dataList) {
            if (ramClean.equals(group.getName())) {
                InstalledAppAndRAM child = new InstalledAppAndRAM();
                child.name = group.getName();
                child.size = mScan.getRAMSelectSize();
                if (child.size > 0) {
                    data.add(child);
                    continue;
                }
            }
            List<JunkChild> childrenItems = group.getChildrenItems();
            for (JunkChild itemChild : childrenItems) {
                if (itemChild instanceof JunkChildCache) {
                    JunkChildCache cache = (JunkChildCache) itemChild;
                    if (JunkChildCache.systemCachePackName.equals(cache.packageName)) {
                        if (cache.getSelect() == 1) {
                            data.add(cache);
                        }
                    } else {
                        if (cache.getSelect() == 1) {
                            data.add(cache);
                        } else {
                            List<JunkChildCacheOfChild> childOfChilds = cache.childCacheOfChild;
                            for (JunkChildCacheOfChild childOfChild : childOfChilds) {
                                if (childOfChild.getSelect() == 1) {
                                    data.add(cache);
                                    break;
                                }
                            }
                        }
                    }
                } else if (itemChild.getSelect() == 1) {
                    data.add(itemChild);
                }

            }
        }
        return data;
    }

    /**
     * CleaningFragment 清理完成回掉
     */
    @Override
    public void onCleanEndCallBack() {
        DataCenterObserver.get(C.get()).setCleanData(new CleanDataModeEvent(datas, selectSize));
        BackgroundDoSomethingService.startActionFoo(C.get(), "", "");
        startCleanOverActivity();
    }

    /**
     * CleanFragment回掉
     * 改变activity标题栏的颜色 跟listview头部颜色同步
     *
     * @param size
     */
    @Override
    public void onUpdateActivityTitleColor(long size) {
        if (size == 0) {
            // 蓝色
            rl_page_title.setBackgroundColor(resources.getColor(R.color.main_blue_new));
        } else if (size <= 10 * ByteConstants.MB) {
            // 绿色
            rl_page_title.setBackgroundColor(resources.getColor(R.color.clean_bg_green));
        } else if (size <= 75 * ByteConstants.MB) {
            // 橙色
            rl_page_title.setBackgroundColor(resources.getColor(R.color.clean_bg_orange));
        } else {
            // 红色
            rl_page_title.setBackgroundColor(resources.getColor(R.color.clean_bg_red));
        }
    }

    //清理结束
    private void startCleanOverActivity() {

        if (isFinishing() || getSupportFragmentManager().isDestroyed()) {
            return;
        }
        //startactivityresult
        setResult(CleanFragmentInfo.ACTIVITY_RESULT_CLEAN);

        if (View.VISIBLE == ani_view.getVisibility()) {
            ani_view.clearAnimation();
            ani_view.setVisibility(View.GONE);
        }
        btn_stop.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rl_page_title.setBackgroundResource(R.color.main_blue_new);
            }
        }, 100);
        CleanOverFragment cf = CleanOverFragment.newInstance(selectSize);
        SingleDisplayFragmentController fragmentController =
                new SingleDisplayFragmentController(getSupportFragmentManager(), R.id.fl_content);
        fragmentController.beginNewTransaction();
        fragmentController.changeDisplayFragment(cf);
        fragmentController.commit();

    }

    /**
     * 检查清理缓存 不通过的话直接做一秒钟的动画效果进去结果页面  显示未发现垃圾
     *
     * @return
     */
    private boolean checkHasCleanCache() {
        long lastTime = CacheCleanUtil.getLaseCleanDate(this, System.currentTimeMillis());
        boolean hasCache = CleanSetSharedPreferences.getLastSet(this, CleanSetSharedPreferences.CLEAN_RESULT_CACHE, false);
        boolean hasUpdate = DataCenterObserver.get(C.get()).isRefreshCleanActivity();
        /**
         *  最后的清理时间小于3分钟并且上次是全部清理的 并且3分钟内白名单项没有发生改变
         */
        if (lastTime <= 3 * TimeConstants.MINUTE && hasCache && !hasUpdate) {
            return true;
        } else {
            DataCenterObserver.get(C.get()).setRefreshCleanActivity(false);
            CleanSetSharedPreferences.setLastSet(this, CleanSetSharedPreferences.CLEAN_RESULT_CACHE, false);
        }
        return false;
    }

    public static class CleanDataModeEvent {
        public List<JunkGroup> datas;
        public long            selectSize;

        public CleanDataModeEvent(List<JunkGroup> datas, long selectSize) {
            this.datas = datas;
            this.selectSize = selectSize;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        openHome(true);
    }

    @Override
    public void finish() {
       super.finish();
    }
}
