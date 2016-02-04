package com.nearsoft.nearbooks.di.components;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.nearsoft.nearbooks.config.Configuration;
import com.nearsoft.nearbooks.di.modules.NearbooksApplicationModule;
import com.nearsoft.nearbooks.di.modules.NetworkModule;
import com.nearsoft.nearbooks.di.qualifiers.Named;
import com.nearsoft.nearbooks.gcm.NearbooksRegistrationIntentService;
import com.nearsoft.nearbooks.view.adapters.BookRecyclerViewCursorAdapter;
import com.nearsoft.nearbooks.ws.BookService;
import com.nearsoft.nearbooks.ws.GoogleBooksService;
import com.squareup.picasso.Picasso;

import javax.inject.Singleton;

import dagger.Component;
import retrofit2.Retrofit;

/**
 * Dagger 2 Nearbooks application component.
 * Created by epool on 11/17/15.
 */
@Singleton
@Component(modules = {NearbooksApplicationModule.class, NetworkModule.class})
public interface NearbooksApplicationComponent {

    void inject(NearbooksRegistrationIntentService nearbooksRegistrationIntentService);

    void inject(BookRecyclerViewCursorAdapter bookRecyclerViewCursorAdapter);

    Configuration provideConfiguration();

    BookService providesBookService();

    GoogleBooksService provideGoogleBooksService();

    Gson provideGson();

    SharedPreferences provideDefaultSharedPreferences();

    Picasso providePicasso();

    @Named(NetworkModule.NAME_RETROFIT_NEARBOOKS)
    Retrofit provideNearbooksRetrofit();

}
