package com.ymnet.onekeyclean.cleanmore.notification;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.os.Bundle;
import android.util.Log;

import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.accountauthenticator.AccountUtils;
import com.ymnet.onekeyclean.cleanmore.accountauthenticator.MyAccountConfig;

import bolts.Task;


public class HomeTabActivity extends Activity {
    private final String TAG = "HomeTabActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homet_tab);

        doAccountAuthenticator();

    }

    private void doAccountAuthenticator() {
        Task.BACKGROUND_EXECUTOR.submit(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: 添加账户功能被调用了");
                AccountManager accountManager = AccountManager.get(HomeTabActivity.this);
                //添加账户
                Account account = AccountUtils.getAccount(accountManager, MyAccountConfig.ACCOUNT_NAME);
                if (account == null) {
                    Log.d(TAG, "run: account为空,new");
                    account = new Account(MyAccountConfig.ACCOUNT_NAME, MyAccountConfig.ACCOUNT_TYPE);
                    //同步周期设置
                    boolean isAdd = accountManager.addAccountExplicitly(account, MyAccountConfig.ACCOUNT_PASSWORD, null);
                    if (isAdd) {
                        Log.d(TAG, "run: 同步周期设置成功");
                        ContentResolver.setIsSyncable(account, MyAccountConfig.CONTENT_AUTHORITY, MyAccountConfig.ACCOUNT_SYNCABLE);
                        ContentResolver.setSyncAutomatically(account, MyAccountConfig.CONTENT_AUTHORITY, true);
                        ContentResolver.addPeriodicSync(account, MyAccountConfig.CONTENT_AUTHORITY, new Bundle(), MyAccountConfig.ACCOUNT_SYNC_TIME);
                    }
                }
                finish();
            }
        });

    }

}
