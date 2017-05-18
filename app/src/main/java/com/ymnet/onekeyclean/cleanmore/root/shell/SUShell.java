package com.ymnet.onekeyclean.cleanmore.root.shell;


import android.util.Log;

import com.ymnet.onekeyclean.cleanmore.root.exception.NoAuthorizationException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.ymnet.onekeyclean.cleanmore.root.Constants.COMMAND_BEGIN;
import static com.ymnet.onekeyclean.cleanmore.root.Constants.COMMAND_END;
import static com.ymnet.onekeyclean.cleanmore.root.Constants.COMMAND_LINE_END;

/**
 * Created with IntelliJ IDEA.
 * Date: 15/8/4
 * Author: zhangcm
 */
public class SUShell extends RootShell {
    private static final String TAG = "SUShell";

    private RootThreadPool mRootThreadPool;

    public SUShell(RootThreadPool rootThreadPool) {
        mRootThreadPool = rootThreadPool;
    }

    @Override
    public String execute(final String command) {
        final StringBuilder sb = new StringBuilder();
        final Lock lock = new ReentrantLock();
        final Condition condition = lock.newCondition();
        lock.lock();
        try {
            mRootThreadPool.execute(new ShellRunnable() {
                @Override
                public void execute(Process proc, BufferedReader in, DataOutputStream out) throws NoAuthorizationException {
                    try {
                        lock.lock();
                        Log.i(TAG, "cmd:" + command);
                        out.write(command.getBytes());
                        out.write(COMMAND_LINE_END.getBytes());
                        out.write(("echo " + COMMAND_END).getBytes());
                        out.write(COMMAND_LINE_END.getBytes());
                        out.flush();

                        String s;
                        while ((s = in.readLine()) != null) {
                            Log.i(TAG, "cmd s:" + s);
                            sb.append(s);
                            if (COMMAND_END.equals(s)) {
                                break;
                            }
                        }
                        if (sb.length() == 0) {
                            throw new NoAuthorizationException();
                        }
                        Log.i(TAG, "cmd result:" + sb.toString());

                        condition.signal();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        lock.unlock();
                    }
                }
            });
            condition.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return sb.toString();
    }

    @Override
    public boolean verifyRoot() {
        final boolean[] result = {false};
        final Lock lock = new ReentrantLock();
        final Condition condition = lock.newCondition();
        lock.lock();

        Log.i(TAG, "verifyRoot lock");
        try {
            mRootThreadPool.execute(new ShellRunnable() {
                @Override
                public void execute(Process proc, BufferedReader in, DataOutputStream out) {
                    try {
                        Log.i(TAG, "execute lock");
                        out.write(("echo " + COMMAND_BEGIN).getBytes());
                        out.write(COMMAND_LINE_END.getBytes());
                        out.flush();
                        while (true) {
                            String line = in.readLine();
                            Log.i(TAG, "execute line:" + line);
                            lock.lock();
                            try {
                                if (null == line) {
                                    condition.signal();
                                    break;
                                }
                                if ("".equals(line)) {
                                    continue;
                                }
                                if (COMMAND_BEGIN.equals(line)) {
                                    result[0] = true;
                                    condition.signal();
                                    break;
                                }
                            } finally {
                                lock.unlock();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            try {
                Log.i(TAG, "verifyRoot await");
                condition.await(15, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } finally {
            lock.unlock();
        }
        Log.i(TAG, "verifyRoot await" + result[0]);

        return result[0];
    }
}
