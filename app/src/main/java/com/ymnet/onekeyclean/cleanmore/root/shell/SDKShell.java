package com.ymnet.onekeyclean.cleanmore.root.shell;/*
package com.example.baidumapsevice.root.shell;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

*/
/**
 * Created with IntelliJ IDEA.
 * Date: 15/8/4
 * Author: zhangcm
 *//*

public class SDKShell extends RootShell {
    private static final String TAG = "SDKShell";

    @Override
    public String execute(final String command) {

        final StringBuilder msg = new StringBuilder();

        final Lock lock = new ReentrantLock();
        final Condition condition = lock.newCondition();
        lock.lock();

        try {
            RootManager.getInstance().shell(command, new IRootManger.IShellCallback() {
                @Override
                public void onStart() {
                    lock.lock();
                }

                @Override
                public void onProcess(String s) {
                    L.i(TAG, "onProcess:" + s);
                    msg.append(s);
                }

                @Override
                public void onStop() {
                    condition.signal();
                    lock.unlock();
                }
            });

            condition.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

        return msg.toString();
    }
}
*/
