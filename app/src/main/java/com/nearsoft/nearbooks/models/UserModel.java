package com.nearsoft.nearbooks.models;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.exceptions.SignInException;
import com.nearsoft.nearbooks.models.sqlite.Book;
import com.nearsoft.nearbooks.models.sqlite.User;
import com.nearsoft.nearbooks.sync.auth.AccountGeneral;
import com.nearsoft.nearbooks.util.Util;
import com.nearsoft.nearbooks.view.activities.BaseActivity;
import com.raizlabs.android.dbflow.sql.language.Delete;

/**
 * User model.
 * Created by epool on 11/18/15.
 */
public class UserModel {

    private final static int UNKNOWN_STATUS_CODE = 12501;

    public static User signIn(Context context, GoogleSignInResult result) throws SignInException {

        if (result.isSuccess()) {
            GoogleSignInAccount googleSignInAccount = result.getSignInAccount();
            if (validateNearsoftAccount(context, googleSignInAccount)) {
                User user = new User(googleSignInAccount);
                user.save();
                return user;
            } else {
                throw new SignInException(
                        context.getString(R.string.message_nearsoft_account_needed)
                );
            }
        } else if (!Util.isThereInternetConnection(context)) {
            throw new SignInException(context.getString(R.string.error_internet_connection));
        } else if (result.getStatus().getStatusCode() != UNKNOWN_STATUS_CODE) {
            throw new SignInException(
                    context.getString(R.string.error_google_api, result.getStatus())
            );
        } else {
            throw new SignInException("Unknown Exception.");
        }
    }

    public static void signOut(BaseActivity baseActivity,
                               final User user,
                               final GoogleApiClient googleApiClient,
                               final Runnable onSignOutSuccess) {

        AccountManager accountManager = AccountManager.get(baseActivity);
        Account account = new Account(user.getEmail(), AccountGeneral.ACCOUNT_TYPE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            accountManager.removeAccount(
                    account,
                    baseActivity,
                    new AccountManagerCallback<Bundle>() {
                        @Override
                        public void run(AccountManagerFuture<Bundle> future) {
                            if (future.isDone()) signOut(user, googleApiClient, onSignOutSuccess);
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
                            if (future.isDone()) signOut(user, googleApiClient, onSignOutSuccess);
                        }
                    },
                    null
            );
        }
    }

    private static boolean validateNearsoftAccount(Context context,
                                                   GoogleSignInAccount googleSignInAccount) {

        return googleSignInAccount != null && googleSignInAccount.getEmail() != null &&
                googleSignInAccount
                        .getEmail()
                        .endsWith(context.getString(R.string.nearsoft_domain));
    }

    private static void signOut(User user, GoogleApiClient googleApiClient,
                                Runnable onSignOutSuccess) {

        Auth.GoogleSignInApi.revokeAccess(googleApiClient);
        Auth.GoogleSignInApi.signOut(googleApiClient);

        user.delete();

        Delete.tables(User.class, Book.class);

        if (onSignOutSuccess != null) {
            onSignOutSuccess.run();
        }
    }

}
