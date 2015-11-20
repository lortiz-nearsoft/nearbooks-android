package com.nearsoft.nearbooks.activities;

import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.nearsoft.nearbooks.di.components.DaggerGoogleApiClientComponent;
import com.nearsoft.nearbooks.di.components.GoogleApiClientComponent;
import com.nearsoft.nearbooks.di.modules.GoogleApiClientModule;

import javax.inject.Inject;

/**
 * Google api client base activity.
 * Created by epool on 11/17/15.
 */
public abstract class GoogleApiClientBaseActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    @Inject
    protected GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GoogleApiClientComponent googleApiClientComponent = DaggerGoogleApiClientComponent.builder()
                .nearbooksApplicationComponent(getNearbooksApplicationComponent())
                .baseActivityModule(getBaseActivityModule())
                .googleApiClientModule(new GoogleApiClientModule(this, this))
                .build();
        injectComponent(googleApiClientComponent);
    }

    protected void injectComponent(GoogleApiClientComponent googleApiClientComponent) {
        googleApiClientComponent.inject(this);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        System.out.println("onConnectionFailed ====>: " + connectionResult.getErrorMessage());
    }

    @Override
    public void onConnectionSuspended(int i) {
        System.out.println("onConnectionSuspended ====>: " + i);
    }
}
