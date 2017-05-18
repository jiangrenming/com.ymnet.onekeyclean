package com.ymnet.onekeyclean.cleanmore.constants;

import android.os.Environment;

public interface Constants
{
    /** 应用分类 */
    static final String CATEGORY_APP = "app";
    
    /** 游戏分类 */
    static final String CATEGORY_GAME = "game";
    
    /** check leftmenu */
    static final String CHECK_LEFTMENU = "check_leftmenu";
    
    /** check slide */
    static final String CHECK_SLIDE = "check_slide";
    
    /** check download */
    static final String CHECK_DOWNLOAD = "check_download";
    
    /** 游戏分类 */
    /** check UPDATE */
    static final String UPDATE_COUNT = "check_download";
    
    /** 排序的类型：下载次数 */
    static final int ORDER_TYPE_DOWNLOAD = 1;
    
    /** 排序的类型：发布时间 */
    static final int ORDER_TYPE_TIME = 2;
    
    /** 排序的类型：装机量 */
    static final int ORDER_TYPE_INSTALLED_NUM = 3;
    
    /** 状态 -- 未下载 */
    static final int STATUS_NORMAL = 0;
    
    /** 状态 -- 准备开始下载 */
    static final int STATUS_PENDING = 1;
    
    /** 状态 -- 已安装 */
    static final int STATUS_INSTALLED = 11;
    
    /** 状态 -- 已下载未安装 */
    static final int STATUS_DOWNLOADED = 9;
    
    /** 状态 -- 可更新 */
    static final int STATUS_UPDATE = 10;
    
    /** 分类信息 */
    static final String EXTRA_CATEGORY = "extra.category";
    
    /** 包名信息 */
    static final String EXTRA_PACKAGE_NAME = "extra.key.package.name";
    
    /** 产品ID信息 */
    static final String EXTRA_PRODUCT_ID = "extra.key.pid";
    
    /** 产品来源信息（市场/谷歌市场） */
    static final String EXTRA_SOURCE_TYPE = "extra.key.source.type";
    
    /** 产品详细信息 */
    static final String EXTRA_PRDUCT_DETAIL = "extra.product.detail";
    
    /** 产品截图ID信息 */
    static final String EXTRA_SCREENSHOT_ID = "extra.screenshot.id";
    
    /** 产品排序信息 （按时间或者下载量） */
    static final String EXTRA_SORT_TYPE = "extra.order";
    
    /** 产品分类ID */
    static final String EXTRA_CATEGORY_ID = "extra.category.id";
    
    /** 搜索结果页分类 */
    static final String EXTRA_SEARCH_TYPE = "extra.search.type";
    
    /** 首页预加载内容 */
    static final String EXTRA_HOME_DATA = "extra.home.data";
    
    /** 首页预加载内容（顶部） */
    static final String EXTRA_HOME_DATA_TOP = "extra.home.data.top";
    
    /** 首页预加载内容（底部） */
    static final String EXTRA_HOME_DATA_BOTTOM = "extra.home.data.bottom";
    
    /** 加载最大值 */
    static final String EXTRA_MAX_ITEMS = "extra.max.items";
    
    static final int INFO_UPDATE = 0;
    
    static final int INFO_REFRESH = 1;
    
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    /** SubTitle Keyword */
    static final String KEY_PLACEHOLDER = "place_holder";
    
    /*
     * Product Item Information
     */
    static final String KEY_PRODUCT_ID = "p_id";
    
    static final String KEY_PRODUCT_NAME = "name";
    
    static final String KEY_PRODUCT_TYPE = "product_type";
    
    static final String KEY_PRODUCT_SUB_CATEGORY = "sub_category";
    
    static final String KEY_PRODUCT_IS_STAR = "is_star";
    
    static final String KEY_PRODUCT_CATEGORY = "product_category";
    
    static final String KEY_PRODUCT_DESCRIPTION = "product_description";
    
    static final String KEY_PRODUCT_PRICE = "price";
    
    static final String KEY_PRODUCT_SIZE = "app_size";
    
    static final String KEY_PRODUCT_RATING = "rating";
    
    static final String KEY_PRODUCT_DOWNLOAD_URI = "url";
    
    static final String KEY_PRODUCT_PAY_TYPE = "pay_category";
    
    static final String KEY_PRODUCT_SHORT_DESCRIPTION = "short_description";
    
    static final String KEY_PRODUCT_SOURCE_TYPE = "source_type";
    
    static final String KEY_PRODUCT_AUTHOR = "author_name";
    
    static final String KEY_PRODUCT_DOWNLOAD = "product_download";
    
    static final String KEY_PRODUCT_ICON_URL = "icon_url";
    
    static final String KEY_PRODUCT_ICON_URL_LDPI = "ldpi_icon_url";
    
    static final String KEY_PRODUCT_PACKAGE_NAME = "packagename";
    
    static final String KEY_PRODUCT_VERSION_NAME = "version_name";
    
    static final String KEY_PRODUCT_VERSION_CODE = "version_code";
    
    static final String KEY_PRODUCT_COMMENTS_COUNT = "comments_count";
    
    static final String KEY_PRODUCT_RATING_COUNT = "ratings_count";
    
    static final String KEY_PRODUCT_DOWNLOAD_COUNT = "download_count";
    
    static final String KEY_PRODUCT_LONG_DESCRIPTION = "long_description";
    
    static final String KEY_PRODUCT_PUBLISH_TIME = "publish_time";
    
    static final String KEY_PRODUCT_MD5 = "filemd5";
    
    static final String KEY_PRODUCT_UP_REASON = "up_reason";
    
    static final String KEY_PRODUCT_UP_TIME = "up_time";
    
    static final String KEY_PRODUCT_PERMISSIONS = "uses_permission";
    
    static final String KEY_PRODUCT_IS_INSTALLED = "is_installed";
    
    static final String KEY_PRODUCT_STATUS = "status";
    
    static final String KEY_PRODUCT_IS_RECOMMEND = "product_is_recommend";
    
    static final String KEY_PRODUCT_ICON = "icon_url";
    
    static final String KEY_PRODUCT_INFO = "info";
    
    static final String KEY_PRODUCT_PUBLIC_TIME = "public_time";
    
    static final String KEY_PRODUCT_LIST = "product_list";
    
    static final String KEY_PRODUCT = "product";
    
    static final String KEY_PRODUCTS = "products";
    
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    
    static final String KEY_CATEGORY_ID = "category_id";
    
    static final String KEY_CATEGORY_NAME = "category_name";
    
    static final String KEY_ID = "id";
    
    static final String KEY_SUBJECT = "subject";
    
    static final String KEY_TOTAL_SIZE = "total_size";
    
    static final String KEY_END_POSITION = "end_position";
    
    static final String KEY_JK_LIST = "bbsAttJkVOList";
    
    static final String KEY_FILE_LIST = "bbsAttJkFileVOList";
    
    static final String KEY_FILE_NAME = "fileName";
    
    static final String KEY_DOWN_URL = "downloadUrl";
    
    static final String KEY_SUB_LIST = "sub";
    
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    
    static final String KEY_CATEGORY = "category";
    
    static final String KEY_CATEGORYS = "categorys";
    
    static final String KEY_SUB_CATEGORY = "sub_category";
    
    static final String KEY_APP_COUNT = "app_count";
    
    static final String KEY_CATEGORY_ICON_URL = "icon_url";
    
    static final String KEY_TOP_APP = "top_app";
    
    static final String KEY_APP_1 = "app_1";
    
    static final String KEY_APP_2 = "app_2";
    
    static final String KEY_APP_3 = "app_3";
    
    static final String KEY_RECOMMEND_TYPE = "top_recommend_type";
    
    static final String KEY_RECOMMEND_ICON = "pic";
    
    static final String KEY_RECOMMEND_ID = "id";
    
    static final String KEY_RECOMMEND_TITLE = "reason";
    
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    
    static final String KEY_KEYLIST = "keys";
    
    static final String KEY_TEXT = "text";
    
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    
    static final String KEY_DOWNLOAD_INFO = "download_info";
    
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    static final String KEY_USER_NAME = "name";
    
    static final String KEY_USER_UID = "uid";
    
    static final String KEY_USER_EMAIL = "email";
    
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    static final String KEY_COMMENTS = "comments";
    
    static final String KEY_COMMENT = "comment";
    
    static final String KEY_COMMENT_BODY = "comment";
    
    static final String KEY_COMMENT_ID = "comment_id";
    
    static final String KEY_COMMENT_AUTHOR = "author";
    
    static final String KEY_COMMENT_DATE = "date";
    
    static final String KEY_COMMENT_LIST = "comment_list";
    
    static final String KEY_VALUE = "value";
    
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    static final String KEY_TOPICS = "topics";
    
    static final String KEY_TOPIC = "topic";
    
    static final String KEY_TOPIC_ID = "topic_id";
    
    static final String KEY_TOPIC_ICON = "app_icon_url";
    
    static final String KEY_TOPIC_ICON_LDPI = "ldpi_app_icon_url";
    
    static final String KEY_TOPIC_NAME = "name";
    
    static final String KEY_REQUIRED_CATEGORY = "req_category";
    
    static final String KEY_REQUIRED_CATEGORY_NAME = "name";
    
    static final String KEY_PAY_CONSUME = "consume";
    
    static final String KEY_PAY_CHARGE = "charge";
    
    static final String KEY_PAY_BUY_APP = "buy_app";
    
    static final String KEY_PAY_LOGS = "logs";
    
    static final String KEY_PAY_FLAG = "flag";
    
    static final String KEY_PAY_ORDER_ID = "order_id";
    
    static final String KEY_PAY_DESCRIPTION = "description";
    
    static final String KEY_PAY_TIME = "create_time";
    
    static final String KEY_PAY_MONEY = "money";
    
    static final String KEY_PAY_STATUS = "status";
    
    // 支付页
    static final String REMOTE_VERSION = "remote_version";
    
    static final String RESULT = "result";
    
    static final String PAY_CARD = "card";
    
    static final String PAY_TYPE = "pay_type";
    
    static final String ACCOUNT_LEN = "account_len";
    
    static final String PASSWORD_LEN = "password_len";
    
    static final String PAY_CREDIT = "credit";
    
    static final String PAY_RESULT = "pay_result";
    
    // 下载文件的状态
    static final int STATUS_DOWNLOADING = 1;
    
    static final int STATUS_PAUSE = 2;
    
    //建议升级
    static final int SUGGEST_UPDATE = 1;
    
    //强制升级
    static final int FORCE_UPDATE = 2;
    
    /** 点击下载列表 */
    static final String BROADCAST_CLICK_INTENT = "com.market2345.download.intent";
    
    // 检查是否有新splash图片
    static final String BROADCAST_SPLASH_CHECK_UPGRADE = "com.mappn.market.broadcast.splash.CHECK_UPGRADE";
    
    // 提示更新的广播
    static final String BROADCAST_NOTICE_UPGRADE = "com.mappn.market.broadcast.notice.upgrade";
    
    static final String BROADCAST_CHECK_UPGRADE = "com.mappn.market.broadcast.check.upgrade";
    
    static final String BROADCAST_FORCE_EXIT = "com.market2345.broadcast.FORCE_EXIT";
    
    static final String BROADCAST_REMIND_LATTER = "com.market2345.broadcast.REMIND_LATTER";
    
    static final String BROADCAST_DOWNLOAD_OPT = "com.market2345.broadcast.DOWNLOAD_OPT";
    
    static final String BROADCAST_DOWNLOAD = "com.market2345.broadcast.DOWNLOAD";
    
    static final int NO_UPDATE = 0;
    
    static final String EXTRA_UPDATE_LEVEL = "update_level";
    
    static final String EXTRA_VERSION_CODE = "version_code";
    
    static final String EXTRA_VERSION_NAME = "version_name";
    
    static final String EXTRA_DESCRIPTION = "description";
    
    static final String EXTRA_URL = "apk_url";
    
    static final String PRODUCT_RESPONSE = "response";
    
    // product attribute
    static final String PRODUCT_PACKAGENAME = "package_name";
    
    static final String INSTALL_APP_LOGO = "logo";
    
    static final String INSTALL_APP_TITLE = "app_title";
    
    static final String INSTALL_APP_DESCRIPTION = "app_detail";
    
    static final String INSTALL_APP_CHECKED = "app_checked";
    
    static final String INSTALL_PLACE_HOLDER = "place_holder";
    
    static final String INSTALL_APP_IS_CHECKED = "is_checked";
    
    // BBS搜索结果Title
    static final String SEARCH_RESULT_TITLE = "search_result_title";
    
    static final String GROUP_1 = "装机必备";
    
    static final String GROUP_2 = "搜索";
    
    static final String GROUP_3 = "推荐位";
    
    static final String GROUP_4 = "首页";
    
    static final String GROUP_5 = "分类页";
    
    static final String GROUP_6 = "排行页";
    
    static final String GROUP_7 = "管理页";
    
    static final String GROUP_8 = "菜单";
    
    static final String GROUP_9 = "登录页";
    
    static final String GROUP_10 = "注册页";
    
    static final String GROUP_11 = "个人中心页";
    
    static final String GROUP_12 = "应用详情页";
    
    static final String GROUP_13 = "反馈页";
    
    static final String GROUP_14 = "产品列表页";
    
    // 装机必备模式提示次数
    static final String ALERT = "提醒";
    
    // 通过提示进入装机必备模式的次数
    static final String ALERT_CLICK = "点击提醒";
    
    // 搜索打开次数
    static final String OPEN_SEARCH = "点击进入搜索";
    
    // 搜索页点击搜索按钮次数
    static final String CLICK_SEARCH = "点击搜索按钮";
    
    // 点击热门关键词次数
    static final String CLICK_SEARCH_KEYWORDS = "点击热门关键词";
    
    // 搜索结果页点击论坛tab的次数
    static final String CLICK_SEARCH_BBS = "点击论坛TAB";
    
    // 搜索结果页附件下载次数
    static final String CLICK_SEARCH_BBS_APK = "附件下载";
    
    // 首页头部广告图9张，每一张的点击
    static final String CLICK_RECOMMEND_TOP = "点击顶部推荐位 -> ";
    
    // 分类菜单点击次数
    static final String CLICK_CATEGORY_TAB = "点击分类TAB";
    
    // 排行菜单点击次数
    static final String CLICK_RANK_TAB = "点击排行TAB";
    
    // 管理菜单点击次数
    static final String CLICK_MANAGER_TAB = "点击管理TAB";
    
    // 首页菜单点击次数
    static final String CLICK_HOME_TAB = "点击首页TAB";
    
    // static final String 首页30条应用已安装的比例
    // static final String 首页30条应用点击应用名称及图标区域的次数
    // 首页30条应用点击直接下载、更新的次数
    static final String DIRECT_DOWNLOAD = "直接下载、更新";
    
    // 装机必备入口点击数
    static final String ENTRY = "点击装机必备";
    
    // 专题入口点击次数
    static final String CLICK_TOPIC_ENTRY = "点击专题";
    
    // 每个专题点击次数
    static final String CLICK_SUB_TOPIC = "点击专题 -> ";
    
    // 每个分类、子分类点击次数
    static final String CLICK_CATEGORY_ITEM = "点击分类 -> ";
    
    // 每个子分类下“最新”tab点击次数
    static final String CLICK_SUB_CATEGORY_NEW_TAB = "点击最新TAB";
    
    // 每个子分类最热、最新产品列表的下一页读取次数
    static final String PRODUCT_LAZY_LOAD = "产品延迟加载";
    
    // 排行下各个tab点击次数
    static final String CLICK_RANK_APP = "点击应用排行";
    
    static final String CLICK_RANK_GAME = "点击游戏排行";
    
    static final String CLICK_RANK_BOOK = "点击电子书排行";
    
    static final String CLICK_RANK_POP = "点击风向标排行";
    
    // static final String 排行下各个tab下的下一页读取次数
    
    // 已下载软件管理点击次数
    static final String CLICK_FILE_MANAGER = "点击文件管理";
    
    // 应用管理界面，更新点击次数
    static final String CLICK_UPDATE = "点击更新";
    
    // 应用管理界面，卸载点击次数
    static final String CLICK_UNINSTALL = "点击卸载";
    
    // 应用管理界面，应用详情页点击次数
    static final String CLICK_DETAIL = "点击应用详情";
    
    // 详情页打开次数
    static final String OPEN_PRODUCT_DETAIL = "打开详情页";
    
    // 在详情页点击下载或更新次数
    static final String DETAIL_DOWNLOAD = "点击下载或更新";
    
    // 详情页介绍，“更多”点击次数
    static final String DETAIL_CLICK_MORE = "点击更多介绍";
    
    // 详情页，截图点击次数
    static final String DETAIL_CLICK_SNAPSHOT = "点击截图";
    
    // 详情页，评论tab点击次数
    static final String DETAIL_CLICK_COMMENT = "查看评论";
    
    // 详情页，发起评论点击次数
    static final String DETAIL_POST_COMMENT = "发表评论";
    
    // 账号点击次数
    static final String MENU_CLICK_ACCOUNT = "点击用户中心";
    
    // 登录次数
    static final String LOGIN = "点击登录";
    
    // 注册次数
    static final String REGISTER = "点击注册";
    
    // 登录成功次数
    static final String LOGIN_SUCCESS = "登录成功";
    
    // 注册成功次数
    static final String REGISTER_SUCCESS = "注册成功";
    
    // 云推送开启次数
    static final String OPEN_PUSH = "开启云推送";
    
    // 云推送关闭次数
    static final String CLOSE_PUSH = "关闭云推送";
    
    // 充值入口点击次数
    static final String CHARGE = "点击充值";
    
    // static final String 支付宝充值每个流程阶段进入次数
    // static final String 电话卡充值每个流程阶段进入次数
    
    // 反馈点击次数
    static final String MENU_CLICK_FEEDBACK = "打开反馈页";
    
    // 反馈发送次数
    static final String SEND_FEEDBACK = "发送反馈";
    
    // 设置点击次数
    static final String MENU_CLICK_SETTINGS = "打开设置";
    
    // 缓存清除次数
    static final String CLICK_CLEAR_CACHE = "清除缓存";
    
    // 搜索历史清除次数
    static final String CLICK_CLEAR_SEARCH_HISTORY = "清除搜索历史";
    
    // 应用过滤设置次数
    static final String OPEN_FILTER = "打开应用过滤";
    
    // 社区点击次数
    static final String MENU_CLICK_BBS = "打开社区";
    
    static final String CATEGORY_DATA_APP = "category_data_app";
    
    static final String CATEGORY_DATA_GAME = "category_data_game";
    
    static final String CATEGORY_DATA_APP_UPDATETIME = "category_data_app_updatetime";
    
    public static final int CHANGE_TIME = 6;
    
    public static final String SP_DAY = "sp_day";
    
    public static final String SP_HOURSE = "sp_hourse";
    
    public static final String SP_AUTO_CHANGE_NORMAL = "change_normal";
    
    public static final String SP_READER_MODE_NIGHT = "reader_mode_night";
    
    public static final String SP_FULL_MODE = "full_mode";
    
    /**
     * 字体大小
     */
    public static final String PREFERENCES_WEBVIEW_FONT_SIZE = "FontSize";
    
    /**
     * 拦截弹窗
     */
    public static final String PREFERENCES_WEBVIEW_POPUP_LOCKING = "PopupLocking";
    
    /**
     * 音量键控制滚动
     */
    public static final String PREFERENCES_UI_VOLUME_KEYS_BEHAVIOUR = "GeneralVolumeKeysBehaviour";
    
    /**
     * 快捷退出
     */
    public static final String PREFERENCES_QUICK_EXIT = "QuickExit";
    
    /**
     * 无图模式
     */
    public static final String PREFERENCES_WEBVIEW_NO_CHART_PATTERNS = "NoChartPatterns";
    
    /**
     * 最常访问
     */
    public static final String PREFERENCES_MOST_VISITED = "MostVisited";
    
    /**
     * 浏览器标识
     */
    public static final String PREFERENCES_WEBVIEW_UA = "UA";
    
    /**
     * 检查网络
     */
    public static final String PREFERENCES_CHECK_NET = "CheckNet";
    
    /**
     * 恢复设置
     */
    public static final String PREFERENCES_RESTORATION = "Restoration";
    
    /**
     * 恢复设置
     */
    public static final String PREFERENCES_BROWSER = "BrowserDef";
    
    /**
     * 清除痕迹
     */
    public static final String PREFERENCES_WIPE_CACHE = "WipeCache";
    
    /**
     * 隐身模式
     */
    public static final String PREFERENCES_IN_PRIVATE = "InPrivate";
    
    /**
     * 用户中心
     */
    public static final String PREFERENCES_USER_CENTER = "UserCenter";
    
    /**
     * 检查更新
     */
    public static final String PREFERENCES_CHECK_FOR_UPDATES = "CheckForUpdates";
    
    /**
     * 反馈
     */
    public static final String PREFERENCES_FEEDBACK = "Feedback";
    
    /**
     * 关于
     */
    public static final String PREFERENCES_ABOUT = "About";
    
    // 其他界面设置项
    public static final String PREFERENCES_WIPE_HISTORY = "WipeHistory";
    
    public static final String PREFERENCES_WIPE_SEARCH_HISTORY = "WipeSearch";
    
    public static final String PREFERENCES_WIPE_WEBVIEW_CACHE = "WIPEWebViewCache";
    
    public static final String PREFERENCES_WIPE_COOKIES = "WIPECookies";
    
    public static final String SDCard = Environment.getExternalStorageDirectory() + "";
    
    /**
     * apk保存路劲
     */
    public static final String DownloadDir = SDCard + "/download/2345手机助手/";
    
    /** 市场文件下载 路径 */
    public static final String DEFAULT_MARKET_SUBDIR = SDCard + "/2345手机助手/apk";
    
    /** 图片保存路径 */
    public static final String DEFAULT_IMAGE_SUBDIR = SDCard + "/2345手机助手/photo";
    
    /** 壁纸保存路径 */
    public static final String DEFAULT_WALLPAPER_SUBDIR = SDCard + "/2345手机助手/wallpaper";
    
    /** 其他图片保存路径 路径 */
    public static final String DEFAULT_OTHERIMAGE_SUBDIR = SDCard + "/2345手机助手/otherimage";
    
    /** 音乐路径 */
    public static final String DEFAULT_MUSIC_SUBDIR = SDCard + "/2345手机助手/music";
    
    /** 视频路径 */
    public static final String DEFAULT_VIDEO_SUBDIR = SDCard + "/2345手机助手/video";
    
    /** 铃音路径 */
    public static final String DEFAULT_RING_SUBDIR = SDCard + "/2345手机助手/ring";
    
    /**
     * 图片缓存路劲
     */
    public static final String ImageCacheDir = SDCard + "/2345手机助手/cache/";
    
    //  public static final String TAG = "aMarketDownloader";
    
    /**
     * The default extension for html files if we can't get one at the HTTP
     * level
     */
    public static final String DEFAULT_DL_HTML_EXTENSION = ".html";
    
    /**
     * The default extension for text files if we can't get one at the HTTP
     * level
     */
    public static final String DEFAULT_DL_TEXT_EXTENSION = ".txt";
    
    /**
     * The default extension for binary files if we can't get one at the HTTP
     * level
     */
    public static final String DEFAULT_DL_BINARY_EXTENSION = ".bin";
    
    /** A magic filename that is allowed to exist within the system cache */
    public static final String KNOWN_SPURIOUS_FILENAME = "lost+found";
    
    /** A magic filename that is allowed to exist within the system cache */
    public static final String RECOVERY_DIRECTORY = "recovery";
    
    /**
     * The default base name for downloaded files if we can't get one at the
     * HTTP level
     */
    public static final String DEFAULT_DL_FILENAME = "downloadfile";
    
    /**
     * When a number has to be appended to the filename, this string is used to
     * separate the base filename from the sequence number
     */
    public static final String FILENAME_SEQUENCE_SEPARATOR = "-";
    
    /** The intent that gets sent when the service must wake up for a retry */
    public static final String ACTION_RETRY = "2345market.intent.action.DOWNLOAD_WAKEUP";
    
    /** the intent that gets sent when clicking an incomplete/failed download */
    public static final String ACTION_LIST = "2345market.intent.action.DOWNLOAD_LIST";
    
    /** the intent that gets sent when clicking a successful download */
    public static final String ACTION_OPEN = "2345market.intent.action.DOWNLOAD_OPEN";
    
    /**
     * the intent that gets sent when deleting the notification of a completed
     * download
     */
    public static final String ACTION_HIDE = "2345market.intent.action.DOWNLOAD_HIDE";
    
    public static final String ACTION_LIST_DOWNLOADED = "2345market.intent.action.DOWNLOAD_LIST_downloaded";
    
    public static final String ACTION_UPDATE = "2345market.intent.action.UPDATE_APP";
    
    /**
     * This download will be saved to the external storage. This is the default
     * behavior, and should be used for any file that the user can freely
     * access, copy, delete. Even with that destination, unencrypted DRM files
     * are saved in secure internal storage. Downloads to the external
     * destination only write files for which there is a registered handler. The
     * resulting files are accessible by filename to all applications.
     */
    public static final int DESTINATION_EXTERNAL = 0;
    
    /**
     * This download will be saved to the download manager's private partition.
     * This is the behavior used by applications that want to download private
     * files that are used and deleted soon after they get downloaded. All file
     * types are allowed, and only the initiating application can access the
     * file (indirectly through a content provider). This requires the
     * android.permission.ACCESS_DOWNLOAD_MANAGER_ADVANCED permission.
     */
    public static final int DESTINATION_CACHE_PARTITION = 1;
    
    /** The MIME type of APKs */
    public static final String MIMETYPE_APK = "application/vnd.android.package-archive";
    
    /** The MIME type of image */
    public static final String MIMETYPE_IMAGE = "image/*";
    
    /** The buffer size used to stream the data */
    public static final int BUFFER_SIZE = 4096;
    
    /**
     * The minimum amount of progress that has to be done before the progress
     * bar gets updated
     */
    public static final int MIN_PROGRESS_STEP = 4096;
    
    public static final int MIN_REPORT_STEP = 1024 * 16;
    
    /**
     * The minimum amount of time that has to elapse before the progress bar
     * gets updated, in ms
     */
    public static final long MIN_PROGRESS_TIME = 1500;

    /** 每次刷新速度的最小间隔 */
    public static final long CONSTANT_SPEED_INTERVAL = 1500;
    
    /** The maximum number of rows in the database (FIFO) */
    public static final int MAX_DOWNLOADS = 1000;
    
    /**
     * The number of times that the download manager will retry its network
     * operations when no progress is happening before it gives up.
     */
    public static final int MAX_RETRIES = 5;
    
    /**
     * The minimum amount of time that the download manager accepts for a
     * Retry-After response header with a parameter in delta-seconds.
     */
    public static final int MIN_RETRY_AFTER = 30; // 30s
    
    /**
     * The maximum amount of time that the download manager accepts for a
     * Retry-After response header with a parameter in delta-seconds.
     */
    public static final int MAX_RETRY_AFTER = 24 * 60 * 60; // 24h
    
    /**
     * The maximum number of redirects.
     */
    public static final int MAX_REDIRECTS = 5; // can't be more than 7.
    
    /**
     * The time between a failure and the first retry after an IOException. Each
     * subsequent retry grows exponentially, doubling each time. The time is in
     * seconds.
     */
    public static final int RETRY_FIRST_DELAY = 30;
    
    public static final String CHECK_SEND = "check_shortcut";

    public static final int MAX_HEAP_SIZE = (int) Runtime.getRuntime().maxMemory();
    public static final int KB = 1024;
    public static final int MB = 1048576;
    public static final int MAX_DISK_CACHE_SIZE = 40 * MB;
    public static final int MAX_MEMORY_CACHE_SIZE = MAX_HEAP_SIZE / 4;

    //mark for download from out
    public static final int DOWNLOAD_FROM_OUT = -1000;


}
