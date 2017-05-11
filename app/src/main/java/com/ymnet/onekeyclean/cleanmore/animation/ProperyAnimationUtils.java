package com.ymnet.onekeyclean.cleanmore.animation;

import android.view.View;

import com.nineoldandroids.animation.IntEvaluator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;
import com.nineoldandroids.view.ViewHelper;

public class ProperyAnimationUtils {
	
	public  static  void performAnimate(final View target, final int start,final int end) {
		if(target==null||target.getLayoutParams()==null){return;}
        ValueAnimator valueAnimator = ValueAnimator.ofInt(1, 100);
		ViewHelper.setPivotX(target, 0.5f);
		ViewHelper.setPivotY(target, 0);
		valueAnimator.addUpdateListener(new AnimatorUpdateListener() {
			private IntEvaluator mEvaluator = new IntEvaluator();
			@Override
			public void onAnimationUpdate(ValueAnimator animator) {
				int currentValue = (Integer) animator.getAnimatedValue();
				float fraction = currentValue / 100f;
				target.getLayoutParams().height = mEvaluator.evaluate(fraction,start, end);
				target.requestLayout();
			}
		});

		valueAnimator.setDuration(900).start();
	}
	public  static  void performAnimateT(final View target, final int start,final int end) {
		if(target==null||target.getLayoutParams()==null){return;}
		ValueAnimator valueAnimator = ValueAnimator.ofInt(1, 100);

		valueAnimator.addUpdateListener(new AnimatorUpdateListener() {

			// 持有一个IntEvaluator对象，方便下面估值的时候使用
			private IntEvaluator mEvaluator = new IntEvaluator();

			@Override
			public void onAnimationUpdate(ValueAnimator animator) {
				// 获得当前动画的进度值，整型，1-100之间
				int currentValue = (Integer) animator.getAnimatedValue();
				// 计算当前进度占整个动画过程的比例，浮点型，0-1之间
				float fraction = currentValue / 100f;

				// 这里我偷懒了，不过有现成的干吗不用呢
				// 直接调用整型估值器通过比例计算出宽度，然后再设给view
				target.getLayoutParams().height = mEvaluator.evaluate(fraction,start, end);

				target.requestLayout();
			}
		});

		valueAnimator.setDuration(200).start();
	}

	public static ValueAnimator powerfulAnimator(BaseAnimatorUpdateListener listener){
		ValueAnimator valueAnimator = ValueAnimator.ofInt(1, 100);
		valueAnimator.addUpdateListener(listener);
		return valueAnimator;
	}

	public static class BaseAnimatorUpdateListener implements AnimatorUpdateListener{
		private UpdateCallBack callback;
		public BaseAnimatorUpdateListener(UpdateCallBack callback) {
			this.callback = callback;
		}

		@Override
		public void onAnimationUpdate(ValueAnimator animator) {
			int currentValue = (Integer) animator.getAnimatedValue();
			float fraction = currentValue / 100f;
			callback.call(fraction);
		}
	}

	public interface UpdateCallBack{
		 void call(float f);
	}
}
