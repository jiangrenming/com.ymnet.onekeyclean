package com.ymnet.onekeyclean.cleanmore.wechat.presenter;


import android.util.Log;

import com.ymnet.onekeyclean.cleanmore.utils.C;
import com.ymnet.onekeyclean.cleanmore.utils.CleanSetSharedPreferences;
import com.ymnet.onekeyclean.cleanmore.utils.FileTreeUtils;
import com.ymnet.onekeyclean.cleanmore.wechat.WeChatScanHelp;
import com.ymnet.onekeyclean.cleanmore.wechat.listener.DataUpdateListener;
import com.ymnet.onekeyclean.cleanmore.wechat.mode.WareFileInfo;
import com.ymnet.onekeyclean.cleanmore.wechat.mode.WeChatContent;
import com.ymnet.onekeyclean.cleanmore.wechat.mode.WeChatFileDefault;
import com.ymnet.onekeyclean.cleanmore.wechat.mode.WeChatFileType;
import com.ymnet.onekeyclean.cleanmore.wechat.view.WeChatMvpView;

import java.util.Iterator;
import java.util.List;

import bolts.Task;

/**
 * Created by wangduheng26 on 4/1/16.
 */
public class WeChatPresenterImpl implements WeChatPresenter {
    private WeChatMvpView      mvpView;
    private WeChatScanHelp     scanHelp;
    private DataUpdateListener listener;

    public WeChatPresenterImpl(final WeChatMvpView mvpView) {
        this.mvpView = mvpView;
        scanHelp = WeChatScanHelp.getInstance();
        listener = new DataUpdateListener() {
            @Override
            public void update() {
                updateData();
            }

            @Override
            public void updateEnd() {
                scanEnd();
            }

            @Override
            public void removeEnd() {
                mvpView.hideLoading();
            }
        };
        scanHelp.setUpdateListener(listener);
    }

    @Override
    public WeChatContent getData() {
        return scanHelp.getDatas();
    }

    @Override
    public WeChatContent initData() {
        return scanHelp.getInitData();
    }

    @Override
    public void updateData() {
        if (mvpView != null)
            mvpView.updateData();
    }

    @Override
    public void scanEnd() {

        WeChatContent content = scanHelp.getDatas();
        content.filterEmpty(new WeChatContent.FilterListener() {
            @Override
            public void removeCallback() {
                if (mvpView != null) mvpView.updateData();
            }
        });
        if (mvpView != null) {
            mvpView.changeDivider();
            mvpView.updateData();
            mvpView.stopAnim();
        }

    }

    @Override
    public long getSize() {
        return scanHelp.getWeChatTrustSize();
    }

    @Override
    public void destory() {
        scanHelp.setExitTime(System.currentTimeMillis());
        if (listener == scanHelp.getListener()) {
            scanHelp.setUpdateListener(null);
        }
        /*if (isEnd()) {
            StatisticSpec.sendEvent(StatisticEventContants.cleanwechat_afterscan_back);
        } else {
            StatisticSpec.sendEvent(StatisticEventContants.cleanwechat_scanning_back);
        }*/
    }

    @Override
    public void remove(int position) {
        if (mvpView != null) {
            mvpView.showLoading();
            deletePaths(position);
        }

    }

    @Override
    public WeChatFileType get(int position) {
        return scanHelp.get(position);
    }

    @Override
    public boolean isEnd() {
        return scanHelp.isScanFinish();
    }

    @Override
    public boolean isInstallAPP() {

        return scanHelp.isInstalled();
    }

    private void deletePaths(int position) {
        final WeChatFileType fileType = scanHelp.get(position);
        if(fileType==null||fileType.isEmpty()||!(fileType instanceof WeChatFileDefault)){
            mvpView.hideLoading();
        }else{
            final long toBeDeleteSize = fileType.getCurrentSize();
            fileType.setDeleteStatus(WeChatFileType.DELETE_ING);
            Task.BACKGROUND_EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {

                    // TODO: 2017/6/13 0013 将将要删除的文件大小存入sp
                    Log.d("WeChatPresenterImpl", "删除文件" + fileType.getCurrentSize());
                    CleanSetSharedPreferences.setWeChatCleanLastTimeSize(C.get(), fileType.getCurrentSize());

                    List<WareFileInfo> paths = ((WeChatFileDefault) fileType).getFilePaths();
                    Iterator<WareFileInfo> iterator = paths.iterator();
                    while (iterator.hasNext()) {
                        WareFileInfo next = iterator.next();
                        FileTreeUtils.simpleDeleteFile(next.path);
                        iterator.remove();
                        fileType.setCurrentSize(fileType.getCurrentSize() - next.size);
                        mvpView.updateData();
                    }
                    fileType.setCurrentSize(0);
                    scanHelp.sizeDecreasing(toBeDeleteSize);
                    fileType.setDeleteStatus(WeChatFileType.DELETE_CLOSE);
                    mvpView.hideLoading();

                }
            });
        }
    }

}
