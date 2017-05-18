package com.ymnet.onekeyclean.cleanmore.framewrok.bean;

/**
 * Created with IntelliJ IDEA.
 * Date: 4/15/16
 * Author: zhangcm
 */
public class PageListBean<T> extends ListBean<T> {
    protected PageBean page;

    public PageBean getPage() {
        return page;
    }

    public PageListBean setPage(PageBean page) {
        this.page = page;
        return this;
    }
}
