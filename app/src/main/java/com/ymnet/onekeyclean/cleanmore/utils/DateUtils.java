package com.ymnet.onekeyclean.cleanmore.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {


	public static final String FORMAT_YMDHMS = "yyyy-MM-dd HH:mm:ss";

	/**
	 * yyyy-MM-dd HH:mm
	 * @param time
	 * @return
	 */
	public static String long2Date(Long time) {
		if(time<100)time=System.currentTimeMillis();
		Calendar c=Calendar.getInstance();
		c.setTimeInMillis(time);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return formatter.format(c.getTime());
	}
	/**
	 * yyyy-MM-dd
	 * @param time
	 * @return
	 */
	public static String long2DateSimple(Long time) {
		if(time<100)time=System.currentTimeMillis();
		Calendar c=Calendar.getInstance();
		c.setTimeInMillis(time);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.format(c.getTime());
	}


	/**
	 * HH:mm
	 * @param time
	 * @return
	 */
	public static String time2String(long time) {
		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		return df.format(new Date(time));
	}


	/**
	 * yyyyMMdd
	 * @param date
	 * @return
	 */
	public static boolean isToday(long date){

		SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
		return sd.format(new Date()).equals(sd.format(new Date(date)));
	}

	public static boolean beforeToday(long date){
		SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
		int flag = sd.format(new Date()).compareTo(sd.format(new Date(date)));
		return flag>0;
	}

	public static Date parseToDate(String format,String date){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date time = null;
		try {
			time = sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return time;
	}

	public static boolean isTodayBetween(String format,String startDate,String endDate){
		Date startTime = parseToDate(format, startDate);
		Date endTime = parseToDate(format, endDate);
		Date today = parseToDate(format, (new SimpleDateFormat(format)).format(new Date()));
		return ((today.compareTo(startTime)*today.compareTo(endTime))<=0);
	}

	public static boolean isTodayOpened(){

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(C.get());
		long lastTime = sp.getLong("navigationshowtime",-1);
		sp.edit().putLong("navigationshowtime",System.currentTimeMillis()).commit();
		if(lastTime<0){
			return false;
		}else{
			return isToday(lastTime);
		}
	}

	public static boolean isTaskViewAnimatorTodayShowed(){

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(C.get());
		long lastTime = sp.getLong("taskviewanimatorshowed",-1);
		sp.edit().putLong("taskviewanimatorshowed",System.currentTimeMillis()).commit();
		if(lastTime<0){
			return false;
		}else{
			return isToday(lastTime);
		}
	}


//	/**
//	 * yyyy-MM-dd
//	 * @param date
//	 * @return
//	 */
//	public static long parseToLong(String date){
//
//		Date time = parseToDate("yyyy/MM/dd", date);
//		String dateStr = ""+time.getYear()+time.getMonth()+time.getDay();
//		time.compareTo()
//		Log.i("janan",time.getTime()+"");
//		return Long.parseLong(dateStr);
//	}
}
