package com.ymnet.onekeyclean.cleanmore.wechat.mode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by wangdh on 6/14/16.
 * gmail:wangduheng26@gamil.com
 */
public class ListDataMode implements Serializable {
    private List<WareFileInfo> content;
    private String             name;
    private boolean            isExpand;
    private long               currentSize;

    public long getCurrentSize() {
        return currentSize;
    }

    public boolean isEmpty() {
        return content == null || content.isEmpty();
    }

    public void setCurrentSize(long currentSize) {
        this.currentSize = currentSize;
    }

    public List<WareFileInfo> getContent() {
        return content;
    }

    public void setContent(List<WareFileInfo> content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }

    public void add(WareFileInfo info) {
        if (info == null)
            return;
        if (content == null) {
            content = new ArrayList<>();
        }

        content.add(info);
        currentSize += info.size;

    }

    /**
     * 删除标记数据
     */
    public void sync(SyncListener listener) {
        if (content == null || content.isEmpty())
            return;
        Iterator<WareFileInfo> iterator = content.iterator();
        while (iterator.hasNext()) {
            WareFileInfo next = iterator.next();
            if (next.hasDelete) {

                currentSize -= next.size;
                if (listener != null) {
                    listener.removeSize(next.size);
                }
                iterator.remove();
            }
        }
        if (currentSize < 0)
            currentSize = 0;
    }


    public interface SyncListener {
        void removeSize(long size);
    }
}
