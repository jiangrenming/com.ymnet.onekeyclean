package com.ymnet.onekeyclean.cleanmore.uninstall.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.commonlibrary.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;
import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.customview.RecyclerViewPlus;
import com.ymnet.onekeyclean.cleanmore.fragment.BaseFragment;
import com.ymnet.onekeyclean.cleanmore.fragment.filemanager.adapter.FileItemAdapter;
import com.ymnet.onekeyclean.cleanmore.uninstall.activity.UninstallActivity;
import com.ymnet.onekeyclean.cleanmore.uninstall.adapter.InstalledAppAdapter;
import com.ymnet.onekeyclean.cleanmore.uninstall.model.AppInfo;
import com.ymnet.onekeyclean.cleanmore.uninstall.model.IgnoreInfo;
import com.ymnet.onekeyclean.cleanmore.uninstall.model.UninstallClickListener;
import com.ymnet.onekeyclean.cleanmore.utils.C;
import com.ymnet.onekeyclean.cleanmore.widget.LinearLayoutItemDecoration;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by MajinBuu on 2017/6/21 0021.
 *
 * @overView 已安装应用卸载界面
 */

public class UninstallFragment extends BaseFragment implements View.OnClickListener {

    private static List<AppInfo> mAppInfoList  = new ArrayList<>();
    private static List<AppInfo> mAppInfoList2 = new ArrayList<>();
    private        Map<Integer, AppInfo> mSelectedApp;
    private static InstalledAppAdapter   mAdapter;
    private        boolean               isExecutedUninstall;
    private        RecyclerViewPlus      mRecyclerView;
    private        View                  mView;
    private        AppUninstallReceiver  mReceiver;
    private        List<String>          mIgnoreList;
    private        PackageManager        mPackageManager;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }
    };
    private TextView mOneKeyUninstalled;

    public static UninstallFragment newInstance() {
        UninstallFragment fragment = new UninstallFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mReceiver = new AppUninstallReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addDataScheme("package");
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        getActivity().registerReceiver(mReceiver, filter);

    }

    private void addCache() {
        mIgnoreList = IgnoreInfo.newInstance().getList();
        mPackageManager = C.get().getPackageManager();
        for (int i = 0; i < mAppInfoList.size(); i++) {
            getPkgSize(mAppInfoList.get(i), i);
        }

    }

    private void getPkgSize(final AppInfo packageInfo, final int position) {
        Method method = null;
        try {
            method = mPackageManager.getClass().getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
            method.invoke(mPackageManager, packageInfo.pkgName, new IPackageStatsObserver.Stub() {

                @Override
                public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {

                    if (succeeded) {
                        long cacheSize1 = pStats.cacheSize;
                        long cacheSize2 = 0;
                        long cacheSize3 = pStats.codeSize;
                        if (Build.VERSION.SDK_INT >= 11) {
                            cacheSize2 = pStats.externalCacheSize;
                        }
                        long cacheSize = cacheSize1 + cacheSize2 + cacheSize3;

                        //忽略名单应用不添加进集合
                        if (!mIgnoreList.contains(packageInfo.pkgName)) {
                            packageInfo.size = cacheSize;
                            mAppInfoList2.add(packageInfo);
                        }
                        //                        mAdapter.notifyItemRangeChanged(position, 1);
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            MobclickAgent.reportError(C.get(), e.fillInStackTrace());
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        List<AppInfo> appInfo = ((UninstallActivity) getActivity()).getAppInfo();
        mAppInfoList = appInfo;
        addCache();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bottom_delete:
                if (mSelectedApp.size() == 0) {
                    ToastUtil.showShort(C.get(), "请选中需要卸载的软件");
                } else {
                    multiUninstall(mSelectedApp);
                }
                break;
        }
    }

    private static Map<String, Integer> mMIList = new HashMap<>();

    private void multiUninstall(Map<Integer, AppInfo> mSelectedApp) {
        //卸载应用
        Iterator<Map.Entry<Integer, AppInfo>> iterator = mSelectedApp.entrySet().iterator();
        Map.Entry<Integer, AppInfo> next;
        while (iterator.hasNext()) {
            next = iterator.next();
            if (next != null) {
                Integer position = next.getKey();
                String pkgName = next.getValue().pkgName;

                uninstallApp(pkgName);
                Log.d("UninstallFragment", "存入的:" + pkgName);
                mMIList.put(pkgName, position);
            }
        }
        mSelectedApp.clear();
        mAdapter.notifyDataSetChanged();
        changeSelectedState();
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);

    }

    public static class AppUninstallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
                String packageName = intent.getData().getSchemeSpecificPart();
                if (mMIList.containsKey(packageName)) {
                    Log.d("AppUninstallReceiver", "packageName:" + packageName + "/mMIList.get(packageName):" + mMIList.get(packageName));
                    int index = mMIList.get(packageName).intValue();
                    mAppInfoList.remove(index);

                    Log.d("AppUninstallReceiver", mAppInfoList.toString());
                    mAdapter.notifyDataSetChanged();
                }

            }

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_uninstall, container, false);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initView(mView);
                initData();
                initListener();
            }
        }, 300);

        return mView;
    }

    private void initListener() {
        mOneKeyUninstalled.setOnClickListener(this);
        mAdapter.setRecyclerListListener(new UninstallClickListener() {
            @Override
            public void onClick(AppInfo appInfo, int position) {
                asyncUninstall(position);
                /*//弹dialog 确认是否删除
                showConfirmDeleteDialog(appInfo.appName, position);*/
            }

        });
        mAdapter.setOnCheckChangedListener(new FileItemAdapter.OnCheckChangedListener() {
            @Override
            public void checkChanged() {
                changeSelectedState();
            }
        });
    }

    private void changeSelectedState() {
        //        mOneKeyUninstalled.setEnabled(mSelectedApp.size() != 0);
        mOneKeyUninstalled.setText(String.format(getResources().getString(R.string.uninstall_withdata), mSelectedApp.size() + "款"));
    }

    private void initView(View view) {
        mRecyclerView = (RecyclerViewPlus) view.findViewById(R.id.rv_installed_app);
        mOneKeyUninstalled = (TextView) view.findViewById(R.id.btn_bottom_delete);
        //        mOneKeyUninstalled.setEnabled(false);
        //        mProgressBar = view.findViewById(R.id.pb_uninstall_loading);
        mSelectedApp = new HashMap<>();
        mAdapter = new InstalledAppAdapter(mAppInfoList, mSelectedApp);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(C.get()));
        mRecyclerView.addItemDecoration(new LinearLayoutItemDecoration(C.get(), LinearLayoutItemDecoration.HORIZONTAL_LIST));
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.notifyDataSetChanged();

    }

    private void initData() {
        mOneKeyUninstalled.setText(R.string.uninstall_nodata);
    }

    public int mPosition;

    private void asyncUninstall(final int position) {
        mMIList.put(mAppInfoList.get(position).pkgName, position);
        //卸载应用
        uninstallApp(mAppInfoList.get(position).pkgName);
        //        mPosition = position;
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
        mAdapter=null;
        getActivity().unregisterReceiver(mReceiver);
        getActivity().finish();
    }


}
