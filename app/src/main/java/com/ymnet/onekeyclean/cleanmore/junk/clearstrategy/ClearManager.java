package com.ymnet.onekeyclean.cleanmore.junk.clearstrategy;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ymnet.onekeyclean.cleanmore.utils.C;
import com.ymnet.onekeyclean.cleanmore.utils.CleanSetSharedPreferences;
import com.ymnet.onekeyclean.cleanmore.utils.MD5;
import com.ymnet.onekeyclean.cleanmore.utils.SecurityAppInfo;
import com.ymnet.onekeyclean.cleanmore.utils.Util;
import com.ymnet.onekeyclean.cleanmore.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

/**
 * Created with IntelliJ IDEA.
 * User: yangning
 * Date: 1/6/15
 * To change this template use File | Settings | File Templates.
 */
public class ClearManager implements ClearDbDownloader.ClearDbDownloadListener {

    public static final String TAG = "ClearManager";
    private static Context mContext;
    public static final String DB_FILE_PATH = "filepath.db";
    private static String DB_FILE_BAK_PATH = "filepath.db.bak";
    private static int UPDATE_PERIOD_IN_DAYS = 7;//一周
    private  SQLiteDatabase sqLiteDatabase;

    public ClearManager(Context context) {
        mContext = context.getApplicationContext();
    }

    public static String getLatestClearDbPath() {
        String dbPath = mContext.getFilesDir().getPath();
        File file = new File(dbPath, DB_FILE_PATH);
        InputStream inputStream = null;
        try {
            inputStream = mContext.getAssets().open(DB_FILE_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!file.exists()){
            Util.copyAssetData(mContext, DB_FILE_PATH, dbPath);
        }else if (file.exists()) {
            String calculateMD5 = MD5.calculateMD5(file);
            if (calculateMD5 != null) {
                if(!calculateMD5.equals(CleanSetSharedPreferences.getCleanDbMd5(mContext))){
                    if(!calculateMD5.equals(MD5.calculateMD5(inputStream))){
                        Util.copyAssetData(mContext, DB_FILE_PATH, dbPath);
                    }
                }
            }
        }
        return file.getPath();
    }

    public  SQLiteDatabase openClearDatabase(){
        try{
            sqLiteDatabase = SQLiteDatabase.openDatabase(getLatestClearDbPath(), null, SQLiteDatabase.OPEN_READONLY);
        }catch (Exception e){
            e.printStackTrace();
        }
        return sqLiteDatabase;
    }
    private void closeClearDatabase(){
        if(sqLiteDatabase!=null&&sqLiteDatabase.isOpen()){
            try {
                sqLiteDatabase.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public void closeClearDatabase(SQLiteDatabase db){
        if(db!=null&&db.isOpen()){
            try {
                db.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static String getDecrytedKey(){
        Log.d(TAG,"getDecrytedKey");
        byte[] solidKey = SecurityAppInfo.getSolidKey(C.get());
        if(solidKey!=null){
            return new String(solidKey);
        }else return null;
    }
    public static String decrptString(String encryptedVal){
        try{
            return Utils.decryptCode(encryptedVal,getDecrytedKey());
        }catch (NullPointerException e){
            e.printStackTrace();
            return "";
        }

    }

    private static boolean isUserAllowed() {
        return CleanSetSharedPreferences.getLastSet(mContext, CleanSetSharedPreferences.CLEAN_DATABASE_UPDATE_SET, true);
    }

    private static boolean isDbFileExist() {
        File file = getDefaultDbFile();
        return file.exists();
    }

    private static boolean backupOldDb() {
        try {
            copy(getDefaultDbFile(), getDefaultDbBackupFile());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void copy(File src, File dst) throws Exception {
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inChannel.close();
        outChannel.close();
        inStream.close();
        outStream.close();
    }

    private static boolean verifyDbMd5(File resultFile, String md5) {
        return md5.equalsIgnoreCase(MD5.calculateMD5(resultFile));
    }

    private static boolean replaceOldDb(File resultFile) {
        File file = getDefaultDbFile();
        //replace 文件的过程中，check以下当前db是否正在打开，如果有，wait一下。
        if (file.exists()) {
            file.delete();
        }
        resultFile.renameTo(file);
        //如果update prefs失败,则下载的加密库中有is_encrypted冗余字段，用于读取数据时候判断是否需要解密。
        updatePrefs("");
        return true;
    }

    public static File getDefaultDbFile() {
        String dbPath = mContext.getFilesDir().getPath();
        return new File(dbPath, DB_FILE_PATH);
    }

    private static File getDefaultDbBackupFile() {
        String dbPath = mContext.getFilesDir().getPath();
        return new File(dbPath, DB_FILE_BAK_PATH);
    }

    private static void updatePrefs(String md5) {
        CleanSetSharedPreferences.setCleanDbLastUpdateTime(C.get());
        CleanSetSharedPreferences.setCleanDbMd5(C.get(), md5);
    }

    @Override
    public void finish(File resultFile, String md5) {
        if (verifyDbMd5(resultFile, md5)) {
           replaceOldDb(resultFile);
        }
        updatePrefs(md5);//，下载完毕，md5不对，放弃此次更新，防止md5出错，重复下载一直出错，
        clearTmp();
    }

    @Override
    public void failed() {
        restoreDefaultDb();
        clearTmp();
    }

    private void restoreDefaultDb() {
        File defaultDbBackupFile = getDefaultDbBackupFile();
        if (defaultDbBackupFile.exists()) {
            if(getDefaultDbFile() !=null) {
                defaultDbBackupFile.renameTo(getDefaultDbFile());
            }
        }
    }

    private File getClearDbTmpFile() {
        return new File(mContext.getFilesDir().getPath(), "cleardb.temp");
    }

    private void clearTmp() {
        File clearDbTmpFile = getClearDbTmpFile();
        if (clearDbTmpFile.exists()) clearDbTmpFile.delete();
    }
}
