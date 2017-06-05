package com.ymnet.onekeyclean.cleanmore.fragment.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.commonlibrary.systemmanager.SystemMemory;
import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.customview.RecyclerViewPlus;
import com.ymnet.onekeyclean.cleanmore.utils.C;

import static com.example.commonlibrary.systemmanager.SystemMemory.getAvailMemorySize;


/**
 * Created by MajinBuu on 2017/6/2 0002.
 *
 * @overView ${todo}.
 */

public class HomeAdapter extends RecyclerViewPlus.HeaderFooterItemAdapter implements View.OnClickListener {

    private Context      mContext;
    private RecyclerInfo mData;


    public HomeAdapter(Context context, RecyclerInfo recyclerInfo) {
        this.mContext = context;
        this.mData = recyclerInfo;
    }

   /* @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = View.inflate(mContext, R.layout.fragment_item0, null);
            return new FunctionHeadHolder(view);

        } else if (viewType == 1) {
            View view = View.inflate(mContext, R.layout.function_home_item_layout, null);
            return new FunctionHolder(view);
        }
        return null;
    }*/

   /* @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
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
        } else if (holder instanceof FunctionHeadHolder) {
            FunctionHeadHolder headHolder = (FunctionHeadHolder) holder;
            String strSize = mData.sizeAndUnit[0];
            String strUnit = mData.sizeAndUnit[1];

            headHolder.size.setText(strSize);
            headHolder.unit.setText(strUnit);
            headHolder.wave.setProgressValue(mData.waveLevel);
            headHolder.progressButton.setOnClickListener(this);
        }

    }*/

    @Override
    public int getContentItemCount() {
        return mData.FunctionTitle.length;
    }

    @Override
    protected void onBindContentViewHolder(ContentViewHolder holder, int position) {
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
        } /*else if (holder instanceof FunctionHeadHolder) {
            FunctionHeadHolder headHolder = (FunctionHeadHolder) holder;
            String strSize = mData.sizeAndUnit[0];
            String strUnit = mData.sizeAndUnit[1];

            headHolder.size.setText(strSize);
            headHolder.unit.setText(strUnit);
            headHolder.wave.setProgressValue(mData.waveLevel);
            headHolder.progressButton.setOnClickListener(this);
        }*/
    }

    @Override
    public ContentViewHolder onCreateContentView(ViewGroup parent, int viewType) {
      /*  if (viewType == 0) {
            View view = View.inflate(mContext, R.layout.fragment_item0, null);
            return new FunctionHeadHolder(view);

        } else if (viewType == 1) {
            View view = View.inflate(mContext, R.layout.function_home_item_layout, null);
            return new FunctionHolder(view);
        }
        return null;*/
        View view = View.inflate(mContext, R.layout.function_home_item_layout, null);
        return new FunctionHolder(view);
    }

    @Override
    public void onClick(View v) {

    }

    private int getUsedMemory() {
        return (int) (100 * ((float) getAvailMemorySize(C.get()) / SystemMemory.getTotalMemorySize(C.get())));
    }

    /*@Override
    public int getItemViewType(int position) {
        if (mData.FunctionTitle[position].equals("#")) {
            return 0;
        } else {
            return 1;
        }
    }*/

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class FunctionHolder extends RecyclerViewPlus.HeaderFooterItemAdapter.ContentViewHolder {
        private ImageView mIcon;
        private TextView  mDesc;
        private TextView  mTitle;

        public FunctionHolder(View itemView) {
            super(itemView);
            mIcon = (ImageView) itemView.findViewById(R.id.function_icon);
            mDesc = (TextView) itemView.findViewById(R.id.tv_function_desc);
            mTitle = (TextView) itemView.findViewById(R.id.tv_function_title);
        }
    }

   /* private class FunctionHeadHolder extends RecyclerViewPlus.HeaderFooterItemAdapter.ContentViewHolder {

        private SGTextView      size;
        private SGTextView      unit;
        private WaveLoadingView wave;
        private ProgressButton  progressButton;

        public FunctionHeadHolder(View itemView) {
            super(itemView);
            size = (SGTextView) itemView.findViewById(R.id.tv_homehead_size);
            unit = (SGTextView) itemView.findViewById(R.id.tv_homehead_unit);
            wave = (WaveLoadingView) itemView.findViewById(R.id.wlv_home);
            progressButton = (ProgressButton) itemView.findViewById(R.id.pb_ram_prompt);

        }

    }*/

//    public  void setData(RecyclerInfo infos){
//        this.mData = infos;
//        this.notifyDataSetChanged();
//    }
}
