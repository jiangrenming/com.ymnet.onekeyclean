package com.ymnet.onekeyclean.cleanmore.filebrowser.lazyload;

import android.graphics.Bitmap;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class MemoryCache
{
    
    private Map<String, Bitmap> cache = Collections.synchronizedMap(new LinkedHashMap<String, Bitmap>(10, 1.5f, true));//Last argument true for LRU ordering
    
    private long size = 0;//current allocated size
    
    private long limit = 1 * 1024 * 1024;//max memory in bytes
    
    public MemoryCache()
    {
        //use 25% of available heap size
        long d = Runtime.getRuntime().maxMemory() / 4;
        if (d < limit)
        {
            setLimit(Runtime.getRuntime().maxMemory() / 4);
        }
    }
    
    private void setLimit(long new_limit)
    {
        limit = new_limit;
    }
    
    public Bitmap get(String id)
    {
        try
        {
            if (!cache.containsKey(id))
                return null;
            //NullPointerException sometimes happen here http://code.google.com/p/osmdroid/issues/detail?id=78 
            return cache.get(id);
        }
        catch (NullPointerException ex)
        {
            ex.printStackTrace();
            return null;
        }
    }
    
    boolean hasMemoryCache(String id)
    {
        if (null == cache)
        {
            return false;
        }
        if (cache.containsKey(id))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    void put(String id, Bitmap bitmap)
    {
        try
        {
            if (cache.containsKey(id))
            {
                size -= getSizeInBytes(cache.get(id));
            }
            cache.put(id, bitmap);
            size += getSizeInBytes(bitmap);
            checkSize();
        }
        catch (Throwable th)
        {
            th.printStackTrace();
        }
    }
    
    private void checkSize()
    {
        if (size > limit)
        {
            Iterator<Entry<String, Bitmap>> iter = cache.entrySet().iterator();//least recently accessed item will be the first one iterated  
            while (iter.hasNext())
            {
                Entry<String, Bitmap> entry = iter.next();
                size -= getSizeInBytes(entry.getValue());
                iter.remove();
                if (size <= limit)
                    break;
            }
        }
    }
    
    public void clear()
    {
        try
        {
            //NullPointerException sometimes happen here http://code.google.com/p/osmdroid/issues/detail?id=78 
            cache.clear();
            size = 0;
        }
        catch (NullPointerException ex)
        {
            ex.printStackTrace();
        }
    }
    
    long getSizeInBytes(Bitmap bitmap)
    {
        if (bitmap == null)
            return 0;
        return bitmap.getRowBytes() * bitmap.getHeight();
    }
}