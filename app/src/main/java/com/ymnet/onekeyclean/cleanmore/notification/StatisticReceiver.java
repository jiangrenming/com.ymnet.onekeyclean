package com.ymnet.onekeyclean.cleanmore.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import com.ymnet.onekeyclean.cleanmore.utils.NotificationUtil;
import com.umeng.analytics.MobclickAgent;
import com.ymnet.killbackground.view.CleanActivity;
import com.ymnet.onekeyclean.cleanmore.utils.C;
import com.ymnet.onekeyclean.cleanmore.utils.OnekeyField;
import com.ymnet.onekeyclean.cleanmore.utils.StatisticMob;

import java.util.HashMap;
import java.util.Map;

/**
 * @create by MajinBuu
 * @Data 2017-05-27
 * @describe 给service发送BroadcastReceiver做统计
 */
public class StatisticReceiver extends BroadcastReceiver {

    public StatisticReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        /*if (intent.getStringExtra(OnekeyField.KEY).equals(OnekeyField.FLASHLIGHT)) {
            Map<String, String> m = new HashMap<>();
            m.put(OnekeyField.ONEKEYCLEAN, "手电筒");
            MobclickAgent.onEvent(C.get(), StatisticMob.STATISTIC_ID, m);
        } else*/ if (intent.getStringExtra(OnekeyField.KEY).equals(OnekeyField.SETTINGS)) {
            Map<String, String> m = new HashMap<>();
            m.put(OnekeyField.ONEKEYCLEAN, "系统设置");
            MobclickAgent.onEvent(C.get(), StatisticMob.STATISTIC_ID, m);

            Intent intent6 = new Intent(Settings.ACTION_SETTINGS);
            intent6.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            C.get().startActivity(intent6);
            //收起通知栏
            NotificationUtil.collapseStatusBar(context);
        } else if (intent.getStringExtra(OnekeyField.KEY).equals(OnekeyField.KILLBACKGROUND)) {
            Intent intent7 = new Intent(context, CleanActivity.class);
            intent7.putExtra(OnekeyField.ONEKEYCLEAN, "notifymanager");
            intent7.putExtra(OnekeyField.STATISTICS_KEY, StatisticMob.STATISTIC_ID);
            intent7.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            C.get().startActivity(intent7);
            //收起通知栏
            NotificationUtil.collapseStatusBar(context);
        }

    }
}
