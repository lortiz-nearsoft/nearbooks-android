package com.nearsoft.nearbooks.di.modules;

import android.content.Context;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.nearsoft.nearbooks.config.Configuration;
import com.nearsoft.nearbooks.di.qualifiers.Named;
import com.nearsoft.nearbooks.gson.BookForeignKeyContainerSerializer;
import com.nearsoft.nearbooks.models.sqlite.User;
import com.nearsoft.nearbooks.other.StethoInterceptor;
import com.nearsoft.nearbooks.ws.BookService;
import com.nearsoft.nearbooks.ws.GoogleBooksService;
import com.raizlabs.android.dbflow.structure.ModelAdapter;
import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Lazy;
import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Dagger 2 Net module.
 * Created by epool on 11/19/15.
 */
@Module
public class NetworkModule {

    public final static String NAME_RETROFIT_NEARBOOKS = "NAME_RETROFIT_NEARBOOKS";
    private final static String NAME_RETROFIT_GOOGLE_BOOKS = "NAME_RETROFIT_GOOGLE_BOOKS";
    private final static String NAME_OK_HTTP_CLIENT_NEARBOOKS = "NAME_OK_HTTP_NEARBOOKS";
    private final static String NAME_OK_HTTP_CLIENT_DEFAULT = "NAME_OK_HTTP_CLIENT_DEFAULT";

    private final static long SECONDS_TIMEOUT = 20;

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
    public HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        return logging;
    }

    @Provides
    public OkHttpClient.Builder provideOkHttpClientBuilder(Cache cache) {
        return new OkHttpClient.Builder()
                .cache(cache)
                .connectTimeout(SECONDS_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(SECONDS_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(SECONDS_TIMEOUT, TimeUnit.SECONDS)
                .addNetworkInterceptor(new StethoInterceptor());
    }

    @Named(NAME_OK_HTTP_CLIENT_NEARBOOKS)
    @Provides
    @Singleton
    public OkHttpClient provideNearbooksOkHttpClient(OkHttpClient.Builder builder,
                                                     HttpLoggingInterceptor httpLoggingInterceptor,
                                                     Lazy<User> lazyUser) {
        return builder
                .addInterceptor(chain -> {
                    User user = lazyUser.get();
                    if (user == null) return chain.proceed(chain.request());

                    Request request = chain
                            .request()
                            .newBuilder()
                            .addHeader("Authorization", user.getIdToken())
                            .build();

                    return chain.proceed(request);
                })
                .addInterceptor(httpLoggingInterceptor)
                .build();
    }

    @Named(NAME_OK_HTTP_CLIENT_DEFAULT)
    @Provides
    @Singleton
    public OkHttpClient provideDefaultOkHttpClient(
            OkHttpClient.Builder builder,
            HttpLoggingInterceptor httpLoggingInterceptor) {
        return builder
                .addInterceptor(httpLoggingInterceptor)
                .build();
    }

    @Provides
    @Singleton
    public Retrofit.Builder provideRetrofitBuilder(
            Gson gson,
            @Named(NAME_OK_HTTP_CLIENT_NEARBOOKS) OkHttpClient okHttpClient) {
        return new Retrofit
                .Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient);
    }

    @Named(NAME_RETROFIT_NEARBOOKS)
    @Provides
    @Singleton
    public Retrofit provideNearbooksRetrofit(Retrofit.Builder builder,
                                             @Named(NAME_OK_HTTP_CLIENT_NEARBOOKS) OkHttpClient okHttpClient,
                                             Configuration configuration) {
        return builder
                .baseUrl(configuration.getWebServiceUrl())
                .client(okHttpClient)
                .build();
    }

    @Named(NAME_RETROFIT_GOOGLE_BOOKS)
    @Provides
    @Singleton
    public Retrofit provideGoogleBooksRetrofit(Retrofit.Builder builder,
                                               @Named(NAME_OK_HTTP_CLIENT_DEFAULT) OkHttpClient okHttpClient,
                                               Configuration configuration) {
        return builder
                .baseUrl(configuration.getGoogleBooksApiUrl())
                .client(okHttpClient)
                .build();
    }

    @Provides
    @Singleton
    public BookService provideBookService(@Named(NAME_RETROFIT_NEARBOOKS) Retrofit retrofit) {
        return retrofit.create(BookService.class);
    }

    @Provides
    @Singleton
    public GoogleBooksService provideGoogleBooksService(
            @Named(NAME_RETROFIT_GOOGLE_BOOKS) Retrofit retrofit) {
        return retrofit.create(GoogleBooksService.class);
    }

    @Provides
    @Singleton
    public Picasso providePicasso(Context context,
                                  @Named(NAME_OK_HTTP_CLIENT_DEFAULT) OkHttpClient okHttpClient) {
        return new Picasso.Builder(context)
                .downloader(new OkHttp3Downloader(okHttpClient))
                .build();
    }

}
