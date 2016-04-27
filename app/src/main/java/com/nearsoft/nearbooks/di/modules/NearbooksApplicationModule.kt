package com.nearsoft.nearbooks.di.modules

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import com.crashlytics.android.Crashlytics
import com.facebook.stetho.Stetho
import com.nearsoft.nearbooks.BuildConfig
import com.nearsoft.nearbooks.NearbooksApplication
import com.nearsoft.nearbooks.R
import com.nearsoft.nearbooks.config.Configuration
import com.nearsoft.nearbooks.models.sqlite.User
import com.nearsoft.nearbooks.view.helpers.ColorsWrapper
import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.sql.language.SQLite
import dagger.Module
import dagger.Provides
import io.fabric.sdk.android.Fabric
import javax.inject.Singleton

/**
 * Dagger 2 Nearbooks module.
 * Created by epool on 11/17/15.
 */
@Module
class NearbooksApplicationModule(private val mNearbooksApplication: NearbooksApplication) {

    init {
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(mNearbooksApplication.applicationContext)
        } else {
            // TODO: Add fabric configuration for debugging.
            Fabric.with(mNearbooksApplication, Crashlytics())
        }

        FlowManager.init(mNearbooksApplication.applicationContext)
    }

    @Provides
    @Singleton
    fun providesConfiguration(): Configuration {
        return Configuration.getConfiguration(Configuration.DEVELOPMENT)
    }

    @Provides
    @Singleton
    fun provideApplicationContext(): Context {
        return mNearbooksApplication.applicationContext
    }

    @Provides
    @Singleton
    fun provideDefaultSharedPreferences(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(mNearbooksApplication)
    }

    @Provides
    @Singleton
    fun provideDefaultColorsWrapper(): ColorsWrapper {
        val colorPrimaryDark = ContextCompat
                .getColor(mNearbooksApplication, R.color.colorPrimaryDark)
        val colorPrimary = ContextCompat.getColor(mNearbooksApplication, R.color.colorPrimary)
        val whiteColor = ContextCompat.getColor(mNearbooksApplication, R.color.white)
        return ColorsWrapper(colorPrimaryDark, colorPrimary, whiteColor, whiteColor)
    }

    @Provides
    @Singleton
    fun provideUser(): User {
        return SQLite.select().from(User::class.java).querySingle()
    }

}
