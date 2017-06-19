package com.ymnet.onekeyclean.cleanmore.qq.presenter;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.constants.ByteConstants;
import com.ymnet.onekeyclean.cleanmore.constants.Constants;
import com.ymnet.onekeyclean.cleanmore.constants.QQConstants;
import com.ymnet.onekeyclean.cleanmore.filebrowser.FileBrowserUtil;
import com.ymnet.onekeyclean.cleanmore.qq.QQScanHelp;
import com.ymnet.onekeyclean.cleanmore.qq.activity.QQDetailMvpView;
import com.ymnet.onekeyclean.cleanmore.qq.mode.QQFileType;
import com.ymnet.onekeyclean.cleanmore.qq.mode.QQPicMode;
import com.ymnet.onekeyclean.cleanmore.qq.mode.QQReceiveMode;
import com.ymnet.onekeyclean.cleanmore.qq.mode.QQSelectDatas;
import com.ymnet.onekeyclean.cleanmore.utils.C;
import com.ymnet.onekeyclean.cleanmore.utils.CleanSetSharedPreferences;
import com.ymnet.onekeyclean.cleanmore.utils.FileTreeUtils;
import com.ymnet.onekeyclean.cleanmore.utils.FormatUtils;
import com.ymnet.onekeyclean.cleanmore.wechat.mode.ChangeMode;
import com.ymnet.onekeyclean.cleanmore.wechat.mode.ExportMode;
import com.ymnet.onekeyclean.cleanmore.wechat.mode.ListDataMode;
import com.ymnet.onekeyclean.cleanmore.wechat.mode.WareFileInfo;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import bolts.Task;


/**
 * Created by wangdh on 6/14/16.
 * gmail:wangduheng26@gamil.com
 */
public class QQDetailPresImpl implements QQDetailPresenter<QQPicMode> {
    private QQDetailMvpView mvpView;
    private String          str;
    private QQFileType      data;
    private QQSelectDatas   selectData;
    private QQFileType      mQQFileType;
    private final String TAG = "QQDetailPresImpl";

    public QQDetailPresImpl(QQDetailMvpView mvpView, int extra, QQFileType qqFileType) {
        this.mvpView = mvpView;
        this.mQQFileType = qqFileType;
        str = C.get().getString(R.string.file_delete);
        selectData = new QQSelectDatas();
        mvpView.setText(str);
        initData(extra, qqFileType);
    }

    /**
     * 拿到QQ垃圾中对应项的数据
     */
    @Override
    public QQFileType getData() {
        return data;
    }

    @Override
    public int getSelectCount() {
        return selectData.isEmpty() ? 0 : selectData.getListDatas().size();
    }

    public int getCount() {
        int result = 0;
        if (data == null)
            return result;
        if (data instanceof QQPicMode) {
            List<ListDataMode> pics = ((QQPicMode) data).getPics();
            if (pics == null || pics.isEmpty())
                return result;
            for (ListDataMode temp : pics) {
                List<WareFileInfo> list = temp.getContent();
                result += list.size();
            }
            return result;
        } else if (data instanceof QQReceiveMode) {
            List<ListDataMode> receiveFile = ((QQReceiveMode) data).getReceiveFile();
            if (receiveFile == null || receiveFile.isEmpty())
                return result;
            for (ListDataMode temp : receiveFile) {
                List<WareFileInfo> list = temp.getContent();
                result += list.size();
            }
            return result;
        }
        return result;
    }

    @Override
    public void remove() {
        int selectCount = getSelectCount();
        if (selectCount == 0)
            return;
        mvpView.showLoading();
        data.setDeleteStatus(QQFileType.DELETE_ING);
        Task.BACKGROUND_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                List<WareFileInfo> list = selectData.getListDatas();

                Log.d("QQDetailPresImpl", "删除文件" + selectData.getCurrentSize());
                CleanSetSharedPreferences.setQQCleanLastTimeSize(C.get(), selectData.getCurrentSize());
                for (WareFileInfo info : list) {
                    if (info.fileName == null) {
                        FileTreeUtils.simpleDeleteFile(info.path);
                    } else {
                        FileTreeUtils.simpleDeleteFile(info.path + File.separator + info.fileName);
                    }
                    info.hasDelete = true;
                }
                selectData.clean();
                boolean syncData = syncData();
                mvpView.hideLoading();
                if (syncData) {
                    mvpView.changeGroupCount();
                }
                toast("删除成功");
                mvpView.setText(str);
            }
        });
    }

    private boolean syncData() {
        if (data == null)
            return false;
        boolean result = false;
        if (data instanceof QQPicMode) {
            List<ListDataMode> pics = ((QQPicMode) data).getPics();
            if (pics == null || pics.isEmpty())
                return false;
            Iterator<ListDataMode> iterator = pics.iterator();
            while (iterator.hasNext()) {
                ListDataMode mode = iterator.next();
                mode.sync(new ListDataMode.SyncListener() {
                    @Override
                    public void removeSize(long size) {
                        QQScanHelp.getInstance().sizeDecreasing(size);
                    }
                });
                if (mode.isEmpty()) {
                    iterator.remove();
                    result = true;
                }
            }
        } else {
            List<ListDataMode> receiveFile = ((QQReceiveMode) data).getReceiveFile();
            if (receiveFile == null || receiveFile.isEmpty())
                return false;
            Iterator<ListDataMode> iterator = receiveFile.iterator();
            while (iterator.hasNext()) {
                ListDataMode mode = iterator.next();
                mode.sync(new ListDataMode.SyncListener() {
                    @Override
                    public void removeSize(long size) {
                        QQScanHelp.getInstance().sizeDecreasing(size);
                    }
                });
                if (mode.isEmpty()) {
                    iterator.remove();
                    result = true;
                }
            }
        }

        long count = getCount();
        if (count == 0) {
            data.setDeleteStatus(QQFileType.DELETE_CLOSE);
            result = false;
        } else {
            data.setScanOldSize(data.getCurrentSize());
            data.setDeleteStatus(QQFileType.DELETE_DEFAULT);
        }
        return result;
    }


    @Override
    public void export(int count) {
        if (selectData.isEmpty())
            return;
        if (TextUtils.isEmpty(Constants.SDCard))
            return;
        //begin export
        Task.BACKGROUND_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                mvpView.showExportDialog(true);
                if (checkExportPath()) {
                    executeCopy();
                } else {
                    mvpView.hideLoading();
                    toast("目标文件夹创建失败");
                }

            }
        });
    }

    /**
     * 执行数据的copy
     */
    private void executeCopy() {
        ChangeMode cMode = new ChangeMode();
        ExportMode eMode = new ExportMode();
        int index = 0;
        List<WareFileInfo> list = selectData.getListDatas();
        for (WareFileInfo fileInfo : list) {
            File sourceFile = new File(fileInfo.path);
            cMode.message = sourceFile.getName();
            cMode.progress = ++index;
            mvpView.changeExportProgress(cMode);
            if (WareFileInfo.EXPORT_SUCCESS == fileInfo.getExportStatus()) {
                eMode.hasExport++;
                continue;
            }

            File targetFile = new File(Constants.SDCard + QQConstants.QQ_EXPORT_PATH, sourceFile.getName());
            if (targetFile.exists()) {
                fileInfo.setExportStatus(WareFileInfo.EXPORT_SUCCESS);
                eMode.hasExport++;
                continue;
            }
            boolean copy = FileTreeUtils.copy(sourceFile, targetFile);
            if (copy) {
                fileInfo.setExportStatus(WareFileInfo.EXPORT_SUCCESS);
                eMode.successCount++;
            } else {
                fileInfo.setExportStatus(WareFileInfo.EXPORT_FAILE);
                eMode.failCount++;

            }
        }
        cancleAll();
        mvpView.showExportDialog(false);
        mvpView.showExportComplete(eMode);

    }

    /**
     * 检查并创建导出的路径
     */
    private boolean checkExportPath() {
        File file = new File(Constants.SDCard, QQConstants.QQ_EXPORT_PATH);
        if (file.exists()) {
            return file.isDirectory() || file.mkdir();
        } else {
            return file.mkdirs();
        }

    }

    public void changeSingle(WareFileInfo info) {
        if (info == null)
            return;
        info.status = !info.status;
        if (info.status) {//false--->true
            selectData.add(info);
        } else {
            selectData.remove(info);
        }
        updateData();
    }

    public void changeList(List<WareFileInfo> infos, boolean targetStatus) {
        if (infos == null || infos.isEmpty())
            return;
        for (WareFileInfo info : infos) {
            if (targetStatus) {//选中
                if (!info.status) {//现在未选中
                    info.status = true;
                    selectData.add(info);
                }
            } else {//取消选中
                if (info.status) {//现在是选中的
                    info.status = false;
                    selectData.remove(info);
                }
            }
        }
        updateData();
    }

    @Override
    public String checkExportFileLimit() {
        int selectCount = getSelectCount();
        if (selectCount > 100) {
            return C.get().getString(R.string.wechat_export_limit_count, selectCount);
        }
        int currentSize = selectData.getCurrentSize();
        if (currentSize > ByteConstants.GB) {
            return C.get().getString(R.string.wechat_export_limit_size, FormatUtils.formatFileSize(currentSize));
        }
        return null;
    }

    @Override
    public boolean checkStorage() {
        FileBrowserUtil.SDCardInfo info = FileBrowserUtil.getSDCardInfo();
        return info != null && selectData.getCurrentSize() * 1.5 < info.free;
    }

    /**
     * 重置数据状态,取消所有选中
     */
    private void cancleAll() {
        if (data == null)
            return;
        List<ListDataMode> temp;
        if (data instanceof QQPicMode) {
            temp = ((QQPicMode) data).getPics();
        } else {//QQReceiveMode类型
            temp = ((QQReceiveMode) data).getReceiveFile();
        }
        if (temp == null || temp.isEmpty())
            return;

        for (ListDataMode mode : temp) {
            List<WareFileInfo> content = mode.getContent();
            for (WareFileInfo info : content) {
                info.status = false;
            }
        }
        selectData.clean();
        updateData();
    }

    /**
     * 更新选中的大小,设置删除btn的text
     */
    private void updateData() {
        int selectSize = selectData.getCurrentSize();
        if (selectSize == 0) {
            mvpView.setText(str);
        } else {
            mvpView.setText(str + FormatUtils.formatFileSize(selectSize));

        }
    }

    /**
     * init list data
     *
     * @param extra
     * @param qqFileType
     */
    private void initData(int extra, QQFileType qqFileType) {
        if (extra < 0)
            extra = 0;
        try {
            data = QQScanHelp.getInstance().get(extra);
            cancleAll();
        } catch (Exception e) {
            e.printStackTrace();
            mvpView.showError();
        }
    }


    void toast(final String content) {
        if (TextUtils.isEmpty(content))
            return;
        Task.UI_THREAD_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(C.get(), content, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
