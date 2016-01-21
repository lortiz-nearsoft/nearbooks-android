package com.nearsoft.nearbooks.di.modules;

import android.content.Context;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.nearsoft.nearbooks.gson.BookForeignKeyContainerSerializer;
import com.nearsoft.nearbooks.other.StethoInterceptor;
import com.nearsoft.nearbooks.ws.BookService;
import com.raizlabs.android.dbflow.structure.ModelAdapter;
import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;

/**
 * Dagger 2 Net module.
 * Created by epool on 11/19/15.
 */
@Module
public class NetworkModule {

    private final static long SECONDS_TIMEOUT = 20;

    private String mBaseUrl;

    public NetworkModule(String baseUrl) {
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
        gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        gsonBuilder.registerTypeAdapter(
                BookForeignKeyContainerSerializer.TYPE, new BookForeignKeyContainerSerializer()
        );
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
        return new OkHttpClient.Builder()
                .cache(cache)
                .connectTimeout(SECONDS_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(SECONDS_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(SECONDS_TIMEOUT, TimeUnit.SECONDS)
                .addNetworkInterceptor(new StethoInterceptor())
//        TODO: Add credentials.
//                .addInterceptor(new Interceptor() {
//                    @Override
//                    public Response intercept(Chain chain) throws IOException {
//                        Request request = chain.request();
//
//                        HttpUrl httpUrl = request.url().newBuilder()
//                                .addQueryParameter("client_id", "f7373613c193424ba4be7f85ec6e6b2c")
//                                .build();
//
//                        Request newRequest = request.newBuilder()
//                                .url(httpUrl)
//                                .build();
//
//                        return chain.proceed(newRequest);
//                    }
//                })
                .build();
    }

    @Provides
    @Singleton
    public Retrofit provideRetrofit(Gson gson, OkHttpClient okHttpClient) {
        return new Retrofit
                .Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(mBaseUrl)
                .client(okHttpClient)
                .build();
    }

    @Provides
    @Singleton
    public BookService provideBookService(Retrofit retrofit) {
        return retrofit.create(BookService.class);
    }

    @Provides
    @Singleton
    public Picasso providePicasso(Context context, OkHttpClient okHttpClient) {
        return new Picasso.Builder(context)
                .downloader(new OkHttp3Downloader(okHttpClient))
                .build();
    }

}
