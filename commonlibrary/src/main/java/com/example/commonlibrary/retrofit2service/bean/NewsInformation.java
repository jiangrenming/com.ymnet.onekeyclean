package com.example.commonlibrary.retrofit2service.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by jrm on 2017-5-2.
 * 信息流数据
 */

public class NewsInformation implements Serializable{
    private int                          code;
    private String                       msg;
    private ArrayList<InformationResult> data;
    private int                          page;
    private int                          count;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ArrayList<InformationResult> getData() {
        return data;
    }

    public void setData(ArrayList<InformationResult> data) {
        this.data = data;
    }
}
