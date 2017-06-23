package com.ymnet.onekeyclean.cleanmore.fragment.filemanager;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.fragment.filemanager.base.BaseFragment;
import com.ymnet.onekeyclean.cleanmore.fragment.filemanager.base.FileManagerListener;

/**
 * Created by Administrator on 2017/4/1.
 */

public class FileManagerActivity extends FragmentActivity implements FileManagerListener {
    private FragmentManager mFM;

    @Override
    public void forwardSendPage(BaseFragment baseFragment) {
        final FragmentTransaction beginTransaction = this.mFM.beginTransaction();
        beginTransaction.setCustomAnimations(R.anim.translate_activity_in_anti, R.anim.translate_activity_out_anti);
        beginTransaction.replace(R.id.file_manager_container, baseFragment);
        beginTransaction.addToBackStack(null);
        beginTransaction.commitAllowingStateLoss();
    }

    @Override
    public void onBack() {
        if (mFM.getBackStackEntryCount() != 1) {
            onBackPressed();
        } else {
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.file_manager_activity);
        this.mFM = this.getSupportFragmentManager();
        startPage();
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

}
