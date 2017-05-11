package com.ymnet.onekeyclean.cleanmore.framewrok.exception;

/**
 * Created with IntelliJ IDEA.
 * Date: 4/18/16
 * Author: zhangcm
 */
public class ConvertException extends RuntimeException {
    public ConvertException(Throwable throwable) {
        super(throwable);
    }

    public ConvertException(String detailMessage) {
        super(detailMessage);
    }
}
