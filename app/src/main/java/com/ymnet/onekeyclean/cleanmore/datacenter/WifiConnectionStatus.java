package com.ymnet.onekeyclean.cleanmore.datacenter;


import android.content.Context;
import android.content.ServiceConnection;
import android.os.Handler;

public class WifiConnectionStatus {

    public static final String TAG = "WifiConnectionStatus";

    public static final String CONN_STATUS_KEY = "conn_status_key";

    public static final String CONN_NOTIFY_KEY = "conn_notify_key";

    public static final String CONN_COMMAND_KEY = "conn_command_key";

    /**
     * 无连接
     */
    public static final int DISCONNECTED = 0;

    /**
     * USB连接
     */
    public static final int USB_CONNECTED = 2;

    private long lastCheckTime;

    //0 disconnect 1 wificonnect 2 usb
    private int connStatus;

//    private CheckBindStstus checkBindStstus;

    private ServiceConnection paramDao;

    private Handler mHandler;

    private Context mContext;

   /* public void init(Handler handler, Context context) {
        this.mHandler = handler;
        mContext = context;
        paramDao = new ServiceConnection() {

            private BindstatusChanged observer = new BindstatusChanged.Stub() {

                @Override
                public void statusChanged(int statusCode) throws RemoteException {
                    setConnStatus(statusCode);

                    L.i("statusChanged", "statusChanged" + statusCode);
                    if (statusCode == DISCONNECTED) {
                        mHandler.sendEmptyMessage(DataCenterObserver.WIFI_PC_LINK_STATUS);
                    } else if (statusCode == USB_CONNECTED) {
                        mHandler.sendEmptyMessage(DataCenterObserver.WIFI_PC_LINK_STATUS);
                    }
                }
            };

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                mHandler.sendEmptyMessage(DataCenterObserver.WIFI_PC_LINK_STATUS);
            }

            @Override
            public void onServiceConnected(ComponentName arg0, IBinder arg1) {
                Log.i("onServiceConnected", "onServiceConnected");
                checkBindStstus = CheckBindStstus.Stub.asInterface(arg1);
                try {
                    checkBindStstus.checkStstus(observer);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(DataCenterObserver.WIFI_PC_LINK_STATUS);
                }
            }
        };

        context.registerReceiver(new ServiceCreatedReceiver(), new IntentFilter(ConnectionService.ACTION_CONNECTION_SERVICE_CREATED));

    }*/

    public void ungesiterService() {
        try {
            mContext.unbindService(paramDao);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   /* public CheckBindStstus getCheckBindStstus() {
        return checkBindStstus;
    }*/

    public int getConnStatus() {

        if (System.currentTimeMillis() - lastCheckTime > 20 * 1000) {
            return WifiConnectionStatus.DISCONNECTED;
        }
        return connStatus;
    }

    public void setConnStatus(int connStatus) {
        lastCheckTime = System.currentTimeMillis();
        this.connStatus = connStatus;
    }

   /* class ServiceCreatedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (paramDao != null) {
                mContext.bindService(new Intent(mContext, ConnectionService.class), paramDao, Context.BIND_AUTO_CREATE);
            }
        }
    }*/

}
