package com.ymnet.onekeyclean.cleanmore.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author gxz
 * @datetime：2013-7-14 上午02:11:35
 * @类说明 <p>
 *      Date and time formatting utilities and constants.
 *      </p>
 *      日期处理；目标是参照实现package org.apache.commons.lang.time;下的DateFormatUtils
 */
public class DateFormatUtils
{
    
    /**
     * The format used is 2013-07-13 05:07
     */
    public static final String PATTERN_YMDHM = "yyyy-MM-dd HH:mm";
    
    public static final String PATTERN_YMDHM2 = "yyyy/MM/dd HH:mm";
    
    /**
     * The format used is 2013-07-13
     */
    public static final String PATTERN_YMD = "yyyy-MM-dd";
    
    /**
     * The format used is 05:07
     */
    public static final String PATTERN_TIME = "HH:mm:ss";
    
    public static String format(long millis, String pattern)
    {
        return format(new Date(millis), pattern);
    }
    
    public static String format(Date date, String pattern)
    {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        return df.format(date);
    }
    
    /**
     * 获取两个操作间共耗时
     * 
     * @param a
     *            System.currentTimeMillis();
     * @param b
     *            System.currentTimeMillis();
     * @return
     * @throws Exception
     */
    public static String getDurtion(long a, long b)
    {
        long cha = a > b ? a - b : b - a;
        return "共耗时：" + (cha / 1000) + "秒" + cha % 1000 + "微妙";
    }

    public static boolean isToday(Date dateA,Date dateB) {
        Calendar calDateA = Calendar.getInstance();
        calDateA.setTime(dateA);

        Calendar calDateB = Calendar.getInstance();
        calDateB.setTime(dateB);

        return calDateA.get(Calendar.YEAR) == calDateB.get(Calendar.YEAR)
                && calDateA.get(Calendar.MONTH) == calDateB.get(Calendar.MONTH)
                &&  calDateA.get(Calendar.DAY_OF_MONTH) == calDateB.get(Calendar.DAY_OF_MONTH);
    }

    public static long getMillisByDate(String dateStr, String pattern) {
        if(TextUtils.isEmpty(dateStr)) {
            return 0;
        }
        long time = 0;
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        Date date = null;
        try {
            date = df.parse(dateStr);
            time = date.getTime()/1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }
}
