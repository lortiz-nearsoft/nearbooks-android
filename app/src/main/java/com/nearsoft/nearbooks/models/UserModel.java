package com.nearsoft.nearbooks.models;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Build;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.nearsoft.nearbooks.BuildConfig;
import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.exceptions.NearbooksException;
import com.nearsoft.nearbooks.exceptions.SignInException;
import com.nearsoft.nearbooks.models.view.User;
import com.nearsoft.nearbooks.sync.auth.AccountGeneral;
import com.nearsoft.nearbooks.util.ErrorUtil;
import com.nearsoft.nearbooks.util.Util;
import com.nearsoft.nearbooks.view.activities.BaseActivity;

import io.realm.Realm;

/**
 * User model.
 * Created by epool on 11/18/15.
 */
public class UserModel {

    private final static String NEARSOFT_EMAIL_DOMAIN = "@nearsoft.com";
    private final static int UNKNOWN_STATUS_CODE = 12501;

    public static User signIn(Context context, GoogleSignInResult result) throws NearbooksException {
        if (result.isSuccess()) {
            GoogleSignInAccount googleSignInAccount = result.getSignInAccount();

            validateNearsoftAccount(googleSignInAccount);

            User user = new User(googleSignInAccount);

            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(r -> {
                r.delete(com.nearsoft.nearbooks.models.realm.User.class);
                r.copyToRealm(new com.nearsoft.nearbooks.models.realm.User(user));
            });
            realm.close();

            if (!BuildConfig.DEBUG) {
                Crashlytics.setUserIdentifier(user.getId());
                Crashlytics.setUserEmail(user.getEmail());
                Crashlytics.setUserName(user.getDisplayName());
            }

            return user;
        } else if (!Util.isThereInternetConnection(context)) {
            throw new NearbooksException("Internet connection error.",
                    R.string.error_internet_connection);
        } else if (result.getStatus().getStatusCode() != UNKNOWN_STATUS_CODE) {
            int statusCode = result.getStatus().getStatusCode();
            throw new NearbooksException(
                    "Google api error: " + statusCode + ".",
                    R.string.error_google_api, statusCode
            );
        } else {
            throw ErrorUtil.getGeneralException("Unknown Exception.");
        }
    }

    @SuppressWarnings("deprecation")
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
                    future -> {
                        if (future.isDone()) signOut(googleApiClient, onSignOutSuccess);
                    },
                    null
            );
        } else {
            accountManager.removeAccount(
                    account,
                    future -> {
                        if (future.isDone()) signOut(googleApiClient, onSignOutSuccess);
                    },
                    null
            );
        }
    }

    private static void validateNearsoftAccount(GoogleSignInAccount googleSignInAccount)
            throws SignInException {

        boolean isNearsoftAccountValid = googleSignInAccount != null &&
                googleSignInAccount.getEmail() != null &&
                googleSignInAccount.getEmail()
                        .endsWith(NEARSOFT_EMAIL_DOMAIN);

        if (!isNearsoftAccountValid) {
            throw new SignInException("Nearsoft account needed.",
                    R.string.message_nearsoft_account_needed);
        }
    }

    private static void signOut(GoogleApiClient googleApiClient, Runnable onSignOutSuccess) {
        Auth.GoogleSignInApi.revokeAccess(googleApiClient);
        Auth.GoogleSignInApi.signOut(googleApiClient);

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(r -> r.delete(com.nearsoft.nearbooks.models.realm.User.class));
        realm.close();

        if (onSignOutSuccess != null) {
            onSignOutSuccess.run();
        }
        SharedPreferenceModel.clear();
    }

}
