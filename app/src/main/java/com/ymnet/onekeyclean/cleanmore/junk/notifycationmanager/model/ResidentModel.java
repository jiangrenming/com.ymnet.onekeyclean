package com.ymnet.onekeyclean.cleanmore.junk.notifycationmanager.model;

/**
 * Created by huyang on 2016/4/13.
 */
public interface ResidentModel {

    /**
     * Get user preferred ongoing notification background color.
     * @return preferred background color.
     */
    int getSkinColor();

    /**
     * Save user preferred ongoing notification background color.
     * @param color preferred background color.
     */
    void setSkinColor(int color);

    /**
     * Get user preferred ongoing notification text color.
     * @return preferred text color.
     */
    int getTextColor();

    /**
     * Save user preferred ongoing notification text color.
     * @param color preferred text color.
     */
    void setTextColor(int color);

    /**
     * Get last occupied memory percent.
     * @return last occupied memory percent.
     */
    int getMemoryPercent();

    /**
     * Set the latest occupied memory percent.
     * @param percent the latest occupied memory percent.
     */
    void setMemoryPercent(int percent);

    /**
     * Call this to judge whether app update tips is on showing.
     * @return true if app update tips is on showing; false otherwise.
     */
    boolean isShowTips();

    /**
     * Save the latest app update tips status.
     * @param status the latest app update tips status.
     */
    void updateTips(boolean status);

    /**
     * Save the latest drawable resource id array of ongoing notification components.
     * @param ids the latest drawable resource ids.
     */
    void setIconResIds(int[] ids);

    /**
     * Get drawable resource id array of ongoing notification components.
     * @return drawable resource id array.
     */
    int[] getIconResIds();

    /**
     * Save the latest app update tips click time point.
     * @param time the latest click time point.
     * @return true if saved successfully; false otherwise.
     */
    boolean setTipsClickTime(long time);

    /**
     * Get last app update tips click time point.
     * @return last click time.
     */
    long getTipsClickTime();
}
