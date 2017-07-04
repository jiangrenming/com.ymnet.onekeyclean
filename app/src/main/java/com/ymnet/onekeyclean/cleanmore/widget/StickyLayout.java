package com.ymnet.onekeyclean.cleanmore.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.animation.ProperyAnimationUtils;
import com.ymnet.onekeyclean.cleanmore.wechat.MTask;

import java.util.NoSuchElementException;

/**
 * Created by wangduheng26 on 15/5/11.
 */
public class StickyLayout extends LinearLayout {
    private static final boolean DEBUG = true;
    private static final String  TAG   = "wdh";
    private HeightChangeListener    heightChangeListener;
    private View                    viewBounce;
    private IpmlScrollChangListener scroll;
    //    private BounceCalculate bc;

    public void setHeightChangeListener(HeightChangeListener heightChangeListener) {
        this.heightChangeListener = heightChangeListener;
    }

    public void setScroll(IpmlScrollChangListener scroll) {
        this.scroll = scroll;
    }

    public interface IpmlScrollChangListener {
        boolean isReadyForPull();
    }

    public View getmHeader() {
        return mHeader;
    }

    public View getmContent() {
        return mContent;
    }


    public interface OnGiveUpTouchEventListener {
        boolean giveUpTouchEvent(MotionEvent event);
    }

    private View                       mHeader;
    private View                       mContent;
    private LinearLayout               ivPelletFloatingLayer;
    private OnGiveUpTouchEventListener mGiveUpTouchEventListener;

    // header的高度  单位：px
    private int mOriginalHeaderHeight;
    private int mOriginalLayerHeight;
    private int mHeaderHeight;

    public              int mStatus          = STATUS_EXPANDED;
    public static final int STATUS_EXPANDED  = 1;
    public static final int STATUS_COLLAPSED = 2;

    private             int flagScroll = START;//标志现在的滑动的位置是开始 / 结束/ 中间
    public static final int START      = 0;
    public static final int END        = 1;
    public static final int MIDDLE     = 2;

    private int mTouchSlop;

    // 分别记录上次滑动的坐标
    private int mLastX = 0;
    private int mLastY = 0;

    // 分别记录上次滑动的坐标(onInterceptTouchEvent)
    private int mLastXIntercept = 0;
    private int mLastYIntercept = 0;

    // 用来控制滑动角度，仅当角度a满足如下条件才进行滑动：tan a = deltaX / deltaY > 2
    private static final int TAN = 2;

    private boolean mIsSticky                            = true;
    private boolean mInitDataSucceed                     = false;
    private boolean mDisallowInterceptTouchEventOnHeader = true;

    public StickyLayout(Context context) {
        super(context);
    }

    public StickyLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public StickyLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus && (mHeader == null || mContent == null)) {
            initData();
        }
    }


    public void initData() {
        //        int headerId = getResources().getIdentifier("sticky_header", "id", getContext().getPackageName());
        int headerId = getResources().getIdentifier("rl_home_head", "id", getContext().getPackageName());
        int contentId = getResources().getIdentifier("sticky_content", "id", getContext().getPackageName());
        if (headerId != 0 && contentId != 0) {
            mHeader = findViewById(headerId);
            mContent = findViewById(contentId);
            viewBounce = findViewById(R.id.v_touch_bounce);
            //            ivPelletFloatingLayer = (ImageView) findViewById(R.id.iv_pellet_floating_layer);
            ivPelletFloatingLayer = (LinearLayout) findViewById(R.id.ll_head_content);
            mOriginalLayerHeight = ivPelletFloatingLayer.getMeasuredHeight();
            mOriginalHeaderHeight = mHeader.getMeasuredHeight();
            mHeaderHeight = mOriginalHeaderHeight;
            mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
            if (mHeaderHeight > 0) {
                mInitDataSucceed = true;
            }
        } else {
            throw new NoSuchElementException("Did your view with id \"sticky_header\" or \"sticky_content\" exists?");
        }
    }

    public void setOnGiveUpTouchEventListener(OnGiveUpTouchEventListener l) {
        mGiveUpTouchEventListener = l;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        int intercepted = 0;
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mLastXIntercept = x;
                mLastYIntercept = y;
                mLastX = x;
                mLastY = y;
                intercepted = 0;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                int deltaY = y - mLastYIntercept;
                if (mDisallowInterceptTouchEventOnHeader && y <= getHeaderHeight()) {
                    intercepted = 0;
                } else if (mStatus == STATUS_EXPANDED && deltaY <= -mTouchSlop) {
                    intercepted = 1;
                } else if (mGiveUpTouchEventListener != null) {
                    if (mGiveUpTouchEventListener.giveUpTouchEvent(event) && deltaY >= mTouchSlop) {
                        intercepted = 1;
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                intercepted = 0;
                mLastXIntercept = mLastYIntercept = 0;
                break;
            }
            default:
                break;
        }

        if (!scroll.isReadyForPull()) {
            return false;
        }

        if (DEBUG) {
        }
        return intercepted != 0 && mIsSticky;
    }

    private int mDeltaY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mIsSticky) {
            return true;
        }
        /*if (bc == null) {
            bc = new BounceCalculate();
        }*/

        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                int deltaX = x - mLastX;
                int deltaY = y - mLastY;
                mDeltaY = deltaY;
                mHeaderHeight += deltaY;
                if (flagScroll == START && deltaY > 0) {
                   /* if (viewBounce != null) {
                        chageBounceHeight((int) (deltaY * bc.getNextCoefficient()));
                    }*/
                } else {
                    if (getViewBounceHeight() != 0) {
                        chageBounceHeight(deltaY);
                    } else {
                        setHeaderHeight(mHeaderHeight);
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                //                bc = null;
                if (viewBounce != null) {
                    ViewGroup.LayoutParams layoutParams = viewBounce.getLayoutParams();
                    if (layoutParams.height != 0) {
                        ProperyAnimationUtils.performAnimateT(viewBounce, layoutParams.height, 0);
                    }
                }
                // 这里做了下判断，当松开手的时候，会自动向两边滑动，具体向哪边滑，要看当前所处的位置
                int destHeight = 0;
                if (mDeltaY < 0) {
                    destHeight = 0;
                    mStatus = STATUS_COLLAPSED;
                } else {
                    destHeight = mOriginalHeaderHeight;
                    mStatus = STATUS_EXPANDED;
                }
                // 慢慢滑向终点
                this.smoothSetHeaderHeight(mHeaderHeight, destHeight, 1200);
                break;
            }
            default:
                break;
        }
        mLastX = x;
        mLastY = y;
        return true;
    }

    public void smoothSetHeaderHeight(final int from, final int to, long duration) {
        smoothSetHeaderHeight(from, to, duration, false);
    }

    public void smoothSetHeaderHeight(final int from, final int to, long duration, final boolean modifyOriginalHeaderHeight) {
        final int frameCount = (int) (duration / 1000f * 30) + 1;
        final float partation = (to - from) / (float) frameCount;
        MTask.BACKGROUND_EXECUTOR.execute(new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < frameCount; i++) {
                    final int height;
                    if (i == frameCount - 1) {
                        height = to;
                    } else {
                        height = (int) (from + partation * i);
                    }
                    post(new Runnable() {
                        public void run() {
                            //                            Log.i(TAG,"smooth set Height:"+height);
                            setHeaderHeight(height);
                        }
                    });
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (modifyOriginalHeaderHeight) {
                    setOriginalHeaderHeight(to);
                }
            }
        });
    }

    public void setOriginalHeaderHeight(int originalHeaderHeight) {
        mOriginalHeaderHeight = originalHeaderHeight;
    }

    public int getOriginalHeaderHeight() {
        return mOriginalHeaderHeight;
    }

    public void setHeaderHeight(int height, boolean modifyOriginalHeaderHeight) {
        if (modifyOriginalHeaderHeight) {
            setOriginalHeaderHeight(height);
        }
        setHeaderHeight(height);
    }

    public void setHeaderHeight(int height) {
        setAlphaDegree(height);

        if (!mInitDataSucceed) {
            initData();
        }
        if (height <= 0) {
            height = 0;
            flagScroll = END;
        } else if (height > mOriginalHeaderHeight) {
            //如果把这行注释了 则可以把头部下拉到任意位置，然后手松开的时候回弹至原位置，个人感觉这么样效果会更好一点。。
            height = mOriginalHeaderHeight;
            flagScroll = START;
        } else {
            flagScroll = MIDDLE;
        }

        if (height == 0) {
            mStatus = STATUS_COLLAPSED;
        } else {
            mStatus = STATUS_EXPANDED;
        }

        if (mHeader != null && mHeader.getLayoutParams() != null) {
            mHeader.getLayoutParams().height = height;

            mHeaderHeight = height;
            if (heightChangeListener != null) {
                if (height == mOriginalHeaderHeight) {
                    heightChangeListener.notifyChange(1f);
                } else if (height == 0) {
                    heightChangeListener.notifyChange(0f);
                } else {
                    heightChangeListener.notifyChange(ivPelletFloatingLayer.getHeight() / (mOriginalLayerHeight * 1f));
                }
            }

            mHeader.requestLayout();
            //            Log.i("wdh","ivPelletFloatingLayer:"+ivPelletFloatingLayer.getWidth()+",wv:"+wv.getWidth());
        } else {
            if (DEBUG) {
                Log.e(TAG, "null LayoutParams when setHeaderHeight");
            }
        }
    }

    private void setAlphaDegree(int height) {
        float value = (float) (height) / mOriginalHeaderHeight;
        Log.d(TAG, "value:" + value);
        ivPelletFloatingLayer.setAlpha(value);
    }

    public int getHeaderHeight() {
        return mHeaderHeight;
    }

    public void setSticky(boolean isSticky) {
        mIsSticky = isSticky;
    }

    public void requestDisallowInterceptTouchEventOnHeader(boolean disallowIntercept) {
        mDisallowInterceptTouchEventOnHeader = disallowIntercept;
    }

    public interface HeightChangeListener {
        void notifyChange(float scale);
    }

    private int getViewBounceHeight() {
        if (viewBounce != null) {
            return viewBounce.getLayoutParams().height;
        }
        return 0;
    }

    private void chageBounceHeight(int value) {
        if (viewBounce != null) {
            ViewGroup.LayoutParams params = viewBounce.getLayoutParams();
            if (params.height <= 1000) {
                params.height += value;
                viewBounce.setLayoutParams(params);
            }
        }
    }
}
