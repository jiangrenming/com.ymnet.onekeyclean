/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ymnet.killbackground;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;

import com.umeng.analytics.MobclickAgent;
import com.ymnet.onekeyclean.cleanmore.utils.C;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Various utilities shared amongst the Launcher's classes.
 */
public final class Utilities {

	public static final String UMENG_CHANNEL = "UMENG_CHANNEL";
	private static final String TAG = "Launcher.Utilities";
		//是否安装
	public static boolean isAppInstalled(Context context, String pkgName) {
		boolean installed = false;
		try {
			PackageManager pm = context.getPackageManager();
			try {
				pm.getPackageInfo(pkgName, PackageManager.GET_ACTIVITIES);
				installed = true;
			} catch (PackageManager.NameNotFoundException e) {
				MobclickAgent.reportError(C.get(),"com.ymnet.killbackground.Utilities:"+e.toString());
				installed = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			MobclickAgent.reportError(C.get(),"com.ymnet.killbackground.Utilities:"+e.toString());
		}
		return installed;
	}

	//是否安装
	public static boolean isAppInstalled(Context context, ComponentName cn) {
		boolean installed = false;
		try {
			PackageManager pm = context.getPackageManager();
			try {
				pm.getActivityInfo(cn, PackageManager.GET_ACTIVITIES);
				installed = true;
			} catch (PackageManager.NameNotFoundException e) {
				installed = false;
				MobclickAgent.reportError(C.get(),"com.ymnet.killbackground.Utilities:"+e.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			MobclickAgent.reportError(C.get(),"com.ymnet.killbackground.Utilities:"+e.toString());
		}
		return installed;
	}

	public static boolean hasSimaCard(Context context){
		boolean hasSimaCard = true;
		try {
			TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			int simState = mTelephonyManager.getSimState();
			String hintMessage = "";
			switch (simState) {
				case TelephonyManager.SIM_STATE_ABSENT:
					hasSimaCard = false;
				default:
			}

			String imsi = mTelephonyManager.getSubscriberId(); // 取出IMSI

			if (imsi == null || imsi.length() <= 0) {
				hasSimaCard = false;
			} else {
				hasSimaCard = true;
			}
		}catch (Exception ex){
			MobclickAgent.reportError(C.get(),"com.ymnet.killbackground.Utilities:"+ex.toString());
		}
		return hasSimaCard;
	}
	public static String builderDateWithoutSplit() {
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		return sf.format(date);
	}
	public static boolean isNum(String str) {
		return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
	}

	public static String getPackageChannel(Context context){
		ApplicationInfo appInfo = null;
		try {
			appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			String channel = appInfo.metaData.getString(UMENG_CHANNEL);
			return channel;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
			MobclickAgent.reportError(C.get(),"com.ymnet.killbackground.Utilities:"+e.toString());
			return null;
		}
	}
}
