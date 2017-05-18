package com.ymnet.onekeyclean.cleanmore.accountauthenticator;

/**
 * Some configs about account
 * <p>
 * Date:2016/5/23
 *
 * @author Aaron
 * @version V4.0
 */
public class MyAccountConfig {
    // TODO: 2017/5/17 0017 必须修改
    public static final String CONTENT_AUTHORITY = "com.ymnet.onekeyclean.cleanmore.accountauthenticator.MyAccountProvider";
    public static final String ACCOUNT_TYPE      = "com.ymnet.onekeyclean";
    public static final String ACCOUNT_NAME      = "一键清理";
    public static final String ACCOUNT_PASSWORD  = "onekeyclean";

    /**
     * how frequently the account sync should be performed, in seconds.
     */
//    public static final long ACCOUNT_SYNC_TIME = 3600;
    public static final long ACCOUNT_SYNC_TIME = 60;

    /**
     * ACCOUNT_SYNCABLE > 0 denotes syncable, 0 means not syncable, < 0 means unknown
     */
    public static final int ACCOUNT_SYNCABLE = 1;

}
