package com.ymnet.onekeyclean.cleanmore.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by MajinBuu on 5/27/17.
 * 自定义ScrollView,设置滑动到底的监听
 */

public class BottomScrollView extends ScrollView {
  
    private OnScrollToBottomListener onScrollToBottom;  
      
    public BottomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);  
    }  
  
    public BottomScrollView(Context context) {  
        super(context);  
    }  
  
    @Override  
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX,  
            boolean clampedY) {  
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);  
        if(scrollY != 0 && null != onScrollToBottom){  
            onScrollToBottom.onScrollBottomListener(clampedY);  
        }  
    }  
      
    public void setOnScrollToBottomListener(OnScrollToBottomListener listener){
        onScrollToBottom = listener;  
    }  
  
    public interface OnScrollToBottomListener{  
        void onScrollBottomListener(boolean isBottom);
    }  
} 