package com.ymnet.onekeyclean.cleanmore.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class DisplayUtil {

    public static int dpToPx(float dp, Resources resources){
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return (int) px;
    }
	 /** 
     * 将px值转换为dip或dp值，保证尺寸大小不变 
     *  （DisplayMetrics类中属性density）
     * @return 
     */  
    public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }  
  
    /** 
     * 将dip或dp值转换为px值，保证尺寸大小不变 
     *  （DisplayMetrics类中属性density）
     * @return 
     */  
    public static int dip2px(Context context, float dipValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dipValue * scale + 0.5f);  
    }  
  
    /** 
     * 将px值转换为sp值，保证文字大小不变 
     * （DisplayMetrics类中属性scaledDensity）
     * @return 
     */  
    public static int px2sp(Context context, float pxValue) {  
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
        return (int) (pxValue / fontScale + 0.5f);  
    }  
  
    /** 
     * 将sp值转换为px值，保证文字大小不变 
     *  
     * @param spValue 
     *            （DisplayMetrics类中属性scaledDensity）
     * @return 
     */  
    public static int sp2px(Context context, float spValue) {  
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
        return (int) (spValue * fontScale + 0.5f);  
    }

    /**
     * 得到屏幕的宽度和高度
     * @param context
     * @param value height width 默认返回height
     * @return
     */
    public static int getDispalyHeight(Activity context,String value){
        if(TextUtils.isEmpty(value)){
            value="height";
        }
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        if("height".equals(value)){
            return screenHeight;
        }else if("width".equals(value)){
            return screenWidth;
        }
        return screenWidth;

    }
}
