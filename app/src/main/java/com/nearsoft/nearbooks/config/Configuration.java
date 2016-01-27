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

    private Configuration(Builder builder) {
        mWebServiceUrl = builder.mWebServiceUrl;
        mGoogleServerClientId = builder.mGoogleServerClientId;
    }

    public static Configuration getConfiguration(@Environment int environment) {
        switch (environment) {
            case DEVELOPMENT:
            default:
                return new Builder("http://nearbookdev.azurewebsites.net/api/",
                        "130661059453-56utmg42k7ojq3a3v9sat13noojvm1uq.apps.googleusercontent.com")
                        .build();
        }
    }

    public String getWebServiceUrl() {
        return mWebServiceUrl;
    }

    public String getGoogleServerClientId() {
        return mGoogleServerClientId;
    }

    @IntDef({DEVELOPMENT})
    private @interface Environment {
    }

    private static class Builder {

        private final String mWebServiceUrl;
        private final String mGoogleServerClientId;

        private Builder(@NonNull String webServiceUrl, @NonNull String googleServerClientId) {
            mWebServiceUrl = webServiceUrl;
            mGoogleServerClientId = googleServerClientId;
        }

        private Configuration build() {
            return new Configuration(this);
        }

    }

}
