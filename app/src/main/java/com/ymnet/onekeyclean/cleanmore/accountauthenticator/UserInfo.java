package com.ymnet.onekeyclean.cleanmore.accountauthenticator;

import org.json.JSONObject;

/**
 * Created by Janan on 2015/6/2.
 */
public class UserInfo {
    public String uId;
    public String uName;
    public String passId;
    public String lastToken;
    public String avatarURL;
    public String regType;
    public String phone;
    public String signin;
    public int jifen;
    //签到
    public int signFinish ;

    public void parseObject(JSONObject infoObj){
        uName = infoObj.optString("uname");
        uId = infoObj.optString("uid");
        passId = infoObj.optString("passid");
        lastToken = infoObj.optString("lastToken");
        avatarURL = infoObj.optString("avatar");
        regType = infoObj.optString("regType");
        phone = infoObj.optString("phone");
        jifen = infoObj.optInt("jifen");
        signFinish = infoObj.optInt("signFinish");
    }
}
