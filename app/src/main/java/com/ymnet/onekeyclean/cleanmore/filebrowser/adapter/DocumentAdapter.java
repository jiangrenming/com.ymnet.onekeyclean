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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;



public class DocumentAdapter extends BaseAdapter implements OnClickListener {

	private Context mContext;

	private ArrayList<FileInfo> mInfos;

	private Map<Integer, FileInfo> mDeleteMap;

	private boolean isEditMode;

	private ViewHolder mHolder;

	private OnCheckChangedListener onCheckChangedListener;

	private HashMap<String, Integer> mIconResIDMap;

	public DocumentAdapter(Context context, ArrayList<FileInfo> infos, Map<Integer, FileInfo> deleteMap) {
		this.mContext = context;
		this.mInfos = infos;
		this.mDeleteMap = deleteMap;
		this.isEditMode = false;

		mIconResIDMap = new HashMap<String, Integer>();
		mIconResIDMap.put("doc", R.drawable.file_management_word_type_doc);
		mIconResIDMap.put("dot", R.drawable.file_management_word_type_doc);
		mIconResIDMap.put("wps", R.drawable.file_management_word_type_wps);
		mIconResIDMap.put("docx", R.drawable.file_management_word_type_doc);
		mIconResIDMap.put("dotx", R.drawable.file_management_word_type_doc);
		mIconResIDMap.put("ppt", R.drawable.file_management_word_type_ppt);
		mIconResIDMap.put("pps", R.drawable.file_management_word_type_ppt);
		mIconResIDMap.put("pos", R.drawable.file_management_word_type_ppt);
		mIconResIDMap.put("pptx", R.drawable.file_management_word_type_ppt);
		mIconResIDMap.put("ppsx", R.drawable.file_management_word_type_ppt);
		mIconResIDMap.put("potx", R.drawable.file_management_word_type_ppt);
		mIconResIDMap.put("dps", R.drawable.file_management_word_type_ppt);
		mIconResIDMap.put("xls", R.drawable.file_management_word_type_xls);
		mIconResIDMap.put("xlt", R.drawable.file_management_word_type_xls);
		mIconResIDMap.put("xlsx", R.drawable.file_management_word_type_xls);
		mIconResIDMap.put("xltx", R.drawable.file_management_word_type_xls);
		mIconResIDMap.put("et", R.drawable.file_management_word_type_xls);
		mIconResIDMap.put("pdf", R.drawable.file_management_word_type_pdf);
		mIconResIDMap.put("txt", R.drawable.file_management_word_type_txt);
		mIconResIDMap.put("ebk", R.drawable.file_management_word_type_ebk3);
		mIconResIDMap.put("ebk3", R.drawable.file_management_word_type_ebk3);
		mIconResIDMap.put("htm", R.drawable.file_management_word_type_html);
		mIconResIDMap.put("html", R.drawable.file_management_word_type_html);
		mIconResIDMap.put("xht", R.drawable.file_management_word_type_html);
		mIconResIDMap.put("xhtm", R.drawable.file_management_word_type_html);
		mIconResIDMap.put("xhtml", R.drawable.file_management_word_type_html);
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
		mHolder.tvTime.setText(DateFormatUtils.format(mInfos.get(position).ModifiedDate, DateFormatUtils.PATTERN_YMDHM2));

		setPic(mHolder.ivPic, mInfos.get(position).fileName);

		if (TextUtils.isEmpty(mInfos.get(position).mimeType)) {
			mInfos.get(position).mimeType = getMimeType(mInfos.get(position).fileName);
		}

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
		String suffix = getSuffix(name);
		if (TextUtils.isEmpty(suffix)) {
			ivPic.setImageResource(R.drawable.file_management_word_type_txt);
			return;
		}

		Integer resID = mIconResIDMap.get(suffix.toLowerCase(Locale.CHINA));
		if (resID != null) {
			ivPic.setImageResource(resID);
		} else {
			ivPic.setImageResource(R.drawable.file_management_word_type_txt);
		}
	}

	public String getMimeType(String name) {
		String suffix = getSuffix(name);
		if (TextUtils.isEmpty(suffix)) {
			return "*/*";
		}

		String mimeType = FileBrowserUtil.getDocMimeTypeMap().get(suffix.toLowerCase(Locale.CHINA));
		if (TextUtils.isEmpty(mimeType)) {
			mimeType = "*/*";
		}

		return mimeType;
	}

	private String getSuffix(String name) {
		String suffix = null;
		if (TextUtils.isEmpty(name)) {
			return suffix;
		}

		int index = name.lastIndexOf(".");
		if (index == -1 || index == name.length() - 1) {
			return suffix;
		}

		suffix = name.substring(index + 1);
		return suffix;
	}

	public void clear() {
		if (mIconResIDMap != null) {
			mIconResIDMap.clear();
			mIconResIDMap = null;
		}
	}

	public void setOnCheckChangedListener(OnCheckChangedListener onCheckChangedListener) {
		this.onCheckChangedListener = onCheckChangedListener;
	}

	public interface OnCheckChangedListener {
		void checkChanged();
	}

}
