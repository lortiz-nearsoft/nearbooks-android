package com.nearsoft.nearbooks.ws.responses.googlebooks.errors

import com.google.gson.annotations.SerializedName

/**
 * Google books error response.
 * Created by epool on 2/9/16.
 */
data class GoogleBooksErrorResponse(
        @SerializedName("error") var error: GoogleBooksError? = null
)
