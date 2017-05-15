package com.ymnet.onekeyclean.cleanmore.qq.activity;

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

import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.animation.TweenAnimationUtils;
import com.ymnet.onekeyclean.cleanmore.constants.QQConstants;
import com.ymnet.onekeyclean.cleanmore.customview.DividerItemDecoration;
import com.ymnet.onekeyclean.cleanmore.customview.RecyclerViewPlus;
import com.ymnet.onekeyclean.cleanmore.junk.SilverActivity;
import com.ymnet.onekeyclean.cleanmore.qq.QQDetailActivity;
import com.ymnet.onekeyclean.cleanmore.qq.adapter.QQRecyclerViewAdapter;
import com.ymnet.onekeyclean.cleanmore.qq.mode.QQContent;
import com.ymnet.onekeyclean.cleanmore.qq.mode.QQFileType;
import com.ymnet.onekeyclean.cleanmore.qq.presenter.QQPresenter;
import com.ymnet.onekeyclean.cleanmore.qq.presenter.QQPresenterImpl;
import com.ymnet.onekeyclean.cleanmore.utils.C;
import com.ymnet.onekeyclean.cleanmore.utils.FormatUtils;
import com.ymnet.onekeyclean.cleanmore.wechat.listener.RecyclerViewClickListener;
import com.ymnet.onekeyclean.cleanmore.wechat.view.BaseFragmentActivity;
import com.ymnet.onekeyclean.cleanmore.widget.SGTextView;

import bolts.Task;


public class QQActivity extends BaseFragmentActivity implements QQMVPView {

    public final static String EXTRA_ITEM_POSITION = "qq_position";
    public static final String QQ_FILE_TYPE         = "qq_filetype";
    private QQPresenter           mPresenter;
    private QQRecyclerViewAdapter adapter;
    private View                  ani_view;
    private String TAG="QQActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qq);
        C.setContext(getApplication());
        mPresenter = new QQPresenterImpl(this);
        initTitleBar();
        initializeRecyclerView();
        ani_view = findViewById(R.id.ani_view);
    }
    private RecyclerViewPlus      rv;
    private DividerItemDecoration did;

    private void initializeRecyclerView() {
        rv = (RecyclerViewPlus) findViewById(R.id.rv_content);
        View emptyView = findViewById(R.id.v_empty);
        initEmptyView(emptyView);
        rv.setEmptyView(emptyView);
        did = new DividerItemDecoration(this, R.drawable.recyclerview_driver_1_bg);
        rv.addItemDecoration(did);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);

        rv.setLayoutManager(llm);

        initializeHeadView();
        QQContent content = mPresenter.initData();
        content.filterDelete();
        adapter = new QQRecyclerViewAdapter(mPresenter, content);
        adapter.addHeaderView(new RecyclerViewPlus.HeaderFooterItemAdapter.ViewHolderWrapper() {
            @Override
            protected View onCreateView(ViewGroup parent) {
                return view_head;
            }
        });
        adapter.setRecyclerListListener(new RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                Log.d(TAG, "onClick: "+position);
                navigationOther(position);
            }
        });
        rv.setAdapter(adapter);
    }


    private void navigationOther(int position) {
        if (mPresenter != null) {
            QQFileType type = mPresenter.get(position);
            if (type != null) {
                Log.d(TAG, "navigationOther: "+"type不为null");
                //                StatisticSpec.sendEvent(type.getsE());
                if (QQFileType.DELETE_DEFAULT == type.getDeleteStatus()) {
                    if (type.getType() == QQConstants.QQ_TYPE_DEFALUT) {
                        mPresenter.remove(position);
                        Log.d(TAG, "navigationOther: "+"不跳转,类型为QQ_TYPE_DEFALUT");
                    } else {
                        Log.d(TAG, "navigationOther: "+"跳转");
                        Intent intent = new Intent(this, QQDetailActivity.class);
                        intent.putExtra(EXTRA_ITEM_POSITION, position);
                        intent.putExtra(QQ_FILE_TYPE,type);
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

    private SGTextView tv_size, tv_unit;
    private View view_head;
    private int headHeight;

    private void initializeHeadView() {
        view_head = getLayoutInflater().inflate(R.layout.qq_head, rv, false);
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

    private void initEmptyView(View emptyView) {
        View btn = emptyView.findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QQActivity.this.finish();
                Intent intent = new Intent(QQActivity.this, SilverActivity.class);
                startActivity(intent);
            }
        });
    }

    View ll_title;
    int  titleHeight;

    private void initTitleBar() {
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

    @Override
    public void updateData() {
        Task.UI_THREAD_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                RecyclerView.Adapter adapter = rv.getAdapter();
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                    long size = mPresenter.getSize();
                    String[] unit = FormatUtils.getFileSizeAndUnit(size);
                    if (unit != null && unit.length == 2) {
                        tv_size.setText(unit[0]);
                        tv_unit.setText(unit[1]);
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
                ani_view.setVisibility(View.VISIBLE);
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
    }

    @Override
    public void changeDivider() {
        did.setDividerId(this, R.drawable.recyclerview_driver_10_bg);
    }

    @Override
    public void showError() {

    }
}