package com.nearsoft.nearbooks.models;

import android.content.SharedPreferences;

import com.nearsoft.nearbooks.NearbooksApplication;

/**
 * Shared preference model.
 * Created by epool on 2/3/16.
 */
public class SharedPreferenceModel {

    public static final String PREFERENCE_IS_GCM_TOKEN_SENT_TO_SERVER =
            "PREFERENCE_IS_GCM_TOKEN_SENT_TO_SERVER";

    public static final String PREFERENCE_IS_UPLOAD_BOOKS_MENU_SHOWN =
            "PREFERENCE_IS_UPLOAD_BOOKS_MENU_SHOWN";

    private final static SharedPreferences SHARED_PREFERENCES = NearbooksApplication
            .getNearbooksApplicationComponent()
            .provideDefaultSharedPreferences();

    public static void clear() {
        SHARED_PREFERENCES.edit().clear().apply();
    }

    public static boolean getBoolean(String key) {
        return SHARED_PREFERENCES.getBoolean(key, false);
    }

    public static void putBoolean(String key, boolean value) {
        SHARED_PREFERENCES.edit().putBoolean(key, value).apply();
    }

}
