package com.example.commonlibrary.utils;

import java.io.Closeable;

/**
 * Created by MajinBuu on 2017/4/19 0019.
 *
 * @overView 关闭功能工具类.
 */

public final class CloseUtil {

    private CloseUtil() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 关闭closeable对象
     * @param closeable 要关闭的对象
     */
    public static void closeQuickly(Closeable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
