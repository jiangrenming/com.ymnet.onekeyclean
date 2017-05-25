package com.ymnet.onekeyclean.cleanmore.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.qq.activity.QQActivity;
import com.ymnet.onekeyclean.cleanmore.utils.C;
import com.ymnet.onekeyclean.cleanmore.wechat.WeChatActivity;


public class MoreFragment extends BaseFragment implements View.OnClickListener {


    private View mQQ;

    public MoreFragment() {
        // Required empty public constructor
    }

    public static MoreFragment newInstance() {
        return new MoreFragment();
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more, container, false);
        mQQ = view.findViewById(R.id.i_morefunction_qq);
        mQQ.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.i_morefunction_qq:
                Intent intent1 = new Intent(C.get(), QQActivity.class);
                startActivity(intent1);
                break;
            case R.id.i_morefunction_wechat:
                Intent intent2 = new Intent(C.get(), WeChatActivity.class);
                startActivity(intent2);
                break;
        }
    }
}
