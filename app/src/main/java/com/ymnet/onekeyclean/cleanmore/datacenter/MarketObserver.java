package com.ymnet.onekeyclean.cleanmore.datacenter;

/**
 * @author zhangcm
 * @description
 * @time 25/10/14 19:12
 */

/**
 * {@code Observer} is the interface to be implemented by objects that
 * receive notification of updates on an {@code Observable} object.
 *
 * @see MarketObservable
 */
public interface MarketObserver {

    /**
     * This method is called if the specified {@code Observable} object's
     * {@code notifyObservers} method is called (because the {@code Observable}
     * object has been updated.
     *
     * @param observable
     *            the {@link MarketObservable} object.
     * @param data
     *            the data passed to {@link MarketObservable#notifyObservers(Object)}.
     */
    void update(MarketObservable observable, Object data);
}
