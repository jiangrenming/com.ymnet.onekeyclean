package com.ymnet.onekeyclean.cleanmore.filebrowser;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.ImmersiveActivity;
import com.ymnet.onekeyclean.cleanmore.utils.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class FileDetailsActivityPaths extends ImmersiveActivity implements View.OnClickListener {
    private String title;
    private List<String> paths;
    private ImageButton ibtn_left_back;
    private TextView page_title;
    private ListView lv_file;
    private BaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_details_activity_paths);
        Intent i = getIntent();
        title = i.getStringExtra("title_name");
        paths = i.getStringArrayListExtra("dirs");
        if (paths == null || paths.size() == 0) {
            this.finish();//如果为空 也没必须要继续了
            return;
        }
        initView();
    }

    private void initView() {
        ibtn_left_back = (ImageButton) findViewById(R.id.ibtn_left_back);
        page_title = (TextView) findViewById(R.id.page_title);
        if (!TextUtils.isEmpty(title)) {
            page_title.setText(title);
        } else {
            page_title.setText(paths.get(0));
        }
        ibtn_left_back.setOnClickListener(this);
        lv_file = (ListView) findViewById(R.id.lv_file);
        List<File> data = new ArrayList<File>();
        adapter = new FilesAdapter(this, data);
        lv_file.setAdapter(adapter);
        for (String path : paths) {
            File f = new File(path);
            if (f.exists()) {
                data.add(new File(path));
            }
        }

        adapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.ibtn_left_back) {
            this.finish();

        }
    }

    class FilesAdapter extends BaseAdapter {
        private List<File> data;
        private Context mContext;

        FilesAdapter(Context context, List<File> data) {
            this.data = data;
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHold hold;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_item_files_details, null);
                hold = new ViewHold();
                hold.im_type = (ImageView) convertView.findViewById(R.id.im_file_type);
                hold.tv_name = (TextView) convertView.findViewById(R.id.tv_filename);
                hold.tv_size = (TextView) convertView.findViewById(R.id.tv_filesize);
                hold.tv_path = (TextView) convertView.findViewById(R.id.tv_path);
                convertView.setTag(hold);
            } else {
                hold = (ViewHold) convertView.getTag();
            }
            File myFile = data.get(position);
            if (myFile.isFile()) {
                hold.im_type.setImageResource(R.drawable.nullfile_icon);
            } else {
                hold.im_type.setImageResource(R.drawable.big_file_folder);
            }
            hold.tv_name.setText(myFile.getName());
            hold.tv_size.setText(Formatter.formatFileSize(mContext, Util.getFileFolderTotalSize(myFile)));
            String path = myFile.getAbsolutePath();
            if (!TextUtils.isEmpty(path) && path.length() >= Util.getRootPath().length()) {
                hold.tv_path.setText(myFile.getAbsolutePath().substring(Util.getRootPath().length()));
            }
            return convertView;
        }


    }

    static class ViewHold {
        public ImageView im_type;
        TextView tv_name, tv_path, tv_size;
    }
}