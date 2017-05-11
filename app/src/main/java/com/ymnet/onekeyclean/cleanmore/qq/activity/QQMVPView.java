package com.ymnet.onekeyclean.cleanmore.qq.activity;


import com.ymnet.onekeyclean.cleanmore.wechat.view.MVPView;

/**
 * Created by MajinBuu on 2017/5/3 0003.
 *
 * @overView ${todo}.
 */

public interface QQMVPView extends MVPView {

    public void updateData();

    public void setText(String str);

    public void hideLoading();

    public void showLoading();


    public void startAnim();

    public void stopAnim();


    public void changeDivider();
}
