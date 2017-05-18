package com.ymnet.onekeyclean.cleanmore.wechat.topic;/*
package com.example.baidumapsevice.wechat.topic;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.baidumapsevice.constants.TopicConstants;
import com.facebook.common.util.UriUtil;
import com.market2345.R;
import com.market2345.applist.activity.TitleBarActivity;
import com.market2345.base.C;
import com.market2345.common.util.Utils;
import com.market2345.framework.http.Call;
import com.market2345.framework.http.Callback;
import com.market2345.framework.http.MHttp;
import com.market2345.framework.http.bean.Response;
import com.market2345.temp.TApier;
import com.market2345.temp.mapper.TopicResponseInfoMapper;
import com.market2345.temp.model.TplTopicEntity;
import com.market2345.topic.model.TopicInfo;
import com.market2345.topic.model.TopicResponseInfo;

public class TopicActivity extends TitleBarActivity implements OnClickListener, TopicExpandable {

    private TextView tvTopTitle;
    private ExpandableListView lvList;
    private ImageView ivTopic;


    private BaseExpandableListAdapter adapter;

    private int topicId;
    private int curTemplate;

    private int from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);
        from = getIntent().getIntExtra("from",-1);
        initView();
        initListener();
        loadDatas();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_btn:
                finish();
                break;
            case R.id.ll_loaded_fail:
                if (!Utils.isNetworkAvailable(this)) {
                    Toast.makeText(C.get(), "网络异常,请稍后再试", Toast.LENGTH_SHORT).show();
                    return;
                }

                loadDatas();
                break;
            default:
                break;
        }
    }


    @Override
    public void ExpandGroup() {
        if (lvList != null && adapter != null) {
            for (int i = 0, size = adapter.getGroupCount(); i < size; i++) {
                lvList.expandGroup(i);
            }
        }
    }

    private void initView() {
        topicId = getIntent().getIntExtra(TopicInfo.TOPIC_ID, -1);
        curTemplate = getIntent().getIntExtra(TopicInfo.TOPIC_TEMPLATE, -1);
        from = getIntent().getIntExtra(TopicConstants.FROM_KEY,-1);
        if (topicId == -1 || curTemplate == -1) {
            Toast.makeText(getApplicationContext(), "主题不存在!", Toast.LENGTH_SHORT).show();
            finish();
        }

        tvTopTitle = (TextView) findViewById(R.id.page_title);
        tvTopTitle.setText(""); // fix BUG #10102
        lvList = (ExpandableListView) findViewById(R.id.lv_list);
        initLoadingView();


        LinearLayout header = (LinearLayout) getLayoutInflater().inflate(R.layout.topic_list_header, lvList, false);
        ivTopic = (ImageView) header.findViewById(R.id.iv_topic);

        lvList.addHeaderView(header, null, false);

        View v = getLayoutInflater().inflate(R.layout.topic_list_footer, lvList, false);
        lvList.addFooterView(v);
    }

    private View fl_loading, pb_loading, ll_loaded_fail;

    private void initLoadingView() {
        fl_loading = findViewById(R.id.fl_loading);
        pb_loading = findViewById(R.id.pb_loading);
        ll_loaded_fail = findViewById(R.id.ll_loaded_fail);
    }

    private void initListener() {
        findViewById(R.id.left_btn).setOnClickListener(this);
        findViewById(R.id.ll_loaded_fail).setOnClickListener(this);
        lvList.setOnGroupClickListener(new OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });

    }

    private void loadDatas() {
        lvList.setVisibility(View.GONE);
        showLoading();
        Call<Response<TplTopicEntity>> call = TApier.get().getTopicInfo(topicId);
        call.enqueue(new Callback<Response<TplTopicEntity>>() {
            @Override
            public void onResponse(Call<Response<TplTopicEntity>> call, Response<TplTopicEntity> response) {
                if (TopicActivity.this.isFinishing()) {
                    return;
                }
                TopicResponseInfo info = new TopicResponseInfoMapper().transform(response);
                if (MHttp.responseOK(response.getCode()) && info.list != null) {
                    initTopic(info.list);
                } else {
                    showError();
                }
            }

            @Override
            public void onFailure(Call<Response<TplTopicEntity>> call, Throwable t) {
                showError();
            }
        });

    }

    private void initTopic(TopicInfo info) {
        tvTopTitle.setText(info.title);

//		DisplayImageOptions mOptions = new DisplayImageOptions
//				.Builder()
//				.showImageForEmptyUri(R.drawable.topic_image_default_big)
//				.showImageOnFail(R.drawable.topic_image_default_big)
//				.showImageOnLoading(R.drawable.topic_image_default_big)
//				.cacheInMemory(true)
//				.bitmapConfig(Config.RGB_565)
//				.considerExifParams(true)
//				.build();
//		ImageLoader.getInstance().displayImage(info.img_url, ivTopic, mOptions);
        ivTopic.setImageURI(UriUtil.parse(info.banner_img_url));

        if (curTemplate == TopicInfo.TEMPLATE_1_GRID) {
            if(from>0){
                adapter = new TopicExpandableListAdapter(this, this, info.chapter, topicId).setFrom(from);
            }else{
                adapter = new TopicExpandableGridAdapter(this, this, info.chapter, topicId);

            }
        } else {
            adapter = new TopicExpandableListAdapter(this, this, info.chapter, topicId).setFrom(from);
        }


        lvList.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        lvList.setVisibility(View.VISIBLE);
        hideLoading();
    }

    private void showError() {
        lvList.setVisibility(View.GONE);
        showLoadingFail();

    }

    private void showLoadingFail() {
        try {
            checkLoadingView();
        } catch (Exception e) {
//            e.printStackTrace();
            return;
        }
        fl_loading.setVisibility(View.VISIBLE);
        pb_loading.setVisibility(View.GONE);
        ll_loaded_fail.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        try {
            checkLoadingView();
        } catch (Exception e) {
//            e.printStackTrace();
            return;
        }
        fl_loading.setVisibility(View.VISIBLE);
        pb_loading.setVisibility(View.VISIBLE);
        ll_loaded_fail.setVisibility(View.GONE);
    }

    private void hideLoading() {
        try {
            checkLoadingView();
        } catch (Exception e) {
//            e.printStackTrace();
            return;
        }
        fl_loading.setVisibility(View.GONE);
        pb_loading.setVisibility(View.GONE);
        ll_loaded_fail.setVisibility(View.GONE);
    }

    private void checkLoadingView() throws Exception {
        if (fl_loading == null || pb_loading == null || ll_loaded_fail == null) {
            throw new IllegalArgumentException("loading view has null");

        }
    }
}
*/
