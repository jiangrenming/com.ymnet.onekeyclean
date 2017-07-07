package com.ymnet.onekeyclean.cleanmore.fragment.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.ImmersiveActivity;
import com.ymnet.onekeyclean.cleanmore.filebrowser.FileBrowserUtil;
import com.ymnet.onekeyclean.cleanmore.filebrowser.FileCategoryHelper;
import com.ymnet.onekeyclean.cleanmore.filebrowser.FileControl;
import com.ymnet.onekeyclean.cleanmore.filebrowser.FileSortHelper;
import com.ymnet.onekeyclean.cleanmore.filebrowser.bean.FileInfo;
import com.ymnet.onekeyclean.cleanmore.fragment.filemanager.adapter.FileItemAdapter;
import com.ymnet.onekeyclean.cleanmore.temp.AsyncTaskwdh;
import com.ymnet.onekeyclean.cleanmore.utils.C;
import com.ymnet.onekeyclean.cleanmore.utils.OnekeyField;
import com.ymnet.onekeyclean.cleanmore.utils.Util;
import com.ymnet.onekeyclean.cleanmore.wechat.DialogFactory;
import com.ymnet.onekeyclean.cleanmore.widget.LinearLayoutItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class VideoManagerActivity extends ImmersiveActivity implements View.OnClickListener {

    protected View                   fl_loading;
    protected View                   pb_loading;
    private   TextView               tvTopTitle;
    private   CheckBox               cbTopSelectAll;
    private   View                   bottom;
    private   TextView               btnBottomDelete;
    private   View                   noData;
    private   Map<Integer, FileInfo> deleteMap;
    private   Dialog                 dialogDelete;
    private   Dialog                 dlgDeleteWaiting;
    private   RecyclerView           recyclerView;
    private   FileItemAdapter        adapter;
    private   ArrayList<FileInfo>    mInfos;
    private   View                   mBack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.documents_fragment_item);

        String stringExtra = getIntent().getStringExtra(OnekeyField.ONEKEYCLEAN);
        String statistics_key = getIntent().getStringExtra(OnekeyField.STATISTICS_KEY);
        if (stringExtra != null) {
            Map<String, String> m = new HashMap<>();
            m.put(OnekeyField.ONEKEYCLEAN, stringExtra);
            MobclickAgent.onEvent(C.get(), statistics_key, m);
        }

        initView(C.get());
        initData(C.get());
        initListener(C.get());
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(C.get());
    }

    private void initView(Context context) {
        fl_loading = findViewById(R.id.fl_loading);
        pb_loading = findViewById(R.id.pb_loading);
        mBack = findViewById(R.id.iv_top_back);
        tvTopTitle = (TextView) findViewById(R.id.tv_base_title);
        tvTopTitle.setText(R.string.video_clean);
        cbTopSelectAll = (CheckBox) findViewById(R.id.cb_top_select_all);
        bottom = findViewById(R.id.bottom_delete);
        btnBottomDelete = (TextView) findViewById(R.id.btn_bottom_delete);
        btnBottomDelete.setText(String.format(context.getResources().getString(R.string.file_delete_withdata), Util.formatFileSizeToPic(0)));
        recyclerView = (RecyclerView) findViewById(R.id.ducuments_recyclerview);
        noData = findViewById(R.id.no_data);
        TextView noDocument = (TextView) noData.findViewById(R.id.tv_no_data);
        noDocument.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.file_type_document, 0, 0);
        noDocument.setText(R.string.video_dir_empty);
        mInfos = new ArrayList<FileInfo>();
        deleteMap = new HashMap<Integer, FileInfo>();
        adapter = new FileItemAdapter(context, mInfos, deleteMap);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new LinearLayoutItemDecoration(context, LinearLayoutItemDecoration.HORIZONTAL_LIST));
        recyclerView.setAdapter(adapter);
    }

    private void initListener(final Context context) {
        mBack.setOnClickListener(this);
        btnBottomDelete.setOnClickListener(this);
        cbTopSelectAll.setOnClickListener(this);
        cbTopSelectAll.setVisibility(View.VISIBLE);
        bottom.setVisibility(View.VISIBLE);
        btnBottomDelete.setEnabled(false);
        adapter.setOnCheckChangedListener(new FileItemAdapter.OnCheckChangedListener() {
            @Override
            public void checkChanged() {
                cbTopSelectAll.setChecked(deleteMap.size() == mInfos.size());

                changeTitle(context);
            }
        });

        adapter.setItemClickListener(new FileItemAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                FileBrowserUtil.openFile(context, mInfos.get(position));
            }

            @Override
            public boolean onLongClick(View view, int position) {
                return false;
            }
        });
    }

    private void initData(final Context context) {
        final FileControl fc = FileControl.getInstance(context);
        AsyncTaskwdh<Void, Void, Void> task = new AsyncTaskwdh<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                showLoading();
            }

            @Override
            protected Void doInBackground(Void... params) {
                FileControl fc = FileControl.getInstance(context);
                ArrayList<FileInfo> infos = fc.getAllVideos(context, FileSortHelper.SortMethod.size);
                if (infos != null && mInfos != null) {
                    mInfos.clear();
                    mInfos.addAll(infos);
                    FileSortHelper helper = new FileSortHelper();
                    Collections.sort(mInfos, helper.getComParator(FileSortHelper.SortMethod.size));
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                hideLoading();
                if (mInfos != null && mInfos.size() > 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    noData.setVisibility(View.GONE);
                    if (adapter != null) {
                        adapter.setDate(mInfos, deleteMap);
                    }
                } else if (mInfos != null && mInfos.size() == 0 && fc.isRunning()) {
                    showLoading();
                    noData.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    noData.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }

        };
        task.execute(new Void[]{});
    }

    private void changeTitle(Context context) {
        long size = 0;
        Iterator<Map.Entry<Integer, FileInfo>> iterator = deleteMap.entrySet().iterator();
        Map.Entry<Integer, FileInfo> next;
        while (iterator.hasNext() && mInfos != null) {
            next = iterator.next();
            if (next != null) {
                size += next.getValue().fileSize;
            }
        }
        btnBottomDelete.setEnabled(deleteMap.size() != 0);
        btnBottomDelete.setText(String.format(context.getResources().getString(R.string.file_delete_withdata), Util.formatFileSizeToPic(size)));
        cbTopSelectAll.setText(cbTopSelectAll.isChecked() ? "取消" : "全选");

        if (deleteMap.size() == 0) {
            tvTopTitle.setText(R.string.video_clean);
        } else {
            tvTopTitle.setText("已选中" + deleteMap.size() + "项");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_top_back:
                finish();
                break;
            case R.id.btn_bottom_delete:
                showConfirmDeleteDialog(deleteMap.size(), this);
                break;
            case R.id.cb_top_select_all:
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
                adapter.setDate(mInfos, deleteMap);
                changeTitle(C.get());
                break;
        }
    }

    private void showConfirmDeleteDialog(int size, final Context context) {
        dialogDelete = DialogFactory.createDialog(context, R.layout.dialog_filedelete, "提示", "确认要删除选中的" + size + "项?",
                getString(R.string.yes_zh), getString(R.string.no_zh),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogDelete.cancel();
                        asyncDelete(context);
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogDelete.cancel();

                    }
                });
        dialogDelete.setCancelable(true);
        dialogDelete.setCanceledOnTouchOutside(true);
        dialogDelete.show();
    }

    private void asyncDelete(final Context context) {
        AsyncTaskwdh<Void, Void, Void> task = new AsyncTaskwdh<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                if (dlgDeleteWaiting == null) {
                    dlgDeleteWaiting = DialogFactory.createDialog(context, R.layout.common_loading_dialog);
                    dlgDeleteWaiting.setCancelable(false);
                    dlgDeleteWaiting.setCanceledOnTouchOutside(false);
                }
                dlgDeleteWaiting.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                Iterator<Map.Entry<Integer, FileInfo>> iterator = deleteMap.entrySet().iterator();
                Map.Entry<Integer, FileInfo> next;
                while (iterator.hasNext() && mInfos != null) {
                    next = iterator.next();
                    if (next != null) {
                        boolean b = FileBrowserUtil.deleteVideo(context, next.getValue().fileId,
                                next.getValue().filePath, FileCategoryHelper.FileCategory.Video);
                        if (b) {
                            if (next.getValue() != null) {
                                mInfos.remove(next.getValue());
                            }
                        }
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (dlgDeleteWaiting != null && dlgDeleteWaiting.isShowing()) {
                    dlgDeleteWaiting.dismiss();
                }
                if (mInfos.size() == 0) {
                    noData.setVisibility(View.VISIBLE);
                }
                deleteMap.clear();
                changeTitle(C.get());
                adapter.setDate(mInfos, deleteMap);
            }

        };
        task.execute(new Void[]{});
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        FileControl instance = FileControl.getInstance(C.get());
        if (instance.isRunning()) {
            instance.close();
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
    }

    public void showLoading() {
        fl_loading.setVisibility(View.VISIBLE);
        pb_loading.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        fl_loading.setVisibility(View.GONE);
        pb_loading.setVisibility(View.GONE);
    }
}