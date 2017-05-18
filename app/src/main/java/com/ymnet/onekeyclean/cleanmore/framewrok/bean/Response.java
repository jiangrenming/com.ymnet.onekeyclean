package com.ymnet.onekeyclean.cleanmore.framewrok.bean;

/**
 * Created with IntelliJ IDEA.
 * Date: 4/10/16
 * Author: zhangcm
 */
public class Response<T> {

    private int code;
    private String message;
    private T data;

    public int getCode() {
        return code;
    }

    public Response setCode(int code) {
        this.code = code;
        return this;
    }

    public T getData() {
        return data;
    }

    public Response setData(T data) {
        this.data = data;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Response setMessage(String message) {
        this.message = message;
        return this;
    }
}
