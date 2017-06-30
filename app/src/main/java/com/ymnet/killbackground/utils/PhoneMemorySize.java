package com.ymnet.killbackground.utils;

import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;

import com.ymnet.onekeyclean.cleanmore.utils.C;

import java.io.File;

/**
 * Created by MajinBuu on 2017/6/29 0029.
 *
 * @overView 手机运行内存及存储大小
 */

public class PhoneMemorySize {
    /**
     * 获取手机可用运行内存
     * @return
     */
    public static long getAvailableSpace() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockCount = stat.getBlockCount();
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();

        String totalSize = Formatter.formatFileSize(C.get(), blockCount*blockSize);
        String availableSize = Formatter.formatFileSize(C.get(), blockCount*availableBlocks);

        //        return "手机Rom总容量:"+totalSize+"\n手机Rom可用容量:"+availableSize;
        return blockCount * blockSize;
    }

    /**
     * 获取手机运行内存总量
     * @return
     */
    public static long getRomTotleSpace() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockCount = stat.getBlockCount();
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();

        String totalSize = Formatter.formatFileSize(C.get(), blockCount*blockSize);
        String availableSize = Formatter.formatFileSize(C.get(), blockCount*availableBlocks);

        return blockCount*blockSize;
    }
}
