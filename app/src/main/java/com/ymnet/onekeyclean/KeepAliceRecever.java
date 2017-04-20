package com.ymnet.onekeyclean;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.yment.killbackground.view.CleanActivity;

/**
 * Created by Administrator on 2016/12/23.
 */

public class KeepAliceRecever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(Intent.ACTION_BOOT_COMPLETED.equals(action)){
            CleanActivity.startStaticApp(context);
        }
        CleanActivity.startStaticApp(context);
    }
}
