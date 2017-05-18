package com.ymnet.onekeyclean.cleanmore.framewrok.http;/*
package com.example.baidumapsevice.framewrok.http;


import android.support.annotation.NonNull;

import com.example.baidumapsevice.framewrok.bean.PageBean;
import com.example.baidumapsevice.framewrok.bean.PageListBean;
import com.example.baidumapsevice.framewrok.bean.PageListResponse;
import com.example.baidumapsevice.framewrok.exception.ConvertException;

import java.io.IOException;
import java.util.Map;

import okhttp3.ResponseBody;

*/
/**
 * Created with IntelliJ IDEA.
 * Date: 4/6/16
 * Author: zhangcm
 *//*

public class PageCall<T> extends ProxyCall<PageListResponse<T>> {

    private int                           mPage;
    private int                           mPendingPage;
    private Callback<PageListResponse<T>> mCallback;

    @Override
    protected PageListResponse<T> convert(ResponseBody response) {
        PageListResponse<T> t = super.convert(response);
        PageListBean<T> data = t.getData();
        if (data != null) {
            PageBean pageBean = data.getPage();
            if (pageBean != null) {
                mPage = pageBean.getNowPage();
            }
        }
        return t;
    }

    @Override
    protected void addParams(Map<String, String> params) {
        if (mPendingPage > 0) {
            params.put("page", mPendingPage + "");
        }
    }


    public PageListResponse<T> next() throws IOException, ConvertException {
        nextPage();
        return super.execute();
    }

    public void next(Callback<PageListResponse<T>> callback) {
        nextPage();
        super.enqueue(callback);
    }

    public void nextEnqueue() {
        if (mCallback != null) {
            next(mCallback);
        }
    }

    @Override
    public void enqueue(@NonNull Callback<PageListResponse<T>> callback) {
        mCallback = callback;
        super.enqueue(callback);
    }

    public void seek(int page) throws IOException, ConvertException {
        setPage(page);
        execute();
    }

    public void seek(int page, Callback<PageListResponse<T>> callback) {
        setPage(page);
        enqueue(callback);
    }


    private void setPage(int page) {
        mPendingPage = page;
    }

    private void nextPage() {
        mPendingPage = (mPage + 1);
    }

    public int currentPage() {
        return mPage < 1 ? 1 : mPage;
    }

    public int pendingPage() {
        return mPendingPage < 1 ? 1 : mPendingPage;
    }
}*/
