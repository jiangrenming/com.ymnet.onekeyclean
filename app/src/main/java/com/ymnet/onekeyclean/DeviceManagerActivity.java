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

/**
 * Created by MajinBuu on 2017/6/16 0016.
 *
 * @overView 供外部调用-开启任务管理器
 */
// TODO: 2017/6/16 0016
public class DeviceManagerActivity extends Activity {

    private static DevicePolicyManager mDPM;
    private static ComponentName       mAdminName;

    private Handler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        WeakReference<DeviceManagerActivity> theActivity;
        public MyHandler(DeviceManagerActivity activityForPCService) {
            theActivity = new WeakReference<>(activityForPCService);
        }

        @Override
        public void dispatchMessage(Message msg) {
            DeviceManagerActivity activity = theActivity.get();
            if (msg.what == 1 && activity != null) {
                activity.finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("DeviceManagerActivity", "DeviceManagerActivity started");
//                openDeviceManager();
//        mHandler.sendEmptyMessageDelayed(1, 2000);
    }

    public static void openDeviceManager(){

        try {
            // Initiate DevicePolicyManager.
            mDPM = (DevicePolicyManager) C.get().getSystemService(Context.DEVICE_POLICY_SERVICE);
            // Set DeviceAdminDemo Receiver for active the component with
            // different option
            mAdminName = new ComponentName(C.get(), DeviceManagerActivity.class);

            if (!mDPM.isAdminActive(mAdminName)) {
                // try to become active
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Click on Activate button to secure your application.");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                C.get().startActivity(intent);
            } else {
                // Already is a device administrator, can do security operations
                // now.
            }
        } catch (Exception e) {
            e.printStackTrace();
            MobclickAgent.reportError(C.get(),e.fillInStackTrace());
        }

    }

}
