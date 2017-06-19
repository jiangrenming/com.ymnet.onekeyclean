package com.ymnet.onekeyclean.cleanmore.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class CleanSetSharedPreferences {
	public static final String CLEAN_SETTING_PREFERENCE = "clean_setting_preference";

	public static final String PROGRESS_BUTTON_STATE = "progress_button_state";

	public static final String BUTTON_STATE = "button_state";

	public static final String CLEAN_QQ_PREFERENCE = "clean_qq_preference";

	public static final String INTERCEPTION_CHANNEL="iterception_channel";

	public static final String APP_FIRST_START="app_first_start_date";

	public static final String LOCAT_TIME="local_update_app_time";

	public static final String CLEAN_ALERT_SET = "clean_alert_set";//是否开启提醒

	public static final String CLEAN_TIME_SET = "clean_time_set";//设置提醒的间隔时间

	public static final String CLEAN_SIZE_SET = "clean_size_set";//设置提醒的大小


	public static final String CLEAN_DATABASE_UPDATE_SET = "clean_database_update_set";
	public static final String PREVIOUS_SCAN_SIZE="previous_scan_size";
	public static final String CLEAN_SIZE_LAST_TIME="clean_size_last_time";//上次清理的大小
	public static final String CLEAN_QQ_SIZE_LAST_TIME     = "clean_qq_size_last_time";//QQ上次清理大小
	public static final String CLEAN_SIZE_TOTAL="clean_size_total";//总共清理大小
	public static final String CLEAN_QQ_SIZE_TOTAL         = "clean_qq_size_total";//QQ总共清理大小
	public static final String CLEAN_SIZE_TODAY="clean_size_today";//今日清理的大小

	private static final String CLEAN_WECHAT_PREFERENCE  = "clean_wechat_preference";
	private static final String CLEAN_WECHAT_SIZE_LAST_TIME = "clean_wechat_size_last_time";
	private static final String CLEAN_WECHAT_SIZE_TOTAL  = "clean_wechat_size_total";
	/**
     * 清理结果缓存
     */
    public static final String CLEAN_RESULT_CACHE        ="clean_result_cache";
	public static final String CLEAN_DB_IS_ENCRYPT       ="clean_db_is_encrypt";//垃圾库是否为加密库 boolean:default false
	public static final String CLEAN_DB_LAST_UPDATE_TIME ="clean_db_last_update_time";//垃圾库上次更新时间
	public static final String ALERT_TIME                ="alert_time";
	private static final String CLEAN_DB_MD5             = "clean_db_md5";


	/**
     * 保存boolean值
     * @param context
     * @param key
     * @param flag
     * @return
     */
	public static boolean getLastSet(Context context, String key, boolean flag) {
		if (context != null) {
			return context.getSharedPreferences(CLEAN_SETTING_PREFERENCE,
					Context.MODE_PRIVATE).getBoolean(key, flag);
		}
		return flag;
	}

    /**
     * 去boolean值
     * @param context
     * @param key
     * @param value
     */
	public static void setLastSet(Context context, String key, boolean value) {
		if (context != null) {
			context.getSharedPreferences(CLEAN_SETTING_PREFERENCE,
					Context.MODE_PRIVATE).edit().putBoolean(key, value)
					.commit();
		}
	}

    /**
     * 保存int 值
     * @param context
     * @param key
     * @param flag
     * @return
     */
	public static int getLastSet(Context context, String key , int flag) {
		if (context != null) {
			return context.getSharedPreferences(CLEAN_SETTING_PREFERENCE,
					Context.MODE_PRIVATE).getInt(key, flag);
		}
		return flag;
	}

    /**
     * 去int值
     * @param context
     * @param key
     * @param value
     */
	public static void setLastSet(Context context, String key, int value) {
		if (context != null) {
			context.getSharedPreferences(CLEAN_SETTING_PREFERENCE,
					Context.MODE_PRIVATE).edit().putInt(key, value).commit();
		}
	}

	/**
	 * 进度按钮的状态
     */
	public static void setPBState(Context context, String key, String value) {
		if (context != null) {
			context.getSharedPreferences(PROGRESS_BUTTON_STATE,
					Context.MODE_PRIVATE).edit().putString(key, value).commit();
		}
	}

	/**
	 * 进度按钮的状态
     */
	public static String getPBState(Context context, String key , String defValue) {
		if (context != null) {
			return context.getSharedPreferences(PROGRESS_BUTTON_STATE,
					Context.MODE_PRIVATE).getString(key, defValue);
		}
		return defValue;
	}

	/**
	 * 保存上次清理大小 历史清理小大累加  判断有没有今日清理  有则累加 没有则清理文件后重新写入
	 * @param context
	 * @param value
	 */ 
	public static void setCleanLastTimeSize(Context context, long value){
		if (context != null) {
			SharedPreferences sp = context.getSharedPreferences(CLEAN_SETTING_PREFERENCE,Context.MODE_PRIVATE);
			//保存上次清理
			sp.edit().putLong(CLEAN_SIZE_LAST_TIME,value).commit();
			long history=sp.getLong(CLEAN_SIZE_TOTAL, 0);
			sp.edit().putLong(CLEAN_SIZE_TOTAL, history+value).commit();
			setTodayCleanSize(context, value);
		}
	}

	// TODO: 2017/6/13 0013 QQ
	public static void setQQCleanLastTimeSize(Context context, long value){
		if (context != null) {
			SharedPreferences sp = context.getSharedPreferences(CLEAN_QQ_PREFERENCE,Context.MODE_PRIVATE);
			//保存上次清理
			sp.edit().putLong(CLEAN_QQ_SIZE_LAST_TIME,value).commit();
			long history=sp.getLong(CLEAN_QQ_SIZE_TOTAL, 0);
			sp.edit().putLong(CLEAN_QQ_SIZE_TOTAL, history+value).commit();
			setQQTodayCleanSize(context, value);
		}
	}

	public static void setWeChatCleanLastTimeSize(Context context, long value){
		if (context != null) {
			SharedPreferences sp = context.getSharedPreferences(CLEAN_WECHAT_PREFERENCE,Context.MODE_PRIVATE);
			//保存上次清理
			sp.edit().putLong(CLEAN_WECHAT_SIZE_LAST_TIME,value).commit();
			long history=sp.getLong(CLEAN_WECHAT_SIZE_TOTAL, 0);
			sp.edit().putLong(CLEAN_WECHAT_SIZE_TOTAL, history+value).commit();
			setWeChatTodayCleanSize(context, value);
		}
	}

	/**
	 * 上次清理的大小
	 * @param context
	 * @param flag
	 * @return
	 */
	public static long getLaseTimeSize(Context context, long flag) {
		if (context != null) {
			return context.getSharedPreferences(CLEAN_SETTING_PREFERENCE,
					Context.MODE_PRIVATE).getLong(CLEAN_SIZE_LAST_TIME, flag);
		}
		return flag;
	}
	/**
	 * 历史清理的总大小
	 * @param context
	 * @param flag
	 * @return
	 */
	public static long  getTotalCleanSize(Context context,long flag){
		if (context != null) {
			return context.getSharedPreferences(CLEAN_SETTING_PREFERENCE,
					Context.MODE_PRIVATE).getLong(CLEAN_SIZE_TOTAL, flag);
		}
		return flag;
	}

	// TODO: 2017/6/13 0013 QQ
	public static long  getQQTotalCleanSize(Context context,long flag){
		if (context != null) {
			return context.getSharedPreferences(CLEAN_QQ_PREFERENCE,
					Context.MODE_PRIVATE).getLong(CLEAN_QQ_SIZE_TOTAL, flag);
		}
		return flag;
	}

	public static long  getWeChatTotalCleanSize(Context context,long flag){
		if (context != null) {
			return context.getSharedPreferences(CLEAN_WECHAT_PREFERENCE,
					Context.MODE_PRIVATE).getLong(CLEAN_WECHAT_SIZE_TOTAL, flag);
		}
		return flag;
	}

	public static long getTodayCleanSize(Context context,long flag){
		if (context != null) {
			return context.getSharedPreferences("temp",
					Context.MODE_PRIVATE).getLong(getTodayString(), flag);
		}
		return flag;
	}

	public static long getQQTodayCleanSize(Context context,long flag){
		if (context != null) {
			return context.getSharedPreferences("qq_temp",
					Context.MODE_PRIVATE).getLong("qq"+getTodayString(), flag);
		}
		return flag;
	}

	public static long getWeChatTodayCleanSize(Context context,long flag){
		if (context != null) {
			return context.getSharedPreferences("wechat_temp",
					Context.MODE_PRIVATE).getLong("wechat"+getTodayString(), flag);
		}
		return flag;
	}

	public static void setTodayCleanSize(Context context,long flag){
		if (context != null) {
			SharedPreferences sp = context.getSharedPreferences("temp",Context.MODE_PRIVATE);
			long l=sp.getLong(getTodayString(), 0L);
			if(l==0){
				sp.edit().clear().commit();
			}
			sp.edit().putLong(getTodayString(), l+flag).commit();
		}
	}

	public static void setQQTodayCleanSize(Context context,long flag){
		if (context != null) {
			SharedPreferences sp = context.getSharedPreferences("qq_temp",Context.MODE_PRIVATE);
			long l=sp.getLong("qq"+getTodayString(), 0L);
			if(l==0){
				sp.edit().clear().commit();
			}
			sp.edit().putLong("qq"+getTodayString(), l+flag).commit();
		}
	}

	public static void setWeChatTodayCleanSize(Context context,long flag){
		if (context != null) {
			SharedPreferences sp = context.getSharedPreferences("wechat_temp",Context.MODE_PRIVATE);
			long l=sp.getLong("wechat"+getTodayString(), 0L);
			if(l==0){
				sp.edit().clear().commit();
			}
			sp.edit().putLong("wechat"+getTodayString(), l+flag).commit();
		}
	}


	/**
	 * 取得今天的日期  如2014-11-11
	 * @return
	 */
	public static String getTodayString(){
		String today=DateUtils.long2DateSimple(System.currentTimeMillis());
		return today;
	}

	public static long getAlertTime(Context context) {
		if (context != null) {
			return context.getSharedPreferences(CLEAN_SETTING_PREFERENCE,Context.MODE_PRIVATE).getLong(ALERT_TIME, 0);
		}
		return 0;
	}
	public static void setAlertTime(Context context,long value){
		if (context != null) {
			SharedPreferences sp = context.getSharedPreferences(CLEAN_SETTING_PREFERENCE,Context.MODE_PRIVATE);
			sp.edit().putLong(ALERT_TIME, value).commit();
		}
	}

    public static boolean isCleanDbIsEncrypt(Context context) {
        if (context != null) {
            return context.getSharedPreferences(CLEAN_SETTING_PREFERENCE,
                    Context.MODE_PRIVATE).getBoolean(CLEAN_DB_IS_ENCRYPT, false);
        }
        return false;
    }
    public static void setCleanDbIsEncrypt(Context context,boolean val) {
        if (context != null) {
            SharedPreferences sp = context.getSharedPreferences(CLEAN_SETTING_PREFERENCE,Context.MODE_PRIVATE);
            //保存上次清理
            sp.edit().putBoolean(CLEAN_DB_IS_ENCRYPT, val).commit();
        }
    }
    public static void setCleanDbLastUpdateTime(Context context){
            if (context != null) {
                SharedPreferences sp = context.getSharedPreferences(CLEAN_SETTING_PREFERENCE,Context.MODE_PRIVATE);
                //保存上次清理
                sp.edit().putLong(CLEAN_DB_LAST_UPDATE_TIME, System.currentTimeMillis()).commit();
            }
    }
    public static void setCleanDbMd5(Context context,String md5){
        if (context != null) {
            SharedPreferences sp = context.getSharedPreferences(CLEAN_SETTING_PREFERENCE,Context.MODE_PRIVATE);
            //保存上次清理
            sp.edit().putString(CLEAN_DB_MD5, md5).commit();
        }
    }
    public static String getCleanDbMd5(Context context){
        if (context != null) {
            return context.getSharedPreferences(CLEAN_SETTING_PREFERENCE,
                    Context.MODE_PRIVATE).getString(CLEAN_DB_MD5,"");
        }
        return null;
    }
    public static int checkCleanDbUpdatePeriodInDays(Context context){
        if (context != null) {
            long timesInMilliseconds = context.getSharedPreferences(CLEAN_SETTING_PREFERENCE,
                    Context.MODE_PRIVATE).getLong(CLEAN_DB_LAST_UPDATE_TIME, 0);
            return (int) ((System.currentTimeMillis()-timesInMilliseconds)/(3600*24*1000));//多少天
        }
        return 0;
    }

	/**
	 * 卓大师渠道号第一次启动的时间 没有就返回0
	 * @param context
	 * @return
	 */
	public static long readLocalTimeTamp(Context context) {
		if (context != null) {
			return context.getSharedPreferences(CLEAN_SETTING_PREFERENCE,
					Context.MODE_PRIVATE).getLong(LOCAT_TIME, 0);
		}
		return 0;
	}

	/**
	 * 去boolean值
	 * @param context
	 * @param value
	 */
	public static void writeLocalTimeTamp(Context context, long value) {
		if (context != null&&readLocalTimeTamp(context)==0) {
			context.getSharedPreferences(CLEAN_SETTING_PREFERENCE,
					Context.MODE_PRIVATE).edit().putLong(LOCAT_TIME, value).commit();
		}
	}


	/**
	 *上次后台扫描的大小
	 */
	public static long getPreviousScanSize(Context context,long flag){
		if (context != null) {
			return context.getSharedPreferences(CLEAN_SETTING_PREFERENCE,
					Context.MODE_PRIVATE).getLong(PREVIOUS_SCAN_SIZE, flag);
		}
		return flag;
	}
	public static void setPreviousScanSize(Context context,long flag){
		if (context != null) {
			SharedPreferences sp = context.getSharedPreferences(CLEAN_SETTING_PREFERENCE,Context.MODE_PRIVATE);
			sp.edit().putLong(PREVIOUS_SCAN_SIZE,flag).apply();
		}
	}
}
