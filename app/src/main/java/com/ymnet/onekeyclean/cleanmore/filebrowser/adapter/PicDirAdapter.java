package com.ymnet.onekeyclean.cleanmore.filebrowser.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.filebrowser.FileControl;
import com.ymnet.onekeyclean.cleanmore.filebrowser.bean.FileInfo;

import java.util.ArrayList;
import java.util.HashMap;

public class PicDirAdapter extends BaseAdapter {

	private Context                              mContext;
	private ArrayList<String>                    mDirNames;
	private HashMap<String, ArrayList<FileInfo>> mPicMap;
//	private DisplayImageOptions mOptions;
	private int                                  imageWidth;
	private int                                  imageHeight;

	private ViewHolder mHolder;

	public PicDirAdapter(Context context) {
		this.mContext = context;

		mPicMap = FileControl.getInstance(mContext).getAllPicMap();
		mDirNames = FileControl.getInstance(mContext).getAllPicDirNames();

		float denity = context.getResources().getDisplayMetrics().density;
		int width = context.getResources().getDisplayMetrics().widthPixels;
		imageWidth = (int) ((width - 45 * denity) / 2);
		imageHeight = (int) (imageWidth / 1.254);

//		mOptions = new DisplayImageOptions
//				.Builder()
//				.showImageForEmptyUri(R.drawable.image_item_griw_default)
//				.showImageOnFail(R.drawable.image_item_griw_default)
//				.showImageOnLoading(R.drawable.image_item_griw_default)
//				.cacheInMemory(true)
//				.bitmapConfig(Config.RGB_565)
//				.considerExifParams(true)
//				.build();
	}

	@Override
	public int getCount() {
		return mDirNames == null ? 0 : mDirNames.size();
	}

	@Override
	public String getItem(int position) {
		return position < getCount() ? mDirNames.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.file_management_pic_dir_item_layout, null);
			mHolder = new ViewHolder();
			mHolder.ivImage = (ImageView) convertView.findViewById(R.id.iv_image);
			mHolder.tvName = (TextView) convertView.findViewById(R.id.tv_name);

			mHolder.rlPic = (RelativeLayout) convertView.findViewById(R.id.rl_pic);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(imageWidth, imageHeight);
			mHolder.rlPic.setLayoutParams(params);

			convertView.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) convertView.getTag();
		}

		String dirName = getItem(position);

		int size = 0;
		ArrayList<FileInfo> infos = mPicMap.get(dirName);
		if (infos != null && infos.size() > 0) {
			Glide.with(mContext)
					.load("file://" + infos.get(0).filePath)
					.placeholder(R.drawable.image_item_griw_default)
					.error(R.drawable.image_item_griw_default)
					.centerCrop()
					.into(mHolder.ivImage);


			size = infos.size();
		}

		mHolder.tvName.setText(dirName + "(" + size + ")");

		return convertView;
	}

	public boolean remove(String dirName) {
		if (mDirNames != null) {
			return mDirNames.remove(dirName);
		}

		return false;
	}

	public int getCountInDir(String dirName) {
		if (mPicMap != null) {
			if(mPicMap.get(dirName) != null) {
				return mPicMap.get(dirName).size();
			}
		}

		return 0;
	}

	private class ViewHolder {
		RelativeLayout rlPic;

		ImageView ivImage;

		TextView tvName;
	}

}
