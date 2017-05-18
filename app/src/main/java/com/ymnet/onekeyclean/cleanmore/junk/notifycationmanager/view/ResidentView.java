package com.ymnet.onekeyclean.cleanmore.junk.notifycationmanager.view;


import com.ymnet.onekeyclean.cleanmore.junk.notifycationmanager.medium.ResidentViewData;

/**
 * Created by huyang on 2016/4/13.
 */
public interface ResidentView {

    /**
     * Initialize and show ongoing notification in the status bar.
     * @param data The processed data used to initialize notification.
     */
    void showResidentView(ResidentViewData data);

    /**
     * Cancel ongoing notification.
     */
    void hideResidentView();

    /**
     * Update memory numerical reading in ongoing notification.
     * @param data The processed data used to update memory numerical reading.
     */
    void updateMemoryPercent(ResidentViewData data);

    /**
     * Refresh status of app update tips,
     * either show or hide a red point in the upper right corner.
     * @param data The processed data used to refresh status of app update tips.
     */
    void updateRedPoint(ResidentViewData data);

}
