package com.ymnet.onekeyclean.cleanmore.wechat;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.ymnet.onekeyclean.cleanmore.utils.C;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * Date: 7/12/16
 * Author: zhangcm
 */
public class MTask {

    private static final int      CPU_COUNT         = Runtime.getRuntime().availableProcessors();
    private static final int      CORE_POOL_SIZE    = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    private static final int      MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    public static       Executor sExecutor         = new ThreadPoolExecutor(
            CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, 10, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(),
            new RejectedExecutionHandler() {
                @Override
                public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                    Toast.makeText(C.get(), "操作太频繁,线程池已经爆炸", Toast.LENGTH_SHORT).show();
                }
            }
    );

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
