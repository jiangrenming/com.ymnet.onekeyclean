package com.ymnet.onekeyclean.cleanmore.wechat.view;

/**
 * Created by wangduheng26 on 4/1/16.
 */
public interface WeChatMvpView extends MVPView {
    public void updateData();

    public void setText(String str);

    public void hideLoading();

    public void showLoading();


    public void startAnim();

    public void stopAnim();


    public void changeDivider();

}
