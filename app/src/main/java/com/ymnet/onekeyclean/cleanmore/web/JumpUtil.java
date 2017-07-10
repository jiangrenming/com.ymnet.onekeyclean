package com.ymnet.onekeyclean.cleanmore.web;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.statisticalsdk.main.StatisticalSdkHerlper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/5/10.
 */

public class JumpUtil {
    private String TAG = "JumpUtil";
    private static JumpUtil util;
    private String SEND_KEYWORDS_TO_SEARCH_ENGINE_URL  = "https://yz.m.sm.cn/s?from=";
    private String SEND_KEYWORDS_TO_SEARCH_ENGINE_WORD = "&q=";
    private List<ResolveInfo> webs = new ArrayList<>();
    public static String   searchEngineChannel = "wm716414";
    private       int      number              = 0;
    private       String[] webOnline           = {"com.xp.browser"," com.browser_llqhz","sogou.mobile.explorer","com.UCMobile","com.tencent.mtt","com.qihoo.browser","com.browser2345","com.baidu.browser.apps"};

    public static JumpUtil getInstance() {
        if (util == null) {
            util = new JumpUtil();
        }
        return util;
    }
    /**
     * 获取本地推广游览器,应用商店
     */
    public void getWebAddresss(final Context context) {
        if (context == null) return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                //获取浏览器
                List<ResolveInfo> activitys;
                PackageManager pm = context.getPackageManager(); // 获得PackageManager对象
                final Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.parse("http://");
                intent.setData(uri);
                activitys = pm.queryIntentActivities(intent, 0);
                String channel;
                webs.clear();
                for (ResolveInfo info : activitys) {
                    String pkgName = info.activityInfo.packageName;
                    String title = info.loadLabel(pm).toString();
                    HashMap<String, String> mapInstall = StatisticalSdkHerlper.getFileContent(context, pkgName);
                    channel = mapInstall.get("channelId");
                    if (!TextUtils.isEmpty(channel)) {
                        for (int i = 0; i < webOnline.length; i++) {
                            if (pkgName.equals(webOnline[i])) {
                                Log.i(TAG, "getWebAddresss Web pkgName:" + pkgName + " title:" + title);
                                webs.add(info);
                                break;
                            }
                        }
                    }
                }
                if(webs == null || webs.size() == 0)webs.add(activitys.get(0));
            }
        }).start();

    }


    /**
     * 启动游览器进入百度联想词搜索页
     *
     * @param word 联想词
     */
    public void unJumpBaiduWord(Context context, String word, int flag) {
        String uri = "";
        if (TextUtils.isEmpty(word)) {
            uri = SEND_KEYWORDS_TO_SEARCH_ENGINE_URL + searchEngineChannel;
        } else {
            uri = SEND_KEYWORDS_TO_SEARCH_ENGINE_URL + searchEngineChannel + SEND_KEYWORDS_TO_SEARCH_ENGINE_WORD + word;
        }
        unJumpAddress(context, uri, flag);
    }

    /**
     * 启动游览器进入对应的url页面
     *
     * @param url 启动页地址
     */
    public void unJumpAddress(Context context, String url, int flag) {
        Uri uri = Uri.parse(url);
        Intent webIntent = new Intent(Intent.ACTION_VIEW, uri);
        try {
            if (webs != null && webs.size() > 0) {
                ResolveInfo info = webs.get(number);
                number++;
                number = number == webs.size() ? 0 : number;
                if (flag == 20 && info.activityInfo.packageName.equals("com.baidu.searchbox")) {
                    info = webs.get(number);
                    number++;
                    number = number == webs.size() ? 0 : number;
                }
                String packageName = info.activityInfo.packageName;
                String className = info.activityInfo.name;
                Log.i(TAG, "unJumpAddress packageName:" + packageName + " className:" + className);
                ComponentName componentName = new ComponentName(packageName, className);
                webIntent.setComponent(componentName);
            }
            webIntent.addCategory(Intent.CATEGORY_DEFAULT);
            webIntent.addCategory(Intent.CATEGORY_BROWSABLE);
            webIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(webIntent);
        } catch (Exception e) {
            webIntent.addCategory(Intent.CATEGORY_DEFAULT);
            webIntent.addCategory(Intent.CATEGORY_BROWSABLE);
            webIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(webIntent);
        }
    }

}
