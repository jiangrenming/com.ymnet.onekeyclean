package com.ymnet.killbackground.view.customwidget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;

/**
 * @author Maj1nBuu
 * @data 2017/2/8 21:14.
 * @overView ${TODO}
 * @updater $Author$
 * @updateTime $Date$
 * @updateOverView ${TODO}
 */

public class BoomView extends View {

    private WindowManager mWindowManager;


    public BoomView(Context context) {
        this(context, null);
    }

    public BoomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public BoomView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }

    private void init() {

        mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
    }



}
