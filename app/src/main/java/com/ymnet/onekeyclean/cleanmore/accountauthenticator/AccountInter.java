package com.ymnet.onekeyclean.cleanmore.accountauthenticator;

import android.content.Context;

/**
 * Created by Janan on 2015/5/13.
 * Account接口
 */
interface AccountInter {



//    /**
//     * 获取用户名
//     * */
//    public String getUsername();
//    /**
//     * 获取用户id
//     * */
//    public String getUserId();
//
//    /**
//     * 获取token
//     * */
//    public String getAccessToken();
//
//    /**
//     * 获取用户手机号
//     * */
//    public String getPhoneNumber();
//
//    /**
//     * 获取用户passid
//     * */
//    public String getPassId();
//
//    /**
//     * 获取用户头像url
//     * */
//    public String getAvartarURL();

    /**
     * 获取用户相关信息
     * */

    public String getUserInfo(int infoKey, Context context);
//
//    public void setUserInfo(int infoKey,String infoValue,Context context);


}
