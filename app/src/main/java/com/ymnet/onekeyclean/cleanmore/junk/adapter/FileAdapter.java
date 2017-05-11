package com.ymnet.onekeyclean.cleanmore.junk.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.utils.DateUtils;
import com.ymnet.onekeyclean.cleanmore.utils.Util;

import java.io.File;
import java.util.List;


public class FileAdapter extends BaseAdapter {
	private List<File> data;
	private Context mContext;
	
	public FileAdapter(Context mContext, List<File> data) {
		super();
		this.data = data;
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		return data == null ? 0 : data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHold hold ;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_item_file_details, null);
			hold = new ViewHold();
			hold.im_type=(ImageView) convertView.findViewById(R.id.im_file_type);
			hold.tv_name = (TextView) convertView.findViewById(R.id.tv_filename);
			hold.tv_time = (TextView) convertView.findViewById(R.id.tv_filetime);
			hold.tv_size = (TextView) convertView.findViewById(R.id.tv_filesize);
			convertView.setTag(hold);
		}else{
			hold=(ViewHold) convertView.getTag();
		}
		File myFile = data.get(position);
		if(myFile.isFile()){
			hold.im_type.setImageResource(R.drawable.nullfile_icon);
		}else{
			hold.im_type.setImageResource(R.drawable.big_file_folder);
		}
		hold.tv_name.setText(myFile.getName());
		hold.tv_time.setText(DateUtils.long2Date(myFile.lastModified()));
		hold.tv_size.setText(Formatter.formatFileSize(mContext, Util.getFileFolderTotalSize(myFile)));
		return convertView;
	}

static	class ViewHold {
		public ImageView im_type;
		TextView tv_name,tv_time,tv_size;
	}
}
