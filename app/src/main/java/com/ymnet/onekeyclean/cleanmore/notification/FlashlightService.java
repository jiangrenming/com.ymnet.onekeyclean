package com.ymnet.onekeyclean.cleanmore.notification;

import android.app.Service;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.ymnet.onekeyclean.cleanmore.utils.NotificationUtil;
import com.example.commonlibrary.utils.PhoneModel;
import com.umeng.analytics.MobclickAgent;
import com.ymnet.onekeyclean.cleanmore.utils.C;
import com.ymnet.onekeyclean.cleanmore.utils.OnekeyField;

import java.util.List;


public class FlashlightService extends Service {

    private static Camera camera;
    private final        String TAG   = "FlashlightService";
    private static final String MODEL = "androidmodel";
    private CameraManager mManager;
    private static boolean toggle = true;
    private String mAndroidModel;

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
            NotificationUtil.collapseStatusBar(this);
            //            ToastUtil.showToastForShort("请先获取系统权限");
        }
        try {
//            mAndroidModel = PhoneModel.getAndroidModel();

            if (PhoneModel.matchModel("M5")) {
                if (camera == null) {
                    camera = Camera.open();
                }
                //改变手电筒显示状态
                Intent intent3 = new Intent();
                intent3.setAction("flashlight_status");
                intent3.putExtra("status", !isFlashlightOn());
                sendBroadcast(intent3);

                if (isFlashlightOn()) {
                    Log.d(TAG, "flashlightUtils: 闪光灯现在开着,关闭动作");
                    camera.getParameters().setFlashMode(Camera.Parameters.FLASH_MODE_OFF);// 关闭
                    camera.setParameters(camera.getParameters());
                    camera.stopPreview();
                    camera.release();
                    camera = null;
                } else {
                    openFlashLight();
                }

            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mManager.setTorchMode("0", toggle);

                Log.d(TAG, "onStartCommand: " + toggle);
                Intent intent1 = new Intent();
                intent1.setAction("flashlight_status");
                intent1.putExtra("status", toggle);
                sendBroadcast(intent1);

                if (toggle) {
                    Intent intent2 = new Intent(this, StatisticReceiver.class);
                    intent2.putExtra(OnekeyField.KEY, OnekeyField.FLASHLIGHT);
                    sendBroadcast(intent2);
                }

                toggle = !toggle;
            } else {
                flashlightUtils();
            }

        } catch (Exception e) {
            MobclickAgent.reportError(C.get(), e.fillInStackTrace());
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

                Intent intent2 = new Intent(this, StatisticReceiver.class);
                intent2.putExtra(OnekeyField.KEY, OnekeyField.FLASHLIGHT);
                sendBroadcast(intent2);

            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "flashlightUtils: " + e.toString());
            MobclickAgent.reportError(C.get(),  e.fillInStackTrace());
        } finally {
            Intent intent3 = new Intent();
            try {
                intent3.setAction("flashlight_status");
                intent3.putExtra("status", isFlashlightOn());
            } catch (Exception e) {
                if (camera != null) {
                    camera.release();
                    camera = null;
                }
            } finally {
                sendBroadcast(intent3);
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

    private void openFlashLight() {

        //        camera = Camera.open();
        Camera.Parameters params = camera.getParameters();
        List<String> list = params.getSupportedFlashModes();
        if (list.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        } else {
            Toast.makeText(getApplicationContext(), "此设备不支持闪光灯模式",
                    Toast.LENGTH_SHORT).show();
        }
        camera.setParameters(params);
        camera.startPreview();

        Camera.Parameters p = camera.getParameters();
        List<String> focusModes = p.getSupportedFocusModes();

        if (focusModes != null && focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            //Phone supports autofocus!
            camera.autoFocus(new Camera.AutoFocusCallback() {
                public void onAutoFocus(boolean success, Camera camera) {
                }
            });
        } else {
            //Phone does not support autofocus!
        }

        //统计
        Intent intent2 = new Intent(this, StatisticReceiver.class);
        intent2.putExtra(OnekeyField.KEY, OnekeyField.FLASHLIGHT);
        sendBroadcast(intent2);
    }

}