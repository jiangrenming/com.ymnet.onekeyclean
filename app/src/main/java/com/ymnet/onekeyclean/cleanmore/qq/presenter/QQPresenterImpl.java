package com.ymnet.onekeyclean.cleanmore.qq.presenter;


import com.ymnet.onekeyclean.cleanmore.qq.QQScanHelp;
import com.ymnet.onekeyclean.cleanmore.qq.activity.QQMVPView;
import com.ymnet.onekeyclean.cleanmore.qq.mode.QQContent;
import com.ymnet.onekeyclean.cleanmore.qq.mode.QQFileDefault;
import com.ymnet.onekeyclean.cleanmore.qq.mode.QQFileType;
import com.ymnet.onekeyclean.cleanmore.utils.FileTreeUtils;
import com.ymnet.onekeyclean.cleanmore.wechat.listener.DataUpdateListener;
import com.ymnet.onekeyclean.cleanmore.wechat.mode.WareFileInfo;
import com.ymnet.onekeyclean.cleanmore.wechat.mode.WeChatFileType;

import java.util.Iterator;
import java.util.List;

import bolts.Task;

/**
 * Created by MajinBuu on 2017/5/3 0003.
 *
 * @overView ${todo}.
 */

public class QQPresenterImpl implements QQPresenter{
    private final QQScanHelp         scanHelp;
    private       QQMVPView          mvpView;
    private       DataUpdateListener listener;

    public QQPresenterImpl(final QQMVPView mvpView) {
        this.mvpView = mvpView;
        scanHelp = QQScanHelp.getInstance();
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
    public QQContent getData() {
        return scanHelp.getDatas();
    }

    @Override
    public QQContent initData() {
        return scanHelp.getInitData();
    }

    @Override
    public void updateData() {
        if (mvpView != null)
            mvpView.updateData();
    }

    @Override
    public void scanEnd() {
        QQContent content = scanHelp.getDatas();
        content.filterEmpty(new QQContent.FilterListener() {
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
        return scanHelp.getQQTrustSize();
    }

    @Override
    public void destory() {
        scanHelp.setExitTime(System.currentTimeMillis());
        if (listener == scanHelp.getListener()) {
            scanHelp.setUpdateListener(null);
        }
    }

    @Override
    public void remove(int position) {
        if (mvpView != null) {
            mvpView.showLoading();
            deletePaths(position);
        }
    }

    private void deletePaths(int position) {
        final QQFileType fileType = scanHelp.get(position);
        if(fileType==null||fileType.isEmpty()||!(fileType instanceof QQFileDefault)){
            mvpView.hideLoading();
        }else{
            final long toBeDeleteSize = fileType.getCurrentSize();
            fileType.setDeleteStatus(WeChatFileType.DELETE_ING);
            Task.BACKGROUND_EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    List<WareFileInfo> paths = ((QQFileDefault) fileType).getFilePaths();
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

    @Override
    public QQFileType get(int position) {
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
}
