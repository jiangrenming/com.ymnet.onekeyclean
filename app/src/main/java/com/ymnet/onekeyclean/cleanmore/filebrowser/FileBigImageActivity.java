package com.ymnet.onekeyclean.cleanmore.filebrowser;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.common.ApplicationUtils;
import com.ymnet.onekeyclean.cleanmore.filebrowser.bean.FileInfo;
import com.ymnet.onekeyclean.cleanmore.junk.adapter.BigImageAdapter;
import com.ymnet.onekeyclean.cleanmore.junk.adapter.BigImageAdapter.ShowHideListerer;
import com.ymnet.onekeyclean.cleanmore.temp.AsyncTaskwdh;
import com.ymnet.onekeyclean.cleanmore.utils.C;
import com.ymnet.onekeyclean.cleanmore.utils.Utils;
import com.ymnet.onekeyclean.cleanmore.wechat.DialogFactory;
import com.ymnet.onekeyclean.cleanmore.wechat.view.BaseFragmentActivity;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

/**
 * 大图
 */
public class FileBigImageActivity extends BaseFragmentActivity implements OnClickListener {

    private View head;

    private View foot;

    private ViewPager mViewPager;

    private BigImageAdapter adapter;

    private ArrayList<FileInfo> mInfos;

    private TextView tvPagePosition;

    private TextView tvPicName;

    private TextView tvPicDesc;

    private int currentPosition;

    private ShowHideListerer showFootAndHead;

    private Dialog dlgConfirmDelete;

    private Dialog dlgDeleteWaiting;

    private int screenWidth = -1;

    private int screenHeight = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_management_big_image_activity_layout);

        initView();
        initListener();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.vp_viewpager);
        head = findViewById(R.id.rl_top);
        foot = findViewById(R.id.ll_bottom);

        tvPagePosition = (TextView) findViewById(R.id.tv_page_position);
        tvPicName = (TextView) findViewById(R.id.tv_pic_name);
        tvPicDesc = (TextView) findViewById(R.id.tv_pic_description);

        showFootAndHead = new ShowHideListerer() {

            @Override
            public void onChange() {
                if (head.getVisibility() == View.VISIBLE) {
                    head.setVisibility(View.GONE);
                    foot.setVisibility(View.GONE);
                } else {
                    head.setVisibility(View.VISIBLE);
                    foot.setVisibility(View.VISIBLE);
                }
            }
        };
    }

    private void initListener() {
        findViewById(R.id.iv_top_back).setOnClickListener(this);
        findViewById(R.id.tv_bottom_open).setOnClickListener(this);
        findViewById(R.id.tv_bottom_del).setOnClickListener(this);
        findViewById(R.id.tv_bottom_set_wallpaper).setOnClickListener(this);

        mViewPager.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (head.getVisibility() == View.VISIBLE) {
                    head.setVisibility(View.GONE);
                    foot.setVisibility(View.GONE);
                } else {
                    head.setVisibility(View.VISIBLE);
                    foot.setVisibility(View.VISIBLE);
                }
            }
        });

        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                setNameAndPage(currentPosition);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    private void initData() {
        mInfos = (ArrayList<FileInfo>)getIntent().getSerializableExtra("infos");
        adapter = new BigImageAdapter(C.get(), mInfos);
        adapter.setChangeListerer(showFootAndHead);
        mViewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        mViewPager.setOffscreenPageLimit(1);

        currentPosition = getIntent().getIntExtra("position", 0);
        mViewPager.setCurrentItem(currentPosition);

        setNameAndPage(currentPosition);
    }

    private void setNameAndPage(int position) {
        tvPagePosition.setText(getPagePosition(position));
        tvPicName.setText(getPicName(position));
        tvPicDesc.setText(getPicDesc(position));
    }

    private String getPagePosition(int position) {
        if (mInfos == null || (position + 1) > mInfos.size()) {
            return "";
        }
        return (position + 1) + "/" + mInfos.size();
    }

    private String getPicName(int position) {
        if (mInfos == null || position >= mInfos.size()) {
            return "";
        }
        String path = mInfos.get(position).filePath;
        int index = path.lastIndexOf("/");
        if (index > 0) {
            return path.substring(index + 1);
        } else {
            return "";
        }
    }

    private String getPicDesc(int position) {
        if (mInfos == null || position >= mInfos.size()) {
            return "";
        }
        String path = mInfos.get(position).filePath;
        File f = new File(path);
        if (f != null && f.exists() && f.isFile()) {
            StringBuilder sb = new StringBuilder();
            sb.append(ApplicationUtils.formatFileSizeToString(f.length()));

            Options options = new Options();
            options.inJustDecodeBounds = true;

            try {
                BitmapFactory.decodeFile(path, options);
                if (options.mCancel || options.outWidth == -1 || options.outHeight == -1) {
                } else {
                    sb.append(", ");
                    sb.append(options.outWidth);
                    sb.append("×");
                    sb.append(options.outHeight);
                }
            } catch (Exception e) {
            }

            return sb.toString();
        } else {
            return "";
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.iv_top_back) {
            finish();

        } else if (i == R.id.tv_bottom_open) {
            openFile(currentPosition);

        } else if (i == R.id.tv_bottom_del) {
            showConfirmDeleteDialog(1);

        } else if (i == R.id.tv_bottom_set_wallpaper) {
            setWallPaper(currentPosition);

        } else {
        }
    }

    private void showConfirmDeleteDialog(int size) {
        dlgConfirmDelete = DialogFactory.createDialog(FileBigImageActivity.this, R.layout.dialog_filedelete)
                .setText(R.id.dialog_title, R.string.alert)
                .setText(R.id.dialog_message, "确认要删除选中的" + size + "项?")
                .setText(R.id.dialog_btn0, R.string.yes_zh)
                .setText(R.id.dialog_btn1, R.string.no_zh);
        if (dlgConfirmDelete != null) {
            ((DialogFactory.MyDialog) dlgConfirmDelete).setBtnOnclickListener(R.id.dialog_btn0, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dlgConfirmDelete.cancel();
                    asyncDelete();
                }
            });
            ((DialogFactory.MyDialog) dlgConfirmDelete).setBtnOnclickListener(R.id.dialog_btn1, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dlgConfirmDelete.cancel();
                }
            });
            dlgConfirmDelete.setCancelable(true);
            dlgConfirmDelete.setCanceledOnTouchOutside(true);
            dlgConfirmDelete.show();
        }

    }

    private void asyncDelete() {
        AsyncTaskwdh<Void, Void, Boolean> task = new AsyncTaskwdh<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                if (dlgDeleteWaiting == null) {
                    dlgDeleteWaiting = DialogFactory.createDialog(FileBigImageActivity.this, R.layout.common_loading_dialog);
                    dlgDeleteWaiting.setCancelable(false);
                    dlgDeleteWaiting.setCanceledOnTouchOutside(false);
                }
                dlgDeleteWaiting.show();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                if (mInfos != null && currentPosition < mInfos.size()) {
                    return FileBrowserUtil.deleteImage(FileBigImageActivity.this, mInfos.get(currentPosition).fileId,
                            mInfos.get(currentPosition).filePath, FileCategoryHelper.FileCategory.Picture);
                }
                return false;

            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (FileBigImageActivity.this.isFinishing()) {
                    return;
                }

                if (dlgDeleteWaiting != null && dlgDeleteWaiting.isShowing()) {
                    dlgDeleteWaiting.dismiss();
                }

                if (mInfos != null && result) {
                    mInfos.remove(currentPosition);
                    showToast(getString(R.string.delete_success));
                    setResult(0, getIntent().putExtra("infos", mInfos));
                    if (mInfos.size() == 0) {
                        adapter.notifyDataSetChanged();
                        finish();
                        return;
                    }

                    if (currentPosition == mInfos.size()) {
                        currentPosition--;
                    }

                    setNameAndPage(currentPosition);
                    adapter.notifyDataSetChanged();
                    mViewPager.setCurrentItem(currentPosition);
                } else {
                    showToast(getString(R.string.delete_faile));
                }
            }

        };

        task.execute();
    }

    private void setWallPaper(int position) {
        String path = null;
        if (mInfos != null && position < mInfos.size() && position >= 0) {
            path = mInfos.get(position).filePath;
        }

        int result = -1;
        try {
            String name = Utils.getMiUiName();
            if (!TextUtils.isEmpty(name) && "V6".equals(name)) {
                result = setWallPaper(path);
            } else {
                result = FileBrowserUtil.setWallPaper(this, new FileInputStream(path), null, false);

                if (result != 1) {
                    result = setWallPaper(path);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        if (result == 1) {
            showToast(getString(R.string.set_wallpaper_success));
        } else {
            showToast(getString(R.string.set_wallpaper_faile));
        }
    }

    private int setWallPaper(String path) {
        if (screenWidth == -1 || screenHeight == -1) {
            getScreenWH();
        }

        Options bfo = new Options();
        bfo.inJustDecodeBounds = true;
        bfo.inDither = false;
        bfo.inPreferredConfig = Bitmap.Config.RGB_565;

        BitmapFactory.decodeFile(path, bfo);

        bfo.inSampleSize = Utils.calculateInSampleSize(bfo, screenWidth, screenHeight);
        bfo.inJustDecodeBounds = false;

        Bitmap b = BitmapFactory.decodeFile(path, bfo);

        return FileBrowserUtil.setWallPaper(this, null, b, true);
    }

    private void getScreenWH() {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
    }

    private void openFile(int position) {
        if (mInfos != null && mInfos.size() > position) {
            FileInfo fileInfo = mInfos.get(position);
            FileBrowserUtil.openFile(this, fileInfo.filePath, fileInfo.mimeType);
        }
    }

    private void showToast(String msg) {
        Toast.makeText(C.get(), msg, Toast.LENGTH_SHORT).show();
    }

}
