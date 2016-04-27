package com.nearsoft.nearbooks.di.components

import com.nearsoft.nearbooks.di.modules.BaseActivityModule
import com.nearsoft.nearbooks.di.modules.GoogleApiClientModule
import com.nearsoft.nearbooks.di.scopes.PerActivity
import com.nearsoft.nearbooks.sync.auth.AuthenticatorActivity
import com.nearsoft.nearbooks.view.activities.GoogleApiClientBaseActivity
import com.nearsoft.nearbooks.view.activities.HomeActivity
import com.nearsoft.nearbooks.view.activities.MainActivity

import dagger.Component

/**
 * Dagger 2 Google api client component.
 * Created by epool on 11/17/15.
 */
@PerActivity
@Component(
        dependencies = arrayOf(NearbooksApplicationComponent::class),
        modules = arrayOf(BaseActivityModule::class, GoogleApiClientModule::class)
)
interface GoogleApiClientComponent : BaseActivityComponent {

    fun inject(googleApiClientBaseActivity: GoogleApiClientBaseActivity)

    override fun inject(mainActivity: MainActivity)

    fun inject(homeActivity: HomeActivity)

    fun inject(authenticatorActivity: AuthenticatorActivity)

}
