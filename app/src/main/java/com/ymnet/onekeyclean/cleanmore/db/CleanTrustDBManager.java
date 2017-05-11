package com.ymnet.onekeyclean.cleanmore.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ymnet.onekeyclean.cleanmore.junk.ITEMTYPE;
import com.ymnet.onekeyclean.cleanmore.junk.TrustMode;

import java.util.ArrayList;
import java.util.List;

public class CleanTrustDBManager {
    private CleanTrustDBHelper helper;

    public  CleanTrustDBManager(Context context) {
        helper = new CleanTrustDBHelper(context);
    }

    /**
     * 数据库添加字段
     */
    public boolean insert(ContentValues values) {
        try {
			SQLiteDatabase db = helper.getReadableDatabase();
			long result = db.insert(CleanTrustDBHelper.TABLE_NAME, null, values);
			db.close();
			return result != -1;
		} catch (Exception e) {
		}
        return true;
    }

    /**
     * 查询数据库的总条数
     */

    public int queryCount() {
        int count = 0;
        SQLiteDatabase db = helper.getReadableDatabase();
        String columns[] = {CleanTrustDBHelper.FILE_PATH};
        Cursor cursor = db.query(CleanTrustDBHelper.TABLE_NAME, columns, null, null, null, null, null);
        if(cursor!=null){
            count=cursor.getCount();
            cursor.close();
        }
        db.close();
        return count;
    }

    /**
     * 查询是否包含有指定路径的记录
     *
     * @param path
     * @return
     */
    public boolean isContains(String path) {
        boolean contains = false;
        SQLiteDatabase db = helper.getReadableDatabase();
        String columns[] = {CleanTrustDBHelper.FILE_PATH};
        // 查询条件
        String whereClause = CleanTrustDBHelper.FILE_PATH + "=?";
        // 条件参数
        String[] whereArgs = {path};
        Cursor cursor = db.query(CleanTrustDBHelper.TABLE_NAME, columns, whereClause, whereArgs, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            contains = true;
            cursor.close();
        }

        db.close();
        return contains;
    }

    public void delete(String path) {
        SQLiteDatabase db = helper.getReadableDatabase();
        // 删除条件
        String whereClause = CleanTrustDBHelper.FILE_PATH + "=?";
        // 删除条件参数
        String[] whereArgs = {path};
        // 执行删除
        db.delete(CleanTrustDBHelper.TABLE_NAME, whereClause, whereArgs);
        db.close();
    }

    /**
     * 查询对应类型的忽略名单
     *
     * @param it 需要查询的类型 有4中类型，只可以查3中枚举ITEMTYPE的值，如果传入ITEMTYPE.TITLE 会返回size=0的集合
     * @return 返回指定类型的白名单集合
     */
    public ArrayList<TrustMode> query(ITEMTYPE it) {
        ArrayList<TrustMode> list = new ArrayList<TrustMode>();
        if(it== ITEMTYPE.TITLE){
            return null;
        }
        int t = it.ordinal();

        SQLiteDatabase db = helper.getReadableDatabase();
        String columns[] = {CleanTrustDBHelper.FILE_NAME, CleanTrustDBHelper.FILE_PATH, CleanTrustDBHelper.FILE_SIZE, CleanTrustDBHelper.FILE_TYPE};
        Cursor cursor = db.query(CleanTrustDBHelper.TABLE_NAME, columns, CleanTrustDBHelper.FILE_TYPE + "=?", new String[]{t + ""}, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(0);
                String path = cursor.getString(1);
                long size = cursor.getLong(2);
//                int type = cursor.getInt(3);
                TrustMode mode = new TrustMode();
                mode.name = name;
                mode.path = path;
                mode.size = size;
                mode.type = it; //ITEMTYPE.values()[type];//把int值转换成对应的枚举类型
                list.add(mode);
            }
            cursor.close();
        }

        db.close();
        return list;
    }


    /**
     * 查询全部数据
     * 返回以hashmap的形式返回
     */
    public List<TrustMode> queryAll() {
        List<TrustMode> result = new ArrayList<TrustMode>();
        ITEMTYPE[] types = ITEMTYPE.values();
        for (int i = 0; i < types.length; i++) {
            ITEMTYPE itemtype = types[i];
            if (itemtype == ITEMTYPE.TITLE) continue;//如果是title 跳出本次循环
            List<TrustMode> data = query(types[i]);
            if (data != null && data.size() > 0) {
                TrustMode mode = new TrustMode();
                switch (itemtype){
                    case CACHE:
                        mode.name="缓存";
                        break;
                    case REMAIN:
                        mode.name="残留文件";
                        break;
                    case APKFILE:
                        mode.name="安装包";
                }

                mode.type = ITEMTYPE.TITLE;
                mode.count = data.size();
                result.add(mode);
                result.addAll(data);
            }
        }
        return result;
    }

}
