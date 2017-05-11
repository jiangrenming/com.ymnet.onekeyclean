package com.ymnet.onekeyclean.cleanmore.junk.mode;/*
package com.example.baidumapsevice.junk.mode;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.baidumapsevice.utils.AccessibilityUtil;
import com.example.baidumapsevice.utils.C;
import com.example.baidumapsevice.wechat.R;


*/
/**
 * Created by huyang on 2016/3/4.
 *//*

public final class InstallTipsViewHelper {

    private static final int TIME_FADE_IN = 200;
    private static final int TIME_TIPS_VIEW_SHOW = 6000;

    private static final View mTipsView = LayoutInflater.from(C.get()).inflate(R.layout.accessibility_install_tips, null);
    private static final Handler mHandler = new Handler(Looper.getMainLooper());

    private static TextView mCountView;
    private static boolean isShown = false;

    public static void showTipsView() {
        if (!isShown) {
            isShown = true;
            mCountView = (TextView) mTipsView.findViewById(R.id.click_time_description);
            setClickTime();
            AccessibilityUtil.addView(C.get(), mTipsView, Gravity.CENTER, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            setAnimation(mTipsView);
        }

        removeTipsViewDelayed();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static void setAnimation(View tipsView) {
        if (tipsView == null) {
            return;
        }

        Animation animFadeIn = AnimationUtils.loadAnimation(C.get(), R.anim.fade_in);
        animFadeIn.setDuration(TIME_FADE_IN);

        Animation animBigGear = AnimationUtils.loadAnimation(C.get(), R.anim.big_gear_rotate);
        Animation animSmallGear = AnimationUtils.loadAnimation(C.get(), R.anim.small_gear_rotate);
        mTipsView.findViewById(R.id.left_gear).startAnimation(animBigGear);
        mTipsView.findViewById(R.id.right_gear).startAnimation(animSmallGear);

        tipsView.clearAnimation();
        tipsView.startAnimation(animFadeIn);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static void startGearAnim(View tipsView) {
        if (tipsView == null) {
            return;
        }

        ImageView bigGear = (ImageView) tipsView.findViewById(R.id.left_gear);
        ObjectAnimator bigAnim = ObjectAnimator.ofFloat(bigGear, "rotation", 0, 360);
        bigAnim.setInterpolator(new LinearInterpolator());
        bigAnim.setDuration(1600);
        bigAnim.setRepeatCount(ValueAnimator.INFINITE);

        ImageView smallGear = (ImageView) tipsView.findViewById(R.id.right_gear);
        ObjectAnimator smallAnim = ObjectAnimator.ofFloat(smallGear, "rotation", 360, 0);
        smallAnim.setInterpolator(new LinearInterpolator());
        smallAnim.setDuration(1200);
        smallAnim.setRepeatCount(ValueAnimator.INFINITE);

        bigAnim.start();
        smallAnim.start();
    }

    private static void setClickTime() {
        if (mCountView != null) {
            String num = String.format(C.get().getResources().getString(R.string.accessibility_click_time), AccessibilityUtil.getClickNumber());
            mCountView.setText(num);
        }
    }

    public static void updateClickTime() {
        AccessibilityUtil.saveClickNumber();
        setClickTime();
    }

    public static void removeTipsView() {
        if (isShown) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler.post(new RemoveViewTask());
        }

    }


    public static void removeTipsViewDelayed() {
        if (isShown) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler.postDelayed(new RemoveViewTask(), TIME_TIPS_VIEW_SHOW);
        }
    }

    public static boolean isTipsViewShown() {
        return isShown;
    }

    private static class RemoveViewTask implements Runnable {

        @Override
        public void run() {
            mTipsView.clearAnimation();
            isShown = false;
            AccessibilityUtil.removeView(C.get(), mTipsView);
        }
    }


}
*/
