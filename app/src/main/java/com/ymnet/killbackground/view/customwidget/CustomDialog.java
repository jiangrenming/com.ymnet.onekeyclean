package com.ymnet.killbackground.view.customwidget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

import com.ymnet.onekeyclean.R;

/**
 * @author Maj1nBuu
 * @data 2017/6/20 09:33.
 * @overView 自定义dialog
 */

public class CustomDialog extends Dialog {

    public CustomDialog(Context context) {
        super(context, R.style.custom_dialog_style);
        Window window = getWindow();
        LayoutParams attributes = window.getAttributes();
        attributes.gravity = Gravity.CENTER;
//        attributes.verticalMargin = 0.1f;
        window.setAttributes(attributes);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(LayoutParams.FLAG_NOT_TOUCH_MODAL, LayoutParams.FLAG_NOT_TOUCH_MODAL);
        getWindow().setFlags(LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        setContentView(R.layout.view_dialog);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == event.ACTION_OUTSIDE) {
            return true;
        }
        return super.onTouchEvent(event);
    }

}
