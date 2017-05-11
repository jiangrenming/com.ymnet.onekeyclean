package com.ymnet.onekeyclean.cleanmore.wechat.activity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;

import com.ymnet.onekeyclean.cleanmore.ImmersiveActivity;


public class TitleBarActivity extends ImmersiveActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    private void downLoadAnimation(View v){
        AnimationSet animationSet=new AnimationSet(true);
        TranslateAnimation animation = new TranslateAnimation(0,0,0,60);
        animation.setDuration(1000);
        animation.setRepeatCount(3);
        animation.setRepeatMode(Animation.RESTART);
        animationSet.addAnimation(animation);
        v.startAnimation(animationSet);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
