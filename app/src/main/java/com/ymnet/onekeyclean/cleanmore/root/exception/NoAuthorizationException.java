package com.ymnet.onekeyclean.cleanmore.root.exception;

/**
 * Created with IntelliJ IDEA.
 * Date: 15/8/5
 * Author: zhangcm
 */
public class NoAuthorizationException extends Exception {
    public NoAuthorizationException() {
    }

    public NoAuthorizationException(String detailMessage) {
        super(detailMessage);
    }

    public NoAuthorizationException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public NoAuthorizationException(Throwable throwable) {
        super(throwable);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
