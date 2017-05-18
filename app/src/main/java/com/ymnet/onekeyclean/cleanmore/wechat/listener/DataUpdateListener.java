package com.ymnet.onekeyclean.cleanmore.wechat.listener;

/**
 * Created by wangduheng26 on 4/5/16.
 */
public interface DataUpdateListener {
    public void update();

    public void updateEnd();

    void removeEnd();
}
