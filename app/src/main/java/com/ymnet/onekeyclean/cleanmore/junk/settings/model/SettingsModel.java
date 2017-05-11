package com.ymnet.onekeyclean.cleanmore.junk.settings.model;

/**
 * Created by huyang on 2016/4/12.
 */
public interface SettingsModel {

    /**
     * Default ongoing notification switch status
     */
    boolean STATUS_OPEN = true;

    /**
     * Default skin style index
     */
    int NUMBER_DEFAULT = 0;

    /**
     * Light skin style index
     */
    int NUMBER_LIGHT = 1;

    /**
     * Dark skin style index
     */
    int NUMBER_BLACK = 2;

    /**
     * Get status of ongoing notification switch.
     * @return true if it is opened; false otherwise.
     */
    boolean getSettingsSwitchStatus();

    /**
     * Save status of ongoing notification switch.
     * @param isOpen the latest switch status.
     * @return true if save successfully; false otherwise.
     */
    boolean setSettingsSwitchStatus(boolean isOpen);

    /**
     * Get skin style of ongoing notification.
     * @return preferred skin style.
     */
    int getSettingsStyle();

    /**
     * Save preferred skin style.
     * @param index the latest skin style.
     * @return true if save successfully; false otherwise.
     */
    boolean setSettingsStyle(int index);

}
