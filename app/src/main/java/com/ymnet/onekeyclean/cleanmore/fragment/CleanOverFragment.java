package com.ymnet.onekeyclean.cleanmore.fragment;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.commonlibrary.retrofit2service.RetrofitService;
import com.example.commonlibrary.retrofit2service.bean.InformationResult;
import com.example.commonlibrary.retrofit2service.bean.NewsInformation;
import com.example.commonlibrary.utils.ConvertParamsUtils;
import com.nineoldandroids.view.ViewHelper;
import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.customview.DividerItemDecoration;
import com.ymnet.onekeyclean.cleanmore.customview.RecyclerViewPlus;
import com.ymnet.onekeyclean.cleanmore.junk.adapter.RecommendAdapter;
import com.ymnet.onekeyclean.cleanmore.qq.activity.QQActivity;
import com.ymnet.onekeyclean.cleanmore.utils.C;
import com.ymnet.onekeyclean.cleanmore.utils.CleanSetSharedPreferences;
import com.ymnet.onekeyclean.cleanmore.utils.DisplayUtil;
import com.ymnet.onekeyclean.cleanmore.utils.FormatUtils;
import com.ymnet.onekeyclean.cleanmore.utils.ToastUtil;
import com.ymnet.onekeyclean.cleanmore.utils.Util;
import com.ymnet.onekeyclean.cleanmore.web.WebHtmlActivity;
import com.ymnet.onekeyclean.cleanmore.wechat.WeChatActivity;
import com.ymnet.onekeyclean.cleanmore.wechat.device.DeviceInfo;
import com.ymnet.onekeyclean.cleanmore.wechat.listener.RecyclerViewClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 垃圾清理完成界面
 */
public class CleanOverFragment extends BaseFragment implements View.OnClickListener {

    private              String TAG             = "CleanOverFragment";
    private static final String ARG_PARAM1      = "param1";
    public static final  int    HAS_CLEAN_CACHE = -1;

    private Long             deleteSize;
    private ImageView        iv_sun;
    private ImageView        iv_sun_center;
    private TextView         tv_clean_success_size;
    private TextView         tv_history_clean_size;
    private ImageView        iv_high_light;
    private RecyclerViewPlus rv;
    private RecommendAdapter adapter;
    //信息流相关
    private int page = 1;
    private ImageView     mBlingBling;
    private WindowManager mWindowManager;

    public static CleanOverFragment newInstance(Long size) {
        CleanOverFragment fragment = new CleanOverFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, size);
        fragment.setArguments(args);
        return fragment;
    }

    public CleanOverFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            deleteSize = getArguments().getLong(ARG_PARAM1);
        }
        mWindowManager = (WindowManager) C.get().getSystemService(Context.WINDOW_SERVICE);
    }

    private AnimatorSet initAnimSet() {
        FragmentActivity context = getActivity();
        AnimatorSet animSet = new AnimatorSet();
        Animator anim = AnimatorInflater.loadAnimator(context,
                R.animator.anim_clean_complete);
        Animator anim2 = AnimatorInflater.loadAnimator(context,
                R.animator.anim_clean_complete_center);// 透明度+缩放动画
        Animator anim3 = AnimatorInflater.loadAnimator(context,
                R.animator.anim_clean_complete_alpha);
        Animator anim4 = AnimatorInflater.loadAnimator(context,
                R.animator.anim_clean_complete_alpha);// 透明度动画
        Animator anim5 = AnimatorInflater.loadAnimator(context,
                R.animator.high_light_translate);// 高亮平移
        Animator anim7 = AnimatorInflater.loadAnimator(context,
                R.animator.from_buttom_to_top);// Button下往上移
        anim2.setDuration(800);
        anim3.setDuration(500);
        anim4.setDuration(800);
        anim7.setDuration(500);

        anim.setTarget(iv_sun);
        anim2.setTarget(iv_sun_center);
        anim3.setTarget(tv_clean_success_size);
        anim4.setTarget(tv_history_clean_size);
        //        anim5.setTarget(iv_high_light);
        //        anim6.setTarget(btn_continue);
        anim7.setTarget(ll_content);

        animSet.play(anim).with(anim2);
        animSet.play(anim).before(anim3);
        animSet.play(anim3).with(anim4);
        animSet.play(anim3).with(anim5);
        animSet.play(anim7).after(anim3);
        iv_high_light.setVisibility(View.VISIBLE);
        return animSet;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_clean_finish, container, false);
    }

    private View ll_content;
    private View head;
    private int  height;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View rootView = getView();
        if (rootView == null)
            return;
        // find content view
        iv_sun = (ImageView) rootView.findViewById(R.id.iv_sun);
        iv_sun_center = (ImageView) rootView.findViewById(R.id.iv_sun_center);
        iv_high_light = (ImageView) rootView.findViewById(R.id.iv_high_light);

        mBlingBling = (ImageView) rootView.findViewById(R.id.iv_blingbling);
        mBlingBling.setImageResource(R.drawable.bling_anim);

        tv_clean_success_size = (TextView) rootView.findViewById(R.id.tv_clean_success_size);
        tv_history_clean_size = (TextView) rootView.findViewById(R.id.tv_history_clean_size);
        ll_content = rootView.findViewById(R.id.ll_content);
        rv = (RecyclerViewPlus) rootView.findViewById(R.id.rv_recommend);

        height = DeviceInfo.getScreenHeight(getActivity());

        ViewHelper.setAlpha(iv_sun_center, 0.0f);
        ViewHelper.setAlpha(tv_clean_success_size, 0.0f);
        ViewHelper.setAlpha(tv_history_clean_size, 0.0f);
        ViewHelper.setTranslationY(ll_content, height);
        ViewHelper.setAlpha(iv_sun, 0f);
        ViewHelper.setRotation(iv_sun, 0f);
        iv_high_light.setVisibility(View.GONE);


        initData();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startAnimation();
            }
        }, 500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    AnimatorSet set = initAnimSet();
                    set.start();


                } catch (Exception e) {
                    ViewHelper.setAlpha(iv_sun_center, 1);
                    ViewHelper.setAlpha(tv_clean_success_size, 1);
                    ViewHelper.setAlpha(tv_history_clean_size, 1);
                    ViewHelper.setAlpha(iv_sun, 1);
                    ViewHelper.setRotation(iv_sun, 0f);
                    ViewHelper.setTranslationY(ll_content, 0);
                }
            }
        }, 100);

    }

    private void startAnimation() {

        mBlingBling.setVisibility(View.VISIBLE);

        AnimationDrawable animationDrawable = (AnimationDrawable) mBlingBling.getDrawable();
        if (animationDrawable.isRunning()) {
            animationDrawable.stop();
        }
        animationDrawable.start();

        //获取动画时间,执行之后移除
        int numberOfFrames = animationDrawable.getNumberOfFrames();
        int duration = 0;
        for (int i = 0; i < numberOfFrames; i++) {
            duration += animationDrawable.getDuration(i);
        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                mWindowManager.removeView(mBlingBling);
                mBlingBling.setVisibility(View.GONE);

            }
        }, duration + 100);
    }

    List<InformationResult> moreData;

    private void initData() {

        String str0;
        if (deleteSize == HAS_CLEAN_CACHE) {
            tv_clean_success_size.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            tv_clean_success_size.setText(getString(R.string.no_found_junk));
        } else if (deleteSize == 0) {
            str0 = getString(R.string.so_beautiful);
            tv_clean_success_size.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            tv_clean_success_size.setText(str0);
        } else {
            str0 = FormatUtils.formatFileSize(CleanSetSharedPreferences.getLaseTimeSize(getActivity(), deleteSize));
            String text = getString(R.string.clean_success_size, str0);
            //            tv_clean_success_size.setText(Html.fromHtml(text));
            tv_clean_success_size.setText(Util.getSpannableString(text));
        }
        String str1 = FormatUtils.formatFileSize(CleanSetSharedPreferences.getTodayCleanSize(getActivity(), deleteSize));
        String str2 = FormatUtils.formatFileSize(CleanSetSharedPreferences.getTotalCleanSize(getActivity(), deleteSize));
        tv_history_clean_size.setText(getString(R.string.today_clean_total_clean, str1, str2));

        DividerItemDecoration did = new DividerItemDecoration(C.get(), R.drawable.recyclerview_driver_1_bg);
        rv.addItemDecoration(did);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        head = LayoutInflater.from(getActivity()).inflate(R.layout.clean_over_head, rv, false);
        head.findViewById(R.id.rl_wechat).setOnClickListener(this);
        head.findViewById(R.id.rl_qq).setOnClickListener(this);


        moreData = new ArrayList<>();
        //获取网络数据
        getNewsInformation(moreData);

        adapter = new RecommendAdapter(moreData);//更多应用推荐
        adapter.addHeaderView(new RecyclerViewPlus.HeaderFooterItemAdapter.ViewHolderWrapper() {
            @Override
            protected View onCreateView(ViewGroup parent) {
                return head;
            }
        });
        adapter.addFooterView(new RecyclerViewPlus.HeaderFooterItemAdapter.ViewHolderWrapper() {
            @Override
            protected View onCreateView(ViewGroup parent) {
                RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DisplayUtil.dip2px(C.get(), 15));
                View foot = new View(getActivity());
                foot.setLayoutParams(lp);
                foot.setBackgroundColor(Color.TRANSPARENT);
                return foot;
            }
        });
        adapter.setRecyclerListListener(new RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                if (position >= moreData.size())
                    return;

                InformationResult info = moreData.get(position);
                String news_url = info.getNews_url();

                Intent intent = new Intent(getActivity(), WebHtmlActivity.class);
                intent.putExtra("html", news_url);
                intent.putExtra("flag", 20);
                startActivity(intent);
                //                delayedFinish();
            }
        });
        rv.setAdapter(adapter);
        //        loadFromServer();
    }


    @Override
    public void setSupportTag(String tag) {

    }

    @Override
    public String getSupportTag() {
        return null;
    }

    @Override
    public void showSelf() {

    }

    @Override
    public void onClick(View v) {
        Intent it;
        int i = v.getId();
        if (i == R.id.rl_wechat) {
            it = new Intent(getActivity(), WeChatActivity.class);
            startActivity(it);
            delayedFinish();
        } else if (i == R.id.rl_qq) {
            it = new Intent(getActivity(), QQActivity.class);
            startActivity(it);
            delayedFinish();
        }
    }

    private void delayedFinish() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentActivity activity = getActivity();
                if (activity == null || activity.isFinishing())
                    return;
                activity.finish();
            }
        }, 300);
    }

    // TODO: 2017/5/25 0025
    //网络获取新闻数据
    private void getNewsInformation(final List<InformationResult> moreData) {
        Map<String, String> infosPamarms = ConvertParamsUtils.getInstatnce().getParamsTwo("type", "all", "p", String.valueOf(page));
        RetrofitService.getInstance().githubApi.createInfomationsTwo(infosPamarms).enqueue(new Callback<NewsInformation>() {
            @Override
            public void onResponse(Call<NewsInformation> call, Response<NewsInformation> response) {
                if (response.raw().body() != null) {
                    NewsInformation newsInformation = response.body();
                    int count = newsInformation.getCount();
                    ArrayList<InformationResult> data = newsInformation.getData();
                    Log.d(TAG, "onResponse: data:" + data);
                    /*if (data != null && data.size() != 0) {
                        recyclerView.setTotalCount(count);
                        if (flag == 10) {
                            recyclerView.endRefresh();
                            recyclerView.setData(data);
                        } else {
                            recyclerView.endLoadMore();
                            recyclerView.addData(data);
                        }
                    }*/
                    moreData.addAll(data);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<NewsInformation> call, Throwable t) {
                //                recyclerView.endRefresh();
                ToastUtil.showToastForShort("网络异常,请检查网络...");
            }
        });

    }
  /*  protected PageCall<ListAppEntity>                 mPageCall;
    private Callback<PageListResponse<ListAppEntity>> mCallBack;*/

    /*private void loadFromServer() {
        if (mPageCall == null) {
            mPageCall = createPageCall();
        }
        if (mCallBack == null) {
            mCallBack = createCallBack();
        }
        mPageCall.enqueue(mCallBack);
    }*/

    /*private Callback<PageListResponse<ListAppEntity>> createCallBack() {
        return new Callback<PageListResponse<ListAppEntity>>() {
            @Override
            public void onResponse(Call<PageListResponse<ListAppEntity>> call, PageListResponse<ListAppEntity> response) {
                if (isAdded()) {
                    if (MHttp.responseOK(response.getCode())) {
                        AppListDatas mDatas = transform(response);
                        if (mDatas != null && mDatas.bufferApps != null && mDatas.bufferApps.list != null) {
                            ArrayList<App> appsList = mDatas.bufferApps.list;
                            data.clear();
                            data.addAll(new ModulePresenter().filter(appsList, 15));
                            adapter.notifyDataSetChanged();
                        }
                    }

                }
            }

            @Override
            public void onFailure(Call<PageListResponse<ListAppEntity>> call, Throwable t) {

            }
        };
    }*/

   /* protected PageCall<ListAppEntity> createPageCall() {
        return TApier.get().getSoftListByRecom(getString(R.string.type_single_game));
    }*/

  /*  protected AppListDatas transform(PageListResponse<ListAppEntity> response) {
        return new AppListDatasMapper.EntityList2AppListDatasMapper().transform(response);
    }*/


}
