package com.yment.killbackground.view;

/**
 * Created by MajinBuu on 2017/4/17 0017.
 *
 * @overView CleanActivity的必要实现接口
 */

public interface CleanView {
    /**
     * 获取应用图标并展示
     * @param cleanMem 清理的内存量
     */
    void getIconAndShow(long cleanMem);

    /**
     * 显示toast内容
     * @param content 内容
     */
    void showToast(String content);
}
