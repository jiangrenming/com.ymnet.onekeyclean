package com.ymnet.onekeyclean.cleanmore.fragment.testfragment;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.example.commonlibrary.systemmanager.SystemMemory;
import com.example.commonlibrary.utils.DensityUtil;
import com.example.commonlibrary.utils.ToastUtil;
import com.ymnet.killbackground.utils.Run;
import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.constants.ByteConstants;
import com.ymnet.onekeyclean.cleanmore.constants.ScanState;
import com.ymnet.onekeyclean.cleanmore.constants.TimeConstants;
import com.ymnet.onekeyclean.cleanmore.customview.RecyclerViewPlus;
import com.ymnet.onekeyclean.cleanmore.datacenter.DataCenterObserver;
import com.ymnet.onekeyclean.cleanmore.fragment.CleanOverFragment;
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
import com.ymnet.onekeyclean.cleanmore.service.BackgroundDoSomethingService;
import com.ymnet.onekeyclean.cleanmore.utils.C;
import com.ymnet.onekeyclean.cleanmore.utils.CleanSetSharedPreferences;
import com.ymnet.onekeyclean.cleanmore.utils.DisplayUtil;
import com.ymnet.onekeyclean.cleanmore.utils.FormatUtils;
import com.ymnet.onekeyclean.cleanmore.widget.LinearLayoutItemDecoration;
import com.ymnet.onekeyclean.cleanmore.widget.ProgressButton;
import com.ymnet.onekeyclean.cleanmore.widget.SGTextView;
import com.ymnet.onekeyclean.cleanmore.widget.WaveLoadingView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.example.commonlibrary.systemmanager.SystemMemory.getAvailMemorySize;
import static com.example.commonlibrary.utils.ToastUtil.showShort;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fragment1.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fragment1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment1 extends Fragment implements View.OnClickListener, ScanHelp.IScanResult {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ScanHelp        mScan;
    private long            selectSize;
    private List<JunkGroup> datas;
    private boolean needSave = false;
    private String mParam1;
    private String mParam2;

    private SGTextView      tv_size;
    private SGTextView      tv_unit;
    private WaveLoadingView mWave;
    private ProgressButton  mProgressButton;
    private Handler mHandler = new MyHandler(this);
    private ArrayList<JunkChild> mJunkChildDatas;
    private long                 mJunkChildSize;
    private RecyclerViewPlus     mRecyclerView;
    private HomeAdapter          mAdapter;
    private RecyclerInfo         mRecyclerInfo;
    private View                 mScrollView;
    private View                 mHead;
    private View                 mLl;
    private View                 mHeadContent;
    //    private View mRecylerView;

    class MyHandler extends Handler {
        WeakReference<Fragment1> theFragment;

        public MyHandler(Fragment1 fragment) {
            theFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x11:
                    startCleanAnimation();
                    break;
            }

        }
    }

    public Fragment1() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment1.
     */
    public static Fragment1 newInstance(String param1, String param2) {
        Fragment1 fragment = new Fragment1();
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
        initScan();
    }

    private void initScan() {
        if (mScan != null) {
            mScan.setiScanResult(null);
            mScan.setRun(false);
            mScan.close();
            mScan = null;
        }
        if (checkHasCleanCache()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    selectSize = CleanOverFragment.HAS_CLEAN_CACHE;
                    // TODO: 2017/6/2 0002 展示清理结果动画
                    //                    startCleanOverActivity();
                }
            }, 1000);
        } else {
            SQLiteDatabase db = new ClearManager(C.get()).openClearDatabase();
            mScan = ScanHelp.getInstance(C.get());
            mScan.setDb(db);
            mScan.setiScanResult(this);
            mScan.setRun(true);
            mScan.startScan(false);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment1, container, false);
        initView(view);

        if (DataCenterObserver.get(C.get()).isRefreshCleanActivity()) {
            DataCenterObserver.get(C.get()).setRefreshCleanActivity(false);
            initScan();
        }
        initData();
        return view;
    }

    float downY = 0;

    private void initView(View view) {
        //        mScrollView = view.findViewById(R.id.sv_home);
        mLl = view.findViewById(R.id.ll_home);
        mRecyclerView = (RecyclerViewPlus) view.findViewById(R.id.rv_frag_function);
        mRecyclerInfo = new RecyclerInfo();
        mAdapter = new HomeAdapter(C.get(), mRecyclerInfo);
        LinearLayoutManager layout = new LinearLayoutManager(C.get())/* {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        }*/;

        mRecyclerView.setLayoutManager(layout);
        mHead = LayoutInflater.from(C.get()).inflate(R.layout.fragment_item0, mRecyclerView, false);


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
        mAdapter.addHeaderView(new RecyclerViewPlus.HeaderFooterItemAdapter.ViewHolderWrapper() {
            @Override
            protected View onCreateView(ViewGroup parent) {
                return mHead;
            }
        });

        mRecyclerView.setAdapter(mAdapter);
        mHeadContent = mHead.findViewById(R.id.ll_head_content);

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                    Log.d("Fragment1", "scrollY:" + scrollY);
//                    Log.d("Fragment1", "oldScrollY:" + oldScrollY);
                    if (scrollY - oldScrollY > 0) {
//                        Log.d("Fragment1", "close");
                    } else if (scrollY - oldScrollY < 0) {
//                        Log.d("Fragment1", "open");
                    }
                }
            });
        }*/

        final GestureDetector gestureDetector = new GestureDetector(new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                Log.d("Fragment1", "onDown");
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Log.d("Fragment1", "distanceX:" + distanceX + "/distanceY:" + distanceY);
                Log.d("Fragment1", "e1.getAction():" + e1.getAction());
                switch (e1.getAction()) {

                    case MotionEvent.ACTION_UP:
                        if (distanceY < 0) {
                            Log.d("Fragment1", "open");
                            //                                        mRecyclerView.smoothScrollBy(0, -DensityUtil.dp2px(C.get(), 300));
                            mRecyclerView.smoothScrollToPosition(0);
                            //                    mRecyclerView.addView(mHead, 0);
                            ViewCompat.animate(mHeadContent).scaleY(1).scaleX(1).setDuration(500).setInterpolator(new DecelerateInterpolator()).start();

                        } else if (distanceY > 0) {
                            Log.d("Fragment1", "close");
                            mRecyclerView.smoothScrollBy(0, DensityUtil.dp2px(C.get(), 300));
                            //                    mRecyclerView.smoothScrollToPosition(1);
                            //                    mRecyclerView.removeView(mHead);
                            ViewCompat.animate(mHeadContent).scaleY(0).scaleX(0).setDuration(500).setInterpolator(new DecelerateInterpolator()).start();

                        }
                        break;
                }

                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.d("Fragment1", "/velocityY:" + velocityY);
                if (velocityY < 0) {
                    mRecyclerView.smoothScrollBy(0, DensityUtil.dp2px(C.get(), 300));
                } else if (velocityY > 0) {
                    mRecyclerView.smoothScrollBy(0, -DensityUtil.dp2px(C.get(), 300));
                }
                return true;
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                /*Log.d("Fragment1", "dy:" + dy);
                if (dy > 0) {
                    Log.d("Fragment1", "open");
                } else {
                    Log.d("Fragment1", "close");
                }*/
            }
        });


        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downY = event.getY();
                        Log.d("Fragment1", "downY:" + downY);
                        break;
                    case MotionEvent.ACTION_UP:
                        float upY = event.getY();
                        Log.d("Fragment1", "upY:" + upY);
                        Log.d("Fragment1", "upY - downY:" + (upY - downY));
                       /* if (upY - downY > 0) {
                            Log.d("Fragment1", "open");
                        } else {
                            Log.d("Fragment1", "close");
                        }*/
                        break;
                }
                return gestureDetector.onTouchEvent(event);
            }

        });
        tv_size = (SGTextView) mHead.findViewById(R.id.tv_homehead_size);
        tv_unit = (SGTextView) mHead.findViewById(R.id.tv_homehead_unit);
        mWave = (WaveLoadingView) mHead.findViewById(R.id.wlv_home);
        mProgressButton = (ProgressButton) mHead.findViewById(R.id.pb_ram_prompt);
        mProgressButton.setStop(false);
        mProgressButton.setOnClickListener(this);
        mProgressButton.setOnStateListener(new ProgressButton.OnStateListener() {
            @Override
            public void onFinish() {
            }

            @Override
            public void onStop() {
                //                mProgressButton.setTag("scanStop");
                mProgressButton.setText("停止扫描,立即清理");
            }

            @Override
            public void onContinue() {
                mProgressButton.setText("---");
            }
        });


        String sAgeFormat = getActivity().getResources().getString(R.string.used_memory);
        String format = String.format(sAgeFormat, getUsedMemory());
        mAdapter.notifyDataSetChanged();

    }

    private int getUsedMemory() {
        return (int) (100 * ((float) getAvailMemorySize(C.get()) / SystemMemory.getTotalMemorySize(C.get())));
    }

    private void initData() {
        if (CleanFragmentInfo.progressButtonState == null || CleanFragmentInfo.displayValue == 0) {
            return;
        }
        Log.d("Fragment1", "初始化:" + CleanFragmentInfo.progressButtonState);
        if (CleanFragmentInfo.progressButtonState.equals("scanFinish")) {
            mProgressButton.setTag("scanFinish");
            mProgressButton.setText("立刻清理2");
        } else if (CleanFragmentInfo.progressButtonState.equals("empty")) {
            mProgressButton.setTag("empty");
            mProgressButton.setText("真干净");
        }

        String[] sizeAndUnit = FormatUtils.getFileSizeAndUnit(CleanFragmentInfo.displayValue);
        String strSize = sizeAndUnit[0];
        String strUnit = sizeAndUnit[1];
        //        mRecyclerInfo.sizeAndUnit = sizeAndUnit;

        tv_size.setText(strSize);
        tv_unit.setText(strUnit);
        updateWaveLevel(CleanFragmentInfo.displayValue);
        //        mAdapter.setData(mRecyclerInfo);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pb_ram_prompt:
                if (mProgressButton.getTag() == null) {
                    return;
                }
                if ("scanFinish".equals(mProgressButton.getTag().toString())) {

                    ArrayList<JunkChild> cleanFragmentDatas = createCleanFragmentDatas();
                    if (cleanFragmentDatas == null || cleanFragmentDatas.size() == 0) {
                        return;
                    }
                    selectSize = mScan.getTotalSelectSize();
                    setCleanFragmentData(cleanFragmentDatas, selectSize);
                    Log.d("Fragment1", "cleanFragmentDatas.size():" + cleanFragmentDatas.size());
                    mHandler.sendEmptyMessageDelayed(0x11, 200);
                    needSave = false;
                } else if ("scanStop".equals(mProgressButton.getTag().toString())) {
                    Log.d("Fragment1", "scanStop这里scanFinish");
                    mProgressButton.setTag("scanFinish");
                    CleanFragmentInfo.progressButtonState = "scanFinish";
                    if (mScan != null && mScan.isRun()) {
                        mScan.setRun(false);
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
                mProgressButton.setTag("scanStop");
                CleanFragmentInfo.progressButtonState = "scanStop";
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
                        showShort(C.get(), "扫描1结束");
                        break;
                    case ScanState.SCAN_RAM_END:
                        mProgressButton.setProgress(40);
                        showShort(C.get(), "扫描2结束");
                        break;
                    case ScanState.SCANING_APP_CACHE_END:
                        mProgressButton.setProgress(60);
                        showShort(C.get(), "扫描3结束");
                        break;
                    case ScanState.SCANING_RESIDUAL_END:
                        mProgressButton.setProgress(80);
                        showShort(C.get(), "扫描4结束");
                        break;
                    case ScanState.SCANING_APK_FILE_END:
                        mProgressButton.setProgress(90);
                        showShort(C.get(), "扫描5结束");
                        break;
                    case ScanState.SCAN_ALL_END:
                        Log.d("Fragment1", "scanFinish这里scanFinish");
                        mProgressButton.setTag("scanFinish");
                        CleanFragmentInfo.progressButtonState = "scanFinish";
                        mProgressButton.setProgress(100);
                        mProgressButton.setText("立刻清理2");

                        datas = mScan.getDatas();
                        if (datas == null || datas.size() == 0) {
                            //todo 清理完成展示界面
                            //                                                        startCleanOverActivity();
                            mProgressButton.setText("清理完成");
                            showShort(C.get(), "清理完成展示界面");
                        } else {
                            ScanFinishFragment scanFinishF = ScanFinishFragment.newInstance();
                            long dataSize = mScan.getTotalSize();
                            scanFinishF.setDatas(datas);
                            scanFinishF.setDataSize(dataSize);
                            needSave = true;
                            ToastUtil.showShort(C.get(), "扫描6结束");
                        }
                        break;
                    case ScanState.SCANING_PATH:
                        showShort(C.get(), "扫描中123456");
                        break;
                    default:
                        break;
                }
                setScanSize();
            }
        });
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
            CleanFragmentInfo.displayValue = size;
            String[] sizeAndUnit = FormatUtils.getFileSizeAndUnit(size);
            //            mRecyclerInfo.sizeAndUnit = sizeAndUnit;
            String strSize = sizeAndUnit[0];
            String strUnit = sizeAndUnit[1];
            tv_size.setText(strSize);
            tv_unit.setText(strUnit);
            updateWaveLevel(size);
            //            mAdapter.notifyDataSetChanged();
            //            mAdapter.setData(mRecyclerInfo);
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
        //        mRecyclerInfo.waveLevel = 0;
        //        mRecyclerInfo.waveLevel = value;
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

    private void startCleanAnimation() {

        mWave.startAnimation();

        JunkChild junkChild = mJunkChildDatas.get(0);
        updateCleanProgress(junkChild);
        mJunkChildDatas.remove(0);
        //                mHandler.sendEmptyMessageDelayed(0x11, 100);
        if (mJunkChildDatas.size() == 0) {
            mProgressButton.setText("清理完成");
            Log.d("Fragment1", "empty这里赋值");
            mProgressButton.setTag("empty");
            onCleanEnd();
        }

    }

    public void onCleanEnd() {
        DataCenterObserver.get(C.get()).setCleanData(new SilverActivity.CleanDataModeEvent(datas, selectSize));
        BackgroundDoSomethingService.startActionFoo(C.get(), "", "");
        displayClear();
    }

    private void displayClear() {
        showShort(C.get(), "真干净");
        // TODO: 2017/6/2 0002 清理动画效果
    }

    private void updateCleanProgress(JunkChild junkChild) {
        if (junkChild == null) {
            return;
        }
        mJunkChildSize -= junkChild.size;
        setCleaningSize(mJunkChildSize);
        updateWaveLevel(mJunkChildSize);
        //        mAdapter.setData(mRecyclerInfo);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 设置头部显示的文字
     *
     * @param size
     */
    private void setCleaningSize(long size) {
        String[] fileSizeAndUnit = FormatUtils.getFileSizeAndUnit(size);
        //        mRecyclerInfo.sizeAndUnit = fileSizeAndUnit;
        if (fileSizeAndUnit != null && fileSizeAndUnit.length == 2) {
            tv_size.setText(fileSizeAndUnit[0]);
            tv_unit.setText(fileSizeAndUnit[1]);
        }
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
                Log.d("Fragment1", "mProgressButton.getTag():" + mProgressButton.getTag());
                CleanFragmentInfo.progressButtonState = (String) mProgressButton.getTag();
            }
        } catch (Exception e) {
        }
    }
}


