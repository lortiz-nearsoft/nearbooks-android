package com.nearsoft.nearbooks.di.components

import android.content.SharedPreferences
import com.google.gson.Gson
import com.nearsoft.nearbooks.config.Configuration
import com.nearsoft.nearbooks.di.modules.NearbooksApplicationModule
import com.nearsoft.nearbooks.di.modules.NetworkModule
import com.nearsoft.nearbooks.gcm.NearbooksRegistrationIntentService
import com.nearsoft.nearbooks.models.sqlite.User
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

    fun providesBookService(): BookService

    fun provideGoogleBooksService(): GoogleBooksService

    fun provideGson(): Gson

    fun provideDefaultSharedPreferences(): SharedPreferences

    fun providePicasso(): Picasso

    @Named(NetworkModule.NAME_RETROFIT_NEARBOOKS)
    fun provideNearbooksRetrofit(): Retrofit

    fun provideUser(): User

}
