package com.ymnet.onekeyclean.cleanmore.filebrowser.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.utils.ApplicationUtils;
import com.ymnet.onekeyclean.cleanmore.utils.DateFormatUtils;
import com.ymnet.onekeyclean.cleanmore.filebrowser.FileBrowserUtil;
import com.ymnet.onekeyclean.cleanmore.filebrowser.bean.FileInfo;

import java.util.ArrayList;
import java.util.Map;

public class ApkAdapter extends BaseAdapter implements OnClickListener {

	private Context mContext;

	private ArrayList<FileInfo> mInfos;

	private Map<Integer, FileInfo> mDeleteMap;

	private boolean isEditMode;

	private ViewHolder mHolder;

	private OnCheckChangedListener onCheckChangedListener;

	public ApkAdapter(Context context, ArrayList<FileInfo> infos, Map<Integer, FileInfo> deleteMap) {
		this.mContext = context;
		this.mInfos = infos;
		this.mDeleteMap = deleteMap;
		this.isEditMode = false;
	}

	@Override
	public int getCount() {
		return mInfos == null ? 0 : mInfos.size();
	}

	@Override
	public FileInfo getItem(int position) {
		return mInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.file_management_apk_item_layout, null);
			mHolder = new ViewHolder();
			mHolder.tvInstall = (TextView) convertView.findViewById(R.id.tv_install);
			mHolder.cbChecked = (CheckBox) convertView.findViewById(R.id.cb_checked);
			mHolder.ivPic = (ImageView) convertView.findViewById(R.id.iv_pic);
			mHolder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
			mHolder.tvSize = (TextView) convertView.findViewById(R.id.tv_size);
			mHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);

			convertView.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) convertView.getTag();
		}

		if (isEditMode) {
			mHolder.cbChecked.setVisibility(View.VISIBLE);
			mHolder.cbChecked.setChecked(mDeleteMap.containsKey(position));
			mHolder.cbChecked.setOnClickListener(this);
			mHolder.cbChecked.setTag(position);

			mHolder.tvInstall.setVisibility(View.GONE);
		} else {
			mHolder.cbChecked.setVisibility(View.GONE);

			mHolder.tvInstall.setVisibility(View.VISIBLE);
			mHolder.tvInstall.setOnClickListener(this);
			mHolder.tvInstall.setTag(position);
		}

		mHolder.tvSize.setText(ApplicationUtils.formatFileSizeToString(mInfos.get(position).fileSize));
		mHolder.tvTime.setText(DateFormatUtils.format(mInfos.get(position).ModifiedDate, DateFormatUtils.PATTERN_YMDHM2));

		if (TextUtils.isEmpty(mInfos.get(position).appName)) {
			FileBrowserUtil.getApkInfo(mContext, mInfos.get(position));
		}
		mHolder.tvName.setText(mInfos.get(position).appName);

//        ImageLoader.getInstance().displayImage(MarketImageDownloader.UNINSTALLED_APP_SCHEME + mInfos.get(position).filePath, mHolder.ivPic);
		mHolder.ivPic.setImageResource(R.drawable.app_icon_bg);
		return convertView;
	}

	private class ViewHolder {
		TextView tvInstall;

		CheckBox cbChecked;

		ImageView ivPic;

		TextView tvName;

		TextView tvSize;

		TextView tvTime;
	}

	public void setEditMode(boolean editMode) {
		this.isEditMode = editMode;

		notifyDataSetChanged();
	}

	public boolean getEditMode() {
		return isEditMode;
	}

	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.cb_checked) {
			if (isEditMode) {
				CheckBox cb = (CheckBox) v;
				int position = (Integer) cb.getTag();
				if (cb.isChecked()) {
					mDeleteMap.put(position, mInfos.get(position));
				} else {
					mDeleteMap.remove(position);
				}

				if (onCheckChangedListener != null) {
					onCheckChangedListener.checkChanged();
				}
			}

		} else {
		}
	}

	public void setOnCheckChangedListener(OnCheckChangedListener onCheckChangedListener) {
		this.onCheckChangedListener = onCheckChangedListener;
	}

	public interface OnCheckChangedListener {
		void checkChanged();
	}

}
