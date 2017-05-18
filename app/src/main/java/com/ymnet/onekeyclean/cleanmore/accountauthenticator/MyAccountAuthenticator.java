package com.ymnet.onekeyclean.cleanmore.accountauthenticator;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.util.Log;

import com.ymnet.onekeyclean.cleanmore.notification.HomeTabActivity;


/**
 * The class {@link MyAccountAuthenticator} is to handle a identity authenticator module of specified account type
 * <p>
 * Date:2016/5/21
 *
 * @author Aaron
 * @version V4.0
 */
public class MyAccountAuthenticator extends AbstractAccountAuthenticator {
    private Context mContext;

    public MyAccountAuthenticator(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    //添加账号
    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        Log.d("HomeTabActivity", "addAccount: 执行添加账号");
        final Intent intent = new Intent(mContext, HomeTabActivity.class);
        intent.putExtra(SyncStateContract.Constants.ACCOUNT_TYPE, authTokenType);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

        final Bundle result = new Bundle();
        result.putParcelable(AccountManager.KEY_INTENT, intent);

        return result;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        return null;
    }
    //认证
    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        return null;
    }
}
