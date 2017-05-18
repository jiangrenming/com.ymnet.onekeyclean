package com.ymnet.killbackground.retrofitservice.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by jrm on 2017-3-30.
 */

public class Folder implements Serializable{

    private int                         code ;
    private String                      msg;
    private ArrayList<FolderLodingInfo> data;

    public ArrayList<FolderLodingInfo> getData() {
        return data;
    }

    public void setData(ArrayList<FolderLodingInfo> data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
