package com.ymnet.onekeyclean.cleanmore.junk.alert;

import android.app.AlarmManager;
import android.content.Context;

import com.ymnet.onekeyclean.cleanmore.constants.TimeConstants;
import com.ymnet.onekeyclean.cleanmore.junk.notifycationmanager.NotificationManager2345;

import java.util.Calendar;

public class CleanAlert {
    private static final String tag="CleanAlert";
	private static AlarmManager alarm;

	public static void notify(Context context,String size) {
		NotificationManager2345 manager = NotificationManager2345.getInstance(context);
		manager.notifyClean2345(size);
		// updateCleanDatabase();
	}
	 
	/*public static void setAlert(Context context,long triggerAtMillis){
		Intent intent=new Intent(context,CoreService.class);
		intent.setAction(CoreService.ACTION_SCAN_DUMP);
		PendingIntent sender=PendingIntent.getService(context, 0, intent, 0);  
		if(alarm!=null)alarm.cancel(sender);
		alarm=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);  
		alarm.set(AlarmManager.RTC_WAKEUP,triggerAtMillis, sender);
		CleanSetSharedPreferences.setAlertTime(context, triggerAtMillis);
	}

	public static void initAlert(Context context) {
		boolean bAlertSet = CleanSetSharedPreferences.getLastSet(context,CleanSetSharedPreferences.CLEAN_ALERT_SET, true);
		if(!bAlertSet){//设置不提醒的话取消闹钟
			if(alarm!=null){
				Intent intent=new Intent(context,CoreService.class);
				intent.setAction(CoreService.ACTION_SCAN_DUMP);
				PendingIntent sender=PendingIntent.getService(context, 0, intent, 0);  
				alarm.cancel(sender);
			}
		}else{
			int timeIndex = CleanSetSharedPreferences.getLastSet(context,CleanSetSharedPreferences.CLEAN_TIME_SET,1);
			if(timeIndex>3)timeIndex=1;
			long d=CleanSetSharedPreferences.getAlertTime(context);
			if(d<System.currentTimeMillis()){
				//如果时间已经过了
				int day=1;
				if(timeIndex==0)day=1;//每天
				else if(timeIndex==1)day=3;//每隔三天
				else if(timeIndex==2)day=7;//每隔七天
				else if(timeIndex==3)day=15;//每隔十五天
				setAlert(context,getlong(day));
			}else{
				//如果时间没到
				setAlert(context,d);
			}
			
		}
		
    	
	}*/

    /**
     * 得到 day天后 下午五点的时间long
     * @param day
     * @return
     */
    public static  long getlong(int day){
    	Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH,day);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTimeInMillis()+17* TimeConstants.HOUR;
    }
}
