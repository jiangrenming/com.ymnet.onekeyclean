package com.ymnet.onekeyclean.cleanmore.widget;


import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class AnimImageView extends ImageView {


	public AnimImageView(Context context) {
		super(context);
		intBackGround();
	}

	public AnimImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		intBackGround();

	}

    public void intBackGround() {
        AnimationDrawable animationDrawable = (AnimationDrawable) this.getBackground();
        if (animationDrawable != null) {
            animationDrawable.start();
        }
    }

    @Override
    public void setBackground(Drawable background) {
        super.setBackground(background);
        intBackGround();
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
        super.setBackgroundDrawable(background);
        intBackGround();
    }
}

