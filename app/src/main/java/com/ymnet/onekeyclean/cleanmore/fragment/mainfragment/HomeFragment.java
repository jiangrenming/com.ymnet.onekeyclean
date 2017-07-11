package com.ymnet.onekeyclean.cleanmore.fragment.mainfragment;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.ymnet.killbackground.view.CleanActivity;
import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.constants.TimeConstants;
import com.ymnet.onekeyclean.cleanmore.customview.CustomGridView;
import com.ymnet.onekeyclean.cleanmore.customview.ProgressWheel;
import com.ymnet.onekeyclean.cleanmore.customview.RecyclerViewPlus;
import com.ymnet.onekeyclean.cleanmore.datacenter.DataCenterObserver;
import com.ymnet.onekeyclean.cleanmore.filebrowser.FileBrowserUtil;
import com.ymnet.onekeyclean.cleanmore.fragment.activity.ApkManagerActivity;
import com.ymnet.onekeyclean.cleanmore.fragment.activity.FileManagerActivity;
import com.ymnet.onekeyclean.cleanmore.fragment.activity.MusicManagerActivity;
import com.ymnet.onekeyclean.cleanmore.fragment.activity.PackageManagerActivity;
import com.ymnet.onekeyclean.cleanmore.fragment.activity.PicManagerActivity;
import com.ymnet.onekeyclean.cleanmore.fragment.activity.VideoManagerActivity;
import com.ymnet.onekeyclean.cleanmore.fragment.filemanager.adapter.FileManagerAdapter;
import com.ymnet.onekeyclean.cleanmore.junk.SilverActivity;
import com.ymnet.onekeyclean.cleanmore.qq.activity.QQActivity;
import com.ymnet.onekeyclean.cleanmore.uninstall.activity.UninstallActivity;
import com.ymnet.onekeyclean.cleanmore.utils.C;
import com.ymnet.onekeyclean.cleanmore.utils.CleanSetSharedPreferences;
import com.ymnet.onekeyclean.cleanmore.utils.OnekeyField;
import com.ymnet.onekeyclean.cleanmore.utils.StatisticMob;
import com.ymnet.onekeyclean.cleanmore.utils.Util;
import com.ymnet.onekeyclean.cleanmore.wechat.WeChatActivity;
import com.ymnet.onekeyclean.cleanmore.wechat.listener.RecyclerViewClickListener;
import com.ymnet.onekeyclean.cleanmore.widget.LinearLayoutItemDecoration;
import com.ymnet.onekeyclean.cleanmore.widget.StickyLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment implements View.OnClickListener, StickyLayout.OnGiveUpTouchEventListener {

    private static final int    CLEAN_CODE     = 13;
    private static final int    SILVER_CODE    = 12;
    private static final int    WECHAT_CODE    = 14;
    private static final int    UNINSTALL_CODE = 15;
    private static final int    QQ_CODE        = 16;
    private static final int    FILE_CODE      = 20;
    private static final int    PM_CODE        = 21;
    private static final int    PP_CODE        = 22;
    private static final int    VM_CODE        = 23;
    private static final int    AM_CODE        = 24;
    private static final int    PKM_CODE       = 25;
    private              String TAG            = "HomeFragment";

    private Button                         mProgressButton;
    private RecyclerViewPlus               mRecyclerView;
    private FileManagerAdapter             mAdapter;
    private View                           mHeadContent;
    private StickyLayout                   mStickLayout;
    private int                            mWaveHeight;
    private WeakReference<HomeFragment>    theFragment;
    private View                           mStickyHead;
    private ArrayList<Map<String, Object>> mHomeMainFunctionList;
    private Handler mHandler = new MyHandler(this);
    private TextView      tv_junk_state;
    private ProgressWheel mProgressWheel;
    private TextView      tv_memory_size_desc;
    private View          mView_head;
    private View mView_foot;
    //    private View          mFlHomeBottom;

    class MyHandler extends Handler {

        public MyHandler(HomeFragment fragment) {
            theFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }
    }

    private int[]    mMainFunctionIcon = {
            R.drawable.onekey_home_main,
            R.drawable.wechat_home_main,
            R.drawable.filemanager_home_main,
            R.drawable.qq_home_main};
    private String[] mMainFunctionName = {"手机加速", "微信清理", "软件管理", "QQ清理"};

    public HomeFragment() {
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);
        initData();
        return view;
    }

    private void initData() {
        FileBrowserUtil.SDCardInfo info = FileBrowserUtil.getSDCardInfo();
        String free = Util.formatFileSizeToPic((info.total - info.free));
        String total = Util.formatFileSizeToPic(info.total);
        initProgressWheel(info);
        if (checkHasCleanCache()) {
            tv_junk_state.setText(R.string.so_clear);
        } else {
            tv_junk_state.setText(R.string.tv_junk_desc);
        }
        String sAgeFormat = getResources().getString(R.string.tv_memory_size_desc);
        String content = String.format(sAgeFormat, free, total);
        tv_memory_size_desc.setText(content);
    }

    private void initView(View view) {

        mStickyHead = view.findViewById(R.id.sticky_header);
        mHeadContent = view.findViewById(R.id.ll_head_content);
//        mFlHomeBottom = view.findViewById(R.id.fl_home_arrow);

        mStickLayout = (StickyLayout) view.findViewById(R.id.sticky_layout);
        mStickLayout.setOnGiveUpTouchEventListener(this);
        mStickLayout.setSticky(true);
        mStickLayout.setHeightChangeListener(new StickyLayout.HeightChangeListener() {
            @Override
            public void notifyChange(float scale) {

                setHeadContentSize(scale);
            }
        });

        mRecyclerView = (RecyclerViewPlus) view.findViewById(R.id.rvp_morefunction);
        mAdapter = new FileManagerAdapter(C.get());
        mAdapter.addHeaderView(new RecyclerViewPlus.HeaderFooterItemAdapter.ViewHolderWrapper() {
            @Override
            protected View onCreateView(ViewGroup parent) {
                return mView_head;
            }
        });
        mAdapter.addFooterView(new RecyclerViewPlus.HeaderFooterItemAdapter.ViewHolderWrapper(){
            @Override
            protected View onCreateView(ViewGroup parent) {
                return mView_foot;
            }
        });
        final LinearLayoutManager layout = new LinearLayoutManager(C.get());
        mRecyclerView.setLayoutManager(layout);
        initHeadView();

        mRecyclerView.addItemDecoration(new LinearLayoutItemDecoration(C.get(), LinearLayoutItemDecoration.HORIZONTAL_LIST));
        mAdapter.setmRecyclerViewClickListener(new RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                forwardSendPage(position);
            }

            @Override
            public void selectState(long selectSize, boolean flag, int position) {

            }

            @Override
            public void selectButton(Map<Integer, Boolean> weChatInfos, int position) {

            }
        });

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.canScrollVertically(1);

        mProgressWheel = (ProgressWheel) view.findViewById(R.id.pw_memory_size);
        tv_junk_state = (TextView) view.findViewById(R.id.tv_junk_state);
        tv_memory_size_desc = (TextView) view.findViewById(R.id.tv_memory_size_desc);
        mProgressButton = (Button) view.findViewById(R.id.pb_ram_prompt);
        mProgressButton.setOnClickListener(this);

        mAdapter.notifyDataSetChanged();

        mStickyHead.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mWaveHeight = mStickyHead.getMeasuredHeight();
                mStickyHead.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });

        mStickLayout.setScroll(new StickyLayout.IpmlScrollChangListener() {
            @Override
            public boolean isReadyForPull() {

                return isOnTop(mRecyclerView);
            }

            @Override
            public void isMoving() {
                /*//监控主页面是否在顶部的状态
                int status = mStickLayout.getStatus();
                if (status == 1) {
                    mFlHomeBottom.setVisibility(View.VISIBLE);
                } else {
                    mFlHomeBottom.setVisibility(View.GONE);
                }*/
            }
        });


    }

    public static boolean isOnTop(ViewGroup viewGroup) {
        int[] groupLocation = new int[2];
        viewGroup.getLocationOnScreen(groupLocation);
        int[] itemLocation = new int[2];
        if (viewGroup.getChildAt(0) != null) {
            viewGroup.getChildAt(0).getLocationOnScreen(itemLocation);
            return groupLocation[1] == itemLocation[1];
        }
        return false;
    }

    private void initHeadView() {
        mView_head = getActivity().getLayoutInflater().inflate(R.layout.home_head, mRecyclerView, false);
        mView_foot = getActivity().getLayoutInflater().inflate(R.layout.home_foot, mRecyclerView, false);
        //gridview
        CustomGridView gridView = (CustomGridView) mView_head.findViewById(R.id.gv_mainfunction);
        mHomeMainFunctionList = new ArrayList<>();
        getData();
        String[] from = {"image", "text"};
        int[] to = {R.id.iv_item_mainfunction, R.id.tv_item_mainfunction};
        SimpleAdapter simpleAdapter = new SimpleAdapter(getContext(), mHomeMainFunctionList, R.layout.home_function_item, from, to);

        gridView.setAdapter(simpleAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent intent = new Intent(C.get(), CleanActivity.class);
                        intent.putExtra(OnekeyField.ONEKEYCLEAN, "home");
                        intent.putExtra(OnekeyField.STATISTICS_KEY, StatisticMob.STATISTIC_HOME_ID);
                        startActivityForResult(intent, CLEAN_CODE);
                        break;
                    case 1:
                        Intent intentWeChat = new Intent(C.get(), WeChatActivity.class);
                        intentWeChat.putExtra(OnekeyField.ONEKEYCLEAN, "微信清理");
                        intentWeChat.putExtra(OnekeyField.STATISTICS_KEY, StatisticMob.STATISTIC_HOME_ID);
                        intentWeChat.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivityForResult(intentWeChat, WECHAT_CODE);
                        break;
                    case 2:
                        Intent intentUninstall = new Intent(C.get(), UninstallActivity.class);
                        intentUninstall.putExtra(OnekeyField.ONEKEYCLEAN, "软件管理");
                        intentUninstall.putExtra(OnekeyField.STATISTICS_KEY, StatisticMob.STATISTIC_HOME_ID);
                        intentUninstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivityForResult(intentUninstall, UNINSTALL_CODE);
                        break;
                    case 3:
                        Intent intentQQ = new Intent(C.get(), QQActivity.class);
                        intentQQ.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intentQQ.putExtra(OnekeyField.ONEKEYCLEAN, "QQ清理");
                        intentQQ.putExtra(OnekeyField.STATISTICS_KEY, StatisticMob.STATISTIC_HOME_ID);
                        startActivityForResult(intentQQ, QQ_CODE);
                        break;
                }
            }
        });
    }

    private void initProgressWheel(FileBrowserUtil.SDCardInfo info) {
        float percent = ((info.total - info.free) * 100 / info.total + 0.5f);
        drawPercent(percent);
    }

    private void drawPercent(float percent) {
        mProgressWheel.setText((int) (percent + 0.5f) + "%\n已用");
        ValueAnimator va = ValueAnimator.ofFloat(0, percent * 100f);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentValue = (float) animation.getAnimatedValue();
                float fraction = currentValue * 360f / 10000f + 0.5f;
                mProgressWheel.setProgress((fraction));
            }
        });
        va.setInterpolator(new LinearInterpolator());
        va.setDuration(800).start();
    }

    //垃圾清理已清理过的状态判断
    private boolean checkHasCleanCache() {
        long lastTime = com.ymnet.onekeyclean.cleanmore.cacheclean.Util.getLaseCleanDate(C.get(), System.currentTimeMillis());
        boolean hasCache = CleanSetSharedPreferences.getLastSet(C.get(), CleanSetSharedPreferences.CLEAN_RESULT_CACHE, false);
        boolean hasUpdate = DataCenterObserver.get(C.get()).isRefreshCleanActivity();
        /**
         *  最后的清理时间小于3分钟并且上次是全部清理的 并且3分钟内白名单项没有发生改变
         */
        if (lastTime <= 3 * TimeConstants.MINUTE && hasCache && !hasUpdate) {

            return true;
        } else {
            DataCenterObserver.get(C.get()).setRefreshCleanActivity(false);
            CleanSetSharedPreferences.setLastSet(C.get(), CleanSetSharedPreferences.CLEAN_RESULT_CACHE, false);
        }
        return false;
    }

    public List<Map<String, Object>> getData() {
        for (int i = 0; i < mMainFunctionIcon.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", mMainFunctionIcon[i]);
            map.put("text", mMainFunctionName[i]);
            mHomeMainFunctionList.add(map);
        }

        return mHomeMainFunctionList;
    }

    public void forwardSendPage(int position) {
        switch (position) {
            case 0:
                Intent intentFM = new Intent(C.get(), FileManagerActivity.class);
                intentFM.putExtra(OnekeyField.ONEKEYCLEAN, "大文件清理");
                intentFM.putExtra(OnekeyField.STATISTICS_KEY, StatisticMob.STATISTIC_HOME_ID);
                startActivityForResult(intentFM, FILE_CODE);
                break;
            case 1:
                Intent intentPM = new Intent(C.get(), PicManagerActivity.class);
                intentPM.putExtra(OnekeyField.ONEKEYCLEAN, "相册清理");
                intentPM.putExtra(OnekeyField.STATISTICS_KEY, StatisticMob.STATISTIC_HOME_ID);
                startActivityForResult(intentPM, PM_CODE);
                break;
            case 2:
                Intent intentMM = new Intent(C.get(), MusicManagerActivity.class);
                intentMM.putExtra(OnekeyField.ONEKEYCLEAN, "音乐清理");
                intentMM.putExtra(OnekeyField.STATISTICS_KEY, StatisticMob.STATISTIC_HOME_ID);
                startActivityForResult(intentMM, PP_CODE);
                break;
            case 3:
                Intent intentVM = new Intent(C.get(), VideoManagerActivity.class);
                intentVM.putExtra(OnekeyField.ONEKEYCLEAN, "视频清理");
                intentVM.putExtra(OnekeyField.STATISTICS_KEY, StatisticMob.STATISTIC_HOME_ID);
                startActivityForResult(intentVM, VM_CODE);
                break;
            case 4:
                Intent intentAM = new Intent(C.get(), ApkManagerActivity.class);
                intentAM.putExtra(OnekeyField.ONEKEYCLEAN, "安装包清理");
                intentAM.putExtra(OnekeyField.STATISTICS_KEY, StatisticMob.STATISTIC_HOME_ID);
                startActivityForResult(intentAM, AM_CODE);
                break;
            case 5:
                Intent intentPKM = new Intent(C.get(), PackageManagerActivity.class);
                intentPKM.putExtra(OnekeyField.ONEKEYCLEAN, "压缩包清理");
                intentPKM.putExtra(OnekeyField.STATISTICS_KEY, StatisticMob.STATISTIC_HOME_ID);
                startActivityForResult(intentPKM, PKM_CODE);
                break;
        }

    }

    /**
     * head缩放动画
     */
    private void setHeadContentSize(float scale) {
        if (mHeadContent.getMeasuredHeight() < mWaveHeight) {
            mHeadContent.setScaleX(scale);
            mHeadContent.setScaleY(scale);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pb_ram_prompt://清理按钮
                startActivityForResult(new Intent(C.get(), SilverActivity.class), SILVER_CODE);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SILVER_CODE) {
            //如果清理了
            if (resultCode == CleanFragmentInfo.ACTIVITY_RESULT_CLEAN) {
                tv_junk_state.setText(R.string.so_clear);
            } else if (resultCode == CleanFragmentInfo.ACTIVITY_RESULT_NO_CLEAN) {
                tv_junk_state.setText(R.string.tv_junk_desc);
            }
        }
        Log.d("HomeFragment", "requestCode:" + requestCode);
        initData();
    }

    @Override
    public boolean giveUpTouchEvent(MotionEvent event) {
        if (mRecyclerView.getScrollY() == 0) {
            return true;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}


