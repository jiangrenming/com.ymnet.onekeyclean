package com.ymnet.onekeyclean.cleanmore.junk.mode;

/**
 * Created by Administrator on 2015/1/9.
 */
public class InstalledAppAndRAM extends JunkChild {
    public static long lastCleanTime;
    public InstalledApp app;
    public InstalledApp getApp() {
        return app;
    }

    public void setApp(InstalledApp app) {
        this.app = app;
    }

    public InstalledAppAndRAM() {}
    public InstalledAppAndRAM(InstalledApp app) {
       this.app=app;
    }
}
