package com.nearsoft.nearbooks.di.modules;

import com.nearsoft.nearbooks.di.scopes.PerActivity;
import com.nearsoft.nearbooks.models.sqlite.User;
import com.nearsoft.nearbooks.sync.SyncChangeHandler;
import com.nearsoft.nearbooks.view.activities.BaseActivity;

import dagger.Lazy;
import dagger.Module;
import dagger.Provides;

/**
 * Dagger 2 Activity module.
 * Created by epool on 11/17/15.
 */
@Module
public class BaseActivityModule {

    private BaseActivity mBaseActivity;

    public BaseActivityModule(BaseActivity baseActivity) {
        mBaseActivity = baseActivity;
    }

    @PerActivity
    @Provides
    public BaseActivity providesBaseActivity() {
        return mBaseActivity;
    }

    @PerActivity
    @Provides
    public SyncChangeHandler provideSyncChangeHandler(Lazy<User> lazyUser) {
        return new SyncChangeHandler(lazyUser);
    }
}
