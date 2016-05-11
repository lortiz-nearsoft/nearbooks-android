package com.nearsoft.nearbooks.di.components

import android.content.SharedPreferences
import com.nearsoft.nearbooks.config.Configuration
import com.nearsoft.nearbooks.di.modules.NearbooksApplicationModule
import com.nearsoft.nearbooks.di.modules.NetworkModule
import com.nearsoft.nearbooks.gcm.NearbooksRegistrationIntentService
import com.nearsoft.nearbooks.models.view.User
import com.nearsoft.nearbooks.view.adapters.BookRecyclerViewCursorAdapter
import com.nearsoft.nearbooks.ws.BookService
import com.nearsoft.nearbooks.ws.GoogleBooksService
import com.squareup.picasso.Picasso
import dagger.Component
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

/**
 * Dagger 2 Nearbooks application component.
 * Created by epool on 11/17/15.
 */
@Singleton
@Component(modules = arrayOf(NearbooksApplicationModule::class, NetworkModule::class))
interface NearbooksApplicationComponent {

    fun inject(nearbooksRegistrationIntentService: NearbooksRegistrationIntentService)

    fun inject(bookRecyclerViewCursorAdapter: BookRecyclerViewCursorAdapter)

    fun provideConfiguration(): Configuration

    fun provideBookService(): BookService

    fun provideGoogleBooksService(): GoogleBooksService

    fun provideDefaultSharedPreferences(): SharedPreferences

    fun providePicasso(): Picasso

    @Named(NetworkModule.NAME_RETROFIT_NEARBOOKS)
    fun provideNearbooksRetrofit(): Retrofit

    fun provideUser(): User

}
