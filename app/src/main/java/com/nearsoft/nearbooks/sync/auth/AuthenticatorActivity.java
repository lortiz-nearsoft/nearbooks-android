package com.nearsoft.nearbooks.sync.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.databinding.ActivityAuthenticatorBinding;
import com.nearsoft.nearbooks.di.components.GoogleApiClientComponent;
import com.nearsoft.nearbooks.exceptions.SignInException;
import com.nearsoft.nearbooks.models.UserModel;
import com.nearsoft.nearbooks.models.sqlite.User;
import com.nearsoft.nearbooks.util.SyncUtil;
import com.nearsoft.nearbooks.util.ViewUtil;

import javax.inject.Inject;

/**
 * Nearbooks authenticator activity.
 * Created by epool on 12/21/15.
 */
public class AuthenticatorActivity extends AccountAuthenticatorAppCompatActivity {

    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TOKEN_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";

    private final static int RC_SIGN_IN = 1;
    @Inject
    protected SharedPreferences sharedPreferences;
    private ActivityAuthenticatorBinding mBinding;
    private AccountManager mAccountManager;
    private String mAuthTokenType;
    private String mAccountType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = getBinding(ActivityAuthenticatorBinding.class);

        mAccountManager = AccountManager.get(getBaseContext());

        mAuthTokenType = getIntent().getStringExtra(ARG_AUTH_TOKEN_TYPE);
        mAccountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);
        if (mAuthTokenType == null) {
            mAuthTokenType = AccountGeneral.AUTH_TOKEN_TYPE_FULL_ACCESS;
        }

        mBinding.signInButton.setOnClickListener(v -> signIn());
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_authenticator;
    }

    @Override
    protected void injectComponent(GoogleApiClientComponent googleApiClientComponent) {
        googleApiClientComponent.inject(this);
    }

    public void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            finishLogin(result);
        }
    }

    private void finishLogin(GoogleSignInResult result) {
        try {
            createSyncAccount(UserModel.signIn(this, result));
        } catch (SignInException e) {
            ViewUtil.showSnackbarMessage(mBinding, e.getDisplayMessage(this));
            Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient);
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        }
    }

    /**
     * Create an entry for this application in the system account list, if it isn't already there.
     */
    private void createSyncAccount(User user) {
        boolean newAccount = false;
        boolean setupComplete = sharedPreferences
                .getBoolean(AccountGeneral.PREF_SETUP_COMPLETE, false);

        // Create account, if it's missing. (Either first run, or user has deleted account.)
        Account account = new Account(user.getEmail(), mAccountType);
        if (mAccountManager.addAccountExplicitly(account, null, null)) {
            mAccountManager.setAuthToken(account, mAuthTokenType, user.getIdToken());

            SyncUtil.configSyncPeriod(account);
            newAccount = true;

            Bundle bundle = new Bundle();
            bundle.putString(AccountManager.KEY_ACCOUNT_NAME, user.getEmail());
            bundle.putString(AccountManager.KEY_ACCOUNT_TYPE, mAccountType);

            Intent intent = new Intent().putExtras(bundle);

            setAccountAuthenticatorResult(intent.getExtras());
            setResult(RESULT_OK, intent);
            finish();
        }

        // Schedule an initial sync if we detect problems with either our account or our local
        // data has been deleted. (Note that it's possible to clear app data WITHOUT affecting
        // the account list, so wee need to check both.)
        if (newAccount || !setupComplete) {
            SyncUtil.triggerRefresh(user);

            sharedPreferences.edit()
                    .putBoolean(AccountGeneral.PREF_SETUP_COMPLETE, true)
                    .apply();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }
}
