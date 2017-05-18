package com.ymnet.onekeyclean.cleanmore;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.junk.SilverActivity;
import com.ymnet.onekeyclean.cleanmore.qq.activity.QQActivity;
import com.ymnet.onekeyclean.cleanmore.wechat.WeChatActivity;
import com.ymnet.onekeyclean.cleanmore.widget.WaveHelper;
import com.ymnet.onekeyclean.cleanmore.widget.WaveView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private Button     mWeChat;
    private Button     mJunk;
    private WaveHelper mWaveHelper;

    private int mBorderColor = Color.parseColor("#44FFFFFF");
    private int mBorderWidth = 10;
    private GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }

    private void initView() {
        WaveView waveView = (WaveView) findViewById(R.id.wave);
        waveView.setBorder(mBorderWidth, mBorderColor);
        /*waveView.setWaveColor(
                Color.parseColor("#88b8f1ed"),//
                Color.parseColor("#b8f1ed"));//318cfc
        mBorderColor = Color.parseColor("#b8f1ed");*/
        waveView.setWaveColor(
                Color.parseColor("#318cfc"),//
                Color.parseColor("#99318cfc"));//318cfc
        mBorderColor = Color.parseColor("#ffffff");
        waveView.setLevel(0.6f);
        waveView.setBorder(mBorderWidth, mBorderColor);
        mWaveHelper = new WaveHelper(waveView);

        mGridView = (GridView) findViewById(R.id.home_gv);

        final ArrayList<ManagementMode> data = new ArrayList<>();
        data.add(new ManagementMode(R.string.junk_clean, R.drawable.management_cleanicon, SilverActivity.class));
        data.add(new ManagementMode(R.string.wechat_clean, R.drawable.wechat, WeChatActivity.class));
        data.add(new ManagementMode(R.string.qq_clean, R.drawable.qq_cleanicon, QQActivity.class));
        final GridViewAdapter adapter = new GridViewAdapter(this, data);
        mGridView.setAdapter(adapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ManagementMode mode = data.get(position);

                startActivity(new Intent(getApplicationContext(), mode.activityClass).putExtra("from", 1));

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWaveHelper.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWaveHelper.start();
    }

    class ManagementMode {
        private String                    title;
        private int                       iconId;
        private Class<? extends Activity> activityClass;

        public ManagementMode(int title, int iconId, Class<? extends Activity> activityClass) {
            this.title = getString(title);
            this.iconId = iconId;
            this.activityClass = activityClass;
        }
    }

    class GridViewAdapter extends BaseAdapter {
        private List<ManagementMode> data;
        private Context              context;
        private LayoutInflater       inflater;

        public GridViewAdapter(Context context, List<ManagementMode> data) {
            this.context = context;
            this.data = data;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return data == null ? 0 : data.size();
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

            convertView = inflater.inflate(R.layout.main_gridview_item, null);
            ImageView iv_icon = (ImageView) convertView.findViewById(R.id.iv_cleanicon);
            //            View iv_new_tools = convertView.findViewById(R.id.iv_new_tools);
            TextView tv = (TextView) convertView.findViewById(R.id.tv_cleanterm);
            ManagementMode mode = (ManagementMode) getItem(position);
            //            checkShowNewTools(mode, iv_new_tools);
            tv.setText(mode.title);
            iv_icon.setImageResource(mode.iconId);
            return convertView;
        }

    }
}