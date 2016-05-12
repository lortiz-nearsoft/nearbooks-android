package com.nearsoft.nearbooks

import android.support.multidex.MultiDexApplication
import com.nearsoft.nearbooks.di.components.DaggerNearbooksApplicationComponent
import com.nearsoft.nearbooks.di.components.NearbooksApplicationComponent
import com.nearsoft.nearbooks.di.modules.NearbooksApplicationModule
import com.nearsoft.nearbooks.di.modules.NetworkModule
import com.squareup.picasso.Picasso

/**
 * Base Nearbooks application.
 * Created by epool on 11/17/15.
 */
class NearbooksApplication : MultiDexApplication() {

    companion object {
        lateinit var applicationComponent: NearbooksApplicationComponent
            private set
    }

    override fun onCreate() {
        super.onCreate()

        applicationComponent = DaggerNearbooksApplicationComponent.builder()
                .nearbooksApplicationModule(NearbooksApplicationModule(this))
                .networkModule(NetworkModule())
                .build()

        Picasso.setSingletonInstance(applicationComponent.providePicasso())
    }
}
