package com.ymnet.onekeyclean.cleanmore.uninstall.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MajinBuu on 2017/6/27 0027.
 *
 * @overView ${todo}.
 */

public class IgnoreInfo {
    private List<String> mIgnoreList = new ArrayList<>();
    public static IgnoreInfo newInstance() {
        IgnoreInfo info = new IgnoreInfo();
        return info;
    }
    public List<String> getList() {
        mIgnoreList.add("com.ymnet.apphelper");
        mIgnoreList.add("com.ymnet.tylauncher");
        mIgnoreList.add("com.ymnet.onekeyclean");
        return mIgnoreList;
    }
}
