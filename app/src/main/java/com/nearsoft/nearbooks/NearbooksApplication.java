package com.nearsoft.nearbooks;

import android.app.Application;

import com.nearsoft.nearbooks.di.components.DaggerNearbooksApplicationComponent;
import com.nearsoft.nearbooks.di.components.NearbooksApplicationComponent;
import com.nearsoft.nearbooks.di.modules.NearbooksApplicationModule;
import com.nearsoft.nearbooks.di.modules.NetworkModule;
import com.squareup.picasso.Picasso;

/**
 * Base Nearbooks application.
 * Created by epool on 11/17/15.
 */
public class NearbooksApplication extends Application {

    private static NearbooksApplicationComponent mNearbooksApplicationComponent;

    public static NearbooksApplicationComponent getNearbooksApplicationComponent() {
        return mNearbooksApplicationComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mNearbooksApplicationComponent = DaggerNearbooksApplicationComponent
                .builder()
                .nearbooksApplicationModule(new NearbooksApplicationModule(this))
                .networkModule(new NetworkModule(getString(R.string.url_base_api)))
                .build();
        Picasso.setSingletonInstance(mNearbooksApplicationComponent.providePicasso());
    }
}
