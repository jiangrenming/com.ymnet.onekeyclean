package com.ymnet.onekeyclean.cleanmore.fragment.fragmentcontroller;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.ymnet.onekeyclean.cleanmore.fragment.BaseFragment;


/**
 * @author janan
 * @version since
 * @date 2014-8-27 下午12:59:24
 * @description
 */
public class BaseFragmentController implements IBaseFragmentController
{

    protected FragmentManager mFragmentManager;

    protected FragmentTransaction transaction = null;

    private boolean isCommited = true;

    public BaseFragmentController(FragmentManager manager)
    {
        this.mFragmentManager = manager;
    }

    @Override
    public void removeAll()
    {
        FragmentTransaction localTransaction = mFragmentManager.beginTransaction();
        for(int i=0;i<mFragmentManager.getFragments().size();i++){
            localTransaction.remove(mFragmentManager.getFragments().get(i));
        }
        localTransaction.commitAllowingStateLoss();
    }

    @Override
    public boolean containsFragment(BaseFragment fragment)
    {
        return containsFragment(fragment.getSupportTag());
    }

    @Override
    public boolean containsFragment(String tag)
    {
        return mFragmentManager.findFragmentByTag(tag) != null;
    }

    @Override
    public BaseFragment findFragmentByTag(String tag)
    {
        return (BaseFragment) mFragmentManager.findFragmentByTag(tag);
    }

    @Override
    public BaseFragment findFragmentById(int viewId)
    {

        return (BaseFragment) mFragmentManager.findFragmentById(viewId);
    }

    @Override
    public void addFragment(int id, BaseFragment fragment)
    {
        transaction.add(id, fragment, fragment.getSupportTag());
    }

    @Override
    public void replaceFragment(int id, BaseFragment fragment)
    {
        transaction.replace(id, fragment, fragment.getSupportTag());
    }

    @Override
    public void showFragment(BaseFragment fragment)
    {
        transaction.show(fragment);
    }

    @Override
    public void hideFragment(BaseFragment fragment)
    {
        transaction.hide(fragment);
    }

    @Override
    public void removeFragment(BaseFragment fragment)
    {
        transaction.remove(fragment);
    }

    @Override
    public void commit()
    {
        transaction.commitAllowingStateLoss();
        commitToggle(true);
    }

    /**
     * excute commit in main thread
     * */
    public void commitImmediately(){
        transaction.commitAllowingStateLoss();
        mFragmentManager.executePendingTransactions();
        commitToggle(true);
    }

    @Override
    public void beginNewTransaction()
    {
        transaction = mFragmentManager.beginTransaction();
        commitToggle(false);
    }

    @Override
    public boolean isEmpty()
    {
        return mFragmentManager.getFragments() == null || mFragmentManager.getFragments().size() == 0;
    }

    @Override
    public boolean isCommited() {
        return isCommited;
    }

    public void commitToggle(boolean committed){
        isCommited = committed;
    }

}
