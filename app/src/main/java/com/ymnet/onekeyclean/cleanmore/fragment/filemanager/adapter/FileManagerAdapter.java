package com.ymnet.onekeyclean.cleanmore.fragment.filemanager.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.customview.RecyclerViewPlus;
import com.ymnet.onekeyclean.cleanmore.fragment.filemanager.base.FileManagerInfo;
import com.ymnet.onekeyclean.cleanmore.wechat.listener.RecyclerViewClickListener;

/**
 * Created by Administrator on 2017/6/21.
 */

public class FileManagerAdapter extends RecyclerViewPlus.HeaderFooterItemAdapter {
    private Context mContext;
    private RecyclerViewClickListener mRecyclerViewClickListener;

    public FileManagerAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getContentItemCount() {
        return FileManagerInfo.title.length;
    }

    @Override
    protected void onBindContentViewHolder(ContentViewHolder holder, final int position) {
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
    public ContentViewHolder onCreateContentView(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.function_home_item_layout, null);
        return new FileManagerHolder(view);
    }

    private class FileManagerHolder extends ContentViewHolder {
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
