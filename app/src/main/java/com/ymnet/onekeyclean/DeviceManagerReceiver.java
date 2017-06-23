package com.ymnet.onekeyclean;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.umeng.analytics.MobclickAgent;
import com.ymnet.onekeyclean.cleanmore.utils.C;

import java.lang.ref.WeakReference;

import static com.ymnet.apphelper.BaiduApp.app;

/**
 * Created by MajinBuu on 2017/6/16 0016.
 *
 * @overView 供外部调用-开启任务管理器
 */
// TODO: 2017/6/16 0016
public class DeviceManagerReceiver extends Activity {

    private static DevicePolicyManager mDPM;
    private static ComponentName       mAdminName;

   private Handler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        WeakReference<DeviceManagerReceiver> theActivity;

        public MyHandler(DeviceManagerReceiver activityForPCService) {
            theActivity = new WeakReference<>(activityForPCService);
        }

        @Override
        public void dispatchMessage(Message msg) {
            DeviceManagerReceiver activity = theActivity.get();
            if (msg.what == 1 && activity != null) {
                activity.finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("DeviceManagerReceiver", "DeviceManagerReceiver started");
        openDeviceManager();
        finish();
        //        mHandler.sendEmptyMessageDelayed(1, 2000);
    }

   /* @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("DeviceManagerReceiver", "DeviceManagerReceiver started");
        openDeviceManager();
    }*/

    public static void openDeviceManager() {

        try {
            // Initiate DevicePolicyManager.
            mDPM = (DevicePolicyManager) C.get().getSystemService(Context.DEVICE_POLICY_SERVICE);
            // Set DeviceAdminDemo Receiver for active the component with
            // different option
            mAdminName = new ComponentName(C.get(), DeviceManagerReceiver.class);
//            mAdminName = new ComponentName(C.get(), CleanActivity.class);

            if (!mDPM.isAdminActive(mAdminName)) {
                Log.d("DeviceManagerReceiver", "active");
                // try to become active
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Click on Activate button to secure your application.");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                C.get().startActivity(intent);
            } else {
                // Already is a device administrator, can do security operations
                // now.
                Log.d("DeviceManagerReceiver", "已激活");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("DeviceManagerReceiver", e.toString());
            MobclickAgent.reportError(C.get(), e.fillInStackTrace());
        }

    }
}
