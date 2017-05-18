package com.ymnet.onekeyclean.cleanmore.customview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.ymnet.onekeyclean.R;


/**
 * Loading wheel,the system version must be higher or equal
 *
 * @author Aaron
 * @see android.support.v4.widget.MaterialProgressDrawable
 * @since v3.6
 */

public class CircularProgress extends View {

    private static final Interpolator ANGLE_INTERPOLATOR = new LinearInterpolator();
    private static final Interpolator SWEEP_INTERPOLATOR = new AccelerateDecelerateInterpolator();
    private static final int ANGLE_ANIMATOR_DURATION = 1600;
    private static final int SWEEP_ANIMATOR_DURATION = 900;
    private static final int MIN_SWEEP_ANGLE = 30;
    private static final float DEFAULT_BORDER_WIDTH = 2.5f;
    private final RectF fBounds = new RectF();

    private ObjectAnimator mObjectAnimatorSweep;
    private ObjectAnimator mObjectAnimatorAngle;
    private boolean mModeAppearing = true;
    private Paint mPaint;
    private float mCurrentGlobalAngleOffset;
    private float mCurrentGlobalAngle;
    private float mCurrentSweepAngle;
    private float mBorderWidth;
    private boolean mRunning;
    private int mDrawColor;
    private int mCurrentColorIndex;

    private Bitmap mLoadingIcon;
    private boolean mIsShowLogo;

    public CircularProgress(Context context) {
        this(context, null);
    }

    public CircularProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        float density = context.getResources().getDisplayMetrics().density;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircularProgress, defStyleAttr, 0);
        mBorderWidth = a.getDimension(R.styleable.CircularProgress_borderWidth,
                DEFAULT_BORDER_WIDTH * density);
        mIsShowLogo = a.getBoolean(R.styleable.CircularProgress_isShowLogo, true);
        a.recycle();

        mLoadingIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon_launcher);

        mDrawColor = context.getResources().getColor(R.color.loading_arc);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mBorderWidth);
        mPaint.setColor(mDrawColor);
        setupAnimations();
    }

    public void start() {
        if (isRunning()) {
            return;
        }
        if (getVisibility() != VISIBLE) {
            return;
        }

        mRunning = true;
        mObjectAnimatorAngle.start();
        mObjectAnimatorSweep.start();
        invalidate();
    }

    public void stop() {
        if (!isRunning()) {
            return;
        }

        mRunning = false;
        mObjectAnimatorAngle.end();
        mObjectAnimatorSweep.end();
        invalidate();
    }

    private boolean isRunning() {
        return mRunning;
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (isShown()) {
            start();
        } else {
            stop();

        }
        if (changedView==this) {
            if(visibility == VISIBLE){
                start();
            }else{
                stop();
            }
        }
    }


//    @Override
//    public void setVisibility(int visibility) {
//        super.setVisibility(visibility);
//        synchronized (this) {
//            if (visibility == View.VISIBLE) {
//                start();
//            } else {
//                stop();
//            }
//        }
//
//    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        fBounds.left = mBorderWidth / 2f + .5f;
        fBounds.right = w - mBorderWidth / 2f - .5f;
        fBounds.top = mBorderWidth / 2f + .5f;
        fBounds.bottom = h - mBorderWidth / 2f - .5f;
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        //draw logo
        if (mIsShowLogo && mLoadingIcon != null) {
            int viewWidth = getMeasuredWidth() / 2;
            int viewHeight = getMeasuredHeight() / 2;
            canvas.drawBitmap(mLoadingIcon, viewWidth - mLoadingIcon.getWidth() / 2, viewHeight - mLoadingIcon.getHeight() / 2, mPaint);
        }

        float startAngle = mCurrentGlobalAngle - mCurrentGlobalAngleOffset;
        float sweepAngle = mCurrentSweepAngle;
        if (mModeAppearing) {
            mPaint.setColor(mDrawColor);
            sweepAngle += MIN_SWEEP_ANGLE;
        } else {
            startAngle = startAngle + sweepAngle;
            sweepAngle = 360 - sweepAngle - MIN_SWEEP_ANGLE;
        }
        canvas.drawArc(fBounds, startAngle, sweepAngle, false, mPaint);
    }

    private void toggleAppearingMode() {
        mModeAppearing = !mModeAppearing;
        if (mModeAppearing) {
            mCurrentColorIndex = ++mCurrentColorIndex % 4;
            mCurrentGlobalAngleOffset = (mCurrentGlobalAngleOffset + MIN_SWEEP_ANGLE * 2) % 360;
        }
    }

    private Property<CircularProgress, Float> mAngleProperty = new Property<CircularProgress, Float>(Float.class, "angle") {
        @Override
        public Float get(CircularProgress object) {
            return object.getCurrentGlobalAngle();
        }

        @Override
        public void set(CircularProgress object, Float value) {
            object.setCurrentGlobalAngle(value);
        }
    };

    private Property<CircularProgress, Float> mSweepProperty = new Property<CircularProgress, Float>(Float.class, "arc") {
        @Override
        public Float get(CircularProgress object) {
            return object.getCurrentSweepAngle();
        }

        @Override
        public void set(CircularProgress object, Float value) {
            object.setCurrentSweepAngle(value);
        }
    };

    private void setupAnimations() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
        mObjectAnimatorAngle = ObjectAnimator.ofFloat(this, mAngleProperty, 360f);
        mObjectAnimatorAngle.setInterpolator(ANGLE_INTERPOLATOR);
        mObjectAnimatorAngle.setDuration(ANGLE_ANIMATOR_DURATION);
        mObjectAnimatorAngle.setRepeatMode(ValueAnimator.RESTART);
        mObjectAnimatorAngle.setRepeatCount(ValueAnimator.INFINITE);

        mObjectAnimatorSweep = ObjectAnimator.ofFloat(this, mSweepProperty, 360f - MIN_SWEEP_ANGLE * 2);
        mObjectAnimatorSweep.setInterpolator(SWEEP_INTERPOLATOR);
        mObjectAnimatorSweep.setDuration(SWEEP_ANIMATOR_DURATION);
        mObjectAnimatorSweep.setRepeatMode(ValueAnimator.RESTART);
        mObjectAnimatorSweep.setRepeatCount(ValueAnimator.INFINITE);
        mObjectAnimatorSweep.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                toggleAppearingMode();
            }
        });
//        }

    }

    public void setCurrentGlobalAngle(float currentGlobalAngle) {
        mCurrentGlobalAngle = currentGlobalAngle;
        invalidate();
    }

    public float getCurrentGlobalAngle() {
        return mCurrentGlobalAngle;
    }

    public void setCurrentSweepAngle(float currentSweepAngle) {
        mCurrentSweepAngle = currentSweepAngle;
        invalidate();
    }

    public float getCurrentSweepAngle() {
        return mCurrentSweepAngle;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }

    @Override
    protected void onAttachedToWindow() {
        start();
        super.onAttachedToWindow();
    }

    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
