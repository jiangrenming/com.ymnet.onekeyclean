package com.ymnet.onekeyclean.cleanmore.constants;

/**
 * Created by wangduheng26 on 4/1/16.
 */
public interface WeChatConstants {
    public static String BASE_PATH = "/tencent/MicroMsg/";

    public static String CHAT_COMMON_PATH = BASE_PATH + "weixin";


    public static String FIREND_CACHE_PATH = BASE_PATH + "***/sns";
    public static String CHAT_PIC = BASE_PATH + "***/image2";
    public static String CHAT_VOICE = BASE_PATH + "***/voice2";
    public static String CHAT_VIDEO = BASE_PATH + "***/video";


    public static final int WECHAT_TYPE_DEFALUT = 0;

    public static final int WECHAT_TYPE_VOICE = 1;

    public static final int WECAHT_TYPE_PIC = 2;

    public static final String WECHAT_TIME_STATUE_SIX = "6个月前";
    public static final String WECHAT_TIME_STATUE_THREE = "3个月前";
    public static final String WECHAT_TIME_STATUE_ONE = "1个月前";
    public static final String WECHAT_TIME_STATUE_ONE_BEFORE = "1个月内";


    //TEMP 路径
    public static String[] TEMP_PATH = {
            BASE_PATH + "cache",
            BASE_PATH + "wvtemp",
            BASE_PATH + "WebviewCache",
            BASE_PATH + "vusericon"
    };

    public static String[] TEMP_ACCOUNT_PATH = {
            BASE_PATH + "***/music"
    };

    //朋友圈缓存路径

    public static String[] CACHE_PATH = null;

    public static String[] CACHE_ACCOUNT_PATH = {
            BASE_PATH + "***/sns"
    };



    /**
     * 主意对应的顺序
     * [0] 公共路径下的后缀
     * [1] 账户下的后缀
     */
    public static String[] PIC_SUFFIX = new String[]{".jpg", ".webp", ".jpg.tmp", ".jpeg"};

    //图片路径
    public static String[] PIC_PATH = {
            CHAT_COMMON_PATH
    };

    public static String[] PIC_ACCOUNT_PATH = {
            BASE_PATH + "***/image2"
    };

    /**
     * 主意对应的顺序
     * [0] 公共路径下的后缀
     * [1] 账户下的后缀
     */
    public static String[] VOICE_SUFFIX = new String[]{".amr"};

    //语音路径
    public static String[] VOICE_PATH = null;

    public static String[] VOICE_ACCOUNT_PATH = {
            BASE_PATH + "***/voice2"
    };


    /**
     * 主意对应的顺序
     * [0] 公共路径下的后缀
     * [1] 账户下的后缀
     */
    public static String[] VIDEO_SUFFIX = new String[]{".mp4"};

    public static String[] VIDEO_SUFFIX_ACCOUNT=new String[]{".mp4"};

    //视频路径
    public static String[] VIDEO_PATH={
            CHAT_COMMON_PATH
    };
    //视频扫描路径--没用了
    public static String[] VIDEO_ACCOUNT_PATH = {
            BASE_PATH + "***/video"
    };

    String WECHAT_EXPORT_PATH ="/storage/wechatfile_export";
}
