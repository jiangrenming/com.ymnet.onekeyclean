package com.ymnet.onekeyclean.cleanmore.qq.mode;


import com.ymnet.onekeyclean.cleanmore.wechat.mode.WareFileInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangdh on 6/3/16.
 * gmail:wangduheng26@gamil.com
 * 详情页中选中的集合
 */
public class QQSelectDatas {
    private List<WareFileInfo> listDatas;
    private int                currentSize;

    public int getCurrentSize() {
        return currentSize;
    }
    public boolean isEmpty(){
        return listDatas==null||listDatas.isEmpty();
    }
    public void clean() {
        if (listDatas != null) listDatas.clear();
        currentSize = 0;
    }

    public List<WareFileInfo> getListDatas() {
        return listDatas;
    }

    public void add(WareFileInfo info) {
        if (info == null) return;
        if (listDatas == null) {
            listDatas = new ArrayList<>();
        }
        listDatas.add(info);
        currentSize += info.size;
    }

    public void remove(WareFileInfo info) {
        if (info == null) return;
        if (listDatas == null) return;
        listDatas.remove(info);
        currentSize -= info.size;
        if (currentSize < 0) currentSize = 0;
    }

}
