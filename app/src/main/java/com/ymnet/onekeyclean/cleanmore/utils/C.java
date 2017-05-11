package com.ymnet.onekeyclean.cleanmore.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class C {

	public static Context sContext;

	public static void setContext(Context c) {
		sContext = c;
	}

	public static Context get() {
		return sContext;
	}

	public static String getPreference(String name, String defValue) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(sContext);
		String result = prefs.getString(name, defValue);
		return result;
	}
	
	public static Boolean getPreference(String name, boolean defValue) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(sContext);
        Boolean result = prefs.getBoolean(name, defValue);
        return result;
    }

	public static void setPreference(String name, String value) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(sContext);
		Editor editor = sp.edit();
		editor.putString(name, value);
		editor.commit();
	}
	
	public static void setPreference(String name, long value) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(sContext);
        Editor editor = sp.edit();
        editor.putLong(name, value);
        editor.commit();
    }
	
	public static void setPreference(String name, boolean value){
		SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(sContext);
        Editor editor = sp.edit();
        editor.putBoolean(name, value);
        editor.commit();
	}
	
	public static Long getPreference(String name, long defValue) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(sContext);
        long result = prefs.getLong(name, defValue);
        return result;
    }
	
	public static void setPreference(String name, int value) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(sContext);
        Editor editor = sp.edit();
        editor.putInt(name, value);
        editor.commit();
    }

    public static void removePreference(String name) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(sContext);
        Editor editor = sp.edit();
        editor.remove(name);
        editor.commit();
    }
	
	public static int getPreference(String name, int defValue) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(sContext);
        return prefs.getInt(name, defValue);
    }

}
