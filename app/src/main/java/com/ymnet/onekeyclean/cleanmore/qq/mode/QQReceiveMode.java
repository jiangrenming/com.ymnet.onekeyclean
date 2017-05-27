package com.ymnet.onekeyclean.cleanmore.qq.mode;


import com.ymnet.onekeyclean.cleanmore.wechat.mode.ListDataMode;
import com.ymnet.onekeyclean.cleanmore.wechat.mode.WareFileInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MajinBuu on 2017/5/5 0005.
 *
 * QQ接收文件mode
 */

public class QQReceiveMode extends QQFileType {

    private List<ListDataMode> mReceiveFile;
    private List<WareFileInfo> filePaths;

    public QQReceiveMode(String fileName, long fileSize, int iconId, String fileInfo, String fileDelEffect) {
        super(fileName, fileSize, iconId, fileInfo, fileDelEffect);
        mReceiveFile = new ArrayList<>();
        filePaths=new ArrayList<>();
    }

    public void add(ListDataMode mode) {
        mReceiveFile.add(mode);
    }

    public List<ListDataMode> getReceiveFile() {
        return mReceiveFile;
    }
    public void setReceiveFile(List<ListDataMode> receiveFile) {
        this.mReceiveFile = receiveFile;
    }

    @Override
    public boolean isEmpty() {
        if (mReceiveFile.isEmpty()) {
            return true;
        } else {
            for (ListDataMode mode : mReceiveFile) {
                if (!mode.isEmpty()) {
                    return false;
                }
            }
            return true;
        }
    }
    public ListDataMode get(String name) {
        for (ListDataMode mode : mReceiveFile) {
            if (name.equals(mode.getName())) {
                return mode;
            }
        }
        return null;
    }


    public List<WareFileInfo> getFilePaths() {
        return filePaths;
    }

    public void setFilePaths(List<WareFileInfo> filePaths) {
        this.filePaths = filePaths;
    }

  /*  @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(mReceiveFile);
        dest.writeList(filePaths);
    }
*/
}




