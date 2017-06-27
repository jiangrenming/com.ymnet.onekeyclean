package com.ymnet.onekeyclean.cleanmore.fragment.view;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.commonlibrary.utils.SystemMemory;
import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.customview.RecyclerViewPlus;
import com.ymnet.onekeyclean.cleanmore.utils.C;
import com.ymnet.onekeyclean.cleanmore.wechat.listener.RecyclerViewClickListener;

import static com.example.commonlibrary.utils.SystemMemory.getAvailMemorySize;


/**
 * Created by MajinBuu on 2017/6/2 0002.
 *
 * @overView ${todo}.
 */

public class HomeAdapter extends RecyclerViewPlus.HeaderFooterItemAdapter {

    private Context      mContext;
    private RecyclerInfo mData;
    private View mItem;
    private RecyclerViewClickListener mRecyclerViewClickListener;

    public HomeAdapter(Context context, RecyclerInfo recyclerInfo) {
        this.mContext = context;
        this.mData = recyclerInfo;
    }

    @Override
    public int getContentItemCount() {
        return mData.FunctionTitle.length;
    }

    public void setRecyclerListListener(RecyclerViewClickListener recyclerViewClickListener) {
        this.mRecyclerViewClickListener = recyclerViewClickListener;
    }

    @Override
    protected void onBindContentViewHolder(final ContentViewHolder holder, final int position) {
        if (holder instanceof FunctionHolder) {
            FunctionHolder mHolder = (FunctionHolder) holder;
            mHolder.mIcon.setImageResource(mData.FunctionDrawable[position]);
            mHolder.mTitle.setText(mData.FunctionTitle[position]);
            if (mData.FunctionDesc[position] == R.string.used_memory) {
                String sAgeFormat = C.get().getResources().getString(R.string.used_memory);
                String format = String.format(sAgeFormat, getUsedMemory());
                mHolder.mDesc.setText(format);
            } else {
                mHolder.mDesc.setText(C.get().getString(mData.FunctionDesc[position]));
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("HomeFragment", "点击");
                    mRecyclerViewClickListener.onClick(((FunctionHolder) holder).itemView,position);
                }
            });
        }
    }

    @Override
    public ContentViewHolder onCreateContentView(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.function_home_item_layout, null);
        mItem= view.findViewById(R.id.rl_home_item);

        return new FunctionHolder(view);
    }

    private int getUsedMemory() {
        return 100-(int) (100 * ((float) getAvailMemorySize(C.get()) / SystemMemory.getTotalMemorySize(C.get())));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class FunctionHolder extends RecyclerViewPlus.HeaderFooterItemAdapter.ContentViewHolder {
        private ImageView mIcon;
        private TextView  mDesc;
        private TextView  mTitle;

        public FunctionHolder(final View itemView ) {
            super(itemView);
            mIcon = (ImageView) itemView.findViewById(R.id.function_icon);
            mDesc = (TextView) itemView.findViewById(R.id.tv_function_desc);
            mTitle = (TextView) itemView.findViewById(R.id.tv_function_title);

        }
    }

}
