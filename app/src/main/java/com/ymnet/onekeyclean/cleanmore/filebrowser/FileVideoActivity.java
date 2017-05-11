package com.ymnet.onekeyclean.cleanmore.filebrowser;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.filebrowser.FileCategoryHelper.FileCategory;
import com.ymnet.onekeyclean.cleanmore.filebrowser.FileSortHelper.SortMethod;
import com.ymnet.onekeyclean.cleanmore.filebrowser.adapter.VideoAdapter;
import com.ymnet.onekeyclean.cleanmore.filebrowser.adapter.VideoAdapter.OnCheckChangedListener;
import com.ymnet.onekeyclean.cleanmore.filebrowser.bean.FileInfo;
import com.ymnet.onekeyclean.cleanmore.temp.AsyncTaskwdh;
import com.ymnet.onekeyclean.cleanmore.wechat.DialogFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


public class FileVideoActivity extends BaseLoadingActivity implements OnClickListener {

	private TextView tvTopTitle;

	private ImageView ivTopDelete;

	private CheckBox cbTopSelectAll;

	private View bottom;

	private TextView btnBottomDelete;

	private View noData;

	private Map<Integer, FileInfo> deleteMap;

	private Dialog dialogDelete;

	private Dialog dlgDeleteWaiting;

	private ListView lvList;

	private VideoAdapter adapter;

	private ArrayList<FileInfo> mInfos;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_management_base_other_file_layout);

		initView();
		initData();
		initListener();
	}

	@Override
	public void onBackPressed() {
		if (adapter.getEditMode()) {
			// 退出编辑模式
			changeEditMode(false);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onDestroy() {
		if (deleteMap != null) {
			deleteMap.clear();
			deleteMap = null;
		}

		if (mInfos != null) {
			mInfos.clear();
			mInfos = null;
		}

		if (adapter != null) {
			adapter = null;
		}

		super.onDestroy();
	}

	private void initView() {
		initLoadingView();
		((TextView) findViewById(R.id.tv_file_type)).setText(R.string.category_video);
		tvTopTitle = (TextView) findViewById(R.id.tv_base_title);
		tvTopTitle.setText(R.string.file_manager);
		ivTopDelete = (ImageView) findViewById(R.id.iv_top_delete);

		cbTopSelectAll = (CheckBox) findViewById(R.id.cb_top_select_all);
		bottom = findViewById(R.id.bottom_delete);
		btnBottomDelete = (TextView) findViewById(R.id.btn_bottom_delete);

		lvList = (ListView) findViewById(R.id.lv_list);
		noData = findViewById(R.id.no_data);
		TextView noVideo = (TextView) noData.findViewById(R.id.tv_no_data);
		noVideo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.file_type_video, 0, 0);
		noVideo.setText(R.string.video_dir_empty);
		mInfos = new ArrayList<FileInfo>();
		deleteMap = new HashMap<Integer, FileInfo>();
		adapter = new VideoAdapter(this, mInfos, deleteMap);
		lvList.setAdapter(adapter);
	}
	private void initLoadingView(){
		fl_loading = findViewById(R.id.fl_loading);
		pb_loading=findViewById(R.id.pb_loading);
		showLoading();

	}
	private void initListener() {
		findViewById(R.id.tv_file_management).setOnClickListener(this);
		findViewById(R.id.iv_top_back).setOnClickListener(this);
		ivTopDelete.setOnClickListener(this);
		btnBottomDelete.setOnClickListener(this);
		cbTopSelectAll.setOnClickListener(this);

		adapter.setOnCheckChangedListener(new OnCheckChangedListener() {

			@Override
			public void checkChanged() {
				cbTopSelectAll.setChecked(deleteMap.size() == mInfos.size());

				changeTitle();
			}
		});

		lvList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				if (!adapter.getEditMode()) {
					deleteMap.put(position, mInfos.get(position));
					cbTopSelectAll.setChecked(deleteMap.size() == mInfos.size());
					ivTopDelete.setVisibility(View.GONE);
					cbTopSelectAll.setVisibility(View.VISIBLE);
					bottom.setVisibility(View.VISIBLE);
					changeTitle();
					adapter.setEditMode(true);

					return true;
				} else {
					return false;
				}
			}
		});
	}

	private void initData() {
		AsyncTaskwdh<Void, Void, Void> task = new AsyncTaskwdh<Void, Void, Void>() {

			@Override
			protected void onPreExecute() {
				showLoading();
				noData.setVisibility(View.GONE);
				lvList.setVisibility(View.GONE);
			}
			
			@Override
			protected Void doInBackground(Void... params) {
				ArrayList<FileInfo> infos = FileControl.getInstance(getApplicationContext()).getAllVideos(
						getApplicationContext(), SortMethod.date);
				if (infos != null && mInfos != null) {
					mInfos.addAll(infos);
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				if (FileVideoActivity.this == null || FileVideoActivity.this.isFinishing()) {
					return;
				}

				hideLoading();
				if (mInfos != null && mInfos.size() > 0) {
					ivTopDelete.setVisibility(View.VISIBLE);
					lvList.setVisibility(View.VISIBLE);
					noData.setVisibility(View.GONE);

					if (adapter != null) {
						adapter.notifyDataSetChanged();
					}
				} else {
					lvList.setVisibility(View.GONE);
					noData.setVisibility(View.VISIBLE);
					
					changeEditMode(false);
				}
			}

		};

		task.execute(new Void[] {});
	}

	private void changeEditMode(boolean editMode) {
		if (deleteMap != null) {
			deleteMap.clear();
		}

		if (adapter != null) {
			adapter.setEditMode(editMode);
		}

		if (editMode) {
			ivTopDelete.setVisibility(View.GONE);
			cbTopSelectAll.setVisibility(View.VISIBLE);
			bottom.setVisibility(View.VISIBLE);
			btnBottomDelete.setEnabled(false);

			tvTopTitle.setText("删除视频");
		} else {
			if (mInfos != null) { // In case of NullPointerException
				ivTopDelete.setVisibility(mInfos.size() == 0 ? View.GONE : View.VISIBLE);
			}
			cbTopSelectAll.setVisibility(View.GONE);
			cbTopSelectAll.setChecked(false);
			cbTopSelectAll.setText("全选");
			bottom.setVisibility(View.GONE);

			tvTopTitle.setText("文件管理");
		}
	}

	private void changeTitle() {
		btnBottomDelete.setEnabled(deleteMap.size() != 0);
		cbTopSelectAll.setText(cbTopSelectAll.isChecked() ? "取消" : "全选");

		if (deleteMap.size() == 0) {
			tvTopTitle.setText("删除视频");
		} else {
			tvTopTitle.setText("已选中" + deleteMap.size() + "项");
		}
	}

	@Override
	public void onClick(View v) {
		int i1 = v.getId();
		if (i1 == R.id.iv_top_back) {
			if (adapter.getEditMode()) {
				// 退出编辑模式
				changeEditMode(false);
				//break;
			}

			finish();

		} else if (i1 == R.id.tv_file_management) {
			finish();

		} else if (i1 == R.id.iv_top_delete) {
			changeEditMode(true);

		} else if (i1 == R.id.btn_bottom_delete) {
			showConfirmDeleteDialog(deleteMap.size());

		} else if (i1 == R.id.cb_top_select_all) {
			if (cbTopSelectAll.isChecked()) {
				for (int i = 0, size = mInfos.size(); i < size; i++) {
					if (!deleteMap.containsKey(i)) {
						deleteMap.put(i, mInfos.get(i));
					}
				}
			} else {
				// 取消全选
				deleteMap.clear();
			}

			adapter.notifyDataSetChanged();
			changeTitle();

		} else {
		}
	}

	private void showConfirmDeleteDialog(int size) {
		dialogDelete= DialogFactory.createDialog(this, R.layout.dialog_filedelete, "提示", "确认要删除选中的" + size + "项?",
				getString(R.string.yes_zh), getString(R.string.no_zh),
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogDelete.cancel();
						asyncDelete();
					}
				}, new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogDelete.cancel();
					}
				});
		dialogDelete.setCancelable(true);
		dialogDelete.setCanceledOnTouchOutside(true);
		dialogDelete.show();
	}

	private void asyncDelete() {
		AsyncTaskwdh<Void, Void, Void> task = new AsyncTaskwdh<Void, Void, Void>() {

			@Override
			protected void onPreExecute() {
				if (dlgDeleteWaiting == null) {
					dlgDeleteWaiting=DialogFactory.createDialog(FileVideoActivity.this,R.layout.common_loading_dialog);
					dlgDeleteWaiting.setCancelable(false);
					dlgDeleteWaiting.setCanceledOnTouchOutside(false);
				}
				dlgDeleteWaiting.show();
			}

			@Override
			protected Void doInBackground(Void... params) {
				Iterator<Entry<Integer, FileInfo>> iterator = deleteMap.entrySet().iterator();
				Entry<Integer, FileInfo> next;
				while (iterator.hasNext() && mInfos != null) {
					next = iterator.next();
					boolean b = FileBrowserUtil.deleteVideo(FileVideoActivity.this, next.getValue().fileId,
							next.getValue().filePath, FileCategory.Video);
					if (b) {
						if(next.getValue() != null) {
							mInfos.remove(next.getValue());
						}
					}
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				if (FileVideoActivity.this == null || FileVideoActivity.this.isFinishing()) {
					return;
				}

				if (dlgDeleteWaiting != null && dlgDeleteWaiting.isShowing()) {
					dlgDeleteWaiting.dismiss();
				}
				if(mInfos.size()==0 && !FileControl.getInstance(getApplicationContext()).isRunning()){
					noData.setVisibility(View.VISIBLE);
				}
				changeEditMode(false);
			}

		};

		task.execute(new Void[] {});
	}

}
