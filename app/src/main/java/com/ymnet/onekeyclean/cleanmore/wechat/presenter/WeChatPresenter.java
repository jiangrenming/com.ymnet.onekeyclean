package com.ymnet.onekeyclean.cleanmore.wechat.presenter;


import com.ymnet.onekeyclean.cleanmore.wechat.mode.WeChatContent;
import com.ymnet.onekeyclean.cleanmore.wechat.mode.WeChatFileType;

/**
 * Created by wangduheng26 on 4/19/16.
 */
public interface WeChatPresenter extends IPresenter {

     WeChatContent getData();

     WeChatContent initData();

     void updateData();

     void scanEnd();


     long getSize();


     void destory();


     void remove(int position);

     WeChatFileType get(int position);

     boolean isEnd();
}
