package utils;

import android.content.Context;

import java.io.File;

import okhttp3.Cache;

/**
 * Created by jrm on 2017-3-29.
 * 网络请求的缓存处理~最大缓存空间
 */

public class CacheUtil {
    private static final int HTTP_RESPONSE_DISK_CACHE_MAX_SIZE = 10 * 1024 * 1024;

    private static File getCacheDir(Context applicationContext) {
        //设置缓存路径
        final File baseDir = getCacheDir(ContextUtils.getInstance().getApplicationContext());
        final File cacheDir = new File(baseDir, "HttpResponseCache");
        return cacheDir;
    }

    public static Cache getCache() {
        return new Cache(getCacheDir(ContextUtils.getInstance().getApplicationContext()), HTTP_RESPONSE_DISK_CACHE_MAX_SIZE);
    }
}
