package com.ymnet.onekeyclean.cleanmore.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.ymnet.onekeyclean.cleanmore.utils.DisplayUtil;


/**
 * Created by wangduheng26 on 15/3/19.
 */
public class SGTextView extends TextView {
    private Paint mPaint;
//    private Paint gradientPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
    Typeface fromAsset;
    Shader shader;

    public SGTextView(Context context) {
        this(context, null);
    }

    public SGTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        setStyle("#ffffffff", "#00ffffff", this.getMeasuredWidth());
    }

    public SGTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }

    private void init() {
        fromAsset = Typeface.createFromAsset(getContext().getAssets(), "fonts/System San Francisco Display Regular.ttf");
        mPaint = getPaint();
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

    }


    public void setStyle(String startColor,String endColor,int gradientHeighDp){
//        fromAsset = Typeface.createFromAsset(getContext().getAssets(), "fonts/cm_main_percent2345.ttf");
        mPaint = getPaint();
        mPaint.setTypeface(fromAsset);
        int[] colors=new int[]{Color.parseColor(startColor),Color.parseColor(endColor)};
        float[] positions=new float[]{0,1};
        shader=new LinearGradient(0,0,0,
                DisplayUtil.dip2px(getContext(),gradientHeighDp),
                colors,positions,Shader.TileMode.MIRROR);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setFilterBitmap(true);

        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        float textSize = getTextSize();
        int radio=DisplayUtil.dip2px(getContext(),8);
        if(radio>25){
            radio=25;
        }
        mPaint.setShadowLayer(radio, 0, DisplayUtil.dip2px(getContext(), 6), Color.parseColor("#33000000"));
        mPaint.setColor(Color.parseColor("#ffffffff"));

//        gradientPaint.setTypeface(fromAsset);
//        gradientPaint.setShader(shader);
//        gradientPaint.setShadowLayer(10, 0, 8, Color.parseColor("#33000000"));
//        gradientPaint.setTextSize(textSize);

        mPaint.setTextSize(textSize);

    }



    @Override
    public void setTypeface(Typeface tf, int style) {
        super.setTypeface(tf, style);
    }



    @Override
    protected void onDraw(Canvas canvas) {
//           super.onDraw(canvas);
        String text = getText().toString();
        int width = getMeasuredWidth();
        if (width == 0) {
            measure(0, 0);
            width = (int) (getMeasuredWidth() + mPaint.getStrokeWidth() * 2);
            setWidth(width);
        }

        float y = getBaseline();
        float x = (width - mPaint.measureText(text)) / 2;

//        mPaint.setShadowLayer(radio, 0, DisplayUtil.dip2px(getContext(),6), Color.parseColor("#33000000"));
//        mPaint.setShader(null);
        canvas.drawText(text, x, y, mPaint);
//        mPaint.clearShadowLayer();
//        mPaint.setShader(shader);
//        canvas.drawText(text, x, y, mPaint);

//        canvas.drawText(text, x, y, gradientPaint);
    }

}
