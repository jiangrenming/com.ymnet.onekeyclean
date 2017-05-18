package com.ymnet.onekeyclean.cleanmore.qq.presenter;


import com.ymnet.onekeyclean.cleanmore.qq.mode.QQFileType;
import com.ymnet.onekeyclean.cleanmore.wechat.mode.WareFileInfo;
import com.ymnet.onekeyclean.cleanmore.wechat.presenter.IPresenter;

import java.util.List;

/**
 * Created by wangduheng26 on 4/19/16.
 */
public interface QQDetailPresenter<T extends QQFileType> extends IPresenter {

     QQFileType getData();

    void remove();

    /**
     * 选中count
     * @return
     */
    int getSelectCount();

    /**
     * 所有的count
     * @return
     */
    int getCount();

    void export(int count);

    /**
     * 改变单个文件
     * @param info
     */
     void changeSingle(WareFileInfo info);

    /**
     * 改变list文件中每个的状态为targetStatus
     * @param infos
     * @param targetStatus
     */
     void changeList(List<WareFileInfo> infos, boolean targetStatus);


    String checkExportFileLimit();

    boolean checkStorage();
}
