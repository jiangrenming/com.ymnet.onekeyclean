package com.ymnet.onekeyclean.cleanmore.root.shell;

import android.util.SparseArray;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created with IntelliJ IDEA.
 * Date: 15/7/31
 * Author: zhangcm
 */
public class RootThreadPool {

    private LinkedBlockingQueue<ShellRunnable>     mTaskQueue   = new LinkedBlockingQueue<>();
    private ConcurrentLinkedQueue<RootThread>      mIdleThreads = new ConcurrentLinkedQueue<>();
    private SparseArray<WeakReference<RootThread>> mWorkThreads = new SparseArray<>();
    private volatile boolean                       mStopped     = false;

    public RootThreadPool() {
        RootThread thread = new RootThread(mTaskQueue, mIdleThreads);
        thread.start();
        mIdleThreads.offer(thread);
    }

    public synchronized void execute(ShellRunnable task) {
        if (this.mStopped) {
            throw new IllegalStateException("ThreadPool is stopped");
        }

        RootThread workThread = mIdleThreads.poll();
        if (workThread == null) {
            workThread = new RootThread(mTaskQueue, mIdleThreads);
            workThread.start();
        }
        mWorkThreads.put(workThread.hashCode(), new WeakReference<RootThread>(workThread));

        try {
            this.mTaskQueue.put(task);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void stop() {
        this.mStopped = true;
        for (RootThread thread; (thread = mIdleThreads.poll()) != null; ) {
            thread.toStop();
        }

        for (int i = 0; i < mWorkThreads.size(); i++) {
            RootThread thread = mWorkThreads.valueAt(i).get();
            if (thread != null) {
                thread.toStop();
            }
        }
    }

}
