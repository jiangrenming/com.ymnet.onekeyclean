package utils;

import android.content.Context;

/**
 * Created by Administrator on 2017-4-5.
 */

public class ContextUtils {

    public  static  ContextUtils utils;
    private Context applicationContext;

    public static ContextUtils getInstance(){
        if (utils == null){
            utils = new ContextUtils();
        }
        return  utils;
    }

    public Context getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(Context applicationContext) {
        this.applicationContext = applicationContext;
    }
}
