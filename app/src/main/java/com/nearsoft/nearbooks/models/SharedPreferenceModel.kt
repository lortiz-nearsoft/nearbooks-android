package com.nearsoft.nearbooks.models

import com.nearsoft.nearbooks.NearbooksApplication

/**
 * Shared preference model.
 * Created by epool on 2/3/16.
 */
object SharedPreferenceModel {

    const val PREFERENCE_IS_GCM_TOKEN_SENT_TO_SERVER = "PREFERENCE_IS_GCM_TOKEN_SENT_TO_SERVER"

    const val PREFERENCE_IS_UPLOAD_BOOKS_MENU_SHOWN = "PREFERENCE_IS_UPLOAD_BOOKS_MENU_SHOWN"

    private val SHARED_PREFERENCES = NearbooksApplication.applicationComponent.provideDefaultSharedPreferences()

    fun clear() {
        SHARED_PREFERENCES.edit().clear().apply()
    }

    fun getBoolean(key: String): Boolean {
        return SHARED_PREFERENCES.getBoolean(key, false)
    }

    fun putBoolean(key: String, value: Boolean) {
        SHARED_PREFERENCES.edit().putBoolean(key, value).apply()
    }

}
