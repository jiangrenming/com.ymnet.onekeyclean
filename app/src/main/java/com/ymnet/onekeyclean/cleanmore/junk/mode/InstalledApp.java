package com.ymnet.onekeyclean.cleanmore.junk.mode;

import com.google.gson.JsonObject;

import java.util.HashSet;

/**
 * @author gxz
 * @editor zhangcm
 * @version 2013-7-25 下午12:16:24
 * @类说明 已安装的app
 */
public class InstalledApp
{

    public static final int APK_USER = 1;

    public static final int APK_SYS = 0;


    public int uid;
    public String packageName = "";
    
    public String versionName = "";
    
    public int versionCode;
    
    public String appName = "";
    
//    public Drawable appIcon;
    
    public String size = "";
    
    public long totalFileSize = 0;

    public long lastUpdateTime = 0;

    public HashSet<String> signatures;
    
    public int iconSize;
    
    public int canMove = 1;
    
    public int store;
    
    public String storeLocation;

    /** 0代表系统应用，1代表用户安装应用 */
    public int flag;


    
    public boolean isCanMoveGeted = false;
    
    public boolean isLatestVersion = true;//true不需要升级，false需要升级
    
    public int softId;
    
    public String softUrl;
    
    public String softName;
    
    public String latestSize;
    
    public int latestVersionCode;
    
    public String latestVersionName;
    
    public String iconUrl;

    public String traceInfo;
    
    
    
    /**
     * Flags associated with the application.  Any combination of
     * {@link android.content.pm.ApplicationInfo#FLAG_SYSTEM}, {@link android.content.pm.ApplicationInfo#FLAG_DEBUGGABLE}, {@link android.content.pm.ApplicationInfo#FLAG_HAS_CODE},
     * {@link android.content.pm.ApplicationInfo#FLAG_PERSISTENT}, {@link android.content.pm.ApplicationInfo#FLAG_FACTORY_TEST}, and
     * {@link android.content.pm.ApplicationInfo#FLAG_ALLOW_TASK_REPARENTING}
     * {@link android.content.pm.ApplicationInfo#FLAG_ALLOW_CLEAR_USER_DATA}, {@link android.content.pm.ApplicationInfo#FLAG_UPDATED_SYSTEM_APP},
     * {@link android.content.pm.ApplicationInfo#FLAG_TEST_ONLY}, {@link android.content.pm.ApplicationInfo#FLAG_SUPPORTS_SMALL_SCREENS},
     * {@link android.content.pm.ApplicationInfo#FLAG_SUPPORTS_NORMAL_SCREENS},
     * {@link android.content.pm.ApplicationInfo#FLAG_SUPPORTS_LARGE_SCREENS}, {@link android.content.pm.ApplicationInfo#FLAG_SUPPORTS_XLARGE_SCREENS},
     * {@link android.content.pm.ApplicationInfo#FLAG_RESIZEABLE_FOR_SCREENS},
     * {@link android.content.pm.ApplicationInfo#FLAG_SUPPORTS_SCREEN_DENSITIES}, {@link android.content.pm.ApplicationInfo#FLAG_VM_SAFE_MODE},
     * {@link android.content.pm.ApplicationInfo#FLAG_INSTALLED}.
     */
    public int flags = 0;

    public InstalledApp()
    {
        
    }
    
    public InstalledApp(String packageName, String versionName, int versionCode)
    {
        this.packageName = packageName;
        this.versionCode = versionCode;
        this.versionName = versionName;
    }
    
    public JsonObject toJSONObject1()
    {
    	JsonObject object = new JsonObject();
        object.addProperty("packageName", packageName);
        object.addProperty("versionName", versionName);
        object.addProperty("versionCode", versionCode);
        object.addProperty("iconSize", iconSize);
        object.addProperty("appName", appName);
        object.addProperty("size", totalFileSize);
        object.addProperty("canMove", canMove);
        object.addProperty("store", store);
        object.addProperty("storeLocation", storeLocation);
        object.addProperty("flag", flag);
//        object.addProperty("isLatestVersion", isLatestVersion);
        if(!isLatestVersion){       	
        	object.addProperty("softId", softId);
        	object.addProperty("softUrl", softUrl);
        	object.addProperty("softName", softName);
        	object.addProperty("latestSize", latestSize);
        	object.addProperty("latestVersionCode", latestVersionCode);
        	object.addProperty("latestVersionName", latestVersionName);
        	object.addProperty("iconUrl", iconUrl);
        }
        return object;
    }
}
