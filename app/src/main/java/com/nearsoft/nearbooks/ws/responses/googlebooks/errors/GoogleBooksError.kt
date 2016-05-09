package com.nearsoft.nearbooks.ws.responses.googlebooks.errors

import com.google.gson.annotations.SerializedName

/**
 * Google books error.
 * Created by epool on 2/9/16.
 */
data class GoogleBooksError(
        @SerializedName("code") var code: Int = 0,
        @SerializedName("message") var message: String? = null
)
