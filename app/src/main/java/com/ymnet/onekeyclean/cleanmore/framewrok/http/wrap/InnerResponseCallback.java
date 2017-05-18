package com.ymnet.onekeyclean.cleanmore.framewrok.http.wrap;


import com.ymnet.onekeyclean.cleanmore.framewrok.http.ResponseBody;

/**
 * Created with IntelliJ IDEA.
 * Date: 4/6/16
 * Author: zhangcm
 */
public interface InnerResponseCallback {

    /**
     * http返回,在主线程执行
     */
    void onResponse(ResponseBody response);

    /**
     * http错误返回,在主线程执行
     */
    void onFailure(Throwable throwable);

}
