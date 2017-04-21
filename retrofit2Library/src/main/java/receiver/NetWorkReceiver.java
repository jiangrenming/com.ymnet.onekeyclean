package receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import utils.HttpNetUtil;

/**
 * Created by jrm on 2017-3-29.
 */

public class NetWorkReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        HttpNetUtil.INSTANCE.setConnected(context);
    }
}
