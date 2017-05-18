package com.ymnet.onekeyclean.cleanmore.qq.mode;

import java.io.Serializable;

/**
 * Created by wangduheng26 on 4/1/16.
 */
public abstract class QQFileType  implements Serializable{
    public static final int DELETE_DEFAULT=0;//默认状态
    public static final int DELETE_ING=1;//正在删除
    public static final int DELETE_CLOSE=2;//删除关闭或者完成

    private String fileName;
    private int iconId;
    private String fileInfo;
    private String fileDelEffect;
    private int type;

    private int deleteStatus;
    private boolean inEndAnim;
    private long scanOldSize;
    private long currentSize;
    /**
     * 统计事件标示
     */
    private String sE;

    public String getsE() {
        return this.sE == null ? "" : sE;
    }

    public void setsE(String sE) {
        this.sE = sE;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public String getFileInfo() {
        return fileInfo;
    }

    public void setFileInfo(String fileInfo) {
        this.fileInfo = fileInfo;
    }

    public String getFileDelEffect() {
        return fileDelEffect;
    }

    public void setFileDelEffect(String fileDelEffect) {
        this.fileDelEffect = fileDelEffect;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDeleteStatus() {
        return deleteStatus;
    }

    public void setDeleteStatus(int deleteStatus) {
        this.deleteStatus = deleteStatus;
    }

    public boolean isInEndAnim() {
        return inEndAnim;
    }

    public void setInEndAnim(boolean inEndAnim) {
        this.inEndAnim = inEndAnim;
    }

    public long getScanOldSize() {
        return scanOldSize;
    }

    public void setScanOldSize(long scanOldSize) {
        this.scanOldSize = scanOldSize;
    }

    public long getCurrentSize() {
        return currentSize;
    }

    public void setCurrentSize(long currentSize) {
        this.currentSize = currentSize;
    }

    public QQFileType(String fileName, long fileSize, int iconId, String fileInfo, String fileDelEffect) {
        this.fileName = fileName;
        this.scanOldSize = fileSize;
        this.iconId = iconId;
        this.fileInfo = fileInfo;
        this.fileDelEffect = fileDelEffect;
    }

    public abstract boolean isEmpty();
}
