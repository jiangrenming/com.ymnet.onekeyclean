package com.ymnet.onekeyclean.cleanmore.fragment.fragmentcontroller;


import com.ymnet.onekeyclean.cleanmore.fragment.BaseFragment;

/**
 * @author janan
 * @version since
 * @date 2014-8-21 上午10:54:02
 * @description
 */
public interface IBaseFragmentController
{
    
    /**
     * 删除所有已添加的fragment
     * */
    public void removeAll();         
    
    /**
     * 判断该fragment是否被添加到该controller中
     * @param fragment
     * @return boolean
     * */
    public boolean containsFragment(BaseFragment fragment);
    
    /**
     * 判断该fragment是否被添加到该controller中
     * @param tag
     * @return boolean
     * */
    public boolean containsFragment(String tag);
    
    /**
     * 查找指定tag所对应的fragment
     * @param tag
     * @return BaseFragment
     * */
    public BaseFragment findFragmentByTag(String tag);
    
    /**
     * 查找指定tag所对应的fragment
     * @param viewId
     * @return BaseFragment
     * */
    public BaseFragment findFragmentById(int viewId);
    
    /**
     * 添加fragment到指定的viewid下
     * @param id
     * @param fragment
     * */
    public void addFragment(int id, BaseFragment fragment);
    
    /**
     * 替换fragment到指定的viewid
     * @param id
     * @param fragment
     * */
    public void replaceFragment(int id, BaseFragment fragment);
    
    /**
     * 显示指定的fragment
     * @param fragment
     * */
    public void showFragment(BaseFragment fragment);
    
    /**
     * 隐藏指定的fragment
     * @param fragment
     * */
    public void hideFragment(BaseFragment fragment);
    
    /**
     * 删除指定的fragment
     * @param fragment
     * */
    public void removeFragment(BaseFragment fragment);
    
    /**
     *提交任务
     * */
    public void commit();
    
    /**
     * 开始一个新任务
     * 
     * */
    public void beginNewTransaction();
    /**
     * 是否有添加的fragment
     * */
    public boolean isEmpty();
    
    /**
     * 是否已经提交
     * 
     * */     
    public boolean isCommited();
}
