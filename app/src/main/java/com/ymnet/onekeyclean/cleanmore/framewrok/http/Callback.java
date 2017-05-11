package com.ymnet.onekeyclean.cleanmore.framewrok.http;

/**
 * Created with IntelliJ IDEA.
 * Date: 4/6/16
 * Author: zhangcm
 */
public interface Callback<T> {

    /**
     * http返回,在主线程执行
     */
    void onResponse(Call<T> call, T response);

    /**
     * http错误返回,在主线程执行
     */
    void onFailure(Call<T> call, Throwable t);

}
