package com.ymnet.onekeyclean.cleanmore.uninstall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.HomeActivity;
import com.ymnet.onekeyclean.cleanmore.ImmersiveActivity;
import com.ymnet.onekeyclean.cleanmore.uninstall.fragment.EmptyFragment;
import com.ymnet.onekeyclean.cleanmore.uninstall.fragment.UninstallFragment;
import com.ymnet.onekeyclean.cleanmore.uninstall.model.AppInfo;
import com.ymnet.onekeyclean.cleanmore.uninstall.model.UninstallCallback;
import com.ymnet.onekeyclean.cleanmore.utils.OnekeyField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by MajinBuu on 2017/6/21 0021.
 *
 * @overView 卸载主界面
 */
public class UninstallActivity extends ImmersiveActivity {

    private static final String uninstallFragmentTag = "uninstall";
    private List<AppInfo> mAppInfo = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uninstall);

        String stringExtra = getIntent().getStringExtra(OnekeyField.ONEKEYCLEAN);
        String statistics_key = getIntent().getStringExtra(OnekeyField.STATISTICS_KEY);
        if (stringExtra != null) {
            Map<String, String> m = new HashMap<>();
            m.put(OnekeyField.ONEKEYCLEAN, stringExtra);
            MobclickAgent.onEvent(this, statistics_key, m);
        }

        initToolbar();
        initFragment();
    }

    private void initToolbar() {
        TextView title = (TextView) findViewById(R.id.junk_title_txt);
        title.setText("软件管理");
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initFragment() {
        EmptyFragment loadingF = EmptyFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_uninstall, loadingF, uninstallFragmentTag).commit();


        loadingF.getMessage(new UninstallCallback() {
            @Override
            public void getMessage(List<AppInfo> appInfo) {
                mAppInfo = appInfo;
            }
        });
    }

    public List<AppInfo> getAppInfo() {
        return mAppInfo;
    }

    private UninstallFragment getScanningFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(uninstallFragmentTag);
        if (fragment != null) {
            return (UninstallFragment) fragment;
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    @Override
    public void finish() {
        super.finish();
        startActivity(new Intent(this,HomeActivity.class));
    }
}
