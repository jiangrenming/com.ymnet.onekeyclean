package com.ymnet.onekeyclean;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.yment.killbackground.CleanActivity;

/**
 * Created by Administrator on 2016/12/23.
 */

public class KeepAliceRecever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i("yyj","KeepAliceRecever received action = " + action);
        if(Intent.ACTION_BOOT_COMPLETED.equals(action)){
            CleanActivity.startStaticApp(context);
            Log.i("yyj","KeepAliceRecever boot start");
        }
        CleanActivity.startStaticApp(context);
    }
}
