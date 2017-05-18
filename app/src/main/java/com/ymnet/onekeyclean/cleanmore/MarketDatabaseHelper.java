package com.ymnet.onekeyclean.cleanmore;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.text.TextUtils;

import com.ymnet.onekeyclean.cleanmore.utils.C;


/**
 * Created with IntelliJ IDEA.
 * Date: 9/23/15
 * Author: zhangcm
 */
public abstract class MarketDatabaseHelper extends SQLiteOpenHelper {

    /**
     * no such table
     */
    public static final String CODE_1 = "code 1";

    /**
     * Could not open database
     */
    public static final String CODE_14 = "code 14";

    /**
     * disk I/O error
     */
    public static final String CODE_3850 = "code 3850";

    private String mName;

    public MarketDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mName = name;
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        SQLiteDatabase db = null;
        int count = 0;
        while (db == null && count++ < 3) {
            try {
                db = super.getWritableDatabase();
            } catch (SQLiteException e) {
                String message = e.getMessage();
                if (!TextUtils.isEmpty(message)) {
                    if (message.contains(CODE_1)) {
                        C.get().deleteDatabase(mName);
                        int mode = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN ? Context.MODE_ENABLE_WRITE_AHEAD_LOGGING : Context.MODE_WORLD_WRITEABLE;
                        db = C.get().openOrCreateDatabase(mName, mode, null);
                        onCreate(db);
                    } else if (message.contains(CODE_14) || message.contains(CODE_3850)) {
                        Process p2 = null;
                        try {
                            String[] args2 = {"chmod", "660", C.get().getDatabasePath(mName).toString()};
                            p2 = Runtime.getRuntime().exec(args2);
                            p2.waitFor();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        } finally {
                            if (p2 != null) {
                                p2.destroy();
                            }
                        }
                    }
                }
                e.printStackTrace();
            }
        }
        return db;
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        SQLiteDatabase db = null;
        int count = 0;
        while (db == null && count++ < 3) {
            try {
                db = super.getReadableDatabase();
            } catch (SQLiteException e) {
                String message = e.getMessage();
                if (!TextUtils.isEmpty(message)) {
                    if (message.contains(CODE_1)) {
                        C.get().deleteDatabase(mName);
                        int mode = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN ? Context.MODE_ENABLE_WRITE_AHEAD_LOGGING : Context.MODE_WORLD_WRITEABLE;
                        db = C.get().openOrCreateDatabase(mName, mode, null);
                        onCreate(db);
                    } else if (message.contains(CODE_14) || message.contains(CODE_3850)) {
                        Process p2 = null;
                        try {
                            String[] args2 = {"chmod", "660", C.get().getDatabasePath(mName).toString()};
                            p2 = Runtime.getRuntime().exec(args2);
                            p2.waitFor();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        } finally {
                            if (p2 != null) {
                                p2.destroy();
                            }
                        }
                    }
                }
                e.printStackTrace();
            }
        }
        return db;
    }
}
