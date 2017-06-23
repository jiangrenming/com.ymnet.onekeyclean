package com.ymnet.onekeyclean.cleanmore.fragment.filemanager;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.filebrowser.FileBrowserUtil;
import com.ymnet.onekeyclean.cleanmore.fragment.filemanager.adapter.FileManagerAdapter;
import com.ymnet.onekeyclean.cleanmore.fragment.filemanager.base.BaseFragment;
import com.ymnet.onekeyclean.cleanmore.utils.Util;
import com.ymnet.onekeyclean.cleanmore.wechat.listener.RecyclerViewClickListener;
import com.ymnet.onekeyclean.cleanmore.widget.LinearLayoutItemDecoration;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/20.
 */

public class FileManagerFragment extends BaseFragment {
    private RecyclerView recyclerView;
    private TextView textView;
    private FileManagerAdapter adapter;
    private ArrayList<BaseFragment> fragments;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.file_manager_fragment, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.file_manager_recyclerview);
        textView = (TextView) view.findViewById(R.id.file_manager_size);
        initSDCardSize();
        Context context = getActivity().getApplicationContext();
        initFragment();
        adapter = new FileManagerAdapter(context);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new LinearLayoutItemDecoration(context, LinearLayoutItemDecoration.HORIZONTAL_LIST));
        adapter.setmRecyclerViewClickListener(new RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                forwardSendPage(fragments.get(position));
            }

            @Override
            public void selectState(long selectSize, boolean flag, int position) {

            }

            @Override
            public void selectButton(Map<Integer, Boolean> weChatInfos, int position) {

            }
        });
        recyclerView.setAdapter(adapter);
        return view;
    }

    public void initSDCardSize() {
        FileBrowserUtil.SDCardInfo info = FileBrowserUtil.getSDCardInfo();
        String free = Util.formatFileSizeToPic((info.total - info.free));
        String total = Util.formatFileSizeToPic(info.total);
        textView.setText("已用:" + free + "/" + total);
    }

    public void initFragment() {
        fragments = new ArrayList<>();
        fragments.add(new DocumentsFragment());
        fragments.add(new PictureFragment());
        fragments.add(new MusicFragment());
        fragments.add(new VideoFragment());
        fragments.add(new ApkFragment());
        fragments.add(new ZipFragment());
    }
}
