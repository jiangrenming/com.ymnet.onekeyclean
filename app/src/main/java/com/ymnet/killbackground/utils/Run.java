package com.ymnet.killbackground.utils;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.ymnet.onekeyclean.cleanmore.utils.C;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by MajinBuu on 2017/5/10.
 */
public class Run {

    private static final int      CPU_COUNT         = Runtime.getRuntime().availableProcessors();
    private static final int      CORE_POOL_SIZE    = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    private static final int      MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static       Handler  sHandler          = new Handler(Looper.getMainLooper());//获取主线程的Looper

    private static       Executor sExecutor         = new ThreadPoolExecutor(
            CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, 10, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(),
            new RejectedExecutionHandler() {
                @Override
                public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                    Toast.makeText(C.get(), "操作太频繁,线程池已经爆炸", Toast.LENGTH_SHORT).show();
                }
            }
    );

    public static void onSub(Runnable runnable) {
        sExecutor.execute(runnable);
    }

    public static void onMain(Runnable runnable) {
        sHandler.post(runnable);
    }

    public static void onMain(Runnable runnable, long delay) {
        sHandler.postDelayed(runnable, delay);
    }

}
