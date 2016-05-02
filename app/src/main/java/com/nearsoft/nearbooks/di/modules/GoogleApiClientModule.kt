package com.nearsoft.nearbooks.di.modules

import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.nearsoft.nearbooks.config.Configuration
import com.nearsoft.nearbooks.di.scopes.PerActivity
import com.nearsoft.nearbooks.view.activities.BaseActivity

import dagger.Module
import dagger.Provides

/**
 * Dagger 2 Google api client module.
 * Created by epool on 11/17/15.
 */
@Module
class GoogleApiClientModule(
        private val mConnectionFailedListener: GoogleApiClient.OnConnectionFailedListener,
        private val mConnectionCallbacks: GoogleApiClient.ConnectionCallbacks
) {

    @PerActivity
    @Provides
    fun provideGoogleSignInOptions(configuration: Configuration): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(configuration.googleServerClientId)
                .requestEmail()
                .build()
    }

    @PerActivity
    @Provides
    fun providesGoogleApiClient(baseActivity: BaseActivity,
                                googleSignInOptions: GoogleSignInOptions): GoogleApiClient {
        return GoogleApiClient.Builder(baseActivity)
                .enableAutoManage(baseActivity, mConnectionFailedListener)
                .addConnectionCallbacks(mConnectionCallbacks)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build()
    }
}
