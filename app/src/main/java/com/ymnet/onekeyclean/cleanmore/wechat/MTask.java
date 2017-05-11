package com.ymnet.onekeyclean.cleanmore.wechat;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * Date: 7/12/16
 * Author: zhangcm
 */
public class MTask {

    public static final ExecutorService BACKGROUND_EXECUTOR = Executors.newCachedThreadPool();
    public static final Executor UI_THREAD_EXECUTOR = new UIThreadExecutor();


    private static class UIThreadExecutor implements Executor {
        private Handler mHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mHandler.post(command);
        }
    }
}
