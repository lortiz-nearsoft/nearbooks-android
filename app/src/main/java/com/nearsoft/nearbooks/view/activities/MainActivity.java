package com.nearsoft.nearbooks.view.activities;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.databinding.ActivityMainBinding;
import com.nearsoft.nearbooks.di.components.BaseActivityComponent;
import com.nearsoft.nearbooks.models.sqlite.User;
import com.nearsoft.nearbooks.sync.auth.AccountGeneral;

import java.io.IOException;

import javax.inject.Inject;

import dagger.Lazy;

public class MainActivity extends BaseActivity {

    @Inject
    protected Lazy<User> mLazyUser;
    private AccountManager mAccountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = getBinding(ActivityMainBinding.class);

        mAccountManager = AccountManager.get(this);

        getTokenForAccountCreateIfNeeded(AccountGeneral.ACCOUNT_TYPE,
                AccountGeneral.AUTH_TOKEN_TYPE_FULL_ACCESS);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected void injectComponent(BaseActivityComponent baseActivityComponent) {
        super.injectComponent(baseActivityComponent);
        baseActivityComponent.inject(this);
    }

    private void getTokenForAccountCreateIfNeeded(String accountType, String authTokenType) {
        mAccountManager.getAuthTokenByFeatures(accountType, authTokenType, null, this, null, null,
                new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> future) {
                        try {
                            future.getResult();
                            if (mLazyUser.get() != null) {
                                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast
                                        .makeText(
                                                MainActivity.this,
                                                getString(
                                                        R.string.error_general,
                                                        getString(R.string.error_user_not_found)
                                                ),
                                                Toast.LENGTH_LONG
                                        )
                                        .show();
                                finish();
                            }
                        } catch (OperationCanceledException e) {
                            finish();
                        } catch (IOException |
                                AuthenticatorException e) {
                            Toast
                                    .makeText(
                                            MainActivity.this,
                                            getString(R.string.error_general, e.getLocalizedMessage()),
                                            Toast.LENGTH_LONG
                                    )
                                    .show();
                            finish();
                        }
                    }
                }
                , null);
    }

}
