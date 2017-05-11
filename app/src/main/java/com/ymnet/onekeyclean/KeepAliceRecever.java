package com.ymnet.onekeyclean;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.ymnet.killbackground.view.CleanActivity;

/**
 * Created by Administrator on 2016/12/23.
 */

public class KeepAliceRecever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        CleanActivity.startStaticApp(context);
    }

}
