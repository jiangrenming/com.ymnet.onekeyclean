package com.ymnet.onekeyclean.cleanmore.wechat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.animation.TweenAnimationUtils;
import com.ymnet.onekeyclean.cleanmore.constants.WeChatConstants;
import com.ymnet.onekeyclean.cleanmore.customview.DividerItemDecoration;
import com.ymnet.onekeyclean.cleanmore.customview.RecyclerViewPlus;
import com.ymnet.onekeyclean.cleanmore.junk.SilverActivity;
import com.ymnet.onekeyclean.cleanmore.utils.C;
import com.ymnet.onekeyclean.cleanmore.utils.FormatUtils;
import com.ymnet.onekeyclean.cleanmore.utils.OnekeyField;
import com.ymnet.onekeyclean.cleanmore.utils.StatisticMob;
import com.ymnet.onekeyclean.cleanmore.wechat.adapter.WeChatRecyclerViewAdapter;
import com.ymnet.onekeyclean.cleanmore.wechat.listener.RecyclerViewClickListener;
import com.ymnet.onekeyclean.cleanmore.wechat.mode.WeChatContent;
import com.ymnet.onekeyclean.cleanmore.wechat.mode.WeChatFileType;
import com.ymnet.onekeyclean.cleanmore.wechat.presenter.WeChatPresenter;
import com.ymnet.onekeyclean.cleanmore.wechat.presenter.WeChatPresenterImpl;
import com.ymnet.onekeyclean.cleanmore.wechat.view.BaseFragmentActivity;
import com.ymnet.onekeyclean.cleanmore.wechat.view.WeChatMvpView;
import com.ymnet.onekeyclean.cleanmore.widget.SGTextView;
import com.ymnet.onekeyclean.cleanmore.widget.WaveLoadingView;

import java.util.HashMap;
import java.util.Map;

import bolts.Task;


public class WeChatActivity extends BaseFragmentActivity implements WeChatMvpView {
    WeChatPresenter mPresenter;
    public final static String EXTRA_ITEM_POSITION = "wechat_position";
    public final static String WECHAT_GUIDE        = "wechat_guide";
    private WaveLoadingView mWaveLoadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_we_chat);

        Map<String, String> m = new HashMap<>();
        m.put(OnekeyField.ONEKEYCLEAN, "微信清理");
        MobclickAgent.onEvent(this, StatisticMob.STATISTIC_ID, m);

        C.setContext(getApplication());
        mPresenter = new WeChatPresenterImpl(this);
        initTitleBar();
        initializeRecyclerView();
        ani_view = findViewById(R.id.ani_view);
    }

    /**
     * 标记是否做扫描动画
     */
    boolean end = true;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
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
                WeChatActivity.this.finish();
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

    private RecyclerViewPlus          rv;
    private WeChatRecyclerViewAdapter adapter;
    private DividerItemDecoration     did;

    private void initializeRecyclerView() {
        rv = (RecyclerViewPlus) findViewById(R.id.rv_content);
        //没有任何东西的界面
        View emptyView = findViewById(R.id.v_empty);
        initEmptyView(emptyView);
        rv.setEmptyView(emptyView);
        did = new DividerItemDecoration(this, R.drawable.recyclerview_driver_1_bg);
        rv.addItemDecoration(did);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);

        rv.setLayoutManager(llm);

        initializeHeadView();
        WeChatContent content = mPresenter.initData();
        content.filterDelete();
        //扫描手机中应用,是否有微信.如果手机中未安装微信该应用,就展示未发现文件界面
        if (!mPresenter.isInstallAPP()) {
            content.clear();
        }

        adapter = new WeChatRecyclerViewAdapter(mPresenter, content);
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
        });
        rv.setAdapter(adapter);

    }

    private void initEmptyView(View emptyView) {
        View btn = emptyView.findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //                StartCleanActUtil.startCleanActivity(WeChatActivity.this);
                WeChatActivity.this.finish();
                Intent intent = new Intent(WeChatActivity.this, SilverActivity.class);
                startActivity(intent);
            }
        });

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
}

