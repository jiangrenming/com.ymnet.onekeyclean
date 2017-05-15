package com.ymnet.onekeyclean.cleanmore.constants;

/**
 * Created by wangduheng26 on 4/1/16.
 */
// TODO: 2017/5/3 0003 QQ扫描路径要修改
public interface QQConstants {
    String BASE_PATH = "/Tencent/MobileQQ/";

    String CHAT_COMMON_PATH = BASE_PATH /*+ "weixin"*/;


    String FIREND_CACHE_PATH = BASE_PATH + "***/sns";
    String CHAT_PIC          = BASE_PATH + "***/image2";
    String CHAT_VOICE        = BASE_PATH + "***/voice2";
    String CHAT_VIDEO        = BASE_PATH + "***/video";


    int QQ_TYPE_DEFALUT = 0;

    int QQ_TYPE_VOICE = 1;

    int QQ_TYPE_PIC = 2;

    int QQ_TYPE_RECEIVE = 3;

    String QQ_TIME_STATUE_SIX        = "6个月前";
    String QQ_TIME_STATUE_THREE      = "3个月前";
    String QQ_TIME_STATUE_ONE        = "1个月前";
    String QQ_TIME_STATUE_ONE_BEFORE = "1个月内";

    // TODO: 2017/5/3 0003 QQ缓存清理目录
    //TEMP 路径
    String[] TEMP_PATH = {
            /*BASE_PATH + "cache",
            BASE_PATH + "wvtemp",
            BASE_PATH + "WebviewCache",
            BASE_PATH + "vusericon"*/
            BASE_PATH + "diskcache"

    };

    String[] TEMP_ACCOUNT_PATH = {
            BASE_PATH + "***/music"
    };

    /**
     * 主意对应的顺序
     * [0] 公共路径下的后缀
     * [1] 账户下的后缀
     */
    String[] PIC_SUFFIX = new String[]{".jpg", ".webp", ".jpg.tmp", ".jpeg"};
    /**
     * 接收文件的后缀
     */
    String[] RECEIVE_FILE_SUFFIX = new String[]{".gif",".rar",".picture",".doc",".excel",".pdf",".ppt",".txt",".radio",".video",""};

    //图片路径
    String[] PIC_PATH = {
            CHAT_COMMON_PATH
    };

    String[] PIC_ACCOUNT_PATH = {
            "/Tencent/QQ_Images"
    };

    /**
     * 主意对应的顺序
     * [0] 公共路径下的后缀
     * [1] 账户下的后缀
     */
//    String[] VOICE_SUFFIX = new String[]{".amr"};
    String[] VOICE_SUFFIX = new String[]{".slk"};

    //语音路径
    String[] VOICE_PATH = null;

    String[] VOICE_ACCOUNT_PATH = {
            BASE_PATH + "***/ptt"
    };


    /**
     * 主意对应的顺序
     * [0] 公共路径下的后缀
     * [1] 账户下的后缀
     */
    String[] VIDEO_SUFFIX = new String[]{".mp4"};
    //无用
    String[] VIDEO_SUFFIX_ACCOUNT = new String[]{".mp4"};

    //视频路径
    String[] VIDEO_PATH         = {
            CHAT_COMMON_PATH
    };

    String[] VIDEO_ACCOUNT_PATH = {//\Tencent\MobileQQ\shortvideo
            BASE_PATH + "shortvideo"
    };
    //导出目录
//    String QQ_EXPORT_PATH = "/DCIM/qqexport";
    String   QQ_EXPORT_PATH     = "/storage/QQfile_export";

    //接收的文件
    String[] RECEIVE_FILE={
            CHAT_COMMON_PATH
    };
    String[] RECEIVE_FILE_ACCOUNT={
      "/Tencent/QQfile_recv"
    };

}