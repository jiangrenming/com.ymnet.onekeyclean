package com.ymnet.onekeyclean.cleanmore.blackandwhite;/*
package com.example.baidumapsevice.blackandwhite;

import android.content.ContentValues;

import com.example.baidumapsevice.utils.MarketProvider;

import java.io.Serializable;
import java.util.Date;

*/
/**
 * @author janan
 * @version since
 * @date 2014-5-14 上午10:33:48
 * @description 新的app类，主要更改了equals方法
 * BAW -->blank and white
 *//*

public class AppBAWLocal implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    public int sid;
    
    public int type_id;
    
    public String category_id;
    
    public String category_title;
    
    public String title;

    public int isHot;
    
    public String version;
    
    public String packageName;
    
    public int versionCode;
    
    public String totalDowns;
    
    public String fileLength;
    
    public String publicDate;
    
    public String mark;
    
    public String summary;
    
    public String url;
    
    public String icon;
    
    public String screenslot;
    
    public String oneword;

    public String sLabel;
    
    public int ad = -1;
    
    public int charge = -1;
    
    public int safe = -1;
    
    public String tagName;
    
    public String iconbg;
    
    //
    public String tagParent;
    
    public String fcolor;
    
    public int recomIco;
    
    public String sLang;
    
    public String sOperName;
    
    public boolean isInspected = false;//是否已经进行过url检测
    
    public String smallimgInfo;
    
    public String bigimgInfo;
    
    public Date lastUpdateTime;

    public String sysIng;

    public int minSDK;
    
    public ContentValues getContentValues()
    {
        final ContentValues values = new ContentValues();
        values.put(MarketProvider.COLUMN_category_id, category_id);
        values.put(MarketProvider.COLUMN_category_title, category_title);
        values.put(MarketProvider.COLUMN_fileLength, fileLength);
        values.put(MarketProvider.COLUMN_icon, icon);
        values.put(MarketProvider.COLUMN_mark, mark);
        values.put(MarketProvider.COLUMN_packageName, packageName);
        values.put(MarketProvider.COLUMN_publicDate, publicDate);
        values.put(MarketProvider.COLUMN_screenslot, screenslot);
        values.put(MarketProvider.COLUMN_sid, sid);
        values.put(MarketProvider.COLUMN_summary, summary);
        values.put(MarketProvider.COLUMN_title, title);
        values.put(MarketProvider.COLUMN_totalDowns, totalDowns);
        values.put(MarketProvider.COLUMN_url, url);
        values.put(MarketProvider.COLUMN_version, version);
        values.put(MarketProvider.COLUMN_versionCode, versionCode);
        values.put(MarketProvider.COLUMN_oneword, oneword);
        values.put(MarketProvider.COLUMN_slabel, sLabel);
        values.put(MarketProvider.COLUMN_ad, ad);
        values.put(MarketProvider.COLUMN_charge, charge);
        values.put(MarketProvider.COLUMN_safe, safe);
        values.put(MarketProvider.COLUMN_tagName, tagName);
        values.put(MarketProvider.COLUMN_sysIng, sysIng);
        values.put(MarketProvider.COLUMN_minSDK, minSDK);
        return values;
    }

    @Override
    public boolean equals(Object o)
    {
        if(o != null && (getClass() == o.getClass())){
            return ((AppBAWLocal) o).packageName.equals(packageName);
        }
        return false;
    }
    @Override
    public int hashCode()
    {
        return  packageName == null ? -1:packageName.hashCode();
    }
    
    public static AppBAWLocal createFromApp(App app)
    {
        AppBAWLocal copy = new AppBAWLocal();
        copy.version = app.version;
        copy.versionCode = app.versionCode;
        copy.packageName = app.packageName;
        return copy;
    }
    
}
*/
