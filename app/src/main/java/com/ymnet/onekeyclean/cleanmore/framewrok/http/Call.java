package com.ymnet.onekeyclean.cleanmore.framewrok.http;

import android.support.annotation.NonNull;

import com.ymnet.onekeyclean.cleanmore.framewrok.exception.ConvertException;

import java.io.IOException;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Date: 4/6/16
 * Author: zhangcm
 */
public interface Call<T> {



    Call<T> initialize(Request request);

    /**
     * 同步执行请求
     *
     * @throws IOException
     */
    T execute() throws IOException, ConvertException;

    /**
     * 异步执行请求
     *
     * @param callback 回调对象
     */
    void enqueue(@NonNull Callback<T> callback);

    /**
     * 获取传入的URL
     *
     * @return 传入的URL
     */
    String getUrl();

    /**
     * 获取原始请求参数(即未加密参数)和加密参数
     *
     * @return 原始请求参数(即未加密参数)和加密参数
     */
    Map<String, String> getParams();
}
