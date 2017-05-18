package com.ymnet.onekeyclean.cleanmore.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.ymnet.onekeyclean.R;


/**
 * Created by wangduheng26 on 15/6/1.
 */
public class CleanAccelerationBallView extends View {
    private final double PI2 = 2 * Math.PI;
    private final float X_SPACE = 20;
    private final float mWaveMultiple = 2f;

    private RefreshProgressRunnable mRefreshProgressRunnable;

    private Paint mPaint;

    private TextPaint textPaint;
    private RectF rectF = new RectF();

    //Draw wave field
    private float mAboveOffset = 0.0f;

    private float mWaveLength;
    private int mWaveHeight = 8;
    private float mWaveHz = 0.09f;

    private float mMaxRight;
    private int left, top, right, bottom;
    private double omega;
    private Paint mAboveWavePaint = new Paint();
    private Path mAboveWavePath = new Path();

    private int unfinishColor;
    private int finishColor;
    private int waveColor;
    private int progress;
    private int pelletHeight;
    public CleanAccelerationBallView(Context context) {
        this(context, null);
    }

    public CleanAccelerationBallView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CleanAccelerationBallView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        pelletHeight = getResources().getDrawable(R.drawable.pellet_zhezao_2).getIntrinsicHeight();
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CleanAccelerationBallView, defStyleAttr, 0);
        initByAttributes(typedArray);
        typedArray.recycle();

    }

    private void initByAttributes(TypedArray attr) {
        unfinishColor = attr.getColor(R.styleable.CleanAccelerationBallView_unfininsh_color, Color.TRANSPARENT);
        finishColor = attr.getColor(R.styleable.CleanAccelerationBallView_finish_color, getResources().getColor(R.color.ball_finish_color));
        waveColor = attr.getColor(R.styleable.CleanAccelerationBallView_wave_color, getResources().getColor(R.color.wave_color));
        progress = attr.getInt(R.styleable.CleanAccelerationBallView_clean_progress, 50);
        initCilrclePaint();
        initTextPaint();
        initWavePaint();
    }

    private void initWavePaint() {
        mAboveWavePaint.setColor(waveColor);
        mAboveWavePaint.setStyle(Paint.Style.FILL);
        mAboveWavePaint.setAntiAlias(true);

    }

    private void initTextPaint() {
        textPaint = new TextPaint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(28);
        textPaint.setAntiAlias(true);
    }

    private void initCilrclePaint() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measure(widthMeasureSpec);
        int height = measure(heightMeasureSpec);
        int temp = Math.min(width, height);
        rectF.set(0, 0, temp, temp);
        setMeasuredDimension(temp, temp);
    }

    private int measure(int measureSpec) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = pelletHeight;
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }

        }
        return result;
    }

    public void startWave() {
        if (getWidth() != 0) {
            int width = (int) rectF.width();//287
            mWaveLength = width * mWaveMultiple;
            left = (int) rectF.left;
            top = getWaveTop() - mWaveHeight;
            right = (int) rectF.right;
            bottom = top + mWaveHeight;
            mMaxRight = right + X_SPACE;
            omega = PI2 / mWaveLength;
        }
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        if (this.progress > 100) {
            this.progress = 100;
            return;
        }
        requestLayout();
    }

    private int getWaveTop() {
        int mWaveToTop = (int) (getHeight() * (1f - getProgress() / 100f));
        return mWaveToTop;
    }

    private class RefreshProgressRunnable implements Runnable {
        public void run() {
            synchronized (CleanAccelerationBallView.this) {
                long start = System.currentTimeMillis();
                calculatePath();
                invalidate();
                long gap = 16 - (System.currentTimeMillis() - start);
                postDelayed(this, gap < 0 ? 0 : gap);
            }
        }
    }

    /**
     * compule wave locus
     */
    private void calculatePath() {
        mAboveWavePath.reset();
        getWaveOffset();
        float y;
        mAboveWavePath.moveTo(left, bottom);
        for (float x = 0; x <= mMaxRight; x += X_SPACE) {
            y = (float) (mWaveHeight * Math.sin(omega * x + mAboveOffset) + mWaveHeight);
            y += top;
            mAboveWavePath.lineTo(x, y);
        }
        mAboveWavePath.lineTo(right, bottom);

    }

    /**
     * get wave offset or reset 0
     */
    private void getWaveOffset() {
        if (mAboveOffset > Float.MAX_VALUE - 100) {
            mAboveOffset = 0;
        } else {
            mAboveOffset += mWaveHz;
        }
    }

    /**
     * stop or start wave
     * pause you should call startOrStopWave(flase)
     * resume you should call startOrStopWave(true)
     *
     * @param flag
     */
    public void startOrStopWave(boolean flag) {

        if (flag) {
            removeCallbacks(mRefreshProgressRunnable);
            mRefreshProgressRunnable = new RefreshProgressRunnable();
            post(mRefreshProgressRunnable);
        } else {
            removeCallbacks(mRefreshProgressRunnable);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        startWave();
        float yHeight = getProgress() / (float) 100 * (getHeight());
        float radius = getWidth() / 2f;
        float angle = (float) (Math.acos((radius - yHeight) / radius) * 180 / Math.PI);
        float startAngle = 90 + angle;
        float sweepAngle = 360 - angle * 2;
        mPaint.setColor(unfinishColor);
        canvas.drawArc(rectF, startAngle, sweepAngle, false, mPaint);

        canvas.save();
        canvas.rotate(180, getWidth() / 2f, getHeight() / 2f);
        mPaint.setColor(finishColor);
        canvas.drawArc(rectF, 270 - angle, angle * 2, false, mPaint);
        canvas.restore();
        //draw wave
        if(!isAnimation){
            canvas.drawPath(mAboveWavePath, mAboveWavePaint);
        }

    }
    private boolean isAnimation;

    public boolean isAnimation() {
        return isAnimation;
    }

    public void setIsAnimation(boolean isAnimation) {
        this.isAnimation = isAnimation;
        invalidate();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        startWave();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        startOrStopWave(View.GONE != visibility);
    }
}
