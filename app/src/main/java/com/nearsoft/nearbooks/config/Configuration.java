package com.nearsoft.nearbooks.config;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

/**
 * Configuration configurations.
 * Created by epool on 1/26/16.
 */
public class Configuration {

    public static final int DEVELOPMENT = 0;

    private final String mWebServiceUrl;
    private final String mGoogleServerClientId;
    private final String mGoogleBooksApiUrl;

    private Configuration(Builder builder) {
        mWebServiceUrl = builder.mWebServiceUrl;
        mGoogleServerClientId = builder.mGoogleServerClientId;
        mGoogleBooksApiUrl = builder.mGoogleBooksApiUrl;
    }

    public static Configuration getConfiguration(@Environment int environment) {
        switch (environment) {
            case DEVELOPMENT:
            default:
                return new Builder("http://nearbookdev.azurewebsites.net/api/",
                        "130661059453-56utmg42k7ojq3a3v9sat13noojvm1uq.apps.googleusercontent.com",
                        "https://www.googleapis.com/books/v1/")
                        .build();
        }
    }

    public String getWebServiceUrl() {
        return mWebServiceUrl;
    }

    public String getGoogleServerClientId() {
        return mGoogleServerClientId;
    }

    public String getGoogleBooksApiUrl() {
        return mGoogleBooksApiUrl;
    }

    @IntDef({DEVELOPMENT})
    private @interface Environment {
    }

    private static class Builder {

        private final String mWebServiceUrl;
        private final String mGoogleServerClientId;
        private final String mGoogleBooksApiUrl;

        private Builder(@NonNull String webServiceUrl, @NonNull String googleServerClientId,
                        @NonNull String googleBooksApiUrl) {
            mWebServiceUrl = webServiceUrl;
            mGoogleServerClientId = googleServerClientId;
            mGoogleBooksApiUrl = googleBooksApiUrl;
        }

        private Configuration build() {
            return new Configuration(this);
        }

    }

}
