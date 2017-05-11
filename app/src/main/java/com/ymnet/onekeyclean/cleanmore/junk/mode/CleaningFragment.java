package com.ymnet.onekeyclean.cleanmore.junk.mode;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.fragment.BaseFragment;
import com.ymnet.onekeyclean.cleanmore.utils.C;
import com.ymnet.onekeyclean.cleanmore.utils.FormatUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link CleaningFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CleaningFragment extends BaseFragment {

    private CleanAdapter nAdapter;
    private int count = 0;
    private long animationTime = 100;

    private FrameLayout headView;
    private TextView tv_size, tv_unit, tv_scan_progress;
    private ListView lv;
    private List<JunkChild> datas;
    private long size;

    private OnCleanFragmentEndListener endListener;


    public static CleaningFragment newInstance() {
        CleaningFragment fragment = new CleaningFragment();
        return fragment;
    }

    public CleaningFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public void setCleanFragmentData(ArrayList<JunkChild> datas, long size) {
        this.datas = datas;
        this.size = size;

    }

    private String deleteing;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cleanning, container, false);
        deleteing = getResources().getString(R.string.deleting);
        initHead(view);
        lv = (ListView) view.findViewById(R.id.lv);
        nAdapter = new CleanAdapter(datas);
        lv.setAdapter(nAdapter);
        lv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        handler.sendEmptyMessageDelayed(0x11, 200);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void initHead(View view) {
        headView = (FrameLayout) view.findViewById(R.id.fl_head);
        tv_size = (TextView) view.findViewById(R.id.tv_size);
        tv_unit = (TextView) view.findViewById(R.id.tv_unit);
        tv_scan_progress = (TextView) view.findViewById(R.id.tv_scan_progress);
        setCleaningSize(size);
        updateHeadViewColor(size);
    }

    /**
     * 设置头部显示的文字
     *
     * @param size
     */
    private void setCleaningSize(long size) {
        String[] fileSizeAndUnit = FormatUtils.getFileSizeAndUnit(size);
        if (fileSizeAndUnit != null && fileSizeAndUnit.length == 2) {
            tv_size.setText(fileSizeAndUnit[0]);
            tv_unit.setText(fileSizeAndUnit[1]);
        }
    }

    private void updateHeadViewColor(long size) {
        ColorDrawable cd;
        if (size == 0) {
             cd = new ColorDrawable(getResources().getColor(R.color.main_blue_new));
            headView.setBackgroundDrawable(cd.mutate());
        } else if (size <= 10 * 1024 * 1024) {
            // 绿色
             cd = new ColorDrawable(getResources().getColor(R.color.clean_bg_green));

            headView.setBackgroundDrawable(cd.mutate());
//            headView.setBackgroundColor(getResources().getColor(R.color.clean_bg_green));

        } else if (size <= 75 * 1024 * 1024) {
            // 橙色
             cd = new ColorDrawable(getResources().getColor(R.color.clean_bg_orange));

            headView.setBackgroundDrawable(cd.mutate());
//            headView.setBackgroundColor((getResources().getColor(R.color.clean_bg_orange));

        } else {
            // 红色
             cd = new ColorDrawable(getResources().getColor(R.color.clean_bg_red));

            headView.setBackgroundDrawable(cd.mutate());
//            headView.setBackgroundColor(getResources().getColor(R.color.clean_bg_red));

        }
        if (endListener != null)
            endListener.onUpdateActivityTitleColor(size);
    }

    Handler handler = new MyHandler(this);

    static class MyHandler extends Handler {
        WeakReference<CleaningFragment> theFragment;

        public MyHandler(CleaningFragment fragment) {
            theFragment = new WeakReference<CleaningFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            CleaningFragment fragment = theFragment.get();
            if (fragment != null) {
                super.handleMessage(msg);
                if (fragment.isAdded() && !fragment.isHidden() && fragment.getActivity() != null) {
                    if (msg.what == 0x11) {
                        fragment.startCleanAnimation();
                    }
                } else {
                    fragment.handler.sendEmptyMessageDelayed(0x11, 200);
                }
            }
        }
    }

    private void startCleanAnimation() {
        View view = lv.getChildAt(0);
        Animation ani = AnimationUtils.loadAnimation(C.get(), R.anim.push_left_out);
        ani.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                count++;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                JunkChild junkChild = datas.get(0);

                updateCleanProgress(junkChild);
                datas.remove(0);
                nAdapter.notifyDataSetChanged();
                handler.sendEmptyMessageDelayed(0x11, animationTime);
            }
        });
        if (view == null) {
            endListener.onCleanEndCallBack();
        } else {
            if (count == 5) {
                cleanALlAnimation();
            } else {
                view.startAnimation(ani);
            }
        }


    }

    private void updateCleanProgress(JunkChild junkChild) {
        if (junkChild == null) {
            return;
        }
        size -= junkChild.size;
        setCleaningSize(size);
        updateHeadViewColor(size);
        tv_scan_progress.setText(deleteing + junkChild.name);

    }

    private void cleanALlAnimation() {
        setCleaningSize(0);
        updateHeadViewColor(0);
        lv.startAnimation(AnimationUtils.loadAnimation(C.get(), R.anim.push_left_out));
        datas.clear();
        nAdapter.notifyDataSetChanged();
        endListener.onCleanEndCallBack();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            endListener = (OnCleanFragmentEndListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        endListener = null;
    }

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
    public interface OnCleanFragmentEndListener {
        public void onCleanEndCallBack();

        public void onUpdateActivityTitleColor(long size);
    }

    public class CleanAdapter extends BaseAdapter {

        private List<JunkChild> mList;

        public List<JunkChild> getmList() {
            return mList;
        }

        public CleanAdapter(List<JunkChild> datas) {
            this.mList = datas;
        }

        @Override
        public int getCount() {
            return mList == null ? 0 : mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList == null ? null : mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHold hold;
            if (convertView == null) {
                convertView = LayoutInflater.from(C.get()).inflate(
                        R.layout.junk_item_child, null);
                hold = new ViewHold();
                hold.tv_name = (TextView) convertView
                        .findViewById(R.id.junk_name);
                hold.tv_tip = (TextView) convertView
                        .findViewById(R.id.junk_label);
                hold.tv_size = (TextView) convertView
                        .findViewById(R.id.file_size);
                hold.junk_child_check = (ImageView) convertView
                        .findViewById(R.id.junk_child_check);
                hold.iv = (ImageView) convertView.findViewById(R.id.junk_icon);
                hold.iv_small = (ImageView) convertView
                        .findViewById(R.id.junk_icon_small);
                convertView.setTag(hold);
            } else {
                hold = (ViewHold) convertView.getTag();
            }
            JunkChild item = mList.get(position);
            hold.junk_child_check
                    .setImageResource(R.drawable.item_check);
            hold.tv_name.setText(item.name);
            hold.tv_size.setText(FormatUtils.formatFileSize(item.size));
            // TODO: 2017/4/26 0026 暂时注解掉 给iv设置图片路径
            /*if (item instanceof JunkChildCache) {
//                ImageLoader.getInstance().displayImage(MarketImageDownloader.INSTALLED_APP_SCHEME + ((JunkChildCache) item).packageName, hold.iv);
                hold.iv.setImageURI(UriUtil.parseUriOrNull(MarkFrescoIconLoadUtils.APP_SCHEME+((JunkChildCache) item).packageName));

            } else if (item instanceof JunkChildApk) {
//                ImageLoader.getInstance().displayImage(MarketImageDownloader.UNINSTALLED_APP_SCHEME + ((JunkChildApk) item).path, hold.iv);
                hold.iv.setImageURI(UriUtil.parseUriOrNull(MarkFrescoIconLoadUtils.APK_SCHEME + ((JunkChildApk) item).packageName));
            } else if (item instanceof InstalledAppAndRAM) {
                hold.iv.setImageResource(R.drawable.icon_header_ram);
//                ImageLoader.getInstance().displayImage(MarketImageDownloader.INSTALLED_APP_SCHEME + ((InstalledAppAndRAM) item).getApp().packageName, hold.iv);
            } else {
                hold.iv.setImageResource(R.drawable.big_file_folder);
            }*/


            return convertView;
        }

        class ViewHold {
            ImageView iv, iv_small, junk_child_check;
            TextView tv_name, tv_size, tv_tip;
        }

    }

}
