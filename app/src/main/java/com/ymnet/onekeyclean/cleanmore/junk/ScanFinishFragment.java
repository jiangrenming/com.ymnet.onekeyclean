package com.ymnet.onekeyclean.cleanmore.junk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.fragment.BaseFragment;
import com.ymnet.onekeyclean.cleanmore.junk.adapter.ExpandableListViewAdapter;
import com.ymnet.onekeyclean.cleanmore.junk.mode.JunkGroup;
import com.ymnet.onekeyclean.cleanmore.utils.FormatUtils;
import com.ymnet.onekeyclean.cleanmore.widget.FloatingGroupExpandableListView;
import com.ymnet.onekeyclean.cleanmore.widget.WaveLoadingView;
import com.ymnet.onekeyclean.cleanmore.widget.WrapperExpandableListAdapter;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ScanFinishFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScanFinishFragment extends BaseFragment {
    private String tag = "ScanFinishFragment";
    /**
     * 必须用两个相同的动画，一个的话两个view颜色更新不同步
     */

    private int handlerSendCount = 0;//空消息发送次数，超过20次就不发送
    private int currentHeadViewColor;

    public static final int ACTION_SELECTED_CHANGE = 0x101;// 界面选中状态发生改变，
    public static final int ACTION_HANDLE_SEND_ANIM = 0X11;
    private FloatingGroupExpandableListView elv_scan_result;
    private List<JunkGroup>                 datas;
    private long                            dataSize;


    private OnScanFinishFragmentInteractionListener mListener;
    private View                                    headView;
    private Resources                               resources;
    private WaveLoadingView                         mWaveLoadingView;

    public void setDatas(List<JunkGroup> datas) {
        this.datas = datas;
    }


    public static ScanFinishFragment newInstance() {
        ScanFinishFragment fragment = new ScanFinishFragment();

        return fragment;
    }

    public ScanFinishFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        flag = true;
        resources = getResources();
        View view = inflater.inflate(R.layout.fragment_scan_finish, container, false);
        headView = inflater.inflate(R.layout.expandlistview_head, elv_scan_result, false);
        mWaveLoadingView = (WaveLoadingView) headView.findViewById(R.id.waveLoadingView);
        initHeadView(headView);
        elv_scan_result = (FloatingGroupExpandableListView) view.findViewById(R.id.elv_scan_result);
        if (datas != null && datas.size() > 0) {
            elv_scan_result.addHeaderView(headView);
            ExpandableListViewAdapter adapter = new ExpandableListViewAdapter(getActivity(), datas, handler);
            WrapperExpandableListAdapter wrapperAdapter = new WrapperExpandableListAdapter(adapter);
            elv_scan_result.setAdapter(wrapperAdapter);
            elv_scan_result.setOnGroupClickListener(listener);
            int expandIndex = 0;
            String cache=resources.getString(R.string.header_cache);
            for (int i = 0; i < datas.size(); i++) {
                if (cache.equals(datas.get(i).getName())) {
                    expandIndex = i;
                    break;
                }
            }
            elv_scan_result.expandGroup(expandIndex);
        }
        elv_scan_result.setOnScrollFloatingGroupListener(scrollListener);
//        handler.sendEmptyMessageDelayed(ACTION_HANDLE_SEND_ANIM, 200);
        return view;
    }

    /**
     * 滑动动画监听
     */
    FloatingGroupExpandableListView.OnScrollFloatingGroupListener scrollListener=new FloatingGroupExpandableListView.OnScrollFloatingGroupListener() {

        @Override
        public void onScrollFloatingGroupListener(View floatingGroupView, int scrollY) {
            float interpolation = -scrollY / (float) floatingGroupView.getHeight();
            // Changing from RGB(255,255,255) to RGB(0,0,0)
            final int whiteToBlackRed = (int) (255 - 255 * interpolation);
            final int whiteToBlackGreen = (int) (255 - 255 * interpolation);
            final int whiteToBlackBlue = (int) (255 - 255 * interpolation);
            final int whileToBlackAlpha = (int) (255 - 255 * interpolation);
            int whiteToBlackColor = Color.argb(whileToBlackAlpha, whiteToBlackRed, whiteToBlackGreen, whiteToBlackBlue);
            final View root = floatingGroupView.findViewById(R.id.rl_group);
            Drawable rootDrawable = root.getBackground().mutate();
            if (rootDrawable != null)
                rootDrawable.setColorFilter(whiteToBlackColor, PorterDuff.Mode.SRC_ATOP);
        }
    };

    boolean flag;//头部缩放动画标记

    @Override
    public void setSupportTag(String tag) {

    }

    @Override
    public String getSupportTag() {
        return null;
    }

    @Override
    public void showSelf() {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (flag) {
            flag = false;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startAnim();
                    if (tv_proposal_clean != null) tv_proposal_clean.setVisibility(View.VISIBLE);
                }
            }, 100);
        }

    }

    Handler handler = new MyHandler(this);

    private static class MyHandler extends Handler {
        WeakReference<ScanFinishFragment> theFragment;
        public MyHandler(ScanFinishFragment fragment) {
            theFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            ScanFinishFragment fragment = theFragment.get();
            if (fragment != null) {
                super.handleMessage(msg);
                if (fragment.isAdded() && !fragment.isHidden() && fragment.isVisible() && fragment.getActivity() != null) {
                    if (msg.what == ACTION_HANDLE_SEND_ANIM) {
                        fragment.startAnim();
                    } else if (msg.what == ACTION_SELECTED_CHANGE && fragment.mListener != null) {
                        fragment.mListener.callback();
                    }
                } else {
                    //20次超时 不再发送消息
                    if (fragment.handlerSendCount < 10) {
                        fragment.handlerSendCount++;
                        fragment.handler.sendEmptyMessageDelayed(ACTION_HANDLE_SEND_ANIM, 100);
                    }
                }
            }
        }
    }
    private TextView tv_proposal_clean;

    private void initHeadView(View headView) {
        TextView tv_scan_progress = (TextView) headView.findViewById(R.id.tv_scan_progress);
        tv_proposal_clean = (TextView) headView.findViewById(R.id.tv_proposal_clean);
        //清理垃圾大数字120dp
        TextView tv_size = (TextView) headView.findViewById(R.id.tv_size);
        TextView tv_unit = (TextView) headView.findViewById(R.id.tv_unit);
        tv_scan_progress.setGravity(Gravity.CENTER_HORIZONTAL);
        tv_scan_progress.setText(getString(R.string.scan_progress_end));
        String[] fileSizeAndUnit = FormatUtils.getFileSizeAndUnit(dataSize);
        if (fileSizeAndUnit != null && fileSizeAndUnit.length == 2) {
            tv_size.setText(fileSizeAndUnit[0]);
            tv_unit.setText(fileSizeAndUnit[1]);
        }

        initHeadViewColor(headView, dataSize);
    }


    @SuppressLint("NewApi")
    private ExpandableListView.OnGroupClickListener listener = new ExpandableListView.OnGroupClickListener() {
        @Override
        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
            if (parent.isGroupExpanded(groupPosition)) {
                parent.collapseGroup(groupPosition);
            } else {
                //第二个参数false表示展开时是否触发默认滚动动画
                if (Build.VERSION.SDK_INT > 14) parent.expandGroup(groupPosition, false);
                else parent.expandGroup(groupPosition);
            }
            //telling the listView we have handled the group click, and don't want the default actions.
            return true;
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnScanFinishFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        if (mWaveLoadingView != null) {
            mWaveLoadingView.cancelAnimation();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(tag, "onDestroy");
    }

    public void setDataSize(long dataSize) {
        this.dataSize = dataSize;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnScanFinishFragmentInteractionListener {

        void callback();

        void updateTitleColor(int color);
    }

    private void initHeadViewColor(View view, long size) {

        currentHeadViewColor = resources.getColor(R.color.main_blue_new1);
        int value;
        if (size <= 10 * 1024 * 1024) {
            // 绿色
//            currentHeadViewColor = resources.getColor(R.color.clean_bg_green);
            value = 10;
        } else if (size <= 75 * 1024 * 1024) {
            // 橙色
//            currentHeadViewColor = resources.getColor(R.color.clean_bg_orange);
            value = 30;
        } else {
            // 红色
//            currentHeadViewColor = resources.getColor(R.color.clean_bg_red);
            value = 70;
        }
        Log.d("CleaningFragment", "value:" + value);
        mWaveLoadingView.setProgressValue(value);
        mWaveLoadingView.setAmplitudeRatio(33);
        mListener.updateTitleColor(currentHeadViewColor);
        view.setBackgroundColor(currentHeadViewColor);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (currentHeadViewColor > 0) {
//            mListener.updateTitleColor(currentHeadViewColor);
        }
    }

    //清理垃圾大数字动画
    public void startAnim() {

       /* if (headView != null && C.get() != null) {
            FrameLayout fl_num_layout = (FrameLayout) headView.findViewById(R.id.fl_num_layout);
            RelativeLayout ll_number = (RelativeLayout) headView.findViewById(R.id.ll_number);
            Util.beforehandMeasuredViewHeight(fl_num_layout);
            TweenAnimationUtils.startAnimation(C.get(), ll_number, R.anim.headscale, new AnimationListenerAdapter() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    super.onAnimationEnd(animation);
                    if (tv_proposal_clean != null) tv_proposal_clean.setVisibility(View.VISIBLE);
                }
            });
            ProperyAnimationUtils.performAnimate(fl_num_layout, fl_num_layout.getMeasuredHeight(), (int) (fl_num_layout.getMeasuredHeight() * 0.6));

        }*/
    }

}
