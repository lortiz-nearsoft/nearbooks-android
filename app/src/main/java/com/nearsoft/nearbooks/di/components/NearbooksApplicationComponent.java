package com.nearsoft.nearbooks.di.components;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.nearsoft.nearbooks.di.modules.NearbooksApplicationModule;
import com.nearsoft.nearbooks.di.modules.NetModule;
import com.nearsoft.nearbooks.ws.BookService;
import com.squareup.picasso.Picasso;

import javax.inject.Singleton;

import dagger.Component;
import retrofit2.Retrofit;

/**
 * Dagger 2 Nearbooks application component.
 * Created by epool on 11/17/15.
 */
@Singleton
@Component(modules = {NearbooksApplicationModule.class, NetModule.class})
public interface NearbooksApplicationComponent {

    BookService providesBookService();

    Gson provideGson();

    SharedPreferences provideDefaultSharedPreferences();

    Picasso providePicasso();

    Retrofit provideRetrofit();

}
