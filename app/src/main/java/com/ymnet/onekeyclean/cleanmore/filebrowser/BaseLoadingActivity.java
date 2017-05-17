package com.ymnet.onekeyclean.cleanmore.filebrowser;

import android.view.View;

import com.umeng.analytics.MobclickAgent;
import com.ymnet.onekeyclean.cleanmore.ImmersiveActivity;
import com.ymnet.onekeyclean.cleanmore.utils.C;


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
            e.printStackTrace();
            MobclickAgent.reportError(C.get(), "com.ymnet.onekeyclean.cleanmore.filebrowser.BaseLoadingActivity:" + e.toString());
            return;
        }
        fl_loading.setVisibility(View.VISIBLE);
        pb_loading.setVisibility(View.VISIBLE);
    }

    protected void hideLoading() {
        try {
            checkLoadingView();
        } catch (Exception e) {
            e.printStackTrace();
            MobclickAgent.reportError(C.get(), "com.ymnet.onekeyclean.cleanmore.filebrowser.BaseLoadingActivity:" + e.toString());
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
