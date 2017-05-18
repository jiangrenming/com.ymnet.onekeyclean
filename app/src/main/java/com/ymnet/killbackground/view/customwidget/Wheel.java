package com.ymnet.killbackground.view.customwidget;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;


/**
 * Created by MajinBuu on 2017/4/11 0011.
 *
 * @overView 自定义控件:按照圆形均匀排布控件
 */

public class Wheel extends RelativeLayout {

    private PointF        mCenter;
    private float         mRadius;
    private double        cellDegree;//夹角
    private boolean isFirst = true;
    //    private int     px      = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());

    public Wheel(Context context) {
        this(context, null);
    }

    public Wheel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        //计算圆心与半径
        compare();
        //根据当前子控件的中心点与圆心，将每个子控件的位置都排版起来(设置的值:两子控件之间夹角是均分圆夹角的1.5倍)
        for (int i = 0; i < this.getChildCount(); i++) {
            View childView = this.getChildAt(i);
            childView.layout(
                    (int) (mCenter.x + Math.sin(i * cellDegree*1.5 ) * mRadius/2 - childView.getWidth() / 2),
                    (int) (mCenter.y - Math.cos(i * cellDegree*1.5 ) * mRadius/2 - childView.getHeight() / 2),
                    (int) (mCenter.x + Math.sin(i * cellDegree*1.5 ) * mRadius/2 + childView.getWidth() / 2),
                    (int) (mCenter.y - Math.cos(i * cellDegree*1.5 ) * mRadius/2 + childView.getHeight() / 2)
            );
        }
        isFirst = false;
    }
//    private Paint mPaint = new Paint();

   /* @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setColor(Color.parseColor("#00f"));
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(3);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(24);
        //计算文字垂直居中的baseline
//        Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
//        float baseline = circleBounds.top + (circleBounds.bottom - circleBounds.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
//        canvas.drawText(percentTxt, circleProgressWidth/2, baseline, mPaint);
        canvas.drawText("123", mCenter.x, mCenter.y, mPaint);
        Log.d(TAG, "onDraw: ");
        canvas.restore();

    }*/

    private void compare() {
        //圆心
        mCenter = new PointF();
        mCenter.x = getWidth() / 2;
        mCenter.y = getHeight() / 2;
        if (isFirst) {
            //计算半径
            int maxWidth = 0;
            int maxHeight = 0;

            for (int i = 0; i < this.getChildCount(); i++) {
                View childview = this.getChildAt(i);
                if (childview.getWidth() > maxWidth) {
                    maxWidth = childview.getWidth();
                }
                if (childview.getHeight() > maxHeight) {
                    maxHeight = childview.getHeight();
                }
            }

            float r1 = mCenter.x - maxWidth / 2;
            float r2 = mCenter.y - maxHeight / 2;
            mRadius = Math.min(r1, r2);
            //计算夹角
            cellDegree = Math.PI * 2 / this.getChildCount();

        }
    }

}
