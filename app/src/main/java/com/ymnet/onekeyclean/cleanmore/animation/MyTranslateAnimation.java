package com.ymnet.onekeyclean.cleanmore.animation;

import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

public class MyTranslateAnimation extends Animation {
    private int viewWidth;
    private int viewHight;
    private int screenWidth;
    private int scanWidth;

    public MyTranslateAnimation(int screenWidth) {
        super();
        this.screenWidth = screenWidth;
    }

    @Override
    public void initialize(int width, int height, int parentWidth,
                           int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        viewWidth = width;
        viewHight = height;
        scanWidth = (screenWidth + viewWidth) * 2;
        this.setDuration(4000); // 设置动画播放的时间
        this.setFillAfter(true); // 设置为true，动画结束的时候保持动画效果
        this.setRepeatCount(Animation.INFINITE);
        this.setInterpolator(new LinearInterpolator()); // 线性动画（速率不变）
    }

    @Override
    protected void applyTransformation(float paramFloat, Transformation t) {
        super.applyTransformation(paramFloat, t);
        if (paramFloat <= 0.5F) {
            float f1 = paramFloat * scanWidth - viewWidth;
            t.getMatrix().postTranslate(f1, 0.0F);
        } else {
            t.getMatrix().setRotate(180.0F, viewWidth / 2f, viewHight / 2f);
            float f1 = (1 - paramFloat) * (scanWidth + 2.0f * viewWidth) - viewWidth;
            t.getMatrix().postTranslate(f1, 0.0F);
        }
    }
}
