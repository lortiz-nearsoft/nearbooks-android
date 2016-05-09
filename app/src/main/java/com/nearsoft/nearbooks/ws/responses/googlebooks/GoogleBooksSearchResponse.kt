package com.nearsoft.nearbooks.ws.responses.googlebooks

import com.google.gson.annotations.SerializedName

/**
 * Google books search response.
 * Created by epool on 2/2/16.
 */
data class GoogleBooksSearchResponse(
        @SerializedName("kind") var kind: String? = null,
        @SerializedName("totalItems") var totalItems: Int = 0,
        @SerializedName("items") var items: List<Volume>? = null
)
