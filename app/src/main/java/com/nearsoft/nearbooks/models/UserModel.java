package com.nearsoft.nearbooks.models;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.models.sqlite.Book;
import com.nearsoft.nearbooks.models.sqlite.User;
import com.nearsoft.nearbooks.sync.auth.AccountGeneral;
import com.nearsoft.nearbooks.view.activities.BaseActivity;
import com.raizlabs.android.dbflow.sql.language.Delete;

/**
 * User model.
 * Created by epool on 11/18/15.
 */
public class UserModel {

    public static void signIn(Context context, String userId) {
        SharedPreferences sharedPreferences = context
                .getSharedPreferences(
                        context.getString(R.string.shared_preference_file_name),
                        Context.MODE_PRIVATE
                );
        sharedPreferences
                .edit()
                .putString(context.getString(R.string.current_user_id), userId)
                .apply();
    }

    public static String getCurrentUserId(Context context) {
        SharedPreferences sharedPreferences = context
                .getSharedPreferences(
                        context.getString(R.string.shared_preference_file_name),
                        Context.MODE_PRIVATE
                );
        return sharedPreferences
                .getString(context.getString(R.string.current_user_id), null);
    }

    public static void signOut(BaseActivity baseActivity, User user, GoogleApiClient googleApiClient,
                               final Runnable onSignOutSuccess) {
        Auth.GoogleSignInApi.revokeAccess(googleApiClient);
        Auth.GoogleSignInApi.signOut(googleApiClient);

        AccountManager accountManager = AccountManager.get(baseActivity);
        Account account = new Account(user.getEmail(), AccountGeneral.ACCOUNT_TYPE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            accountManager.removeAccount(
                    account,
                    baseActivity,
                    new AccountManagerCallback<Bundle>() {
                        @Override
                        public void run(AccountManagerFuture<Bundle> future) {
                            if (future.isDone()) {
                                if (onSignOutSuccess != null) {
                                    onSignOutSuccess.run();
                                }
                            }
                        }
                    },
                    null
            );
        } else {
            accountManager.removeAccount(
                    account,
                    new AccountManagerCallback<Boolean>() {
                        @Override
                        public void run(AccountManagerFuture<Boolean> future) {
                            if (future.isDone()) {
                                if (onSignOutSuccess != null) {
                                    onSignOutSuccess.run();
                                }
                            }
                        }
                    },
                    null
            );
        }

        user.delete();

        Delete.tables(User.class, Book.class);
    }

}
