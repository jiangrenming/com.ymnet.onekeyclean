package com.ymnet.onekeyclean.cleanmore.uninstall.fragment;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.fragment.BaseFragment;
import com.ymnet.onekeyclean.cleanmore.junk.ScanHelp;
import com.ymnet.onekeyclean.cleanmore.uninstall.model.AppInfo;
import com.ymnet.onekeyclean.cleanmore.uninstall.model.IgnoreInfo;
import com.ymnet.onekeyclean.cleanmore.uninstall.model.UninstallCallback;
import com.ymnet.onekeyclean.cleanmore.utils.C;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MajinBuu on 2017/6/21 0021.
 *
 * @overView 扫描已安装应用-加载界面
 */

public class EmptyFragment extends BaseFragment {
    private List<String> mIgnoreList = new ArrayList<>();
    private Handler      mHandler    = new Handler();
    private PackageManager    mPackageManager;
    private List<PackageInfo> installedPackages;
    private List<AppInfo> mAppInfoList = new ArrayList<>();
    private List<String>  mPkgNameList = new ArrayList<>();
    private boolean           isExecutedUninstall;
    private View              mProgressBar;
    private UninstallCallback mUninstallCallback;

    public static EmptyFragment newInstance() {
        EmptyFragment fragment = new EmptyFragment();
        return fragment;
    }

    public void getMessage(UninstallCallback uninstallCallback) {
        this.mUninstallCallback = uninstallCallback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIgnoreList = IgnoreInfo.newInstance().getList();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.fragment_empty, null);
        mProgressBar = view.findViewById(R.id.pb_uninstall_loading);
        initView();
        return view;
    }

    private void initView() {
        //扫描手机安装的应用
        scanInstalledAPP();
    }

    private void scanInstalledAPP() {
        mPackageManager = C.get().getPackageManager();
        if (installedPackages != null) {
            installedPackages.clear();
        }
        installedPackages = mPackageManager.getInstalledPackages(0);
        if (isExecutedUninstall) {//已经执行了卸载操作
            mAppInfoList.clear();
            mPkgNameList.clear();
        }

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
            }

            @Override
            protected Void doInBackground(Void... params) {
                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);

                for (int i = 0; i < installedPackages.size(); i++) {

                    if ((installedPackages.get(i).applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {//非系统应用
                        // 获取该应用安装包的Intent，用于启动该应用
                        // info.appIntent = pm.getLaunchIntentForPackage(installedPackages.get(i).packageName);
                        AppInfo info = new AppInfo();
                        info.appName = installedPackages.get(i).applicationInfo.loadLabel(mPackageManager).toString();
                        info.versionName = installedPackages.get(i).versionName;
                        info.pkgName = installedPackages.get(i).packageName;
                        info.appIcon = drawableToBitmap(installedPackages.get(i).applicationInfo.loadIcon(mPackageManager));
                        if (!mIgnoreList.contains(installedPackages.get(i).packageName)) {
                            mAppInfoList.add(info);
                        }
                    } else {//系统应用

                    }
                }

                while (ScanHelp.getInstance(C.get()).isRun()) {
                }//勿删!

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mUninstallCallback.getMessage(mAppInfoList);

                        FragmentManager fm = getFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        UninstallFragment uf = UninstallFragment.newInstance();

                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList("appInfoList", (ArrayList<? extends Parcelable>) mAppInfoList);
                        uf.setArguments(bundle);
                        ft.replace(R.id.fl_uninstall, uf);
                        //                        ft.addToBackStack(null);
                        ft.commit();
                    }
                }, 500);
            }

        };

        task.execute(new Void[]{});
    }

    public Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
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
    }
}
