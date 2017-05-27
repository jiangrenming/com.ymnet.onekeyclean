package com.ymnet.onekeyclean.cleanmore.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.constants.ScanState;
import com.ymnet.onekeyclean.cleanmore.widget.WaveLoadingView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link .} interface
 * to handle interaction events.
 * Use the {@link ScanningFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScanningFragment extends BaseFragment {


    private FrameLayout headView;
    private TextView    tv_scan_progress;
    private TextView    tv_size;
    private TextView    tv_unit;
    private View        junk_sort_item_ram_progress, junk_sort_item_ram_image;
    private View junk_sort_item_cache_progress, junk_sort_item_cache_image;
    private View junk_sort_item_residual_progress, junk_sort_item_residual_image;
    private View junk_sort_item_apk_progress, junk_sort_item_apk_image;
    private WaveLoadingView mWaveLoadingView;

    //    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static ScanningFragment newInstance() {
        ScanningFragment fragment = new ScanningFragment();
        return fragment;
    }

    public ScanningFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private Resources resources;
    private String    scanning;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scanning, container, false);
        resources = getResources();
        scanning = resources.getString(R.string.scanning);
        initView(view);
        return view;
    }


    private void initView(View view) {
        headView = (FrameLayout) view.findViewById(R.id.fl_head);

        mWaveLoadingView = (WaveLoadingView) view.findViewById(R.id.waveLoadingView);
        mWaveLoadingView.setAmplitudeRatio(33);

        tv_scan_progress = (TextView) view.findViewById(R.id.tv_scan_progress);
        tv_size = (TextView) view.findViewById(R.id.tv_size);
        tv_unit = (TextView) view.findViewById(R.id.tv_unit);

        junk_sort_item_ram_progress = view.findViewById(R.id.junk_sort_item_ram_progress);
        junk_sort_item_ram_image = view.findViewById(R.id.junk_sort_item_ram_image);

        junk_sort_item_cache_progress = view.findViewById(R.id.junk_sort_item_cache_progress);
        junk_sort_item_cache_image = view.findViewById(R.id.junk_sort_item_cache_image);

        junk_sort_item_residual_progress = view.findViewById(R.id.junk_sort_item_residual_progress);
        junk_sort_item_residual_image = view.findViewById(R.id.junk_sort_item_residual_image);

        junk_sort_item_apk_progress = view.findViewById(R.id.junk_sort_item_apk_progress);
        junk_sort_item_apk_image = view.findViewById(R.id.junk_sort_item_apk_image);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //        Log.i("wdh","onActivityCreated");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //        try {
        //            mListener = (OnFragmentInteractionListener) activity;
        //        } catch (ClassCastException e) {
        //            throw new ClassCastException(activity.toString()
        //                    + " must implement OnFragmentInteractionListener");
        //        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //        mListener = null;
    }

    public void scanning(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        tv_scan_progress.setText(scanning + path);
    }

    public void scanState(int state) {
        switch (state) {
            case ScanState.SCAN_RAM_END:
                junk_sort_item_ram_progress.setVisibility(View.GONE);
                junk_sort_item_ram_image.setVisibility(View.VISIBLE);
                break;
            case ScanState.SCANING_APK_FILE_END:
                junk_sort_item_apk_progress.setVisibility(View.GONE);
                junk_sort_item_apk_image.setVisibility(View.VISIBLE);
                break;
            case ScanState.SCANING_APP_CACHE_END:
                junk_sort_item_cache_progress.setVisibility(View.GONE);
                junk_sort_item_cache_image.setVisibility(View.VISIBLE);
                break;
            case ScanState.SCANING_RESIDUAL_END:
                junk_sort_item_residual_progress.setVisibility(View.GONE);
                junk_sort_item_residual_image.setVisibility(View.VISIBLE);
                break;

        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void scanColor(TransitionDrawable drawable, int duration) {
        int value;
        if (drawable == (TransitionDrawable) resources.getDrawable(R.drawable.drawable_blue2green)) {
            value = 10;
        } else if (drawable == (TransitionDrawable) resources.getDrawable(R.drawable.drawable_green2oragle)) {
            value = 30;
        } else {
            value = 70;
        }
        Log.d("CleaningFragment", "value:" + value);
        mWaveLoadingView.setProgressValue(value);
        mWaveLoadingView.setAmplitudeRatio(33);

        if (headView != null && drawable != null) {
//            headView.setBackground(drawable.mutate());
            drawable.startTransition(duration);
        }
    }

    public void scanColor(int color) {
        int value;
        if (color == resources.getColor(R.color.clean_bg_green)) {
            value = 10;
        } else if (color == resources.getColor(R.color.clean_bg_orange)) {
            value = 30;
        } else {
            value = 70;
        }
        Log.d("CleaningFragment", "value:" + value);
        mWaveLoadingView.setProgressValue(value);

       /* if (headView != null) {
            //            ColorDrawable cd = new ColorDrawable(color);
            //            headView.setBackgroundDrawable(cd.mutate());
            headView.setBackgroundColor(color);
        }*/
    }

    public void setScanSize(String[] array) {
        if (array == null || array.length != 2) {
            return;
        }
        String strSize = array[0];
        String strUnit = array[1];
        tv_size.setText("" + strSize);
        tv_unit.setText("" + strUnit);

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
    //    public interface OnFragmentInteractionListener {
    //        public void onFragmentInteraction(Uri uri);
    //    }

}
