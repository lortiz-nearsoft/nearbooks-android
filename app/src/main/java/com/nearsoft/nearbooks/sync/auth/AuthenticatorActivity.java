package com.nearsoft.nearbooks.sync.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.databinding.ActivityLoginBinding;
import com.nearsoft.nearbooks.models.sqlite.User;

/**
 * Nearbooks authenticator activity.
 * Created by epool on 12/21/15.
 */
public class AuthenticatorActivity extends AccountAuthenticatorAppCompatActivity {

    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TOKEN_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";

    private final static int RC_SIGN_IN = 1;

    private ActivityLoginBinding mBinding;
    private AccountManager mAccountManager;
    private String mAuthTokenType;
    private String mAccountType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = getBinding(ActivityLoginBinding.class);

        mAccountManager = AccountManager.get(getBaseContext());

        mAuthTokenType = getIntent().getStringExtra(ARG_AUTH_TOKEN_TYPE);
        mAccountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);
        if (mAuthTokenType == null) {
            mAuthTokenType = AccountGeneral.AUTH_TOKEN_TYPE_FULL_ACCESS;
        }

        mBinding.signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_login;
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
        String errorMessage = null;

        if (result.isSuccess()) {
            GoogleSignInAccount googleSignInAccount = result.getSignInAccount();
            if (validateNearsoftAccount(googleSignInAccount)) {
                User user = new User(googleSignInAccount);

                final Account account = new Account(user.getEmail(), mAccountType);
                // Creating the account on the device and setting the auth token we got
                // (Not setting the auth token will cause another call to the server to authenticate
                // the user)
                if (mAccountManager.addAccountExplicitly(account, null, null)) {
                    mAccountManager.setAuthToken(account, mAuthTokenType, user.getIdToken());

                    user.save();

                    Bundle bundle = new Bundle();
                    bundle.putString(AccountManager.KEY_ACCOUNT_NAME, user.getEmail());
                    bundle.putString(AccountManager.KEY_ACCOUNT_TYPE, mAccountType);

                    Intent intent = new Intent().putExtras(bundle);

                    setAccountAuthenticatorResult(intent.getExtras());
                    setResult(RESULT_OK, intent);
                    finish();

                    // TODO: Request sync...
                }
            } else {
                errorMessage = getString(R.string.message_nearsoft_account_needed);
            }
        } else {
            errorMessage = getString(R.string.error_google_api, result.getStatus());
        }

        if (errorMessage != null) {
            Snackbar
                    .make(
                            mBinding.getRoot(),
                            errorMessage,
                            Snackbar.LENGTH_LONG
                    )
                    .show();
            Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient);
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        }
    }

    private boolean validateNearsoftAccount(GoogleSignInAccount googleSignInAccount) {
        return googleSignInAccount != null && googleSignInAccount.getEmail() != null &&
                googleSignInAccount.getEmail().endsWith(getString(R.string.nearsoft_domain));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }
}
