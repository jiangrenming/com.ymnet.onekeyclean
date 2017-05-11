package com.ymnet.onekeyclean.cleanmore.root.shell;


import android.util.Log;

import com.ymnet.onekeyclean.cleanmore.root.exception.NoAuthorizationException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static com.ymnet.onekeyclean.cleanmore.root.Constants.COMMAND_SU;

/**
 * Created with IntelliJ IDEA.
 * Date: 15/7/31
 * Author: zhangcm
 */
public class RootThread extends Thread {

    private static final String TAG = RootThread.class.getSimpleName();
    private LinkedBlockingQueue<ShellRunnable> mTaskQueue = null;
    private ConcurrentLinkedQueue<RootThread> mIdleThreads = null;

    private volatile boolean mStopped = false;

    private boolean mSleep = false;

    private Process mProc;
    private BufferedReader mIn;
    private DataOutputStream mOut;

    public RootThread(LinkedBlockingQueue<ShellRunnable> queue, ConcurrentLinkedQueue<RootThread> idleThreads) {
        mTaskQueue = queue;
        mIdleThreads = idleThreads;
    }

    public void run() {


        try {
            mProc = new ProcessBuilder(COMMAND_SU).redirectErrorStream(true).start();
            mIn = new BufferedReader(new InputStreamReader(mProc.getInputStream()));
            mOut = new DataOutputStream(mProc.getOutputStream());

            Log.i(TAG, "RootThread run");

            while (!isStopped()) {
                try {
                    ShellRunnable runnable;
                    if (mSleep) {
                        runnable = mTaskQueue.take();
                        Log.i(TAG, "RootThread run" + runnable);
                    } else {
                        runnable = mTaskQueue.poll(30, TimeUnit.SECONDS);
                        Log.i(TAG, "RootThread run" + runnable);
                    }
                    if (runnable != null) {
                        try {
                            runnable.execute(mProc, mIn, mOut);
                            mIdleThreads.offer(this);
                        } catch (NoAuthorizationException e) {
                            mStopped = true;
                            e.printStackTrace();
                        }
                    } else {
                        synchronized (RootThread.class) {//防止两条线程同时醒来互相stop
                            if (!isStopped() && !this.equals(mIdleThreads.peek())) {
                                RootThread idler = mIdleThreads.poll();
                                if (idler != null) {
                                    idler.toStop();
                                }
                                if (this.equals(mIdleThreads.peek())) {
                                    Log.i(TAG, "mSleep");
                                    mSleep = true;
                                }
                                Log.i(TAG, "mSleep false");
                            }
                        }//synchronized结束
                    }
                } catch (InterruptedException e) {
                    Log.i(TAG, "", e);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            Log.i(TAG, "", e);
            e.printStackTrace();
        } finally {
            if (mProc != null) {
                try {
                    mProc.destroy();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (mIn != null) {
                try {
                    mIn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (mOut != null) {
                try {
                    mOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void toStop() {
        mStopped = true;
        if (mProc != null) {
            mProc.destroy();
        }
        if (mIn != null) {
            try {
                mIn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mOut != null) {
            try {
                mOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.interrupt();
    }

    public boolean isStopped() {
        return mStopped;
    }

}
