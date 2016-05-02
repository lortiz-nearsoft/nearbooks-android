package com.nearsoft.nearbooks.config

import android.support.annotation.IntDef

/**
 * Configuration configurations.
 * Created by epool on 1/26/16.
 */
class Configuration private constructor(builder: Configuration.Builder) {

    companion object {

        const val DEVELOPMENT = 0

        fun getConfiguration(@Environment environment: Int): Configuration = when (environment) {
            DEVELOPMENT -> Builder("http://nearbookdev.azurewebsites.net/api/",
                    "130661059453-56utmg42k7ojq3a3v9sat13noojvm1uq.apps.googleusercontent.com",
                    "https://www.googleapis.com/books/v1/")
                    .build()
            else -> Builder("http://nearbookdev.azurewebsites.net/api/",
                    "130661059453-56utmg42k7ojq3a3v9sat13noojvm1uq.apps.googleusercontent.com",
                    "https://www.googleapis.com/books/v1/")
                    .build()
        }
    }

    val webServiceUrl: String
    val googleServerClientId: String
    val googleBooksApiUrl: String

    init {
        webServiceUrl = builder.mWebServiceUrl
        googleServerClientId = builder.mGoogleServerClientId
        googleBooksApiUrl = builder.mGoogleBooksApiUrl
    }

    @IntDef(DEVELOPMENT.toLong())
    private annotation class Environment

    private class Builder(val mWebServiceUrl: String, val mGoogleServerClientId: String,
                          val mGoogleBooksApiUrl: String) {

        fun build(): Configuration {
            return Configuration(this)
        }

    }

}
