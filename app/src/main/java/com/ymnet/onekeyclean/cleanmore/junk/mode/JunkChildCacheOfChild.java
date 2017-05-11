package com.ymnet.onekeyclean.cleanmore.junk.mode;

/**
 * Created by Administrator on 2015/1/15.
 */
public class JunkChildCacheOfChild extends JunkChild{
    public static final int SYSTEM_CACHE=0;
    public static final int APP_CACHE=1;
    public int type;
    public String fileTip;
    public String packageName;
    public String path;
}
