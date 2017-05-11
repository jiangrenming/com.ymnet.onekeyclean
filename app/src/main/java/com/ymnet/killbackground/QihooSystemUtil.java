package com.ymnet.killbackground;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * Created by yyj on 2017-2-7.
 */

public class QihooSystemUtil {

    public static void openAllPermission(Context context, String pkgName){
        if(!isQihooSystem()){
            return;
        }
        openAutoRun(context,pkgName,true);
        openScreenOffClean(context,pkgName,true);
    }

    public static boolean openAutoRun(Context context, final String packageName, final boolean open) {
        if(!isQihooSystem()){
            return false;
        }
        try {
            final Uri parse = Uri.parse("content://com.qiku.android.config/permission");
            final Bundle bundle = new Bundle();
            final ContentValues contentValues = new ContentValues();
            int actionValue = open ? 1 : 3;
            contentValues.put("action", String.valueOf(actionValue));
            bundle.putParcelable("values", (Parcelable) contentValues);
            bundle.putString("groupIds", String.valueOf(21));
            final int count = context.getContentResolver().call(parse, "updatePermission", packageName, bundle).getInt("effectrow");
            boolean result = count > 0 ? true:false;
            return result;
        } catch (Exception ex) {
            return false;
        }
    }

    public static void openScreenOffClean(Context context , final String s, final boolean open) {
        if(!isQihooSystem()){
            return;
        }
        try {
            int n = 1;
            final Uri parse = Uri.parse("content://com.qiku.android.config/user/screenOffClean");
            final ContentValues contentValues = new ContentValues();
            contentValues.put("c_pkg_name", s);

            if (!open) {
                n = 0;
            }
            contentValues.put("c_value", n);
            context.getContentResolver().update(parse, contentValues, "c_pkg_name=?", new String[]{s});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isQihooSystem(){
        String versionName = AndroidOSUtils.getSystemProperty("ro.qiku.version.software");
        return !TextUtils.isEmpty(versionName);
    }
}
