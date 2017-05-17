package com.ymnet.onekeyclean.cleanmore.download;


import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

/**
 * Contains the internal constants that are used in the download manager.
 * As a general rule, modifying these constants should be done with care.
 */
public class Constants {

    /**
     * Tag used for debugging/logging
     */
    public static final String TAG = "DownloadManager";

    /**
     * The default extension for html files if we can't get one at the HTTP level
     */
    public static final String DEFAULT_DL_HTML_EXTENSION = ".html";

    /**
     * The default extension for text files if we can't get one at the HTTP level
     */
    public static final String DEFAULT_DL_TEXT_EXTENSION = ".txt";

    /**
     * The default extension for binary files if we can't get one at the HTTP level
     */
    public static final String DEFAULT_DL_BINARY_EXTENSION = ".bin";


    public static final String FILENAME_SEQUENCE_SEPARATOR = "_";
    public static final String FILENAME_SEQUENCE_SEPARATOR_LEFT = "(";
    public static final String FILENAME_SEQUENCE_SEPARATOR_RIGHT = ")";


    /**
     * The default user agent used for downloads
     */
    public static final String DEFAULT_USER_AGENT;

    static {
        final StringBuilder builder = new StringBuilder();

        final boolean validRelease = !TextUtils.isEmpty(Build.VERSION.RELEASE);
        final boolean validId = !TextUtils.isEmpty(Build.ID);
        final boolean includeModel = "REL".equals(Build.VERSION.CODENAME)
                && !TextUtils.isEmpty(Build.MODEL);

        builder.append("AndroidDownloadManager");
        if (validRelease) {
            builder.append("/").append(Build.VERSION.RELEASE);
        }
        builder.append(" (Linux; U; Android");
        if (validRelease) {
            builder.append(" ").append(Build.VERSION.RELEASE);
        }
        if (includeModel || validId) {
            builder.append(";");
            if (includeModel) {
                builder.append(" ").append(Build.MODEL);
            }
            if (validId) {
                builder.append(" Build/").append(Build.ID);
            }
        }
        builder.append(")");

        DEFAULT_USER_AGENT = builder.toString();
    }

    /**
     * The MIME type of APKs
     */
    public static final String MIMETYPE_APK = "application/vnd.android.package";

    /**
     * The buffer size used to stream the data
     */
    public static final int BUFFER_SIZE = 4096;

    /**
     * The minimum amount of progress that has to be done before the progress bar gets updated
     */
    public static final int MIN_PROGRESS_STEP = 1024 * 20;

    /**
     * The minimum amount of time that has to elapse before the progress bar gets updated, in ms
     */
    public static final long MIN_PROGRESS_TIME = 50;

    /**
     * The maximum number of redirects.
     */
    public static final int MAX_REDIRECTS = 5; // can't be more than 7.

    /**
     * Enable separate connectivity logging
     */
    static final boolean LOGX = false;

    /**
     * Enable verbose logging - use with "setprop log.tag.DownloadManager VERBOSE"
     */
    private static final boolean LOCAL_LOGV = false;
    public static final boolean LOGV = LOCAL_LOGV && Log.isLoggable(TAG, Log.VERBOSE);

    /**
     * Enable super-verbose logging
     */
    private static final boolean LOCAL_LOGVV = false;
    public static final boolean LOGVV = LOCAL_LOGVV && LOGV;

    public static final String STORAGE_AUTHORITY = "com.android.providers.downloads.documents";
    public static final String STORAGE_ROOT_ID = "downloads";


    public static final String DOWNLOAD_DIRTYP = "ymnet" + File.separator + "apk";

    public static final String DOWNLOAD_FILE_DIR = "ymnet" + File.separator + "files";

    public static final String INNER_DOWNLOAD_DIRTYP = "apk";

}
