package com.nearsoft.nearbooks.ws.responses.googlebooks

import com.google.gson.annotations.SerializedName

data class SearchInfo(
        @SerializedName("textSnippet") var textSnippet: String? = null
)
