package com.ymnet.onekeyclean.cleanmore.wechat.listener;

import android.view.View;

/**
 * Created by wangduheng26 on 4/6/16.
 */
public interface RecyclerViewClickListener {
    /**
     * Callback method to be invoked when a item in a
     * RecyclerView is clicked
     *
     * @param v        The view within the RecyclerView.Adapter
     * @param position The position of the view in the adapter
     */
    void onClick(View v, int position);

    void selectState(long selectSize, boolean flag);
}
