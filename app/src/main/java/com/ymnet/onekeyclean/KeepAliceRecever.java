package com.ymnet.onekeyclean;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ymnet.killbackground.view.CleanActivity;
import com.ymnet.onekeyclean.cleanmore.notification.NotifyService;

/**
 * Created by Administrator on 2016/12/23.
 */

public class KeepAliceRecever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        CleanActivity.startStaticApp(context);

        //常驻通知栏保活
        Intent service = new Intent(context, NotifyService.class);
        context.startService(service);
    }

}
