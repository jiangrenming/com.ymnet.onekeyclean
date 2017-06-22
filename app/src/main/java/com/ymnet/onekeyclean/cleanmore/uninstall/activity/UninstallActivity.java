package com.ymnet.onekeyclean.cleanmore.uninstall.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.ImmersiveActivity;
import com.ymnet.onekeyclean.cleanmore.uninstall.fragment.EmptyFragment;
import com.ymnet.onekeyclean.cleanmore.uninstall.fragment.UninstallFragment;

/**
 * Created by MajinBuu on 2017/6/21 0021.
 *
 * @overView 卸载主界面
 */
public class UninstallActivity extends ImmersiveActivity {

    private static final String uninstallFragmentTag = "uninstall";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uninstall);
        initToolbar();
        initFragment();
    }

    private void initToolbar() {
        TextView title = (TextView) findViewById(R.id.junk_title_txt);
        title.setText("应用卸载");
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
    }

    private UninstallFragment getScanningFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(uninstallFragmentTag);
        if (fragment != null) {
            return (UninstallFragment) fragment;
        }
        return null;
    }
}
