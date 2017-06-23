package com.ymnet.onekeyclean.cleanmore.fragment.filemanager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.fragment.filemanager.base.FileManagerInfo;
import com.ymnet.onekeyclean.cleanmore.wechat.listener.RecyclerViewClickListener;

/**
 * Created by Administrator on 2017/6/21.
 */

public class FileManagerAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private RecyclerViewClickListener mRecyclerViewClickListener;

    public FileManagerAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.function_home_item_layout, null);
        return new FileManagerHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof FileManagerHolder) {
            FileManagerHolder mHolder = (FileManagerHolder) holder;
            mHolder.mIcon.setImageResource(FileManagerInfo.img[position]);
            mHolder.mTitle.setText(FileManagerInfo.title[position]);
            mHolder.mDesc.setText(FileManagerInfo.dec[position]);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mRecyclerViewClickListener != null)
                        mRecyclerViewClickListener.onClick(v, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return FileManagerInfo.title.length;
    }

    private class FileManagerHolder extends RecyclerView.ViewHolder {
        private ImageView mIcon;
        private TextView mDesc;
        private TextView mTitle;

        public FileManagerHolder(final View itemView) {
            super(itemView);
            mIcon = (ImageView) itemView.findViewById(R.id.function_icon);
            mDesc = (TextView) itemView.findViewById(R.id.tv_function_desc);
            mTitle = (TextView) itemView.findViewById(R.id.tv_function_title);

        }
    }

    public void setmRecyclerViewClickListener(RecyclerViewClickListener mRecyclerViewClickListener) {
        this.mRecyclerViewClickListener = mRecyclerViewClickListener;
    }
}
