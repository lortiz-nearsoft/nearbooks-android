package com.nearsoft.nearbooks.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.ResultCallback;
import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.databinding.ActivityMainBinding;
import com.nearsoft.nearbooks.models.BookModel;
import com.nearsoft.nearbooks.view.models.BookViewModel;
import com.nearsoft.nearbooks.view.models.UserViewModel;
import com.nearsoft.nearbooks.ws.BookService;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MainActivity extends GoogleApiClientBaseActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = getBinding(ActivityMainBinding.class);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Class<? extends BaseActivity> clazz;
        GoogleSignInAccount googleSignInAccount = null;
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            googleSignInAccount = result.getSignInAccount();
            String email = googleSignInAccount.getEmail();
            clazz = (email != null && email.endsWith(getString(R.string.nearsoft_domain))) ?
                    HomeActivity.class :
                    LoginActivity.class;
        } else {
            clazz = LoginActivity.class;
        }

        if (clazz == HomeActivity.class) {
            doCache();
        }

        Intent intent = new Intent(this, clazz);
        if (clazz == HomeActivity.class && googleSignInAccount != null) {
            intent.putExtra(HomeActivity.USER_KEY, new UserViewModel(googleSignInAccount));
        }
        startActivity(intent);
        finish();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Auth.GoogleSignInApi.silentSignIn(googleApiClient).setResultCallback(new ResultCallback<GoogleSignInResult>() {
            @Override
            public void onResult(GoogleSignInResult googleSignInResult) {
                handleSignInResult(googleSignInResult);
            }
        });
    }

    private void doCache() {
        BookService bookService = getNearbooksApplicationComponent().providesBookService();
        Call<List<BookViewModel>> call = bookService.getAllBooks();
        call.enqueue(new Callback<List<BookViewModel>>() {
            @Override
            public void onResponse(Response<List<BookViewModel>> response, Retrofit retrofit) {
                BookModel.cacheBooks(response.body());
            }

            @Override
            public void onFailure(Throwable t) {
                Snackbar.make(binding.getRoot(), getString(R.string.error_google_api, t.getLocalizedMessage()), Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
