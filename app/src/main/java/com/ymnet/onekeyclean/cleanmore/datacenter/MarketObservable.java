package com.ymnet.onekeyclean.cleanmore.datacenter;


import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;


/**
 * @description 避免内存泄露
 *
 * @author zhangcm
 * @time 25/10/14 18:34
 */
public class MarketObservable {

    private static final String TAG = MarketObservable.class.getSimpleName();

    private boolean loop = false;


    LinkedList<WeakReference<MarketObserver>> observers = new LinkedList<WeakReference<MarketObserver>>();



    /**
     * Constructs a new {@code Observable} object.
     */
    public MarketObservable() {
    }

    /**
     * Adds the specified observer to the list of observers. If it is already
     * registered, it is not added a second time.
     *
     * @param observer the Observer to add.
     */
    public void addObserver(MarketObserver observer) {

        if (observer == null) {
            throw new NullPointerException("observer == null");
        }
        synchronized (this) {
            if(loop){
                throw new RuntimeException("don't call addObserver() in update() method");
            }
            observers.add(new WeakReference<MarketObserver>(observer));
        }
    }



    /**
     * Returns the number of observers registered to this {@code Observable}.
     *
     * @return the number of observers.
     */
    public int countObservers() {
        return observers.size();
    }

    /**
     * Removes the specified observer from the list of observers. Passing null
     * won't do anything.
     *
     * @param observer the observer to remove.
     */
    public synchronized void deleteObserver(MarketObserver observer) {
        loop = true;
        for (Iterator<WeakReference<MarketObserver>> iterator = observers.iterator(); iterator.hasNext(); ) {
            WeakReference<MarketObserver> reference = iterator.next();
            if (reference.get() == null || reference.get() == observer) {
                if (reference.get() == null) {
                    Log.i(TAG, "remove because of GC");
                }

                iterator.remove();
            }
        }
        loop = false;
    }

    /**
     * Removes all observers from the list of observers.
     */
    public synchronized void deleteObservers() {
        observers.clear();
    }



    /**
     * If {@code hasChanged()} returns {@code true}, calls the {@code update()}
     * method for every observer in the list of observers using null as the
     * argument. Afterwards, calls {@code clearChanged()}.
     * <p/>
     * Equivalent to calling {@code notifyObservers(null)}.
     */
    public void notifyObservers() {
        notifyObservers(null);
    }

    /**
     * If {@code hasChanged()} returns {@code true}, calls the {@code update()}
     * method for every Observer in the list of observers using the specified
     * argument. Afterwards calls {@code clearChanged()}.
     *
     * @param data the argument passed to {@code update()}.
     */
    @SuppressWarnings("unchecked")
    public void notifyObservers(Object data) {
        synchronized (this) {
            loop = true;
            for (Iterator<WeakReference<MarketObserver>> iterator = observers.iterator(); iterator.hasNext(); ) {
                WeakReference<MarketObserver> reference = iterator.next();
                MarketObserver observer = reference.get();
                if (observer == null) {
                    Log.i(TAG, "remove because of GC");
                    iterator.remove();
                } else {
                    observer.update(this, data);
                }
            }
            loop = false;
        }
    }

    /**
     * Sets the changed flag for this {@code Observable}. After calling
     * {@code setChanged()}, {@code hasChanged()} will return {@code true}.
     */
    @Deprecated
    protected void setChanged() {

    }


}
