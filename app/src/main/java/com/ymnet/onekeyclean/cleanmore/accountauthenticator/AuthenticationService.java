package com.ymnet.onekeyclean.cleanmore.accountauthenticator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 *
 * Date:2016/5/21
 * @version V4.0
 * @author Aaron
 */
public class AuthenticationService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyAccountAuthenticator(this).getIBinder();
    }
}
