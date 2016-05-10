package com.nearsoft.nearbooks.view.activities

import android.os.Bundle

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.nearsoft.nearbooks.NearbooksApplication
import com.nearsoft.nearbooks.di.components.DaggerGoogleApiClientComponent
import com.nearsoft.nearbooks.di.components.GoogleApiClientComponent
import com.nearsoft.nearbooks.di.modules.GoogleApiClientModule

import javax.inject.Inject

/**
 * Google api client base activity.
 * Created by epool on 11/17/15.
 */
abstract class GoogleApiClientBaseActivity : BaseActivity(), GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    @Inject
    lateinit protected var mGoogleApiClient: GoogleApiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val googleApiClientComponent = DaggerGoogleApiClientComponent
                .builder()
                .nearbooksApplicationComponent(NearbooksApplication.applicationComponent)
                .baseActivityModule(baseActivityModule)
                .googleApiClientModule(GoogleApiClientModule(this, this))
                .build()
        injectComponent(googleApiClientComponent)
    }

    protected open fun injectComponent(googleApiClientComponent: GoogleApiClientComponent) {
        googleApiClientComponent.inject(this)
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        println("onConnectionFailed ====>: " + connectionResult.errorMessage!!)
    }

    override fun onConnectionSuspended(i: Int) {
        println("onConnectionSuspended ====>: " + i)
    }
}
