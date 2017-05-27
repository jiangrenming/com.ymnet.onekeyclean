package com.ymnet.onekeyclean.cleanmore.notification;

import android.app.Service;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.umeng.analytics.MobclickAgent;
import com.ymnet.onekeyclean.cleanmore.utils.C;
import com.example.commonlibrary.utils.NotificationUntil;
import com.ymnet.onekeyclean.cleanmore.utils.OnekeyField;
import com.ymnet.onekeyclean.cleanmore.utils.ToastUtil;


public class FlashlightService extends Service {

    private static Camera camera;
    private final String TAG = "FlashlightService";
    private static final String MODEL = "androidmodel";
    private CameraManager mManager;
    private static boolean toggle = true;

    public FlashlightService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: 绑定服务");
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mManager = (CameraManager) getSystemService(C.get().CAMERA_SERVICE);
        boolean model = intent.getBooleanExtra(MODEL, false);

        if (model) {
            Log.d(TAG, "onStartCommand: 需要收起通知栏的机型");
            //收起通知栏
            NotificationUntil.collapseStatusBar(this);
            ToastUtil.showToastForShort("请先获取系统权限");
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mManager.setTorchMode("0", toggle);

                Log.d(TAG, "onStartCommand: " + toggle);
                Intent intent1 = new Intent();
                intent1.setAction("flashlight_status");
                intent1.putExtra("status", toggle);
                sendBroadcast(intent1);

                if (toggle) {
                    Intent intent2 = new Intent(this, SettingsReceiver.class);
                    intent2.putExtra(OnekeyField.KEY, OnekeyField.FLASHLIGHT);
                    sendBroadcast(intent2);
                }

                toggle = !toggle;
            } else {
                flashlightUtils();
            }

        } catch (Exception e) {
            MobclickAgent.reportError(C.get(), e.toString());
            e.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public void flashlightUtils() {

        try {
            if (camera == null) {
                camera = Camera.open();
            }
            //            mParameters = camera.getParameters();
            if (isFlashlightOn()) {
                Log.d(TAG, "flashlightUtils: 闪光灯现在开着,关闭动作");
                camera.getParameters().setFlashMode(Camera.Parameters.FLASH_MODE_OFF);// 关闭
                camera.setParameters(camera.getParameters());
                camera.stopPreview();
                camera.release();
                camera = null;

            } else {
                Log.d(TAG, "flashlightUtils: 闪光灯现在关着,打开动作");
                camera.startPreview();
                Camera.Parameters parameters = camera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);// 开启
                camera.setParameters(parameters);

                Intent intent2 = new Intent(this, SettingsReceiver.class);
                intent2.putExtra(OnekeyField.KEY, OnekeyField.FLASHLIGHT);
                sendBroadcast(intent2);

            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "flashlightUtils: " + e.toString());
            MobclickAgent.reportError(C.get(), "com.ymnet.onekeyclean.cleanmore.notification.FlashlightService:flashlightUtils:" + e.toString());
        } finally {
            Intent intent2 = new Intent();
            try {
                intent2.setAction("flashlight_status");
                intent2.putExtra("status", isFlashlightOn());
            } catch (Exception e) {
                if (camera != null) {
                    camera.release();
                    camera = null;
                }
            } finally {
                sendBroadcast(intent2);
            }
        }

    }

    /**
     * 是否开启了闪光灯
     */
    public boolean isFlashlightOn() {
        String flashMode = camera.getParameters().getFlashMode();
        return flashMode.equals(Camera.Parameters.FLASH_MODE_TORCH);
    }

}