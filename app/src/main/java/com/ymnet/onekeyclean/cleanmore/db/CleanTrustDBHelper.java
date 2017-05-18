package com.ymnet.onekeyclean.cleanmore.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2014/12/19.
 */
public class CleanTrustDBHelper extends SQLiteOpenHelper {
    public static final String DBNAME="trust.db";//数据库名称
    public static final String TABLE_NAME="clean_filter";//数据库表名
    public static final String ID = "_id";

    public static final String FILE_NAME = "file_name";

    public static final String FILE_PATH = "file_path";

    public static final String FILE_SIZE = "file_size";

    public static final String FILE_TYPE = "file_type";// 0 缓存忽略, 1残留文件忽略  2 无用安装包
    /**
     * 创建表的sql语句
     */
    private static final String SQL = "create table if not exists "
            + TABLE_NAME
            + "("
            + ID + " integer primary key autoincrement, "
            + FILE_NAME + " nvarchar(200), "
            + FILE_PATH + " nvarchar(500) UNIQUE, "
            + FILE_SIZE + " long, "
            + FILE_TYPE + " int, " + "data1 nvarchar(50)"
            + ");";

    public CleanTrustDBHelper(Context context) {
        super(context, DBNAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists "+DBNAME);
        onCreate(db);
    }


}
