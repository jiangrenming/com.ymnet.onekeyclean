package com.ymnet.onekeyclean.cleanmore.filebrowser;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.ImmersiveActivity;
import com.ymnet.onekeyclean.cleanmore.filebrowser.adapter.PicDirAdapter;
import com.ymnet.onekeyclean.cleanmore.utils.C;



/**
 * 照片目录--九宫格
 */
public class FilePicDirActivity extends ImmersiveActivity implements OnClickListener {

	protected static final int REQUESTCODE_IMAGE = 1;

	private TextView tvTopTitle;

	private View noData;

	private GridView gvImages;

	private PicDirAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_management_pic_dir_activity_layout);

		initView();
		initData();
		initListener();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (data == null) {
			return;
		}

		if (requestCode == REQUESTCODE_IMAGE && data.getBooleanExtra("isDelete", false)) {
			String dirName = data.getStringExtra("dirName");
			if (adapter != null) {
				if (adapter.getCountInDir(dirName) == 0 && !TextUtils.isEmpty(dirName)) {
					adapter.remove(dirName);
				}

				adapter.notifyDataSetChanged();
			}
		}
	}

	private void initView() {
		tvTopTitle = (TextView) findViewById(R.id.tv_base_title);
		tvTopTitle.setText(R.string.category_picture);

		gvImages = (GridView) findViewById(R.id.gv_image);
		noData = findViewById(R.id.no_data);
		TextView noImage = (TextView) noData.findViewById(R.id.tv_no_data);
		noImage.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.file_type_image, 0, 0);
		noImage.setText(R.string.image_dir_empty);
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		adapter = new PicDirAdapter(this);
		gvImages.setAdapter(adapter);
		gvImages.setEmptyView(noData);
	}

	private void initListener() {
		findViewById(R.id.iv_top_back).setOnClickListener(this);

		gvImages.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(C.get(), FileImageActivity.class);
				intent.putExtra("index", position);
				startActivityForResult(intent, REQUESTCODE_IMAGE);
			}
		});
	}

	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.iv_top_back) {
			finish();

		} else {
		}
	}

}
