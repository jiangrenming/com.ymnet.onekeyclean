package com.ymnet.onekeyclean.cleanmore.fragment.filemanager;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

import com.umeng.analytics.MobclickAgent;
import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.ImmersiveActivity;
import com.ymnet.onekeyclean.cleanmore.fragment.filemanager.base.BaseFragment;
import com.ymnet.onekeyclean.cleanmore.fragment.filemanager.base.FileManagerListener;
import com.ymnet.onekeyclean.cleanmore.utils.OnekeyField;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/1.
 */

public class FileManagerActivity extends ImmersiveActivity implements FileManagerListener {
    private FragmentManager mFM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.file_manager_activity);
        this.mFM = this.getSupportFragmentManager();

        String stringExtra = getIntent().getStringExtra(OnekeyField.ONEKEYCLEAN);
        String statistics_key = getIntent().getStringExtra(OnekeyField.STATISTICS_KEY);
        if (stringExtra != null) {
            Map<String, String> m = new HashMap<>();
            m.put(OnekeyField.ONEKEYCLEAN, stringExtra);
            MobclickAgent.onEvent(this, statistics_key, m);
        }

        startPage();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void forwardSendPage(BaseFragment baseFragment) {
        final FragmentTransaction beginTransaction = this.mFM.beginTransaction();
        beginTransaction.setCustomAnimations(R.anim.translate_activity_in_anti, R.anim.translate_activity_out_anti);
        beginTransaction.replace(R.id.file_manager_container, baseFragment);
        beginTransaction.addToBackStack(null);
        beginTransaction.commitAllowingStateLoss();
    }

    private void startPage() {
        this.forwardSendPage(new FileManagerFragment());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBack() {
        if (mFM.getBackStackEntryCount() != 1) {
            onBackPressed();
        } else {
            finish();
        }
    }

}
