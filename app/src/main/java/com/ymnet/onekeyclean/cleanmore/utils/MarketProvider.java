package com.ymnet.onekeyclean.cleanmore.utils;/*
 * Copyright (C) 2010 mAPPn.Inc
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
 *//*

package com.example.baidumapsevice.utils;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;

import com.example.baidumapsevice.MarketDatabaseHelper;
import com.example.baidumapsevice.download.Downloads;

import java.util.ArrayList;
import java.util.List;

*/
/**
 * Market Content Provider
 * 
 * @author andrew
 * @date 2011-4-25
 * @since Version 0.7.0
 *//*

public class MarketProvider extends ContentProvider
{

    private static final String TAG = MarketProvider.class.getSimpleName();
    */
/** The database that lies underneath this content provider *//*

    private SQLiteOpenHelper mOpenHelper = null;
    
    */
/** Database filename *//*

    private static final String DB_NAME = "market.db";

    */
/** Current database version *//*

    //1.3加入新字段
    private static final int DB_VERSION = 10;

    */
/** Name of search history table in the database *//*

    public static final String TABLE_SEARCH_HISTORY = "search_history";
    
    */
/** Name of update table in the database *//*

    public static final String TABLE_UPDATES = "updates";
    
    public static final String TABLE_REPORTS = "reports";

    public static final String TABLE_LM_UPDATE_REPORTS = "lm_update_reports";

    public static final String TABLE_WIFI_RECEIVE = "wifireceive";
    
    */
/** MIME type for the entire list *//*

    private static final String LIST_TYPE = "vnd.android.cursor.dir/";
    
    */
/** MIME type for an individual item *//*

    private static final String ITEM_TYPE = "vnd.android.cursor.item/";
    
    */
/** URI matcher used to recognize URIs sent by applications *//*

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    
    */
/** URI matcher constant for the URI of an search_history *//*

    private static final int SEARCH_HISTORY = 1;
    
    private static final int SEARCH_HISTORY_ID = 2;
    
    private static final int UPDATE_PRODUCT = 4;
    
    private static final int REPORTS = 5;

    private static final int LM_UPDATE_REPORTS = 9;

    private static final int REPORTS_ID = 6;

    private static final int LM_UPDATE_REPORTS_ID = 10;

    private static final int WIFI_RECEIVE = 7;
    
    private static final int WIFI_RECEIVE_ID = 8;

    public static final String AUTHORITY = "2345market";

    static
    {
        sURIMatcher.addURI(AUTHORITY, TABLE_SEARCH_HISTORY, SEARCH_HISTORY);
        sURIMatcher.addURI(AUTHORITY, TABLE_SEARCH_HISTORY + "/#", SEARCH_HISTORY_ID);
        sURIMatcher.addURI(AUTHORITY, TABLE_UPDATES, UPDATE_PRODUCT);
        sURIMatcher.addURI(AUTHORITY, TABLE_REPORTS, REPORTS);
        sURIMatcher.addURI(AUTHORITY, TABLE_REPORTS + "/#", REPORTS_ID);
        sURIMatcher.addURI(AUTHORITY, TABLE_LM_UPDATE_REPORTS, LM_UPDATE_REPORTS);
        sURIMatcher.addURI(AUTHORITY, TABLE_LM_UPDATE_REPORTS + "/#", LM_UPDATE_REPORTS_ID);
        sURIMatcher.addURI(AUTHORITY, TABLE_WIFI_RECEIVE, WIFI_RECEIVE);
        sURIMatcher.addURI(AUTHORITY, TABLE_WIFI_RECEIVE + "/#", WIFI_RECEIVE_ID);
    }
    
    */
/**
     * The content:// URI to access search history
     *//*

    public static final Uri SEARCH_CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/" + TABLE_SEARCH_HISTORY);
    
    */
/**
     * The content:// URI to access update products
     *//*

    public static final Uri UPDATE_CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/"  + TABLE_UPDATES);
    
    public static final Uri REPORT_URL = Uri.parse("content://"+AUTHORITY+"/"  + TABLE_REPORTS);

    public static final Uri REPORT_LM_UPDATE_URL = Uri.parse("content://"+AUTHORITY+"/"  + TABLE_LM_UPDATE_REPORTS);

    public static final Uri RECEIVER_URL = Uri.parse("content://"+AUTHORITY+"/"  + TABLE_WIFI_RECEIVE);
    
    */
/** Table ID *//*

    public static final String COLUMN_ID = "_id";
    
    */
/** 搜索关键词 *//*

    public static final String COLUMN_SEARCH_KEY_WORD = "keyword";
    
    */
/** 产品表 *//*

    public static final String COLUMN_sid = "sid";
    
    public static final String COLUMN_category_id = "category_id";
    
    public static final String COLUMN_category_title = "category_title";
    
    public static final String COLUMN_title = "title";
    
    public static final String COLUMN_version = "version";
    
    public static final String COLUMN_packageName = "packageName";
    
    public static final String COLUMN_versionCode = "versionCode";
    
    public static final String COLUMN_totalDowns = "totalDowns";
    
    public static final String COLUMN_fileLength = "fileLength";
    
    public static final String COLUMN_publicDate = "publicDate";
    
    public static final String COLUMN_mark = "mark";
    
    public static final String COLUMN_summary = "summary";
    
    public static final String COLUMN_url = "url";
    
    public static final String COLUMN_icon = "icon";
    
    public static final String COLUMN_screenslot = "screenslot";
    
    public static final String COLUMN_oneword = "oneword";

    public static final String COLUMN_slabel = "slabel";

    public static final String COLUMN_ad = "ad";
    
    public static final String COLUMN_charge = "charge";
    
    public static final String COLUMN_safe = "safe";
    
    public static final String COLUMN_tagName = "tagName";

    public static final String COLUMN_sysIng = "sysIng";

    public static final String COLUMN_minSDK = "minSDK";

    public static final String COLUMN_certMd5="certMd5";

      public static final String COLUMN_INSTALLED = "installed";

      public static final int INSTALLED_SUCCESS = 1;
      public static final int INSTALLED_FALSE = 2;

    */
/** 联盟升级表 *//*

    public static final String COLUMN_UPDATE_CONTENT = "update_content";







    private static class SqlSelection
    {
        public StringBuilder mWhereClause = new StringBuilder();
        
        public List<String> mParameters = new ArrayList<String>();
        
        public <T> void appendClause(String newClause, final T... parameters)
        {
            if (TextUtils.isEmpty(newClause))
            {
                return;
            }
            if (mWhereClause.length() != 0)
            {
                mWhereClause.append(" AND ");
            }
            mWhereClause.append("(");
            mWhereClause.append(newClause);
            mWhereClause.append(")");
            if (parameters != null)
            {
                for (Object parameter : parameters)
                {
                    mParameters.add(parameter.toString());
                }
            }
        }
        
        public String getSelection()
        {
            return mWhereClause.toString();
        }
        
        public String[] getParameters()
        {
            String[] array = new String[mParameters.size()];
            return mParameters.toArray(array);
        }
    }
    
    */
/**
     * Creates and updated database on demand when opening it. Helper class to
     * create database the first time the provider is initialized and upgrade it
     * when a new version of the provider needs an updated version of the
     * database.
     *//*

    private final class DatabaseHelper extends MarketDatabaseHelper
    {
        public DatabaseHelper(final Context context)
        {
            super(context, DB_NAME, null, DB_VERSION);
        }
        
        @Override
        public void onCreate(SQLiteDatabase db)
        {
            Utils.D("create the new database...");
            onUpgrade(db, 0, DB_VERSION);
        }
        
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            Utils.D("update the database...");
            createSearchHistoryTable(db);
            createUpdateTable(db);
        }
        
        */
/*
         * 创建搜索历史表
         *//*

        private void createSearchHistoryTable(SQLiteDatabase db)
        {
            try
            {
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEARCH_HISTORY);
                db.execSQL("CREATE TABLE " + TABLE_SEARCH_HISTORY + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_SEARCH_KEY_WORD + " TEXT);");
            }
            catch (SQLException ex)
            {
                Utils.D("couldn't create " + TABLE_SEARCH_HISTORY + " table in market database");
                throw ex;
            }
        }
        
        */
/*
         * 创建更新产品表
         *//*

        private void createUpdateTable(SQLiteDatabase db)
        {
            try
            {
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_UPDATES);
                db.execSQL("CREATE TABLE " + TABLE_UPDATES + "(" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_sid + " TEXT, " +
                        COLUMN_category_id + " TEXT, " +
                        COLUMN_category_title + " TEXT, " +
                        COLUMN_title + " TEXT, " +
                        COLUMN_version + " TEXT, " +
                        COLUMN_packageName + " TEXT, " +
                        COLUMN_versionCode + " TEXT, " +
                        COLUMN_totalDowns + " TEXT, " +
                        COLUMN_fileLength + " TEXT, " +
                        COLUMN_publicDate + " TEXT, " +
                        COLUMN_mark + " TEXT, " +
                        COLUMN_summary + " TEXT, " +
                        COLUMN_url + " TEXT, " +
                        COLUMN_icon + " TEXT, " +
                        COLUMN_oneword + " TEXT, " +
                        COLUMN_ad + " INTEGER, " +
                        COLUMN_charge + " INTEGER, " +
                        COLUMN_safe + " INTEGER, " +
                        COLUMN_tagName + " TEXT, " +
                        COLUMN_certMd5 +" TEXT, " +
                        COLUMN_sysIng +" TEXT, " +
                        COLUMN_minSDK + " INTEGER, " +
                        COLUMN_screenslot + " TEXT);");


                db.execSQL("DROP TABLE IF EXISTS " + TABLE_LM_UPDATE_REPORTS);
                db.execSQL("CREATE TABLE " + TABLE_LM_UPDATE_REPORTS + "(" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_UPDATE_CONTENT + " TEXT);");


                db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPORTS);
                db.execSQL("CREATE TABLE " + TABLE_REPORTS + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "data1" + " TEXT, " +
                        Downloads.Impl.COLUMN_SOURCEFROM + " INTEGER, " +
                        COLUMN_INSTALLED + " INTEGER);");
                
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_WIFI_RECEIVE);
                db.execSQL("CREATE TABLE " + TABLE_WIFI_RECEIVE + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + "path" + " TEXT, " + "filename" + " TEXT, " + "minitype" + " TEXT, " + "size"
                        + " INTEGER, " + "modify" + " INTEGER, " + "data4" + " TEXT, " + "data5" + " TEXT, " + "data6" + " TEXT, " + "data7" + " TEXT, " + "data8" + " TEXT, " + "data9" + " TEXT, "
                        + "data10" + " TEXT);");
            }
            catch (SQLException ex)
            {
                Utils.D("couldn't create " + TABLE_UPDATES + " table in market database");
                throw ex;
            }
        }
        
    }
    
    */
/* (non-Javadoc)
     * @see android.content.ContentProvider#onCreate()
     *//*

    @Override
    public boolean onCreate()
    {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }
    
    */
/* (non-Javadoc)
     * @see android.content.ContentProvider#getType(android.net.Uri)
     *//*

    @Override
    public String getType(Uri uri)
    {
        
        int match = sURIMatcher.match(uri);
        switch (match)
        {
            case SEARCH_HISTORY:
                return LIST_TYPE + TABLE_SEARCH_HISTORY;
                
            case SEARCH_HISTORY_ID:
                return ITEM_TYPE + TABLE_SEARCH_HISTORY;
                
            case UPDATE_PRODUCT:
                return LIST_TYPE + TABLE_UPDATES;
                
            case REPORTS:
                return LIST_TYPE + TABLE_REPORTS;
                
            case REPORTS_ID:
                return LIST_TYPE + TABLE_REPORTS;
                
            case WIFI_RECEIVE:
                
                return LIST_TYPE + TABLE_WIFI_RECEIVE;
                
            case WIFI_RECEIVE_ID:
                
                return LIST_TYPE + TABLE_WIFI_RECEIVE;
            case LM_UPDATE_REPORTS:

                return LIST_TYPE + TABLE_LM_UPDATE_REPORTS;
            case LM_UPDATE_REPORTS_ID:

                return ITEM_TYPE + TABLE_LM_UPDATE_REPORTS;

            default:
                break;
        }
        return null;
    }
    
    */
/* (non-Javadoc)
     * @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)
     *//*

    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        
        int match = sURIMatcher.match(uri);
        
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final String table = getTableFromUri(uri);
        long rowID = db.insert(table, null, values);
        if (rowID == -1)
        {
            Utils.D("couldn't insert into " + table + " database");
            return null;
        }

        Uri inserResult = ContentUris.withAppendedId(uri, rowID);
        notifyContentChanged(uri, match);
        return inserResult;
    }
    
    */
/* (non-Javadoc)
     * @see android.content.ContentProvider#delete(android.net.Uri, java.lang.String, java.lang.String[])
     *//*

    @Override
    public int delete(Uri uri, String where, String[] whereArgs)
    {
        int match = sURIMatcher.match(uri);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final String table = getTableFromUri(uri);
        SqlSelection selection = getWhereClause(uri, where, whereArgs);
        int count = db.delete(table, selection.getSelection(), selection.getParameters());
        
        if (count == 0)
        {
            Utils.D("couldn't delete URI " + uri);
            return count;
        }
        
        notifyContentChanged(uri, match);
        return count;
    }
    
    */
/* (non-Javadoc)
     * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
     *//*

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        
        int match = sURIMatcher.match(uri);
        if (match == -1)
        {
            Utils.D("updating unknown URI: " + uri);
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        
        final String table = getTableFromUri(uri);
        return db.update(table, values, selection, selectionArgs);
    }
    
    */
/* (non-Javadoc)
     * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
     *//*

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        
        int match = sURIMatcher.match(uri);
        if (match == -1)
        {
            Utils.D("querying unknown URI: " + uri);
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        
        SqlSelection fullSelection = getWhereClause(uri, selection, selectionArgs);
        
        logVerboseQueryInfo(projection, selection, selectionArgs, sortOrder, db);
        
        final String table = getTableFromUri(uri);
        Cursor ret = db.query(table, projection, fullSelection.getSelection(), fullSelection.getParameters(), null, null, sortOrder);
        
        if (ret == null)
        {
            Utils.D("query failed in market database");
        }
        return ret;
    }
    
    */
/**
     * Notify of a change through both URIs
     *//*

    private void notifyContentChanged(final Uri uri, int uriMatch)
    {
        getContext().getContentResolver().notifyChange(uri, null);
    }
    
    */
/**
     * 从URI中获取表名
     * 
     * @param uri
     *            目标Uri
     * @return 操作目标的表名
     *//*

    private static String getTableFromUri(final Uri uri)
    {
        return uri.getPathSegments().get(0);
    }
    
    */
/**
     * 获取SQL条件的工具方法
     * 
     * @param uri
     *            Content URI
     * @param where
     *            条件
     * @param whereArgs
     *            参数
     * @return 合成的SqlSelection对象
     *//*

    private static SqlSelection getWhereClause(final Uri uri, final String where, final String[] whereArgs)
    {
        SqlSelection selection = new SqlSelection();
        selection.appendClause(where, whereArgs);
        return selection;
    }
    
    */
/**
     * 打印 【查询SQL】 详细信息
     *//*

    private static void logVerboseQueryInfo(String[] projection, final String selection, final String[] selectionArgs, final String sort, SQLiteDatabase db)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("starting query, database is ");
        if (db != null)
        {
            sb.append("not ");
        }
        sb.append("null; ");
        if (projection == null)
        {
            sb.append("projection is null; ");
        }
        else if (projection.length == 0)
        {
            sb.append("projection is empty; ");
        }
        else
        {
            for (int i = 0; i < projection.length; ++i)
            {
                sb.append("projection[");
                sb.append(i);
                sb.append("] is ");
                sb.append(projection[i]);
                sb.append("; ");
            }
        }
        sb.append("selection is ");
        sb.append(selection);
        sb.append("; ");
        if (selectionArgs == null)
        {
            sb.append("selectionArgs is null; ");
        }
        else if (selectionArgs.length == 0)
        {
            sb.append("selectionArgs is empty; ");
        }
        else
        {
            for (int i = 0; i < selectionArgs.length; ++i)
            {
                sb.append("selectionArgs[");
                sb.append(i);
                sb.append("] is ");
                sb.append(selectionArgs[i]);
                sb.append("; ");
            }
        }
        sb.append("sort is ");
        sb.append(sort);
        sb.append(".");
        Utils.D(sb.toString());
    }
    
}
*/
