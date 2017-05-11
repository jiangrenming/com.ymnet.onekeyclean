package com.ymnet.killbackground.view.customwidget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by MajinBuu on 2017/4/13 0013.
 *
 * @overView 水球爆炸自定义布局
 */

public class BoomLayout extends FrameLayout {

    public BoomLayout(Context context) {
        this(context,null);
    }

    public BoomLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BoomLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //布置控件位置
        final ImageView childAt = (ImageView) getChildAt(0);
        childAt.layout((int)(x-childAt.getMeasuredWidth()/2),(int)(y-childAt.getMeasuredHeight()/2),(int)(x+childAt.getMeasuredWidth()/2),(int)(y+childAt.getMeasuredHeight()/2));
    }

    private float x;
    private float y;
    public void setPosition( float x, float y) {
        this.x = x;
        this.y = y;
    }

}
