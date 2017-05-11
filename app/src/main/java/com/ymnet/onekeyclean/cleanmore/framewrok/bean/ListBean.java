package com.ymnet.onekeyclean.cleanmore.framewrok.bean;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Date: 4/15/16
 * Author: zhangcm
 */
public class ListBean<T> {
    protected List<T> list;

    public List<T> getList() {
        return list;
    }

    public ListBean setList(List<T> list) {
        this.list = list;
        return this;
    }
}
