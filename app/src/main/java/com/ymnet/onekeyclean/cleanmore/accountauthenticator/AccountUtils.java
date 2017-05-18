package com.ymnet.onekeyclean.cleanmore.accountauthenticator;

import android.accounts.Account;
import android.accounts.AccountManager;

public class AccountUtils {

	public static Account getAccount(AccountManager accountManager, String accountName) {
		Account[] accounts = accountManager.getAccountsByType(MyAccountConfig.ACCOUNT_TYPE);
		for (Account account : accounts) {
			if (account.name.equalsIgnoreCase(accountName)) {
				return account;
			}
		}
		return null;
	}
	
}
