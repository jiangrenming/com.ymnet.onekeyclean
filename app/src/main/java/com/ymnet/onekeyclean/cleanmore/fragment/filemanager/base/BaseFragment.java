package com.ymnet.onekeyclean.cleanmore.fragment.filemanager.base;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.ymnet.onekeyclean.R;

/**
 * Created by Administrator on 2017/4/1.
 */

public abstract class BaseFragment extends Fragment implements View.OnClickListener {

    public void forwardSendPage(final BaseFragment baseFragment) {
        if (this.getActivity() instanceof FileManagerListener) {
            ((FileManagerListener) this.getActivity()).forwardSendPage(baseFragment);
        }
    }

    public void onBack() {
        if (this.getActivity() instanceof FileManagerListener) {
            ((FileManagerListener) this.getActivity()).onBack();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getView().findViewById(R.id.iv_top_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_top_back:
                onBack();
                break;
        }
    }
}
