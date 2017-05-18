package com.ymnet.onekeyclean.cleanmore.filebrowser.lazyload;

import android.content.Context;
import android.os.StatFs;

import com.ymnet.onekeyclean.cleanmore.constants.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 图片缓存
 * 
 * @author gxz
 * 
 */
public class FileCache
{
    
    private Context applicationContext;
    
    FileCache(Context context)
    {
        // Find the dir to save cached images
        applicationContext = context.getApplicationContext();
    }
    
    File checkByHasecode(String url)
    {
        File check;
        String filename = String.valueOf(url.hashCode());
        File cacheDir = applicationContext.getCacheDir();
        check = new File(cacheDir, filename);
        if (check.exists())
        {
            return check;
        }
        
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
        {
            cacheDir = new File(Constants.ImageCacheDir);
            if (cacheDir.exists())
            {
                check = new File(cacheDir, filename);
                if (check.exists())
                {
                    return check;
                }
            }
        }
        return check;
    }
    
    File generatorByHashcode(String url, int length)
    {
        File cacheDir;
        File generator;
        String filename = String.valueOf(url.hashCode());
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
        {
            cacheDir = new File(Constants.ImageCacheDir);
            if (cacheDir.exists())
            {
                if (checkSize(cacheDir) > (length + 100000))
                {
                    if (testOByte(cacheDir))
                    {
                        generator = new File(cacheDir, filename);
                        return generator;
                    }
                }
            }
            else if (cacheDir.mkdirs())
            {
                if (checkSize(cacheDir) > (length + 100000))
                {
                    if (testOByte(cacheDir))
                    {
                        generator = new File(cacheDir, filename);
                        return generator;
                    }
                }
            }
        }
        cacheDir = applicationContext.getCacheDir();
        generator = new File(cacheDir, filename);
        return generator;
    }
    
    private long checkSize(File cacheDir)
    {
        StatFs stat = new StatFs(cacheDir.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }
    
    File checkAndGeneratorbyMD5(String url)
    {
        return null;
    }
    
    private boolean testOByte(File cacheDir)
    {
        boolean test;
        FileOutputStream out = null;
        try
        {
            File f = new File(cacheDir, "test.txt");
            out = new FileOutputStream(f);
            out.write("test".getBytes());
            out.flush();
            out.close();
            test = true;
        }
        catch (IOException e)
        {
            test = false;
        }finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return test;
    }
    
}