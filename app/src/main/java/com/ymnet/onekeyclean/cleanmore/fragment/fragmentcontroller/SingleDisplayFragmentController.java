package com.ymnet.onekeyclean.cleanmore.fragment.fragmentcontroller;

import android.support.v4.app.FragmentManager;

import com.ymnet.onekeyclean.cleanmore.fragment.BaseFragment;


/**
 * @author janan
 * @version since
 * @date 2014-6-18 下午4:17:16
 * @description 用于指定viewid下多个fragment的切换， 被指定管理的fragment必须是同一view id下的fragment
 */
public class SingleDisplayFragmentController extends BaseFragmentController
{

    private String currentTag;//当前显示的fragment的tag

    private BaseFragment currentFragment;

    private String currentTagBack;//当前显示的fragment的tag备份

    private int viewId;

    public SingleDisplayFragmentController(FragmentManager manager, int viewId)
    {
        super(manager);
        this.viewId = viewId;
    }

    @Override
    public void commit()
    {
        super.commit();
        reset();
    }

    @Override
    public void beginNewTransaction()
    {
        if(!isCommited()){
            resetWithUnDo();
        }
        super.beginNewTransaction();
        cacheTag();
    }

    public BaseFragment getCurrentFragment()
    {
        BaseFragment frag = null;
        if (isCommited())
        {
            frag = (BaseFragment) mFragmentManager.findFragmentByTag(currentTag);
        }
        else
        {
            frag = (BaseFragment) mFragmentManager.findFragmentByTag(currentTagBack);
        }
        return frag;
    }

    public BaseFragment getcurrentFragmentForWDH(){

        return currentFragment;

    }

    /**
     * 切换显示的fragment
     * @param fragment
     *            要展示的fragment 注：指定的viewid的当前fragment必须是通过该controller添加并展示的
     * */
    public void changeDisplayFragment(BaseFragment fragment)
    {
        if (!containsFragment(fragment))
            addFragment(viewId, fragment);
        BaseFragment currentFrag = getCurrentFragment();
        if (currentFrag != null)
            hideFragment(currentFrag);
        showFragment(fragment);
        currentTag = fragment.getSupportTag();
        currentFragment = fragment;
    }

    /**
     * 切换显示的fragment
     * @param tag
     *            要展示的fragment的tag 注：指定的viewid的当前fragment必须是通过该controller添加并展示的
     * */
    public void changeDisplayFragment(String tag)
    {
        changeDisplayFragment(findFragmentByTag(tag));
    }

    public void addFragmentLazy(int id, BaseFragment fragment,boolean commitImmediately)
    {
        beginNewTransaction();
        addFragment(id, fragment);
        commitLocal(commitImmediately);
    }

    public void replaceFragmentLazy(int id, BaseFragment fragment,boolean commitImmediately)
    {
        beginNewTransaction();
        replaceFragment(id, fragment);
        commitLocal(commitImmediately);

    }

    public void showFragmentLazy(BaseFragment fragment,boolean commitImmediately)
    {
        beginNewTransaction();
        showFragment(fragment);
        commitLocal(commitImmediately);
    }

    public void hideFragmentLazy(BaseFragment fragment,boolean commitImmediately)
    {
        beginNewTransaction();
        hideFragment(fragment);
        commitLocal(commitImmediately);
    }

    public void removeFragmentLazy(BaseFragment fragment,boolean commitImmediately)
    {
        beginNewTransaction();
        removeFragment(fragment);
        commitLocal(commitImmediately);
    }

    private void commitLocal(boolean commitImmediately){
        if(commitImmediately){
            commitImmediately();
        }else{
            commit();
        }
    }

    private void reset()
    {//commit后的复位
        commitToggle(true);
        transaction = null;
    }

    private void resetWithUnDo()
    {//放弃原子操作的复位
        reset();
        resetTag();
    }

    private void cacheTag()
    {
        currentTagBack = currentTag;
    }

    private void resetTag()
    {
        currentTag = currentTagBack;
    }
}
