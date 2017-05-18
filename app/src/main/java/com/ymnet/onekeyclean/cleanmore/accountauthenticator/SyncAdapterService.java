package com.ymnet.onekeyclean.cleanmore.accountauthenticator;

import android.accounts.Account;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.IBinder;

public class SyncAdapterService extends Service{
    private static SyncAdapterImpl sSyncAdapter = null;
    private static final Object sSyncAdapterLock = new Object();

    public SyncAdapterService() {
        super();
    }

    private static class SyncAdapterImpl extends AbstractThreadedSyncAdapter {
        public SyncAdapterImpl(Context context) {
            super(context, true);
        }

        @Override
        public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return getSyncAdapter().getSyncAdapterBinder();
    }

    private SyncAdapterImpl getSyncAdapter() {
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null)
                sSyncAdapter = new SyncAdapterImpl(this);
        }
        return sSyncAdapter;
    }
}
