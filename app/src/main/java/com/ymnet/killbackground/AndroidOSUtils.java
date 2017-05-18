package com.ymnet.killbackground;

import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AndroidOSUtils {
	private static final String TAG = "AndroidOSUtils";
	private static int sOsWidth = -1;
	private static int sOsHeight = -1;
	public static final int DEFAULT_OS_WIDTH = 720;
	public static final int DEFAULT_OS_HEIGHT = 1280;

	private static DisplayMetrics sDisplayMetrics;

	public static DisplayMetrics getDisplayMetrics(Context context) {
		if (sDisplayMetrics == null) {
			synchronized (AndroidOSUtils.class) {
				if (sDisplayMetrics == null) {
					sDisplayMetrics = context.getResources().getDisplayMetrics();
				}
			}
		}
		return sDisplayMetrics;
	}

	public static void initOsHeigth(Context context) {
		if (sOsHeight <= 0) {
			synchronized (AndroidOSUtils.class) {
				if (sOsHeight <= 0) {
					sOsHeight = getDisplayMetrics(context).heightPixels;
				}
			}
		}
	}

	public static void initOSWidth(Context context) {
		if (sOsWidth <= 0) {
			synchronized (AndroidOSUtils.class) {
				if (sOsWidth <= 0) {
					sOsWidth = getDisplayMetrics(context).widthPixels;
				}
			}
		}
	}

	public static int getDisplayHeight() {
		return sOsHeight > 0 ? sOsHeight : DEFAULT_OS_HEIGHT;
	}

	public static int getDisplayWidth() {
		return sOsWidth > 0 ? sOsWidth : DEFAULT_OS_WIDTH;
	}

	public static int dip2Pix(Context context, float dip) {
		return ((int) (dip * getDisplayMetrics(context).density + 0.5f));
	}

	public static void clear() {
		sDisplayMetrics = null;
	}

	public static float getDispalyDensity(Context context) {
		return getDisplayMetrics(context).density;
	}

	public static String getProductInfo() {

		String phoneInfo = Build.BRAND;

		return  phoneInfo;
	}

	public static String getSystemProperty(String propName){
		String line  = null;
		BufferedReader input = null;
		try {
			Process p = Runtime.getRuntime().exec("getprop " + propName);
			input = new BufferedReader(new InputStreamReader(p.getInputStream()), 256);
			line = input.readLine();
			input.close();
		} catch (IOException ex) {
			Log.e("PhoneBrandFactory", "Unable to read sysprop " + propName, ex);
		} finally {
			if(input != null){
				try {
					input.close();
				} catch (IOException e) {
					Log.e("PhoneBrandFactory", "Exception while closing InputStream", e);
				}
			}
		}

		return line;
	}

}
