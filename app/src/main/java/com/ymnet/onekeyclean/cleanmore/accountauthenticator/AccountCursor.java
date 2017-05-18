package com.ymnet.onekeyclean.cleanmore.accountauthenticator;

import android.content.ContentResolver;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;


import java.io.Serializable;


/**
 * Created by liup on 2015/11/13.
 */
public class AccountCursor implements Cursor, Serializable {
    private int mPosition = -1;

    public void setData(UserInfo info) {
        Bundle bundle = new Bundle();
        bundle.putString("username", info.uName);
        bundle.putString("userid", info.uId);
        bundle.putString("useraccesstoken", info.lastToken);
        bundle.putString("userphonenum", info.phone);
        bundle.putString("userpassid", info.passId);
        bundle.putString("useravatarurl", info.avatarURL);
        bundle.putString("userregtype", info.regType);
        bundle.putString("userissignin", info.signin);
        respond(bundle);
    }

    private boolean isEmpty() {
        return mBundle == null;
    }

    private String[] colus = {"username", "userid", "useraccesstoken", "userphonenum", "userpassid", "useravatarurl", "userregtype", "userissignin"};

    @Override
    public int getCount() {
        return isEmpty() ? 0 : 1;
    }

    @Override
    public int getPosition() {
        return mPosition;
    }

    @Override
    public boolean move(int offset) {
        if (!isEmpty()) {
            int posiBuf = mPosition + offset;
            if (posiBuf >= 0 && posiBuf < 1) {
                mPosition = posiBuf;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean moveToPosition(int position) {
        if (!isEmpty()) {
            if (position >= 0 && position < 1) {
                mPosition = position;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean moveToFirst() {
        if (mBundle != null) {
            mPosition = -1;
            return true;
        }
        return false;
    }

    @Override
    public boolean moveToLast() {
        if (!isEmpty()) {
            mPosition = 0;
            return true;
        }
        return false;
    }

    @Override
    public boolean moveToNext() {
        if (!isEmpty() && (mPosition + 1) < 1) {
            mPosition++;
            return true;
        }
        return false;
    }

    @Override
    public boolean moveToPrevious() {
        if (!isEmpty() && (mPosition - 1) > 0) {
            mPosition--;
            return true;
        }

        return false;
    }

    @Override
    public boolean isFirst() {
        if (!isEmpty()) {
            return mPosition == 0;
        }
        return false;
    }

    @Override
    public boolean isLast() {
        if (!isEmpty()) {
            return mPosition == 0;
        }
        return false;
    }

    @Override
    public boolean isBeforeFirst() {
        return false;
    }

    @Override
    public boolean isAfterLast() {
        return false;
    }

    @Override
    public int getColumnIndex(String columnName) {
        int columnIndex = 0;
        for (int i = 0; i < colus.length; i++) {
            if (colus[i].equals(columnName)) {
                columnIndex = i;
                break;
            }
        }
        return columnIndex;
    }

    @Override
    public int getColumnIndexOrThrow(String columnName) throws IllegalArgumentException {
        int columnIndex = 0;
        for (int i = 0; i < colus.length; i++) {
            if (colus[i].equals(columnName)) {
                columnIndex = i;
                break;
            }
        }
        return columnIndex;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return colus[columnIndex];
    }

    @Override
    public String[] getColumnNames() {
        return colus;
    }

    @Override
    public int getColumnCount() {
        return 8;
    }

    @Override
    public byte[] getBlob(int columnIndex) {
        return new byte[0];
    }

    @Override
    public String getString(int columnIndex) {
        if (columnIndex == -1 || mBundle == null) {
            return "";
        }
        return getExtras().getString(colus[columnIndex]);
    }

    @Override
    public void copyStringToBuffer(int columnIndex, CharArrayBuffer buffer) {

    }

    @Override
    public short getShort(int columnIndex) {
        return 0;
    }

    @Override
    public int getInt(int columnIndex) {
        return 0;
    }

    @Override
    public long getLong(int columnIndex) {
        return 0;
    }

    @Override
    public float getFloat(int columnIndex) {
        return 0;
    }

    @Override
    public double getDouble(int columnIndex) {
        return 0;
    }

    @Override
    public int getType(int columnIndex) {
        return 0;
    }

    @Override
    public boolean isNull(int columnIndex) {
        return false;
    }

    @Override
    public void deactivate() {

    }

    @Override
    public boolean requery() {
        return false;
    }

    @Override
    public void close() {
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public void registerContentObserver(ContentObserver observer) {

    }

    @Override
    public void unregisterContentObserver(ContentObserver observer) {

    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void setNotificationUri(ContentResolver cr, Uri uri) {

    }

    @Override
    public Uri getNotificationUri() {
        return null;
    }

    @Override
    public boolean getWantsAllOnMoveCalls() {
        return false;
    }

    @Override
    public Bundle getExtras() {
        return mBundle;
    }

    public Bundle mBundle;

    @Override
    public Bundle respond(Bundle extras) {
        mBundle = extras;
        return mBundle;
    }

    //为了适配6.0系统，必须实现该方法
    public void setExtras(Bundle extras) {

    }
}
