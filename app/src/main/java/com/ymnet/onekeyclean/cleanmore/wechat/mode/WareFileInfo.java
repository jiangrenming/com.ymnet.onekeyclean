package com.ymnet.onekeyclean.cleanmore.wechat.mode;


import com.ymnet.onekeyclean.cleanmore.qq.adapter.QQExpandableAdapter2;

import java.io.Serializable;

import static com.ymnet.onekeyclean.cleanmore.qq.adapter.QQExpandableAdapter2.FileIconType.DOC;
import static com.ymnet.onekeyclean.cleanmore.qq.adapter.QQExpandableAdapter2.FileIconType.EXCEL;
import static com.ymnet.onekeyclean.cleanmore.qq.adapter.QQExpandableAdapter2.FileIconType.EXE;
import static com.ymnet.onekeyclean.cleanmore.qq.adapter.QQExpandableAdapter2.FileIconType.GIF;
import static com.ymnet.onekeyclean.cleanmore.qq.adapter.QQExpandableAdapter2.FileIconType.PDF;
import static com.ymnet.onekeyclean.cleanmore.qq.adapter.QQExpandableAdapter2.FileIconType.PICTURE;
import static com.ymnet.onekeyclean.cleanmore.qq.adapter.QQExpandableAdapter2.FileIconType.PPT;
import static com.ymnet.onekeyclean.cleanmore.qq.adapter.QQExpandableAdapter2.FileIconType.RADIO;
import static com.ymnet.onekeyclean.cleanmore.qq.adapter.QQExpandableAdapter2.FileIconType.RARs;
import static com.ymnet.onekeyclean.cleanmore.qq.adapter.QQExpandableAdapter2.FileIconType.TXT;
import static com.ymnet.onekeyclean.cleanmore.qq.adapter.QQExpandableAdapter2.FileIconType.UNKNOWN;
import static com.ymnet.onekeyclean.cleanmore.qq.adapter.QQExpandableAdapter2.FileIconType.VIDEO;


/**
 * Created by wangduheng26 on 4/6/16.
 */
public class WareFileInfo implements Serializable{
    public static final int EXPORT_DEFAULT=0;
    public static final int EXPORT_SUCCESS=1;
    public static final int EXPORT_FAILE=2;

    public String path;
    public long time;
    public long size;
    public String fileName;
    public boolean status;
    private int exportStatus;


    public boolean hasDelete;
    public WareFileInfo(String path, long time,long size) {
        this.path = path;
        this.time = time;
        this.size=size;
        exportStatus=EXPORT_DEFAULT;
        hasDelete=false;
    }

    public int getExportStatus() {
        return exportStatus;
    }

    public void setExportStatus(int exportStatus) {
        this.exportStatus = exportStatus;
    }

    public String getFileName(){
        return fileName;
    }

    public WareFileInfo(String path, long time, String name, long size) {
        this.path = path;
        this.time = time;
        this.size=size;
        this.fileName = name;
        exportStatus=EXPORT_DEFAULT;
        hasDelete=false;

    }

    public QQExpandableAdapter2.FileIconType getFileType() {
        if (fileName.toLowerCase().endsWith(".exe")) {
            return EXE;
        }
        if (fileName.toLowerCase().endsWith(".gif")) {
            return GIF;
        }
        if (fileName.toLowerCase().endsWith(".rar")||fileName.toLowerCase().endsWith(".zip")) {
            return RARs;
        }
        if (fileName.toLowerCase().endsWith(".png")||fileName.toLowerCase().endsWith(".jpg")||fileName.toLowerCase().endsWith(".jpeg")) {
            return PICTURE;
        }
        if (fileName.toLowerCase().endsWith(".doc")||fileName.toLowerCase().endsWith(".docx")) {
            return DOC;
        }
        if (fileName.toLowerCase().endsWith(".excel")) {
            return EXCEL;
        }
        if (fileName.toLowerCase().endsWith(".pdf")) {
            return PDF;
        }
        if (fileName.toLowerCase().endsWith(".ppt")) {
            return PPT;
        }
        if (fileName.toLowerCase().endsWith(".txt")) {
            return TXT;
        }
        if (fileName.toLowerCase().endsWith(".mp4")||fileName.toLowerCase().endsWith(".3gp")) {
            return RADIO;
        }
        if (fileName.toLowerCase().endsWith(".mp3")) {
            return VIDEO;
        }
        return UNKNOWN;
    }

    @Override
    public String toString() {
        return "WareFileInfo{" +
                "path='" + path + '\'' +
                ", time=" + time +
                ", size=" + size +
                ", status=" + status +
                ", exportStatus=" + exportStatus +
                ", hasDelete=" + hasDelete +
                '}';
    }
}
