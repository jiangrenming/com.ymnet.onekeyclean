package com.ymnet.onekeyclean.cleanmore.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.commonlibrary.retrofit2service.bean.InformationResult;
import com.example.commonlibrary.utils.NetworkUtils;
import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.utils.ToastUtil;

import java.util.ArrayList;


/**
 * Created by JRM on 2017/5/4.
 */

public class RecyclerViewLayout extends SwipeRefreshLayout implements SwipeRefreshLayout.OnRefreshListener {

    private Context                         mContext;
    private OnRecylerRefresh                refresh;
    private OnRecyclerViewItemClickListener itemClick;
    private onChangeTextBgListener          changeTextBgListener;
    private OnRecylerScrollListener         onRecylerScrollListener;
    private RecyclerView                    recyclerView;
    private boolean unRefresh  = false;
    private boolean unLoadMore = false;
    private RefreshRecylerAdapter recylerAdapter;

    public RecyclerViewLayout(Context context) {
        super(context);
        init(context);
    }

    public RecyclerViewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    protected void init(Context context) {
        this.mContext = context;
        setOnRefreshListener(this);
        recyclerView = new RecyclerView(context);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.addItemDecoration(new LinearLayoutItemDecoration(mContext, LinearLayoutItemDecoration.HORIZONTAL_LIST));
        recyclerView.addOnScrollListener(new RecyclerOnScrollListener());
        recyclerView.setAdapter(recylerAdapter = new RefreshRecylerAdapter(mContext));
        addView(recyclerView, params);
    }

    public RefreshRecylerAdapter getAdapter() {
        return recylerAdapter;
    }

    public class RefreshRecylerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public ArrayList<InformationResult> data;
        private int mTotalCount = 0;
        private LayoutInflater inflater;
        private final int SINGLE_IMAGE_VIEW         = 0;
        private final int THREE_IMAGE_VIEW          = 1;
        private final int VIDEO_VIEW                = 2;
        private final int FOOT_PROGRESS_HOLDER_VIEW = 3;
        private final int EMPTY_VIEW                = 4;
        private final int FOOT_HOLDER_VIEW          = 5;


        public RefreshRecylerAdapter(Context context) {
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.data = new ArrayList<>();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder holder = null;
            if (viewType == FOOT_PROGRESS_HOLDER_VIEW) {
                View view = inflater.inflate(R.layout.recycler_view_layout_progress, parent, false);
                holder = new FootViewHolder(view);
            } else if (viewType == THREE_IMAGE_VIEW) {
                View view = inflater.inflate(R.layout.recycler_view_layout_item_three, parent, false);
                holder = new ThreeViewHolder(view, itemClick, changeTextBgListener);
            } else if (viewType == SINGLE_IMAGE_VIEW) {
                View view = inflater.inflate(R.layout.recycler_view_layout_item_one, parent, false);
                holder = new OneImagViewHolder(view, itemClick, changeTextBgListener);
            } else if (viewType == VIDEO_VIEW) {  //-->待加
                View view = inflater.inflate(R.layout.recycler_view_layout_item_one, parent, false);
                holder = new VideoViewHolder(view);
            } else if (viewType == FOOT_HOLDER_VIEW) { //底部加载更多字样
                View view = inflater.inflate(R.layout.footer_no_data, parent, false);
                holder = new FooterVisableHolder(view, itemClick, changeTextBgListener);
            } else {
                View view = inflater.inflate(R.layout.empty_item, parent, false);
                holder = new EmptyViewHolder(view);
            }
            return holder;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof FootViewHolder) { //底部界面
                FootViewHolder footHolder = (FootViewHolder) holder;
                //return;
            } else if (holder instanceof ThreeViewHolder) {
                ThreeViewHolder viewHolder = (ThreeViewHolder) holder;
                InformationResult informationsResult = data.get(position);
                if (informationsResult != null) {
                    viewHolder.author_name.setText(informationsResult.getAuthor_name());
                    viewHolder.publish_time.setText(informationsResult.getPublish_time());
                    viewHolder.new_title.setText(informationsResult.getTitle());
                    Glide.with(mContext).load(informationsResult.getThumbnail_pic_s1()).asBitmap().into(viewHolder.img1);
                    Glide.with(mContext).load(informationsResult.getThumbnail_pic_s2()).asBitmap().into(viewHolder.img2);
                    Glide.with(mContext).load(informationsResult.getThumbnail_pic_s3()).asBitmap().into(viewHolder.img3);
                }
            } else if (holder instanceof OneImagViewHolder) {
                OneImagViewHolder oneImagHolder = (OneImagViewHolder) holder;
                oneImagHolder.author_name.setText(data.get(position).getAuthor_name());
                oneImagHolder.publish_time.setText(data.get(position).getPublish_time());
                oneImagHolder.content.setText(data.get(position).getTitle());
                Glide.with(mContext).load(data.get(position).getThumbnail_pic_s1()).asBitmap().into(oneImagHolder.img);

            } else if (holder instanceof VideoViewHolder) {
                //暂时不做
            } else if (holder instanceof FooterVisableHolder) {
                FooterVisableHolder footHolder = (FooterVisableHolder) holder;
            } else {
                //无数据时 空白页
                EmptyViewHolder emptyHolder = (EmptyViewHolder) holder;
                return;
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        @Override
        public int getItemViewType(int position) {
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

        public void setToalData(int totalCount) {
            this.mTotalCount = totalCount;
        }

        public void addData(int position, InformationResult result) {
            if (mTotalCount != 0) {
                if (getItemCount() == mTotalCount) {
                    result = new InformationResult();
                    result.show_type = -1;
                    result.news_url = "http://zm.youmeng.com/?from=001";
                } else if (isMaxData()) {
                    return;
                }
            }
            data.add(position, result);
            notifyItemInserted(position);
        }

        public void setData(ArrayList<InformationResult> list) {
            this.data = list;
            notifyDataSetChanged();
        }

        public void removeData(int position) {
            data.remove(position);
            notifyItemRemoved(position);
        }


        public void addData(ArrayList<InformationResult> list) {
            for (int i = 0; i < list.size(); i++) {
                addData(data.size(), list.get(i));
            }
        }

        public void addData(InformationResult result) {
            addData(data.size(), result);
        }

        public void addFootData() {
            data.add(null);
            notifyItemInserted(data.size() - 1);
        }

        public void removeFootData() {
            int size = data.size() - 1;
            removeData(size);
        }

        public boolean isMaxData() {
            return mTotalCount != 0 && getItemCount() > mTotalCount;
        }

    }


    /**
     * 见底时的界面
     */
    public class FooterVisableHolder extends RecyclerView.ViewHolder implements OnClickListener {
        public TextView                        txt_more;
        public OnRecyclerViewItemClickListener mItemClick;
        public onChangeTextBgListener          listener;

        public FooterVisableHolder(View itemView, OnRecyclerViewItemClickListener itemClick, onChangeTextBgListener changeTextBgListener) {
            super(itemView);
            this.mItemClick = itemClick;
            this.listener = changeTextBgListener;
            txt_more = (TextView) itemView.findViewById(R.id.footer_more);
            listener.onChangeTextColor(txt_more, null, null);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClick != null) {
                mItemClick.onItemClick(v, getPosition());
            }
        }
    }

    /**
     * 视频界面
     */
    public class VideoViewHolder extends RecyclerView.ViewHolder {
        public VideoViewHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * 无数据时的界面
     */
    public class EmptyViewHolder extends RecyclerView.ViewHolder {
        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * 显示3张图片的布局
     */
    protected class ThreeViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        private ImageView                       img1;
        private ImageView                       img2;
        private ImageView                       img3;
        private TextView                        new_title;
        private TextView                        author_name;
        private TextView                        publish_time;
        private OnRecyclerViewItemClickListener mItemClick;
        public  onChangeTextBgListener          listener;

        public ThreeViewHolder(View view, OnRecyclerViewItemClickListener itemClick, onChangeTextBgListener mListener) {
            super(view);
            this.mItemClick = itemClick;
            this.listener = mListener;
            new_title = (TextView) view.findViewById(R.id.new_title);
            author_name = (TextView) view.findViewById(R.id.author_name);
            publish_time = (TextView) view.findViewById(R.id.publish_time);
            img1 = (ImageView) view.findViewById(R.id.item_bitmap_1);
            img2 = (ImageView) view.findViewById(R.id.item_bitmap_2);
            img3 = (ImageView) view.findViewById(R.id.item_bitmap_3);
            listener.onChangeTextColor(new_title, publish_time, author_name);
            view.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if (itemClick != null) {
                itemClick.onItemClick(v, getPosition());
            }
        }
    }

    /**
     * 显示一张图片的布局
     */
    protected class OneImagViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        private TextView                        content;
        private ImageView                       img;
        private TextView                        author_name;
        private TextView                        publish_time;
        private OnRecyclerViewItemClickListener itemClick;
        private onChangeTextBgListener          listener;

        public OneImagViewHolder(View view, OnRecyclerViewItemClickListener itemClick, onChangeTextBgListener mListener) {
            super(view);
            this.itemClick = itemClick;
            this.listener = mListener;
            content = (TextView) view.findViewById(R.id.item_content);
            img = (ImageView) view.findViewById(R.id.item_bitmap);
            author_name = (TextView) view.findViewById(R.id.author_name);
            publish_time = (TextView) view.findViewById(R.id.publish_time);
            listener.onChangeTextColor(content, publish_time, author_name);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemClick != null) {
                itemClick.onItemClick(v, getPosition());
            }
        }
    }

    /**
     * 到达最底部时显示的布局
     */
    protected class FootViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar load;
        public TextView    more;

        public FootViewHolder(View view) {
            super(view);
            load = (ProgressBar) view.findViewById(R.id.recycle_load_more);
            more = (TextView) view.findViewById(R.id.recycle_open_more);
        }
    }

    public class RecyclerOnScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (onRecylerScrollListener != null)
                onRecylerScrollListener.onScrolled(recyclerView, dx, dy);
            if (isNetWork()) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                if (totalItemCount == lastVisibleItem + 1) {
                    startLoadMore();
                }
            }
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (mListener != null) {
                mListener.isVisableToolBar(newState);
            }
        }
    }

    @Override
    public void onRefresh() {
        startRefresh();
    }


    /**
     * 此数据是判断是否到底部
     *
     * @param count
     */
    public void setTotalCount(int count) {
        recylerAdapter.setToalData(count);
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    /**
     * 添加数据
     */
    public void setData(ArrayList<InformationResult> list) {
        recylerAdapter.setData(list);
    }

    public void addData(ArrayList<InformationResult> list) {
        recylerAdapter.addData(list);
    }

    public void addData(int position, InformationResult result) {
        recylerAdapter.addData(position, result);
    }

    public void removeData(int position) {
        recylerAdapter.removeData(position);
    }

    public OnRecyclerViewItemClickListener getItemClick() {
        return itemClick;
    }

    public void setItemClick(OnRecyclerViewItemClickListener itemClick) {
        this.itemClick = itemClick;
    }

    public OnRecylerRefresh getRefresh() {
        return refresh;
    }

    public void setRefresh(OnRecylerRefresh refresh) {
        this.refresh = refresh;
    }

    /**
     * refreshRecylerAdapter的子控件点击回调
     */
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View v, int position);

        void onItemLongClick(View v, int position);
    }

    public void setChangeTextBgListener(onChangeTextBgListener changeTextBgListener) {
        this.changeTextBgListener = changeTextBgListener;
    }

    public void setOnRecylerScrollListener(OnRecylerScrollListener onRecylerScrollListener) {
        this.onRecylerScrollListener = onRecylerScrollListener;
    }

    public interface onChangeTextBgListener {
        void onChangeTextColor(View view, View view1, View view2);
    }

    /**
     * RecyclerView的上下拉刷新回调
     */
    public interface OnRecylerRefresh {
        void onRefresh();

        void onLoadMore();
    }

    /**
     * RecyclerView的滑动回调
     */
    public interface OnRecylerScrollListener {
        void onScrolled(RecyclerView recyclerView, int dx, int dy);
    }

    public boolean isNetWork() {
        return !unLoadMore && !unRefresh;
    }

    /**
     * 结束上拉刷新，必须在数据更新之前上拉刷新成功的时候调用
     */
    public void endLoadMore() {
        if (unLoadMore) {
            recylerAdapter.removeFootData();
            unLoadMore = false;
        }
    }

    /**
     * 结束下拉刷新，必须在数据更新之前下拉刷新成功的时候调用
     */
    public void endRefresh() {
        if (unRefresh) {
            setRefreshing(false);
            unRefresh = false;
        }
    }

    /**
     * 开始上拉刷新
     */
    public void startLoadMore() {
        unLoadMore = true;
        recylerAdapter.addFootData();
        if (unNetwork() && !recylerAdapter.isMaxData()) {
            refresh.onLoadMore();
        } else {
            endLoadMore();
        }
    }

    /**
     * 开始下拉刷新
     */
    public void startRefresh() {
        unRefresh = true;
        if (unNetwork()) {
            refresh.onRefresh();
        } else {
            endRefresh();
        }
    }

    public boolean unNetwork() {
        if (!NetworkUtils.isNetworkAvailable(mContext.getApplicationContext())) {
            ToastUtil.showToastForShort("网络不可用,请检查网络...");
            return false;
        }
        return true;
    }

    public changeRefreshListener mListener;

    public void setChangeRefreshListener(changeRefreshListener listener) {
        this.mListener = listener;
    }

    public interface changeRefreshListener {
        void isVisableToolBar(int state);
    }
}
