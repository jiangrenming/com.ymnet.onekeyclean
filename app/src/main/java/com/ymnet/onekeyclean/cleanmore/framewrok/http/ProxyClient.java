package com.ymnet.onekeyclean.cleanmore.framewrok.http;

import android.support.annotation.NonNull;

import com.ymnet.onekeyclean.cleanmore.framewrok.http.wrap.InnerResponseCallback;

import java.io.IOException;
import java.util.Map;

import okhttp3.ResponseBody;

/**
 * Created with IntelliJ IDEA.
 * Date: 4/7/16
 * Author: zhangcm
 */
public interface ProxyClient {

    /**
     * 以POST方式同步请求
     */
    ResponseBody executePost(@NonNull String url, @NonNull Map<String, String> params) throws IOException;

    /**
     * 以GET方式同步请求
     */
    ResponseBody executeGet(@NonNull String url) throws IOException;

    /**
     * 以POST方式异步请求
     */
    void enqueuePost(@NonNull String url, @NonNull Map<String, String> params, @NonNull InnerResponseCallback callback);

    /**
     * 以GET方式异步请求
     */
    void enqueueGet(@NonNull String url, @NonNull InnerResponseCallback callback);
}
