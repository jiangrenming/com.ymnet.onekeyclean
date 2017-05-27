package com.ymnet.onekeyclean.cleanmore.qq.presenter;


import com.ymnet.onekeyclean.cleanmore.qq.mode.QQContent;
import com.ymnet.onekeyclean.cleanmore.qq.mode.QQFileType;
import com.ymnet.onekeyclean.cleanmore.wechat.presenter.IPresenter;

/**
 * Created by MajinBuu on 2017/5/3 0003.
 *
 */

public interface QQPresenter extends IPresenter {

    QQContent getData();

    QQContent initData();

    void updateData();

    void scanEnd();


    long getSize();


    void destory();


    void remove(int position);

    QQFileType get(int position);

    boolean isEnd();

    boolean isInstallAPP();
}
