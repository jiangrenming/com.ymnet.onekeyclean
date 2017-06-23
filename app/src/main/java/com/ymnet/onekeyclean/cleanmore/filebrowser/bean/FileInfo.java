package com.ymnet.onekeyclean.cleanmore.filebrowser.bean;


import android.graphics.drawable.Drawable;

import com.ymnet.onekeyclean.cleanmore.filebrowser.FileCategoryHelper;

import java.io.Serializable;

public class FileInfo implements Serializable
{

    public int fileId;
    
    public String fileName;
    
    public String filePath;
    
    public long fileSize;
    
    public boolean IsDir;
    
    public int duration; //时长
    
    public long ModifiedDate;
    
    public boolean canRead;
    
    public boolean canWrite;
    
    public boolean isHidden;
    
    public String appName;
    
    public String mimeType;

    public Drawable drawable;

    public FileCategoryHelper.FileCategory fc;
}
