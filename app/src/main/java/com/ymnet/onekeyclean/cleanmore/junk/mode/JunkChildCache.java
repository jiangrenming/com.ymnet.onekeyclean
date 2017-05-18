package com.ymnet.onekeyclean.cleanmore.junk.mode;

import java.util.List;

/**
 * Created by Administrator on 2015/1/15.
 */
public class JunkChildCache extends JunkChild{
    public static final String systemCachePackName="com.android.system.cache" ;
    public static final String adSdk="adsdk";
    public List<JunkChildCacheOfChild> childCacheOfChild;
    public String packageName;

    public String tip;
    public boolean isExpanded;
}
