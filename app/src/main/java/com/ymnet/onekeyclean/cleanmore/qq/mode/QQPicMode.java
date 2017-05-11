package com.ymnet.onekeyclean.cleanmore.qq.mode;


import com.ymnet.onekeyclean.cleanmore.wechat.mode.ListDataMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangdh on 6/14/16.
 * gmail:wangduheng26@gamil.com
 * 2345:wangdh@2345.com
 */

/**
 * [{"name","size",["WareFileInfo"]},{...},{...}]
 */
public class QQPicMode extends QQFileType {

    private List<ListDataMode> pics;

    public QQPicMode(String fileName, long fileSize, int iconId, String fileInfo, String fileDelEffect) {
        super(fileName, fileSize, iconId, fileInfo, fileDelEffect);
        pics = new ArrayList<>();
    }

    public ListDataMode get(String name) {
        for (ListDataMode mode : pics) {
            if (name.equals(mode.getName())) {
                return mode;
            }
        }
        return null;
    }

    public void add(ListDataMode mode) {
        pics.add(mode);
    }

    public List<ListDataMode> getPics() {
        return pics;
    }

    public void setPics(List<ListDataMode> pics) {
        this.pics = pics;
    }

    public boolean isEmpty() {
        if (pics.isEmpty()) {
            return true;
        } else {
            for (ListDataMode mode : pics) {
                if (!mode.isEmpty()) {
                    return false;
                }
            }
            return true;
        }
    }
}
