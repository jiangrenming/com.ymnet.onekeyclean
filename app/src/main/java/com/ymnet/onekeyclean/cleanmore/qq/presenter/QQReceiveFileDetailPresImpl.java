package com.ymnet.onekeyclean.cleanmore.qq.presenter;


import com.ymnet.onekeyclean.cleanmore.qq.mode.QQReceiveMode;
import com.ymnet.onekeyclean.cleanmore.wechat.mode.WareFileInfo;

import java.util.List;

/**
 * Created by MajinBuu on 2017/5/5 0005.
 *
 * @overView ${todo}.
 */

public class QQReceiveFileDetailPresImpl implements QQDetailPresenter<QQReceiveMode> {
    @Override
    public QQReceiveMode getData() {
        return null;
    }

    @Override
    public void remove() {

    }

    @Override
    public int getSelectCount() {
        return 0;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public void export(int count) {

    }

    @Override
    public void changeSingle(WareFileInfo info) {

    }

    @Override
    public void changeList(List<WareFileInfo> infos, boolean targetStatus) {

    }

    @Override
    public String checkExportFileLimit() {
        return null;
    }

    @Override
    public boolean checkStorage() {
        return false;
    }
}
