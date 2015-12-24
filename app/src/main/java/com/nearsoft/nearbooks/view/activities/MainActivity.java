package com.nearsoft.nearbooks.view.activities;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.databinding.ActivityMainBinding;
import com.nearsoft.nearbooks.di.components.GoogleApiClientComponent;
import com.nearsoft.nearbooks.models.BookModel;
import com.nearsoft.nearbooks.models.sqlite.Book;
import com.nearsoft.nearbooks.models.sqlite.User;
import com.nearsoft.nearbooks.sync.auth.AccountGeneral;
import com.nearsoft.nearbooks.ws.BookService;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MainActivity extends GoogleApiClientBaseActivity {

    @Inject
    Lazy<User> mUser;
    private ActivityMainBinding mBinding;
    private AccountManager mAccountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = getBinding(ActivityMainBinding.class);

        mAccountManager = AccountManager.get(this);

        getTokenForAccountCreateIfNeeded(AccountGeneral.ACCOUNT_TYPE,
                AccountGeneral.AUTH_TOKEN_TYPE_FULL_ACCESS);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected void injectComponent(GoogleApiClientComponent googleApiClientComponent) {
        super.injectComponent(googleApiClientComponent);
        googleApiClientComponent.inject(this);
    }

    private void getTokenForAccountCreateIfNeeded(String accountType, String authTokenType) {
        mAccountManager.getAuthTokenByFeatures(accountType, authTokenType, null, this, null, null,
                new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> future) {
                        try {
                            future.getResult();
                            if (mUser.get() != null) {
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

    @Override
    public void onConnected(Bundle bundle) {
    }

    private void doCache() {
        BookService bookService = getNearbooksApplicationComponent().providesBookService();
        Call<List<Book>> call = bookService.getAllBooks();
        call.enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(Response<List<Book>> response, Retrofit retrofit) {
                BookModel.cacheBooks(response.body());
            }

            @Override
            public void onFailure(Throwable t) {
                Snackbar
                        .make(
                                mBinding.getRoot(),
                                getString(R.string.error_google_api, t.getLocalizedMessage()),
                                Snackbar.LENGTH_LONG
                        )
                        .show();
            }
        });
    }
}
