package com.ymnet.onekeyclean.cleanmore;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.home.HomeActivity;
import com.ymnet.onekeyclean.cleanmore.utils.C;
import com.ymnet.onekeyclean.cleanmore.wechat.activity.SystemBarTintManager;
import com.ymnet.onekeyclean.cleanmore.wechat.view.BaseFragmentActivity;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by wangdh on 2016-05-25.
 * gmail:wangduheng26@gamil.com
 * class info: 沉浸式状态栏基类
 */
public class ImmersiveActivity extends BaseFragmentActivity {
    protected boolean immersiveMode = true;
    private boolean mOpen;
    public boolean isClearAll = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindow();
    }

    @SuppressLint("NewApi")
    @Override
    public void setContentView(int id) {
        if (isAfterKitkat()) {
            View view = getLayoutInflater().inflate(id, null);
            view.setFitsSystemWindows(true);
            setContentView(view);
        } else {
            super.setContentView(id);
        }

    }

    @SuppressLint("NewApi")
    @Override
    public void setContentView(View view) {
        if (isAfterKitkat()) {
            view.setFitsSystemWindows(true);
        }
        super.setContentView(view);
    }

    @SuppressLint("NewApi")
    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        if (isAfterKitkat()) {
            view.setFitsSystemWindows(true);
        }
        super.setContentView(view, params);
    }

    /**
     * @return 是否是4.4.4以后的版本
     */
    private boolean isAfterKitkat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && immersiveMode;
    }

    private void initWindow() {
        if (isAfterKitkat()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintColor(getResources().getColor(R.color.main_blue_new));
            tintManager.setStatusBarTintEnabled(true);
        }
    }

    protected SystemBarTintManager tintManager;

    protected SystemBarTintManager getTintManager() {
        return tintManager;
    }
//    private List<Activity> mList = new LinkedList<>();
    private static List<Activity> mList = new ArrayList<>();
    public void addActivity(Activity activity) {
        mList.add(activity);
    }

    public void exit() {
        Log.d("ImmersiveActivity", mList.toString());
        try {
            for (Activity activity : mList) {
                if (activity != null) {
                    activity.finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } /*finally {
            System.exit(0);
        }*/
    }

    public void openHome(boolean open) {
        this.mOpen = open;
        startActivity(new Intent(C.get(), HomeActivity.class));
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        if (isClearAll) {
            mOpen = false;
        }

    }
}
