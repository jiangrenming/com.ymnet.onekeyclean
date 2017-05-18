package com.ymnet.onekeyclean.cleanmore.framewrok.http;/*
package com.example.baidumapsevice.framewrok.http;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;

import com.example.baidumapsevice.framewrok.exception.ConvertException;
import com.example.baidumapsevice.framewrok.http.wrap.InnerResponseCallback;
import com.example.baidumapsevice.wechat.BaseApplication;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.ResponseBody;

*/
/**
 * Created with IntelliJ IDEA.
 * Date: 4/6/16
 * Author: zhangcm
 *//*

public class ProxyCall<T> implements Call<T> {


    private static final String TAG = "ProxyCall";
    */
/**
     * 请求方式
     *
     * @see Request.Method#POST
     * @see Request.Method#GET
     *//*

    protected int mMethod;

    */
/**
     * 请求地址
     *//*

    protected String mUrl;

    */
/**
     * 请求参数
     *//*

    protected Map<String, String> mParams = new ArrayMap<>();

    */
/**
     * 加密方式
     *//*

    protected int mAlgorithm;


    */
/**
     * 转换器
     *//*

    @NonNull
    protected Converter mConverter;

    */
/**
     * 请求的返回类型
     *//*

    @NonNull
    protected Type mReturnType;


    protected Request mRequest;

    */
/**
     * 原始请求参数(即未加密参数)和加密参数
     *//*

    protected Map<String, String> mOriginParams = new ArrayMap<>();


    protected ProxyClient mProxyClient = ProxyClientFactory.createOkHttp3Client();

    protected Gson mGson;


    public Call<T> initialize(@NonNull Request request) {
        this.mRequest = request;
        return this;
    }

    @Override
    public T execute() throws IOException, ConvertException {
        assign();
        if (!isNetOn()) {
            throw new IOException("do not have active network");
        }
        preTreatment();
        ResponseBody response;
        if (Request.Method.POST == mMethod) {
            response = mProxyClient.executePost(mUrl, mParams);
        } else {
            response = mProxyClient.executeGet(mUrl);
        }

        T t;
        try {
            t = convert(response);
            if (t == null) {
                throw new ConvertException("response is null");
            }
        } catch (Exception e) {
            throw new ConvertException(e);
        }
        return t;
    }

    protected T convert(ResponseBody response) {
        return mConverter.convert(response, mReturnType);
    }

    @Override
    public void enqueue(@NonNull final Callback<T> callback) {
        if (callback != null) {
            assign();
            if (!isNetOn()) {
                callback.onFailure(ProxyCall.this, new IOException("do not have active network"));
                return;
            }
            preTreatment();
            InnerResponseCallback stringCallback = createCallback(callback);

            if (Request.Method.POST == mMethod) {
                mProxyClient.enqueuePost(mUrl, mParams, stringCallback);
            } else {
                mProxyClient.enqueueGet(mUrl, stringCallback);
            }
        }
    }

    @NonNull
    private InnerResponseCallback createCallback(@NonNull final Callback<T> callback) {
        return new InnerResponseCallback() {
            @Override
            public void onResponse(final ResponseBody response) {
                Observable.just(response)
                        .observeOn(Schedulers.io())
                        .map(new Func1<ResponseBody, T>() {
                            @Override
                            public T call(ResponseBody s) {
                                T t;
                                try {
                                    t = convert(s);
                                } catch (Exception e) {
                                    throw Exceptions.propagate(new ConvertException(e));
                                }
                                return t;
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<T>() {
                            @Override
                            public void call(T t) {
                                if (t == null) {
                                    callback.onFailure(ProxyCall.this, new ConvertException("response is null"));
                                } else {
                                    callback.onResponse(ProxyCall.this, t);
                                }
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Observable.just(throwable)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Action1<Throwable>() {
                                            @Override
                                            public void call(Throwable t) {
                                                callback.onFailure(ProxyCall.this, t);
                                            }
                                        });
                            }
                        });
            }


            @Override
            public void onFailure(Throwable throwable) {
                Observable.just(throwable)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Throwable>() {
                            @Override
                            public void call(Throwable t) {
                                callback.onFailure(ProxyCall.this, t);
                            }
                        });
            }
        };
    }

    @Override
    public String getUrl() {
        return mUrl;
    }


    @Override
    public Map<String, String> getParams() {
        return mOriginParams;
    }


    */
/**
     * 增加加密参数
     * 如果是strcode方式加密，即{@link SC#SALT_BASE64},在加密之后拼加公共参数
     * 如果是计算sign，即{@link SC#SALT_MD5},在拼加公共参数再计算sign值
     *//*

    protected void preTreatment() {
        addParams(mParams);
        mOriginParams.clear();
        if (mMethod == Request.Method.POST) {
            if (mAlgorithm == SC.SALT_BASE64) {
                String encrypt = "";
                if (!mParams.isEmpty()) {
                    mOriginParams.putAll(mParams);
                    String json = getGson().toJson(mParams);
                    encrypt = SC.encryptString(BaseApplication.getInstance(), json, SC.SALT_BASE64);
//                    encrypt = DataUtils.encode(json);
                    mParams.clear();
                }
                mParams.put("zsData", encrypt);
                setCommonParams();
                mOriginParams.putAll(mParams);
            } else if (mAlgorithm == SC.SALT_MD5) {
                setCommonParams();
                Set<String> keySet = mParams.keySet();
                List<String> keys = new ArrayList<>(keySet);
                Collections.sort(keys);
                StringBuilder sb = new StringBuilder();
                for (String key : keys) {
                    sb.append(key)
                            .append('=')
                            .append(mParams.get(key))
                            .append('+');
                }
                String encrypt = SC.encryptString(BaseApplication.getInstance(), sb.toString(), SC.SALT_MD5);
//                String encrypt = DataUtils.strCode(DataUtils.getMD5(sb.toString()));
                mParams.put("zsSign", encrypt);
                mOriginParams.putAll(mParams);
            }
        } else {
            setCommonParams();
            mOriginParams.putAll(mParams);
            assembleUrl();
        }

    }

    private void assign() {
        if (mUrl == null) {
            this.mUrl = mRequest.getUrl();
            this.mMethod = mRequest.getMethod();
            this.mAlgorithm = mRequest.getAlgorithm();
            this.mConverter = mRequest.getConverter();
            this.mReturnType = mRequest.getReturnType();
        }
        this.mParams.clear();
        this.mParams.putAll(mRequest.getParams());
    }


    */
/**
     * 拼加公共参数
     *//*

    protected void setCommonParams() {
        mParams.put("zsVerCode", BaseApplication.versioncode + "");
        mParams.put("zsChannel", BaseApplication.getChannel());
    }

    protected void addParams(Map<String, String> params) {
    }

    */
/**
     * 拼装url
     *//*

    protected void assembleUrl() {
        setCommonParams();
        int index = mUrl.lastIndexOf("?");
        StringBuilder sb = new StringBuilder(mUrl);
        if (index < 0) {
            sb.append('?');
        } else {
            sb.append('&');
        }
        int len = mParams.size();
        int i = 0;
        for (Map.Entry<String, String> entries : mParams.entrySet()) {
            sb.append(entries.getKey())
                    .append("=")
                    .append(entries.getValue());
            if (++i != len) {
                sb.append('&');
            }
        }
        mUrl = sb.toString();
    }


    protected Gson getGson() {
        if (mGson == null) {
            mGson = new Gson();
        }
        return mGson;
    }


    private boolean isNetOn() {
        ConnectivityManager connectMgr = (ConnectivityManager) BaseApplication.getInstance()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobNetInfo = connectMgr.getActiveNetworkInfo();
        return mobNetInfo != null;
    }


}
*/
