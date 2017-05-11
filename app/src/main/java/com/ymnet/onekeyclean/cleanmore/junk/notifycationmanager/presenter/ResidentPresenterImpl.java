package com.ymnet.onekeyclean.cleanmore.junk.notifycationmanager.presenter;/*
package com.example.baidumapsevice.junk.notifycationmanager.presenter;

import android.content.res.Resources;

import com.example.baidumapsevice.constants.TimeConstants;
import com.example.baidumapsevice.junk.notifycationmanager.medium.ResidentViewData;
import com.example.baidumapsevice.junk.notifycationmanager.model.ResidentModel;
import com.example.baidumapsevice.junk.notifycationmanager.view.ResidentView;
import com.example.baidumapsevice.junk.settings.model.SettingsModel;
import com.example.baidumapsevice.utils.C;
import com.example.baidumapsevice.wechat.R;

import java.util.Timer;
import java.util.TimerTask;

*/
/**
 * Created by huyang on 2016/4/13.
 *//*

public class ResidentPresenterImpl implements ResidentPresenter {

    private ResidentView mResidentView;

    private SettingsModel mSettingsModel;

    private ResidentModel mResidentModel;

    private TimerTask mRefreshTask;

    private ResidentViewData mData;

    private static final int DELAY = 10000;

    private static final int REFRESH_INTERVAL = 60000;

    ResidentPresenterImpl(ResidentView view) {
        this.mResidentView = view;
        this.mSettingsModel = ModelFactory.getSettingsModel();
        this.mResidentModel = ModelFactory.getResidentModel();
        this.mData = new ResidentViewData();
    }

    @Override
    public void updateMemoryPercent(int percent) {
        boolean switchStatus = this.mSettingsModel.getSettingsSwitchStatus();
        if (!switchStatus || percent == mResidentModel.getMemoryPercent()) {
            return;
        }

        this.mResidentModel.setMemoryPercent(percent);
        if (mResidentView != null) {
            this.mResidentView.updateMemoryPercent(this.mData.transform(mResidentModel));
        }
    }

    @Override
    public void updateRedPoint(boolean status) {
        boolean switchStatus = this.mSettingsModel.getSettingsSwitchStatus();
        if (!switchStatus || !updateTipsStatus(status)) {
            return;
        }

        if (mResidentView != null) {
            this.mResidentView.updateRedPoint(this.mData.transform(mResidentModel));
        }
    }

    @Override
    public void removeRedPoint() {
        if (!mResidentModel.isShowTips()) {
            return;
        }

        this.mResidentModel.setTipsClickTime(System.currentTimeMillis());
        this.mResidentModel.updateTips(false);
        if (mResidentView != null) {
            this.mResidentView.updateRedPoint(this.mData.transform(mResidentModel));
        }
    }

    private boolean updateTipsStatus(boolean status) {
        long current = System.currentTimeMillis();
        if (current - mResidentModel.getTipsClickTime() > TimeConstants.DAY) {
            if (status != mResidentModel.isShowTips()) {
                this.mResidentModel.updateTips(status);
                return true;
            }
        }
        return false;
    }

    @Override
    public void updateSkinStyle() {
        boolean switchStatus = this.mSettingsModel.getSettingsSwitchStatus();
        if (mResidentView == null || !switchStatus) {
            return;
        }

        this.initSkinStyle();
        this.mResidentView.showResidentView(this.mData.transform(mResidentModel));
    }

    @Override
    public void updateResidentView() {
        if (mResidentView == null) {
            return;
        }

        boolean switchStatus = this.mSettingsModel.getSettingsSwitchStatus();
        if (!switchStatus) {
            return;
        }

        this.initSkinStyle();

        int currentPercent = getMemoryPercent();
        if (mResidentModel.getMemoryPercent() != currentPercent) {
            this.mResidentModel.setMemoryPercent(currentPercent);
        }

        DataCenterObserver session = DataCenterObserver.get(C.get());
        int number = session.getUpgradeNumber();
        updateTipsStatus(number > 0);

        this.mResidentView.showResidentView(this.mData.transform(mResidentModel));
        this.autoRefreshMemPercent();
    }

    @Override
    public void showResidentView() {
        if (mResidentView == null) {
            return;
        }
        boolean switchStatus = this.mSettingsModel.getSettingsSwitchStatus();
        if (!switchStatus) {
            if (mRefreshTask != null) {
                mRefreshTask.cancel();
                mRefreshTask = null;
            }
            this.mResidentView.hideResidentView();
        } else {
            updateResidentView();
        }
    }

    private void autoRefreshMemPercent() {

        if (mRefreshTask != null) {
            mRefreshTask.cancel();
            mRefreshTask = null;
        }

        mRefreshTask = new TimerTask() {
            @Override
            public void run() {
                updateMemoryPercent(getMemoryPercent());
            }
        };

        new Timer().schedule(mRefreshTask, DELAY, REFRESH_INTERVAL);
    }

    private int getMemoryPercent() {
        return new TaskInfoProvider(C.get()).getUsedPercent();
    }

    private void initSkinStyle() {
        int skinStyle = this.mSettingsModel.getSettingsStyle();
        Resources res = C.get().getResources();
        switch (skinStyle) {
            case SettingsModel.NUMBER_DEFAULT:
                int textColor = res.getColor(android.R.color.white);
                int skinColor = res.getColor(android.R.color.transparent);
                this.mResidentModel.setTextColor(textColor);
                this.mResidentModel.setSkinColor(skinColor);
                this.mResidentModel.setIconResIds(new int[]{R.drawable.icon_speed_light, R.drawable.icon_clean_light,
                        R.drawable.icon_search_light, R.drawable.icon_upgrade_light,
                        R.drawable.icon_manager_light, R.drawable.icon_setting_light});
                break;
            case SettingsModel.NUMBER_LIGHT:
                textColor = res.getColor(R.color.main_text_color);
                skinColor = res.getColor(android.R.color.white);
                this.mResidentModel.setTextColor(textColor);
                this.mResidentModel.setSkinColor(skinColor);
                this.mResidentModel.setIconResIds(new int[]{R.drawable.icon_speed_dark, R.drawable.icon_clean_dark,
                        R.drawable.icon_search_dark, R.drawable.icon_upgrade_dark,
                        R.drawable.icon_manager_dark, R.drawable.icon_setting_dark});
                break;
            case SettingsModel.NUMBER_BLACK:
                textColor = res.getColor(android.R.color.white);
                skinColor = res.getColor(android.R.color.black);
                this.mResidentModel.setTextColor(textColor);
                this.mResidentModel.setSkinColor(skinColor);
                this.mResidentModel.setIconResIds(new int[]{R.drawable.icon_speed_light, R.drawable.icon_clean_light,
                        R.drawable.icon_search_light, R.drawable.icon_upgrade_light,
                        R.drawable.icon_manager_light, R.drawable.icon_setting_light});
                break;
            default:
                break;
        }
    }
}
*/
