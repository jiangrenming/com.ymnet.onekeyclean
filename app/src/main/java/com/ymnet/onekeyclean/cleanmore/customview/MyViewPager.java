package com.ymnet.onekeyclean.cleanmore.customview;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @author 你的名字
 * @version 2014-1-26 上午09:56:29
 * @类说明
 */
public class MyViewPager extends ViewPager {
    public final String tag=getClass().getSimpleName();
    public MyViewPager(Context context) {
        super(context);
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void setCurrentItem(int item) {
        try {
            super.setCurrentItem(item);
        } catch (IllegalStateException ie) {
            PagerAdapter adapter = getAdapter();
            // TODO: 2017/4/28 0028 找不到暂时注释
            /*if(BuildConfig.DEV_DEBUG&&adapter!=null&&adapter instanceof BigImageAdapter){
                Log.wtf(tag,"ie:"+ie.getMessage()+",other message:"+((BigImageAdapter) adapter).getDirName());
            }*/
            throw ie;
        }
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        try {
            super.setCurrentItem(item, smoothScroll);
        } catch (IllegalStateException ie) {
            PagerAdapter adapter = getAdapter();
            // TODO: 2017/4/28 0028 找不到暂时注释
            /*if(BuildConfig.DEV_DEBUG&&adapter!=null&&adapter instanceof BigImageAdapter){
                Log.wtf(tag,"ie:"+ie.getMessage()+",other message:"+((BigImageAdapter) adapter).getDirName());
            }*/
            throw  ie;
        }
    }
}
