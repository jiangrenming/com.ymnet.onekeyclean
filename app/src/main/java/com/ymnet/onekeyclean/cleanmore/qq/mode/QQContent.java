package com.ymnet.onekeyclean.cleanmore.qq.mode;


import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by wangdh on 5/3/16.
 * gmail:wangduheng26@gamil.com
 */
public class QQContent {
    private long size;
    private List<QQFileType> datas;

    public void sizeImmersive(long value) {
        size += value;
    }

    public long getSize() {
        return size;
    }

    public int length() {
        return datas == null ? 0 : datas.size();
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void sizeDecreasing(long value) {
        size -= value;
        if (size < 0) size = 0;
    }

    public QQContent() {
        size = 0;
    }

    public void add(QQFileType data) {
        if (data == null) return;
        if (datas == null) {
            datas = new ArrayList<>();
        }
        datas.add(data);
        sizeImmersive(data.getCurrentSize());
    }

    public void clear() {
        if (datas != null) datas.clear();
        size = 0;
    }

    public List<QQFileType> getDatas() {
        return datas;
    }

    public void setDatas(List<QQFileType> datas) {
        this.datas = datas;
    }

    public QQFileType get(int position) {
        if (datas == null) return null;
        try {
            QQFileType qqFileType = datas.get(position);
            Log.i("Tag",qqFileType.toString());
            return qqFileType;
        } catch (ArrayIndexOutOfBoundsException aiobe) {
            Log.i("Tag",aiobe.toString());
            aiobe.printStackTrace();
            return null;
        }
    }

    public void filterEmpty(FilterListener listener) {
        if (datas == null || datas.isEmpty()) return;
        Iterator<QQFileType> iterator = datas.iterator();
        while (iterator.hasNext()) {
            QQFileType next = iterator.next();
            if (next.isEmpty()) {
                iterator.remove();
                if(listener!=null){
                    listener.removeCallback();
                }
            }
        }
    }

    public void filterDelete() {
        if (datas == null || datas.isEmpty()) return;
        Iterator<QQFileType> iterator = datas.iterator();
        while (iterator.hasNext()) {
            QQFileType type = iterator.next();
            if (QQFileType.DELETE_CLOSE == type.getDeleteStatus()) {
                iterator.remove();
            }
        }

    }
    public int getType(int position){
        if(datas==null||datas.isEmpty())return 0;
        return datas.get(position).getType();
    }
    public interface FilterListener {
        void removeCallback();
    }
}
