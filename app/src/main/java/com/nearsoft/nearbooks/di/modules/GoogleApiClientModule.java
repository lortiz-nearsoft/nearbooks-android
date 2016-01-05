package com.nearsoft.nearbooks.di.modules;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.nearsoft.nearbooks.R;
import com.nearsoft.nearbooks.di.scopes.PerActivity;
import com.nearsoft.nearbooks.view.activities.BaseActivity;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger 2 Google api client module.
 * Created by epool on 11/17/15.
 */
@Module
public class GoogleApiClientModule {

    private GoogleApiClient.OnConnectionFailedListener mConnectionFailedListener;
    private GoogleApiClient.ConnectionCallbacks mConnectionCallbacks;

    public GoogleApiClientModule(
            GoogleApiClient.OnConnectionFailedListener connectionFailedListener,
            GoogleApiClient.ConnectionCallbacks connectionCallbacks) {
        mConnectionFailedListener = connectionFailedListener;
        mConnectionCallbacks = connectionCallbacks;
    }

    @PerActivity
    @Provides
    public GoogleSignInOptions provideGoogleSignInOptions(BaseActivity baseActivity) {
        return new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(baseActivity.getString(R.string.server_client_id))
                .requestEmail().build();
    }

    @PerActivity
    @Provides
    public GoogleApiClient providesGoogleApiClient(BaseActivity baseActivity,
                                                   GoogleSignInOptions googleSignInOptions) {
        return new GoogleApiClient
                .Builder(baseActivity)
                .enableAutoManage(baseActivity, mConnectionFailedListener)
                .addConnectionCallbacks(mConnectionCallbacks)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();
    }
}
