package com.ymnet.onekeyclean.cleanmore.root;/*
package com.example.baidumapsevice.root;

import android.text.TextUtils;

import com.example.baidumapsevice.junk.root.RootStatus;
import com.example.baidumapsevice.root.exception.NoAuthorizationException;
import com.example.baidumapsevice.root.shell.RootShell;
import com.example.baidumapsevice.root.shell.RootThreadPool;
import com.example.baidumapsevice.root.shell.SUShell;
import com.example.baidumapsevice.utils.C;



*/
/**
 * Created with IntelliJ IDEA.
 * Date: 15/7/30
 * Author: zhangcm
 *//*


public class RootSupervise {

    private static final String TAG = "ShellSupervise";

    private static RootSupervise sInstance;

    private RootThreadPool mRootThreadPool;


    private RootShell mShell;

    private boolean mRootedBySDK;

    private RootSupervise() {
        mRootedBySDK = RootStatus.getInstance().isRootedBySDK();
        if (!mRootedBySDK) {
            mRootThreadPool = new RootThreadPool();
            mShell = new SUShell(mRootThreadPool);
        } else {
            mShell = new SDKShell();
        }
    }


    public static RootSupervise requireRoot() throws NoAuthorizationException {
        if (sInstance == null) {
            synchronized (RootSupervise.class) {
                if (sInstance == null) {
                    sInstance = new RootSupervise();
                    if (!sInstance.mRootedBySDK) {
                        if (!sInstance.mShell.verifyRoot()) {
                            sInstance.mRootThreadPool.stop();
                            sInstance = null;
                            throw new NoAuthorizationException();
                        } else {
                            RootStatus.getInstance().setRootedBySU(true);
                            sInstance.execute(RootManager.getInstance().getFuInstallCmd(C.get()));
                        }
                    }
                }
            }
        }
        return sInstance;
    }

    public RootShell getShell() {
        return mShell;
    }

    public String execute(String command) {
        if (!TextUtils.isEmpty(command)) {
            return mShell.execute(command);
        }
        return "";
    }

}
*/
