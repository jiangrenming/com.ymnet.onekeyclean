package com.ymnet.onekeyclean.cleanmore.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * @author Janan
 * @data 1/3/16 12:32.
 * @since version  3.5
 */
public class SharedPreferencesUtil {

    public static final String PREFERENCES_SETTINGS = "settings";//the sharepreferences of settings
    public static final String PREFERENCES_LISTDATA = "specialAppListData";//the sharepreferences of data list,eg:recomend list
    public static final String PREFERENCES_ACCOUNT = "account";  //the sharepreferences pf account

    public static final String PREFERENCES_GUIDE = "guide";


    /**
     * returning a SharedPreferences of MODE_PRIVATE through which you can retrieve and modify its
     * values.
     *
     * @param name Desired preferences file. If a preferences file by this name
     *             does not exist, it will be created when you retrieve an
     *             editor (SharedPreferences.edit()) and then commit changes (Editor.commit()).
     * @return The single instance that can be used
     * to retrieve and modify the preference values.
     */
    public static SharedPreferences getSharedPreferences(String name) {
        if (C.get() == null)
            return null;
        return C.get().getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    /**
     * Gets a SharedPreferences instance that points to the default file
     *
     * @return An Application Context's SharedPreferences  instance that can be used to retrieve and
     * listen to values of the preferences
     */
    public static SharedPreferences getDefaultSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(C.get());
    }

    /**
     * Retrieve an int value from the default preferences
     *
     * @param key The name of the preference to retrieve
     * @return Returns the preference value if it exists, or Integer.MIN_VALUE.  Throws
     * ClassCastException if there is a preference with this name that is not
     * an int.
     * @throws ClassCastException
     */
    public static int getIntFromDefaultSharedPreferences(String key) {
        SharedPreferences sp = getDefaultSharedPreferences();
        return sp == null ? Integer.MIN_VALUE : sp.getInt(key, Integer.MIN_VALUE);
    }


    /**
     * Retrieve a boolean  value from the default preferences
     *
     * @param key The name of the preference to retrieve
     * @return Returns the preference value if it exists, or Boolean.FALSE.  Throws
     * ClassCastException if there is a preference with this name that is not
     * an boolean.
     * @throws ClassCastException
     */
    public static boolean getBooleanFromDefaultSharedPreferences(String key) {
        SharedPreferences sp = getDefaultSharedPreferences();
        return sp == null ? Boolean.FALSE : sp.getBoolean(key, Boolean.FALSE);
    }

    /**
     * Retrieve a String  value from the default preferences
     *
     * @param key The name of the preference to retrieve
     * @return Returns the preference value if it exists, or "".  Throws
     * ClassCastException if there is a preference with this name that is not
     * an string.
     * @throws ClassCastException
     */
    public static String getStringFromDefaultSharedPreferences(String key) {
        SharedPreferences sp = getDefaultSharedPreferences();
        return sp == null ? "" : sp.getString(key, "");
    }

    /**
     * Retrieve an int value from the preferences.
     *
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.  Throws
     * ClassCastException if there is a preference with this name that is not
     * an int.
     * @throws ClassCastException
     */
    public static int getIntFromDefaultSharedPreferences(String key, int defValue) {
        SharedPreferences sp = getDefaultSharedPreferences();
        return sp == null ? defValue : sp.getInt(key, defValue);
    }

    /**
     * Retrieve a boolean  value from the default preferences
     *
     * @param key      The name of the preference to retrieve
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.  Throws
     * ClassCastException if there is a preference with this name that is not
     * an boolean.
     * @throws ClassCastException
     */
    public static boolean getBooleanFromDefaultSharedPreferences(String key, boolean defValue) {
        SharedPreferences sp = getDefaultSharedPreferences();
        return sp == null ? defValue : sp.getBoolean(key, defValue);
    }

    /**
     * Retrieve a String  value from the default preferences
     *
     * @param key The name of the preference to retrieve
     * @return Returns the preference value if it exists, or defValue.  Throws
     * ClassCastException if there is a preference with this name that is not
     * an string.
     * @throws ClassCastException
     */
    public static String getStringFromDefaultSharedPreferences(String key, String defValue) {
        SharedPreferences sp = getDefaultSharedPreferences();
        return sp == null ? defValue : sp.getString(key, defValue);
    }

    /**
     * Retrieve a long  value from the default preferences
     *
     * @param key The name of the preference to retrieve
     * @return Returns the preference value if it exists, or defValue.  Throws
     * ClassCastException if there is a preference with this name that is not
     * an string.
     * @throws ClassCastException
     */
    public static long getLongFromDefaultSharedPreferences(String key, long defValue) {
        SharedPreferences sp = getDefaultSharedPreferences();
        return sp == null ? defValue : sp.getLong(key, defValue);
    }


    /**
     * Retrieve an int value from the preferences of given name.
     *
     * @param name     the name of the preference
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.  Throws
     * ClassCastException if there is a preference with this name that is not
     * an int.
     * @throws ClassCastException
     */
    public static int getIntFromSharePreferences(String name, String key, int defValue) {
        SharedPreferences sp = getSharedPreferences(name);
        return sp == null ? defValue : sp.getInt(key, defValue);
    }

    /**
     * Retrieve an boolean value from the preferences of given name.
     *
     * @param name     the name of the preference
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.  Throws
     * ClassCastException if there is a preference with this name that is not
     * an int.
     * @throws ClassCastException
     */
    public static boolean getBooleanFromSharePreferences(String name, String key, boolean defValue) {
        SharedPreferences sp = getSharedPreferences(name);
        return sp == null ? defValue : sp.getBoolean(key, defValue);
    }

    /**
     * Retrieve an int value from the preferences of given name.
     *
     * @param name     the name of the preference
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.  Throws
     * ClassCastException if there is a preference with this name that is not
     * an int.
     * @throws ClassCastException
     */
    public static String getStringFromSharePreferences(String name, String key, String defValue) {
        SharedPreferences sp = getSharedPreferences(name);
        return sp == null ? defValue : sp.getString(key, defValue);
    }

    /**
     * Retrieve an long value from the preferences of given name.
     *
     * @param name     the name of the preference
     * @param key      The name of the preference to retrieve.
     * @param defValue Value to return if this preference does not exist.
     * @return Returns the preference value if it exists, or defValue.  Throws
     * ClassCastException if there is a preference with this name that is not
     * an int.
     * @throws ClassCastException
     */
    public static long getLongFromSharePreferences(String name, String key, long defValue) {
        SharedPreferences sp = getSharedPreferences(name);
        return sp == null ? defValue : sp.getLong(key, defValue);
    }

    /**
     * Set an int value in the application context default preferences, to be written back once
     *
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference.
     * @return Returns a reference to the same Editor object.
     */
    public static boolean putIntToDefaultSharedPreferences(String key, int value) {
        SharedPreferences sp = getDefaultSharedPreferences();
        return sp.edit().putInt(key, value).commit();
    }

    /**
     * Set a boolean value in the application context default preferences, to be written back once
     *
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference.
     * @return Returns a reference to the same Editor object.
     */
    public static boolean putBooleanToDefaultSharedPreferences(String key, boolean value) {
        SharedPreferences sp = getDefaultSharedPreferences();
        return sp == null ? false : sp.edit().putBoolean(key, value).commit();
    }

    /**
     * Set a String value in the application context default preferences, to be written back once
     *
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference.
     * @return Returns a reference to the same Editor object.
     */
    public static boolean putStringToDefaultSharedPreferences(String key, String value) {
        SharedPreferences sp = getDefaultSharedPreferences();
        return sp == null ? false : sp.edit().putString(key, value).commit();
    }

    /**
     * Set a long value in the application context default preferences, to be written back once
     *
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference.
     * @return Returns a reference to the same Editor object.
     */
    public static boolean putLongToDefaultSharedPreferences(String key, long value) {
        SharedPreferences sp = getDefaultSharedPreferences();
        return sp == null ? false : sp.edit().putLong(key, value).commit();
    }

    /**
     * Set a int value in ths preferences of given name, to be written back once
     *
     * @param name  Desired preferences file
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference.
     * @return Returns a reference to the same Editor object.
     */
    public static boolean putIntToSharePreferences(String name, String key, int value) {
        SharedPreferences sp = getSharedPreferences(name);
        return sp == null ? false : sp.edit().putInt(key, value).commit();
    }

    /**
     * Set a boolean value in ths preferences of given name, to be written back once
     *
     * @param name  Desired preferences file
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference.
     * @return Returns a reference to the same Editor object.
     */
    public static boolean putBooleanToSharePreferences(String name, String key, boolean value) {
        SharedPreferences sp = getSharedPreferences(name);
        return sp == null ? false : sp.edit().putBoolean(key, value).commit();
    }

    /**
     * Set a String value in ths preferences of given name, to be written back once
     *
     * @param name  Desired preferences file
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference.
     * @return Returns a reference to the same Editor object.
     */
    public static boolean putStringToSharePreferences(String name, String key, String value) {
        SharedPreferences sp = getSharedPreferences(name);
        return sp == null ? false : sp.edit().putString(key, value).commit();
    }

    /**
     * Set a Long value in ths preferences of given name, to be written back once
     *
     * @param name  Desired preferences file
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference.
     * @return Returns a reference to the same Editor object.
     */
    public static boolean putLongToSharePreferences(String name, String key, long value) {
        SharedPreferences sp = getSharedPreferences(name);
        return sp == null ? false : sp.edit().putLong(key, value).commit();
    }
}
