package com.ymnet.onekeyclean.cleanmore.junk.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.commonlibrary.retrofit2service.bean.NewsInformation;
import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.customview.RecyclerViewPlus;
import com.ymnet.onekeyclean.cleanmore.datacenter.MarketObservable;
import com.ymnet.onekeyclean.cleanmore.datacenter.MarketObserver;
import com.ymnet.onekeyclean.cleanmore.utils.C;
import com.ymnet.onekeyclean.cleanmore.utils.SharedPreferencesUtil;
import com.ymnet.onekeyclean.cleanmore.wechat.listener.RecyclerViewClickListener;
import com.ymnet.onekeyclean.cleanmore.wechat.listener.RecyclerViewScrollListener;

import java.util.List;

/**
 * Created by MajinBuu on 5/24/17.
 */
public class RecommendAdapter extends RecyclerViewPlus.HeaderFooterItemAdapter implements MarketObserver {

    private String TAG = "RecommendAdapter";
    private List<NewsInformation.DataBean> data;
    private final int SINGLE_IMAGE_VIEW         = 0;
    private final int THREE_IMAGE_VIEW          = 1;
    private final int VIDEO_VIEW                = 2;
    private final int FOOT_PROGRESS_HOLDER_VIEW = 3;
    private final int EMPTY_VIEW                = 4;
    private final int FOOT_HOLDER_VIEW          = 5;
    private RecyclerViewClickListener  listener;
    private RecyclerViewScrollListener mScrollListener;
    private LayoutInflater             inflater;
    private String ItemID = "item_id";

    public RecommendAdapter(List<NewsInformation.DataBean> data) {
        this.data = data;
        this.inflater = (LayoutInflater) C.get().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setRecyclerListListener(RecyclerViewClickListener mRecyclerClickListener) {
        this.listener = mRecyclerClickListener;
    }

    public void addScrollListener(RecyclerViewScrollListener recyclerViewScrollListener) {
        this.mScrollListener = recyclerViewScrollListener;
    }

    @Override
    protected int getContentItemViewType(int position) {
        if (data != null) {
            if (data.get(position) == null) {
                return FOOT_PROGRESS_HOLDER_VIEW;
            } else {
                int show_type = data.get(position).getShow_type();
                if (show_type == 3) {
                    return SINGLE_IMAGE_VIEW;
                } else if (show_type == 4) {
                    return THREE_IMAGE_VIEW;
                } else if (show_type == -1) {
                    return FOOT_HOLDER_VIEW;
                } else {
                    return VIDEO_VIEW;
                }
            }
        }
        return EMPTY_VIEW;
    }

    @Override
    public int getContentItemCount() {
        return (data == null || data.isEmpty()) ? 0 : data.size()/* + 1*/;
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        int adapterPosition = holder.getAdapterPosition();
        Log.d("RecommendAdapter", "adapterPosition:" + adapterPosition);
    }

    @Override
    protected void onBindContentViewHolder(final ContentViewHolder holder, final int position) {
        Log.d(TAG, "onBindContentViewHolder:position: " + position);
        if (holder instanceof FootViewHolder) { //底部loadmore界面
            FootViewHolder footHolder = (FootViewHolder) holder;
        } else if (holder instanceof ThreeViewHolder) {
            ThreeViewHolder viewHolder = (ThreeViewHolder) holder;
            NewsInformation.DataBean moreData = data.get(position);

            if (moreData != null) {
                viewHolder.author_name.setText(moreData.getAuthor_name());
                viewHolder.publish_time.setText(moreData.getPublish_time());
                viewHolder.new_title.setText(moreData.getTitle());
                Glide.with(C.get()).load(moreData.getThumbnail_pic_s1()).asBitmap().into(viewHolder.img1);
                Glide.with(C.get()).load(moreData.getThumbnail_pic_s2()).asBitmap().into(viewHolder.img2);
                Glide.with(C.get()).load(moreData.getThumbnail_pic_s3()).asBitmap().into(viewHolder.img3);

                if (SharedPreferencesUtil.getIntFromDefaultSharedPreferences(data.get(position).getId()+"") == moreData.getId()) {
                    changeState(holder, position);
                }
            }
        } else if (holder instanceof OneImagViewHolder) {
            OneImagViewHolder oneImagHolder = (OneImagViewHolder) holder;
            oneImagHolder.author_name.setText(data.get(position).getAuthor_name());
            oneImagHolder.publish_time.setText(data.get(position).getPublish_time());
            oneImagHolder.content.setText(data.get(position).getTitle());
            Glide.with(C.get()).load(data.get(position).getThumbnail_pic_s1()).asBitmap().into(oneImagHolder.img);

            if (SharedPreferencesUtil.getIntFromDefaultSharedPreferences(data.get(position).getId()+"") == data.get(position).getId()) {
                changeState(holder, position);
            }

        } else if (holder instanceof VideoViewHolder) {
            //暂时不做
        } else if (holder instanceof FooterVisibleHolder) {//底部没有数据之后
            FooterVisibleHolder footHolder = (FooterVisibleHolder) holder;
        } else {
            //无数据时 空白页
            EmptyViewHolder emptyHolder = (EmptyViewHolder) holder;
            return;
        }
        if (listener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(v, position);
                    changeState(holder, position);
                }
            });
        }

    }

    private void changeState(ContentViewHolder holder, int position) {

        if (holder instanceof ThreeViewHolder) {
            ThreeViewHolder viewHolder = (ThreeViewHolder) holder;
            NewsInformation.DataBean moreData = data.get(position);
            if (moreData != null) {
                viewHolder.author_name.setTextColor(Color.parseColor("#9c9c9c"));
                viewHolder.publish_time.setTextColor(Color.parseColor("#9c9c9c"));
                viewHolder.new_title.setTextColor(Color.parseColor("#9c9c9c"));
            }
        } else if (holder instanceof OneImagViewHolder) {
            OneImagViewHolder oneImagHolder = (OneImagViewHolder) holder;
            oneImagHolder.author_name.setTextColor(Color.parseColor("#9c9c9c"));
            oneImagHolder.publish_time.setTextColor(Color.parseColor("#9c9c9c"));
            oneImagHolder.content.setTextColor(Color.parseColor("#9c9c9c"));
        }

        int id = data.get(position).getId();
        SharedPreferencesUtil.putIntToDefaultSharedPreferences(data.get(position).getId()+"", id);
    }

    /**
     * 见底时的界面
     */
    public class FooterVisibleHolder extends RecyclerViewPlus.HeaderFooterItemAdapter.ContentViewHolder {

        public TextView txt_more;

        public FooterVisibleHolder(View itemView) {
            super(itemView);
            txt_more = (TextView) itemView.findViewById(R.id.footer_more);
            Log.d(TAG, "FooterVisibleHolder: 见底了");
            if (mScrollListener != null) {
                mScrollListener.onScroll();
            }
        }

    }

    /**
     * 视频界面
     */
    public class VideoViewHolder extends RecyclerViewPlus.HeaderFooterItemAdapter.ContentViewHolder {

        public VideoViewHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * 无数据时的界面
     */
    public class EmptyViewHolder extends RecyclerViewPlus.HeaderFooterItemAdapter.ContentViewHolder {

        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * 显示3张图片的布局
     */
    protected class ThreeViewHolder extends RecyclerViewPlus.HeaderFooterItemAdapter.ContentViewHolder {

        private ImageView img1;
        private ImageView img2;
        private ImageView img3;
        private TextView  new_title;
        private TextView  author_name;
        private TextView  publish_time;

        public ThreeViewHolder(View view) {
            super(view);
            new_title = (TextView) view.findViewById(R.id.new_title);
            author_name = (TextView) view.findViewById(R.id.author_name);
            publish_time = (TextView) view.findViewById(R.id.publish_time);
            img1 = (ImageView) view.findViewById(R.id.item_bitmap_1);
            img2 = (ImageView) view.findViewById(R.id.item_bitmap_2);
            img3 = (ImageView) view.findViewById(R.id.item_bitmap_3);
        }

    }

    /**
     * 显示一张图片的布局
     */
    protected class OneImagViewHolder extends RecyclerViewPlus.HeaderFooterItemAdapter.ContentViewHolder {

        private TextView  content;
        private ImageView img;
        private TextView  author_name;
        private TextView  publish_time;

        public OneImagViewHolder(View view) {
            super(view);
            content = (TextView) view.findViewById(R.id.item_content);
            img = (ImageView) view.findViewById(R.id.item_bitmap);
            author_name = (TextView) view.findViewById(R.id.author_name);
            publish_time = (TextView) view.findViewById(R.id.publish_time);
        }

    }

    /**
     * 到达最底部时显示的布局
     */
    protected class FootViewHolder extends RecyclerViewPlus.HeaderFooterItemAdapter.ContentViewHolder {

        public ProgressBar load;

        public TextView more;

        public FootViewHolder(View view) {
            super(view);
            load = (ProgressBar) view.findViewById(R.id.recycle_load_more);
            more = (TextView) view.findViewById(R.id.recycle_open_more);
            Log.d(TAG, "FootViewHolder: 到达最底部了");
        }

    }

    @Override
    public ContentViewHolder onCreateContentView(ViewGroup parent, int viewType) {

        ContentViewHolder holder;
        if (viewType == FOOT_PROGRESS_HOLDER_VIEW) {
            View view = inflater.inflate(R.layout.recycler_view_layout_progress, parent, false);
            holder = new FootViewHolder(view);
        } else if (viewType == THREE_IMAGE_VIEW) {
            View view = inflater.inflate(R.layout.recycler_view_layout_item_three, parent, false);
            holder = new ThreeViewHolder(view);
        } else if (viewType == SINGLE_IMAGE_VIEW) {
            View view = inflater.inflate(R.layout.recycler_view_layout_item_one, parent, false);
            holder = new OneImagViewHolder(view);
        } else if (viewType == VIDEO_VIEW) {  //-->待加
            View view = inflater.inflate(R.layout.recycler_view_layout_item_one, parent, false);
            holder = new VideoViewHolder(view);
        } else if (viewType == FOOT_HOLDER_VIEW) { //底部加载更多字样-7条
            View view = inflater.inflate(R.layout.footer_no_data, parent, false);
            holder = new FooterVisibleHolder(view);
        } else {
            View view = inflater.inflate(R.layout.empty_item, parent, false);
            holder = new EmptyViewHolder(view);
        }
        return holder;

    }

    private int mTotalCount = 0;

    public void setTotalCount(int count) {
        this.mTotalCount = count;
    }

    public void setToalData(int totalCount) {
        this.mTotalCount = totalCount;
    }

    public void update(MarketObservable observable, Object data) {
    }

    public void addFootData() {
        data.add(null);
        notifyItemInserted(data.size() - 1);
    }

    public void removeFootData() {
        int size = data.size() - 1;
        removeData(size);
    }

    public void removeData(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }
}


