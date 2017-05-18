package com.ymnet.onekeyclean.cleanmore.customview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created with IntelliJ IDEA.
 * Date: 15/7/16
 * Author: zhangcm
 */

public class RecyclerViewPlus extends RecyclerView {

    private static final String TAG = RecyclerViewPlus.class.getSimpleName();

//    private SpecialListFragment.OnListviewScrollListener listener;

    @Nullable
    private View emptyView;

    private LayoutManager mLayoutManager;

    public RecyclerViewPlus(Context context) {
        super(context);
        init();
    }

    public RecyclerViewPlus(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RecyclerViewPlus(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
//        setOnScrollListener(scrollListener);
    }

   /* public void setListviewScrollListener(SpecialListFragment.OnListviewScrollListener listener) {
        this.listener = listener;
    }

    private OnScrollListener scrollListener = new OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (listener != null) {
                listener.onScrolled(recyclerView, dx, dy);
            }
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (listener != null) {
                listener.onScrollStateChanged(recyclerView, newState);
            }
        }
    };*/

    private void checkIfEmpty() {
        if (emptyView != null && getAdapter() != null) {
            RecyclerView.Adapter adapter = getAdapter();
            int count;
            if (adapter instanceof HeaderFooterItemAdapter) {
                HeaderFooterItemAdapter itemAdapter = (HeaderFooterItemAdapter) adapter;
                count = itemAdapter.getContentItemCount();
            } else {
                count = adapter.getItemCount();
            }

            emptyView.setVisibility(count > 0 ? GONE : VISIBLE);
            this.setVisibility(count > 0 ? VISIBLE : GONE);
        }
    }

    public final AdapterDataObserver observer = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            checkIfEmpty();
        }
    };

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        mLayoutManager = layout;
    }

    @Override
    public void setAdapter(@Nullable final Adapter adapter) {
        final Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(observer);
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer);
        }

        if (adapter instanceof HeaderFooterItemAdapter) {
            HeaderFooterItemAdapter headerFooterItemAdapter = (HeaderFooterItemAdapter) adapter;
            if (headerFooterItemAdapter.hasHeaderFooterView()) {
                if (mLayoutManager != null) {
                    if (mLayoutManager instanceof LinearLayoutManager) {
                        if (mLayoutManager instanceof GridLayoutManager) {
                            final GridLayoutManager gridLayoutManager = (GridLayoutManager) mLayoutManager;
                            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                                @Override
                                public int getSpanSize(int position) {
                                    return ((HeaderFooterItemAdapter) adapter).isHeaderView(position) || (((HeaderFooterItemAdapter) adapter).isBottomView(position)) ? gridLayoutManager.getSpanCount() : 1;
                                }
                            });
                        }
                    } else {
                        throw new RuntimeException("The layoutManager must be the instance of LinearLayoutManager or GridLayoutManager when hasHeaderFooter");
                    }
                } else {
                    throw new RuntimeException("setLayoutManager() must be called before than setAdapter()");
                }
            }
        }
    }

    public void setEmptyView(@Nullable View emptyView) {
        this.emptyView = emptyView;
        checkIfEmpty();
    }

    public static abstract class HeaderFooterItemAdapter extends RecyclerView.Adapter {

        private SparseArray<ViewHolderWrapper> mHeaderViews;
        private int mHeaderFooterViewType;
        private SparseArray<ViewHolderWrapper> mFooterViews;
        private int mHeaderCount;//头部View个数
        private int mFooterCount;//底部View个数


        public boolean isHeaderView(int position) {
            return mHeaderCount != 0 && position < mHeaderCount;
        }

        public boolean isBottomView(int position) {
            return mFooterCount != 0 && position >= (mHeaderCount + getContentItemCount());
        }

        public boolean hasHeaderFooterView() {
            return mHeaderCount + mFooterCount > 0;
        }

        public boolean hasHeaderView() {
            return mHeaderCount > 0;
        }

        public boolean hasFooterView() {
            return mFooterCount > 0;
        }


/**
         * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
         * an item.
         * <p>
         * This new ViewHolder should be constructed with a new View that can represent the items
         * of the given type. You can either create a new View manually or inflate it from an XML
         * layout file.
         * <p>
         * The new ViewHolder will be used to display items of the adapter using
         * {@link #onBindViewHolder(ViewHolder, int)}. Since it will be re-used to display different
         * items in the data set, it is a good idea to cache references to sub views of the View to
         * avoid unnecessary {@link View#findViewById(int)} calls.
         * <p>
         * You can NOT override this yourself!
         * Use {@link #onCreateHeaderView(ViewGroup, int)},{@link #onCreateFooterView(ViewGroup, int)},{@link #onCreateContentView(ViewGroup, int)}
         * if you want to create your own ViewHolder.
         *
         * @param parent   The ViewGroup into which the new View will be added after it is bound to
         *                 an adapter position.
         * @param viewType The view type of the new View.
         * @return A new ViewHolder that holds a View of the given view type.
         * @see #getItemViewType(int)
         * @see #onBindViewHolder(ViewHolder, int)
         */

        @Override
        public final ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType < 0) {
                if (mHeaderViews != null && mHeaderViews.get(viewType) != null) {
                    return onCreateHeaderView(parent, viewType);
                } else {
                    return onCreateFooterView(parent, viewType);
                }
            } else {
                return onCreateContentView(parent, viewType);
            }
        }


/**
         * Return the view type of the item at <code>position</code> for the purposes
         * of view recycling.
         * <p>
         * <p>The default implementation of this method returns 0, making the assumption of
         * a single view type for the adapter. Unlike ListView adapters, types need not
         * be contiguous. Consider using id resources to uniquely identify item view types.
         * <p>
         * You can NOT override this yourself!
         * Use {@link #getContentItemViewType(int)}}
         * if you want return your own type.
         *
         * @param position position to query
         * @return integer value identifying the type of the view needed to represent the item at
         * <code>position</code>. Type codes need not be contiguous.
         */

        @Override
        public final int getItemViewType(int position) {
            int dataItemCount = getContentItemCount();
            if (mHeaderCount != 0 && position < mHeaderCount) {//头部View
                return mHeaderViews.keyAt(position);
            }
            int front = mHeaderCount + dataItemCount;
            if (mFooterCount != 0 && position >= front) {//底部View
                return mFooterViews.keyAt(position - front);
            } else {
                int viewType = getContentItemViewType(position - mHeaderCount);
                if (viewType < 0) {
                    throw new RuntimeException("The contentItemViewType must be large than zero!");
                } else {
                    return viewType;
                }
            }
        }


/**
         * Returns the total number of items in the data set hold by the adapter.
         * <p>
         * You can NOT override this yourself!
         * Use {@link #getItemCount()}
         * if you want return the total number of the content items.
         *
         * @return The total number of items in this adapter.
         */

        @Override
        public final int getItemCount() {
            return mHeaderCount + getContentItemCount() + mFooterCount;
        }

        public void addHeaderView(ViewHolderWrapper wrapper) {
            if (mHeaderViews == null) {
                mHeaderViews = new SparseArray<ViewHolderWrapper>();
            }
            mHeaderViews.put(--mHeaderFooterViewType, wrapper);
            mHeaderCount++;
            notifyDataSetChanged();
        }

        public void deleteHeaderView(ViewHolderWrapper wrapper) {
            if (mHeaderViews != null) {
                int index = mHeaderViews.indexOfValue(wrapper);
                mHeaderViews.delete(index);
                mHeaderCount--;
                notifyDataSetChanged();
            }
        }

        public void addFooterView(ViewHolderWrapper wrapper) {
            if (mFooterViews == null) {
                mFooterViews = new SparseArray<>();
            }
            mFooterViews.put(--mHeaderFooterViewType, wrapper);
            mFooterCount++;
            notifyDataSetChanged();
        }

        public void deleteFooterView(ViewHolderWrapper wrapper) {
            if (mFooterViews != null) {
                int index = mFooterViews.indexOfValue(wrapper);
                mFooterViews.delete(index);
                mFooterCount--;
                notifyDataSetChanged();
            }
        }


/**
         * Called by RecyclerView to display the data at the specified position. This method
         * should update the contents of the {@link ViewHolder#itemView} to reflect the item at
         * the given position.
         * <p>
         * Note that unlike {@link android.widget.ListView}, RecyclerView will not call this
         * method again if the position of the item changes in the data set unless the item itself
         * is invalidated or the new position cannot be determined. For this reason, you should only
         * use the <code>position</code> parameter while acquiring the related data item inside this
         * method and should not keep a copy of it. If you need the position of an item later on
         * (e.g. in a click listener), use {@link ViewHolder#getAdapterPosition()} which will have
         * the updated adapter position.
         * <p>
         * You can NOT override this yourself!
         * Use {@link #onBindContentViewHolder(ContentViewHolder, int)} instead of it.
         *
         * @param holder   The ViewHolder which should be updated to represent the contents of the
         *                 item at the given position in the data set.
         * @param position The position of the item within the adapter's data set.
         */

        @Override
        public final void onBindViewHolder(ViewHolder holder, int position) {
            if (holder instanceof ContentViewHolder) {
                onBindContentViewHolder((ContentViewHolder) holder, position - mHeaderCount);
            }
        }


        protected int getContentItemViewType(int position) {
            return 0;
        }

        public abstract int getContentItemCount();//获取中间内容个数

        protected abstract void onBindContentViewHolder(ContentViewHolder holder, int position);

        public abstract ContentViewHolder onCreateContentView(ViewGroup parent, int viewType);


        public ViewHolder onCreateHeaderView(ViewGroup parent, int viewType) {
            return mHeaderViews == null ? null : mHeaderViews.get(viewType).onCreateViewHolder(parent);
        }

        public ViewHolder onCreateFooterView(ViewGroup parent, int viewType) {
            return mFooterViews == null ? null : mFooterViews.get(viewType).onCreateViewHolder(parent);
        }

        public static class ContentViewHolder extends RecyclerView.ViewHolder {
            public ContentViewHolder(View itemView) {
                super(itemView);
            }
        }

        public static abstract class ViewHolderWrapper {

            private ViewHolder onCreateViewHolder(ViewGroup parent) {
                return new ViewHolder(onCreateView(parent)) {
                };
            }

            protected abstract View onCreateView(ViewGroup parent);
        }
    }

}
