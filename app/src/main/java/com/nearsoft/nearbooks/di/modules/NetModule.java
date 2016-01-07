package com.nearsoft.nearbooks.di.modules;

import android.content.Context;

import com.facebook.stetho.okhttp.StethoInterceptor;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nearsoft.nearbooks.ws.BookService;
import com.raizlabs.android.dbflow.structure.ModelAdapter;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Dagger 2 Net module.
 * Created by epool on 11/19/15.
 */
@Module
public class NetModule {

    private String mBaseUrl;

    public NetModule(String baseUrl) {
        mBaseUrl = baseUrl;
    }

    @Provides
    @Singleton
    public Cache provideOkHttpCache(Context context) {
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        return new Cache(context.getCacheDir(), cacheSize);
    }

    @Provides
    @Singleton
    public Gson provideGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return f.getDeclaredClass().equals(ModelAdapter.class);
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        });
        return gsonBuilder.create();
    }

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(Cache cache) {
        OkHttpClient client = new OkHttpClient();
        client.setCache(cache);
        client.networkInterceptors().add(new StethoInterceptor());
//        TODO: Add credentials.
//        client.interceptors().add(new Interceptor() {
//            @Override
//            public Response intercept(Chain chain) throws IOException {
//                Request request = chain.request();
//
//                HttpUrl httpUrl = request.httpUrl().newBuilder()
//                        .addQueryParameter("client_id", "f7373613c193424ba4be7f85ec6e6b2c")
//                        .build();
//
//                Request newRequest = request.newBuilder()
//                        .url(httpUrl)
//                        .build();
//
//                return chain.proceed(newRequest);
//            }
//        });
        return client;
    }

    @Provides
    @Singleton
    public Retrofit provideRetrofit(Gson gson, OkHttpClient okHttpClient) {
        return new Retrofit
                .Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(mBaseUrl)
                .client(okHttpClient)
                .build();
    }

    @Provides
    @Singleton
    public BookService provideBookService(Retrofit retrofit) {
        return retrofit.create(BookService.class);
    }
}
