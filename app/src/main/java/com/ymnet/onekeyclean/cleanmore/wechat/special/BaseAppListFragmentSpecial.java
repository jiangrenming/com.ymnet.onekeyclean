package com.ymnet.onekeyclean.cleanmore.wechat.special;/*
package com.example.baidumapsevice.wechat.special;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.baidumapsevice.wechat.R;
import com.example.baidumapsevice.customview.RecyclerViewPlus;
import com.example.baidumapsevice.fragment.SlidingBaseFragment;
import com.example.baidumapsevice.utils.C;
import com.example.baidumapsevice.model.TopicUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.PtrUIHandler;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;


*/
/**
 * Created by wangdh on 4/21/16.
 * gmail:wangduheng26@gamil.com
 * 2345:wangdh@2345.com
 *//*

public abstract class BaseAppListFragmentSpecial extends SlidingBaseFragment
        implements */
/*ComplexAppListRecyclerViewAdapter.LazyloadListener,*//*
 ImagePagerAdapter.OnItemClickListener, */
/*ComplexAppListRecyclerViewAdapter.OnDisplayListener,*//*
 AppListView {
    private static final String TAG = BaseAppListFragmentSpecial.class.getSimpleName();
    public static final int TYPE_APP_ITEM = 0;
    public static final int TYPE_TOPIC_ITEM = 1;
    public static final int LOOP_COUNT = 7; // show one topic every LOOP_COUNT - 1 app

    public static final int SOURCE_RECOMMEND = 11;
    private static final String KEY_POSSTION = "_11";
    private static final String APP_DISPLAY_UNDERLINE = "appDisplay_";

    public static final String RANK_MORE = "rank_more";
    protected RecyclerViewPlus    refreshListView;
    private   LinearLayoutManager linearLayoutManager;
    private   PtrFrameLayout      mPtrFrame;

//    ComplexAppListRecyclerViewAdapter specialAdapter;
    ArrayList<TopicItem> mBannerTopics;
    ArrayList<Object> mInsertedList;
    ImagePagerAdapter bannerTopicAdapter;
    protected View bannerArea;
    protected AutoScrollViewPager mViewPager;
    protected RadioGroup mPageIndicator;
    protected ImageView actEnter;

    boolean firstLoad = true;

    String supportTag;

    boolean loadDelay = false;

    private boolean loading = false;

    public static int showWhat = 0;

    BounceTouchListener bounceTouchListener;

    protected ConfirmData confirmData = new ConfirmData();

    private AppListSpecialPresenter presenter;

    protected View.OnClickListener itemClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v instanceof LinearLayout) {
                if (v.getTag() instanceof App) {
                    App app = (App) v.getTag();
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra(App.class.getSimpleName(), app);
                    intent.putExtra("type", confirmData.type);
                    intent.putExtra(App.class.getSimpleName(), app);
                    getActivity().startActivity(intent);
                   */
/* Object obj = v.getTag(R.id.ll_tag);
                    Object topicId = v.getTag(v.getId());
                    if(obj != null && obj instanceof Boolean && (Boolean) obj){
                        if(topicId != null && topicId instanceof Integer)
                        StatisticSpec.sendEvent(StatisticEventContants.zhuanti_applist_detail+((Integer) topicId));
                    }*//*

                    return;
                }
            }

            Object oHolder = v.getTag();
            if (oHolder instanceof AppItemViewHolder) {
                AppItemViewHolder appItemHolder = (AppItemViewHolder) oHolder;
                int posi = appItemHolder.position;
                int clickPostion = posi + 1;
                if(!TextUtils.isEmpty(confirmData.itemClickPushEvent)){
                    StatisticSpec.sendEvent(confirmData.itemClickPushEvent+clickPostion);
                }

                if (clickPostion <= 100) {
                    if (TYPE_RECOMMEND.equals(confirmData.type)) {
                        Statistics.onEvent(getActivity(), StatisticEventContants.Location_Main_Click_id + clickPostion);
                    } else if (TYPE_SOFT.equals(confirmData.type)) {
                        Statistics.onEvent(getActivity(), StatisticEventContants.Location_Soft_Click_id + clickPostion);
                    } else if (TYPE_GAME.equals(confirmData.type)) {
                        Statistics.onEvent(getActivity(), StatisticEventContants.Location_Game_Click_id + clickPostion);
                    }
                }

                Object o = mInsertedList.get(posi);
                if (o instanceof App) {
                    App app = (App) o;
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra(App.class.getSimpleName(), app)
                            .putExtra("type", confirmData.type)
                            .putExtra("position", posi);
                    if(!TextUtils.isEmpty(confirmData.itemClickToDetailPushEvent)){
                        intent.putExtra("clicktoevent",confirmData.itemClickToDetailPushEvent+(posi +1));
                    }
                    startActivity(intent);

                }
            } else if (oHolder instanceof BannerTopicItemViewHolder) {
                BannerTopicItemViewHolder bannerTopicItemViewHolder = (BannerTopicItemViewHolder) oHolder;
                int position = bannerTopicItemViewHolder.position;
                TopicItem topicItem = bannerTopicItemViewHolder.topicItem;


                int clickPostion = position + 1;
                if (clickPostion <= 100) {
                    if (TYPE_RECOMMEND.equals(confirmData.type)) {
                        Statistics.onEvent(getActivity(), StatisticEventContants.Location_Main_Click_id + clickPostion);
                    } else if (TYPE_SOFT.equals(confirmData.type)) {
                        Statistics.onEvent(getActivity(), StatisticEventContants.Location_Soft_Click_id + clickPostion);
                    } else if (TYPE_GAME.equals(confirmData.type)) {
                        Statistics.onEvent(getActivity(), StatisticEventContants.Location_Game_Click_id + clickPostion);
                    }
                }

                if (topicItem != null) {
                    TopicUtils.startTopic(getActivity(), topicItem, confirmData.type);
                    if(!TextUtils.isEmpty(confirmData.topicItemClickPushEvent)){
                        StatisticSpec.sendEvent(confirmData.topicItemClickPushEvent+(position+1));
                    }
                }


            } else if (oHolder instanceof FourAppsTopicItemViewHolder) {
                FourAppsTopicItemViewHolder fourAppsTopicItemViewHolder = (FourAppsTopicItemViewHolder) oHolder;
                TopicItem topicItem = fourAppsTopicItemViewHolder.topicItem;
                int position = fourAppsTopicItemViewHolder.position;
                if (topicItem != null) {
                    TopicUtils.startTopic(getActivity(), topicItem, RANK_MORE);
                    if(!TextUtils.isEmpty(confirmData.topicItemClickPushEvent)){
                        StatisticSpec.sendEvent(confirmData.topicItemClickPushEvent+(position+1));
                    }
                }
            } else if (oHolder instanceof BoonsTopicItemViewHolder) {
                BoonsTopicItemViewHolder boonsTopicItemViewHolder = (BoonsTopicItemViewHolder) oHolder;
                TopicItem topicItem = boonsTopicItemViewHolder.topicItem;
                if (topicItem != null) {
                    StatisticSpec.sendEvent(StatisticEventContants.zhuanti_fuli_click+topicItem.topicId);
                    TopicUtils.startTopic(getActivity(), topicItem, null);
                    int position = boonsTopicItemViewHolder.position;
                    if(!TextUtils.isEmpty(confirmData.topicItemClickPushEvent)){
                        StatisticSpec.sendEvent(confirmData.topicItemClickPushEvent+(position+1));
                    }
                }
            }
        }
    };

    @Override
    public void onDisplay(RecyclerViewPlus.HeaderFooterItemAdapter.ContentViewHolder holder) {
        if (holder instanceof AppItemViewHolder) {
            AppItemViewHolder appItemHolder = (AppItemViewHolder) holder;
            int posi = appItemHolder.position;
            Object object = mInsertedList.get(posi);
            if (object instanceof App) {
                App app = (App) object;
                if (TYPE_RECOMMEND.equals(confirmData.type) && app.isAd > 0) {
                    Statistics.onEvent(C.get(), APP_DISPLAY_UNDERLINE + app.sid + KEY_POSSTION);
                }
            }
        } else if (holder instanceof FourAppsTopicItemViewHolder) {
            FourAppsTopicItemViewHolder fourAppsTopicItemViewHolder = (FourAppsTopicItemViewHolder) holder;
            TopicItem topicItem = fourAppsTopicItemViewHolder.topicItem;
            for (App app : topicItem.appList) {
                if (TYPE_RECOMMEND.equals(confirmData.type) && app.isAd > 0) {
                    Statistics.onEvent(C.get(), APP_DISPLAY_UNDERLINE + app.sid + KEY_POSSTION);
                }
            }

        }
    }

    private void setListViewDisplay(boolean display) {
        if (mPtrFrame == null) {
            return;
        }
        if (display) {
            mPtrFrame.setVisibility(View.VISIBLE);
        } else {
            mPtrFrame.setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
//        L.d(TAG, "onCreateView");
        confirmDataSaveNeeded(confirmData);
        presenter = initPresenter();
        presenter.setView(this);
        View view = inflater.inflate(R.layout.special, container, false);
        refreshListView = (RecyclerViewPlus) view.findViewById(R.id.rv_listview);
        linearLayoutManager = new LinearLayoutManager(C.get());
        refreshListView.setLayoutManager(linearLayoutManager);
        createDefaultFooterView(inflater, refreshListView);
        bounceTouchListener = new BounceTouchListener(refreshListView);
        bounceTouchListener.setDirection(BounceTouchListener.DIRECTION_UP);
        bounceTouchListener.setShown(false);
        refreshListView.setOnTouchListener(bounceTouchListener);

        mPtrFrame = (PtrFrameLayout) view.findViewById(R.id.list_view_frame);
        // the following are default settings
        mPtrFrame.setResistance(1.7f);
        mPtrFrame.setRatioOfHeaderHeightToRefresh(1.2f);
        mPtrFrame.setDurationToClose(200);
        mPtrFrame.setDurationToCloseHeader(1000);
        // default is false
        mPtrFrame.setPullToRefresh(false);
        // default is true
        mPtrFrame.setKeepHeaderWhenRefresh(true);
        mPtrFrame.disableWhenHorizontalMove(true);
        setListViewDisplay(false);
        mRetry.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!Utils.isNetworkAvailable(C.get())) {
                    Toast.makeText(C.get(), R.string.net_error_later_try, Toast.LENGTH_SHORT).show();
                } else {
                    showFootLoading();
                    loadData(false);
                }

            }
        });
        // loading控件内部

        initAdapter();

        actEnter = (ImageView) view.findViewById(R.id.act_enter);

        View PtrHeader = new Ptr2345Header(C.get());
        mPtrFrame.setHeaderView(PtrHeader);
        mPtrFrame.addPtrUIHandler((PtrUIHandler) PtrHeader);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
//                return false;
                return Ptr2345Handler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout ptrFrameLayout) {
                if (TYPE_RECOMMEND.equals(confirmData.type)) {
                    StatisticSpec.sendEvent(StatisticEventContants.Main_List_Refresh);
                } else if (TYPE_SOFT.equals(confirmData.type)) {
                    Statistics.onEvent(getActivity(), StatisticEventContants.Refresh_SoftWare);
                } else if (TYPE_GAME.equals(confirmData.type)) {
                    Statistics.onEvent(getActivity(), StatisticEventContants.Refresh_Game);
                }

                if (mViewPager != null) {
                    mViewPager.stopAutoScroll();
                }
                loadData(true);
            }
        });
        hideFootLoading();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoading();
                loadData(true);
            }
        });
        if (loadDelay) {
            loadData(true);
        }

    }

    @Override
    public void onResume() {
        if (refreshListView != null && refreshListView.getAdapter() != null && specialAdapter != null) {
            specialAdapter.notifyDataSetChanged();
        }
        if (mViewPager != null && mViewPager.getVisibility() == View.VISIBLE && mViewPager.getChildCount() > 1 && !mViewPager.isCurrentAutoScroll()) {
            mViewPager.startAutoScroll();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mViewPager != null && mViewPager.getVisibility() == View.VISIBLE) {
            mViewPager.stopAutoScroll();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (specialAdapter != null) {
            LinearLayoutManager linearLayoutManager = specialAdapter.getBoonsTopicLinearLayoutManager();
            if (linearLayoutManager != null) {
                int firsitVisible = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                if (firsitVisible > 0) {
                    C.setPreference(BoonsTopicAppListRecyclerViewAdapter.PREF_LAST_BOONS_APP_POS, firsitVisible);
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.destroy();
    }

    private void loadData(boolean reload) {
        loadFromServer(reload);
    }

    protected void loadFromServer(boolean reload) {
        if (presenter != null) {
            presenter.loadData(reload);
        }
    }

    private void handlerFloatActData() {
        loadFloatActData();
    }

    private void handlerPtrFrame() {
        if (mPtrFrame == null) {
            return;
        }
        View ptrHeader = mPtrFrame.getHeaderView();
        if (ptrHeader instanceof Ptr2345Header) {
            ((Ptr2345Header) ptrHeader).setmCompleteStatus(showWhat);
        }

        boolean isFreshing = true;
        try {
            Class<?> clazz = mPtrFrame.getClass();
            Field field = clazz.getDeclaredField("mStatus");
            field.setAccessible(true);
            byte status = (byte) field.get(mPtrFrame);
            isFreshing = (PtrClassicFrameLayout.PTR_STATUS_LOADING == status);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isFreshing) {
            mPtrFrame.refreshComplete();
            StatisticSpec.sendEvent(confirmData.refreshCompletePushEvent);
        }
    }

    private void initAdapter() {
        if (specialAdapter == null) {
            mInsertedList = new ArrayList<>();
            specialAdapter = new ComplexAppListRecyclerViewAdapter(getActivity(), mInsertedList);
            final View header = createHeaderView();
            specialAdapter.addHeaderView(new RecyclerViewPlus.HeaderFooterItemAdapter.ViewHolderWrapper() {
                @Override
                protected View onCreateView(ViewGroup parent) {
                    return header;
                }
            });

            specialAdapter.addFooterView(new RecyclerViewPlus.HeaderFooterItemAdapter.ViewHolderWrapper() {
                @Override
                protected View onCreateView(ViewGroup parent) {
                    return footer;
                }
            });
            specialAdapter.setTag(confirmData.type);
            specialAdapter.setItemClick(itemClick);
            specialAdapter.setmLazyloadListener(this);
            specialAdapter.setShowRankTag(confirmData.showRankTag);
            specialAdapter.setConfirmData(confirmData);
            refreshListView.setAdapter(specialAdapter);
            specialAdapter.setOnDisplayListener(this);
        }
    }

    @Override
    public void onItemClicked(TopicItem item, String tag,int position) {
        if (item != null) {
            if(!TextUtils.isEmpty(confirmData.topicItemClickPushEvent)){
                StatisticSpec.sendEvent(confirmData.topicItemClickPushEvent+(position+1));
            }
            TopicUtils.startTopic(getActivity(), item, tag);
        }
    }

    //处理游戏宫格g
    protected void doGameTopGrid(ArrayList<TopicItem> topicItems) {
    }

    private void initTopics(List<TopicItem> banner, int page) {
        if (!isAdded()) {
            return;
        }
        if (mBannerTopics == null) {
            mBannerTopics = new ArrayList<>();
        }
        if (banner != null && bannerArea != null) {
            ArrayList<TopicItem> topicItems = new ArrayList<>();
            topicItems.addAll(banner);
            if (TYPE_GAME.equals(confirmData.type)||TYPE_SOFT.equals(confirmData.type)) {
                if (topicItems != null && topicItems.size() > 0) {
                    doGameTopGrid(topicItems);
                } else if (page == 1) {
                    bannerArea.setVisibility(View.GONE);
                }
            } else {
                if (mViewPager != null && mPageIndicator != null) {
                    if (mBannerTopics.size() > 0) {
                        if (topicItems != null) {
                            boolean dataChanged = isBannerTopicDataChanged(topicItems, mBannerTopics);
                            if (dataChanged) {
                                mBannerTopics.clear();
                                mBannerTopics.addAll(topicItems);
                                if (bannerTopicAdapter != null) {
                                    bannerTopicAdapter.resetSize();
                                    bannerTopicAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                        mViewPager.startAutoScroll();
                    } else {
                        // first inited
                        if (topicItems != null) {
                            mBannerTopics.addAll(topicItems);
                        }
                        int size = mBannerTopics.size();
                        if (size > 0) {
                            if (bannerTopicAdapter == null) {
                                bannerTopicAdapter = new ImagePagerAdapter(C.get(), mBannerTopics);
                                bannerTopicAdapter.setOnItemClickListener(this);
                                mViewPager.setInterval(getResources().getInteger(R.integer.topic_banner_auto_switch_interval));
                            }

                            bannerTopicAdapter.resetSize();
//                        if (size > 1) {
//                            bannerTopicAdapter.setInfiniteLoop(true);
//                        } else {
//                            bannerTopicAdapter.setInfiniteLoop(false);
//                        }
                            bannerTopicAdapter.setTag(confirmData.type);
                            mViewPager.setAdapter(bannerTopicAdapter);
                            mViewPager.setOffscreenPageLimit(1);
                            mViewPager.setOnPageChangeListener(new CircleIndicatorChangeListener(size, mPageIndicator));
                            mPageIndicator.removeAllViews();
                            for (int i = 0; i < size && size > 1; i++) {
                                View v = View.inflate(C.get(), R.layout.base_auto_scroll_view_pager_circle_indicator, null);
                                v.setId(i + 1);
                                RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);

                                params.leftMargin = (int) getResources().getDimension(R.dimen.topic_banner_indicator_margin);
                                v.setClickable(false);
                                mPageIndicator.addView(v, params);
                            }

                            if (size > 1) {
                                mViewPager.setCurrentItem(size * 10000);
                                mViewPager.startAutoScroll();
                            }

                            bannerArea.setVisibility(View.VISIBLE);
                            mViewPager.setVisibility(View.VISIBLE);
                            mPageIndicator.setVisibility(View.VISIBLE);
                            ((HomeTabActivity) getActivity()).getSlidingMenu().addIgnoredView(bannerArea);
                        } else {
                            mViewPager.setVisibility(View.GONE);
                            mPageIndicator.setVisibility(View.GONE);
                            bannerArea.setVisibility(View.GONE);
                        }
                    }
                } else {
                    if (!(mBannerTopics.size() > 0)) {
                        bannerArea.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    */
/**
     * 判断标准：banner 中包含的专题 ID 相同且顺序相同。
     *
     * @param d1
     * @param d2
     * @return 符合标准返回 fasle ，否则返回 true
     *//*

    private boolean isBannerTopicDataChanged(ArrayList<TopicItem> d1, ArrayList<TopicItem> d2) {
        if (d1 != null && d2 != null) {
            if (d1.size() == d1.size()) {
                for (int i = 0; i < d1.size(); i++) {
                    TopicItem t1 = d1.get(i);
                    TopicItem t2 = d2.get(i);
                    if (t1.topicId != t2.topicId) {
                        return true;
                    }
                }
                return false; // 专题个数与专题 ID 顺序相同
            }
        }
        return true;
    }

    @Override
    public boolean isEnd() {
        if (presenter == null) {
            return false;
        }
        if (presenter.dataIsNull()) {
            return false;
        }
        if (mRetry.getVisibility() == View.GONE) {
            return !presenter.hasMore();
        } else {
            return true;
        }
    }

    @Override
    public void lazyload() {
        if (loading) {
            return;
        }
        loading = true;
        showFootLoading();
        loadData(false);
    }

    @Override
    public boolean isLoadOver() {
        return !loading;
    }

    @Override
    public void setSupportTag(String tag) {
        supportTag = tag;
    }

    @Override
    public String getSupportTag() {
        if (TextUtils.isEmpty(supportTag)) {
            // 由于android studio的BUG，getClass()有时候会报错，转成Object可以解决
            supportTag = ((Object) this).getClass().getSimpleName() + hashCode();
        }
        return supportTag;
    }

    @Override
    public void showSelf() {
        L.d(TAG, "showSelf");
        confirmDataSaveNeeded(confirmData);
        if (getActivity() == null) {
            loadDelay = true;
        } else {
            if (firstLoad) {
                showLoading();
                loadData(true);
                firstLoad = false;
            }
        }
    }

    public void scrollToListTop() {
        if (refreshListView != null) {
            L.d(TAG, "scrollToListTop");
            UIUtils.scrollToListViewTop(refreshListView);
        }
    }

    @Override
    public void showBannerTopics(List<TopicItem> banner, int page) {
        if (isAdded()) {
            initTopics(banner, page);
        }
    }
    @Override
    public void showAppList(List<Object> apps) {
        if (apps != null) {
            mInsertedList.clear();
            mInsertedList.addAll(apps);
        }
        if (specialAdapter != null) {
            specialAdapter.notifyDataSetChanged();
        }
    }
    @Override
    public void setLoadStatus(boolean onloading) {
        loading = onloading;
    }

    @Override
    public void handlerActData() {
        if (isAdded()) {
            handlerFloatActData();
        }
    }

    @Override
    public void handlerPtrFrameView() {
        if (isAdded()) {
            handlerPtrFrame();
        }
    }

    @Override
    public void lazyloadData() {
        lazyload();
    }
    @Override
    public void setListViewDisplayStatus(boolean display) {
        setListViewDisplay(display);
    }
    @Override
    public void setFootBounce() {
        if (bounceTouchListener != null) {
            bounceTouchListener.setShown(true);
        }
        showFootReachBotton();
    }
    @Override
    public void setShowWhat(int showWhat) {
        this.showWhat = showWhat;
    }
    @Override
    public void sendEvent() {
        FragmentFinishEvent event = new FragmentFinishEvent();
        event.status = showWhat;
        EventBus.getDefault().post(event);
    }
    @Override
    public void showFootRetryView() {
        showFootRetry();
    }

    @Override
    public void hideLoadingView() {
        hideLoading();
    }
    @Override
    public void showRetry() {
        showLoadFail();
    }

    @Override
    public void hideFootLoadingView() {
        hideFootLoading();
    }

    public abstract void confirmDataSaveNeeded(ConfirmData confirmData);

    public abstract View createHeaderView();

    public abstract void loadFloatActData();

    protected abstract AppListSpecialPresenter initPresenter();

    */
/**
     * 返回一个设置了url与参数的builder
     *//*


    public static class ConfirmData {
        public String dataKey;

        public String timeKey;

        */
/**
         * 是否忽略非当天安装的应用
         *//*

        public boolean ignoreInstalledApp = false;

        public boolean showRankTag = false;

        public String type;

        public String downloadPushEvent;

        public String itemClickPushEvent;

        public String itemClickToDetailPushEvent;

        public String topicItemClickPushEvent;

        public String refreshCompletePushEvent;

    }

    public interface DownloadCallback {
        void onClick();
    }
}
*/
