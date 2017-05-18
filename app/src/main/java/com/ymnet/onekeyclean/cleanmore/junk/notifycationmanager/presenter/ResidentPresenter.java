package com.ymnet.onekeyclean.cleanmore.junk.notifycationmanager.presenter;

/**
 * Created by huyang on 2016/4/13.
 */
public interface ResidentPresenter {

    /**
     * Refresh memory numerical reading in ongoing notification.
     * @param percent The latest memory numerical reading.
     */
    void updateMemoryPercent(int percent);

    /**
     * Either show or hide app update tips.
     * @param status The latest app update status.
     */
    void updateRedPoint(boolean status);

    /**
     * Hide app update tips when user clicked
     * the update component of ongoing notification.
     */
    void removeRedPoint();

    /**
     * Update skin style includes background and text color
     * of ongoing notification when skin style in settings changed.
     */
    void updateSkinStyle();

    /**
     * Call this when first show ongoing notification, or
     * something has changed which has invalidated this notification.
     */
    void updateResidentView();

    /**
     * Either update or hide ongoing notification depends on
     * whether the status of notification switch in settings changed.
     */
    void showResidentView();
}
