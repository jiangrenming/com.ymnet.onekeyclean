package com.ymnet.onekeyclean.cleanmore.download;/*


package com.example.baidumapsevice.download;


import android.app.DownloadManager;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Binder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.example.baidumapsevice.MarketDatabaseHelper;
import com.example.baidumapsevice.datacenter.DataCenterObserver;

import java.util.ArrayList;
import java.util.List;

*/
/**
 * Allows application to interact with the download manager.
 *//*

public final class DownloadProvider extends ContentProvider {

    private static final String TAG = DownloadProvider.class.getSimpleName();


    */
/**
     * Database filename
     *//*

    public static final String DB_NAME = "downloads.db";
    */
/**
     * Current database version
     *//*

    public static final int DB_VERSION = 120;
    */
/**
     * Name of table in the database
     *//*

    private static final String DB_TABLE = "downloads";

    public static final String AUTHORITIES = "market2345_downloads";

    public static final String URI_PATH = "all_downloads";

    public static final String INIT_DOWNLOADS_URI_PATH = "init_all_downloads";

    public static final String RESUME_DOWNLOADS_URI_PATH = "resume_all_downloads";

    public static final String CANCEL_DOWNLOADS_URI_PATH = "cancel_all_downloads";


    */
/**
     * MIME type for the entire download list
     *//*

    private static final String DOWNLOAD_LIST_TYPE = "vnd.android.cursor.dir/download";

    */
/**
     * URI matcher used to recognize URIs sent by applications
     *//*

    public static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    public static final int DOWNLOADS = 1;

    public static final int DOWNLOADS_ID = 2;

    public static final int INIT_ALL_DOWNLOADS = 3;

    public static final int CANCEL_DOWNLOADS = 4;

    public static final int CANCEL_DOWNLOADS_ID = 5;

    public static final int RESUME_ALL_DOWNLOADS = 6;

    static {
        sURIMatcher.addURI(AUTHORITIES, URI_PATH, DOWNLOADS);
        sURIMatcher.addURI(AUTHORITIES, URI_PATH + "/#", DOWNLOADS_ID);
        sURIMatcher.addURI(AUTHORITIES, INIT_DOWNLOADS_URI_PATH, INIT_ALL_DOWNLOADS);
        sURIMatcher.addURI(AUTHORITIES, CANCEL_DOWNLOADS_URI_PATH, CANCEL_DOWNLOADS);
        sURIMatcher.addURI(AUTHORITIES, CANCEL_DOWNLOADS_URI_PATH + "/#", CANCEL_DOWNLOADS_ID);
        sURIMatcher.addURI(AUTHORITIES, RESUME_DOWNLOADS_URI_PATH, RESUME_ALL_DOWNLOADS);
    }

    */
/**
     * The database that lies underneath this content provider
     *//*

    private SQLiteOpenHelper mOpenHelper = null;


    */
/**
     * This class encapsulates a SQL where clause and its parameters.  It makes it possible for
     * shared methods (like {@link DownloadProvider#getWhereClause(Uri, String, String[], int)})
     * to return both pieces of information, and provides some utility logic to ease piece-by-piece
     * construction of selections.
     *//*

    private static class SqlSelection {
        public final StringBuilder mWhereClause = new StringBuilder();
        public final List<String> mParameters = new ArrayList<>();

        public <T> void appendClause(String newClause, final T... parameters) {
            if (newClause == null || TextUtils.isEmpty(newClause)) {
                return;
            }
            if (mWhereClause.length() != 0) {
                mWhereClause.append(" AND ");
            }
            mWhereClause.append("(");
            mWhereClause.append(newClause);
            mWhereClause.append(")");
            if (parameters != null) {
                for (Object parameter : parameters) {
                    mParameters.add(String.valueOf(parameter));
                }
            }
        }

        public String getSelection() {
            return mWhereClause.toString();
        }

        public String[] getParameters() {
            String[] array = new String[mParameters.size()];
            return mParameters.toArray(array);
        }
    }

    */
/**
     * Creates and updated database on demand when opening it.
     * Helper class to create database the first time the provider is
     * initialized and upgrade it when a new version of the provider needs
     * an updated version of the database.
     *//*

    public static final class DatabaseHelper extends MarketDatabaseHelper {
        public DatabaseHelper(final Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        */
/**
         * Creates database the first time we try to open it.
         *//*

        @Override
        public void onCreate(final SQLiteDatabase db) {
            onUpgrade(db, 0, DB_VERSION);
        }

        */
/**
         * Updates the database format when a content provider is used
         * with a database that was created with a different format.
         * <p/>
         * Note: to support downgrades, creating a table should always drop it first if it already
         * exists.
         *//*

        @Override
        public void onUpgrade(final SQLiteDatabase db, int oldV, final int newV) {
            createDownloadsTable(db);
        }


        */
/**
         * Add a column to a table using ALTER TABLE.
         *
         * @param dbTable          name of the table
         * @param columnName       name of the column to add
         * @param columnDefinition SQL for the column definition
         *//*

        private void addColumn(SQLiteDatabase db, String dbTable, String columnName,
                               String columnDefinition) {
            db.execSQL("ALTER TABLE " + dbTable + " ADD COLUMN " + columnName + " "
                    + columnDefinition);
        }

        */
/**
         * Creates the table that'll hold the download information.
         *//*

        private void createDownloadsTable(SQLiteDatabase db) {
            try {
                db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
                db.execSQL("CREATE TABLE " + DB_TABLE + "(" +
                        Downloads.Impl._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        Downloads.Impl.COLUMN_URI + " TEXT, " +
                        Downloads.Impl.COLUMN_APP_DATA + " TEXT, " +
                        Downloads.Impl.COLUMN_DELETE_FILE + " INTEGER, " +
                        Downloads.Impl.COLUMN_DOWN_PARTIAL + " INTEGER, " +
                        Downloads.Impl.COLUMN_TARGET_SIZE + " INTEGER, " +

                        Downloads.Impl.COLUMN_FILE_NAME + " TEXT, " +
                        Downloads.Impl.COLUMN_MIME_TYPE + " TEXT, " +
                        Downloads.Impl.COLUMN_NUMFAILED + " INTEGER, " +
                        Downloads.Impl.COLUMN_SIGNATUREERROR + " INTEGER, " +

                        Downloads.Impl.COLUMN_IS_UPDATE + " INTEGER, " +
                        Downloads.Impl.COLUMN_STATUS + " INTEGER, " +
                        Downloads.Impl.COLUMN_ALLOWED_NETWORK_TYPES + " INTEGER, " +
                        Downloads.Impl.COLUMN_CERTMD5 + " TEXT, " +
                        Downloads.Impl.COLUMN_LAST_MODIFIED + " INTEGER, " +
                        Downloads.Impl.COLUMN_USER_AGENT + " TEXT, " +
                        Downloads.Impl.COLUMN_REFERER + " TEXT, " +
                        Downloads.Impl.COLUMN_TOTAL_BYTES + " INTEGER, " +
                        Downloads.Impl.COLUMN_CURRENT_BYTES + " INTEGER, " +
                        Downloads.Impl.COLUMN_ETAG + " TEXT, " +

                        Downloads.Impl.COLUMN_TITLE + " TEXT, " +
                        Downloads.Impl.COLUMN_DESCRIPTION + " TEXT, " +
                        Downloads.Impl.COLUMN_PACKAGENAME + " TEXT, " +
                        Downloads.Impl.COLUMN_VERSIONNAME + " TEXT, " +
                        Downloads.Impl.COLUMN_SID + " TEXT, " +
                        Downloads.Impl.COLUMN_ICON_URL + " TEXT, " +
                        Downloads.Impl.COLUMN_SPEED + " INTEGER, " +
                        Downloads.Impl.COLUMN_PATCH_URL + " TEXT, " +
                        Downloads.Impl.COLUMN_PATCH_SIZE + " INTEGER, " +
                        Downloads.Impl.COLUMN_LOWMD5 + " TEXT, " +


                        Downloads.Impl.COLUMN_VERSIONCODE + " INTEGER, " +
                        Downloads.Impl.COLUMN_FIRSTMD5 + " TEXT, " +
                        Downloads.Impl.COLUMN_FIRSTMD5ERROR_LENGTH + " INTEGER, " +
                        Downloads.Impl.COLUMN_SECONDMD5 + " TEXT, " +
                        Downloads.Impl.COLUMN_SERVERMD5 + " TEXT, " +
                        Downloads.Impl.COLUMN_PLATFORM + " TEXT, " +
                        Downloads.Impl.COLUMN_SOURCEFROM + " INTEGER, " +
                        Downloads.Impl.COLUMN_MIN_SDK + " INTEGER, " +

                        Downloads.Impl.COLUMN_START_INSTALL + " INTEGER);");
            } catch (SQLException ex) {
//                Log.e(Constants.TAG, "couldn't create table in downloads database");
                throw ex;
            }
        }
    }

    */
/**
     * Initializes the content provider when it is created.
     *//*

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    */
/**
     * Returns the content-provider-style MIME types of the various
     * types accessible through this content provider.
     *//*

    @Override
    public String getType(final Uri uri) {

        int match = sURIMatcher.match(uri);
        switch (match) {
            case DOWNLOADS:
            case INIT_ALL_DOWNLOADS:
            case CANCEL_DOWNLOADS:
            case RESUME_ALL_DOWNLOADS:
                return DOWNLOAD_LIST_TYPE;
            case DOWNLOADS_ID:
            case CANCEL_DOWNLOADS_ID:
            default: {
                if (Constants.LOGV) {
                    Log.v(Constants.TAG, "calling getType on an unknown URI: " + uri);
                }
                throw new IllegalArgumentException("Unknown URI: " + uri);
            }
        }
    }

    */
/**
     * Inserts a row in the database
     *//*

    @Override
    public Uri insert(final Uri uri, final ContentValues values) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        // note we disallow inserting into DOWNLOADS
        int match = sURIMatcher.match(uri);
        if (match != DOWNLOADS) {
            Log.d(Constants.TAG, "calling insert on an unknown/invalid URI: " + uri);
            throw new IllegalArgumentException("Unknown/Invalid URI " + uri);
        }


        // 删除相同下载地址的数据
        String downloadUri = values.getAsString(Downloads.Impl.COLUMN_URI);
        if (!TextUtils.isEmpty(downloadUri)) {
            String selection = Downloads.Impl.COLUMN_URI + " = ?";
            String[] selectionArgs = {downloadUri};
            final Cursor cursor = query(Downloads.Impl.DOWNLOADS_CONTENT_URI, new String[]{Downloads.Impl._ID}, selection, selectionArgs, null);
            try {
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        DownloadManager.getInstance(getContext()).deleteDownload(true, cursor.getInt(0));
                    }
                }
            } finally {
                if (cursor != null) cursor.close();
            }
        }


        // copy some of the input values as it
        ContentValues filteredValues = new ContentValues();
        copyString(Downloads.Impl.COLUMN_URI, values, filteredValues);
        copyString(Downloads.Impl.COLUMN_APP_DATA, values, filteredValues);
        copyString(Downloads.Impl.COLUMN_MIME_TYPE, values, filteredValues);


        // copy some more columns as is
        copyString(Downloads.Impl.COLUMN_USER_AGENT, values, filteredValues);
        copyString(Downloads.Impl.COLUMN_REFERER, values, filteredValues);
        copyString(Downloads.Impl.COLUMN_PACKAGENAME, values, filteredValues);
        copyString(Downloads.Impl.COLUMN_VERSIONNAME, values, filteredValues);
        copyString(Downloads.Impl.COLUMN_SID, values, filteredValues);
        copyString(Downloads.Impl.COLUMN_ICON_URL, values, filteredValues);
        copyString(Downloads.Impl.COLUMN_DESCRIPTION, values, filteredValues);

        String packageName = values.getAsString(Downloads.Impl.COLUMN_PACKAGENAME);
        int downType = DataCenterObserver.get(getContext()).isInstalled(packageName) ?
                Downloads.Impl.DOWN_TYPE_UPDATE : Downloads.Impl.DOWN_TYPE_DOWN;

        filteredValues.put(Downloads.Impl.COLUMN_IS_UPDATE, downType);


        copyInteger(Downloads.Impl.COLUMN_VERSIONCODE, values, filteredValues);
        filteredValues.put(Downloads.Impl.COLUMN_CURRENT_BYTES, 0);
        copyLong(Downloads.Impl.COLUMN_TARGET_SIZE, values, filteredValues);
        copyLong(Downloads.Impl.COLUMN_TOTAL_BYTES, values, filteredValues);

        copyString(Downloads.Impl.COLUMN_FIRSTMD5, values, filteredValues);
        filteredValues.put(Downloads.Impl.COLUMN_FIRSTMD5ERROR_LENGTH, 0);
        filteredValues.put(Downloads.Impl.COLUMN_NUMFAILED, 0);
        filteredValues.put(Downloads.Impl.COLUMN_SIGNATUREERROR, Downloads.Impl.UNSIGNATUREERROR);
        filteredValues.put(Downloads.Impl.COLUMN_LAST_MODIFIED, System.currentTimeMillis());
        copyString(Downloads.Impl.COLUMN_CERTMD5, values, filteredValues);
        copyString(Downloads.Impl.COLUMN_SECONDMD5, values, filteredValues);
        copyString(Downloads.Impl.COLUMN_SERVERMD5, values, filteredValues);
        copyString(Downloads.Impl.COLUMN_PLATFORM, values, filteredValues);
        copyString(Downloads.Impl.COLUMN_PATCH_URL, values, filteredValues);
        copyString(Downloads.Impl.COLUMN_PATCH_SIZE, values, filteredValues);
        copyString(Downloads.Impl.COLUMN_LOWMD5, values, filteredValues);


        copyInteger(Downloads.Impl.COLUMN_SOURCEFROM, values, filteredValues);
        copyInteger(Downloads.Impl.COLUMN_START_INSTALL, values, filteredValues);
        copyInteger(Downloads.Impl.COLUMN_MIN_SDK, values, filteredValues);


        // copy some more columns as is
        copyStringWithDefault(Downloads.Impl.COLUMN_TITLE, values, filteredValues, "");

        // public api requests and networktypes/roaming columns
        copyInteger(Downloads.Impl.COLUMN_ALLOWED_NETWORK_TYPES, values, filteredValues);
        copyInteger(Downloads.Impl.COLUMN_STATUS, values, filteredValues);
        copyInteger(Downloads.Impl.COLUMN_DOWN_PARTIAL, values, filteredValues);

        long rowID = db.insert(DB_TABLE, null, filteredValues);
        if (rowID == -1) {
            Log.d(Constants.TAG, "couldn't insert into downloads database");
            return null;
        }

        final Context context = getContext();
        if (context != null) {
            Intent intent = new Intent(context, DownloadService.class);
            intent.putExtra(DownloadService.DOWNLOAD_URI_KEY, ContentUris.withAppendedId(uri, rowID));
            context.startService(intent);
        }

        return ContentUris.withAppendedId(Downloads.Impl.DOWNLOADS_CONTENT_URI, rowID);
    }


    private String getDownloadIdFromUri(final Uri uri) {
        return uri.getPathSegments().get(1);
    }


    */
/**
     * Starts a database query
     *//*

    @Override
    public Cursor query(final Uri uri, String[] projection,
                        final String selection, final String[] selectionArgs,
                        final String sort) {

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        int match = sURIMatcher.match(uri);

        if (match == UriMatcher.NO_MATCH) {
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SqlSelection fullSelection = getWhereClause(uri, selection, selectionArgs, match);

        Cursor ret = null;
        if(db != null) {
            ret = db.query(DB_TABLE, projection, fullSelection.getSelection(),
                    fullSelection.getParameters(), null, null, sort);
        }

        if (ret != null) {
            if (getContext() != null){
                ret.setNotificationUri(getContext().getContentResolver(), uri);
            }
            if (Constants.LOGVV) {
                Log.v(Constants.TAG,
                        "created cursor " + ret + " on behalf of " + Binder.getCallingPid());
            }
        } else {
            if (Constants.LOGV) {
                Log.v(Constants.TAG, "query failed in downloads database");
            }
        }

        return ret;
    }

    private void logVerboseQueryInfo(String[] projection, final String selection,
                                     final String[] selectionArgs, final String sort, SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder();
        sb.append("starting query, database is ");
        if (db != null) {
            sb.append("not ");
        }
        sb.append("null; ");
        if (projection == null) {
            sb.append("projection is null; ");
        } else if (projection.length == 0) {
            sb.append("projection is empty; ");
        } else {
            for (int i = 0; i < projection.length; ++i) {
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
        if (selectionArgs == null) {
            sb.append("selectionArgs is null; ");
        } else if (selectionArgs.length == 0) {
            sb.append("selectionArgs is empty; ");
        } else {
            for (int i = 0; i < selectionArgs.length; ++i) {
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
        Log.v(Constants.TAG, sb.toString());
    }

    */
/**
     * Updates a row in the database
     *//*

    @Override
    public int update(final Uri uri, final ContentValues values,
                      final String where, final String[] whereArgs) {

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int count;

        int type = -1;

        boolean startService = false;

        Integer status = values.getAsInteger(Downloads.Impl.COLUMN_STATUS);
        if (status != null) {
            startService = true;
            switch (status) {
                case Downloads.Impl.STATUS_DOWNLOAD_SUCCESS:
                    type = DownloadLog.DOWNLOAD_SUCCESS;
                    values.put(Downloads.Impl.COLUMN_LAST_MODIFIED, System.currentTimeMillis());
                    break;
                case Downloads.Impl.STATUS_INSTALLED:
                    type = DownloadLog.INSTALL_SUCCESS;
                    values.put(Downloads.Impl.COLUMN_LAST_MODIFIED, System.currentTimeMillis());
                    break;
            }
        }

        int match = sURIMatcher.match(uri);
        switch (match) {

            case DOWNLOADS_ID:
            case CANCEL_DOWNLOADS:
            case CANCEL_DOWNLOADS_ID:
                SqlSelection selection = getWhereClause(uri, where, whereArgs, match);
                count = db.update(DB_TABLE, values, selection.getSelection(), selection.getParameters());
                break;

            default:
                Log.d(Constants.TAG, "updating unknown/invalid URI: " + uri);
                throw new UnsupportedOperationException("Cannot update URI: " + uri);
        }


        if (startService || DownloadService.getNotifyUriHandler() == null) {
            Context context = getContext();
            if (context != null){
                Intent intent = new Intent(context, DownloadService.class);
                intent.putExtra(DownloadService.DOWNLOAD_URI_KEY, uri);
                context.startService(intent);
            }
        } else {
            Message msg = DownloadService.getNotifyUriHandler().obtainMessage((int) ContentUris.parseId(uri));
            msg.obj = uri;
            msg.sendToTarget();
        }

        if (type > 0) {
            DownloadLog.downloadEvent(type, ContentUris.parseId(uri));
        }
        return count;
    }


    private SqlSelection getWhereClause(final Uri uri, final String where, final String[] whereArgs,
                                        int uriMatch) {
        SqlSelection selection = new SqlSelection();
        selection.appendClause(where, whereArgs);
        if (uriMatch == DOWNLOADS_ID || uriMatch == CANCEL_DOWNLOADS_ID) {
            selection.appendClause(Downloads.Impl._ID + " = ?", getDownloadIdFromUri(uri));
        }

        return selection;
    }

    */
/**
     * Deletes a row in the database
     *//*

    @Override
    public int delete(final Uri uri, final String where,
                      final String[] whereArgs) {

//        Helpers.validateSelection(where, sAppReadableColumnsSet);

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        int match = sURIMatcher.match(uri);
        switch (match) {
            case DOWNLOADS:
            case DOWNLOADS_ID:
            case CANCEL_DOWNLOADS:
            case CANCEL_DOWNLOADS_ID:
                SqlSelection selection = getWhereClause(uri, where, whereArgs, match);

                count = db.delete(DB_TABLE, selection.getSelection(), selection.getParameters());
                break;

            default:
                Log.d(Constants.TAG, "deleting unknown/invalid URI: " + uri);
                throw new UnsupportedOperationException("Cannot delete URI: " + uri);
        }
        return count;
    }


    private static void copyLong(String key, ContentValues from, ContentValues to) {
        Long i = from.getAsLong(key);
        if (i != null) {
            to.put(key, i);
        }
    }


    private static void copyInteger(String key, ContentValues from, ContentValues to) {
        Integer i = from.getAsInteger(key);
        if (i != null) {
            to.put(key, i);
        }
    }

    private static void copyBoolean(String key, ContentValues from, ContentValues to) {
        Boolean b = from.getAsBoolean(key);
        if (b != null) {
            to.put(key, b);
        }
    }

    private static void copyString(String key, ContentValues from, ContentValues to) {
        String s = from.getAsString(key);
        if (s != null) {
            to.put(key, s);
        }
    }

    private static void copyStringWithDefault(String key, ContentValues from,
                                              ContentValues to, String defaultValue) {
        copyString(key, from, to);
        if (!to.containsKey(key)) {
            to.put(key, defaultValue);
        }
    }
}
*/
