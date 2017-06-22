package com.ymnet.onekeyclean.cleanmore;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.fragment.mainfragment.Fragment2;
import com.ymnet.onekeyclean.cleanmore.fragment.mainfragment.Fragment3;
import com.ymnet.onekeyclean.cleanmore.fragment.mainfragment.Fragment4;
import com.ymnet.onekeyclean.cleanmore.fragment.mainfragment.HomeFragment;


public class HomeActivity extends FragmentActivity implements HomeFragment.OnFragmentInteractionListener, Fragment2.OnFragmentInteractionListener, Fragment3.OnFragmentInteractionListener, Fragment4.OnFragmentInteractionListener {

    private ViewPager              mViewPager;
    private TabLayout              mTabLayout;
    private MyFragmentPagerAdapter myFragmentPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
    }

    private void initView() {

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(myFragmentPagerAdapter);
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
            return new HomeFragment();

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // TODO: 2017/6/16 0016 再传递数据给对应的fragment

    }
}
