package com.nearsoft.nearbooks.di.modules;

import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.nearsoft.nearbooks.di.qualifiers.Named;
import com.nearsoft.nearbooks.di.scopes.PerActivity;
import com.nearsoft.nearbooks.models.sqlite.User;
import com.nearsoft.nearbooks.sync.SyncChangeHandler;
import com.nearsoft.nearbooks.view.activities.BaseActivity;
import com.nearsoft.nearbooks.view.activities.HomeActivity;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import dagger.Lazy;
import dagger.Module;
import dagger.Provides;

/**
 * Dagger 2 Activity module.
 * Created by epool on 11/17/15.
 */
@Module
public class BaseActivityModule {

    public static final String GCM_BROAD_CAST_RECEIVER = "GcmBroadCastReceiver";
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

    @PerActivity
    @Provides
    public User provideUser() {
        return SQLite
                .select()
                .from(User.class)
                .querySingle();
    }

    @PerActivity
    @Provides
    public AccountManager provideAccountManager() {
        return AccountManager.get(mBaseActivity);
    }

    @Named(GCM_BROAD_CAST_RECEIVER)
    @PerActivity
    @Provides
    public BroadcastReceiver provideGcmBroadCastReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Intent intentHomeActivity = new Intent(mBaseActivity, HomeActivity.class);
                mBaseActivity.startActivity(intentHomeActivity);
                mBaseActivity.finish();
            }
        };
    }

}
