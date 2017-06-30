package com.ymnet.onekeyclean.cleanmore.uninstall.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.customview.RecyclerViewPlus;
import com.ymnet.onekeyclean.cleanmore.uninstall.model.UninstallClickListener;
import com.ymnet.onekeyclean.cleanmore.uninstall.model.AppInfo;
import com.ymnet.onekeyclean.cleanmore.utils.FormatUtils;

import java.util.List;

/**
 * Created by MajinBuu on 2017/6/21 0021.
 *
 * @overView 卸载子界面
 */
public class InstalledAppAdapter extends RecyclerViewPlus.Adapter {
    private List<AppInfo>          mDataList;
    private UninstallClickListener mUninstallClickListener;

    public InstalledAppAdapter(List<AppInfo> appInfoList) {
       /* if (appInfoList.size() == 0) {
            throw new NullPointerException("请传入数据-InstalledAppAdapter");
        }*/
        this.mDataList = appInfoList;
    }

    public void setRecyclerListListener(UninstallClickListener recyclerViewClickListener) {
        this.mUninstallClickListener = recyclerViewClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.installed_item, null);
        return new InstalledAppHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof InstalledAppHolder) {
            final InstalledAppHolder mHolder = (InstalledAppHolder) holder;
            mHolder.mIcon.setImageBitmap(mDataList.get(position).appIcon);
            mHolder.mAppName.setText(mDataList.get(position).appName);
            long size = mDataList.get(position).size;
            String s = FormatUtils.formatFileSize(size);
            mHolder.mVersion.setText(s);
            mHolder.mUninstall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mUninstallClickListener.onClick(mDataList.get(position), position);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    private class InstalledAppHolder extends RecyclerView.ViewHolder {

        private final ImageView mIcon;
        private final TextView  mAppName;
        private final TextView  mVersion;
        private final ImageView mUninstall;

        public InstalledAppHolder(View view) {
            super(view);
            mIcon = (ImageView) view.findViewById(R.id.iv_icon);
            mAppName = (TextView) view.findViewById(R.id.tv_app_name);
            mVersion = (TextView) view.findViewById(R.id.tv_version);
            mUninstall = (ImageView) view.findViewById(R.id.iv_uninstall);
        }
    }
}
