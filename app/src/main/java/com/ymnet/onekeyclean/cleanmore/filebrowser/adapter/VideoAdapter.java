package com.ymnet.onekeyclean.cleanmore.filebrowser.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.utils.ApplicationUtils;
import com.ymnet.onekeyclean.cleanmore.filebrowser.FileBrowserUtil;
import com.ymnet.onekeyclean.cleanmore.filebrowser.bean.FileInfo;
import com.ymnet.onekeyclean.cleanmore.filebrowser.lazyload.ImageLoader;

import java.util.ArrayList;
import java.util.Map;


public class VideoAdapter extends BaseAdapter implements OnClickListener {

	private Context mContext;

	private ArrayList<FileInfo> mInfos;

	private Map<Integer, FileInfo> mDeleteMap;

	private boolean isEditMode;

	private ViewHolder mHolder;

	private OnCheckChangedListener onCheckChangedListener;

	public VideoAdapter(Context context, ArrayList<FileInfo> infos, Map<Integer, FileInfo> deleteMap) {
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
			convertView = View.inflate(mContext, R.layout.file_management_video_item_layout, null);
			mHolder = new ViewHolder();
			mHolder.videoPlay = convertView.findViewById(R.id.iv_video_play);
			mHolder.cbChecked = (CheckBox) convertView.findViewById(R.id.cb_checked);
			mHolder.ivPic = (ImageView) convertView.findViewById(R.id.iv_pic);
			mHolder.tvName = (TextView) convertView.findViewById(R.id.tv_video_name);
			mHolder.tvDuration = (TextView) convertView.findViewById(R.id.tv_video_duration);
			mHolder.tvSize = (TextView) convertView.findViewById(R.id.tv_video_size);

			convertView.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) convertView.getTag();
		}

		if (isEditMode) {
			mHolder.cbChecked.setVisibility(View.VISIBLE);
			mHolder.cbChecked.setChecked(mDeleteMap.containsKey(position));
			mHolder.cbChecked.setOnClickListener(this);
			mHolder.cbChecked.setTag(position);
		} else {
			mHolder.cbChecked.setVisibility(View.GONE);

			mHolder.videoPlay.setOnClickListener(this);
			mHolder.videoPlay.setTag(position);
		}

		ImageLoader.getInstance(mContext).DisplayImage(mInfos.get(position).fileId, mHolder.ivPic,
				R.drawable.screenshot, R.drawable.screenshot, -1, -1, ImageLoader.MEDIATYPE.VIDEO);
		mHolder.tvName.setText(mInfos.get(position).fileName);
		mHolder.tvSize.setText(ApplicationUtils.formatFileSizeToString(mInfos.get(position).fileSize));
		mHolder.tvDuration.setText(FileBrowserUtil.formatDuration(mInfos.get(position).duration));

		return convertView;
	}

	private class ViewHolder {
		View videoPlay;

		CheckBox cbChecked;

		ImageView ivPic;

		TextView tvName;

		TextView tvDuration;

		TextView tvSize;
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

		} else if (i == R.id.iv_video_play) {
			if (!isEditMode) {
				Integer position = (Integer) v.getTag();
				FileBrowserUtil.openFile(mContext, mInfos.get(position).filePath, mInfos.get(position).mimeType);
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
