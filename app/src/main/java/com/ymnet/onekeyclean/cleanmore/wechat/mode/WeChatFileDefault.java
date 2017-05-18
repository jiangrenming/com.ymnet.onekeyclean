package com.ymnet.onekeyclean.cleanmore.wechat.mode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangduheng26 on 4/11/16.
 */
public  class WeChatFileDefault extends WeChatFileType {
    private List<WareFileInfo> filePaths;

    public List<WareFileInfo> getFilePaths() {
        return filePaths;
    }

    public void setFilePaths(List<WareFileInfo> filePaths) {
        this.filePaths = filePaths;
    }

    public WeChatFileDefault(String fileName, long fileSize, int iconId, String fileInfo, String fileDelEffect) {
        super(fileName, fileSize, iconId, fileInfo, fileDelEffect);
        filePaths=new ArrayList<>();
    }

    @Override
    public boolean isEmpty() {
        return filePaths == null || filePaths.isEmpty();
    }
}
