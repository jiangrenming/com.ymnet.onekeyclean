package com.ymnet.onekeyclean.cleanmore.fragment;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.filebrowser.FileCategoryActivity;
import com.ymnet.onekeyclean.cleanmore.utils.CleanSetSharedPreferences;
import com.ymnet.onekeyclean.cleanmore.utils.FormatUtils;
import com.ymnet.onekeyclean.cleanmore.utils.Util;
import com.ymnet.onekeyclean.cleanmore.wechat.WeChatActivity;
import com.ymnet.onekeyclean.cleanmore.wechat.device.DeviceInfo;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link CleanOverFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CleanOverFragment extends BaseFragment implements View.OnClickListener {
    private static final String ARG_PARAM1 = "param1";
    public static final int HAS_CLEAN_CACHE = -1;


    private Long deleteSize;
    private ImageView iv_sun;
    private ImageView iv_sun_center;
    private TextView tv_clean_success_size;
    private TextView tv_history_clean_size;
    private ImageView iv_high_light;
    private int height;

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

    }

    private AnimatorSet initAnimSet() {
        FragmentActivity context = getActivity();
        AnimatorSet animSet = new AnimatorSet();
        Animator anim = AnimatorInflater.loadAnimator(context,
                R.animator.anim_clean_complete);
        Animator anim2 = AnimatorInflater.loadAnimator(context,
                R.animator.anim_clean_complete);// 透明度+缩放动画
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
        anim5.setTarget(iv_high_light);
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

    private View             ll_content;
//    private RecyclerViewPlus rv;
    private View             head;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View rootView = getView();
        if (rootView == null) return;
        // find content view
        iv_sun = (ImageView) rootView.findViewById(R.id.iv_sun);
        iv_sun_center = (ImageView) rootView.findViewById(R.id.iv_sun_center);
        iv_high_light = (ImageView) rootView.findViewById(R.id.iv_high_light);
        tv_clean_success_size = (TextView) rootView.findViewById(R.id.tv_clean_success_size);
        tv_history_clean_size = (TextView) rootView.findViewById(R.id.tv_history_clean_size);
        ll_content = rootView.findViewById(R.id.ll_content);
//        rv = (RecyclerViewPlus) rootView.findViewById(R.id.rv_recommend);

        height = DeviceInfo.getScreenHeight(getActivity());

        ViewHelper.setAlpha(iv_sun_center, 0.0f);
        ViewHelper.setAlpha(tv_clean_success_size, 0.0f);
        ViewHelper.setAlpha(tv_history_clean_size, 0.0f);
        ViewHelper.setTranslationY(ll_content, height);
        ViewHelper.setAlpha(iv_sun, 0f);
        iv_high_light.setVisibility(View.GONE);
        initData();
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
                    ViewHelper.setTranslationY(ll_content, 0);
                }
            }
        }, 100);

    }

    // TODO: 2017/4/27 0027 最后
//    List<App>        data;
//    RecommendAdapter adapter;

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


        /*DividerItemDecoration did = new DividerItemDecoration(C.get(), R.drawable.recyclerview_driver_1_bg);
        rv.addItemDecoration(did);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        head = LayoutInflater.from(getActivity()).inflate(R.layout.clean_over_head, rv, false);
        head.findViewById(R.id.rl_wechat).setOnClickListener(this);
        head.findViewById(R.id.rl_file).setOnClickListener(this);
        data = new ArrayList<>();
        adapter = new RecommendAdapter(getActivity(), data);//更多应用推荐
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
                if (position >= data.size()) return;
                App app = data.get(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(App.class.getSimpleName(), app);
                intent.putExtra("clicktoevent", StatisticEventContants.clean_finish_recommend_detail_download);
                startActivity(intent);
                delayedFinish();
            }
        });
        rv.setAdapter(adapter);*/
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
            //                StatisticSpec.sendEvent(StatisticEventContants.clean_finish_cleanwechat);

        } else if (i == R.id.rl_file) {
            it = new Intent(getActivity(), FileCategoryActivity.class);
            startActivity(it);
            delayedFinish();
            //                StatisticSpec.sendEvent(StatisticEventContants.clean_finish_filemanage);

        }
    }

    private void delayedFinish() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentActivity activity = getActivity();
                if (activity == null || activity.isFinishing()) return;
                activity.finish();
            }
        }, 300);
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
