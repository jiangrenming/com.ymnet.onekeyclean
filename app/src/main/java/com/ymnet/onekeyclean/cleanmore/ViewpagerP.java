/*
package com.ymnet.onekeyclean.cleanmore;

<<<<<<< HEAD
=======
import android.net.Uri;
>>>>>>> guest
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.Tab;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.ymnet.onekeyclean.R;
<<<<<<< HEAD

public class ViewpagerP extends FragmentActivity {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
=======
import com.ymnet.onekeyclean.cleanmore.fragment.testfragment.Fragment1;
import com.ymnet.onekeyclean.cleanmore.fragment.testfragment.Fragment2;
import com.ymnet.onekeyclean.cleanmore.fragment.testfragment.Fragment3;
import com.ymnet.onekeyclean.cleanmore.fragment.testfragment.Fragment4;


public class ViewpagerP extends FragmentActivity implements Fragment1.OnFragmentInteractionListener, Fragment2.OnFragmentInteractionListener,Fragment3.OnFragmentInteractionListener,Fragment4.OnFragmentInteractionListener{

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private MyFragmentPagerAdapter myFragmentPagerAdapter;
>>>>>>> guest

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpager_p);
        initView();
    }

    private void initView() {
        //使用适配器将ViewPager与Fragment绑定在一起
<<<<<<< HEAD
        mViewPager= (ViewPager) findViewById(R.id.viewPager);
=======
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
>>>>>>> guest
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
        four.setIcon(R.mipmap.robot);

    }

<<<<<<< HEAD
    public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        private String[] mTitles = new String[]{"首页", "发现", "进货单","我的"};
=======
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        private String[] mTitles = new String[]{"首页", "发现", "进货单", "我的"};
>>>>>>> guest

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
<<<<<<< HEAD
            */
/*if (position == 1) {
                return new Fragment2();
            } else if (position == 2) {
                return new Fragment3();
            }else if (position==3){
                return new Fragment4();
            }
            return new Fragment1();*//*
=======
            if (position == 1) {
                return new Fragment2();
            } else if (position == 2) {
                return new Fragment3();
            } else if (position == 3) {
                return new Fragment4();
            }
            return new Fragment1();
>>>>>>> guest

        }

        @Override
        public int getCount() {
            return mTitles.length;
        }

        //ViewPager与TabLayout绑定后，这里获取到PageTitle就是Tab的Text
        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }
    }

<<<<<<< HEAD
=======

>>>>>>> guest
}
*/
