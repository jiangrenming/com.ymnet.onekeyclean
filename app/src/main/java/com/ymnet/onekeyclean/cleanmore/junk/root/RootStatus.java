package com.ymnet.onekeyclean.cleanmore.junk.root;


/**
 * Created with IntelliJ IDEA.
 * Date: 15/7/27
 * Author: zhangcm
 */
public class RootStatus {

    private static RootStatus sRootState;

    private boolean mRootedBySU;

    private boolean mRootedBySDK;


    private RootStatus() {
    }

    public static RootStatus getInstance() {
        if (sRootState == null) {
            synchronized (RootStatus.class) {
                if (sRootState == null) {
                    sRootState = new RootStatus();
                }
            }
        }
        return sRootState;
    }


//    public void setRooted(boolean rooted) {
//        mRooted = rooted;
//    }

    public RootStatus setRootedBySU(boolean rootedBySU) {
        mRootedBySU = rootedBySU;
        return this;
    }


    public boolean isRooted() {
        return isRootedBySDK() || isRootedBySU();
    }


    /**
     * 是否通过我们自己的SDK root成功
     *
     * @return
     */
    public boolean isRootedBySDK() {
        return mRootedBySDK;
    }

    public RootStatus setRootedBySDK(boolean rootedBySDK) {
        mRootedBySDK = rootedBySDK;
        return this;
    }

    /**
     * 是否通过第三方工具ROOT
     *
     * @return
     */
    public boolean isRootedBySU() {
        return mRootedBySU;
    }


    /**
     * 获取rootsdk上次执行完毕状态
     *
     * @return
     */
   /* public int getRecentSDKRootStatus() {
        return RootManager.getInstance().getRootStatus(C.get());
    }*/


}
