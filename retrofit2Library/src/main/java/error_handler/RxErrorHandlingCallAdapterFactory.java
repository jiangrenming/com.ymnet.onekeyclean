package error_handler;

import android.util.Log;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.HttpException;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by jrm on 2017-4-5.
 * 处理调用RxJava时 出现的网络请求错误异常
 */

public class RxErrorHandlingCallAdapterFactory extends CallAdapter.Factory{

    private final RxJavaCallAdapterFactory original;
    private RxErrorHandlingCallAdapterFactory() {
        original = RxJavaCallAdapterFactory.create();
        Log.i("tag---",original.toString());
    }

    public static CallAdapter.Factory create() {
        Log.i("tag","aaa");
        return new RxErrorHandlingCallAdapterFactory();
    }

    @Override
    public CallAdapter<?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        Log.i("tag---",returnType.toString());
        return new RxCallAdapterWrapper(retrofit, original.get(returnType, annotations, retrofit));
    }

    private static class RxCallAdapterWrapper implements CallAdapter<Observable<?>> {
        private final Retrofit retrofit;
        private final CallAdapter<?> wrapped;

        public RxCallAdapterWrapper(Retrofit retrofit, CallAdapter<?> wrapped) {
            this.retrofit = retrofit;
            this.wrapped = wrapped;
            Log.i("tag---",wrapped.toString());
        }

        @Override
        public Type responseType() {
            Type type = wrapped.responseType();
            return wrapped.responseType();
        }

        @SuppressWarnings("unchecked")
        @Override
        public <R> Observable<?> adapt(Call<R> call) {
            return ((Observable) wrapped.adapt(call)).onErrorResumeNext(new Func1<Throwable, Observable>() {
                @Override
                public Observable call(Throwable throwable) {
                    return Observable.error(asRetrofitException(throwable));
                }
            });
        }

        private RetrofitException asRetrofitException(Throwable throwable) {
            if (throwable instanceof HttpException) {
                HttpException httpException = (HttpException) throwable;
                Response response = httpException.response();
                return RetrofitException.httpError(response.raw().request().url().toString(), response, retrofit);
            }
            //网络错误
            if (throwable instanceof IOException) {
                return RetrofitException.networkError((IOException) throwable);
            }
            return RetrofitException.unexpectedError(throwable);
        }
    }
}
