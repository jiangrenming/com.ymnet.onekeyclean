package com.ymnet.onekeyclean.cleanmore.customview;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ymnet.onekeyclean.R;

import java.util.ArrayList;

/**
 * Created by MajinBuu on 2017/6/30 0030.
 *
 * @overView ${todo}.
 */

public abstract class BaseAutoScrollUpTextView<T> extends ListView implements AutoScrollData<T> {
    /**
     * 数据源
     */
    private ArrayList<T> mDataList = new ArrayList<T>();

    /**
     * 字体大小
     */
    private float mSize = 16;

    /**
     * 数据总数
     */
    private int mMax;

    private int position = -1;

    /**
     * 向上滚动距离
     */
    private int scroll_Y;

    private Context mContext;

    private int mScrollY;

    /**
     * 适配器
     */
    private AutoScrollAdapter mAutoScrollAdapter = new AutoScrollAdapter();

    /**
     * 监听器
     */
    private OnItemClickListener mOnItemClickListener;

    private long mTimer = 1000;

    /**
     * 获取高度
     *
     * @return
     */
    protected abstract int getAdertisementHeight();

    private Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // 开启轮播
            switchItem();
//            handler.postDelayed(this, mTimer);
        }
    };

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private void switchItem() {
        if (position == -1) {
            scroll_Y = 0;
            // TODO: 2017/6/30 0030 新增
            handler.postDelayed(runnable, mTimer);
        } else {
            scroll_Y = mScrollY;
        }
        smoothScrollBy(scroll_Y, 2000);
        setSelection(position);
        position++;
    }

    public BaseAutoScrollUpTextView(Context context) {
        this(context,null);
    }

    public BaseAutoScrollUpTextView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BaseAutoScrollUpTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        mScrollY = dip2px(getAdertisementHeight());
        init();
    }

    private int dip2px(int dipValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    private void init() {
        this.setDivider(null);
        this.setFastScrollEnabled(false);
        this.setDividerHeight(0);
        this.setEnabled(false);
    }


    private class AutoScrollAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            final int count = mDataList == null ? 0 : mDataList.size();
            return count > 1 ? Integer.MAX_VALUE : count;
        }

        @Override
        public Object getItem(int position) {
            return mDataList.get(position % mMax);
        }

        @Override
        public long getItemId(int position) {
            return position % mMax;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            ViewHolder viewHolder;
            if (null == convertView) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.item_scrollup_view, null);

                viewHolder.mInfoView = (TextView) convertView
                        .findViewById(R.id.tv_info);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            T data = mDataList.get(position % mMax);
            /*viewHolder.mTitleView
                    .setLayoutParams(new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            dip2px(getAdertisementHeight())));*/

            viewHolder.mInfoView.setTextSize(mSize);

            viewHolder.mInfoView.setText(getTextInfo(data));
            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(position % mMax);
                }
            });
            return convertView;
        }
    }

    static class ViewHolder {
//        TextView mTitleView;// 标题
        TextView mInfoView;// 内容
    }
    /**
     * 添加数据
     *
     * @param _datas
     */
    public void setData(ArrayList<T> _datas) {
        mDataList.clear();
        mDataList.addAll(_datas);
        mMax = mDataList == null ? 0 : mDataList.size();
        this.setAdapter(mAutoScrollAdapter);
        mAutoScrollAdapter.notifyDataSetChanged();
    }

    /**
     * 设置文字大小
     * @param _size
     */
    public void setTextSize(float _size){
        this.mSize=_size;
    }

    /**
     * 设置监听事件
     */
    public void setOnItemClickListener(OnItemClickListener _listener) {
        this.mOnItemClickListener = _listener;
    }

    /**
     * 设置轮播间隔时间
     *
     * @param _time
     *            毫秒单位
     */
    public void setTimer(long _time) {
        this.mTimer = _time;
    }

    /**
     * 开启轮播
     */
    public void start() {
        handler.postDelayed(runnable, 0);
    }

    /**
     * 关闭轮播
     */
    public void stop() {
        handler.removeCallbacks(runnable);
    }
}