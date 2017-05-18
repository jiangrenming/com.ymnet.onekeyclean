package com.ymnet.onekeyclean.cleanmore.junk.notifycationmanager.medium;


import com.ymnet.onekeyclean.cleanmore.junk.notifycationmanager.model.ResidentModel;

/**
 * Data used to initialize ongoing {@link android.app.Notification}.
 * @author huyang
 * @since 2016/4/28
 */
public class ResidentViewData {

    private int mPercent;

    private int mSkinColor;

    private int mTextColor;

    private boolean isShowTips;

    private int[] mIds;


    public int getSkinColor() {
        return this.mSkinColor;
    }

    public int getTextColor() {
        return this.mTextColor;
    }

    public int getMemoryPercent() {
        return this.mPercent;
    }

    public boolean isShowTips() {
        return this.isShowTips;
    }

    public int[] getIconResIds() {
        return this.mIds;
    }

    public ResidentViewData transform(ResidentModel model) {
        if (model != null) {
            this.mSkinColor = model.getSkinColor();
            this.mTextColor = model.getTextColor();
            this.mPercent = model.getMemoryPercent();
            this.isShowTips = model.isShowTips();
            this.mIds = model.getIconResIds();
            return this;
        }
        return null;
    }
}
