package com.ymnet.onekeyclean.cleanmore.filebrowser;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.ImmersiveActivity;
import com.ymnet.onekeyclean.cleanmore.junk.adapter.FileAdapter;
import com.ymnet.onekeyclean.cleanmore.utils.DateUtils;
import com.ymnet.onekeyclean.cleanmore.utils.DisplayUtil;
import com.ymnet.onekeyclean.cleanmore.utils.Util;
import com.ymnet.onekeyclean.cleanmore.wechat.DialogFactory;
import com.ymnet.onekeyclean.cleanmore.wechat.view.ListViewDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FileDetailsActivity extends ImmersiveActivity implements
		OnClickListener, OnItemClickListener {
    private ImageButton ibtn_left_back;

    private ListViewDialog dialogSize;
	private TextView       page_title;
    private ImageButton    ibtn_right;
	private PopupWindow    popWindow;
	private View           contentView;
	private ListView       lv_file;
	private List<File> data = new ArrayList<File>();
	private BaseAdapter adapter;
	private LinearLayout ll_idle;
	private TextView tv_file_root;
	private String rootPath;
	private long size;
	private long date;
    private String titleName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_details);
		Intent i = getIntent();
        titleName=i.getStringExtra("title_name");
		rootPath =i.getStringExtra("dir");
		if (TextUtils.isEmpty(rootPath)){
			this.finish();//如果为空 也没必须要继续了
			return;
		}
		if(!new File(rootPath).exists()){
			this.finish();
			return;//如果目录不存在 也没必要继续了吧
		}
		initView();
		initData();
	}

	private void initView() {
        ibtn_left_back = (ImageButton) findViewById(R.id.ibtn_left_back);
        page_title= (TextView) findViewById(R.id.page_title);
        if(!TextUtils.isEmpty(titleName)){
            page_title.setText(titleName);
        }else{
            page_title.setText(rootPath);
        }
        ibtn_right = (ImageButton) findViewById(R.id.ibtn_right);
        ibtn_left_back.setOnClickListener(this);
        ibtn_right.setOnClickListener(this);
		ll_idle = (LinearLayout) findViewById(R.id.ll_idle);
		tv_file_root = (TextView) findViewById(R.id.tv_file_root);
		lv_file = (ListView) findViewById(R.id.lv_file);

		tv_file_root.setText(new File(rootPath).getName());
		tv_file_root.setTag(rootPath);
		tv_file_root.setOnClickListener(getOnClickListener());
		adapter = new FileAdapter(this, data);
		lv_file.setAdapter(adapter);
		lv_file.setOnItemClickListener(this);
		data.addAll(Arrays.asList(new File(rootPath).listFiles()));
		adapter.notifyDataSetChanged();
	}
	private void initData(){
		File f=new File(rootPath);
		size= Util.getFileFolderTotalSize(f);
		date=f.lastModified();
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int i = arg0.getId();
		if (i == R.id.ibtn_right) {
			if (popWindow == null) {
				contentView = getLayoutInflater().inflate(R.layout.ppw_item, null, false);
				((TextView) contentView.findViewById(R.id.tv_location)).setText(rootPath);

				((TextView) contentView.findViewById(R.id.tv_total_size))
						.setText(Formatter.formatFileSize(this, size));
				((TextView) contentView.findViewById(R.id.tv_date)).setText(DateUtils.long2DateSimple(date));
				popWindow = new PopupWindow(contentView,
						WindowManager.LayoutParams.MATCH_PARENT,
						WindowManager.LayoutParams.WRAP_CONTENT, true);
				popWindow.setAnimationStyle(R.style.ppwAnimation);
				popWindow.setBackgroundDrawable(new BitmapDrawable());
				popWindow.setOutsideTouchable(true);
			}
			if (popWindow.isShowing()) {
				popWindow.dismiss();
			} else {
				popWindow.showAsDropDown(ibtn_right, 0, 0);
			}

		} else if (i == R.id.ibtn_left_back) {
			this.finish();

		} else {
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		File file = data.get(position);
		updateUi(file);
	}

	private void updateUi(File file) {
		if (file.isDirectory()) {
			data.clear();
			data.addAll(Arrays.asList(file.listFiles()));
			adapter.notifyDataSetChanged();
			addTitleFold(file);// 添加顶部文件目录
		} else {
            showOpenFileDialog(file);
		}
	}

    private void showOpenFileDialog(final File file) {
        String[] item=getResources().getStringArray(R.array.file_mime_type);
        // TODO Auto-generated method stub
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,R.layout.textview,R.id.textview,item);
         dialogSize= DialogFactory.createListViewDialog(this, adapter, new OnItemClickListener() {

			 @Override
			 public void onItemClick(AdapterView<?> parent, View view,
									 int position, long id) {
				 // TODO Auto-generated method stub
				 chooseOver(position, file);
				 dialogSize.dismiss();
			 }
		 });
        dialogSize.show();
        dialogSize.setCleanTitle(getString(R.string.please_choose_file_tyle));
        dialogSize.setCanceledOnTouchOutside(true);
    }
    private void chooseOver(int position,File file){
        String type="";
        switch (position){
            case 0://文本
                type="text/plain";
                break;
            case 1://音频
                type="audio/*";
                break;
            case 2://视频
                type="video/*";
                break;
            case 3://图片
                type="image/*";
                break;
            default:
                type="*/*";
                break;
        }
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), type);
        startActivity(intent);
    }


    /**
     * 动态的给顶部文件夹导航条添加一个textview
     * @param file
     */
	private void addTitleFold(final File file) {
		// TODO Auto-generated method stub
		if (file == null)
			return;
		TextView tv = new TextView(this);
		int padding = DisplayUtil.dip2px(this, 5);
		tv.setPadding(padding, padding, padding, padding);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		tv.setText(file.getName());
		tv.setTag(file.getAbsolutePath());

		Drawable drawable = getResources().getDrawable(R.drawable.icon_path_arrow_small);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		tv.setCompoundDrawables(null, null, drawable, null);
		tv.setGravity(Gravity.CENTER_VERTICAL);
		LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT);
		tv.setOnClickListener(getOnClickListener());
		ll_idle.addView(tv, lp1);

	}

	private OnClickListener getOnClickListener() {
		return new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String tagPath = (String) v.getTag();
				// int index=-1;
				int indexOfChild = ll_idle.indexOfChild(v);
				ll_idle.removeViews(indexOfChild, ll_idle.getChildCount()- indexOfChild);
				updateUi(new File(tagPath));

			}
		};
	}
}
