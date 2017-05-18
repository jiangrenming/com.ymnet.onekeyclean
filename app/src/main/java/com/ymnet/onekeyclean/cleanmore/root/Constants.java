package com.ymnet.onekeyclean.cleanmore.root;

public class Constants {


    public static final String SP_ROOT_TIP = "sp_root_tip";

    /**
     * 清理垃圾完成后的提示
     */
    public static final String KEY_ROOT_CLEAN = "key_root_clean";
    /**
     * 下载页面炒鸡模式提示
     */
    public static final String KEY_ROOT_CANCEL_SILENCE_INSTALL = "key_root_cancel_silence_install";

    /**
     * 管理开机启动页面，如果别的页面root失败，此处可再给一次机会
     */
    public static final String KEY_ROOT_BOOT_OTHER = "key_root_boot_other";

    /**
     * 下载页面炒鸡模式提示两次
     */
    public static final String KEY_ROOT_INSTALL_ALERT_TWICE = "key_root_install_alert_twice";


    public static final int ROOT_STATUS_NOT_PERFORMED = 0;//root还未执行过

    public static final int ROOT_STATUS_FAIL = 1;//root失败

    public static final int ROOT_STATUS_SUCCESS = 2;//root成功

    public static final int ROOT_STATUS_NO_UNSTABLE_ROOT = 3;//本地root失败，且没有任何非稳定漏洞

    public static final String COMMAND_SU = "su";

    public static final String COMMAND_SH = "sh";

    public static final String COMMAND_LINE_END = "\n";

    public static final String COMMAND_EXIT = "exit\n";

    public static final String COMMAND_BEGIN = "command_begin";

    public static final String COMMAND_END = "command_end";


    /**
     * The set of SU location I know by now.
     */
    public static final String[] SU_BINARY_DIRS = {
            "/system/bin", "/system/sbin", "/system/xbin",
            "/vendor/bin", "/sbin"
    };

    /**
     * The expire time for granted permission, ten minutes
     */
    public static final long PERMISSION_EXPIRE_TIME = 1000 * 60 * 10;

    /**
     * The command string for install app
     */
    public static final String COMMAND_INSTALL = "pm install -r ";

    /**
     * The patch for some android versions and devices. Install process may fail
     * without this patch.
     */
    public static final String COMMAND_INSTALL_PATCH = "LD_LIBRARY_PATH=/vendor/lib:/system/lib ";

    /**
     * Install apps on SD-card.
     */
    public static final String COMMAND_INSTALL_LOCATION_EXTERNAL = "-s ";

    /**
     * Install apps on phone RAM.
     */
    public static final String COMMAND_INSTALL_LOCATION_INTERNAL = "-f ";

    /**
     * The command string for uninstall app.
     */
    public static final String COMMAND_UNINSTALL = "pm uninstall ";

    /**
     * The command string for screen cap.
     */
    public static final String COMMAND_SCREENCAP = "screencap ";

    /**
     * The command string for processing show.
     */
    public static final String COMMAND_PS = "ps";

    /**
     * The command string for kill process.
     */
    public static final String COMMAND_KILL = "kill ";

    /**
     * The command string for find pid of a process.
     */
    public static final String COMMAND_PIDOF = "pidof ";

    /**
     * The command string for screen record.
     */
    public static final String COMMAND_SCREENRECORD = "screenrecord ";

    /**
     * The default command timeout is 5 min.
     */
    public static final int COMMAND_TIMEOUT = 1000 * 60 * 5;

    /**
     * The path of system
     */
    public static final String PATH_SYSTEM = "/system/";

    /**
     * The path of system bin
     */
    public static final String PATH_SYSTEM_BIN = "/system/bin/";

    /**
     * The default recording bit rate
     */

    public static final long SCREENRECORD_BITRATE_DEFAULT = 4000000L;

    /**
     * The default recording time limit
     */
    public static final int SCREENRECORD_TIMELIMIT_DEFAULT = 30;

}
