package com.ymnet.onekeyclean.cleanmore.download;/*
package com.example.baidumapsevice.download;


import android.net.Uri;
import android.provider.BaseColumns;

*/
/**
 * The Download Manager
 *
 * @pending
 *//*

public final class Downloads {
    private Downloads() {
    }

    */
/**
     * Implementation details
     * <p/>
     * Exposes constants used to interact with the download manager's
     * content provider.
     * The constants URI ... STATUS are the names of columns in the downloads table.
     *
     * @hide
     *//*

    public static final class Impl implements BaseColumns {
        private Impl() {
        }





        public static final Uri DOWNLOADS_CONTENT_URI =
                Uri.parse("content://" + DownloadProvider.AUTHORITIES +  "/" + DownloadProvider.URI_PATH);

        */
/**
         * 初始化所有下载
         *//*

        public static final Uri INIT_DOWNLOADS_CONTENT_URI =
                Uri.parse("content://" + DownloadProvider.AUTHORITIES +  "/" + DownloadProvider.INIT_DOWNLOADS_URI_PATH);


        */
/**
         * RESUME
         *//*

        public static final Uri RESUME_DOWNLOADS_CONTENT_URI =
                Uri.parse("content://" + DownloadProvider.AUTHORITIES +  "/" + DownloadProvider.RESUME_DOWNLOADS_URI_PATH);



        */
/**
         * CANCEL
         *//*

        public static final Uri CANCEL_DOWNLOADS_CONTENT_URI =
                Uri.parse("content://" + DownloadProvider.AUTHORITIES +  "/" + DownloadProvider.CANCEL_DOWNLOADS_URI_PATH);



        */
/**
         * The name of the column containing the URI of the data being downloaded.
         * <P>Type: TEXT</P>
         * <P>Owner can Init/Read</P>
         *//*

        public static final String COLUMN_URI = "uri";

        */
/**
         * The name of the column containing application-specific data.
         * <P>Type: TEXT</P>
         * <P>Owner can Init/Read/Write</P>
         *//*

        public static final String COLUMN_APP_DATA = "entity";


        */
/**
         * The name of the column containing the filename where the downloaded data
         * was actually stored.
         * <P>Type: TEXT</P>
         * <P>Owner can Read</P>
         *//*

        public static final String COLUMN_FILE_NAME = "fileName";

        */
/**
         * The name of the column containing the MIME type of the downloaded data.
         * <P>Type: TEXT</P>
         * <P>Owner can Init/Read</P>
         *//*

        public static final String COLUMN_MIME_TYPE = "mimetype";



        public static final String COLUMN_NUMFAILED = "mNumFailed";


        public static final String COLUMN_IS_UPDATE = "isUpdate";


        */
/**
         * The name of the column containing the current status of the download.
         * Applications can read this to follow the progress of each download. See
         * the STATUS_* constants for a list of legal values.
         * <P>Type: INTEGER</P>
         * <P>Owner can Read</P>
         *//*

        public static final String COLUMN_STATUS = "status";


        */
/**
         * The name of the column containing the package name of the application
         * that initiating the download. The download manager will send
         * notifications to a component in this package when the download completes.
         * <P>Type: TEXT</P>
         * <P>Owner can Init/Read</P>
         *//*

        public static final String COLUMN_CERTMD5 = "certMd5";






        public static final String COLUMN_PATCH_URL = "patchUrl";

        public static final String COLUMN_PATCH_SIZE = "patchSize";

        public static final String COLUMN_LOWMD5 = "lowMD5";

        public static final String COLUMN_DOWN_PARTIAL = "downPartial";

        public static final String COLUMN_TARGET_SIZE = "targetSize";

        public static final int DOWN_FULL = 0;

        public static final int DOWN_PARTIAL = 1;



        */
/**
         * The name of the column containing the component name of the class that
         * will receive notifications associated with the download. The
         * package/class combination is passed to
         * Intent.setClassName(String,String).
         * <P>Type: TEXT</P>
         * <P>Owner can Init/Read</P>
         *//*

        public static final String COLUMN_LAST_MODIFIED = "lastModified";


        */
/**
         * The name of the column containing the user agent that the initiating
         * application wants the download manager to use for this download.
         * <P>Type: TEXT</P>
         * <P>Owner can Init</P>
         *//*

        public static final String COLUMN_USER_AGENT = "useragent";

        */
/**
         * The name of the column containing the referer (sic) that the initiating
         * application wants the download manager to use for this download.
         * <P>Type: TEXT</P>
         * <P>Owner can Init</P>
         *//*

        public static final String COLUMN_REFERER = "referer";

        */
/**
         * The name of the column containing the total size of the file being
         * downloaded.
         * <P>Type: INTEGER</P>
         * <P>Owner can Read</P>
         *//*

        public static final String COLUMN_TOTAL_BYTES = "total_bytes";

        */
/**
         * The name of the column containing the size of the part of the file that
         * has been downloaded so far.
         * <P>Type: INTEGER</P>
         * <P>Owner can Read</P>
         *//*

        public static final String COLUMN_CURRENT_BYTES = "current_bytes";


        */
/**
         * The name of the column where the initiating application can provided the
         * title of this download. The title will be displayed ito the user in the
         * list of downloads.
         * <P>Type: TEXT</P>
         * <P>Owner can Init/Read/Write</P>
         *//*

        public static final String COLUMN_TITLE = "title";

        */
/**
         * The name of the column where the initiating application can provide the
         * description of this download. The description will be displayed to the
         * user in the list of downloads.
         * <P>Type: TEXT</P>
         * <P>Owner can Init/Read/Write</P>
         *//*

        public static final String COLUMN_DESCRIPTION = "description";


        */
/**
         * The name of the column holding a bitmask of allowed network types.  This is only used for
         * public API downloads.
         * <P>Type: INTEGER</P>
         * <P>Owner can Init/Read</P>
         *//*

        public static final String COLUMN_ALLOWED_NETWORK_TYPES = "allowed_network_types";


        */
/** The column that is used for the downloads's ETag *//*

        public static final String COLUMN_ETAG = "etag";



        public static final int DOWN_ALL_ALLOWED = 0;

        public static final int DOWN_ONLY_WIFI = 1;


        public static final String COLUMN_DELETE_FILE = "delete_file";


        public static final int DELETE_FILE_FALSE = 0;


        public static final int DELETE_FILE_TRUE = 1;


        public static final String COLUMN_PACKAGENAME = "packageName";


        public static final String COLUMN_VERSIONNAME = "versionName";

        public static final String COLUMN_SID = "sid";

        public static final String COLUMN_ICON_URL = "iconUrl";

        public static final String COLUMN_VERSIONCODE = "versionCode";

        public static final String COLUMN_FIRSTMD5 = "firstMD5";

        public static final String COLUMN_FIRSTMD5ERROR_LENGTH = "firstMD5_length";

        public static final String COLUMN_SECONDMD5 = "secondMD5";

        public static final String COLUMN_SERVERMD5 = "serverMD5";

        public static final String COLUMN_PLATFORM = "platform";

        public static final String COLUMN_SOURCEFROM = "sourcefrom";

        public static final String COLUMN_START_INSTALL = "startInstall";

        public static final String COLUMN_MIN_SDK = "minSDK";

        public static final String COLUMN_SPEED = "speed";

        public static final int START_INSTALL_FALSE = 0;


        public static final int START_INSTALL_TRUE = 1;


        */
/**
         * 签名不一致
         *//*

        public static final String COLUMN_SIGNATUREERROR = "signatureError";

        */
/** 签名相同 *//*

        public static final int UNSIGNATUREERROR = 0;
        */
/** 签名不相同 *//*

        public static final int SIGNATUREERROR = 1;


        */
/**
         * 下载类型：下载
         *//*

        public static final int DOWN_TYPE_DOWN = 0;

        */
/**
         * 下载类型：更新
         *//*

        public static final int DOWN_TYPE_UPDATE = 1;





        */
/**
         * This download hasn't stated yet
         *//*

        public static final int STATUS_PENDING = 190;

        */
/**
         * This download has started
         *//*

        public static final int STATUS_RUNNING = 192;

        */
/**
         * This download has been paused by the owning app.
         *//*

        public static final int STATUS_PAUSED_BY_APP = 193;

        */
/**
         * This download encountered some network error and is waiting before retrying the request.
         *//*

        public static final int STATUS_WAITING_TO_RETRY = 194;

        */
/**
         * This download is waiting for network connectivity to proceed.
         *//*

        public static final int STATUS_WAITING_FOR_NETWORK = 195;

        */
/**
         * This download exceeded a size limit for mobile networks and is waiting for a Wi-Fi
         * connection to proceed.
         *//*

        public static final int STATUS_QUEUED_FOR_WIFI = 196;

        */
/**
         * This download is retrying
         *//*

        public static final int STATUS_RETRYING = 197;

        */
/**
         * This download couldn't be completed due to insufficient storage
         * space.  Typically, this is because the SD card is full.
         *//*

        public static final int STATUS_INSUFFICIENT_SPACE_ERROR = 198;

        */
/**
         * This download couldn't be completed because no external storage
         * device was found.  Typically, this is because the SD card is not
         * mounted.
         *//*

        public static final int STATUS_DEVICE_NOT_FOUND_ERROR = 199;

        */
/**
         * This download has successfully completed.
         * Warning: there might be other status values that indicate success
         * in the future.
         * Use isSuccess() to capture the entire category.
         *//*

        public static final int STATUS_DOWNLOAD_SUCCESS = 200;


        */
/**
         * 检查MD5中
         *//*

        public static final int STATUS_CHECKING = 600;


        */
/**
         * 安装中（只有静默安装才有）
         *//*

        public static final int STATUS_INSTALLING = 601;

        */
/**
         * 已安装
         *//*

        public static final int STATUS_INSTALLED = 602;


        */
/**
         * This request couldn't be parsed. This is also used when processing
         * requests with unknown/unsupported URI schemes.
         *//*

        public static final int STATUS_BAD_REQUEST = 400;


        public static final int HTTP_STATUS_NOT_FOUND = 404;


        */
/**
         * This download can't be performed because the content type cannot be
         * handled.
         *//*

        public static final int STATUS_NOT_ACCEPTABLE = 406;

        */
/**
         * This download cannot be performed because the length cannot be
         * determined accurately. This is the code for the HTTP error "Length
         * Required", which is typically used when making requests that require
         * a content length but don't have one, and it is also used in the
         * client when a response is received whose length cannot be determined
         * accurately (therefore making it impossible to know when a download
         * completes).
         *//*

        public static final int STATUS_LENGTH_REQUIRED = 411;

        */
/**
         * This download was interrupted and cannot be resumed.
         * This is the code for the HTTP error "Precondition Failed", and it is
         * also used in situations where the client doesn't have an ETag at all.
         *//*

        public static final int STATUS_PRECONDITION_FAILED = 412;

        */
/**
         * The lowest-valued error status that is not an actual HTTP status code.
         *//*

        public static final int MIN_ARTIFICIAL_ERROR_STATUS = 488;

        */
/**
         * The requested destination file already exists.
         *//*

        public static final int STATUS_FILE_ALREADY_EXISTS_ERROR = 488;

        */
/**
         * Some possibly transient error occurred, but we can't resume the download.
         *//*

        public static final int STATUS_CANNOT_RESUME = 489;

        */
/**
         * This download was canceled
         *//*

        public static final int STATUS_CANCELED = 490;

        */
/**
         * This download has completed with an error.
         * Warning: there will be other status values that indicate errors in
         * the future. Use isStatusError() to capture the entire category.
         *//*

        public static final int STATUS_UNKNOWN_ERROR = 491;

        */
/**
         * This download couldn't be completed because of a storage issue.
         * Typically, that's because the filesystem is missing or full.
         * Use the more specific {@link #STATUS_INSUFFICIENT_SPACE_ERROR}
         * and {@link #STATUS_DEVICE_NOT_FOUND_ERROR} when appropriate.
         *//*

        public static final int STATUS_FILE_ERROR = 492;

        */
/**
         * This download couldn't be completed because of an HTTP
         * redirect response that the download manager couldn't
         * handle.
         *//*

        public static final int STATUS_UNHANDLED_REDIRECT = 493;

        */
/**
         * This download couldn't be completed because of an
         * unspecified unhandled HTTP code.
         *//*

        public static final int STATUS_UNHANDLED_HTTP_CODE = 494;

        */
/**
         * This download couldn't be completed because of an
         * error receiving or processing data at the HTTP level.
         *//*

        public static final int STATUS_HTTP_DATA_ERROR = 495;

        */
/**
         * This download couldn't be completed because of an
         * HttpException while setting up the request.
         *//*

        public static final int STATUS_HTTP_EXCEPTION = 496;

        */
/**
         * This download couldn't be completed because there were
         * too many redirects.
         *//*

        public static final int STATUS_TOO_MANY_REDIRECTS = 497;


        */
/**
         * The file is deleted
         *//*

        public static final int STATUS_FILE_DELETED = 498;

        */
/**
         * This download couldn't be completed because there were
         * too many retries.
         *//*

        public static final int STATUS_TOO_MANY_RETRIES = 499;

        public static final int STATUS_FIRST_VALIDATE_ERROR = 700;


        public static final int STATUS_SECOND_VALIDATE_ERROR = 701;


        public static final int STATUS_TOO_MANY_DELS = 702;


        public static final int STATUS_SIGNATURE_ERROR = 703;

        public static final int STATUS_VALIDATE_LM_ERROR = 704;


        public static String statusToString(int status) {
            switch (status) {
                case STATUS_PENDING:
                    return "PENDING";
                case STATUS_RUNNING:
                    return "RUNNING";
                case STATUS_PAUSED_BY_APP:
                    return "PAUSED_BY_APP";
                case STATUS_WAITING_TO_RETRY:
                    return "WAITING_TO_RETRY";
                case STATUS_WAITING_FOR_NETWORK:
                    return "WAITING_FOR_NETWORK";
                case STATUS_QUEUED_FOR_WIFI:
                    return "QUEUED_FOR_WIFI";
                case STATUS_INSUFFICIENT_SPACE_ERROR:
                    return "INSUFFICIENT_SPACE_ERROR";
                case STATUS_DEVICE_NOT_FOUND_ERROR:
                    return "DEVICE_NOT_FOUND_ERROR";
                case STATUS_DOWNLOAD_SUCCESS:
                    return "SUCCESS";
                case STATUS_BAD_REQUEST:
                    return "BAD_REQUEST";
                case STATUS_NOT_ACCEPTABLE:
                    return "NOT_ACCEPTABLE";
                case STATUS_LENGTH_REQUIRED:
                    return "LENGTH_REQUIRED";
                case STATUS_PRECONDITION_FAILED:
                    return "PRECONDITION_FAILED";
                case STATUS_FILE_ALREADY_EXISTS_ERROR:
                    return "FILE_ALREADY_EXISTS_ERROR";
                case STATUS_CANNOT_RESUME:
                    return "CANNOT_RESUME";
                case STATUS_CANCELED:
                    return "CANCELED";
                case STATUS_UNKNOWN_ERROR:
                    return "UNKNOWN_ERROR";
                case STATUS_FILE_ERROR:
                    return "FILE_ERROR";
                case STATUS_UNHANDLED_REDIRECT:
                    return "UNHANDLED_REDIRECT";
                case STATUS_UNHANDLED_HTTP_CODE:
                    return "UNHANDLED_HTTP_CODE";
                case STATUS_HTTP_DATA_ERROR:
                    return "HTTP_DATA_ERROR";
                case STATUS_HTTP_EXCEPTION:
                    return "HTTP_EXCEPTION";
                case STATUS_TOO_MANY_REDIRECTS:
                    return "TOO_MANY_REDIRECTS";
                default:
                    return Integer.toString(status);
            }
        }
    }
}
*/
