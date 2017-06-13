package com.ymnet.onekeyclean.cleanmore.qq.activity;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
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

import com.example.commonlibrary.retrofit2service.RetrofitService;
import com.example.commonlibrary.retrofit2service.bean.NewsInformation;
import com.example.commonlibrary.utils.ConvertParamsUtils;
import com.example.commonlibrary.utils.NetworkUtils;
import com.example.commonlibrary.utils.ScreenUtil;
import com.nineoldandroids.view.ViewHelper;
import com.umeng.analytics.MobclickAgent;
import com.ymnet.killbackground.customlistener.MyViewPropertyAnimatorListener;
import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.animation.TweenAnimationUtils;
import com.ymnet.onekeyclean.cleanmore.constants.QQConstants;
import com.ymnet.onekeyclean.cleanmore.customview.DividerItemDecoration;
import com.ymnet.onekeyclean.cleanmore.customview.RecyclerViewPlus;
import com.ymnet.onekeyclean.cleanmore.junk.SilverActivity;
import com.ymnet.onekeyclean.cleanmore.junk.adapter.RecommendAdapter;
import com.ymnet.onekeyclean.cleanmore.qq.QQDetailActivity;
import com.ymnet.onekeyclean.cleanmore.qq.QQScanHelp;
import com.ymnet.onekeyclean.cleanmore.qq.adapter.QQRecyclerViewAdapter;
import com.ymnet.onekeyclean.cleanmore.qq.mode.QQContent;
import com.ymnet.onekeyclean.cleanmore.qq.mode.QQFileType;
import com.ymnet.onekeyclean.cleanmore.qq.presenter.QQPresenter;
import com.ymnet.onekeyclean.cleanmore.qq.presenter.QQPresenterImpl;
import com.ymnet.onekeyclean.cleanmore.utils.C;
import com.ymnet.onekeyclean.cleanmore.utils.CleanSetSharedPreferences;
import com.ymnet.onekeyclean.cleanmore.utils.DisplayUtil;
import com.ymnet.onekeyclean.cleanmore.utils.FormatUtils;
import com.ymnet.onekeyclean.cleanmore.utils.OnekeyField;
import com.ymnet.onekeyclean.cleanmore.utils.StatisticMob;
import com.ymnet.onekeyclean.cleanmore.utils.ToastUtil;
import com.ymnet.onekeyclean.cleanmore.utils.Util;
import com.ymnet.onekeyclean.cleanmore.web.WebHtmlActivity;
import com.ymnet.onekeyclean.cleanmore.wechat.WeChatActivity;
import com.ymnet.onekeyclean.cleanmore.wechat.device.DeviceInfo;
import com.ymnet.onekeyclean.cleanmore.wechat.listener.RecyclerViewClickListener;
import com.ymnet.onekeyclean.cleanmore.wechat.view.BaseFragmentActivity;
import com.ymnet.onekeyclean.cleanmore.widget.BottomScrollView;
import com.ymnet.onekeyclean.cleanmore.widget.LinearLayoutItemDecoration;
import com.ymnet.onekeyclean.cleanmore.widget.SGTextView;
import com.ymnet.onekeyclean.cleanmore.widget.WaveLoadingView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bolts.Task;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ymnet.onekeyclean.R.id.fl_idle;
import static com.ymnet.onekeyclean.R.id.iv_sun_center;
import static com.ymnet.onekeyclean.R.id.ll_content;
import static com.ymnet.onekeyclean.R.id.tv_clean_success_size;
import static com.ymnet.onekeyclean.R.id.tv_history_clean_size;


public class QQActivity extends BaseFragmentActivity implements QQMVPView, View.OnClickListener {

    public final static String EXTRA_ITEM_POSITION = "qq_position";
    public static final String QQ_FILE_TYPE        = "qq_filetype";
    private QQPresenter           mPresenter;
    private QQRecyclerViewAdapter adapter;
    private View                  ani_view;
    private String TAG = "QQActivity";
    private WaveLoadingView  mWaveLoadingView;
    private RelativeLayout   mRl;
    private TextView         mTvBtn;
    private boolean          isRemove;
    private RecyclerViewPlus mRvNews;
    private View             head;
    private View             mNewsHead;
    private RecommendAdapter mRecommendAdapter;
    private List<NewsInformation.DataBean> moreData = new ArrayList<>();
    private View foot;
    //信息流相关
    private int page = 1;
    private ImageView        mIv_sun;
    private ImageView        mIv_sun_center;
    private ImageView        mBlingBling;
    private TextView         mTv_clean_success_size;
    private TextView         mTv_history_clean_size;
    private View             mEmptyView;
    private View             mFl_idle;
    private View             mLl_content;
    private RecyclerViewPlus mEmptyRv;
    private RecommendAdapter mEmptyRecommendAdapter;
    private View             mEmptyHead;
    private View             mEmptyNewsHead;
    private View             mEmptyFoot;
    private long mSuccessCleanSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qq);

        Map<String, String> m = new HashMap<>();
        m.put(OnekeyField.ONEKEYCLEAN, "QQ清理");
        MobclickAgent.onEvent(this, StatisticMob.STATISTIC_ID, m);

        C.setContext(getApplication());
        mPresenter = new QQPresenterImpl(this);
        initTitleBar();
        initializeRecyclerView();
        initBottom();
        ani_view = findViewById(R.id.ani_view);
    }

    private void initBottom() {
        mRl = (RelativeLayout) findViewById(R.id.rl_qq_btn);
        mTvBtn = (TextView) findViewById(R.id.btn_bottom_delete);
        mTvBtn.setEnabled(true);
        mTvBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationOther(0);
                if ((adapter.getContentItemViewType(0) == QQConstants.QQ_TYPE_DEFALUT)) {
                    hideItem();
                }

            }
        });
    }

    private void hideItem() {
        ViewGroup.LayoutParams layoutParams = rv.getChildAt(1).getLayoutParams();
        layoutParams.height = 0;
        rv.requestLayout();

        bottomGone();
        isRemove = true;
        adapter.notifyDataSetChanged();
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
        final int translationY = ScreenUtil.getScreenHeight(QQActivity.this) - ScreenUtil.getStatusHeight(QQActivity.this) - rv.getBottom();
        Log.d("QQActivity", "translationY:" + translationY);
        mRvNews.setTranslationY(translationY);
        mRvNews.setVisibility(View.VISIBLE);
        ViewCompat.animate(mRvNews).translationY(0).setDuration(500).start();
    }

    private RecyclerViewPlus      rv;
    private DividerItemDecoration did;

    private void initializeRecyclerView() {
        rv = (RecyclerViewPlus) findViewById(R.id.rv_content);
        mEmptyView = findViewById(R.id.v_empty);
        initEmptyView();
        initEmptyData();
        rv.setEmptyView(mEmptyView);

        BottomScrollView sv = (BottomScrollView) findViewById(R.id.sv_scanend);
        sv.setSmoothScrollingEnabled(true);
        //滑动到底的监听
        sv.setOnScrollToBottomListener(new BottomScrollView.OnScrollToBottomListener() {
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
        QQContent content = mPresenter.initData();
        content.filterDelete();
        //扫描手机中应用,是否有QQ.如果手机中未安装QQ该应用,就展示未发现文件界面
        if (!mPresenter.isInstallAPP()) {
            content.clear();
            ToastUtil.showToastForShort("未检测到QQ应用");
        }
        adapter = new QQRecyclerViewAdapter(mPresenter, content, isRemove);
        adapter.addHeaderView(new RecyclerViewPlus.HeaderFooterItemAdapter.ViewHolderWrapper() {
            @Override
            protected View onCreateView(ViewGroup parent) {
                return view_head;
            }
        });
        adapter.setRecyclerListListener(new RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                Log.d(TAG, "onClick: " + position);
                navigationOther(position);
            }

            @Override
            public void selectState(long selectSize, boolean flag) {
                mTvBtn.setEnabled(flag);
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

        mEmptyRecommendAdapter = new RecommendAdapter(moreData);//更多应用推荐
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

                NewsInformation.DataBean info = moreData.get(position);
                String news_url = info.getNews_url();

                Intent intent = new Intent(QQActivity.this, WebHtmlActivity.class);
                intent.putExtra("html", news_url);
                intent.putExtra("flag", 10);
                startActivity(intent);

            }

            @Override
            public void selectState(long selectSize, boolean flag) {

            }
        });
        mEmptyRv.setAdapter(mEmptyRecommendAdapter);
    }

    private void initEmptyHead() {
        ImageView icon = (ImageView) mEmptyHead.findViewById(R.id.qq_icon);
        TextView name = (TextView) mEmptyHead.findViewById(R.id.qq_features);
        TextView desc = (TextView) mEmptyHead.findViewById(R.id.qq_desc);
        icon.setImageResource(R.drawable.brush_features_icon);
        name.setText(R.string.tv_junk_clean);
        //        desc.setText(R.string.);
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

        head = LayoutInflater.from(this).inflate(R.layout.clean_over_qq_head, mRvNews, false);
        mNewsHead = head.findViewById(R.id.tv_news_head);
        //更多应用推荐
        mRecommendAdapter = new RecommendAdapter(moreData);
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

                NewsInformation.DataBean info = moreData.get(position);
                String news_url = info.getNews_url();

                Intent intent = new Intent(QQActivity.this, WebHtmlActivity.class);
                intent.putExtra("html", news_url);
                intent.putExtra("flag", 10);
                startActivity(intent);

            }

            @Override
            public void selectState(long selectSize, boolean flag) {

            }
        });

        mRvNews.setAdapter(mRecommendAdapter);
        getNewsInformation(mRecommendAdapter);
    }

    //网络获取新闻数据
    private void getNewsInformation(final RecommendAdapter recommendAdapter) {
        Map<String, String> infosPamarms = ConvertParamsUtils.getInstatnce().getParamsTwo("type", "all", "p", String.valueOf(page++));

        RetrofitService.getInstance().githubApi.createInfomationsTwo(infosPamarms).enqueue(new Callback<NewsInformation>() {
            @Override
            public void onResponse(Call<NewsInformation> call, Response<NewsInformation> response) {
                if (response.raw().body() != null) {
                    NewsInformation newsInformation = response.body();
                    int count = newsInformation.getCount();
                    recommendAdapter.setTotalCount(count);
                    List<NewsInformation.DataBean> data = newsInformation.getData();
                    Log.d(TAG, "onResponse: data:" + data);

                    moreData.addAll(data);
                    recommendAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<NewsInformation> call, Throwable t) {
            }
        });

    }

    private void navigationOther(int position) {
        if (mPresenter != null) {
            QQFileType type = mPresenter.get(position);
            if (type != null) {
                Log.d(TAG, "navigationOther: " + "type不为null");
                //                StatisticSpec.sendEvent(type.getsE());
                if (QQFileType.DELETE_DEFAULT == type.getDeleteStatus()) {
                    if (type.getType() == QQConstants.QQ_TYPE_DEFALUT) {
                        Log.d("QQActivity", "删除文件" + mPresenter.getSize());
                        // TODO: 2017/6/13 0013 存入sp
                        CleanSetSharedPreferences.setQQCleanLastTimeSize(C.get(), mPresenter.getSize());
                        mPresenter.remove(position);
                    } else {
                        Log.d(TAG, "navigationOther: " + "跳转");
                        Intent intent = new Intent(this, QQDetailActivity.class);
                        intent.putExtra(EXTRA_ITEM_POSITION, position);
                        intent.putExtra(QQ_FILE_TYPE, type);
                        startActivityForResult(intent, REQUEST_DETAIL_CHANGE);
                    }
                }
            }
        }
    }

    public static final int    REQUEST_DETAIL_CHANGE = 0X10;
    public static final String FLAG_CHANGE           = "flag_change";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("QQActivity", "返回结果");
        if (REQUEST_DETAIL_CHANGE == requestCode && resultCode == RESULT_OK) {
            boolean extra = data.getBooleanExtra(FLAG_CHANGE, false);
            Log.d("QQActivity", "extra:" + extra);
            if (extra && adapter != null) {
                updateData();
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    private SGTextView tv_size, tv_unit;
    private View view_head;
    private int  headHeight;

    private void initializeHeadView() {
        view_head = getLayoutInflater().inflate(R.layout.qq_head, rv, false);
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

    private void initEmptyView() {
       /* View btn = emptyView.findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QQActivity.this.finish();
                Intent intent = new Intent(QQActivity.this, SilverActivity.class);
                startActivity(intent);
            }
        });*/

        mIv_sun = (ImageView) mEmptyView.findViewById(R.id.iv_sun);
        mIv_sun_center = (ImageView) mEmptyView.findViewById(iv_sun_center);

        mBlingBling = (ImageView) mEmptyView.findViewById(R.id.iv_blingbling);
        mBlingBling.setImageResource(R.drawable.bling_anim);

        mTv_clean_success_size = (TextView) mEmptyView.findViewById(tv_clean_success_size);
        mTv_history_clean_size = (TextView) mEmptyView.findViewById(tv_history_clean_size);
        mFl_idle = mEmptyView.findViewById(fl_idle);
        mLl_content = mEmptyView.findViewById(ll_content);
        mEmptyRv = (RecyclerViewPlus) mEmptyView.findViewById(R.id.rv_recommend);

        BottomScrollView emptySv = (BottomScrollView) mEmptyView.findViewById(R.id.sv_scanfinish);
        emptySv.setSmoothScrollingEnabled(true);
        //滑动到底的监听
        emptySv.setOnScrollToBottomListener(new BottomScrollView.OnScrollToBottomListener() {
            @Override
            public void onScrollBottomListener(boolean isBottom) {
                if (isBottom) {
                    getNewsInformation(mEmptyRecommendAdapter);
                }
            }
        });
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

                mFl_idle.getViewTreeObserver().removeOnGlobalLayoutListener(this);
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
        Animator anim = AnimatorInflater.loadAnimator(context,
                R.animator.anim_clean_complete);
        Animator anim2 = AnimatorInflater.loadAnimator(context,
                R.animator.anim_clean_complete_center);// 透明度+缩放动画
        Animator anim3 = AnimatorInflater.loadAnimator(context,
                R.animator.anim_clean_complete_alpha);
        Animator anim4 = AnimatorInflater.loadAnimator(context,
                R.animator.anim_clean_complete_alpha);// 透明度动画
        Animator anim5 = AnimatorInflater.loadAnimator(context,
                R.animator.high_light_translate);// 高亮平移
        Animator anim7 = AnimatorInflater.loadAnimator(context,
                R.animator.from_buttom_to_top);// Button下往上移
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

    View ll_title;
    int  titleHeight;

    private void initTitleBar() {
        mWaveLoadingView = (WaveLoadingView) findViewById(R.id.waveLoadingView);

        ll_title = findViewById(R.id.ll_title);
        TextView left_btn = (TextView) findViewById(R.id.junk_title_txt);
        left_btn.setText(R.string.qq_clean);
        left_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QQActivity.this.finish();
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
    }

    int count = 0;
    int temp  = 0;

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
                    if (QQScanHelp.getInstance().isScanFinish()) {

                        if (mSuccessCleanSize < mPresenter.getSize()) {
                            mSuccessCleanSize = mPresenter.getSize();
                        }

                    }
                    if (size == 0 && QQScanHelp.getInstance().isScanFinish() && count++ == 0) {
                        //todo adapter更换布局
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

                        String str1 = FormatUtils.formatFileSize(CleanSetSharedPreferences.getQQTodayCleanSize(QQActivity.this, 0));
                        String str2 = FormatUtils.formatFileSize(CleanSetSharedPreferences.getQQTotalCleanSize(QQActivity.this, 0));
                        mTv_history_clean_size.setText(getString(R.string.today_clean_total_clean, str1, str2));
                        Log.d("QQActivity", "刷新了界面");
                        animDisplay();
                    }
                }
            }
        });
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

    /**
     * 标记是否做扫描动画
     */
    boolean end = true;

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

                TweenAnimationUtils.startScanTranslateAnimation(QQActivity.this, ani_view);
            }
        }, 100);
    }

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
                Log.d("QQActivity", "adapter.getContentItemCount():" + adapter.getContentItemCount());
                if (!(adapter.getContentItemViewType(0) == QQConstants.QQ_TYPE_DEFALUT)) {
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
    public void showError() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mWaveLoadingView != null) {
            mWaveLoadingView.cancelAnimation();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_wechat:
                startActivity(new Intent(this, WeChatActivity.class));
                break;
            case R.id.rl_qq:
                startActivity(new Intent(this, SilverActivity.class));
                break;
        }
    }
}
