package com.ymnet.onekeyclean.cleanmore.framewrok.bean;

/**
 * 分页信息
 * <p/>
 * nowPage : 1
 * pageSize : 25
 * pageCount : 90
 * dataCount : 2233
 * <p/>
 * Created with IntelliJ IDEA.
 * Date: 4/15/16
 * Author: zhangcm
 */
public class PageBean {

    /**
     * 当前页数
     */
    private int nowPage;

    /**
     * 每页数量
     */
    private int pageSize;

    /**
     * 分页数量
     */
    private int pageCount;

    /**
     * 数据数量
     */
    private int dataCount;

    public int getDataCount() {
        return dataCount;
    }

    public PageBean setDataCount(int dataCount) {
        this.dataCount = dataCount;
        return this;
    }

    public int getNowPage() {
        return nowPage;
    }

    public PageBean setNowPage(int nowPage) {
        this.nowPage = nowPage;
        return this;
    }

    public int getPageCount() {
        return pageCount;
    }

    public PageBean setPageCount(int pageCount) {
        this.pageCount = pageCount;
        return this;
    }

    public int getPageSize() {
        return pageSize;
    }

    public PageBean setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }
}
