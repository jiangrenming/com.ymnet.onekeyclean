package com.ymnet.onekeyclean.cleanmore.wechat.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.ymnet.onekeyclean.MarketApplication;
import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.notification.HomeTabActivity;
import com.ymnet.onekeyclean.cleanmore.wechat.Navigator;
import com.ymnet.onekeyclean.cleanmore.wechat.component.ApplicationComponent;
import com.ymnet.onekeyclean.cleanmore.wechat.modules.ActivityModule;

import javax.inject.Inject;

/**
 * Created by wangdh on 5/25/16.
 * gmail:wangduheng26@gamil.com
 */
public class BaseFragmentActivity extends FragmentActivity {
    protected boolean afterSaveInstance = false;
    protected boolean   moveAnimed;
    @Inject
    public    Navigator navigator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getApplicationComponent().inject(this);

        Intent intent = new Intent(this, HomeTabActivity.class);
        startActivity(intent);

    }

    @Override
    protected void onResume() {
//        StatisticSpec.onResume(this);
//        MobclickAgent.onResume(this);
        super.onResume();
    }

    @Override
    public void finish() {
        super.finish();
        if (!moveAnimed) {
            overridePendingTransition(R.anim.translate_activity_in_anti, R.anim.translate_activity_out_anti);
        } else {
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    @Override
    protected void onPause() {
//        StatisticSpec.onPause(this);
//        MobclickAgent.onPause(this);
        super.onPause();
    }

    @Override
    public void startActivity(Intent intent) {
        try {
            super.startActivity(intent);
        } catch (Exception e) {

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        afterSaveInstance = true;
    }

    @Override
    public void onBackPressed() {
        if (afterSaveInstance) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Get the Main Application component for dependency injection.
     */
    protected ApplicationComponent getApplicationComponent() {
        return ((MarketApplication) getApplication()).getApplicationComponent();
    }

    /**
     * Get an Activity module for dependency injection.
     */
    protected ActivityModule getActivityModule() {
        return new ActivityModule(this);
    }

}
