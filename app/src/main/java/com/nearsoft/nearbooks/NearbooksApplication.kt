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
        private var sApplicationComponent: NearbooksApplicationComponent? = null
        fun applicationComponent() = sApplicationComponent!!
    }

    override fun onCreate() {
        super.onCreate()

        sApplicationComponent = DaggerNearbooksApplicationComponent.builder()
                .nearbooksApplicationModule(NearbooksApplicationModule(this))
                .networkModule(NetworkModule())
                .build()

        Picasso.setSingletonInstance(applicationComponent().providePicasso())
    }
}
