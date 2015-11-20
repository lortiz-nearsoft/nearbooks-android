package com.nearsoft.nearbooks.di.modules;

import com.nearsoft.nearbooks.di.scopes.PerActivity;
import com.nearsoft.nearbooks.view.activities.BaseActivity;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger 2 Activity module.
 * Created by epool on 11/17/15.
 */
@Module
public class BaseActivityModule {
    private BaseActivity baseActivity;

    public BaseActivityModule(BaseActivity baseActivity) {
        this.baseActivity = baseActivity;
    }

    @PerActivity
    @Provides
    public BaseActivity providesBaseActivity() {
        return baseActivity;
    }
}
