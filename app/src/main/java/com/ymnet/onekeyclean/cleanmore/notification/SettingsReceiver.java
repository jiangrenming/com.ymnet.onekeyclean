package com.ymnet.onekeyclean.cleanmore.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import com.umeng.analytics.MobclickAgent;
import com.ymnet.onekeyclean.cleanmore.utils.C;
import com.example.commonlibrary.utils.NotificationUntil;
import com.ymnet.onekeyclean.cleanmore.utils.OnekeyField;
import com.ymnet.onekeyclean.cleanmore.utils.StatisticMob;

import java.util.HashMap;
import java.util.Map;

public class SettingsReceiver extends BroadcastReceiver {

    public SettingsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getStringExtra(OnekeyField.KEY).equals(OnekeyField.FLASHLIGHT)) {
            Map<String, String> m = new HashMap<>();
            m.put(OnekeyField.ONEKEYCLEAN, "手电筒");
            MobclickAgent.onEvent(C.get(), StatisticMob.STATISTIC_ID, m);
        } else if (intent.getStringExtra(OnekeyField.KEY).equals(OnekeyField.SETTINGS)) {
            Map<String, String> m = new HashMap<>();
            m.put(OnekeyField.ONEKEYCLEAN, "系统设置");
            MobclickAgent.onEvent(C.get(), StatisticMob.STATISTIC_ID, m);

            Intent intent6 = new Intent(Settings.ACTION_SETTINGS);
            intent6.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            C.get().startActivity(intent6);
            //收起通知栏
            NotificationUntil.collapseStatusBar(context);
        }


    }
}
