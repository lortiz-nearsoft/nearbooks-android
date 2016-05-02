package com.nearsoft.nearbooks.di.modules

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.facebook.stetho.Stetho
import com.nearsoft.nearbooks.BuildConfig
import com.nearsoft.nearbooks.NearbooksApplication
import com.nearsoft.nearbooks.R
import com.nearsoft.nearbooks.config.Configuration
import com.nearsoft.nearbooks.models.view.User
import com.nearsoft.nearbooks.view.helpers.ColorsWrapper
import dagger.Module
import dagger.Provides
import io.fabric.sdk.android.Fabric
import io.realm.Realm
import io.realm.RealmConfiguration
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
        }

        val crashlytics = Crashlytics.Builder()
                .core(CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build()
        Fabric.with(mNearbooksApplication, crashlytics)

        val realmConfiguration = RealmConfiguration.Builder(mNearbooksApplication.applicationContext)
                .name("nearbooks.realm")
                .schemaVersion(1)
                .build()
        Realm.setDefaultConfiguration(realmConfiguration)
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
    fun provideUser(): User {
        val realm = Realm.getDefaultInstance()
        val user = realm.where(com.nearsoft.nearbooks.models.realm.User::class.java)
                .findFirst()
        val userView = User(user)
        realm.close()
        return userView
    }

}
