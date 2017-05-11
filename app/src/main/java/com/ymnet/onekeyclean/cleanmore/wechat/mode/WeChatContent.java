package com.ymnet.onekeyclean.cleanmore.wechat.mode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by wangdh on 5/3/16.
 * gmail:wangduheng26@gamil.com
 * 2345:wangdh@2345.com
 */
public class WeChatContent {
    private long size;
    private List<WeChatFileType> datas;

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

    public WeChatContent() {
        size = 0;
    }

    public void add(WeChatFileType data) {
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

    public List<WeChatFileType> getDatas() {
        return datas;
    }

    public void setDatas(List<WeChatFileType> datas) {
        this.datas = datas;
    }

    public WeChatFileType get(int position) {
        if (datas == null) return null;
        try {
            return datas.get(position);
        } catch (ArrayIndexOutOfBoundsException aiobe) {
            return null;
        }
    }

    public void filterEmpty(FilterListener listener) {
        if (datas == null || datas.isEmpty()) return;
        Iterator<WeChatFileType> iterator = datas.iterator();
        while (iterator.hasNext()) {
            WeChatFileType next = iterator.next();
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
        Iterator<WeChatFileType> iterator = datas.iterator();
        while (iterator.hasNext()) {
            WeChatFileType type = iterator.next();
            if (WeChatFileType.DELETE_CLOSE == type.getDeleteStatus()) {
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
