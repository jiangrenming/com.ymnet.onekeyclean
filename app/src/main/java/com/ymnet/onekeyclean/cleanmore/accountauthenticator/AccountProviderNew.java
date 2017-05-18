package com.ymnet.onekeyclean.cleanmore.accountauthenticator;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by liup on 2015/10/20.
 */
public class AccountProviderNew extends ContentProvider {
    /**
     * provider authorities
     */
    public static final String AUTHORITIES = "onekeyclean_account";

    public static final String URI_PATH_NEW = "accountnew";

    public static final int RESULT_NEW_ACCOUNT = 2;

    private static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(AUTHORITIES, URI_PATH_NEW, RESULT_NEW_ACCOUNT);
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case RESULT_NEW_ACCOUNT:
                if(Account.getExistedInstance().isLocalExisted(getContext())) {
                    AccountCursor cursor = new AccountCursor();
                    UserInfo info = new UserInfo();
                    info.uId = Account.getExistedInstance().getUserInfo(Account.INFOKEY_USERID, getContext());
                    info.uName = Account.getExistedInstance().getUserInfo(Account.INFOKEY_USERNAME, getContext());
                    info.passId = Account.getExistedInstance().getUserInfo(Account.INFOKEY_PASSID, getContext());
                    info.phone = Account.getExistedInstance().getUserInfo(Account.INFOKEY_PHONENUMBER, getContext());
                    info.avatarURL = Account.getExistedInstance().getUserInfo(Account.INFOKEY_AVARTARURL, getContext());
                    info.lastToken = Account.getExistedInstance().getUserInfo(Account.INFOKEY_ACCESSTOKEN, getContext());
                    info.regType = Account.getExistedInstance().getUserInfo(Account.INFOKEY_REGTYPE, getContext());
                    info.signin = Account.getExistedInstance().getUserInfo(Account.INFOKEY_SIGNIN, getContext());
                    cursor.setData(info);
                    return cursor;
                }
                break;
            default:
                return null;
        }
        return null;

    }

    @Override
    public String getType(Uri uri) {
        return "";
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }


}
