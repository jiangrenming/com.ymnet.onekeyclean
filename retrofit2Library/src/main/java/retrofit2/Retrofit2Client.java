package retrofit2;

import error_handler.RxErrorHandlingCallAdapterFactory;
import okhttp.OkHttp;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converfactory.StringConverterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jrm on 2017-3-29.
 * 针对返回值：字符串；gson格式;Oservable<T>三种数据类型的处理,以及发生excepition的转换器，防止发生exception
 */

public abstract  class  Retrofit2Client {

    public  Retrofit.Builder retrofitBuilder;

    public Retrofit2Client(){
        retrofitBuilder = new Retrofit.Builder()
                //设置OKHttpClient
                .client(OkHttp.INSTANCE.getOkHttpClient())
                //增加返回值为Oservable<T>的支持，这下面两部分暂时不用~以后需要拓展使用时,可以直接使用
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                //gson转化器
               .addConverterFactory(GsonConverterFactory.create())
                //String转换器
                .addConverterFactory(StringConverterFactory.create());
                //error的错误转换器,没有用到RxJava，所以这部分暂时可以不需要~，以后要是需要用到RxJava的话，可以选择性的使用
                //.addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create());
    }
}
