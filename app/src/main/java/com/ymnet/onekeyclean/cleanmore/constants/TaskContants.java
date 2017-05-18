package com.ymnet.onekeyclean.cleanmore.constants;

/**
 * Created by liup on 2016/4/18.
 */
public class TaskContants {
    /**
     * search
     */
    public static final int TYPE_TASK_SEARCH = 10002;
    /**
     * enter app detail activity
     */
    public static final int TYPE_TASK_APP_DETAIL = 10003;
    /**
     * install a app
     */
    public static final int TYPE_TASK_INSTALL = 10006;
    /**
     * all installed app is latest
     */
    public static final int TYPE_TASK_UPDATE_APP = 10004;
    /**
     * use clean
     */
    public static final int TYPE_TASK_CLEAN = 10005;
    /**
     * bind phone
     */
    public static final int TYPE_TASK_BIND_PHONE = 10000;
    /**
     * checkin
     */
    public static final int TYPE_TASK_CHECKIN = 10001;

    /**
     *  point wall
     */
    public static final int TYPE_TASK_POINT_WALL = 10008;

    public static final String THREAD_NAME = "taskcenter";

    public static final String TASK_MODEL = "taskmodel";
    public static final String TOPIC_MODEL = "topicmodel";
    public static final String LAST_SHOW_UNLOGIN_TIP_TIME = "last_show_unlogintip_time_";
    public static final long TASK_LIST_EXPIRY_DATE = 1*60*60*1000;
    public static final int DOTASK_SUCCESS_CODE = 0;
    public static final int DOTASK_ERROR_CODE_NET_ERROR = 406;
    public static final int DOTASK_ERROR_CODE_REPEAT = 4;
    public static final int DOTASK_ERROR_CODE_UNLOGIN = 500;
}
