package com.ymnet.onekeyclean.cleanmore.junk.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.utils.ApplicationUtils;
import com.ymnet.onekeyclean.cleanmore.filebrowser.FileBrowserUtil;
import com.ymnet.onekeyclean.cleanmore.filebrowser.bean.FileInfo;

import java.util.ArrayList;
import java.util.Map;


public class MusicAdapter extends BaseAdapter implements OnClickListener {

	private Context mContext;

	private ArrayList<FileInfo> mInfos;

	private Map<Integer, FileInfo> mDeleteMap;

	private boolean isEditMode;

	private ViewHolder mHolder;

	private OnCheckChangedListener onCheckChangedListener;

	private AudioListener mAudioListener;

	public MusicAdapter(Context context, ArrayList<FileInfo> infos, Map<Integer, FileInfo> deleteMap) {
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
			convertView = View.inflate(mContext, R.layout.file_management_music_item_layout, null);
			mHolder = new ViewHolder();
			mHolder.tvSetRing = (TextView) convertView.findViewById(R.id.tv_set_ring);
			mHolder.cbChecked = (CheckBox) convertView.findViewById(R.id.cb_checked);
			mHolder.tvMusicName = (TextView) convertView.findViewById(R.id.tv_music_name);
			mHolder.tvMucisDuration = (TextView) convertView.findViewById(R.id.tv_music_duration);
			mHolder.tvMucisSize = (TextView) convertView.findViewById(R.id.tv_music_size);

			convertView.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) convertView.getTag();
		}

		if (isEditMode) {
			mHolder.cbChecked.setVisibility(View.VISIBLE);
			mHolder.cbChecked.setChecked(mDeleteMap.containsKey(position));
			mHolder.cbChecked.setOnClickListener(this);
			mHolder.cbChecked.setTag(position);

			mHolder.tvSetRing.setVisibility(View.GONE);


			SpannableStringBuilder duration = changeDurationStyle(-1, mInfos.get(position).duration);
			mHolder.tvMucisDuration.setText(duration);
		} else {
			mHolder.cbChecked.setVisibility(View.GONE);

			mHolder.tvSetRing.setVisibility(View.VISIBLE);
			mHolder.tvSetRing.setOnClickListener(this);
			mHolder.tvSetRing.setTag(position);


			SpannableStringBuilder duration = changeDurationStyle(-1, mInfos.get(position).duration);
			mHolder.tvMucisDuration.setText(duration);
		}

		mHolder.tvMusicName.setText(mInfos.get(position).fileName);
		mHolder.tvMucisSize.setText(ApplicationUtils.formatFileSizeToString(mInfos.get(position).fileSize));

		return convertView;
	}

	private class ViewHolder {
		TextView tvSetRing;

		CheckBox cbChecked;

		// CheckBox cbMusicPlayStop;

		TextView tvMusicName;

		TextView tvMucisDuration;

		TextView tvMucisSize;
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
		if (i == R.id.tv_set_ring) {
			if (!isEditMode) {
				if (mAudioListener != null) {
					int position = (Integer) v.getTag();
					mAudioListener.setAudio(mInfos.get(position).fileId);
				}
			}

		} else if (i == R.id.cb_checked) {
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

	private SpannableStringBuilder changeDurationStyle(int currentDuration, int duration) {
		if (currentDuration > 0) {
			String current = FileBrowserUtil.formatDuration(currentDuration);
			String total = FileBrowserUtil.formatDuration(duration);
			SpannableStringBuilder builder = new SpannableStringBuilder(current + "/" + total);
			builder.setSpan(new ForegroundColorSpan(Color.RED), 0, current.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			return builder;
		} else {
			// notifyDataSetChanged();
			return new SpannableStringBuilder(FileBrowserUtil.formatDuration(duration));
		}
	}

	public void setOnCheckChangedListener(OnCheckChangedListener onCheckChangedListener) {
		this.onCheckChangedListener = onCheckChangedListener;
	}

	public void setAudioListener(AudioListener audioListener) {
		this.mAudioListener = audioListener;
	}

	public interface OnCheckChangedListener {
		void checkChanged();
	}

	public interface AudioListener {
		void setAudio(int id);
	}

}
