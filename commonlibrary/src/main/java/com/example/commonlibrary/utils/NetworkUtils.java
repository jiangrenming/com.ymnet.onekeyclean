package com.example.commonlibrary.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 网络相关工具类
 * 
 */
public class NetworkUtils {

	public static final String TAG = "NetworkUtils";

	/**
	 * Returns whether the network is available
	 * 
	 * @param context
	 *            Context
	 * @return 网络是否可用
	 * @see [类、类#方法、类#成员]
	 */
	public static boolean isNetworkAvailable(Context context) {
		if(context == null){
			return false;
		}

		try {
			ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity == null) {
			} else {
				NetworkInfo[] info = connectivity.getAllNetworkInfo();
				if (info != null) {
					for (int i = 0; i < info.length; i++) {
						if (info[i].getState() == NetworkInfo.State.CONNECTED) {
							return true;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * 获取网络类型
	 * 
	 * @param context
	 *            Context
	 * @return 网络类型
	 * @see [类、类#方法、类#成员]
	 */
	public static int getNetworkType(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {

		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {

						return info[i].getType();
					}
				}
			}
		}

		return -1;
	}

	/**
	 * 判断网络是不是手机网络，非wifi
	 * 
	 * @param context
	 *            Context
	 * @return boolean
	 * @see [类、类#方法、类#成员]
	 */
	public static boolean isMobileNetwork(Context context) {
		return isNetworkAvailable(context) && (ConnectivityManager.TYPE_MOBILE == getNetworkType(context));
	}

	/**
	 * 判断网络是不是wifi
	 * 
	 * @param context
	 *            Context
	 * @return boolean
	 * @see [类、类#方法、类#成员]
	 */
	public static boolean isWifiNetwork(Context context) {
		return isNetworkAvailable(context) && (ConnectivityManager.TYPE_WIFI == getNetworkType(context));
	}

	/**
	 * Returns whether the network is roaming
	 * 
	 * @param context
	 *            Context
	 * @return boolean
	 * @see [类、类#方法、类#成员]
	 */
	public static boolean isNetworkRoaming(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {

		} else {
			NetworkInfo info = connectivity.getActiveNetworkInfo();
			TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			if (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE) {
				if (/* TelephonyManager.getDefault() */telephonyManager.isNetworkRoaming()) {

					return true;
				} else {

				}
			} else {

			}
		}
		return false;
	}

	/** 获取本机ip。4.0有问题 */
	public static String getLocalIpAddress() {
		// String ipaddress = "";
		try {
			Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
			while (en.hasMoreElements()) {
				NetworkInterface intf = en.nextElement();
				Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
				while (enumIpAddr.hasMoreElements()) {
					InetAddress inetAddress = enumIpAddr.nextElement();

					// loopback地址就是代表本机的IP地址，只要第一个字节是127，就是lookback地址
					if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
						return inetAddress.getHostAddress().toString();
						// ipaddress = ipaddress + ";" +
						// inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			ex.printStackTrace();
		}
		return null;
		// return ipaddress;
	}

	/** 手机号码 */
	public static String getLine1Number(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getLine1Number();
	}

	/** 判断端口是否被占用 */
	public static boolean isPortUsed(int port) {
		String[] cmds = { "netstat", "-an" };

		Process process = null;
		InputStream is = null;
//		DataInputStream dis = null;
		BufferedReader dis = null;
		String line = "";
		Runtime runtime = Runtime.getRuntime();

		try {
			process = runtime.exec(cmds);
			is = process.getInputStream();
			dis=new BufferedReader(new InputStreamReader(is));
//			dis = new DataInputStream(is);
			while ((line = dis.readLine()) != null) {
				// LogUtils.error(line);
				if (line.contains(":" + port)) {
					return true;
				}
			}
		} catch (IOException ie) {
			ie.printStackTrace();
		} finally {
			try {
				if (dis != null) {
					dis.close();
				}
				if (is != null) {
					is.close();
				}
				if (process != null) {
					process.destroy();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
}
