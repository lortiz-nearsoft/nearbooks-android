package com.nearsoft.nearbooks.di.modules;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.nearsoft.nearbooks.activities.BaseActivity;
import com.nearsoft.nearbooks.di.scopes.PerActivity;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger 2 Google api client module.
 * Created by epool on 11/17/15.
 */
@Module
public class GoogleApiClientModule {
    private GoogleApiClient.OnConnectionFailedListener connectionFailedListener;
    private GoogleApiClient.ConnectionCallbacks connectionCallbacks;

    public GoogleApiClientModule(GoogleApiClient.OnConnectionFailedListener connectionFailedListener, GoogleApiClient.ConnectionCallbacks connectionCallbacks) {
        this.connectionFailedListener = connectionFailedListener;
        this.connectionCallbacks = connectionCallbacks;
    }

    @PerActivity
    @Provides
    public GoogleSignInOptions provideGoogleSignInOptions() {
        return new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
    }

    @PerActivity
    @Provides
    public GoogleApiClient providesGoogleApiClient(BaseActivity baseActivity, GoogleSignInOptions googleSignInOptions) {
        return new GoogleApiClient.Builder(baseActivity)
                .enableAutoManage(baseActivity, connectionFailedListener)
                .addConnectionCallbacks(connectionCallbacks)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();
    }
}
