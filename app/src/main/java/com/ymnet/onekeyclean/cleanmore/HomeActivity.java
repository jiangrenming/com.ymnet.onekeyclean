package com.ymnet.onekeyclean.cleanmore;


import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.commonlibrary.utils.ConvertParamsUtils;
import com.umeng.analytics.MobclickAgent;
import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.fragment.mainfragment.Fragment2;
import com.ymnet.onekeyclean.cleanmore.fragment.mainfragment.Fragment3;
import com.ymnet.onekeyclean.cleanmore.fragment.mainfragment.Fragment4;
import com.ymnet.onekeyclean.cleanmore.fragment.mainfragment.HomeFragment;
import com.ymnet.onekeyclean.cleanmore.junk.ScanHelp;
import com.ymnet.onekeyclean.cleanmore.utils.C;
import com.ymnet.onekeyclean.cleanmore.utils.OnekeyField;
import com.ymnet.onekeyclean.cleanmore.web.JumpUtil;
import com.ymnet.retrofit2service.RetrofitService;
import com.ymnet.retrofit2service.bean.NewsInformation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ymnet.retrofit2service.api.GithubApi.url;


public class HomeActivity extends ImmersiveActivity implements Fragment2.OnFragmentInteractionListener, Fragment3.OnFragmentInteractionListener, Fragment4.OnFragmentInteractionListener {

    private static final String TAG = "HomeActivity";
    private static HomeActivity mInstance;
    private ViewPager mViewPager;
    private TabLayout              mTabLayout;
    private MyFragmentPagerAdapter myFragmentPagerAdapter;
    public  FragmentManager        mFM;
    public static boolean isVisible;
    private ImageView mAdvertisement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        String stringExtra = getIntent().getStringExtra(OnekeyField.ONEKEYCLEAN);
        String statistics_key = getIntent().getStringExtra(OnekeyField.STATISTICS_KEY);
        if (stringExtra != null) {
            Map<String, String> m = new HashMap<>();
            m.put(OnekeyField.ONEKEYCLEAN, stringExtra);
            MobclickAgent.onEvent(this, statistics_key, m);
        }

        mFM = getSupportFragmentManager();
        initView();
    }

    public static HomeActivity getInstance() {

        if (mInstance == null) {
            synchronized (ScanHelp.class) {
                if (mInstance == null) {
                    mInstance = new HomeActivity();
                }
            }
        }
        return mInstance;
    }


    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isVisible = false;
    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(myFragmentPagerAdapter);

        initAdvertisement();
       /* //使用适配器将ViewPager与Fragment绑定在一起

        mViewPager = (ViewPager) findViewById(R.id.viewPager);

        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(myFragmentPagerAdapter);

        //将TabLayout与ViewPager绑定在一起
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mTabLayout.setupWithViewPager(mViewPager);

        //指定Tab的位置
        Tab one = mTabLayout.getTabAt(0);
        Tab two = mTabLayout.getTabAt(1);
        Tab three = mTabLayout.getTabAt(2);
        Tab four = mTabLayout.getTabAt(3);

        //设置Tab的图标，假如不需要则把下面的代码删去
        one.setIcon(R.mipmap.robot);
        two.setIcon(R.mipmap.robot);
        three.setIcon(R.mipmap.robot);
        four.setIcon(R.mipmap.robot);*/

    }

    private void initAdvertisement() {
        mAdvertisement = (ImageView) findViewById(R.id.iv_clean_advertisement);
//        requestData();

    }

    private void requestData() {
        Map<String, String> infosPamarms = ConvertParamsUtils.getInstatnce().getParamsOne("","");

        RetrofitService.getInstance().githubApi.createInfomationsTwo(infosPamarms).enqueue(new Callback<NewsInformation>() {
            @Override
            public void onResponse(Call<NewsInformation> call, Response<NewsInformation> response) {
                if (response.raw().body() != null) {
                    NewsInformation newsInformation = response.body();
                    int count = newsInformation.getCount();
//                    adapter.setTotalCount(count);
                    List<NewsInformation.DataBean> data = newsInformation.getData();
                    Log.d(TAG, "onResponse: data:" + data.toString());

//                    moreData.addAll(data);
//                    adapter.notifyDataSetChanged();
                    //如果获取的数据需要展示
                    mAdvertisement.setVisibility(View.VISIBLE);
                    //else
                    mAdvertisement.setVisibility(View.GONE);
                    mAdvertisement.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            JumpUtil.getInstance().unJumpAddress(C.get(), url, 10);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<NewsInformation> call, Throwable t) {
            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        private String[] mTitles = new String[]{"首页", "发现", "常用", "我的"};

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            //勿删,之后的其他界面
            /*if (position == 1) {
                return new Fragment2();
            } else if (position == 2) {
                return new Fragment3();
            } else if (position == 3) {
                return new Fragment4();
            }*/
            //            return new HomeFragment();
            return HomeFragment.newInstance();
        }

        @Override
        public int getCount() {
            //            return mTitles.length;
            return 1;
        }

        //ViewPager与TabLayout绑定后，这里获取到PageTitle就是Tab的Text
        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isClearAll = true;
        exit();
    }

    @Override
    public void finish() {
        super.finish();
    }
}
