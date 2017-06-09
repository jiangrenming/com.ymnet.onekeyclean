package com.ymnet.onekeyclean.cleanmore.junk;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.animation.TweenAnimationUtils;
import com.ymnet.onekeyclean.cleanmore.constants.ByteConstants;
import com.ymnet.onekeyclean.cleanmore.constants.ScanState;
import com.ymnet.onekeyclean.cleanmore.constants.TimeConstants;
import com.ymnet.onekeyclean.cleanmore.datacenter.DataCenterObserver;
import com.ymnet.onekeyclean.cleanmore.fragment.CleanOverFragment;
import com.ymnet.onekeyclean.cleanmore.fragment.ScanningFragment;
import com.ymnet.onekeyclean.cleanmore.fragment.fragmentcontroller.SingleDisplayFragmentController;
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
import com.ymnet.onekeyclean.cleanmore.wechat.view.BaseFragmentActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SilverActivity extends BaseFragmentActivity implements View.OnClickListener, ScanHelp.IScanResult, ScanFinishFragment.OnScanFinishFragmentInteractionListener, CleaningFragment.OnCleanFragmentEndListener {
    private String TAG = "SilverActivity";
    private RelativeLayout rl_page_title;
    private Button btn_stop;
    private View ani_view;
    private static final String scanningFragmentTag="scanning";
    private ScanHelp mScan;
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
    private int from = -1;//1为来自管理,2为微信

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_silver);

        Map<String, String> m = new HashMap<>();
        m.put(OnekeyField.ONEKEYCLEAN, "垃圾清理");
        MobclickAgent.onEvent(this, StatisticMob.STATISTIC_ID, m);

        resources = getResources();
        initDrawable();
        initView();
        initScanningFragment();

//        Bundle bundle = getIntent().getExtras();
//        ScanHelp scanHelp = (ScanHelp) bundle.get(OnekeyField.SCANRESULT);
//        if (scanHelp != null) {
//            mScan = scanHelp;
//            SQLiteDatabase db = new ClearManager(this).openClearDatabase();
//            mScan = ScanHelp.getInstance(this);
//            mScan.getDb();
//            mScan.setiScanResult(this);
//            mScan.setRun(false);
//            mScan.setRun(true);
//            mScan.startScan(false);
//        } else {

            initScan();
            from = getIntent().getIntExtra("from",-1);
//        }

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
       /* if (mScan != null) {
            mScan.setiScanResult(null);
            mScan.setRun(false);
            mScan.close();
            mScan = null;
        }*/
        if (checkHasCleanCache()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    selectSize = CleanOverFragment.HAS_CLEAN_CACHE;
                    startCleanOverActivity();
                }
            }, 1000);
        } else {
            SQLiteDatabase db = new ClearManager(this).openClearDatabase();
            mScan = ScanHelp.getInstance(this);
            mScan.setDb(db);
            mScan.setiScanResult(this);
            mScan.setRun(true);
            mScan.startScan(false);
        }
        Log.d("MyHandler", "mScan:" + mScan.getTotalSelectSize() + "--" + mScan.hashCode());
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

    private ScanningFragment getScanningFragment(){
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(scanningFragmentTag);
        if(fragment!=null){
            return (ScanningFragment) fragment;
        }
        return null;
    }
    private void initView() {

        rl_page_title = (RelativeLayout) findViewById(R.id.rl_page_title);
        btn_stop = (Button) findViewById(R.id.btn_stop);
        ani_view = findViewById(R.id.ani_view);
        TextView junk_title_txt = (TextView) findViewById(R.id.junk_title_txt);
//        ImageView iv_clean_setting = (ImageView) findViewById(R.id.iv_clean_setting);
        junk_title_txt.setOnClickListener(this);
//        iv_clean_setting.setOnClickListener(this);
        btn_stop.setOnClickListener(this);
        btn_stop.setTag("stop");
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
            this.finish();

           /* case R.id.iv_clean_setting:
                Intent it = new Intent(this, CleanSetActivity.class);
                startActivity(it);
//                StatisticSpec.sendEvent(StatisticEventContants.clean_setting);
                break;*/
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
        if (isFinishing()) return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ScanningFragment fragment = getScanningFragment();
                if(fragment!=null){
                    fragment.scanning(path);
                    setScanSize();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        /*if (mScan != null) {
            datas = mScan.getDatas();
            if (datas == null || datas.size() == 0) {
                Log.d(TAG, "onStop: 结束界面");
                finish();
            }
        }*/
    }

    @Override
    public void scanState(final int state) {

        if (isFinishing()||getSupportFragmentManager().isDestroyed()) {
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
            if(fragment!=null){
                fragment.setScanSize(sizeAndUnit);
                updateHeadColor(size);
            }
        }
    }

    @SuppressLint("NewApi")
    private void updateHeadColor(long size) {

        ScanningFragment fragment = getScanningFragment();
        if(fragment==null)return;
        if (android.os.Build.VERSION.SDK_INT >= 16) {
            if (size <= 10 * ByteConstants.MB) {
                // 绿色
                if (currentDrawable == null) {
                    currentDrawable = blue2Green1;
                    fragment.scanColor(blue2Green2, transitionAnimationDuration);
//                    rl_page_title.setBackground(blue2Green1);
                    blue2Green1.startTransition(transitionAnimationDuration);
//                    blue2Green2.startTransition(transitionAnimationDuration);
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
//                    rl_page_title.setBackground(orange2Red2);
                    orange2Red2.startTransition(transitionAnimationDuration);
                }
            }
        } else {
            if (size <= 10 * ByteConstants.MB) {
                // 绿色
//                rl_page_title.setBackgroundColor(resources.getColor(R.color.clean_bg_green));
                fragment.scanColor(resources.getColor(R.color.clean_bg_green));
            } else if (size <= 75 * ByteConstants.MB) {
                // 橙色
//                rl_page_title.setBackgroundColor(resources.getColor(R.color.clean_bg_orange));
                fragment.scanColor(resources.getColor(R.color.clean_bg_orange));
            } else {
                // 红色
//                rl_page_title.setBackgroundColor(resources.getColor(R.color.clean_bg_red));
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
     *  ScanFinish界面数据选中大小发生改变后的 回掉
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

        if (isFinishing()||getSupportFragmentManager().isDestroyed()) {
            return;
        }
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
     * @return
     */
    private boolean checkHasCleanCache() {
        long lastTime = com.ymnet.onekeyclean.cleanmore.cacheclean.Util.getLaseCleanDate(this, System.currentTimeMillis());
        boolean hasCache = CleanSetSharedPreferences.getLastSet(this, CleanSetSharedPreferences.CLEAN_RESULT_CACHE, false);
        boolean hasUpdate = DataCenterObserver.get(C.get()).isRefreshCleanActivity();
        /**
         *  最后的清理时间小于3分钟并且上次是全部清理的 并且3分钟内白名单项没有发生改变
          */
        if (lastTime <= 3 * TimeConstants.MINUTE&& hasCache && !hasUpdate) {
            return true;
        } else {
            DataCenterObserver.get(C.get()).setRefreshCleanActivity(false);
            CleanSetSharedPreferences.setLastSet(this, CleanSetSharedPreferences.CLEAN_RESULT_CACHE, false);
        }
        return false;
    }

    public static class CleanDataModeEvent {
        public List<JunkGroup> datas;
        public long selectSize;

        public CleanDataModeEvent(List<JunkGroup> datas, long selectSize) {
            this.datas = datas;
            this.selectSize = selectSize;
        }
    }


    @Override
    public void finish() {
        super.finish();
        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.fl_content);
    }
}
