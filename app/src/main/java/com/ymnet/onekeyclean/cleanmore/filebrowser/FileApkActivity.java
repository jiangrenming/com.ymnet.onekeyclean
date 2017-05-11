package com.ymnet.onekeyclean.cleanmore.filebrowser;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.constants.FileScanState;
import com.ymnet.onekeyclean.cleanmore.datacenter.MarketObservable;
import com.ymnet.onekeyclean.cleanmore.datacenter.MarketObserver;
import com.ymnet.onekeyclean.cleanmore.filebrowser.FileCategoryHelper.FileCategory;
import com.ymnet.onekeyclean.cleanmore.filebrowser.FileSortHelper.SortMethod;
import com.ymnet.onekeyclean.cleanmore.filebrowser.adapter.ApkAdapter;
import com.ymnet.onekeyclean.cleanmore.filebrowser.adapter.ApkAdapter.OnCheckChangedListener;
import com.ymnet.onekeyclean.cleanmore.filebrowser.bean.FileInfo;
import com.ymnet.onekeyclean.cleanmore.temp.AsyncTaskwdh;
import com.ymnet.onekeyclean.cleanmore.wechat.DialogFactory;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class FileApkActivity extends BaseLoadingActivity implements OnClickListener, MarketObserver {

    private TextView tvTopTitle;

    private ImageView ivTopDelete;

    private ImageView ivTopLoading;

    private CheckBox cbTopSelectAll;

    private View bottom;

    private TextView btnBottomDelete;

    private View noData;

    private Map<Integer, FileInfo> deleteMap;

    private Dialog dialogDelete;

    private Dialog dlgDeleteWaiting;

    private ListView lvList;

    private ApkAdapter adapter;

    private ArrayList<FileInfo> mInfos;


    private boolean scanIsFinish = false;
    private Handler handler = new MyHandler(this);

    private static class MyHandler extends Handler {
        WeakReference<FileApkActivity> theActivity;

        public MyHandler(FileApkActivity activity) {
            theActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            FileApkActivity activity = theActivity.get();
            if (activity != null) {
                if (msg.what == FileScanState.SCAN_FINSH) {
                    activity.initData();
                    activity.ivTopDelete.setVisibility(View.VISIBLE);
                    activity.ivTopDelete.setEnabled(true);
                    activity.scanIsFinish = true;
                } else if (msg.what == FileScanState.SCAN_SDCARD_ING) {
                    activity.initData();
                    activity.ivTopDelete.setVisibility(View.GONE);
                    activity.ivTopDelete.setEnabled(false);
                }
            }
        }
    }

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
        if (FileControl.getInstance(getApplicationContext()) != null) {
            FileControl.getInstance(getApplicationContext()).deleteObserver(this);
        }

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
        ((TextView) findViewById(R.id.tv_file_type)).setText(R.string.category_apk);
        tvTopTitle = (TextView) findViewById(R.id.tv_base_title);
        tvTopTitle.setText(R.string.file_manager);
        ivTopDelete = (ImageView) findViewById(R.id.iv_top_delete);
        ivTopLoading = (ImageView) findViewById(R.id.iv_top_loading);

        cbTopSelectAll = (CheckBox) findViewById(R.id.cb_top_select_all);
        bottom = findViewById(R.id.bottom_delete);
        btnBottomDelete = (TextView) findViewById(R.id.btn_bottom_delete);

        lvList = (ListView) findViewById(R.id.lv_list);
        noData = findViewById(R.id.no_data);
        TextView noApk = (TextView) noData.findViewById(R.id.tv_no_data);
        noApk.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.file_type_apk, 0, 0);
        noApk.setText(R.string.apk_dir_empty);


        mInfos = new ArrayList<FileInfo>();
        deleteMap = new HashMap<Integer, FileInfo>();
        adapter = new ApkAdapter(this, mInfos, deleteMap);
        lvList.setAdapter(adapter);

        FileControl.getInstance(getApplicationContext()).addObserver(this);
    }
    private void initLoadingView(){
        fl_loading = findViewById(R.id.fl_loading);
        pb_loading=findViewById(R.id.pb_loading);
        showLoading();

    }

    private void initListener() {
        findViewById(R.id.iv_top_back).setOnClickListener(this);
        findViewById(R.id.tv_file_management).setOnClickListener(this);
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
                if (!adapter.getEditMode()&&scanIsFinish) {
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
        if (FileApkActivity.this == null || FileApkActivity.this.isFinishing()) {
            return;
        }

        FileControl fc = FileControl.getInstance(getApplicationContext());

        scanIsFinish = !fc.isRunning();
        ArrayList<FileInfo> infos = fc.getAllApk(SortMethod.date);
        if (mInfos != null && infos != null) {
            mInfos.clear();
            mInfos.addAll(infos);

            FileSortHelper helper = new FileSortHelper();
            Collections.sort(mInfos, helper.getComParator(SortMethod.date));
        }

        if (mInfos != null && mInfos.size() > 0) {
            if(scanIsFinish){
                ivTopDelete.setVisibility(View.VISIBLE);
            }
            lvList.setVisibility(View.VISIBLE);
            noData.setVisibility(View.GONE);
            hideLoading();
            if (!fc.isRunning()) {
                ivTopLoading.clearAnimation();
                ivTopLoading.setVisibility(View.GONE);
            } else {
                ivTopLoading.setVisibility(View.VISIBLE);
                Animation anim = ivTopLoading.getAnimation();
                if (anim == null) {
                    Animation rotateAnim = AnimationUtils.loadAnimation(this, R.anim.about_update_loading_rotate);
                    LinearInterpolator li = new LinearInterpolator();
                    rotateAnim.setInterpolator(li);
                    ivTopLoading.startAnimation(rotateAnim);
                }
            }

            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        } else if (mInfos != null && mInfos.size() == 0 && fc.isRunning()) {
            showLoading();
            noData.setVisibility(View.GONE);
            lvList.setVisibility(View.GONE);
            ivTopLoading.setVisibility(View.GONE);
        } else {
            noData.setVisibility(View.VISIBLE);
            lvList.setVisibility(View.GONE);
            hideLoading();
            ivTopLoading.setVisibility(View.GONE);
            changeEditMode(false);
        }
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

            tvTopTitle.setText("删除安装包");
        } else {
            ivTopDelete.setVisibility(mInfos.size() == 0 ? View.GONE : View.VISIBLE);
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
            tvTopTitle.setText("删除安装包");
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
        dialogDelete = DialogFactory.createDialog(this, R.layout.dialog_filedelete, "提示", "确认要删除选中的" + size + "项?",
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
                    dlgDeleteWaiting=DialogFactory.createDialog(FileApkActivity.this,R.layout.common_loading_dialog);
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
                    boolean b = FileBrowserUtil.deleteOtherFile(FileApkActivity.this, next.getValue().fileId,
                            next.getValue().filePath, FileCategory.Apk);
                    if (b) {
                        mInfos.remove(next.getValue());
                        if (FileControl.getInstance(getApplicationContext()).getAllApk(SortMethod.date) != null) {
                            FileControl.getInstance(getApplicationContext()).getAllApk(SortMethod.date)
                                    .remove(next.getValue());
                        }
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (FileApkActivity.this.isFinishing()) {
                    return;
                }

                if (dlgDeleteWaiting != null && dlgDeleteWaiting.isShowing()) {
                    dlgDeleteWaiting.dismiss();
                }
                if(mInfos.size()==0 && scanIsFinish){
                    noData.setVisibility(View.VISIBLE);
                }
                changeEditMode(false);
            }

        };

        task.execute(new Void[]{});
    }

    @Override
    public void update(MarketObservable observable, Object data) {
        if (data != null && data instanceof Integer) {
            if ((Integer) data == FileScanState.SCAN_FINSH) {
                handler.sendEmptyMessage(FileScanState.SCAN_FINSH);// 注意：此处要用handler来更新页面内容
            } else if ((Integer) data == FileScanState.SCAN_SDCARD_ING) {
                handler.sendEmptyMessage(FileScanState.SCAN_SDCARD_ING);
            }
        }
    }

}
