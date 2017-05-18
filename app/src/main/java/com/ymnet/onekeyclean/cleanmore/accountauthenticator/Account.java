package com.ymnet.onekeyclean.cleanmore.accountauthenticator;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.ymnet.onekeyclean.cleanmore.utils.C;


/**
 * Created by Janan on 2015/5/13.
 */
public class Account implements AccountInter {
    public static final int INFOKEY_USERNAME    = 1;
    public static final int INFOKEY_USERID      = 2;
    public static final int INFOKEY_ACCESSTOKEN = 3;
    public static final int INFOKEY_PHONENUMBER = 4;
    public static final int INFOKEY_PASSID      = 5;
    public static final int INFOKEY_AVARTARURL  = 6;
    public static final int INFOKEY_REGTYPE     = 7;
    //    public static final int INFOKEY_HASPHONE = 8;
    public static final int INFOKEY_SIGNIN      = 9;
    public static final int INFOKEY_JIFEN       = 10;
    public static final int INFOKEY_SIGNFINISH  = 11;

    public static final String INFOKEY_USERNAME_LOCAL    = "username";
    public static final String INFOKEY_USERID_LOCAL      = "userid";
    public static final String INFOKEY_ACCESSTOKEN_LOCAL = "useraccesstoken";
    public static final String INFOKEY_PHONENUMBER_LOCAL = "userphonenum";
    public static final String INFOKEY_PASSID_LOCAL      = "userpassid";
    public static final String INFOKEY_AVARTARURL_LOCAL  = "useravatarurl";
    public static final String INFOKEY_REGTYPE_LOCAL     = "userregtype";
    //    private static final String INFOKEY_HASPHONE_LOCAL = "userhasphone";
    public static final String INFOKEY_SIGNIN_LOCAL      = "userissignin";
    public static final String INFOKEY_JIFEN_LOCAL       = "userjifen";
    public static final String INFOKEY_SIGNFINISH_LOCAL  = "usersignfinish";

    private static class AccountHolder {
        private static Account account = new Account();
    }

    public static Account getExistedInstance() {
        return AccountHolder.account;
    }


    @Override
    public String getUserInfo(int infoKey, Context context) {
        return getUserInfo(getLocalKey(infoKey), context);
    }

    /**
     * 是否存在本地账户
     */
    public boolean isLocalExisted(Context context) {
        if (context == null) {
            return false;
        }
        if (!checkUserInfoStyle(context)) {
            signOut(context);
        }

        return !TextUtils.isEmpty(getUserInfo(INFOKEY_PASSID, context));
    }

    /**
     * 是否已登陆
     */
    public boolean isLocalAccountSignin(Context context) {
        if (TextUtils.isEmpty(getUserInfo(INFOKEY_SIGNIN, context))) {
            return false;
        }

        return Integer.parseInt(getUserInfo(INFOKEY_SIGNIN, context)) == 1;
    }

    /**
     * 是否已签到
     */
    public boolean isLocalAccountSignFinish(Context context) {
        if (TextUtils.isEmpty(getUserInfo(INFOKEY_SIGNFINISH, context))) {
            return false;
        }

        return Integer.parseInt(getUserInfo(INFOKEY_SIGNFINISH, context)) == 1;
    }

    /**
     * 设置签到
     */
    public void setLocalAccountSignFinish(Context context, boolean signFinish) {
        if (signFinish) {
            setUserInfo(INFOKEY_SIGNFINISH, "1", context);
        } else {
            setUserInfo(INFOKEY_SIGNFINISH, "0", context);
        }
    }

    /**
     * 更新积分信息
     *
     * @param point 新的总积分
     */
    public void updateLocalAccountPoint(int point) {
        setUserInfo(INFOKEY_JIFEN, String.valueOf(point), C.get());
    }


    /**
     * 是否绑定手机
     */
    public boolean hasPhone(Context context) {
        if (context == null) {
            return false;
        }

        return !TextUtils.isEmpty(getUserInfo(INFOKEY_PHONENUMBER, context));
    }

    /**
     * 判断本地信息格式是否正确
     */
    private boolean checkUserInfoStyle() {

        return checkUserInfoStyle(C.get());
    }

    private boolean checkUserInfoStyle(Context context) {

        if (!isKeyUsedExsited(getLocalKey(INFOKEY_USERID), context) || !isKeyUsedExsited(getLocalKey(INFOKEY_PASSID), context)) {
            return true;
        }
        if (isKeyUsedExsited(getLocalKey(INFOKEY_PHONENUMBER), context)) {
            return true;
        }
        return false;
    }


    /**
     * 添加账户信息
     */
    protected void setUserInfo(int infoKey, String infoValue, Context context) {

        saveUserInfo(getLocalKey(infoKey), infoValue, context);
    }


    /**
     * 退出
     */
    protected void signOut(Context context) {
        signOut(context, null);
    }


    private void signOut(Context context, String tag) {
        removeUserInfo(getLocalKey(Account.INFOKEY_PASSID), context);
        removeUserInfo(getLocalKey(Account.INFOKEY_USERNAME), context);
        removeUserInfo(getLocalKey(Account.INFOKEY_AVARTARURL), context);
        removeUserInfo(getLocalKey(Account.INFOKEY_ACCESSTOKEN), context);
        removeUserInfo(getLocalKey(Account.INFOKEY_USERID), context);
        removeUserInfo(getLocalKey(Account.INFOKEY_REGTYPE), context);
        //        removeUserInfo(getLocalKey(Account.INFOKEY_HASPHONE),context);
        removeUserInfo(getLocalKey(Account.INFOKEY_SIGNIN), context);
        removeUserInfo(getLocalKey(Account.INFOKEY_PHONENUMBER), context);
        removeUserInfo(getLocalKey(Account.INFOKEY_JIFEN), context);
        removeUserInfo(getLocalKey(Account.INFOKEY_SIGNFINISH), context);
    }

    /**
     * 获取本地保存时使用的key
     */
    private String getLocalKey(int infoKey) {
        String localKey = null;
        switch (infoKey) {
            case INFOKEY_USERNAME:
                localKey = INFOKEY_USERNAME_LOCAL;
                break;
            case INFOKEY_USERID:
                localKey = INFOKEY_USERID_LOCAL;
                break;
            case INFOKEY_ACCESSTOKEN:
                localKey = INFOKEY_ACCESSTOKEN_LOCAL;
                break;
            case INFOKEY_PASSID:
                localKey = INFOKEY_PASSID_LOCAL;
                break;
            case INFOKEY_AVARTARURL:
                localKey = INFOKEY_AVARTARURL_LOCAL;
                break;
            case INFOKEY_REGTYPE:
                localKey = INFOKEY_REGTYPE_LOCAL;
                break;
            //            case INFOKEY_HASPHONE:
            //                localKey = INFOKEY_HASPHONE_LOCAL;
            //                break;
            case INFOKEY_SIGNIN:
                localKey = INFOKEY_SIGNIN_LOCAL;
                break;
            case INFOKEY_PHONENUMBER:
                localKey = INFOKEY_PHONENUMBER_LOCAL;
                break;
            case INFOKEY_JIFEN:
                localKey = INFOKEY_JIFEN_LOCAL;
                break;
            case INFOKEY_SIGNFINISH:
                localKey = INFOKEY_SIGNFINISH_LOCAL;
                break;


        }
        return localKey;
    }


    private String getUserInfo(String localKey, Context context) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(localKey, "");
    }

    private boolean saveUserInfo(String localKey, String infoValue, Context context) {
        if (null == infoValue)
            return false;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.edit().putString(localKey, infoValue).commit();

    }

    private void removeUserInfo(String localKey, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().remove(localKey).commit();
    }

    private boolean isKeyUsedExsited(String localKey, Context context) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.contains(localKey);
    }

}
