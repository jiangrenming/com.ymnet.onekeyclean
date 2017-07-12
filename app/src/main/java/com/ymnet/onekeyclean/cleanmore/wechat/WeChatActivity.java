package com.ymnet.onekeyclean.cleanmore.wechat;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.commonlibrary.utils.ConvertParamsUtils;
import com.example.commonlibrary.utils.NetworkUtils;
import com.example.commonlibrary.utils.ScreenUtil;
import com.nineoldandroids.view.ViewHelper;
import com.umeng.analytics.MobclickAgent;
import com.ymnet.killbackground.customlistener.MyViewPropertyAnimatorListener;
import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.ImmersiveActivity;
import com.ymnet.onekeyclean.cleanmore.animation.TweenAnimationUtils;
import com.ymnet.onekeyclean.cleanmore.constants.WeChatConstants;
import com.ymnet.onekeyclean.cleanmore.customview.DividerItemDecoration;
import com.ymnet.onekeyclean.cleanmore.customview.RecyclerViewPlus;
import com.ymnet.onekeyclean.cleanmore.home.HomeToolBarAD;
import com.ymnet.onekeyclean.cleanmore.junk.SilverActivity;
import com.ymnet.onekeyclean.cleanmore.qq.activity.QQActivity;
import com.ymnet.onekeyclean.cleanmore.utils.C;
import com.ymnet.onekeyclean.cleanmore.utils.CleanSetSharedPreferences;
import com.ymnet.onekeyclean.cleanmore.utils.DisplayUtil;
import com.ymnet.onekeyclean.cleanmore.utils.FormatUtils;
import com.ymnet.onekeyclean.cleanmore.utils.OnekeyField;
import com.ymnet.onekeyclean.cleanmore.utils.StatisticMob;
import com.ymnet.onekeyclean.cleanmore.utils.ToastUtil;
import com.ymnet.onekeyclean.cleanmore.utils.Util;
import com.ymnet.onekeyclean.cleanmore.web.JumpUtil;
import com.ymnet.onekeyclean.cleanmore.web.WebHtmlActivity;
import com.ymnet.onekeyclean.cleanmore.wechat.adapter.WeChatRecommendAdapter;
import com.ymnet.onekeyclean.cleanmore.wechat.adapter.WeChatRecyclerViewAdapter;
import com.ymnet.onekeyclean.cleanmore.wechat.device.DeviceInfo;
import com.ymnet.onekeyclean.cleanmore.wechat.listener.RecyclerViewClickListener;
import com.ymnet.onekeyclean.cleanmore.wechat.mode.WeChatContent;
import com.ymnet.onekeyclean.cleanmore.wechat.mode.WeChatFileType;
import com.ymnet.onekeyclean.cleanmore.wechat.presenter.WeChatPresenter;
import com.ymnet.onekeyclean.cleanmore.wechat.presenter.WeChatPresenterImpl;
import com.ymnet.onekeyclean.cleanmore.wechat.view.WeChatMvpView;
import com.ymnet.onekeyclean.cleanmore.widget.BottomScrollView;
import com.ymnet.onekeyclean.cleanmore.widget.LinearLayoutItemDecoration;
import com.ymnet.onekeyclean.cleanmore.widget.SGTextView;
import com.ymnet.onekeyclean.cleanmore.widget.WaveLoadingView;
import com.ymnet.retrofit2service.RetrofitService;
import com.ymnet.retrofit2service.bean.WeChatNewsInformation;

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

import bolts.Task;
import pl.droidsonroids.gif.GifDrawable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ymnet.onekeyclean.R.id.fl_idle;
import static com.ymnet.onekeyclean.R.id.iv_sun_center;
import static com.ymnet.onekeyclean.R.id.ll_content;
import static com.ymnet.onekeyclean.R.id.tv_clean_success_size;
import static com.ymnet.onekeyclean.R.id.tv_history_clean_size;


public class WeChatActivity extends ImmersiveActivity implements WeChatMvpView, View.OnClickListener {
    private static final int NEWS_CODE = 11;
    WeChatPresenter mPresenter;
    public final static String EXTRA_ITEM_POSITION = "wechat_position";
    public final static String WECHAT_GUIDE        = "wechat_guide";
    private WaveLoadingView        mWaveLoadingView;
    private boolean                isRemove;
    private RelativeLayout         mRl;
    private TextView               mTvBtn;
    private RecyclerViewPlus       mRvNews;
    private WeChatRecommendAdapter mRecommendAdapter;
    private View                   mNewsHead;
    private View                   foot;
    private List<WeChatNewsInformation.DataBean.ResultBean> moreData = new ArrayList<>();
    //信息流相关
    private int                                             page     = 1;
    private View                   mEmptyView;
    private ImageView              mIv_sun;
    private ImageView              mIv_sun_center;
    private ImageView              mBlingBling;
    private TextView               mTv_clean_success_size;
    private TextView               mTv_history_clean_size;
    private View                   mFl_idle;
    private View                   mLl_content;
    private RecyclerViewPlus       mEmptyRv;
    private View                   mEmptyHead;
    private View                   mEmptyNewsHead;
    private WeChatRecommendAdapter mEmptyRecommendAdapter;
    private View                   mEmptyFoot;
    private long                      mSuccessCleanSize = 0;
    private HashMap<Integer, Boolean> removeMap         = new HashMap<>();
    private int position;
    private BottomScrollView mSv;
    private BottomScrollView mEmptySv;
    private boolean saveScrollState =false;
    private ImageView mAdvertisement;
    private String mHomeIsExist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_we_chat);

        mHomeIsExist = getIntent().getStringExtra("HomeIsExist");
        addActivity(this);

        String stringExtra = getIntent().getStringExtra(OnekeyField.ONEKEYCLEAN);
        String statistics_key = getIntent().getStringExtra(OnekeyField.STATISTICS_KEY);
        if (stringExtra != null) {
            Map<String, String> m = new HashMap<>();
            m.put(OnekeyField.ONEKEYCLEAN, stringExtra);
            MobclickAgent.onEvent(this, statistics_key, m);
        }

        C.setContext(getApplication());
        mPresenter = new WeChatPresenterImpl(this);
        initTitleBar();
        initializeRecyclerView();
        initBottom();
        ani_view = findViewById(R.id.ani_view);

    }

    /**
     * 标记是否做扫描动画
     */
    boolean end = true;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        saveScrollState =false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        if (!saveScrollState) {
            mEmptySv.scrollTo(0, 0);
            mSv.scrollTo(0, 0);
        }
        /*if (mHomeIsExist !=null) {
            Log.d("ImmersiveActivity", mHomeIsExist);
            deleteHomeActivity();
        }*/

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            headHeight = view_head.getHeight();
            titleHeight = ll_title.getHeight();
            if (mPresenter != null) {
                end = mPresenter.isEnd();
            }
            if (!end) {
                startAnim();
            } else {
                updateData();
                stopAnim();
            }
        } else {
            stopAnim();
        }
    }

    View ll_title;
    int  titleHeight;
    View ani_view;

    private void initTitleBar() {
        ll_title = findViewById(R.id.ll_title);
        TextView left_btn = (TextView) findViewById(R.id.junk_title_txt);
        //        findViewById(R.id.iv_clean_setting).setVisibility(View.GONE);
        left_btn.setText(R.string.managementfragment_wechat_clean);
        left_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                WeChatActivity.this.finish();
                openHome(true);
            }
        });
        ViewTreeObserver observer = ll_title.getViewTreeObserver();
        if (observer.isAlive()) {
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    titleHeight = ll_title.getHeight();
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        ll_title.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        ll_title.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }
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
                                m.put(OnekeyField.ONEKEYCLEAN, "微信清理ToolBar");
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

    private RecyclerViewPlus          rv;
    private WeChatRecyclerViewAdapter adapter;
    private DividerItemDecoration     did;

    private void initializeRecyclerView() {
        rv = (RecyclerViewPlus) findViewById(R.id.rv_content);
        //没有任何东西的界面
        mEmptyView = findViewById(R.id.v_empty);
        initEmptyView();
        initEmptyData();
        rv.setEmptyView(mEmptyView);

        mSv = (BottomScrollView) findViewById(R.id.sv_scanend);
        mSv.setSmoothScrollingEnabled(true);
        //滑动到底的监听
        mSv.setOnScrollToBottomListener(new BottomScrollView.OnScrollToBottomListener() {
            @Override
            public void onScrollBottomListener(boolean isBottom) {
                if (isBottom && mRl.getVisibility() == View.GONE) {
                    getNewsInformation(mRecommendAdapter);
                }
            }
        });

        did = new DividerItemDecoration(this, R.drawable.recyclerview_driver_1_bg);
        //        rv.addItemDecoration(did);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);

        rv.setLayoutManager(llm);

        initializeHeadView();
        WeChatContent content = mPresenter.initData();
        content.filterDelete();
        //扫描手机中应用,是否有微信.如果手机中未安装微信该应用,就展示未发现文件界面
        if (!mPresenter.isInstallAPP()) {
            content.clear();
            ToastUtil.showToastForShort("未检测到微信应用");
        }
        adapter = new WeChatRecyclerViewAdapter(mPresenter, content, isRemove);
        adapter.addHeaderView(new RecyclerViewPlus.HeaderFooterItemAdapter.ViewHolderWrapper() {
            @Override
            protected View onCreateView(ViewGroup parent) {
                return view_head;
            }
        });
        adapter.setRecyclerListListener(new RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {

                navigationOther(position);

            }

            @Override
            public void selectState(long selectSize, boolean flag, int position) {
                /*mTvBtn.setEnabled(flag);*/
            }

            @Override
            public void selectButton(Map<Integer, Boolean> weChatInfos, int position) {
                Log.i("postion", position + "/wec=" + weChatInfos.size());
                if (adapter.getContentItemCount() >= 2) {
                    if (adapter.getContentItemViewType(1) != WeChatConstants.WECHAT_TYPE_DEFALUT) {
                        if (weChatInfos.get(position)) {
                            mTvBtn.setEnabled(true);
                            removeMap.put(position, true);
                        } else {
                            mTvBtn.setEnabled(false);
                            removeMap.put(position, false);
                        }

                    } else {
                        for (int i = 0; i < weChatInfos.size(); i++) {
                            if (weChatInfos.get(i)) {
                                mTvBtn.setEnabled(true);
                                break;
                            } else {
                                mTvBtn.setEnabled(false);
                            }
                        }
                        // TODO: 2017/6/14
                        for (int i = 0; i < weChatInfos.size(); i++) {
                            if (weChatInfos.get(i)) {
                                removeMap.put(i, true);
                            } else {
                                removeMap.put(i, false);
                            }
                        }
                    }
                } else {
                    for (int i = 0; i < weChatInfos.size(); i++) {
                        if (weChatInfos.get(i)) {
                            mTvBtn.setEnabled(true);
                            break;
                        } else {
                            mTvBtn.setEnabled(false);
                        }
                    }
                    // TODO: 2017/6/14
                    for (int i = 0; i < weChatInfos.size(); i++) {
                        if (weChatInfos.get(i)) {
                            removeMap.put(i, true);
                        } else {
                            removeMap.put(i, false);
                        }
                    }
                }


            }
        });
        rv.setAdapter(adapter);
        initNewsRecyclerView();
    }

    private void initEmptyData() {
        LinearLayoutManager layout = new LinearLayoutManager(C.get()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mEmptyRv.addItemDecoration(new LinearLayoutItemDecoration(C.get(), LinearLayoutItemDecoration.HORIZONTAL_LIST));
        mEmptyRv.setLayoutManager(layout);

        mEmptyHead = LayoutInflater.from(this).inflate(R.layout.clean_over_head, mEmptyRv, false);

        mEmptyNewsHead = mEmptyHead.findViewById(R.id.tv_news_head);
        initEmptyHead();


        /*//获取网络数据
        getNewsInformation();*/

        mEmptyRecommendAdapter = new WeChatRecommendAdapter(moreData);//更多应用推荐
        mEmptyRecommendAdapter.addHeaderView(new RecyclerViewPlus.HeaderFooterItemAdapter.ViewHolderWrapper() {
            @Override
            protected View onCreateView(ViewGroup parent) {
                return mEmptyHead;
            }
        });
        if (NetworkUtils.isNetworkAvailable(C.get())) {
            mEmptyFoot = LayoutInflater.from(C.get()).inflate(R.layout.recycler_view_layout_progress, mEmptyRv, false);
            mEmptyNewsHead.setVisibility(View.VISIBLE);
        } else {
            //            foot = LayoutInflater.from(C.get()).inflate(R.layout.footer_no_data, rv, false);
            //            foot.findViewById(R.id.footer_more).setOnClickListener(this);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DisplayUtil.dip2px(C.get(), 6));
            mEmptyFoot = new View(this);
            mEmptyFoot.setLayoutParams(lp);
            mEmptyFoot.setBackgroundColor(Color.TRANSPARENT);

            mEmptyNewsHead.setVisibility(View.GONE);
        }

        mEmptyRecommendAdapter.addFooterView(new RecyclerViewPlus.HeaderFooterItemAdapter.ViewHolderWrapper() {
            @Override
            protected View onCreateView(ViewGroup parent) {

                return mEmptyFoot;
            }
        });

        mEmptyRecommendAdapter.setRecyclerListListener(new RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                if (position >= moreData.size())
                    return;

                WeChatNewsInformation.DataBean.ResultBean info = moreData.get(position);
                String news_url = info.getNews_url();

                Intent intent = new Intent(WeChatActivity.this, WebHtmlActivity.class);
                intent.putExtra("html", news_url);
                intent.putExtra("flag", 10);
                intent.putExtra(OnekeyField.CLEAN_NEWS, "微信清理新闻");
                saveScrollState = true;

                startActivityForResult(intent,NEWS_CODE);
            }

            @Override
            public void selectState(long selectSize, boolean flag, int position) {

            }

            @Override
            public void selectButton(Map<Integer, Boolean> weChatInfos, int position) {

            }
        });
        mEmptyRv.setAdapter(mEmptyRecommendAdapter);
    }


    private void initEmptyHead() {
        ImageView icon = (ImageView) mEmptyHead.findViewById(R.id.wechat_icon);
        TextView name = (TextView) mEmptyHead.findViewById(R.id.wechat_features);
        TextView desc = (TextView) mEmptyHead.findViewById(R.id.wechat_desc);
        icon.setImageResource(R.drawable.brush_features_icon);
        name.setText(R.string.tv_junk_clean);
        desc.setText(R.string.tv_junk_desc);
        mEmptyHead.findViewById(R.id.rl_wechat).setOnClickListener(this);
        mEmptyHead.findViewById(R.id.rl_qq).setOnClickListener(this);
    }

    private void initNewsRecyclerView() {
        mRvNews = (RecyclerViewPlus) findViewById(R.id.rv_news);
        mRvNews.addItemDecoration(new LinearLayoutItemDecoration(C.get(), LinearLayoutItemDecoration.HORIZONTAL_LIST));
        LinearLayoutManager layout = new LinearLayoutManager(C.get()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mRvNews.setLayoutManager(layout);

        final View head = LayoutInflater.from(this).inflate(R.layout.clean_over_qq_head, mRvNews, false);
        mNewsHead = head.findViewById(R.id.tv_news_head);
        //更多应用推荐
        mRecommendAdapter = new WeChatRecommendAdapter(moreData);
        mRecommendAdapter.addHeaderView(new RecyclerViewPlus.HeaderFooterItemAdapter.ViewHolderWrapper() {
            @Override
            protected View onCreateView(ViewGroup parent) {
                return head;
            }
        });

        if (NetworkUtils.isNetworkAvailable(C.get())) {
            foot = LayoutInflater.from(C.get()).inflate(R.layout.recycler_view_layout_progress, rv, false);
            mNewsHead.setVisibility(View.VISIBLE);
        } else {
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DisplayUtil.dip2px(C.get(), 6));
            foot = new View(this);
            foot.setLayoutParams(lp);
            foot.setBackgroundColor(Color.TRANSPARENT);

            mNewsHead.setVisibility(View.GONE);
        }

        mRecommendAdapter.addFooterView(new RecyclerViewPlus.HeaderFooterItemAdapter.ViewHolderWrapper() {
            @Override
            protected View onCreateView(ViewGroup parent) {

                return foot;
            }
        });

        mRecommendAdapter.setRecyclerListListener(new RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                if (position >= moreData.size())
                    return;

                WeChatNewsInformation.DataBean.ResultBean info = moreData.get(position);
                String news_url = info.getNews_url();

                Intent intent = new Intent(WeChatActivity.this, WebHtmlActivity.class);
                intent.putExtra("html", news_url);
                intent.putExtra("flag", 10);
                intent.putExtra(OnekeyField.CLEAN_NEWS, "微信清理新闻");
                saveScrollState = true;

                startActivityForResult(intent,NEWS_CODE);

            }

            @Override
            public void selectState(long selectSize, boolean flag, int position) {

            }

            @Override
            public void selectButton(Map<Integer, Boolean> weChatInfos, int position) {

            }
        });

        mRvNews.setAdapter(mRecommendAdapter);
        getNewsInformation(mRecommendAdapter);
    }

    private void getNewsInformation(final WeChatRecommendAdapter recommendAdapter) {
        Map<String, String> infosPamarms = ConvertParamsUtils.getInstatnce().getParamsTwo("type", "all", "p", String.valueOf(page++));

        RetrofitService.getInstance().githubApi.createWeChatInformations(infosPamarms).enqueue(new Callback<WeChatNewsInformation>() {
            @Override
            public void onResponse(Call<WeChatNewsInformation> call, Response<WeChatNewsInformation> response) {
                if (response.raw().body() != null) {
                    WeChatNewsInformation newsInformation = response.body();
                    int count = newsInformation.getData().getCount();
                    recommendAdapter.setTotalCount(count);
                    List<WeChatNewsInformation.DataBean.ResultBean> data = newsInformation.getData().getResult();

                    moreData.addAll(data);
                    recommendAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<WeChatNewsInformation> call, Throwable t) {
            }
        });

    }

    private void initEmptyView() {
        mIv_sun = (ImageView) mEmptyView.findViewById(R.id.iv_sun);
        mIv_sun_center = (ImageView) mEmptyView.findViewById(iv_sun_center);

        mBlingBling = (ImageView) mEmptyView.findViewById(R.id.iv_blingbling);
        mBlingBling.setImageResource(R.drawable.bling_anim);

        mTv_clean_success_size = (TextView) mEmptyView.findViewById(tv_clean_success_size);
        mTv_history_clean_size = (TextView) mEmptyView.findViewById(tv_history_clean_size);
        mFl_idle = mEmptyView.findViewById(fl_idle);
        mLl_content = mEmptyView.findViewById(ll_content);
        mEmptyRv = (RecyclerViewPlus) mEmptyView.findViewById(R.id.rv_recommend);

        mEmptySv = (BottomScrollView) mEmptyView.findViewById(R.id.sv_scanfinish);
        mEmptySv.setSmoothScrollingEnabled(true);
        //滑动到底的监听
        mEmptySv.setOnScrollToBottomListener(new BottomScrollView.OnScrollToBottomListener() {
            @Override
            public void onScrollBottomListener(boolean isBottom) {
                if (isBottom) {
                    getNewsInformation(mEmptyRecommendAdapter);
                }
            }
        });
    }

    private void initBottom() {
        mRl = (RelativeLayout) findViewById(R.id.rl_wechat_btn);
        mTvBtn = (TextView) findViewById(R.id.btn_bottom_delete);
        mTvBtn.setEnabled(true);
        mTvBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2017/6/14
                int count = 0;
                Log.d("WeChatActivity", "removeMap.size():" + removeMap.size());
                if (removeMap.size() != 0) {
                    if (removeMap.size() == 1) {
                        if (removeMap.get(position)) {
                            navigationOther(0);
                            hideItem(0);
                            bottomGone();
                        }
                    }
                    if (removeMap.size() == 2) {
                        for (int i = 0; i < removeMap.size(); i++) {
                            if (removeMap.get(i)) {
                                navigationOther(i);
                                hideItem(i);
                                count++;
                            }

                        }
                        if (count == removeMap.size()) {
                            bottomGone();
                        }
                    }

                }
                if (adapter.getContentItemViewType(0) != WeChatConstants.WECHAT_TYPE_DEFALUT) {
                    bottomGone();
                    isRemove = true;
                }

                adapter.notifyDataSetChanged();
            }
        });
    }

    private void hideItem(int position) {
        ViewGroup.LayoutParams layoutParams = rv.getChildAt(position + 1).getLayoutParams();
        layoutParams.height = 0;
        rv.requestLayout();

    }

    private void bottomGone() {
        ViewCompat.animate(mRl).alpha(0).setDuration(500).setListener(new MyViewPropertyAnimatorListener() {
            @Override
            public void onAnimationEnd(View view) {
                super.onAnimationEnd(view);
                mRl.setVisibility(View.GONE);

                if (mRvNews.getVisibility() == View.GONE) {
                    newsAnimation();
                }

            }
        }).start();
    }

    private void newsAnimation() {
        final int translationY = ScreenUtil.getScreenHeight(WeChatActivity.this) - ScreenUtil.getStatusHeight(WeChatActivity.this) - rv.getBottom();
        Log.d("QQActivity", "translationY:" + translationY);
        mRvNews.setTranslationY(translationY);
        mRvNews.setVisibility(View.VISIBLE);
        ViewCompat.animate(mRvNews).translationY(0).setDuration(500).start();
    }

    private SGTextView tv_size, tv_unit;
    private View view_head;

    private void initializeHeadView() {
        view_head = getLayoutInflater().inflate(R.layout.wechat_head, rv, false);
        mWaveLoadingView = (WaveLoadingView) view_head.findViewById(R.id.waveLoadingView);
        mWaveLoadingView.setAmplitudeRatio(33);
        tv_size = (SGTextView) view_head.findViewById(R.id.tv_size);
        tv_unit = (SGTextView) view_head.findViewById(R.id.tv_unit);
        ViewTreeObserver observer = view_head.getViewTreeObserver();
        if (observer.isAlive()) {
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    headHeight = view_head.getHeight();
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        view_head.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        view_head.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }

    }

    private int headHeight;
    private int count;
    private int temp;

    @Override
    public void updateData() {

        Task.UI_THREAD_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                RecyclerView.Adapter adapter = rv.getAdapter();
                if (adapter != null) {
                    int value;
                    adapter.notifyDataSetChanged();
                    long size = mPresenter.getSize();
                    String[] unit = FormatUtils.getFileSizeAndUnit(size);
                    if (unit != null && unit.length == 2) {
                        tv_size.setText(unit[0]);
                        tv_unit.setText(unit[1]);
                        if (size < 10 * 1024 * 1024) {
                            value = 10;
                        } else if (size < 75 * 1024 * 1024) {
                            value = 30;
                        } else {
                            value = 70;
                        }
                        Log.d("CleaningFragment", "value:" + value);
                        mWaveLoadingView.setProgressValue(value);
                    }
                    if (WeChatScanHelp.getInstance().isScanFinish()) {

                        if (mSuccessCleanSize < mPresenter.getSize()) {
                            mSuccessCleanSize = mPresenter.getSize();
                        }

                    }
                    if (size == 0 && WeChatScanHelp.getInstance().isScanFinish() && count++ == 0) {
                        mPresenter.initData().clear();
                    }
                    if (mEmptyView.getVisibility() == View.VISIBLE && temp++ == 0) {
                        String str0 = FormatUtils.formatFileSize(mSuccessCleanSize);
                        String text = getString(R.string.clean_success_size, str0);
                        if (mSuccessCleanSize == 0) {
                            mTv_clean_success_size.setText(R.string.so_beautiful);
                            mTv_clean_success_size.setTextSize(22);
                        } else {
                            mTv_clean_success_size.setText(Util.getSpannableString(text));
                        }

                        String str1 = FormatUtils.formatFileSize(CleanSetSharedPreferences.getWeChatTodayCleanSize(WeChatActivity.this, 0));
                        String str2 = FormatUtils.formatFileSize(CleanSetSharedPreferences.getWeChatTotalCleanSize(WeChatActivity.this, 0));
                        mTv_history_clean_size.setText(getString(R.string.today_clean_total_clean, str1, str2));
                        Log.d("QQActivity", "刷新了界面");
                        animDisplay();
                    }

                }
            }
        });

    }

    private void navigationOther(int position) {
        if (mPresenter != null) {
            WeChatFileType type = mPresenter.get(position);
            if (type != null) {
                //                StatisticSpec.sendEvent(type.getsE());
                if (WeChatFileType.DELETE_DEFAULT == type.getDeleteStatus()) {
                    if (type.getType() == WeChatConstants.WECHAT_TYPE_DEFALUT) {
                        Log.d("WeChatActivity", "删除文件" + mPresenter.getSize());
                        // TODO: 2017/6/13 0013 存入sp
                        CleanSetSharedPreferences.setWeChatCleanLastTimeSize(C.get(), mPresenter.getSize());
                        mPresenter.remove(position);
                    } else {
                        Intent intent = new Intent(this, WeChatDetailActivity.class);
                        intent.putExtra(EXTRA_ITEM_POSITION, position);
                        startActivityForResult(intent, REQUEST_DETAIL_CHANGE);
                    }
                }
            }
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
        saveScrollState = false;
    }

    public static final int    REQUEST_DETAIL_CHANGE = 0X10;
    public static final String FLAG_CHANGE           = "flag_change";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_DETAIL_CHANGE == requestCode && resultCode == RESULT_OK) {
            boolean extra = data.getBooleanExtra(FLAG_CHANGE, false);
            if (extra && adapter != null) {
                updateData();
            }
        } else if (requestCode == NEWS_CODE) {
            //更改状态
//            saveScrollState = true;
        }
    }


    @Override
    public void setText(String str) {

    }

    @Override
    public void hideLoading() {
        updateData();

    }

    @Override
    public void showLoading() {
        updateData();
    }

    private void animDisplay() {

        getNewsInformation(mEmptyRecommendAdapter);

        final int width = DeviceInfo.getScreenWidth(this);
        int height = DeviceInfo.getScreenHeight(this);

        ViewHelper.setAlpha(mIv_sun_center, 0.0f);
        ViewHelper.setAlpha(mTv_clean_success_size, 0.0f);
        ViewHelper.setAlpha(mTv_history_clean_size, 0.0f);
        ViewHelper.setTranslationY(mLl_content, height);
        ViewHelper.setAlpha(mIv_sun, 0f);
        ViewHelper.setRotation(mIv_sun, 0f);

        mFl_idle.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.d("CleanOverFragment", "fl_idle.getMeasuredWidth():" + mFl_idle.getMeasuredWidth());
                mFl_idle.setTranslationX(width / 2 - mFl_idle.getMeasuredWidth() / 2);

                mFl_idle.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
        //星星闪烁动画
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startAnimation();
            }
        }, 500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    AnimatorSet set = initAnimSet();
                    set.start();

                } catch (Exception e) {
                    ViewHelper.setAlpha(mIv_sun_center, 1);
                    ViewHelper.setAlpha(mTv_clean_success_size, 1);
                    ViewHelper.setAlpha(mTv_history_clean_size, 1);
                    ViewHelper.setAlpha(mIv_sun, 1);
                    ViewHelper.setRotation(mIv_sun, 0f);
                    ViewHelper.setTranslationY(mLl_content, 0);
                }
            }
        }, 100);
    }

    private void startAnimation() {
        ViewCompat.animate(mFl_idle).translationX(0).setDuration(500).setListener(new MyViewPropertyAnimatorListener() {
            @Override
            public void onAnimationEnd(View view) {
                super.onAnimationEnd(view);
                blingAnim();
            }
        }).start();
    }

    private AnimatorSet initAnimSet() {
        FragmentActivity context = this;
        AnimatorSet animSet = new AnimatorSet();
        Animator anim = AnimatorInflater.loadAnimator(context, R.animator.anim_clean_complete);
        Animator anim2 = AnimatorInflater.loadAnimator(context, R.animator.anim_clean_complete_center);// 透明度+缩放动画
        Animator anim3 = AnimatorInflater.loadAnimator(context, R.animator.anim_clean_complete_alpha);
        Animator anim4 = AnimatorInflater.loadAnimator(context, R.animator.anim_clean_complete_alpha);// 透明度动画
        Animator anim5 = AnimatorInflater.loadAnimator(context, R.animator.high_light_translate);// 高亮平移
        Animator anim7 = AnimatorInflater.loadAnimator(context, R.animator.from_buttom_to_top);// Button下往上移
        anim2.setDuration(800);
        anim3.setDuration(500);
        anim4.setDuration(800);
        anim7.setDuration(500);

        anim.setTarget(mIv_sun);
        anim2.setTarget(mIv_sun_center);
        anim3.setTarget(mTv_clean_success_size);
        anim4.setTarget(mTv_history_clean_size);
        //        anim6.setTarget(btn_continue);
        anim7.setTarget(mLl_content);

        animSet.play(anim).with(anim2);
        animSet.play(anim).before(anim3);
        animSet.play(anim3).with(anim4);
        animSet.play(anim3).with(anim5);
        animSet.play(anim7).after(anim3);
        return animSet;
    }

    private void blingAnim() {
        mBlingBling.setVisibility(View.VISIBLE);

        AnimationDrawable animationDrawable = (AnimationDrawable) mBlingBling.getDrawable();
        if (animationDrawable.isRunning()) {
            animationDrawable.stop();
        }
        animationDrawable.start();

        //获取动画时间,执行之后移除
        int numberOfFrames = animationDrawable.getNumberOfFrames();
        int duration = 0;
        for (int i = 0; i < numberOfFrames; i++) {
            duration += animationDrawable.getDuration(i);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mBlingBling.setVisibility(View.GONE);
            }
        }, duration + 100);
    }


    /**
     * 开始扫描动画
     */
    @Override
    public void startAnim() {
        rv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        ani_view.postDelayed(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams params = ani_view.getLayoutParams();
                params.height = headHeight + titleHeight;
                //                ani_view.setVisibility(View.VISIBLE);
                ani_view.setVisibility(View.GONE);
                ani_view.requestLayout();

                TweenAnimationUtils.startScanTranslateAnimation(WeChatActivity.this, ani_view);
            }
        }, 100);

    }

    /**
     * 停止扫描动画
     */
    @Override
    public void stopAnim() {
        Task.UI_THREAD_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                ani_view.clearAnimation();
                ani_view.setVisibility(View.GONE);
                rv.setOnTouchListener(null);

            }
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!(adapter.getContentItemViewType(0) == WeChatConstants.WECHAT_TYPE_DEFALUT)) {
                    bottomGone();
                } else if (adapter.getContentItemCount() > 0 && mPresenter.get(0).getCurrentSize() > 0) {
                    mRl.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    public void changeDivider() {
        did.setDividerId(this, R.drawable.recyclerview_driver_10_bg);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mWaveLoadingView != null) {
            mWaveLoadingView.cancelAnimation();
        }


    }

    @Override
    protected void onDestroy() {
        mPresenter.destory();
        super.onDestroy();
    }

    @Override
    public void showError() {

    }

    @Override
    public void select(int i) {
        this.position = i;
        removeMap.put(i, true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_wechat:
                Intent intent1 = new Intent(this, SilverActivity.class);
                intent1.putExtra(OnekeyField.ONEKEYCLEAN, "");
                intent1.putExtra(OnekeyField.STATISTICS_KEY, "");
                startActivity(intent1);
                break;
            case R.id.rl_qq:
                Intent intent = new Intent(this, QQActivity.class);
                intent.putExtra(OnekeyField.ONEKEYCLEAN, "");
                intent.putExtra(OnekeyField.STATISTICS_KEY, "");
                startActivity(intent);
                break;
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

