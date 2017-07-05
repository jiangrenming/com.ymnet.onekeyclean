package com.ymnet.killbackground.presenter;

import android.content.Context;

/**
 * Created by MajinBuu on 2017/4/17 0017.
 *
 * @overView CleanActivity的逻辑类CleanPresenter的必要实现接口
 */

public interface CleanPresenter {
    /**
     * 杀死所有缓存
     * @param context 上下文
     * @param visible true为弹toast展示,false为不弹toast
     * @return 杀死缓存应用个数
     */
    void killAll(Context context, boolean visible);

}
