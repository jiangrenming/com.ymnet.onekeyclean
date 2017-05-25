
package com.ymnet.onekeyclean.cleanmore.model;

import java.io.Serializable;
import java.util.List;


/**
 * @author
 * @version 2013-9-29 下午02:15:12
 * @类说明 一个app
 */


public class App implements Serializable {

    private static final long serialVersionUID = 1L;

    public int sid;
    //游戏or软件   17：软件  18：游戏
    public int type_id;

    public String category_id;

    public String category_title;

    public String title;

    //搜索结果列表页 是否有标签 1 火爆 2 推荐
    public int isHot;

    public String version;

    public String packageName;

    public int versionCode;

    public String totalDowns;

    public String fileLength;

    public String publicDate;

    public String mark; //评分

    public String summary;

    // 完整包下载地址
    public String url;

    public String icon;

    public String smallimgInfo;

    public String bigimgInfo;

    public String oneword;

    public String eventTitle;

    public String eventIntro;

    public String sNotice;

    public String sLabel;

    public String chapterSoftWords;

    public int ad = -1;

    public int charge = -1;

    public int safe = -1;

    public String tagName;


    public int recomIco;

    public String language;

    public String author;

    public String adverType;

    /**
     * 最低版本
     */
    public String sysIng;

    /**
     * 最低版本号
     */
    public int minSDK;

    /**
     * 判断签名
     */
    public String certMd5;

    /**
     * 增量包下载地址，如果有增量包的话
     * 注意:讯飞开屏上报数据会临时将该字段作他用
     */
    public String patch_url;


    /**
     * 增量包大小
     */

    public long patch_size;

    /**
     * 低版本包md5值
     */

    public String low_md5;


    /**
     * 更新日志
     */
    public String updateLog;
    /**
     * 评论数目
     */
    public int commentTotal;
    /**
     * 礼包
     */
    public int giftTotal;

    public String imgInfo;

    public int appPos;

    public boolean isDisplayGuessYouLike;

    public List<App> guessYouLikeData;

    /**
     * 历史版本
     */
    public int historyTotal;

    /**
     *     biddingos traceInfo
     */
    public String traceInfo;

    /**
     * 软件下载来源
     */
//    public int mSourceFrom = DownloadReportProvider.SOURCEFROM_NORMAL;

    /**
     * 上报所需的字段,可参见
     */

//    public String mPlatform = DownloadReportProvider.MAPI;


    public boolean isLM = false;

//    public int mDownPartial = Downloads.Impl.DOWN_FULL;

    public int seoKeyColor;

    public int sort;//推荐中排序使用
    public int weight;//推荐中排序使用
    public int fixed;//推荐中排序使用
    /**
     * 代表非合作,大于0代表合作
     */
    public int isAd;

    /**
     * 排名变化
     */
    public int deltaRank;

    // 是否搜索推荐, 1=是; 0=否
    public int isRecom;
    /**
     * 标记是否来自搜索建议,本地使用,非后端数据
     */
    public boolean inSearchSuggestion;

    public boolean isFixed() {
        return fixed == 1;
    }

    /**
     * 是否是top200内
     */
    public boolean isTop;

    public String downloadClickPushEvent = null;

    public String clickToDetailEvent = null;

    public String detailDownloadPushEvent = null;


   /* public ContentValues getContentValues() {
        final ContentValues values = new ContentValues();
        values.put(MarketProvider.COLUMN_category_id, category_id);
        values.put(MarketProvider.COLUMN_category_title, category_title);
        values.put(MarketProvider.COLUMN_fileLength, fileLength);
        values.put(MarketProvider.COLUMN_icon, icon);
        values.put(MarketProvider.COLUMN_mark, mark);
        values.put(MarketProvider.COLUMN_packageName, packageName);
        values.put(MarketProvider.COLUMN_publicDate, publicDate);
        values.put(MarketProvider.COLUMN_screenslot, smallimgInfo);
        values.put(MarketProvider.COLUMN_sid, sid);
        values.put(MarketProvider.COLUMN_summary, summary);
        values.put(MarketProvider.COLUMN_title, title);
        values.put(MarketProvider.COLUMN_totalDowns, totalDowns);
        values.put(MarketProvider.COLUMN_url, url);
        values.put(MarketProvider.COLUMN_version, version);
        values.put(MarketProvider.COLUMN_versionCode, versionCode);
        values.put(MarketProvider.COLUMN_oneword, oneword);
        values.put(MarketProvider.COLUMN_ad, ad);
        values.put(MarketProvider.COLUMN_charge, charge);
        values.put(MarketProvider.COLUMN_safe, safe);
        values.put(MarketProvider.COLUMN_tagName, tagName);
        values.put(MarketProvider.COLUMN_sysIng, sysIng);
        values.put(MarketProvider.COLUMN_minSDK, minSDK);
        values.put(MarketProvider.COLUMN_certMd5, certMd5);
        return values;
    }*/
}


