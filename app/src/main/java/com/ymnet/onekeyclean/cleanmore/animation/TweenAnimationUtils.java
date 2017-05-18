package com.ymnet.onekeyclean.cleanmore.animation;


import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class TweenAnimationUtils {
	/**
	 * 启动清理首页来回扫描动画
	 * @param context
	 * @param view
	 */
	public static void startScanTranslateAnimation(Context context,View view){
		int screenWidth=getScreenWidth(context);
		MyTranslateAnimation scanAnimation = new MyTranslateAnimation(screenWidth);
		scanAnimation.setRepeatCount(Animation.INFINITE);
		view.startAnimation(scanAnimation);
	}
	private static int getScreenWidth(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}
	
	public static void startAnimation(Context context,View view,int id,AnimationListenerAdapter listener){
		Animation anim = AnimationUtils.loadAnimation(context,id);
		if(listener!=null)
		anim.setAnimationListener(listener);
		view.startAnimation(anim);

    }
}
