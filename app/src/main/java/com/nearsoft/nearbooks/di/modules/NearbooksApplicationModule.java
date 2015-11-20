package com.nearsoft.nearbooks.di.modules;

import android.content.Context;

import com.nearsoft.nearbooks.NearbooksApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Dagger 2 Nearbooks module.
 * Created by epool on 11/17/15.
 */
@Module
public class NearbooksApplicationModule {
    private final NearbooksApplication nearbooksApplication;

    public NearbooksApplicationModule(NearbooksApplication nearbooksApplication) {
        this.nearbooksApplication = nearbooksApplication;

        RealmConfiguration config = new RealmConfiguration.Builder(this.nearbooksApplication.getApplicationContext())
                .name("nearbooks.realm")
                .schemaVersion(1)
                .build();
        Realm.setDefaultConfiguration(config);
    }

    @Singleton
    @Provides
    public Context provideApplicationContext() {
        return nearbooksApplication.getApplicationContext();
    }

}
