package com.nearsoft.nearbooks.di.modules

import android.content.Context
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.picasso.OkHttp3Downloader
import com.nearsoft.nearbooks.config.Configuration
import com.nearsoft.nearbooks.gson.BookForeignKeyContainerSerializer
import com.nearsoft.nearbooks.models.sqlite.User
import com.nearsoft.nearbooks.other.StethoInterceptor
import com.nearsoft.nearbooks.ws.BookService
import com.nearsoft.nearbooks.ws.GoogleBooksService
import com.raizlabs.android.dbflow.structure.ModelAdapter
import com.squareup.picasso.Picasso
import dagger.Lazy
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

/**
 * Dagger 2 Net module.
 * Created by epool on 11/19/15.
 */
@Module
class NetworkModule {

    companion object {
        const val NAME_RETROFIT_NEARBOOKS = "NAME_RETROFIT_NEARBOOKS"
        private const val NAME_OK_HTTP_CLIENT_NEARBOOKS = "NAME_OK_HTTP_NEARBOOKS"
        private const val NAME_OK_HTTP_CLIENT_DEFAULT = "NAME_OK_HTTP_CLIENT_DEFAULT"
        private const val NAME_RETROFIT_GOOGLE_BOOKS = "NAME_RETROFIT_GOOGLE_BOOKS"

        private val SECONDS_TIMEOUT: Long = 20
    }

    @Provides
    @Singleton
    fun provideOkHttpCache(context: Context): Cache {
        val cacheSize = 10 * 1024 * 1024 // 10 MiB
        return Cache(context.cacheDir, cacheSize.toLong())
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        gsonBuilder.registerTypeAdapter(
                BookForeignKeyContainerSerializer.TYPE, BookForeignKeyContainerSerializer())
        gsonBuilder.setExclusionStrategies(object : ExclusionStrategy {
            override fun shouldSkipField(f: FieldAttributes): Boolean {
                return f.declaredClass.equals(ModelAdapter::class.java)
            }

            override fun shouldSkipClass(clazz: Class<*>): Boolean {
                return false
            }
        })
        return gsonBuilder.create()
    }

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        return logging
    }

    @Provides
    fun provideOkHttpClientBuilder(cache: Cache): OkHttpClient.Builder {
        return OkHttpClient.Builder()
                .cache(cache)
                .connectTimeout(SECONDS_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(SECONDS_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(SECONDS_TIMEOUT, TimeUnit.SECONDS)
                .addNetworkInterceptor(StethoInterceptor())
    }

    @Named(NAME_OK_HTTP_CLIENT_NEARBOOKS)
    @Provides
    @Singleton
    fun provideNearbooksOkHttpClient(builder: OkHttpClient.Builder,
                                     httpLoggingInterceptor: HttpLoggingInterceptor,
                                     lazyUser: Lazy<User>): OkHttpClient {
        return builder
                .addInterceptor { chain ->
                    val user = lazyUser.get() ?: return@addInterceptor chain.proceed(chain.request())

                    val request = chain
                            .request()
                            .newBuilder()
                            .addHeader("Authorization", user.idToken)
                            .build()

                    return@addInterceptor chain.proceed(request)
                }
                .addInterceptor(httpLoggingInterceptor)
                .build()
    }

    @Named(NAME_OK_HTTP_CLIENT_DEFAULT)
    @Provides
    @Singleton
    fun provideDefaultOkHttpClient(
            builder: OkHttpClient.Builder,
            httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return builder
                .addInterceptor(httpLoggingInterceptor)
                .build()
    }

    @Provides
    @Singleton
    fun provideRetrofitBuilder(
            gson: Gson): Retrofit.Builder {
        return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
    }

    @Named(NAME_RETROFIT_NEARBOOKS)
    @Provides
    @Singleton
    fun provideNearbooksRetrofit(builder: Retrofit.Builder,
                                 @Named(NAME_OK_HTTP_CLIENT_NEARBOOKS) okHttpClient: OkHttpClient,
                                 configuration: Configuration): Retrofit {
        return builder
                .baseUrl(configuration.webServiceUrl)
                .client(okHttpClient)
                .build()
    }

    @Named(NAME_RETROFIT_GOOGLE_BOOKS)
    @Provides
    @Singleton
    fun provideGoogleBooksRetrofit(builder: Retrofit.Builder,
                                   @Named(NAME_OK_HTTP_CLIENT_DEFAULT) okHttpClient: OkHttpClient,
                                   configuration: Configuration): Retrofit {
        return builder
                .baseUrl(configuration.googleBooksApiUrl)
                .client(okHttpClient)
                .build()
    }

    @Provides
    @Singleton
    fun provideBookService(@Named(NAME_RETROFIT_NEARBOOKS) retrofit: Retrofit): BookService {
        return retrofit.create(BookService::class.java)
    }

    @Provides
    @Singleton
    fun provideGoogleBooksService(
            @Named(NAME_RETROFIT_GOOGLE_BOOKS) retrofit: Retrofit): GoogleBooksService {
        return retrofit.create(GoogleBooksService::class.java)
    }

    @Provides
    @Singleton
    fun providePicasso(context: Context,
                       @Named(NAME_OK_HTTP_CLIENT_DEFAULT) okHttpClient: OkHttpClient): Picasso {
        return Picasso.Builder(context).downloader(OkHttp3Downloader(okHttpClient)).build()
    }

}
