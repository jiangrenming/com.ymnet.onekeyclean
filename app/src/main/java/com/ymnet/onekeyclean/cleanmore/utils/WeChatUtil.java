package com.ymnet.onekeyclean.cleanmore.utils;


import com.ymnet.onekeyclean.cleanmore.constants.WeChatConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangduheng26 on 4/8/16.
 */
public class WeChatUtil {
    public static List<String> MMPaths;

    static {
        MMPaths = new ArrayList<>();
        File file = new File(Util.getRootPath(), WeChatConstants.BASE_PATH);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File item : files) {
                    if (item.getName().length() == 32) {
                        MMPaths.add(item.getName());
                    }
                }
            }
        }
    }


    public static String[] createTempAccountPaths() {
        return replace(WeChatConstants.TEMP_ACCOUNT_PATH);
    }


    public static String[] createCacheAccountPaths() {
        return replace(WeChatConstants.CACHE_ACCOUNT_PATH);
    }

    public static String[] createPicAccountPaths() {
        return replace(WeChatConstants.PIC_ACCOUNT_PATH);
    }

    public static String[] createVoiceAccountPaths() {
        return replace(WeChatConstants.VOICE_ACCOUNT_PATH);
    }

    public static String[] createVideoAccountPaths() {
        return replace(WeChatConstants.VIDEO_ACCOUNT_PATH);
    }


    private static String[] replace(String[] targe) {
        if (MMPaths.isEmpty() || targe == null || targe.length == 0) {
            return null;
        }
        List<String> res = new ArrayList<>();

        for (String path : targe) {
            for (String temp : MMPaths) {
                String replace = path.replace("***", temp);
                res.add(replace);
            }

        }
        return res.toArray(new String[res.size()]);
    }

    public static <T> T[] concat(T[] a, T[] b) {
        if (a == null || a.length == 0) {
            return b;
        }
        if (b == null || b.length == 0) {
            return a;
        }
        int aLen = a.length;
        int bLen = b.length;
        final T[] result = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(a, 0, result, 0, aLen);
        System.arraycopy(b, 0, result, aLen, bLen);
        return result;
    }

  /*  *//**
     * 取得集合中数据结构的大小
     *
     * @param temp 需要计算大小的集合
     * @return
     *//*
    public static long getDataSize(List<WareFileInfo> temp) {
        if (temp == null || temp.size() == 0) return 0;
        long size = 0;
        for (WareFileInfo info : temp) {
            size += info.size;
        }
        return size;
    }*/

    public static int percent(double p1, double p2) {
        if (p2 == 0) return 0;
        return (int) (p1 / p2 * 100);
    }


}
