package com.nearsoft.nearbooks;

import android.app.Application;

import com.nearsoft.nearbooks.di.components.DaggerNearbooksApplicationComponent;
import com.nearsoft.nearbooks.di.components.NearbooksApplicationComponent;
import com.nearsoft.nearbooks.di.modules.NearbooksApplicationModule;
import com.nearsoft.nearbooks.di.modules.NetModule;

/**
 * Base Nearbooks application.
 * Created by epool on 11/17/15.
 */
public class NearbooksApplication extends Application {

    private NearbooksApplicationComponent mNearbooksApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mNearbooksApplicationComponent = DaggerNearbooksApplicationComponent
                .builder()
                .nearbooksApplicationModule(new NearbooksApplicationModule(this))
                .netModule(new NetModule(getString(R.string.url_base_api)))
                .build();
    }

    public NearbooksApplicationComponent getNearbooksApplicationComponent() {
        return mNearbooksApplicationComponent;
    }
}
