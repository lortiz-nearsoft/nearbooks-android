package com.nearsoft.nearbooks.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.databinding.ActivityLoginBinding;
import com.nearsoft.nearbooks.models.BookModel;
import com.nearsoft.nearbooks.models.sqlite.Book;
import com.nearsoft.nearbooks.models.sqlite.User;
import com.nearsoft.nearbooks.ws.BookService;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends GoogleApiClientBaseActivity {

    private final static int RC_SIGN_IN = 1;
    private ActivityLoginBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = getBinding(ActivityLoginBinding.class);

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

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount googleSignInAccount = result.getSignInAccount();
            if (googleSignInAccount != null && googleSignInAccount.getEmail() != null &&
                    googleSignInAccount.getEmail().endsWith(getString(R.string.nearsoft_domain))) {
                doCache();

                Intent intent = new Intent(this, HomeActivity.class);
                intent.putExtra(HomeActivity.USER_KEY, new User(googleSignInAccount));
                startActivity(intent);
                finish();
            } else {
                Snackbar
                        .make(
                                mBinding.getRoot(),
                                getString(R.string.message_nearsoft_account_needed),
                                Snackbar.LENGTH_LONG
                        )
                        .show();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            }
        } else {
            Snackbar
                    .make(
                            mBinding.getRoot(),
                            getString(R.string.error_google_api, result.getStatus()),
                            Snackbar.LENGTH_LONG
                    )
                    .show();
        }
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
                                getString(R.string.error_general, t.getLocalizedMessage()),
                                Snackbar.LENGTH_LONG
                        )
                        .show();
            }
        });
    }

}

