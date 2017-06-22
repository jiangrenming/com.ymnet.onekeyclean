package com.ymnet.onekeyclean.cleanmore.uninstall.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.customview.RecyclerViewPlus;
import com.ymnet.onekeyclean.cleanmore.fragment.BaseFragment;
import com.ymnet.onekeyclean.cleanmore.temp.AsyncTaskwdh;
import com.ymnet.onekeyclean.cleanmore.uninstall.adapter.InstalledAppAdapter;
import com.ymnet.onekeyclean.cleanmore.uninstall.model.AppInfo;
import com.ymnet.onekeyclean.cleanmore.uninstall.model.UninstallClickListener;
import com.ymnet.onekeyclean.cleanmore.utils.C;
import com.ymnet.onekeyclean.cleanmore.wechat.DialogFactory;
import com.ymnet.onekeyclean.cleanmore.widget.LinearLayoutItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MajinBuu on 2017/6/21 0021.
 *
 * @overView 已安装应用卸载界面
 */

public class UninstallFragment extends BaseFragment {

    private static List<AppInfo> mAppInfoList = new ArrayList<>();
    private static InstalledAppAdapter    mAdapter;
    private        boolean                isExecutedUninstall;
    private        RecyclerViewPlus       mRecyclerView;
    private        DialogFactory.MyDialog mDlgUninstallWaiting;
    private        View                   mView;
    private        AppUninstallReceiver   mReceiver;

    public static UninstallFragment newInstance() {
        UninstallFragment fragment = new UninstallFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<AppInfo> appInfoList = getArguments().getParcelableArrayList("appInfoList");
        if (appInfoList == null) {
            throw new NullPointerException("扫描数据为null");
        }
        Log.d("UninstallFragment", "传来的值appInfoList:" + appInfoList);
        mAppInfoList.clear();
        mAppInfoList.addAll(appInfoList);

        mReceiver = new AppUninstallReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addDataScheme("package");
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        getActivity().registerReceiver(mReceiver, filter);

    }

    public static class AppUninstallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
                Log.d("AppUninstallReceiver", "收到卸载成功的广播了");
                mAppInfoList.remove(mPosition);
                mAdapter.notifyDataSetChanged();
            }

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_uninstall, container, false);
        initView(mView);
        return mView;
    }

    private void initView(View view) {
        mRecyclerView = (RecyclerViewPlus) view.findViewById(R.id.rv_installed_app);
        //        mProgressBar = view.findViewById(R.id.pb_uninstall_loading);
        mAdapter = new InstalledAppAdapter(mAppInfoList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(C.get()));
        mRecyclerView.addItemDecoration(new LinearLayoutItemDecoration(C.get(), LinearLayoutItemDecoration.HORIZONTAL_LIST));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setRecyclerListListener(new UninstallClickListener() {
            @Override
            public void onClick(AppInfo appInfo, int position) {
                asyncUninstall(position);
                /*//弹dialog 确认是否删除
                showConfirmDeleteDialog(appInfo.appName, position);*/

            }

        });
        mAdapter.notifyDataSetChanged();
    }

    public static int mPosition;

    private void asyncUninstall(final int position) {
        AsyncTaskwdh<Void, Void, Void> task = new AsyncTaskwdh<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                if (mDlgUninstallWaiting == null) {
                    mDlgUninstallWaiting = DialogFactory.createDialog(getActivity(), R.layout.common_loading_dialog);
                    mDlgUninstallWaiting.setCancelable(false);
                    mDlgUninstallWaiting.setCanceledOnTouchOutside(false);
                }
                mDlgUninstallWaiting.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                //卸载应用
                uninstallApp(mAppInfoList.get(position).pkgName);
                mPosition = position;

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (getActivity().isFinishing()) {
                    return;
                }
                if (mDlgUninstallWaiting != null && mDlgUninstallWaiting.isShowing()) {
                    mDlgUninstallWaiting.dismiss();
                }

            }

        };

        task.execute(new Void[]{});
    }

    private void uninstallApp(String pkgName) {
        isExecutedUninstall = true;

        Intent uninstallIntent = new Intent();
        uninstallIntent.setAction(Intent.ACTION_DELETE);
        uninstallIntent.setData(Uri.parse("package:" + pkgName));
        startActivity(uninstallIntent);
    }

    @Override
    public void setSupportTag(String tag) {

    }

    @Override
    public String getSupportTag() {
        return null;
    }

    @Override
    public void showSelf() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mReceiver);
        getActivity().finish();
    }
}
