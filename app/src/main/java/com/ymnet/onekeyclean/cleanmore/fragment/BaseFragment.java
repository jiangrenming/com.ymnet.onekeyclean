package com.ymnet.onekeyclean.cleanmore.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.umeng.analytics.MobclickAgent;
import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.customview.CircularProgress;
import com.ymnet.onekeyclean.cleanmore.utils.C;
import com.ymnet.onekeyclean.cleanmore.wechat.component.HasComponent;


//import com.squareup.leakcanary.RefWatcher;

/**
 * @author janan
 * @version since
 * @date 2014-8-21 上午10:30:03
 * @description 根fragment，程序中所有fragment的父类
 */
public abstract class BaseFragment extends Fragment {
    protected           String pb_tag         = getClass().getSimpleName();
    public final static String TYPE_RECOMMEND = "type_recommend";

    public final static String TYPE_SOFT = "type_soft";

    public final static String TYPE_GAME = "type_game";

    /**
     * 设置fragment的tag
     */
    public abstract void setSupportTag(String tag);

    /**
     * 获取fragment的tag
     */
    public abstract String getSupportTag();

    /**
     * fragment显示
     */
    public abstract void showSelf();


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {
            outState.putString("supportTag", getSupportTag());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            String supportTag = savedInstanceState.getString("supportTag");
            if (!TextUtils.isEmpty(supportTag)) {
                setSupportTag(supportTag);
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //        RefWatcher refWatcher = MarketApplication.getRefWatcher(getActivity());
        //        refWatcher.watch(this);
    }

    public boolean isActivityNull() {
        return getActivity() == null;
    }

    public interface FragmentCallBack {
        void hideOrShowTitleBar(int visibility);
    }


    protected View fl_loading;
    protected View pb_loading;
    protected View ll_loaded_fail;
    protected View btn_retry;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        if (view != null) {
            fl_loading = view.findViewById(R.id.fl_loading);
            pb_loading = view.findViewById(R.id.pb_loading);
            if (pb_loading instanceof CircularProgress) {
                ((CircularProgress) pb_loading).setName(pb_tag);
            }
            ll_loaded_fail = view.findViewById(R.id.ll_loaded_fail);
            btn_retry = view.findViewById(R.id.btn_retry);

        }
    }

    public void showLoading() {
        try {
            checkView();
        } catch (Exception e) {
            MobclickAgent.reportError(C.get(), "com.ymnet.onekeyclean.cleanmore.fragment.BaseFragment:" + e.toString());
            Log.e(pb_tag, "checkView is exception");
            return;
        }
        fl_loading.setVisibility(View.VISIBLE);
        pb_loading.setVisibility(View.VISIBLE);
        ll_loaded_fail.setVisibility(View.GONE);
        btn_retry.setVisibility(View.GONE);
    }

    protected void showLoadFail() {
        try {
            checkView();
        } catch (Exception e) {
            Log.e(pb_tag, "checkView is exception");
            MobclickAgent.reportError(C.get(), "com.ymnet.onekeyclean.cleanmore.fragment.BaseFragment:" + e.toString());
            return;
        }
        fl_loading.setVisibility(View.VISIBLE);
        pb_loading.setVisibility(View.GONE);
        ll_loaded_fail.setVisibility(View.VISIBLE);
        btn_retry.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        try {
            checkView();
        } catch (Exception e) {
            Log.e(pb_tag, "checkView is exception");
            MobclickAgent.reportError(C.get(), "com.ymnet.onekeyclean.cleanmore.fragment.BaseFragment:" + e.toString());
            return;
        }
        fl_loading.setVisibility(View.GONE);
        pb_loading.setVisibility(View.GONE);
        ll_loaded_fail.setVisibility(View.GONE);
        btn_retry.setVisibility(View.GONE);
    }

    private void checkView() throws Exception {
        if (fl_loading == null || pb_loading == null || ll_loaded_fail == null || btn_retry == null) {
            throw new IllegalArgumentException("loading view has null");
        }
    }

    protected void setRetryListener(View.OnClickListener li) {
        try {
            checkView();
        } catch (Exception e) {
            e.printStackTrace();
            MobclickAgent.reportError(C.get(), "com.ymnet.onekeyclean.cleanmore.fragment.BaseFragment:" + e.toString());
            return;
        }
        btn_retry.setOnClickListener(li);
    }

    /**
     * Gets a component for dependency injection by its type.
     */
    @SuppressWarnings("unchecked")
    protected <C> C getComponent(Class<C> componentType) {
        return componentType.cast(((HasComponent<C>) getActivity()).getComponent());
    }
}
