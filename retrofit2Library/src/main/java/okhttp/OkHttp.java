package okhttp;

import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.concurrent.TimeUnit;

import okhttp.iterceptor.OfflineCacheControlInterceptor;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * Created by jrm on 2017-3-29.
 * 初始化OkHttpClient对象的使用, 供Retrofit2.0 的使用
 */

public enum  OkHttp {

    INSTANCE;
    private final OkHttpClient okHttpClient;
    private static final int TIMEOUT_READ = 10;
    private static final int TIMEOUT_CONNECTION = 10;

    private Interceptor cacheInterceptor = new OfflineCacheControlInterceptor();

    OkHttp() {
        okHttpClient = new OkHttpClient.Builder()
                //打印日志
                .addInterceptor(sLoggingInterceptor)
                 //设置Cache目录
                //  .cache(CacheUtil.getCache())
                //设置缓存
                //  .addInterceptor(cacheInterceptor)
               //   .addNetworkInterceptor(cacheInterceptor)
                //失败重连
                .retryOnConnectionFailure(true)
                //设置超时时长
                .readTimeout(TIMEOUT_READ, TimeUnit.SECONDS)
                .connectTimeout(TIMEOUT_CONNECTION, TimeUnit.SECONDS)
                .build();
    }

    public OkHttpClient getOkHttpClient( ) {
        return okHttpClient;
    }

    /**
     * 打印返回的json数据拦截器
     */
    private  final Interceptor sLoggingInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            final Request request = chain.request();
            Buffer requestBuffer = new Buffer();
            if (request.body() != null) {
                request.body().writeTo(requestBuffer);
            } else {
                Log.i("LogTAG","request.body() == null");
            }
            //打印url信息
            Log.i("LogTAG",request.url() + (request.body() != null ? "?" + _parseParams(request.body(), requestBuffer) : ""));
            final Response response = chain.proceed(request);
            return response;
        }
    };

    private static String _parseParams(RequestBody body, Buffer requestBuffer) throws UnsupportedEncodingException {
        if (body.contentType() != null && !body.contentType().toString().contains("multipart")) {
            return URLDecoder.decode(requestBuffer.readUtf8(), "UTF-8");
        }
        return "null";
    }
}
