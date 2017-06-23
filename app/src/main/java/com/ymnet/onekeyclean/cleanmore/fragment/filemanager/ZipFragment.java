package com.ymnet.onekeyclean.cleanmore.fragment.filemanager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.filebrowser.FileBrowserUtil;
import com.ymnet.onekeyclean.cleanmore.filebrowser.FileCategoryHelper;
import com.ymnet.onekeyclean.cleanmore.filebrowser.FileControl;
import com.ymnet.onekeyclean.cleanmore.filebrowser.FileSortHelper;
import com.ymnet.onekeyclean.cleanmore.filebrowser.FileSortHelper.SortMethod;
import com.ymnet.onekeyclean.cleanmore.filebrowser.bean.FileInfo;
import com.ymnet.onekeyclean.cleanmore.fragment.filemanager.adapter.FileItemAdapter;
import com.ymnet.onekeyclean.cleanmore.fragment.filemanager.base.BaseFragment;
import com.ymnet.onekeyclean.cleanmore.temp.AsyncTaskwdh;
import com.ymnet.onekeyclean.cleanmore.utils.Util;
import com.ymnet.onekeyclean.cleanmore.wechat.DialogFactory;
import com.ymnet.onekeyclean.cleanmore.widget.LinearLayoutItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/21.
 */

public class ZipFragment extends BaseFragment {

    protected View fl_loading;
    protected View pb_loading;
    private TextView tvTopTitle;
    private CheckBox cbTopSelectAll;
    private View bottom;
    private TextView btnBottomDelete;
    private View noData;
    private Map<Integer, FileInfo> deleteMap;
    private Dialog dialogDelete;
    private Dialog dlgDeleteWaiting;
    private RecyclerView recyclerView;
    private FileItemAdapter adapter;
    private ArrayList<FileInfo> mInfos;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.documents_fragment_item, container, false);
        Context context = getActivity();
        initView(context, view);
        initData(context);
        initListener(context, view);
        return view;
    }

    private void initView(Context context, View view) {
        fl_loading = view.findViewById(R.id.fl_loading);
        pb_loading = view.findViewById(R.id.pb_loading);
        tvTopTitle = (TextView) view.findViewById(R.id.tv_base_title);
        tvTopTitle.setText(R.string.zip_clean);
        cbTopSelectAll = (CheckBox) view.findViewById(R.id.cb_top_select_all);
        bottom = view.findViewById(R.id.bottom_delete);
        btnBottomDelete = (TextView) view.findViewById(R.id.btn_bottom_delete);
        btnBottomDelete.setText(String.format(context.getResources().getString(R.string.file_delete), Util.formatFileSizeToPic(0)));
        recyclerView = (RecyclerView) view.findViewById(R.id.ducuments_recyclerview);
        noData = view.findViewById(R.id.no_data);
        TextView noDocument = (TextView) noData.findViewById(R.id.tv_no_data);
        noDocument.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.file_type_document, 0, 0);
        noDocument.setText(R.string.zip_dir_empty);
        mInfos = new ArrayList<FileInfo>();
        deleteMap = new HashMap<Integer, FileInfo>();
        adapter = new FileItemAdapter(context, mInfos, deleteMap);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new LinearLayoutItemDecoration(context, LinearLayoutItemDecoration.HORIZONTAL_LIST));
        recyclerView.setAdapter(adapter);
    }

    private void initListener(final Context context, View view) {
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

    private void initData(Context context) {
        final FileControl fc = FileControl.getInstance(context);
        AsyncTaskwdh<Void, Void, Void> task = new AsyncTaskwdh<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                showLoading();
            }

            @Override
            protected Void doInBackground(Void... params) {

                ArrayList<FileInfo> infos = fc.getAllZip(SortMethod.size);
                if (infos != null && mInfos != null) {
                    mInfos.clear();
                    mInfos.addAll(infos);
                    FileSortHelper helper = new FileSortHelper();
                    Collections.sort(mInfos, helper.getComParator(SortMethod.size));
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
        btnBottomDelete.setText(String.format(context.getResources().getString(R.string.file_delete), Util.formatFileSizeToPic(size)));
        cbTopSelectAll.setText(cbTopSelectAll.isChecked() ? "取消" : "全选");
        if (deleteMap.size() == 0) {
            tvTopTitle.setText(R.string.zip_clean);
        } else {
            tvTopTitle.setText("已选中" + deleteMap.size() + "项");
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.btn_bottom_delete:
                showConfirmDeleteDialog(deleteMap.size(), getActivity());
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
                changeTitle(getActivity());
                break;
        }
    }

    private void showConfirmDeleteDialog(int size, final Activity activity) {
        dialogDelete = DialogFactory.createDialog(activity, R.layout.dialog_filedelete, "提示", "确认要删除选中的" + size + "项?",
                getString(R.string.yes_zh), getString(R.string.no_zh),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogDelete.dismiss();
                        asyncDelete(activity);
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogDelete.dismiss();

                    }
                });
        dialogDelete.setCancelable(true);
        dialogDelete.setCanceledOnTouchOutside(true);
        dialogDelete.show();
    }

    private void asyncDelete(final Activity activity) {
        AsyncTaskwdh<Void, Void, Void> task = new AsyncTaskwdh<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                if (dlgDeleteWaiting == null) {
                    dlgDeleteWaiting = DialogFactory.createDialog(activity, R.layout.common_loading_dialog);
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
                        boolean b = FileBrowserUtil.deleteOtherFile(activity, next.getValue().fileId,
                                next.getValue().filePath, FileCategoryHelper.FileCategory.Zip);
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
                adapter.setDate(mInfos, deleteMap);
            }

        };
        task.execute(new Void[]{});
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (deleteMap != null) {
            deleteMap.clear();
            deleteMap = null;
        }

        if (mInfos != null) {
            mInfos.clear();
            mInfos = null;
        }

        if (adapter != null) {
            adapter.clear();
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
