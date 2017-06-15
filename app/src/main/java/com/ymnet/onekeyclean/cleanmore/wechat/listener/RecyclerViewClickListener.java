package com.ymnet.onekeyclean.cleanmore.wechat.listener;

import android.view.View;

import java.util.Map;

/**
 * Created by wangduheng26 on 4/6/16.
 */
public interface RecyclerViewClickListener {

    void onClick(View v, int position);
    void selectState(long selectSize, boolean flag, int position);
    void selectButton(Map<Integer, Boolean> weChatInfos,int position);

}
