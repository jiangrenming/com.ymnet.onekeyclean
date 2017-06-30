package com.ymnet.onekeyclean.cleanmore.customview;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by MajinBuu on 2017/6/30 0030.
 *
 * @overView ${todo}.
 */

public class MainScrollUpAdvertisementView extends BaseAutoScrollUpTextView<String> {

    public MainScrollUpAdvertisementView(Context context) {
        super(context);
    }

    public MainScrollUpAdvertisementView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MainScrollUpAdvertisementView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    /**
     * 这里面的高度应该和你的xml里设置的高度一致
     */
    @Override
    protected int getAdertisementHeight() {
        return 40;
    }


   /* @Override
    public String getTextTitle(AdvertisementObject data) {
        return data.title;
    }*/

    @Override
    public String getTextInfo(String data) {
        return data;
    }


}
