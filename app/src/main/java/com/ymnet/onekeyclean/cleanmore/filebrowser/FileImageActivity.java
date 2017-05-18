package com.ymnet.onekeyclean.cleanmore.filebrowser;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.ImmersiveActivity;
import com.ymnet.onekeyclean.cleanmore.filebrowser.FileCategoryHelper.FileCategory;
import com.ymnet.onekeyclean.cleanmore.filebrowser.adapter.ImageAdapter;
import com.ymnet.onekeyclean.cleanmore.filebrowser.bean.FileInfo;
import com.ymnet.onekeyclean.cleanmore.temp.AsyncTaskwdh;
import com.ymnet.onekeyclean.cleanmore.utils.C;
import com.ymnet.onekeyclean.cleanmore.wechat.DialogFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 照片缩略图
 */
public class FileImageActivity extends ImmersiveActivity implements OnClickListener {

    private static final int REQUESTCODE_BIGIMAGE = 1;

    private TextView tvTopTitle;

    private ImageView ivTopDelete;

    private CheckBox cbTopSelectAll;

    private View bottom;

    private TextView btnBottomDelete;

    private View noData;

    private GridView gvImages;

    private ImageAdapter adapter;

    private ArrayList<FileInfo> mInfos;

    private Map<Integer, FileInfo> deleteMap;

    private Dialog dialog;

    private Dialog dlgDeleteWaiting;

    private int currentIndex;

    private String curDirName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_management_image_activity_layout);

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

        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }

        if (requestCode == REQUESTCODE_BIGIMAGE && data.getBooleanExtra("isDelete", false)) {
            setResult();

            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void initView() {
        tvTopTitle = (TextView) findViewById(R.id.tv_base_title);
        ivTopDelete = (ImageView) findViewById(R.id.iv_top_delete);
        ivTopDelete.setVisibility(View.VISIBLE);

        cbTopSelectAll = (CheckBox) findViewById(R.id.cb_top_select_all);
        bottom = findViewById(R.id.bottom_delete);
        btnBottomDelete = (TextView) findViewById(R.id.btn_bottom_delete);

        gvImages = (GridView) findViewById(R.id.gv_image);
        noData = findViewById(R.id.no_data);
        TextView noImage = (TextView) noData.findViewById(R.id.tv_no_data);
        noImage.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.file_type_image, 0, 0);
        noImage.setText("没有任何图片");

        currentIndex = getIntent().getIntExtra("index", 0);
        ArrayList<String> names = FileControl.getInstance(C.get()).getAllPicDirNames();
        curDirName = "图片";
        if (names != null && currentIndex < names.size()) {
            curDirName = names.get(currentIndex);
        }

        tvTopTitle.setText(curDirName);
        ((TextView) findViewById(R.id.tv_file_type)).setText(curDirName);
    }

    private void initListener() {
        findViewById(R.id.tv_file_management).setOnClickListener(this);
        findViewById(R.id.iv_top_back).setOnClickListener(this);
        ivTopDelete.setOnClickListener(this);
        btnBottomDelete.setOnClickListener(this);
        cbTopSelectAll.setOnClickListener(this);

        gvImages.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                FileInfo info = adapter.getItem(position);

                if (adapter.getEditMode()) {
                    CheckBox cb = (CheckBox) view.findViewById(R.id.cb_checked);

                    if (cb.isChecked()) {
                        cb.setChecked(false);
                        deleteMap.remove(position);
                    } else {
                        cb.setChecked(true);
                        deleteMap.put(position, info);
                    }

                    cbTopSelectAll.setChecked(deleteMap.size() == mInfos.size());

                    changeTitle();
                } else {
                    Intent intent = new Intent(FileImageActivity.this, FileBigImageActivity.class);
                    intent.putExtra("position", position);
                    intent.putExtra("index", currentIndex);
                    startActivityForResult(intent, REQUESTCODE_BIGIMAGE);
                }
            }
        });

        gvImages.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (!adapter.getEditMode()) {
                    if (mInfos != null && mInfos.size() > 0) {
                        deleteMap.put(position, mInfos.get(position));
                        cbTopSelectAll.setChecked(deleteMap.size() == mInfos.size());
                    }
                    ivTopDelete.setVisibility(View.GONE);
                    cbTopSelectAll.setVisibility(View.VISIBLE);
                    bottom.setVisibility(View.VISIBLE);
                    changeTitle();
                    adapter.setEditMode(true, gvImages.getLastVisiblePosition(), gvImages.getCount());

                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private void initData() {
        FileControl control = FileControl.getInstance(this);
        control.setUserStop(true);//不论扫描是否完成 都停止扫描微信图片,
        if(control.getAllPicMap() != null) {
            mInfos = control.getAllPicMap().get(curDirName);
        }

        deleteMap = new HashMap<Integer, FileInfo>();
        adapter = new ImageAdapter(this, mInfos, deleteMap);
        gvImages.setAdapter(adapter);
        gvImages.setEmptyView(noData);

        if (mInfos != null && mInfos.size() > 0) {
            adapter.notifyDataSetChanged();
        } else {
            changeEditMode(false);
        }
    }

    private void changeEditMode(boolean editMode) {
        if (deleteMap != null) {
            deleteMap.clear();
        }

        if (adapter != null) {
            adapter.setEditMode(editMode, gvImages.getLastVisiblePosition(),gvImages.getCount());
        }

        if (editMode) {
            ivTopDelete.setVisibility(View.GONE);
            cbTopSelectAll.setVisibility(View.VISIBLE);
            bottom.setVisibility(View.VISIBLE);
            btnBottomDelete.setEnabled(false);

            tvTopTitle.setText("删除" + curDirName);
        } else {
            if(mInfos != null) {
                ivTopDelete.setVisibility(mInfos.size() == 0 ? View.GONE : View.VISIBLE);
            }else{
                ivTopDelete.setVisibility(View.GONE);
            }
            cbTopSelectAll.setVisibility(View.GONE);
            cbTopSelectAll.setChecked(false);
            cbTopSelectAll.setText("全选");
            bottom.setVisibility(View.GONE);

            tvTopTitle.setText(curDirName);
        }
    }

    private void changeTitle() {
        btnBottomDelete.setEnabled(deleteMap.size() != 0);
        cbTopSelectAll.setText(cbTopSelectAll.isChecked() ? "取消" : "全选");

        if (deleteMap.size() == 0) {
            tvTopTitle.setText("删除" + curDirName);
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
               // break;
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
            adapter.setEditMode(true, gvImages.getLastVisiblePosition(), gvImages.getCount());
            //                adapter.setLastVisiblePosition(gvImages.getLastVisiblePosition());
            adapter.notifyDataSetChanged();
            changeTitle();

        } else {
        }
    }

    private void showConfirmDeleteDialog(int size) {
        dialog = DialogFactory.createDialog(this, R.layout.dialog_filedelete, "提示", "确认要删除选中的" + size + "项?",
                getString(R.string.yes_zh), getString(R.string.no_zh),
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                        asyncDelete();
                    }
                }, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();

                    }
                });
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void asyncDelete() {
        AsyncTaskwdh<Void, Void, Boolean> task = new AsyncTaskwdh<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                if (dlgDeleteWaiting == null) {
                    dlgDeleteWaiting = DialogFactory.createDialog(FileImageActivity.this,  R.layout.common_loading_dialog);
                    dlgDeleteWaiting.setCancelable(false);
                    dlgDeleteWaiting.setCanceledOnTouchOutside(false);
                }
                dlgDeleteWaiting.show();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                Iterator<Entry<Integer, FileInfo>> iterator = deleteMap.entrySet().iterator();
                Entry<Integer, FileInfo> next;
                boolean result = false;
                while (iterator.hasNext() && mInfos != null) {
                    next = iterator.next();
                    boolean b = FileBrowserUtil.deleteImage(FileImageActivity.this, next.getValue().fileId, next.getValue().filePath, FileCategory.Picture);
                    if (b) {
                        mInfos.remove(next.getValue());
                        result = true;
                    }
                }
                return result;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (FileImageActivity.this.isFinishing()) {
                    return;
                }

                if (dlgDeleteWaiting != null && dlgDeleteWaiting.isShowing()) {
                    dlgDeleteWaiting.dismiss();
                }

                if (result) {
                    setResult();
                }

                changeEditMode(false);
            }

        };

        task.execute(new Void[]{});
    }

    private void setResult() {
        Intent intent = getIntent();
        intent.putExtra("isDelete", true);
        intent.putExtra("dirName", curDirName);
        setResult(0, intent);
    }

}
