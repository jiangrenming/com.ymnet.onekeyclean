package com.ymnet.onekeyclean.cleanmore.fragment.testfragment;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.ymnet.killbackground.utils.Run;
import com.ymnet.killbackground.view.CleanActivity;
import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.constants.ByteConstants;
import com.ymnet.onekeyclean.cleanmore.constants.ScanState;
import com.ymnet.onekeyclean.cleanmore.constants.TimeConstants;
import com.ymnet.onekeyclean.cleanmore.customview.RecyclerViewPlus;
import com.ymnet.onekeyclean.cleanmore.datacenter.DataCenterObserver;
import com.ymnet.onekeyclean.cleanmore.fragment.CleanOverFragment;
import com.ymnet.onekeyclean.cleanmore.fragment.filemanager.FileManagerActivity;
import com.ymnet.onekeyclean.cleanmore.fragment.view.HomeAdapter;
import com.ymnet.onekeyclean.cleanmore.fragment.view.RecyclerInfo;
import com.ymnet.onekeyclean.cleanmore.junk.ScanFinishFragment;
import com.ymnet.onekeyclean.cleanmore.junk.ScanHelp;
import com.ymnet.onekeyclean.cleanmore.junk.SilverActivity;
import com.ymnet.onekeyclean.cleanmore.junk.clearstrategy.ClearManager;
import com.ymnet.onekeyclean.cleanmore.junk.mode.InstalledAppAndRAM;
import com.ymnet.onekeyclean.cleanmore.junk.mode.JunkChild;
import com.ymnet.onekeyclean.cleanmore.junk.mode.JunkChildCache;
import com.ymnet.onekeyclean.cleanmore.junk.mode.JunkChildCacheOfChild;
import com.ymnet.onekeyclean.cleanmore.junk.mode.JunkGroup;
import com.ymnet.onekeyclean.cleanmore.qq.activity.QQActivity;
import com.ymnet.onekeyclean.cleanmore.utils.C;
import com.ymnet.onekeyclean.cleanmore.utils.CleanSetSharedPreferences;
import com.ymnet.onekeyclean.cleanmore.utils.DisplayUtil;
import com.ymnet.onekeyclean.cleanmore.utils.FormatUtils;
import com.ymnet.onekeyclean.cleanmore.wechat.WeChatActivity;
import com.ymnet.onekeyclean.cleanmore.wechat.listener.RecyclerViewClickListener;
import com.ymnet.onekeyclean.cleanmore.widget.LinearLayoutItemDecoration;
import com.ymnet.onekeyclean.cleanmore.widget.ProgressButton;
import com.ymnet.onekeyclean.cleanmore.widget.SGTextView;
import com.ymnet.onekeyclean.cleanmore.widget.StickyLayout;
import com.ymnet.onekeyclean.cleanmore.widget.WaveLoadingView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements View.OnClickListener, ScanHelp.IScanResult, StickyLayout.OnGiveUpTouchEventListener {

    private static final int CLEAN_CODE    = 13;
    private static final int SILVER_CODE   = 12;
    private String TAG                     = "HomeFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ScanHelp        mScan;

    private long            selectSize;
    private List<JunkGroup> datas;
    private boolean needSave = false;
    private String mParam1;
    private String mParam2;
    private SGTextView                  tv_size;

    private SGTextView                  tv_unit;
    private WaveLoadingView             mWave;
    private ProgressButton              mProgressButton;
    private ArrayList<JunkChild>        mJunkChildDatas;
    private long                        mJunkChildSize;
    private RecyclerViewPlus            mRecyclerView;
    private HomeAdapter                 mAdapter;
    private RecyclerInfo                mRecyclerInfo;
    private View                        mScrollView;
    private View                        mHeadContent;
    private int                         measuredHeight;
    private View                        mRlHomeHead;
    private StickyLayout                mStickLayout;
    private int                         mWaveHeight;
    private WeakReference<HomeFragment> theFragment;
    private View     mStickyHead;
    private View     mRlHeadClear;
    private View     mLlNumber;
    private TextView mTvCleanDesc;
    private static final String SCAN_STOP = "scanStop";
    private static final String SCAN_FINISH = "scanFinish";
    private static final String CLEAN_FINISH = "cleanFinish";
    private static final String SCAN_AGAIN    = "scanAgain";
    private static final String SCANNING   = "scanning";
    private Handler mHandler = new MyHandler(this);
    private ImageView mIvCleanDown;

    class MyHandler extends Handler {

        public MyHandler(HomeFragment fragment) {
            theFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //跳转界面,执行清理动画
                    if (mScan != null) {
                        mScan.setRun(false);
                        mScan.close();
                    }

                    Intent intent = new Intent(getContext(), SilverActivity.class);
                    intent.putExtra("state", "scanFi");
                    Log.d("MyHandler", "mScan:" + mScan.getTotalSelectSize() + "--" + mScan.hashCode());
                    startActivityForResult(intent, SILVER_CODE);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String[] sizeAndUnit = FormatUtils.getFileSizeAndUnit(0);
                            tv_size.setText(sizeAndUnit[0]);
                            tv_unit.setText(sizeAndUnit[1]);
                            updateWaveLevel(0);
                        }
                    }, 500);
                    break;
            }
        }
    }

    public HomeFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        Log.d(TAG, "onCreate: ");

    }

    private void initScan() {
        if (checkHasCleanCache()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    selectSize = CleanOverFragment.HAS_CLEAN_CACHE;
                    Log.d("HomeFragment", "selectSize:" + selectSize);
                    // TODO: 2017/6/2 0002 展示三分钟内的缓存数据
                    String pbState = CleanSetSharedPreferences.getPBState(C.get(), CleanSetSharedPreferences.BUTTON_STATE, SCANNING);
                    Log.d("HomeFragment", "当前按钮状态:"+pbState);
                    mProgressButton.setTag(pbState);
                    cleanOverHead();
                }
            }, 1000);
        } else {
            scan();
        }

    }

    private void scan() {
        mScan = ScanHelp.getInstance(C.get());
        if (!mScan.isScanned()) {
            SQLiteDatabase db = new ClearManager(C.get()).openClearDatabase();
            mScan.setDb(db);
            mScan.setiScanResult(this);
            mScan.setRun(true);
            mScan.startScan(false);
            mScan.hadScan(true);
        } else {
            Log.d("HomeFragment", "已经扫描过了,直接展示数值");
            scanState(ScanState.SCAN_ALL_END);
        }
    }

    private void cleanOverHead() {
        mLlNumber.setVisibility(View.GONE);
        mRlHeadClear.setVisibility(View.VISIBLE);

        mWave.cancelAnimation();
        mWave.setVisibility(View.GONE);
        Log.d("HomeFragment", "mProgressButton.getTag():" + mProgressButton.getTag());
        if (mProgressButton.getTag().equals(CLEAN_FINISH)) {
            mTvCleanDesc.setText(R.string.so_clear);
            mProgressButton.setText(R.string.done);
            mIvCleanDown.setImageResource(R.drawable.clean_over_scanagain);
            mProgressButton.setTag(SCAN_AGAIN);
        } else if (mProgressButton.getTag().equals(SCAN_AGAIN)) {
            mTvCleanDesc.setText(R.string.keep_habit);
            mProgressButton.setText(R.string.scan_again);
            mIvCleanDown.setImageResource(R.drawable.clean_over_clear);
            mProgressButton.setTag(SCANNING);
        } else if (mProgressButton.getTag().equals(SCANNING)) {
            mTvCleanDesc.setText(R.string.keep_habit);
            mProgressButton.setText(R.string.scan_again);
            mIvCleanDown.setImageResource(R.drawable.clean_over_clear);
            mProgressButton.setTag(SCANNING);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home1, container, false);
        initView(view);

        if (DataCenterObserver.get(C.get()).isRefreshCleanActivity()) {
            DataCenterObserver.get(C.get()).setRefreshCleanActivity(false);
            Log.d(TAG, "onCreateView: ");
            initScan();
        }

        initScan();
        return view;
    }

    private void initView(View view) {
        mStickyHead = view.findViewById(R.id.sticky_header);
        mHeadContent = view.findViewById(R.id.ll_head_content);
        mRlHomeHead = view.findViewById(R.id.rl_home_head);
        mLlNumber = view.findViewById(R.id.ll_number);
        mRlHeadClear = view.findViewById(R.id.ll_clean_down);
        mIvCleanDown = (ImageView) view.findViewById(R.id.iv_clean_down);
        mTvCleanDesc = (TextView) view.findViewById(R.id.tv_clean_down);

        mStickLayout = (StickyLayout) view.findViewById(R.id.sticky_layout);
        mStickLayout.setOnGiveUpTouchEventListener(this);
        mStickLayout.setSticky(true);
        final ViewGroup.LayoutParams layoutParams = mStickyHead.getLayoutParams();

        mStickLayout.setHeightChangeListener(new StickyLayout.HeightChangeListener() {
            @Override
            public void notifyChange(float scale) {

                //                setWaveHeight(scale,layoutParams);
                setHeadContentSize(scale);
            }
        });
        mRecyclerView = (RecyclerViewPlus) view.findViewById(R.id.sticky_content);
        mRecyclerInfo = new RecyclerInfo();
        mAdapter = new HomeAdapter(C.get(), mRecyclerInfo);

        final LinearLayoutManager layout = new LinearLayoutManager(C.get());
        mRecyclerView.setLayoutManager(layout);

        mRecyclerView.addItemDecoration(new LinearLayoutItemDecoration(C.get(), LinearLayoutItemDecoration.HORIZONTAL_LIST));
        mAdapter.addFooterView(new RecyclerViewPlus.HeaderFooterItemAdapter.ViewHolderWrapper() {
            @Override
            protected View onCreateView(ViewGroup parent) {
                RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DisplayUtil.dip2px(C.get(), 165));
                View foot = new View(getActivity());
                foot.setLayoutParams(lp);
                foot.setBackgroundColor(Color.TRANSPARENT);
                return foot;
            }
        });
        mAdapter.setRecyclerListListener(new RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                recyclerViewOnClick(v, position);
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

        tv_size = (SGTextView) view.findViewById(R.id.tv_homehead_size);
        tv_unit = (SGTextView) view.findViewById(R.id.tv_homehead_unit);
        mWave = (WaveLoadingView) view.findViewById(R.id.wlv_home);
        mProgressButton = (ProgressButton) view.findViewById(R.id.pb_ram_prompt);
        mProgressButton.setStop(false);
        mProgressButton.setOnClickListener(this);
        mProgressButton.setOnStateListener(new ProgressButton.OnStateListener() {
            @Override
            public void onFinish() {
            }

            @Override
            public void onStop() {

                mProgressButton.setText("停止扫描,立即清理");
            }

            @Override
            public void onContinue() {
                mProgressButton.setText("---");
            }
        });

        mAdapter.notifyDataSetChanged();
        mStickyHead.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mWaveHeight = mStickyHead.getMeasuredHeight();
                mStickyHead.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SILVER_CODE) {
            //如果清理了
            if (resultCode == CleanFragmentInfo.ACTIVITY_RESULT_CLEAN) {
                CleanSetSharedPreferences.setPBState(C.get(), CleanSetSharedPreferences.BUTTON_STATE, CLEAN_FINISH);
                mProgressButton.setTag(CLEAN_FINISH);
            } else if (resultCode == CleanFragmentInfo.ACTIVITY_RESULT_NO_CLEAN) {
                mProgressButton.setTag(SCAN_FINISH);
            }
            initScan();

        } else if (requestCode == CLEAN_CODE) {
            Log.d("HomeFragment", "一键加速传回");
            mAdapter.notifyDataSetChanged();
        }
    }

    private void recyclerViewOnClick(View v, int position) {
        switch (position) {
            case 0:
                Intent intent = new Intent(C.get(), CleanActivity.class);
                startActivityForResult(intent,CLEAN_CODE);
                break;
            case 1:
                C.get().startActivity(new Intent(C.get(), WeChatActivity.class));
                break;
            case 2:
                C.get().startActivity(new Intent(C.get(), QQActivity.class));
                break;
            case 4:
                Intent file = new Intent(C.get(), FileManagerActivity.class);
                file.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                C.get().startActivity(file);
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
    public void onDetach() {
        super.onDetach();

        if (mWave != null) {
            mWave.cancelAnimation();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pb_ram_prompt:
                if (mProgressButton.getTag() == null) {
                    return;
                }
                if (SCAN_FINISH.equals(mProgressButton.getTag().toString())) {

                    ArrayList<JunkChild> cleanFragmentDatas = createCleanFragmentDatas();
                    if (cleanFragmentDatas == null || cleanFragmentDatas.size() == 0) {
                        return;
                    }
                    selectSize = mScan.getTotalSelectSize();
                    setCleanFragmentData(cleanFragmentDatas, selectSize);

                    mHandler.sendEmptyMessageDelayed(0, 200);
                    needSave = false;
                } else if (SCAN_STOP.equals(mProgressButton.getTag().toString())) {
                    Log.d("HomeFragment", "scanStop这里scanFinish");
                    mProgressButton.setTag("scanFinish");
                    if (mScan != null && mScan.isRun()) {
                        mScan.setRun(false);
                    }
                } else if (CLEAN_FINISH.equals(mProgressButton.getTag().toString())) {
                    Log.d("HomeFragment", "清理完成界面");
                    cleanOverHead();
                    CleanSetSharedPreferences.setPBState(C.get(), CleanSetSharedPreferences.BUTTON_STATE, SCAN_AGAIN);
                } else if (SCAN_AGAIN.equals(mProgressButton.getTag().toString())) {
                    Log.d("HomeFragment", "点击重新扫描");
                    cleanOverHead();
                    CleanSetSharedPreferences.setPBState(C.get(), CleanSetSharedPreferences.BUTTON_STATE, SCANNING);
                } else if (SCANNING.equals(mProgressButton.getTag().toString())) {
                    Log.d("HomeFragment", "再次扫描中");

                    mProgressButton.setTag("scanStop");
                    mLlNumber.setVisibility(View.VISIBLE);
                    mRlHeadClear.setVisibility(View.GONE);
                    mWave.startAnimation();
                    mWave.setVisibility(View.VISIBLE);
                    if (checkHasCleanCache()) {
                        mProgressButton.setTag(CLEAN_FINISH);
                        cleanOverHead();
                    } else {
                        scan();
                    }
                }
                break;
        }
    }

    @Override
    public void scanning(final String path) {
        if (getActivity().isFinishing())
            return;
        Run.onMain(new Runnable() {
            @Override
            public void run() {
                mProgressButton.setTag(SCAN_STOP);
                CleanSetSharedPreferences.setPBState(C.get(), CleanSetSharedPreferences.BUTTON_STATE, SCAN_STOP);
                setScanSize();
            }
        });
    }

    @Override
    public void scanState(final int state) {
        if (getActivity().isFinishing())
            return;
        Run.onMain(new Runnable() {
            @Override
            public void run() {

                switch (state) {

                    case ScanState.SCANING_SYSTEM_CACHE_END:
                        mProgressButton.setProgress(20);
                        break;
                    case ScanState.SCAN_RAM_END:
                        mProgressButton.setProgress(40);
                        break;
                    case ScanState.SCANING_APP_CACHE_END:
                        mProgressButton.setProgress(60);
                        break;
                    case ScanState.SCANING_RESIDUAL_END:
                        mProgressButton.setProgress(80);
                        break;
                    case ScanState.SCANING_APK_FILE_END:
                        mProgressButton.setProgress(90);
                        break;
                    case ScanState.SCAN_ALL_END:
                        Log.d("HomeFragment", "scanFinish这里scanFinish");
                        mProgressButton.setTag(SCAN_FINISH);
                        CleanSetSharedPreferences.setPBState(C.get(), CleanSetSharedPreferences.BUTTON_STATE, SCAN_FINISH);
                        mProgressButton.setProgress(100);
                        mProgressButton.setText("立刻清理");

                        datas = mScan.getDatas();
                        if (datas == null || datas.size() == 0) {
                            cleanOverHead();
                            mProgressButton.setText("清理完成");
                            mProgressButton.setTag(SCAN_AGAIN);
//                            showShort(C.get(), "清理完成界面展示");
                        } else {
                            ScanFinishFragment scanFinishF = ScanFinishFragment.newInstance();
                            long dataSize = mScan.getTotalSize();
                            scanFinishF.setDatas(datas);
                            scanFinishF.setDataSize(dataSize);
                            needSave = true;
                        }
                        break;
                    case ScanState.SCANING_PATH:
                        break;
                    default:
                        break;
                }
                setScanSize();
            }
        });
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public boolean giveUpTouchEvent(MotionEvent event) {
        if (mRecyclerView.getScrollY() == 0) {
            return true;
        }
        return false;
    }

    /**
     * 检查清理缓存 不通过的话直接做一秒钟的动画效果进去结果页面  显示未发现垃圾
     *
     * @return
     */
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

    private void setScanSize() {
        if (mScan != null && !getActivity().isFinishing()) {
            long size = mScan.getTotalSize();
            String[] sizeAndUnit = FormatUtils.getFileSizeAndUnit(size);
            String strSize = sizeAndUnit[0];
            String strUnit = sizeAndUnit[1];
            tv_size.setText(strSize);
            tv_unit.setText(strUnit);
            updateWaveLevel(size);
        }
    }

    public void setCleanFragmentData(ArrayList<JunkChild> datas, long size) {
        this.mJunkChildDatas = datas;
        this.mJunkChildSize = size;

    }

    private void updateWaveLevel(long size) {
        int value;
        if (size <= 10 * ByteConstants.MB) {
            value = 10;
        } else if (size <= 80 * ByteConstants.MB) {
            value = 30;
        } else if (size <= 300 * ByteConstants.MB) {
            value = 50;
        } else {
            value = 70;
        }
        mWave.setProgressValue(value);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (mScan != null) {
                mScan.setRun(false);
                mScan.close();
                if (needSave) {
                    DataCenterObserver.get(C.get()).setJunkDataList(datas);
                    DataCenterObserver.get(C.get()).setLastScanTime(System.currentTimeMillis());
                }
                Log.d("HomeFragment", "mProgressButton.getTag():" + mProgressButton.getTag());

            }
        } catch (Exception e) {
        }
    }
}


