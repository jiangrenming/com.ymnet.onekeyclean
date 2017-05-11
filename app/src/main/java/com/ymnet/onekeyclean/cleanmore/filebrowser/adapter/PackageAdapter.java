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
import com.ymnet.onekeyclean.cleanmore.common.ApplicationUtils;
import com.ymnet.onekeyclean.cleanmore.common.DateFormatUtils;
import com.ymnet.onekeyclean.cleanmore.filebrowser.bean.FileInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;



public class PackageAdapter extends BaseAdapter implements OnClickListener {

	private Context mContext;

	private ArrayList<FileInfo> mInfos;

	private Map<Integer, FileInfo> mDeleteMap;

	private boolean isEditMode;

	private ViewHolder mHolder;

	private OnCheckChangedListener onCheckChangedListener;

	private HashMap<String, Integer> mTypeMap;

	public PackageAdapter(Context context, ArrayList<FileInfo> infos, Map<Integer, FileInfo> deleteMap) {
		this.mContext = context;
		this.mInfos = infos;
		this.mDeleteMap = deleteMap;
		this.isEditMode = false;

		mTypeMap = new HashMap<String, Integer>();
		mTypeMap.put("7z", R.drawable.file_management_compress_type_7zip);
		mTypeMap.put("zip", R.drawable.file_management_compress_type_zip);
		mTypeMap.put("rar", R.drawable.file_management_compress_type_rar);
		mTypeMap.put("iso", R.drawable.file_management_compress_type_iso);
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
			convertView = View.inflate(mContext, R.layout.file_management_document_item_layout, null);
			mHolder = new ViewHolder();
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
		} else {
			mHolder.cbChecked.setVisibility(View.GONE);
		}

		mHolder.tvName.setText(mInfos.get(position).fileName);
		mHolder.tvSize.setText(ApplicationUtils.formatFileSizeToString(mInfos.get(position).fileSize));
		mHolder.tvTime
				.setText(DateFormatUtils.format(mInfos.get(position).ModifiedDate, DateFormatUtils.PATTERN_YMDHM2));

		setPic(mHolder.ivPic, mInfos.get(position).fileName);

		return convertView;
	}

	private class ViewHolder {
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

	private void setPic(ImageView ivPic, String name) {
		try {
			String suffix = name.substring(name.lastIndexOf(".") + 1);
			Integer resID = mTypeMap.get(suffix.toLowerCase(Locale.CHINA));
			if (resID != null) {
				ivPic.setImageResource(resID);
			} else {
				ivPic.setImageResource(R.drawable.file_management_compress_type_rar);
			}
		} catch (Exception e) {
			ivPic.setImageResource(R.drawable.file_management_compress_type_rar);
		}
	}

	public void clear() {
		if (mTypeMap != null) {
			mTypeMap.clear();
			mTypeMap = null;
		}
	}

	public void setOnCheckChangedListener(OnCheckChangedListener onCheckChangedListener) {
		this.onCheckChangedListener = onCheckChangedListener;
	}

	public interface OnCheckChangedListener {
		void checkChanged();
	}

}
