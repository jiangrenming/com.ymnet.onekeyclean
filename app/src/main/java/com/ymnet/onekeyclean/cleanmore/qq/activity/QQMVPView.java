package com.ymnet.onekeyclean.cleanmore.qq.activity;


import com.ymnet.onekeyclean.cleanmore.wechat.view.MVPView;

/**
 * Created by MajinBuu on 2017/5/3 0003.
 */

public interface QQMVPView extends MVPView {

    void updateData();

    void setText(String str);

    void hideLoading();

    void showLoading();


    void startAnim();

    void stopAnim();


    void changeDivider();
}
