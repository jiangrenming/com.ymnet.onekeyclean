package com.ymnet.onekeyclean.cleanmore.wechat.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ymnet.onekeyclean.R;


public class ListViewDialog extends Dialog {
    private Context context;
    private TextView tv_title;
    private ListView lv_show;
    private BaseAdapter adapter;
    private OnItemClickListener click;
    public ListViewDialog(Context context) {
        super(context, R.style.DialogTheme);
        this.context = context;
    }
    public ListViewDialog(Context context, BaseAdapter adapter, OnItemClickListener click) {
        super(context, R.style.DialogTheme);
        this.context = context;
        this.click = click;
        this.adapter = adapter;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_listview);
        initView();
    }
    private void initView() {
        tv_title = (TextView) findViewById(R.id.tv_dialog_title);
        lv_show = (ListView) findViewById(R.id.lv_show);
        lv_show.setOnItemClickListener(click);


        lv_show.setAdapter(adapter);
    }
    public void setCleanTitle(String str) {
        if (!TextUtils.isEmpty(str))
            tv_title.setText(str);
    }
    public void setAdapter(BaseAdapter adapter) {
        lv_show.setAdapter(adapter);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        lv_show.setOnItemClickListener(listener);
    }
}
