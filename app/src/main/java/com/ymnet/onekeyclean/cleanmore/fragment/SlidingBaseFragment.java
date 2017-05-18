package com.ymnet.onekeyclean.cleanmore.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.customview.CircularProgress;



public abstract class SlidingBaseFragment extends BaseFragment {
    protected View footer;
    protected View ll_foot_loading_pb;
    protected View bar;

    protected View mRetry;

    protected View mReachBottom;

    protected View mLoadMore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    public void createDefaultFooterView(LayoutInflater inflater) {
        footer = inflater.inflate(R.layout.foot_loading, null);
        ll_foot_loading_pb=footer.findViewById(R.id.ll_foot_loading_pb);
        bar = footer.findViewById(R.id.pb_foot_loading);
        bar.setVisibility(View.GONE);
        if(bar instanceof CircularProgress){
            ((CircularProgress) bar).setName(pb_tag);
        }
        mRetry = footer.findViewById(R.id.retry);
        mReachBottom = footer.findViewById(R.id.reach_bottom);
        mLoadMore = footer.findViewById(R.id.click_loading);
    }

    public void createDefaultFooterView(LayoutInflater inflater, ViewGroup parent) {
        footer = inflater.inflate(R.layout.foot_loading, parent, false);
        ll_foot_loading_pb=footer.findViewById(R.id.ll_foot_loading_pb);
        bar = footer.findViewById(R.id.pb_foot_loading);
        bar.setVisibility(View.GONE);
        if(bar instanceof CircularProgress){
            ((CircularProgress) bar).setName(pb_tag);
        }
        mRetry = footer.findViewById(R.id.retry);
        mReachBottom = footer.findViewById(R.id.reach_bottom);
        mLoadMore = footer.findViewById(R.id.click_loading);
    }

    protected void showFootLoading() {
        try {
            checkFootView();
        } catch (Exception e) {
//            e.printStackTrace();
            return;
        }
        footer.setVisibility(View.VISIBLE);
        ll_foot_loading_pb.setVisibility(View.VISIBLE);
        bar.setVisibility(View.VISIBLE);
        mRetry.setVisibility(View.GONE);
        mReachBottom.setVisibility(View.GONE);
        mLoadMore.setVisibility(View.GONE);
    }

    protected void showFootRetry() {
        try {
            checkFootView();
        } catch (Exception e) {
//            e.printStackTrace();
            return;
        }
        footer.setVisibility(View.VISIBLE);
        ll_foot_loading_pb.setVisibility(View.GONE);
        bar.setVisibility(View.GONE);
        mRetry.setVisibility(View.VISIBLE);
        mReachBottom.setVisibility(View.GONE);
        mLoadMore.setVisibility(View.GONE);
    }

    protected void showFootReachBotton() {
        try {
            checkFootView();
        } catch (Exception e) {
//            e.printStackTrace();
            return;
        }
        footer.setVisibility(View.VISIBLE);
        ll_foot_loading_pb.setVisibility(View.GONE);
        bar.setVisibility(View.GONE);
        mRetry.setVisibility(View.GONE);
        mReachBottom.setVisibility(View.VISIBLE);
        mLoadMore.setVisibility(View.GONE);
    }

    protected void showFootLoadMore() {
        try {
            checkFootView();
        } catch (Exception e) {
//            e.printStackTrace();
            return;
        }
        footer.setVisibility(View.VISIBLE);
        ll_foot_loading_pb.setVisibility(View.GONE);
        bar.setVisibility(View.GONE);
        mRetry.setVisibility(View.GONE);
        mReachBottom.setVisibility(View.GONE);
        mLoadMore.setVisibility(View.VISIBLE);
    }

    protected void hideFootLoading() {
        try {
            checkFootView();
        } catch (Exception e) {
//            e.printStackTrace();
            return;
        }
        footer.setVisibility(View.GONE);
        bar.setVisibility(View.GONE);
        mRetry.setVisibility(View.GONE);
        mReachBottom.setVisibility(View.GONE);
        mLoadMore.setVisibility(View.GONE);
    }

    protected void checkFootView() throws Exception{
        if(footer==null||bar==null||mRetry==null||mReachBottom==null||mLoadMore==null||ll_foot_loading_pb==null){
            throw new IllegalArgumentException("loading view has null");
        }
    }

}
