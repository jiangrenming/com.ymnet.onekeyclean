package com.ymnet.onekeyclean.cleanmore.fragment;/*
package com.example.baidumapsevice.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.baidumapsevice.wechat.R;
import com.example.baidumapsevice.wechat.listener.FastScrollTopListener;
import com.example.baidumapsevice.wechat.special.BaseAppListFragmentSpecial;
import com.example.baidumapsevice.utils.C;
import com.example.baidumapsevice.model.TopicUtils;
import com.facebook.common.util.UriUtil;
import com.facebook.imageutils.BitmapUtil;

import java.util.ArrayList;
import java.util.List;

*/
/**
 * @author janan
 * @version since
 * @date 2014-9-11 下午3:02:13
 * @description
 *//*

public class SpecialListFragment extends BaseAppListFragmentSpecial implements FastScrollTopListener*/
/*, RecommendAppView*//*
 {
    public static final String HOT_RED_POINT = "hotRedPoint";
    public static final String NEWGAME_RED_POINT = "newgame_red_point";
    public static final String FLOAT_ACT = "floatact";
    public static final String BANNER_ACT = "banneract";
    private View galleryView;
    private ActItem floatActData; //悬浮活动数据
    private ActItem bannerActData; //横幅活动数据
    private boolean isshow = true;//当前活动悬浮入口是否展示
    private boolean needShow = false;//活动悬浮入口是否需要展示，用于如果当前入口正在做隐藏操作，做完后需要马上做展示操作
    private boolean needHide = false;//活动悬浮入口是否需要隐藏，用途如上。防止用户频繁上下滚动
    private int scrollY = 0;
    private ImageView redPoint1;
    private ImageView redPoint2;

    private ImageView mImageAlonegame;
    private ImageView mImageClassify;
    private ImageView mImageTopic;
    private ImageView mImageNecessary;
    private ImageView mImageReader;
    private ImageView mBannerAct;
    private ImageView mBannerActShadow;
    private RelativeLayout mBannerActLayout, mBannerActArea;

    private List<BaseIconInfo> mReplaceIconInfos;

    private RecommendAppListPresenter presenter;

    private OnClickListener recommendListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            IconAd ad = (IconAd) v.getTag();
            if (ad!=null){
//                StatisticSpec.sendEvent(ad.clickEvent);
            }
            Object o = v.getTag(R.id.position);
            if (o != null) {
                int position = (int)o;
                if (position == 1) {
                    SharedPreferencesUtil.putBooleanToDefaultSharedPreferences(NEWGAME_RED_POINT, true);
                    redPoint1.setVisibility(View.GONE);
                }
            }

            IconsUtil.navigateTo(getActivity(),ad);
        }
    };


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showSelf();
    }

    @Override
    public View createHeaderView() {
        mReplaceIconInfos = IconsUtil.getLocalIcons(IconsUtil.RECOMMEND);

        galleryView = getActivity().getLayoutInflater().inflate(R.layout.top_recommand, refreshListView, false);
        bannerArea = galleryView.findViewById(R.id.banner_area);
        mViewPager = (AutoScrollViewPager) galleryView.findViewById(R.id.view_pager);
        mPageIndicator = (RadioGroup) galleryView.findViewById(R.id.indicator);

        ArrayList<IconAd> list = localTopAdData();
        TextView first = (TextView) galleryView.findViewById(R.id.position_one);
        TextView second = (TextView) galleryView.findViewById(R.id.position_two);
        TextView third = (TextView) galleryView.findViewById(R.id.position_three);
        TextView fourth = (TextView) galleryView.findViewById(R.id.position_four);
        TextView five = (TextView) galleryView.findViewById(R.id.position_five);

        RelativeLayout layoutAlonegame = (RelativeLayout) galleryView.findViewById(R.id.ll_alonegame);
        RelativeLayout layoutClassify = (RelativeLayout) galleryView.findViewById(R.id.ll_classify);
        RelativeLayout layoutTopic = (RelativeLayout) galleryView.findViewById(R.id.ll_topic);
        RelativeLayout layoutNecessary = (RelativeLayout) galleryView.findViewById(R.id.ll_hot);
        RelativeLayout layoutReader = (RelativeLayout) galleryView.findViewById(R.id.ll_reader);

        mImageAlonegame = (ImageView) galleryView.findViewById(R.id.iv_alonegame);
        mImageClassify = (ImageView) galleryView.findViewById(R.id.iv_classify);
        mImageTopic = (ImageView) galleryView.findViewById(R.id.iv_topic);
        mImageNecessary = (ImageView) galleryView.findViewById(R.id.iv_hot);
        mImageReader = (ImageView) galleryView.findViewById(R.id.iv_reader);

        redPoint1 = (ImageView) galleryView.findViewById(R.id.red_point1);
        redPoint2 = (ImageView) galleryView.findViewById(R.id.red_point5);

        mBannerAct = (ImageView) galleryView.findViewById(R.id.banner_act);
        mBannerActShadow = (ImageView) galleryView.findViewById(R.id.banner_act_shadow);
        mBannerActArea = (RelativeLayout) galleryView.findViewById(R.id.banner_act_area);
        mBannerActLayout = (RelativeLayout) galleryView.findViewById(R.id.banner_act_layout);
        mBannerActArea.setVisibility(View.GONE);
        mBannerActLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                TopicUtils.startTopic(getActivity(), bannerActData, BANNER_ACT);
//                StatisticSpec.sendEvent(StatisticEventContants.main_ADbanner);
            }
        });

        if (null != list) {
            Drawable drawable = null;
            for (final IconAd ad : list) {
                if (ad.src != null) {
                    drawable = BitmapUtil.bytes2Drawable(ad.src);
                }
                switch (ad.position) {
                    case 1:
                        if (ad.src != null) {
                            if (drawable != null) {
                                mImageAlonegame.setImageDrawable(drawable);
                            }
                        } else {
                            if (ad.drawableTopId > 0) {
                                mImageAlonegame.setBackgroundResource(ad.drawableTopId);
                            }
                        }
                        if (!SharedPreferencesUtil.getBooleanFromDefaultSharedPreferences(NEWGAME_RED_POINT, false)) {
                            redPoint1.setVisibility(View.VISIBLE);
                        } else {
                            redPoint1.setVisibility(View.GONE);
                        }
                        layoutAlonegame.setTag(ad);
                        layoutAlonegame.setTag(R.id.position, 1);
                        layoutAlonegame.setVisibility(View.VISIBLE);
                        first.setText(ad.btn_title);
                        layoutAlonegame.setOnClickListener(recommendListener);
                        break;
                    case 2:
                        if (ad.src != null) {
                            if (drawable != null) {
                                mImageClassify.setImageDrawable(drawable);
                            }
                        } else {
                            if (ad.drawableTopId > 0) {
                                mImageClassify.setBackgroundResource(ad.drawableTopId);
                            }
                        }
                        layoutClassify.setTag(ad);
                        layoutClassify.setVisibility(View.VISIBLE);
                        second.setText(ad.btn_title);
                        layoutClassify.setOnClickListener(recommendListener);
                        break;
                    case 3:
                        if (ad.src != null) {
                            if (mImageTopic != null) {
                                mImageTopic.setImageDrawable(drawable);
                            }
                        } else {
                            if (ad.drawableTopId > 0) {
                                mImageTopic.setBackgroundResource(ad.drawableTopId);
                            }
                        }
                        layoutTopic.setTag(ad);
                        layoutTopic.setVisibility(View.VISIBLE);
                        third.setText(ad.btn_title);
                        layoutTopic.setOnClickListener(recommendListener);
                        break;

                    case 4:
                        if (ad.src != null) {
                            if (mImageReader != null) {
                                mImageReader.setImageDrawable(drawable);
                            }
                        } else {
                            if (ad.drawableTopId > 0) {
                                mImageReader.setBackgroundResource(ad.drawableTopId);
                            }
                        }
                        layoutReader.setTag(ad);
                        layoutReader.setVisibility(View.VISIBLE);
                        fourth.setText(ad.btn_title);
                        layoutReader.setOnClickListener(recommendListener);
                        break;
                    case 5:
                        if (ad.src != null) {
                            if (drawable != null) {
                                mImageNecessary.setImageDrawable(drawable);
                            }
                        } else {
                            if (ad.drawableTopId > 0) {
                                mImageNecessary.setBackgroundResource(ad.drawableTopId);
                            }
                        }
                        layoutNecessary.setTag(ad);
                        layoutNecessary.setVisibility(View.VISIBLE);
                        five.setText(ad.btn_title);
                        if (ChannelUtil.isUnionShouldShow(C.get())) {
                            if (SPUtil.isJf1()) {
                                showRedPoint();
                                if (C.getPreference(SpecialListFragment.HOT_RED_POINT, false)) {
                                    redPoint2.setVisibility(View.VISIBLE);
                                }
                            }

                            layoutNecessary.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Statistics.onEvent(getActivity(), StatisticEventContants.Main_Remen);
                                    boolean flag = C.getPreference(SpecialListFragment.HOT_RED_POINT, false);
                                    if (flag) {
                                        C.setPreference(SpecialListFragment.HOT_RED_POINT, false);
                                        redPoint2.setVisibility(View.GONE);
                                    }
                                    startActivity(new Intent(getActivity(), FastinstallActivity.class));
                                }
                            });
                        }else {
                            layoutNecessary.setOnClickListener(recommendListener);
                        }

                        break;
                    default:
                        break;
                }
            }
        }
        return galleryView;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (actEnter != null && floatActData != null && !TextUtils.isEmpty(floatActData.icon)) {
            if (SettingUtils.checkLastSetValue(C.get(), SettingUtils.SETTING.START_ACTIVITY_ENTER, true)) {
                readyShowActFloat();
            } else {
                setFloatGone();
            }
        }

        if (mBannerActArea != null && bannerActData != null && !TextUtils.isEmpty(bannerActData.img_url)) {
            if (SettingUtils.checkLastSetValue(C.get(), SettingUtils.SETTING.START_ACTIVITY_ENTER, true)) {
                showBannerActView(bannerActData);
            } else {
                hideBannerAct();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.destroy();
        }
    }

    @Override
    protected AppListSpecialPresenter initPresenter() {
        return new RecommendAppListPresenter(confirmData);
    }

    @Override
    public void loadFloatActData() {
        setFloatGone();
        if (presenter == null) {
            presenter = new RecommendAppListPresenter(confirmData);
            presenter.setRecommendView(this);
        }
        presenter.loadActivityData();
    }

    private ArrayList<IconAd> localTopAdData() {
        ArrayList<IconAd> list = new ArrayList<>();

        // 新游
        IconAd recommendAlonegame = new IconAd();
        recommendAlonegame.position = 1;
        recommendAlonegame.btn_title = getString(R.string.title_btn_recommend_newgame);
        recommendAlonegame.title = getString(R.string.title_btn_recommend_newgame);
        recommendAlonegame.typeId=BaseIconInfo.NEWGAME;
        recommendAlonegame.typeVal = "";
        recommendAlonegame.drawableTopId = R.drawable.recommend_alonegame_normal;
        recommendAlonegame.clickEvent = StatisticEventBuilder.buildEvent(StatisticEventContants.main_list_mid_1);


        // 分类
        IconAd recommendClassify = new IconAd();
        recommendClassify.position = 2;
        recommendClassify.title = getString(R.string.title_recommend_classify);
        recommendClassify.btn_title = getString(R.string.title_btn_recommend_classify);
        recommendClassify.typeVal = "";
        recommendClassify.typeId=BaseIconInfo.CATE;
        recommendClassify.drawableTopId = R.drawable.recommend_classify_normal;
        recommendClassify.clickEvent = StatisticEventBuilder.buildEvent(StatisticEventContants.main_list_mid_2);

        // 专题
        IconAd recommendTopic = new IconAd();
        recommendTopic.position = 3;
        recommendTopic.btn_title = getString(R.string.title_btn_recommend_topic);
        recommendTopic.typeId=BaseIconInfo.TOPIC;
        recommendTopic.drawableTopId = R.drawable.recommend_topic_normal;
        recommendTopic.clickEvent = StatisticEventBuilder.buildEvent(StatisticEventContants.main_list_mid_3);

        // 小说
        IconAd recommendAdReader = new IconAd();
        recommendAdReader.position = 4;
        recommendAdReader.title = getString(R.string.title_recommend_reader);
        recommendAdReader.btn_title = getString(R.string.title_btn_recommend_reader);
        recommendAdReader.typeId=BaseIconInfo.READER;
        recommendAdReader.drawableTopId = R.drawable.recommend_novel_normal;
        recommendAdReader.clickEvent = StatisticEventBuilder.buildEvent(StatisticEventContants.main_list_mid_4);

        // 必备（联盟版本则显示热门）
        IconAd recommendAdNecessary = new IconAd();
        recommendAdNecessary.position = 5;
        if (ChannelUtil.isUnionShouldShow(getActivity())) {
            recommendAdNecessary.btn_title = getString(R.string.title_btn_recommend_hot);
            recommendAdNecessary.drawableTopId = R.drawable.recommend_hot_normal;
            recommendAdNecessary.clickEvent = StatisticEventBuilder.buildEvent(StatisticEventContants.main_list_mid_5_lm);
        } else {
            recommendAdNecessary.title = getString(R.string.title_recommend_necessary);
            recommendAdNecessary.btn_title = getString(R.string.title_btn_recommend_necessary);
            recommendAdNecessary.typeId=BaseIconInfo.LIST;
            recommendAdNecessary.typeVal= getString(R.string.type_install_necessary);
            recommendAdNecessary.drawableTopId = R.drawable.recommend_necessary_normal;
            recommendAdNecessary.clickEvent = StatisticEventBuilder.buildEvent(StatisticEventContants.main_list_mid_5);
        }

        list.add(recommendAlonegame);
        list.add(recommendClassify);
        list.add(recommendTopic);
        list.add(recommendAdReader);
        list.add(recommendAdNecessary);

        if (mReplaceIconInfos != null && mReplaceIconInfos.size() == 5) {
            for (int i=0;i<mReplaceIconInfos.size();i++){
                BaseIconInfo iconInfo=mReplaceIconInfos.get(i);
                if (!IconsUtil.validateData(iconInfo)){
                    continue;
                }
                if (i==4&&ChannelUtil.isUnionShouldShow(C.get())){
                    break;
                }
                list.get(i).btn_title=iconInfo.imgName;
                list.get(i).src=iconInfo.src;
                list.get(i).typeId=iconInfo.typeId;
                list.get(i).typeVal=iconInfo.typeVal;

            }
        }



        return list;

    }


    @Override
    public void confirmDataSaveNeeded(ConfirmData confirmData) {
        confirmData.dataKey = "special_data_new25";
        confirmData.timeKey = "special_time_new25";
        confirmData.type = TYPE_RECOMMEND;
        confirmData.ignoreInstalledApp = true;
        confirmData.downloadPushEvent = StatisticEventContants.main_list_download;
        confirmData.itemClickToDetailPushEvent = StatisticEventContants.main_list_appdetail_download;
        confirmData.topicItemClickPushEvent = StatisticEventContants.Main_List_Topic;
        confirmData.itemClickPushEvent = StatisticEventContants.main_list_appdetail;
    }

    @Override
    public void scrollBackTop() {
        scrollToListTop();
    }

    public void showRedPoint() {
        String pastDate = C.getPreference(FastinstallActivity.HOT_SHOW_RED_POINT, "");
        String todayDate = AppUtils.getTodayDate();
        boolean flag = C.getPreference(HOT_RED_POINT, true);
        if (flag && "WiFi".equals(ApplicationUtils.getNetworkType(getActivity())) && !TextUtils.isEmpty(pastDate)) {
            if (Integer.parseInt(todayDate) > Integer.parseInt(pastDate)) {
                C.setPreference(HOT_RED_POINT, true);
            }
        }
    }

    public void readyShowActFloat() {
        actEnter.setImageURI(UriUtil.parse(floatActData.icon));
        showActFloatButton();
        setListviewScrollListener();
    }

    private void setListviewScrollListener() {
        actEnter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                TopicUtils.startTopic(getActivity(), floatActData, FLOAT_ACT);
            }
        });
        if (refreshListView != null) {
            refreshListView.setListviewScrollListener(new OnListviewScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dy <= 0) {
                        if (scrollY <= 0) {
                            scrollY += dy;
                        } else {
                            scrollY = dy;
                        }
                        if (Math.abs(scrollY) >= getResources().getDimensionPixelSize(R.dimen.default_50dp)) {
                            showActFloatButton();
                        }
                    } else {
                        if (scrollY >= 0) {
                            scrollY += dy;
                        } else {
                            scrollY = dy;
                        }
                        if (Math.abs(scrollY) >= getResources().getDimensionPixelSize(R.dimen.default_50dp)) {
                            hideActFloatButton();
                        }
                    }
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                }
            });
        }
    }

    private boolean onMoving = false;

    private void showActFloatButton() {
        needShow = true;
        needHide = false;
        if (!isshow && !onMoving) {
            isshow = true;
            actEnter.setVisibility(View.VISIBLE);
            ObjectAnimator animator = ObjectAnimator.ofFloat(actEnter, "translationY", SPUtil.dip2px(C.get(), 200), 0F);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    onMoving = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    isshow = true;
                    onMoving = false;
                    needShow = false;
                    if (needHide) {
                        hideActFloatButton();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });//Y轴平移
            animator.setDuration(400);
            animator.start();
        }
    }

    private void hideActFloatButton() {
        needShow = false;
        needHide = true;
        if (isshow && !onMoving) {
            isshow = false;
            ObjectAnimator animator = ObjectAnimator.ofFloat(actEnter, "translationY", 0F, SPUtil.dip2px(C.get(), 200));
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    onMoving = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    isshow = false;
                    onMoving = false;
                    needHide = false;
                    if (needShow) {
                        showActFloatButton();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });//Y轴平移
            animator.setDuration(600);
            animator.start();
        }
    }

    private void setFloatGone() {
        if (refreshListView != null) {
            refreshListView.setListviewScrollListener(null);
        }
        if (actEnter != null) {
            hideActFloatButton();
            scrollY = 0;
            needShow = false;
            needHide = false;
        }
    }

*/
/*    @Override
    public void showFloatActView(ActItem item) {
        if (isAdded() && item != null && actEnter != null) {
            floatActData = item;
            //图片下载成功后，先将入口隐藏，然后开始做展示操作，使之出现由下往上的出现动画
            if (SettingUtils.checkLastSetValue(C.get(), SettingUtils.SETTING.START_ACTIVITY_ENTER, true)) {
                readyShowActFloat();
            }
        }
    }*//*


    */
/*@Override
    public void showBannerActView(ActItem item) {
        if (isAdded() && item != null) {
            bannerActData = item;
            if (SettingUtils.checkLastSetValue(C.get(), SettingUtils.SETTING.START_ACTIVITY_ENTER, true)
                    && mBannerAct != null && mBannerActArea != null) {
                ViewGroup.LayoutParams params = mBannerAct.getLayoutParams();
                int width = (int) DataCacheForViewPager.getInstance().get(DataCacheForViewPager.WINDOW_WIDTH);
                params.width = width;
                params.height = width / 5;
                mBannerAct.setLayoutParams(params);
                mBannerActShadow.setLayoutParams(params);
                mBannerAct.setImageURI(UriUtil.parse(bannerActData.icon));
                mBannerActArea.setVisibility(View.VISIBLE);
            }
        }
    }*//*


    */
/*@Override
    public void hideBannerActView() {
        bannerActData = null;
        hideBannerAct();
    }*//*


    private void hideBannerAct() {
        if (mBannerActArea != null) {
            mBannerActArea.setVisibility(View.GONE);
        }
    }

    public interface OnListviewScrollListener {
        void onScrolled(RecyclerView recyclerView, int dx, int dy);

        void onScrollStateChanged(RecyclerView recyclerView, int newState);
    }


}
*/
