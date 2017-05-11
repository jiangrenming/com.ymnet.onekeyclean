package com.ymnet.onekeyclean.cleanmore.filebrowser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.ImmersiveActivity;
import com.ymnet.onekeyclean.cleanmore.cacheclean.Util;
import com.ymnet.onekeyclean.cleanmore.common.ApplicationUtils;
import com.ymnet.onekeyclean.cleanmore.constants.FileScanState;
import com.ymnet.onekeyclean.cleanmore.datacenter.MarketObservable;
import com.ymnet.onekeyclean.cleanmore.datacenter.MarketObserver;
import com.ymnet.onekeyclean.cleanmore.filebrowser.FileCategoryHelper.FileCategory;

import java.lang.ref.WeakReference;
import java.util.HashMap;

public class FileCategoryActivity extends ImmersiveActivity implements MarketObserver, OnClickListener
{
    
    private CategoryBar mCategoryBar;
    
    private FileControl mControl;
    
    private HashMap<FileCategory, Integer> categoryIndex = new HashMap<FileCategory, Integer>();
    
    private ScannerReceiver mScannerReceiver;
    
    private TextView title;
    
    private ImageView back;
    
    private LinearLayout noPage;
    
    private LinearLayout categoryPage;
    
    private FileBrowserUtil.SDCardInfo sdCardInfo;
    
    private LinearLayout CartegoryPic;
    
    private LinearLayout CartegoryMusic;
    
    private LinearLayout CartegoryVideo;
    
    private LinearLayout CartegoryDoc;
    
    private LinearLayout CartegoryApk;
    
    private LinearLayout CartegoryZip;
    
    private boolean refresh = false;
    
    private boolean hasPopShowed = false;

    private Handler handler = new MyHandler(this);

    private static class MyHandler extends Handler {
        WeakReference<FileCategoryActivity> theActivity;
        public MyHandler(FileCategoryActivity activity) {
            theActivity = new WeakReference<FileCategoryActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            FileCategoryActivity activity = theActivity.get();
            if (activity != null && !activity.isFinishing()) {
                if (msg.what == FileScanState.SCAN_FINSH) {
                    activity.refreshCategoryInfo(FileScanState.SCAN_FINSH);
                    if (activity.mCategoryBar.getVisibility() == View.VISIBLE) {
                        activity.mCategoryBar.startAnimation();
                    }
                } else if (msg.what == FileScanState.DATA_CHANGE) {
                    activity.refresh = true;
                } else if (msg.what == FileScanState.SCAN_PROVIDER_FINSH) {
                    activity.refreshCategoryInfo(FileScanState.SCAN_PROVIDER_FINSH);
                } else if (msg.what == FileScanState.SCAN_SDCARD_ING) {
                    activity.refreshCategoryInfo(FileScanState.SCAN_SDCARD_ING);
                }
            }
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filebrowser_main_activity);
        
        mControl = FileControl.getInstance(getApplicationContext());
        mControl.addObserver(this);
        
        title = (TextView) findViewById(R.id.tv_base_title);
        back = (ImageView) findViewById(R.id.iv_top_back);
        noPage = (LinearLayout) findViewById(R.id.sd_not_available_page);
        categoryPage = (LinearLayout) findViewById(R.id.category_page);
        
        CartegoryPic = (LinearLayout) findViewById(R.id.category_picture);
        CartegoryMusic = (LinearLayout) findViewById(R.id.category_music);
        CartegoryVideo = (LinearLayout) findViewById(R.id.category_video);
        CartegoryDoc = (LinearLayout) findViewById(R.id.category_document);
        CartegoryApk = (LinearLayout) findViewById(R.id.category_apk);
        CartegoryZip = (LinearLayout) findViewById(R.id.category_zip);
        CartegoryPic.setOnClickListener(this);
        CartegoryMusic.setOnClickListener(this);
        CartegoryVideo.setOnClickListener(this);
        CartegoryDoc.setOnClickListener(this);
        CartegoryApk.setOnClickListener(this);
        CartegoryZip.setOnClickListener(this);
        
        title.setText(R.string.file_manager);
        back.setOnClickListener(this);
        updateUI();
        registerScannerReceiver();
    }
    
    @Override
    protected void onResume()
    {
        if (refresh)
        {
            refreshCategoryInfo(FileScanState.DATA_CHANGE);
        }
        refresh = false;
        super.onResume();
    }
    
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        
        FileCategoryActivity.this.unregisterReceiver(mScannerReceiver);
        mControl.close();
        
    }
    
    @Override
    public void onClick(View view)
    {
        int i = view.getId();
        if (i == R.id.iv_top_back) {
            this.finish();

        } else if (i == R.id.category_picture) {
            startActivity(new Intent(this, FilePicDirActivity.class));

        } else if (i == R.id.category_music) {
            startActivity(new Intent(this, FileMusicActivity.class));

        } else if (i == R.id.category_video) {
            startActivity(new Intent(this, FileVideoActivity.class));

        } else if (i == R.id.category_document) {
            startActivity(new Intent(this, FileDocumentsActivity.class));

        } else if (i == R.id.category_apk) {
            startActivity(new Intent(this, FileApkActivity.class));

        } else if (i == R.id.category_zip) {
            startActivity(new Intent(this, FilePackageActivity.class));

        } else {
        }
    }
    
    @Override
    public void update(MarketObservable observable, Object data)
    {
        Message msg = Message.obtain();
        if (data instanceof Integer)
        {
            if ((Integer) data == FileScanState.SCAN_FINSH)
            {
                msg.what = FileScanState.SCAN_FINSH;
                handler.sendMessage(msg);// 注意：此处要用handler来更新页面内容
            }
            else if ((Integer) data == FileScanState.DATA_CHANGE)
            {
                msg.what = FileScanState.DATA_CHANGE;
                handler.sendMessage(msg);
            }
            else if ((Integer) data == FileScanState.SCAN_PROVIDER_FINSH)
            {
                msg.what = FileScanState.SCAN_PROVIDER_FINSH;
                handler.sendMessage(msg);
            }
            else if ((Integer) data == FileScanState.SCAN_SDCARD_ING)
            {
                msg.what = FileScanState.SCAN_SDCARD_ING;
                handler.sendMessage(msg);
            }
        }
    }

    /**
     * 检查sd卡是否存在 是否可读
     */
    private void updateUI()
    {
        boolean sdCardReady = FileBrowserUtil.isSDCardReady();
        if (sdCardReady)
        {
            categoryPage.setVisibility(View.VISIBLE);
            noPage.setVisibility(View.GONE);
            setupCategoryInfo();
        }
        else
        {
            categoryPage.setVisibility(View.GONE);
            noPage.setVisibility(View.VISIBLE);
        }
    }
    
    private void setupCategoryInfo()
    {
        mCategoryBar = (CategoryBar) findViewById(R.id.category_bar);
        int[] imgs = new int[] { R.drawable.category_bar_picture, R.drawable.category_bar_music, R.drawable.category_bar_video, R.drawable.category_bar_document, R.drawable.category_bar_apk,
                R.drawable.category_bar_zip, R.drawable.category_bar_other };
        
        for (int i = 0; i < imgs.length; i++)
        {
            mCategoryBar.addCategory(imgs[i]);
        }
        
        for (int i = 0; i < FileControl.sCategories.length; i++)
        {
            categoryIndex.put(FileControl.sCategories[i], i);
        }
        mControl.scan();
    }
    
    public void refreshCategoryInfo(int flag)
    {
        sdCardInfo = FileBrowserUtil.getSDCardInfo();
        if (sdCardInfo != null)
        {
            mCategoryBar.setFullValue(sdCardInfo.total);
            setTextView(R.id.sd_card_capacity, getString(R.string.sd_card_size, ApplicationUtils.formatFileSizeToString(sdCardInfo.total)));
            setTextView(R.id.sd_card_available, getString(R.string.sd_card_available, ApplicationUtils.formatFileSizeToString(sdCardInfo.free)));
        }
        // the other category size should include those files didn't get
        // scanned.
        long size = 0;
        for (FileCategory fc : FileControl.sCategories)
        {
            FileControl.CategoryInfo categoryInfo = mControl.getCategoryInfos().get(fc);
            setCategoryCount(fc, categoryInfo.count,flag);
            // other category size should be set separately with calibration
            if (fc == FileCategory.Other) {
            	continue;
            }
            
            setCategorySize(fc, categoryInfo.size);
            setCategoryBarValue(fc, categoryInfo.size);
            size += categoryInfo.size;
        }
        
        if (sdCardInfo != null)
        {
            long otherSize = sdCardInfo.total - sdCardInfo.free - size;
            setCategorySize(FileCategory.Other, otherSize);
            setCategoryBarValue(FileCategory.Other, otherSize);
            
            if (FileScanState.SCAN_FINSH == flag && !hasPopShowed) {
            	float f = ((float) sdCardInfo.free) / sdCardInfo.total;
            	long dur = Util.getLaseCleanDate(this, System.currentTimeMillis());
            	int day = (int) (dur / (24 * 60 * 60));
            	if (f < 0.2 && day > 3) {
            		hasPopShowed = true;
            		
            		// pop
            		View v = View.inflate(this, R.layout.file_pop_tips, null);
            		PopupWindow pop = new PopupWindow(v, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            		pop.setFocusable(true);
            		pop.setOutsideTouchable(true);
            		pop.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_search_press));
            		
            		int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            		int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            		v.measure(w, h);
            		
            		int xoff = mCategoryBar.getWidth() - v.getMeasuredWidth();
            		int yoff = mCategoryBar.getHeight() + v.getMeasuredHeight();

            		pop.showAsDropDown(mCategoryBar, xoff, -yoff);
            	}
            }
        }
    }
    
    private void setCategoryCount(FileCategory fc, long count,int flag)
    {
        int id = getCategoryCountId(fc);
        if (id == 0)
            return;
        String showString;
        if(count==0&&flag!=FileScanState.DATA_CHANGE&&flag!=FileScanState.SCAN_FINSH){
            showString=getString(R.string.file_category_count_default);
        }else{
            showString=getString(R.string.file_account_in_managerment,count+"");
        }
        setTextView(id, showString);
    }
    
    private void setTextView(int id, String t)
    {
        TextView text = (TextView) findViewById(id);
        text.setText(t);
    }
    
    private void setCategoryBarValue(FileCategory f, long size)
    {
        if (mCategoryBar == null)
        {
            mCategoryBar = (CategoryBar) findViewById(R.id.category_bar);
        }
        mCategoryBar.setCategoryValue(categoryIndex.get(f), size);
    }
    
    private void setCategorySize(FileCategory fc, long size)
    {
        int txtId = 0;
        int resId = 0;
        switch (fc)
        {
            case Music:
                txtId = R.id.category_legend_music;
                resId = R.string.category_music;
                break;
            case Video:
                txtId = R.id.category_legend_video;
                resId = R.string.category_video;
                break;
            case Picture:
                txtId = R.id.category_legend_picture;
                resId = R.string.category_picture;
                break;
            case Doc:
                txtId = R.id.category_legend_document;
                resId = R.string.category_document;
                break;
            case Zip:
                txtId = R.id.category_legend_zip;
                resId = R.string.category_zip;
                break;
            case Apk:
                txtId = R.id.category_legend_apk;
                resId = R.string.category_apk;
                break;
            case Other:
                txtId = R.id.category_legend_other;
                resId = R.string.category_other;
                break;
        }
        
        if (txtId == 0 || resId == 0)
            return;
        if(size<=0)size=0;
        setTextView(txtId, getString(resId) + ":" + FileBrowserUtil.convertStorage(size));
    }
    
    private static int getCategoryCountId(FileCategory fc)
    {
        switch (fc)
        {
            case Picture:
                return R.id.category_picture_count;
            case Music:
                return R.id.category_music_count;
            case Video:
                return R.id.category_video_count;
            case Doc:
                return R.id.category_document_count;
            case Apk:
                return R.id.category_apk_count;
            case Zip:
                return R.id.category_zip_count;
        }
        
        return 0;
    }
    
    private class ScannerReceiver extends BroadcastReceiver
    {
        
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            // handle intents related to external storage
            if (action.equals(Intent.ACTION_MEDIA_SCANNER_FINISHED) || action.equals(Intent.ACTION_MEDIA_MOUNTED) || action.equals(Intent.ACTION_MEDIA_UNMOUNTED))
            {
                updateUI();
            }
        }
    }
    
    private void registerScannerReceiver()
    {
        mScannerReceiver = new ScannerReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        intentFilter.addDataScheme("file");
        FileCategoryActivity.this.registerReceiver(mScannerReceiver, intentFilter);
    }
    
    
    
}
