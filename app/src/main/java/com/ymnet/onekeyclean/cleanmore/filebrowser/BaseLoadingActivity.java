package com.ymnet.onekeyclean.cleanmore.filebrowser;

import android.view.View;

import com.ymnet.onekeyclean.cleanmore.ImmersiveActivity;


/**
 * Created by wangduheng26 on 3/4/16.
 * android market2345 wangdh@2345.com
 */
public class BaseLoadingActivity extends ImmersiveActivity {
    protected View fl_loading, pb_loading;

    protected void showLoading() {
        try {
            checkLoadingView();
        } catch (Exception e) {
//            e.printStackTrace();
            return;
        }
        fl_loading.setVisibility(View.VISIBLE);
        pb_loading.setVisibility(View.VISIBLE);
    }

    protected void hideLoading() {
        try {
            checkLoadingView();
        } catch (Exception e) {
//            e.printStackTrace();
            return;
        }
        fl_loading.setVisibility(View.GONE);
        pb_loading.setVisibility(View.GONE);
    }

    private void checkLoadingView() throws Exception {
        if (fl_loading == null || pb_loading == null) {
            throw new IllegalArgumentException("loading view has null");
        }
    }
}
