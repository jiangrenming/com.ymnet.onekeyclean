package com.example.commonlibrary.utils;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

/**
 * Created by MajinBuu on 2017/4/10 0010.
 *
 * @overView 强大的吐司，能够连续弹的吐司,且能在子线程中弹吐司
 */

public class ToastUtil {
    private static Toast toast;

    /**
     * 强大的吐司，能够连续弹的吐司,且能在子线程中弹吐司
     *
     * @param text 内容
     */
    public static void showToast(Context context, final String text) {
        //判断是否是主线程,如果不是就调用Looper.prepare()方法
        if (!"main".equals(Thread.currentThread().getName())) {
            Looper.prepare();
        }
        if (toast == null) {
//            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        } else {
            toast.setText(text);//如果不为空，则直接改变当前toast的文本
        }
        //判断是否在主线程中,如果是主线程就直接show,如果不是就调用Looper.loop()方法
        if ("main".equals(Thread.currentThread().getName())) {
            toast.show();
        } else {
            toast.show();
            Looper.loop();
        }
    }
}
