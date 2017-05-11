package com.ymnet.onekeyclean.cleanmore.filebrowser.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.filebrowser.bean.FileInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ImageAdapter extends BaseAdapter {

	private Context mContext;

	private ArrayList<FileInfo> mInfos;

	private Map<Integer, FileInfo> mDeleteMap;

	private boolean isEditMode;

	private ViewHolder mHolder;

	private int imageWidth;

//	private DisplayImageOptions mOptions;
	
	private boolean isChanging;
	
	private int curLastVisiblePosition;
	
	public ImageAdapter(Context context, ArrayList<FileInfo> infos, Map<Integer, FileInfo> deleteMap) {
		this.mContext = context;
		this.mInfos = infos;
		this.mDeleteMap = deleteMap == null ? new HashMap<Integer, FileInfo>() : deleteMap;
		this.isEditMode = false;
		this.isChanging = false;
		this.curLastVisiblePosition = -1;
		float denity = context.getResources().getDisplayMetrics().density;
		int width = context.getResources().getDisplayMetrics().widthPixels;
		imageWidth = (int) ((width - 12 * denity) / 3);

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
			convertView = View.inflate(mContext, R.layout.file_management_image_item_layout, null);
			mHolder = new ViewHolder();
			mHolder.ivImage = (ImageView) convertView.findViewById(R.id.iv_image);
			mHolder.cbChecked = (CheckBox) convertView.findViewById(R.id.cb_checked);

			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(imageWidth, imageWidth);
			mHolder.ivImage.setLayoutParams(params);

			convertView.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) convertView.getTag();
		}

		if (isEditMode) {
			mHolder.cbChecked.setVisibility(View.VISIBLE);
			mHolder.cbChecked.setChecked(mDeleteMap.containsKey(position));
		} else {
			mHolder.cbChecked.setVisibility(View.GONE);
		}

		if (!isChanging) {
			/*ImageRequest imageRequest =
					ImageRequestBuilder.newBuilderWithSource(UriUtil.parseUriOrNull("file://" + mInfos.get(position).filePath))
							.setResizeOptions(new ResizeOptions(imageWidth,imageWidth))
							.setProgressiveRenderingEnabled(true)
							.build();
			DraweeController controller = Fresco.newDraweeControllerBuilder()
					.setImageRequest(imageRequest)
					.setOldController(mHolder.ivImage.getController())
					.build();

			mHolder.ivImage.setController(controller);*/

			Glide.with(mContext)
					.load("file://" + mInfos.get(position).filePath)
					.placeholder(R.drawable.image_item_griw_default)
					.error(R.drawable.image_item_griw_default)
					.centerCrop()
					.into(mHolder.ivImage);

//			mHolder.ivImage.setImageURI(Uri.parse("file://" + mInfos.get(position).filePath));
//			ImageLoader.getInstance().displayImage("file://" + mInfos.get(position).filePath, mHolder.ivImage, mOptions);
		}
		
		if (isChanging && position == curLastVisiblePosition) {
			isChanging = false;
		}
		
		return convertView;
	}

	private class ViewHolder {
		ImageView ivImage;

		CheckBox cbChecked;
	}

	public void setEditMode(boolean editMode, int lastVisiblePosition,int gridCount) {
		this.isEditMode = editMode;
		setLastVisiblePosition(lastVisiblePosition, editMode || getCount() == gridCount);
		notifyDataSetChanged();
	}
	
	public void setLastVisiblePosition(int lastVisiblePosition,boolean isChanging) {
		this.isChanging = isChanging;
		this.curLastVisiblePosition = lastVisiblePosition;
	}

	public boolean getEditMode() {
		return isEditMode;
	}
}
