package com.ymnet.onekeyclean.cleanmore.wechat.detail;/*
package com.example.baidumapsevice.wechat.detail;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.common.util.UriUtil;
import com.market2345.MarketApplication;
import com.market2345.R;
import com.market2345.SessionManager;
import com.market2345.base.C;
import com.market2345.clean.shortcut.ShortcutActivity;
import com.market2345.common.util.ApplicationUtils;
import com.market2345.common.util.Utils;
import com.market2345.customview.CircularProgress;
import com.market2345.customview.CustomShareBoard;
import com.market2345.customview.DetailDownloadProgressView;
import com.market2345.customview.ToggleText;
import com.market2345.datacenter.DataCenterObserver;
import com.market2345.datacenter.MarketObservable;
import com.market2345.datacenter.MarketObserver;
import com.market2345.detail.ReportDialogFragment.OnReportListener;
import com.market2345.detail.event.CommentEvent;
import com.market2345.detail.event.CommentNumEvent;
import com.market2345.detail.event.DetailLoadEvent;
import com.market2345.detail.event.DetailScrollEvent;
import com.market2345.download.DownloadInfo;
import com.market2345.download.DownloadManager;
import com.market2345.framework.http.Call;
import com.market2345.framework.http.Callback;
import com.market2345.framework.http.MHttp;
import com.market2345.framework.http.bean.Response;
import com.market2345.lm.util.AppUtils;
import com.market2345.model.App;
import com.market2345.model.InstalledApp;
import com.market2345.model.SingleAppResp;
import com.market2345.search.view.fragment.CleanSearchFragment;
import com.market2345.slidemenu.drawlayer.PagerSlidingTabStripNative;
import com.market2345.statistic.StatisticSpec;
import com.market2345.temp.TApier;
import com.market2345.temp.mapper.SingleAppRespMapper;
import com.market2345.temp.model.DetailAppEntity;
import com.market2345.usercenter.manager.TaskType;
import com.market2345.util.AppsUtils;
import com.market2345.statistic.StatisticEventContants;
import com.market2345.util.ViewTagger;
import com.market2345.view.fragment.BaseFragment;
import com.nineoldandroids.view.ViewHelper;
import com.statistic2345.log.Statistics;
import com.umeng.socialize.media.UMImage;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

*/
/**
 * @author 你的名字
 * @version 2013-11-25 上午09:16:21
 * @类说明
 *//*

public class DetailActivity extends DetailBaseActivity implements
        OnClickListener, MarketObserver, OnReportListener {
    private static final String TAG = DetailActivity.class.getSimpleName();

    public static final int COMMENT_CHARACTER_SIZE = 2;

    public static final String DETAIL_TYPE = "detail_type_recomment";

    private TextView app_detail_name, mark, size;
    private ToggleText ttIcon;

    private ImageView app_icon;

    private DetailDownloadProgressView downloaded_app_instal_btn;

    private TextView mSafe, tvAd, tvCharge, tvAdDown, tvAdDownDesc, tvChargeDown, tvChargeDownDesc, tvSimpleDescription, tvTitle;

    private View llSecurity, llDesc, llSafety, llAd, llCharge;

    private ImageButton ibTopBack, ibTopShare;

    private TextView downloadRight;

    private PagerSlidingTabStripNative tabs;

    private View downloadBar;

    private int allNum;

    private App app;

    private SingleAppResp appDetail;

    private int sid;

    private DownloadManager mDownloadManager;

    private DownloadClickCallBack callBack;

    private SubCommentsFragment subCommentsFragment = null;
    private SubDetailFragment subDetailFragment = null;
    private SubGiftFragment subGiftFragment = null;

    public RelativeLayout rlTop;

    private String[] TITLES = null;

    public static final String TAB_ITEM = "tabItem";
    public static int TAB_ID = 0;
    public static final int TAB_NUM = 2;  //2代表礼包   0代表详情   1代表评论
    private boolean hasGift = false;
    MyPagerAdapter adapter;
    float translationY;
    private int llDescHeight;
    int newHeaderHeight = 0;
    private Intent intent;
    private CustomShareBoard shareBoard;
    private String downloadPushEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_details);
        EventBus.getDefault().register(this);
        mDownloadManager = DownloadManager.getInstance(this.getApplicationContext());
        initView();
        initListener();
        if (app != null) {
//            loading.setVisibility(View.GONE);
            hideLoading();
            rlTop.getBackground().mutate().setAlpha(0);
            initData();
        } else {
            showLoading();
            rlTop.getBackground().mutate().setAlpha(255);
        }
        loadData();
        downloadPushEvent = getIntent().getStringExtra("clicktoevent");
        DataCenterObserver.get(getApplicationContext()).addObserver(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (shareBoard != null) {
            shareBoard.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        DataCenterObserver.get(getApplicationContext()).deleteObserver(this);

        if (appDetail != null) {
            if (appDetail.response != null) {
                appDetail.response = null;
            }

            if (appDetail.list != null) {
                appDetail.list = null;
            }

            appDetail = null;
        }
        EventBus.getDefault().unregister(this);
        if (shareBoard != null) {
            shareBoard.cleanListeners();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_top_back:
                AppUtils.hideSoftKeyboard(this);
                finish();
                break;
            case R.id.ib_top_share:
                StatisticSpec.sendEvent(StatisticEventContants.appdetail_share);

                //shareURL();
                if (app != null) {
                    UMImage urlImage = new UMImage(this, app.icon);
                    String title = String.format(getString(R.string.qq_share_title), app.title);
                    String content = String.format(getString(R.string.share_text), app.title);
                    if (this.getSupportFragmentManager().isDestroyed()) {
                        return;
                    }
                    shareBoard = new CustomShareBoard(this);
                    shareBoard.configPlatforms();
                    shareBoard.setAllShareContent(title, content, getTargetUrl(), urlImage);
                    if (!shareBoard.isShowing()) {
                        shareBoard.showAtLocation(this.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
                    }
                }

                break;
            case R.id.btn_retry:
                if (!Utils.isNetworkAvailable(DetailActivity.this)) {
                    Toast.makeText(C.get(), getString(R.string.detail_network_abnormal), Toast.LENGTH_SHORT).show();
                    return;
                }
//                noData.setVisibility(View.GONE);
                downloadBar.setVisibility(View.GONE);
//                loading.setVisibility(View.VISIBLE);
//                progress.setVisibility(View.VISIBLE);
                showLoading();
                loadData();
                break;
            case R.id.ll_security:
                ttIcon.toggle();
                if (ttIcon.isClick()) {
                    // 当前为展开状态
                    Animation animation = AnimationUtils.loadAnimation(C.get(), R.anim.arrow_rotate_clockwise);
                    ttIcon.startAnimation(animation);
                    newHeaderHeight = mHeaderHeight + llDescHeight;
                    mMinHeaderHeight += llDescHeight;
                    llDesc.setVisibility(View.VISIBLE);
                    EventBus.getDefault().post(new DetailScrollEvent(TAG, newHeaderHeight));
                } else {
                    mHeaderHeight = newHeaderHeight - llDescHeight;
                    mMinHeaderHeight -= llDescHeight;
                    Animation animation = AnimationUtils.loadAnimation(C.get(), R.anim.arrow_rotate_anticlockwise);
                    ttIcon.startAnimation(animation);
                    // 当前为收起状态
                    llDesc.setVisibility(View.GONE);
                    EventBus.getDefault().post(new DetailScrollEvent(TAG, mHeaderHeight));
                }
                break;
            case R.id.tv_report:
                ReportDialogFragment f = new ReportDialogFragment();
                f.setOnReportListener(this);
                f.show(f, getSupportFragmentManager(), "ReportDialogFragment");
                break;
            default:
                break;
        }
    }

    private String getTargetUrl() {
        String appType = "";

        if (app.type_id == 17) {
            appType = "soft";
        } else if (app.type_id == 18) {
            appType = "game";
        } else {
            return app.url;
        }

        return String.format(getString(R.string.target_url), appType, app.sid);
    }

    @Override
    public void update(MarketObservable observable, Object data) {
        if (null == app) {
            return;
        }

        if (data instanceof Pair) {
            Pair<String, Object> pair = (Pair<String, Object>) data;
            if (pair.first.equals(SessionManager.P_INSTALL_APP)) {
                initAppstate(app);
            }
        } else if (data instanceof String) {
            if (SessionManager.ADD_OR_REMOVE_DOWNLOAD.equals(data)) {
                initAppstate(app);
            } else if (DataCenterObserver.STR_STATUS_CHANGE.equals(data)) {
                initAppstate(app);
            }
        }
    }

    private void initView() {
        ibTopBack = (ImageButton) findViewById(R.id.ib_top_back);
        ibTopShare = (ImageButton) findViewById(R.id.ib_top_share);

        tvTitle = (TextView) findViewById(R.id.tv_title);
        // safe ad charge
        llSecurity = findViewById(R.id.ll_security);
        llDesc = findViewById(R.id.ll_desc);
        llSafety = findViewById(R.id.ll_safety);
        llAd = findViewById(R.id.ll_ad);
        llCharge = findViewById(R.id.ll_charge);
        ttIcon = (ToggleText) findViewById(R.id.tt_icon);
        mSafe = (TextView) findViewById(R.id.safety);
        tvAd = (TextView) findViewById(R.id.ad);
        tvAdDown = (TextView) findViewById(R.id.tv_ad_down);
        tvAdDownDesc = (TextView) findViewById(R.id.tv_ad_down_desc);
        tvCharge = (TextView) findViewById(R.id.charge);
        tvChargeDown = (TextView) findViewById(R.id.tv_charge_down);
        tvChargeDownDesc = (TextView) findViewById(R.id.tv_charge_down_desc);
        tvSimpleDescription = (TextView) findViewById(R.id.tv_simple_description);

        // install btn
        downloaded_app_instal_btn = (DetailDownloadProgressView) findViewById(R.id.downloaded_app_instal_btn);

        rlTop = (RelativeLayout) findViewById(R.id.rl_top_layout);
        // loading
        initLoadView();

        size = (TextView) findViewById(R.id.size);
        mark = (TextView) findViewById(R.id.mark);
        app_detail_name = (TextView) findViewById(R.id.app_detail_name);
        app_icon = (ImageView) findViewById(R.id.app_icon);
        mHeader = (LinearLayout) findViewById(R.id.header);
        mHeaderHeight = getTargetHeight(mHeader);
        mHeader.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                llDescHeight = getTargetHeight(llDesc);
                mMinHeaderHeight = findViewById(R.id.id_stickynavlayout_topview).getHeight();
                mInnerHeight = findViewById(R.id.rl_top_layout_inner).getHeight();
            }
        });

        downloadBar = findViewById(R.id.app_detail_bottom_rl_012);
        downloadRight = (TextView) findViewById(R.id.downloadright);
        intent = getIntent();
        app = (App) intent.getSerializableExtra(App.class.getSimpleName());
        sid = intent.getIntExtra("sid", -1);
        if (sid != -1) {
            downloaded_app_instal_btn.setTag(R.id.download_from_shortcut, ShortcutActivity.Download_From_Shortcut);
        }
        TAB_ID = intent.getIntExtra(TAB_ITEM, 0);
        openDetailType = intent.getStringExtra("openDetailType");
        searchDetailType = intent.getStringExtra(CleanSearchFragment.SEARCH_DETAIL_DOWNLOAD);
        zhuantiSingleappDetailType = intent.getStringExtra("zhuantiSingleappDetailType");
        zhuantiSingleappTopicId = intent.getIntExtra("zhuantiSingleappTopicId", 0);
        bFromDesk = intent.getBooleanExtra("bFromDesk", false);

    }

    private void initListener() {
        ibTopBack.setOnClickListener(this);
        ibTopShare.setOnClickListener(this);
        if (btn_retry != null) {
            btn_retry.setOnClickListener(this);
        }
        llSecurity.setOnClickListener(this);
        findViewById(R.id.tv_report).setOnClickListener(this);
        callBack = new DownloadClickCallBack() {

            @Override
            public void clickCallBack() {
                if ("searchFragment".equals(openDetailType)) {
                    if (!bFromDesk) {
                        Statistics.onEvent(C.get(), StatisticEventContants.Search_In_Click_DownLoad);
                    }
                }
                if ("searchFragmentSuggestion".equals(searchDetailType)) {
                    Statistics.onEvent(C.get(), StatisticEventContants.Search_Association_Detail_Download);
                }
                if ("zhuantiSingleappDetailType".equals(zhuantiSingleappDetailType)) {
                    Statistics.onEvent(C.get(), StatisticEventContants.zhuanti_singleapp_click_detaildownload_id + zhuantiSingleappTopicId);
                }

                StatisticSpec.sendEvent(downloadPushEvent);
                StatisticSpec.sendEvent(StatisticEventContants.appdetail_download);
            }

            @Override
            public void clickCallBack(int position) {

            }
        };
    }

    private Callback<Response<DetailAppEntity>> appInfoCallback = new Callback<Response<DetailAppEntity>>() {
        @Override
        public void onResponse(Call<Response<DetailAppEntity>> call, Response<DetailAppEntity> response) {

            if (response.getData() != null) {
                appDetail = new SingleAppRespMapper().transform(response);
                downloadBar.setVisibility(View.VISIBLE);
                hideLoading();
                rlTop.getBackground().mutate().setAlpha(0);
                if (appDetail != null && appDetail.list != null) {
                    if (app == null) {
                        app = appDetail.list;
                        initData();
                    } else {
                        if (appDetail.list.giftTotal > 0 && !hasGift) {
                            TITLES = new String[]{getString(R.string.detail), getString(R.string.comment), getString(R.string.gift)};
                            FragmentManager fm = getSupportFragmentManager();
                            FragmentTransaction ft = fm.beginTransaction();
                            for (int i = 0; i < adapter.getScrollTabHolders().size(); i++) {
                                ft.remove((Fragment) adapter.getScrollTabHolders().get(i));
                            }
                            ft.commitAllowingStateLoss();
                            adapter = new MyPagerAdapter(getSupportFragmentManager(), TITLES.length);
                            viewPager.setAdapter(adapter);
                            tabs = (PagerSlidingTabStripNative) findViewById(R.id.fl_tab);
                            tabs.setViewPager(viewPager);
                            tabs.setOnPageChangeListener(new DetailViewPagerChangeListener());
                            viewPager.setCurrentItem(TAB_ID, false);
                            viewPager.setOffscreenPageLimit(2);
                        }
                        EventBus.getDefault().post(new DetailLoadEvent(app.sid, appDetail));
                    }

                }
                initDatas();
                MarketApplication.getInstance().getApplicationComponent().taskManager().putTaskEvent(TaskType.TYPE_APP_DETAIL, null);
            }

        }

        @Override
        public void onFailure(Call<Response<DetailAppEntity>> call, Throwable t) {
            rlTop.getBackground().mutate().setAlpha(255);
            showError();
        }
    };


    */
/**
     * 详情数据
     *//*

    private void loadData() {
        if (app == null) {
            if (sid <= 0) {
                Toast.makeText(getApplicationContext(), getString(R.string.software_not_exit), Toast.LENGTH_SHORT).show();
                finish();
            } else {
                TApier.get().getAppByID(sid).enqueue(appInfoCallback);
            }
        } else {
            sid = app.sid;
            TApier.get().getAppByID(sid).enqueue(appInfoCallback);
        }

    }

    private void initData() {
        AppsUtils.notifyDisplayEvent(app.packageName);

        downloaded_app_instal_btn.setTag(R.id.download_item, app);
        ViewTagger.setTag(downloaded_app_instal_btn, R.id.hold_activty, DetailActivity.this);
        downloaded_app_instal_btn.setTag(R.id.download_url, app.url);
        downloaded_app_instal_btn.setTag(R.id.download_result_click, callBack);
        String type = intent.getStringExtra("type");
        int sort = intent.getIntExtra("sort", 0);
        int position = intent.getIntExtra("position", -1);
        downloaded_app_instal_btn.setTag(R.id.soft_game_sort, sort);
        downloaded_app_instal_btn.setTag(R.id.download_position, position);
        if (BaseFragment.TYPE_RECOMMEND.equals(type)) {
            mDownloadManager.setTypeAndPostion(position, DETAIL_TYPE);
        }
        downloaded_app_instal_btn.setTag(R.id.download_detail_type, type);
        mDownloadManager.setOnClickListener(downloaded_app_instal_btn);
        if (app.giftTotal > 0) {
            TITLES = new String[]{getString(R.string.detail), getString(R.string.comment), getString(R.string.gift)};
            hasGift = true;
        } else {
            TITLES = new String[]{getString(R.string.detail), getString(R.string.comment)};
        }
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        adapter = new MyPagerAdapter(getSupportFragmentManager(), TITLES.length);
        viewPager.setAdapter(adapter);
        tabs = (PagerSlidingTabStripNative) findViewById(R.id.fl_tab);
        tabs.setViewPager(viewPager);
        tabs.setOnPageChangeListener(new DetailViewPagerChangeListener());
        viewPager.setCurrentItem(TAB_ID, false);
        viewPager.setOffscreenPageLimit(2);
        initAppstate(app);

        // app detail
        app_detail_name.setText(app.title);
        app_icon.setImageURI(UriUtil.parse(app.icon));
        if (!TextUtils.isEmpty(app.mark)) {
            mark.setText(app.mark + getString(R.string.mark));
        }
        size.setText(getString(R.string.detail_size, app.fileLength) + "  |");
        if (!TextUtils.isEmpty(app.oneword)) {
            tvSimpleDescription.setText(app.oneword);
        }
        downloadRight.setText(ApplicationUtils.getFormatDownloads(app.totalDowns) + "  |");

        //详情页展示次数统计
        StatisticSpec.sendEvent(StatisticEventContants.AppDetail_Show);
    }

    private void initDatas() {
        if (appDetail != null) {
            if (appDetail.list != null) {
                if (appDetail.list.safe == -1 && appDetail.list.ad == -1 && appDetail.list.charge == -1) {
                    llSecurity.setVisibility(View.GONE);
                } else {
                    if (appDetail.list.safe == 1) {
                        // 安全
                    } else {
                        mSafe.setVisibility(View.GONE);
                        llSafety.setVisibility(View.GONE);
                    }

                    if (appDetail.list.charge == 0) {
                        // 免费
                    } else if (appDetail.list.charge == 1) {
                        // 部分内容付费
                        tvCharge.setText(getString(R.string.part_free));
                        tvCharge.setCompoundDrawablesWithIntrinsicBounds(getMyDrawable(R.drawable.appdetail_description_blue), null, null, null);
                        tvChargeDown.setCompoundDrawablesWithIntrinsicBounds(getMyDrawable(R.drawable.appdetail_description_blue), null, null, null);
                        tvChargeDownDesc.setText(getString(R.string.part_free));
                    } else if (appDetail.list.charge == 2) {
                        // 免费试用
                        tvCharge.setText(getString(R.string.free));
                        tvCharge.setCompoundDrawablesWithIntrinsicBounds(getMyDrawable(R.drawable.appdetail_description_blue), null, null, null);
                        tvChargeDown.setCompoundDrawablesWithIntrinsicBounds(getMyDrawable(R.drawable.appdetail_description_blue), null, null, null);
                        tvChargeDownDesc.setText(getString(R.string.free));
                    } else if (appDetail.list.charge == 3) {
                        // 道具收费
                        tvCharge.setText(getString(R.string.item_based));
                        tvCharge.setCompoundDrawablesWithIntrinsicBounds(getMyDrawable(R.drawable.appdetail_description_blue), null, null, null);
                        tvChargeDown.setCompoundDrawablesWithIntrinsicBounds(getMyDrawable(R.drawable.appdetail_description_blue), null, null, null);
                        tvChargeDownDesc.setText(getString(R.string.item_based));
                    } else if (appDetail.list.charge == 4) {
                        // 部分功能付费
                        tvCharge.setText(getString(R.string.part_payment));
                        tvCharge.setCompoundDrawablesWithIntrinsicBounds(getMyDrawable(R.drawable.appdetail_description_blue), null, null, null);
                        tvChargeDown.setCompoundDrawablesWithIntrinsicBounds(getMyDrawable(R.drawable.appdetail_description_blue), null, null, null);
                        tvChargeDownDesc.setText(getString(R.string.part_payment));
                    } else if (appDetail.list.charge == 5) {
                        // 解锁付费
                        tvCharge.setText(getString(R.string.unlock_payment));
                        tvCharge.setCompoundDrawablesWithIntrinsicBounds(getMyDrawable(R.drawable.appdetail_description_blue), null, null, null);
                        tvChargeDown.setCompoundDrawablesWithIntrinsicBounds(getMyDrawable(R.drawable.appdetail_description_blue), null, null, null);
                        tvChargeDownDesc.setText(getString(R.string.unlock_payment));
                    } else {
                        tvCharge.setVisibility(View.GONE);
                        llCharge.setVisibility(View.GONE);
                    }

                    if (appDetail.list.ad == 1) {
                        // 有广告
                        tvAd.setText(getString(R.string.ad));
                        tvAd.setCompoundDrawablesWithIntrinsicBounds(getMyDrawable(R.drawable.appdetail_description_bad), null, null, null);
                        tvAdDown.setCompoundDrawablesWithIntrinsicBounds(getMyDrawable(R.drawable.appdetail_description_bad), null, null, null);

                        String strAd = getString(R.string.ad);
                        if (!TextUtils.isEmpty(appDetail.list.adverType)) {
                            strAd = appDetail.list.adverType;
                        }
                        tvAdDownDesc.setText(strAd);
                    } else if (appDetail.list.ad == 0) {
                        // 无广告
                    } else {
                        tvAd.setVisibility(View.GONE);
                        llAd.setVisibility(View.GONE);
                    }
                }

            }
        }
    }

    */
/**
     * 底部下载按钮、分析/取消 按钮操作
     *
     * @param app
     *//*

    private void initAppstate(App app) {
        DownloadInfo info = mDownloadManager.getDownloadInfo(app.url);
        if (info != null) {
            info.addProgressViews(downloaded_app_instal_btn);
            info.notifyProgress(this);
        } else {
            downloaded_app_instal_btn.setEnabled(true);
            if (DataCenterObserver.get(getApplicationContext()).getAppsInfoHandler().checkInupdatelist(app.packageName)) {
                // 升级
                downloaded_app_instal_btn.setDownloadText(true, getString(R.string.detail_update), R.color.item_update_color, R.drawable.install_update_bg, 0);
            } else if (DataCenterObserver.get(getApplicationContext()).getAppsInfoHandler().checkIsHasInatall(app.packageName)) {
                InstalledApp installedApp = DataCenterObserver.get(C.get()).getInstalledApp(app.packageName);
                if (installedApp != null) {
                    if (DataCenterObserver.get(C.get()).getAppsInfoHandler().checkInupdatelist(installedApp.packageName)) {
                        downloaded_app_instal_btn.setDownloadText(true, getString(R.string.detail_update), R.color.item_update_color, R.drawable.install_update_bg, 0);
                    } else {
                        downloaded_app_instal_btn.setDownloadText(true, getString(R.string.detail_open), R.color.item_update_color, R.drawable.install_update_bg, 0);
                    }
                }
            } else {
                downloaded_app_instal_btn.setDownloadText(true, getString(R.string.detail_download), R.color.detail_before_download, R.drawable.download_bg, 0);
            }
        }
    }

    private Drawable getMyDrawable(int resID) {
        return getResources().getDrawable(resID);
    }

    public void showError() {
        downloadBar.setVisibility(View.GONE);
        showLoadFail();
    }

    private String openDetailType;
    private String searchDetailType;
    private String zhuantiSingleappDetailType;
    private int zhuantiSingleappTopicId;

    private boolean bFromDesk;

    public interface DownloadClickCallBack {
        void clickCallBack();

        void clickCallBack(int position);
    }

    @Override
    public void onReport(String cause, String report, String contact) {
        if (TextUtils.isEmpty(cause)) {
            cause = "";
        }
        if (TextUtils.isEmpty(report)) {
            report = "";
        }
        if (TextUtils.isEmpty(contact)) {
            contact = "";
        }

        if (app != null) {
            Map<String, String> paraMap = new HashMap<String, String>();
            paraMap.put("softId", "" + sid);
            paraMap.put("cause", cause);
            paraMap.put("report", report);
            paraMap.put("contact", contact);
            paraMap.put("packageName", app.packageName);
            paraMap.put("version", app.version);
            TApier.get().softwareReport(paraMap).enqueue(softwareReportCallback);
            StatisticSpec.sendEvent(StatisticEventContants.appdetail_report);
        }
    }

    private Callback<Response> softwareReportCallback = new Callback<Response>() {
        @Override
        public void onResponse(Call<Response> call, Response response) {
            if (isFinishing()) {
                return;
            }
            if (response != null && MHttp.responseOK(response.getCode())) {
                Toast.makeText(C.get(), getString(R.string.report_success), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(C.get(), getString(R.string.report_failure), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<Response> call, Throwable t) {
            if (isFinishing()) {
                return;
            }

            Toast.makeText(C.get(), getString(R.string.report_failure), Toast.LENGTH_SHORT).show();
        }
    };

    public class MyPagerAdapter extends DetailFragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm, int count) {
            super(fm, count);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle;
            switch (position) {
                case 0:
                    subDetailFragment = SubDetailFragment.newInstance();
                    bundle = new Bundle();
                    if (app != null) {
                        bundle.putSerializable("app", app);
                        bundle.putSerializable("appDetail", appDetail);
                        bundle.putInt("HeaderHeight", mHeaderHeight);
                        bundle.putInt("position", position);
                        subDetailFragment.setArguments(bundle);
                    }
                    return subDetailFragment;
                case 1:
                    subCommentsFragment = SubCommentsFragment.newInstance();
                    bundle = new Bundle();
                    bundle.putInt("sid", sid);
                    bundle.putInt("HeaderHeight", mHeaderHeight);
                    bundle.putString("packageName", app.packageName);
                    bundle.putInt("position", position);
                    subCommentsFragment.setArguments(bundle);
                    return subCommentsFragment;
                case 2:
                    subGiftFragment = SubGiftFragment.newInstance();
                    bundle = new Bundle();
                    bundle.putInt("HeaderHeight", mHeaderHeight);
                    bundle.putInt("position", position);
                    bundle.putString(SubGiftFragment.KEY_SOFT_ID, app.sid + "");
                    subGiftFragment.setArguments(bundle);
                    return subGiftFragment;

                default:
                    return null;
            }

        }
    }

    public void onEventMainThread(Object event) {
        if (event instanceof CommentNumEvent) {
            if (app.sid == (((CommentNumEvent) event).getTag())) {
                allNum = ((CommentNumEvent) event).getAllNum();
                if (allNum > 0) {
                    if (allNum < 10000) {
                        TITLES[1] = getString(R.string.comment) + "(" + allNum + ")";
                    } else {
                        DecimalFormat df = new DecimalFormat("0.00");
                        double commentsNum = new BigDecimal(df.format(((double) allNum) / 10000)).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                        TITLES[1] = getString(R.string.comment) + "(" + commentsNum + "万)";
                    }
                }
                if (tabs != null) {
                    tabs.notifyDataSetChanged();
                }
            }
        } else if (event instanceof CommentEvent) {
            allNum += 1;
            if (allNum > 0) {
                if (allNum < 10000) {
                    TITLES[1] = getString(R.string.comment) + "(" + allNum + ")";
                } else {
                    DecimalFormat df = new DecimalFormat("0.00");
                    double commentsNum = new BigDecimal(df.format(((double) allNum) / 10000)).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                    TITLES[1] = getString(R.string.comment) + "(" + commentsNum + "万)";
                }
            }
            tabs.notifyDataSetChanged();
        }

    }

    @Override
    public void scrollHeader(int scrollY) {
        translationY = Math.max(-scrollY, -mMinHeaderHeight + mInnerHeight);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            mHeader.scrollTo(0, -(int) translationY);
        } else {
            ViewHelper.setTranslationY(mHeader, translationY);
        }

        if (Math.abs(translationY) <= (mMinHeaderHeight - mInnerHeight)) {
            if (translationY == (mMinHeaderHeight - mInnerHeight)) {
                llSecurity.setClickable(false);
                ibTopShare.setBackgroundResource(R.drawable.menu_share_selector);
                ibTopBack.setBackgroundResource(R.drawable.menu_back_selector);
            } else {
                llSecurity.setVisibility(View.VISIBLE);
                tvSimpleDescription.setVisibility(View.VISIBLE);
                llSecurity.setClickable(true);
                tvTitle.setText("");
                ibTopShare.setBackgroundResource(R.drawable.menu_share_inner_selector);
                ibTopBack.setBackgroundResource(R.drawable.menu_back_inner_selector);
            }
            if ((int) ((Math.abs(translationY)) * 255 / (mMinHeaderHeight - mInnerHeight + 10)) > 256 * 0.7) {
                llSecurity.setVisibility(View.INVISIBLE);
                tvSimpleDescription.setVisibility(View.INVISIBLE);
                tvTitle.setText(app.title);
            }
            rlTop.getBackground().mutate().setAlpha((int) ((Math.abs(translationY) * 255) / (mMinHeaderHeight - mInnerHeight + 10)));
        } else {
            if (translationY == 0) {
                rlTop.getBackground().mutate().setAlpha(0);
                tvTitle.setText("");
            } else {
                tvTitle.setText(app.title);
                rlTop.getBackground().mutate().setAlpha(255);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (shareBoard != null && shareBoard.isShowing()) {
            shareBoard.dismiss();
        }
    }

    class DetailViewPagerChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            int currentItem = viewPager.getCurrentItem();
            if (positionOffsetPixels > 0) {
                SparseArrayCompat<ScrollTabHolder> scrollTabHolders = adapter.getScrollTabHolders();

                ScrollTabHolder fragmentContent;
                if (position < currentItem) {
                    // Revealed the previous page
                    fragmentContent = scrollTabHolders.valueAt(position);
                } else {
                    // Revealed the next page
                    fragmentContent = scrollTabHolders.valueAt(position + 1);
                }
                fragmentContent.adjustScroll((int) (translationY), mHeader.getHeight());
            }
        }

        @Override
        public void onPageSelected(int position) {
            SparseArrayCompat<ScrollTabHolder> scrollTabHolders = adapter.getScrollTabHolders();

            if (scrollTabHolders == null || scrollTabHolders.size() != TITLES.length) {
                return;
            }

            ScrollTabHolder currentHolder = scrollTabHolders.valueAt(position);
            currentHolder.adjustScroll((int) (translationY), mHeader.getHeight());
            if (position == 2) {
                StatisticSpec.sendEvent(StatisticEventContants.appdetail_gift);
            } else if (position == 1) {
                StatisticSpec.sendEvent(StatisticEventContants.appdetail_comment);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

    private int getTargetHeight(View v) {

        try {
            Method m = v.getClass().getDeclaredMethod("onMeasure", int.class,
                    int.class);
            m.setAccessible(true);
            m.invoke(v, View.MeasureSpec.makeMeasureSpec(
                    ((View) v.getParent()).getMeasuredWidth(),
                    View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED));
        } catch (Exception e) {

        }
        return v.getMeasuredHeight();
    }

    private View fl_loading, pb_loading, ll_loaded_fail, btn_retry;

    */
/**
     * 抽取loadingView 方便以后更改
     *//*

    private void initLoadView() {
        fl_loading = findViewById(R.id.fl_loading);
        pb_loading = findViewById(R.id.pb_loading);
        if (pb_loading instanceof CircularProgress) {
            ((CircularProgress) pb_loading).setName("detail");
        }
        ll_loaded_fail = findViewById(R.id.ll_loaded_fail);
        btn_retry = findViewById(R.id.btn_retry);
    }

    protected void showLoading() {
        try {
            checkView();
        } catch (Exception e) {
            Log.e(TAG, "checkView is exception");
            return;
        }
        fl_loading.setVisibility(View.VISIBLE);
        pb_loading.setVisibility(View.VISIBLE);
        ll_loaded_fail.setVisibility(View.GONE);
        btn_retry.setVisibility(View.GONE);
    }

    protected void showLoadFail() {
        try {
            checkView();
        } catch (Exception e) {
            Log.e(TAG, "checkView is exception");

            return;
        }
        fl_loading.setVisibility(View.VISIBLE);
        pb_loading.setVisibility(View.GONE);
        ll_loaded_fail.setVisibility(View.VISIBLE);
        btn_retry.setVisibility(View.VISIBLE);
    }

    protected void hideLoading() {
        try {
            checkView();
        } catch (Exception e) {
            Log.e(TAG, "checkView is exception");
            return;
        }
        fl_loading.setVisibility(View.GONE);
        pb_loading.setVisibility(View.GONE);
        ll_loaded_fail.setVisibility(View.GONE);
        btn_retry.setVisibility(View.GONE);
    }

    private void checkView() throws Exception {
        if (fl_loading == null || pb_loading == null || ll_loaded_fail == null || btn_retry == null) {
            throw new IllegalArgumentException("loading view has null");
        }
    }
}
*/
