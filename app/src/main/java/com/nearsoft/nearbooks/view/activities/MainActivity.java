package com.nearsoft.nearbooks.view.activities;

import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.common.Constants;
import com.nearsoft.nearbooks.di.components.BaseActivityComponent;
import com.nearsoft.nearbooks.di.modules.BaseActivityModule;
import com.nearsoft.nearbooks.di.qualifiers.Named;
import com.nearsoft.nearbooks.gcm.NearbooksRegistrationIntentService;
import com.nearsoft.nearbooks.sync.auth.AccountGeneral;
import com.nearsoft.nearbooks.util.Util;

import java.io.IOException;

import javax.inject.Inject;

public class MainActivity extends BaseActivity {

    @Inject
    protected AccountManager mAccountManager;
    @Inject
    @Named(BaseActivityModule.GCM_BROAD_CAST_RECEIVER)
    protected BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!Util.checkPlayServices(this)) {
            Toast.makeText(this,
                    R.string.message_google_play_services_required,
                    Toast.LENGTH_LONG)
                    .show();
            finish();
            return;
        }

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

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Constants.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private void getTokenForAccountCreateIfNeeded(String accountType, String authTokenType) {
        mAccountManager.getAuthTokenByFeatures(accountType, authTokenType, null, this, null, null,
                future -> {
                    try {
                        future.getResult();
                        if (mLazyUser.get() != null) {
                            // Start IntentService to register this application with GCM.
                            Intent intent = new Intent(MainActivity.this,
                                    NearbooksRegistrationIntentService.class);
                            startService(intent);
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
                , null);
    }

}
