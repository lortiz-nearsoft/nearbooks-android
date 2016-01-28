package com.nearsoft.nearbooks;

import android.support.multidex.MultiDexApplication;

import com.nearsoft.nearbooks.di.components.DaggerNearbooksApplicationComponent;
import com.nearsoft.nearbooks.di.components.NearbooksApplicationComponent;
import com.nearsoft.nearbooks.di.modules.NearbooksApplicationModule;
import com.nearsoft.nearbooks.di.modules.NetworkModule;
import com.squareup.picasso.Picasso;

/**
 * Base Nearbooks application.
 * Created by epool on 11/17/15.
 */
public class NearbooksApplication extends MultiDexApplication {

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
                .networkModule(new NetworkModule())
                .build();
        Picasso.setSingletonInstance(mNearbooksApplicationComponent.providePicasso());
    }
}
