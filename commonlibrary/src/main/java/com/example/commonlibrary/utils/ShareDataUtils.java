package com.example.commonlibrary.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * Created by MajinBuu on 2017/4/7.
 * @override SharedPreferences的存入与获取
 */
public class ShareDataUtils {
    public static String getSharePrefData(Context context, String name, String key) {
        if (context == null) return null;
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sp.getString(key, null);
    }

    public static Map<String,?> getSharePrefDataAll(Context context, String name) {
        if (context == null) return null;
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sp.getAll();
    }

    public static boolean getSharePrefBooleanData(Context context, String name, String key) {
        if (context == null) return false;
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sp.getBoolean(key, false);

    }



    public static long getSharePrefLongData(Context context, String name, String key) {
        if (context == null) return 0;
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sp.getLong(key, 0);

    }

    public static int getSharePrefIntData(Context context, String name, String key) {
        if (context == null) return 0;
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sp.getInt(key, 0);

    }

    public static void setSharePrefData(Context context, String name, String key, String value) {
        if (context == null) return;
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void setSharePrefData(Context context, String name, String key, boolean value) {
        if (context == null) return;
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void setSharePrefData(Context context, String name, String key, long value) {
        if (context == null) return;
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static void setSharePrefData(Context context, String name, String key, int value) {
        if (context == null) return;
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void removeSharePrefData(Context context, String name, String key) {
        if (context == null) return;
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }

    public static int getInt(Context context, String prfName, String key, int defaultValue) {
        if (context == null) return defaultValue;
        return context.getSharedPreferences(prfName, Context.MODE_PRIVATE).getInt(key, defaultValue);
    }

    public static void setInt(Context context, String prfName, String key, int Value) {
        if (context == null) return;
        SharedPreferences sharePreference = context.getSharedPreferences(prfName, Context.MODE_PRIVATE);
        if (sharePreference != null) {
            SharedPreferences.Editor editor = sharePreference.edit();
            editor.putInt(key, Value).commit();
        }

    }

}
