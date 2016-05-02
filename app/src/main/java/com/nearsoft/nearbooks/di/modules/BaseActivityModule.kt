package com.nearsoft.nearbooks.di.modules

import android.accounts.AccountManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.nearsoft.nearbooks.di.scopes.PerActivity
import com.nearsoft.nearbooks.models.view.User
import com.nearsoft.nearbooks.sync.SyncChangeHandler
import com.nearsoft.nearbooks.view.activities.BaseActivity
import com.nearsoft.nearbooks.view.activities.HomeActivity
import dagger.Lazy
import dagger.Module
import dagger.Provides
import javax.inject.Named

/**
 * Dagger 2 Activity module.
 * Created by epool on 11/17/15.
 */
@Module
class BaseActivityModule(private val mBaseActivity: BaseActivity) {

    companion object {
        const val GCM_BROAD_CAST_RECEIVER = "GcmBroadCastReceiver"
    }

    @PerActivity
    @Provides
    fun providesBaseActivity(): BaseActivity {
        return mBaseActivity
    }

    @PerActivity
    @Provides
    fun provideSyncChangeHandler(lazyUser: Lazy<User>): SyncChangeHandler {
        return SyncChangeHandler(lazyUser)
    }

    @PerActivity
    @Provides
    fun provideAccountManager(): AccountManager {
        return AccountManager.get(mBaseActivity)
    }

    @Named(GCM_BROAD_CAST_RECEIVER)
    @PerActivity
    @Provides
    fun provideGcmBroadCastReceiver(): BroadcastReceiver {
        return object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val intentHomeActivity = Intent(mBaseActivity, HomeActivity::class.java)
                mBaseActivity.startActivity(intentHomeActivity)
                mBaseActivity.finish()
            }
        }
    }

}
